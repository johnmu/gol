package com.umnhoj.gol.rle;

import java.util.ArrayList;

/**
 * Defined at http://conwaylife.com/wiki/Run_Length_Encoded
 *
 * @author johnmu
 *
 */
public class RleFile {
	protected final int width;
	protected final int height;

	protected final ArrayList<Cell> cells;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.cells == null) ? 0 : this.cells.hashCode());
		result = prime * result + this.height;
		result = prime * result + this.width;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final RleFile other = (RleFile) obj;
		if (this.cells == null) {
			if (other.cells != null) {
				return false;
			}
		} else if (!this.cells.equals(other.cells)) {
			return false;
		}
		if (this.height != other.height) {
			return false;
		}
		if (this.width != other.width) {
			return false;
		}
		return true;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public ArrayList<Cell> getCells() {
		return this.cells;
	}

	public RleFile(final int width, final int height, final ArrayList<Cell> cells) {
		super();
		this.width = width;
		this.height = height;
		this.cells = cells;
	}
}
