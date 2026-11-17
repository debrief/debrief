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

package ASSET.Util.MonteCarlo;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Util.MonteCarlo.XMLChoice.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ASSET.Util.MonteCarlo.XMLChoice.*;
import java.util.Iterator;
import java.util.Vector;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ASSET.Util.RandomGenerator;

/***************************************************************
 * member variables
 ***************************************************************/

public class XMLChoiceTest {

	@Test
	public void test2WayDistribution() {
		final XMLChoice choice = new XMLChoice();
		choice._myList.add("a");
		choice._myList.add("b");

		final int size = 1000000;
		int numA = 0;
		int numB = 0;

		for (int i = 0; i < size; i++) {
			choice.newPermutation();
			final String thisVal = choice.getValue();
			if (thisVal.equals("a")) {
				numA++;
			}
			if (thisVal.equals("b")) {
				numB++;
			}
		}

		// check the dist
		assertEquals(size / 2, numA, 2000, "correct num of a");
		assertEquals(size / 2, numB, 2000, "correct num of b");

	}

	@Test
	public void test3WayDistribution() {
		final XMLChoice choice = new XMLChoice();
		choice._myList.add("a");
		choice._myList.add("b");
		choice._myList.add("c");

		final int size = 1000000;
		int numA = 0;
		int numB = 0;
		int numC = 0;

		for (int i = 0; i < size; i++) {
			choice.newPermutation();
			final String thisVal = choice.getValue();
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
		final XMLChoice choice = new XMLChoice();
		choice._myList.add("a");
		choice._myList.add("b");
		choice._myList.add("c");

		assertEquals(3, choice._myList.size(), 0, "they got stored");

		assertTrue(choice._myList.contains("a"), "found a");
		assertTrue(choice._myList.contains("b"), "found b");
		assertTrue(choice._myList.contains("c"), "found c");
	}
}
