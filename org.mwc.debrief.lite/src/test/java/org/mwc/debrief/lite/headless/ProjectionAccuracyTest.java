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

package org.mwc.debrief.lite.headless;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.Position2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mwc.debrief.lite.LiteApplication;
import org.mwc.debrief.lite.LiteSession;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.util.PlotFiles;
import org.geotools.api.referencing.operation.MathTransform;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.ImportManager;

/**
 * Position accuracy of Lite's map projection, for the GeoTools upgrade (plan
 * Phase D1: "coordinates are within 1 m when projected and transformed back on
 * the sample plots").
 *
 * <p>
 * Every fix in the sample plots is put through {@link GeoToolMapProjection}:
 * <ul>
 * <li>degrees to the map's Mercator metres and back must return to within 1 m;
 * <li>the Mercator metres must agree, to within 1 m, with the WGS84 ellipsoidal
 * Mercator formula (EPSG:3395) worked out here independently of GeoTools, so a
 * changed projection definition fails even if it still round-trips;
 * <li>degrees to screen pixels and back (what the user clicks) must return to
 * within one pixel's width on the ground.
 * </ul>
 */
public class ProjectionAccuracyTest {

	/** WGS84 semi-major axis, metres */
	private static final double A = 6378137.0;

	/** WGS84 first eccentricity */
	private static final double E = Math.sqrt(2 / 298.257223563 - Math.pow(1 / 298.257223563, 2));

	private static final double ONE_METRE = 1.0;

	/** the sample plots whose fixes are checked: Atlantic and UK waters */
	private static final String[] PLOTS = { "sample.dpf", "boat1.rep", "uk_track.rep" };

	/** WGS84 ellipsoidal Mercator, EPSG:3395: {x, y} in metres */
	static double[] mercator(final double latDegs, final double longDegs) {
		final double phi = Math.toRadians(latDegs);
		final double esin = E * Math.sin(phi);
		final double y = A * Math.log(Math.tan(Math.PI / 4 + phi / 2) * Math.pow((1 - esin) / (1 + esin), E / 2));
		return new double[] { A * Math.toRadians(longDegs), y };
	}

	/** great-circle distance in metres, near enough for errors of a metre or so */
	private static double metresBetween(final WorldLocation a, final WorldLocation b) {
		return a.rangeFrom(b) * 60 * 1852;
	}

	private LiteApplication _app;

	private List<WorldLocation> fixesIn(final Layers layers) {
		final List<WorldLocation> res = new ArrayList<>();
		for (final TrackWrapper track : HeadlessFixture.tracks(layers)) {
			for (final FixWrapper fix : HeadlessFixture.fixes(track)) {
				res.add(fix.getLocation());
			}
		}
		return res;
	}

	private Layers load(final String sampleFile) throws Exception {
		final LiteSession session = HeadlessFixture.newSession(_app);
		final File file = new File(HeadlessFixture.SAMPLE_DATA, sampleFile);
		if (sampleFile.endsWith(".dpf")) {
			PlotFiles.importPlot(file, session, _app);
		} else {
			ImportManager.importThese(new File[] { file }, session.getData(), null);
		}
		return session.getData();
	}

	@BeforeEach
	public void setUp() throws Exception {
		_app = HeadlessFixture.newApp();
	}

	/** the formula itself, against published EPSG:3395 values */
	@Test
	public void testMercatorFormula() {
		final double[] origin = mercator(0, 0);
		assertEquals(0, origin[0], 1e-6);
		assertEquals(0, origin[1], 1e-6);
		// 51.5N 0.1W: PROJ (cs2cs EPSG:4326 EPSG:3395) gives -11131.9491, 6676757.7541
		final double[] london = mercator(51.5, -0.1);
		assertEquals(-11131.9491, london[0], 0.001);
		assertEquals(6676757.7541, london[1], 0.001);
	}

	/** degrees to Mercator and back, and against the formula, for every fix */
	@Test
	public void testDegreesToMercatorAndBack() throws Exception {
		final MapContent map = new MapContent();
		try {
			for (final String plot : PLOTS) {
				final Layers layers = load(plot);
				final GeoToolMapProjection projection = new GeoToolMapProjection(map, layers);
				final MathTransform toMap = projection.getDataTransform();
				final MathTransform fromMap = toMap.inverse();
				final List<WorldLocation> fixes = fixesIn(layers);
				assertTrue(fixes.size() > 100, plot + " has fixes");

				double worstTrip = 0;
				double worstFormula = 0;
				for (final WorldLocation loc : fixes) {
					final Position2D degs = new Position2D(loc.getLong(), loc.getLat());
					final Position2D metres = new Position2D();
					toMap.transform(degs, metres);

					final double[] expected = mercator(loc.getLat(), loc.getLong());
					worstFormula = Math.max(worstFormula,
							Math.hypot(metres.x - expected[0], metres.y - expected[1]));

					final Position2D back = new Position2D();
					fromMap.transform(metres, back);
					worstTrip = Math.max(worstTrip, metresBetween(loc, new WorldLocation(back.y, back.x, 0)));
				}
				assertTrue(worstTrip < ONE_METRE, plot + ": worst round trip " + worstTrip + " m");
				assertTrue(worstFormula < ONE_METRE,
						plot + ": worst difference from EPSG:3395 " + worstFormula + " m");
			}
		} finally {
			map.dispose();
		}
	}

	/**
	 * degrees to screen pixels and back, as the map does when drawing and when
	 * the user clicks: within one pixel on the ground
	 */
	@Test
	public void testDegreesToScreenAndBack() throws Exception {
		for (final String plot : PLOTS) {
			final MapContent map = new MapContent();
			try {
				final Layers layers = load(plot);
				map.getViewport().setMatchingAspectRatio(true);
				map.getViewport().setScreenArea(new Rectangle(0, 0, 800, 600));
				final GeoToolMapProjection projection = new GeoToolMapProjection(map, layers);
				final WorldArea area = layers.getBounds();
				projection.setDataArea(area);
				final ReferencedEnvelope degs = new ReferencedEnvelope(area.getTopLeft().getLong(),
						area.getBottomRight().getLong(), area.getBottomRight().getLat(), area.getTopLeft().getLat(),
						CRS.decode("EPSG:4326"));
				map.getViewport().setBounds(degs.transform(map.getViewport().getCoordinateReferenceSystem(), true));

				// one pixel on the ground, at the centre of the plot
				final WorldLocation centre = area.getCentre();
				final Point p = projection.toScreen(centre);
				final double pixelMetres = metresBetween(projection.toWorld(new Point(p.x, p.y)),
						projection.toWorld(new Point(p.x + 1, p.y + 1)));
				assertTrue(pixelMetres > 0, plot + ": pixel size " + pixelMetres + " m");

				double worst = 0;
				for (final WorldLocation loc : fixesIn(layers)) {
					final Point screen = projection.toScreen(loc);
					assertNotNull(screen, plot + ": on screen");
					worst = Math.max(worst, metresBetween(loc, projection.toWorld(screen)));
				}
				assertTrue(worst <= pixelMetres,
						plot + ": worst screen round trip " + worst + " m against a pixel of " + pixelMetres + " m");
			} finally {
				map.dispose();
			}
		}
	}
}
