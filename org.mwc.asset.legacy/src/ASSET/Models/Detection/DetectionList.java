
package ASSET.Models.Detection;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

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

import ASSET.Models.Decision.TargetType;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.Initial.OpticSensor;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Category;
import ASSET.Util.SupportTesting;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

/**
 * object containing a series of detections recorded for a single platform at a
 * particular instant. Each detections is the product of a
 * {@link ASSET.ParticipantType participant} using a
 * {@link ASSET.Models.SensorType sensor} to detect another
 * {@link ASSET.ParticipantType participant}.
 */
public class DetectionList extends Vector<DetectionEvent> {

	/**
	 * our list of detections
	 *
	 */
	// private Vector _list;

	//////////////////////////////////////////////////
	// property testing
	//////////////////////////////////////////////////
	public static class ListTest extends SupportTesting {
		public ListTest(final String s) {
			super(s);
		}

		public void testFullLists() {
			final SSN ssnA = new SSN(44);
			final SSN ssnB = new SSN(45);
			final CoreSensor newSensor = new OpticSensor(22);
			final DetectionEvent de1 = new DetectionEvent(12, 12, null, newSensor, null, null, null, null, null,
					new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
					ssnA);
			final DetectionEvent de2 = new DetectionEvent(13, 12, null, newSensor,
					new WorldDistance(12, WorldDistance.NM), null, null, null, null,
					new Category(Category.Force.GREEN, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
					ssnA);
			final DetectionEvent de3 = new DetectionEvent(11, 12, null, newSensor, null, null, null, null, null,
					new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE),
					new WorldSpeed(12, WorldSpeed.Kts), null, ssnA, DetectionEvent.DETECTED);
			final DetectionEvent de4 = new DetectionEvent(10, 12, null, newSensor,
					new WorldDistance(3, WorldDistance.NM), null, null, null, null,
					new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
					ssnA);
			final DetectionEvent de5 = new DetectionEvent(17, 12, null, newSensor,
					new WorldDistance(7, WorldDistance.NM), null, null, null, null,
					new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
					ssnB);
			final DetectionEvent de6 = new DetectionEvent(15, 12, null, newSensor, null, null, null, null, null,
					new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE),
					new WorldSpeed(12, WorldSpeed.Kts), null, ssnB, DetectionEvent.CLASSIFIED);

			final DetectionList dl = new DetectionList();
			dl.add(de1);
			dl.add(de2);
			dl.add(de3);
			dl.add(de4);
			dl.add(de5);
			dl.add(de6);

			// ok, do some tests
			assertEquals("wrong number of items in list", 6, dl.size(), 0);

			// see which is the nearest
			final DetectionEvent nearest = dl.getNearestDetection();
			assertEquals("failed to find closest detection :", de4, nearest);

			final TargetType redType = new TargetType(Category.Force.RED);
			final DetectionList reds = dl.getDetectionsOf(redType);
			assertEquals("wrong number of red targets", 3, reds.size());

			final DetectionEvent mostRecent = reds.getMostRecentDetection();
			assertEquals("wrong detection returned as most recent", de5, mostRecent);

			// couple of other quick ones
			final DetectionList validDets = dl.getDetectionsOf(ssnB.getId());
			assertEquals("wrong number of ssnb detections", 2, validDets.size(), 0);
			assertEquals("wrong one returned as most recent", de5, validDets.getMostRecentDetection());
			assertEquals("wrong one returned as overall most recent", de5, dl.getMostRecentDetection());
			// DetectionEvent bestDet = validDets.getBestDetection();
			assertEquals("wrong one returned as best", de6, dl.getBestDetection());

		}

