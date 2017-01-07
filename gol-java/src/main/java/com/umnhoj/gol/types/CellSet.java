package com.umnhoj.gol.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CellSet {
	final Set<Cell> cells = new LinkedHashSet<>();

	public CellSet(final Collection<? extends Cell> cells) {
		this.cells.addAll(cells);
	}

	public boolean contains(final Cell cell) {
		return this.cells.contains(cell);
	}

	public int countNeighbors(final Cell cell) {
		return CellSet.mapNeighbors(cell, c -> {
			if (!(c.equals(cell)) && this.contains(c)) {
				return 1;
			}
			return 0;
		}).stream().reduce(Integer::sum).orElse(0);
	}

	public static <T> Collection<T> mapNeighbors(final Cell cell, final Function<Cell, T> func) {
		final List<T> retVal = new ArrayList<>();
		for (int i = cell.getX() - 1; i <= cell.getX() + 1; i++) {
			for (int j = cell.getY() - 1; j <= cell.getY() + 1; j++) {
				retVal.add(func.apply(new Cell(i, j)));
			}
		}
		return retVal;
	}
}
