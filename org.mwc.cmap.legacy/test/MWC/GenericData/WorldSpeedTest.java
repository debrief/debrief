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

package MWC.GenericData;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GenericData.WorldSpeed.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GenericData.WorldSpeed.*;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class WorldSpeedTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testStrings() {
		WorldSpeed da = new WorldSpeed(12, Kts);
		String res = da.toString();
		assertEquals(res, "12.0 kts", "correct output format");

		da = new WorldSpeed(1.75, M_sec);
		res = da.toString();
		assertEquals(res, "1.75 m/s", "correct output format");
	}

	@Test
	public final void testWorldDistanceUnits() {
		final WorldSpeed w1 = new WorldSpeed(1, WorldSpeed.M_sec);
		assertEquals(w1.getValueIn(WorldSpeed.M_sec), 1d, 0.000001, "m sec correct");

		final WorldSpeed w2 = new WorldSpeed(1, WorldSpeed.Kts);
		assertEquals(1, w2._mySpeed, 0.001, "correct value stored");
		assertEquals(WorldSpeed.Kts, w2._myUnits, 0.001, "correct unts stored");
		assertEquals(w2.getValueIn(WorldSpeed.M_sec), 0.5144444, 0.000001, "m/sec correct");
		assertEquals(w2.getValueIn(WorldSpeed.Kts), 1, 0.000001, "kts correct");

		final WorldSpeed w3 = new WorldSpeed(1, WorldSpeed.ft_sec);
		assertEquals(0.3048, w3.getValueIn(WorldSpeed.M_sec), 0.000001, "m/sec correct");
		assertEquals(1, w3.getValueIn(WorldSpeed.ft_sec), 0.000001, "ft/sec correct");

		final WorldSpeed w4 = new WorldSpeed(1, WorldSpeed.ft_min);
		assertEquals(0.00508, w4.getValueIn(WorldSpeed.M_sec), 0.000001, "m/sec correct");
		assertEquals(1, w4.getValueIn(WorldSpeed.ft_min), 0.000001, "ft/sec correct");

		assertEquals(w2.getValueIn(WorldSpeed.Kts), 1, 0.000001, "Back to kts correct");

		final WorldSpeed w5 = new WorldSpeed(w3);
		assertEquals(w5.getValueIn(WorldSpeed.ft_sec), 1, 0.000001, "Copy constructor");

		assertEquals(WorldSpeed.getLabelFor(WorldSpeed.M_sec), "m/s", "retrieve m/s label");
		assertEquals(WorldSpeed.getLabelFor(WorldSpeed.ft_sec), "ft/s", "retrieve ft/s label");
		assertEquals(WorldSpeed.getLabelFor(WorldSpeed.Kts), "kts", "retrieve kts label");

		// test conversions
		final WorldDistance w6 = new WorldDistance(10, WorldDistance.YARDS);
		assertEquals(30, w6.getValueIn(WorldDistance.FT), 0.001, "valid units conversion");

	}
}