		public void testLists() {
			final SSN ssnA = new SSN(44);
			final SSN ssnB = new SSN(45);
			final SSN ssnC = new SSN(65);
			final CoreSensor newSensor = new OpticSensor(22);
			final DetectionEvent de1 = new DetectionEvent(12, 12, null, newSensor, null, null, null, null, null,
					new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
					ssnA);
			final DetectionEvent de2 = new DetectionEvent(13, 12, null, newSensor, null, null, null, null, null,
					new Category(Category.Force.GREEN, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
					ssnA);
			final DetectionEvent de3 = new DetectionEvent(11, 12, null, newSensor, null, null, null, null, null,
					new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
					ssnA);
			final DetectionEvent de4 = new DetectionEvent(10, 12, null, newSensor, null, null, null, null, null,
					new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
					ssnC);
			final DetectionEvent de5 = new DetectionEvent(17, 12, null, newSensor, null, null, null, null, null,
					new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
					ssnB);
			final DetectionEvent de6 = new DetectionEvent(15, 12, null, newSensor, null, null, null, null, null,
					new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
					ssnB);

			final DetectionList dl = new DetectionList();
			dl.add(de1);
			dl.add(de2);
			dl.add(de3);
			dl.add(de4);
			dl.add(de5);
			dl.add(de6);

			// ok, do some tests
			assertEquals("wrong number of items in list", 6, dl.size(), 0);
			DetectionList myDets = dl.getDetectionsOf(new Integer(ssnC.getId()), new TargetType(Category.Force.RED));
			assertEquals("didn't find the target we're after", 1, myDets.size(), 0);
			assertEquals("didn't find the target we're after", de4, myDets.getMostRecentDetection());
			myDets = dl.getDetectionsOf(new Integer(712), new TargetType(Category.Force.RED));
			assertEquals("didn't find the target we're after", 3, myDets.size(), 0);
			assertEquals("didn't find the target we're after", de5, myDets.getMostRecentDetection());
			myDets = dl.getDetectionsOf(null, new TargetType(Category.Force.RED));
			assertEquals("didn't find the target we're after", 3, myDets.size(), 0);
			assertEquals("didn't find the target we're after", de5, myDets.getMostRecentDetection());
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////
	// constructor
	/////////////////////////////////////////////////

	/**
	 * the time period we cover
	 */
	private TimePeriod _thePeriod;

	public DetectionList() {
		// _list = new Vector(0,1);
		_thePeriod = new TimePeriod.BaseTimePeriod(TimePeriod.INVALID_DATE, TimePeriod.INVALID_DATE);
	}

	/**
	 * Constructs a vector containing the elements of the specified collection, in
	 * the order they are returned by the collection's iterator.
	 *
	 * @param collection the collection whose elements are to be placed into this
	 *                   vector.
	 * @throws NullPointerException if the specified collection is null.
	 * @since 1.2
	 */
	public DetectionList(final Collection<DetectionEvent> collection) {
		super(collection);
	}

	/**
	 * add the specific detection to our list
	 *
	 * @param event the detection to add
	 */
	@Override
	public boolean add(final DetectionEvent event) {
		// extend our time period to include this
		super.add(event);

		// and extend our time period
		_thePeriod.extend(new HiResDate(event.getTime()));

		return true;
	}

	/**
	 * combine this list with the set of detections passed in
	 *
	 * @param other the set of detections to append to ourselves
	 */
	public void extend(final DetectionList other) {
		if (other != null) {
			if (other.size() > 0) {
				addAll(other);
				// also extend our time period
				_thePeriod.extend(other.getTimeCoverage().getStartDTG());
				_thePeriod.extend(other.getTimeCoverage().getEndDTG());
			}
		}

	}

	/**
	 * find the best detection - the on with the highest classification level
	 *
	 * @return the highest detection in the list
	 * @see DetectionEvent.IDENTIFIED
	 */
	public DetectionEvent getBestDetection() {
		DetectionEvent res = null;

		// create the list to store the items
		final SortedSet<DetectionEvent> ss = new TreeSet<DetectionEvent>(new Comparator<DetectionEvent>() {
			@Override
			public int compare(final DetectionEvent d1, final DetectionEvent d2) {
				int resa = 0;

				if (d1.getDetectionState() < d2.getDetectionState())
					resa = -1;
				else if (d1.getDetectionState() > d2.getDetectionState())
					resa = 1;

				return resa;
			}
		});

		// store them
		ss.addAll(this);

		// get the last one
		res = ss.last();

		// and return it.
		return res;
	}

	/**
	 * retrieve a particular detection from our list
	 *
	 * @param index
	 * @return
	 */
	public DetectionEvent getDetection(final int index) {
		return get(index);
	}

	/**
	 * loop through this list of detections and produce a sub-set related to the
	 * supplied target
	 *
	 * @param targetId = the target we're looking for
	 * @return valid subset of detections (or null)
	 */
	public DetectionList getDetectionsOf(final int targetId) {
		DetectionList res = null;

		// loop through our detections
		final Iterator<DetectionEvent> it = iterator();
		while (it.hasNext()) {
			final DetectionEvent de = it.next();
			// is this our target?
			if (de.getTarget() == targetId) {
				if (res == null)
					res = new DetectionList();

				// remember this detection
				res.add(de);
			}
		}

		// and return the goods
		return res;
	}

	/**
	 * get detections, either of our current target (favourite), else of any
	 * compatible targets
	 *
	 * @param targetId     the (optional) id we're currently tracking
	 * @param myTargetType the type of target we're after in general
	 * @return list of valid detections (or null)
	 */
	public DetectionList getDetectionsOf(final Integer targetId, final TargetType myTargetType) {
		DetectionList res = null;

		// have we received a target id?
		if (targetId != null) {
			// first try for this id
			res = getDetectionsOf(targetId.intValue());
		}

		// did we find any
		if (res == null) {
			// no, just try for something compatible
			res = getDetectionsOf(myTargetType);
		}

		// done
		return res;
	}

	/**
	 * step through the list and find the matching detections
	 *
	 * @param subject the type of detection we're looking for
	 * @return the any matching detections (or null)
	 */
	public DetectionList getDetectionsOf(final TargetType subject) {
		DetectionList res = null;

		final Iterator<DetectionEvent> it = iterator();
		while (it.hasNext()) {
			final DetectionEvent de = it.next();
			if (subject.matches(de.getTargetType())) {
				if (res == null)
					res = new DetectionList();

				res.add(de);
			}
		}

		return res;
	}

	/**
	 * find the most recent detection from our current list
	 *
	 * @return the most recent detection in the list
	 */
	public DetectionEvent getMostRecentDetection() {
		DetectionEvent res = null;

		// create the list to store the items
		final SortedSet<DetectionEvent> ss = new TreeSet<DetectionEvent>(new Comparator<DetectionEvent>() {
			@Override
			public int compare(final DetectionEvent d1, final DetectionEvent d2) {
				int resa = 0;

				if (d1.getTime() < d2.getTime())
					resa = -1;
				else if (d1.getTime() > d2.getTime())
					resa = 1;

				return resa;
			}
		});

		// store them
		ss.addAll(this);

		// get the last one
		res = ss.last();

		// and return it.
		return res;
	}

	/**
	 * find the most recent detection from our current list
	 *
	 * @return the most recent detection in the list
	 */
	public DetectionEvent getNearestDetection() {
		DetectionEvent res = null;

		// create the list to store the items
		final SortedSet<DetectionEvent> ss = new TreeSet<DetectionEvent>(new Comparator<DetectionEvent>() {
			@Override
			public int compare(final DetectionEvent d1, final DetectionEvent d2) {
				int resa = 0;

				if (d1.getRange().lessThan(d2.getRange()))
					resa = -1;
				else if (d1.getRange().greaterThan(d2.getRange()))
					resa = 1;

				return resa;

			}
		});

		// only add the detections which have a range
		final Iterator<DetectionEvent> iter = this.iterator();
		while (iter.hasNext()) {
			final DetectionEvent thisDet = iter.next();
			// do we know range?
			if (thisDet.getEstimatedRange() != null) {
				ss.add(thisDet);
			}
		}

		// get the first (nearest) one
		res = ss.first();

		// and return it.
		return res;
	}

	/**
	 * all access to the time period we cover
	 *
	 * @return our time period
	 */
	public TimePeriod getTimeCoverage() {
		return _thePeriod;
	}

}