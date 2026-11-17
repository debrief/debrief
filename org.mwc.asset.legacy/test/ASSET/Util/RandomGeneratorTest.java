/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package ASSET.Util;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Util.RandomGenerator.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ASSET.Util.RandomGenerator.*;
import org.junit.jupiter.api.Test;

import java.util.Random;

//////////////////////////////////////////////////
// property testing
//////////////////////////////////////////////////
public class RandomGeneratorTest extends SupportTesting {

	@Test
	public void testConstrainedGaussian() {
		final double min = 60;
		final double max = 140;
		final int num = 10000;
		for (int i = 0; i < num; i++) {
			final double nextV = RandomGenerator.generateRandomNumber(min, max, NORMAL_CONSTRAINED);
			assertTrue(nextV >= min, "slipped outside lower bounds at num:" + i);
			assertTrue(nextV <= max, "slipped outside upper bounds at num:" + i);
		}
	}

	@Test
	public void testGaussian() {
		final double min = 60;
		final double max = 140;
		final int num = 10000;
		boolean lowerExceeded = false;
		boolean upperExceeded = false;
		for (int i = 0; i < num; i++) {
			final double nextV = RandomGenerator.generateRandomNumber(min, max, NORMAL);
			if (nextV < min)
				lowerExceeded = true;
			if (nextV > max)
				upperExceeded = true;
		}

		assertTrue(lowerExceeded, "didn't go off bottom of range");
		assertTrue(upperExceeded, "didn't go off top of range");
	}

	@Test
	public void testUniform() {
		final double min = 60;
		final double max = 140;
		final int num = 10000;
		for (int i = 0; i < num; i++) {
			final double nextV = RandomGenerator.generateRandomNumber(min, max, UNIFORM);
			assertTrue(nextV >= min, "slipped outside lower bounds at num:" + i);
			assertTrue(nextV <= max, "slipped outside upper bounds at num:" + i);
		}
	}
}
