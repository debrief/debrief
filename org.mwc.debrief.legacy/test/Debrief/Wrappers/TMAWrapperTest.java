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

package Debrief.Wrappers;

import static Debrief.Wrappers.TMAWrapper.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.Wrappers.TMAWrapper.*;
import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact.PlottableWrapperWithTimeAndOverrideableColor;
import MWC.GUI.Editable;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

// //////////////////////////////////////
// member variables
// //////////////////////////////////////

public final class TMAWrapperTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testDuplicates() {
		// ok, create the test object
		final TMAWrapper sensor = new TMAWrapper("tester");

		final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

		// and create the list of sensor contact data items
		cal.set(2001, 10, 4, 4, 4, 0);
		sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
				null, null, null, null));

		cal.set(2001, 10, 4, 4, 4, 23);
		sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
				null, null, null, null));

		cal.set(2001, 10, 4, 4, 4, 24);
		sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
				null, null, null, null));

		cal.set(2001, 10, 4, 4, 4, 25);
		sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
				null, null, null, null));

		cal.set(2001, 10, 4, 4, 4, 25);
		sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
				null, null, null, null));

		cal.set(2001, 10, 4, 4, 4, 01);
		sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
				null, null, null, null));

		cal.set(2001, 10, 4, 4, 4, 05);
		sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
				null, null, null, null));

		cal.set(2001, 10, 4, 4, 4, 55);
		sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
				null, null, null, null));

		// so, we've now build up the list
		// check it has the correct quantity
		assertEquals(8, sensor._myContacts.size(), "Count of items");

		// check the correct number get returned
		cal.set(2001, 10, 4, 4, 4, 25);
		final Watchable[] list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime()));
		assertEquals(2, list.length, "after end of data");

	}

	/**
	 * resample a range/bearing solution whose course and bearing pass through
	 * north
	 */
	@Test
	public void testResampleThroughNorth() {
		final TMAWrapper sol = new TMAWrapper("sol");
		final long t0 = 60000;
		sol.add(new TMAContactWrapper("sol", "trk", new HiResDate(t0), 1000, 350, 350, 5, 10, null, "a", null,
				null));
		sol.add(new TMAContactWrapper("sol", "trk", new HiResDate(t0 + 60000), 3000, 10, 10, 7, 400, null, "b",
				null, null));

		// resample every 30 secs (micros)
		sol.decimate(new HiResDate(30000), t0 * 1000);
		assertEquals(3, sol._myContacts.size(), "correct number of decimated");

		TMAContactWrapper mid = null;
		for (final Editable ed : sol._myContacts) {
			final TMAContactWrapper tc = (TMAContactWrapper) ed;
			if (tc.getDTG().getDate().getTime() == t0 + 30000) {
				mid = tc;
			}
		}
		assertNotNull(mid, "found mid-point");
		assertEquals(0d, MWC.Algorithms.Conversions.signedDegs(mid.getTargetCourse()), 0.001,
				"course through north");
		assertEquals(0d, MWC.Algorithms.Conversions.signedDegs(mid.getBearing()), 0.001, "bearing through north");
		assertEquals(6d, mid.getSpeed(), 0.001, "speed");
		assertEquals(205d, mid.getDepth(), 0.001, "depth interpolated linearly");
		assertEquals(2000d, mid.getRange().getValueIn(WorldDistance.YARDS), 0.001, "range interpolated");
	}

	@Test
	public void testMultipleContacts() {
		final TMAContactWrapper sc1 = new TMAContactWrapper("aaa", "bbb", new HiResDate(12), 0, 0, 0, 0, 0, null,
				"first", null, null);
		final TMAContactWrapper sc2 = new TMAContactWrapper("aaa", "bbb", new HiResDate(13), 0, 0, 0, 0, 0, null,
				"first", null, null);
		final TMAContactWrapper sc3 = new TMAContactWrapper("aaa", "bbb", new HiResDate(12), 0, 0, 0, 0, 0, null,
				"first", null, null);
		final TMAContactWrapper sc4 = new TMAContactWrapper("aaa", "bbb", new HiResDate(15), 0, 0, 0, 0, 0, null,
				"first", null, null);

		final TMAWrapper sw = new TMAWrapper("bbb");
		sw.add(sc1);
		sw.add(sc2);
		sw.add(sc3);
		sw.add(sc4);

		assertEquals(4, sw._myContacts.size(), "four contacts loaded");

		// check we can delete from it
		sw.removeElement(sc3);

		assertEquals(3, sw._myContacts.size(), "now only three contacts loaded");

	}

	@Test
	public final void testValues() {
		// ok, create the test object
		final TMAWrapper solution = new TMAWrapper("tester");

		final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

		int solutionCounter = 1;

		// and create the list of sensor contact data items
		cal.set(2001, 10, 4, 4, 4, 0);
		final long start_time = cal.getTime().getTime();
		solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
				0, null, "sol:" + solutionCounter++, null, null));

		cal.set(2001, 10, 4, 4, 4, 23);
		solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
				0, null, "sol:" + solutionCounter++, null, null));

		cal.set(2001, 10, 4, 4, 4, 25);
		solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
				0, null, "sol:" + solutionCounter++, null, null));

		cal.set(2001, 10, 4, 4, 4, 27);
		solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
				0, null, "sol:" + solutionCounter++, null, null));

		cal.set(2001, 10, 4, 4, 4, 02);
		solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
				0, null, "sol:" + solutionCounter++, null, null));

		cal.set(2001, 10, 4, 4, 4, 01);
		solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
				0, null, "sol:" + solutionCounter++, null, null));

		cal.set(2001, 10, 4, 4, 4, 05);
		solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
				0, null, "sol:" + solutionCounter++, null, null));

		cal.set(2001, 10, 4, 4, 4, 55);
		final long end_time = cal.getTime().getTime();
		solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
				0, null, null, null, null));

		// so, we've now build up the list
		// check it has the correct quantity
		assertTrue((solution._myContacts.size() == 8), "Count of items");

		// check the outer limits
		final HiResDate start = solution.getStartDTG();
		final HiResDate end = solution.getEndDTG();
		assertTrue((start_time == start.getDate().getTime()), "first time");
		assertTrue((end_time == end.getDate().getTime()), "last time");

		// //////////////////////////////////////////////////////////////////////
		// finding the nearest entry
		cal.set(2001, 10, 4, 4, 4, 05);
		Watchable[] list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
		TMAContactWrapper nearest = (TMAContactWrapper) list[0];
		assertEquals(nearest.getDTG().getDate().getTime(), cal.getTime().getTime(), "Nearest matching fix");

		final java.util.Calendar cal_other = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);
		cal_other.set(2001, 10, 4, 4, 4, 03);
		list = solution.getNearestTo(new HiResDate(cal_other.getTime().getTime()));
		nearest = (TMAContactWrapper) list[0];
		assertTrue((nearest.getDTG().getDate().getTime() == cal.getTime().getTime()),
				"Nearest or greater than fix");

		// ///////////////////////////////////////////////////////////////////
		// filter the list
		cal.set(2001, 10, 4, 4, 4, 22);
		cal_other.set(2001, 10, 4, 4, 4, 25);

		// ////////////////////////////////////////////////////////////////////////
		// do the filter
		solution.filterListTo(new HiResDate(cal.getTime().getTime()), new HiResDate(cal_other.getTime().getTime()));

		// see how many remain visible
		java.util.Enumeration<Editable> iter = solution.elements();
		int counter = 0;
		while (iter.hasMoreElements()) {
			final TMAContactWrapper contact = (TMAContactWrapper) iter.nextElement();
			if (contact.getVisible())
				counter++;
		}
		// check that the correct number are visible
		assertTrue((counter == 2), "Correct filtering of list");

		// clear the filter
		solution.filterListTo(solution.getStartDTG(), solution.getEndDTG());
		// see how many remain visible
		iter = solution.elements();
		counter = 0;
		while (iter.hasMoreElements()) {
			final TMAContactWrapper contact = (TMAContactWrapper) iter.nextElement();
			if (contact.getVisible())
				counter++;
		}
		// check that the correct number are visible
		assertTrue((counter == 8), "Correct removal of list filter");

		// //////////////////////////////////////////////////////
		// get items between
		java.util.Collection<Editable> res = solution.getItemsBetween(new HiResDate(cal.getTime().getTime()),
				new HiResDate(cal_other.getTime().getTime()));
		assertTrue((res.size() == 2), "get items between");

		// do recheck, since this time we will be resetting the working variables,
		// rather and creating them
		cal.set(2001, 10, 4, 4, 4, 5);
		cal_other.set(2001, 10, 4, 4, 4, 27);
		res = solution.getItemsBetween(new HiResDate(cal.getTime().getTime()),
				new HiResDate(cal_other.getTime().getTime()));
		assertEquals(4, res.size(), "recheck get items between:" + res.size());

		// and show all of the data
		res = solution.getItemsBetween(solution.getStartDTG(), solution.getEndDTG());
		assertTrue((res.size() == 8), "recheck get items between:" + res.size());

		// /////////////////////////////////////////////////////////
		// test the position related stuff
		final TrackWrapper track = new TrackWrapper();

		// and add the fixes
		cal.set(2001, 10, 4, 4, 4, 0);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new WorldLocation(2.0, 2.0, 0.0), 12, 12)));

		cal.set(2001, 10, 4, 4, 4, 01);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new WorldLocation(2.0, 2.25, 0.0), 12, 12)));

		cal.set(2001, 10, 4, 4, 4, 02);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new WorldLocation(2.0, 2.5, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 05);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new WorldLocation(2.0, 2.75, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 23);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new WorldLocation(2.25, 2.0, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 25);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new WorldLocation(2.5, 2.0, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 28);
		final WorldLocation theLoc = new WorldLocation(2.75d, 2.0, 0.0);
		track.addFix(new FixWrapper(
				new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0), theLoc, 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 55);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new WorldLocation(2.25, 2.25, 0.0), 12, 12)));

		// ok, put the sensor data into the track
		track.add(solution);

		// now find the location of an item, any item!
		cal.set(2001, 10, 4, 4, 4, 27);
		list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
		nearest = (TMAContactWrapper) list[0];
		final WorldLocation theCentre = nearest.getCentre(track);
		assertEquals(new WorldLocation(2.75, 2.0, 0.0), theCentre, "first test");

		// ah-ha! what about a contact between two fixes
		cal.set(2001, 10, 4, 4, 4, 26);
		list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
		nearest = (TMAContactWrapper) list[0];
		assertEquals(nearest.getCentre(track), new WorldLocation(2.75, 2.0, 0.0), "test mid way");

		// ok, that was half-way, what making it nearer to one of the fixes
		cal.set(2001, 10, 4, 4, 4, 25);
		list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
		nearest = (TMAContactWrapper) list[0];
		assertEquals(nearest.getCentre(track), (new WorldLocation(2.5, 2.0, 0.0)), "test nearer first point");

		// start point?
		cal.set(2001, 10, 4, 4, 4, 0);
		list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
		nearest = (TMAContactWrapper) list[0];
		assertEquals(nearest.getCentre(track), new WorldLocation(2.0, 2.0, 0.0), "test start point");

		// end point?
		cal.set(2001, 10, 4, 4, 4, 55);
		list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
		nearest = (TMAContactWrapper) list[0];
		assertEquals(nearest.getCentre(track), new WorldLocation(2.25, 2.25, 0.0), "test end point");

		// before start of track data?
		cal.set(2001, 10, 4, 4, 3, 0);
		list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
		assertEquals(list.length, 0, "before range of data");

		// after end of track data?
		cal.set(2001, 10, 4, 4, 7, 0);
		list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
		assertEquals(1, list.length, "after end of data");

	}
}
