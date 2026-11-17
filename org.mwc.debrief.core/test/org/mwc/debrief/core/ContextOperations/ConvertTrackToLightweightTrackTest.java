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
import static org.mwc.debrief.core.ContextOperations.ConvertTrackToLightweightTrack.*;
import org.mwc.debrief.core.ContextOperations.ConvertTrackToLightweightTrack.*;
import java.awt.Color;
import java.util.Enumeration;
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
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class ConvertTrackToLightweightTrackTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	@Test
	public final static void testIWork() {
		final Layers theLayers = new Layers();
		final BaseLayer holder = new BaseLayer();
		holder.setName("Trk");
		theLayers.addThisLayer(holder);

		final TrackWrapper track = new TrackWrapper();
		track.setName("BIG ONE");

		final int NUM_FIXES = 4;
		for (int i = 0; i < NUM_FIXES; i++) {
			final WorldLocation thisLoc = new WorldLocation(0, i, 0, 'N', 0, 0, 0, 'W', 0);
			final Fix theFix = new Fix(new HiResDate(i * 10000), thisLoc, 2 * i, NUM_FIXES * i);
			final FixWrapper fixW = new FixWrapper(theFix);
			track.addFix(fixW);
		}
		theLayers.addThisLayer(track);

		assertEquals(2, theLayers.size(), "heavy track present");
		assertEquals(0, holder.size(), "holder empty");

		// ok, now do the interpolation
		final ConvertIt ct = new ConvertIt("convert it", theLayers, new Editable[] { track }, holder);

		try {
			ct.execute(null, null);
		} catch (final ExecutionException e) {
			fail("Exception thrown");
		}

		assertEquals(1, theLayers.size(), "heavy track gone");

		assertEquals(1, holder.size(), "holder not empty");

		// check the track got generated
		final LightweightTrackWrapper tw = (LightweightTrackWrapper) holder.elements().nextElement();

		// did we find it?
		assertNotNull(tw, "track generated");

		assertEquals("BIG ONE", tw.getName(), "correct name");
		assertEquals(track.numFixes(), tw.numFixes(), "correct size");
		assertEquals(NUM_FIXES, tw.numFixes(), "correct size");
		assertEquals(track.getColor(), tw.getColor(), "correct color");
		assertEquals(track.getName(), tw.getName(), "correct name");
	}

}
