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

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.cmap.core.ui_support.OutlineNameSorter.*;
import org.mwc.cmap.core.ui_support.OutlineNameSorter.*;
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
import org.junit.jupiter.api.Test;

/**
 * The outline sort order must obey the Comparator contract (transitivity in
 * particular). Since Java 7 the JDK sort detects breaches and throws
 * "Comparison method violates its general contract!", which stops the Outline
 * view from populating (issue #5199). The breaches all came from the same
 * pattern: comparing two items by time when both have one, but by name
 * otherwise, so the name order can contradict the time order.
 *
 * Note that the product file (debriefng.product) still sets
 * -Djava.util.Arrays.useLegacyMergeSort=true, which suppresses the JDK's
 * check. That flag is a safety net that the JDK may withdraw; these tests are
 * what keeps the sort order correct without it.
 */
public class OutlineNameSorterTest {

	/**
	 * check that the supplied comparator obeys the Comparator contract (sign
	 * symmetry and transitivity) for every triple of the supplied items
	 */
	private static <T> void assertComparatorContract(final Comparator<T> comp, final List<T> items) {
		for (final T a : items) {
			for (final T b : items) {
				final int ab = Integer.signum(comp.compare(a, b));
				final int ba = Integer.signum(comp.compare(b, a));
				assertEquals(-ba, ab, "sign symmetry broken for " + a + " / " + b);
				for (final T c : items) {
					final int bc = Integer.signum(comp.compare(b, c));
					final int ac = Integer.signum(comp.compare(a, c));
					if (ab > 0 && bc > 0) {
						assertTrue(ac > 0, "transitivity broken: " + a + " > " + b + " > " + c + ", but " + a + " > " + c
								+ " is false");
					}
					if (ab == 0) {
						assertEquals(bc, ac, "equal items " + a + " / " + b + " compare differently against " + c);
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

	@Test
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
			assertNotNull(thisStart, "timed legs come first");
			if (lastStart != null) {
				assertTrue(thisStart.greaterThan(lastStart), "legs in time order");
			}
			lastStart = thisStart;
		}
		assertSame(emptyInfill, unwrap(items.get(numLegs)), "empty segment sorted last");
	}

	@Test
	public void testSensorsSortedByDate() {
		// sensors named so that name order contradicts time order, plus one with no
		// contacts (so no start time)
		final SensorWrapper early = sensorStarting("b", 10000L);
		final SensorWrapper late = sensorStarting("a", 20000L);
		final SensorWrapper untimed = sensorStarting("aa", null);
		assertNull(untimed.getStartDTG(), "sensor without contacts has no start time");

		final OutlineNameSorter byDate = new OutlineNameSorter(() -> true);
		final Comparator<Object> comp = byDate::compare;
		final List<Object> items = wrap(untimed, late, early);
		assertComparatorContract(comp, items);

		Collections.sort(items, comp);
		assertSame(early, unwrap(items.get(0)), "early first");
		assertSame(late, unwrap(items.get(1)), "late second");
		assertSame(untimed, unwrap(items.get(2)), "untimed last");

		// sorting by name must be unaffected
		final OutlineNameSorter byName = new OutlineNameSorter();
		final Comparator<Object> nameComp = byName::compare;
		assertComparatorContract(nameComp, items);
		Collections.sort(items, nameComp);
		assertSame(late, unwrap(items.get(0)), "a first");
		assertSame(untimed, unwrap(items.get(1)), "aa second");
		assertSame(early, unwrap(items.get(2)), "b last");
	}

	@Test
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
		assertSame(early, unwrap(items.get(0)), "early first");
		assertSame(late, unwrap(items.get(1)), "late second");
		assertSame(empty, unwrap(items.get(2)), "empty last");
	}

	@Test
	public void testWatchablesWithMissingTimes() {
		// labels named so that name order contradicts time order, plus one without a
		// time
		final LabelWrapper early = labelAt("b", new HiResDate(10000));
		final LabelWrapper late = labelAt("a", new HiResDate(20000));
		final LabelWrapper untimed = labelAt("aa", null);
		assertNull(untimed.getTime(), "label has no time");

		final Comparator<Editable> comp = new EditableComparer();
		final List<Editable> items = new ArrayList<>(Arrays.asList(untimed, late, early));
		assertComparatorContract(comp, items);

		Collections.sort(items, comp);
		assertSame(early, items.get(0), "early first");
		assertSame(late, items.get(1), "late second");
		assertSame(untimed, items.get(2), "untimed last");
	}
}
