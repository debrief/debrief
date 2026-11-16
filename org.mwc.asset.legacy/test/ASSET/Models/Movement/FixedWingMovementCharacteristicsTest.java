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

package ASSET.Models.Movement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Movement.FixedWingMovementCharacteristics.*;
import ASSET.Models.Movement.FixedWingMovementCharacteristics.*;
import MWC.GUI.Editable;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

//////////////////////////////////////////////////
// member objects
//////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class FixedWingMovementCharacteristicsTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return FixedWingMovementCharacteristics.getSampleChars();
	}

	@Test
	public void testTurningCircle() {
		double rate = 3;
		double speed = 60;
		double circle = FixedWingMovementCharacteristics._calcTurnCircle(rate, speed);
		assertEquals(2291.831, circle, 0.01, "correct turning circle calculated");

		rate = 3;
		speed = 0;
		circle = FixedWingMovementCharacteristics._calcTurnCircle(rate, speed);
		assertEquals(2 * _hoverTurnCircle, circle, 0.01, "correct turning circle calculated");

		rate = 0;
		speed = 60;
		circle = FixedWingMovementCharacteristics._calcTurnCircle(rate, speed);
		assertEquals(2 * _hoverTurnCircle, circle, 0.01, "correct turning circle calculated");

	}

}
