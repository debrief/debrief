/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.Wrappers.Track;

import java.awt.Color;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import flanagan.interpolation.CubicSpline;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.Algorithms.Conversions;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Editable.editableTesterSupport;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.Plottable;
import MWC.GUI.Canvas.MockCanvasType;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * @author Administrator
 * 
 */
public class TrackWrapper_Test extends junit.framework.TestCase
{

	protected static class TestMockCanvas extends MockCanvasType
	{
		@Override
		public void drawPolyline(final int[] points)
		{
			callCount++;
			pointCount += points.length;
		}
	}

	public static final String TRACK_NAME = "test track";

	public static FixWrapper createFix(final int timeMillis, final int vLatDeg,
			final int vLatMin, final double vLatSec, final int vLongDeg,
			final int vLongMin, final double vLongSec, final int crseDegs,
			final int spdKts)
	{
		final double vLat = vLatDeg + (vLatMin / 60d) + (vLatSec / 3600d);
		final double vLong = vLongDeg + (vLongMin / 60d) + (vLongSec / 3600d);
		final FixWrapper theFix = createFix(timeMillis, vLat, vLong);
		theFix.getFix().setCourse(MWC.Algorithms.Conversions.Degs2Rads(crseDegs));
		theFix.getFix()
				.setSpeed(
						new WorldSpeed(spdKts, WorldSpeed.Kts)
								.getValueIn(WorldSpeed.ft_sec) / 3);

		return theFix;
	}

	public static TrackSegment getDummyList()
	{
		final TrackSegment ts0 = new TrackSegment();
		final FixWrapper newFix1 = new FixWrapper(new Fix(new HiResDate(10000),
				new WorldLocation(1, -1, 3), 1, 2));
		final FixWrapper newFix2 = new FixWrapper(new Fix(new HiResDate(20000),
				new WorldLocation(1, 0, 3), 1, 2));
		final FixWrapper newFix3 = new FixWrapper(new Fix(new HiResDate(30000),
				new WorldLocation(1, 1, 3), 1, 2));
		final FixWrapper newFix4 = new FixWrapper(new Fix(new HiResDate(40000),
				new WorldLocation(1, 2, 3), 1, 2));
		ts0.addFix(newFix1);
		ts0.addFix(newFix2);
		ts0.addFix(newFix3);
		ts0.addFix(newFix4);
		return ts0;
	}

	public static FixWrapper createFix(final int timeMillis, final int vLat,
			final int vLong, final int crseDegs, final int spdKts)
	{
		final FixWrapper theFix = createFix(timeMillis, vLat, vLong);
		theFix.getFix().setCourse(MWC.Algorithms.Conversions.Degs2Rads(crseDegs));
		theFix.getFix()
				.setSpeed(
						new WorldSpeed(spdKts, WorldSpeed.Kts)
								.getValueIn(WorldSpeed.ft_sec) / 3);

		return theFix;
	}

	public static FixWrapper createFix(final long timeMillis, final double vLat,
			final double vLong)
	{
		final FixWrapper fw = new FixWrapper(new Fix(new HiResDate(timeMillis),
				new WorldLocation(vLat, vLong, 0), 1, 1));
		return fw;
	}

	/**
	 * fixes we can easily refer to in a test..
	 * 
	 */
	private final FixWrapper _fw1 = createFix(300000, 2, 3);

	private final FixWrapper _fw2 = createFix(500000, 2, 3);

	private TrackWrapper _tw;

	private MessageProvider.TestableMessageProvider _messages;

	private int _ctr = 0;

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

	private int countCuts(final Enumeration<Editable> sensors)
	{
		if (sensors == null)
			return 0;

		int counter = 0;
		while (sensors.hasMoreElements())
		{
			final SensorWrapper sw = (SensorWrapper) sensors.nextElement();
			final Enumeration<Editable> ele = sw.elements();
			while (ele.hasMoreElements())
			{
				counter++;
				ele.nextElement();
				// SensorContactWrapper sc = (SensorContactWrapper) ele.nextElement();
				// System.out.println("cut time is:" +
				// MWC.Utilities.TextFormatting.FormatRNDateTime.toString(sc.getTime().getDate().getTime())
				// + " brg is:" + sc.getBearing());
			}
		}
		System.out.println("===========");
		return counter;
	}

	private int countSolutions(final Enumeration<Editable> solutions)
	{
		if (solutions == null)
			return 0;

		int counter = 0;
		while (solutions.hasMoreElements())
		{
			final TMAWrapper sw = (TMAWrapper) solutions.nextElement();
			final Enumeration<Editable> ele = sw.elements();
			while (ele.hasMoreElements())
			{
				counter++;
				ele.nextElement();
				// TMAContactWrapper sc = (TMAContactWrapper) ele.nextElement();
				// System.out.println(" solution time is:" +
				// MWC.Utilities.TextFormatting.FormatRNDateTime.toString(sc.getTime().getDate().getTime())
				// + " brg is:" + MWC.Algorithms.Conversions.Rads2Degs(sc.getCourse()));
			}
		}
		System.out.println("===========");
		return counter;
	}

	private int countVisibleItems()
	{
		final Enumeration<Editable> all = _tw.contiguousElements();
		int ctr = 0;
		while (all.hasMoreElements())
		{
			final Plottable thisE = (Plottable) all.nextElement();
			if (thisE.getVisible())
				ctr++;
		}
		return ctr;
	}

	private SensorContactWrapper createSensorItem(final TrackWrapper tw,
			final SensorWrapper sw, final int sensorDTG)
	{
		return new SensorContactWrapper(tw.getName(), new HiResDate(sensorDTG),
				new WorldDistance(12, WorldDistance.NM), 12d, null, Color.red,
				"some lable", 12, sw.getName());
	}

