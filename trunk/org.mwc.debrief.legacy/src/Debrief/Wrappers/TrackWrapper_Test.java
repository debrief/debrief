/**
 * 
 */
package Debrief.Wrappers;

import static org.junit.Assert.assertEquals;

import java.util.Enumeration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Debrief.Tools.Tote.Watchable;
import Debrief.Tools.Tote.WatchableList;
import MWC.GUI.Editable;
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
	/** fixes we can easily refer to in a test..
	 * 
	 */
	private final FixWrapper fw1 = createFix(3000,2,3);
	private final FixWrapper fw2 = createFix(5000,2,3);
	private TrackWrapper _tw;

	private int _ctr = 0;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		_tw = new TrackWrapper();
		_tw.setName(TRACK_NAME);
		_tw.addFix(createFix(1000,1,1));
		_tw.addFix(createFix(2000,2,3));
		_tw.addFix(fw1);
		_tw.addFix(createFix(4000,3,3));
		_tw.addFix(fw2);
		_tw.addFix(createFix(6000,4,6));
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
		assertEquals("correct name",  TRACK_NAME, _tw.getName());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getBounds()}.
	 */
	@Test
	public void testGetBounds()
	{
		WorldArea correctBounds = new WorldArea(new WorldLocation(1,1,0), new WorldLocation(4,6,0));
		assertEquals("wrong bounds returned", correctBounds, _tw.getBounds());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#add(MWC.GUI.Editable)}.
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
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#addFix(Debrief.Wrappers.FixWrapper)}.
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
		SensorWrapper sw = new SensorWrapper("some sensor"){
			private static final long serialVersionUID = 1L;
			public void setHost(WatchableList host)
			{
				super.setHost(host);
				_ctr++;
			}};
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
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#append(MWC.GUI.Layer)}.
	 */
	@Test
	public void testAppend()
	{
		TrackWrapper tw2 = new TrackWrapper();
		FixWrapper f1 = createFix(13,2,2);
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
		sw.add(new SensorContactWrapper("trk", new HiResDate(12), null, 0, null, null,null,0,null ));
		sw.add(new SensorContactWrapper("trk", new HiResDate(13), null, 0, null, null,null,0,null ));
		sw.add(new SensorContactWrapper("trk", new HiResDate(14), null, 0, null, null,null,0,null ));
		
		_tw.add(sw);
		
		Enumeration<Editable> tester = _tw.contiguousElements();
		_ctr = 0;
		while(tester.hasMoreElements())
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
		while(list.hasMoreElements())
		{
			_ctr++;
			list.nextElement();
		}
		assertEquals("just has positions", 1, _ctr);

		// give it a little more data
		SensorWrapper sw = new SensorWrapper("sensor a");
		sw.add(new SensorContactWrapper("trk", new HiResDate(12), null, 0, null, null,null,0,null ));
		sw.add(new SensorContactWrapper("trk", new HiResDate(13), null, 0, null, null,null,0,null ));
		sw.add(new SensorContactWrapper("trk", new HiResDate(14), null, 0, null, null,null,0,null ));
		_tw.add(sw);
		
		list = _tw.elements();
		_ctr = 0;
		while(list.hasMoreElements())
		{
			_ctr++;
			list.nextElement();
		}
		assertEquals("shows new item", 2, _ctr);
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#filterListTo(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}.
	 */
	@Test
	public void testFilterListTo()
	{
		assertEquals("start off with them all visible", 6, countVisibleItems());
		_tw.filterListTo(new HiResDate(2000), new HiResDate(4000));
		assertEquals("start off with them all visible", 3, countVisibleItems());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#findNearestHotSpotIn(java.awt.Point, MWC.GenericData.WorldLocation, MWC.GUI.Shapes.HasDraggableComponents.ComponentConstruct, MWC.GUI.Layer)}.
	 */
	@Test
	public void testFindNearestHotSpotInPointWorldLocationComponentConstructLayer()
	{
	//	fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#findNearestHotSpotIn(java.awt.Point, MWC.GenericData.WorldLocation, MWC.GUI.Shapes.DraggableItem.LocationConstruct, MWC.GUI.Layer)}.
	 */
	@Test
	public void testFindNearestHotSpotInPointWorldLocationLocationConstructLayer()
	{
//		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getEndDTG()}.
	 */
	@Test
	public void testGetEndDTG()
	{
		HiResDate dt = _tw.getEndDTG();
		assertEquals("correct end time", 6000, dt.getDate().getTime());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getItemsBetween(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}.
	 */
	@Test
	public void testGetItemsBetween()
	{
		assertEquals("found the items", 2, _tw.getItemsBetween(new HiResDate(2003), new HiResDate(4003)).size());
		assertEquals("found the items", 3, _tw.getItemsBetween(new HiResDate(0), new HiResDate(3000)).size());
		assertEquals("found the items", 6, _tw.getItemsBetween(new HiResDate(1000), new HiResDate(8000)).size());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getNearestTo(MWC.GenericData.HiResDate)}.
	 */
	@Test
	public void testGetNearestTo()
	{
		Watchable[] res = _tw.getNearestTo(new HiResDate(3000));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", fw1, res[0]);

		res = _tw.getNearestTo(new HiResDate(5000));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", fw2, res[0]);

		res = _tw.getNearestTo(new HiResDate(4005));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", fw2, res[0]);

		res = _tw.getNearestTo(new HiResDate(5000));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", fw2, res[0]);

		res = _tw.getNearestTo(new HiResDate(2995));
		assertEquals("found one", 1, res.length);
		assertEquals("found right one", fw1, res[0]);
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getPositions()}.
	 */
	@Test
	public void testGetPositions()
	{
		Enumeration<Editable> list= _tw.getPositions();
		// check the length
		_ctr = 0;
		while(list.hasMoreElements())
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
		assertEquals("correct end time", 1000, dt.getDate().getTime());
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#getUnfilteredItems(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}.
	 */
	@Test
	public void testGetUnfilteredItems()
	{
		assertEquals("found right num ", 3, _tw.getUnfilteredItems(new HiResDate(1000), new HiResDate(3000)).size());
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
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#rangeFrom(MWC.GenericData.WorldLocation)}.
	 */
	@Test
	public void testRangeFromWorldLocation()
	{
		WorldLocation wl = new WorldLocation(4,1,0);
		double res = _tw.rangeFrom(wl);
		assertEquals("correct range", 0d, res);
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#removeElement(MWC.GUI.Editable)}.
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
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#shift(MWC.GenericData.WorldLocation, MWC.GenericData.WorldVector)}.
	 */
	@Test
	public void testShiftWorldLocationWorldVector()
	{
	//	fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#shift(MWC.GenericData.WorldVector)}.
	 */
	@Test
	public void testShiftWorldVector()
	{
	//	fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#shiftTrack(java.util.Enumeration, MWC.GenericData.WorldVector)}.
	 */
	@Test
	public void testShiftTrack()
	{
	//	fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#visibleBetween(MWC.GenericData.HiResDate, MWC.GenericData.HiResDate)}.
	 */
	@Test
	public void testVisibleBetween()
	{
		assertEquals("is vis", false, _tw.visibleBetween(new HiResDate(7000), new HiResDate(9000)));
		assertEquals("is vis", true, _tw.visibleBetween(new HiResDate(0000), new HiResDate(3000)));
		assertEquals("is vis", true, _tw.visibleBetween(new HiResDate(3000), new HiResDate(5000)));
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#splitTrack(Debrief.Wrappers.FixWrapper, boolean)}.
	 */
	@Test
	public void testSplitTrack()
	{
//		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#combineSections(java.util.Vector)}.
	 */
	@Test
	public void testCombineSections()
	{
//		fail("Not yet implemented"); // TODO
	}

	private FixWrapper createFix(int time, double vLat, double vLong)
	{
		FixWrapper fw = new FixWrapper(new Fix(new HiResDate(time), new WorldLocation(vLat, vLong,0),1,1 ));
		return fw;
	}

	private int trackLength()
	{
		Enumeration<Editable> all = _tw.contiguousElements();
		int ctr = 0;
		while(all.hasMoreElements())
		{
			ctr ++;
			all.nextElement();
		}
		return ctr;
	}
	
	private int countVisibleItems()
	{
		Enumeration<Editable> all = _tw.contiguousElements();
		int ctr = 0;
		while(all.hasMoreElements())
		{
			Plottable thisE = (Plottable) all.nextElement();
			if(thisE.getVisible())
				ctr ++;
		}
		return ctr;
	}
	
}
