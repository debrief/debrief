/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2026, Deep Blue C Technology Ltd
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

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.SLD;
import org.geotools.api.style.Style;
import org.geotools.api.style.StyleFactory;
import org.geotools.xml.styling.SLDParser;
import org.geotools.api.feature.type.GeometryDescriptor;

/**
 * The style for a shapefile layer: the shapefile's own SLD file if it has one
 * (the same name with a .sld extension, as the coastline has), or else a plain
 * style for its geometry type.
 *
 * <p>
 * This replaces {@code org.geotools.temporal.object.Utils.createStyle}, the only use
 * Debrief made of the gt-swt library, which GeoTools no longer publishes. The
 * fallback styles match the ones gt-swt used: blue outlines, with a
 * half-transparent cyan fill for polygons and points.
 */
public final class ShapeFileStyles {

	/**
	 * the style for a shapefile
	 *
	 * @param shapeFile     the .shp file
	 * @param featureSource its features
	 * @return its SLD style, or a plain style for its geometry type
	 */
	public static Style createStyle(final File shapeFile, final SimpleFeatureSource featureSource)
			throws IOException {
		final File sld = sldFileFor(shapeFile);
		if (sld != null) {
			final StyleFactory factory = CommonFactoryFinder.getStyleFactory(null);
			final Style[] styles = new SLDParser(factory, sld.toURI().toURL()).readXML();
			if (styles.length > 0 && styles[0] != null) {
				return styles[0];
			}
		}
		return plainStyle(featureSource.getSchema().getGeometryDescriptor());
	}

	/** the SLD file beside a shapefile, or null if there isn't one */
	static File sldFileFor(final File shapeFile) {
		final String path = shapeFile.getAbsolutePath();
		final int dot = path.lastIndexOf('.');
		final String base = dot > 0 ? path.substring(0, dot) : path;
		for (final String ext : new String[] { ".sld", ".SLD" }) {
			final File sld = new File(base + ext);
			if (sld.exists()) {
				return sld;
			}
		}
		return null;
	}

	/** a plain style for a geometry type (polygon, line or point) */
	static Style plainStyle(final GeometryDescriptor geometry) {
		final Class<?> type = geometry == null ? null : geometry.getType().getBinding();
		if (type != null && (org.locationtech.jts.geom.Polygon.class.isAssignableFrom(type)
				|| org.locationtech.jts.geom.MultiPolygon.class.isAssignableFrom(type))) {
			return SLD.createPolygonStyle(Color.BLUE, Color.CYAN, 0.5f);
		}
		if (type != null && (org.locationtech.jts.geom.LineString.class.isAssignableFrom(type)
				|| org.locationtech.jts.geom.MultiLineString.class.isAssignableFrom(type))) {
			return SLD.createLineStyle(Color.BLUE, 1f);
		}
		return SLD.createPointStyle("Circle", Color.BLUE, Color.CYAN, 0.5f, 6f);
	}

	private ShapeFileStyles() {
	}
}
