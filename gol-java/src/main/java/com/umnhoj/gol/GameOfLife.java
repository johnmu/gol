package com.umnhoj.gol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.umnhoj.gol.rle.RleFile;
import com.umnhoj.gol.types.Cell;
import com.umnhoj.gol.types.CellSet;

public class GameOfLife implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(GameOfLife.class);

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
		final RleFile rleFile = GameOfLife.parseRle(this.inputGrid);

		// Run iterations
		long startTime = System.nanoTime();
		final List<CellSet> generations = this.runIterations(rleFile);
		long endTime = System.nanoTime();

		// Print output GIF
		{
			// Determine bounds
			final Rectangle bounds = computeBounds(generations);

			final List<BufferedImage> images = new ArrayList<>();
			int i = 0;
			for (final CellSet generation : generations) {
				// Create a BufferedImage for each generation
				final BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_RGB);
				final Graphics2D graphics = image.createGraphics();
				graphics.setPaint(Color.WHITE);
				graphics.fillRect(0, 0, bounds.width, bounds.height);
				for (final Cell cell : generation.getCells()) {
					image.setRGB(cell.getX() - bounds.x, cell.getY() - bounds.y, Color.BLACK.getRGB());
				}
				images.add(image);
				try {
					ImageIO.write(image, "gif", Paths.get(this.outputGif.toString() + i + ".gif").toFile());
				} catch (IOException e) {
					throw new RuntimeException("Error writing GIF file", e);
				}
				i++;
			}

			// Create a GIF with the buffered images

		}

		System.out.println((endTime - startTime) / 1000000.0 + " ms");
	}

	protected List<CellSet> runIterations(final RleFile rleFile) {
		final List<CellSet> cellGenerations = new ArrayList<>();
		cellGenerations.add(new CellSet(rleFile.getCells()));
		for (int generation = 0; generation < this.numGenerations; generation++) {
			final CellSet prevCellSet = cellGenerations.get(cellGenerations.size() - 1);
			cellGenerations.add(new CellSet(prevCellSet.getCells().stream().flatMap(cell -> {
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
