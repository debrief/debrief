package org.mwc.cmap.xyplot.views;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.Status;
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
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.xyplot.views.providers.ICrossSectionDatasetProvider;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


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
	
	private List<XYSeries> _series = new ArrayList<XYSeries>();
	
	private XYLineAndShapeRenderer _snailRenderer = new XYLineAndShapeRenderer();
	
	private XYDotRenderer _discreteRenderer = new XYDotRenderer();
	
	/**
	 * The current time we are looking at
	 */
	private HiResDate _currentTime = null;
	
	private DateFormat _dateFormat = new SimpleDateFormat("dd-mm-yyyy HH:mm:ss");
	
	/**
	 * the chart marker
	 */
	private static final Shape _markerShape = new Rectangle2D.Double(-2, -2, 2, 2);
	private static final int _markerSize = 4;
	
	private long _timePeriod = 0;
	
	/**
	 * Memento parameters
	 */
	private static interface PLOT_ATTRIBUTES
	{
		final String DATA = "XYPlot_Data";
		final String TIME = "Current_Time";
		final String TIME_PERIOD = "Time_Period";
		final String IS_SNAIL = "Is_Snail";
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
		_series.clear();
		_dataset.removeAllSeries();
	
		if (_currentTime != null)
			walkThrough(theLayers, line);
		
		double maxX = 0;
		double maxY = 0;
		for (XYSeries series: _series)
		{
			final double x = series.getMaxX(); 
			if (maxX < x)
				maxX = x;
			final double y = series.getMaxY(); 
			if (maxY < y)
				maxY = y;
			_dataset.addSeries(series);
		}
		
		final ValueAxis yAxis = _chart.getXYPlot().getRangeAxis(); 
		if (yAxis.getUpperBound() < maxY)
			yAxis.setUpperBound(maxY + 5);
		
		final ValueAxis xAxis = _chart.getXYPlot().getDomainAxis();
		if (xAxis.getUpperBound() < maxX)
			xAxis.setUpperBound(maxX + 5);
		
		if (isSnail())
			_chart.getXYPlot().setRenderer(_snailRenderer);
		else
			_chart.getXYPlot().setRenderer(_discreteRenderer);
		_chart.getXYPlot().setDataset(_dataset);
		//printDataset(_dataset);
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
	
	private void printDataset(XYDataset xydataset)
	{
		int indexOf;
		for (int i = 0; i < xydataset.getSeriesCount(); i++) 
		{
			indexOf = xydataset.indexOf(xydataset.getSeriesKey(i));
			for (int j = 0; j < xydataset.getItemCount(indexOf); j++) 
			{
				double x = xydataset.getXValue(indexOf, j);
				double y = xydataset.getYValue(indexOf, j);
				System.out.println(x + " ; " + y);
			}
		}
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

	private void walkThrough(final Object root, final LineShape line)
	{		
		Enumeration<Editable> numer; 
	    if (root instanceof Layer)
	    	numer = ((Layer) root).elements();
	    else if (root instanceof Layers)
	    	numer = ((Layers) root).elements();
	    else return;
	    	
	    while(numer.hasMoreElements())  
	    {
	    	final Editable next = numer.nextElement();  
	    	if (next instanceof WatchableList)
		    {
	    			final WatchableList wlist = (WatchableList) next;
	    			if (!(wlist instanceof TrackWrapper))
	    				return;
	    			   			
	    			if (isSnail())
	    			{
	    				final HiResDate startDTG = new HiResDate(_currentTime
	    						.getDate().getTime() - _timePeriod);	    				
	    				final XYSeries series = _datasetProvider.getSeries(line,
	    						(TrackWrapper) wlist, startDTG, _currentTime);
	    		        _series.add(series);	    		        	
		    			setSnailRenderer(_series.size()-1, wlist.getColor());
	    			}
	    			else
	    			{	    				
	    				final XYSeries series = _datasetProvider.getSeries(line,
								(TrackWrapper) wlist, _currentTime);
		    			_series.add(series);
		    			setDiscreteRenderer(_series.size()-1, wlist.getColor());	    					    				
	    			}
		    }		    
	    	if (!(next instanceof WatchableList))
	    		walkThrough(next, line);
	    }
	}
	
	public void saveState(final IMemento memento)
	{
		final boolean is_snail = isSnail();
		memento.putBoolean(PLOT_ATTRIBUTES.IS_SNAIL, is_snail);	
		memento.putFloat(PLOT_ATTRIBUTES.TIME_PERIOD, (float)_timePeriod);
	
		if (_currentTime != null)
			memento.putString(PLOT_ATTRIBUTES.TIME, 
				_dateFormat.format(_currentTime.getDate()));
		
		final XStream xs = new XStream(new DomDriver());
		String str = xs.toXML(_dataset);
		memento.putString(PLOT_ATTRIBUTES.DATA, str);
		
		if (is_snail)
		{
			for (int i = 0; i < _dataset.getSeriesCount(); i++) 
			{
				Paint color = _snailRenderer.getSeriesPaint(i);
				saveColor(memento, i, (Color) color);
			}
		}
		else
		{
			for (int i = 0; i < _dataset.getSeriesCount(); i++) 
			{
				Paint color = _discreteRenderer.getSeriesPaint(i);
				saveColor(memento, i, (Color) color);
			}
		}
	}
	
	private void saveColor(final IMemento memento, final int series, final Color color)
	{
		memento.putInteger("SERIES_" + series + "_COLOR", color.getRGB());
	}
	
	public void restoreState(final IMemento memento)
	{
		final XStream xs = new XStream(new DomDriver());
		final String dataStr = memento.getString(PLOT_ATTRIBUTES.DATA);

		if (dataStr == null)
			return;
		
		_dataset = (XYSeriesCollection) xs.fromXML(dataStr);
		
		final Boolean is_snail = memento.getBoolean(PLOT_ATTRIBUTES.IS_SNAIL);
		if (is_snail)
		{
			
			_timePeriod = memento.getFloat(PLOT_ATTRIBUTES.TIME_PERIOD).longValue();
			
			for (int i = 0; i < _dataset.getSeriesCount(); i++) 
			{
				final int RGB = memento.getInteger("SERIES_" + i + "_COLOR");
				setSnailRenderer(i, new Color(RGB));
			}
			_chart.getXYPlot().setRenderer(_snailRenderer);
		}
		else
		{
			for (int i = 0; i < _dataset.getSeriesCount(); i++) 
			{
				final int RGB = memento.getInteger("SERIES_" + i + "_COLOR");
				setDiscreteRenderer(i, new Color(RGB));
			}
			_chart.getXYPlot().setRenderer(_discreteRenderer);
		}
		_chart.getXYPlot().setDataset(_dataset);
		
		final String timeStr = memento.getString(PLOT_ATTRIBUTES.TIME);
		
		try 
		{
			if (timeStr != null)
				_currentTime = new HiResDate(_dateFormat.parse(timeStr));
		} catch (ParseException e) 
		{
			CorePlugin.logError(Status.ERROR, 
					"Failed to read time in saved XY Plot data", e);
		}
	}
	
	protected long getPeriod()
	{
		return _timePeriod;
	}
	
	
	static public final class CrossSectionViewerTest extends junit.framework.TestCase
	{
		//TODO: test for null current time
		//TODO: test for time stepping
	}
	

}
