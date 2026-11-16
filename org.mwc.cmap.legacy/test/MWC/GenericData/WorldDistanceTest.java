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

import static MWC.GenericData.WorldDistance.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GenericData.WorldDistance.*;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class WorldDistanceTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testEquals() {
		final WorldDistance da = new WorldDistance(60, WorldDistance.MINUTES);
		final WorldDistance db = new WorldDistance(1, WorldDistance.DEGS);
		assertEquals(da, db, "distances should be equal");
	}

	@Test
	public final void testStrings() {
		WorldDistance da = new WorldDistance(12, METRES);
		String res = da.toString();
		assertEquals(res, "12 m", "correct output format");

		da = new WorldDistance(1.75, KM);
		res = da.toString();
		assertEquals(res, "1.75 km", "correct output format, received:" + res);
	}

	@Test
	public final void testWorldDistanceUnits() {
		final WorldDistance w1 = new WorldDistance(1, WorldDistance.NM);
		assertEquals(w1.getValueIn(WorldDistance.NM), 1d, 0.000001, "Minutes correct");

		final WorldDistance w2 = new WorldDistance(1852 * 2, WorldDistance.METRES);
		assertEquals(w2.getValueIn(WorldDistance.NM), 2d, 0.000001, "Metres correct");

		final WorldDistance w3 = new WorldDistance(5, WorldDistance.DEGS);
		assertEquals(w3.getValueIn(WorldDistance.NM), 300d, 0.000001, "Degrees correct");

		final WorldDistance w4 = new WorldDistance(1.852 / 22, WorldDistance.KM);
		assertEquals(w4.getValueIn(WorldDistance.NM), 1 / 22d, 0.000001, "Km correct");

		assertEquals(w2.getValueIn(WorldDistance.METRES), 1852 * 2d, 0.000001, "Back to Metres correct");

		final WorldDistance w5 = new WorldDistance(w4);
		assertEquals(w5.getValueIn(WorldDistance.NM), 1 / 22d, 0.000001, "Copy constructor");

		assertEquals(WorldDistance.getLabelFor(WorldDistance.METRES), "m", "retrieve metres label");
		assertEquals(WorldDistance.getLabelFor(WorldDistance.DEGS), "degs", "retrieve degs label");

		final WorldDistance w6 = new WorldDistance(2000, WorldDistance.YARDS);
		assertEquals(w6.getValueIn(WorldDistance.KYDS), 2d, 0.000001, "Kyds correct");

	}
}
