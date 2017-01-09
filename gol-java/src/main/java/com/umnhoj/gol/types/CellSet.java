package com.umnhoj.gol.types;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CellSet {
	public static <T> Collection<T> mapNeighbors(final Cell cell, final Function<Cell, T> func) {
		final List<T> retVal = new ArrayList<>();
		for (int i = cell.getX() - 1; i <= cell.getX() + 1; i++) {
			for (int j = cell.getY() - 1; j <= cell.getY() + 1; j++) {
				final Cell cell2 = new Cell(i, j);
				if (!cell.equals(cell2)) {
					retVal.add(func.apply(cell2));
				}
			}
		}
		return retVal;
	}

	final Set<Cell> cells = new LinkedHashSet<>();

	public CellSet(final Collection<? extends Cell> cells) {
		this.cells.addAll(cells);
	}

	public boolean contains(final Cell cell) {
		return this.cells.contains(cell);
	}

	public int countNeighbors(final Cell cell) {
		return CellSet.mapNeighbors(cell, c -> {
			if (this.contains(c)) {
				return 1;
			}
			return 0;
		}).stream().reduce(Integer::sum).orElse(0);
	}

	public Set<Cell> getCells() {
		return this.cells;
	}

	public Rectangle getBounds() {
		if (this.cells.isEmpty()) {
			return new Rectangle(0, 0, 0, 0);
		}

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (final Cell cell : this.cells) {
			minX = Math.min(minX, cell.getX());
			minY = Math.min(minY, cell.getY());
			maxX = Math.max(maxX, cell.getX());
			maxY = Math.max(maxY, cell.getY());
		}
		return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
	}

	@Override
	public String toString() {
		return this.toString(this.getBounds());
	}

	public String toString(final Rectangle bounds) {
		final StringBuilder retVal = new StringBuilder();
		for (int y = 0; y < bounds.height; y++) {
			for (int x = 0; x < bounds.width; x++) {
				if (this.contains(new Cell(bounds.x + x, bounds.y + y))) {
					retVal.append("#");
				} else {
					retVal.append(".");
				}
			}
			retVal.append("\n");
		}

		return retVal.toString();
	}
}
