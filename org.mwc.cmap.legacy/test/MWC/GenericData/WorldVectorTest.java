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

import static MWC.GenericData.WorldVector.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GenericData.WorldVector.*;
import java.io.Serializable;
import MWC.Algorithms.Conversions;
import MWC.Algorithms.EarthModels.FlatEarth;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class WorldVectorTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";
	private WorldVector wv1 = null;


	@BeforeEach
	public final void setUp() {
		// set the earth model we are expecting
		WorldLocation.setModel(new FlatEarth());
		wv1 = new WorldVector(4.3, 12.0, 45.0);
	}

	@AfterEach
	public final void tearDown() {
		wv1 = null;
	}

	@Test
	public final void testConstructor() {
		assertEquals(wv1.getBearing(), 4.3, 0.00001, "check bearing rads");
		assertEquals(wv1.getRange(), 12.0, 0.0001, "check range degs");
		assertEquals(wv1.getDepth(), 45.0, 0.0001, "check depth m");
	}

	@Test
	public final void testSetter() {
		wv1.setValues(33.0, 44.0, 55.0);
		assertEquals(wv1.getBearing(), 33.0, 0.00001, "check bearing rads");
		assertEquals(wv1.getRange(), 44.0, 0.0001, "check range degs");
		assertEquals(wv1.getDepth(), 55.0, 0.0001, "check depth m");
	}
}
