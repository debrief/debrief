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

package MWC.Utilities.TextFormatting;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.Utilities.TextFormatting.BriefFormatLocation.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import MWC.Utilities.TextFormatting.BriefFormatLocation.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import MWC.GenericData.WorldLocation;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class BriefFormatLocationTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testFormatting() {
		// try to check we've successfully created our degrees symbol
		assertNotNull(DEGREE_SYMBOL, "failed to create degree symbol");
		assertEquals(1, DEGREE_SYMBOL.length(), "degree symbol wrong length");

		final WorldLocation la = new WorldLocation(0d, 0d, 0d);
		String res1 = " 00" + DEGREE_SYMBOL + "00\'00.00\" 000" + DEGREE_SYMBOL + "00\'00.00\"";
		Assertions.assertEquals(res1, BriefFormatLocation.toString(la), "first test");
		la.setLat(-12.345);
		la.setLong(22.432);
		res1 = " 12" + DEGREE_SYMBOL + "20\'42.00\"S 022" + DEGREE_SYMBOL + "25\'55.20\"E";
		Assertions.assertEquals(res1, BriefFormatLocation.toString(la), "second test");
		la.setLat(12.34533);
		la.setLong(-22.43222);
		res1 = " 12" + DEGREE_SYMBOL + "20\'43.19\"N 022" + DEGREE_SYMBOL + "25\'55.99\"W";
		Assertions.assertEquals(res1, BriefFormatLocation.toString(la), "third test");
		la.setLat(82.345);
		la.setLong(172.432);
		res1 = " 82" + DEGREE_SYMBOL + "20\'42.00\"N 172" + DEGREE_SYMBOL + "25\'55.20\"E";
		Assertions.assertEquals(res1, BriefFormatLocation.toString(la), "fourth test");

	}
}
