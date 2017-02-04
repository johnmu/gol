package com.umnhoj.gol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import com.umnhoj.gol.rle.RleFile;
import com.umnhoj.gol.types.Cell;
import com.umnhoj.gol.types.CellSet;

public class GameOfLife implements Runnable {

	private static final String GRAPHIC_CONTROL_EXTENSION = "GraphicControlExtension";

	private static final Logger log = LoggerFactory.getLogger(GameOfLife.class);

	public static final int ZOOM = 2;

	protected int numGenerations;
	protected Path inputGrid;
	protected Path outputGif;

	public static void main(final String[] args) {
		if (args.length != 3) {
			log.error("Incorrect number of arguments, please see ../README.md for details");
			System.exit(1);
		}

		new GameOfLife(Integer.parseInt(args[0]), Paths.get(args[1]), Paths.get(args[2])).run();
	}

	public GameOfLife() {

	}

	public GameOfLife(final int numGenerations, final Path inputGrid, final Path outputGif) {
		this.numGenerations = numGenerations;
		this.inputGrid = inputGrid;
		this.outputGif = outputGif;
	}

	@Override
	public void run() {
		// Parse the RLE file
		log.info("Starting loading file");
		final RleFile rleFile = GameOfLife.parseRle(this.inputGrid);
		log.info("Done loading file");

		// Run iterations
		log.info("Starting iterations");
		long startTime = System.nanoTime();
		final List<CellSet> generations = this.runIterations(rleFile);
		long endTime = System.nanoTime();
		log.info("Done iterations");

		System.out.println((endTime - startTime) / 1000000.0 + " ms");

		// Print output GIF
		{
			// Determine bounds
			final Rectangle bounds = computeBounds(generations);

			// Create a GIF with the buffered images
			// https://en.wikipedia.org/wiki/GIF
			// http://giflib.sourceforge.net/whatsinagif/bits_and_bytes.html
			try {
				this.createGif(generations, bounds);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected void createGif(final List<CellSet> generations, final Rectangle bounds)
			throws FileNotFoundException, IOException {
		{
			final ImageWriter iw = ImageIO.getImageWritersByFormatName("gif").next();
			final ImageWriteParam param = iw.getDefaultWriteParam();
			final ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier
					.createFromRenderedImage(GameOfLife.createImage(generations.get(0), bounds, ZOOM));
			final IIOMetadata metadata = iw.getDefaultImageMetadata(typeSpecifier, param);

			final String nativeMetadataFormatName = metadata.getNativeMetadataFormatName();

			final IIOMetadataNode node = (IIOMetadataNode) metadata.getAsTree(nativeMetadataFormatName);
			{
				final IIOMetadataNode child = getOrCreateNode(node, GRAPHIC_CONTROL_EXTENSION);

				// In hundredth of a second -_-"
				child.setAttribute("delayTime", Integer.toString(2));
			}
			try {
				metadata.setFromTree(nativeMetadataFormatName, node);
			} catch (IIOInvalidTreeException e) {
				throw new RuntimeException("Invalid GIF metadata", e);
			}

			try (final ImageOutputStream output = new FileImageOutputStream(this.outputGif.toFile())) {
				iw.setOutput(output);
				iw.prepareWriteSequence(null);

				int frame = 1;
				for (final CellSet generation : generations) {
					iw.writeToSequence(new IIOImage(GameOfLife.createImage(generation, bounds, ZOOM), null, metadata),
							param);
					log.info("Rendered frame {}", frame);
					frame++;
				}
			}
		}
	}

	/**
	 *
	 * @param node
	 * @param name
	 * @return null if the node is not unique
	 */
	protected static IIOMetadataNode getOrCreateNode(final IIOMetadataNode node, final String name) {
		final NodeList nodeList = node.getElementsByTagName(name);
		final IIOMetadataNode retNode;
		if (nodeList.getLength() == 0) {
			retNode = new IIOMetadataNode(name);
			node.appendChild(retNode);
		} else if (nodeList.getLength() == 1) {
			retNode = (IIOMetadataNode) nodeList.item(0);
		} else {
			retNode = null;
		}
		return retNode;
	}

	protected static BufferedImage createImage(final CellSet generation, final Rectangle bounds, final int zoom) {
		final BufferedImage image = new BufferedImage(bounds.width * zoom, bounds.height * zoom,
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics = image.createGraphics();
		graphics.setPaint(Color.WHITE);
		graphics.fillRect(0, 0, bounds.width * zoom, bounds.height * zoom);
		graphics.setPaint(Color.BLACK);
		for (final Cell cell : generation.getCells()) {
			graphics.fillRect((cell.getX() - bounds.x) * zoom, (cell.getY() - bounds.y) * zoom, zoom, zoom);
		}
		return image;
	}

	protected List<CellSet> runIterations(final RleFile rleFile) {
		final List<CellSet> cellGenerations = new ArrayList<>();
		cellGenerations.add(new CellSet(rleFile.getCells()));
		for (int generation = 0; generation < this.numGenerations; generation++) {
			final CellSet prevCellSet = cellGenerations.get(cellGenerations.size() - 1);
			cellGenerations.add(new CellSet(prevCellSet.getCells().parallelStream().flatMap(cell -> {
				return CellSet.mapNeighbors(cell, cell2 -> {
					if (applyB3S23Rule(prevCellSet.contains(cell2), prevCellSet.countNeighbors(cell2))) {
						return cell2;
					}
					return null;
				}).stream().filter(Objects::nonNull);
			}).collect(Collectors.toList())));
		}

		return cellGenerations;
	}

	/**
	 * TODO can be abstracted to some rules interface for alternative rules
	 *
	 * @param alive
	 *            Is the current cell alive?
	 * @param neighborsAlive
	 *            How many neighbors are alive
	 * @return Is the cell alive in the next generation?
	 */
	protected static boolean applyB3S23Rule(final boolean alive, final int neighborsAlive) {
		if (alive) {
			if (neighborsAlive < 2 || neighborsAlive > 3) {
				return false;
			} else {
				return true;
			}
		} else {
			if (neighborsAlive == 3) {
				return true;
			} else {
				return false;
			}
		}
	}

	protected static RleFile parseRle(final Path input) {
		try {
			return RleFile.parse(input);
		} catch (IOException e) {
			log.error("Error parsing RLE file");
			System.exit(1);
		}
		return null;
	}

	public static Rectangle computeBounds(final List<CellSet> generations) {
		final Rectangle retVal = new Rectangle(-1, -1);
		for (final CellSet generation : generations) {
			retVal.add(generation.getBounds());
		}
		return retVal;
	}

	public static void printGenerations(final OutputStream out, final List<CellSet> generations) {
		final PrintStream ps = new PrintStream(out);
		final Rectangle bounds = computeBounds(generations);
		for (int generation = 0; generation < generations.size(); generation++) {
			ps.format("\nGeneration: %d\n", generation);
			ps.print(generations.get(generation).toString(bounds));
		}
	}
}
