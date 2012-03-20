// Copyright MWC 1999, Debrief 3 Project
// Revision 1.1  1999-01-31 13:33:08+00  sm11td
// Initial revision
//

package Debrief.Wrappers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import Debrief.ReaderWriter.Replay.FormatTracks;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.SplittableLayer;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support;
import Debrief.Wrappers.Track.TrackWrapper_Support.FixSetter;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import Debrief.Wrappers.Track.WormInHoleOffset;
import MWC.Algorithms.Conversions;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.DynamicPlottable;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layer.ProvidesContiguousElements;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Canvas.MockCanvasType;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.HasDraggableComponents;
import MWC.GUI.Shapes.Symbols.Vessels.WorldScaledSym;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

/**
 * the TrackWrapper maintains the GUI and data attributes of the whole track
 * iteself, but the responsibility for the fixes within the track are demoted to
 * the FixWrapper
 */
public final class TrackWrapper extends MWC.GUI.PlainWrapper implements
		WatchableList, DynamicPlottable, MWC.GUI.Layer, DraggableItem,
		HasDraggableComponents, ProvidesContiguousElements {

	// //////////////////////////////////////
	// member variables
	// //////////////////////////////////////

	private static final String SOLUTIONS_LAYER_NAME = "Solutions";

	public static final String SENSORS_LAYER_NAME = "Sensors";

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase {
		protected static class TestMockCanvas extends MockCanvasType {
			public void drawPolyline(int[] points) {
				callCount++;
				pointCount += points.length;
			}
		}

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

		public testMe(final String val) {
			super(val);
		}

		@SuppressWarnings("synthetic-access")
		private int countVisibleFixes(TrackWrapper tw) {
			int ctr = 0;
			final Enumeration<Editable> iter = tw.getPositions();
			while (iter.hasMoreElements()) {
				final Plottable thisE = (Plottable) iter.nextElement();
				if (thisE.getVisible())
					ctr++;
			}
			return ctr;
		}

		@SuppressWarnings("synthetic-access")
		private int countVisibleSensorWrappers(TrackWrapper tw) {
			final Enumeration<Editable> iter2 = tw._mySensors.elements();
			int sCtr = 0;
			while (iter2.hasMoreElements()) {
				final SensorWrapper sw = (SensorWrapper) iter2.nextElement();
				final Enumeration<Editable> enumS = sw.elements();
				while (enumS.hasMoreElements()) {
					final Plottable pl = (Plottable) enumS.nextElement();
					if (pl.getVisible())
						sCtr++;
				}
			}
			return sCtr;
		}

		@SuppressWarnings("synthetic-access")
		private int countVisibleSolutionWrappers(TrackWrapper tw) {
			final Enumeration<Editable> iter2 = tw._mySolutions.elements();
			int sCtr = 0;
			while (iter2.hasMoreElements()) {
				final TMAWrapper sw = (TMAWrapper) iter2.nextElement();
				final Enumeration<Editable> enumS = sw.elements();
				while (enumS.hasMoreElements()) {
					final Plottable pl = (Plottable) enumS.nextElement();
					if (pl.getVisible())
						sCtr++;
				}
			}
			return sCtr;
		}

		private TrackWrapper getDummyTrack() {
			final TrackWrapper tw = new TrackWrapper();

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100,
					10000), loc_1.add(new WorldVector(33, new WorldDistance(
					100, WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200,
					20000), loc_1.add(new WorldVector(33, new WorldDistance(
					200, WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300,
					30000), loc_1.add(new WorldVector(33, new WorldDistance(
					300, WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400,
					40000), loc_1.add(new WorldVector(33, new WorldDistance(
					400, WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500,
					50000), loc_1.add(new WorldVector(33, new WorldDistance(
					500, WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);
			// also give it some sensor data
			final SensorWrapper swa = new SensorWrapper("title one");
			final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa",
					new HiResDate(150, 0), null, null, null, null, null, 0,
					null);
			final SensorContactWrapper scwa2 = new SensorContactWrapper("bbb",
					new HiResDate(180, 0), null, null, null, null, null, 0,
					null);
			final SensorContactWrapper scwa3 = new SensorContactWrapper("ccc",
					new HiResDate(250, 0), null, null, null, null, null, 0,
					null);
			swa.add(scwa1);
			swa.add(scwa2);
			swa.add(scwa3);
			tw.add(swa);
			final SensorWrapper sw = new SensorWrapper("title two");
			final SensorContactWrapper scw1 = new SensorContactWrapper("ddd",
					new HiResDate(260, 0), null, null, null, null, null, 0,
					null);
			final SensorContactWrapper scw2 = new SensorContactWrapper("eee",
					new HiResDate(280, 0), null, null, null, null, null, 0,
					null);
			final SensorContactWrapper scw3 = new SensorContactWrapper("fff",
					new HiResDate(350, 0), null, null, null, null, null, 0,
					null);
			sw.add(scw1);
			sw.add(scw2);
			sw.add(scw3);
			tw.add(sw);

			final TMAWrapper mwa = new TMAWrapper("bb");
			final TMAContactWrapper tcwa1 = new TMAContactWrapper("aaa", "bbb",
					new HiResDate(130), null, 0, 0, 0, null, null, null, null);
			final TMAContactWrapper tcwa2 = new TMAContactWrapper("bbb", "bbb",
					new HiResDate(190), null, 0, 0, 0, null, null, null, null);
			final TMAContactWrapper tcwa3 = new TMAContactWrapper("ccc", "bbb",
					new HiResDate(230), null, 0, 0, 0, null, null, null, null);
			mwa.add(tcwa1);
			mwa.add(tcwa2);
			mwa.add(tcwa3);
			tw.add(mwa);
			final TMAWrapper mw = new TMAWrapper("cc");
			final TMAContactWrapper tcw1 = new TMAContactWrapper("ddd", "bbb",
					new HiResDate(230), null, 0, 0, 0, null, null, null, null);
			final TMAContactWrapper tcw2 = new TMAContactWrapper("eee", "bbb",
					new HiResDate(330), null, 0, 0, 0, null, null, null, null);
			final TMAContactWrapper tcw3 = new TMAContactWrapper("fff", "bbb",
					new HiResDate(390), null, 0, 0, 0, null, null, null, null);
			mw.add(tcw1);
			mw.add(tcw2);
			mw.add(tcw3);
			tw.add(mw);

			return tw;
		}

		@SuppressWarnings("synthetic-access")
		public final void testFilterToTimePeriod() {
			TrackWrapper tw = getDummyTrack();
			HiResDate startH = new HiResDate(150, 0);
			HiResDate endH = new HiResDate(450, 0);
			tw.filterListTo(startH, endH);
			int ctr = countVisibleFixes(tw);
			int sCtr = countVisibleSensorWrappers(tw);
			int tCtr = countVisibleSolutionWrappers(tw);
			assertEquals("contains correct number of entries", 3, ctr);
			assertEquals("contains correct number of sensor entries", 6, sCtr);
			assertEquals("contains correct number of sensor entries", 5, tCtr);

			tw = getDummyTrack();
			startH = new HiResDate(350, 0);
			endH = new HiResDate(550, 0);
			tw.filterListTo(startH, endH);
			ctr = countVisibleFixes(tw);
			sCtr = countVisibleSensorWrappers(tw);
			tCtr = countVisibleSolutionWrappers(tw);
			assertEquals("contains correct number of entries", 2, ctr);
			assertEquals("contains correct number of sensor entries", 1, sCtr);
			assertEquals("contains correct number of sensor entries", 1, tCtr);

			tw = getDummyTrack();
			startH = new HiResDate(0, 0);
			endH = new HiResDate(450, 0);
			tw.filterListTo(startH, endH);
			ctr = countVisibleFixes(tw);
			sCtr = countVisibleSensorWrappers(tw);
			tCtr = countVisibleSolutionWrappers(tw);
			assertEquals("contains correct number of entries", 4, ctr);
			assertEquals("contains correct number of sensor entries", 6, sCtr);
			assertEquals("contains correct number of sensor entries", 6, tCtr);
		}

		public void testGetItemsBetween_Second() {
			final TrackWrapper tw = new TrackWrapper();

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(0, 1),
					loc_1, 0, 0));
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(0, 2),
					loc_1, 0, 0));
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(0, 3),
					loc_1, 0, 0));
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(0, 4),
					loc_1, 0, 0));
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(0, 5),
					loc_1, 0, 0));
			final FixWrapper fw6 = new FixWrapper(new Fix(new HiResDate(0, 6),
					loc_1, 0, 0));
			final FixWrapper fw7 = new FixWrapper(new Fix(new HiResDate(0, 7),
					loc_1, 0, 0));
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

			Collection<Editable> col = tw.getItemsBetween(new HiResDate(0, 3),
					new HiResDate(0, 5));
			assertEquals("found correct number of items", 3, col.size());

			// make the fourth item not visible
			fw4.setVisible(false);

			col = tw.getUnfilteredItems(new HiResDate(0, 3),
					new HiResDate(0, 5));
			assertEquals("found correct number of items", 2, col.size());

			final Watchable[] pts2 = tw.getNearestTo(new HiResDate(0, 3));
			assertEquals("found something", 1, pts2.length);
			assertEquals("found the third item", fw3, pts2[0]);

			final Watchable[] pts = tw.getNearestTo(new HiResDate(0, 1));
			assertEquals("found something", 1, pts.length);
			assertEquals("found the first item", fw1, pts[0]);

		}

		public final void testGettingTimes() {
			// Enumeration<SensorContactWrapper>
			final TrackWrapper tw = new TrackWrapper();

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final WorldLocation loc_2 = new WorldLocation(1, 1, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(
					new HiResDate(0, 100), loc_1, 0, 0));
			final FixWrapper fw2 = new FixWrapper(new Fix(
					new HiResDate(0, 300), loc_2, 0, 0));
			final FixWrapper fw3 = new FixWrapper(new Fix(
					new HiResDate(0, 500), loc_2, 0, 0));
			final FixWrapper fw4 = new FixWrapper(new Fix(
					new HiResDate(0, 700), loc_2, 0, 0));

			// check returning empty data
			Collection<Editable> coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 40));
			assertEquals("Return empty when empty", coll, null);

			tw.addFix(fw1);

			// check returning single field
			coll = tw
					.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0,
					540));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 140));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 100), new HiResDate(0,
					100));
			assertEquals("Return valid point", coll.size(), 1);

			tw.addFix(fw2);

			// check returning with fields
			coll = tw
					.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0,
					540));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 140));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 440));
			assertEquals("Return valid point", coll.size(), 2);

			coll = tw.getItemsBetween(new HiResDate(0, 150), new HiResDate(0,
					440));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0,
					440));
			assertEquals("Return valid point", coll.size(), 1);

			tw.addFix(fw3);

			// check returning with fields
			coll = tw
					.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0,
					540));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 140));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 440));
			assertEquals("Return valid point", coll.size(), 2);

			coll = tw.getItemsBetween(new HiResDate(0, 150), new HiResDate(0,
					440));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0,
					440));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 100), new HiResDate(0,
					300));
			assertEquals("Return valid point", coll.size(), 2);

			coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0,
					500));
			assertEquals("Return valid point", coll.size(), 2);

			tw.addFix(fw4);

		}

		public final void testInterpolation() {
			final TrackWrapper tw = new TrackWrapper();

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100,
					10000), loc_1.add(new WorldVector(33, new WorldDistance(
					100, WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200,
					20000), loc_1.add(new WorldVector(33, new WorldDistance(
					200, WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300,
					30000), loc_1.add(new WorldVector(33, new WorldDistance(
					300, WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400,
					40000), loc_1.add(new WorldVector(33, new WorldDistance(
					400, WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500,
					50000), loc_1.add(new WorldVector(33, new WorldDistance(
					500, WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			tw.addFix(fw1);
			tw.addFix(fw2);
			// tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);

			// check that we're not interpolating
			assertFalse("interpolating switched off by default",
					tw.getInterpolatePoints());

			// ok, get on with it.
			Watchable[] list = tw.getNearestTo(new HiResDate(200, 20000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);
			assertEquals("right answer", list[0], fw2);

			// and the end
			list = tw.getNearestTo(new HiResDate(500, 50000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);
			assertEquals("right answer", list[0], fw5);

			// and now an in-between point
			// ok, get on with it.
			list = tw.getNearestTo(new HiResDate(230, 23000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);
			assertEquals("right answer", list[0], fw4);

			// ok, with interpolation on
			tw.setInterpolatePoints(true);

			assertTrue("interpolating now switched on",
					tw.getInterpolatePoints());

			// ok, get on with it.
			list = tw.getNearestTo(new HiResDate(200, 20000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);
			assertEquals("right answer", list[0], fw2);

			// and the end
			list = tw.getNearestTo(new HiResDate(500, 50000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);
			assertEquals("right answer", list[0], fw5);

			// hey

			// and now an in-between point
			// ok, get on with it.
			list = tw.getNearestTo(new HiResDate(300, 30000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);

			// have a look at them
			final FixWrapper res = (FixWrapper) list[0];
			final WorldVector rangeError = res.getFixLocation().subtract(
					fw3.getFixLocation());
			assertEquals("right answer", 0,
					Conversions.Degs2m(rangeError.getRange()), 0.0001);
			// assertEquals("right speed", res.getSpeed(), fw3.getSpeed(), 0);
			// assertEquals("right course", res.getCourse(), fw3.getCourse(),
			// 0);

		}

		public final void testMyParams() {
			TrackWrapper ed = new TrackWrapper();
			ed.setName("blank");

			editableTesterSupport.testParams(ed, this);
			ed = null;
		}

		public void testPaintingColChange() {
			final TrackWrapper tw = new TrackWrapper();
			tw.setColor(Color.RED);
			tw.setName("test track");

			/**
			 * intention of this test: line is broken into three segments (red,
			 * yellow, green). - first of 2 points, next of 2 points, last of 3
			 * points (14 values)
			 */

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100,
					10000), loc_1.add(new WorldVector(33, new WorldDistance(
					100, WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			fw1.setColor(Color.red);
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200,
					20000), loc_1.add(new WorldVector(33, new WorldDistance(
					200, WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			fw2.setColor(Color.yellow);
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300,
					30000), loc_1.add(new WorldVector(33, new WorldDistance(
					300, WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			fw3.setColor(Color.green);
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400,
					40000), loc_1.add(new WorldVector(33, new WorldDistance(
					400, WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			fw4.setColor(Color.green);
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500,
					50000), loc_1.add(new WorldVector(33, new WorldDistance(
					500, WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			fw5.setColor(Color.green);
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);

			callCount = 0;
			pointCount = 0;

			assertNull("our array of points starts empty", tw._myPts);
			assertEquals("our point array counter is zero", tw._ptCtr, 0);

			final CanvasType dummyDest = new TestMockCanvas();

			tw.paint(dummyDest);

			assertEquals("our array has correct number of points", 10,
					tw._myPts.length);
			assertEquals("the pointer counter has been reset", 0, tw._ptCtr);

			// check it got called the correct number of times
			assertEquals("We didnt paint enough polygons", 3, callCount);
			assertEquals("We didnt paint enough polygons points", 14,
					pointCount);
		}

		public void testPaintingLineJoinedChange() {
			final TrackWrapper tw = new TrackWrapper();
			tw.setColor(Color.RED);
			tw.setName("test track");

			/**
			 * intention of this test: line is broken into two segments - one of
			 * two points, the next of three, thus two polygons should be drawn
			 * - 10 points total (4 then 6).
			 */

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100,
					10000), loc_1.add(new WorldVector(33, new WorldDistance(
					100, WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			fw1.setColor(Color.red);
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200,
					20000), loc_1.add(new WorldVector(33, new WorldDistance(
					200, WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			fw2.setColor(Color.red);
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300,
					30000), loc_1.add(new WorldVector(33, new WorldDistance(
					300, WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			fw3.setColor(Color.red);
			fw3.setLineShowing(false);
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400,
					40000), loc_1.add(new WorldVector(33, new WorldDistance(
					400, WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			fw4.setColor(Color.red);
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500,
					50000), loc_1.add(new WorldVector(33, new WorldDistance(
					500, WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			fw5.setColor(Color.red);
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);

			callCount = 0;
			pointCount = 0;

			assertNull("our array of points starts empty", tw._myPts);
			assertEquals("our point array counter is zero", tw._ptCtr, 0);

			final CanvasType dummyDest = new TestMockCanvas();

			tw.paint(dummyDest);

			assertEquals("our array has correct number of points", 10,
					tw._myPts.length);
			assertEquals("the pointer counter has been reset", 0, tw._ptCtr);

			// check it got called the correct number of times
			assertEquals("We didnt paint enough polygons", 2, callCount);
			assertEquals("We didnt paint enough polygons points", 10,
					pointCount);

		}

		public void testPaintingVisChange() {
			final TrackWrapper tw = new TrackWrapper();
			tw.setColor(Color.RED);
			tw.setName("test track");

			/**
			 * intention of this test: line is broken into two segments of two
			 * points, thus two polygons should be drawn, each with 4 points - 8
			 * points total.
			 */

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100,
					10000), loc_1.add(new WorldVector(33, new WorldDistance(
					100, WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			fw1.setColor(Color.red);
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200,
					20000), loc_1.add(new WorldVector(33, new WorldDistance(
					200, WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			fw2.setColor(Color.red);
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300,
					30000), loc_1.add(new WorldVector(33, new WorldDistance(
					300, WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			fw3.setColor(Color.red);
			fw3.setVisible(false);
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400,
					40000), loc_1.add(new WorldVector(33, new WorldDistance(
					400, WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			fw4.setColor(Color.red);
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500,
					50000), loc_1.add(new WorldVector(33, new WorldDistance(
					500, WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			fw5.setColor(Color.red);
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);

			callCount = 0;
			pointCount = 0;

			assertNull("our array of points starts empty", tw._myPts);
			assertEquals("our point array counter is zero", tw._ptCtr, 0);

			final CanvasType dummyDest = new TestMockCanvas();

			tw.paint(dummyDest);

			assertEquals("our array has correct number of points", 10,
					tw._myPts.length);
			assertEquals("the pointer counter has been reset", 0, tw._ptCtr);

			// check it got called the correct number of times
			assertEquals("We didnt paint enough polygons", 2, callCount);
			assertEquals("We didnt paint enough polygons points", 8, pointCount);

		}
	}

	/**
	 * class containing editable details of a track
	 */
	public final class trackInfo extends Editable.EditorType implements
			Editable.DynamicDescriptors {

		/**
		 * constructor for this editor, takes the actual track as a parameter
		 * 
		 * @param data
		 *            track being edited
		 */
		public trackInfo(final TrackWrapper data) {
			super(data, data.getName(), "");
		}

		public final MethodDescriptor[] getMethodDescriptors() {
			// just add the reset color field first
			final Class<TrackWrapper> c = TrackWrapper.class;

			final MethodDescriptor[] mds = {
					method(c, "exportThis", null, "Export Shape"),
					method(c, "resetLabels", null, "Reset DTG Labels") };

			return mds;
		}

		public final String getName() {
			return super.getName();
		}

		public final PropertyDescriptor[] getPropertyDescriptors() {
			try {
				PropertyDescriptor[] res = {
						expertProp("SymbolType",
								"the type of symbol plotted for this label",
								FORMAT),
						expertProp("LineThickness",
								"the width to draw this track", FORMAT),
						expertProp("Name", "the track name"),
						expertProp(
								"InterpolatePoints",
								"whether to interpolate points between known data points",
								SPATIAL),
						expertProp("Color", "the track color", FORMAT),
						expertProp("SymbolColor",
								"the color of the symbol (when used)", FORMAT),
						expertProp(
								"PlotArrayCentre",
								"highlight the sensor array centre when non-zero array length provided",
								FORMAT),
						expertProp("TrackFont", "the track label font", FORMAT),
						expertProp("NameVisible", "show the track label",
								VISIBILITY),
						expertProp("PositionsVisible",
								"show individual Positions", VISIBILITY),
						expertProp(
								"NameAtStart",
								"whether to show the track name at the start (or end)",
								VISIBILITY),
						expertProp("LinkPositions",
								"whether to join the track points", FORMAT),
						expertProp("Visible", "whether the track is visible",
								VISIBILITY),
						expertLongProp("NameLocation",
								"relative location of track label",
								MWC.GUI.Properties.LocationPropertyEditor.class),
						expertLongProp(
								"LabelFrequency",
								"the label frequency",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						expertLongProp(
								"SymbolFrequency",
								"the symbol frequency",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						expertLongProp(
								"ResampleDataAt",
								"the data sample rate",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						expertLongProp(
								"ArrowFrequency",
								"the direction marker frequency",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						expertLongProp(
								"LineStyle",
								"the line style used to join track points",
								MWC.GUI.Properties.LineStylePropertyEditor.class),

				};
				res[0].setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor.class);
				res[1].setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);

				// SPECIAL CASE: if we have a world scaled symbol, provide
				// editors for
				// the symbol size
				TrackWrapper item = (TrackWrapper) this.getData();
				if (item._theSnailShape instanceof WorldScaledSym) {
					// yes = better create height/width editors
					PropertyDescriptor[] res2 = new PropertyDescriptor[res.length + 2];
					System.arraycopy(res, 0, res2, 2, res.length);
					res2[0] = expertProp("SymbolLength", "Length of symbol",
							FORMAT);
					res2[1] = expertProp("SymbolWidth", "Width of symbol",
							FORMAT);

					// and now use the new value
					res = res2;
				}
				return res;
			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	/**
	 * keep track of versions - version id
	 */
	static final long serialVersionUID = 1;

	/**
	 * put the other objects into this one as children
	 * 
	 * @param wrapper
	 *            whose going to receive it
	 * @param theLayers
	 *            the top level layers object
	 * @param parents
	 *            the track wrapppers containing the children
	 * @param subjects
	 *            the items to insert.
	 */
	public static void groupTracks(TrackWrapper target, Layers theLayers,
			Layer[] parents, Editable[] subjects) {
		// ok, loop through the subjects, adding them onto the target
		for (int i = 0; i < subjects.length; i++) {
			final Layer thisL = (Layer) subjects[i];
			final TrackWrapper thisP = (TrackWrapper) parents[i];
			if (thisL != target) {
				// is it a plain segment?
				if (thisL instanceof TrackWrapper) {
					// pass down through the positions/segments
					final Enumeration<Editable> pts = thisL.elements();

					while (pts.hasMoreElements()) {
						final Editable obj = pts.nextElement();

						if (obj instanceof SegmentList) {
							final SegmentList sl = (SegmentList) obj;

							final Enumeration<Editable> segs = sl.elements();
							while (segs.hasMoreElements()) {
								final TrackSegment ts = (TrackSegment) segs
										.nextElement();

								// reset the name if we need to
								if (ts.getName().startsWith("Posi"))
									ts.setName(FormatRNDateTime.toString(ts
											.startDTG().getDate().getTime()));

								target.add(ts);
							}
						} else {
							if (obj instanceof TrackSegment) {
								TrackSegment ts = (TrackSegment) obj;

								// reset the name if we need to
								if (ts.getName().startsWith("Posi"))
									ts.setName(FormatRNDateTime.toString(ts
											.startDTG().getDate().getTime()));

								// and remember it
								target.add(ts);
							}
						}
					}
				} else {
					// get it's data, and add it to the target
					target.add(thisL);
				}

				// and remove the layer from it's parent
				if (thisL instanceof TrackSegment) {
					thisP.removeElement(thisL);

					// does this just leave an empty husk?
					if (thisP.numFixes() == 0) {
						// may as well ditch it anyway
						theLayers.removeThisLayer(thisP);
					}
				} else {
					// we'll just remove it from the top level layer
					theLayers.removeThisLayer(thisL);
				}
			}
		}
	}

	/**
	 * perform a merge of the supplied tracks.
	 * 
	 * @param target
	 *            the final recipient of the other items
	 * @param theLayers
	 * @param parents
	 *            the parent tracks for the supplied items
	 * @param subjects
	 *            the actual selected items
	 * @return sufficient information to undo the merge
	 */
	public static int mergeTracks(final Editable target, Layers theLayers,
			final Layer[] parents, final Editable[] subjects) {
		// where we dump the new data points
		Layer receiver = (Layer) target;

		// first, check they don't overlap.
		// start off by collecting the periods
		TimePeriod[] _periods = new TimePeriod.BaseTimePeriod[subjects.length];
		for (int i = 0; i < subjects.length; i++) {
			Editable editable = subjects[i];
			TimePeriod thisPeriod = null;
			if (editable instanceof TrackWrapper) {
				TrackWrapper tw = (TrackWrapper) editable;
				thisPeriod = new TimePeriod.BaseTimePeriod(tw.getStartDTG(),
						tw.getEndDTG());
			} else if (editable instanceof TrackSegment) {
				TrackSegment ts = (TrackSegment) editable;
				thisPeriod = new TimePeriod.BaseTimePeriod(ts.startDTG(),
						ts.endDTG());
			}
			_periods[i] = thisPeriod;
		}
		// now test them.
		String failedMsg = null;
		for (int i = 0; i < _periods.length; i++) {
			TimePeriod timePeriod = _periods[i];
			for (int j = 0; j < _periods.length; j++) {
				TimePeriod timePeriod2 = _periods[j];
				// check it's not us
				if (timePeriod2 != timePeriod) {
					if (timePeriod.overlaps(timePeriod2)) {
						failedMsg = "'" + subjects[i].getName() + "' and '"
								+ subjects[j].getName() + "'";
						break;
					}
				}

			}

		}

		// how did we get on?
		if (failedMsg != null) {
			MessageProvider.Base.Provider.show("Merge tracks", "Sorry, "
					+ failedMsg
					+ " overlap in time. Please correct this and retry",
					MessageProvider.ERROR);
			return MessageProvider.ERROR;
		}

		// right, if the target is a TMA track, we have to change it into a
		// proper
		// track, since
		// the merged tracks probably contain manoeuvres
		if (target instanceof CoreTMASegment) {
			CoreTMASegment tma = (CoreTMASegment) target;
			TrackSegment newSegment = new TrackSegment(tma);

			// now do some fancy footwork to remove the target from the wrapper,
			// and
			// replace it with our new segment
			newSegment.getWrapper().removeElement(target);
			newSegment.getWrapper().add(newSegment);

			// store the new segment into the receiver
			receiver = newSegment;
		}

		// ok, loop through the subjects, adding them onto the target
		for (int i = 0; i < subjects.length; i++) {
			final Layer thisL = (Layer) subjects[i];
			final TrackWrapper thisP = (TrackWrapper) parents[i];
			// is this the target item (note we're comparing against the item
			// passed
			// in, not our
			// temporary receiver, since the receiver may now be a tracksegment,
			// not a
			// TMA segment
			if (thisL != target) {
				// is it a plain segment?
				if (thisL instanceof TrackWrapper) {
					// pass down through the positions/segments
					final Enumeration<Editable> pts = thisL.elements();

					while (pts.hasMoreElements()) {
						final Editable obj = pts.nextElement();
						if (obj instanceof SegmentList) {
							final SegmentList sl = (SegmentList) obj;
							final Enumeration<Editable> segs = sl.elements();
							while (segs.hasMoreElements()) {
								final TrackSegment ts = (TrackSegment) segs
										.nextElement();
								receiver.add(ts);
							}
						} else {
							final Layer ts = (Layer) obj;
							receiver.append(ts);
						}
					}
				} else {
					// get it's data, and add it to the target
					receiver.append(thisL);
				}

				// and remove the layer from it's parent
				if (thisL instanceof TrackSegment) {
					thisP.removeElement(thisL);

					// does this just leave an empty husk?
					if (thisP.numFixes() == 0) {
						// may as well ditch it anyway
						theLayers.removeThisLayer(thisP);
					}

				} else {
					// we'll just remove it from the top level layer
					theLayers.removeThisLayer(thisL);
				}
			}

		}

		return MessageProvider.OK;
	}

	/**
	 * whether to interpolate points in this track
	 */
	private boolean _interpolatePoints = false;

	/**
	 * the end of the track to plot the label
	 */
	private boolean _LabelAtStart = true;

	private HiResDate _lastLabelFrequency = new HiResDate(0,
			TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY);

	private HiResDate _lastSymbolFrequency = new HiResDate(0,
			TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY);

	private HiResDate _lastArrowFrequency = new HiResDate(0,
			TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY);

	private HiResDate _lastDataFrequency = new HiResDate(0,
			TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY);

	/**
	 * the width of this track
	 */
	private int _lineWidth = 2;

	/**
	 * the style of this line
	 * 
	 */
	private int _lineStyle = CanvasType.SOLID;

	/**
	 * whether or not to link the Positions
	 */
	private boolean _linkPositions;

	/**
	 * whether to show a highlight for the array centre
	 * 
	 */
	private boolean _plotArrayCentre;

	/**
	 * our editable details
	 */
	transient private Editable.EditorType _myEditor = null;

	/**
	 * keep a list of points waiting to be plotted
	 * 
	 */
	transient int[] _myPts;

	/**
	 * the sensor tracks for this vessel
	 */
	final private BaseLayer _mySensors;

	/**
	 * the TMA solutions for this vessel
	 */
	final private BaseLayer _mySolutions;

	/**
	 * keep track of how far we are through our array of points
	 * 
	 */
	transient int _ptCtr = 0;

	/**
	 * whether or not to show the Positions
	 */
	private boolean _showPositions;

	/**
	 * the label describing this track
	 */
	private final MWC.GUI.Shapes.TextLabel _theLabel;

	/**
	 * the list of wrappers we hold
	 */
	private SegmentList _thePositions;

	/**
	 * the symbol to pass on to a snail plotter
	 */
	private MWC.GUI.Shapes.Symbols.PlainSymbol _theSnailShape;

	/**
	 * working ZERO location value, to reduce number of working values
	 */
	final private WorldLocation _zeroLocation = new WorldLocation(0, 0, 0);

	// //////////////////////////////////////
	// member functions
	// //////////////////////////////////////

	transient private FixWrapper finisher;

	transient private HiResDate lastDTG;

	transient private FixWrapper lastFix;

	// for getNearestTo
	transient private FixWrapper nearestFix;

	/**
	 * working parameters
	 */
	// for getFixesBetween
	transient private FixWrapper starter;

	transient private TimePeriod _myTimePeriod;

	transient private WorldArea _myWorldArea;

	transient private final PropertyChangeListener _locationListener;

	// //////////////////////////////////////
	// constructors
	// //////////////////////////////////////
	/**
	 * Wrapper for a Track (a series of position fixes). It combines the data
	 * with the formatting details
	 */
	public TrackWrapper() {
		_mySensors = new SplittableLayer(true);
		_mySensors.setName(SENSORS_LAYER_NAME);
		_mySolutions = new BaseLayer(true);
		_mySolutions.setName(SOLUTIONS_LAYER_NAME);

		// create a property listener for when fixes are moved
		_locationListener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent arg0) {
				fixMoved();
			}
		};

		// declare our arrays
		_thePositions = new TrackWrapper_Support.SegmentList();
		_thePositions.setWrapper(this);

		_linkPositions = true;

		// start off with positions showing (although the default setting for a
		// fix
		// is to not show a symbol anyway). We need to make this "true" so that
		// when a fix position is set to visible it is not over-ridden by this
		// setting
		_showPositions = true;

		_theLabel = new MWC.GUI.Shapes.TextLabel(new WorldLocation(0, 0, 0),
				null);
		// set an initial location for the label
		_theLabel.setRelativeLocation(new Integer(
				MWC.GUI.Properties.LocationPropertyEditor.RIGHT));

		// initialise the symbol to use for plotting this track in snail mode
		_theSnailShape = MWC.GUI.Shapes.Symbols.SymbolFactory
				.createSymbol("Submarine");
	}

	/**
	 * add the indicated point to the track
	 * 
	 * @param point
	 *            the point to add
	 */
	public final void add(final MWC.GUI.Editable point) {
		boolean done = false;
		// see what type of object this is
		if (point instanceof FixWrapper) {
			final FixWrapper fw = (FixWrapper) point;
			fw.setTrackWrapper(this);
			addFix(fw);
			done = true;
		}
		// is this a sensor?
		else if (point instanceof SensorWrapper) {
			final SensorWrapper swr = (SensorWrapper) point;

			// add to our list
			_mySensors.add(swr);

			// tell the sensor about us
			swr.setHost(this);

			// and the track name (if we're loading from REP it will already
			// know
			// the name, but if this data is being pasted in, it may start with
			// a different
			// parent track name - so override it here)
			swr.setTrackName(this.getName());

			// indicate success
			done = true;

		}
		// is this a TMA solution track?
		else if (point instanceof TMAWrapper) {
			final TMAWrapper twr = (TMAWrapper) point;
			// add to our list
			_mySolutions.add(twr);

			// tell the sensor about us
			twr.setHost(this);

			// and the track name (if we're loading from REP it will already
			// know
			// the name, but if this data is being pasted in, it may start with
			// a different
			// parent track name - so override it here)
			twr.setTrackName(this.getName());

			// indicate success
			done = true;

		} else if (point instanceof TrackSegment) {
			TrackSegment seg = (TrackSegment) point;
			seg.setWrapper(this);
			_thePositions.addSegment((TrackSegment) point);
			done = true;

			// hey, sort out the positions
			sortOutRelativePositions();
		}

		if (!done) {
			MWC.GUI.Dialogs.DialogFactory.showMessage("Add point",
					"Sorry it is not possible to add:" + point.getName()
							+ " to " + this.getName());
		}
	}

	/**
	 * add the fix wrapper to the track
	 * 
	 * @param theFix
	 *            the Fix to be added
	 */
	public final void addFix(final FixWrapper theFix) {
		// do we have any track segments
		if (_thePositions.size() == 0) {
			// nope, add one
			final TrackSegment firstSegment = new TrackSegment();
			firstSegment.setName("Positions");
			_thePositions.addSegment(firstSegment);
		}

		// add fix to last track segment
		final TrackSegment last = (TrackSegment) _thePositions.last();
		last.addFix(theFix);

		// tell the fix about it's daddy
		theFix.setTrackWrapper(this);

		// and extend the start/end DTGs
		if (_myTimePeriod == null)
			_myTimePeriod = new TimePeriod.BaseTimePeriod(
					theFix.getDateTimeGroup(), theFix.getDateTimeGroup());
		else
			_myTimePeriod.extend(theFix.getDateTimeGroup());

		if (_myWorldArea == null)
			_myWorldArea = new WorldArea(theFix.getLocation(),
					theFix.getLocation());
		else
			_myWorldArea.extend(theFix.getLocation());

		// we want to listen out for the fix being moved. better listen in to it
		//
		// theFix.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED,
		// _locationListener);
	}

	/**
	 * append this other layer to ourselves (although we don't really bother
	 * with it)
	 * 
	 * @param other
	 *            the layer to add to ourselves
	 */
	public final void append(final Layer other) {
		// is it a track?
		if ((other instanceof TrackWrapper) || (other instanceof TrackSegment)) {
			// yes, break it down.
			final java.util.Enumeration<Editable> iter = other.elements();
			while (iter.hasMoreElements()) {
				final Editable nextItem = iter.nextElement();
				if (nextItem instanceof Layer)
					append((Layer) nextItem);
				else
					add(nextItem);
			}
		} else {
			// nope, just add it to us.
			add(other);
		}
	}

	/**
	 * instruct this object to clear itself out, ready for ditching
	 */
	public final void closeMe() {
		// do the parent
		super.closeMe();

		// and my objects
		// first ask them to close themselves
		final Enumeration<Editable> it = getPositions();
		while (it.hasMoreElements()) {
			final Object val = it.nextElement();
			if (val instanceof PlainWrapper) {
				final PlainWrapper pw = (PlainWrapper) val;
				pw.closeMe();
			}
		}

		// now ditch them
		_thePositions.removeAllElements();
		_thePositions = null;

		// and my objects
		// first ask the sensors to close themselves
		if (_mySensors != null) {
			final Enumeration<Editable> it2 = _mySensors.elements();
			while (it2.hasMoreElements()) {
				final Object val = it2.nextElement();
				if (val instanceof PlainWrapper) {
					final PlainWrapper pw = (PlainWrapper) val;
					pw.closeMe();
				}
			}
			// now ditch them
			_mySensors.removeAllElements();
		}

		// now ask the solutions to close themselves
		if (_mySolutions != null) {
			final Enumeration<Editable> it2 = _mySolutions.elements();
			while (it2.hasMoreElements()) {
				final Object val = it2.nextElement();
				if (val instanceof PlainWrapper) {
					final PlainWrapper pw = (PlainWrapper) val;
					pw.closeMe();
				}
			}
			// now ditch them
			_mySolutions.removeAllElements();
		}

		// and our utility objects
		finisher = null;
		lastFix = null;
		nearestFix = null;
		starter = null;

		// and our editor
		_myEditor = null;
	}

	/**
	 * switch the two track sections into one track section
	 * 
	 * @param res
	 *            the previously split track sections
	 */
	public void combineSections(Vector<TrackSegment> res) {
		// ok, remember the first
		final TrackSegment keeper = res.firstElement();

		// now remove them all, adding them to the first
		final Iterator<TrackSegment> iter = res.iterator();
		while (iter.hasNext()) {
			final TrackSegment pl = iter.next();
			if (pl != keeper) {
				keeper.append((Layer) pl);
			}

			_thePositions.removeElement(pl);
		}

		// and put the keepers back in
		_thePositions.addSegment(keeper);
	}

	/**
	 * return our tiered data as a single series of elements
	 * 
	 * @return
	 */
	public final Enumeration<Editable> contiguousElements() {
		final Vector<Editable> res = new Vector<Editable>(0, 1);

		if (_mySensors != null) {
			final Enumeration<Editable> iter = _mySensors.elements();
			while (iter.hasMoreElements()) {
				final SensorWrapper sw = (SensorWrapper) iter.nextElement();
				// is it visible?
				if (sw.getVisible()) {
					res.addAll(sw._myContacts);
				}
			}
		}

		if (_mySolutions != null) {
			final Enumeration<Editable> iter = _mySolutions.elements();
			while (iter.hasMoreElements()) {
				final TMAWrapper sw = (TMAWrapper) iter.nextElement();
				if (sw.getVisible()) {
					res.addAll(sw._myContacts);
				}
			}
		}

		if (_thePositions != null) {
			res.addAll(getRawPositions());
		}

		return res.elements();
	}

	/**
	 * get an enumeration of the points in this track
	 * 
	 * @return the points in this track
	 */
	public final Enumeration<Editable> elements() {
		final TreeSet<Editable> res = new TreeSet<Editable>();

		if (_mySensors.size() > 0) {
			res.add(_mySensors);
		}

		if (_mySolutions.size() > 0) {
			res.add(_mySolutions);
		}

		// ok, we want to wrap our fast-data as a set of plottables
		// see how many track segments we have
		if (_thePositions.size() == 1) {
			// just the one, insert it
			res.add(_thePositions.first());
		} else {
			// more than one, insert them as a tree
			res.add(_thePositions);
		}

		return new TrackWrapper_Support.IteratorWrapper(res.iterator());
	}

	/**
	 * export this track to REPLAY file
	 */
	public final void exportShape() {
		// call the method in PlainWrapper
		this.exportThis();
	}

	/**
	 * filter the list to the specified time period, then inform any listeners
	 * (such as the time stepper)
	 * 
	 * @param start
	 *            the start dtg of the period
	 * @param end
	 *            the end dtg of the period
	 */
	public final void filterListTo(final HiResDate start, final HiResDate end) {
		final Enumeration<Editable> fixWrappers = getPositions();
		while (fixWrappers.hasMoreElements()) {
			final FixWrapper fw = (FixWrapper) fixWrappers.nextElement();
			final HiResDate dtg = fw.getTime();
			if ((dtg.greaterThanOrEqualTo(start))
					&& (dtg.lessThanOrEqualTo(end))) {
				fw.setVisible(true);
			} else {
				fw.setVisible(false);
			}
		}

		// now do the same for our sensor data
		if (_mySensors != null) {
			final Enumeration<Editable> iter = _mySensors.elements();
			while (iter.hasMoreElements()) {
				final WatchableList sw = (WatchableList) iter.nextElement();
				sw.filterListTo(start, end);
			} // through the sensors
		} // whether we have any sensors

		// and our solution data
		if (_mySolutions != null) {
			final Enumeration<Editable> iter = _mySolutions.elements();
			while (iter.hasMoreElements()) {
				final WatchableList sw = (WatchableList) iter.nextElement();
				sw.filterListTo(start, end);
			} // through the sensors
		} // whether we have any sensors

		// do we have any property listeners?
		if (getSupport() != null) {
			final Debrief.GUI.Tote.StepControl.somePeriod newPeriod = new Debrief.GUI.Tote.StepControl.somePeriod(
					start, end);
			getSupport().firePropertyChange(WatchableList.FILTERED_PROPERTY,
					null, newPeriod);
		}
	}

	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			ComponentConstruct currentNearest, Layer parentLayer) {
		// initialise thisDist, since we're going to be over-writing it
		WorldDistance thisDist = new WorldDistance(0, WorldDistance.DEGS);

		// cycle through the fixes
		final Enumeration<Editable> fixes = getPositions();
		while (fixes.hasMoreElements()) {
			final FixWrapper thisF = (FixWrapper) fixes.nextElement();

			// only check it if it's visible
			if (thisF.getVisible()) {

				// how far away is it?
				thisDist = thisF.getLocation().rangeFrom(cursorLoc, thisDist);

				final WorldLocation fixLocation = new WorldLocation(
						thisF.getLocation()) {
					private static final long serialVersionUID = 1L;

					public void addToMe(WorldVector delta) {
						super.addToMe(delta);
						thisF.setFixLocation(this);
					}
				};

				// try range
				currentNearest.checkMe(this, thisDist, null, parentLayer,
						fixLocation);
			}
		}

	}

	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			LocationConstruct currentNearest, Layer parentLayer, Layers theData) {
		// initialise thisDist, since we're going to be over-writing it
		WorldDistance thisDist = new WorldDistance(0, WorldDistance.DEGS);

		// cycle through the fixes
		final Enumeration<Editable> fixes = getPositions();
		while (fixes.hasMoreElements()) {
			final FixWrapper thisF = (FixWrapper) fixes.nextElement();

			if (thisF.getVisible()) {
				// how far away is it?
				thisDist = thisF.getLocation().rangeFrom(cursorLoc, thisDist);

				// is it closer?
				currentNearest.checkMe(this, thisDist, null, parentLayer);
			}
		}
	}

	public void findNearestSegmentHotspotFor(WorldLocation cursorLoc,
			Point cursorPt, LocationConstruct currentNearest) {
		// initialise thisDist, since we're going to be over-writing it
		WorldDistance thisDist;

		// cycle through the track segments
		final Collection<Editable> segments = _thePositions.getData();
		for (final Iterator<Editable> iterator = segments.iterator(); iterator
				.hasNext();) {
			final TrackSegment thisSeg = (TrackSegment) iterator.next();
			if (thisSeg.getVisible()) {
				// how far away is it?

				thisDist = new WorldDistance(thisSeg.rangeFrom(cursorLoc),
						WorldDistance.DEGS);

				// is it closer?
				currentNearest.checkMe(thisSeg, thisDist, null, this);
			}

		}
	}

	/**
	 * one of our fixes has moved. better tell any bits that rely on the
	 * locations of our bits
	 * 
	 * @param theFix
	 *            the fix that moved
	 */
	public void fixMoved() {
		if (_mySensors != null) {
			final Enumeration<Editable> iter = _mySensors.elements();
			while (iter.hasMoreElements()) {
				final SensorWrapper nextS = (SensorWrapper) iter.nextElement();
				nextS.setHost(this);
			}
		}
	}

	/**
	 * what geographic area is covered by this track?
	 * 
	 * @return get the outer bounds of the area
	 */
	public final WorldArea getBounds() {
		// we no longer just return the bounds of the track, because a portion
		// of the track may have been made invisible.

		// instead, we will pass through the full dataset and find the outer
		// bounds
		// of the visible area
		WorldArea res = null;

		if (!getVisible()) {
			// hey, we're invisible, return null
		} else {
			final Enumeration<Editable> it = getPositions();
			while (it.hasMoreElements()) {
				final FixWrapper fw = (FixWrapper) it.nextElement();

				// is this point visible?
				if (fw.getVisible()) {

					// has our data been initialised?
					if (res == null) {
						// no, initialise it
						res = new WorldArea(fw.getLocation(), fw.getLocation());
					} else {
						// yes, extend to include the new area
						res.extend(fw.getLocation());
					}
				}
			}

			// also extend to include our sensor data
			if (_mySensors != null) {
				final Enumeration<Editable> iter = _mySensors.elements();
				while (iter.hasMoreElements()) {
					final PlainWrapper sw = (PlainWrapper) iter.nextElement();
					final WorldArea theseBounds = sw.getBounds();
					if (theseBounds != null) {
						if (res == null)
							res = new WorldArea(theseBounds);
						else
							res.extend(sw.getBounds());
					}
				} // step through the sensors
			} // whether we have any sensors

			// and our solution data
			if (_mySolutions != null) {
				final Enumeration<Editable> iter = _mySolutions.elements();
				while (iter.hasMoreElements()) {
					final PlainWrapper sw = (PlainWrapper) iter.nextElement();
					final WorldArea theseBounds = sw.getBounds();
					if (theseBounds != null) {
						if (res == null)
							res = new WorldArea(theseBounds);
						else
							res.extend(sw.getBounds());
					}
				} // step through the sensors
			} // whether we have any sensors

		} // whether we're visible

		return res;
	}

	/**
	 * the time of the last fix
	 * 
	 * @return the DTG
	 */
	public final HiResDate getEndDTG() {
		TimePeriod res = getTimePeriod();

		return res.getEndDTG();
	}

	// //////////////////////////////////////
	// editing parameters
	// //////////////////////////////////////

	/**
	 * the editable details for this track
	 * 
	 * @return the details
	 */
	public final Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new trackInfo(this);

		return _myEditor;
	}

	/**
	 * create a new, interpolated point between the two supplied
	 * 
	 * @param previous
	 *            the previous point
	 * @param next
	 *            the next point
	 * @return and interpolated point
	 */
	private final FixWrapper getInterpolatedFix(final FixWrapper previous,
			final FixWrapper next, HiResDate requestedDTG) {
		FixWrapper res = null;

		// do we have a start point
		if (previous == null)
			res = next;

		// hmm, or do we have an end point?
		if (next == null)
			res = previous;

		// did we find it?
		if (res == null) {
			res = FixWrapper.interpolateFix(previous, next, requestedDTG);
		}

		return res;
	}

	public final boolean getInterpolatePoints() {
		return _interpolatePoints;
	}

	/**
	 * get the set of fixes contained within this time period (inclusive of both
	 * end values)
	 * 
	 * @param start
	 *            start DTG
	 * @param end
	 *            end DTG
	 * @return series of fixes
	 */
	public final Collection<Editable> getItemsBetween(final HiResDate start,
			final HiResDate end) {
		//
		SortedSet<Editable> set = null;

		// does our track contain any data at all
		if (_thePositions.size() > 0) {

			// see if we have _any_ points in range
			if ((getStartDTG().greaterThan(end))
					|| (getEndDTG().lessThan(start))) {
				// don't bother with it.
			} else {

				// SPECIAL CASE! If we've been asked to show interpolated data
				// points,
				// then
				// we should produce a series of items between the indicated
				// times. How
				// about 1 minute resolution?
				if (getInterpolatePoints()) {
					final long ourInterval = 1000 * 60; // one minute
					set = new TreeSet<Editable>();
					for (long newTime = start.getDate().getTime(); newTime < end
							.getDate().getTime(); newTime += ourInterval) {
						final HiResDate newD = new HiResDate(newTime);
						final Watchable[] nearestOnes = getNearestTo(newD);
						if (nearestOnes.length > 0) {
							final FixWrapper nearest = (FixWrapper) nearestOnes[0];
							set.add(nearest);
						}
					}
				} else {
					// bugger that - get the real data

					// have a go..
					if (starter == null) {
						starter = new FixWrapper(new Fix((start),
								_zeroLocation, 0.0, 0.0));
					} else {
						starter.getFix().setTime(
								new HiResDate(0, start.getMicros() - 1));
					}

					if (finisher == null) {
						finisher = new FixWrapper(new Fix(new HiResDate(0,
								end.getMicros() + 1), _zeroLocation, 0.0, 0.0));
					} else {
						finisher.getFix().setTime(
								new HiResDate(0, end.getMicros() + 1));
					}

					// ok, ready, go for it.
					set = getPositionsBetween(starter, finisher);
				}

			}
		}

		return set;
	}

	/**
	 * just have the one property listener - rather than an anonymous class
	 * 
	 * @return
	 */
	public PropertyChangeListener getLocationListener() {
		return _locationListener;
	}

	/**
	 * method to allow the setting of label frequencies for the track
	 * 
	 * @return frequency to use
	 */
	public final HiResDate getLabelFrequency() {
		return this._lastLabelFrequency;
	}

	/**
	 * method to allow the setting of data sampling frequencies for the track &
	 * sensor data
	 * 
	 * @return frequency to use
	 */
	public final HiResDate getResampleDataAt() {
		return this._lastDataFrequency;
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 * 
	 * @return
	 */
	public int getLineThickness() {
		return _lineWidth;
	}

	/**
	 * what is the style used for plotting this track?
	 * 
	 * @return
	 */
	public int getLineStyle() {
		return _lineStyle;
	}

	/**
	 * set the style used for plotting the lines for this track
	 * 
	 * @param val
	 */
	@FireReformatted
	public void setLineStyle(int val) {
		_lineStyle = val;
	}

	/**
	 * name of this Track (normally the vessel name)
	 * 
	 * @return the name
	 */
	public final String getName() {
		return _theLabel.getString();
	}

	/**
	 * whether to show the track label at the start or end of the track
	 * 
	 * @return yes/no to indicate <I>At Start</I>
	 */
	public final boolean getNameAtStart() {
		return _LabelAtStart;
	}

	/**
	 * the relative location of the label
	 * 
	 * @return the relative location
	 */
	public final Integer getNameLocation() {
		return _theLabel.getRelativeLocation();
	}

	/**
	 * whether the track label is visible or not
	 * 
	 * @return yes/no
	 */
	public final boolean getNameVisible() {
		return _theLabel.getVisible();
	}

	/**
	 * whether to link points
	 * 
	 * @return
	 */
	public boolean getLinkPositions() {
		return _linkPositions;
	}

	/**
	 * whether to link points
	 * 
	 * @param linkPositions
	 */
	@FireReformatted
	public void setLinkPositions(boolean linkPositions) {
		_linkPositions = linkPositions;
	}

	/**
	 * find the fix nearest to this time (or the first fix for an invalid time)
	 * 
	 * @param DTG
	 *            the time of interest
	 * @return the nearest fix
	 */
	public final Watchable[] getNearestTo(final HiResDate srchDTG) {
		/**
		 * we need to end up with a watchable, not a fix, so we need to work our
		 * way through the fixes
		 */
		FixWrapper res = null;

		// check that we do actually contain some data
		if (_thePositions.size() == 0)
			return new MWC.GenericData.Watchable[] {};

		// special case - if we've been asked for an invalid time value
		if (srchDTG == TimePeriod.INVALID_DATE) {
			final TrackSegment seg = (TrackSegment) _thePositions.first();
			final FixWrapper fix = (FixWrapper) seg.first();
			// just return our first location
			return new MWC.GenericData.Watchable[] { (Watchable) fix };
		}

		// see if this is the DTG we have just requestsed
		if ((srchDTG.equals(lastDTG)) && (lastFix != null)) {
			res = lastFix;
		} else {
			final TrackSegment firstSeg = (TrackSegment) _thePositions.first();
			final TrackSegment lastSeg = (TrackSegment) _thePositions.last();

			// see if this DTG is inside our data range
			// in which case we will just return null
			final FixWrapper theFirst = (FixWrapper) firstSeg.first();
			final FixWrapper theLast = (FixWrapper) lastSeg.last();

			if ((srchDTG.greaterThan(theFirst.getTime()))
					&& (srchDTG.lessThanOrEqualTo(theLast.getTime()))) {
				// yes it's inside our data range, find the first fix
				// after the indicated point

				// right, increment the time, since we want to allow matching
				// points
				// HiResDate DTG = new HiResDate(0, srchDTG.getMicros() + 1);

				// see if we have to create our local temporary fix
				if (nearestFix == null) {
					nearestFix = new FixWrapper(new Fix(srchDTG, _zeroLocation,
							0.0, 0.0));
				} else
					nearestFix.getFix().setTime(srchDTG);

				// right, we really should be filtering the list according to if
				// the
				// points are visible.
				// how do we do filters?

				// get the data. use tailSet, since it's inclusive...
				SortedSet<Editable> set = getRawPositions().tailSet(nearestFix);

				// see if the requested DTG was inside the range of the data
				if (!set.isEmpty() && (set.size() > 0)) {
					res = (FixWrapper) set.first();

					// is this one visible?
					if (!res.getVisible()) {
						// right, the one we found isn't visible. duplicate the
						// set, so that
						// we can remove items
						// without affecting the parent
						set = new TreeSet<Editable>(set);

						// ok, start looping back until we find one
						while ((!res.getVisible()) && (set.size() > 0)) {

							// the first one wasn't, remove it
							set.remove(res);
							if (set.size() > 0)
								res = (FixWrapper) set.first();
						}
					}

				}

				// right, that's the first points on or before the indicated
				// DTG. Are we
				// meant
				// to be interpolating?
				if (res != null)
					if (getInterpolatePoints()) {
						// right - just check that we aren't actually on the
						// correct time
						// point.
						// HEY, USE THE ORIGINAL SEARCH TIME, NOT THE
						// INCREMENTED ONE,
						// SINCE WE DON'T WANT TO COMPARE AGAINST A MODIFIED
						// TIME

						if (!res.getTime().equals(srchDTG)) {

							// right, we haven't found an actual data point.
							// Better calculate
							// one

							// hmm, better also find the point before our one.
							// the
							// headSet operation is exclusive - so we need to
							// find the one
							// after the first
							final SortedSet<Editable> otherSet = getRawPositions()
									.headSet(nearestFix);

							FixWrapper previous = null;

							if (!otherSet.isEmpty()) {
								previous = (FixWrapper) otherSet.last();
							}

							// did it work?
							if (previous != null) {
								// cool, sort out the interpolated point USING
								// THE ORIGINAL
								// SEARCH TIME
								res = getInterpolatedFix(previous, res, srchDTG);
							}
						}
					}

			} else if (srchDTG.equals(theFirst.getDTG())) {
				// aaah, special case. just see if we're after a data point
				// that's the
				// same
				// as our start time
				res = theFirst;
			}

			// and remember this fix
			lastFix = res;
			lastDTG = srchDTG;
		}

		if (res != null)
			return new MWC.GenericData.Watchable[] { res };
		else
			return new MWC.GenericData.Watchable[] {};

	}

	/**
	 * get the position data, not all the sensor/contact/position data mixed
	 * together
	 * 
	 * @return
	 */
	public final Enumeration<Editable> getPositions() {
		final SortedSet<Editable> res = getRawPositions();
		return new TrackWrapper_Support.IteratorWrapper(res.iterator());
	}

	private SortedSet<Editable> getPositionsBetween(FixWrapper starter2,
			FixWrapper finisher2) {
		// first get them all as one list
		final SortedSet<Editable> pts = getRawPositions();

		// now do the sort
		return pts.subSet(starter2, finisher2);
	}

	/**
	 * whether positions are being shown
	 * 
	 * @return
	 */
	public final boolean getPositionsVisible() {
		return _showPositions;
	}

	private SortedSet<Editable> getRawPositions() {
		SortedSet<Editable> res = null;

		// do we just have the one list?
		if (_thePositions.size() == 1) {
			final TrackSegment p = (TrackSegment) _thePositions.first();
			res = (SortedSet<Editable>) p.getData();
		} else {
			// loop through them
			res = new TreeSet<Editable>();
			final Enumeration<Editable> segs = _thePositions.elements();
			while (segs.hasMoreElements()) {
				// get this segment
				final TrackSegment seg = (TrackSegment) segs.nextElement();

				// add all the points
				res.addAll(seg.getData());
			}
		}
		return res;
	}

	/**
	 * get the list of sensors for this track
	 */
	public final BaseLayer getSensors() {
		return _mySensors;
	}

	/**
	 * return the symbol to be used for plotting this track in snail mode
	 */
	public final MWC.GUI.Shapes.Symbols.PlainSymbol getSnailShape() {
		return _theSnailShape;
	}

	/**
	 * get the list of sensors for this track
	 */
	public final BaseLayer getSolutions() {
		return _mySolutions;
	}

	// //////////////////////////////////////
	// watchable (tote related) parameters
	// //////////////////////////////////////
	/**
	 * the earliest fix in the track
	 * 
	 * @return the DTG
	 */
	public final HiResDate getStartDTG() {
		HiResDate res = null;
		TimePeriod period = getTimePeriod();
		if (period != null)
			res = period.getStartDTG();

		return res;
	}

	private TimePeriod getTimePeriod() {
		TimePeriod res = null;

		Enumeration<Editable> segs = _thePositions.elements();
		while (segs.hasMoreElements()) {
			TrackSegment seg = (TrackSegment) segs.nextElement();
			if (res == null)
				res = new TimePeriod.BaseTimePeriod(seg.startDTG(),
						seg.endDTG());
			else {
				res.extend(seg.startDTG());
				res.extend(seg.endDTG());
			}
		}
		return res;
	}

	/**
	 * return the symbol frequencies for the track
	 * 
	 * @return frequency in seconds
	 */
	public final HiResDate getSymbolFrequency() {
		return _lastSymbolFrequency;
	}

	/**
	 * get the type of this symbol
	 */
	public final String getSymbolType() {
		return _theSnailShape.getType();
	}

	/**
	 * get the color used to plot the symbol
	 * 
	 * @return the color
	 */
	public final Color getSymbolColor() {
		return _theSnailShape.getColor();
	}

	@FireReformatted
	public final void setSymbolColor(Color col) {
		_theSnailShape.setColor(col);
	}

	/**
	 * the colour of the points on the track
	 * 
	 * @return the colour
	 */
	public final Color getTrackColor() {
		return getColor();
	}

	/**
	 * font handler
	 * 
	 * @return the font to use for the label
	 */
	public final java.awt.Font getTrackFont() {
		return _theLabel.getFont();
	}

	/**
	 * get the set of fixes contained within this time period which haven't been
	 * filtered, and which have valid depths. The isVisible flag indicates
	 * whether a track has been filtered or not. We also have the
	 * getVisibleFixesBetween method (below) which decides if a fix is visible
	 * if it is set to Visible, and it's label or symbol are visible.
	 * <p/>
	 * We don't have to worry about a valid depth, since 3d doesn't show points
	 * with invalid depth values
	 * 
	 * @param start
	 *            start DTG
	 * @param end
	 *            end DTG
	 * @return series of fixes
	 */
	public final Collection<Editable> getUnfilteredItems(final HiResDate start,
			final HiResDate end) {

		// see if we have _any_ points in range
		if ((getStartDTG().greaterThan(end)) || (getEndDTG().lessThan(start)))
			return null;

		if (this.getVisible() == false)
			return null;

		// get ready for the output
		final Vector<Editable> res = new Vector<Editable>(0, 1);

		// put the data into a period
		final TimePeriod thePeriod = new TimePeriod.BaseTimePeriod(start, end);

		// step through our fixes
		final Enumeration<Editable> iter = getPositions();
		while (iter.hasMoreElements()) {
			final FixWrapper fw = (FixWrapper) iter.nextElement();
			if (fw.getVisible()) {
				// is it visible?
				if (thePeriod.contains(fw.getTime())) {
					// hey, it's valid - continue
					res.add(fw);
				}
			}
		}
		return res;
	}

	/**
	 * whether this object has editor details
	 * 
	 * @return yes/no
	 */
	public final boolean hasEditor() {
		return true;
	}

	public boolean hasOrderedChildren() {
		return false;
	}

	/**
	 * quick accessor for how many fixes we have
	 * 
	 * @return
	 */
	public int numFixes() {
		return getRawPositions().size();
	}

	/**
	 * if we've got a relative track segment, it only learns where its
	 * individual fixes are once they've been initialised. This is where we do
	 * it.
	 */
	private void sortOutRelativePositions() {
		Enumeration<Editable> segments = _thePositions.elements();
		while (segments.hasMoreElements()) {
			TrackSegment seg = (TrackSegment) segments.nextElement();

			// SPECIAL HANDLING, SEE IF IT'S A TMA SEGMENT TO BE PLOTTED IN
			// RELATIVE MODE
			boolean isRelative = seg.getPlotRelative();
			WorldLocation tmaLastLoc = null;
			long tmaLastDTG = 0;

			// if it's not a relative track, and it's not visible, we don't
			// need to
			// work with ut
			if (!isRelative)
				continue;

			final Enumeration<Editable> fixWrappers = seg.elements();
			while (fixWrappers.hasMoreElements()) {
				final FixWrapper fw = (FixWrapper) fixWrappers.nextElement();

				// now there's a chance that our fix has forgotten it's parent,
				// particularly if it's the victim of a
				// copy/paste operation. Tell it about it's children
				fw.setTrackWrapper(this);

				// ok, are we in relative?
				if (isRelative) {
					long thisTime = fw.getDateTimeGroup().getDate().getTime();

					// ok, is this our first location?
					if (tmaLastLoc == null) {
						tmaLastLoc = new WorldLocation(seg.getTrackStart());
					} else {
						// calculate a new vector
						long timeDelta = thisTime - tmaLastDTG;
						if (lastFix != null) {
							double speedKts = lastFix.getSpeed();
							double courseRads = lastFix.getCourse();
							double depthM = lastFix.getDepth();
							// use the value of depth as read in from the file
							tmaLastLoc.setDepth(depthM);
							WorldVector thisVec = seg.vectorFor(timeDelta,
									speedKts, courseRads);
							tmaLastLoc.addToMe(thisVec);
						}
					}
					lastFix = fw;
					tmaLastDTG = thisTime;

					// dump the location into the fix
					fw.setFixLocationSilent(new WorldLocation(tmaLastLoc));
				}
			}
		}
	}

	/**
	 * draw this track (we can leave the Positions to draw themselves)
	 * 
	 * @param dest
	 *            the destination
	 */
	public final void paint(final CanvasType dest) {
		if (getVisible()) {
			// check we have some track data, else we won't work
			if (this.getStartDTG() == null)
				return;

			// set the thickness for this track
			dest.setLineWidth(_lineWidth);

			// and set the initial colour for this track
			dest.setColor(getColor());

			// /////////////////////////////////////////////
			// firstly plot the solutions
			// /////////////////////////////////////////////
			if (_mySolutions.getVisible()) {
				final Enumeration<Editable> iter = _mySolutions.elements();
				while (iter.hasMoreElements()) {
					final TMAWrapper sw = (TMAWrapper) iter.nextElement();
					// just check that the sensor knows we're it's parent
					if (sw.getHost() == null)
						sw.setHost(this);
					// and do the paint
					sw.paint(dest);

				} // through the solutions
			} // whether the solutions are visible

			// /////////////////////////////////////////////
			// now plot the sensors
			// /////////////////////////////////////////////
			if (_mySensors.getVisible()) {
				final Enumeration<Editable> iter = _mySensors.elements();
				while (iter.hasMoreElements()) {
					final SensorWrapper sw = (SensorWrapper) iter.nextElement();
					// just check that the sensor knows we're it's parent
					if (sw.getHost() == null)
						sw.setHost(this);

					// and do the paint
					sw.paint(dest);

				} // through the sensors
			} // whether the sensor layer is visible

			// /////////////////////////////////////////////
			// and now the track itself
			// /////////////////////////////////////////////

			// is our points store long enough?
			if ((_myPts == null) || (_myPts.length < numFixes() * 2)) {
				_myPts = new int[numFixes() * 2];
			}

			// reset the points counter
			_ptCtr = 0;

			// java.awt.Point lastP = null;
			Color lastCol = null;
			final int defaultlineStyle = _lineStyle;

			boolean locatedTrack = false;
			WorldLocation lastLocation = null;
			FixWrapper lastFix = null;

			// just check if we are drawing anything at all
			if ((!_linkPositions) && (!_showPositions))
				return;

			// keep track of if we have plotted any points (since
			// we won't be plotting the name if none of the points are visible).
			// this typically occurs when filtering is applied and a short
			// track is completely outside the time period
			boolean plotted_anything = false;

			// ///////////////////////////////////////////
			// let the fixes draw themselves in
			// ///////////////////////////////////////////

			Enumeration<Editable> segments = _thePositions.elements();
			while (segments.hasMoreElements()) {
				TrackSegment seg = (TrackSegment) segments.nextElement();

				// how shall we plot this segment?
				final int thisLineStyle;

				// is the parent using the default style?
				if (defaultlineStyle == CanvasType.SOLID) {
					// yes, let's override it, if the segment wants to
					thisLineStyle = seg.getLineStyle();
				} else {
					// no, we're using a custom style - don't override it.
					thisLineStyle = defaultlineStyle;
				}

				// SPECIAL HANDLING, SEE IF IT'S A TMA SEGMENT TO BE PLOTTED IN
				// RELATIVE
				// MODE
				boolean isRelative = seg.getPlotRelative();
				WorldLocation tmaLastLoc = null;
				long tmaLastDTG = 0;

				// if it's not a relative track, and it's not visible, we don't
				// need to
				// work with ut
				if (!getVisible() && !isRelative)
					continue;

				// is this segment visible?
				if (!seg.getVisible())
					continue;

				final Enumeration<Editable> fixWrappers = seg.elements();
				while (fixWrappers.hasMoreElements()) {
					final FixWrapper fw = (FixWrapper) fixWrappers
							.nextElement();

					// now there's a chance that our fix has forgotten it's
					// parent,
					// particularly if it's the victim of a
					// copy/paste operation. Tell it about it's children
					fw.setTrackWrapper(this);

					// is this fix visible?
					if (!fw.getVisible()) {
						// nope. Don't join it to the last position.
						// ok, if we've built up a polygon, we need to write it
						// now
						paintTrack(dest, lastCol, thisLineStyle);
					}

					// Note: we're carrying on working with this position even
					// if it isn't
					// visible,
					// since we need to use non-visible positions to build up a
					// DR track.

					// ok, so we have plotted something
					plotted_anything = true;

					// ok, are we in relative?
					if (isRelative) {
						long thisTime = fw.getDateTimeGroup().getDate()
								.getTime();

						// ok, is this our first location?
						if (tmaLastLoc == null) {
							tmaLastLoc = new WorldLocation(seg.getTrackStart());
							lastLocation = tmaLastLoc;
						} else {
							// calculate a new vector
							long timeDelta = thisTime - tmaLastDTG;
							if (lastFix != null) {
								double speedKts = lastFix.getSpeed();
								double courseRads = lastFix.getCourse();
								double depthM = lastFix.getDepth();
								// use the value of depth as read in from the
								// file
								tmaLastLoc.setDepth(depthM);
								WorldVector thisVec = seg.vectorFor(timeDelta,
										speedKts, courseRads);
								tmaLastLoc.addToMe(thisVec);
								lastLocation = tmaLastLoc;
							}

						}
						lastFix = fw;
						tmaLastDTG = thisTime;

						// dump the location into the fix
						fw.setFixLocationSilent(new WorldLocation(tmaLastLoc));

					} else {
						// this is an absolute position
						lastLocation = fw.getLocation();
					}

					// ok, we only do this writing to screen if the actual
					// position is
					// visible
					if (fw.getVisible()) {

						final java.awt.Point thisP = dest
								.toScreen(lastLocation);

						// just check that there's enough GUI to create the plot
						// (i.e. has a point been returned)
						if (thisP == null)
							return;

						// so, we're looking at the first data point. Do
						// we want to use this to locate the track name?
						if (_LabelAtStart) {
							// or have we already sorted out the location
							if (!locatedTrack) {
								locatedTrack = true;
								_theLabel.setLocation(new WorldLocation(
										lastLocation));
							}
						}

						// are we
						if (_linkPositions) {
							// right, just check if we're a different colour to
							// the
							// previous one
							final Color thisCol = fw.getColor();

							// do we know the previous colour
							if (lastCol == null) {
								lastCol = thisCol;
							}

							// is this to be joined to the previous one?
							if (fw.getLineShowing()) {
								// so, grow the the polyline, unless we've got a
								// colour
								// change...
								if (thisCol != lastCol) {
									// add our position to the list - so it
									// finishes on us
									_myPts[_ptCtr++] = thisP.x;
									_myPts[_ptCtr++] = thisP.y;

									// yup, better get rid of the previous
									// polygon
									paintTrack(dest, lastCol, thisLineStyle);
								}

								// add our position to the list - we'll output
								// the
								// polyline at the end
								_myPts[_ptCtr++] = thisP.x;
								_myPts[_ptCtr++] = thisP.y;
							} else {

								// nope, output however much line we've got so
								// far -
								// since this
								// line won't be joined to future points
								paintTrack(dest, thisCol, thisLineStyle);

								// start off the next line
								_myPts[_ptCtr++] = thisP.x;
								_myPts[_ptCtr++] = thisP.y;

							}

							/*
							 * set the colour of the track from now on to this
							 * colour, so that the "link" to the next fix is set
							 * to this colour if left unchanged
							 */
							dest.setColor(fw.getColor());

							// and remember the last colour
							lastCol = thisCol;

						}

						if (_showPositions && fw.getVisible()) {
							fw.paintMe(dest, lastLocation, fw.getColor());
						}
					}
				}

				// ok, just see if we have any pending polylines to paint
				paintTrack(dest, lastCol, thisLineStyle);

			}

			// are we trying to put the label at the end of the track?
			if (!_LabelAtStart) {
				// check that we have found at least one location to plot.
				if (lastLocation != null)
					_theLabel.setLocation(new WorldLocation(lastLocation));
			}

			// and draw the track label
			if (_theLabel.getVisible()) {

				// still, we only plot the track label if we have plotted any
				// points
				if (plotted_anything) {
					// just see if we have multiple segments. if we do,
					// name them individually
					if (this._thePositions.size() <= 1) {

						// check that we have found a location for the lable
						if (_theLabel.getLocation() != null) {

							// is the first track a DR track?
							TrackSegment t1 = (TrackSegment) _thePositions
									.first();
							if (t1.getPlotRelative()) {
								_theLabel.setFont(_theLabel.getFont()
										.deriveFont(Font.ITALIC));
							} else {
								if (_theLabel.getFont().isItalic())
									_theLabel.setFont(_theLabel.getFont()
											.deriveFont(Font.PLAIN));
							}

							// check that we have set the name for the label
							if (_theLabel.getString() == null) {
								_theLabel.setString(getName());
							}

							if (_theLabel.getColor() == null) {
								_theLabel.setColor(getColor());
							}

							// and paint it
							_theLabel.paint(dest);
						}// if the label has a location
					} else {
						// we've got multiple segments, name them
						Enumeration<Editable> posis = _thePositions.elements();
						while (posis.hasMoreElements()) {
							TrackSegment thisE = (TrackSegment) posis
									.nextElement();

							// is this segment visible?
							if (!thisE.getVisible())
								continue;

							// is the first track a DR track?
							if (thisE.getPlotRelative()) {
								_theLabel.setFont(_theLabel.getFont()
										.deriveFont(Font.ITALIC));
							} else {
								if (_theLabel.getFont().isItalic())
									_theLabel.setFont(_theLabel.getFont()
											.deriveFont(Font.PLAIN));
							}

							WorldLocation theLoc = thisE.getTrackStart();
							String oldTxt = _theLabel.getString();
							_theLabel.setString(thisE.getName());
							_theLabel.setLocation(theLoc);
							_theLabel.paint(dest);
							_theLabel.setString(oldTxt);

						}

					} // whether there's only one track
				}
			} // if the label is visible

		} // if visible
	}

	/**
	 * paint any polyline that we've built up
	 * 
	 * @param dest
	 *            - where we're painting to
	 * @param thisCol
	 * @param lineStyle
	 */
	private void paintTrack(final CanvasType dest, final Color thisCol,
			int lineStyle) {
		if (_ptCtr > 0) {
			dest.setColor(thisCol);
			dest.setLineStyle(lineStyle);
			final int[] poly = new int[_ptCtr];
			System.arraycopy(_myPts, 0, poly, 0, _ptCtr);
			dest.drawPolyline(poly);

			dest.setLineStyle(CanvasType.SOLID);

			// and reset the counter
			_ptCtr = 0;
		}
	}

	/**
	 * return the range from the nearest corner of the track
	 * 
	 * @param other
	 *            the other location
	 * @return the range
	 */
	public final double rangeFrom(final WorldLocation other) {
		double nearest = -1;

		// do we have a track?
		if (_myWorldArea != null) {
			// find the nearest point on the track
			nearest = _myWorldArea.rangeFrom(other);
		}

		return nearest;
	}

	/**
	 * remove the requested item from the track
	 * 
	 * @param point
	 *            the point to remove
	 */
	public final void removeElement(final Editable point) {
		// just see if it's a sensor which is trying to be removed
		if (point instanceof SensorWrapper) {
			_mySensors.removeElement(point);

			// tell the sensor wrapper to forget about us
			TacticalDataWrapper sw = (TacticalDataWrapper) point;
			sw.setHost(null);
		} else if (point instanceof TMAWrapper) {
			_mySolutions.removeElement(point);

			// tell the sensor wrapper to forget about us
			TacticalDataWrapper sw = (TacticalDataWrapper) point;
			sw.setHost(null);
		} else if (point instanceof SensorContactWrapper) {
			// ok, cycle through our sensors, try to remove this contact...
			final Enumeration<Editable> iter = _mySensors.elements();
			while (iter.hasMoreElements()) {
				final SensorWrapper sw = (SensorWrapper) iter.nextElement();
				// try to remove it from this one...
				sw.removeElement(point);
			}
		} else if (point instanceof TrackSegment) {
			_thePositions.removeElement(point);

			// and clear the parent item
			TrackSegment ts = (TrackSegment) point;
			ts.setWrapper(null);
		} else if (point == _mySensors) {
			// ahh, the user is trying to delete all the solution, cycle through
			// them
			Enumeration<Editable> iter = _mySensors.elements();
			while (iter.hasMoreElements()) {
				Editable editable = (Editable) iter.nextElement();

				// tell the sensor wrapper to forget about us
				TacticalDataWrapper sw = (TacticalDataWrapper) editable;
				sw.setHost(null);

			}

			// and empty them out
			_mySensors.removeAllElements();

		} else if (point == _mySolutions) {
			// ahh, the user is trying to delete all the solution, cycle through
			// them
			Enumeration<Editable> iter = _mySolutions.elements();
			while (iter.hasMoreElements()) {
				Editable editable = (Editable) iter.nextElement();

				// tell the sensor wrapper to forget about us
				TacticalDataWrapper sw = (TacticalDataWrapper) editable;
				sw.setHost(null);

			}

			// and empty them out
			_mySolutions.removeAllElements();

		}

		else {
			// loop through the segments
			final Enumeration<Editable> segments = _thePositions.elements();
			while (segments.hasMoreElements()) {
				final TrackSegment seg = (TrackSegment) segments.nextElement();
				seg.removeElement(point);
				// and stop listening to it (if it's a fix)
				if (point instanceof FixWrapper) {
					FixWrapper fw = (FixWrapper) point;
					fw.removePropertyChangeListener(
							PlainWrapper.LOCATION_CHANGED, _locationListener);
				}
			}
		}

	}

	/**
	 * pass through the track, resetting the labels back to their original DTG
	 */
	public void resetLabels() {
		FormatTracks.formatTrack(this);
	}

	// ////////////////////////////////////////////////////
	// LAYER support methods
	// /////////////////////////////////////////////////////

	/**
	 * set the colour of this track label
	 * 
	 * @param theCol
	 *            the colour
	 */
	@FireReformatted
	public final void setColor(final Color theCol) {
		// do the parent
		super.setColor(theCol);

		// now do our processing
		_theLabel.setColor(theCol);
	}

	/**
	 * the setter function which passes through the track
	 */
	private void setFixes(final FixSetter setter, final HiResDate theVal) {
		final long freq = theVal.getMicros();

		// briefly check if we are revealing/hiding all times (ie if freq is 1
		// or 0)
		if (freq == TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY) {
			// show all of the labels
			final Enumeration<Editable> iter = getPositions();
			while (iter.hasMoreElements()) {
				final FixWrapper fw = (FixWrapper) iter.nextElement();
				setter.execute(fw, true);
			}
		} else {
			// no, we're not just blindly doing all of them. do them at the
			// correct
			// frequency

			// hide all of the labels/symbols first
			final Enumeration<Editable> enumA = getPositions();
			while (enumA.hasMoreElements()) {
				final FixWrapper fw = (FixWrapper) enumA.nextElement();
				setter.execute(fw, false);
			}

			if (freq == 0) {
				// we can ignore this, since we have just hidden all of the
				// points
			} else {

				// pass through the track setting the values

				// sort out the start and finish times
				long start_time = getStartDTG().getMicros();
				final long end_time = getEndDTG().getMicros();

				// first check that there is a valid time period between start
				// time
				// and end time
				if (start_time + freq < end_time) {
					long num = start_time / freq;

					// we need to add one to the quotient if it has rounded down
					if (start_time % freq == 0) {
						// start is at our freq, so we don't need to increment
						// it
					} else {
						num++;
					}

					// calculate new start time
					start_time = num * freq;
				} else {
					// there is not one of our 'intervals' between the start and
					// the end,
					// so use the start time
				}

				while (start_time <= end_time) {
					// right, increment the start time by one, because we were
					// getting the
					// fix immediately before the requested time
					final HiResDate thisDTG = new HiResDate(0, start_time);
					final MWC.GenericData.Watchable[] list = this
							.getNearestTo(thisDTG);
					// check we found some
					if (list.length > 0) {
						final FixWrapper fw = (FixWrapper) list[0];
						setter.execute(fw, true);
					}
					// produce the next time step
					start_time += freq;
				}
			}

		}
	}

	public final void setInterpolatePoints(boolean val) {
		_interpolatePoints = val;
	}

	/**
	 * set the data frequency (in seconds) for the track & sensor data
	 * 
	 * @param theVal
	 *            frequency to use
	 */
	@FireExtended
	public final void setResampleDataAt(final HiResDate theVal) {
		this._lastDataFrequency = theVal;

		// have a go at trimming the start time to a whole number of intervals
		final long interval = theVal.getMicros();

		// do we have a start time (we may just be being tested...)
		if (this.getStartDTG() == null) {
			return;
		}

		final long currentStart = this.getStartDTG().getMicros();
		long startTime = (currentStart / interval) * interval;

		// just check we're in the range
		if (startTime < currentStart)
			startTime += interval;

		// just check it's not a barking frequency
		if (theVal.getDate().getTime() <= 0) {
			// ignore, we don't need to do anything for a zero or a -1
		} else {

			SegmentList segments = _thePositions;
			Enumeration<Editable> theEnum = segments.elements();
			while (theEnum.hasMoreElements()) {
				TrackSegment seg = (TrackSegment) theEnum.nextElement();
				seg.decimate(theVal, this, startTime);
			}

			// start off with the sensor data
			if (_mySensors != null) {
				for (Enumeration<Editable> iterator = _mySensors.elements(); iterator
						.hasMoreElements();) {
					SensorWrapper thisS = (SensorWrapper) iterator
							.nextElement();
					thisS.decimate(theVal, startTime);
				}
			}

			// now the solutions
			if (_mySolutions != null) {
				for (Enumeration<Editable> iterator = _mySolutions.elements(); iterator
						.hasMoreElements();) {
					TMAWrapper thisT = (TMAWrapper) iterator.nextElement();
					thisT.decimate(theVal, startTime);
				}
			}

		}
	}

	/**
	 * set the label frequency (in seconds)
	 * 
	 * @param theVal
	 *            frequency to use
	 */
	@FireReformatted
	public final void setLabelFrequency(final HiResDate theVal) {
		this._lastLabelFrequency = theVal;

		final FixSetter setLabel = new FixSetter() {
			public void execute(final FixWrapper fix, final boolean val) {
				fix.setLabelShowing(val);
			}
		};
		setFixes(setLabel, theVal);
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 */
	@FireReformatted
	public void setLineThickness(final int val) {
		_lineWidth = val;
	}

	// ////////////////////////////////////////////////////
	// track-shifting operation
	// /////////////////////////////////////////////////////

	// /////////////////////////////////////////////////
	// support for dragging the track around
	// ////////////////////////////////////////////////

	/**
	 * set the name of this track (normally the name of the vessel
	 * 
	 * @param theName
	 *            the name as a String
	 */
	@FireReformatted
	public final void setName(final String theName) {
		_theLabel.setString(theName);
	}

	/**
	 * whether to show the track name at the start or end of the track
	 * 
	 * @param val
	 *            yes no for <I>show label at start</I>
	 */
	@FireReformatted
	public final void setNameAtStart(final boolean val) {
		_LabelAtStart = val;
	}

	/**
	 * the relative location of the label
	 * 
	 * @param val
	 *            the relative location
	 */
	@FireReformatted
	public final void setNameLocation(final Integer val) {
		_theLabel.setRelativeLocation(val);
	}

	/**
	 * whether to show the track name
	 * 
	 * @param val
	 *            yes/no
	 */
	@FireReformatted
	public final void setNameVisible(final boolean val) {
		_theLabel.setVisible(val);
	}

	/**
	 * whether to show the position fixes
	 * 
	 * @param val
	 *            yes/no
	 */
	@FireReformatted
	public final void setPositionsVisible(final boolean val) {
		_showPositions = val;
	}

	/**
	 * return the arrow frequencies for the track
	 * 
	 * @return frequency in seconds
	 */
	public final HiResDate getArrowFrequency() {
		return _lastArrowFrequency;
	}

	/**
	 * how frequently symbols are placed on the track
	 * 
	 * @param theVal
	 *            frequency in seconds
	 */
	@FireReformatted
	public final void setArrowFrequency(final HiResDate theVal) {
		this._lastArrowFrequency = theVal;

		// set the "showPositions" parameter, as long as we are
		// not setting the symbols off
		if (theVal.getMicros() != 0.0) {
			this.setPositionsVisible(true);
		}

		final FixSetter setSymbols = new FixSetter() {
			public void execute(final FixWrapper fix, final boolean val) {
				fix.setArrowShowing(val);
			}
		};

		setFixes(setSymbols, theVal);
	}

	/**
	 * how frequently symbols are placed on the track
	 * 
	 * @param theVal
	 *            frequency in seconds
	 */
	@FireReformatted
	public final void setSymbolFrequency(final HiResDate theVal) {
		this._lastSymbolFrequency = theVal;

		// set the "showPositions" parameter, as long as we are
		// not setting the symbols off
		if (theVal.getMicros() != 0.0) {
			this.setPositionsVisible(true);
		}

		final FixSetter setSymbols = new FixSetter() {
			public void execute(final FixWrapper fix, final boolean val) {
				fix.setSymbolShowing(val);
			}
		};

		setFixes(setSymbols, theVal);
	}

	@FireReformatted
	public final void setSymbolType(final String val) {
		// is this the type of our symbol?
		if (val.equals(_theSnailShape.getType())) {
			// don't bother we're using it already
		} else {
			// remember the size of the symbol
			final double scale = _theSnailShape.getScaleVal();
			// remember the color of the symbol
			final Color oldCol = _theSnailShape.getColor();

			// replace our symbol with this new one
			_theSnailShape = MWC.GUI.Shapes.Symbols.SymbolFactory
					.createSymbol(val);
			_theSnailShape.setColor(oldCol);
			_theSnailShape.setScaleVal(scale);
		}
	}

	public WorldDistance getSymbolLength() {
		WorldDistance res = null;
		if (_theSnailShape instanceof WorldScaledSym) {
			WorldScaledSym sym = (WorldScaledSym) _theSnailShape;
			res = sym.getLength();
		}
		return res;
	}

	public void setSymbolLength(WorldDistance symbolLength) {
		if (_theSnailShape instanceof WorldScaledSym) {
			WorldScaledSym sym = (WorldScaledSym) _theSnailShape;
			sym.setLength(symbolLength);
		}
	}

	public WorldDistance getSymbolWidth() {
		WorldDistance res = null;
		if (_theSnailShape instanceof WorldScaledSym) {
			WorldScaledSym sym = (WorldScaledSym) _theSnailShape;
			res = sym.getWidth();
		}
		return res;
	}

	@FireReformatted
	public void setSymbolWidth(WorldDistance symbolWidth) {
		if (_theSnailShape instanceof WorldScaledSym) {
			WorldScaledSym sym = (WorldScaledSym) _theSnailShape;
			sym.setHeight(symbolWidth);
		}
	}

	// note we are putting a track-labelled wrapper around the colour
	// parameter, to make the properties window less confusing
	/**
	 * the colour of the points on the track
	 * 
	 * @param theCol
	 *            the colour to use
	 */
	@FireReformatted
	public final void setTrackColor(final Color theCol) {
		setColor(theCol);
	}

	/**
	 * font handler
	 * 
	 * @param font
	 *            the font to use for the label
	 */
	@FireReformatted
	public final void setTrackFont(final java.awt.Font font) {
		_theLabel.setFont(font);
	}

	public void shift(WorldLocation feature, WorldVector vector) {
		feature.addToMe(vector);

		// right, one of our fixes has moved. get the sensors to update
		// themselves
		fixMoved();
	}

	public void shift(WorldVector vector) {
		this.shiftTrack(elements(), vector);
	}

	/**
	 * move the whole of the track be the provided offset
	 */
	public final void shiftTrack(Enumeration<Editable> theEnum,
			final WorldVector offset) {
		// keep track of if the track contains something that doesn't get
		// dragged
		boolean handledData = false;

		if (theEnum == null)
			theEnum = elements();

		while (theEnum.hasMoreElements()) {
			final Object thisO = theEnum.nextElement();
			if (thisO instanceof TrackSegment) {
				TrackSegment seg = (TrackSegment) thisO;
				seg.shift(offset);

				// ok - job well done
				handledData = true;

			} else if (thisO instanceof SegmentList) {
				SegmentList list = (SegmentList) thisO;
				Collection<Editable> items = list.getData();
				for (Iterator<Editable> iterator = items.iterator(); iterator
						.hasNext();) {
					TrackSegment segment = (TrackSegment) iterator.next();
					segment.shift(offset);
				}
				handledData = true;
			} else if (thisO instanceof SensorWrapper) {
				final SensorWrapper sw = (SensorWrapper) thisO;
				final Enumeration<Editable> enumS = sw.elements();
				while (enumS.hasMoreElements()) {
					final SensorContactWrapper scw = (SensorContactWrapper) enumS
							.nextElement();
					// does this fix have it's own origin?
					final WorldLocation sensorOrigin = scw.getOrigin();

					if (sensorOrigin != null) {
						// create new object to contain the updated location
						final WorldLocation newSensorLocation = new WorldLocation(
								sensorOrigin);
						newSensorLocation.addToMe(offset);

						// so the contact did have an origin, change it
						scw.setOrigin(newSensorLocation);
					}
				} // looping through the contacts

				// ok - job well done
				handledData = true;

			} // whether this is a sensor wrapper
			else if (thisO instanceof TrackSegment) {
				final TrackSegment tw = (TrackSegment) thisO;
				final Enumeration<Editable> enumS = tw.elements();

				// fire recursively, smart-arse.
				shiftTrack(enumS, offset);

				// ok - job well done
				handledData = true;

			} // whether this is a sensor wrapper
		} // looping through this track

		// ok, did we handle the data?
		if (!handledData) {
			System.err.println("TrackWrapper problem; not able to shift:"
					+ theEnum);
		}
	}

	/**
	 * split this whole track into two sub-tracks
	 * 
	 * @param splitPoint
	 *            the point at which we perform the split
	 * @param splitBeforePoint
	 *            whether to put split before or after specified point
	 * @return a list of the new track segments (used for subsequent undo
	 *         operations)
	 */
	public Vector<TrackSegment> splitTrack(FixWrapper splitPoint,
			boolean splitBeforePoint) {
		Vector<TrackSegment> res = null;
		TrackSegment relevantSegment = null;

		// are we still in one section?
		if (_thePositions.size() == 1) {
			relevantSegment = (TrackSegment) _thePositions.first();

			// yup, looks like we're going to be splitting it.
			// better give it a proper name
			relevantSegment.setName(relevantSegment.startDTG().getDate()
					.toString());
		} else {
			// ok, find which segment contains our data
			final Enumeration<Editable> segments = _thePositions.elements();
			while (segments.hasMoreElements()) {
				final TrackSegment seg = (TrackSegment) segments.nextElement();
				if (seg.getData().contains(splitPoint)) {
					relevantSegment = seg;
					break;
				}
			}
		}

		if (relevantSegment == null) {
			throw new RuntimeException(
					"failed to provide relevant segment, alg will break");
		}

		// hmm, if we're splitting after the point, we need to move along the
		// bus by
		// one
		if (!splitBeforePoint) {
			Collection<Editable> items = relevantSegment.getData();
			Iterator<Editable> theI = items.iterator();
			Editable previous = null;
			while (theI.hasNext()) {
				Editable thisE = (Editable) theI.next();

				// have we chosen to remember the previous item?
				if (previous != null) {
					// yes, this must be the one we're after
					splitPoint = (FixWrapper) thisE;
					break;
				}

				// is this the one we're looking for?
				if (thisE == splitPoint) {
					// yup, remember it - we want to use the next value
					previous = (FixWrapper) thisE;
				}
			}
		}

		// yup, do our first split
		final SortedSet<Editable> p1 = relevantSegment.headSet(splitPoint);
		final SortedSet<Editable> p2 = relevantSegment.tailSet(splitPoint);

		// get our results ready
		final TrackSegment ts1, ts2;

		// aaah, just sort out if we are splitting a TMA segment, in which case
		// we
		// want to create two
		// tma segments, not track segments
		if (relevantSegment instanceof RelativeTMASegment) {
			RelativeTMASegment theTMA = (RelativeTMASegment) relevantSegment;

			// aah, sort out if we are splitting before or after.

			// find out the offset at the split point, so we can initiate it for
			// the
			// second part of the track
			WorldLocation refTrackLoc = theTMA.getReferenceTrack()
					.getNearestTo(splitPoint.getDateTimeGroup())[0]
					.getLocation();
			WorldVector secondOffset = splitPoint.getLocation().subtract(
					refTrackLoc);

			// put the lists back into plottable layers
			RelativeTMASegment tr1 = new RelativeTMASegment(theTMA, p1,
					theTMA.getOffset());
			RelativeTMASegment tr2 = new RelativeTMASegment(theTMA, p2,
					secondOffset);

			// update the freq's
			tr1.setBaseFrequency(((CoreTMASegment) relevantSegment)
					.getBaseFrequency());
			tr2.setBaseFrequency(((CoreTMASegment) relevantSegment)
					.getBaseFrequency());

			// and store them
			ts1 = tr1;
			ts2 = tr2;

		} else if (relevantSegment instanceof AbsoluteTMASegment) {
			AbsoluteTMASegment theTMA = (AbsoluteTMASegment) relevantSegment;

			// aah, sort out if we are splitting before or after.

			// find out the offset at the split point, so we can initiate it for
			// the
			// second part of the track
			Watchable[] matches = this.getNearestTo(splitPoint
					.getDateTimeGroup());
			WorldLocation origin = matches[0].getLocation();

			FixWrapper t1Start = (FixWrapper) p1.first();

			// put the lists back into plottable layers
			AbsoluteTMASegment tr1 = new AbsoluteTMASegment(theTMA, p1,
					t1Start.getLocation(), null, null);
			AbsoluteTMASegment tr2 = new AbsoluteTMASegment(theTMA, p2, origin,
					null, null);

			// update the freq's
			tr1.setBaseFrequency(((CoreTMASegment) relevantSegment)
					.getBaseFrequency());
			tr2.setBaseFrequency(((CoreTMASegment) relevantSegment)
					.getBaseFrequency());

			// and store them
			ts1 = tr1;
			ts2 = tr2;

		} else {
			// put the lists back into plottable layers
			ts1 = new TrackSegment(p1);
			ts2 = new TrackSegment(p2);
		}

		// now clear the positions
		_thePositions.removeElement(relevantSegment);

		// and put back our new layers
		_thePositions.addSegment(ts1);
		_thePositions.addSegment(ts2);

		// remember them
		res = new Vector<TrackSegment>();
		res.add(ts1);
		res.add(ts2);

		return res;
	}

	/**
	 * extra parameter, so that jvm can produce a sensible name for this
	 * 
	 * @return the track name, as a string
	 */
	public final String toString() {
		return "Track:" + getName();
	}

	/**
	 * is this track visible between these time periods?
	 * 
	 * @param start
	 *            start DTG
	 * @param end
	 *            end DTG
	 * @return yes/no
	 */
	public final boolean visibleBetween(final HiResDate start,
			final HiResDate end) {
		boolean visible = false;
		if (getStartDTG().lessThan(end) && (getEndDTG().greaterThan(start))) {
			visible = true;
		}

		return visible;
	}

	/**
	 * calculate a position the specified distance back along the ownship track
	 * (note, we always interpolate the parent track position)
	 * 
	 * @param searchTime
	 *            the time we're looking at
	 * @param sensorOffset
	 *            how far back the sensor should be
	 * @param wormInHole
	 *            whether to plot a straight line back, or make sensor follow
	 *            ownship
	 * @return the location
	 */
	public FixWrapper getBacktraceTo(HiResDate searchTime,
			ArrayLength sensorOffset, boolean wormInHole) {
		FixWrapper res = null;

		if (wormInHole && sensorOffset != null) {
			res = WormInHoleOffset.getWormOffsetFor(this, searchTime,
					sensorOffset);
		} else {

			boolean parentInterpolated = getInterpolatePoints();
			setInterpolatePoints(true);

			final MWC.GenericData.Watchable[] list = getNearestTo(searchTime);

			// and restore the interpolated value
			setInterpolatePoints(parentInterpolated);

			FixWrapper wa = null;
			if (list.length > 0)
				wa = (FixWrapper) list[0];

			// did we find it?
			if (wa != null) {
				// yes, store it
				res = new FixWrapper(wa.getFix().makeCopy());

				// ok, are we dealing with an offset?
				if (sensorOffset != null) {
					// get the current heading
					double hdg = wa.getCourse();
					// and calculate where it leaves us
					WorldVector vector = new WorldVector(hdg, sensorOffset,
							null);

					// now apply this vector to the origin
					res.setLocation(new WorldLocation(res.getLocation().add(
							vector)));
				}
			}

		}

		return res;
	}

	@FireReformatted
	public void setPlotArrayCentre(boolean plotArrayCentre) {
		_plotArrayCentre = plotArrayCentre;
	}

	public boolean getPlotArrayCentre() {
		return _plotArrayCentre;
	}

}
