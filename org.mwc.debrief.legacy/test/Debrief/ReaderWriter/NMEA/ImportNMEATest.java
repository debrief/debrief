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

package Debrief.ReaderWriter.NMEA;

import static Debrief.ReaderWriter.NMEA.ImportNMEA.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.NMEA.ImportNMEA.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.Test;

public class ImportNMEATest {
	@SuppressWarnings("deprecation")
	@Test
	public void testDate() {
		final String dateStr = "20160720";
		final String timeStr = "082807.345";
		final Date date = State.dateFor(dateStr, timeStr);
		assertNotNull("got date");
		assertEquals("20 Jul 2016 08:28:07 GMT", date.toGMTString(), "got right date/time");
	}

	@Test
	public void testDegs() {
		assertEquals(34.159, degsFor("3409.5794,N"), 0.001, "got it");
		assertEquals(-15.621, degsFor("01537.3128,W"), 0.001, "got it");
		assertEquals(-34.159, degsFor("3409.5794,S"), 0.001, "got it");
		assertEquals(15.621, degsFor("01537.3128,E"), 0.001, "got it");
		assertEquals(-34.5, degsFor("3430.0,S"), 0.001, "got it");
		assertEquals(15.25, degsFor("01515.0,E"), 0.001, "got it");
		assertEquals(15.25, degsFor("01515.0,E"), 0.001, "got it");
		assertEquals(2.693, degsFor("00241.5907,E"), 0.001, "got it");
		assertEquals(36.2395, degsFor("3614.3708,N"), 0.001, "got it");
	}

	@Test
	public void testFullImportAllValues() throws Exception {
		final String testFile = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/NMEA_TRIAL.log";
		final File testI = new File(testFile);

		// only run the test if we have the log-file available
		assertTrue(testI.exists());

		InputStream is = new FileInputStream(testI);

		final Layers tLayers = new Layers();

		final ImportNMEA importer = new ImportNMEA(tLayers);
		importer.importThis(testFile, is, 0L, 0L, false);

		assertEquals(4, tLayers.size(), "got new layers");

		TrackWrapper tOne = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP_POS_GPS");
		assertEquals(12736, tOne.numFixes(), "found GPS cuts");

		TrackWrapper tTwo = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP-DR");
		assertEquals(21746, tTwo.numFixes(), "found GPS cuts");

		TrackWrapper tThree = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP_POS_CMP");
		assertEquals(87, tThree.numFixes(), "found GPS cuts");

		Layer contacts = tLayers.findLayer("WECDIS Contacts");
		assertEquals("WECDIS Contacts (14 items)", contacts.toString(), "loaded tracks");
		LightweightTrackWrapper aisTrack = (LightweightTrackWrapper) contacts.elements().nextElement();
		assertEquals(1250, aisTrack.numFixes(), "loaded lwt track");

		// try another import frequency
		tLayers.clear();

		assertEquals(0, tLayers.size(), "layers empty");

		is = new FileInputStream(testI);
		importer.importThis(testFile, is, 15000L, 15000L, false);

		assertEquals(4, tLayers.size(), "got new layers");

		tOne = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP_POS_GPS");
		assertEquals(4141, tOne.numFixes(), "found GPS cuts");
		assertEquals(1, tOne.getSegments().size(), "correct legs");

		tTwo = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP-DR");
		assertEquals(4816, tTwo.numFixes(), "found GPS cuts");
		assertEquals(1, tTwo.getSegments().size(), "correct legs");

		tThree = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP_POS_CMP");
		assertEquals(26, tThree.numFixes(), "found GPS cuts");
		assertEquals(1, tThree.getSegments().size(), "correct legs");

		contacts = tLayers.findLayer("WECDIS Contacts");
		assertEquals("WECDIS Contacts (14 items)", contacts.toString(), "loaded tracks");
		aisTrack = (LightweightTrackWrapper) contacts.elements().nextElement();
		assertEquals(744, aisTrack.numFixes(), "loaded lwt track");
	}

