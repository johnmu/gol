package com.umnhoj.gol;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.umnhoj.gol.rle.RleFile;
import com.umnhoj.gol.types.Cell;

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
		{
			final List<Cell> cells = rleFile.getCells();
			final List<? extends List<Cell>> cellGenerations = new ArrayList<>();
			for (int generation = 0; generation < this.numGenerations; generation++) {
				// final List<Cell> nextCells = new ArrayList<>
			}
		}

		// Print output GIF
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
}
