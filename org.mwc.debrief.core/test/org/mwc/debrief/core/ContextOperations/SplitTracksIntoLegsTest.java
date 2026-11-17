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

package org.mwc.debrief.core.ContextOperations;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.debrief.core.ContextOperations.SplitTracksIntoLegs.*;
import org.mwc.debrief.core.ContextOperations.SplitTracksIntoLegs.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.Algorithms.Conversions;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

public class SplitTracksIntoLegsTest {
	private static FixWrapper getFix(final long dtg, final double course, final double speed) {
		final Fix theFix = new Fix(new HiResDate(dtg), new WorldLocation(2, 2, 2), course,
				Conversions.Kts2Yps(speed));
		final FixWrapper res = new FixWrapper(theFix);
		return res;
	}
	private static FixWrapper getFix2(final long dtg, final double lat, final double lng) {
		final Fix theFix = new Fix(new HiResDate(dtg), new WorldLocation(lat, lng, 0), 0d,0d);
		final FixWrapper res = new FixWrapper(theFix);
		return res;
	}

	private static TrackWrapper getOne() {
		final TrackWrapper tOne = new TrackWrapper();
		tOne.setName("t-1");
		tOne.addFix(getFix(1000, 22, 33));
		tOne.addFix(getFix(2000, 22, 33));
		tOne.addFix(getFix(2100, 22, 33));
		tOne.addFix(getFix(4000, 22, 33));
		tOne.addFix(getFix(5000, 22, 33));
		tOne.addFix(getFix(6000, 22, 33));
		tOne.addFix(getFix(7000, 22, 33));
		tOne.addFix(getFix(8000, 22, 33));
		tOne.addFix(getFix(9000, 22, 33));
		tOne.addFix(getFix(10000, 22, 33));
		tOne.addFix(getFix(11100, 22, 33));
		tOne.addFix(getFix(12000, 22, 33));
		tOne.addFix(getFix(13000, 22, 33));
		tOne.addFix(getFix(14000, 22, 33));
		return tOne;
	}

	private static TrackWrapper getTwo() {
		final TrackWrapper tTwo = new TrackWrapper();
		tTwo.setName("t-2");
		tTwo.addFix(getFix(1000, 22, 33));
		tTwo.addFix(getFix(2000, 22, 33));
		tTwo.addFix(getFix(2100, 22, 33));
		tTwo.addFix(getFix(4000, 22, 33));
		tTwo.addFix(getFix(5000, 22, 33));
		tTwo.addFix(getFix(8000, 22, 33));
		tTwo.addFix(getFix(9000, 22, 33));
		tTwo.addFix(getFix(10000, 22, 33));
		tTwo.addFix(getFix(11100, 22, 33));
		tTwo.addFix(getFix(12000, 22, 33));
		tTwo.addFix(getFix(13000, 22, 33));
		tTwo.addFix(getFix(14000, 22, 33));
		return tTwo;
	}

	private static TrackWrapper getThree() {
		final TrackWrapper tThree = new TrackWrapper();
		tThree.setName("t-3");
		tThree.addFix(getFix2(1000, 22, 33));
		tThree.addFix(getFix2(2000, 22, 34));
		tThree.addFix(getFix2(2100, 22, 35));
		tThree.addFix(getFix2(4000, 22, 36));
		tThree.addFix(getFix2(5000, 22, 30));
		tThree.addFix(getFix2(8000, 22, 31));
		tThree.addFix(getFix2(9000, 22, 32));
		tThree.addFix(getFix2(10000, 22, 38));
		tThree.addFix(getFix2(11100, 22, 39));
		tThree.addFix(getFix2(12000, 22, 38));
		tThree.addFix(getFix2(13000, 22, 37));
		tThree.addFix(getFix2(14000, 22, 36));
		return tThree;
	}

	@Test
	public void testSplitOperation() throws ExecutionException {

		final TrackWrapper tOne = getOne();

		final TrackWrapper tTwo = getTwo();

		final Layers layers = new Layers();
		layers.addThisLayer(tOne);
		layers.addThisLayer(tTwo);

		final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();
		tracks.add(tOne);
		tracks.add(tTwo);
		final SplitTracksOperation oper = new SplitTracksOperation("Split tracks", layers, tracks, 1000L);

		assertEquals(1, tOne.getSegments().size(), "just one leg");
		assertEquals(1, tTwo.getSegments().size(), "just one leg");
		assertEquals(14, tOne.numFixes(), "correct positions");
		assertEquals(12, tTwo.numFixes(), "correct positions");

		oper.execute(null, null);

		// check the contents of the operation
		final HashMap<TrackWrapper, List<TrackSegment>> map = oper._trackChanges;
		assertEquals(2, map.keySet().size(), "two tracks");

		assertEquals(3, tOne.getSegments().size(), "more leg");
		assertEquals(4, tTwo.getSegments().size(), "more legs");
		assertEquals(14, tOne.numFixes(), "correct positions");
		assertEquals(12, tTwo.numFixes(), "correct positions");

		oper.undo(null, null);

		assertEquals(1, tOne.getSegments().size(), "just one leg");
		assertEquals(1, tTwo.getSegments().size(), "just one leg");
		assertEquals(14, tOne.numFixes(), "correct positions");
		assertEquals(12, tTwo.numFixes(), "correct positions");
	}
	