	@Test
	public void testFullImportAllValuesWithSplit() throws Exception {
		final String testFile = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/NMEA_TRIAL.log";
		final File testI = new File(testFile);

		// only run the test if we have the log-file available
		assertTrue(testI.exists());

		InputStream is = new FileInputStream(testI);

		final Layers tLayers = new Layers();

		final ImportNMEA importer = new ImportNMEA(tLayers);
		importer.importThis(testFile, is, 0L, 0L, false);

		assertEquals(4, tLayers.size(), "got new layers");

		TrackWrapper tOne = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP_POS_GPS");
		assertEquals(12736, tOne.numFixes(), "found GPS cuts");

		TrackWrapper tTwo = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP-DR");
		assertEquals(21746, tTwo.numFixes(), "found GPS cuts");

		TrackWrapper tThree = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP_POS_CMP");
		assertEquals(87, tThree.numFixes(), "found GPS cuts");

		Layer contacts = tLayers.findLayer("WECDIS Contacts");
		assertEquals("WECDIS Contacts (14 items)", contacts.toString(), "loaded tracks");
		LightweightTrackWrapper aisTrack = (LightweightTrackWrapper) contacts.elements().nextElement();
		assertEquals(1250, aisTrack.numFixes(), "loaded lwt track");

		// try another import frequency
		tLayers.clear();

		assertEquals(0, tLayers.size(), "layers empty");

		is = new FileInputStream(testI);
		importer.importThis(testFile, is, 15000L, 15000L, true);

		assertEquals(4, tLayers.size(), "got new layers");

		tOne = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP_POS_GPS");
		assertEquals(4141, tOne.numFixes(), "found GPS cuts");
		assertEquals(2, tOne.getSegments().size(), "correct legs");

		tTwo = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP-DR");
		assertEquals(4816, tTwo.numFixes(), "found GPS cuts");
		assertEquals(1, tTwo.getSegments().size(), "correct legs");

		tThree = (TrackWrapper) tLayers.findLayer("WECDIS_OWNSHIP_POS_CMP");
		assertEquals(26, tThree.numFixes(), "found GPS cuts");
		assertEquals(1, tThree.getSegments().size(), "correct legs");

		contacts = tLayers.findLayer("WECDIS Contacts");
		assertEquals("WECDIS Contacts (14 items)", contacts.toString(), "loaded tracks");
		aisTrack = (LightweightTrackWrapper) contacts.elements().nextElement();
		assertEquals(744, aisTrack.numFixes(), "loaded lwt track");
	}

	@SuppressWarnings({"deprecation"})
	@Test
	public void testKnownImport() {
		final String test1 = "$POSL,CONTACT,OC,DR,CHARLIE NAME,CHARLIE NAME,13.0,254.6,T,20160720,082807.345,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,3409.5794,N,01537.3128,W,0,,,*5D";
		final String test2 = "$POSL,VNM,HMS NONSUCH*03";
		final String test3 = "$POSL,POS,GPS,1122.2222,N,00712.6666,W,0.00,,Center of Rotation,N,,,,,*41";
		final String test4 = "$POSL,DZA,20160720,000000.859,0007328229*42";
		final String test5 = "$POSL,CONTACT,OC,DELETE,AIS 5,AIS 5,1.0,125.3,T,20160720,010059.897,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,1212.1234,N,12312.1234,W,0,,,*6E";
		final String test6 = "$POSL,POS2,GPS,4422.1122,N,00812.1111,W,0.00,,GPS Antenna,N,,,,,*5C";
		final String test7 = "$POSL,AIS,564166000,3606.3667,N,00522.3698,W,0,7.8,327.9,0,330.0,AIS1,0,0*06";
		final String test8 = "$POSL,PDS,9.2,M*03";
		final String test9 = "$POSL,VEL,GPS,276.3,4.6,,,*35";
		final String test10_drSpd = "$POSL,VEL,SPL,,,4.1,0.0,4.0*12";
		final String test11_drCrse = "$POSL,HDG,111.2,-04.1*7F";

		assertEquals(MsgType.CONTACT, parseType(test1), "Tgt POS");
		assertEquals(MsgType.VESSEL_NAME, parseType(test2), "Vessel name");
		assertEquals(MsgType.OS_POS, parseType(test3), "OS POS");
		assertEquals(MsgType.TIMESTAMP, parseType(test4), "Timestamp");

		// ok, let's try the ownship name
		assertEquals("HMS NONSUCH", parseMyName(test2), "got name");

		// and the AIS track fields
		final State aisState1 = parseContact(test1);
		assertNotNull(aisState1, "found state");
		assertEquals("CHARLIE NAME", aisState1.name, "got name");
		assertNotNull(aisState1.date, "found date");
		assertEquals("20 Jul 2016 08:28:07 GMT", aisState1.date.toGMTString(), "got date");
		assertNotNull(aisState1.location, "found location");
		assertEquals(34.1596, aisState1.location.getLat(), 0.001, "got lat");
		assertEquals(-15.622, aisState1.location.getLong(), 0.001, "got long");

		// and the AIS track fields
		final State aisState = parseContact(test5);
		assertNotNull(aisState, "found state");
		assertEquals("AIS 5", aisState.name, "got name");

		// and the ownship track fields
		final State oState1 = parseOwnship(test3, "test_name");
		assertNotNull(oState1, "found state");
		assertEquals("test_name_POS_GPS", oState1.name, "got name");
		assertNull(oState1.date, "found date");
		assertNotNull(oState1.location, "found location");
		assertEquals(11.370, oState1.location.getLat(), 0.001, "got lat");
		assertEquals(-7.211, oState1.location.getLong(), 0.001, "got long");

		// and the ownship track fields
		final State oState2 = parseOwnship(test6, "test_name2");
		assertNotNull(oState2, "found state");
		assertEquals("test_name2_POS2_GPS", oState2.name, "got name");
		assertNull(oState2.date, "found date");
		assertNotNull(oState2.location, "found location");
		assertEquals(44.368, oState2.location.getLat(), 0.001, "got lat");
		assertEquals(-8.201, oState2.location.getLong(), 0.001, "got long");

		// and the ownship track fields
		final State oState3 = parseAIS(test7);
		assertNotNull(oState3, "found state");
		assertEquals("564166000", oState3.name, "got name");
		assertNull(oState3.date, "found date");
		assertNotNull(oState3.location, "found location");

		// ok, let's try the ownship name
		assertEquals("20 Jul 2016 00:00:00 GMT", parseMyDate(test4).toGMTString(), "got time");

		assertEquals(9.2d, parseMyDepth(test8), 0.001, "got depth");

		assertEquals(276.3d, parseMyCourse(coursePatternGPS, test9), 0.001, "got course");
		assertEquals(4.6d, parseMySpeed(speedPatternGPS, test9), 0.001, "got speed");

		// and the DR equivalents
		assertEquals(4.1d, parseMySpeed(speedPatternLOG, test10_drSpd), 0.001, "got speed");
		assertEquals(111.2d, parseMyCourse(coursePatternHDG, test11_drCrse), 0.001, "got course");

	}

