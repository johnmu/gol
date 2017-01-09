package com.umnhoj.gol.types;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CellSetTest {

	protected CellSet A;
	protected CellSet B;

	@Before
	public void before() {
		this.A = new CellSet(Arrays.asList(new Cell(0, 1), new Cell(0, 2), new Cell(0, 3)));
		this.B = new CellSet(Arrays.asList(new Cell(0, 1), new Cell(1, 2), new Cell(-1, 3)));
	}

	@Test
	public void testToStringA() {
		Assert.assertEquals("#\n#\n#\n", this.A.toString());
	}

	@Test
	public void testToStringB() {
		Assert.assertEquals(".#.\n..#\n#..\n", this.B.toString());
	}

}
