package org.mwc.cmap.xyplot.views;

import java.awt.Frame;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
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
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;

public class CrossSectionViewer
{
	
	private List<ISelectionChangedListener> _listeners = new ArrayList<ISelectionChangedListener>();
	private List<PropertyChangeListener> _propListeners = new ArrayList<PropertyChangeListener>();
	
	/**
	 * the Swing control we insert the plot into
	 */
	private Frame _plotControl;
		
	JFreeChart _chart;
	
	//TODO: get the units
	ILocationCalculator _calc = new LocationCalculator();
	
	XYDataset _dataset;
	
	protected CrossSectionViewer(final Composite parent)
	{
		// TODO Auto-generated method stub
		//we need an SWT.EMBEDDED object to act as a holder
		final Composite holder = new Composite(parent, SWT.EMBEDDED);
		holder.setLayoutData(new GridData(GridData.FILL_VERTICAL
						| GridData.FILL_HORIZONTAL));

		// now we need a Swing object to put our chart into
		_plotControl = SWT_AWT.new_Frame(holder);

		//TODO: restore Previous Plot?
		
		
	}
	
	public void fillPlot(final Layers theLayers, final LineShape line)
	{
		if (theLayers == null || line == null)
			return;
		walkThrough(theLayers, line);
		//TODO implement
				
		_chart = ChartFactory.createXYAreaChart
				("Sample XY Chart", // Title
				"Distances along the line", // X-Axis label
				"depth/elevation", // Y-Axis label
				_dataset, // Dataset,
				PlotOrientation.HORIZONTAL,
				true, // Show legend,
				true, //tooltips
				true //urs
				);
		
		final ChartPanel jfreeChartPanel = new ChartPanel(_chart);
		_plotControl.add(jfreeChartPanel);
	}
	
//	private ValueAxis createTimeAxis()
//	{
//		// see if we are in hi-res mode. If we are, don't use a formatted
//		// axis, just use the plain long microseconds value
//		if (HiResDate.inHiResProcessingMode())
//		{
//
//			final NumberAxis nAxis = new NumberAxis("time (secs.micros)")
//			{
//				private static final long serialVersionUID = 1L;
//			};
//			nAxis.setAutoRangeIncludesZero(false);
//			return nAxis;
//		}
//		else
//		{
//			// create a date-formatting axis
//			final DateAxis dAxis = new RelativeDateAxis();
//			dAxis.setStandardTickUnits(DateAxisEditor
//						.createStandardDateTickUnitsAsTickUnits());
//			return dAxis;
//		}
//
//	}

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
	    			final HiResDate now = new HiResDate();
	    			//TODO: check for Snail period
	    			final boolean is_snail = false;
	    			if (is_snail)
	    			{
	    				//TODO: get the snail period
	    				final HiResDate snail_period = new HiResDate(now.getDate().getTime() - 5);
	    				final long diff = now.getDate().getTime() - snail_period.getDate().getTime();
	    				final HiResDate start_date = new HiResDate(diff);
	    				
	    				final Collection<Editable> wbs  = wlist.getItemsBetween(start_date, now);
	    			    final Iterator<Editable> itr = wbs.iterator();
		        		//TODO: set the series name
	    			    final XYSeries series = new XYSeries("Snail name todo");
	    		        while (itr.hasNext()) 
	    		        {
	    		        	final Editable ed = itr.next();
	    		        	if (ed instanceof Watchable) 
	    		        	{
	    		        		final Double x_coord = new Double(_calc.getDistance(line, (Watchable) ed));
	    		        		final Double y_coord = new Double(((Watchable) ed).getDepth());
	    		        		series.add(x_coord, y_coord);
	    		        	}	
	    		        	_dataset = new XYSeriesCollection(series);   	
	    		        }	    		        
	    			}
	    			else
	    			{
	    				final Watchable[] wbs = wlist.getNearestTo(now);
	    				//TODO: set the series name
	    			    final XYSeries series = new XYSeries("name todo");
	    				for(Watchable wb: wbs)
	    				{
	    					final Double x_coord = new Double(_calc.getDistance(line, wb));
    		        		final Double y_coord = new Double(wb.getDepth());
    		        		series.add(x_coord, y_coord);    		        		
	    				}
	    				_dataset = new XYSeriesCollection(series);
	    			}
		    }		    
	    	if (!(next instanceof WatchableList))
	    		walkThrough(next, line);
	    }
	}
	
	

}
