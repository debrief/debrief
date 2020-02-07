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

package org.mwc.debrief.lite.map;

import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import Debrief.GUI.Frames.Application;
import MWC.GUI.ToolParent;

/**
 *
 * @author Unni Mana <unnivm@gmail.com>
 *
 */
public class GeoToolMapRenderer {

	public static interface MapRenderer {
		public void paint(final Graphics gc);
	}

	private final LiteMapPane mapPane;

	private final MapContent mapContent;

	private Graphics graphics;

	private SimpleFeatureSource featureSource;

	private final MathTransform _transform;

	private final List<MapRenderer> _myRenderers = new ArrayList<MapRenderer>();

	public GeoToolMapRenderer(final float alpha, final MapContent _mapContent, final MathTransform transform) {
		super();

		// Create a map content and add our shape file to it
		mapContent = _mapContent;
		_transform = transform;
		mapContent.setTitle("Debrief Lite");

		mapPane = new LiteMapPane(this, alpha);
		final StreamingRenderer streamer = new StreamingRenderer();
		mapPane.setRenderer(streamer);
		mapPane.setMapContent(mapContent);
	}

	public void addRenderer(final MapRenderer renderer) {
		_myRenderers.add(renderer);
	}

	/**
	 * returns java.awt.Graphics object
	 *
	 * @return
	 */
	public Graphics getGraphicsContext() {
		return graphics;
	}

	public Component getMap() {
		return mapPane;
	}

	/**
	 * return map component
	 *
	 * @return
	 */
	public MapContent getMapComponent() {
		return mapContent;
	}

	public MathTransform getTransform() {
		return _transform;
	}

	/**
	 * gets a MathTransform object
	 *
	 * @return MathTransform
	 */
	public MathTransform getTransformObject() {
		final SimpleFeatureType schema = featureSource.getSchema();
		final CoordinateReferenceSystem dataCRS = schema.getCoordinateReferenceSystem();
		final CoordinateReferenceSystem worldCRS = mapContent.getCoordinateReferenceSystem();
		MathTransform transform = null;
		try {
			transform = CRS.findMathTransform(dataCRS, worldCRS);
		} catch (final FactoryException e) {
			Application.logError2(ToolParent.ERROR, "Failure in projection transform", e);
		}
		return transform;
	}

	public void paintEvent(final Graphics arg0) {
		for (final MapRenderer r : _myRenderers) {
			r.paint(arg0);
		}
	}

}
