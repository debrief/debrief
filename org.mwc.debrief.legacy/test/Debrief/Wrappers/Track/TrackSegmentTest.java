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

import static Debrief.Wrappers.Track.TrackSegment.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.Wrappers.Track.TrackSegment.*;
import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Vector;
import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.CreationOrder;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.BaseItemLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.ICantContainLayers;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsWrappingInLayerManager;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import org.junit.jupiter.api.Test;

public class TrackSegmentTest {

	private static class MockParent implements ToolParent {
		final private List<String> log;

		public MockParent(final List<String> log) {
			this.log = log;
		}

		@Override
		public void addActionToBuffer(final Action theAction) {
			// TODO Auto-generated method stub

		}

		@Override
		public Map<String, String> getPropertiesLike(final String pattern) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getProperty(final String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void logError(final int status, final String text, final Exception e) {
			log.add(text);
		}

		@Override
		public void logError(final int status, final String text, final Exception e, final boolean revealLog) {
			log.add(text);
		}

		@Override
		public void logStack(final int status, final String text) {
			// TODO Auto-generated method stub

		}

		@Override
		public void restoreCursor() {
			// TODO Auto-generated method stub

		}

		@Override
		public void setCursor(final int theCursor) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setProperty(final String name, final String value) {
			// TODO Auto-generated method stub

		}
	}

