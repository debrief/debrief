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
package org.mwc.debrief.sensorfusion.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Vector;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.mwc.debrief.sensorfusion.views.DataSupport.SensorSeries;
import org.mwc.debrief.sensorfusion.views.DataSupport.TacticalSeries;

import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.JFreeChart.AttractiveDataItem;

public class FusionPlotRenderer extends XYLineAndShapeRenderer
{

	public static interface FusionHelper
	{
		/**
		 * find the selected items
		 * 
		 * @return
		 */
		public Vector<SensorSeries> getSelectedItems();

		/**
		 * should we use original colours?
		 * 
		 * @return
		 */
		public boolean useOriginalColors();
		
		public HashMap<SensorWrapper, SensorSeries> getIndex();
	}

	private final BasicStroke THICK_STROKE = new BasicStroke(4);
	int _seriesNum = -1;
	TacticalSeries _series = null;
	boolean _isSensor;
	private boolean _isSelected;
	private AttractiveDataItem _thisItem;
	final private FusionHelper _myHelper;
	private Paint _trackColor;

	public FusionPlotRenderer(final FusionHelper helper)
	{
		super();
		_myHelper = helper;
	}

	@Override
	public XYItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea,
			final XYPlot plot, final XYDataset data, final PlotRenderingInfo info)
	{
		// clear the counter, so we know we start counting again
		_seriesNum = -1;

		return super.initialise(g2, dataArea, plot, data, info);
	}

	@Override
	public Boolean getSeriesShapesVisible(final int series)
	{
		return  _series.getVisible();
	}

	/** note: this method has been generated to correct the mistaken JFreeChart
	 * implementation that breaks whtn in PlotOrientation.Horizontal.
	 */
	protected void addEntity(final EntityCollection entities, final Shape area,
			final XYDataset dataset, final int series, final int item, final double entityX,
			final double entityY) {
		if (!getItemCreateEntity(series, item)) {
			return;
		}
		Shape hotspot = area;
		if (hotspot == null) {
			final double r = getDefaultEntityRadius();
			final double w = r * 2;
			// NOTE: the JFreeChart problem is in the next line, it tries
			// to switch the X & Y when in horizontal mode, but the X and Y
			// values have already been transposed.
			hotspot = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
		}

		String tip = null;
		final XYToolTipGenerator generator = getToolTipGenerator(series, item);
		if (generator != null) {
			tip = generator.generateToolTip(dataset, series, item);
		}
		String url = null;
		if (getURLGenerator() != null) {
			url = getURLGenerator().generateURL(dataset, series, item);
		}
		final XYItemEntity entity = new XYItemEntity(hotspot, dataset, series,
				item, tip, url);
		entities.add(entity);
	}
	
	@Override
	public Paint getItemPaint(final int row, final int column)
	{
		if (_trackColor == null)
			_trackColor = super.getItemPaint(row, column);
		return _trackColor;
	}

	@Override
	public Stroke getItemStroke(final int row, final int column)
	{
		Stroke res;
		if (!_isSensor)
		{
			res = THICK_STROKE;
		}
		else
			res = super.getItemStroke(row, column);
		return res;
	}

	public boolean getItemLineVisible(final int series, final int item)
	{
		boolean res = true;
		res = _thisItem.connectToPrevious();

		return res;
	}

	@Override
	public void drawItem(final Graphics2D g2, final XYItemRendererState state,
			final Rectangle2D dataArea, final PlotRenderingInfo info, final XYPlot plot,
			final ValueAxis domainAxis, final ValueAxis rangeAxis, final XYDataset dataset, final int series,
			final int item, final CrosshairState crosshairState, final int pass)
	{
		// is this a new series?
		if (series != _seriesNum)
		{
			final TimeSeriesCollection tData = (TimeSeriesCollection) dataset;
			_seriesNum = series;
			_series = (TacticalSeries) tData.getSeries(series);
			_isSensor = (_series instanceof SensorSeries);
			_isSelected = _myHelper.getSelectedItems().contains(_series);

			// and sort out the color
			_trackColor = null;
			if (_isSelected)
				_trackColor = Color.black;
			else if (!_isSensor)
				_trackColor = _series.getColor();

			if (_trackColor == null)
				if (_myHelper.useOriginalColors())
					_trackColor = _series.getColor();
		}

		// store this new point
		_thisItem = (AttractiveDataItem) _series.getDataItem(item);

		// and let the parent do the plotting
		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis,
				dataset, series, item, crosshairState, pass);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
