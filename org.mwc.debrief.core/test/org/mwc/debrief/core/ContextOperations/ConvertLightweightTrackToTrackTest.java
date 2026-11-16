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
import static org.mwc.debrief.core.ContextOperations.ConvertLightweightTrackToTrack.*;
import org.mwc.debrief.core.ContextOperations.ConvertLightweightTrackToTrack.*;
import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class ConvertLightweightTrackToTrackTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	@Test
	public final static void testIWork() {
		final Layers theLayers = new Layers();
		final BaseLayer holder = new BaseLayer();
		holder.setName("Trk");
		theLayers.addThisLayer(holder);

		final int NUM_TRACKS = 4;
		final int NUM_POSITIONS = 5;
		final Editable[] selection = new Editable[NUM_TRACKS];

		LightweightTrackWrapper firstTrack = null;

		for (int j = 0; j < NUM_TRACKS; j++) {
			final LightweightTrackWrapper light = new LightweightTrackWrapper("track:" + j, true, true, Color.RED,
					LineStylePropertyEditor.DOTTED);

			for (int i = 0; i < NUM_POSITIONS; i++) {
				final Fix theFix = new Fix(new HiResDate(i * 1000), new WorldLocation(1, i, 0), 12d, 12d);
				light.addFix(new FixWrapper(theFix));
			}

			holder.add(light);
			selection[j] = light;

			if (firstTrack == null) {
				firstTrack = light;
			}

		}

		assertEquals(1, theLayers.size(), "have single layer before operation");

		// ok, now do the interpolation
		final ConvertIt ct = new ConvertIt("convert it", theLayers, selection);

		try {
			ct.execute(null, null);
		} catch (final ExecutionException e) {
			fail("Exception thrown");
		}

		assertEquals(5, theLayers.size(), "have new layers");

		// check the track got generated
		final TrackWrapper tw = (TrackWrapper) theLayers.findLayer("track:0");

		// did we find it?
		assertNotNull(tw, "track generated");

		// check we've got the right number of fixes

		assertEquals("track:0", tw.getName(), "correct name");
		assertEquals(firstTrack.numFixes(), tw.numFixes(), "correct size");
		assertEquals(NUM_POSITIONS, tw.numFixes(), "correct size");
		assertEquals(firstTrack.getColor(), tw.getColor(), "correct color");
		assertEquals(firstTrack.getName(), tw.getName(), "correct name");

	}

}
