package com.umnhoj.gol;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.umnhoj.gol.rle.RleFile;
import com.umnhoj.gol.types.CellSet;

public class GameOfLife implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(GameOfLife.class);

	protected final int numGenerations;
	protected final Path inputGrid;
	protected final Path outputGif;

	public static void main(final String[] args) {
		if (args.length != 3) {
			log.error("Incorrect number of arguments, please see ../README.md for details");
			System.exit(1);
		}

		new GameOfLife(Integer.parseInt(args[0]), Paths.get(args[1]), Paths.get(args[2])).run();
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
		final List<CellSet> cellGenerations = this.runIterations(rleFile);

		// Print output GIF
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

	public static void printGenerations(final PrintStream out, final List<CellSet> generations) {
		final Rectangle bounds = computeBounds(generations);
		for (int generation = 0; generation < generations.size(); generation++) {
			out.format("Generations: %d\n", generation);
			out.println(generations.get(generation).toString(bounds));
		}
	}
}
