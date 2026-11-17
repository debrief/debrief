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

package Debrief.Wrappers.Track;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicShapeWrapper;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.TrackWrapper.WrappedIterators;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.Algorithms.Conversions;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Editable.editableTesterSupport;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.Plottable;
import MWC.GUI.PlottableSelection;
import MWC.GUI.Canvas.MockCanvasType;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor.CopyItem;
import MWC.GUI.Tools.Operations.RightClickPasteAdaptor;
import MWC.GUI.Tools.Operations.RightClickPasteAdaptor.PasteLayer;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import flanagan.interpolation.CubicSpline;

/**
 * @author Administrator
 *
 */
public class TrackWrapper_Test {

	protected static class TestMockCanvas extends MockCanvasType {
		@Override
		public void drawPolyline(final int[] points) {
			callCount++;
			pointCount += points.length;
		}
	}

	private final static String ownship_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";

	private static final String TRACK_NAME = "test track";

	/**
	 * utility to track number of calls
	 *
	 */
	static int callCount = 0;

	/**
	 * utility to track number of points passed to paint polyline method
	 *
	 */
	static int pointCount = 0;

	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	private static int countCuts(final Enumeration<Editable> sensors) {
		if (sensors == null)
			return 0;

		int counter = 0;
		while (sensors.hasMoreElements()) {
			final SensorWrapper sw = (SensorWrapper) sensors.nextElement();
			final Enumeration<Editable> ele = sw.elements();
			while (ele.hasMoreElements()) {
				counter++;
				ele.nextElement();
				// SensorContactWrapper sc = (SensorContactWrapper) ele.nextElement();
				// System.out.println("cut time is:" +
				// MWC.Utilities.TextFormatting.FormatRNDateTime.toString(sc.getTime().getDate().getTime())
				// + " brg is:" + sc.getBearing());
			}
		}
		// System.out.println("===========");
		return counter;
	}

	private static int countDecorations(final TrackWrapper parent, final boolean countSyms, final boolean countArrows,
			final boolean countLabels) {
		final Enumeration<Editable> iter = parent.getPositionIterator();
		int ctr = 0;
		while (iter.hasMoreElements()) {
			final FixWrapper fix = (FixWrapper) iter.nextElement();
			if (countSyms && fix.getSymbolShowing()) {
				ctr++;
			}
			if (countArrows && fix.getArrowShowing()) {
				ctr++;
			}
			if (countLabels && fix.getLabelShowing()) {
				ctr++;
			}
		}
		return ctr;
	}

	private static int countSolutions(final Enumeration<Editable> solutions) {
		if (solutions == null)
			return 0;

		int counter = 0;
		while (solutions.hasMoreElements()) {
			final TMAWrapper sw = (TMAWrapper) solutions.nextElement();
			final Enumeration<Editable> ele = sw.elements();
			while (ele.hasMoreElements()) {
				counter++;
				ele.nextElement();
				// TMAContactWrapper sc = (TMAContactWrapper) ele.nextElement();
				// System.out.println(" solution time is:" +
				// MWC.Utilities.TextFormatting.FormatRNDateTime.toString(sc.getTime().getDate().getTime())
				// + " brg is:" + MWC.Algorithms.Conversions.Rads2Degs(sc.getCourse()));
			}
		}
		// System.out.println("===========");
		return counter;
	}

	private static int countVisibleFixes(final TrackWrapper tw) {
		int ctr = 0;
		final Enumeration<Editable> iter = tw.getPositionIterator();
		while (iter.hasMoreElements()) {
			final Plottable thisE = (Plottable) iter.nextElement();
			if (thisE.getVisible()) {
				ctr++;
			}
		}
		return ctr;
	}

	private static int countVisibleSensorWrappers(final TrackWrapper tw) {
		final Enumeration<Editable> iter2 = tw.getSensors().elements();
		int sCtr = 0;
		while (iter2.hasMoreElements()) {
			final SensorWrapper sw = (SensorWrapper) iter2.nextElement();
			final Enumeration<Editable> enumS = sw.elements();
			while (enumS.hasMoreElements()) {
				final Plottable pl = (Plottable) enumS.nextElement();
				if (pl.getVisible()) {
					sCtr++;
				}
			}
		}
		return sCtr;
	}

	private static int countVisibleSolutionWrappers(final TrackWrapper tw) {
		final Enumeration<Editable> iter2 = tw.getSolutions().elements();
		int sCtr = 0;
		while (iter2.hasMoreElements()) {
			final TMAWrapper sw = (TMAWrapper) iter2.nextElement();
			final Enumeration<Editable> enumS = sw.elements();
			while (enumS.hasMoreElements()) {
				final Plottable pl = (Plottable) enumS.nextElement();
				if (pl.getVisible()) {
					sCtr++;
				}
			}
		}
		return sCtr;
	}

	public static FixWrapper createFix1(final int timeMillis, final int vLatDeg, final int vLatMin,
			final double vLatSec, final int vLongDeg, final int vLongMin, final double vLongSec, final int crseDegs,
			final int spdKts) {
		final double vLat = vLatDeg + (vLatMin / 60d) + (vLatSec / 3600d);
		final double vLong = vLongDeg + (vLongMin / 60d) + (vLongSec / 3600d);
		final FixWrapper theFix = createFix3(timeMillis, vLat, vLong);
		theFix.getFix().setCourse(MWC.Algorithms.Conversions.Degs2Rads(crseDegs));
		theFix.getFix().setSpeed(new WorldSpeed(spdKts, WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec) / 3);

		return theFix;
	}

	public static FixWrapper createFix2(final int timeMillis, final int vLat, final int vLong, final int crseDegs,
			final int spdKts) {
		final FixWrapper theFix = createFix3(timeMillis, vLat, vLong);
		theFix.getFix().setCourse(MWC.Algorithms.Conversions.Degs2Rads(crseDegs));
		theFix.getFix().setSpeed(new WorldSpeed(spdKts, WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec) / 3);

		return theFix;
	}

	public static FixWrapper createFix3(final long timeMillis, final double vLat, final double vLong) {
		final FixWrapper fw = new FixWrapper(
				new Fix(new HiResDate(timeMillis), new WorldLocation(vLat, vLong, 0), 1, 1));
		return fw;
	}

	public static FixWrapper createFix4(final long timeMillis, final double vLat, final double vLong,
			final double course, final double speed) {
		final FixWrapper fw = new FixWrapper(
				new Fix(new HiResDate(timeMillis), new WorldLocation(vLat, vLong, 0), Math.toRadians(course), speed));
		return fw;
	}

	public static SensorContactWrapper createSensorItem(final TrackWrapper tw, final SensorWrapper sw,
			final int sensorDTG) {
		return new SensorContactWrapper(tw.getName(), new HiResDate(sensorDTG), new WorldDistance(12, WorldDistance.NM),
				12d, null, Color.red, "some lable", 12, sw.getName());
	}

	private static TrackSegment getDummyList() {
		final TrackSegment ts0 = new TrackSegment(TrackSegment.ABSOLUTE);
		final FixWrapper newFix1 = new FixWrapper(new Fix(new HiResDate(10000), new WorldLocation(1, -1, 3), 1, 2));
		final FixWrapper newFix2 = new FixWrapper(new Fix(new HiResDate(20000), new WorldLocation(1, 0, 3), 1, 2));
		final FixWrapper newFix3 = new FixWrapper(new Fix(new HiResDate(30000), new WorldLocation(1, 1, 3), 1, 2));
		final FixWrapper newFix4 = new FixWrapper(new Fix(new HiResDate(40000), new WorldLocation(1, 2, 3), 1, 2));
		ts0.addFix(newFix1);
		ts0.addFix(newFix2);
		ts0.addFix(newFix3);
		ts0.addFix(newFix4);
		return ts0;
	}

