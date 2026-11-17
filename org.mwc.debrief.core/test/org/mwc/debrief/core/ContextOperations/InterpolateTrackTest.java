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
import static org.junit.jupiter.api.Assertions.fail;
import static org.mwc.debrief.core.ContextOperations.InterpolateTrack.*;
import org.mwc.debrief.core.ContextOperations.InterpolateTrack.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
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
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.TimeStepPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class InterpolateTrackTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testInterpolate() {
		final Layers theLayers = new Layers();
		final TrackWrapper track = new TrackWrapper();
		track.setName("Trk");
		theLayers.addThisLayer(track);

		for (int i = 0; i < 3; i++) {
			final WorldLocation thisLoc = new WorldLocation(0, i, 0, 'N', 0, 0, 0, 'W', 0);
			final Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(0);
			cal.set(2005, 6, 6, 12, i * 5, 0);
			final Fix newFix = new Fix(new HiResDate(cal.getTime()), thisLoc, 0, 0);

			final FixWrapper sw = new FixWrapper(newFix);
			track.add(sw);
		}

		// ok, now do the interpolation
		final InterpolateTrackOperation ct = new InterpolateTrackOperation("convert it", theLayers, track, "1 min",
				60 * 1000 * 1000);

		// check we're starting with the right number of items
		assertEquals(3, track.numFixes(), "starting with right number");

		try {
			ct.execute(null, null);
		} catch (final ExecutionException e) {
			fail("Exception thrown");
		}

		// check we've got the right number of fixes (12:00 to 12:10 inclusive)
		assertEquals(11, track.numFixes(), "right num of fixes generated");
		assertEquals(0, ((FixWrapper) track.getPositionIterator().nextElement()).getDTG()
				.getDate().getTime() % (5 * 60 * 1000), "keeps start");

	}

	private static List<List<Editable>> contentsOf(final TrackWrapper track) {
		final List<List<Editable>> res = new ArrayList<List<Editable>>();
		final Enumeration<Editable> segs = track.getSegments().elements();
		while (segs.hasMoreElements()) {
			final TrackSegment seg = (TrackSegment) segs.nextElement();
			res.add(Collections.list(seg.elements()));
		}
		return res;
	}

	private static String legSizes(final TrackWrapper track) {
		final List<Integer> sizes = new ArrayList<Integer>();
		for (final List<Editable> leg : contentsOf(track)) {
			sizes.add(leg.size());
		}
		Collections.sort(sizes);
		return sizes.toString();
	}

	/**
	 * undo must put back exactly the original fixes, in their original segments
	 */
	@Test
	public final void testUndoRestoresOriginalFixes() throws ExecutionException {
		final Layers theLayers = new Layers();
		final TrackWrapper track = new TrackWrapper();
		track.setName("Trk");
		theLayers.addThisLayer(track);

		final TrackSegment legOne = new TrackSegment(TrackSegment.ABSOLUTE);
		final TrackSegment legTwo = new TrackSegment(TrackSegment.ABSOLUTE);
		for (int i = 0; i < 6; i++) {
			final WorldLocation thisLoc = new WorldLocation(0, i, 0, 'N', 0, 0, 0, 'W', 0);
			final Fix newFix = new Fix(new HiResDate(1000000000000L + i * 5 * 60 * 1000L), thisLoc, 0, 0);
			(i < 3 ? legOne : legTwo).addFix(new FixWrapper(newFix));
		}
		track.add(legOne);
		track.add(legTwo);

		final List<List<Editable>> before = contentsOf(track);
		assertEquals(2, before.size(), "two legs");
		assertEquals(6, track.numFixes(), "six fixes");

		final InterpolateTrackOperation ct = new InterpolateTrackOperation("convert it", theLayers, track, "1 min",
				60 * 1000 * 1000);

		ct.execute(null, null);
		assertEquals(26, track.numFixes(), "resampled");
		assertEquals("[0, 26]", legSizes(track), "resampled fixes all in one leg");

		ct.undo(null, null);
		assertEquals(before, contentsOf(track), "original fixes restored");
		assertEquals(6, track.numFixes(), "six fixes");
		assertEquals(1000000000000L, track.getStartDTG().getDate().getTime(), "same period");

		// and redo/undo again
		ct.execute(null, null);
		assertEquals(26, track.numFixes(), "resampled");
		assertEquals("[0, 26]", legSizes(track), "resampled fixes all in one leg");
		ct.undo(null, null);
		assertEquals(before, contentsOf(track), "original fixes restored");
	}
}
