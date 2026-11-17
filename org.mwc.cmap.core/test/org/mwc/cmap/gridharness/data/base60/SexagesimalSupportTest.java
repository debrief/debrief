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

package org.mwc.cmap.gridharness.data.base60;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.cmap.gridharness.data.base60.SexagesimalSupport.*;
import org.mwc.cmap.gridharness.data.base60.SexagesimalSupport.*;
import java.text.NumberFormat;
import java.text.ParseException;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class SexagesimalSupportTest {
	@Test
	public void testNearGreenwich() {
		WorldLocation loc = new WorldLocation(1, 0, 0, 'N', 0, 0, 30, 'E', 0);
		String res = _DD_MM_MMM.convertToString(loc);
		assertEquals("01\u00B000.000\u2032N 000\u00B000.500\u2032E",
				res, "Result was not 1 degree, 0 minutes 0 seconds North, 0 degrees 0 minutes 30 seconds East");
		loc = new WorldLocation(1, 0, 0, 'N', 1, 0, 30, 'W', 0);
		res = _DD_MM_MMM.convertToString(loc);
		assertEquals("01\u00B000.000\u2032N 001\u00B000.500\u2032W",
				res, "Result was not 1 degree, 0 minutes 0 seconds North, 0 degrees 0 minutes 30 seconds West");
		loc = new WorldLocation(1, 0, 0, 'N', 0, 0, 30, 'W', 0);
		res = _DD_MM_MMM.convertToString(loc);
		assertEquals("01\u00B000.000\u2032N 000\u00B000.500\u2032W",
				res, "Result was not 1 degree, 0 minutes 0 seconds North, 0 degrees 0 minutes 30 seconds West");
		loc = new WorldLocation(0, 0, 30, 'N', 1, 0, 00, 'W', 0);
		res = _DD_MM_MMM.convertToString(loc);
		assertEquals("00\u00B000.500\u2032N 001\u00B000.000\u2032W",
				res, "Result was not 0 degree, 0 minutes 30 seconds North, 1 degrees 0 minutes 0 seconds West");
		loc = new WorldLocation(0, 0, 30, 'N', 0, 0, 00, 'E', 0);
		res = _DD_MM_MMM.convertToString(loc);
		assertEquals("00\u00B000.500\u2032N 000\u00B000.000\u2032E",
				res, "Result was not 0 degrees, 0 minutes 30 seconds North, 0 degrees 0 minutes 0 seconds East");
		loc = new WorldLocation(0.5, -0.5, 0);
		res = _DD_MM_MMM.convertToString(loc);
		assertEquals("00\u00B030.000\u2032N 000\u00B030.000\u2032W", res, "correct conversion");
		loc = new WorldLocation(0.5, -0.5, 0);
		res = _DD_MM_SS_SSS.convertToString(loc);
		assertEquals("00\u00B030\u203200.000\u2033N 000\u00B030\u203200.000\u2033W", res, "correct conversion");
		loc = new WorldLocation(0.5, 0.5, 0);
		res = _DD_MM_SS_SSS.convertToString(loc);
		assertEquals("00\u00B030\u203200.000\u2033N 000\u00B030\u203200.000\u2033E", res, "correct conversion");

	}
}