	private int numSegments()
	{
		int res = 0;
		final Enumeration<Editable> layers = _tw.elements();
		while (layers.hasMoreElements())
		{
			final Object child = layers.nextElement();
			if (child instanceof TrackSegment)
			{
				res++;
			}
			else if (child instanceof SegmentList)
			{
				final SegmentList segl = (SegmentList) child;
				final Enumeration<Editable> segs = segl.elements();
				while (segs.hasMoreElements())
				{
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
	@Override
	public void setUp() throws Exception
	{
		// give the fixes some names, so we can track them
		_fw1.setLabel("fw1");
		_fw2.setLabel("fw2");

		_tw = new TrackWrapper();
		_tw.setName(TRACK_NAME);
		_tw.addFix(createFix(100000, 1, 1));
		_tw.addFix(createFix(200000, 2, 3));
		_tw.addFix(_fw1);
		_tw.addFix(createFix(400000, 3, 3));
		_tw.addFix(_fw2);
		_tw.addFix(createFix(600000, 4, 6));
		_messages = new MessageProvider.TestableMessageProvider();
		MessageProvider.Base.setProvider(_messages);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void tearDown() throws Exception
	{
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#add(MWC.GUI.Editable)}
	 * .
	 */

	public void testAdd()
	{
		assertEquals("start condition", 6, this.trackLength());

		// check we can add a fix
		final FixWrapper fw = createFix(12, 3d, 4d);
		_tw.add(fw);

		assertEquals("got added", 7, this.trackLength());

		// now something else
		final SensorWrapper sw = new SensorWrapper("some sensor");
		SensorContactWrapper scw = new SensorContactWrapper();
		scw.setDTG(new HiResDate(1000));
		sw.add(scw);
		sw.setVisible(true);
		_tw.add(sw);

		assertEquals("got added", 8, this.trackLength());

	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#addFix(Debrief.Wrappers.FixWrapper)}.
	 */

	public void testAddFix()
	{
		assertEquals("start condition", 6, this.trackLength());

		// check we can add a fix
		final FixWrapper fw = createFix(12, 3d, 4d);
		_tw.addFix(fw);

		assertEquals("got added", 7, this.trackLength());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#append(MWC.GUI.Layer)}
	 * .
	 */

	public void testAppend()
	{
		final TrackWrapper tw2 = new TrackWrapper();
		final FixWrapper f1 = createFix(13, 2, 2);
		final FixWrapper f2 = createFix(14, 32, 12);
		tw2.addFix(f1);
		tw2.addFix(f2);

		// check current state of track
		assertEquals("in start condition", 6, trackLength());

		// combine the two
		_tw.append(tw2);
		assertEquals("received extra points", 8, trackLength());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#contiguousElements()}.
	 */

	public void testContiguousElements()
	{
		assertEquals("have initial items", 6, trackLength());

		// give it a little more data
		final SensorWrapper sw = new SensorWrapper("sensor a");
		sw.add(new SensorContactWrapper("trk", new HiResDate(12), null, null, null,
				null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(13), null, null, null,
				null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(14), null, null, null,
				null, null, 0, null));
		sw.setVisible(true);

		_tw.add(sw);

		final Enumeration<Editable> tester = _tw.contiguousElements();
		_ctr = 0;
		while (tester.hasMoreElements())
		{
			tester.nextElement();
			_ctr++;
		}
		assertEquals("have new items", 9, _ctr);
	}

	public void testDecimate()
	{
		final TrackSegment ts1 = new TrackSegment();
		ts1.addFix(createFix(0 * 1000000l, 32, 33));
		ts1.addFix(createFix(1 * 1000000l, 32, 33));
		ts1.addFix(createFix(2 * 1000000l, 32, 33));
		ts1.addFix(createFix(3 * 1000000l, 32, 33));
		ts1.addFix(createFix(4 * 1000000l, 32, 33));

		final TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(5 * 1000000l, 32, 33));
		ts2.addFix(createFix(6 * 1000000l, 32, 33));
		ts2.addFix(createFix(7 * 1000000l, 32, 33));
		ts2.addFix(createFix(8 * 1000000l, 32, 33));
		ts2.addFix(createFix(9 * 1000000l, 32, 33));

		final TrackSegment ts3 = new TrackSegment();
		ts3.addFix(createFix(10 * 1000000l, 32, 33));
		ts3.addFix(createFix(11 * 1000000l, 32, 33));
		ts3.addFix(createFix(12 * 1000000l, 32, 33));
		ts3.addFix(createFix(13 * 1000000l, 32, 33));
		ts3.addFix(createFix(24 * 1000000l, 32, 33));

		final TrackWrapper tw = new TrackWrapper();
		tw.add(ts1);
		tw.add(ts2);
		tw.add(ts3);

		final SegmentList sl = (SegmentList) tw.getSegments();

		// check it's got the segs
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 15, tw.numFixes());

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(4 * 1000000l));

		// how was it?
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 7, tw.numFixes());

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(500000l));

		// how was it?
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 49, tw.numFixes());
	}

	public void testDecimatePositionsAndData()
	{
		final TrackSegment ts1 = new TrackSegment();
		ts1.addFix(createFix(0 * 60000, 32, 33));
		ts1.addFix(createFix(1 * 60000, 32, 33));
		ts1.addFix(createFix(2 * 60000, 32, 33));
		ts1.addFix(createFix(3 * 60000, 32, 33));
		ts1.addFix(createFix(4 * 60000, 32, 33));

		final TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(5 * 60000, 32, 33));
		ts2.addFix(createFix(6 * 60000, 32, 33));
		ts2.addFix(createFix(7 * 60000, 32, 33));
		ts2.addFix(createFix(8 * 60000, 32, 33));
		ts2.addFix(createFix(9 * 60000, 32, 33));

		final TrackSegment ts3 = new TrackSegment();
		ts3.addFix(createFix(10 * 60000, 32, 33));
		ts3.addFix(createFix(11 * 60000, 32, 33));
		ts3.addFix(createFix(12 * 60000, 32, 33));
		ts3.addFix(createFix(13 * 60000, 32, 33));
		ts3.addFix(createFix(24 * 60000, 32, 33));

		final SensorWrapper sw = new SensorWrapper("dummy sensor");
		final SensorContactWrapper scw1 = new SensorContactWrapper("the track",
				new HiResDate(1 * 60000), new WorldDistance(2, WorldDistance.NM), 12d,
				new Double(12), new Double(44), null, Color.red, "aa", 1,
				"dummy sensor");
		final SensorContactWrapper scw2 = new SensorContactWrapper("the track",
				new HiResDate(3 * 60000), new WorldDistance(4, WorldDistance.NM), 15d,
				new Double(15), new Double(46), null, Color.red, "aa", 1,
				"dummy sensor");
		final SensorContactWrapper scw3 = new SensorContactWrapper("the track",
				new HiResDate(7 * 60000), new WorldDistance(12, WorldDistance.NM), 18d,
				new Double(12), new Double(12), null, Color.red, "aa", 1,
				"dummy sensor");
		final SensorContactWrapper scw4 = new SensorContactWrapper("the track",
				new HiResDate(8 * 60000), new WorldDistance(7, WorldDistance.NM), 35d,
				new Double(12), new Double(312), null, Color.red, "aa", 1,
				"dummy sensor");
		sw.add(scw1);
		sw.add(scw2);
		sw.add(scw3);
		sw.add(scw4);

		final TMAWrapper tmw = new TMAWrapper("dummy tma");
		final TMAContactWrapper tc1 = new TMAContactWrapper("sola", "tracla",
				new HiResDate(2 * 60000), 12d, 14d, 22d, 12.2, 0d, Color.red, "bb",
				new EllipseShape(null, 12, new WorldDistance(12, WorldDistance.DEGS),
						new WorldDistance(12, WorldDistance.DEGS)), "aa");
		final TMAContactWrapper tc2 = new TMAContactWrapper("sola", "tracla",
				new HiResDate(7 * 60000), 13d, 22d, 17d, 12.2, 0d, Color.red, "bb",
				new EllipseShape(null, 14, new WorldDistance(21, WorldDistance.DEGS),
						new WorldDistance(15, WorldDistance.DEGS)), "aa");
		final TMAContactWrapper tc3 = new TMAContactWrapper("sola", "tracla",
				new HiResDate(9 * 60000), 21d, 23d, 15d, 12.2, 0d, Color.red, "bb",
				new EllipseShape(null, 19, new WorldDistance(29, WorldDistance.DEGS),
						new WorldDistance(32, WorldDistance.DEGS)), "aa");
		final TMAContactWrapper tc4 = new TMAContactWrapper("sola", "tracla",
				new HiResDate(16 * 60000), 14d, 14d, 22d, 12.2, 0d, Color.red, "bb",
				new EllipseShape(null, 12, new WorldDistance(22, WorldDistance.DEGS),
						new WorldDistance(12, WorldDistance.DEGS)), "aa");

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
		while (data.hasMoreElements())
		{
			final Object nextO = data.nextElement();
			if (nextO instanceof SegmentList)
				sl = (SegmentList) nextO;
		}

		// check it's got the segs
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 15, tw.numFixes());
		// check we've got all the sensor data
		assertEquals("has all sensor cuts", 4,
				countCuts(tw.getSensors().elements()));
		assertEquals("has all tma cuts", 4, countSolutions(tw.getSolutions()
				.elements()));

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(30 * 1000l));

		// how was it?
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 49, tw.numFixes());
		assertEquals("has all sensor cuts", 7,
				countCuts(tw.getSensors().elements()));
		assertEquals("has all tma cuts", 4, countSolutions(tw.getSolutions()
				.elements()));

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(4 * 60000));

		// how was it?
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 7, tw.numFixes());
		assertEquals("has all resampled sensor cuts", 2, countCuts(tw.getSensors()
				.elements()));
		assertEquals("has all tma cuts", 3, countSolutions(tw.getSolutions()
				.elements()));

	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#elements()}.
	 */

	public void testElements()
	{
		Enumeration<Editable> list = _tw.elements();
		_ctr = 0;
		while (list.hasMoreElements())
		{
			_ctr++;
			list.nextElement();
		}
		
		final int startCount = _ctr;
		
		// give it a little more data
		final SensorWrapper sw = new SensorWrapper("sensor a");
		sw.add(new SensorContactWrapper("trk", new HiResDate(12), null, null, null,
				null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(13), null, null, null,
				null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(14), null, null, null,
				null, null, 0, null));
		_tw.add(sw);

		list = _tw.elements();
		_ctr = 0;
		while (list.hasMoreElements())
		{
			_ctr++;
			list.nextElement();
		}
		assertEquals("shows new item", startCount + 1, _ctr);
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#filterListTo(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */

	public void testFilterListTo()
	{
		assertEquals("start off with them all visible", 6, countVisibleItems());
		_tw.filterListTo(new HiResDate(200000), new HiResDate(400000));
		assertEquals("start off with them all visible", 3, countVisibleItems());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#splitTrack(Debrief.Wrappers.FixWrapper, boolean)}
	 * .
	 */

	public void testFixAsVector()
	{
		final TrackSegment ts = new TrackSegment();
		final long period = 1000 * 60 * 60;
		final double speedKts = 60;
		WorldVector res = ts.vectorFor(period, speedKts, 0);
		assertEquals("Correct course", 0, res.getBearing(), 0.001);
		assertEquals("Correct distance", 1, res.getRange(), 0.001);
		res = ts.vectorFor(period, speedKts, 0.6);
		assertEquals("Correct course", 0.6, res.getBearing(), 0.001);
		assertEquals("Correct distance", 1, res.getRange(), 0.001);
		double easyCourse = MWC.Algorithms.Conversions.Degs2Rads(80);
		res = ts.vectorFor(period, speedKts, easyCourse);
		assertEquals("Correct course", easyCourse, res.getBearing(), 0.001);
		assertEquals("Correct distance", 1, res.getRange(), 0.001);
		easyCourse = MWC.Algorithms.Conversions.Degs2Rads(280);
		res = ts.vectorFor(period, speedKts, easyCourse);
		assertEquals("Correct course", easyCourse, res.getBearing(), 0.001);
		assertEquals("Correct distance", 1, res.getRange(), 0.001);
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#fixMoved()}.
	 */

	public void testFixMoved()
	{
		// now something else
		_ctr = 0;
		final SensorWrapper sw = new SensorWrapper("some sensor")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void setHost(final TrackWrapper host)
			{
				super.setHost(host);
				_ctr++;
			}
		};
		SensorContactWrapper scw = new SensorContactWrapper();
		scw.setDTG(new HiResDate(1000));
		sw.add(scw);
		_tw.add(sw);

		// it should only have been fired once
		assertEquals("only called to tell sensor of it's ownership", 1, _ctr);

		// tell the track it's moved
		_tw.fixMoved();

		// did it hear?
		assertEquals("got informed of sensor movement", 2, _ctr);

	}

	public void testGenInfill()
	{
		final TrackSegment ts1 = new TrackSegment();
		final TrackSegment ts2 = new TrackSegment();

		ts1.addFix(createFix(1000, 1, 0, 5d, 1, 0, 00d, 135, 12));
		ts1.addFix(createFix(2000, 1, 0, 4d, 1, 0, 01d, 135, 12));
		ts1.addFix(createFix(3000, 1, 0, 3d, 1, 0, 02d, 135, 12));
		ts1.addFix(createFix(4000, 1, 0, 2d, 1, 0, 03d, 135, 12));

		ts2.addFix(createFix(80000, 1, 0, 0d, 1, 0, 07d, 90, 12));
		ts2.addFix(createFix(90000, 1, 0, 0d, 1, 0, 08d, 90, 12));
		ts2.addFix(createFix(100000, 1, 0, 0d, 1, 0, 09d, 90, 12));
		ts2.addFix(createFix(110000, 1, 0, 0d, 1, 0, 10d, 90, 12));
		ts2.addFix(createFix(120000, 1, 0, 0d, 1, 0, 11d, 90, 12));
		ts2.addFix(createFix(130000, 1, 0, 0d, 1, 0, 12d, 90, 12));

		// the test was broken after adding this line to the TrackSegment
		// constructor:
		// 458: tDelta = Math.max(tDelta, 10000);
		// tDelta is the time loop step
		// Fixed the test: increased second track times.
		final TrackSegment infill = new DynamicInfillSegment(ts1, ts2);

		// check there are the correct number of items
		assertEquals("wrong num entries", 7, infill.size());

	}

	public void testGenInfillSpeed()
	{
		final TrackSegment ts1 = new TrackSegment();
		final TrackSegment ts2 = new TrackSegment();

		ts1.addFix(createFix(1000, 1, 0, 5d, 1, 0, 00d, 135, 12));
		ts1.addFix(createFix(2000, 1, 0, 4d, 1, 0, 01d, 135, 12));
		ts1.addFix(createFix(3000, 1, 0, 3d, 1, 0, 02d, 135, 12));
		ts1.addFix(createFix(4000, 1, 0, 2d, 1, 0, 03d, 135, 12));

		ts2.addFix(createFix(80000, 1, 0, 0d, 1, 0, 07d, 90, 12));
		ts2.addFix(createFix(90000, 1, 0, 0d, 1, 0, 08d, 90, 12));
		ts2.addFix(createFix(100000, 1, 0, 0d, 1, 0, 09d, 90, 12));
		ts2.addFix(createFix(110000, 1, 0, 0d, 1, 0, 10d, 90, 12));
		ts2.addFix(createFix(120000, 1, 0, 0d, 1, 0, 11d, 90, 12));
		ts2.addFix(createFix(130000, 1, 0, 0d, 1, 0, 12d, 90, 12));

		// the test was broken after adding this line to the TrackSegment
		// constructor:
		// 458: tDelta = Math.max(tDelta, 10000);
		// tDelta is the time loop step
		// Fixed the test: increased second track times.
		final TrackSegment infill = new DynamicInfillSegment(ts1, ts2);

		// check there are the correct number of items
		assertEquals("wrong num entries", 7, infill.size());

		// Enumeration<Editable> numer = infill.elements();
		// double minCourse = Math.toRadians(90);
		// double maxCourse = Math.toRadians(135);
		//
		// while (numer.hasMoreElements())
		// {
		// FixWrapper thisF = (FixWrapper) numer.nextElement();
		//
		// // TODO: investigate splines to ensure the turn only include decrease or
		// // increase in speed, not both (Github Issue# 664)
		//
		// // check they maintain the speed
		// // assertEquals("correct speed", 12, thisF.getSpeed(), 0.002);
		//
		// // check the speed doesn't go outside the provided range
		// // assertTrue("correct course", thisF.getCourse() >= minCourse);
		// // assertTrue("correct course", thisF.getCourse() <= maxCourse);
		// }

	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getBounds()}.
	 */
	public void testGetBounds()
	{
		final WorldArea correctBounds = new WorldArea(new WorldLocation(1, 1, 0),
				new WorldLocation(4, 6, 0));
		assertEquals("wrong bounds returned", correctBounds, _tw.getBounds());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getEndDTG()}.
	 */

	public void testGetEndDTG()
	{
		final HiResDate dt = _tw.getEndDTG();
		assertEquals("correct end time", 600000, dt.getDate().getTime());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#getItemsBetween(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */

	public void testGetItemsBetween()
	{
		assertEquals("found the items", 2,
				_tw.getItemsBetween(new HiResDate(200003), new HiResDate(400003))
						.size());
		assertEquals("found the items", 3,
				_tw.getItemsBetween(new HiResDate(0), new HiResDate(300000)).size());
		assertEquals("found the items", 6,
				_tw.getItemsBetween(new HiResDate(100000), new HiResDate(800000))
						.size());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getName()}.
	 */
	public void testGetName()
	{
		assertEquals("correct name", TRACK_NAME, _tw.getName());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#getNearestTo(MWC.GenericData.HiResDate)}
	 * .
	 */

	public void testGetNearestTo()
	{
		Watchable[] res = _tw.getNearestTo(new HiResDate(300000));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", _fw1, res[0]);

		res = _tw.getNearestTo(new HiResDate(500000));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", _fw2, res[0]);

		res = _tw.getNearestTo(new HiResDate(400005));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", _fw2, res[0]);

		res = _tw.getNearestTo(new HiResDate(500000));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", _fw2, res[0]);

		res = _tw.getNearestTo(new HiResDate(299995));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", _fw1, res[0]);
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getPositions()}.
	 */

	public void testGetPositions()
	{
		final Enumeration<Editable> list = _tw.getPositions();
		// check the length
		_ctr = 0;
		while (list.hasMoreElements())
		{
			list.nextElement();
			_ctr++;
		}
		assertEquals("have correct number of elements", 6, _ctr);
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getStartDTG()}.
	 */

	public void testGetStartDTG()
	{
		final HiResDate dt = _tw.getStartDTG();
		assertEquals("correct end time", 100000, dt.getDate().getTime());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#getUnfilteredItems(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */

	public void testGetUnfilteredItems()
	{
		assertEquals("found right num ", 3,
				_tw.getUnfilteredItems(new HiResDate(100000), new HiResDate(300000))
						.size());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#numFixes()}.
	 */

	public void testNumFixes()
	{
		assertEquals("have correct num", 6, _tw.numFixes());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#rangeFrom(MWC.GenericData.WorldLocation)}
	 * .
	 */

	public void testRangeFromWorldLocation()
	{
		final WorldLocation wl = new WorldLocation(4, 1, 0);
		final double res = _tw.rangeFrom(wl);
		assertEquals("correct range", 0d, res, 0.001);
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#removeElement(MWC.GUI.Editable)}.
	 */

	public void testRemoveElement()
	{
		assertEquals("all there", 6, _tw.numFixes());
		_tw.removeElement(_fw1);
		assertEquals("one less now", 5, _tw.numFixes());
		_tw.removeElement(_fw2);
		assertEquals("one less now", 4, _tw.numFixes());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#shiftTrack(java.util.Enumeration, MWC.GenericData.WorldVector)}
	 * .
	 */

	public void testShiftTrack()
	{
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#shift(MWC.GenericData.WorldLocation, MWC.GenericData.WorldVector)}
	 * .
	 */

	public void testShiftWorldLocationWorldVector()
	{
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#shift(MWC.GenericData.WorldVector)}.
	 */

	public void testShiftWorldVector()
	{
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#splitTrack(Debrief.Wrappers.FixWrapper, boolean)}
	 * .
	 */

	public void testSplitTrack1()
	{
		final Vector<TrackSegment> segs = _tw.splitTrack(_fw1, true);
		assertEquals("now two segments", 2, segs.size());
		assertEquals("first is of correct length", 2, segs.firstElement().size());
		assertEquals("first is of correct length", 4, segs.lastElement().size());

		// check the names.
		SegmentList list = _tw.getSegments();
		Enumeration<Editable> segments = list.elements();
		TrackSegment s1 = (TrackSegment) segments.nextElement();
		TrackSegment s2 = (TrackSegment) segments.nextElement();
		assertEquals("correct layer name:", "010001.40", s1.getName());
		assertEquals("correct layer name:", "010005.00", s2.getName());

		// split it again
		final Vector<TrackSegment> segs2 = _tw.splitTrack(_fw2, true);
		assertEquals("two blocks returned", 2, segs2.size());
		assertEquals("has 3 segments", 3, numSegments());
		assertEquals("first is of correct length", 2, segs2.firstElement().size());
		assertEquals("first is of correct length", 2, segs2.lastElement().size());

		list = (SegmentList) _tw.getSegments();
		segments = list.elements();
		s1 = (TrackSegment) segments.nextElement();
		s2 = (TrackSegment) segments.nextElement();
		final TrackSegment s3 = (TrackSegment) segments.nextElement();
		assertEquals("correct layer name:", "010001.40", s1.getName());
		assertEquals("correct layer name:", "010005.00", s2.getName());
		assertEquals("correct layer name:", "010008.20", s3.getName());

		// now recombine them
		_tw.combineSections(segs2);
		assertEquals("has 2 segments", 2, numSegments());
		assertEquals("first is of correct length", 2, segs.firstElement().size());
		assertEquals("first is of correct length", 4, segs.lastElement().size());

		list = (SegmentList) _tw.getSegments();
		segments = list.elements();
		s1 = (TrackSegment) segments.nextElement();
		s2 = (TrackSegment) segments.nextElement();
		assertEquals("correct layer name:", "010001.40", s1.getName());
		assertEquals("correct layer name:", "010005.00", s2.getName());

		_tw.combineSections(segs);
		assertEquals("has 1 segment1", 1, numSegments());
		assertEquals("first is of correct length", 6, segs.firstElement().size());

	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#splitTrack(Debrief.Wrappers.FixWrapper, boolean)}
	 * .
	 */

	public void testSplitTrack2()
	{
		final Vector<TrackSegment> segs = _tw.splitTrack(_fw1, false);
		assertEquals("now two segments", 2, segs.size());
		assertEquals("first is of correct length", 3, segs.firstElement().size());
		assertEquals("first is of correct length", 3, segs.lastElement().size());
	}

	public void testSegmentList()
	{

		final TrackWrapper tw = new TrackWrapper();

		final TrackSegment ts0 = new TrackSegment();
		ts0.addFix(createFix(10000, 1, 1, 20, 30));
		ts0.addFix(createFix(11000, 1, 1, 20, 30));
		ts0.addFix(createFix(12000, 1, 1, 20, 30));
		ts0.addFix(createFix(13000, 1, 1, 20, 30));

		final TrackSegment ts1 = new TrackSegment();
		ts1.addFix(createFix(14000, 1, 1, 20, 30));
		ts1.addFix(createFix(15000, 1, 1, 20, 30));
		ts1.addFix(createFix(16000, 1, 1, 20, 30));
		ts1.addFix(createFix(17000, 1, 1, 20, 30));

		final TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(18000, 1, 1, 20, 30));
		ts2.addFix(createFix(19000, 1, 1, 20, 30));
		ts2.addFix(createFix(20000, 1, 1, 20, 30));
		ts2.addFix(createFix(21000, 1, 1, 20, 30));

		tw.add(ts0);
		tw.add(ts1);
		tw.add(ts2);

		SegmentList sList = tw.getSegments();
		TrackSegment i1 = sList.getSegmentFor(16000);
		assertEquals("correct segment", ts1, i1);
		i1 = sList.getSegmentFor(17000);
		assertEquals("correct segment", ts1, i1);
		i1 = sList.getSegmentFor(14000);
		assertEquals("correct segment", ts1, i1);

		TrackSegment i0 = sList.getSegmentFor(10000);
		assertEquals("correct segment", ts0, i0);
		i0 = sList.getSegmentFor(13000);
		assertEquals("correct segment", ts0, i0);
		i0 = sList.getSegmentFor(11000);
		assertEquals("correct segment", ts0, i0);

		TrackSegment i2 = sList.getSegmentFor(18000);
		assertEquals("correct segment", ts2, i2);
		i2 = sList.getSegmentFor(21000);
		assertEquals("correct segment", ts2, i2);

	}

	/**
	 * .
	 */

	public void testTMASegment()
	{
		// //////////////////////////////////
		// start off building from a track
		// //////////////////////////////////
		final TrackWrapper tw = new TrackWrapper();

		final FixWrapper f1 = createFix(100000, 1, 1, 4, 12);
		final FixWrapper f2 = createFix(200000, 2, 3, 4, 12);
		tw.addFix(createFix(80000, 3, 3, 4, 12));
		tw.addFix(f1);
		tw.addFix(f2);
		tw.addFix(createFix(300000, 3, 3, 4, 12));
		tw.addFix(createFix(400000, 4, 6, 4, 12));

		final WorldVector offset = new WorldVector(12, 12, 0);
		final WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);
		final double course = 33;

		// check the before
		FixWrapper firstFix = null;

		// ////////////////////////
		// NOW FROM A SENSOR WRAPPER
		// /////////////////////////
		final SensorWrapper sw = new SensorWrapper("some sensor");
		sw.setHost(tw);
		sw.add(createSensorItem(tw, sw, 110000));
		sw.add(createSensorItem(tw, sw, 120000));
		sw.add(createSensorItem(tw, sw, 130000));
		sw.add(createSensorItem(tw, sw, 140000));
		final CoreTMASegment seg1 = new RelativeTMASegment(sw, offset, speed,
				course, null);

		// check the create worked
		assertEquals("enough points created", 4, seg1.size());

		// check the before
		firstFix = (FixWrapper) seg1.getData().iterator().next();
		assertEquals("correct course before", 33, seg1.getCourse(), 0.001);
		assertEquals("correct speed before", 5,
				seg1.getSpeed().getValueIn(WorldSpeed.Kts), 0.001);
		assertEquals("correct course before", 33,
				MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001);
		assertEquals("correct speed before", 5, firstFix.getSpeed(), 0.001);

		seg1.setCourse(35);
		seg1.setSpeed(new WorldSpeed(15, WorldSpeed.Kts));

		assertEquals("correct course after", 35, seg1.getCourse(), 0.001);
		assertEquals("correct speed after", 15,
				seg1.getSpeed().getValueIn(WorldSpeed.Kts), 0.001);
		assertEquals("correct course after", 35,
				MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001);
		assertEquals("correct speed after", 15, firstFix.getSpeed(), 0.001);

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
		for (int i = 0; i < items.length; i++)
		{
			final SensorContactWrapper sensorContactWrapper = items[i];
			sensorContactWrapper.setSensor(sw);
		}

		final RelativeTMASegment seg2 = new RelativeTMASegment(items, offset,
				speed, course, null);

		// check the create worked
		assertEquals("enough points created", 5, seg2.size());

		final Iterator<Editable> someIt = seg2.getData().iterator();
		// check the before
		firstFix = (FixWrapper) someIt.next();
		assertEquals("correct course before", 33, seg2.getCourse(), 0.001);
		assertEquals("correct speed before", 5,
				seg2.getSpeed().getValueIn(WorldSpeed.Kts), 0.001);
		assertEquals("correct course before", 33,
				MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001);
		assertEquals("correct speed before", 5, firstFix.getSpeed(), 0.001);

		// check the next dtg
		firstFix = (FixWrapper) someIt.next();
		assertEquals("check dtg produced", 115000, firstFix.getDTG().getDate()
				.getTime(), 0.001);
		firstFix = (FixWrapper) someIt.next();
		assertEquals("check dtg produced", 119000, firstFix.getDTG().getDate()
				.getTime(), 0.001);
		firstFix = (FixWrapper) someIt.next();
		assertEquals("check dtg produced", 141000, firstFix.getDTG().getDate()
				.getTime(), 0.001);
		firstFix = (FixWrapper) someIt.next();
		assertEquals("check dtg produced", 150000, firstFix.getDTG().getDate()
				.getTime(), 0.001);

		seg2.setCourse(35);
		seg2.setSpeed(new WorldSpeed(15, WorldSpeed.Kts));

		assertEquals("correct course after", 35, seg2.getCourse(), 0.001);
		assertEquals("correct speed after", 15,
				seg2.getSpeed().getValueIn(WorldSpeed.Kts), 0.001);
		assertEquals("correct course after", 35,
				MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001);
		assertEquals("correct speed after", 15, firstFix.getSpeed(), 0.001);

		// check that new points get added as we extend the solution
		assertEquals("start with correct points", 5, seg2.size());

		seg2.setDTG_End(new HiResDate(200000));
		assertEquals("more points after stretch", 11, seg2.size());
		assertEquals("new end time", 200000, seg2.getDTG_End().getDate().getTime());

		// now try to stretch the start

		seg2.setDTG_Start(new HiResDate(80002));
		assertEquals("new start time", 80002, seg2.getDTG_Start().getDate()
				.getTime());
		assertEquals("more points after stretch", 17, seg2.size());

		// have a look at the times
		Iterator<Editable> sIter = seg2.getData().iterator();
		while (sIter.hasNext())
		{
			FixWrapper fw = (FixWrapper) sIter.next();
			System.out.println(fw.getDTG().getDate().getTime());
		}

	}

	/**
	 * .
	 */

	public void testTMASegmentRotate()
	{

		final FixWrapper f1 = createFix(100000, 1, 1, 270, 12);
		final FixWrapper f2 = createFix(200000, 1, 0, 270, 12);
		final WorldVector vector = new WorldVector(0, 1, 0);
		final RelativeTMASegment ts = new RelativeTMASegment(270, new WorldSpeed(
				12, WorldSpeed.Kts), vector, null)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public WorldLocation getHostLocation()
			{
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
		assertEquals("at new offset bearing", -90, ts.getOffsetBearing(), 0.001);
		assertEquals("at new offset range", 1,
				ts.getOffsetRange().getValueIn(WorldDistance.DEGS), 0.001);
		assertEquals("on new course", 0, ts.getCourse(), 0.001);
		assertEquals("at original speed", 12,
				ts.getSpeed().getValueIn(WorldSpeed.Kts), 0.001);

		// ok, try to turn back!
		ts.rotate(-brg, origin);

		// check we're on the new course
		assertEquals("at new offset bearing", 0, ts.getOffsetBearing(), 0.001);
		assertEquals("at new offset range", 1,
				ts.getOffsetRange().getValueIn(WorldDistance.DEGS), 0.001);
		assertEquals("on new course", -90, ts.getCourse(), 0.001);
		assertEquals("at original speed", 12,
				ts.getSpeed().getValueIn(WorldSpeed.Kts), 0.001);
	}

	/**
	 * .
	 */

	public void testTMASegmentStretch()
	{
		final FixWrapper f1 = createFix(0, 1, 1, 270, 12);
		final FixWrapper f2 = createFix(1000 * 60 * 60, 1, 0, 270, 12);
		final WorldVector vector = new WorldVector(0, 1, 0);
		final RelativeTMASegment ts = new RelativeTMASegment(270, new WorldSpeed(
				12, WorldSpeed.Kts), vector, null)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public WorldLocation getHostLocation()
			{
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
		assertEquals("at new offset bearing", 0, ts.getOffsetBearing(), 0.001);
		assertEquals("at new offset range", 1,
				ts.getOffsetRange().getValueIn(WorldDistance.DEGS), 0.001);
		assertEquals("on new course", 270, ts.getCourse(), 1);
		assertEquals("at new speed", 120, ts.getSpeed().getValueIn(WorldSpeed.Kts),
				0.001);

		// ok, try to turn back!
		ts.rotate(-rng, origin);

		// check we're on the new course
		assertEquals("at new offset bearing", 0, ts.getOffsetBearing(), 0.001);
		assertEquals("at new offset range", 1,
				ts.getOffsetRange().getValueIn(WorldDistance.DEGS), 0.001);
		assertEquals("on new course", 385, ts.getCourse(), 1);
		assertEquals("at original speed", 120,
				ts.getSpeed().getValueIn(WorldSpeed.Kts), 0.001);
	}

	// ////////////

	/**
	 * .
	 */

	public void testTMASplit()
	{
		// //////////////////////////////////
		// start off building from a track
		// //////////////////////////////////
		final TrackWrapper tw = new TrackWrapper();

		tw.addFix(createFix(100000, 1, 1, 4, 12));
		tw.addFix(createFix(200000, 2, 3, 4, 12));
		tw.addFix(createFix(300000, 3, 3, 4, 12));
		tw.addFix(createFix(400000, 4, 6, 4, 12));
		tw.addFix(createFix(500000, 4, 6, 4, 12));
		tw.addFix(createFix(600000, 4, 6, 4, 12));
		tw.addFix(createFix(700000, 4, 6, 4, 12));

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
		sw.add(createSensorItem(tw, sw, 110000));
		sw.add(createSensorItem(tw, sw, 120000));
		sw.add(createSensorItem(tw, sw, 130000));
		sw.add(createSensorItem(tw, sw, 140000));
		sw.add(createSensorItem(tw, sw, 150000));
		sw.add(createSensorItem(tw, sw, 160000));
		sw.add(createSensorItem(tw, sw, 170000));
		seg = new RelativeTMASegment(sw, offset, speed, course, null);

		// check the create worked
		assertEquals("enough points created", 7, seg.size());

		// check the before
		firstFix = (FixWrapper) seg.getData().iterator().next();
		assertEquals("correct course before", 33, seg.getCourse(), 0.001);
		assertEquals("correct speed before", 5,
				seg.getSpeed().getValueIn(WorldSpeed.Kts), 0.001);
		assertEquals("correct course before", 33,
				MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001);
		assertEquals("correct speed before", 5, firstFix.getSpeed(), 0.001);

		// ok, now do the split
		final TrackWrapper segW = new TrackWrapper();
		segW.setName("TMA");
		segW.add(seg);

		// get hold of an item in the segment
		final Enumeration<Editable> enumer = seg.elements();
		enumer.nextElement();
		enumer.nextElement();
		final FixWrapper fw = (FixWrapper) enumer.nextElement();
		assertNotNull("Found a fix", fw);

		// do the split
		final Vector<TrackSegment> segs = segW.splitTrack(fw, false);

		// check we have enough segments
		assertEquals("now two segments", 2, segs.size());
		assertEquals("first is of correct length", 3, segs.firstElement().size());
		assertEquals("first is of correct length", 4, segs.lastElement().size());

		// check they're of the correct type
		final TrackSegment seg1 = segs.firstElement();
		final TrackSegment seg2 = segs.lastElement();
		assertTrue(" is a tma segment", seg1 instanceof RelativeTMASegment);
		assertTrue(" is a tma segment", seg2 instanceof RelativeTMASegment);

	}

	/**
	 * .
	 */

	public void testTMASplit2()
	{
		// //////////////////////////////////
		// start off building from a track
		// //////////////////////////////////
		final TrackWrapper tw = new TrackWrapper();

		tw.addFix(createFix(100000, 1, 1, 4, 12));
		tw.addFix(createFix(200000, 2, 3, 4, 12));
		tw.addFix(createFix(300000, 3, 3, 4, 12));
		tw.addFix(createFix(400000, 4, 6, 4, 12));
		tw.addFix(createFix(500000, 4, 6, 4, 12));
		tw.addFix(createFix(600000, 4, 6, 4, 12));
		tw.addFix(createFix(700000, 4, 6, 4, 12));

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
		assertEquals("enough points created", 7, seg.size());

		// check the before
		firstFix = (FixWrapper) seg.getData().iterator().next();
		assertEquals("correct course before", 33, seg.getCourse(), 0.001);
		assertEquals("correct speed before", 5,
				seg.getSpeed().getValueIn(WorldSpeed.Kts), 0.001);
		assertEquals("correct course before", 33,
				MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001);
		assertEquals("correct speed before", 5, firstFix.getSpeed(), 0.001);

		// ok, now do the split
		final TrackWrapper segW = new TrackWrapper();
		segW.setName("TMA");
		segW.add(seg);

		// get hold of an item in the segment
		final Enumeration<Editable> enumer = seg.elements();
		enumer.nextElement();
		enumer.nextElement();
		final FixWrapper fw = (FixWrapper) enumer.nextElement();
		assertNotNull("Found a fix", fw);

		// do the split
		final Vector<TrackSegment> segs = segW.splitTrack(fw, false);

		// check we have enough segments
		assertEquals("now two segments", 2, segs.size());
		assertEquals("first is of correct length", 3, segs.firstElement().size());
		assertEquals("first is of correct length", 4, segs.lastElement().size());

		// check they're of the correct type
		final TrackSegment seg1 = segs.firstElement();
		final TrackSegment seg2 = segs.lastElement();
		assertTrue(" is a tma segment", seg1 instanceof AbsoluteTMASegment);
		assertTrue(" is a tma segment", seg2 instanceof AbsoluteTMASegment);

	}

	public void testTrackGroup1()
	{
		final TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(310000, 32, 33));
		ts2.addFix(createFix(311000, 32, 33));
		ts2.addFix(createFix(312000, 32, 33));
		ts2.addFix(createFix(313000, 32, 33));
		ts2.addFix(createFix(314000, 32, 33));
		final TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());

		// do a merge
		final Layer[] parents = new Layer[]
		{ _tw, tw3 };
		final Editable[] subjects = new Editable[]
		{ _tw, ts2 };
		TrackWrapper.groupTracks(_tw, theLayers, parents, subjects);

		// have a look at the results
		assertEquals("track 3 is longer", 11, _tw.numFixes());

		// check it's been a group, not an add
		_ctr = 0;
		final SegmentList sl = (SegmentList) _tw.getSegments();
		final Enumeration<Editable> segments = sl.elements();
		while (segments.hasMoreElements())
		{
			_ctr++;
			segments.nextElement();
		}
		assertEquals("track _tw has several segments", 2, _ctr);

		assertEquals("track got ditched", 1, theLayers.size());
	}

	public void testTrackGroupOrder()
	{
		final TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(310000, 32, 33));
		ts2.addFix(createFix(311000, 32, 33));
		ts2.addFix(createFix(312000, 32, 33));
		ts2.addFix(createFix(313000, 32, 33));
		ts2.addFix(createFix(314000, 32, 33));

		final TrackSegment ts3 = new TrackSegment();
		ts3.addFix(createFix(410000, 32, 33));
		ts3.addFix(createFix(411000, 32, 33));
		ts3.addFix(createFix(412000, 32, 33));
		ts3.addFix(createFix(413000, 32, 33));
		ts3.addFix(createFix(414000, 32, 33));

		TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		tw3.add(ts3);

		SegmentList list = (SegmentList) tw3.getSegments();
		Enumeration<Editable> segments = list.elements();
		TrackSegment seg1 = (TrackSegment) segments.nextElement();
		TrackSegment seg2 = (TrackSegment) segments.nextElement();

		assertEquals("correct one is first", ts2, seg1);
		assertEquals("correct one is segment", ts3, seg2);

		// try in another list
		assertEquals("correct one is first", ts2, seg1);
		assertEquals("correct one is segment", ts3, seg2);

		tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		tw3.add(ts3);

		list = (SegmentList)tw3.getSegments();
		segments = list.elements();
		seg1 = (TrackSegment) segments.nextElement();
		seg2 = (TrackSegment) segments.nextElement();
	}

	public void testTrackMerge1()
	{
		final TrackSegment ts2 = new TrackSegment();
		ts2.setName("ts2");
		ts2.addFix(createFix(910000, 32, 33));
		ts2.addFix(createFix(911000, 32, 33));
		ts2.addFix(createFix(912000, 32, 33));
		ts2.addFix(createFix(913000, 32, 33));
		ts2.addFix(createFix(914000, 32, 33));
		final TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());
		assertEquals("fix starts in place", "test track", _fw1.getTrackWrapper()
				.getName());

		// do a merge
		final Editable[] subjects = new Editable[]
		{ _tw, ts2 };
		final TrackWrapper newTarget = new TrackWrapper();
		newTarget.setName("Merged");

		TrackWrapper.mergeTracks(newTarget, theLayers, subjects);

		// have a look at the results
		assertEquals("new track got created", 3, theLayers.size());
		assertEquals("track 3 is longer", 11, newTarget.numFixes());
		assertEquals("new track has correct name", "Merged", newTarget.getName());
		assertEquals("original fix still in place", "test track", _fw1
				.getTrackWrapper().getName());
	}

	public void testTrackMerge2()
	{
		final TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(910000, 32, 33));
		ts2.addFix(createFix(911000, 32, 33));
		ts2.addFix(createFix(912000, 32, 33));
		ts2.addFix(createFix(913000, 32, 33));
		ts2.addFix(createFix(914000, 32, 33));
		final TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());

		// do a merge
		final Editable[] subjects = new Editable[]
		{ _tw, ts2 };
		TrackWrapper.mergeTracks(_tw, theLayers, subjects);

		// have a look at the results
		assertEquals("track is longer", 11, _tw.numFixes());
		assertEquals("track got ditched", 2, theLayers.size());
		final TrackSegment sl = (TrackSegment) _tw.getSegments().elements()
				.nextElement();
		assertEquals("just the one segment - with all our points", 11, sl.size());

	}

	public void testTrackMerge3()
	{
		final TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(310000, 32, 33));
		ts2.addFix(createFix(311000, 32, 33));
		ts2.addFix(createFix(312000, 32, 33));
		ts2.addFix(createFix(313000, 32, 33));
		ts2.addFix(createFix(314000, 32, 33));
		final TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());

		// do a merge
		final Editable[] subjects = new Editable[]
		{ _tw, ts2 };
		TrackWrapper.mergeTracks(_tw, theLayers, subjects);

		// have a look at the results
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());

		// check the error message got thrown
		assertEquals("have error", 1, _messages._messages.size());
		assertEquals("correct title", "Merge tracks",
				_messages._titles.firstElement());
		assertEquals(
				"correct title",
				"Sorry, 'Positions' and 'test track' overlap in time. Please correct this and retry",
				_messages._messages.firstElement());
		assertEquals("correct title", MessageProvider.ERROR,
				(int) _messages._statuses.firstElement());
	}

	public void testTrackMergeAllSegments()
	{
		final TrackSegment ts1 = new TrackSegment();
		ts1.addFix(createFix(110000, 32, 33));
		ts1.addFix(createFix(111000, 32, 33));
		ts1.addFix(createFix(112000, 32, 33));
		ts1.addFix(createFix(113000, 32, 33));
		ts1.addFix(createFix(114000, 32, 33));

		final TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(210000, 32, 33));
		ts2.addFix(createFix(211000, 32, 33));
		ts2.addFix(createFix(212000, 32, 33));
		ts2.addFix(createFix(213000, 32, 33));
		ts2.addFix(createFix(214000, 32, 33));

		final TrackSegment ts3 = new TrackSegment();
		ts3.addFix(createFix(910000, 32, 33));
		ts3.addFix(createFix(911000, 32, 33));
		ts3.addFix(createFix(912000, 32, 33));
		ts3.addFix(createFix(913000, 32, 33));
		ts3.addFix(createFix(914000, 32, 33));

		final TrackWrapper tw = new TrackWrapper();
		tw.add(ts1);
		tw.add(ts2);
		tw.add(ts3);

		Editable[] items = new TrackSegment[3];
		int ctr = 0;
		items[ctr++] = ts1;
		items[ctr++] = ts2;
		items[ctr++] = ts3;

		TrackWrapper newTrack = new TrackWrapper();
		newTrack.setName("Merged");
		Layers theLayers = new Layers();

		TrackWrapper.mergeTracks(newTrack, theLayers, items);
		// do the merge

		assertEquals("has all fixes", 15, newTrack.numFixes());

		assertEquals("correct start time", 110000, tw.getStartDTG().getDate()
				.getTime());
		assertEquals("correct end time", 914000, tw.getEndDTG().getDate().getTime());

	}

	public void testTrackStartEnd()
	{
		final TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(910000, 32, 33));
		ts2.addFix(createFix(911000, 32, 33));
		ts2.addFix(createFix(912000, 32, 33));
		ts2.addFix(createFix(913000, 32, 33));
		ts2.addFix(createFix(914000, 32, 33));
		final TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());

		// do a merge
		final Editable[] subjects = new Editable[]
		{ _tw, ts2 };

		TrackWrapper result = new TrackWrapper();
		result.setName("Merged");

		TrackWrapper.mergeTracks(result, theLayers, subjects);

		// have a look at the results
		assertEquals("track 3 is longer", 11, result.numFixes());
		assertEquals("track got created", 3, theLayers.size());
		assertEquals("fix has new parent", "test track", _fw1.getTrackWrapper()
				.getName());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#visibleBetween(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */

	public void testVisibleBetween()
	{
		assertEquals("is vis", false,
				_tw.visibleBetween(new HiResDate(700000), new HiResDate(900000)));
		assertEquals("is vis", true,
				_tw.visibleBetween(new HiResDate(000000), new HiResDate(300000)));
		assertEquals("is vis", true,
				_tw.visibleBetween(new HiResDate(300000), new HiResDate(500000)));
	}

	private int trackLength()
	{
		final Enumeration<Editable> all = _tw.contiguousElements();
		int ctr = 0;
		while (all.hasMoreElements())
		{
			ctr++;
			all.nextElement();
		}
		return ctr;
	}

	@SuppressWarnings("synthetic-access")
	private int countVisibleFixes(final TrackWrapper tw)
	{
		int ctr = 0;
		final Enumeration<Editable> iter = tw.getPositions();
		while (iter.hasMoreElements())
		{
			final Plottable thisE = (Plottable) iter.nextElement();
			if (thisE.getVisible())
			{
				ctr++;
			}
		}
		return ctr;
	}

	@SuppressWarnings("synthetic-access")
	private int countVisibleSensorWrappers(final TrackWrapper tw)
	{
		final Enumeration<Editable> iter2 = tw.getSensors().elements();
		int sCtr = 0;
		while (iter2.hasMoreElements())
		{
			final SensorWrapper sw = (SensorWrapper) iter2.nextElement();
			final Enumeration<Editable> enumS = sw.elements();
			while (enumS.hasMoreElements())
			{
				final Plottable pl = (Plottable) enumS.nextElement();
				if (pl.getVisible())
				{
					sCtr++;
				}
			}
		}
		return sCtr;
	}

	@SuppressWarnings("synthetic-access")
	private int countVisibleSolutionWrappers(final TrackWrapper tw)
	{
		final Enumeration<Editable> iter2 = tw.getSolutions().elements();
		int sCtr = 0;
		while (iter2.hasMoreElements())
		{
			final TMAWrapper sw = (TMAWrapper) iter2.nextElement();
			final Enumeration<Editable> enumS = sw.elements();
			while (enumS.hasMoreElements())
			{
				final Plottable pl = (Plottable) enumS.nextElement();
				if (pl.getVisible())
				{
					sCtr++;
				}
			}
		}
		return sCtr;
	}

	public static TrackWrapper getDummyTrack()
	{
		final TrackWrapper tw = new TrackWrapper();
		tw.setName(TRACK_NAME);

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100,
						WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200,
						WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300,
						WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400,
						WorldDistance.METRES), null)), 40, 140));
		fw4.setLabel("fw4");
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500,
						WorldDistance.METRES), null)), 50, 150));
		fw5.setLabel("fw5");
		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);
		// also give it some sensor data
		final SensorWrapper swa = new SensorWrapper("title one");
		final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa",
				new HiResDate(150, 0), null, null, null, null, null, 0, null);
		final SensorContactWrapper scwa2 = new SensorContactWrapper("bbb",
				new HiResDate(180, 0), null, null, null, null, null, 0, null);
		final SensorContactWrapper scwa3 = new SensorContactWrapper("ccc",
				new HiResDate(250, 0), null, null, null, null, null, 0, null);
		swa.add(scwa1);
		swa.add(scwa2);
		swa.add(scwa3);
		tw.add(swa);
		final SensorWrapper sw = new SensorWrapper("title two");
		final SensorContactWrapper scw1 = new SensorContactWrapper("ddd",
				new HiResDate(260, 0), null, null, null, null, null, 0, null);
		final SensorContactWrapper scw2 = new SensorContactWrapper("eee",
				new HiResDate(280, 0), null, null, null, null, null, 0, null);
		final SensorContactWrapper scw3 = new SensorContactWrapper("fff",
				new HiResDate(350, 0), null, null, null, null, null, 0, null);
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
	public final void testFilterToTimePeriod()
	{
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

	public void testGetItemsBetween_Second()
	{
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(0, 1), loc_1,
				0, 0));
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(0, 2), loc_1,
				0, 0));
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(0, 3), loc_1,
				0, 0));
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(0, 4), loc_1,
				0, 0));
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(0, 5), loc_1,
				0, 0));
		final FixWrapper fw6 = new FixWrapper(new Fix(new HiResDate(0, 6), loc_1,
				0, 0));
		final FixWrapper fw7 = new FixWrapper(new Fix(new HiResDate(0, 7), loc_1,
				0, 0));
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

		col = tw.getUnfilteredItems(new HiResDate(0, 3), new HiResDate(0, 5));
		assertEquals("found correct number of items", 2, col.size());

		final Watchable[] pts2 = tw.getNearestTo(new HiResDate(0, 3));
		assertEquals("found something", 1, pts2.length);
		assertEquals("found the third item", fw3, pts2[0]);

		final Watchable[] pts = tw.getNearestTo(new HiResDate(0, 1));
		assertEquals("found something", 1, pts.length);
		assertEquals("found the first item", fw1, pts[0]);

	}

	public final void testGettingTimes()
	{
		// Enumeration<SensorContactWrapper>
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final WorldLocation loc_2 = new WorldLocation(1, 1, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(0, 100), loc_1,
				0, 0));
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(0, 300), loc_2,
				0, 0));
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(0, 500), loc_2,
				0, 0));
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(0, 700), loc_2,
				0, 0));

		// check returning empty data
		Collection<Editable> coll = tw.getItemsBetween(new HiResDate(0, 0),
				new HiResDate(0, 40));
		assertEquals("Return empty when empty", coll, null);

		tw.addFix(fw1);

		// check returning single field
		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
		assertEquals("Return empty when out of range", coll, null);

		coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0, 540));
		assertEquals("Return empty when out of range", coll, null);

		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 140));
		assertEquals("Return valid point", coll.size(), 1);

		coll = tw.getItemsBetween(new HiResDate(0, 100), new HiResDate(0, 100));
		assertEquals("Return valid point", coll.size(), 1);

		tw.addFix(fw2);

		// check returning with fields
		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
		assertEquals("Return empty when out of range", coll, null);

		coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0, 540));
		assertEquals("Return empty when out of range", coll, null);

		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 140));
		assertEquals("Return valid point", coll.size(), 1);

		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 440));
		assertEquals("Return valid point", coll.size(), 2);

		coll = tw.getItemsBetween(new HiResDate(0, 150), new HiResDate(0, 440));
		assertEquals("Return valid point", coll.size(), 1);

		coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0, 440));
		assertEquals("Return valid point", coll.size(), 1);

		tw.addFix(fw3);

		// check returning with fields
		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
		assertEquals("Return empty when out of range", coll, null);

		coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0, 540));
		assertEquals("Return empty when out of range", coll, null);

		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 140));
		assertEquals("Return valid point", coll.size(), 1);

		coll = tw.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 440));
		assertEquals("Return valid point", coll.size(), 2);

		coll = tw.getItemsBetween(new HiResDate(0, 150), new HiResDate(0, 440));
		assertEquals("Return valid point", coll.size(), 1);

		coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0, 440));
		assertEquals("Return valid point", coll.size(), 1);

		coll = tw.getItemsBetween(new HiResDate(0, 100), new HiResDate(0, 300));
		assertEquals("Return valid point", coll.size(), 2);

		coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0, 500));
		assertEquals("Return valid point", coll.size(), 2);

		tw.addFix(fw4);

	}

	public final void testInterpolation()
	{
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100,
						WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200,
						WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300,
						WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400,
						WorldDistance.METRES), null)), 40, 140));
		fw4.setLabel("fw4");
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500,
						WorldDistance.METRES), null)), 50, 150));
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

		assertTrue("interpolating now switched on", tw.getInterpolatePoints());

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
		assertEquals("right answer", 0, Conversions.Degs2m(rangeError.getRange()),
				0.0001);
		assertEquals("right speed", res.getSpeed(), fw3.getSpeed(), 0.00000000001);
		assertEquals("right course", res.getCourse(), fw3.getCourse(), 0);

	}

	public final void testMyParams()
	{
		TrackWrapper ed = new TrackWrapper();
		ed.setName("blank");

		editableTesterSupport.testParams(ed, this);
		ed = null;
	}

	public void testPaintingColChange()
	{
		final TrackWrapper tw = new TrackWrapper();
		tw.setColor(Color.RED);
		tw.setName("test track");

		/**
		 * intention of this test: line is broken into three segments (red, yellow,
		 * green). - first of 2 points, next of 2 points, last of 3 points (14
		 * values)
		 */

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100,
						WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		fw1.setColor(Color.red);
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200,
						WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		fw2.setColor(Color.yellow);
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300,
						WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");
		fw3.setColor(Color.green);
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400,
						WorldDistance.METRES), null)), 40, 140));
		fw4.setLabel("fw4");
		fw4.setColor(Color.green);
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500,
						WorldDistance.METRES), null)), 50, 150));
		fw5.setLabel("fw5");
		fw5.setColor(Color.green);
		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);

		callCount = 0;
		pointCount = 0;

		assertNull("our array of points starts empty", tw.debug_GetPoints());
		assertEquals("our point array counter is zero", tw.debug_GetPointCtr(), 0);

		final CanvasType dummyDest = new TestMockCanvas();

		tw.paint(dummyDest);

		assertEquals("our array has correct number of points", 10,
				tw.debug_GetPoints().length);
		assertEquals("the pointer counter has been reset", 0,
				tw.debug_GetPointCtr());

		// check it got called the correct number of times
		assertEquals("We didnt paint enough polygons", 3, callCount);
		assertEquals("We didnt paint enough polygons points", 14, pointCount);
	}

	public void testPaintingLineJoinedChange()
	{
		final TrackWrapper tw = new TrackWrapper();
		tw.setColor(Color.RED);
		tw.setName("test track");

		/**
		 * intention of this test: line is broken into two segments - one of two
		 * points, the next of three, thus two polygons should be drawn - 10 points
		 * total (4 then 6).
		 */

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100,
						WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		fw1.setColor(Color.red);
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200,
						WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		fw2.setColor(Color.red);
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300,
						WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");
		fw3.setColor(Color.red);
		fw3.setLineShowing(false);
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400,
						WorldDistance.METRES), null)), 40, 140));
		fw4.setLabel("fw4");
		fw4.setColor(Color.red);
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500,
						WorldDistance.METRES), null)), 50, 150));
		fw5.setLabel("fw5");
		fw5.setColor(Color.red);
		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);

		callCount = 0;
		pointCount = 0;

		assertNull("our array of points starts empty", tw.debug_GetPoints());
		assertEquals("our point array counter is zero", tw.debug_GetPointCtr(), 0);

		final CanvasType dummyDest = new TestMockCanvas();

		tw.paint(dummyDest);

		assertEquals("our array has correct number of points", 10,
				tw.debug_GetPoints().length);
		assertEquals("the pointer counter has been reset", 0,
				tw.debug_GetPointCtr());

		// check it got called the correct number of times
		assertEquals("We didnt paint enough polygons", 2, callCount);
		assertEquals("We didnt paint enough polygons points", 10, pointCount);

	}

	public void testPaintingVisChange()
	{
		final TrackWrapper tw = new TrackWrapper();
		tw.setColor(Color.RED);
		tw.setName("test track");

		/**
		 * intention of this test: line is broken into two segments of two points,
		 * thus two polygons should be drawn, each with 4 points - 8 points total.
		 */

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100,
						WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		fw1.setColor(Color.red);
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200,
						WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		fw2.setColor(Color.red);
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300,
						WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");
		fw3.setColor(Color.red);
		fw3.setVisible(false);
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400,
						WorldDistance.METRES), null)), 40, 140));
		fw4.setLabel("fw4");
		fw4.setColor(Color.red);
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500,
						WorldDistance.METRES), null)), 50, 150));
		fw5.setLabel("fw5");
		fw5.setColor(Color.red);
		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);

		callCount = 0;
		pointCount = 0;

		assertNull("our array of points starts empty", tw.debug_GetPoints());
		assertEquals("our point array counter is zero", tw.debug_GetPointCtr(), 0);

		final CanvasType dummyDest = new TestMockCanvas();

		tw.paint(dummyDest);

		assertEquals("our array has correct number of points", 10,
				tw.debug_GetPoints().length);
		assertEquals("the pointer counter has been reset", 0,
				tw.debug_GetPointCtr());

		// check it got called the correct number of times
		assertEquals("We didnt paint enough polygons", 2, callCount);
		assertEquals("We didnt paint enough polygons points", 8, pointCount);

	}

	public void testDynamicInfill()
	{
		final TrackSegment ts0 = getDummyList();

		final TrackSegment ts1 = new TrackSegment();
		final FixWrapper newFix5 = new FixWrapper(new Fix(new HiResDate(150000),
				new WorldLocation(3, 4, 3), 1, 2));
		final FixWrapper newFix6 = new FixWrapper(new Fix(new HiResDate(160000),
				new WorldLocation(4, 4, 3), 1, 2));
		final FixWrapper newFix7 = new FixWrapper(new Fix(new HiResDate(170000),
				new WorldLocation(5, 4, 3), 1, 2));
		final FixWrapper newFix8 = new FixWrapper(new Fix(new HiResDate(180000),
				new WorldLocation(6, 4, 3), 1, 2));
		ts1.addFix(newFix5);
		ts1.addFix(newFix6);
		ts1.addFix(newFix7);
		ts1.addFix(newFix8);

		TrackSegment newS = new DynamicInfillSegment(ts0, ts1);
		assertEquals("got lots of points", 10, newS.size());

		// have a look at the generated points
		final Enumeration<Editable> pts = newS.elements();
		while (pts.hasMoreElements())
		{
			final Editable editable = (Editable) pts.nextElement();
			final FixWrapper fix = (FixWrapper) editable;
			System.out.println(fix.getLocation().getLat() + ", "
					+ fix.getLocation().getLong() + " , "
					+ fix.getDateTimeGroup().getDate().getTime());
		}
		System.out.println("========");

		// cause the monster problem
		newFix6.setDateTimeGroup(new HiResDate(210000));
		newFix7.setDateTimeGroup(new HiResDate(230000));
		newFix7.setDateTimeGroup(new HiResDate(250000));

		// try the mini algorithm first
		final FixWrapper[] items = DynamicInfillSegment.getLastElementsFrom(ts1, 2);
		assertTrue("times should not be the same ",
				items[0].getDateTimeGroup() != items[1].getDateTimeGroup());

		try
		{
			newS = new DynamicInfillSegment(ts0, ts1);
			assertEquals("got lots of points", 10, newS.size());
		}
		catch (final RuntimeException re)
		{
			re.printStackTrace();
			fail("runtime exception thrown!!!");
		}

	}

	public void testInfillSpline()
	{
		// generate the location spline
		final double[] times = new double[]
		{ 3d, 4d, 8d, 9d };
		final double[] lats = new double[]
		{ 1d, 1d, 3d, 4d };
		final double[] longs = new double[]
		{ 1d, 2d, 4d, 4d };

		final CubicSpline latSpline = new CubicSpline(times, lats);
		final CubicSpline longSpline = new CubicSpline(times, longs);

		for (int t = 0; t < 4; t++)
		{
			System.out.println(longs[t] + ", " + lats[t] + "," + times[t]);
		}

		for (long t = 50; t < 80; t++)
		{
			final double tNow = t / 10d;
			final double thisLat = latSpline.interpolate(tNow);
			final double thisLong = longSpline.interpolate(tNow);
			System.out.println(thisLong + ", " + thisLat + ", " + tNow);
		}
	}

}
