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
import static org.mwc.debrief.core.ContextOperations.GenerateTrack.*;
import org.mwc.debrief.core.ContextOperations.GenerateTrack.*;
import java.awt.Color;
import java.util.Date;
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
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class GenerateTrackTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testIWork() {
		final Layers theLayers = new Layers();
		final BaseLayer holder = new BaseLayer();
		holder.setName("Trk");
		theLayers.addThisLayer(holder);

		WorldLocation lastLoc = null;
		for (int i = 0; i < 4; i++) {
			final WorldLocation thisLoc = new WorldLocation(0, i, 0, 'N', 0, 0, 0, 'W', 0);
			if (lastLoc != null) {
				// ok, add the line
				final LineShape ls = new LineShape(lastLoc, thisLoc);

				final long theDate1 = 20000000 + i * 60000;
				final long theDate2 = 20000000 + i * 61000;

				final ShapeWrapper sw = new ShapeWrapper("shape:" + i, ls, Color.red, new HiResDate(theDate1));
				sw.setStartDTG(new HiResDate(theDate1));
				sw.setEndDTG(new HiResDate(theDate2));
				holder.add(sw);
			}

			// and remember the last location
			lastLoc = thisLoc;
		}

		// ok, now do the interpolation
		final ConvertTrack ct = new ConvertTrack("convert it", theLayers, new Editable[] { holder });

		try {
			ct.execute(null, null);
		} catch (final ExecutionException e) {
			fail("Exception thrown");
		}

		// check the track got generated
		final TrackWrapper tw = (TrackWrapper) theLayers.findLayer("T_Trk");

		// did we find it?
		assertNotNull(tw, "track generated");

		// check we've got the right number of fixes
		assertEquals(tw.numFixes(), 4, "right num of fixes generated");

	}
}
