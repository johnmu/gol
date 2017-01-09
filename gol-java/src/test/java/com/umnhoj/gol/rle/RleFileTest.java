package com.umnhoj.gol.rle;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.umnhoj.gol.GameOfLifeTest;
import com.umnhoj.gol.types.Cell;

public class RleFileTest {

	@Test
	public void testParse() throws IOException {
		final RleFile rleFile = RleFile.parse(GameOfLifeTest.class.getResourceAsStream("simple_oscillator.rle"));
		assertEquals(4, rleFile.getWidth());
		assertEquals(5, rleFile.getHeight());
		assertEquals(Arrays.asList(new Cell(1, 0), new Cell(1, 1), new Cell(1, 2)), rleFile.getCells());
	}

}
