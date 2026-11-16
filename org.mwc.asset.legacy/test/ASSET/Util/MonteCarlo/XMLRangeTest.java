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

import static ASSET.Util.MonteCarlo.XMLRange.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ASSET.Util.MonteCarlo.XMLRange.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Vector;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

//////////////////////////////////////////////////
// property testing
//////////////////////////////////////////////////
public class XMLRangeTest extends SupportTesting {

	// TODO FIX-TEST
	public void NtestGaussianPerms() {
		final XMLRange newR = new XMLRange();
		newR.assign(140, 60, RandomGenerator.NORMAL, new Integer(120), null, null);

		// ok, check we haven't got the perms yet
		assertEquals(60, newR._min, 0, "wrong min");
		assertEquals(140, newR._max, 0, "wrong max");
		assertEquals(120, newR._numPerms.intValue(), "wrong num perms");
		assertEquals(RandomGenerator.NORMAL, newR._model, "wrong model");

		assertEquals(120, newR._myPerms.size(), 0, "wrong num perms generated");
		for (int i = 0; i < newR._myPerms.size(); i++) {
			final Double aDouble = newR._myPerms.elementAt(i);
			assertTrue(aDouble.doubleValue() > 60, "out of range");
			assertTrue(aDouble.doubleValue() < 140, "out of range");
		}

		// and check that we only get the right values
		final HashMap<Integer, Integer> scores = new HashMap<Integer, Integer>();
		for (int i = 0; i < 5000; i++) {
			newR.newPermutation();
			final Double thisVal = new Double(newR._currentValue);
			assertTrue(newR._myPerms.contains(thisVal), "not one of ours found");
			final int index = newR._myPerms.indexOf(thisVal);
			Integer res = scores.get(new Integer(index));
			if (res == null)
				res = new Integer(0);

			res = new Integer(res.intValue() + 1);
			scores.put(new Integer(index), res);
		}

	}

	@Test
	public void testNumPerms() {
		final XMLRange newR = new XMLRange();
		newR.assign(140, 60, RandomGenerator.UNIFORM, new Integer(12), null, null);

		// ok, check we haven't got the perms yet
		assertEquals(60, newR._min, 0, "wrong min");
		assertEquals(140, newR._max, 0, "wrong max");
		assertEquals(12, newR._numPerms.intValue(), "wrong num perms");
		assertEquals(RandomGenerator.UNIFORM, newR._model, "wrong model");

		assertEquals(12, newR._myPerms.size(), 0, "wrong num perms generated");
		for (int i = 0; i < newR._myPerms.size(); i++) {
			final Double aDouble = newR._myPerms.elementAt(i);
			assertTrue(aDouble.doubleValue() > 60, "out of range");
			assertTrue(aDouble.doubleValue() < 140, "out of range");
		}

		// and check that we only get the right values
		final HashMap<Integer, Integer> scores = new HashMap<Integer, Integer>();
		for (int i = 0; i < 5000; i++) {
			newR.newPermutation();
			final Double thisVal = new Double(newR._currentValue);
			assertTrue(newR._myPerms.contains(thisVal), "not one of ours found");
			final int index = newR._myPerms.indexOf(thisVal);
			Integer res = scores.get(new Integer(index));
			if (res == null)
				res = new Integer(0);

			res = new Integer(res.intValue() + 1);
			scores.put(new Integer(index), res);
		}

		// Iterator<Integer> freq = scores.keySet().iterator();
		// while (freq.hasNext())
		// {
		// Integer index = (Integer) freq.next();
		// Integer score = (Integer) scores.get(index);
		// }

	}
}
