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

import static Debrief.ReaderWriter.Replay.ImportTMA_Pos.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import Debrief.ReaderWriter.Replay.ImportTMA_Pos.*;
import java.awt.Color;
import java.text.ParseException;
import java.util.StringTokenizer;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.Shapes.EllipseShape;
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
public final class ImportTMA_PosTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testImport() throws ParseException {

		// ;TMA_POS: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H
		// TT...TT OOO.O XXXX YYYY CCC SSS DDD xx.xx
		// ;; date, time, ownship name, symbology, tma lat, tma long, track name,
		// ellipse orientation (deg from north), maxima (yds), minima (yds),
		// course, speed, depth (m), label string

		final String testLine = ";TMA_POS: 030211 120312 CARPET S@ 22 11 10.63 N 21 41 52.37 W TRACK_060 045.0  4000 2000 050 12.4 100 Trial label";

		// ok, create the importer
		final ImportTMA_Pos importer = new ImportTMA_Pos();

		// see if we can read this type
		final String theType = importer.getYourType();
		assertEquals(theType, ";TMA_POS:", "returned correct type");

		// now read the line
		final Object res = importer.readThisLine(testLine);
		assertNotNull(res, "managed to read item");

		// check it's of the correct type
		assertEquals("class Debrief.Wrappers.TMAContactWrapper", res.getClass().toString(), "of correct class");
		final TMAContactWrapper tc = (TMAContactWrapper) res;

		// check the values we've used
		final HiResDate theDate = DebriefFormatDateTime.parseThis("030211 120312.000");
		assertEquals(theDate, tc.getDTG(), "correct date");
		assertEquals("CARPET", tc.getTrackName(), "Correct track");
		assertEquals(Color.white, tc.getColor(), "correct color");
		assertEquals("Submarine", tc.getSymbol(), "correct symbol");
		assertNotNull(tc.buildGetOrigin(), "correct origin");
		assertEquals("TRACK_060", tc.getSolutionName(), "correct solution name");
		assertEquals(45, tc.getEllipse().getOrientation(), 0.0001d, "correct orientation");
		assertEquals(4000, tc.getEllipse().getMaxima().getValueIn(WorldDistance.YARDS), 0.0001d, "correct maxima");
		assertEquals(2000, tc.getEllipse().getMinima().getValueIn(WorldDistance.YARDS), 0.0001d, "correct minima");
		assertEquals(50, tc.getTargetCourse(), 0.001d, "correct course");
		assertEquals(12.4, tc.getSpeed(), 0.001d, "correct speed");
		assertEquals(100, tc.getDepth(), 0.001d, "correct depth");
		assertEquals("Trial label", tc.getLabel(), "correct label");

	}

	@Test
	public final void testImportNoEllipse() throws ParseException {
		// ;TMA_POS: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H
		// TT...TT OOO.O XXXX YYYY CCC SSS DDD xx.xx
		// ;; date, time, ownship name, symbology, tma lat, tma long, track name,
		// ellipse orientation (deg from north), maxima (yds), minima (yds),
		// course, speed, depth (m), label string

		final String testLine = ";TMA_POS: 030211 120312 CARPET S@ 22 11 10.63 N 21 41 52.37 W TRACK_060 NULL 050 12.4 100 Trial label";

		// ok, create the importer
		final ImportTMA_Pos importer = new ImportTMA_Pos();

		// see if we can read this type
		final String theType = importer.getYourType();
		assertEquals(theType, ";TMA_POS:", "returned correct type");

		// now read the line
		final Object res = importer.readThisLine(testLine);
		assertNotNull(res, "managed to read item");

		// check it's of the correct type
		assertEquals("class Debrief.Wrappers.TMAContactWrapper", res.getClass().toString(), "of correct class");
		final TMAContactWrapper tc = (TMAContactWrapper) res;

		// check the values we've used
		final HiResDate theDate = DebriefFormatDateTime.parseThis("030211 120312.000");
		assertEquals(theDate, tc.getDTG(), "correct date");
		assertEquals("CARPET", tc.getTrackName(), "Correct track");
		assertEquals(Color.white, tc.getColor(), "correct color");
		assertEquals("Submarine", tc.getSymbol(), "correct symbol");
		assertNotNull(tc.buildGetOrigin(), "correct origin");
		assertEquals(Conversions.Yds2Degs(0), tc.getRange().getValueIn(WorldDistance.YARDS), 0.001d,
				"correct range");
		assertEquals(0, tc.getBearing(), 0.001d, "correct bearing");
		assertEquals("TRACK_060", tc.getSolutionName(), "correct solution name");
		assertEquals(0, tc.getEllipse().getOrientation(), 0.0001d, "correct orientation");
		assertEquals(0, tc.getEllipse().getMaxima().getValueIn(WorldDistance.YARDS), 0.0001d, "correct maxima");
		assertEquals(0, tc.getEllipse().getMinima().getValueIn(WorldDistance.YARDS), 0.0001d, "correct minima");
		assertEquals(50, tc.getTargetCourse(), 0.001d, "correct course");
		assertEquals(12.4, tc.getSpeed(), 0.001d, "correct speed");
		assertEquals(100, tc.getDepth(), 0.001d, "correct depth");
		assertEquals("Trial label", tc.getLabel(), "correct label");
	}

	@Test
	public final void testImportSpaces() throws ParseException {

		// ;TMA_POS: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H
		// TT...TT OOO.O XXXX YYYY CCC SSS DDD xx.xx
		// ;; date, time, ownship name, symbology, tma lat, tma long, track name,
		// ellipse orientation (deg from north), maxima (yds), minima (yds),
		// course, speed, depth (m), label string

		final String testLine = ";TMA_POS: 030211 120312 CARPET S@ 22 11 10.63 N 21 41 52.37 W \"TRACK 060\" 045.0  4000 2000 050 12.4 100 Trial label";

		// ok, create the importer
		final ImportTMA_Pos importer = new ImportTMA_Pos();

		// see if we can read this type
		final String theType = importer.getYourType();
		assertEquals(theType, ";TMA_POS:", "returned correct type");

		// now read the line
		final Object res = importer.readThisLine(testLine);
		assertNotNull(res, "managed to read item");

		// check it's of the correct type
		assertEquals("class Debrief.Wrappers.TMAContactWrapper", res.getClass().toString(), "of correct class");
		final TMAContactWrapper tc = (TMAContactWrapper) res;

		// check the values we've used
		assertEquals("TRACK 060", tc.getSolutionName(), "correct solution name");
	}
}
