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

package Debrief.Wrappers;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.Wrappers.BuoyPatternWrapper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Debrief.Wrappers.BuoyPatternWrapper.*;
import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class BuoyPatternWrapperTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testListManagement() {
		// ok, create our list of buoys
		final BuoyPatternWrapper bpw = new BuoyPatternWrapper(null);

		// check the area covered
		WorldArea wa = bpw.getBounds();
		assertTrue(wa == null, "null area returned for empty pattern");

		// add first buoy
		final WorldLocation w1 = new WorldLocation(2, 1, 2);
		final LabelWrapper lw = new LabelWrapper("here", w1, Color.red, new HiResDate(100), new HiResDate(200));
		bpw.add(lw);

		// now recheck the area
		wa = bpw.getBounds();
		assertEquals(wa.getTopLeft(), w1, "area of first buoy returned");
		assertEquals(wa.getBottomRight(), w1, "area of first buoy returned");

		final WorldLocation w2 = new WorldLocation(1, 2, 1);
		final LabelWrapper lw2 = new LabelWrapper("here", w2, Color.red, new HiResDate(100), new HiResDate(200));
		bpw.add(lw2);

		wa = bpw.getBounds();
		assertEquals(wa.getTopLeft(), w1, "area of first buoy returned");
		assertEquals(wa.getBottomRight(), w2, "area of first buoy returned");

		// now mess with the times
		assertEquals(lw.getVisible(), true, "first buoy visible");
		assertEquals(lw2.getVisible(), true, "second buoy visible");

		bpw.filterListTo(new HiResDate(40), new HiResDate(50));
		assertEquals(lw.getVisible(), false, "first buoy not visible");
		assertEquals(lw2.getVisible(), false, "second buoy not visible");

		bpw.filterListTo(new HiResDate(50), new HiResDate(150));
		assertEquals(lw.getVisible(), true, "first buoy visible");
		assertEquals(lw2.getVisible(), true, "second buoy visible");

		bpw.filterListTo(new HiResDate(110), new HiResDate(150));
		assertEquals(lw.getVisible(), true, "first buoy visible");
		assertEquals(lw2.getVisible(), true, "second buoy visible");

		bpw.filterListTo(new HiResDate(90), new HiResDate(200));
		assertEquals(lw.getVisible(), true, "first buoy visible");
		assertEquals(lw2.getVisible(), true, "second buoy visible");

		// stick in the third buoy
		final WorldLocation w3 = new WorldLocation(2, 2, 1);
		final LabelWrapper lw3 = new LabelWrapper("here", w3, Color.red, new HiResDate(180), new HiResDate(260));
		bpw.add(lw3);

		bpw.filterListTo(new HiResDate(90), new HiResDate(200));
		assertEquals(lw3.getVisible(), true, "third buoy visible");

		bpw.filterListTo(new HiResDate(90), new HiResDate(100));
		assertEquals(lw3.getVisible(), false, "third buoy not visible");

		bpw.filterListTo(new HiResDate(290), new HiResDate(300));
		assertEquals(lw3.getVisible(), false, "third buoy not visible");

		bpw.filterListTo(new HiResDate(190), new HiResDate(200));
		assertEquals(lw3.getVisible(), true, "third buoy visible");

	}

	@Test
	public final void testMyParams() {
		MWC.GUI.Editable ed = new BuoyPatternWrapper();
		editableTesterSupport.testParams(ed);
		ed = null;
	}

	/**
	 * put test for BaseTimePeriod in here, since we can't put testing code (main
	 * method) into TimePeriod interface
	 */
	@Test
	public final void testPeriod() {
		final TimePeriod btp = new TimePeriod.BaseTimePeriod(new HiResDate(500), new HiResDate(600));
		assertEquals(btp.contains(new HiResDate(550)), true, "contains valid time");
		assertEquals(btp.contains(new HiResDate(450)), false, "doesn't contain invalid time");
		assertEquals(btp.contains(new HiResDate(650)), false, "doesn't contain invalid time");
		btp.extend(new HiResDate(650));
		assertEquals(btp.contains(new HiResDate(550)), true, "contains valid time");
		assertEquals(btp.contains(new HiResDate(650)), true, "contains valid time");
		assertEquals(btp.contains(new HiResDate(450)), false, "doesn't contain invalid time");
		assertEquals(btp.contains(new HiResDate(750)), false, "doesn't contain invalid time");
		assertEquals(btp.overlaps(new HiResDate(450), new HiResDate(550)), true, "overlaps valid period");
		assertEquals(btp.overlaps(new HiResDate(550), new HiResDate(750)), true, "overlaps valid period");
		assertEquals(btp.overlaps(new HiResDate(550), new HiResDate(580)), true, "overlaps valid period");
		assertEquals(btp.overlaps(new HiResDate(450), new HiResDate(780)), true, "overlaps valid period");
		assertEquals(btp.overlaps(new HiResDate(450), new HiResDate(480)), false, "overlaps valid period");
		assertEquals(btp.overlaps(new HiResDate(650), new HiResDate(780)), false, "overlaps valid period");
	}

}
