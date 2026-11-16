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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mwc.cmap.geotools.gt2plot.ShapeFileLayer;
import org.mwc.debrief.lite.LiteApplication;
import org.mwc.debrief.lite.LiteSession;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.util.PlotFiles;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import Debrief.Testing.GoldenImage;
import MWC.GUI.Layers;
import MWC.GUI.Canvas.ExtendedCanvasAdapter;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.ImportManager;

/**
 * Screenshot tests of map rendering: draw sample plots, and the coastline
 * shapefile Lite uses as its backdrop, headless at fixed sizes, the way Lite's
 * map pane draws them, and compare each image with a committed reference PNG.
 *
 * <p>
 * The coastline goes through GeoTools (shapefile reading, styling, the Mercator
 * projection and the renderer); the tracks, sensors and shapes go through
 * Debrief's own canvas, projected by {@link GeoToolMapProjection}. So these
 * catch a GeoTools or Java 2D upgrade that moves, restyles or drops what the
 * user sees.
 *
 * <p>
 * The comparison allows small differences (font rasterising and antialiasing
 * vary between machines): see {@link #CHANNEL_TOLERANCE} and
 * {@link #MAX_DIFFERENT}. The references were made on CI's Linux runner
 * (Ubuntu 24.04): fonts differ between machines, so text-heavy images made
 * elsewhere fail.
 * Regenerate them as described in
 * {@code org.mwc.debrief.legacy/test_data/golden/README.md}.
 */
public class MapRenderingTest {

	/** largest per-channel colour difference that still counts as the same pixel */
	static final int CHANNEL_TOLERANCE = 48;

	/**
	 * largest share of different pixels that still passes: 0.2%, about 1,000
	 * pixels at 800x600. {@link #testDetectsSmallShift} checks this still catches
	 * the track moving by a few pixels.
	 */
	static final double MAX_DIFFERENT = 0.002;

	/** Lite's backdrop: Natural Earth 1:10M countries (relative to the Lite plugin) */
	private static final String COASTLINE = "data/coastline/ne_10M_admin0_countries_89S.shp";

	/** the map's background (Lite's map pane is white) */
	private static final Color BACKGROUND = Color.WHITE;

	/** around uk_track.rep, with the Pembrokeshire coast to the east */
	private static final WorldArea PEMBROKESHIRE = new WorldArea(new WorldLocation(52.1, -6.3, 0),
			new WorldLocation(51.55, -5.0, 0));

	private LiteApplication _app;

	/** the area the plot fills, with a margin, as Lite's "fit to window" does */
	private static ReferencedEnvelope fitTo(final WorldArea area, final CoordinateReferenceSystem degs)
			throws Exception {
		final WorldLocation tl = area.getTopLeft();
		final WorldLocation br = area.getBottomRight();
		final double marginLat = Math.max(0.01, (tl.getLat() - br.getLat()) * 0.1);
		final double marginLong = Math.max(0.01, (br.getLong() - tl.getLong()) * 0.1);
		return new ReferencedEnvelope(tl.getLong() - marginLong, br.getLong() + marginLong,
				br.getLat() - marginLat, tl.getLat() + marginLat, degs);
	}

