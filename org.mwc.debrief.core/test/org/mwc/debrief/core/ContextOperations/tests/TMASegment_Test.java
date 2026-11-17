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
package org.mwc.debrief.core.ContextOperations.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;

import org.eclipse.core.commands.ExecutionException;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import org.junit.jupiter.api.Test;

public final class TMASegment_Test {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	@SuppressWarnings("deprecation")
	private TrackWrapper getLongerTrack() {
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0.00000001, 0.000000001, 0);
		WorldLocation lastLoc = loc_1;

		for (int i = 0; i < 50; i++) {
			final long thisTime = new Date(2016, 1, 14, 12, i, 0).getTime();
			final FixWrapper fw = new FixWrapper(new Fix(new HiResDate(thisTime), lastLoc.add(getVector(25, 0)),
					MWC.Algorithms.Conversions.Degs2Rads(0), 110));
			fw.setLabel("fw1");
			tw.addFix(fw);

			lastLoc = new WorldLocation(fw.getLocation());
		}

		final SensorWrapper swa = new SensorWrapper("title one");
		tw.add(swa);
		swa.setSensorOffset(new ArrayLength(-400));

		for (int i = 0; i < 50; i += 3) {
			final long thisTime = new Date(2016, 1, 14, 12, i, 30).getTime();
			final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa", new HiResDate(thisTime), null, null,
					null, null, null, 0, null);
			swa.add(scwa1);
		}

		return tw;
	}

	/**
	 * @return
	 */
	private WorldVector getVector(final double courseDegs, final double distM) {
		return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(courseDegs),
				new WorldDistance(distM, WorldDistance.METRES), null);
	}

	@Test
	public void testDecimate() throws ExecutionException {
		final TrackWrapper tw = getLongerTrack();

		assertNotNull(tw);

		// get the sensor data
		final SensorWrapper sw = (SensorWrapper) tw.getSensors().elements().nextElement();

		assertNotNull(sw);

		// create a list of cuts (to simulate the selection)
		final SensorContactWrapper[] items = new SensorContactWrapper[sw.size()];
		final Enumeration<Editable> numer = sw.elements();
		int ctr = 0;
		while (numer.hasMoreElements()) {
			final SensorContactWrapper cut = (SensorContactWrapper) numer.nextElement();
			items[ctr++] = cut;
		}

		final Layers theLayers = new Layers();
		final WorldVector worldOffset = new WorldVector(Math.PI, 0.002, 0);
		final double tgtCourse = 0;
		final WorldSpeed tgtSpeed = new WorldSpeed(3, WorldSpeed.Kts);

		// create it, then
		final TrackSegment seg = new RelativeTMASegment(items, worldOffset, tgtSpeed, tgtCourse, theLayers, Color.RED);

		// now wrap it
		final TrackWrapper track = new TrackWrapper();
		track.setColor(Color.red);
		track.add(seg);
		final String tNow = TrackSegment.TMA_LEADER
				+ FormatRNDateTime.toString(track.getStartDTG().getDate().getTime());
		track.setName(tNow);

		// check track size
		Collection<Editable> pts = track.getItemsBetween(track.getStartDTG(), track.getEndDTG());
		assertEquals(17, pts.size(), "has correct points");

		track.setResampleDataAt(new HiResDate(300000));

		pts = track.getItemsBetween(track.getStartDTG(), track.getEndDTG());
		assertEquals(9, pts.size(), "has more points");

		// check a label is present
		for (final Editable item : pts) {
			final FixWrapper fix = (FixWrapper) item;
			final String label = fix.getLabel();
			assertNotNull(label, "after resampling, all fixes require labels");
			assertTrue(label.length() > 0, "label has contents");
		}

//    RelativeTMASegment seg =
//        (RelativeTMASegment) sol.getSegments().elements().nextElement();

		assertNotNull(seg, "new seg not found");

		// ok, and we split it.
		int ctr2 = 0;
		FixWrapper beforeF = null;
		FixWrapper afterF = null;
		final Enumeration<Editable> eF = seg.elements();
		while (eF.hasMoreElements()) {
			final FixWrapper fix = (FixWrapper) eF.nextElement();
			ctr2++;
			if (ctr2 > seg.size() / 2) {
				if (beforeF == null) {
					beforeF = fix;
				} else {
					afterF = fix;
					break;
				}
			}
		}

		assertNotNull(beforeF, "fix not found");

		// ok, what's the time offset
		final WorldLocation afterBeforeSplit = afterF.getLocation();

		// ok, time to split
		final SubjectAction[] actions = beforeF.getInfo().getUndoableActions();
		final SubjectAction doSplit = actions[1];
		doSplit.execute(beforeF);

		// ok, have another look
		assertEquals(2, track.getSegments().size(), "now has two segments");
		Enumeration<Editable> aNum = track.getSegments().elements();
		aNum.nextElement();
		TrackSegment afterSeg = (TrackSegment) aNum.nextElement();
		WorldLocation locAfterSplit = afterSeg.getTrackStart();

		assertEquals(afterBeforeSplit, locAfterSplit, "origin remains valid");

		// hey, try the undo
		doSplit.undo(beforeF);

		assertEquals(1, track.getSegments().size(), "now has one segment again");

		// hey, try the undo
		doSplit.execute(beforeF);
		assertEquals(2, track.getSegments().size(), "now has two segments");

		aNum = track.getSegments().elements();
		aNum.nextElement();
		afterSeg = (TrackSegment) aNum.nextElement();
		locAfterSplit = afterSeg.getTrackStart();
		assertEquals(afterBeforeSplit, locAfterSplit, "origin remains valid, after undo/redo");

	}
}
