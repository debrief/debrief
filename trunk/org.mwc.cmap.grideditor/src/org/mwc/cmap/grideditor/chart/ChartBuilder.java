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

	public ChartBuilder(ChartDataManager dataSetManager) {
		myManager = dataSetManager;
	}

	public JFreeChart buildChart() {
		ValueAxis xAxis = myManager.createXAxis();
		ValueAxis yAxis = myManager.createYAxis();
		XYDataset data = myManager.getXYDataSet();
		XYLineAndShapeRenderer renderer = new RendererWithDynamicFeedback();
		XYPlot xyplot = new XYPlot(data, xAxis, yAxis, renderer);
		xyplot.setOrientation(PlotOrientation.HORIZONTAL);
		xyplot.setBackgroundPaint(Color.lightGray);
		xyplot.setDomainGridlinePaint(Color.white);
		xyplot.setRangeGridlinePaint(Color.white);
		xyplot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
		JFreeChart result = new JFreeChart(myManager.getChartTitle(), JFreeChart.DEFAULT_TITLE_FONT, xyplot, false);
		ChartUtilities.applyCurrentTheme(result);
		return result;
	}
}
