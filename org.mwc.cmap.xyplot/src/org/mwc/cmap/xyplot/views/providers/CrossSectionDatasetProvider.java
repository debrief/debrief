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

import java.awt.Color;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.mwc.cmap.xyplot.views.CrossSectionViewer.SnailRenderer;
import org.mwc.cmap.xyplot.views.ILocationCalculator;
import org.mwc.cmap.xyplot.views.LocationCalculator;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;

public class CrossSectionDatasetProvider implements ICrossSectionDatasetProvider {

	private final ILocationCalculator _calc;

	private final Map<Integer, Color> _seriesColors = new HashMap<Integer, Color>();

	public CrossSectionDatasetProvider() {
		this(WorldDistance.KM);
	}

	public CrossSectionDatasetProvider(final int units) {
		_calc = new LocationCalculator(units);
	}

	@Override
	public XYSeriesCollection getDataset(final LineShape line, final Layers layers, final HiResDate timeT) {
		return walk(layers, line, timeT, null, null);
	}

	@Override
	public XYSeriesCollection getDataset(final LineShape line, final Layers layers, final HiResDate startT,
			final HiResDate endT, final SnailRenderer renderer) {
		return walk(layers, line, startT, endT, renderer);
	}

	private XYSeries getSeries(final LineShape line, final String seriesName, final Watchable[] wbs,
			final SnailRenderer renderer, final HiResDate startT, final HiResDate endT, final int colCounter) {
		final XYSeries series = new XYSeries(seriesName, false, true);
		final int rowCounter = 0;
		for (final Watchable wb : wbs) {
			final Double x_coord = new Double(_calc.getDistance(line, wb));
			final Double y_coord = new Double(-1.0 * wb.getDepth());
			series.add(x_coord, y_coord);

			// do we have a renderer?
			if (renderer != null) {
				// ok, work out how far back we are
				final long elapsed = wb.getTime().getMicros() - startT.getMicros();
				final double proportion = (double) elapsed / (endT.getMicros() - startT.getMicros());
				renderer.setRowColProportion(colCounter, rowCounter, 1d - proportion);
			}
		}
		return series;
	}

	XYSeries getSeries(final LineShape line, final TrackWrapper wlist, final HiResDate timeT) {
		final Watchable[] wbs = wlist.getNearestTo(timeT);
		return getSeries(line, wlist.getName(), wbs, null, null, null, 0);
	}

	XYSeries getSeries(final LineShape line, final TrackWrapper wlist, final HiResDate startT, final HiResDate endT,
			final SnailRenderer renderer, final int colCounter) {
		final Collection<Editable> editables = wlist.getItemsBetween(startT, endT);
		final String theName = wlist.getName();
		if (editables == null)
			return new XYSeries(theName, false, true);
		final Watchable[] wbs = editables.toArray(new Watchable[editables.size()]);
		return getSeries(line, theName, wbs, renderer, startT, endT, colCounter);
	}

	@Override
	public Map<Integer, Color> getSeriesColors() {
		return _seriesColors;
	}

	private XYSeriesCollection walk(final Layers layers, final LineShape line, final HiResDate startT,
			final HiResDate endT, final SnailRenderer renderer) {
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final Enumeration<Editable> numer = layers.elements();

		// ok, reset the renderer
		if (renderer != null) {
			renderer.reset();
		}
		int colCounter = 0;

		while (numer.hasMoreElements()) {
			final Editable next = numer.nextElement();
			if (next instanceof WatchableList) {
				final WatchableList wlist = (WatchableList) next;
				if (wlist.getVisible()) {
					if (wlist instanceof TrackWrapper) {
						final TrackWrapper track = (TrackWrapper) wlist;

						if (endT != null) {
							final XYSeries series = getSeries(line, track, startT, endT, renderer, colCounter++);
							dataset.addSeries(series);
						} else {
							final XYSeries series = getSeries(line, track, startT);
							dataset.addSeries(series);
						}
						_seriesColors.put(new Integer(dataset.getSeriesCount() - 1), wlist.getColor());
					}
				}
			}
		}
		return dataset;
	}

}
