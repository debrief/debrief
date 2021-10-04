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

package org.mwc.cmap.grideditor.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

public class ChartBuilder {

	protected static class ColorRenderer extends RendererWithDynamicFeedback {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private XYDataset _dataset;

		@Override
		public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea,
				final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis,
				final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState,
				final int pass) {
			// capture the dataset - so we can plot color-sensitive items
			_dataset = dataset;

			// let the parent continue with the draw operation
			super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item,
					crosshairState, pass);
		}

		@Override
		public Paint getItemPaint(final int row, final int column) {
			Paint res = null;
			// capture (and type-cast) the dataset
			if (_dataset instanceof TimeSeriesCollection) {
				final TimeSeriesCollection collection = (TimeSeriesCollection) _dataset;
				final TimeSeries series = collection.getSeries(row);
				final TimeSeriesDataItem item = series.getDataItem(column);
				if (item instanceof BackedTimeSeriesDataItem) {
					final BackedTimeSeriesDataItem bs = (BackedTimeSeriesDataItem) item;
					res = bs.getDomainItem().getColor();
				}
			} else if (_dataset instanceof XYSeriesCollection) {
				final XYSeriesCollection collection = (XYSeriesCollection) _dataset;
				final ScatteredXYSeries series = (ScatteredXYSeries) collection.getSeries(row);
				final XYDataItem item = series.getDataItem(column);
				if (item instanceof BackedXYDataItem) {
					final BackedXYDataItem var = (BackedXYDataItem) item;
					res = var.getDomainItem().getColor();
				}
			}

			// did we find anything?
			if (res == null)
				res = lookupSeriesFillPaint(column);

			return res;
		}

	}

	private final ChartDataManager myManager;

	public ChartBuilder(final ChartDataManager dataSetManager) {
		myManager = dataSetManager;
	}

	public JFreeChart buildChart() {
		final ValueAxis xAxis = myManager.createXAxis();
		final ValueAxis yAxis = myManager.createYAxis();
		final XYDataset data = myManager.getXYDataSet();
		final XYLineAndShapeRenderer renderer = new ColorRenderer();
		final XYPlot xyplot = new XYPlot(data, xAxis, yAxis, renderer);
		xyplot.setOrientation(PlotOrientation.HORIZONTAL);
		xyplot.setBackgroundPaint(Color.lightGray);
		xyplot.setDomainGridlinePaint(Color.white);
		xyplot.setRangeGridlinePaint(Color.white);
		xyplot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
		final JFreeChart result = new JFreeChart(myManager.getChartTitle(), JFreeChart.DEFAULT_TITLE_FONT, xyplot,
				false);
		ChartUtils.applyCurrentTheme(result);
		return result;
	}
}
