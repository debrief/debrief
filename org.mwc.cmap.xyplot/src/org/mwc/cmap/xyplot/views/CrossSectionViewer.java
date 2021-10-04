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
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.swt.ChartComposite;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.mwc.cmap.xyplot.views.providers.ICrossSectionDatasetProvider;

import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldVector;

public class CrossSectionViewer {

	/**
	 * Memento parameters
	 */
	private static interface PLOT_ATTRIBUTES {
		final String TIME_PERIOD = "Time_Period";
		final String IS_SNAIL = "Is_Snail";
		final String ID = "Plot_Id";
	}

	public static class SnailRenderer extends XYLineAndShapeRenderer {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		private final ArrayList<ArrayList<Double>> rows = new ArrayList<ArrayList<Double>>();

		private boolean _doFade = true;

		@Override
		public Paint getItemFillPaint(final int row, final int column) {
			return getItemPaint(row, column);
		}

		@Override
		public Paint getItemPaint(final int row, final int column) {
			// ok, get the series color
			Paint seriesColor = super.getItemPaint(row, column);

			if (_doFade) {
				if (seriesColor instanceof Color) {
					final double proportion = getProportionFor(row, column);

					final Color thisC = (Color) seriesColor;
					final float[] parts = thisC.getColorComponents(new float[3]);
					seriesColor = new Color(parts[0], parts[1], parts[2], 1 - (float) proportion);
				}
			}

			return seriesColor;
		}

		public double getProportionFor(final int row, final int column) {
			final List<Double> thisRow = rows.get(row);
			final double proportion = thisRow.get(column);
			return proportion;
		}

		public void reset() {
			rows.clear();
		}

		public void setRowColProportion(final int row, final int column, final double proportion) {
			final ArrayList<Double> thisRow;

			if (rows.size() < row + 1) {
				thisRow = new ArrayList<Double>();
				rows.add(thisRow);
			} else {
				thisRow = rows.get(row);
			}

			thisRow.add(proportion);
		}

		public void setSnailFade(final boolean doFade) {
			_doFade = doFade;
		}

	}

	/**
	 * the chart marker
	 */
	private static final Shape _markerShape = new Rectangle2D.Double(-3, -3, 5, 5);

	/**
	 * Number of ticks to show before and after min and max axis values.
	 */
	private static final int TICK_OFFSET = 1;

	private final List<ISelectionChangedListener> _listeners = new ArrayList<ISelectionChangedListener>();

	/**
	 * the Swing control we insert the plot into
	 */
	private final ChartComposite _chartFrame;

	private final JFreeChart _chart;

	private ICrossSectionDatasetProvider _datasetProvider;

	private XYSeriesCollection _dataset = new XYSeriesCollection();

	private final SnailRenderer _snailRenderer = new SnailRenderer();

//	private static final int _markerSize = 4;

	private final XYLineAndShapeRenderer _discreteRenderer = new XYLineAndShapeRenderer();

	/**
	 * The current time we are looking at
	 */
	private HiResDate _currentTime = null;

	private long _timePeriod = 0;

	/**
	 * sometimes we need to rescale the axes, but we don't have any data yet. We use
	 * the pending flag to remember that one is due
	 */
	private boolean _resetPending = false;

	private String _plotId;

	protected CrossSectionViewer(final Composite parent) {

		_chartFrame = new ChartComposite(parent, SWT.NONE, null, 400, 600, 300, 200, 1800, 1800, true, false, true,
				true, true, true) {
			@Override
			public void mouseUp(final MouseEvent event) {
				super.mouseUp(event);
				final JFreeChart c = getChart();
				if (c != null) {
					c.setNotify(true); // force redraw
				}
			}
		};

		_chart = ChartFactory.createXYLineChart(null, // Title
				"Distance (km)", // X-Axis label
				"Elevation (m)", // Y-Axis label
				_dataset, // Dataset,
				PlotOrientation.VERTICAL, true, // Show legend,
				true, // tooltips
				true // urs
		);

		// get the XY plot
		final XYPlot plot = (XYPlot) _chart.getPlot();

		// do some basic plot formatting
		final BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
				new float[] { 8.0f, 2.0f }, 0);
		plot.setDomainGridlineStroke(stroke);
		plot.setRangeGridlineStroke(stroke);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.setBackgroundPaint(Color.white);

		// Fix the axes start at zero
		final ValueAxis yAxis = _chart.getXYPlot().getRangeAxis();
		yAxis.setLowerBound(0);
		final ValueAxis xAxis = _chart.getXYPlot().getDomainAxis();
		xAxis.setLowerBound(0);
		xAxis.setAutoRange(false);

