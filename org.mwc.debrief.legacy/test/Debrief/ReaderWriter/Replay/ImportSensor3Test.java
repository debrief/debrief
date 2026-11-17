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

package Debrief.ReaderWriter.Replay;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.ReaderWriter.Replay.ImportSensor3.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.ReaderWriter.Replay.ImportSensor3.*;
import java.text.ParseException;
import java.util.StringTokenizer;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class ImportSensor3Test {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testImport() throws ParseException {
		// final String lineA =
		// ";SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL
		// Contact_bearings 0414";
		// final String lineB =
		// ";SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL
		// \"Contact bearings\" 0414";
		// final String lineTabs =
		// ";SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL
		// \"Contact bearings\" 0414";
		// final String lineD =
		// ";SENSOR2: 20090722 041434.000 \"NON SUCH\" @B NULL 59.3 NULL NULL NULL
		// \"Contact bearings\" 0414";

		// NOTE: This test just uses the mangled form of SENSOR3 output that we get from
		// system G.
		final String lineA = ";SENSOR3: 20090722 041434.000 NULL @@ NULL 0.0 NULL 155.55 0.001 NULL \"UNKNOWN\" \"UNKNOWN\"";

		final ImportSensor3 is2 = new ImportSensor3();
		final SensorContactWrapper resA = (SensorContactWrapper) is2.readThisLine(lineA);
		assertEquals("NULL", resA.getTrackName(), "lineA failed");
		assertEquals("UNKNOWN", resA.getSensorName(), "lineA failed");
		assertEquals("\"UNKNOWN\"", resA.getLabel(), "lineA failed");
		assertEquals(155.55, resA.getFrequency(), 0.001, "correct freq");
	}
}
