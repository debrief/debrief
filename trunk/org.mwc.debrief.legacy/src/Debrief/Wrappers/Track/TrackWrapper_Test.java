/**
 * 
 */
package Debrief.Wrappers.Track;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
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

	private static final String TRACK_NAME = "test track";
	/**
	 * fixes we can easily refer to in a test..
	 * 
	 */
	private final FixWrapper fw1 = createFix(300000, 2, 3);
	private final FixWrapper fw2 = createFix(500000, 2, 3);
	private TrackWrapper _tw;
	private MessageProvider.TestableMessageProvider _messages;

	private int _ctr = 0;

	/**
	 * @throws java.lang.Exception
	 */
	public void setUp() throws Exception
	{
		_tw = new TrackWrapper();
		_tw.setName(TRACK_NAME);
		_tw.addFix(createFix(100000, 1, 1));
		_tw.addFix(createFix(200000, 2, 3));
		_tw.addFix(fw1);
		_tw.addFix(createFix(400000, 3, 3));
		_tw.addFix(fw2);
		_tw.addFix(createFix(600000, 4, 6));
		_messages = new MessageProvider.TestableMessageProvider();
		MessageProvider.Base.setProvider(_messages);
	}

	/**
	 * @throws java.lang.Exception
	 */
	public void tearDown() throws Exception
	{
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getName()}.
	 */
	public void testGetName()
	{
		assertEquals("correct name", TRACK_NAME, _tw.getName());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getBounds()}.
	 */
	public void testGetBounds()
	{
		WorldArea correctBounds = new WorldArea(new WorldLocation(1, 1, 0),
				new WorldLocation(4, 6, 0));
		assertEquals("wrong bounds returned", correctBounds, _tw.getBounds());
	}

	public void testGenInfill()
	{
		TrackSegment ts1 = new TrackSegment();
		TrackSegment ts2 = new TrackSegment();

		ts1.addFix(createFix(1000, 1, 0, 5d, 1, 0, 00d, 135, 12));
		ts1.addFix(createFix(2000, 1, 0, 4d, 1, 0, 01d, 135, 12));
		ts1.addFix(createFix(3000, 1, 0, 3d, 1, 0, 02d, 135, 12));
		ts1.addFix(createFix(4000, 1, 0, 2d, 1, 0, 03d, 135, 12));

		ts2.addFix(createFix(8000, 1, 0, 0d, 1, 0, 07d, 90, 12));
		ts2.addFix(createFix(9000, 1, 0, 0d, 1, 0, 08d, 90, 12));
		ts2.addFix(createFix(10000, 1, 0, 0d, 1, 0, 09d, 90, 12));
		ts2.addFix(createFix(11000, 1, 0, 0d, 1, 0, 10d, 90, 12));
		ts2.addFix(createFix(12000, 1, 0, 0d, 1, 0, 11d, 90, 12));
		ts2.addFix(createFix(13000, 1, 0, 0d, 1, 0, 12d, 90, 12));

		// try the function
		TrackSegment infill = new TrackSegment(ts1, ts2);

		// check there are the correct number of items
		assertEquals("wrong num entries", 3, infill.size());

	}

	public void testDecimate()
	{
		TrackSegment ts1 = new TrackSegment();
		ts1.addFix(createFix(0 * 1000000l, 32, 33));
		ts1.addFix(createFix(1 * 1000000l, 32, 33));
		ts1.addFix(createFix(2 * 1000000l, 32, 33));
		ts1.addFix(createFix(3 * 1000000l, 32, 33));
		ts1.addFix(createFix(4 * 1000000l, 32, 33));

		TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(5 * 1000000l, 32, 33));
		ts2.addFix(createFix(6 * 1000000l, 32, 33));
		ts2.addFix(createFix(7 * 1000000l, 32, 33));
		ts2.addFix(createFix(8 * 1000000l, 32, 33));
		ts2.addFix(createFix(9 * 1000000l, 32, 33));

		TrackSegment ts3 = new TrackSegment();
		ts3.addFix(createFix(10 * 1000000l, 32, 33));
		ts3.addFix(createFix(11 * 1000000l, 32, 33));
		ts3.addFix(createFix(12 * 1000000l, 32, 33));
		ts3.addFix(createFix(13 * 1000000l, 32, 33));
		ts3.addFix(createFix(24 * 1000000l, 32, 33));

		TrackWrapper tw = new TrackWrapper();
		tw.add(ts1);
		tw.add(ts2);
		tw.add(ts3);

		Enumeration<Editable> data = tw.elements();
		SegmentList sl = (SegmentList) data.nextElement();

		// check it's got the segs
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 15, tw.numFixes());

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(4 * 1000000l));

		// how was it?
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 8, tw.numFixes());

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(500000l));

		// how was it?
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 43, tw.numFixes());
	}

	public void testDecimatePositionsAndData()
	{
		TrackSegment ts1 = new TrackSegment();
		ts1.addFix(createFix(0 * 60 * 1000000l, 32, 33));
		ts1.addFix(createFix(1 * 60 * 1000000l, 32, 33));
		ts1.addFix(createFix(2 * 60 * 1000000l, 32, 33));
		ts1.addFix(createFix(3 * 60 * 1000000l, 32, 33));
		ts1.addFix(createFix(4 * 60 * 1000000l, 32, 33));

		TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(5 * 60 * 1000000l, 32, 33));
		ts2.addFix(createFix(6 * 60 * 1000000l, 32, 33));
		ts2.addFix(createFix(7 * 60 * 1000000l, 32, 33));
		ts2.addFix(createFix(8 * 60 * 1000000l, 32, 33));
		ts2.addFix(createFix(9 * 60 * 1000000l, 32, 33));

		TrackSegment ts3 = new TrackSegment();
		ts3.addFix(createFix(10 * 60 * 1000000l, 32, 33));
		ts3.addFix(createFix(11 * 60 * 1000000l, 32, 33));
		ts3.addFix(createFix(12 * 60 * 1000000l, 32, 33));
		ts3.addFix(createFix(13 * 60 * 1000000l, 32, 33));
		ts3.addFix(createFix(24 * 60 * 1000000l, 32, 33));

		SensorWrapper sw = new SensorWrapper("dummy sensor");
		SensorContactWrapper scw1 = new SensorContactWrapper("the track",
				new HiResDate(1 * 60 * 1000000l),
				new WorldDistance(2, WorldDistance.NM), 12, new Double(12), new Double(
						44), null, Color.red, "aa", 1, "dummy sensor");
		SensorContactWrapper scw2 = new SensorContactWrapper("the track",
				new HiResDate(3 * 60 * 1000000l),
				new WorldDistance(4, WorldDistance.NM), 14, new Double(15), new Double(
						46), null, Color.red, "aa", 1, "dummy sensor");
		SensorContactWrapper scw3 = new SensorContactWrapper("the track",
				new HiResDate(7 * 60 * 1000000l), new WorldDistance(12,
						WorldDistance.NM), 18, new Double(12), new Double(12), null,
				Color.red, "aa", 1, "dummy sensor");
		SensorContactWrapper scw4 = new SensorContactWrapper("the track",
				new HiResDate(8 * 60 * 1000000l),
				new WorldDistance(7, WorldDistance.NM), 35, new Double(12), new Double(
						312), null, Color.red, "aa", 1, "dummy sensor");
		sw.add(scw1);
		sw.add(scw2);
		sw.add(scw3);
		sw.add(scw4);

		TrackWrapper tw = new TrackWrapper();
		tw.add(ts1);
		tw.add(ts2);
		tw.add(ts3);
		tw.add(sw);

		Enumeration<Editable> data = tw.elements();
		SegmentList sl = (SegmentList) data.nextElement();

		// check it's got the segs
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 15, tw.numFixes());
		// check we've got all the sensor data
		assertEquals("has all sensor cuts", 4, countCuts(tw.getSensors()));

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(4 * 60 * 1000000l));

		// how was it?
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 2, tw.numFixes());
		assertEquals("has all sensor cuts", 4, countCuts(tw.getSensors()));

		// GO FOR ULTIMATE DECIMATION
		tw.setResampleDataAt(new HiResDate(30 * 1000000l));

		// how was it?
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 43, tw.numFixes());
		assertEquals("has all sensor cuts", 8, countCuts(tw.getSensors()));
	}

	private int countCuts(Enumeration<SensorWrapper> sensors)
	{
		int counter=0;
		while(sensors.hasMoreElements())
		{
			SensorWrapper sw = sensors.nextElement();
			Enumeration<Editable> ele = sw.elements();
			while(ele.hasMoreElements())
			{
				counter++;
				ele.nextElement();
			}
		}
		return counter;
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#add(MWC.GUI.Editable)}
	 * .
	 */

	public void testAdd()
	{
		assertEquals("start condition", 6, this.trackLength());

		// check we can add a fix
		FixWrapper fw = createFix(12, 3d, 4d);
		_tw.add(fw);

		assertEquals("got added", 7, this.trackLength());

		// now something else
		SensorWrapper sw = new SensorWrapper("some sensor");
		sw.add(new SensorContactWrapper());
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
		FixWrapper fw = createFix(12, 3d, 4d);
		_tw.addFix(fw);

		assertEquals("got added", 7, this.trackLength());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#fixMoved()}.
	 */

	public void testFixMoved()
	{
		// now something else
		_ctr = 0;
		SensorWrapper sw = new SensorWrapper("some sensor")
		{
			private static final long serialVersionUID = 1L;

			public void setHost(WatchableList host)
			{
				super.setHost(host);
				_ctr++;
			}
		};
		sw.add(new SensorContactWrapper());
		_tw.add(sw);

		// it should only have been fired once
		assertEquals("only called to tell sensor of it's ownership", 1, _ctr);

		// tell the track it's moved
		_tw.fixMoved();

		// did it hear?
		assertEquals("got informed of sensor movement", 2, _ctr);

	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#append(MWC.GUI.Layer)}
	 * .
	 */

	public void testAppend()
	{
		TrackWrapper tw2 = new TrackWrapper();
		FixWrapper f1 = createFix(13, 2, 2);
		FixWrapper f2 = createFix(14, 32, 12);
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
		SensorWrapper sw = new SensorWrapper("sensor a");
		sw.add(new SensorContactWrapper("trk", new HiResDate(12), null, 0, null,
				null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(13), null, 0, null,
				null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(14), null, 0, null,
				null, null, 0, null));
		sw.setVisible(true);

		_tw.add(sw);

		Enumeration<Editable> tester = _tw.contiguousElements();
		_ctr = 0;
		while (tester.hasMoreElements())
		{
			tester.nextElement();
			_ctr++;
		}
		assertEquals("have new items", 9, _ctr);
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
		assertEquals("just has positions", 1, _ctr);

		// give it a little more data
		SensorWrapper sw = new SensorWrapper("sensor a");
		sw.add(new SensorContactWrapper("trk", new HiResDate(12), null, 0, null,
				null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(13), null, 0, null,
				null, null, 0, null));
		sw.add(new SensorContactWrapper("trk", new HiResDate(14), null, 0, null,
				null, null, 0, null));
		_tw.add(sw);

		list = _tw.elements();
		_ctr = 0;
		while (list.hasMoreElements())
		{
			_ctr++;
			list.nextElement();
		}
		assertEquals("shows new item", 2, _ctr);
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
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getEndDTG()}.
	 */

	public void testGetEndDTG()
	{
		HiResDate dt = _tw.getEndDTG();
		assertEquals("correct end time", 600000, dt.getDate().getTime());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#getItemsBetween(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */

	public void testGetItemsBetween()
	{
		assertEquals("found the items", 2, _tw.getItemsBetween(
				new HiResDate(200003), new HiResDate(400003)).size());
		assertEquals("found the items", 3, _tw.getItemsBetween(new HiResDate(0),
				new HiResDate(300000)).size());
		assertEquals("found the items", 6, _tw.getItemsBetween(
				new HiResDate(100000), new HiResDate(800000)).size());
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
		assertEquals("found right one", fw1, res[0]);

		res = _tw.getNearestTo(new HiResDate(500000));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", fw2, res[0]);

		res = _tw.getNearestTo(new HiResDate(400005));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", fw2, res[0]);

		res = _tw.getNearestTo(new HiResDate(500000));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", fw2, res[0]);

		res = _tw.getNearestTo(new HiResDate(299995));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", fw1, res[0]);
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getPositions()}.
	 */

	public void testGetPositions()
	{
		Enumeration<Editable> list = _tw.getPositions();
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
		HiResDate dt = _tw.getStartDTG();
		assertEquals("correct end time", 100000, dt.getDate().getTime());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#getUnfilteredItems(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */

	public void testGetUnfilteredItems()
	{
		assertEquals("found right num ", 3, _tw.getUnfilteredItems(
				new HiResDate(100000), new HiResDate(300000)).size());
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
		WorldLocation wl = new WorldLocation(4, 1, 0);
		double res = _tw.rangeFrom(wl);
		assertEquals("correct range", 0d, res, 0.001);
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#removeElement(MWC.GUI.Editable)}.
	 */

	public void testRemoveElement()
	{
		assertEquals("all there", 6, _tw.numFixes());
		_tw.removeElement(fw1);
		assertEquals("one less now", 5, _tw.numFixes());
		_tw.removeElement(fw2);
		assertEquals("one less now", 4, _tw.numFixes());
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
	 * {@link Debrief.Wrappers.TrackWrapper#shiftTrack(java.util.Enumeration, MWC.GenericData.WorldVector)}
	 * .
	 */

	public void testShiftTrack()
	{
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#visibleBetween(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */

	public void testVisibleBetween()
	{
		assertEquals("is vis", false, _tw.visibleBetween(new HiResDate(700000),
				new HiResDate(900000)));
		assertEquals("is vis", true, _tw.visibleBetween(new HiResDate(000000),
				new HiResDate(300000)));
		assertEquals("is vis", true, _tw.visibleBetween(new HiResDate(300000),
				new HiResDate(500000)));
	}

	/**
	 * .
	 */

	public void testTMASplit()
	{
		// //////////////////////////////////
		// start off building from a track
		// //////////////////////////////////
		TrackWrapper tw = new TrackWrapper();

		tw.addFix(createFix(100000, 1, 1, 4, 12));
		tw.addFix(createFix(200000, 2, 3, 4, 12));
		tw.addFix(createFix(300000, 3, 3, 4, 12));
		tw.addFix(createFix(400000, 4, 6, 4, 12));
		tw.addFix(createFix(500000, 4, 6, 4, 12));
		tw.addFix(createFix(600000, 4, 6, 4, 12));
		tw.addFix(createFix(700000, 4, 6, 4, 12));

		WorldVector offset = new WorldVector(12, 12, 0);
		WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);
		double course = 33;

		// ok, create the segment
		CoreTMASegment seg = null;

		// check the before
		FixWrapper firstFix = null;

		// ////////////////////////
		// NOW FROM A SENSOR WRAPPER
		// /////////////////////////
		SensorWrapper sw = new SensorWrapper("some sensor");
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
		assertEquals("correct speed before", 5, seg.getSpeed().getValueIn(
				WorldSpeed.Kts), 0.001);
		assertEquals("correct course before", 33, MWC.Algorithms.Conversions
				.Rads2Degs(firstFix.getCourse()), 0.001);
		assertEquals("correct speed before", 5, firstFix.getSpeed(), 0.001);

		// ok, now do the split
		TrackWrapper segW = new TrackWrapper();
		segW.setName("TMA");
		segW.add(seg);

		// get hold of an item in the segment
		Enumeration<Editable> enumer = seg.elements();
		enumer.nextElement();
		enumer.nextElement();
		FixWrapper fw = (FixWrapper) enumer.nextElement();
		assertNotNull("Found a fix", fw);

		// do the split
		Vector<TrackSegment> segs = segW.splitTrack(fw, false);

		// check we have enough segments
		assertEquals("now two segments", 2, segs.size());
		assertEquals("first is of correct length", 3, segs.firstElement().size());
		assertEquals("first is of correct length", 4, segs.lastElement().size());

		// check they're of the correct type
		TrackSegment seg1 = segs.firstElement();
		TrackSegment seg2 = segs.lastElement();
		assertTrue(" is a tma segment", seg1 instanceof RelativeTMASegment);
		assertTrue(" is a tma segment", seg2 instanceof RelativeTMASegment);

	}

	private SensorContactWrapper createSensorItem(TrackWrapper tw,
			SensorWrapper sw, int sensorDTG)
	{
		return new SensorContactWrapper(tw.getName(), new HiResDate(sensorDTG),
				new WorldDistance(12, WorldDistance.NM), 12, null, Color.red,
				"some lable", 12, sw.getName());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#splitTrack(Debrief.Wrappers.FixWrapper, boolean)}
	 * .
	 */

	public void testFixAsVector()
	{
		TrackSegment ts = new TrackSegment();
		long period = 1000 * 60 * 60;
		double speedKts = 60;
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
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#splitTrack(Debrief.Wrappers.FixWrapper, boolean)}
	 * .
	 */

	public void testSplitTrack1()
	{
		Vector<TrackSegment> segs = _tw.splitTrack(fw1, true);
		assertEquals("now two segments", 2, segs.size());
		assertEquals("first is of correct length", 2, segs.firstElement().size());
		assertEquals("first is of correct length", 4, segs.lastElement().size());

		// check the names.
		Enumeration<Editable> items = _tw.elements();
		SegmentList list = (SegmentList) items.nextElement();
		Enumeration<Editable> segments = list.elements();
		TrackSegment s1 = (TrackSegment) segments.nextElement();
		TrackSegment s2 = (TrackSegment) segments.nextElement();
		assertEquals("correct layer name:", "010001.40", s1.getName());
		assertEquals("correct layer name:", "010005.00", s2.getName());

		// split it again
		Vector<TrackSegment> segs2 = _tw.splitTrack(fw2, true);
		assertEquals("two blocks returned", 2, segs2.size());
		assertEquals("has 3 segments", 3, numSegments());
		assertEquals("first is of correct length", 2, segs2.firstElement().size());
		assertEquals("first is of correct length", 2, segs2.lastElement().size());

		items = _tw.elements();
		list = (SegmentList) items.nextElement();
		segments = list.elements();
		s1 = (TrackSegment) segments.nextElement();
		s2 = (TrackSegment) segments.nextElement();
		TrackSegment s3 = (TrackSegment) segments.nextElement();
		assertEquals("correct layer name:", "010001.40", s1.getName());
		assertEquals("correct layer name:", "010005.00", s2.getName());
		assertEquals("correct layer name:", "010008.20", s3.getName());

		// now recombine them
		_tw.combineSections(segs2);
		assertEquals("has 2 segments", 2, numSegments());
		assertEquals("first is of correct length", 2, segs.firstElement().size());
		assertEquals("first is of correct length", 4, segs.lastElement().size());

		items = _tw.elements();
		list = (SegmentList) items.nextElement();
		segments = list.elements();
		s1 = (TrackSegment) segments.nextElement();
		s2 = (TrackSegment) segments.nextElement();
		assertEquals("correct layer name:", "010001.40", s1.getName());
		assertEquals("correct layer name:", "010005.00", s2.getName());

		_tw.combineSections(segs);
		assertEquals("has 1 segment1", 1, numSegments());
		assertEquals("first is of correct length", 6, segs.firstElement().size());

	}

	private int numSegments()
	{
		int res = 0;
		Enumeration<Editable> layers = _tw.elements();
		while (layers.hasMoreElements())
		{
			Object child = layers.nextElement();
			if (child instanceof TrackSegment)
			{
				res++;
			}
			else if (child instanceof SegmentList)
			{
				SegmentList segl = (SegmentList) child;
				Enumeration<Editable> segs = segl.elements();
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
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#splitTrack(Debrief.Wrappers.FixWrapper, boolean)}
	 * .
	 */

	public void testSplitTrack2()
	{
		Vector<TrackSegment> segs = _tw.splitTrack(fw1, false);
		assertEquals("now two segments", 2, segs.size());
		assertEquals("first is of correct length", 3, segs.firstElement().size());
		assertEquals("first is of correct length", 3, segs.lastElement().size());
	}

	public void testTrackMerge1()
	{
		TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(910000, 32, 33));
		ts2.addFix(createFix(911000, 32, 33));
		ts2.addFix(createFix(912000, 32, 33));
		ts2.addFix(createFix(913000, 32, 33));
		ts2.addFix(createFix(914000, 32, 33));
		TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());

		// do a merge
		Layer[] parents = new Layer[]
		{ _tw, tw3 };
		Editable[] subjects = new Editable[]
		{ _tw, ts2 };
		TrackWrapper.mergeTracks(ts2, theLayers, parents, subjects);

		// have a look at the results
		assertEquals("track 3 is longer", 11, tw3.numFixes());
		assertEquals("track got ditched", 1, theLayers.size());
		assertEquals("fix has new parent", "tw3", fw1.getTrackWrapper().getName());
	}

	public void testTrackStartEnd()
	{
		TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(910000, 32, 33));
		ts2.addFix(createFix(911000, 32, 33));
		ts2.addFix(createFix(912000, 32, 33));
		ts2.addFix(createFix(913000, 32, 33));
		ts2.addFix(createFix(914000, 32, 33));
		TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());

		// do a merge
		Layer[] parents = new Layer[]
		{ _tw, tw3 };
		Editable[] subjects = new Editable[]
		{ _tw, ts2 };
		TrackWrapper.mergeTracks(ts2, theLayers, parents, subjects);

		// have a look at the results
		assertEquals("track 3 is longer", 11, tw3.numFixes());
		assertEquals("track got ditched", 1, theLayers.size());
		assertEquals("fix has new parent", "tw3", fw1.getTrackWrapper().getName());
	}

	public void testTrackMergeAllSegments()
	{
		TrackSegment ts1 = new TrackSegment();
		ts1.addFix(createFix(110000, 32, 33));
		ts1.addFix(createFix(111000, 32, 33));
		ts1.addFix(createFix(112000, 32, 33));
		ts1.addFix(createFix(113000, 32, 33));
		ts1.addFix(createFix(114000, 32, 33));

		TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(210000, 32, 33));
		ts2.addFix(createFix(211000, 32, 33));
		ts2.addFix(createFix(212000, 32, 33));
		ts2.addFix(createFix(213000, 32, 33));
		ts2.addFix(createFix(214000, 32, 33));

		TrackSegment ts3 = new TrackSegment();
		ts3.addFix(createFix(910000, 32, 33));
		ts3.addFix(createFix(911000, 32, 33));
		ts3.addFix(createFix(912000, 32, 33));
		ts3.addFix(createFix(913000, 32, 33));
		ts3.addFix(createFix(914000, 32, 33));

		TrackWrapper tw = new TrackWrapper();
		tw.add(ts1);
		tw.add(ts2);
		tw.add(ts3);

		Enumeration<Editable> data = tw.elements();
		SegmentList sl = (SegmentList) data.nextElement();

		// check it's got the segs
		assertEquals("has segments", "Track segments (3 items)", sl.toString());
		assertEquals("has all fixes", 15, tw.numFixes());

		// do the merge
		sl.mergeAllSegments();

		assertEquals("has merged", "Track segments (1 items)", sl.toString());
		assertEquals("track has correct data", "Positions (15 items)", tw
				.elements().nextElement().toString());
		assertEquals("has all fixes", 15, tw.numFixes());

		assertEquals("correct start time", 110000, tw.getStartDTG().getDate()
				.getTime());
		assertEquals("correct end time", 914000, tw.getEndDTG().getDate().getTime());

	}

	public void testTrackMerge2()
	{
		TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(910000, 32, 33));
		ts2.addFix(createFix(911000, 32, 33));
		ts2.addFix(createFix(912000, 32, 33));
		ts2.addFix(createFix(913000, 32, 33));
		ts2.addFix(createFix(914000, 32, 33));
		TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());

		// do a merge
		Layer[] parents = new Layer[]
		{ _tw, tw3 };
		Editable[] subjects = new Editable[]
		{ _tw, ts2 };
		TrackWrapper.mergeTracks(_tw, theLayers, parents, subjects);

		// have a look at the results
		assertEquals("track is longer", 11, _tw.numFixes());
		assertEquals("track got ditched", 1, theLayers.size());
		TrackSegment sl = (TrackSegment) _tw.elements().nextElement();
		assertEquals("just the one segment - with all our points", 11, sl.size());

	}

	public void testTrackMerge3()
	{
		TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(310000, 32, 33));
		ts2.addFix(createFix(311000, 32, 33));
		ts2.addFix(createFix(312000, 32, 33));
		ts2.addFix(createFix(313000, 32, 33));
		ts2.addFix(createFix(314000, 32, 33));
		TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());

		// do a merge
		Layer[] parents = new Layer[]
		{ _tw, tw3 };
		Editable[] subjects = new Editable[]
		{ _tw, ts2 };
		TrackWrapper.mergeTracks(_tw, theLayers, parents, subjects);

		// have a look at the results
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());

		// check the error message got thrown
		assertEquals("have error", 1, _messages._messages.size());
		assertEquals("correct title", "Merge tracks", _messages._titles
				.firstElement());
		assertEquals(
				"correct title",
				"Sorry, 'Positions' and 'test track' overlap in time. Please correct this and retry",
				_messages._messages.firstElement());
		assertEquals("correct title", MessageProvider.ERROR,
				(int) _messages._statuses.firstElement());
	}

	// ////////////

	public void testTrackGroup1()
	{
		TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(310000, 32, 33));
		ts2.addFix(createFix(311000, 32, 33));
		ts2.addFix(createFix(312000, 32, 33));
		ts2.addFix(createFix(313000, 32, 33));
		ts2.addFix(createFix(314000, 32, 33));
		TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		Layers theLayers = new Layers();
		theLayers.addThisLayer(tw3);
		theLayers.addThisLayer(_tw);

		// check startup status
		assertEquals("track starts correctly", 6, trackLength());
		assertEquals("track 3 starts correctly", 5, tw3.numFixes());
		assertEquals("have right num tracks", 2, theLayers.size());

		// do a merge
		Layer[] parents = new Layer[]
		{ _tw, tw3 };
		Editable[] subjects = new Editable[]
		{ _tw, ts2 };
		TrackWrapper.groupTracks(_tw, theLayers, parents, subjects);

		// have a look at the results
		assertEquals("track 3 is longer", 11, _tw.numFixes());

		// check it's been a group, not an add
		Enumeration<Editable> iter = _tw.elements();
		_ctr = 0;
		while (iter.hasMoreElements())
		{
			SegmentList sl = (SegmentList) iter.nextElement();
			Enumeration<Editable> segments = sl.elements();
			while (segments.hasMoreElements())
			{
				_ctr++;
				segments.nextElement();
			}
		}
		assertEquals("track _tw has several segments", 2, _ctr);

		assertEquals("track got ditched", 1, theLayers.size());
	}

	public void testTrackGroupOrder()
	{
		TrackSegment ts2 = new TrackSegment();
		ts2.addFix(createFix(310000, 32, 33));
		ts2.addFix(createFix(311000, 32, 33));
		ts2.addFix(createFix(312000, 32, 33));
		ts2.addFix(createFix(313000, 32, 33));
		ts2.addFix(createFix(314000, 32, 33));

		TrackSegment ts3 = new TrackSegment();
		ts3.addFix(createFix(410000, 32, 33));
		ts3.addFix(createFix(411000, 32, 33));
		ts3.addFix(createFix(412000, 32, 33));
		ts3.addFix(createFix(413000, 32, 33));
		ts3.addFix(createFix(414000, 32, 33));

		TrackWrapper tw3 = new TrackWrapper();
		tw3.setName("tw3");
		tw3.add(ts2);
		tw3.add(ts3);

		Enumeration<Editable> elements = tw3.elements();
		SegmentList list = (SegmentList) elements.nextElement();
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

		elements = tw3.elements();
		list = (SegmentList) elements.nextElement();
		segments = list.elements();
		seg1 = (TrackSegment) segments.nextElement();
		seg2 = (TrackSegment) segments.nextElement();
	}

	public static FixWrapper createFix(long timeMillis, double vLat, double vLong)
	{
		FixWrapper fw = new FixWrapper(new Fix(new HiResDate(timeMillis),
				new WorldLocation(vLat, vLong, 0), 1, 1));
		return fw;
	}

	public static FixWrapper createFix(int timeMillis, int vLat, int vLong,
			int crseDegs, int spdKts)
	{
		FixWrapper theFix = createFix(timeMillis, vLat, vLong);
		theFix.getFix().setCourse(MWC.Algorithms.Conversions.Degs2Rads(crseDegs));
		theFix.getFix()
				.setSpeed(
						new WorldSpeed(spdKts, WorldSpeed.Kts)
								.getValueIn(WorldSpeed.ft_sec) / 3);

		return theFix;
	}

	public static FixWrapper createFix(int timeMillis, int vLatDeg, int vLatMin,
			double vLatSec, int vLongDeg, int vLongMin, double vLongSec,
			int crseDegs, int spdKts)
	{
		double vLat = vLatDeg + (vLatMin / 60d) + (vLatSec / 3600d);
		double vLong = vLongDeg + (vLongMin / 60d) + (vLongSec / 3600d);
		FixWrapper theFix = createFix(timeMillis, vLat, vLong);
		theFix.getFix().setCourse(MWC.Algorithms.Conversions.Degs2Rads(crseDegs));
		theFix.getFix()
				.setSpeed(
						new WorldSpeed(spdKts, WorldSpeed.Kts)
								.getValueIn(WorldSpeed.ft_sec) / 3);

		return theFix;
	}

	private int trackLength()
	{
		Enumeration<Editable> all = _tw.contiguousElements();
		int ctr = 0;
		while (all.hasMoreElements())
		{
			ctr++;
			all.nextElement();
		}
		return ctr;
	}

	private int countVisibleItems()
	{
		Enumeration<Editable> all = _tw.contiguousElements();
		int ctr = 0;
		while (all.hasMoreElements())
		{
			Plottable thisE = (Plottable) all.nextElement();
			if (thisE.getVisible())
				ctr++;
		}
		return ctr;
	}

	/**
	 * .
	 */

	public void testTMASegmentRotate()
	{

		FixWrapper f1 = createFix(100000, 1, 1, 270, 12);
		FixWrapper f2 = createFix(200000, 1, 0, 270, 12);
		WorldVector vector = new WorldVector(0, 1, 0);
		RelativeTMASegment ts = new RelativeTMASegment(270, new WorldSpeed(12,
				WorldSpeed.Kts), vector, null)
		{
			private static final long serialVersionUID = 1L;

			public WorldLocation getHostLocation()
			{
				return new WorldLocation(0, 1, 0);
			}
		};
		ts.addFix(f1);
		ts.addFix(f2);

		Iterator<Editable> iter = ts.getData().iterator();
		iter.next();
		FixWrapper farEnd = (FixWrapper) iter.next();
		WorldLocation origin = farEnd.getLocation();
		double brg = MWC.Algorithms.Conversions.Degs2Rads(-90);
		ts.rotate(brg, origin);

		// check we're on the new course
		assertEquals("at new offset bearing", -90, ts.getOffsetBearing(), 0.001);
		assertEquals("at new offset range", 1, ts.getOffsetRange().getValueIn(
				WorldDistance.DEGS), 0.001);
		assertEquals("on new course", 0, ts.getCourse(), 0.001);
		assertEquals("at original speed", 12, ts.getSpeed().getValueIn(
				WorldSpeed.Kts), 0.001);

		// ok, try to turn back!
		ts.rotate(-brg, origin);

		// check we're on the new course
		assertEquals("at new offset bearing", 0, ts.getOffsetBearing(), 0.001);
		assertEquals("at new offset range", 1, ts.getOffsetRange().getValueIn(
				WorldDistance.DEGS), 0.001);
		assertEquals("on new course", -90, ts.getCourse(), 0.001);
		assertEquals("at original speed", 12, ts.getSpeed().getValueIn(
				WorldSpeed.Kts), 0.001);
	}

	/**
	 * .
	 */

	public void testTMASegmentStretch()
	{
		FixWrapper f1 = createFix(0, 1, 1, 270, 12);
		FixWrapper f2 = createFix(1000 * 60 * 60, 1, 0, 270, 12);
		WorldVector vector = new WorldVector(0, 1, 0);
		RelativeTMASegment ts = new RelativeTMASegment(270, new WorldSpeed(12,
				WorldSpeed.Kts), vector, null)
		{
			private static final long serialVersionUID = 1L;

			public WorldLocation getHostLocation()
			{
				return new WorldLocation(0, 0, 0);
			}
		};
		ts.addFix(f1);
		ts.addFix(f2);

		Iterator<Editable> iter = ts.getData().iterator();
		FixWrapper nearEnd = (FixWrapper) iter.next();
		WorldLocation origin = nearEnd.getLocation();
		double rng = 2;
		ts.stretch(rng, origin);

		// check we're on the new course
		assertEquals("at new offset bearing", 0, ts.getOffsetBearing(), 0.001);
		assertEquals("at new offset range", 1, ts.getOffsetRange().getValueIn(
				WorldDistance.DEGS), 0.001);
		assertEquals("on new course", 270, ts.getCourse(), 1);
		assertEquals("at new speed", 120, ts.getSpeed().getValueIn(WorldSpeed.Kts),
				0.001);

		// ok, try to turn back!
		ts.rotate(-rng, origin);

		// check we're on the new course
		assertEquals("at new offset bearing", 0, ts.getOffsetBearing(), 0.001);
		assertEquals("at new offset range", 1, ts.getOffsetRange().getValueIn(
				WorldDistance.DEGS), 0.001);
		assertEquals("on new course", 385, ts.getCourse(), 1);
		assertEquals("at original speed", 120, ts.getSpeed().getValueIn(
				WorldSpeed.Kts), 0.001);
	}

	/**
	 * .
	 */

	public void testTMASegment()
	{
		// //////////////////////////////////
		// start off building from a track
		// //////////////////////////////////
		TrackWrapper tw = new TrackWrapper();

		FixWrapper f1 = createFix(100000, 1, 1, 4, 12);
		FixWrapper f2 = createFix(200000, 2, 3, 4, 12);
		tw.addFix(f1);
		tw.addFix(f2);
		tw.addFix(createFix(300000, 3, 3, 4, 12));
		tw.addFix(createFix(400000, 4, 6, 4, 12));

		WorldVector offset = new WorldVector(12, 12, 0);
		WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);
		double course = 33;

		// ok, create the segment
		CoreTMASegment seg = null;

		// check the before
		FixWrapper firstFix = null;

		// ////////////////////////
		// NOW FROM A SENSOR WRAPPER
		// /////////////////////////
		SensorWrapper sw = new SensorWrapper("some sensor");
		sw.setHost(tw);
		sw.add(createSensorItem(tw, sw, 110000));
		sw.add(createSensorItem(tw, sw, 120000));
		sw.add(createSensorItem(tw, sw, 130000));
		sw.add(createSensorItem(tw, sw, 140000));
		seg = new RelativeTMASegment(sw, offset, speed, course, null);

		// check the create worked
		assertEquals("enough points created", 4, seg.size());

		// check the before
		firstFix = (FixWrapper) seg.getData().iterator().next();
		assertEquals("correct course before", 33, seg.getCourse(), 0.001);
		assertEquals("correct speed before", 5, seg.getSpeed().getValueIn(
				WorldSpeed.Kts), 0.001);
		assertEquals("correct course before", 33, MWC.Algorithms.Conversions
				.Rads2Degs(firstFix.getCourse()), 0.001);
		assertEquals("correct speed before", 5, firstFix.getSpeed(), 0.001);

		seg.setCourse(35);
		seg.setSpeed(new WorldSpeed(15, WorldSpeed.Kts));

		assertEquals("correct course after", 35, seg.getCourse(), 0.001);
		assertEquals("correct speed after", 15, seg.getSpeed().getValueIn(
				WorldSpeed.Kts), 0.001);
		assertEquals("correct course after", 35, MWC.Algorithms.Conversions
				.Rads2Degs(firstFix.getCourse()), 0.001);
		assertEquals("correct speed after", 15, firstFix.getSpeed(), 0.001);

		// ///////////////////////////////////////////
		// lastly, build from a set of sensor observations
		// ///////////////////////////////////////////
		SensorContactWrapper[] items = new SensorContactWrapper[5];
		items[0] = createSensorItem(tw, sw, 110000);
		items[1] = createSensorItem(tw, sw, 115000);
		items[2] = createSensorItem(tw, sw, 119000);
		items[3] = createSensorItem(tw, sw, 141000);
		items[4] = createSensorItem(tw, sw, 150000);

		// sort out the host
		for (int i = 0; i < items.length; i++)
		{
			SensorContactWrapper sensorContactWrapper = items[i];
			sensorContactWrapper.setSensor(sw);
		}

		seg = new RelativeTMASegment(items, offset, speed, course, null);

		// check the create worked
		assertEquals("enough points created", 5, seg.size());

		Iterator<Editable> someIt = seg.getData().iterator();
		// check the before
		firstFix = (FixWrapper) someIt.next();
		assertEquals("correct course before", 33, seg.getCourse(), 0.001);
		assertEquals("correct speed before", 5, seg.getSpeed().getValueIn(
				WorldSpeed.Kts), 0.001);
		assertEquals("correct course before", 33, MWC.Algorithms.Conversions
				.Rads2Degs(firstFix.getCourse()), 0.001);
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

		seg.setCourse(35);
		seg.setSpeed(new WorldSpeed(15, WorldSpeed.Kts));

		assertEquals("correct course after", 35, seg.getCourse(), 0.001);
		assertEquals("correct speed after", 15, seg.getSpeed().getValueIn(
				WorldSpeed.Kts), 0.001);
		assertEquals("correct course after", 35, MWC.Algorithms.Conversions
				.Rads2Degs(firstFix.getCourse()), 0.001);
		assertEquals("correct speed after", 15, firstFix.getSpeed(), 0.001);

	}

	/**
	 * .
	 */

	public void testTMASplit2()
	{
		// //////////////////////////////////
		// start off building from a track
		// //////////////////////////////////
		TrackWrapper tw = new TrackWrapper();

		tw.addFix(createFix(100000, 1, 1, 4, 12));
		tw.addFix(createFix(200000, 2, 3, 4, 12));
		tw.addFix(createFix(300000, 3, 3, 4, 12));
		tw.addFix(createFix(400000, 4, 6, 4, 12));
		tw.addFix(createFix(500000, 4, 6, 4, 12));
		tw.addFix(createFix(600000, 4, 6, 4, 12));
		tw.addFix(createFix(700000, 4, 6, 4, 12));

		WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);
		double course = 33;

		// ok, create the segment
		CoreTMASegment seg = null;

		// check the before
		FixWrapper firstFix = null;

		// ////////////////////////
		// NOW AN ABSOLUTE ONE
		// /////////////////////////
		WorldLocation origin = new WorldLocation(12, 12, 12);
		HiResDate startTime = new HiResDate(11 * 60 * 1000);
		HiResDate endTime = new HiResDate(17 * 60 * 1000);
		seg = new AbsoluteTMASegment(course, speed, origin, startTime, endTime);

		// check the create worked
		assertEquals("enough points created", 7, seg.size());

		// check the before
		firstFix = (FixWrapper) seg.getData().iterator().next();
		assertEquals("correct course before", 33, seg.getCourse(), 0.001);
		assertEquals("correct speed before", 5, seg.getSpeed().getValueIn(
				WorldSpeed.Kts), 0.001);
		assertEquals("correct course before", 33, MWC.Algorithms.Conversions
				.Rads2Degs(firstFix.getCourse()), 0.001);
		assertEquals("correct speed before", 5, firstFix.getSpeed(), 0.001);

		// ok, now do the split
		TrackWrapper segW = new TrackWrapper();
		segW.setName("TMA");
		segW.add(seg);

		// get hold of an item in the segment
		Enumeration<Editable> enumer = seg.elements();
		enumer.nextElement();
		enumer.nextElement();
		FixWrapper fw = (FixWrapper) enumer.nextElement();
		assertNotNull("Found a fix", fw);

		// do the split
		Vector<TrackSegment> segs = segW.splitTrack(fw, false);

		// check we have enough segments
		assertEquals("now two segments", 2, segs.size());
		assertEquals("first is of correct length", 3, segs.firstElement().size());
		assertEquals("first is of correct length", 4, segs.lastElement().size());

		// check they're of the correct type
		TrackSegment seg1 = segs.firstElement();
		TrackSegment seg2 = segs.lastElement();
		assertTrue(" is a tma segment", seg1 instanceof AbsoluteTMASegment);
		assertTrue(" is a tma segment", seg2 instanceof AbsoluteTMASegment);

	}

}
