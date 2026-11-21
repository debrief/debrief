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

package org.mwc.cmap.xyplot.views.providers;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mwc.cmap.xyplot.views.providers.CrossSectionDatasetProvider.*;
import org.mwc.cmap.xyplot.views.providers.CrossSectionDatasetProvider.*;
import java.awt.Color;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.mwc.cmap.xyplot.views.CrossSectionViewer.SnailRenderer;
import org.mwc.cmap.xyplot.views.ILocationCalculator;
import org.mwc.cmap.xyplot.views.LocationCalculator;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class CrossSectionDatasetProviderTest {
	DateFormat _dateFormat = new GMTDateFormat("dd-MM-yyyy HH:mm");
	HiResDate[] _times;
	int TIME_ARRAY_SIZE = 7;

	TrackWrapper _track;
	LineShape _line;

	CrossSectionDatasetProvider _testable = new CrossSectionDatasetProvider();

	@BeforeEach
	public void setUp() throws ParseException {
		final WorldLocation start = new WorldLocation(0, 0, 0);
		final WorldLocation end = new WorldLocation(0, 1, 0);
		_line = new LineShape(start, end);

		_times = new HiResDate[TIME_ARRAY_SIZE];
		// every 10 minutes from 10:00 (building "10:60" as text broke the parse)
		final long start10 = _dateFormat.parse("09-09-2013 10:00").getTime();
		for (int i = 0; i < TIME_ARRAY_SIZE; i++) {
			_times[i] = new HiResDate(new Date(start10 + i * 10 * 60 * 1000L));
		}

		_track = new TrackWrapper();
		int j = 0;
		for (double i = 0.0; i <= 1.2; i += 0.2) {
			final WorldLocation loc = new WorldLocation(0, i, 0);
			final Fix fix = new Fix(_times[j], loc, 2, 2);
			_track.addFix(new FixWrapper(fix));
			j++;
		}
	}

	@Test
	public void testDiscreteSeries() throws ParseException {
		for (int i = 0; i < TIME_ARRAY_SIZE; i++) {
			final XYSeries series = _testable.getSeries(_line, _track, _times[i]);
			assertEquals(1, series.getItemCount());
			final XYDataItem item = series.getDataItem(0);
			assertNotNull(item.getXValue());
			assertEquals(0.0, item.getYValue(), 0.001); // depth is 0
		}
	}

	@Test
	public void testSnailSeries() throws ParseException {
		for (int i = 0; i < TIME_ARRAY_SIZE - 2; i++) {
			final XYSeries series = _testable.getSeries(_line, _track, _times[i], _times[i + 1], null, 0);
			assertEquals(2, series.getItemCount());
			for (int j = 0; j < 2; j++) {
				final XYDataItem item = series.getDataItem(j);
				assertNotNull(item.getXValue());
				assertEquals(0.0, item.getYValue(), 0.001); // depth is 0
			}
		}
	}

	@Test
	public void testSnailSeries2() throws ParseException {
		for (int i = 0; i < TIME_ARRAY_SIZE - 1; i++) {
			final XYSeries series = _testable.getSeries(_line, _track, _times[i], _times[i + 1], null, 0);
			assertEquals(2, series.getItemCount());
			for (int j = 0; j < 2; j++) {
				final XYDataItem item = series.getDataItem(j);
				assertNotNull(item.getXValue());
				assertEquals(0.0, item.getYValue(), 0.001); // depth is 0
			}
		}
	}

	@Test
	public void testSnailShading() throws ParseException {
		final SnailRenderer renderer = new SnailRenderer();
		final XYSeries series = _testable.getSeries(_line, _track, _times[0], _times[6], renderer, 0);
		assertEquals(7, series.getItemCount());
		for (int j = 0; j < 7; j++) {
			final XYDataItem item = series.getDataItem(j);
			assertNotNull(item.getXValue());
			assertEquals(0.0, item.getYValue(), 0.001); // depth is 0

			assertEquals((6d - j) / 6d, renderer.getProportionFor(0, j), 0.1, "has proportion");

			System.out.println("prop:" + renderer.getProportionFor(0, j));

		}

		// and the renderer
		assertNotNull(renderer.getProportionFor(0, 2), "has a color");

	}

}
