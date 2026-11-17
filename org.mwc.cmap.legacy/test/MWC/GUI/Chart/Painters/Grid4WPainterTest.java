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

package MWC.GUI.Chart.Painters;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GUI.Chart.Painters.Grid4WPainter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GUI.Chart.Painters.Grid4WPainter.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import MWC.GUI.CanvasType;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

// ///////////////////////////////////////////////////////////
// member variables
// //////////////////////////////////////////////////////////

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class Grid4WPainterTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testIndexMgt() {
		// first some valid ones
		assertEquals(1, Grid4WPainter.indexOf("B"), "index not calculated properly");
		assertEquals(0, Grid4WPainter.indexOf("A"), "index not calculated properly");
		assertEquals(5, Grid4WPainter.indexOf("F"), "index not calculated properly");
		assertEquals(22, Grid4WPainter.indexOf("Y"), "index not calculated properly");
		assertEquals(24, Grid4WPainter.indexOf("AA"), "index not calculated properly");
		assertEquals(26, Grid4WPainter.indexOf("AC"), "index not calculated properly");
		assertEquals(47, Grid4WPainter.indexOf("AZ"), "index not calculated properly");
		// and some invalid ones
		assertEquals(INVALID_INDEX, Grid4WPainter.indexOf("3"), "invalid index not rejected properly");
		assertEquals(INVALID_INDEX, Grid4WPainter.indexOf(""), "invalid index not rejected properly");
		assertEquals(INVALID_INDEX, Grid4WPainter.indexOf("CC"), "invalid index not rejected properly");
		assertEquals(INVALID_INDEX, Grid4WPainter.indexOf("?A"), "invalid index not rejected properly");
		// now some reverse checks
		// first some valid ones
		assertEquals(Grid4WPainter.labelFor(1), "B", "index not calculated properly");
		assertEquals(Grid4WPainter.labelFor(0), "A", "index not calculated properly");
		assertEquals(Grid4WPainter.labelFor(5), "F", "index not calculated properly");
		assertEquals(Grid4WPainter.labelFor(22), "Y", "index not calculated properly");
		assertEquals(Grid4WPainter.labelFor(24), "AA", "index not calculated properly");
		assertEquals(Grid4WPainter.labelFor(26), "AC", "index not calculated properly");
		assertEquals(Grid4WPainter.labelFor(47), "AZ", "index not calculated properly");

	}

	@Test
	public void testInit() {
		final WorldLocation origin = new WorldLocation(2, 3, 2);
		final Grid4WPainter pt = new Grid4WPainter(origin);
		assertEquals(pt.getName(), DEFAULT_NAME, "wrong name");
		assertEquals(new WorldDistance(10, WorldDistance.NM), pt.getXDelta(), "wrong x def");
		assertEquals(new WorldDistance(10, WorldDistance.NM), pt.getYDelta(), "wrong y def");
		assertEquals(1, pt.getYMin().intValue(), "wrong init index");
		assertEquals(24, pt.getYMax().intValue(), "wrong init index");
		assertEquals("A", pt.getXMin(), "wrong init index");
		assertEquals("Z", pt.getXMax(), "wrong init index");
		assertEquals(2d, pt._origin.getLat(), "wrong origin lat");
		assertEquals(3d, pt._origin.getLong(), "wrong origin lat");
	}

	@Test
	public void testMyParams() {
		MWC.GUI.Editable ed = new Grid4WPainter(null);
		MWC.GUI.Editable.editableTesterSupport.testParams(ed);
		ed = null;
	}

	@Test
	public void testPosCalc() {
		// coords to get
		final int x = 1, y = 1;
		final WorldLocation origin = new WorldLocation(0, 0, 0);
		final double orientation = 0;
		final WorldDistance xDelta = new WorldDistance(1, WorldDistance.DEGS);
		final WorldDistance yDelta = new WorldDistance(1, WorldDistance.DEGS);

		final Grid4WPainter pt = new Grid4WPainter(origin);
		pt.setXDelta(xDelta);
		pt.setYDelta(yDelta);
		pt.setOrientation(orientation);
		WorldLocation newLoc = pt.calcLocationFor(1, 1);
		assertEquals(1d, newLoc.getLat(), 0.001, "easy Lat wrong");
		assertEquals(1d, newLoc.getLong(), 0.001, "easy Long wrong");
		newLoc = pt.calcLocationFor(2, 2);
		assertEquals(2d, newLoc.getLat(), 0.001, "easy Lat wrong");
		assertEquals(2d, newLoc.getLong(), 0.001, "easy Long wrong");

		// turn, baby
		pt.setOrientation(90d);
		newLoc = pt.calcLocationFor(x, y);
		assertEquals(-1d, newLoc.getLat(), 0.001, "easy Lat wrong");
		assertEquals(1d, newLoc.getLong(), 0.001, "easy Long wrong");

		// have another go
		pt.setXDelta(new WorldDistance(2, WorldDistance.DEGS));
		pt.setYDelta(new WorldDistance(3, WorldDistance.DEGS));
		newLoc = pt.calcLocationFor(x, y);
		assertEquals(-2d, newLoc.getLat(), 0.001, "easy Lat wrong");
		assertEquals(3d, newLoc.getLong(), 0.001, "easy Long wrong");

		// and turn back
		pt.setOrientation(0d);
		newLoc = pt.calcLocationFor(x, y);
		assertEquals(3d, newLoc.getLat(), "easy Lat wrong");
		assertEquals(2d, newLoc.getLong(), "easy Long wrong");

		// now try more complex orientations

	}

	@Test
	public void testProperties() {
		final Grid4WPainter pt = new Grid4WPainter(null);
		pt.setName("new grid");
		pt.setXDelta(new WorldDistance(12, WorldDistance.DEGS));
		pt.setYDelta(new WorldDistance(5, WorldDistance.DEGS));
		pt.setXMin("C");
		pt.setXMax("E");
		pt.setYMin(7);
		pt.setYMax(12);
		final WorldLocation origin = new WorldLocation(2, 2, 0);
		pt.setOrigin(origin);
		assertEquals("new grid", pt.getName(), "wrong name");
		assertEquals(new WorldDistance(12, WorldDistance.DEGS), pt.getXDelta(), "wrong x val");
		assertEquals(new WorldDistance(5, WorldDistance.DEGS), pt.getYDelta(), "wrong y val");
		assertEquals("C", pt.getXMin(), "wrong x index");
		assertEquals("E", pt.getXMax(), "wrong x index");
		assertEquals(7, pt.getYMin().intValue(), "wrong y index");
		assertEquals(12, pt.getYMax().intValue(), "wrong y index");
		assertEquals(origin, pt.getOrigin(), "wrong origin");
	}
}
