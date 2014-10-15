/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.grideditor.chart;

import java.awt.Color;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

public class ChartBuilder {

	private final ChartDataManager myManager;

	public ChartBuilder(final ChartDataManager dataSetManager) {
		myManager = dataSetManager;
	}

	public JFreeChart buildChart() {
		final ValueAxis xAxis = myManager.createXAxis();
		final ValueAxis yAxis = myManager.createYAxis();
		final XYDataset data = myManager.getXYDataSet();
		final XYLineAndShapeRenderer renderer = new RendererWithDynamicFeedback();
		final XYPlot xyplot = new XYPlot(data, xAxis, yAxis, renderer);
		xyplot.setOrientation(PlotOrientation.HORIZONTAL);
		xyplot.setBackgroundPaint(Color.lightGray);
		xyplot.setDomainGridlinePaint(Color.white);
		xyplot.setRangeGridlinePaint(Color.white);
		xyplot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
		final JFreeChart result = new JFreeChart(myManager.getChartTitle(), JFreeChart.DEFAULT_TITLE_FONT, xyplot, false);
		ChartUtilities.applyCurrentTheme(result);
		return result;
	}
}
