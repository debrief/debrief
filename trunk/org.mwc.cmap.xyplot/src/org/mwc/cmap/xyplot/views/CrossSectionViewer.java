package org.mwc.cmap.xyplot.views;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.mwc.cmap.xyplot.views.providers.ICrossSectionDatasetProvider;

import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;


public class CrossSectionViewer
{
		
	private List<ISelectionChangedListener> _listeners = new ArrayList<ISelectionChangedListener>();
	private List<PropertyChangeListener> _propListeners = new ArrayList<PropertyChangeListener>();
	
	/**
	 * the Swing control we insert the plot into
	 */
	private Frame _chartFrame;
		
	private JFreeChart _chart;
	
	private ICrossSectionDatasetProvider _datasetProvider;
	
	private XYSeriesCollection _dataset = new XYSeriesCollection();
	
	private XYLineAndShapeRenderer _snailRenderer = new XYLineAndShapeRenderer();
	
	private XYDotRenderer _discreteRenderer = new XYDotRenderer();
	
	/**
	 * The current time we are looking at
	 */
	private HiResDate _currentTime = null;
	
	/**
	 * the chart marker
	 */
	private static final Shape _markerShape = new Rectangle2D.Double(-2, -2, 2, 2);
	private static final int _markerSize = 4;
	
	private long _timePeriod = 0;
	
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
	    //we need an SWT.EMBEDDED object to act as a holder
		final Composite holder = new Composite(parent, SWT.EMBEDDED);
		holder.setLayoutData(new GridData(GridData.FILL_VERTICAL
						| GridData.FILL_HORIZONTAL));

		// now we need a Swing object to put our chart into
		_chartFrame = SWT_AWT.new_Frame(holder);

		_chart = ChartFactory.createXYLineChart
				("Cross Section", // Title
				"Distance (km)", // X-Axis label
				"Elevation (m)", // Y-Axis label
				_dataset, // Dataset,
				PlotOrientation.VERTICAL,
				true, // Show legend,
				true, //tooltips
				true //urs
				);
        
		// Fix the axises start at zero
		final ValueAxis yAxis = _chart.getXYPlot().getRangeAxis(); 
		yAxis.setLowerBound(0);
		final ValueAxis xAxis = _chart.getXYPlot().getDomainAxis();
		xAxis.setLowerBound(0);
		
		final ChartPanel jfreeChartPanel = new ChartPanel(_chart);
		_chartFrame.add(jfreeChartPanel);		
	}
	
	protected boolean isSnail()
	{
		return _timePeriod != 0;
	}
	
	public void newTime(final HiResDate newDTG)
	{
		_currentTime = newDTG;
	}
	
	public void fillPlot(final Layers theLayers, final LineShape line,
			final ICrossSectionDatasetProvider prov)
	{
		fillPlot(theLayers, line, prov, _timePeriod);
	}
	
	public void fillPlot(final Layers theLayers, final LineShape line,
			final ICrossSectionDatasetProvider prov,
			final long timePeriod)
	{
		_timePeriod = timePeriod;
		if (theLayers == null || line == null)
			return;
		_datasetProvider = prov;		
		_dataset.removeAllSeries();
	
		if (_currentTime != null)
		{
			if(isSnail())
			{
				final HiResDate startDTG = new HiResDate(_currentTime
						.getDate().getTime() - _timePeriod);	
				_dataset = _datasetProvider.getDataset(line, theLayers, startDTG, _currentTime);
				final Map<Integer, Color> colors = _datasetProvider.getSeriesColors();
				for(Integer seriesKey: colors.keySet())
				{
					setSnailRenderer(seriesKey, colors.get(seriesKey));
				}
				_chart.getXYPlot().setRenderer(_snailRenderer);
			}
			else
			{
				_dataset = _datasetProvider.getDataset(line, theLayers, _currentTime);
				final Map<Integer, Color> colors = _datasetProvider.getSeriesColors();
				for(Integer seriesKey: colors.keySet())
				{
					setDiscreteRenderer(seriesKey, colors.get(seriesKey));
				}
				_chart.getXYPlot().setRenderer(_discreteRenderer);
			}
			
		}
		
		double maxX = 0;
		double maxY = 0;
		
		for (int i = 0; i < _dataset.getSeriesCount(); i++) 
		{
			final XYSeries series = _dataset.getSeries(_dataset.getSeriesKey(i));
			final double x = series.getMaxX(); 
			if (maxX < x)
				maxX = x;
			final double y = series.getMaxY(); 
			if (maxY < y)
				maxY = y;
		}
		
		final ValueAxis yAxis = _chart.getXYPlot().getRangeAxis(); 
		if (yAxis.getUpperBound() < maxY)
			yAxis.setUpperBound(maxY + 5);
		
		final ValueAxis xAxis = _chart.getXYPlot().getDomainAxis();
		if (xAxis.getUpperBound() < maxX)
			xAxis.setUpperBound(maxX + 5);
		
		_chart.getXYPlot().setDataset(_dataset);
	}
	
	private void setDiscreteRenderer(final int series, final Color paint)
	{	    
		_discreteRenderer.setSeriesShape(series, _markerShape);
		
		_discreteRenderer.setSeriesFillPaint(series, paint);
		_discreteRenderer.setSeriesPaint(series, paint);
		_discreteRenderer.setDotHeight(_markerSize);
		_discreteRenderer.setDotWidth(_markerSize);	   
	}
	
	private void setSnailRenderer(final int series, final Color paint) 
	{		
		_snailRenderer.setSeriesShape(series, _markerShape);
		
		_snailRenderer.setSeriesFillPaint(series, paint);
		_snailRenderer.setSeriesPaint(series, paint);

		_snailRenderer.setUseFillPaint(true);
		_snailRenderer.setSeriesShapesFilled(series, true);
		_snailRenderer.setSeriesShapesVisible(series, true);	  
	}	
	
	public void addSelectionChangedListener(final ISelectionChangedListener listener) 
	{
		if (! _listeners.contains(listener))
			_listeners.add(listener);			
	}

	
	public void removeSelectionChangedListener(
			final ISelectionChangedListener listener) 
	{
		_listeners.remove(listener);		
	}
	
	public void addPropertyChangedListener(final PropertyChangeListener listener) 
	{
		if (! _propListeners.contains(listener))
			_propListeners.add(listener);			
	}

	
	public void removePropertyChangedListener(
			final PropertyChangeListener listener) 
	{
		_propListeners.remove(listener);		
	}
	
	public void saveState(final IMemento memento)
	{
		memento.putString(PLOT_ATTRIBUTES.ID, _plotId);
		final boolean is_snail = isSnail();
		memento.putBoolean(PLOT_ATTRIBUTES.IS_SNAIL, is_snail);	
		memento.putFloat(PLOT_ATTRIBUTES.TIME_PERIOD, (float)_timePeriod);		
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
	
	
	static public final class CrossSectionViewerTest extends junit.framework.TestCase
	{
		//TODO: test for null current time
		//TODO: test for time stepping
		//To write such test we'd need some Mock framework
	}
	

}
