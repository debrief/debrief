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

package MWC.GUI.Shapes;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GUI.Shapes.PolygonShape.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GUI.Shapes.PolygonShape.*;
import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import MWC.GUI.CanvasType;
import MWC.GUI.CreateEditorForParent;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.FireExtended;
import MWC.GUI.Layer;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

public class PolygonShapeTest {
	@Test
	public void testConstructor() {
		final Vector<PolygonNode> nodes = new Vector<PolygonNode>();
		final PolygonShape poly = new PolygonShape(nodes);
		nodes.add(new PolygonNode("1", new WorldLocation(2, 2, 0d), poly));
	}

	@Test
	public void testRangeFromEmpty() {
		final Vector<PolygonNode> nodes = new Vector<PolygonNode>();
		final PolygonShape poly = new PolygonShape(nodes);
		assertEquals(Plottable.INVALID_RANGE, poly.rangeFrom(new WorldLocation(3, 3, 3d)), 0.001,
				"returns duff range");
	}

	@Test
	public void testRangeIdenticalPoints() {
		final Vector<PolygonNode> nodes = new Vector<PolygonNode>();
		final PolygonShape poly = new PolygonShape(nodes);
		final WorldLocation origin = new WorldLocation(4, 4, 0d);
		final WorldLocation locA = new WorldLocation(3, 3, 0d);
		final WorldLocation locB = new WorldLocation(3, 3, 0d);
		final double realRange = origin.rangeFrom(locA);
		poly.add(new PolygonNode("1", locA, poly));
		poly.add(new PolygonNode("2", locB, poly));
		assertEquals(realRange, poly.rangeFrom(origin), "returns valid range");
	}

	@Test
	public void testRangeMultiPoints() {
		final Vector<PolygonNode> nodes = new Vector<PolygonNode>();
		final PolygonShape poly = new PolygonShape(nodes);
		final WorldLocation origin = new WorldLocation(4, 4, 0d);
		final WorldLocation locA = new WorldLocation(3, 3, 0d);
		final WorldLocation locB = new WorldLocation(5, 5, 0d);
		final double realRange = 0d; // origin is mid-way between the points
		poly.add(new PolygonNode("1", locA, poly));
		poly.add(new PolygonNode("2", locB, poly));
		assertEquals(realRange, poly.rangeFrom(origin), "returns valid range");
	}

	@Test
	public void testRangeSinglePoint() {
		final Vector<PolygonNode> nodes = new Vector<PolygonNode>();
		final PolygonShape poly = new PolygonShape(nodes);
		final WorldLocation origin = new WorldLocation(4, 4, 0d);
		final WorldLocation locA = new WorldLocation(3, 3, 0d);
		final double realRange = origin.rangeFrom(locA);
		poly.add(new PolygonNode("1", locA, poly));
		assertEquals(realRange, poly.rangeFrom(origin), "returns valid range");
	}
}
