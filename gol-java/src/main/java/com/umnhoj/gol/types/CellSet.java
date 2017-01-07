package com.umnhoj.gol.types;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class CellSet {
	final Set<Cell> cells = new LinkedHashSet<>();

	public CellSet(final Collection<? extends Cell> cells) {
		this.cells.addAll(cells);
	}

	public boolean contains(final int x, final int y) {
		return this.cells.contains(new Cell(x, y));
	}

	public int countNeighbors(final Cell cell) {
		return this.countNeighbors(cell.getX(), cell.getY());
	}

	public int countNeighbors(final int x, final int y) {
		int count = 0;
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (!(i == x && j == y) && this.contains(i, j)) {
					count++;
				}
			}
		}
		return count;
	}
}
