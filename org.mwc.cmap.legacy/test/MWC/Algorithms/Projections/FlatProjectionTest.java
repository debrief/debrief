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

package MWC.Algorithms.Projections;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.Algorithms.Projections.FlatProjection.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.Algorithms.Projections.FlatProjection.*;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.EarthModels.CompletelyFlatEarth;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FlatProjectionTest {

	FlatProjection proj;

	@BeforeEach
	public void setUp() {
		proj = new FlatProjection();
		WorldLocation.setModel(new CompletelyFlatEarth());

		final WorldLocation oldTopLeft = new WorldLocation(0, 0, 0);
		final WorldLocation oldBottomRight = new WorldLocation(10, 10, 0);
		final WorldArea oldArea = new WorldArea(oldTopLeft, oldBottomRight);
		proj.setDataArea(oldArea);
		proj.setScreenArea(new Dimension(10, 10));
	}

	@Test
	public void testZoomAreaBottomRightCorner() {
		final WorldLocation topLeft = new WorldLocation(5, 5, 0);
		final WorldLocation bottomRight = new WorldLocation(10, 10, 0);
		final WorldArea area = new WorldArea(topLeft, bottomRight);

		proj.setScaleVal(1);
		proj.zoom(2, area);

		assertEquals(new WorldLocation(10, -10, 0), proj.getDataArea().getTopLeft());
		assertEquals(new WorldLocation(-10, 10, 0), proj.getDataArea().getBottomRight());
	}

	@Test
	public void testZoomAreaTopLeftCorner() {
		final WorldLocation topLeft = new WorldLocation(0, 0, 0);
		final WorldLocation bottomRight = new WorldLocation(5, 5, 0);
		final WorldArea area = new WorldArea(topLeft, bottomRight);
		// after 'normalization' in WorldArea constructor
		// topLeft is 5 (lat), 0 (lon)
		// bottomRight is 0 (lat), 5 (lon)

		proj.setScaleVal(1);
		proj.zoom(2, area);

		assertEquals(new WorldLocation(20, 0, 0), proj.getDataArea().getTopLeft());
		assertEquals(new WorldLocation(0, 20, 0), proj.getDataArea().getBottomRight());
	}
}
