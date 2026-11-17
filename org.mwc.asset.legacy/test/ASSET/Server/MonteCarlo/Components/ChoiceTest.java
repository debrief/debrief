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

package ASSET.Server.MonteCarlo.Components;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Server.MonteCarlo.Components.Choice.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ASSET.Server.MonteCarlo.Components.Choice.*;
import java.util.Vector;
import ASSET.Util.RandomGenerator;
import org.junit.jupiter.api.Test;

public class ChoiceTest {

	@Test
	public void testDistribution() {
		final Choice choice = new Choice();
		choice.add("a");
		choice.add("b");
		choice.add("c");

		final int size = 1000000;
		int numA = 0;
		int numB = 0;
		int numC = 0;

		for (int i = 0; i < size; i++) {
			final String thisVal = choice.getNewValue();
			if (thisVal.equals("a")) {
				numA++;
			}
			if (thisVal.equals("b")) {
				numB++;
			}
			if (thisVal.equals("c")) {
				numC++;
			}
		}

		// check the dist
		assertEquals(size / 3, numA, 2000, "correct num of a");
		assertEquals(size / 3, numB, 2000, "correct num of b");
		assertEquals(size / 3, numC, 2000, "correct num of c");

	}

	@Test
	public void testGenerate() {
		final Choice choice = new Choice();
		choice.add("a");
		choice.add("b");
		choice.add("c");

		assertEquals(3, choice._myChoices.size(), 0, "they got stored");

		assertTrue(choice._myChoices.contains("a"), "found a");
		assertTrue(choice._myChoices.contains("b"), "found b");
		assertTrue(choice._myChoices.contains("c"), "found c");
	}
}
