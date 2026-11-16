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

import static Debrief.ReaderWriter.Replay.ImportSensor.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import Debrief.ReaderWriter.Replay.ImportSensor.*;
import java.text.ParseException;
import java.util.StringTokenizer;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import org.junit.jupiter.api.Test;

public class ImportSensorTest {

	static public final String TEST_ALL_TEST_TYPE = "CONV";

	@Test
	public void testValues1() throws ParseException {
		// ;SENSOR: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B RRR
		// XXX YYY.....YYY

		final String iLine = ";SENSOR: 100112 121314 T23 @A NULL 90.0 40503.2 \"Plain Cookie\" FisherOne held on Plain Cookie";
		final AbstractPlainLineImporter iff = new ImportSensor();
		final SensorContactWrapper res = (SensorContactWrapper) iff.readThisLine(iLine);

		assertNotNull(res, "Read it in");

		// and check the result
		assertNotNull(res, "read it in");
		assertEquals("FisherOne held on Plain Cookie", res.getLabel(), "right track");
		assertEquals(new java.awt.Color(0, 100, 189), res.getColor(), "right color");
		assertEquals("Plain Cookie", res.getSensorName(), "right sensor");
		assertEquals("1213", FormatRNDateTime.toShortString(res.getDTG().getDate().getTime()), "correct date");
		assertEquals("121213.14", FormatRNDateTime.toString(res.getDTG().getDate().getTime()), "correct date");
		assertEquals(90d, res.getBearing(), 0.01, "correct bearing");
		assertEquals(40503.2, res.getRange().getValueIn(WorldDistance.YARDS), 0.001, "correct range");
		assertEquals("T23", res.getTrackName(), "correct track");
		assertEquals("100112 121314", res.getName(), "right name");
		assertEquals(DebriefColors.BLUE, res.getColor(), "correct color");
	}

	@Test
	public void testValuesNANBearing() throws ParseException {
		// ;SENSOR: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B RRR
		// XXX YYY.....YYY

		final String iLine = ";SENSOR: 100112 121314 T23 @A NULL NAN 40503.2 \"Plain Cookie\" FisherOne held on Plain Cookie";
		final AbstractPlainLineImporter iff = new ImportSensor();
		final SensorContactWrapper res = (SensorContactWrapper) iff.readThisLine(iLine);

		assertNotNull(res, "Read it in");

		// and check the result
		assertNotNull(res, "read it in");
		assertEquals("FisherOne held on Plain Cookie", res.getLabel(), "right track");
		assertEquals(new java.awt.Color(0, 100, 189), res.getColor(), "right color");
		assertEquals("100112 121314", res.getName(), "right name");
	}

	@Test
	public void testValuesNullBearing() throws ParseException {
		// ;SENSOR: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B RRR
		// XXX YYY.....YYY

		final String iLine = ";SENSOR: 100112 121314 T23 @A NULL NULL 40503.2 \"Plain Cookie\" FisherOne held on Plain Cookie";
		final AbstractPlainLineImporter iff = new ImportSensor();
		final SensorContactWrapper res = (SensorContactWrapper) iff.readThisLine(iLine);

		assertNotNull(res, "Read it in");

		// and check the result
		assertNotNull(res, "read it in");
		assertEquals("FisherOne held on Plain Cookie", res.getLabel(), "right track");
		assertEquals(new java.awt.Color(0, 100, 189), res.getColor(), "right color");
		assertEquals("100112 121314", res.getName(), "right name");
	}

}
