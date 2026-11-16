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
import static org.mwc.debrief.core.ContextOperations.GroupLightweightTracks.*;
import org.mwc.debrief.core.ContextOperations.GroupLightweightTracks.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

public class GroupLightweightTracksTest {
	private FixWrapper create(final double lat, final double lon, final long date) {
		return new FixWrapper(new Fix(new HiResDate(date), new WorldLocation(lat, lon, 0d), 2d, 4d));
	}

	@Test
	public void testGroup() throws ExecutionException {
		final Layers theLayers = new Layers();

		final TrackWrapper tw = new TrackWrapper();
		tw.setName("tw1");
		tw.addFix(create(2, 3, 4000));
		tw.addFix(create(2, 2, 5000));
		tw.addFix(create(2, 4, 6000));
		tw.addFix(create(2, 6, 7000));

		final LightweightTrackWrapper l1 = new LightweightTrackWrapper();
		l1.setName("l1");
		l1.addFix(create(2, 3, 14000));
		l1.addFix(create(2, 2, 15000));
		l1.addFix(create(2, 4, 16000));
		l1.addFix(create(2, 6, 17000));

		final LightweightTrackWrapper l2 = new LightweightTrackWrapper();
		l2.setName("l2");
		l2.addFix(create(2, 3, 24000));
		l2.addFix(create(2, 2, 25000));
		l2.addFix(create(2, 4, 26000));
		l2.addFix(create(2, 6, 27000));

		final LightweightTrackWrapper l3 = new LightweightTrackWrapper();
		l3.setName("l3");
		l3.addFix(create(2, 3, 34000));
		l3.addFix(create(2, 2, 35000));
		l3.addFix(create(2, 4, 36000));
		l3.addFix(create(2, 6, 37000));

		final BaseLayer holder = new BaseLayer();
		holder.setName("Light tracks");

		holder.add(l1);
		holder.add(l2);
		holder.add(l3);

		theLayers.addThisLayer(holder);
		theLayers.addThisLayer(tw);

		// check layers looks how we think
		assertEquals(2, theLayers.size(), "right num layers");
		assertEquals(3, holder.size(), "right num in holder");

		final Editable[] selection = new Editable[] { l3, l1, tw };
		final GroupLightTracksOperation oper = new GroupLightTracksOperation("Do generate", l2, theLayers,
				selection);

		oper.execute(null, null);

		// check layers looks how we think
		assertEquals(1, theLayers.size(), "right num layers");
		assertEquals(1, holder.size(), "right num in holder");
	}
}