		_chartFrame.setChart(_chart);
		_chartFrame.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
				clearPlot();
				_chartFrame.removeDisposeListener(this);
			}
		});
	}

	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		if (!_listeners.contains(listener))
			_listeners.add(listener);
	}

	public void clearPlot() {
		if (_chartFrame == null || _chartFrame.isDisposed()) {
			return;
		}
		_dataset.removeAllSeries();
		_chart.getXYPlot().setDataset(_dataset);
		_chart.getXYPlot().clearAnnotations();
	}

	public void fillPlot(final Layers theLayers, final LineShape line, final ICrossSectionDatasetProvider prov) {
		fillPlot(theLayers, line, prov, _timePeriod);
	}

	public void fillPlot(final Layers theLayers, final LineShape line, final ICrossSectionDatasetProvider prov,
			final long timePeriod) {
		// clear the chart
		_chart.setTitle("");

		// do some state checking
		if (line == null) {
			_chart.setTitle("Waiting for line");
		}

		if (theLayers != null && line != null) {
			_timePeriod = timePeriod;
			_datasetProvider = prov;
			_dataset.removeAllSeries();

			if (_currentTime != null) {
				if (isSnail()) {
					final HiResDate startDTG = new HiResDate(_currentTime.getDate().getTime() - _timePeriod);
					_dataset = _datasetProvider.getDataset(line, theLayers, startDTG, _currentTime, _snailRenderer);
					final Map<Integer, Color> colors = _datasetProvider.getSeriesColors();
					for (final Integer seriesKey : colors.keySet()) {
						setSnailRenderer(seriesKey, colors.get(seriesKey));
					}
					_chart.getXYPlot().setRenderer(_snailRenderer);
				} else {
					_dataset = _datasetProvider.getDataset(line, theLayers, _currentTime);
					final Map<Integer, Color> colors = _datasetProvider.getSeriesColors();
					for (final Integer seriesKey : colors.keySet()) {
						setDiscreteRenderer(seriesKey, colors.get(seriesKey));
					}
					_chart.getXYPlot().setRenderer(_discreteRenderer);
				}

			} else {
				_chart.setTitle("Waiting for time change");
			}

			_chart.getXYPlot().setDataset(_dataset);

			// are we waiting to reset?
			if (_resetPending) {
				_resetPending = false;
				resetBothAxes(line);
			} else {
				resetDepthAxis();
			}

		}
	}

	public Composite getControl() {
		return _chartFrame;
	}

	protected long getPeriod() {
		return _timePeriod;
	}

	protected String getPlotId() {
		return _plotId;
	}

	private double getXAxisLength(final LineShape line) {
		final WorldVector wv = line.getLine_Start().subtract(line.getLineEnd());
		return new WorldDistance(wv).getValueIn(WorldDistance.KM);
	}

	protected boolean isSnail() {
		return _timePeriod != 0;
	}

	public void newTime(final HiResDate newDTG) {
		_currentTime = newDTG;
	}

	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	void resetBothAxes(final LineShape line) {

		// hmm, if we don't have a time, we can't do a reaset
		if (_currentTime == null) {
			_resetPending = true;
		}

		resetDepthAxis();

		final ValueAxis xAxis = _chart.getXYPlot().getDomainAxis();
		xAxis.setUpperBound(getXAxisLength(line) + TICK_OFFSET);
		xAxis.setLowerBound(0);
	}

	void resetDepthAxis() {
		double maxY = 0;
		double minY = Integer.MAX_VALUE;

		for (int i = 0; i < _dataset.getSeriesCount(); i++) {
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

	public void restoreState(final IMemento memento) {
		_plotId = memento.getString(PLOT_ATTRIBUTES.ID);

		final Boolean is_snail = memento.getBoolean(PLOT_ATTRIBUTES.IS_SNAIL);
		if (is_snail) {
			_timePeriod = memento.getFloat(PLOT_ATTRIBUTES.TIME_PERIOD).longValue();
		} else {
			_timePeriod = 0;
		}

	}

	public void saveState(final IMemento memento) {
		memento.putString(PLOT_ATTRIBUTES.ID, _plotId);
		final boolean is_snail = isSnail();
		memento.putBoolean(PLOT_ATTRIBUTES.IS_SNAIL, is_snail);
		memento.putFloat(PLOT_ATTRIBUTES.TIME_PERIOD, _timePeriod);
	}

	private void setDiscreteRenderer(final int series, final Color paint) {
		_discreteRenderer.setSeriesShape(series, _markerShape);
		_discreteRenderer.setSeriesFillPaint(series, paint);
		_discreteRenderer.setSeriesPaint(series, paint);
		_discreteRenderer.setSeriesShapesVisible(series, true);
	}

	protected void setPlotId(final String _plotId) {
		this._plotId = _plotId;
	}

	public void setSnailFade(final boolean doFade) {
		_snailRenderer.setSnailFade(doFade);

		// and force repaint
		_chart.fireChartChanged();
	}

	private void setSnailRenderer(final int series, final Color paint) {
		_snailRenderer.setSeriesShape(series, _markerShape);

		_snailRenderer.setSeriesFillPaint(series, paint);
		_snailRenderer.setSeriesPaint(series, paint);

		_snailRenderer.setUseFillPaint(true);
		_snailRenderer.setSeriesShapesFilled(series, true);
		_snailRenderer.setSeriesShapesVisible(series, true);
		_snailRenderer.setSeriesStroke(series, new BasicStroke(3));
	}
}
