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

package Debrief.ReaderWriter.FlatFile;

import static Debrief.ReaderWriter.FlatFile.FlatFileExporter.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.FlatFile.FlatFileExporter.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.ArrayOffsetHelper.LegacyArrayOffsetModes;
import Debrief.Wrappers.Track.Doublet;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////
// TEST THIS CLASS
// ////////////////////////////////////////////////////////////////
public final class FlatFileExporterTest {

	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	private void dumpToFile(final String str, final String filename) {
		final File outFile = new File(filename);
		FileWriter out = null;
		try {
			out = new FileWriter(outFile);
			out.write(str);
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getTestData(final String fName) {
		String res = null;
		try {
			res = readFileAsString(fName);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	// TODO FIX-TEST
	public void NtestAgainstSample1() throws IOException {
		final String TARGET_STR = getTestData("src/Debrief/ReaderWriter/FlatFile/fakedata.txt");
		assertNotNull(TARGET_STR, "test data found");
		final FlatFileExporter fa = new FlatFileExporter();
		String res = null;
		try {
			res = fa.testExport("V1.0");
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		assertNotNull(res, "produced string");

		assertEquals(TARGET_STR, res, "correct string");

		dumpToFile(res, "src/Debrief/ReaderWriter/FlatFile/data_out.txt");
	}

	// TODO FIX-TEST
	public void NtestAgainstSample2() throws IOException {
		// collate the data
		final TrackWrapper primary = new TrackWrapper();
		final TrackWrapper secondary = new TrackWrapper();

		for (int i = 0; i < 20; i++) {
			final double lat = 2 + i / 3600;
			final double lon = 3 + i / 3700;
			final double depth = 19 + i / 10;
			final WorldLocation wa = new WorldLocation(lat, -lon, depth);
			final WorldLocation wb = new WorldLocation(-lat, lon, depth + 25);
			final Fix fa = new Fix(new HiResDate(10000 + i * 1000), wa, 45, 12);
			final Fix fb = new Fix(new HiResDate(10004 + i * 1000), wb, 45, 12);
			final FixWrapper fwa = new FixWrapper(fa);
			final FixWrapper fwb = new FixWrapper(fb);
			primary.addFix(fwa);
			secondary.addFix(fwb);
		}

		final SensorWrapper sa = new SensorWrapper("sensor-A");
		sa.setHost(primary);
		sa.setVisible(true);
		primary.add(sa);
		final SensorWrapper sb = new SensorWrapper("sensor-B");
		sb.setHost(primary);
		sb.setVisible(true);
		primary.add(sb);
		for (int i = 0; i < 15; i++) {
			// primary.getName(),,
			final SensorContactWrapper sca = new SensorContactWrapper();
			sca.setBearing(i * 3);
			sca.setSensor(sa);
			sca.setDTG(new HiResDate(11000 + i * 950));
			sa.add(sca);
		}

		for (int i = 0; i < 15; i++) {
			// primary.getName(),,
			final SensorContactWrapper sca = new SensorContactWrapper();
			sca.setBearing(i * 1.6);
			sca.setSensor(sa);
			sca.setDTG(new HiResDate(10900 + i * 930));
			sb.add(sca);
		}

		final WatchableList[] secList = new WatchableList[] { secondary };
		final HiResDate start = new HiResDate(10000);
		final HiResDate end = new HiResDate(50000);
		final TimePeriod period = new TimePeriod.BaseTimePeriod(start, end);
		final String s1Type = "s1_type";
		final String s2Type = "s2_TYPE";
		final String protMarking = "PROT_MARKING";
		final String serName = "SER_NAME";

		// do the export
		final FlatFileExporter fa = new FlatFileExporter();
		final String res = fa.export(primary, secList, period, s1Type, s2Type, null, null, null, null,
				INITIAL_VERSION, protMarking, serName, 1600d);

		// get the test data
		final String TARGET_STR = getTestData("src/Debrief/ReaderWriter/FlatFile/fakedata2.txt");
		assertNotNull(TARGET_STR, "test data found");

		// and now the checking
		assertNotNull(res, "produced string");
		assertEquals(TARGET_STR, res, "correct string");

		dumpToFile(res, "src/Debrief/ReaderWriter/FlatFile/data_out.txt");
	}

	private String readFileAsString(final String filePath) throws java.io.IOException {
		final StringBuffer fileData = new StringBuffer(1000);
		final BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			final String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	@Test
	public void testDateFormat() throws ParseException {
		final Date theDate = dateFrom("01:45:00	22/12/2002");
		final String val = formatThis(theDate);
		assertEquals("01:45:00	22/12/2002", val, "correct start date");
	}

	@Test
	public void testGetNearestCut() {
		final SensorWrapper sa = new SensorWrapper("sensor-A");
		for (int i = 1; i < 5; i++) {
			// primary.getName(),,
			final SensorContactWrapper sca = new SensorContactWrapper();
			sca.setDTG(new HiResDate(i * 10));
			sca.setLabel("" + i * 10);
			sa.add(sca);
		}

		SensorContactWrapper fw = FlatFileExporter.nearestCutTo(sa, new HiResDate(5));
		assertNull(fw, "should not return one - before data starts");
		fw = FlatFileExporter.nearestCutTo(sa, new HiResDate(11));
		assertEquals("10", fw.getLabel(), "got first one");
		fw = FlatFileExporter.nearestCutTo(sa, new HiResDate(19));
		assertEquals("20", fw.getLabel(), "got correct one");
		fw = FlatFileExporter.nearestCutTo(sa, new HiResDate(85));
		assertNull(fw, "should not return one - after data finishes");
		fw = FlatFileExporter.nearestCutTo(sa, new HiResDate(40));
		assertEquals("40", fw.getLabel(), "got last one");
		fw = FlatFileExporter.nearestCutTo(sa, new HiResDate(35));
		assertEquals("40", fw.getLabel(), "got correct one");
	}
}