	private TrackSegment getDummyList() {
		final TrackSegment ts0 = new TrackSegment(false);
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

	private FixWrapper getFix(final long dtg, final double course, final double speed) {
		final Fix theFix = new Fix(new HiResDate(dtg), new WorldLocation(2, 2, 2), course, speed);
		final FixWrapper res = new FixWrapper(theFix);

		return res;
	}

	/**
	 * check that the supplied comparator obeys the Comparator contract (sign
	 * symmetry and transitivity) for every triple of the supplied items. A breach
	 * is what makes the JDK sort throw
	 * "Comparison method violates its general contract!" (see issue #5199).
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

	private static TrackSegment segmentStarting(final long dtg, final String name) {
		final TrackSegment seg = new TrackSegment(false);
		seg.addFix(new FixWrapper(new Fix(new HiResDate(dtg), new WorldLocation(1, 1, 0), 0, 0)));
		// name it last, since adding the first fix relabels the segment with its date
		seg.setName(name);
		return seg;
	}

	@Test
	public void testCompareToWithEmptySegment() {
		// segments named so that name order contradicts time order
		final TrackSegment early = segmentStarting(10000, "leg_b");
		final TrackSegment late = segmentStarting(20000, "leg_a");

		// an empty segment (such as an infill that couldn't be generated) has no
		// start time, so it can only be compared by name
		final TrackSegment empty = new TrackSegment(false);
		empty.setName("leg_aa");
		assertNull(empty.startDTG(), "empty segment has no start time");

		final Comparator<TrackSegment> comp = (a, b) -> a.compareTo(b);
		assertComparatorContract(comp, Arrays.asList(early, late, empty));

		// timed segments come first, in time order, then the untimed ones
		final List<TrackSegment> segs = new ArrayList<>(Arrays.asList(empty, late, early));
		Collections.sort(segs, comp);
		assertEquals(early, segs.get(0), "early first");
		assertEquals(late, segs.get(1), "late second");
		assertEquals(empty, segs.get(2), "empty last");
	}

	/**
	 * two legs that start at the same time must both be stored, and each must be
	 * removable
	 */
	@Test
	public void testEqualStartSegments() {
		final TrackWrapper tw = new TrackWrapper();
		tw.setName("track");
		final TrackSegment early = segmentStarting(5000, "early");
		final TrackSegment first = segmentStarting(10000, "first");
		final TrackSegment second = segmentStarting(10000, "second");
		tw.add(early);
		tw.add(first);
		tw.add(second);

		final TrackWrapper_Support.SegmentList segs = tw.getSegments();
		assertEquals(3, segs.size(), "all legs stored");

		final Comparator<TrackSegment> comp = (a, b) -> a.compareTo(b);
		assertComparatorContract(comp, Arrays.asList(early, first, second));
		assertTrue(first.compareTo(second) != 0, "equal start legs distinct");

		segs.removeElement(first);
		assertEquals(2, segs.size(), "one removed");
		assertFalse(segs.getData().stream().anyMatch(s -> s == first), "right one removed");
		segs.removeElement(second);
		assertEquals(1, segs.size(), "two removed");
		assertSame(early, segs.first(), "early remains");
	}

	/**
	 * a leg whose start time changes after it has been stored is re-sorted
	 */
	@Test
	public void testSegmentStartChanges() {
		final TrackWrapper tw = new TrackWrapper();
		tw.setName("track");
		final TrackSegment early = segmentStarting(5000, "early");
		final TrackSegment late = segmentStarting(10000, "late");
		tw.add(early);
		tw.add(late);
		assertSame(late, tw.getSegments().last(), "late is last");

		// the track appends new fixes to its last leg. Give it one that's
		// earlier than the first leg
		tw.addFix(new FixWrapper(new Fix(new HiResDate(1000), new WorldLocation(1, 1, 0), 0, 0)));
		assertEquals(1000, late.startDTG().getDate().getTime(), "late now starts first");

		final TrackWrapper_Support.SegmentList segs = tw.getSegments();
		assertSame(late, segs.first(), "re-sorted: late now first");
		assertSame(early, segs.last(), "re-sorted: early now last");

		segs.removeElement(early);
		assertEquals(1, segs.size(), "removed");
		assertSame(late, segs.first(), "right one left");
	}

	/**
	 * removing (and re-adding) segments whilst looping through them, as
	 * TrackWrapper.tidyUpOnPaste does, must still visit every segment
	 */
	@Test
	public void testRemoveWhilstLooping() {
		final TrackWrapper tw = new TrackWrapper();
		tw.setName("track");
		for (int i = 1; i <= 4; i++) {
			tw.add(segmentStarting(i * 5000, "leg" + i));
		}
		final TrackWrapper_Support.SegmentList segs = tw.getSegments();
		assertEquals(4, segs.size(), "four legs");

		int visited = 0;
		final Enumeration<Editable> iter = segs.elements();
		while (iter.hasMoreElements()) {
			final TrackSegment seg = (TrackSegment) iter.nextElement();
			segs.removeElement(seg);
			segs.addSegment(seg);
			visited++;
		}
		assertEquals(4, visited, "all legs visited");
		assertEquals(4, segs.size(), "all legs still present");
	}

	@Test
	public void testDeleteNotVisible() {
		final TrackSegment ts = getDummyList();

		// check everyone is at home
		assertEquals(4, ts.size(), "all present");

		// hide the first
		final Enumeration<Editable> ele = ts.elements();
		final FixWrapper first = (FixWrapper) ele.nextElement();
		final FixWrapper second = (FixWrapper) ele.nextElement();
		first.setVisible(false);
		second.setVisible(false);

		ts.deleteNonVisiblePositions();

		// check everyone is at home
		assertEquals(2, ts.size(), "first two gone");

	}

	@Test
	public void testResample() {
		final TrackWrapper tw = new TrackWrapper();
		tw.setName("Some name");

		final List<String> log = new ArrayList<String>();

		tw.add(getFix(0, 2, 4));
		tw.add(getFix(60000, 2, 4));
		tw.add(getFix(120000, 2, 4));
		tw.add(getFix(180000, 2, 4));
		tw.add(getFix(240000, 2, 4));
		tw.add(getFix(300000, 2, 4));

		final TrackSegment t1 = (TrackSegment) tw.getSegments().elements().nextElement();
		assertEquals(6, t1.size(), "right length");

		// do resample
		t1.setResampleDataAt(new HiResDate(120000));
		assertEquals(3, t1.size(), "right length");

		t1.setResampleDataAt(new HiResDate(12000));
		assertEquals(21, t1.size(), "right length");

		// also try it for a TMA segment
		tw.removeElement(t1);

		Application.initialise(new MockParent(log));

		final AbsoluteTMASegment as = new AbsoluteTMASegment(12, new WorldSpeed(4, WorldSpeed.Kts),
				new WorldLocation(2, 2, 2), new HiResDate(100000), new HiResDate(1000000));
		tw.add(as);
		assertEquals(16, as.size(), "right length");
		assertEquals(0, log.size(), "empty log");

		as.setResampleDataAt(new HiResDate(120000));

		assertEquals(16, as.size(), "still right length");
		assertEquals(1, log.size(), "non-empty log");

	}

	@Test
	public void testTrim() {
		TrackSegment ts0 = getDummyList();
		TimePeriod newP = new TimePeriod.BaseTimePeriod(new HiResDate(30000), new HiResDate(40000));
		assertEquals(4, ts0.size(), "correct len");
		ts0.trimTo(newP);
		assertEquals(2, ts0.size(), "correct new len");

		ts0 = getDummyList();
		newP = new TimePeriod.BaseTimePeriod(new HiResDate(35000), new HiResDate(40000));
		assertEquals(4, ts0.size(), "correct len");
		ts0.trimTo(newP);
		assertEquals(1, ts0.size(), "correct new len");

		ts0 = getDummyList();
		newP = new TimePeriod.BaseTimePeriod(new HiResDate(15000), new HiResDate(40000));
		assertEquals(4, ts0.size(), "correct len");
		ts0.trimTo(newP);
		assertEquals(3, ts0.size(), "correct new len");

		ts0 = getDummyList();
		newP = new TimePeriod.BaseTimePeriod(new HiResDate(45000), new HiResDate(50000));
		assertEquals(4, ts0.size(), "correct len");
		ts0.trimTo(newP);
		assertEquals(0, ts0.size(), "correct new len");

	}

}
