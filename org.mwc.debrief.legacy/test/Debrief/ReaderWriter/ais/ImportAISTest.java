package Debrief.ReaderWriter.ais;

import static Debrief.ReaderWriter.ais.ImportAIS.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.ais.ImportAIS.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

public class ImportAISTest {
	@Test
	public void testBefore() {
			assertEquals(true, isPreviousMinute(2, 57), "is before");
			assertEquals(false, isPreviousMinute(22, 4), "is before");
			assertEquals(true, isPreviousMinute(2, 33), "is before");
			assertEquals(false, isPreviousMinute(1, 0), "is before");
			assertEquals(true, isPreviousMinute(1, 57), "is before");
			assertEquals(false, isPreviousMinute(2, 28), "is after");
			assertEquals(false, isPreviousMinute(57, 2), "is after");
			assertEquals(false, isPreviousMinute(4, 31), "is after");

			assertEquals(true, isNextMinute(57, 2), "is after");
			assertEquals(false, isNextMinute(5, 52), "is after");
			assertEquals(false, isNextMinute(5, 15), "is after");

		}

	@Test
	public void testFullImport() throws Exception {
			testImport("../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/150304_0854.txt", 6);
		}

	public void testImport(final String testFile, final int len) throws Exception {
			final File testI = new File(testFile);
			assertTrue(testI.exists());

			final InputStream is = new FileInputStream(testI);

			final Layers tLayers = new Layers();

			final ImportAIS importer = new ImportAIS(tLayers);
			importer.importThis(testFile, is);

			// hmmm, how many tracks
			assertEquals(1, tLayers.size(), "got track folder");

			final BaseLayer parent = (BaseLayer) tLayers.findLayer(LAYER_NAME);
			assertEquals(7, parent.size(), "got new tracks");

			final LightweightTrackWrapper thisT = (LightweightTrackWrapper) parent.find("BW LIONESS");
			final Enumeration<Editable> fixes = thisT.getPositionIterator();
			while (fixes.hasMoreElements()) {
				final FixWrapper thisF = (FixWrapper) fixes.nextElement();
				final SubjectAction[] actions = thisF.getInfo().getUndoableActions();
				assertNull(actions, "should not return actions");
//        System.out.println(thisF.getDateTimeGroup().getDate() + " COG:"
//            + (int) Math.toDegrees(thisF.getCourse()) + " SOG:" + (int) thisF
//                .getSpeed());

			}

		}

	@Test
	public void testKnownImport() throws AISParseException {
			final String test = "!AIVDM,1,1,,A,15RTgt0PAso;90TKcjM8h6g208CQ,0*4A";
			final AISParser parser = new AISParser();
			final Optional<IAISMessage> oRes = parser.parse(test, 1);
			if (!oRes.isPresent()) {
				throw new AISParseException();
			}
		}

	@SuppressWarnings("deprecation")
	@Test
	public void testMissingTimes() {
			final Layers layers = new Layers();
			final ImportAIS ia = new ImportAIS(layers);
			Timestamp lastTime = null;
			int mmsi = 5;

			ia.storeThis(1, 1, 1, 1, mmsi, 50, lastTime);

			// check it got cached
			assertEquals(1, ia._queuedFixes.size(), "queue present");

			lastTime = new Timestamp(2010, 6, 6, 6, 6, 2, 0);

			// ok - handle them
			ia.processQueuedPositions(lastTime);

			// note - we should interpret this value as being from the previous minute
			ia.storeThis(1, 1, 1, 1, mmsi, 5, lastTime);

			// check queue cleared
			assertEquals(0, ia._queuedFixes.size(), "queue present");

			// check order of points
			final BaseLayer parent = (BaseLayer) layers.findLayer(LAYER_NAME);
			LightweightTrackWrapper tw = (LightweightTrackWrapper) parent.find("" + mmsi);

			// check the start time
			assertEquals(5, tw.getStartDTG().getDate().getMinutes(), "start time correct");
			assertEquals(50, tw.getStartDTG().getDate().getSeconds(), "start time correct");
			assertEquals(6, tw.getEndDTG().getDate().getMinutes(), "end time correct");
			assertEquals(5, tw.getEndDTG().getDate().getSeconds(), "end time correct");

			// ok now, we'll have a previous position appearing after the time stamp
			// note - we should interpret this value as being from the previous minute
			mmsi = 12;
			lastTime = null;
			ia.storeThis(1, 1, 1, 1, mmsi, 50, lastTime);

			lastTime = new Timestamp(2010, 6, 6, 6, 9, 2, 0);

			// ok - handle them
			ia.processQueuedPositions(lastTime);

			// note - we should interpret this value as being from the previous minute
			ia.storeThis(1, 1, 1, 1, mmsi, 57, lastTime);

			// note - we should interpret this value as being from the previous minute
			ia.storeThis(1, 1, 1, 1, mmsi, 12, lastTime);

			// check queue cleared
			assertEquals(0, ia._queuedFixes.size(), "queue present");

			// check order of points
			final BaseLayer parentL = (BaseLayer) layers.findLayer(LAYER_NAME);
			tw = (LightweightTrackWrapper) parentL.find("" + mmsi);

			// check the start time
			System.out.println("start time:" + tw.getStartDTG().getDate());
			assertEquals(8, tw.getStartDTG().getDate().getMinutes(), "start time correct");
			assertEquals(50, tw.getStartDTG().getDate().getSeconds(), "start time correct");
			assertEquals(9, tw.getEndDTG().getDate().getMinutes(), "end time correct");
			assertEquals(12, tw.getEndDTG().getDate().getSeconds(), "end time correct");

		}

	@Test
	public void testNewImport() throws Exception {
			String testFile = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/150304_0914.txt";
			File testI = new File(testFile);
			assertTrue(testI.exists());

			InputStream is = new FileInputStream(testI);

			final Layers tLayers = new Layers();

			final ImportAIS importer = new ImportAIS(tLayers);
			importer.importThis(testFile, is);

			// ok, now for the second file
			is.close();
			testFile = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/150304_0924.txt";
			testI = new File(testFile);
			assertTrue(testI.exists());

			is = new FileInputStream(testI);
			importer.importThis(testFile, is);

			// hmmm, how many tracks
			assertEquals(1, tLayers.size(), "got new tracks");

			// hmmm, how many tracks
			final BaseLayer parent = (BaseLayer) tLayers.findLayer(LAYER_NAME);
			assertEquals(22, parent.size(), "got new tracks");

			final LightweightTrackWrapper thisT = (LightweightTrackWrapper) parent.find("LOLLAND");
			final Enumeration<Editable> fixes = thisT.getPositionIterator();
			while (fixes.hasMoreElements()) {
				final FixWrapper thisF = (FixWrapper) fixes.nextElement();
				final SubjectAction[] actions = thisF.getInfo().getUndoableActions();
				assertNull(actions, "should not return actions");
//        System.out.println(thisF.getDateTimeGroup().getDate() + " COG:"
//            + (int) Math.toDegrees(thisF.getCourse()) + " SOG:" + (int) thisF
//                .getSpeed() + " loc:" + thisF.getLocation());
			}

		}

	}
