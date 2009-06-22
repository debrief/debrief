package org.mwc.cmap.grideditor.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ShapeUtilities;

/**
 * Extends {@link LineAndShapeRenderer} with ability to render additional
 * move-feedback for some data item.
 */
public class RendererWithDynamicFeedback extends XYLineAndShapeRenderer {

	private static final long serialVersionUID = 1L;

	private Point myFeedBackRowAndColumn;

	private Point2D.Double myFeedBackValue;

	private Paint myFeedbackEdgePaint = Color.gray;

	private Paint myFeedbackNodePaint = Color.darkGray;

	private Stroke myFeedbackStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] { 5.0f, 5.0f }, 0.0f);

	public void setFeedbackEdgePaint(Paint feedbackEdgePaint) {
		myFeedbackEdgePaint = feedbackEdgePaint;
	}

	public void setFeedbackNodePaint(Paint feedbackNodePaint) {
		myFeedbackNodePaint = feedbackNodePaint;
	}

	public void setFeedbackSubject(int row, int column) {
		myFeedBackRowAndColumn = new Point(row, column);
	}

	public void setFeedbackSubject(XYItemEntity entity) {
		if (entity == null) {
			myFeedBackRowAndColumn = null;
		} else {
			setFeedbackSubject(entity.getSeriesIndex(), entity.getItem());
		}
	}

	public void setFeedBackValue(Point2D.Double feedBackValue) {
		myFeedBackValue = feedBackValue;
	}

	@Override
	public void drawItem(Graphics2D g2, //
			XYItemRendererState state, //
			Rectangle2D dataArea, //
			PlotRenderingInfo info, //
			XYPlot plot, //
			ValueAxis domainAxis, //
			ValueAxis rangeAxis, //
			XYDataset dataset, //
			int series,//
			int item, //
			CrosshairState crosshairState, //
			int pass) {

		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);

		if (hasFeedbackFor(series, item)) {
			drawItemFeedback(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
		}
	}

	private boolean hasFeedbackFor(int series, int item) {
		return myFeedBackRowAndColumn != null && myFeedBackValue != null && myFeedBackRowAndColumn.x == series && myFeedBackRowAndColumn.y == item;
	}

	/**
	 * We are extensively using implementation of the super.drawItem() here.
	 */
	protected void drawItemFeedback(Graphics2D g2, //
			XYItemRendererState state, //
			Rectangle2D dataArea, //
			PlotRenderingInfo info, //
			XYPlot plot, //
			ValueAxis domainAxis, //
			ValueAxis rangeAxis, //
			XYDataset dataset, //
			int series,//
			int item, //
			CrosshairState crosshairState, //
			int pass) {

		// do nothing if item is not visible
		if (!getItemVisible(series, item)) {
			return;
		}

		// first pass draws the background (lines, for instance)
		if (isLinePass(pass)) {
			if (getItemLineVisible(series, item)) {
				drawFeedBackPrimaryLine(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
			}
		} else if (isItemPass(pass)) {
			// second pass adds shapes where the items are ..
			// setup for collecting optional entity info...
			EntityCollection entities = null;
			if (info != null) {
				entities = info.getOwner().getEntityCollection();
			}

			drawFeedBackNode(g2, plot, dataset, pass, series, item, domainAxis, dataArea, rangeAxis, crosshairState, entities);
		}
	}

	/**
	 * @see drawSecondaryPass
	 */
	private void drawFeedBackNode(Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, //
			int series, int item, ValueAxis domainAxis, Rectangle2D dataArea, ValueAxis rangeAxis, //
			CrosshairState crosshairState, EntityCollection entities) {

		// get the data point...
		double x1 = myFeedBackValue != null ? myFeedBackValue.x : dataset.getXValue(series, item);
		double y1 = myFeedBackValue != null ? myFeedBackValue.y : dataset.getYValue(series, item);
		if (Double.isNaN(y1) || Double.isNaN(x1)) {
			return;
		}

		PlotOrientation orientation = plot.getOrientation();
		RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
		RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
		double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
		double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

		if (getItemShapeVisible(series, item)) {
			Shape shape = getItemShape(series, item);
			if (orientation == PlotOrientation.HORIZONTAL) {
				shape = ShapeUtilities.createTranslatedShape(shape, transY1, transX1);
			} else if (orientation == PlotOrientation.VERTICAL) {
				shape = ShapeUtilities.createTranslatedShape(shape, transX1, transY1);
			}
			if (shape.intersects(dataArea)) {
				g2.setPaint(getFeedbackNodePaint());
				g2.fill(shape);
			}
		}

		double xx = transX1;
		double yy = transY1;
		if (orientation == PlotOrientation.HORIZONTAL) {
			xx = transY1;
			yy = transX1;
		}
		drawFeedbackItemLabel(g2, orientation, dataset, series, item, xx, yy, (y1 < 0.0));
	}

	private void drawFeedbackItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double xx, double yy, boolean b) {
		// no labels for now
	}

	/**
	 * @see XYLineAndShapeRenderer#drawPrimaryLine
	 */
	protected void drawFeedBackPrimaryLine(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, //
			int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {

		if (item != 0) {
			double x1 = myFeedBackValue != null ? myFeedBackValue.x : dataset.getXValue(series, item);
			double y1 = myFeedBackValue != null ? myFeedBackValue.y : dataset.getYValue(series, item);
			double x0 = dataset.getXValue(series, item - 1);
			double y0 = dataset.getYValue(series, item - 1);
			drawFeedbackEdge(x0, y0, x1, y1, state, g2, plot, domainAxis, rangeAxis, dataArea);
		}

		if (item < dataset.getItemCount(series) - 1) {
			double x0 = myFeedBackValue != null ? myFeedBackValue.x : dataset.getXValue(series, item);
			double y0 = myFeedBackValue != null ? myFeedBackValue.y : dataset.getYValue(series, item);
			double x1 = dataset.getXValue(series, item + 1);
			double y1 = dataset.getYValue(series, item + 1);
			drawFeedbackEdge(x0, y0, x1, y1, state, g2, plot, domainAxis, rangeAxis, dataArea);
		}
	}

	/**
	 * All parameters are domain coordinates that have to be translated to
	 * Java2D points.
	 */
	private void drawFeedbackEdge(double x0, double y0, double x1, double y1, XYItemRendererState state, Graphics2D g2, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
		if (Double.isNaN(y0) || Double.isNaN(x0) || Double.isNaN(y1) || Double.isNaN(x1)) {
			return;
		}

		RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
		RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

		double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
		double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);

		double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
		double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

		// only draw if we have good values
		if (Double.isNaN(transX0) || Double.isNaN(transY0) || Double.isNaN(transX1) || Double.isNaN(transY1)) {
			return;
		}

		PlotOrientation orientation = plot.getOrientation();
		if (orientation == PlotOrientation.HORIZONTAL) {
			state.workingLine.setLine(transY0, transX0, transY1, transX1);
		} else if (orientation == PlotOrientation.VERTICAL) {
			state.workingLine.setLine(transX0, transY0, transX1, transY1);
		}

		if (state.workingLine.intersects(dataArea)) {
			g2.setStroke(getFeedbackStroke());
			g2.setPaint(getFeedbackEdgePaint());
			g2.draw(state.workingLine);
		}

	}

	private Stroke getFeedbackStroke() {
		return myFeedbackStroke;
	}

	private Paint getFeedbackEdgePaint() {
		return myFeedbackEdgePaint;
	}

	private Paint getFeedbackNodePaint() {
		return myFeedbackNodePaint;
	}

}