	/**
	 * draw the layers, and optionally the coastline beneath them, into an image
	 * of the given size, zoomed to the given area (or to the layers' bounds)
	 */
	private static BufferedImage render(final Layers layers, final boolean coastline, final WorldArea area,
			final int width, final int height) throws Exception {
		final MapContent map = new MapContent();
		try {
			final MapViewport viewport = map.getViewport();
			viewport.setMatchingAspectRatio(true);
			final Rectangle screen = new Rectangle(0, 0, width, height);
			viewport.setScreenArea(screen);

			// as Lite: the map in Mercator, data in WGS84 degrees
			final GeoToolMapProjection projection = new GeoToolMapProjection(map, layers);
			if (coastline) {
				final ShapeFileLayer coast = new ShapeFileLayer("Background coastline", COASTLINE);
				projection.addGeoToolsLayer(coast);
				assertEquals(1, map.layers().size(), "coastline loaded into the map");
			}

			final CoordinateReferenceSystem degs = CRS.decode("EPSG:4326");
			final ReferencedEnvelope fit = fitTo(area != null ? area : layers.getBounds(), degs);
			viewport.setBounds(fit.transform(viewport.getCoordinateReferenceSystem(), true));

			final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			final Graphics2D g = image.createGraphics();
			try {
				g.setColor(BACKGROUND);
				g.fillRect(0, 0, width, height);
				// fixed hints, so the output does not depend on the machine's defaults
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				// the GeoTools layers, as Lite's map pane draws them
				if (!map.layers().isEmpty()) {
					final StreamingRenderer renderer = new StreamingRenderer();
					renderer.setJava2DHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_OFF));
					renderer.setMapContent(map);
					renderer.paint(g, screen, viewport.getBounds());
				}

				// then Debrief's layers on top, as DebriefLiteApp.doPaint does
				final ExtendedCanvasAdapter dest = new ExtendedCanvasAdapter(projection, g, Color.red);
				dest.setLineWidth(2f);
				dest.startDraw(g);
				layers.paint(dest);
				dest.endDraw(g);
			} finally {
				g.dispose();
			}
			return image;
		} finally {
			map.dispose();
		}
	}

	private Layers load(final String sampleFile) throws Exception {
		final LiteSession session = HeadlessFixture.newSession(_app);
		final File file = new File(HeadlessFixture.SAMPLE_DATA, sampleFile);
		if (sampleFile.endsWith(".dpf")) {
			PlotFiles.importPlot(file, session, _app);
		} else {
			// as Lite imports a REP file: through the import manager
			ImportManager.importThese(new File[] { file }, session.getData(), null);
		}
		final Layers layers = session.getData();
		assertNotNull(layers.getBounds(), sampleFile + " has bounds");
		return layers;
	}

	private static void check(final String name, final BufferedImage image) throws Exception {
		GoldenImage.assertMatches("rendering/" + name + ".png", image, CHANNEL_TOLERANCE, MAX_DIFFERENT);
	}

	@BeforeEach
	public void setUp() throws Exception {
		_app = HeadlessFixture.newApp();
	}

	/** the coastline alone, over the British Isles and the North Sea */
	@Test
	public void testCoastline() throws Exception {
		final WorldArea britishIsles = new WorldArea(new WorldLocation(61, -11, 0), new WorldLocation(49, 9, 0));
		check("coastline_british_isles_800x600", render(new Layers(), true, britishIsles, 800, 600));
	}

	/**
	 * uk_track.rep, off south-west Wales, over the coastline: Debrief's track
	 * must land in the same place on GeoTools' map
	 */
	@Test
	public void testTrackOverCoastline() throws Exception {
		check("uk_track_coastline_800x600", render(load("uk_track.rep"), true, PEMBROKESHIRE, 800, 600));
	}

	/**
	 * the tolerance is tight enough: the same plot, with the map panned by about
	 * 3 pixels (0.005 degrees), fails the comparison
	 */
	@Test
	public void testDetectsSmallShift() throws Exception {
		final BufferedImage base = render(load("uk_track.rep"), true, PEMBROKESHIRE, 800, 600);
		final WorldArea panned = new WorldArea(new WorldLocation(52.1, -6.295, 0),
				new WorldLocation(51.55, -4.995, 0));
		final BufferedImage moved = render(load("uk_track.rep"), true, panned, 800, 600);
		final GoldenImage.Comparison c = GoldenImage.compare(base, moved, CHANNEL_TOLERANCE);
		assertTrue(c.fraction() > MAX_DIFFERENT,
				"a 3-pixel pan should exceed the limit, but only " + c.differentPixels + " pixels differ");
	}

	/** sample.dpf: tracks, sensors, shapes and labels */
	@Test
	public void testSample() throws Exception {
		check("sample_dpf_800x600", render(load("sample.dpf"), false, null, 800, 600));
	}

	/** sample.dpf at a second, smaller size, without the coastline */
	@Test
	public void testSampleSmall() throws Exception {
		check("sample_dpf_400x300", render(load("sample.dpf"), false, null, 400, 300));
	}

	/**
	 * shapes.rep: rectangles, lines, vectors, circles in each line style and
	 * fill, text, and the SVG symbols
	 */
	@Test
	public void testShapes() throws Exception {
		check("shapes_rep_800x600", render(load("shapes.rep"), false, null, 800, 600));
	}

	/**
	 * the comparison itself: an identical image passes, and a clearly different
	 * one is counted as different
	 */
	@Test
	public void testComparison() {
		final BufferedImage a = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		final BufferedImage b = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		assertEquals(0, GoldenImage.compare(a, b, 0).differentPixels);
		b.setRGB(3, 4, 0xffffff);
		b.setRGB(5, 6, 0x101010); // within a tolerance of 16
		assertEquals(1, GoldenImage.compare(a, b, 16).differentPixels);
		assertEquals(2, GoldenImage.compare(a, b, 0).differentPixels);
	}
}
