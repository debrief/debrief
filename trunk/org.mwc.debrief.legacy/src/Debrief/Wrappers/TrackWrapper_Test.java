/**
 * 
 */
package Debrief.Wrappers;

import static org.junit.Assert.assertEquals;

import java.util.Enumeration;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Debrief.Tools.Tote.Watchable;
import Debrief.Tools.Tote.WatchableList;
import Debrief.Wrappers.TrackWrapper_Support.SegmentList;
import Debrief.Wrappers.TrackWrapper_Support.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

/**
 * @author Administrator
 * 
 */
public class TrackWrapper_Test
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
	@Before
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
	@After
	public void tearDown() throws Exception
	{
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getName()}.
	 */
	@Test
	public void testGetName()
	{
		assertEquals("correct name", TRACK_NAME, _tw.getName());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getBounds()}.
	 */
	@Test
	public void testGetBounds()
	{
		WorldArea correctBounds = new WorldArea(new WorldLocation(1, 1, 0),
				new WorldLocation(4, 6, 0));
		assertEquals("wrong bounds returned", correctBounds, _tw.getBounds());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#add(MWC.GUI.Editable)}
	 * .
	 */
	@Test
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
		_tw.add(sw);

		assertEquals("got added", 8, this.trackLength());

	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#addFix(Debrief.Wrappers.FixWrapper)}.
	 */
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
	public void testFilterListTo()
	{
		assertEquals("start off with them all visible", 6, countVisibleItems());
		_tw.filterListTo(new HiResDate(200000), new HiResDate(400000));
		assertEquals("start off with them all visible", 3, countVisibleItems());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getEndDTG()}.
	 */
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
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
	@Test
	public void testGetUnfilteredItems()
	{
		assertEquals("found right num ", 3, _tw.getUnfilteredItems(
				new HiResDate(100000), new HiResDate(300000)).size());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#numFixes()}.
	 */
	@Test
	public void testNumFixes()
	{
		assertEquals("have correct num", 6, _tw.numFixes());
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#rangeFrom(MWC.GenericData.WorldLocation)}
	 * .
	 */
	@Test
	public void testRangeFromWorldLocation()
	{
		WorldLocation wl = new WorldLocation(4, 1, 0);
		double res = _tw.rangeFrom(wl);
		assertEquals("correct range", 0d, res);
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#removeElement(MWC.GUI.Editable)}.
	 */
	@Test
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
	@Test
	public void testShiftWorldLocationWorldVector()
	{
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#shift(MWC.GenericData.WorldVector)}.
	 */
	@Test
	public void testShiftWorldVector()
	{
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#shiftTrack(java.util.Enumeration, MWC.GenericData.WorldVector)}
	 * .
	 */
	@Test
	public void testShiftTrack()
	{
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#visibleBetween(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}
	 * .
	 */
	@Test
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
	 * Test method for
	 * {@link Debrief.Wrappers.TrackWrapper#splitTrack(Debrief.Wrappers.FixWrapper, boolean)}
	 * .
	 */
	@Test
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
	@Test
	public void testSplitTrack2()
	{
		Vector<TrackSegment> segs = _tw.splitTrack(fw1, false);
		assertEquals("now two segments", 2, segs.size());
		assertEquals("first is of correct length", 3, segs.firstElement().size());
		assertEquals("first is of correct length", 3, segs.lastElement().size());

	}

	@Test
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
	}

	@Test
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
	}


	@Test
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
		assertEquals("correct title", "Merge tracks", _messages._titles.firstElement());
		assertEquals("correct title", "Sorry, '010005.10' and 'test track' overlap in time. Please correct this and retry", _messages._messages.firstElement());
		assertEquals("correct title", MessageProvider.ERROR, _messages._statuses.firstElement());
	}

	
	// ////////////

	@Test
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


	@Test
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
	

	private FixWrapper createFix(int time, double vLat, double vLong)
	{
		FixWrapper fw = new FixWrapper(new Fix(new HiResDate(time),
				new WorldLocation(vLat, vLong, 0), 1, 1));
		return fw;
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

}