	@Test
	public void testMultiGPSImport() throws Exception {
		String test1 = "$POSL,CONTACT,OC,DR,CHARLIE NAME,CHARLIE NAME,13.0,254.6,T,20160720,082807.345,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,3409.5794,N,01537.3128,W,0,,,*5D\r\n";
		test1 += "$POSL,VNM,HMS NONSUCH*03\r\n";
		test1 += "$POSL,POS,GPS,1122.2222,N,00712.6666,W,0.00,,Center of Rotation,N,,,,,*41\r\n";
		test1 += "$POSL,DZA,20160720,000000.859,0007328229*42\r\n";
		test1 += "$POSL,CONTACT,OC,DELETE,AIS 5,AIS 5,1.0,125.3,T,20160720,013059.897,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,1212.1234,N,12312.1234,W,0,,,*6E\r\n";
		test1 += "$POSL,POS2,GPS,4422.1122,N,00812.1111,W,0.00,,GPS Antenna,N,,,,,*5C\r\n";
		test1 += "$POSL,AIS,564166000,3606.3667,N,00522.3698,W,0,7.8,327.9,0,330.0,AIS1,0,0*06\r\n";
		test1 += "$POSL,PDS,9.2,M*03\r\n";
		test1 += "$POSL,VEL,GPS,276.3,4.6,,,*35\r\n";
		test1 += "$POSL,CONTACT,OC,DELETE,AIS 5,AIS 5,1.0,125.3,T,20160720,010259.897,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,1212.1234,N,12312.1234,W,0,,,*6E\r\n";
		test1 += "$POSL,VEL,SPL,,,4.1,0.0,4.0*12\r\n";
		test1 += "$POSL,HDG,111.2,-04.1*7F\r\n";
		test1 += "$POSL,POS,GPS,1122.2222,N,00712.6666,W,0.00,,Center of Rotation,N,,,,,*41\r\n";
		test1 += "$POSL,DZA,20160720,000001.859,0007328229*42\r\n";
		test1 += "$POSL,CONTACT,OC,DELETE,AIS 5,AIS 5,1.0,125.3,T,20160720,110049.897,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,1212.1234,N,12312.1234,W,0,,,*6E\r\n";
		test1 += "$POSL,POS2,GPS,4422.1122,N,00812.1111,W,0.00,,GPS Antenna,N,,,,,*5C\r\n";
		test1 += "$POSL,AIS,564166000,3606.3667,N,00522.3698,W,0,7.8,327.9,0,330.0,AIS1,0,0*06\r\n";
		test1 += "$POSL,PDS,9.2,M*03\r\n";
		test1 += "$POSL,VEL,GPS,276.3,4.6,,,*35\r\n";
		test1 += "$POSL,AIS,564166022,3606.3667,N,00522.3698,W,0,7.8,327.9,0,330.0,AIS1,0,0*06\r\n";
		test1 += "$POSL,VEL,SPL,,,4.1,0.0,4.0*12\r\n";
		test1 += "$POSL,HDG,111.2,-04.1*7F\r\n";
		test1 += "$POSL,VNM,HMS NONSUCH*03\r\n";
		test1 += "$POSL,POS,GPS,1122.2222,N,00712.6666,W,0.00,,Center of Rotation,N,,,,,*41\r\n";
		test1 += "$POSL,DZA,20160720,000002.859,0007328229*42\r\n";
		test1 += "$POSL,CONTACT,OC,DELETE,AIS 5,AIS 5,1.0,125.3,T,20160720,020059.897,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,1212.1234,N,12312.1234,W,0,,,*6E\r\n";
		test1 += "$POSL,POS2,GPS,4422.1122,N,00812.1111,W,0.00,,GPS Antenna,N,,,,,*5C\r\n";
		test1 += "$POSL,AIS,564166000,3606.3667,N,00522.3698,W,0,7.8,327.9,0,330.0,AIS1,0,0*06\r\n";
		test1 += "$POSL,PDS,9.2,M*03\r\n";
		test1 += "$POSL,VEL,GPS,276.3,4.6,,,*35\r\n";
		test1 += "$POSL,VEL,SPL,,,4.1,0.0,4.0*12\r\n";
		test1 += "$POSL,HDG,111.2,-04.1*7F\r\n";
		test1 += "$POSL,POS,GPS,1122.2222,N,00712.6666,W,0.00,,Center of Rotation,N,,,,,*41\r\n";
		test1 += "$POSL,DZA,20160720,000003.859,0007328229*42\r\n";
		test1 += "$POSL,CONTACT,OC,DELETE,AIS 5,AIS 5,1.0,125.3,T,20160720,011059.897,FS,SFSP------^2a^2a^2a^2a^2a,0.0,M,1212.1234,N,12312.1234,W,0,,,*6E\r\n";
		test1 += "$POSL,POS2,GPS,4422.1122,N,00812.1111,W,0.00,,GPS Antenna,N,,,,,*5C\r\n";
		test1 += "$POSL,AIS,564166000,3606.3667,N,00522.3698,W,0,7.8,327.9,0,330.0,AIS1,0,0*06\r\n";
		test1 += "$POSL,PDS,9.2,M*03\r\n";
		test1 += "$POSL,VEL,GPS,276.3,4.6,,,*35\r\n";
		test1 += "$POSL,VEL,SPL,,,4.1,0.0,4.0*12\r\n";
		test1 += "$POSL,HDG,111.2,-04.1*7F\r\n";

		final InputStream is = new ByteArrayInputStream(test1.getBytes(StandardCharsets.UTF_8));
		final Layers layers = new Layers();

		assertEquals(0, layers.size(), "empty");

		final ImportNMEA importer = new ImportNMEA(layers);
		importer.importThis("file.log", is, 0, 0, false);

		assertEquals(4, layers.size(), "not empty");
		final LightweightTrackWrapper t1 = (LightweightTrackWrapper) layers.elementAt(0);
		final LightweightTrackWrapper t2 = (LightweightTrackWrapper) layers.elementAt(1);
		final LightweightTrackWrapper t3 = (LightweightTrackWrapper) layers.elementAt(2);
		final BaseLayer t4 = (BaseLayer) layers.elementAt(3);

		assertEquals("HMS NONSUCH_POS_GPS", t1.getName(), "correct name");
		assertEquals("HMS NONSUCH_POS2_GPS", t2.getName(), "correct name");
		assertEquals("HMS NONSUCH-DR", t3.getName(), "correct name");
		assertEquals("WECDIS Contacts", t4.getName(), "correct name");

		assertEquals(3, t1.numFixes(), "correct size");
		assertEquals(4, t2.numFixes(), "correct size");
		assertEquals(3, t3.numFixes(), "correct size");
		assertEquals(2, t4.size(), "correct size");

		final LightweightTrackWrapper a1 = (LightweightTrackWrapper) t4.first();
		final LightweightTrackWrapper a2 = (LightweightTrackWrapper) t4.last();

		assertEquals("564166000", a1.getName(), "correct name");
		assertEquals("564166022", a2.getName(), "correct name");

		assertEquals(4, a1.numFixes(), "correct size");
		assertEquals(1, a2.numFixes(), "correct size");

	}
}
