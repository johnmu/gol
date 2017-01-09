package com.umnhoj.gol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.umnhoj.gol.rle.RleFile;

public class GameOfLifeTest extends GameOfLife {

	@Before
	public void setUp() throws Exception {
		this.numGenerations = 3;
	}

	@Test
	public void testRunIterations() throws IOException {
		final RleFile rleFile = RleFile.parse(GameOfLifeTest.class.getResourceAsStream("simple_oscillator.rle"));
		final byte[] expected = IOUtils.toByteArray(GameOfLifeTest.class.getResourceAsStream("simple_oscillator.out"));
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GameOfLife.printGenerations(baos, this.runIterations(rleFile));
		final byte[] actual = baos.toByteArray();
		Assert.assertArrayEquals(expected, actual);
	}

}
