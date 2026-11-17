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

package MWC.GUI;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GUI.Plottables.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GUI.Plottables.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import MWC.GUI.Chart.Painters.Grid4WPainter;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Chart.Painters.ScalePainter;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class PlottablesTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testAddRemove() {
		final Plottables pl = new Plottables();
		assertEquals(pl.size(), 0, "Empty list");
		final GridPainter cp = new GridPainter();
		pl.add(cp);
		assertEquals(pl.size(), 1, "non-empty list");
		pl.removeElement(cp);
		assertEquals(pl.size(), 0, "list");

		final ScalePainter sp = new ScalePainter();
		pl.add(sp);
		assertEquals(pl.size(), 1, "non-empty list");
		pl.removeElement(sp);
		assertEquals(pl.size(), 0, "list empty");
		final Grid4WPainter c4p = new Grid4WPainter(null);
		pl.add(c4p);
		assertEquals(pl.size(), 1, "non-empty list");
		pl.removeElement(c4p);
		assertEquals(pl.size(), 0, "list");
	}

}
