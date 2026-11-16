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

import static Debrief.ReaderWriter.Replay.ImportSensor2.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import Debrief.ReaderWriter.Replay.ImportSensor2.*;
import java.text.ParseException;
import java.util.StringTokenizer;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class ImportSensor2Test {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testImport() throws ParseException {
		final String lineA = ";SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL Contact_bearings 0414";
		final String lineB = ";SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL \"Contact bearings\" 0414";
		final String lineTabs = ";SENSOR2:	20090722	041434.000	NONSUCH	@B	NULL	59.3	300.8	49.96	NULL	\"Contact bearings\"	0414";
		final String lineD = ";SENSOR2: 20090722 041434.000 \"NON SUCH\" @B NULL 59.3 NULL NULL NULL \"Contact bearings\" 0414";

		final ImportSensor2 is2 = new ImportSensor2();
		final SensorContactWrapper resA = (SensorContactWrapper) is2.readThisLine(lineA);
		assertEquals("Contact_bearings", resA.getSensorName(), "lineA failed");
		assertEquals("0414", resA.getLabel(), "lineA failed");
		final SensorContactWrapper resB = (SensorContactWrapper) is2.readThisLine(lineB);
		assertEquals("0414", resB.getLabel(), "lineB failed");
		final SensorContactWrapper resC = (SensorContactWrapper) is2.readThisLine(lineTabs);
		assertEquals("0414", resC.getLabel(), "lineTabs failed");
		final SensorContactWrapper resD = (SensorContactWrapper) is2.readThisLine(lineD);
		assertEquals("0414", resD.getLabel(), "lineD failed");
	}

	@Test
	public final void testInvalidImport() {
		final String lineA = ";SENSOR2: 20090022 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL Contact_bearings 0414";

		final ImportSensor2 is2 = new ImportSensor2();
		SensorContactWrapper resA = null;
		try {
			resA = (SensorContactWrapper) is2.readThisLine(lineA);
		} catch (final ParseException e) {
		}
		assertNull(resA, "should have failed");
	}
}
