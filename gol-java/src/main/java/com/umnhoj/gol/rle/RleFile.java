package com.umnhoj.gol.rle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import com.umnhoj.gol.types.Cell;

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

	public List<Cell> getCells() {
		return this.cells;
	}

	public RleFile(final int width, final int height, final ArrayList<Cell> cells) {
		this.width = width;
		this.height = height;
		this.cells = cells;
	}

	public static RleFile parse(final Path path) throws IOException {
		return RleFile.parse(Files.newInputStream(path));
	}

	public static RleFile parse(final InputStream inputStream) throws IOException {
		// Sigh.. this may be improved
		final MutableInt _height = new MutableInt();
		final MutableInt _width = new MutableInt();
		final StringBuffer rleLine = new StringBuffer();

		String line;
		try (final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
			while ((line = in.readLine()) != null) {
				if (line.isEmpty()) {
					// ignore blank lines
					continue;
				}
				final char start = Character.toLowerCase(line.charAt(0));
				if (start == '#' && line.length() > 1) {
					final char second = Character.toUpperCase(line.charAt(1));
					switch (second) {
					case 'C':
					case 'N':
					case 'O':
					case 'P':
					case 'R':
					default:
						// do nothing for now
					}
				} else if (Character.isDigit(start) || start == 'b' || start == 'o' || start == '$' || start == '!') {
					rleLine.append(line);
				} else if (start == 'x') {
					final String[] tokens = StringUtils.split(line, ',');
					for (final String token : tokens) {
						final String[] split = StringUtils.split(token, '=');
						if (split.length != 2) {
							throw new RuntimeException("Bad dimention line in RLE file: " + line);
						}
						switch (split[0].trim()) {
						case "x":
							_width.setValue(Integer.parseInt(split[1].trim()));
						case "y":
							_height.setValue(Integer.parseInt(split[1].trim()));
						default:
						}
					}
				} else {
					throw new RuntimeException("Bad line in RLE file: " + line);
				}
			}
		}

		return new RleFile(_width.toInteger(), _height.toInteger(), parseCells(rleLine));
	}

	/**
	 * Parse RLE string into cells
	 */
	protected static ArrayList<Cell> parseCells(final StringBuffer rleLine) {
		final ArrayList<Cell> _cells = new ArrayList<>();

		// Parse the RLE into cells
		int currY = 0;
		int currX = 0;
		final StringBuffer currCount = new StringBuffer();
		for (char c : rleLine.toString().toCharArray()) {
			final int count = parseLength(currCount);
			switch (Character.toLowerCase(c)) {
			case 'b': {
				currX += count;
				currCount.setLength(0);
			}
				break;
			case 'o': {
				for (int i = 0; i < count; i++) {
					_cells.add(new Cell(currX, currY));
					currX++;
				}
				currCount.setLength(0);
			}
				break;
			case '$': {
				currX = 0;
				currY += count;
				currCount.setLength(0);
			}
				break;
			case '!':
				break;
			default:
				if (Character.isDigit(c)) {
					currCount.append(c);
				} else {
					throw new RuntimeException("Bad RLE string in file: " + rleLine);
				}
			}
		}
		return _cells;
	}

	/**
	 * By definition an unspecified run length is 1
	 */
	protected static int parseLength(final StringBuffer currCount) {
		return currCount.length() == 0 ? 1 : Integer.parseInt(currCount.toString());
	}
}