	private static TrackWrapper getDummyTrack() {
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100, WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200, WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300, WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400, WorldDistance.METRES), null)), 40, 140));
		fw4.setLabel("fw4");
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500, WorldDistance.METRES), null)), 50, 150));
		fw5.setLabel("fw5");
		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);
		// also give it some sensor data
		final SensorWrapper swa = new SensorWrapper("title one");
		final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa", new HiResDate(150, 0), null, null, null,
				null, null, 0, null);
		final SensorContactWrapper scwa2 = new SensorContactWrapper("bbb", new HiResDate(180, 0), null, null, null,
				null, null, 0, null);
		final SensorContactWrapper scwa3 = new SensorContactWrapper("ccc", new HiResDate(250, 0), null, null, null,
				null, null, 0, null);
		swa.add(scwa1);
		swa.add(scwa2);
		swa.add(scwa3);
		tw.add(swa);
		final SensorWrapper sw = new SensorWrapper("title two");
		final SensorContactWrapper scw1 = new SensorContactWrapper("ddd", new HiResDate(260, 0), null, null, null, null,
				null, 0, null);
		final SensorContactWrapper scw2 = new SensorContactWrapper("eee", new HiResDate(280, 0), null, null, null, null,
				null, 0, null);
		final SensorContactWrapper scw3 = new SensorContactWrapper("fff", new HiResDate(350, 0), null, null, null, null,
				null, 0, null);
		sw.add(scw1);
		sw.add(scw2);
		sw.add(scw3);
		tw.add(sw);

		final TMAWrapper mwa = new TMAWrapper("bb");
		final TMAContactWrapper tcwa1 = new TMAContactWrapper("aaa", "bbb", new HiResDate(130), null, 0, 0, 0, null,
				null, null, null);
		final TMAContactWrapper tcwa2 = new TMAContactWrapper("bbb", "bbb", new HiResDate(190), null, 0, 0, 0, null,
				null, null, null);
		final TMAContactWrapper tcwa3 = new TMAContactWrapper("ccc", "bbb", new HiResDate(230), null, 0, 0, 0, null,
				null, null, null);
		mwa.add(tcwa1);
		mwa.add(tcwa2);
		mwa.add(tcwa3);
		tw.add(mwa);
		final TMAWrapper mw = new TMAWrapper("cc");
		final TMAContactWrapper tcw1 = new TMAContactWrapper("ddd", "bbb", new HiResDate(230), null, 0, 0, 0, null,
				null, null, null);
		final TMAContactWrapper tcw2 = new TMAContactWrapper("eee", "bbb", new HiResDate(330), null, 0, 0, 0, null,
				null, null, null);
		final TMAContactWrapper tcw3 = new TMAContactWrapper("fff", "bbb", new HiResDate(390), null, 0, 0, 0, null,
				null, null, null);
		mw.add(tcw1);
		mw.add(tcw2);
		mw.add(tcw3);
		tw.add(mw);

		return tw;
	}

	private static FixWrapper getFix(final long dtg, final double course, final double speed) {
		final Fix theFix = new Fix(new HiResDate(dtg), new WorldLocation(2, 2, 2), course, Conversions.Kts2Yps(speed));
		final FixWrapper res = new FixWrapper(theFix);
		return res;
	}

	private static void listTimes(final TrackSegment ts) {
		final Enumeration<Editable> iter = ts.elements();
		while (iter.hasMoreElements()) {
			final FixWrapper fix = (FixWrapper) iter.nextElement();
			System.out.println(fix.getDTG().getDate().getTime());
		}
	}

	@Test
	public final static void testFilterToTimePeriod() {
		TrackWrapper tw = getDummyTrack();
		HiResDate startH = new HiResDate(150, 0);
		HiResDate endH = new HiResDate(450, 0);
		tw.filterListTo(startH, endH);
		int ctr = countVisibleFixes(tw);
		int sCtr = countVisibleSensorWrappers(tw);
		int tCtr = countVisibleSolutionWrappers(tw);
		assertEquals(3, ctr, "contains correct number of entries");
		assertEquals(6, sCtr, "contains correct number of sensor entries");
		assertEquals(5, tCtr, "contains correct number of sensor entries");

		tw = getDummyTrack();
		startH = new HiResDate(350, 0);
		endH = new HiResDate(550, 0);
		tw.filterListTo(startH, endH);
		ctr = countVisibleFixes(tw);
		sCtr = countVisibleSensorWrappers(tw);
		tCtr = countVisibleSolutionWrappers(tw);
		assertEquals(2, ctr, "contains correct number of entries");
		assertEquals(1, sCtr, "contains correct number of sensor entries");
		assertEquals(1, tCtr, "contains correct number of sensor entries");

		tw = getDummyTrack();
		startH = new HiResDate(0, 0);
		endH = new HiResDate(450, 0);
		tw.filterListTo(startH, endH);
		ctr = countVisibleFixes(tw);
		sCtr = countVisibleSensorWrappers(tw);
		tCtr = countVisibleSolutionWrappers(tw);
		assertEquals(4, ctr, "contains correct number of entries");
		assertEquals(6, sCtr, "contains correct number of sensor entries");
		assertEquals(6, tCtr, "contains correct number of sensor entries");
	}

	@Test
	public final static void testGettingTimes() throws InterruptedException {
		// Enumeration<SensorContactWrapper>
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final WorldLocation loc_2 = new WorldLocation(1, 1, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(0, 100), loc_1, 0, 0));
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(0, 300), loc_2, 0, 0));
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(0, 500), loc_2, 0, 0));
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(0, 700), loc_2, 0, 0));

		// check returning empty data
		Collection<Editable> coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
		assertEquals(coll, null, "Return empty when empty");

		tw.addFix(fw1);

		// check returning single field
		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
		assertEquals(coll, null, "Return empty when out of range");

		coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0, 540));
		assertEquals(coll, null, "Return empty when out of range");

		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 140));
		assertEquals(coll.size(), 1, "Return valid point");

		coll = tw.getItemsBetween(new HiResDate(0, 100), new HiResDate(0, 100));
		assertEquals(coll.size(), 1, "Return valid point");

		tw.addFix(fw2);

		// check returning with fields
		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
		assertEquals(coll, null, "Return empty when out of range");

		coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0, 540));
		assertEquals(coll, null, "Return empty when out of range");

		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 140));
		assertEquals(coll.size(), 1, "Return valid point");

		// insert delay, to overcome cacheing
		Thread.sleep(550);

		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 440));
		assertEquals(coll.size(), 2, "Return valid point");

		coll = tw.getItemsBetween(new HiResDate(0, 150), new HiResDate(0, 440));
		assertEquals(coll.size(), 1, "Return valid point");

		coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0, 440));
		assertEquals(coll.size(), 1, "Return valid point");

		tw.addFix(fw3);

		// check returning with fields
		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
		assertEquals(coll, null, "Return empty when out of range");

		coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0, 540));
		assertEquals(coll, null, "Return empty when out of range");

		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 140));
		assertEquals(coll.size(), 1, "Return valid point");

		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 440));
		assertEquals(coll.size(), 2, "Return valid point");

		coll = tw.getItemsBetween(new HiResDate(0, 150), new HiResDate(0, 440));
		assertEquals(coll.size(), 1, "Return valid point");

		coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0, 440));
		assertEquals(coll.size(), 1, "Return valid point");

		coll = tw.getItemsBetween(new HiResDate(0, 100), new HiResDate(0, 300));
		assertEquals(coll.size(), 2, "Return valid point");
		Thread.sleep(850);

		coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0, 500));
		Thread.sleep(850);
		assertEquals(coll.size(), 2, "Return valid point");

		tw.addFix(fw4);

	}

	@Test
	public final static void testInterpolateDepth() {
		final WorldLocation loc_1 = new WorldLocation(0, 0, 10);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(1000, 0), loc_1, 0.10, 110));
		fw1.setLabel("fw1");
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(2000, 0), loc_1.add(new WorldVector(33,
				new WorldDistance(20, WorldDistance.METRES), new WorldDistance(10, WorldDistance.METRES))), 0.20, 120));
		fw2.setLabel("fw2");
		final FixWrapper res = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(1500, 0));
		assertEquals(15d, res.getDepth(), "correct depth");
	}

	@Test
	public final static void testInterpolation() {
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100, WorldDistance.METRES), null)), 0.10, 110));
		fw1.setLabel("fw1");
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200, WorldDistance.METRES), null)), 0.20, 120));
		fw2.setLabel("fw2");
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300, WorldDistance.METRES), null)), 0.30, 130));
		fw3.setLabel("fw3");
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400, WorldDistance.METRES), null)), 0.40, 140));
		fw4.setLabel("fw4");
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500, WorldDistance.METRES), null)), 0.50, 150));
		fw5.setLabel("fw5");
		tw.addFix(fw1);
		tw.addFix(fw2);
		// tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);

		// check that we're not interpolating
		assertFalse(tw.getInterpolatePoints(), "interpolating switched off by default");

		// ok, get on with it.
		Watchable[] list = tw.getNearestTo(new HiResDate(200, 20000));
		assertNotNull(list, "found list");
		assertEquals(list.length, 1, "contains something");
		assertEquals(list[0], fw2, "right answer");

		// and the end
		list = tw.getNearestTo(new HiResDate(500, 50000));
		assertNotNull(list, "found list");
		assertEquals(list.length, 1, "contains something");
		assertEquals(list[0], fw5, "right answer");

		// and now an in-between point
		// ok, get on with it.
		list = tw.getNearestTo(new HiResDate(230, 23000));
		assertNotNull(list, "found list");
		assertEquals(list.length, 1, "contains something");
		assertEquals(list[0], fw4, "right answer");

		// ok, with interpolation on
		tw.setInterpolatePoints(true);

		assertTrue(tw.getInterpolatePoints(), "interpolating now switched on");

		// ok, get on with it.
		list = tw.getNearestTo(new HiResDate(200, 20000));
		assertNotNull(list, "found list");
		assertEquals(list.length, 1, "contains something");
		assertEquals(list[0], fw2, "right answer");

		// and the end
		list = tw.getNearestTo(new HiResDate(500, 50000));
		assertNotNull(list, "found list");
		assertEquals(list.length, 1, "contains something");
		assertEquals(list[0], fw5, "right answer");

		// hey

		// and now an in-between point
		// ok, get on with it.
		list = tw.getNearestTo(new HiResDate(300, 30000));
		assertNotNull(list, "found list");
		assertEquals(list.length, 1, "contains something");

		// have a look at them
		final FixWrapper res = (FixWrapper) list[0];
		final WorldVector rangeError = res.getFixLocation().subtract(fw3.getFixLocation());
		assertEquals(0, Conversions.Degs2m(rangeError.getRange()), 0.0001, "right answer");
		assertEquals(res.getSpeed(), fw3.getSpeed(), 0.00000000001, "right speed");
		assertEquals(res.getCourse(), fw3.getCourse(), 0.0001, "right course");

	}

	/**
	 * fixes we can easily refer to in a test..
	 *
	 */
	private final FixWrapper _fw1 = createFix3(300000, 2, 3);

	private final FixWrapper _fw2 = createFix3(500000, 2, 3);

	private TrackWrapper _tw;

	private MessageProvider.TestableMessageProvider _messages;

	private int _ctr = 0;

	private int countVisibleItems() {
		final Enumeration<Editable> all = _tw.contiguousElements();
		int ctr = 0;
		while (all.hasMoreElements()) {
			final Plottable thisE = (Plottable) all.nextElement();
			if (thisE.getVisible())
				ctr++;
		}
		return ctr;
	}

	private int numSegments() {
		int res = 0;
		final Enumeration<Editable> layers = _tw.elements();
		while (layers.hasMoreElements()) {
			final Object child = layers.nextElement();
			if (child instanceof TrackSegment) {
				res++;
			} else if (child instanceof SegmentList) {
				final SegmentList segl = (SegmentList) child;
				final Enumeration<Editable> segs = segl.elements();
				while (segs.hasMoreElements()) {
					res++;
					segs.nextElement();
				}

			}

		}
		return res;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	public void setUp() throws Exception {
		// give the fixes some names, so we can track them
		_fw1.setLabel("fw1");
		_fw2.setLabel("fw2");

		_tw = new TrackWrapper();
		_tw.setName(TRACK_NAME);
		_tw.addFix(createFix3(100000, 1, 1));
		_tw.addFix(createFix3(200000, 2, 3));
		_tw.addFix(_fw1);
		_tw.addFix(createFix3(400000, 3, 3));
		_tw.addFix(_fw2);
		_tw.addFix(createFix3(600000, 4, 6));
		_messages = new MessageProvider.TestableMessageProvider();
		MessageProvider.Base.setProvider(_messages);

		// set the correct earth model
		WorldLocation.setModel(new MWC.Algorithms.EarthModels.FlatEarth());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#add(MWC.GUI.Editable)} .
	 *
	 * @throws InterruptedException
	 */

	/**
	 * the clipboard deserialisation filter must accept the Debrief wrappers (as
	 * used in cut/copy/paste)
	 */
	@Test
	public void testClipboardFilterAcceptsWrappers() throws Exception {
		final Layers tLayers = new Layers();
		ImportReplay.initialise(new Debrief.ReaderWriter.Replay.ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		try (final InputStream bs = new FileInputStream(ownship_track)) {
			new ImportReplay().importThis(ownship_track, bs, tLayers);
		}
		final TrackWrapper loaded = (TrackWrapper) tLayers.findLayer("NELSON");
		assertNotNull(loaded, "loaded track");

		final TrackWrapper dummy = getDummyTrack();
		dummy.setName("dummy");
		final ShapeWrapper shape = new ShapeWrapper("rect",
				new RectangleShape(new WorldLocation(1, 1, 0), new WorldLocation(2, 2, 0)), Color.RED,
				new HiResDate(1000));
		final LabelWrapper label = new LabelWrapper("lbl", new WorldLocation(1, 1, 0), Color.BLUE);
		final Editable[] items = new Editable[] { loaded, dummy, shape, label };

		final java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
		try (final java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos)) {
			oos.writeObject(items);
		}
		final Editable[] res;
		try (final java.io.ObjectInputStream ois = MWC.Utilities.ReaderWriter.ClipboardInputFilter
				.createStream(new java.io.ByteArrayInputStream(bos.toByteArray()))) {
			res = (Editable[]) ois.readObject();
		}
		assertEquals(4, res.length);
		assertEquals(loaded.numFixes(), ((TrackWrapper) res[0]).numFixes());
		assertEquals(dummy.numFixes(), ((TrackWrapper) res[1]).numFixes());
		assertEquals(dummy.getSensors().size(), ((TrackWrapper) res[1]).getSensors().size());
		assertEquals("rect", res[2].getName());
		assertEquals("lbl", res[3].getName());
	}

	/**
	 * towed array data is held in sensors as January datasets. Check a track
	 * carrying it can still be pasted
	 */
	@Test
	public void testClipboardFilterAcceptsTASensorData() throws Exception {
		final SensorWrapper sensor = new SensorWrapper("TA");
		final Debrief.Wrappers.Extensions.Measurements.DataFolder folder = new Debrief.Wrappers.Extensions.Measurements.DataFolder();
		sensor.getAdditionalData().add(folder);
		folder.add(new Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble("Fore", "degs",
				new long[] { 1000, 2000 }, new double[] { 1, 2 }));
		final TrackWrapper track = new TrackWrapper();
		track.setName("ta_track");
		track.add(sensor);

		final java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
		try (final java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos)) {
			oos.writeObject(new Editable[] { track });
		}
		final Editable[] res;
		try (final java.io.ObjectInputStream ois = MWC.Utilities.ReaderWriter.ClipboardInputFilter
				.createStream(new java.io.ByteArrayInputStream(bos.toByteArray()))) {
			res = (Editable[]) ois.readObject();
		}
		assertEquals(1, res.length);
		final SensorWrapper pasted = (SensorWrapper) ((TrackWrapper) res[0]).getSensors().elements().nextElement();
		assertEquals(1, pasted.getAdditionalData().size(), "TA data present");
	}

	@Test
	public void testAdd() throws InterruptedException {
		assertEquals(6, this.trackLength(), "start condition");

		// check we can add a fix
		final FixWrapper fw = createFix3(12, 3d, 4d);
		_tw.add(fw);

		// insert delay, to overcome cacheing
		Thread.sleep(550);

		assertEquals(7, this.trackLength(), "got added");

		// now something else
		final SensorWrapper sw = new SensorWrapper("some sensor");
		final SensorContactWrapper scw = new SensorContactWrapper();
		scw.setDTG(new HiResDate(1000));
		sw.add(scw);
		sw.setVisible(true);
		_tw.add(sw);

		assertEquals(8, this.trackLength(), "got added");

	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#addFix(Debrief.Wrappers.FixWrapper)}.
	 *
	 * @throws InterruptedException
	 */

	@Test
	public void testAddFix() throws InterruptedException {
		assertEquals(6, this.trackLength(), "start condition");

		// check we can add a fix
		final FixWrapper fw = createFix3(12, 3d, 4d);
		_tw.addFix(fw);

		// insert delay, to overcome cacheing
		Thread.sleep(550);

		assertEquals(7, this.trackLength(), "got added");
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#append(MWC.GUI.Layer)} .
	 *
	 * @throws InterruptedException
	 */

	@Test
	public void testAppend() throws InterruptedException {
		final TrackWrapper tw2 = new TrackWrapper();
		final FixWrapper f1 = createFix3(13, 2, 2);
		final FixWrapper f2 = createFix3(14, 32, 12);
		tw2.addFix(f1);
		tw2.addFix(f2);

		// check current state of track
		assertEquals(6, trackLength(), "in start condition");

		// combine the two
		_tw.append(tw2);

		// insert delay, to overcome cacheing
		Thread.sleep(550);

		assertEquals(8, trackLength(), "received extra points");
	}

	@Test
	public void testCacheIterators() {
		final TrackWrapper track = new TrackWrapper();
		track.setName("track");

		track.addFix(getFix(1000, 10, 10));
		track.addFix(getFix(2000, 10, 20));
		track.addFix(getFix(3000, 10, 30));
		track.addFix(getFix(4000, 10, 40));
		track.addFix(getFix(5000, 10, 50));
		track.addFix(getFix(6000, 10, 60));
		track.addFix(getFix(7000, 10, 70));

		assertNull(track.getCachedIterator(), "iterator not cached yet");

		Watchable[] resArr = track.getNearestTo(new HiResDate(500));
		assertEquals(0, resArr.length, "Should not get anything");
		assertNull(track.getCachedIterator(), "iterator still not cached yet");

		Watchable res = track.getNearestTo(new HiResDate(1000))[0];
		assertNotNull(res);
		assertNull(track.getCachedIterator(), "iterator still not cached yet");
		assertEquals(res.getSpeed(), 10d, 0.001);

		res = track.getNearestTo(new HiResDate(1200))[0];
		assertNotNull(res);
		assertNotNull(track.getCachedIterator(), "iterator should be there");
		assertEquals(res.getSpeed(), 20d, 0.001);

		res = track.getNearestTo(new HiResDate(2100))[0];
		assertNotNull(res);
		assertNotNull(track.getCachedIterator(), "iterator should be there");
		assertEquals(res.getSpeed(), 30d, 0.001);

		final WrappedIterators theIter = track.getCachedIterator();

		res = track.getNearestTo(new HiResDate(6100))[0];
		assertNotNull(res);
		assertNull(track.getCachedIterator(), "iterator gone");
		assertEquals(res.getSpeed(), 70d, 0.001);

		// ALSO CHECK WHEN WE ONLY MOVE FORWARD VERY SLIGHTLY

		resArr = track.getNearestTo(new HiResDate(8100));
		assertEquals(0, resArr.length, "Empty array");
		assertNull(track.getCachedIterator(), "iterator gone");

		res = track.getNearestTo(new HiResDate(2100))[0];
		assertNotNull(res);
		assertNotNull(track.getCachedIterator(), "iterator should be there");
		assertEquals(res.getSpeed(), 30d, 0.001);

		assertTrue(theIter != track.getCachedIterator(), "should be new iterator");

		// switch on interpolation, and check speeds
		track.setInterpolatePoints(true);
		res = track.getNearestTo(new HiResDate(2200))[0];
		assertNotNull(res);
		assertNotNull(track.getCachedIterator(), "iterator should be there");
		assertEquals(res.getSpeed(), 22d, 0.001);

		res = track.getNearestTo(new HiResDate(5600))[0];
		assertNotNull(res);
		assertNotNull(track.getCachedIterator(), "iterator should be there");
		assertEquals(res.getSpeed(), 56d, 0.001);

		res = track.getNearestTo(new HiResDate(6600))[0];
		assertNotNull(res);
		assertNull(track.getCachedIterator(), "iterator cleared, since we have no more elements");
		assertEquals(res.getSpeed(), 66d, 0.001);

		res = track.getNearestTo(new HiResDate(1600))[0];
		assertNotNull(res);
		assertNotNull(track.getCachedIterator(), "iterator should be there");
		assertEquals(res.getSpeed(), 16d, 0.001);

	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#contiguousElements()}.
	 */

	@Test
	public void testContiguousElements() {
		assertEquals(6, trackLength(), "have initial items");

		// give it a little more data
		final SensorWrapper sw = new SensorWrapper("sensor a");
		sw.add(new SensorContactWrapper("trk", new HiResDate(12), null, null, null, null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(13), null, null, null, null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(14), null, null, null, null, null, 0, null));
		sw.setVisible(true);

		_tw.add(sw);

		final Enumeration<Editable> tester = _tw.contiguousElements();
		_ctr = 0;
		while (tester.hasMoreElements()) {
			tester.nextElement();
			_ctr++;
		}
		assertEquals(9, _ctr, "have new items");
	}

	/**
	 * course & speed calculation with fixes less than a second apart, and gaps that
	 * aren't whole seconds
	 */
	@Test
	public void testCalcCourseSpeedSubSecond() {
		final TrackWrapper tw = new TrackWrapper();
		tw.setName("fast");
		// 10 m/s due east, sampled at 0.5 sec, then a 1.9 sec gap
		final long[] times = new long[] { 10000, 10500, 11000, 12900, 13400 };
		for (final long t : times) {
			final double metresEast = (t - times[0]) / 1000d * 10d;
			tw.addFix(new FixWrapper(
					new Fix(new HiResDate(t), new WorldLocation(0, Conversions.m2Degs(metresEast), 0), 0, 0)));
		}
		tw.calcCourseSpeed();

		final double tenMpsInKts = Conversions.Mps2Kts(10);
		final Enumeration<Editable> iter = tw.getPositionIterator();
		int ctr = 0;
		while (iter.hasMoreElements() && ctr < times.length - 1) {
			final FixWrapper fw = (FixWrapper) iter.nextElement();
			assertEquals(tenMpsInKts, fw.getSpeed(), 0.01, "speed correct for fix " + ctr);
			assertEquals(90d, Conversions.Rads2Degs(fw.getCourse()), 0.01, "course correct for fix " + ctr);
			ctr++;
		}
		assertEquals(times.length - 1, ctr, "checked fixes");
	}

	/**
	 * selecting 'None' for resample shouldn't throw
	 */
	@Test
	public void testResampleAtZero() {
		final TrackWrapper tw = new TrackWrapper();
		tw.setName("some track");
		tw.addFix(createFix3(10000, 1, 1));
		tw.addFix(createFix3(20000, 1, 2));
		tw.addFix(createFix3(30000, 1, 3));
		tw.setResampleDataAt(new HiResDate(0));
		assertEquals(3, tw.numFixes(), "unchanged");
	}

	/**
	 * resampling a track whose first leg is a relative TMA segment keeps the right
	 * leg name (start DTG) and offset
	 */
	@Test
	public void testDecimateRelativeTMALeg() {
		final TrackWrapper host = new TrackWrapper();
		host.setName("host");
		host.addFix(createFix2(80000, 3, 3, 4, 12));
		host.addFix(createFix2(100000, 1, 1, 4, 12));
		host.addFix(createFix2(200000, 2, 3, 4, 12));
		host.addFix(createFix2(300000, 3, 3, 4, 12));

		final SensorWrapper sw = new SensorWrapper("some sensor");
		host.add(sw);

		final SensorContactWrapper[] items = new SensorContactWrapper[4];
		items[0] = createSensorItem(host, sw, 115000);
		items[1] = createSensorItem(host, sw, 125000);
		items[2] = createSensorItem(host, sw, 135000);
		items[3] = createSensorItem(host, sw, 145000);
		for (final SensorContactWrapper item : items) {
			item.setSensor(sw);
		}

		final WorldVector offset = new WorldVector(Conversions.Degs2Rads(45), 0.1, 0);
		final RelativeTMASegment seg = new RelativeTMASegment(items, offset, new WorldSpeed(5, WorldSpeed.Kts), 33,
				null, Color.yellow);
		final TrackWrapper tma = new TrackWrapper();
		tma.setName(TrackSegment.TMA_LEADER + "leg");
		tma.add(seg);
		// (adding a single leg renames it, so name it afterwards)
		seg.setName(TrackSegment.TMA_LEADER + "leg");

		// resample at 10 second intervals, so the leg starts at 120000
		tma.setResampleDataAt(new HiResDate(10000));

		final String expectedName = MWC.Utilities.TextFormatting.FormatRNDateTime.toString(120000);
		assertEquals(expectedName, seg.getName(), "leg named for real start time");

		// the offset should now be from the host position at the new start time
		final FixWrapper first = (FixWrapper) seg.first();
		assertEquals(120000, first.getDateTimeGroup().getDate().getTime(), "first point at new start");
	}

	@Test
	public void testDecimateAbsolute() throws InterruptedException {
		final TrackSegment ts1 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts1.addFix(createFix3(0 * 1000000L, 31, 34));
		ts1.addFix(createFix3(1 * 1000000L, 32, 33));
		ts1.addFix(createFix3(2 * 1000000L, 33, 32));
		ts1.addFix(createFix3(3 * 1000000L, 34, 31));
		ts1.addFix(createFix3(4 * 1000000L, 35, 30));

		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts2.addFix(createFix3(5 * 1000000L, 35, 33));
		ts2.addFix(createFix3(6 * 1000000L, 36, 34));
		ts2.addFix(createFix3(7 * 1000000L, 37, 35));
		ts2.addFix(createFix3(8 * 1000000L, 38, 36));
		ts2.addFix(createFix3(9 * 1000000L, 39, 37));

		final TrackSegment ts3 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts3.addFix(createFix3(10 * 1000000L, 32, 37));
		ts3.addFix(createFix3(11 * 1000000L, 31, 36));
		ts3.addFix(createFix3(12 * 1000000L, 30, 35));
		ts3.addFix(createFix3(13 * 1000000L, 29, 34));
		ts3.addFix(createFix3(24 * 1000000L, 28, 33));

		final TrackWrapper tw = new TrackWrapper();
		tw.add(ts1);
		tw.add(ts2);
		tw.add(ts3);

		final SegmentList sl = tw.getSegments();

		// check it's got the segs
		assertEquals("Positions (3 legs)", sl.toString(), "has segments");
		assertEquals(15, tw.numFixes(), "has all fixes");

		// what's the area before
		assertEquals(5.8372d, tw.getBounds().getWidth(), 0.001, "correct wid");
		assertEquals(11d, tw.getBounds().getHeight(), 0.001, "correct ht");

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(4 * 1000000L));

		// what's the area before
		assertEquals(5.032d, tw.getBounds().getWidth(), 0.001, "still correct wid");
		assertEquals(10d, tw.getBounds().getHeight(), 0.001, "still correct ht");

		// check labels present
		final Enumeration<Editable> pIter = tw.getPositionIterator();
		while (pIter.hasMoreElements()) {
			final FixWrapper fix = (FixWrapper) pIter.nextElement();
			final String label = fix.getLabel();
			assertNotNull(label, "label is present");
			assertTrue(label.length() > 0, "label has contents");
		}

		// insert delay, to overcome cacheing
		Thread.sleep(550);

		// how was it?
		assertEquals("Positions (3 legs)", sl.toString(), "has segments");
		// assertEquals("has all fixes", 7, tw.numFixes());

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(500000L));

		Thread.sleep(550);

		// how was it?
		assertEquals("Positions (3 legs)", sl.toString(), "has segments");
		// assertEquals("has all fixes", 49, tw.numFixes());
	}

	@Test
	public void testDecimateAbsoluteDown() {
		final TrackWrapper parent = new TrackWrapper();
		parent.setName("Parent");
		final long startT = System.currentTimeMillis();

		final TrackSegment ts = new TrackSegment(TrackSegment.ABSOLUTE);
		ts.setName("Some name");
		final int ctr = 12;

		for (int i = 0; i < ctr; i++) {
			ts.add(getFix(10001 * i, 2, 4));
		}

		parent.add(ts);

		assertEquals(ctr, ts.size(), "Before len:");

		// listTimes(ts);

		// ok, do resample at higher rate
		final HiResDate interval = new HiResDate(30000);
		ts.setResampleDataAt(interval);

		// listTimes(ts);

		assertEquals(4, ts.size(), "After len:");
		// System.out.println("test took:" + (System.currentTimeMillis() - startT));
	}

	@Test
	public void testDecimateAbsoluteUp() {
		final TrackWrapper parent = new TrackWrapper();
		parent.setName("Parent");
		final long startT = System.currentTimeMillis();

		final TrackSegment ts = new TrackSegment(TrackSegment.ABSOLUTE);
		ts.setName("Some name");
		final int ctr = 12;

		for (int i = 0; i < ctr; i++) {
			ts.add(getFix(10000 * i, 2, 4));
		}

		parent.add(ts);

		assertEquals(ctr, ts.size(), "Before len:");

		listTimes(ts);

		// ok, do resample at higher rate
		final HiResDate interval = new HiResDate(8000);
		ts.setResampleDataAt(interval);

		listTimes(ts);

		assertEquals(14, ts.size(), "After len:");
		// System.out.println("test took:" + (System.currentTimeMillis() - startT));
	}

	@Test
	public void testDecimatePositionsAndData() throws InterruptedException {
		final TrackSegment ts1 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts1.addFix(createFix3(0 * 60000, 32, 33));
		ts1.addFix(createFix3(1 * 60000, 32, 33));
		ts1.addFix(createFix3(2 * 60000, 32, 33));
		ts1.addFix(createFix3(3 * 60000, 32, 33));
		ts1.addFix(createFix3(4 * 60000, 32, 33));

		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts2.addFix(createFix3(5 * 60000, 32, 33));
		ts2.addFix(createFix3(6 * 60000, 32, 33));
		ts2.addFix(createFix3(7 * 60000, 32, 33));
		ts2.addFix(createFix3(8 * 60000, 32, 33));
		ts2.addFix(createFix3(9 * 60000, 32, 33));

		final TrackSegment ts3 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts3.addFix(createFix3(10 * 60000, 32, 33));
		ts3.addFix(createFix3(11 * 60000, 32, 33));
		ts3.addFix(createFix3(12 * 60000, 32, 33));
		ts3.addFix(createFix3(13 * 60000, 32, 33));
		ts3.addFix(createFix3(24 * 60000, 32, 33));

		final SensorWrapper sw = new SensorWrapper("dummy sensor");
		final SensorContactWrapper scw1 = new SensorContactWrapper("the track", new HiResDate(1 * 60000),
				new WorldDistance(2, WorldDistance.NM), 12d, 12d, 44d, null, Color.red, "aa", 1, "dummy sensor");
		final SensorContactWrapper scw2 = new SensorContactWrapper("the track", new HiResDate(3 * 60000),
				new WorldDistance(4, WorldDistance.NM), 15d, 15d, 46d, null, Color.red, "aa", 1, "dummy sensor");
		final SensorContactWrapper scw3 = new SensorContactWrapper("the track", new HiResDate(7 * 60000),
				new WorldDistance(12, WorldDistance.NM), 18d, 12d, 12d, null, Color.red, "aa", 1, "dummy sensor");
		final SensorContactWrapper scw4 = new SensorContactWrapper("the track", new HiResDate(8 * 60000),
				new WorldDistance(7, WorldDistance.NM), 35d, 12d, 312d, null, Color.red, "aa", 1, "dummy sensor");
		sw.add(scw1);
		sw.add(scw2);
		sw.add(scw3);
		sw.add(scw4);

		final TMAWrapper tmw = new TMAWrapper("dummy tma");
		final WorldLocation origin = new WorldLocation(2, 2, 0);
		final TMAContactWrapper tc1 = new TMAContactWrapper("sola", "tracla", new HiResDate(2 * 60000), 12d, 14d, 22d,
				12.2, 0d, Color.red, "bb", new EllipseShape(origin, 12, new WorldDistance(12, WorldDistance.DEGS),
						new WorldDistance(12, WorldDistance.DEGS)),
				"aa");
		final TMAContactWrapper tc2 = new TMAContactWrapper("sola", "tracla", new HiResDate(7 * 60000), 13d, 22d, 17d,
				12.2, 0d, Color.red, "bb", new EllipseShape(origin, 14, new WorldDistance(21, WorldDistance.DEGS),
						new WorldDistance(15, WorldDistance.DEGS)),
				"aa");
		final TMAContactWrapper tc3 = new TMAContactWrapper("sola", "tracla", new HiResDate(9 * 60000), 21d, 23d, 15d,
				12.2, 0d, Color.red, "bb", new EllipseShape(origin, 19, new WorldDistance(29, WorldDistance.DEGS),
						new WorldDistance(32, WorldDistance.DEGS)),
				"aa");
		final TMAContactWrapper tc4 = new TMAContactWrapper("sola", "tracla", new HiResDate(16 * 60000), 14d, 14d, 22d,
				12.2, 0d, Color.red, "bb", new EllipseShape(origin, 12, new WorldDistance(22, WorldDistance.DEGS),
						new WorldDistance(12, WorldDistance.DEGS)),
				"aa");

		tmw.add(tc1);
		tmw.add(tc2);
		tmw.add(tc3);
		tmw.add(tc4);

		final TrackWrapper tw = new TrackWrapper();
		tw.add(ts1);
		tw.add(ts2);
		tw.add(ts3);
		tw.add(sw);
		tw.add(tmw);

		SegmentList sl = null;
		final Enumeration<Editable> data = tw.elements();
		while (data.hasMoreElements()) {
			final Object nextO = data.nextElement();
			if (nextO instanceof SegmentList)
				sl = (SegmentList) nextO;
		}

		// check it's got the segs
		assertEquals("Positions (3 legs)", sl.toString(), "has segments");
		assertEquals(15, tw.numFixes(), "has all fixes");
		// check we've got all the sensor data
		assertEquals(4, countCuts(tw.getSensors().elements()), "has all sensor cuts");
		assertEquals(4, countSolutions(tw.getSolutions().elements()), "has all tma cuts");

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(30 * 1000L));

		// insert delay, to overcome cacheing
		Thread.sleep(550);

		// how was it?
		assertEquals("Positions (3 legs)", sl.toString(), "has segments");

		// insert delay, to overcome cacheing
		Thread.sleep(550);

		// assertEquals("has all fixes", 49, tw.numFixes());
		// assertEquals("has all sensor cuts", 7,
		// countCuts(tw.getSensors().elements()));
		// assertEquals("has all tma cuts", 4, countSolutions(tw.getSolutions()
		// .elements()));
		//
		// // GO FOR ULTIMATE DECIMATION
		// tw.setResampleDataAt(new HiResDate(4 * 60000));
		//
		// // insert delay, to overcome cacheing
		// Thread.sleep(550);
		//
		// // how was it?
		// assertEquals("has segments", "Track segments (3 items)", sl.toString());
		// assertEquals("has all fixes", 7, tw.numFixes());
		// assertEquals("has all resampled sensor cuts", 2, countCuts(tw.getSensors()
		// .elements()));
		// assertEquals("has all tma cuts", 3, countSolutions(tw.getSolutions()
		// .elements()));

	}

	@Test
	public void testDecimateRelative() throws InterruptedException, ParseException {
		final SimpleDateFormat sdf = new GMTDateFormat("HH:mm:ss");

		final TrackSegment ts1 = new TrackSegment(TrackSegment.RELATIVE);
		ts1.addFix(createFix4(sdf.parse("13:00:14").getTime(), 0.0, 0.0, 0, 5));
		ts1.addFix(createFix4(sdf.parse("13:01:14").getTime(), 0.5, 0.0, 0, 5));
		ts1.addFix(createFix4(sdf.parse("13:02:14").getTime(), 1.0, 0.0, 90, 5));
		ts1.addFix(createFix4(sdf.parse("13:03:14").getTime(), 1.0, 0.5, 90, 5));
		ts1.addFix(createFix4(sdf.parse("13:04:14").getTime(), 1.0, 1.0, 180, 5));
		ts1.addFix(createFix4(sdf.parse("13:04:14").getTime(), 0.5, 1.0, 180, 5));
		ts1.addFix(createFix4(sdf.parse("13:05:14").getTime(), 0.0, 1.0, 270, 5));
		ts1.addFix(createFix4(sdf.parse("13:06:14").getTime(), 0.0, 0.5, 270, 5));
		ts1.addFix(createFix4(sdf.parse("13:07:14").getTime(), 0.0, 0.0, 315, 5));

		final TrackWrapper tw = new TrackWrapper();
		tw.add(ts1);

		Enumeration<Editable> pI = tw.getPositionIterator();
		while (pI.hasMoreElements()) {
			final FixWrapper fix = (FixWrapper) pI.nextElement();
			// System.out.println(fix.getDTG().getDate() + " // " + fix.getLocation());
		}

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(60 * 1000L));
		// ts1.decimate(new HiResDate(60 * 1000L), tw, sdf.parse("13:01:00").getTime());

		assertEquals(7, ts1.size(), "have correct fixes");

		pI = tw.getPositionIterator();
		while (pI.hasMoreElements()) {
			final FixWrapper fix = (FixWrapper) pI.nextElement();
			// System.out.println(fix.getDTG().getDate() + " // " + fix.getLocation());
		}

	}

	@Test
	public void testDecimateRelativeDown() {
		final TrackWrapper parent = new TrackWrapper();
		parent.setName("Parent");
		final long startT = System.currentTimeMillis();

		final TrackSegment ts = new TrackSegment(TrackSegment.RELATIVE);
		ts.setName("Some name");
		final int ctr = 12;

		for (int i = 0; i < ctr; i++) {
			ts.add(getFix(10001 * i, 2, 4));
		}

		parent.add(ts);

		assertEquals(ctr, ts.size(), "Before len:");

		// listTimes(ts);

		// ok, do resample at higher rate
		final HiResDate interval = new HiResDate(30000);
		ts.setResampleDataAt(interval);

		// listTimes(ts);

		assertEquals(4, ts.size(), "After len:");
		// System.out.println("test took:" + (System.currentTimeMillis() - startT));
	}

	@Test
	public void testDecimateRelativeUp() {
		final TrackWrapper parent = new TrackWrapper();
		parent.setName("Parent");
		final long startT = System.currentTimeMillis();

		final TrackSegment ts = new TrackSegment(TrackSegment.RELATIVE);
		ts.setName("Some name");
		final int ctr = 12;

		for (int i = 0; i < ctr; i++) {
			ts.add(getFix(10000 * i, 2, 4));
		}

		parent.add(ts);

		assertEquals(ctr, ts.size(), "Before len:");

		listTimes(ts);

		// ok, do resample at higher rate
		final HiResDate interval = new HiResDate(8000);
		ts.setResampleDataAt(interval);

		listTimes(ts);

		assertEquals(14, ts.size(), "After len:");
		// System.out.println("test took:" + (System.currentTimeMillis() - startT));
	}

	@Test
	public void testDuplicateDynamicSetShape() {
		final TrackWrapper parent = new TrackWrapper();
		parent.setName("Parent");

		final PlainShape theShape = new RectangleShape(new WorldLocation(2, 2, 2), new WorldLocation(3, 3, 3));
		final DynamicShapeWrapper fds = new DynamicShapeWrapper("alpha", theShape, Color.RED, new HiResDate(20000),
				"name");

		final String theName = "set one";
		final DynamicTrackShapeSetWrapper set = new DynamicTrackShapeSetWrapper(theName);
		set.add(fds);

		assertEquals(0, parent.getDynamicShapes().size(), "is empty");

		parent.add(set);

		assertEquals(1, parent.getDynamicShapes().size(), "now has item");

		parent.add(set);

		assertEquals(1, parent.getDynamicShapes().size(), "hasn't added duplicate");
	}

	@Test
	public void testDynamicInfill() {
		final TrackSegment ts0 = getDummyList();

		final TrackSegment ts1 = new TrackSegment(TrackSegment.ABSOLUTE);
		final FixWrapper newFix5 = new FixWrapper(new Fix(new HiResDate(150000), new WorldLocation(3, 4, 3), 1, 2));
		final FixWrapper newFix6 = new FixWrapper(new Fix(new HiResDate(160000), new WorldLocation(4, 4, 3), 1, 2));
		final FixWrapper newFix7 = new FixWrapper(new Fix(new HiResDate(170000), new WorldLocation(5, 4, 3), 1, 2));
		final FixWrapper newFix8 = new FixWrapper(new Fix(new HiResDate(180000), new WorldLocation(6, 4, 3), 1, 2));
		ts1.addFix(newFix5);
		ts1.addFix(newFix6);
		ts1.addFix(newFix7);
		ts1.addFix(newFix8);

		TrackSegment newS = new DynamicInfillSegment(ts0, ts1);
		assertEquals(10, newS.size(), "got lots of points");

		// does the first one have a label showing
		final FixWrapper first = (FixWrapper) newS.elements().nextElement();
		assertFalse(first.getLabelShowing(), "label not showing");

		// have a look at the generated points
		final Enumeration<Editable> pts = newS.elements();
		while (pts.hasMoreElements()) {
			final Editable editable = pts.nextElement();
			final FixWrapper fix = (FixWrapper) editable;
			// System.out.println(fix.getLocation().getLat() + ", " + fix.getLocation()
			// .getLong() + " , " + fix.getDateTimeGroup().getDate().getTime());
		}
		// System.out.println("========");

		// cause the monster problem
		newFix6.setDateTimeGroup(new HiResDate(210000));
		newFix7.setDateTimeGroup(new HiResDate(230000));
		newFix7.setDateTimeGroup(new HiResDate(250000));

		// try the mini algorithm first
		final FixWrapper[] items = DynamicInfillSegment.getLastElementsFrom(ts1, 2);
		assertTrue(items[0].getDateTimeGroup() != items[1].getDateTimeGroup(), "times should not be the same ");

		try {
			newS = new DynamicInfillSegment(ts0, ts1);
			assertEquals(10, newS.size(), "got lots of points");
		} catch (final RuntimeException re) {
			re.printStackTrace();
			fail("runtime exception thrown!!!");
		}

	}

	@Test
	public void testDynamicInfillLabel() {
		final TrackSegment ts0 = getDummyList();

		final TrackSegment ts1 = new TrackSegment(TrackSegment.ABSOLUTE);
		final FixWrapper newFix5 = new FixWrapper(new Fix(new HiResDate(150000), new WorldLocation(3, 4, 3), 1, 2));
		final FixWrapper newFix6 = new FixWrapper(new Fix(new HiResDate(160000), new WorldLocation(4, 4, 3), 1, 2));
		final FixWrapper newFix7 = new FixWrapper(new Fix(new HiResDate(170000), new WorldLocation(5, 4, 3), 1, 2));
		final FixWrapper newFix8 = new FixWrapper(new Fix(new HiResDate(180000), new WorldLocation(6, 4, 3), 1, 2));
		ts1.addFix(newFix5);
		ts1.addFix(newFix6);
		ts1.addFix(newFix7);
		ts1.addFix(newFix8);

		// set the label showing on the last point in the first leg
		final FixWrapper last = (FixWrapper) ts0.last();
		last.setLabelShowing(true);

		final TrackSegment newS = new DynamicInfillSegment(ts0, ts1);
		assertEquals(10, newS.size(), "got lots of points");

		// does the first one have a label showing
		final FixWrapper first = (FixWrapper) newS.elements().nextElement();
		assertFalse(first.getLabelShowing(), "label not showing");
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#elements()}.
	 */

	@Test
	public void testElements() {
		Enumeration<Editable> list = _tw.elements();
		_ctr = 0;
		while (list.hasMoreElements()) {
			_ctr++;
			list.nextElement();
		}

		final int startCount = _ctr;

		// give it a little more data
		final SensorWrapper sw = new SensorWrapper("sensor a");
		sw.add(new SensorContactWrapper("trk", new HiResDate(12), null, null, null, null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(13), null, null, null, null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(14), null, null, null, null, null, 0, null));
		_tw.add(sw);

		list = _tw.elements();
		_ctr = 0;
		while (list.hasMoreElements()) {
			_ctr++;
			list.nextElement();
		}
		assertEquals(startCount + 1, _ctr, "shows new item");
	}

	@Test
	public void testEmptyLayerBounds() {
		final Layers layers = new Layers();
		final TrackWrapper track = new TrackWrapper();
		track.setName("Track");
		final FixWrapper fix = createFix3(1000, 10, 20);
		track.addFix(fix);

		assertNotNull(layers.getBounds(), "returned an area");
		assertEquals(" 50\u00b051'17.63\"N 001\u00b020'32.10\"W ", layers.getBounds().getCentre().toString(),
				"correct location");
		assertEquals(" 51\u00b012'08.27\"N 001\u00b058'07.62\"W ", layers.getBounds().getTopLeft().toString(),
				"correct location");
		assertEquals(" 50\u00b030'26.99\"N 000\u00b042'56.58\"W ", layers.getBounds().getBottomRight().toString(),
				"correct location");

		// ok, now put the track in the layers
		layers.addThisLayer(track);
		assertEquals(" 10\u00b000'00.00\"N 020\u00b000'00.00\"E ", layers.getBounds().getCentre().toString(),
				"correct location");
		assertEquals(" 10\u00b000'42.43\"N 019\u00b059'16.92\"E ", layers.getBounds().getTopLeft().toString(),
				"correct location");
		assertEquals(" 09\u00b059'17.57\"N 020\u00b000'43.08\"E ", layers.getBounds().getBottomRight().toString(),
				"correct location");

	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#filterListTo(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */

	@Test
	public void testFilterListTo() {
		assertEquals(6, countVisibleItems(), "start off with them all visible");
		_tw.filterListTo(new HiResDate(200000), new HiResDate(400000));
		assertEquals(3, countVisibleItems(), "start off with them all visible");
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#splitTrack(Debrief.Wrappers.FixWrapper, boolean)}
	 * .
	 */

	@Test
	public void testFixAsVector() {
		final TrackSegment ts = new TrackSegment(TrackSegment.ABSOLUTE);
		final long period = 1000 * 60 * 60;
		final double speedKts = 60;
		WorldVector res = ts.vectorFor(period, speedKts, 0);
		assertEquals(0, res.getBearing(), 0.001, "Correct course");
		assertEquals(1, res.getRange(), 0.001, "Correct distance");
		res = ts.vectorFor(period, speedKts, 0.6);
		assertEquals(0.6, res.getBearing(), 0.001, "Correct course");
		assertEquals(1, res.getRange(), 0.001, "Correct distance");
		double easyCourse = MWC.Algorithms.Conversions.Degs2Rads(80);
		res = ts.vectorFor(period, speedKts, easyCourse);
		assertEquals(easyCourse, res.getBearing(), 0.001, "Correct course");
		assertEquals(1, res.getRange(), 0.001, "Correct distance");
		easyCourse = MWC.Algorithms.Conversions.Degs2Rads(280);
		res = ts.vectorFor(period, speedKts, easyCourse);
		assertEquals(easyCourse, res.getBearing(), 0.001, "Correct course");
		assertEquals(1, res.getRange(), 0.001, "Correct distance");
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#fixMoved()}.
	 */

	@Test
	public void testFixMoved() {
		// now something else
		_ctr = 0;
		final SensorWrapper sw = new SensorWrapper("some sensor") {
			private static final long serialVersionUID = 1L;

			@Override
			public void setHost(final TrackWrapper host) {
				super.setHost(host);
				_ctr++;
			}
		};
		final SensorContactWrapper scw = new SensorContactWrapper();
		scw.setDTG(new HiResDate(1000));
		sw.add(scw);
		_tw.add(sw);

		// it should only have been fired once
		assertEquals(1, _ctr, "only called to tell sensor of it's ownership");

		// tell the track it's moved
		_tw.fixMoved();

		// did it hear?
		assertEquals(2, _ctr, "got informed of sensor movement");

	}

	@Test
	public void testGenInfill() {
		final TrackSegment ts1 = new TrackSegment(TrackSegment.ABSOLUTE);
		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);

		ts1.addFix(createFix1(1000, 1, 0, 5d, 1, 0, 00d, 135, 12));
		ts1.addFix(createFix1(2000, 1, 0, 4d, 1, 0, 01d, 135, 12));
		ts1.addFix(createFix1(3000, 1, 0, 3d, 1, 0, 02d, 135, 12));
		ts1.addFix(createFix1(4000, 1, 0, 2d, 1, 0, 03d, 135, 12));

		ts2.addFix(createFix1(80000, 1, 0, 0d, 1, 0, 07d, 90, 12));
		ts2.addFix(createFix1(90000, 1, 0, 0d, 1, 0, 08d, 90, 12));
		ts2.addFix(createFix1(100000, 1, 0, 0d, 1, 0, 09d, 90, 12));
		ts2.addFix(createFix1(110000, 1, 0, 0d, 1, 0, 10d, 90, 12));
		ts2.addFix(createFix1(120000, 1, 0, 0d, 1, 0, 11d, 90, 12));
		ts2.addFix(createFix1(130000, 1, 0, 0d, 1, 0, 12d, 90, 12));

		// the test was broken after adding this line to the TrackSegment
		// constructor:
		// 458: tDelta = Math.max(tDelta, 10000);
		// tDelta is the time loop step
		// Fixed the test: increased second track times.
		final TrackSegment infill = new DynamicInfillSegment(ts1, ts2);

		// check there are the correct number of items
		assertEquals(6, infill.size(), "wrong num entries");

	}

	@Test
	public void testGenInfillSpeed() {
		final TrackSegment ts1 = new TrackSegment(TrackSegment.ABSOLUTE);
		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);

		ts1.addFix(createFix1(1000, 1, 0, 5d, 1, 0, 00d, 135, 12));
		ts1.addFix(createFix1(2000, 1, 0, 4d, 1, 0, 01d, 135, 12));
		ts1.addFix(createFix1(3000, 1, 0, 3d, 1, 0, 02d, 135, 12));
		ts1.addFix(createFix1(4000, 1, 0, 2d, 1, 0, 03d, 135, 12));

		ts2.addFix(createFix1(80000, 1, 0, 0d, 1, 0, 07d, 90, 12));
		ts2.addFix(createFix1(90000, 1, 0, 0d, 1, 0, 08d, 90, 12));
		ts2.addFix(createFix1(100000, 1, 0, 0d, 1, 0, 09d, 90, 12));
		ts2.addFix(createFix1(110000, 1, 0, 0d, 1, 0, 10d, 90, 12));
		ts2.addFix(createFix1(120000, 1, 0, 0d, 1, 0, 11d, 90, 12));
		ts2.addFix(createFix1(130000, 1, 0, 0d, 1, 0, 12d, 90, 12));

		// the test was broken after adding this line to the TrackSegment
		// constructor:
		// 458: tDelta = Math.max(tDelta, 10000);
		// tDelta is the time loop step
		// Fixed the test: increased second track times.
		final TrackSegment infill = new DynamicInfillSegment(ts1, ts2);

		// check there are the correct number of items
		assertEquals(6, infill.size(), "wrong num entries");
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getBounds()}.
	 */
	@Test
	public void testGetBounds() {
		final WorldArea correctBounds = new WorldArea(new WorldLocation(1, 1, 0), new WorldLocation(4, 6, 0));
		assertEquals(correctBounds, _tw.getBounds(), "wrong bounds returned");
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getEndDTG()}.
	 */

	@Test
	public void testGetEndDTG() {
		final HiResDate dt = _tw.getEndDTG();
		assertEquals(600000, dt.getDate().getTime(), "correct end time");
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#getItemsBetween(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */

	@Test
	public void testGetItemsBetween() {
		assertEquals(2, _tw.getItemsBetween(new HiResDate(200003), new HiResDate(400003)).size(), "found the items");
		assertEquals(3, _tw.getItemsBetween(new HiResDate(0), new HiResDate(300000)).size(), "found the items");
		assertEquals(6, _tw.getItemsBetween(new HiResDate(100000), new HiResDate(800000)).size(), "found the items");
	}

	@Test
	public void testGetItemsBetween_Second() {
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(0, 1), loc_1, 0, 0));
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(0, 2), loc_1, 0, 0));
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(0, 3), loc_1, 0, 0));
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(0, 4), loc_1, 0, 0));
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(0, 5), loc_1, 0, 0));
		final FixWrapper fw6 = new FixWrapper(new Fix(new HiResDate(0, 6), loc_1, 0, 0));
		final FixWrapper fw7 = new FixWrapper(new Fix(new HiResDate(0, 7), loc_1, 0, 0));
		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);
		tw.addFix(fw6);
		tw.addFix(fw7);
		fw1.setLabelShowing(true);
		fw2.setLabelShowing(true);
		fw3.setLabelShowing(true);
		fw4.setLabelShowing(true);
		fw5.setLabelShowing(true);
		fw6.setLabelShowing(true);
		fw7.setLabelShowing(true);

		Collection<Editable> col = tw.getItemsBetween(new HiResDate(0, 3), new HiResDate(0, 5));
		assertEquals(3, col.size(), "found correct number of items");

		// make the fourth item not visible
		fw4.setVisible(false);

		col = tw.getUnfilteredItems(new HiResDate(0, 3), new HiResDate(0, 5));
		assertEquals(2, col.size(), "found correct number of items");

		final Watchable[] pts2 = tw.getNearestTo(new HiResDate(0, 3));
		assertEquals(1, pts2.length, "found something");
		assertEquals(fw3, pts2[0], "found the third item");

		final Watchable[] pts = tw.getNearestTo(new HiResDate(0, 1));
		assertEquals(1, pts.length, "found something");
		assertEquals(fw1, pts[0], "found the first item");

	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals(TRACK_NAME, _tw.getName(), "correct name");
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#getNearestTo(MWC.GenericData.HiResDate)}
	 * .
	 */

	@Test
	public void testGetNearestTo() {
		Watchable[] res = _tw.getNearestTo(new HiResDate(300000));
		assertEquals(1, res.length, "found one");
		assertEquals(_fw1, res[0], "found right one");

		res = _tw.getNearestTo(new HiResDate(500000));
		assertEquals(1, res.length, "found one");
		assertEquals(_fw2, res[0], "found right one");

		res = _tw.getNearestTo(new HiResDate(400005));
		assertEquals(1, res.length, "found one");
		assertEquals(_fw2, res[0], "found right one");

		res = _tw.getNearestTo(new HiResDate(500000));
		assertEquals(1, res.length, "found one");
		assertEquals(_fw2, res[0], "found right one");

		res = _tw.getNearestTo(new HiResDate(299995));
		assertEquals(1, res.length, "found one");
		assertEquals(_fw1, res[0], "found right one");
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getPositionIterator()).
	 */

	@Test
	public void testGetPositions() {
		final Enumeration<Editable> list = _tw.getPositionIterator();
		// check the length
		_ctr = 0;
		while (list.hasMoreElements()) {
			list.nextElement();
			_ctr++;
		}
		assertEquals(6, _ctr, "have correct number of elements");
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getStartDTG()}.
	 */

	@Test
	public void testGetStartDTG() {
		final HiResDate dt = _tw.getStartDTG();
		assertEquals(100000, dt.getDate().getTime(), "correct end time");
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#getUnfilteredItems(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */

	@Test
	public void testGetUnfilteredItems() {
		assertEquals(3, _tw.getUnfilteredItems(new HiResDate(100000), new HiResDate(300000)).size(),
				"found right num ");
	}

	@Test
	public void testInfillSpline() {
		// generate the location spline
		final double[] times = new double[] { 3d, 4d, 8d, 9d };
		final double[] lats = new double[] { 1d, 1d, 3d, 4d };
		final double[] longs = new double[] { 1d, 2d, 4d, 4d };

		final CubicSpline latSpline = new CubicSpline(times, lats);
		final CubicSpline longSpline = new CubicSpline(times, longs);

		/*
		 * for (int t = 0; t < 4; t++) { System.out.println(longs[t] + ", " + lats[t] +
		 * "," + times[t]); }
		 */

		for (long t = 50; t < 80; t++) {
			final double tNow = t / 10d;
			final double thisLat = latSpline.interpolate(tNow);
			final double thisLong = longSpline.interpolate(tNow);
			// System.out.println(thisLong + ", " + thisLat + ", " + tNow);
		}
	}

	@Test
	public void testListOfLists() {
		final TrackSegment t1 = new TrackSegment(false);
		long time = 1000;
		int pos = 1;
		t1.addFix(createFix3(time++, ++pos, pos));
		t1.addFix(createFix3(time++, ++pos, pos));
		t1.addFix(createFix3(time++, ++pos, pos));
		t1.addFix(createFix3(time++, ++pos, pos));
		t1.addFix(createFix3(time++, ++pos, pos));

		final TrackSegment t2 = new TrackSegment(false);
		t2.addFix(createFix3(time++, ++pos, pos));
		t2.addFix(createFix3(time++, ++pos, pos));
		t2.addFix(createFix3(time++, ++pos, pos));
		t2.addFix(createFix3(time++, ++pos, pos));

		final TrackWrapper track = new TrackWrapper();
		track.add(t1);
		track.add(t2);

		int ctr = 0;
		final Enumeration<Editable> iter = track.getPositionIterator();
		while (iter.hasMoreElements()) {
			iter.nextElement();
			ctr++;
		}

		assertEquals(9, ctr, "correct num items");
	}

	@Test
	public final void testMyParams() {
		TrackWrapper ed = new TrackWrapper();
		ed.setName("blank");

		editableTesterSupport.testParams(ed);
		ed = null;
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#numFixes()}.
	 */

	@Test
	public void testNumFixes() {
		assertEquals(6, _tw.numFixes(), "have correct num");
	}

	@Test
	public void testPaintingColChange() {
		final TrackWrapper tw = new TrackWrapper();
		tw.setColor(Color.RED);
		tw.setName("test track");

		/**
		 * intention of this test: line is broken into three segments (red, yellow,
		 * green). - first of 2 points, next of 2 points, last of 3 points (14 values)
		 */

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100, WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		fw1.setColor(Color.red);
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200, WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		fw2.setColor(Color.yellow);
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300, WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");
		fw3.setColor(Color.green);
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400, WorldDistance.METRES), null)), 40, 140));
		fw4.setLabel("fw4");
		fw4.setColor(Color.green);
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500, WorldDistance.METRES), null)), 50, 150));
		fw5.setLabel("fw5");
		fw5.setColor(Color.green);
		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);

		callCount = 0;
		pointCount = 0;

		assertNull(tw.debug_GetPoints(), "our array of points starts empty");
		assertEquals(tw.debug_GetPointCtr(), 0, "our point array counter is zero");

		final CanvasType dummyDest = new TestMockCanvas();

		tw.paint(dummyDest);

		assertEquals(10, tw.debug_GetPoints().length, "our array has correct number of points");
		assertEquals(0, tw.debug_GetPointCtr(), "the pointer counter has been reset");

		// check it got called the correct number of times
		assertEquals(3, callCount, "We didnt paint enough polygons");
		assertEquals(14, pointCount, "We didnt paint enough polygons points");
	}

	@Test
	public void testPaintingLineJoinedChange() {
		final TrackWrapper tw = new TrackWrapper();
		tw.setColor(Color.RED);
		tw.setName("test track");

		/**
		 * intention of this test: line is broken into two segments - one of two points,
		 * the next of three, thus two polygons should be drawn - 10 points total (4
		 * then 6).
		 */

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100, WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		fw1.setColor(Color.red);
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200, WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		fw2.setColor(Color.red);
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300, WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");
		fw3.setColor(Color.red);
		fw3.setLineShowing(false);
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400, WorldDistance.METRES), null)), 40, 140));
		fw4.setLabel("fw4");
		fw4.setColor(Color.red);
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500, WorldDistance.METRES), null)), 50, 150));
		fw5.setLabel("fw5");
		fw5.setColor(Color.red);
		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);

		callCount = 0;
		pointCount = 0;

		assertNull(tw.debug_GetPoints(), "our array of points starts empty");
		assertEquals(tw.debug_GetPointCtr(), 0, "our point array counter is zero");

		final CanvasType dummyDest = new TestMockCanvas();

		tw.paint(dummyDest);

		assertEquals(10, tw.debug_GetPoints().length, "our array has correct number of points");
		assertEquals(0, tw.debug_GetPointCtr(), "the pointer counter has been reset");

		// check it got called the correct number of times
		assertEquals(2, callCount, "We didnt paint enough polygons");
		assertEquals(10, pointCount, "We didnt paint enough polygons points");

	}

	@Test
	public void testPaintingVisChange() {
		final TrackWrapper tw = new TrackWrapper();
		tw.setColor(Color.RED);
		tw.setName("test track");

		/**
		 * intention of this test: line is broken into two segments of two points, thus
		 * two polygons should be drawn, each with 4 points - 8 points total.
		 */

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100, WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		fw1.setColor(Color.red);
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200, WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		fw2.setColor(Color.red);
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300, WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");
		fw3.setColor(Color.red);
		fw3.setVisible(false);
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400, WorldDistance.METRES), null)), 40, 140));
		fw4.setLabel("fw4");
		fw4.setColor(Color.red);
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500, WorldDistance.METRES), null)), 50, 150));
		fw5.setLabel("fw5");
		fw5.setColor(Color.red);
		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);

		callCount = 0;
		pointCount = 0;

		assertNull(tw.debug_GetPoints(), "our array of points starts empty");
		assertEquals(tw.debug_GetPointCtr(), 0, "our point array counter is zero");

		final CanvasType dummyDest = new TestMockCanvas();

		tw.paint(dummyDest);

		assertEquals(10, tw.debug_GetPoints().length, "our array has correct number of points");
		assertEquals(0, tw.debug_GetPointCtr(), "the pointer counter has been reset");

		// check it got called the correct number of times
		assertEquals(2, callCount, "We didnt paint enough polygons");
		assertEquals(8, pointCount, "We didnt paint enough polygons points");

	}

	@Test
	public void testPaintSinglePointSegment() {
		final TrackWrapper tw = new TrackWrapper();
		tw.setName("Test Track");
		final Fix theFix = new Fix(new HiResDate(1000), new WorldLocation(2, 2, 2), 1, 5);
		final FixWrapper fw = new FixWrapper(theFix) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintMe(final CanvasType dest, final WorldLocation centre, final Color theColor) {
				super.paintMe(dest, centre, theColor);

				_ctr++;
			}

		};
		tw.add(fw);

		_ctr = 0;

		// ok, now try to paint
		final TestMockCanvas canvas = new TestMockCanvas();
		tw.paint(canvas);

		assertEquals(1, _ctr, "single point segment got painted");

	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#rangeFrom(MWC.GenericData.WorldLocation)}
	 * .
	 */

	@Test
	public void testRangeFromWorldLocation() {
		final WorldLocation wl = new WorldLocation(4, 1, 0);
		final double res = _tw.rangeFrom(wl);
		assertEquals(0d, res, 0.001, "correct range");
	}

	// ////////////

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#removeElement(MWC.GUI.Editable)}.
	 */

	@Test
	public void testRemoveElement() {
		assertEquals(6, _tw.numFixes(), "all there");
		_tw.removeElement(_fw1);
		assertEquals(5, _tw.numFixes(), "one less now");
		_tw.removeElement(_fw2);
		assertEquals(4, _tw.numFixes(), "one less now");
	}

	@Test
	public void testSegmentList() {

		final TrackWrapper tw = new TrackWrapper();

		final TrackSegment ts0 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts0.addFix(createFix2(10000, 1, 1, 20, 30));
		ts0.addFix(createFix2(11000, 1, 1, 20, 30));
		ts0.addFix(createFix2(12000, 1, 1, 20, 30));
		ts0.addFix(createFix2(13000, 1, 1, 20, 30));

		final TrackSegment ts1 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts1.addFix(createFix2(14000, 1, 1, 20, 30));
		ts1.addFix(createFix2(15000, 1, 1, 20, 30));
		ts1.addFix(createFix2(16000, 1, 1, 20, 30));
		ts1.addFix(createFix2(17000, 1, 1, 20, 30));

		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts2.addFix(createFix2(18000, 1, 1, 20, 30));
		ts2.addFix(createFix2(19000, 1, 1, 20, 30));
		ts2.addFix(createFix2(20000, 1, 1, 20, 30));
		ts2.addFix(createFix2(21000, 1, 1, 20, 30));

		tw.add(ts0);
		tw.add(ts1);
		tw.add(ts2);

		final SegmentList sList = tw.getSegments();
		TrackSegment i1 = sList.getSegmentFor(16000);
		assertEquals(ts1, i1, "correct segment");
		i1 = sList.getSegmentFor(17000);
		assertEquals(ts1, i1, "correct segment");
		i1 = sList.getSegmentFor(14000);
		assertEquals(ts1, i1, "correct segment");

		TrackSegment i0 = sList.getSegmentFor(10000);
		assertEquals(ts0, i0, "correct segment");
		i0 = sList.getSegmentFor(13000);
		assertEquals(ts0, i0, "correct segment");
		i0 = sList.getSegmentFor(11000);
		assertEquals(ts0, i0, "correct segment");

		TrackSegment i2 = sList.getSegmentFor(18000);
		assertEquals(ts2, i2, "correct segment");
		i2 = sList.getSegmentFor(21000);
		assertEquals(ts2, i2, "correct segment");

	}

	@Test
	public void testSetArrowFrequency() {
		final TrackWrapper parent = new TrackWrapper();
		parent.setName("Parent");
		final long startT = System.currentTimeMillis();

		final TrackSegment ts = new TrackSegment(TrackSegment.ABSOLUTE);
		ts.setName("Some name");
		final int num = 3200;

		for (int i = 0; i < num; i++) {
			ts.add(getFix(10001 * i, 2, 4));
		}

		parent.add(ts);

		// ok, check before freq
		assertEquals(0, countDecorations(parent, false, true, false), "no syms");

		// ok, now set freq. YES. WE'RE USING A VERY HIGH INTERVAL, as an edge case
		parent.setArrowFrequency(new HiResDate(50000000));

		// System.out.println("elapsed setting syms:" + (System.currentTimeMillis()
		// - startT));

		// ok, check before freq
		assertEquals(1, countDecorations(parent, false, true, false), "with syms");
	}

	@Test
	public void testSetLabelFrequency() {
		final TrackWrapper parent = new TrackWrapper();
		parent.setName("Parent");

		final TrackSegment ts = new TrackSegment(TrackSegment.ABSOLUTE);
		ts.setName("Some name");
		final int num = 3200;

		for (int i = 0; i < num; i++) {
			ts.add(getFix(10001 * i, 2, 4));
		}

		parent.add(ts);

		// ok, check before freq
		assertEquals(0, countDecorations(parent, false, false, true), "no syms");

		// ok, now set freq
		parent.setLabelFrequency(new HiResDate(50000));

		// ok, check before freq
		assertEquals(640, countDecorations(parent, false, false, true), "with syms");

		// ok, now another rate
		parent.setLabelFrequency(new HiResDate(31111));

		// ok, check before freq
		assertEquals(1029, countDecorations(parent, false, false, true), "with syms");

		// ok, now set the original frequency. This will effectively
		// check that we're resetting the labels before we decide which
		// ones we do want to display
		parent.setLabelFrequency(new HiResDate(50000));

		// ok, check before freq
		assertEquals(640, countDecorations(parent, false, false, true), "with syms");

	}

	@Test
	public void testSetLabelFrequencyWithGap() throws FileNotFoundException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new Debrief.ReaderWriter.Replay.ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");

		// get the track
		final TrackWrapper track = (TrackWrapper) tLayers.findLayer("NELSON");

		// ok, now delete some items
		final ArrayList<FixWrapper> toDelete = new ArrayList<FixWrapper>();

		final Enumeration<Editable> pIter = track.getPositionIterator();
		int ctr = 0;
		while (pIter.hasMoreElements()) {
			ctr++;
			final FixWrapper nextF = (FixWrapper) pIter.nextElement();
			if (ctr > 60 && ctr < 150) {
				toDelete.add(nextF);
			}
		}

		// ok check before
		assertEquals(0, countDecorations(track, false, false, true), "no syms");

		// ok, now set freq
		track.setLabelFrequency(new HiResDate(10 * 60000));

		// ok, check before freq
		assertEquals(41, countDecorations(track, false, false, true), "with syms");

		// clear them again
		track.setLabelFrequency(new HiResDate(0));

		// ok, check before freq
		assertEquals(0, countDecorations(track, false, false, true), "with syms");

		for (final FixWrapper fix : toDelete) {
			track.removeElement(fix);
		}

		// ok, now set freq
		track.setLabelFrequency(new HiResDate(10 * 60000));

		// ok, check before freq
		assertEquals(32, countDecorations(track, false, false, true), "with syms");

		/*
		 * pIter = track.getPositionIterator(); while (pIter.hasMoreElements()) { final
		 * FixWrapper fix = (FixWrapper) pIter.nextElement(); if (fix.getLabelShowing())
		 * { System.out.println(fix.getLabel()); } }
		 */
	}

	@Test
	public void testSetSymbolFrequency() {
		final TrackWrapper parent = new TrackWrapper();
		parent.setName("Parent");
		final long startT = System.currentTimeMillis();

		final TrackSegment ts = new TrackSegment(TrackSegment.ABSOLUTE);
		ts.setName("Some name");
		final int num = 3200;

		for (int i = 0; i < num; i++) {
			ts.add(getFix(10001 * i, 2, 4));
		}

		parent.add(ts);

		// ok, check before freq
		assertEquals(0, countDecorations(parent, true, false, false), "no syms");

		// ok, now set freq
		parent.setSymbolFrequency(new HiResDate(50000));

		// System.out.println("elapsed setting syms:" + (System.currentTimeMillis()
		// - startT));
		// ok, check before freq
		assertEquals(640, countDecorations(parent, true, false, false), "with syms");
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#shiftTrack(java.util.Enumeration, MWC.GenericData.WorldVector)}
	 * .
	 */

	@Test
	public void testShiftTrack() {
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#shift(MWC.GenericData.WorldLocation, MWC.GenericData.WorldVector)}
	 * .
	 */

	@Test
	public void testShiftWorldLocationWorldVector() {
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#shift(MWC.GenericData.WorldVector)}.
	 */

	@Test
	public void testShiftWorldVector() {
	}

	@Test
	public void testSinglePoint() {
		final TrackWrapper tw = new TrackWrapper();
		assertFalse(tw.isSinglePointTrack());
		tw.add(createFix3(100, 12, 13));
		assertTrue(tw.isSinglePointTrack());
		tw.add(createFix3(200, 12, 13));
		assertFalse(tw.isSinglePointTrack());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#splitTrack(Debrief.Wrappers.FixWrapper, boolean)}
	 * .
	 */

	@Test
	public void testSplitTrack1() {
		final Vector<TrackSegment> segs = _tw.splitTrack(_fw1, true);
		assertEquals(2, segs.size(), "now two segments");
		assertEquals(2, segs.firstElement().size(), "first is of correct length");
		assertEquals(4, segs.lastElement().size(), "first is of correct length");

		// check the names.
		SegmentList list = _tw.getSegments();
		Enumeration<Editable> segments = list.elements();
		TrackSegment s1 = (TrackSegment) segments.nextElement();
		TrackSegment s2 = (TrackSegment) segments.nextElement();
		assertEquals("010001.40", s1.getName(), "correct layer name:");
		assertEquals("010005.00", s2.getName(), "correct layer name:");

		// split it again
		final Vector<TrackSegment> segs2 = _tw.splitTrack(_fw2, true);
		assertEquals(2, segs2.size(), "two blocks returned");
		assertEquals(3, numSegments(), "has 3 segments");
		assertEquals(2, segs2.firstElement().size(), "first is of correct length");
		assertEquals(2, segs2.lastElement().size(), "first is of correct length");

		list = _tw.getSegments();
		segments = list.elements();
		s1 = (TrackSegment) segments.nextElement();
		s2 = (TrackSegment) segments.nextElement();
		final TrackSegment s3 = (TrackSegment) segments.nextElement();
		assertEquals("010001.40", s1.getName(), "correct layer name:");
		assertEquals("010005.00", s2.getName(), "correct layer name:");
		assertEquals("010008.20", s3.getName(), "correct layer name:");

		// now recombine them
		_tw.combineSections(segs2);
		assertEquals(2, numSegments(), "has 2 segments");
		assertEquals(2, segs.firstElement().size(), "first is of correct length");
		assertEquals(4, segs.lastElement().size(), "first is of correct length");

		list = _tw.getSegments();
		segments = list.elements();
		s1 = (TrackSegment) segments.nextElement();
		s2 = (TrackSegment) segments.nextElement();
		assertEquals("010001.40", s1.getName(), "correct layer name:");
		assertEquals("010005.00", s2.getName(), "correct layer name:");

		// note: the second split replaced the last leg in 'segs' with a new one
		// (which starts at the same time). This is what happens when undoing
		// nested splits.
		_tw.combineSections(segs);
		assertEquals(1, numSegments(), "has 1 segment1");
		assertEquals(6, segs.firstElement().size(), "first is of correct length");

	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#splitTrack(Debrief.Wrappers.FixWrapper, boolean)}
	 * .
	 */

	@Test
	public void testSplitTrack2() {
		final Vector<TrackSegment> segs = _tw.splitTrack(_fw1, false);
		assertEquals(2, segs.size(), "now two segments");
		assertEquals(3, segs.firstElement().size(), "first is of correct length");
		assertEquals(3, segs.lastElement().size(), "first is of correct length");
	}

	@Test
	public void testSplitTrackIntoLegs() {
		final TrackWrapper gw = new TrackWrapper();
		gw.setName("the track");

		gw.addFix(getFix(1000, 22, 33));
		gw.addFix(getFix(2000, 22, 33));
		gw.addFix(getFix(2100, 22, 33));
		gw.addFix(getFix(4000, 22, 33));
		gw.addFix(getFix(5000, 22, 33));
		gw.addFix(getFix(6000, 22, 33));
		gw.addFix(getFix(7000, 22, 33));
		gw.addFix(getFix(8000, 22, 33));
		gw.addFix(getFix(9000, 22, 33));
		gw.addFix(getFix(10000, 22, 33));
		gw.addFix(getFix(11100, 22, 33));
		gw.addFix(getFix(12000, 22, 33));
		gw.addFix(getFix(13000, 22, 33));
		gw.addFix(getFix(14000, 22, 33));

		assertEquals(1, gw.getSegments().size(), "just one leg");

		final List<TrackSegment> splits = TrackWrapper_Support.splitTrackAtJumps(gw, 1000);
		assertTrue(splits != null, "found some");
		assertEquals(3, gw.getSegments().size(), "In 3 legs");
	}

	/**
	 * .
	 */

	@Test
	public void testTMASegment() {
		// //////////////////////////////////
		// start off building from a track
		// //////////////////////////////////
		final TrackWrapper tw = new TrackWrapper();

		final FixWrapper f1 = createFix2(100000, 1, 1, 4, 12);
		final FixWrapper f2 = createFix2(200000, 2, 3, 4, 12);
		tw.addFix(createFix2(80000, 3, 3, 4, 12));
		tw.addFix(f1);
		tw.addFix(f2);
		tw.addFix(createFix2(300000, 3, 3, 4, 12));
		tw.addFix(createFix2(400000, 4, 6, 4, 12));

		final WorldVector offset = new WorldVector(12, 12, 0);
		final WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);
		final double course = 33;

		// check the before
		FixWrapper firstFix = null;

		// ////////////////////////
		// NOW FROM A SENSOR WRAPPER
		// /////////////////////////
		final SensorWrapper sw = new SensorWrapper("some sensor");

		// store the sensor
		tw.add(sw);

		final SensorContactWrapper[] items2 = new SensorContactWrapper[4];
		items2[0] = createSensorItem(tw, sw, 110000);
		items2[1] = createSensorItem(tw, sw, 120000);
		items2[2] = createSensorItem(tw, sw, 130000);
		items2[3] = createSensorItem(tw, sw, 140000);

		// sort out the host
		for (int i = 0; i < items2.length; i++) {
			final SensorContactWrapper sensorContactWrapper = items2[i];
			sensorContactWrapper.setSensor(sw);
		}

		final CoreTMASegment seg1 = new RelativeTMASegment(items2, offset, speed, course, null, Color.yellow);

		// check the create worked
		assertEquals(4, seg1.size(), "enough points created");

		// check the before
		firstFix = (FixWrapper) seg1.getData().iterator().next();
		assertEquals(33, seg1.getCourse(), 0.001, "correct course before");
		assertEquals(5, seg1.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "correct speed before");
		assertEquals(33, MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001, "correct course before");
		assertEquals(5, firstFix.getSpeed(), 0.001, "correct speed before");

		seg1.setCourse(35);
		seg1.setSpeed(new WorldSpeed(15, WorldSpeed.Kts));

		assertEquals(35, seg1.getCourse(), 0.001, "correct course after");
		assertEquals(15, seg1.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "correct speed after");
		assertEquals(35, MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001, "correct course after");
		assertEquals(15, firstFix.getSpeed(), 0.001, "correct speed after");

		// ///////////////////////////////////////////
		// lastly, build from a set of sensor observations
		// ///////////////////////////////////////////
		final SensorContactWrapper[] items = new SensorContactWrapper[5];
		items[0] = createSensorItem(tw, sw, 110000);
		items[1] = createSensorItem(tw, sw, 115000);
		items[2] = createSensorItem(tw, sw, 119000);
		items[3] = createSensorItem(tw, sw, 141000);
		items[4] = createSensorItem(tw, sw, 150000);

		// sort out the host
		for (int i = 0; i < items.length; i++) {
			final SensorContactWrapper sensorContactWrapper = items[i];
			sensorContactWrapper.setSensor(sw);
		}

		final RelativeTMASegment seg2 = new RelativeTMASegment(items, offset, speed, course, null, Color.yellow);

		// check the create worked
		assertEquals(5, seg2.size(), "enough points created");

		final Iterator<Editable> someIt = seg2.getData().iterator();
		// check the before
		firstFix = (FixWrapper) someIt.next();
		assertEquals(33, seg2.getCourse(), 0.001, "correct course before");
		assertEquals(5, seg2.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "correct speed before");
		assertEquals(33, MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001, "correct course before");
		assertEquals(5, firstFix.getSpeed(), 0.001, "correct speed before");

		// check the next dtg
		firstFix = (FixWrapper) someIt.next();
		assertEquals(115000, firstFix.getDTG().getDate().getTime(), 0.001, "check dtg produced");
		firstFix = (FixWrapper) someIt.next();
		assertEquals(119000, firstFix.getDTG().getDate().getTime(), 0.001, "check dtg produced");
		firstFix = (FixWrapper) someIt.next();
		assertEquals(141000, firstFix.getDTG().getDate().getTime(), 0.001, "check dtg produced");
		firstFix = (FixWrapper) someIt.next();
		assertEquals(150000, firstFix.getDTG().getDate().getTime(), 0.001, "check dtg produced");

		seg2.setCourse(35);
		seg2.setSpeed(new WorldSpeed(15, WorldSpeed.Kts));

		assertEquals(35, seg2.getCourse(), 0.001, "correct course after");
		assertEquals(15, seg2.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "correct speed after");
		assertEquals(35, MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001, "correct course after");
		assertEquals(15, firstFix.getSpeed(), 0.001, "correct speed after");

		// check that new points get added as we extend the solution
		assertEquals(5, seg2.size(), "start with correct points");

		seg2.setDTG_End(new HiResDate(200000));
		assertEquals(10, seg2.size(), "more points after stretch");
		assertEquals(200000, seg2.getDTG_End().getDate().getTime(), "new end time");

		// now try to stretch the start

		seg2.setDTG_Start(new HiResDate(80002));
		assertEquals(80002, seg2.getDTG_Start().getDate().getTime(), "new start time");
		assertEquals(13, seg2.size(), "more points after stretch");
	}

	/**
	 * .
	 */

	@Test
	public void testTMASegmentRotate() {

		final FixWrapper f1 = createFix2(100000, 1, 1, 270, 12);
		final FixWrapper f2 = createFix2(200000, 1, 0, 270, 12);
		final WorldVector vector = new WorldVector(0, 1, 0);
		final RelativeTMASegment ts = new RelativeTMASegment(270, new WorldSpeed(12, WorldSpeed.Kts), vector, null,
				null, null) {
			private static final long serialVersionUID = 1L;

			@Override
			public WorldLocation getHostLocation() {
				return new WorldLocation(0, 1, 0);
			}
		};
		ts.addFix(f1);
		ts.addFix(f2);

		final Iterator<Editable> iter = ts.getData().iterator();
		iter.next();
		final FixWrapper farEnd = (FixWrapper) iter.next();
		final WorldLocation origin = farEnd.getLocation();
		final double brg = MWC.Algorithms.Conversions.Degs2Rads(-90);
		ts.rotate(brg, origin);

		// check we're on the new course
		assertEquals(-90, ts.getOffsetBearing(), 0.001, "at new offset bearing");
		assertEquals(1, ts.getOffsetRange().getValueIn(WorldDistance.DEGS), 0.001, "at new offset range");
		assertEquals(0, ts.getCourse(), 0.001, "on new course");
		assertEquals(12, ts.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "at original speed");

		// ok, try to turn back!
		ts.rotate(-brg, origin);

		// check we're on the new course
		assertEquals(0, ts.getOffsetBearing(), 0.001, "at new offset bearing");
		assertEquals(1, ts.getOffsetRange().getValueIn(WorldDistance.DEGS), 0.001, "at new offset range");
		assertEquals(270, ts.getCourse(), 0.001, "on new course");
		assertEquals(12, ts.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "at original speed");
	}

	/**
	 * .
	 */

	@Test
	public void testTMASegmentStretch() {
		final FixWrapper f1 = createFix2(0, 1, 1, 270, 12);
		final FixWrapper f2 = createFix2(1000 * 60 * 60, 1, 0, 270, 12);
		final WorldVector vector = new WorldVector(0, 1, 0);
		final RelativeTMASegment ts = new RelativeTMASegment(270, new WorldSpeed(12, WorldSpeed.Kts), vector, null,
				null, null) {
			private static final long serialVersionUID = 1L;

			@Override
			public WorldLocation getHostLocation() {
				return new WorldLocation(0, 0, 0);
			}
		};
		ts.addFix(f1);
		ts.addFix(f2);

		final Iterator<Editable> iter = ts.getData().iterator();
		final FixWrapper nearEnd = (FixWrapper) iter.next();
		final WorldLocation origin = nearEnd.getLocation();
		final double rng = 2;
		ts.stretch(rng, origin);

		// check we're on the new course
		assertEquals(0, ts.getOffsetBearing(), 0.001, "at new offset bearing");
		assertEquals(1, ts.getOffsetRange().getValueIn(WorldDistance.DEGS), 0.001, "at new offset range");
		assertEquals(270, ts.getCourse(), 1, "on new course");
		assertEquals(120, ts.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "at new speed");

		// ok, try to turn back!
		ts.rotate(-rng, origin);

		// check we're on the new course
		assertEquals(0, ts.getOffsetBearing(), 0.001, "at new offset bearing");
		assertEquals(1, ts.getOffsetRange().getValueIn(WorldDistance.DEGS), 0.001, "at new offset range");
		assertEquals(385, ts.getCourse(), 1, "on new course");
		assertEquals(120, ts.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "at original speed");
	}

	/**
	 * .
	 */

	@Test
	public void testTMASplit() {
		// //////////////////////////////////
		// start off building from a track
		// //////////////////////////////////
		final TrackWrapper tw = new TrackWrapper();

		tw.addFix(createFix2(100000, 1, 1, 4, 12));
		tw.addFix(createFix2(200000, 2, 3, 4, 12));
		tw.addFix(createFix2(300000, 3, 3, 4, 12));
		tw.addFix(createFix2(400000, 4, 6, 4, 12));
		tw.addFix(createFix2(500000, 4, 6, 4, 12));
		tw.addFix(createFix2(600000, 4, 6, 4, 12));
		tw.addFix(createFix2(700000, 4, 6, 4, 12));

		final WorldVector offset = new WorldVector(12, 12, 0);
		final WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);
		final double course = 33;

		// ok, create the segment
		CoreTMASegment seg = null;

		// check the before
		FixWrapper firstFix = null;

		// ////////////////////////
		// NOW FROM A SENSOR WRAPPER
		// /////////////////////////
		final SensorWrapper sw = new SensorWrapper("some sensor");
		sw.setHost(tw);

		// attach the sensor to the host
		tw.add(sw);

		final int senLen = 30;
		final SensorContactWrapper[] items = new SensorContactWrapper[senLen];

		for (int i = 0; i < senLen; i++) {
			items[i] = createSensorItem(tw, sw, 100000 + i * 10000);

		}

		// items[0] = createSensorItem(tw, sw, 110000);
		// items[1] = createSensorItem(tw, sw, 120000);
		// items[2] = createSensorItem(tw, sw, 130000);
		// items[3] = createSensorItem(tw, sw, 140000);
		// items[4] = createSensorItem(tw, sw, 150000);
		// items[5] = createSensorItem(tw, sw, 160000);
		// items[6] = createSensorItem(tw, sw, 170000);

		// sort out the host
		for (int i = 0; i < items.length; i++) {
			final SensorContactWrapper sensorContactWrapper = items[i];
			sw.add(sensorContactWrapper);
		}

		// seg = new RelativeTMASegment(sw, offset, speed, course, null);
		seg = new RelativeTMASegment(items, offset, speed, course, null, Color.yellow);

		// check the create worked
		assertEquals(30, seg.size(), "enough points created");

		// check the before
		firstFix = (FixWrapper) seg.getData().iterator().next();
		assertEquals(33, seg.getCourse(), 0.001, "correct course before");
		assertEquals(5, seg.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "correct speed before");
		assertEquals(33, MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001, "correct course before");
		assertEquals(5, firstFix.getSpeed(), 0.001, "correct speed before");

		// ok, now do the split
		final TrackWrapper segW = new TrackWrapper();
		segW.setName("TMA");
		segW.add(seg);

		// get hold of an item in the segment
		final Enumeration<Editable> enumer = seg.elements();
		enumer.nextElement();
		enumer.nextElement();
		enumer.nextElement();
		enumer.nextElement();
		enumer.nextElement();
		enumer.nextElement();
		enumer.nextElement();
		enumer.nextElement();
		enumer.nextElement();
		final FixWrapper fw = (FixWrapper) enumer.nextElement();
		assertNotNull(fw, "Found a fix");

		// do the split
		final Vector<TrackSegment> segs = segW.splitTrack(fw, false);

		// check we have enough segments
		assertEquals(2, segs.size(), "now two segments");
		assertEquals(10, segs.firstElement().size(), "first is of correct length");
		assertEquals(20, segs.lastElement().size(), "second is of correct length");

		// check they're of the correct type
		final TrackSegment seg1 = segs.firstElement();
		final TrackSegment seg2 = segs.lastElement();
		assertTrue(seg1 instanceof RelativeTMASegment, " is a tma segment");
		assertTrue(seg2 instanceof RelativeTMASegment, " is a tma segment");
	}

	/**
	 * .
	 */

	@Test
	public void testTMASplit2() {
		// //////////////////////////////////
		// start off building from a track
		// //////////////////////////////////
		final TrackWrapper tw = new TrackWrapper();

		tw.addFix(createFix2(100000, 1, 1, 4, 12));
		tw.addFix(createFix2(200000, 2, 3, 4, 12));
		tw.addFix(createFix2(300000, 3, 3, 4, 12));
		tw.addFix(createFix2(400000, 4, 6, 4, 12));
		tw.addFix(createFix2(500000, 4, 6, 4, 12));
		tw.addFix(createFix2(600000, 4, 6, 4, 12));
		tw.addFix(createFix2(700000, 4, 6, 4, 12));

		final WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);
		final double course = 33;

		// ok, create the segment
		CoreTMASegment seg = null;

		// check the before
		FixWrapper firstFix = null;

		// ////////////////////////
		// NOW AN ABSOLUTE ONE
		// /////////////////////////
		final WorldLocation origin = new WorldLocation(12, 12, 12);
		final HiResDate startTime = new HiResDate(11 * 60 * 1000);
		final HiResDate endTime = new HiResDate(17 * 60 * 1000);
		seg = new AbsoluteTMASegment(course, speed, origin, startTime, endTime);

		// check the create worked
		assertEquals(7, seg.size(), "enough points created");

		// check the before
		firstFix = (FixWrapper) seg.getData().iterator().next();
		assertEquals(33, seg.getCourse(), 0.001, "correct course before");
		assertEquals(5, seg.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "correct speed before");
		assertEquals(33, MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001, "correct course before");
		assertEquals(5, firstFix.getSpeed(), 0.001, "correct speed before");

		// ok, now do the split
		final TrackWrapper segW = new TrackWrapper();
		segW.setName("TMA");
		segW.add(seg);

		// get hold of an item in the segment
		final Enumeration<Editable> enumer = seg.elements();
		enumer.nextElement();
		enumer.nextElement();
		final FixWrapper fw = (FixWrapper) enumer.nextElement();
		assertNotNull(fw, "Found a fix");

		// do the split
		final Vector<TrackSegment> segs = segW.splitTrack(fw, false);

		// check we have enough segments
		assertEquals(2, segs.size(), "now two segments");
		assertEquals(3, segs.firstElement().size(), "first is of correct length");
		assertEquals(4, segs.lastElement().size(), "first is of correct length");

		// check they're of the correct type
		final TrackSegment seg1 = segs.firstElement();
		final TrackSegment seg2 = segs.lastElement();
		assertTrue(seg1 instanceof AbsoluteTMASegment, " is a tma segment");
		assertTrue(seg2 instanceof AbsoluteTMASegment, " is a tma segment");

	}

	@Test
	public void testTrackGroup1() {
		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts2.addFix(createFix3(310000, 32, 33));
		ts2.addFix(createFix3(311000, 32, 33));
		ts2.addFix(createFix3(312000, 32, 33));
		ts2.addFix(createFix3(313000, 32, 33));
		ts2.addFix(createFix3(314000, 32, 33));
		final TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals(6, trackLength(), "track starts correctly");
		assertEquals(5, tw3.numFixes(), "track 3 starts correctly");
		assertEquals(2, theLayers.size(), "have right num tracks");

		// do a merge
		final Layer[] parents = new Layer[] { _tw, tw3 };
		final Editable[] subjects = new Editable[] { _tw, ts2 };
		TrackWrapper.groupTracks(_tw, theLayers, parents, subjects);

		// have a look at the results
		assertEquals(11, _tw.numFixes(), "track 3 is longer");

		// check it's been a group, not an add
		_ctr = 0;
		final SegmentList sl = _tw.getSegments();
		final Enumeration<Editable> segments = sl.elements();
		while (segments.hasMoreElements()) {
			_ctr++;
			segments.nextElement();
		}
		assertEquals(2, _ctr, "track _tw has several segments");

		assertEquals(1, theLayers.size(), "track got ditched");
	}

	@Test
	public void testTrackGroupOrder() {
		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts2.addFix(createFix3(310000, 32, 33));
		ts2.addFix(createFix3(311000, 32, 33));
		ts2.addFix(createFix3(312000, 32, 33));
		ts2.addFix(createFix3(313000, 32, 33));
		ts2.addFix(createFix3(314000, 32, 33));

		final TrackSegment ts3 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts3.addFix(createFix3(410000, 32, 33));
		ts3.addFix(createFix3(411000, 32, 33));
		ts3.addFix(createFix3(412000, 32, 33));
		ts3.addFix(createFix3(413000, 32, 33));
		ts3.addFix(createFix3(414000, 32, 33));

		TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		tw3.add(ts3);

		SegmentList list = tw3.getSegments();
		Enumeration<Editable> segments = list.elements();
		TrackSegment seg1 = (TrackSegment) segments.nextElement();
		TrackSegment seg2 = (TrackSegment) segments.nextElement();

		assertEquals(ts2, seg1, "correct one is first");
		assertEquals(ts3, seg2, "correct one is segment");

		// try in another list
		assertEquals(ts2, seg1, "correct one is first");
		assertEquals(ts3, seg2, "correct one is segment");

		tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		tw3.add(ts3);

		list = tw3.getSegments();
		segments = list.elements();
		seg1 = (TrackSegment) segments.nextElement();
		seg2 = (TrackSegment) segments.nextElement();
	}

	@Test
	public void testTrackMerge1() {
		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts2.setName("ts2");
		ts2.addFix(createFix3(910000, 32, 33));
		ts2.addFix(createFix3(911000, 32, 33));
		ts2.addFix(createFix3(912000, 32, 33));
		ts2.addFix(createFix3(913000, 32, 33));
		ts2.addFix(createFix3(914000, 32, 33));
		final TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		_tw.setSymbolType(SymbolFactory.SCALED_FRIGATE);
		_tw.setSymbolLength(new WorldDistance(200, WorldDistance.METRES));
		_tw.setSymbolWidth(new WorldDistance(40, WorldDistance.METRES));
		_tw.setSymbolColor(DebriefColors.CYAN);

		// check startup status
		assertEquals(6, trackLength(), "track starts correctly");
		assertEquals(5, tw3.numFixes(), "track 3 starts correctly");
		assertEquals(2, theLayers.size(), "have right num tracks");
		assertEquals("test track", _fw1.getTrackWrapper().getName(), "fix starts in place");

		// do a merge
		final Editable[] subjects = new Editable[] { _tw, ts2 };
		final TrackWrapper newTarget = new TrackWrapper();
		newTarget.setName("Merged");

		TrackWrapper.mergeTracks(newTarget, theLayers, subjects, null);

		// have a look at the results
		assertEquals(3, theLayers.size(), "new track got created");
		assertEquals(11, newTarget.numFixes(), "track 3 is longer");
		assertEquals("Merged", newTarget.getName(), "new track has correct name");
		assertEquals("test track", _fw1.getTrackWrapper().getName(), "original fix still in place");

		assertEquals(SymbolFactory.SCALED_FRIGATE, newTarget.getSymbolType());
		assertEquals(new WorldDistance(200, WorldDistance.METRES), newTarget.getSymbolLength());
		assertEquals(new WorldDistance(40, WorldDistance.METRES), newTarget.getSymbolWidth());
		assertEquals(DebriefColors.CYAN, newTarget.getSymbolColor());

	}

	@Test
	public void testTrackMerge2() {
		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts2.addFix(createFix3(910000, 32, 33));
		ts2.addFix(createFix3(911000, 32, 33));
		ts2.addFix(createFix3(912000, 32, 33));
		ts2.addFix(createFix3(913000, 32, 33));
		ts2.addFix(createFix3(914000, 32, 33));
		final TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals(6, trackLength(), "track starts correctly");
		assertEquals(5, tw3.numFixes(), "track 3 starts correctly");
		assertEquals(2, theLayers.size(), "have right num tracks");
		assertEquals(5, ts2.size(), "segment is correct length");

		// do a merge
		final Editable[] subjects = new Editable[] { _tw, ts2 };
		TrackWrapper.mergeTracks(_tw, theLayers, subjects, null);

		// have a look at the results
		assertEquals(17, _tw.numFixes(), "track is longer");
		assertEquals(2, theLayers.size(), "track got ditched");
		final TrackSegment sl = (TrackSegment) _tw.getSegments().elements().nextElement();
		assertEquals(17, sl.size(), "just the one segment - with all our points");

	}

	@Test
	public void testTrackMerge3() {
		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts2.addFix(createFix3(310000, 32, 33));
		ts2.addFix(createFix3(311000, 32, 33));
		ts2.addFix(createFix3(312000, 32, 33));
		ts2.addFix(createFix3(313000, 32, 33));
		ts2.addFix(createFix3(314000, 32, 33));
		final TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals(6, trackLength(), "track starts correctly");
		assertEquals(5, tw3.numFixes(), "track 3 starts correctly");
		assertEquals(2, theLayers.size(), "have right num tracks");

		// do a merge
		final Editable[] subjects = new Editable[] { _tw, ts2 };
		TrackWrapper.mergeTracks(_tw, theLayers, subjects, null);

		// have a look at the results
		assertEquals(6, trackLength(), "track starts correctly");
		assertEquals(5, tw3.numFixes(), "track 3 starts correctly");
		assertEquals(2, theLayers.size(), "have right num tracks");

		// check the error message got thrown
		assertEquals(1, _messages._messages.size(), "have error");
		assertEquals("Merge tracks", _messages._titles.firstElement(), "correct title");
		assertEquals("Sorry, 'Positions' and 'test track' overlap in time. Please correct this and retry",
				_messages._messages.firstElement(),
				"correct title");
		assertEquals(MessageProvider.ERROR, (int) _messages._statuses.firstElement(), "correct title");
	}

	@Test
	public void testTrackMergeAllSegments() {
		final TrackSegment ts1 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts1.addFix(createFix3(110000, 32, 33));
		ts1.addFix(createFix3(111000, 32, 33));
		ts1.addFix(createFix3(112000, 32, 33));
		ts1.addFix(createFix3(113000, 32, 33));
		ts1.addFix(createFix3(114000, 32, 33));

		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts2.addFix(createFix3(210000, 32, 33));
		ts2.addFix(createFix3(211000, 32, 33));
		ts2.addFix(createFix3(212000, 32, 33));
		ts2.addFix(createFix3(213000, 32, 33));
		ts2.addFix(createFix3(214000, 32, 33));

		final TrackSegment ts3 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts3.addFix(createFix3(910000, 32, 33));
		ts3.addFix(createFix3(911000, 32, 33));
		ts3.addFix(createFix3(912000, 32, 33));
		ts3.addFix(createFix3(913000, 32, 33));
		ts3.addFix(createFix3(914000, 32, 33));

		final TrackWrapper tw = new TrackWrapper();
		tw.add(ts1);
		tw.add(ts2);
		tw.add(ts3);

		final Editable[] items = new TrackSegment[3];
		int ctr = 0;
		items[ctr++] = ts1;
		items[ctr++] = ts2;
		items[ctr++] = ts3;

		final TrackWrapper newTrack = new TrackWrapper();
		newTrack.setName("Merged");
		final Layers theLayers = new Layers();

		TrackWrapper.mergeTracks(newTrack, theLayers, items, null);
		// do the merge

		assertEquals(15, newTrack.numFixes(), "has all fixes");

		assertEquals(110000, tw.getStartDTG().getDate().getTime(), "correct start time");
		assertEquals(914000, tw.getEndDTG().getDate().getTime(), "correct end time");

	}

	@Test
	public void testTrackStartEnd() {
		final TrackSegment ts2 = new TrackSegment(TrackSegment.ABSOLUTE);
		ts2.addFix(createFix3(910000, 32, 33));
		ts2.addFix(createFix3(911000, 32, 33));
		ts2.addFix(createFix3(912000, 32, 33));
		ts2.addFix(createFix3(913000, 32, 33));
		ts2.addFix(createFix3(914000, 32, 33));
		final TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals(6, trackLength(), "track starts correctly");
		assertEquals(5, tw3.numFixes(), "track 3 starts correctly");
		assertEquals(2, theLayers.size(), "have right num tracks");

		// do a merge
		final Editable[] subjects = new Editable[] { _tw, ts2 };

		final TrackWrapper result = new TrackWrapper();
		result.setName("Merged");

		TrackWrapper.mergeTracks(result, theLayers, subjects, null);

		// have a look at the results
		assertEquals(11, result.numFixes(), "track 3 is longer");
		assertEquals(3, theLayers.size(), "track got created");
		assertEquals("test track", _fw1.getTrackWrapper().getName(), "fix has new parent");
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#visibleBetween(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */

	@Test
	public void testVisibleBetween() {
		assertEquals(false, _tw.visibleBetween(new HiResDate(700000), new HiResDate(900000)), "is vis");
		assertEquals(true, _tw.visibleBetween(new HiResDate(000000), new HiResDate(300000)), "is vis");
		assertEquals(true, _tw.visibleBetween(new HiResDate(300000), new HiResDate(500000)), "is vis");
	}

	private int trackLength() {
		final Enumeration<Editable> all = _tw.contiguousElements();
		int ctr = 0;
		while (all.hasMoreElements()) {
			ctr++;
			all.nextElement();
		}
		return ctr;
	}

	/**
	 * .
	 *
	 * @throws IOException
	 * @throws UnsupportedFlavorException
	 */

	public void unTestShortenCopiedTMASegment() throws UnsupportedFlavorException, IOException {
		final Layers layers = new Layers();

		// //////////////////////////////////
		// start off building from a track
		// //////////////////////////////////
		final TrackWrapper tw = new TrackWrapper();
		final String trackName = "TRACK";
		tw.setName(trackName);
		layers.addThisLayer(tw);

		final FixWrapper f1 = createFix2(100000, 1, 1, 4, 12);
		final FixWrapper f2 = createFix2(200000, 2, 3, 4, 12);
		tw.addFix(createFix2(80000, 3, 3, 4, 12));
		tw.addFix(f1);
		tw.addFix(f2);
		tw.addFix(createFix2(300000, 3, 3, 4, 12));
		tw.addFix(createFix2(400000, 4, 6, 4, 12));

		final WorldVector offset = new WorldVector(12, 12, 0);
		final WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);
		final double course = 33;

		// ////////////////////////
		// NOW FROM A SENSOR WRAPPER
		// /////////////////////////
		final SensorWrapper sw = new SensorWrapper("some sensor");

		// store the sensor
		tw.add(sw);

		final SensorContactWrapper[] items2 = new SensorContactWrapper[4];
		items2[0] = createSensorItem(tw, sw, 110000);
		items2[1] = createSensorItem(tw, sw, 120000);
		items2[2] = createSensorItem(tw, sw, 130000);
		items2[3] = createSensorItem(tw, sw, 140000);

		System.err.println("gate 1");

		// sort out the host
		for (int i = 0; i < items2.length; i++) {
			final SensorContactWrapper sensorContactWrapper = items2[i];
			sensorContactWrapper.setSensor(sw);
		}

		final RelativeTMASegment seg1 = new RelativeTMASegment(items2, offset, speed, course, layers, Color.yellow);
		seg1.setLayers(layers);

		// check the create worked
		assertEquals(4, seg1.size(), "enough points created");

		seg1.setCourse(35);
		seg1.setSpeed(new WorldSpeed(15, WorldSpeed.Kts));

		// first, try to copy/paste the leg
		final Clipboard clipboard = new Clipboard("Debrief");
		final UndoBuffer buffer = new UndoBuffer();

		// check only two tracks
		assertEquals(1, layers.size(), "one tracks");

		System.err.println("gate 2");

		// duplicate the track
		final CopyItem copier1 = new RightClickCutCopyAdaptor.CopyItem(seg1, clipboard, null, layers, null, buffer);
		copier1.execute();

		final Transferable tr1 = clipboard.getContents(this);
		// see if there is currently a plottable on the clipboard

		// extract the plottable
		final Plottable theData1 = (Plottable) tr1.getTransferData(PlottableSelection.PlottableFlavor);
		final PasteLayer paster1 = new RightClickPasteAdaptor.PasteLayer((Layer) theData1, clipboard, null, layers,
				true);
		paster1.execute();

		// check now two tracks
		assertEquals(2, layers.size(), "two tracks");

		// have a look at the name
		TrackWrapper pastedLeg = null;
		final Enumeration<Editable> iter = layers.elements();
		while (iter.hasMoreElements()) {
			final TrackWrapper next = (TrackWrapper) iter.nextElement();
			if (!next.getName().equals(trackName)) {
				pastedLeg = next;
			}
		}

		System.err.println("gate 3");

		assertNotNull(pastedLeg, "found tma track");

		// ok, create a parent track for the segment
		final TrackWrapper tma = new TrackWrapper();
		tma.setName("TMA");
		tma.add(seg1);

		layers.addThisLayer(tma);

		// duplicate the track
		final CopyItem copier = new RightClickCutCopyAdaptor.CopyItem(tma, clipboard, null, layers, null, buffer);
		copier.execute();

		final Transferable tr = clipboard.getContents(this);
		// see if there is currently a plottable on the clipboard

		// check only two tracks
		assertEquals(3, layers.size(), "three tracks");

		System.err.println("gate 4");

		// extract the plottable
		final Plottable theData = (Plottable) tr.getTransferData(PlottableSelection.PlottableFlavor);
		final PasteLayer paster = new RightClickPasteAdaptor.PasteLayer((Layer) theData, clipboard, null, layers, true);
		paster.execute();

		// check only two tracks
		assertEquals(4, layers.size(), "four tracks");

		// change the start time for the pasted leg
		TrackWrapper pastedTrack = null;
		Enumeration<Editable> lIter = layers.elements();
		while (lIter.hasMoreElements()) {
			final TrackWrapper trk = (TrackWrapper) lIter.nextElement();
			if (trk.getName().equals(TRACK_NAME)) {
				continue;
			}
			if (trk.getName().contains(pastedLeg.getName())) {
				pastedTrack = trk;
				break;
			}
		}
		assertNotNull(pastedTrack, "Found pasted track");

		System.err.println("gate 5");

		// ok, now shorten the TMA leg
		final RelativeTMASegment seg2 = (RelativeTMASegment) pastedTrack.getSegments().elements().nextElement();
		seg2.setDTG_Start(new HiResDate(105000));
		pastedTrack.sortOutRelativePositions();

		// change the start time for the pasted leg
		TrackWrapper pastedTMA = null;
		lIter = layers.elements();
		while (lIter.hasMoreElements()) {
			final TrackWrapper trk = (TrackWrapper) lIter.nextElement();
			if (trk.getName().equals(TRACK_NAME)) {
				continue;
			}
			if (trk.getName().contains("TMA 1")) {
				pastedTMA = trk;
				break;
			}
		}
		assertNotNull(pastedTMA, "Found pasted track");

		// ok, now shorten the TMA leg
		final RelativeTMASegment seg3 = (RelativeTMASegment) pastedTMA.getSegments().elements().nextElement();
		seg3.setDTG_Start(new HiResDate(105000));
		pastedTMA.sortOutRelativePositions();

		// check the segment doesn't have positions

	}

}
