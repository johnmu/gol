package com.umnhoj.gol;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		// Run iterations

		// Print output GIF
	}
}
