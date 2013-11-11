package org.mwc.cmap.xyplot.views;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.mwc.cmap.xyplot.views.providers.ICrossSectionDatasetProvider;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;


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
	protected HiResDate _currentTime = null;
	
	/**
	 * the chart marker
	 */
	private static final Shape _markerShape = new Rectangle2D.Double(-4, -4, 8, 8);
	private static final int _markerSize = 4;
	
   	
	protected CrossSectionViewer(final Composite parent)
	{
	    //we need an SWT.EMBEDDED object to act as a holder
		final Composite holder = new Composite(parent, SWT.EMBEDDED);
		holder.setLayoutData(new GridData(GridData.FILL_VERTICAL
						| GridData.FILL_HORIZONTAL));

		// now we need a Swing object to put our chart into
		_chartFrame = SWT_AWT.new_Frame(holder);

		//TODO: restore Previous Plot?
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
        
		final ChartPanel jfreeChartPanel = new ChartPanel(_chart);
		_chartFrame.add(jfreeChartPanel);		
	}
	
	public void newTime(final HiResDate newDTG)
	{
		_currentTime = newDTG;
	}
	
	public void fillPlot(final Layers theLayers, final LineShape line,
			final ICrossSectionDatasetProvider prov)
	{
		if (theLayers == null || line == null)
			return;
		_datasetProvider = prov;
		_series.clear();
		_dataset.removeAllSeries();
		//TODO: check for Snail period
		final boolean is_snail = false;
		if (_currentTime != null)
			walkThrough(theLayers, line, is_snail);
		
		for (XYSeries series: _series)
			_dataset.addSeries(series);
		
		if (is_snail)
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
			ISelectionChangedListener listener) 
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

	private void walkThrough(final Object root, final LineShape line, final boolean is_snail)
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
	    			   			
	    			if (is_snail)
	    			{
	    				//TODO: get the snail period
	    				final HiResDate snail_period = new HiResDate(_currentTime.getDate().getTime() - 5);
	    				final long diff = _currentTime.getDate().getTime() - snail_period.getDate().getTime();
	    				final HiResDate start_date = new HiResDate(diff);
	    				final XYSeries series = _datasetProvider.getSeries(line,
							(TrackWrapper) wlist, start_date, _currentTime);
	    		        _series.add(series);	    		        	
		    			setSnailRenderer(_series.size()-1, wlist.getColor());
	    			}
	    			else
	    			{	    				
	    				final XYSeries series = _datasetProvider.getSeries(line,
								(TrackWrapper) wlist, _currentTime);
		    			//TODO: remove hard-coded values
		    			series.add(0.0450068580566741 + _series.size(), 0.0);
		    			_series.add(series);
		    			setDiscreteRenderer(_series.size()-1, wlist.getColor());	    					    				
	    			}
		    }		    
	    	if (!(next instanceof WatchableList))
	    		walkThrough(next, line, is_snail);
	    }
	}
	
	static public final class CrossSectionViewerTest extends junit.framework.TestCase
	{
		//TODO: test for null current time
		//TODO: test for time stepping
	}
	

}
