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

package org.mwc.cmap.core.DataTypes.TrackData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.cmap.core.DataTypes.TrackData.TrackManager.*;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.TrackDataProvider;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class TrackManagerTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testLists() {
		final TrackWrapper ta = new TrackWrapper();
		ta.setName("ta");
		final TrackWrapper tb = new TrackWrapper();
		tb.setName("tb");
		final TrackWrapper tc = new TrackWrapper();
		tc.setName("tc");
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(ta);
		theLayers.addThisLayer(tb);
		theLayers.addThisLayer(tc);

		final String pri_a = "ta";
		final String pri_b = "tz";
		final String sec_b = "tb";
		final String sec_c = "tc";
		final String sec_d = "tz";
		final Vector<String> secs = new Vector<String>(0, 1);
		secs.add(sec_b);
		secs.add(sec_c);

		// create the mgr
		final TrackManager tm = new TrackManager(theLayers);

		// do some checks
		assertNull(tm._thePrimary, "pri empty");
		assertNull(tm._theSecondaries, "secs empty");
		assertNotNull(tm._theLayers, "layers assigned");

		// now get going
		tm.assignTracks(pri_a, secs);

		// and do the tests
		assertNotNull(tm._thePrimary, "pri assigned");
		assertEquals(tm._thePrimary, ta, "pri matches");

		// and the secs
		assertNotNull(tm._theSecondaries, "sec assigned");
		assertEquals(2, tm._theSecondaries.length, "correct num");

		// setup duff data
		secs.clear();
		secs.add(sec_b);
		secs.add(sec_d);

		// assign duff data
		tm.assignTracks(pri_b, secs);

		// and test duff data
		assertNotNull(tm._thePrimary, "pri still assigned");
		assertEquals(tm._thePrimary, ta, "pri matches");
		assertNotNull(tm._theSecondaries, "sec assigned");
		assertEquals(1, tm._theSecondaries.length, "correct num");

		// assign more real data
		tm.assignTracks(sec_c, secs);

		// and test duff data
		assertNotNull(tm._thePrimary, "pri still assigned");
		assertEquals(tm._thePrimary, tc, "pri matches");
		assertNotNull(tm._theSecondaries, "sec assigned");
		assertEquals(1, tm._theSecondaries.length, "correct num");
	}
}
