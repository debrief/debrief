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
package org.mwc.cmap.xyplot.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.mwc.cmap.xyplot.views.providers.ICrossSectionDatasetProvider;

import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldVector;

public class CrossSectionViewer
{
  
  public static class SnailRenderer extends XYLineAndShapeRenderer
  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private ArrayList<ArrayList<Double>> rows = new ArrayList<ArrayList<Double>>();
    
    public void reset()
    {
      rows.clear();
    }
    
    public void setRowColProportion(int row, int column, double proportion)
    {
      final ArrayList<Double> thisRow;
      
      if(rows.size() < row + 1)
      {
        thisRow = new ArrayList<Double>(); 
        rows.add(thisRow);
      }
      else
      {
        thisRow = rows.get(row);
      }
            
      thisRow.add(proportion);
    }
    
    

    @Override
    public Paint getItemFillPaint(int row, int column)
    {
      return getItemPaint(row, column);
    }

    @Override
    public Paint getItemPaint(int row, int column)
    {
      double proportion = getProportionFor(row, column);
      
      // ok, get the series color
      Paint seriesColor = super.getItemPaint(row, column);
      
      if(seriesColor instanceof Color)
      {
        Color thisC = (Color) seriesColor;
        float[] parts = thisC.getColorComponents(new float[3]);
        seriesColor = new Color(parts[0], parts[1], parts[2], 1-(float)proportion);
      }
      
      return seriesColor;
    }

    public double getProportionFor(int row, int column)
    {
      List<Double> thisRow = rows.get(row);
      double proportion = thisRow.get(column);
      return proportion;
    }
    
  }

	private List<ISelectionChangedListener> _listeners = new ArrayList<ISelectionChangedListener>();

	/**
	 * the Swing control we insert the plot into
	 */
	private ChartComposite _chartFrame;

	private JFreeChart _chart;

	private ICrossSectionDatasetProvider _datasetProvider;

	private XYSeriesCollection _dataset = new XYSeriesCollection();

	private SnailRenderer _snailRenderer = new SnailRenderer();

	private XYLineAndShapeRenderer _discreteRenderer = new XYLineAndShapeRenderer();

	/**
	 * The current time we are looking at
	 */
	private HiResDate _currentTime = null;

	/**
	 * the chart marker
	 */
	private static final Shape _markerShape = new Rectangle2D.Double(-3, -3, 5, 5);
	
