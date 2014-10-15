/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

	private final Stroke myFeedbackStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] { 5.0f, 5.0f }, 0.0f);

	public void setFeedbackEdgePaint(final Paint feedbackEdgePaint) {
		myFeedbackEdgePaint = feedbackEdgePaint;
	}

	public void setFeedbackNodePaint(final Paint feedbackNodePaint) {
		myFeedbackNodePaint = feedbackNodePaint;
	}

	public void setFeedbackSubject(final int row, final int column) {
		myFeedBackRowAndColumn = new Point(row, column);
	}

	public void setFeedbackSubject(final XYItemEntity entity) {
		if (entity == null) {
			myFeedBackRowAndColumn = null;
		} else {
			setFeedbackSubject(entity.getSeriesIndex(), entity.getItem());
		}
	}

	public void setFeedBackValue(final Point2D.Double feedBackValue) {
		myFeedBackValue = feedBackValue;
	}

	@Override
	public void drawItem(final Graphics2D g2, //
			final XYItemRendererState state, //
			final Rectangle2D dataArea, //
			final PlotRenderingInfo info, //
			final XYPlot plot, //
			final ValueAxis domainAxis, //
			final ValueAxis rangeAxis, //
			final XYDataset dataset, //
			final int series,//
			final int item, //
			final CrosshairState crosshairState, //
			final int pass) {

		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);

		if (hasFeedbackFor(series, item)) {
			drawItemFeedback(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
		}
	}

	private boolean hasFeedbackFor(final int series, final int item) {
		return myFeedBackRowAndColumn != null && myFeedBackValue != null && myFeedBackRowAndColumn.x == series && myFeedBackRowAndColumn.y == item;
	}

	/**
	 * We are extensively using implementation of the super.drawItem() here.
	 */
	protected void drawItemFeedback(final Graphics2D g2, //
			final XYItemRendererState state, //
			final Rectangle2D dataArea, //
			final PlotRenderingInfo info, //
			final XYPlot plot, //
			final ValueAxis domainAxis, //
			final ValueAxis rangeAxis, //
			final XYDataset dataset, //
			final int series,//
			final int item, //
			final CrosshairState crosshairState, //
			final int pass) {

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
	private void drawFeedBackNode(final Graphics2D g2, final XYPlot plot, final XYDataset dataset, final int pass, //
			final int series, final int item, final ValueAxis domainAxis, final Rectangle2D dataArea, final ValueAxis rangeAxis, //
			final CrosshairState crosshairState, final EntityCollection entities) {

		// get the data point...
		final double x1 = myFeedBackValue != null ? myFeedBackValue.x : dataset.getXValue(series, item);
		final double y1 = myFeedBackValue != null ? myFeedBackValue.y : dataset.getYValue(series, item);
		if (Double.isNaN(y1) || Double.isNaN(x1)) {
			return;
		}

		final PlotOrientation orientation = plot.getOrientation();
		final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
		final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
		final double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
		final double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

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

	private void drawFeedbackItemLabel(final Graphics2D g2, final PlotOrientation orientation, final XYDataset dataset, final int series, final int item, final double xx, final double yy, final boolean b) {
		// no labels for now
	}

	/**
	 * @see XYLineAndShapeRenderer#drawPrimaryLine
	 */
	protected void drawFeedBackPrimaryLine(final XYItemRendererState state, final Graphics2D g2, final XYPlot plot, final XYDataset dataset, final int pass, //
			final int series, final int item, final ValueAxis domainAxis, final ValueAxis rangeAxis, final Rectangle2D dataArea) {

		if (item != 0) {
			final double x1 = myFeedBackValue != null ? myFeedBackValue.x : dataset.getXValue(series, item);
			final double y1 = myFeedBackValue != null ? myFeedBackValue.y : dataset.getYValue(series, item);
			final double x0 = dataset.getXValue(series, item - 1);
			final double y0 = dataset.getYValue(series, item - 1);
			drawFeedbackEdge(x0, y0, x1, y1, state, g2, plot, domainAxis, rangeAxis, dataArea);
		}

		if (item < dataset.getItemCount(series) - 1) {
			final double x0 = myFeedBackValue != null ? myFeedBackValue.x : dataset.getXValue(series, item);
			final double y0 = myFeedBackValue != null ? myFeedBackValue.y : dataset.getYValue(series, item);
			final double x1 = dataset.getXValue(series, item + 1);
			final double y1 = dataset.getYValue(series, item + 1);
			drawFeedbackEdge(x0, y0, x1, y1, state, g2, plot, domainAxis, rangeAxis, dataArea);
		}
	}

	/**
	 * All parameters are domain coordinates that have to be translated to
	 * Java2D points.
	 */
	private void drawFeedbackEdge(final double x0, final double y0, final double x1, final double y1, final XYItemRendererState state, final Graphics2D g2, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final Rectangle2D dataArea) {
		if (Double.isNaN(y0) || Double.isNaN(x0) || Double.isNaN(y1) || Double.isNaN(x1)) {
			return;
		}

		final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
		final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

		final double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
		final double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);

		final double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
		final double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

		// only draw if we have good values
		if (Double.isNaN(transX0) || Double.isNaN(transY0) || Double.isNaN(transX1) || Double.isNaN(transY1)) {
			return;
		}

		final PlotOrientation orientation = plot.getOrientation();
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
