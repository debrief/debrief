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

package MWC.GUI.JFreeChart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: Feb 5, 2003 Time: 10:59:40 AM
 * To change this template use Options | File Templates.
 */
// ////////////////////////////////////////////////
// custom renderer, which uses the specified color for the data series
// ////////////////////////////////////////////////
public class ColourStandardXYItemRenderer extends DefaultXYItemRenderer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/** A working line (to save creating thousands of instances). */
	// private Line2D workingLine = new Line2D.Double(0.0, 0.0, 0.0, 0.0);

	/**
	 * the plot whose data we're plotting
	 *
	 */
	private XYPlot _myPlot;

	private double _symbolSize = 6;

	/**
	 * Constructs a new renderer.
	 * <p>
	 * To specify the type of renderer, use one of the constants: SHAPES, LINES or
	 * SHAPES_AND_LINES.
	 *
	 * @param toolTipGenerator the tooltip generator.
	 * @param urlGenerator     the URL generator.
	 * @param plot             the plot to repaint
	 */
	public ColourStandardXYItemRenderer(final XYToolTipGenerator toolTipGenerator, final XYURLGenerator urlGenerator,
			final XYPlot plot) {
		super();
		this.setDefaultToolTipGenerator(toolTipGenerator);
		this.setURLGenerator(urlGenerator);
		_myPlot = plot;
	}

	/**
	 * accessor method to find out if we should connect this point to the previous
	 * one
	 *
	 * @param plot   the plot (can be used to obtain standard color information
	 *               etc).
	 * @param series the series index.
	 * @param item   the item index.
	 * @return yes/no
	 */
	protected boolean connectToPrevious(final XYPlot plot, final int series, final int item) {

		final XYDataset data = plot.getDataset();

		boolean res = true;

		if (data instanceof TimeSeriesCollection) {
			final TimeSeriesCollection tsc = (TimeSeriesCollection) data;
			// get the data series
			TimeSeries bts;
			try {
				bts = tsc.getSeries(series);

				TimeSeriesDataItem tsdp;
				tsdp = bts.getDataItem(item);
				if (tsdp instanceof AttractiveDataItem) {
					final AttractiveDataItem cdi = (AttractiveDataItem) tsdp;
					res = cdi.connectToPrevious();
				}
			} catch (final IndexOutOfBoundsException ee) {
				// ee.printStackTrace();
				res = false;
			} catch (final IllegalArgumentException ee) {
				// ee.printStackTrace();
				res = false;
			}
		}

		return res;
	}

	/**
	 * Draws the visual representation of a single data item.
	 *
	 * @param g2             the graphics device.
	 * @param state          the renderer state.
	 * @param dataArea       the area within which the data is being drawn.
	 * @param info           collects information about the drawing.
	 * @param plot           the plot (can be used to obtain standard color
	 *                       information etc).
	 * @param domainAxis     the domain axis.
	 * @param rangeAxis      the range axis.
	 * @param dataset        the dataset.
	 * @param series         the series index (zero-based).
	 * @param item           the item index (zero-based).
	 * @param crosshairState crosshair information for the plot (<code>null</code>
	 *                       permitted).
	 * @param pass           the pass index.
	 */
	@Override
	public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea,
			final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis,
			final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState,
			final int pass) {

		// do nothing if item is not visible
		if (!getItemVisible(series, item)) {
			return;
		}

		final boolean connectToPrev = connectToPrevious(_myPlot, series, item);

		// first pass draws the background (lines, for instance)
		if (isLinePass(pass)) {
			if (connectToPrev) {
				if (this.getDrawSeriesLineAsPath()) {
					drawPrimaryLineAsPath(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis,
							dataArea);
				} else {
					drawPrimaryLine(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
				}
			}
		}
		// second pass adds shapes where the items are ..
		else if (isItemPass(pass)) {

			// setup for collecting optional entity info...
			EntityCollection entities = null;
			if (info != null) {
				entities = info.getOwner().getEntityCollection();
			}

			drawSecondaryPass(g2, plot, dataset, pass, series, item, domainAxis, dataArea, rangeAxis, crosshairState,
					entities);
		}
	}

	@Override
	public Paint getItemPaint(final int row, final int column) {
		Color theColor = null;

		final XYDataset data = _myPlot.getDataset();

		Paint res = null;

		if (data instanceof TimeSeriesCollection) {
			final TimeSeriesCollection tsc = (TimeSeriesCollection) data;
			// get the data series
			final TimeSeries bts = tsc.getSeries(row);
			final TimeSeriesDataItem tsdp = bts.getDataItem(column);
			if (tsdp instanceof AttractiveDataItem) {
				final AttractiveDataItem cdi = (AttractiveDataItem) tsdp;
				theColor = cdi.getColor();
			}
		}

		if (theColor != null) {
			res = theColor;
		} else {
			res = super.getItemPaint(row, column);
		}

		return res;
	}

	@Override
	public boolean getItemShapeFilled(final int row, final int column) {
		final XYDataset data = _myPlot.getDataset();

		final boolean defaultRes = super.getItemShapeVisible(row, column);
		final boolean res;

		if (data instanceof TimeSeriesCollection) {
			final TimeSeriesCollection tsc = (TimeSeriesCollection) data;
			// get the data series
			final TimeSeries bts = tsc.getSeries(row);

			if (bts.getItemCount() > 0) {
				final TimeSeriesDataItem tsdp = bts.getDataItem(column);
				if (tsdp instanceof ColouredDataItem) {
					final ColouredDataItem cdi = (ColouredDataItem) tsdp;
					// is the base renderer set to show all
					if (this.getDefaultShapesVisible() && cdi.isShapeFilled()) {
						res = true;
					} else {
						res = false;
					}
				} else {
					res = defaultRes;
				}
			} else {
				res = defaultRes;
			}
		} else {
			res = defaultRes;
		}

		return res;
	}

	@Override
	public boolean getItemShapeVisible(final int row, final int column) {
		final XYDataset data = _myPlot.getDataset();

		final boolean defaultRes = super.getItemShapeVisible(row, column);
		final boolean res;

		if (data instanceof TimeSeriesCollection) {
			final TimeSeriesCollection tsc = (TimeSeriesCollection) data;
			// get the data series
			final TimeSeries bts = tsc.getSeries(row);

			// check it has some data
			if (bts.getItemCount() > 0) {
				final TimeSeriesDataItem tsdp = bts.getDataItem(column);
				if (tsdp instanceof ColouredDataItem) {
					final ColouredDataItem cdi = (ColouredDataItem) tsdp;
					// is the base renderer set to show all
					if (this.getDefaultShapesVisible() && cdi.isParentSymVisible()) {
						res = true;
					} else {
						res = false;
					}
				} else {
					res = defaultRes;
				}
			} else {
				res = defaultRes;
			}
		} else {
			res = defaultRes;
		}

		return res;
	}

	/**
	 * Returns a legend item for a series.
	 *
	 * @param series the series (zero-based index).
	 *
	 * @return a legend item for the series.
	 */
	public LegendItem getLegendItem(final int series) {

		final XYPlot plot = this.getPlot();

		final XYDataset dataset = plot.getDataset();
		final String label = (String) dataset.getSeriesKey(series);
		final String description = label;
		final Shape shape = null;
		final Paint paint = this.getSeriesPaint(series);
		final Paint outlinePaint = paint;
		final Stroke stroke = plot.getRenderer().getSeriesStroke(series);

		return new LegendItem(label, description, null, null, shape, paint, stroke, outlinePaint);
	}

	@Override
	public Shape getSeriesShape(final int series) {
		final Shape theShape = super.getSeriesShape(series);
		final double defaultScale = 6;

		final Shape newShape;
		if (theShape instanceof Rectangle2D) {
			final Rectangle2D rect = (Rectangle2D) theShape;
			final double ht = rect.getHeight() / defaultScale * _symbolSize;
			final double wid = rect.getWidth() / defaultScale * _symbolSize;
			newShape = new Rectangle2D.Double(-wid / 2, -ht / 2, wid, ht);
		} else if (theShape instanceof Ellipse2D) {
			final Ellipse2D ell = (Ellipse2D) theShape;
			final double ht = ell.getHeight() / defaultScale * _symbolSize;
			final double wid = ell.getWidth() / defaultScale * _symbolSize;
			newShape = new Ellipse2D.Double(-wid / 2, -ht / 2, wid, ht);
		} else if (theShape instanceof Polygon) {
			final Polygon helloPoly = (Polygon) theShape;

			// retrieve the points
			final int[] xp = helloPoly.xpoints;
			final int[] yp = helloPoly.ypoints;
			final int np = helloPoly.npoints;

			// create a new array, to store the data
			final int[] newX = new int[np];
			final int[] newY = new int[np];

			for (int i = 0; i < np; i++) {
				newX[i] = (int) ((xp[i]) / defaultScale * _symbolSize);
				newY[i] = (int) ((yp[i]) / defaultScale * _symbolSize);
			}

			newShape = new Polygon(newX, newY, np);
		} else {
			newShape = theShape;
		}

		// ok, scale this shape;
		return newShape;
	}

	public double getSymbolSize() {
		return _symbolSize;
	}

	@Override
	public void setPlot(final XYPlot thePlot) {
		super.setPlot(thePlot);
		_myPlot = thePlot;
	}

	public void setSymbolSize(final double size) {
		_symbolSize = size;
	}

	/**
	 * Draws the visual representation of a single data item.
	 *
	 * @param g2            the graphics device.
	 * @param dataArea      the area within which the data is being drawn.
	 * @param info          collects information about the drawing.
	 * @param plot          the plot (can be used to obtain standard color
	 *                      information etc).
	 * @param domainAxis    the domain (horizontal) axis.
	 * @param rangeAxis     the range (vertical) axis.
	 * @param data          the dataset.
	 * @param series        the series index.
	 * @param item          the item index.
	 * @param crosshairInfo information about crosshairs on a plot.
	 */
	// public void drawItem(Graphics2D g2,
	// Rectangle2D dataArea,
	// ChartRenderingInfo info,
	// XYPlot plot,
	// ValueAxis domainAxis,
	// ValueAxis rangeAxis,
	// XYDataset data,
	// int series,
	// int item,
	// CrosshairInfo crosshairInfo) {
	//
	// // setup for collecting optional entity info...
	// Shape entityArea = null;
	// EntityCollection entities = null;
	// if (info != null) {
	// entities = info.getEntityCollection();
	// }
	//
	// Paint seriesPaint = plot.getRenderer().getSeriesPaint(series);
	// Stroke seriesStroke = plot.getRenderer().getSeriesStroke(series);
	// g2.setPaint(seriesPaint);
	// g2.setStroke(seriesStroke);
	//
	// // get the data point...
	// Number x1n = data.getXValue(series, item);
	// Number y1n = data.getYValue(series, item);
	// if (y1n != null) {
	// double x1 = x1n.doubleValue();
	// double y1 = y1n.doubleValue();
	// double transX1 = domainAxis.valueToJava2D(x1, dataArea);
	// double transY1 = rangeAxis.valueToJava2D(y1, dataArea);
	//
	// Paint paint = getPaint(plot, series, item, transX1, transY1);
	// if (paint != null) {
	// g2.setPaint(paint);
	// }
	//
	// if (true) {
	// if (item > 0) {
	//
	// // find out if we're going to connect this line
	// boolean connectToPrevious = connectToPrevious(plot, series, item, transX1,
	// transY1);
	//
	// if(connectToPrevious)
	// {
	// // get the previous data point...
	// Number x0n = data.getXValue(series, item - 1);
	// Number y0n = data.getYValue(series, item - 1);
	// if (y0n != null) {
	// double x0 = x0n.doubleValue();
	// double y0 = y0n.doubleValue();
	//
	// double transX0 = domainAxis.translateValueToJava2D(x0, dataArea);
	// double transY0 = rangeAxis.translateValueToJava2D(y0, dataArea);
	//
	// workingLine.setLine(transX0, transY0, transX1, transY1);
	// if (workingLine.intersects(dataArea)) {
	// g2.draw(workingLine);
	// }
	// }
	// }
	// }
	// }
	//
	// // if (this.plotShapes) {
	// if(this.getPlotShapes())
	// {
	// double scale = getShapeScale(plot, series, item, transX1, transY1);
	// Shape shape = getShape(plot, series, item, transX1, transY1, scale);
	// if (shape.intersects(dataArea)) {
	// if (isShapeFilled(plot, series, item, transX1, transY1)) {
	// g2.fill(shape);
	// }
	// else {
	// g2.draw(shape);
	// }
	// }
	// entityArea = shape;
	//
	// }
	//
	// if (false) {
	// // if (this.plotImages) {
	// // use shape scale with transform??
	// double scale = getShapeScale(plot, series, item, transX1, transY1);
	// Image image = this.getImage(plot, series, item, transX1, transY1);
	// if (image != null) {
	// Point hotspot = getImageHotspot(plot, series, item, transX1, transY1,
	// image);
	// g2.drawImage(image,
	// (int) (transX1 - hotspot.getX()),
	// (int) (transY1 - hotspot.getY()), (ImageObserver) null);
	// }
	// // tooltipArea = image; not sure how to handle this yet
	// }
	//
	// // add an entity for the item...
	// if (entities != null) {
	// if (entityArea == null) {
	// entityArea = new Rectangle2D.Double(transX1 - 2, transY1 - 2, 4, 4);
	// }
	// String tip = "";
	// if (getToolTipGenerator() != null) {
	// tip = getToolTipGenerator().generateToolTip(data, series, item);
	// }
	// String url = null;
	// if (getURLGenerator() != null) {
	// url = getURLGenerator().generateURL(data, series, item);
	// }
	// XYItemEntity entity = new XYItemEntity(entityArea, tip, url, series, item);
	// entities.addEntity(entity);
	// }
	//
	// // do we need to update the crosshair values?
	// if (domainAxis.isCrosshairLockedOnData()) {
	// if (rangeAxis.isCrosshairLockedOnData()) {
	// // both axes
	// crosshairInfo.updateCrosshairPoint(x1, y1);
	// }
	// else {
	// // just the horizontal axis...
	// crosshairInfo.updateCrosshairX(x1);
	// }
	// }
	// else {
	// if (rangeAxis.isCrosshairLockedOnData()) {
	// // just the vertical axis...
	// crosshairInfo.updateCrosshairY(y1);
	// }
	// }
	// }
	// }

}
