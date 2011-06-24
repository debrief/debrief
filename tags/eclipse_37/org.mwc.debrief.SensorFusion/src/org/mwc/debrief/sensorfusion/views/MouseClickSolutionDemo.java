package org.mwc.debrief.sensorfusion.views;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MouseClickSolutionDemo extends ApplicationFrame {

	/**
    *
    */
	private static final long serialVersionUID = 1L;

	/**
	 * @param title
	 *            the frame title.
	 */
	public MouseClickSolutionDemo(String title) {
		super(title);

		TimeSeries s1 = new TimeSeries("Series to click");
		s1.add(new Month(2, 2001), 181.8);
		s1.add(new Month(3, 2001), 167.3);
		s1.add(new Month(4, 2001), 153.8);
		s1.add(new Month(5, 2001), 167.6);
		s1.add(new Month(6, 2001), 152.8);
		s1.add(new Month(7, 2001), 148.3);
		s1.add(new Month(8, 2001), 153.9);
		s1.add(new Month(9, 2001), 142.7);
		s1.add(new Month(10, 2001), 123.2);

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(s1);

		final JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"[Alt]-click to switch orientation", // title
				"Time axis", // x-axis label
				"Value axis", // y-axis label
				dataset, // data
				false, // create legend?
				false, // generate tooltips?
				false // generate URLs?
				);

		//FIX IS HERE
		fixProblem(chart);

		ChartPanel chartPanel = new ChartPanel(chart);

		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

		chartPanel.addChartMouseListener(new ChartMouseListener() {
			public void chartMouseMoved(ChartMouseEvent arg0) {
			}

			public void chartMouseClicked(ChartMouseEvent arg0) {
				System.out.println("clicked on:" + arg0.getEntity());

				if (arg0.getTrigger().isAltDown()) {
					if (chart.getXYPlot().getOrientation() == PlotOrientation.HORIZONTAL)
						chart.getXYPlot().setOrientation(
								PlotOrientation.VERTICAL);
					else
						chart.getXYPlot().setOrientation(
								PlotOrientation.HORIZONTAL);
				}
			}
		});
		setContentPane(chartPanel);
	}

	private void fixProblem(JFreeChart chart) {
		if (chart.getPlot() instanceof XYPlot) {
			XYPlot plot = (XYPlot) chart.getPlot();
			fixProblem(plot);
		}
	}

	private void fixProblem(XYPlot plot) {
		for (int i = 0; i < plot.getRendererCount(); i++) {
			XYItemRenderer renderer = plot.getRenderer(i);
			XYItemRenderer fixed = XYLineAndShapeRendererFix
					.newFixedVersion(renderer);
			if (renderer != fixed) {
				plot.setRenderer(i, fixed);
			}
		}
	}

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {
		MouseClickSolutionDemo demo = new MouseClickSolutionDemo(
				"Time Series Demo 1");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

	public static class XYLineAndShapeRendererFix extends
			XYLineAndShapeRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public static XYItemRenderer newFixedVersion(XYItemRenderer renderer) {
			if (false == renderer instanceof XYLineAndShapeRenderer) {
				return renderer;
			}
			if (renderer instanceof XYLineAndShapeRendererFix) {
				// fixed already
				return renderer;
			}
			if (renderer.getClass() != XYLineAndShapeRenderer.class) {
				System.err
						.println("I can't fix subclass of the XYLineAndShapeRenderer: "
								+ renderer.getClass()
								+ ", you need to make similar fix yourself");
				return renderer;
			}
			XYLineAndShapeRenderer broken = (XYLineAndShapeRenderer) renderer;
			XYLineAndShapeRendererFix fixed = new XYLineAndShapeRendererFix(
					broken.getBaseLinesVisible(), broken.getBaseShapesVisible());

			// those are only fields set in ChartFactory#createTimeSeriesChart
			// you may need to set other fields if you use different
			// ChartFactory# method
			fixed.setBaseToolTipGenerator(renderer.getBaseToolTipGenerator());
			fixed.setURLGenerator(renderer.getURLGenerator());

			return fixed;
		}

		public XYLineAndShapeRendererFix(boolean lines, boolean shapes) {
			super(lines, shapes);
		}

		@Override
		protected void addEntity(EntityCollection entities, Shape area,
				XYDataset dataset, int series, int item, double entityX,
				double entityY) {
			if (!getItemCreateEntity(series, item)) {
				return;
			}
			Shape hotspot = area;
			if (hotspot == null) {
				double r = getDefaultEntityRadius();
				double w = r * 4;
				hotspot = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
			}

			String tip = null;
			XYToolTipGenerator generator = getToolTipGenerator(series, item);
			if (generator != null) {
				tip = generator.generateToolTip(dataset, series, item);
			}
			String url = null;
			if (getURLGenerator() != null) {
				url = getURLGenerator().generateURL(dataset, series, item);
			}
			XYItemEntity entity = new XYItemEntity(hotspot, dataset, series,
					item, tip, url);
			entities.add(entity);
		}
	}

}