//	private static final int _markerSize = 4;

	/**
	 * Number of ticks to show before and after min and max axis values.
	 */
	private static final int TICK_OFFSET = 1;

	private long _timePeriod = 0;
	
	/** sometimes we need to rescale the axes,
	 * but we don't have any data yet. We use the 
	 * pending flag to remember that one is due
	 */
	private boolean _resetPending = false;

	private String _plotId;

	/**
	 * Memento parameters
	 */
	private static interface PLOT_ATTRIBUTES
	{
		final String TIME_PERIOD = "Time_Period";
		final String IS_SNAIL = "Is_Snail";
		final String ID = "Plot_Id";
	}

	protected CrossSectionViewer(final Composite parent)
	{
	  
		_chartFrame = new ChartComposite(parent, SWT.NONE, null, 400, 600, 300,
        200, 1800, 1800, true, false, true, true, true, true)
		{
			@Override
			public void mouseUp(MouseEvent event)
			{
				super.mouseUp(event);
				JFreeChart c = getChart();
				if (c != null) 
				{
					c.setNotify(true); // force redraw
				}
			}
		};

		_chart = ChartFactory.createXYLineChart("Cross Section", // Title
				"Distance (km)", // X-Axis label
				"Elevation (m)", // Y-Axis label
				_dataset, // Dataset,
				PlotOrientation.VERTICAL, true, // Show legend,
				true, // tooltips
				true // urs
				);

		// Fix the axes start at zero
		final ValueAxis yAxis = _chart.getXYPlot().getRangeAxis();
		yAxis.setLowerBound(0);
		final ValueAxis xAxis = _chart.getXYPlot().getDomainAxis();
		xAxis.setLowerBound(0);
		xAxis.setAutoRange(false);

		_chartFrame.setChart(_chart);
		_chartFrame.addDisposeListener(new DisposeListener()
		{
			
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				clearPlot();
				_chartFrame.removeDisposeListener(this);
			}
		});
	}

	protected boolean isSnail()
	{
		return _timePeriod != 0;
	}

	public void newTime(final HiResDate newDTG)
	{
		_currentTime = newDTG;
	}

	public void clearPlot()
	{
		if (_chartFrame == null || _chartFrame.isDisposed()) {
			return;
		}
		_dataset.removeAllSeries();
		_chart.getXYPlot().setDataset(_dataset);
		_chart.getXYPlot().clearAnnotations();
	}

	public void fillPlot(final Layers theLayers, final LineShape line,
			final ICrossSectionDatasetProvider prov)
	{
		fillPlot(theLayers, line, prov, _timePeriod);
	}

	public void fillPlot(final Layers theLayers, final LineShape line,
			final ICrossSectionDatasetProvider prov, final long timePeriod)
	{
		if (theLayers != null && line != null)
		{
			_timePeriod = timePeriod;
			_datasetProvider = prov;
			_dataset.removeAllSeries();

			if (_currentTime != null)
			{
				if (isSnail())
				{
					final HiResDate startDTG = new HiResDate(_currentTime.getDate()
							.getTime() - _timePeriod);
					_dataset = _datasetProvider.getDataset(line, theLayers, startDTG,
							_currentTime, _snailRenderer);
					final Map<Integer, Color> colors = _datasetProvider.getSeriesColors();
					for (Integer seriesKey : colors.keySet())
					{
						setSnailRenderer(seriesKey, colors.get(seriesKey));
					}
					_chart.getXYPlot().setRenderer(_snailRenderer);
				}
				else
				{
					_dataset = _datasetProvider.getDataset(line, theLayers, _currentTime);
					final Map<Integer, Color> colors = _datasetProvider.getSeriesColors();
					for (Integer seriesKey : colors.keySet())
					{
						setDiscreteRenderer(seriesKey, colors.get(seriesKey));
					}
					_chart.getXYPlot().setRenderer(_discreteRenderer);
				}

			}

			_chart.getXYPlot().setDataset(_dataset);			
			
			// are we waiting to reset?
			if(_resetPending)
			{
			  _resetPending = false;
			  resetBothAxes(line);
			}
			else
			{
			  resetDepthAxis();
			}

		}
	}

  void resetBothAxes(final LineShape line)
  {
    
    // hmm, if we don't have a time, we can't do a reaset
    if(_currentTime == null)
    {
      _resetPending = true;
    }

    resetDepthAxis();
    
    final ValueAxis xAxis = _chart.getXYPlot().getDomainAxis();
    xAxis.setUpperBound(getXAxisLength(line) + TICK_OFFSET);
    xAxis.setLowerBound(0);
  }
  
  void resetDepthAxis()
  {
    double maxY = 0;
    double minY = Integer.MAX_VALUE;

    for (int i = 0; i < _dataset.getSeriesCount(); i++)
    {
      final XYSeries series = _dataset.getSeries(_dataset.getSeriesKey(i));
      final double y = series.getMaxY();
      if (maxY < y)
        maxY = y;
      final double mY = series.getMinY();
      if (minY > mY)
        minY = mY;
    }

    final ValueAxis yAxis = _chart.getXYPlot().getRangeAxis();
    yAxis.setUpperBound(maxY + TICK_OFFSET);
    yAxis.setLowerBound(minY - TICK_OFFSET);
    
  }

	private double getXAxisLength(LineShape line)
	{
		final WorldVector wv = line.getLine_Start().subtract(line.getLineEnd());
		return new WorldDistance(wv).getValueIn(WorldDistance.KM);
	}

	private void setDiscreteRenderer(final int series, final Color paint)
	{
		_discreteRenderer.setSeriesShape(series, null);
		_discreteRenderer.setSeriesFillPaint(series, Color.green);
		_discreteRenderer.setSeriesPaint(series, Color.yellow);
		_discreteRenderer.setSeriesShapesVisible(series, false);
	}

	private void setSnailRenderer(final int series, final Color paint)
	{
		_snailRenderer.setSeriesShape(series, _markerShape);

		_snailRenderer.setSeriesFillPaint(series, paint);
		_snailRenderer.setSeriesPaint(series, paint);

		_snailRenderer.setUseFillPaint(true);
		_snailRenderer.setSeriesShapesFilled(series, true);
		_snailRenderer.setSeriesShapesVisible(series, true);
		_snailRenderer.setSeriesStroke(series, new BasicStroke(3));
	}

	public void addSelectionChangedListener(
			final ISelectionChangedListener listener)
	{
		if (!_listeners.contains(listener))
			_listeners.add(listener);
	}

	public void removeSelectionChangedListener(
			final ISelectionChangedListener listener)
	{
		_listeners.remove(listener);
	}

	public void saveState(final IMemento memento)
	{
		memento.putString(PLOT_ATTRIBUTES.ID, _plotId);
		final boolean is_snail = isSnail();
		memento.putBoolean(PLOT_ATTRIBUTES.IS_SNAIL, is_snail);
		memento.putFloat(PLOT_ATTRIBUTES.TIME_PERIOD, (float) _timePeriod);
	}

	public void restoreState(final IMemento memento)
	{
		_plotId = memento.getString(PLOT_ATTRIBUTES.ID);

		final Boolean is_snail = memento.getBoolean(PLOT_ATTRIBUTES.IS_SNAIL);
		if (is_snail)
		{
			_timePeriod = memento.getFloat(PLOT_ATTRIBUTES.TIME_PERIOD).longValue();
		}
		else
		{
			_timePeriod = 0;
		}

	}

	protected long getPeriod()
	{
		return _timePeriod;
	}

	protected String getPlotId()
	{
		return _plotId;
	}

	protected void setPlotId(final String _plotId)
	{
		this._plotId = _plotId;
	}
}
