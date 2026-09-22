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
package org.mwc.cmap.core.ui_support;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

public class OutlineNameSorter extends ViewerComparator {

	public static class EditableComparer implements Comparator<Editable> {
		@Override
		public int compare(final Editable arg0, final Editable arg1) {
			return compareEditables(arg0, arg1);
		}
	}

	public static interface NameSortHelper {
		boolean sortByDate();
	}

	/**
	 * The outline sort order must obey the Comparator contract (transitivity in
	 * particular). Since Java 7 the JDK sort detects breaches and throws
	 * "Comparison method violates its general contract!", which stops the Outline
	 * view from populating (issue #5199). The breaches all came from the same
	 * pattern: comparing two items by time when both have one, but by name
	 * otherwise, so the name order can contradict the time order.
	 */
	public static class TestSorter extends TestCase {

		/**
		 * check that the supplied comparator obeys the Comparator contract (sign
		 * symmetry and transitivity) for every triple of the supplied items
		 */
		private static <T> void assertComparatorContract(final Comparator<T> comp, final List<T> items) {
			for (final T a : items) {
				for (final T b : items) {
					final int ab = Integer.signum(comp.compare(a, b));
					final int ba = Integer.signum(comp.compare(b, a));
					assertEquals("sign symmetry broken for " + a + " / " + b, -ba, ab);
					for (final T c : items) {
						final int bc = Integer.signum(comp.compare(b, c));
						final int ac = Integer.signum(comp.compare(a, c));
						if (ab > 0 && bc > 0) {
							assertTrue("transitivity broken: " + a + " > " + b + " > " + c + ", but " + a + " > " + c
									+ " is false", ac > 0);
						}
						if (ab == 0) {
							assertEquals("equal items " + a + " / " + b + " compare differently against " + c, bc, ac);
						}
					}
				}
			}
		}

		private static LabelWrapper labelAt(final String name, final HiResDate dtg) {
			return new LabelWrapper(name, new WorldLocation(1, 1, 0), Color.RED, dtg, null);
		}

		private static TrackSegment segmentStarting(final long dtg, final String name) {
			final TrackSegment seg = new TrackSegment(false);
			seg.addFix(new FixWrapper(new Fix(new HiResDate(dtg), new WorldLocation(1, 1, 0), 0, 0)));
			// name it last, since adding the first fix relabels the segment with its date
			seg.setName(name);
			return seg;
		}

		private static SensorWrapper sensorStarting(final String name, final Long dtg) {
			final SensorWrapper sensor = new SensorWrapper(name);
			if (dtg != null) {
				sensor.add(new SensorContactWrapper("track", new HiResDate(dtg), null, 45d, new WorldLocation(1, 1, 0),
						Color.RED, name, 0, name));
			}
			return sensor;
		}

		private static Editable unwrap(final Object wrapped) {
			return ((EditableWrapper) wrapped).getEditable();
		}

		/** wrap the items as the outline content provider does */
		private static List<Object> wrap(final Editable... items) {
			final List<Object> res = new ArrayList<>();
			for (final Editable item : items) {
				res.add(new EditableWrapper.OrderedEditableWrapper(item, null, null, res.size()));
			}
			return res;
		}

		public void testLargeSegmentListSorts() {
			// mimic the real failure: a track with many TMA legs, whose names aren't in
			// time order, plus an infill segment that has lost its positions. The list is
			// long enough for the JDK to use TimSort, which checks the contract.
			final int numLegs = 60;
			final List<Editable> segments = new ArrayList<>();
			for (int i = 0; i < numLegs; i++) {
				segments.add(segmentStarting(1000L * (numLegs - i), "leg_" + i));
			}
			// the empty segment is named so that it falls among the legs by name
			final TrackSegment emptyInfill = new TrackSegment(false);
			emptyInfill.setName("leg_30_infill");
			segments.add(emptyInfill);
			final List<Object> items = wrap(segments.toArray(new Editable[0]));
			Collections.shuffle(items, new Random(1234));

			final OutlineNameSorter sorter = new OutlineNameSorter();
			final Comparator<Object> comp = sorter::compare;
			assertComparatorContract(comp, items);
			Collections.sort(items, comp);

			HiResDate lastStart = null;
			for (int i = 0; i < numLegs; i++) {
				final HiResDate thisStart = ((TrackSegment) unwrap(items.get(i))).startDTG();
				assertNotNull("timed legs come first", thisStart);
				if (lastStart != null) {
					assertTrue("legs in time order", thisStart.greaterThan(lastStart));
				}
				lastStart = thisStart;
			}
			assertSame("empty segment sorted last", emptyInfill, unwrap(items.get(numLegs)));
		}

		public void testSensorsSortedByDate() {
			// sensors named so that name order contradicts time order, plus one with no
			// contacts (so no start time)
			final SensorWrapper early = sensorStarting("b", 10000L);
			final SensorWrapper late = sensorStarting("a", 20000L);
			final SensorWrapper untimed = sensorStarting("aa", null);
			assertNull("sensor without contacts has no start time", untimed.getStartDTG());

			final OutlineNameSorter byDate = new OutlineNameSorter(() -> true);
			final Comparator<Object> comp = byDate::compare;
			final List<Object> items = wrap(untimed, late, early);
			assertComparatorContract(comp, items);

			Collections.sort(items, comp);
			assertSame("early first", early, unwrap(items.get(0)));
			assertSame("late second", late, unwrap(items.get(1)));
			assertSame("untimed last", untimed, unwrap(items.get(2)));

			// sorting by name must be unaffected
			final OutlineNameSorter byName = new OutlineNameSorter();
			final Comparator<Object> nameComp = byName::compare;
			assertComparatorContract(nameComp, items);
			Collections.sort(items, nameComp);
			assertSame("a first", late, unwrap(items.get(0)));
			assertSame("aa second", untimed, unwrap(items.get(1)));
			assertSame("b last", early, unwrap(items.get(2)));
		}

