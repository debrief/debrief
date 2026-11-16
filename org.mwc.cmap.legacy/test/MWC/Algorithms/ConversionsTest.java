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

package MWC.Algorithms;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.Algorithms.Conversions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.Algorithms.Conversions.*;
import org.junit.jupiter.api.Test;


//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class ConversionsTest {
	static public final String TEST_ALL_TEST_TYPE = "CONV";


	@Test
	public void testValues() {
		final double degs = 1;
		assertEquals(60, Conversions.Degs2Nm(degs), 0.01, "degs 2 nm");
		assertEquals(111.12, Conversions.Degs2Km(degs), 0.01, "degs 2 km");
		assertEquals(111120.000, Conversions.Degs2m(degs), 0.01, "degs 2 m");
		assertEquals(121522.31, Conversions.Degs2Yds(degs), 0.01, "degs 2 yds");
		assertEquals(0.3048, Conversions.ft2m(1), 0.01, "ft 2 m");
		assertEquals(1, Conversions.m2ft(0.3048), 0.01, "m 2 ft");
		/**
		 * note, we're not using Excel value for degs2yds, but RN approximation (6080/3)
		 */
	}

	@Test
	public void testNormaliseDegs() {
		assertEquals(0d, Conversions.normaliseDegs(0), 1e-9);
		assertEquals(0d, Conversions.normaliseDegs(360), 1e-9);
		assertEquals(350d, Conversions.normaliseDegs(-10), 1e-9);
		assertEquals(10d, Conversions.normaliseDegs(730), 1e-9);
		assertEquals(180d, Conversions.normaliseDegs(-540), 1e-9);
		assertEquals(359.5, Conversions.normaliseDegs(-0.5), 1e-9);
	}

	@Test
	public void testSignedDegs() {
		assertEquals(-10d, Conversions.signedDegs(350), 1e-9);
		assertEquals(180d, Conversions.signedDegs(180), 1e-9);
		assertEquals(180d, Conversions.signedDegs(-180), 1e-9);
		assertEquals(-170d, Conversions.signedDegs(190), 1e-9);
		assertEquals(10d, Conversions.signedDegs(730), 1e-9);
		assertEquals(-10d, Conversions.signedDegs(-730), 1e-9);
		assertEquals(170d, Conversions.signedDegs(-550), 1e-9);
	}

	@Test
	public void testDegsDifference() {
		// from 350 to 010 is +20, not -340
		assertEquals(20d, Conversions.degsDifference(350, 10), 1e-9);
		assertEquals(-20d, Conversions.degsDifference(10, 350), 1e-9);
		assertEquals(-10d, Conversions.degsDifference(-175, 175), 1e-9);
		assertEquals(0d, Conversions.degsDifference(45, 405), 1e-9);
	}

	@Test
	public void testRadiansHelpers() {
		assertEquals(Math.PI * 1.5, Conversions.normaliseRads(-Math.PI / 2), 1e-9);
		assertEquals(0d, Conversions.normaliseRads(2 * Math.PI), 1e-9);
		assertEquals(-Math.PI / 2, Conversions.signedRads(Math.PI * 1.5), 1e-9);
		assertEquals(Math.PI, Conversions.signedRads(-Math.PI), 1e-9);
		assertEquals(Degs2Rads(20), Conversions.radsDifference(Degs2Rads(350), Degs2Rads(10)), 1e-9);
	}
}
