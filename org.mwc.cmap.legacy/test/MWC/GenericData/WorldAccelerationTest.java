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

import static MWC.GenericData.WorldAcceleration.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GenericData.WorldAcceleration.*;
import org.junit.jupiter.api.Test;


/////////////////////////////////////////////////////////////////
// member variables
/////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class WorldAccelerationTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testStrings() {
		WorldAcceleration da = new WorldAcceleration(12, Kts_sec);
		String res = da.toString();
		assertEquals(res, "12.0 kts/s", "correct output format");

		da = new WorldAcceleration(1.75, M_sec_sec);
		res = da.toString();
		assertEquals(res, "1.75 m/s/s", "correct output format");
	}

	@Test
	public final void testWorldDistanceUnits() {
		final WorldAcceleration w1 = new WorldAcceleration(1, WorldAcceleration.M_sec_sec);
		assertEquals(w1.getValueIn(WorldAcceleration.M_sec_sec), 1d, 0.000001, "m sec correct");

		final WorldAcceleration w2 = new WorldAcceleration(1, WorldAcceleration.Kts_sec);
		assertEquals(1, w2._myAccel, 0.001, "correct value stored");
		assertEquals(WorldAcceleration.Kts_sec, w2._myUnits, 0.001, "correct units stored");
		assertEquals(w2.getValueIn(WorldAcceleration.M_sec_sec), 0.5144444, 0.000001, "m/sec correct");
		assertEquals(w2.getValueIn(WorldAcceleration.Kts_sec), 1, 0.000001, "kts correct");

		assertEquals(w2.getValueIn(WorldAcceleration.Kts_sec), 1, 0.000001, "Back to kts correct");

		assertEquals(WorldAcceleration.getLabelFor(WorldAcceleration.M_sec_sec), "m/s/s", "retrieve m/s label");
		assertEquals(WorldAcceleration.getLabelFor(WorldAcceleration.Kts_sec), "kts/s", "retrieve kts label");

	}
}
