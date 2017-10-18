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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.track_shift.views;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import org.jfree.chart.LegendItem;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;

import MWC.GUI.JFreeChart.AttractiveDataItem;
import MWC.GUI.JFreeChart.ColouredDataItem;

// ////////////////////////////////////////////////
// custom renderer, which uses the specified color for the data series
// ////////////////////////////////////////////////
public class ResidualXYItemRenderer extends DefaultXYItemRenderer
{

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
  private final TimeSeriesCollection _dataset;

  private boolean _lightweightMode = false;

  /**
   * Constructs a new renderer.
   * <p>
   * To specify the type of renderer, use one of the constants: SHAPES, LINES or SHAPES_AND_LINES.
   * 
   * @param type
   *          the type of renderer.
   * @param toolTipGenerator
   *          the tooltip generator.
   * @param urlGenerator
   *          the URL generator.
   * @param dataset
   * @param plot
   */
  public ResidualXYItemRenderer(final XYToolTipGenerator toolTipGenerator,
      final XYURLGenerator urlGenerator, TimeSeriesCollection dataset)
  {
    super();
    this.setBaseToolTipGenerator(toolTipGenerator);
    this.setURLGenerator(urlGenerator);
    _dataset = dataset;
  }

  public void setLightweightMode(boolean light)
  {
    _lightweightMode = light;
  }

  @Override
  public boolean getItemShapeVisible(int series, int item)
  {
    final boolean res;
    if (_lightweightMode)
    {
      res = false;
    }
    else
    {
      res = super.getItemShapeVisible(series, item);
    }
    return res;
  }

  @Override
  public boolean getItemShapeFilled(int row, int column)
  {
    boolean res = true;

    if (_lightweightMode)
    {
      res = false;
    }
    else
    {
      final TimeSeriesCollection tsc = (TimeSeriesCollection) _dataset;
      // get the data series
      final TimeSeries bts = tsc.getSeries(row);
      final TimeSeriesDataItem tsdp = bts.getDataItem(column);
      if (tsdp instanceof AttractiveDataItem)
      {
        final AttractiveDataItem item = (AttractiveDataItem) tsdp;
        if (item.connectToPrevious())
        {
          res = false;
        }
        else
        {
          res = true;
        }
      }
    }

    return res;
  }

  @Override
  public Paint getItemPaint(final int row, final int column)
  {
    final Paint theColor;
    
    final TimeSeriesCollection tsc = (TimeSeriesCollection) _dataset;
    // get the data series
    final TimeSeries bts = tsc.getSeries(row);
    final TimeSeriesDataItem tsdp = bts.getDataItem(column);
    if (tsdp instanceof AttractiveDataItem)
    {
      final AttractiveDataItem cdi = (AttractiveDataItem) tsdp;
      theColor = cdi.getColor();
    }
    else
    {
      theColor = super.getItemPaint(row, column);
    }

    return theColor;
  }

  /**
   * Returns a legend item for a series.
   * 
   * @param series
   *          the series (zero-based index).
   * 
   * @return a legend item for the series.
   */
  public LegendItem getLegendItem(final int series)
  {

    final XYPlot plot = this.getPlot();

    final XYDataset dataset = plot.getDataset();
    final String label = (String) dataset.getSeriesKey(series);
    final String description = label;
    final Shape shape = null;
    final Paint paint = this.getSeriesPaint(series);
    final Paint outlinePaint = paint;
    final Stroke stroke = plot.getRenderer().getSeriesStroke(series);

    return new LegendItem(label, description, null, null, shape, paint, stroke,
        outlinePaint);
  }

}
