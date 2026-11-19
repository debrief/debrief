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

package org.mwc.cmap.geotools.gt2plot;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.cmap.geotools.gt2plot.GtProjection.*;
import org.mwc.cmap.geotools.gt2plot.GtProjection.*;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Iterator;
import org.geotools.geometry.Position2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.projection.ProjectionException;
import org.junit.jupiter.api.Test;
import org.geotools.api.geometry.BoundingBox;
import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import Debrief.GUI.Frames.Application;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.GeoToolsHandler;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class GtProjectionTest {
	@Test
	public void testOne() throws NoSuchAuthorityCodeException, FactoryException, NoninvertibleTransformException {
		final MapContent mc = new MapContent();

		// set a coordinate reference system
		final CoordinateReferenceSystem crs = CRS.decode(DATA_PROJECTION);
		mc.getViewport().setCoordinateReferenceSystem(crs);

		// set a data area
		final Position2D tlDegs = new Position2D(5, 1);
		final Position2D brDegs = new Position2D(1, 5);
		final ReferencedEnvelope rEnv = new ReferencedEnvelope(tlDegs.x, brDegs.x, tlDegs.y, brDegs.y, crs);
		mc.getViewport().setBounds(rEnv);

		// set a screen area
		mc.getViewport().setScreenArea(new Rectangle(0, 0, 800, 400));

		// sort out the aspect ration
		mc.getViewport().setMatchingAspectRatio(true);

		// create a point to test
		final Position2D degs = new Position2D(5, 4);

		// and results object
		final Position2D pixels = new Position2D();
		final Position2D rDegs = new Position2D();

		// transform the test point
		mc.getViewport().getWorldToScreen().transform(degs, pixels);

		System.out.println("pixels:" + pixels);
		assertEquals(600, (int) pixels.x, "correct x");
		assertEquals(600, (int) pixels.x, "correct y");

		// and the reverse transform
		mc.getViewport().getWorldToScreen().inverseTransform(pixels, rDegs);

		System.out.println("degs:" + rDegs);
		assertEquals(5, (int) rDegs.x, "correct x");
		assertEquals(4, (int) rDegs.y, "correct y");
	}

	@Test
	public void testTwo() throws NoSuchAuthorityCodeException, FactoryException, NoninvertibleTransformException {
		final MapContent mc = new MapContent();

		// set a coordinate reference system
		final CoordinateReferenceSystem crs = CRS.decode(DATA_PROJECTION);
		mc.getViewport().setCoordinateReferenceSystem(crs);

		// set a data area
		final Position2D tlDegs = new Position2D(45, -5);
		final Position2D brDegs = new Position2D(41, -1);
		final ReferencedEnvelope rEnv = new ReferencedEnvelope(tlDegs.x, brDegs.x, tlDegs.y, brDegs.y, crs);
		mc.getViewport().setBounds(rEnv);

		// set a screen area
		mc.getViewport().setScreenArea(new Rectangle(0, 0, 800, 200));

		// sort out the aspect ration
		mc.getViewport().setMatchingAspectRatio(true);

		// try with series of points
		System.out.println(
				"test 2:" + mc.getViewport().getWorldToScreen().transform(new Position2D(45, -4), null));
		System.out.println(
				"test 2:" + mc.getViewport().getWorldToScreen().transform(new Position2D(44, -4), null));
		System.out.println(
				"test 2:" + mc.getViewport().getWorldToScreen().transform(new Position2D(43, -4), null));
		System.out.println(
				"test 2:" + mc.getViewport().getWorldToScreen().transform(new Position2D(42, -4), null));
		System.out.println(
				"test 2:" + mc.getViewport().getWorldToScreen().transform(new Position2D(41, -4), null));

		// try with series of points
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new Position2D(45, -5), null));
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new Position2D(45, -4), null));
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new Position2D(45, -3), null));
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new Position2D(45, -2), null));
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new Position2D(45, -1), null));
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new Position2D(45, 0), null));

	}

}
