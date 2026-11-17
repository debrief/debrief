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
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.projection.ProjectionException;
import org.junit.jupiter.api.Test;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
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
		final DirectPosition2D tlDegs = new DirectPosition2D(5, 1);
		final DirectPosition2D brDegs = new DirectPosition2D(1, 5);
		final Envelope2D env = new Envelope2D(tlDegs, brDegs);
		final ReferencedEnvelope rEnv = new ReferencedEnvelope(env, crs);
		mc.getViewport().setBounds(rEnv);

		// set a screen area
		mc.getViewport().setScreenArea(new Rectangle(0, 0, 800, 400));

		// sort out the aspect ration
		mc.getViewport().setMatchingAspectRatio(true);

		// create a point to test
		final DirectPosition2D degs = new DirectPosition2D(5, 4);

		// and results object
		final DirectPosition2D pixels = new DirectPosition2D();
		final DirectPosition2D rDegs = new DirectPosition2D();

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
		final DirectPosition2D tlDegs = new DirectPosition2D(45, -5);
		final DirectPosition2D brDegs = new DirectPosition2D(41, -1);
		final Envelope2D env = new Envelope2D(tlDegs, brDegs);
		final ReferencedEnvelope rEnv = new ReferencedEnvelope(env, crs);
		mc.getViewport().setBounds(rEnv);

		// set a screen area
		mc.getViewport().setScreenArea(new Rectangle(0, 0, 800, 200));

		// sort out the aspect ration
		mc.getViewport().setMatchingAspectRatio(true);

		// try with series of points
		System.out.println(
				"test 2:" + mc.getViewport().getWorldToScreen().transform(new DirectPosition2D(45, -4), null));
		System.out.println(
				"test 2:" + mc.getViewport().getWorldToScreen().transform(new DirectPosition2D(44, -4), null));
		System.out.println(
				"test 2:" + mc.getViewport().getWorldToScreen().transform(new DirectPosition2D(43, -4), null));
		System.out.println(
				"test 2:" + mc.getViewport().getWorldToScreen().transform(new DirectPosition2D(42, -4), null));
		System.out.println(
				"test 2:" + mc.getViewport().getWorldToScreen().transform(new DirectPosition2D(41, -4), null));

		// try with series of points
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new DirectPosition2D(45, -5), null));
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new DirectPosition2D(45, -4), null));
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new DirectPosition2D(45, -3), null));
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new DirectPosition2D(45, -2), null));
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new DirectPosition2D(45, -1), null));
		System.out.println(
				"test 3:" + mc.getViewport().getWorldToScreen().transform(new DirectPosition2D(45, 0), null));

	}

}