		public void testTrackSegmentsWithEmptySegment() {
			// segments named so that name order contradicts time order
			final TrackSegment early = segmentStarting(10000, "leg_b");
			final TrackSegment late = segmentStarting(20000, "leg_a");
			final TrackSegment empty = new TrackSegment(false);
			empty.setName("leg_aa");

			final OutlineNameSorter sorter = new OutlineNameSorter();
			final Comparator<Object> comp = sorter::compare;
			final List<Object> items = wrap(empty, late, early);
			assertComparatorContract(comp, items);

			Collections.sort(items, comp);
			assertSame("early first", early, unwrap(items.get(0)));
			assertSame("late second", late, unwrap(items.get(1)));
			assertSame("empty last", empty, unwrap(items.get(2)));
		}

		public void testWatchablesWithMissingTimes() {
			// labels named so that name order contradicts time order, plus one without a
			// time
			final LabelWrapper early = labelAt("b", new HiResDate(10000));
			final LabelWrapper late = labelAt("a", new HiResDate(20000));
			final LabelWrapper untimed = labelAt("aa", null);
			assertNull("label has no time", untimed.getTime());

			final Comparator<Editable> comp = new EditableComparer();
			final List<Editable> items = new ArrayList<>(Arrays.asList(untimed, late, early));
			assertComparatorContract(comp, items);

			Collections.sort(items, comp);
			assertSame("early first", early, items.get(0));
			assertSame("late second", late, items.get(1));
			assertSame("untimed last", untimed, items.get(2));
		}
	}

	@SuppressWarnings("unchecked")
	protected static int compareEditables(final Editable e1, final Editable e2) {
		// ha. if they're watchables, sort them in time order. Items without a
		// time go after those with one.
		int res = compareTimes(timeOf(e1), timeOf(e2));

		if (res == 0) {
			// neither has a time (or they share one), so fall back to the
			// items' own ordering, or failing that their names
			if ((e1 instanceof Comparable) && (e2 instanceof Comparable)) {
				@SuppressWarnings("rawtypes")
				final Comparable p1c = (Comparable) e1;
				@SuppressWarnings("rawtypes")
				final Comparable p2c = (Comparable) e2;
				res = p1c.compareTo(p2c);
			} else {
				final String p1Name = e1.getName();
				final String p2Name = e2.getName();
				res = p1Name.compareTo(p2Name);
			}
		}

		return res;
	}

	/**
	 * compare two optional times. Items with a time come before items without one,
	 * so a list that mixes them still has a consistent order: comparing such
	 * pairs by name instead would let the name order contradict the time order,
	 * breaking the Comparator contract (transitivity) and making the JDK sort
	 * throw "Comparison method violates its general contract!" (issue #5199).
	 *
	 * @return zero when neither has a time, or both have the same time, so the
	 *         caller can fall back to another key
	 */
	private static int compareTimes(final HiResDate ha, final HiResDate hb) {
		final int res;
		if ((ha != null) && (hb != null)) {
			res = ha.compareTo(hb);
		} else if (ha != null) {
			res = -1;
		} else if (hb != null) {
			res = 1;
		} else {
			res = 0;
		}
		return res;
	}

	private static HiResDate timeOf(final Editable item) {
		final HiResDate res;
		if (item instanceof Watchable) {
			res = ((Watchable) item).getTime();
		} else {
			res = null;
		}
		return res;
	}

	private final NameSortHelper _sortHelper;

	public OutlineNameSorter() {
		this(new NameSortHelper() {

			@Override
			public boolean sortByDate() {
				return false;
			}
		});
	}

	public OutlineNameSorter(final NameSortHelper helper) {
		_sortHelper = helper;
	}

	@SuppressWarnings("unchecked")
	public int compare(final Object e1, final Object e2) {
		final int res;

		if ((e1 instanceof Comparable) && (e2 instanceof Comparable)) {
			// special case. Just double-check they aren't sensor wrappers
			if (e1 instanceof EditableWrapper && e2 instanceof EditableWrapper) {
				final EditableWrapper p1 = (EditableWrapper) e1;
				final EditableWrapper p2 = (EditableWrapper) e2;

				if (p1.getEditable() instanceof SensorWrapper && p2.getEditable() instanceof SensorWrapper) {
					return compareSensors((SensorWrapper) p1.getEditable(), (SensorWrapper) p2.getEditable());
				} else {
					return compareEditables(p1.getEditable(), p2.getEditable());
				}
			} else {
				// just see if we have sorted editables
				final Comparable<Object> w1 = (Comparable<Object>) e1;
				final Comparable<Object> w2 = (Comparable<Object>) e2;
				res = w1.compareTo(w2);
			}
		} else {
			if (e1 instanceof EditableWrapper && e2 instanceof EditableWrapper) {
				final EditableWrapper p1 = (EditableWrapper) e1;
				final EditableWrapper p2 = (EditableWrapper) e2;

				return compareEditables(p1.getEditable(), p2.getEditable());
			} else {
				return e1.toString().compareTo(e2.toString());
			}
		}

		return res;
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		return compare(e1, e2);
	}

	private int compareSensors(final SensorWrapper s1, final SensorWrapper s2) {
		int res = 0;

		if (_sortHelper.sortByDate()) {
			// sensors without a start time (no cuts) go after those with one
			res = compareTimes(s1.getStartDTG(), s2.getStartDTG());
		}

		if (res == 0) {
			res = s1.getName().compareTo(s2.getName());
		}

		return res;
	}
}