	private static TrackWrapper getContinuous() {
		final TrackWrapper tFour = new TrackWrapper();
		tFour.setName("t-4");
		for (int i = 1; i <= 10; i++) {
			tFour.addFix(getFix(i * 500, 22, 33));
		}
		return tFour;
	}

	/**
	 * undo must cope with tracks that weren't split
	 */
	@Test
	public void testUndoWhenOneTrackNotSplit() throws ExecutionException {
		final TrackWrapper tOne = getOne();
		final TrackWrapper tFour = getContinuous();

		final Layers layers = new Layers();
		layers.addThisLayer(tOne);
		layers.addThisLayer(tFour);

		final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();
		tracks.add(tFour);
		tracks.add(tOne);
		final SplitTracksOperation oper = new SplitTracksOperation("Split tracks", layers, tracks, 1000L);

		oper.execute(null, null);
		assertEquals(3, tOne.getSegments().size(), "more legs");
		assertEquals(1, tFour.getSegments().size(), "still one leg");

		oper.undo(null, null);
		assertEquals(1, tOne.getSegments().size(), "just one leg");
		assertEquals(1, tFour.getSegments().size(), "just one leg");
		assertEquals(14, tOne.numFixes(), "correct positions");
		assertEquals(10, tFour.numFixes(), "correct positions");
	}

	private static List<Integer> legSizes(final TrackWrapper track) {
		final List<Integer> res = new ArrayList<Integer>();
		final Enumeration<Editable> segs = track.getSegments().elements();
		while (segs.hasMoreElements()) {
			res.add(((TrackSegment) segs.nextElement()).size());
		}
		return res;
	}

	/**
	 * undo must restore the original legs, not merge them all into one
	 */
	@Test
	public void testUndoKeepsOriginalLegs() throws ExecutionException {
		final TrackWrapper track = new TrackWrapper();
		track.setName("two-legs");
		final TrackSegment legOne = new TrackSegment(TrackSegment.ABSOLUTE);
		legOne.setName("one");
		for (final long t : new long[] { 1000, 2000, 5000, 6000, 7000 }) {
			legOne.addFix(getFix(t, 22, 33));
		}
		final TrackSegment legTwo = new TrackSegment(TrackSegment.ABSOLUTE);
		legTwo.setName("two");
		for (final long t : new long[] { 20000, 21000, 25000, 26000 }) {
			legTwo.addFix(getFix(t, 22, 33));
		}
		track.add(legOne);
		track.add(legTwo);

		final Layers layers = new Layers();
		layers.addThisLayer(track);
		final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();
		tracks.add(track);
		final SplitTracksOperation oper = new SplitTracksOperation("Split tracks", layers, tracks, 1000L);

		assertEquals("[5, 4]", legSizes(track).toString(), "before");
		oper.execute(null, null);
		assertEquals("[2, 3, 2, 2]", legSizes(track).toString(), "split");
		oper.undo(null, null);
		assertEquals("[5, 4]", legSizes(track).toString(), "original legs restored");
	}

	/**
	 * undo must cope with tracks that weren't split
	 */
	@Test
	public void testSpatialUndoWhenOneTrackNotSplit() throws ExecutionException {
		final TrackWrapper tThree = getThree();
		final TrackWrapper tTwo = getTwo();

		final Layers layers = new Layers();
		layers.addThisLayer(tThree);
		layers.addThisLayer(tTwo);

		final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();
		tracks.add(tTwo);
		tracks.add(tThree);
		final SpatialSplitTracksOperation oper = new SpatialSplitTracksOperation("Split tracks", layers, tracks,
				3d);

		oper.execute(null, null);
		assertEquals(3, tThree.getSegments().size(), "more legs");
		assertEquals(1, tTwo.getSegments().size(), "still one leg");

		oper.undo(null, null);
		assertEquals(1, tThree.getSegments().size(), "just one leg");
		assertEquals(1, tTwo.getSegments().size(), "just one leg");
		assertEquals(12, tThree.numFixes(), "correct positions");
		assertEquals(12, tTwo.numFixes(), "correct positions");
	}

	@Test
	public void testSpatialSplitOperation1() throws ExecutionException {

		final TrackWrapper tThree = getThree();
		final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();
		tracks.add(tThree);
		List<TrackSegment> segments = TrackWrapper_Support.splitTrackAtSpatialJumps(tThree, 3d);
		assertEquals(3, segments.size(), "three legs");
	}

	@Test
	public void testSpatialSplitOperation2() throws ExecutionException {

		final TrackWrapper tTwo = getTwo();
		final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();
		tracks.add(tTwo);
		List<TrackSegment> segments = TrackWrapper_Support.splitTrackAtSpatialJumps(tTwo, 3d);
		assertEquals(0, segments.size(), "three legs");
	}
}
