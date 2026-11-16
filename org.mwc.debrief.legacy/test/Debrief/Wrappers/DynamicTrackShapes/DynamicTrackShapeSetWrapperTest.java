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

package Debrief.Wrappers.DynamicTrackShapes;

import static Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper.*;
import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.DynamicPlottable;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

public final class DynamicTrackShapeSetWrapperTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testValues() {

	}

	/**
	 * several shapes starting at the same DTG must all be stored, and each one
	 * must be removable
	 */
	@Test
	public final void testEqualTimeShapes() {
		final DynamicTrackShapeSetWrapper set = new DynamicTrackShapeSetWrapper("shapes");
		final java.util.List<DynamicTrackCoverageWrapper> items = new java.util.ArrayList<DynamicTrackCoverageWrapper>();
		items.add(new DynamicTrackCoverageWrapper("trk", new HiResDate(5000), new HiResDate(6000),
				new java.util.ArrayList<DynamicTrackShapeWrapper.DynamicShape>(), Color.RED, 0, "a"));
		for (int i = 0; i < 5; i++) {
			items.add(new DynamicTrackCoverageWrapper("trk", new HiResDate(10000), new HiResDate(11000 + i),
					new java.util.ArrayList<DynamicTrackShapeWrapper.DynamicShape>(), Color.RED, 0, "b" + i));
		}
		items.add(new DynamicTrackCoverageWrapper("trk", new HiResDate(20000), new HiResDate(21000),
				new java.util.ArrayList<DynamicTrackShapeWrapper.DynamicShape>(), Color.RED, 0, "c"));
		for (final DynamicTrackCoverageWrapper item : items) {
			set.add(item);
		}
		assertEquals(items.size(), set.size(), "all stored");

		for (final DynamicTrackCoverageWrapper a : items) {
			for (final DynamicTrackCoverageWrapper b : items) {
				if (a != b) {
					assertTrue(a.compareTo(b) != 0, "distinct");
					assertEquals(Integer.signum(a.compareTo(b)), -Integer.signum(b.compareTo(a)), "antisymmetric");
				}
			}
		}

		int expected = items.size();
		for (final int i : new int[] { 3, 1, 5, 2, 4, 0, 6 }) {
			final DynamicTrackCoverageWrapper item = items.get(i);
			set.removeElement(item);
			expected--;
			assertEquals(expected, set.size(), "removed item " + i);
			final Enumeration<Editable> iter = set.elements();
			while (iter.hasMoreElements()) {
				assertNotSame(item, iter.nextElement(), "item " + i + " gone");
			}
		}
	}

}
