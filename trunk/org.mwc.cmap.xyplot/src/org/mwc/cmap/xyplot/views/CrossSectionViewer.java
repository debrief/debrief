package org.mwc.cmap.xyplot.views;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
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
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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
	private Frame _chartFrame;
		
	private JFreeChart _chart;
	
	private XYLineAndShapeRenderer _renderer = new XYLineAndShapeRenderer();
	
	//TODO: get the units
	private ILocationCalculator _calc = new LocationCalculator();
	
	private XYDataset _dataset;
	
	/**
	 * the chart marker
	 */
	private static final Shape circle = new Ellipse2D.Double(-3, -3, 6, 6);
   	
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
				"Distances along the line", // X-Axis label
				"depth/elevation", // Y-Axis label
				_dataset, // Dataset,
				PlotOrientation.HORIZONTAL,
				true, // Show legend,
				true, //tooltips
				true //urs
				);
		
	    _chart.getXYPlot().setRenderer(_renderer);
        
		final ChartPanel jfreeChartPanel = new ChartPanel(_chart);
		_chartFrame.add(jfreeChartPanel);		
	}
	
	public void fillPlot(final Layers theLayers, final LineShape line)
	{
		if (theLayers == null || line == null)
			return;
		walkThrough(theLayers, line);
		_chart.getXYPlot().setDataset(_dataset);
		printDataset(_dataset);
	}
	
	private void setDiscreteRenderer(Color paint)
	{
		_renderer.setSeriesShape(0, circle);
		_renderer.setSeriesPaint(0, paint);
	   // _renderer.setSeriesPaint(0, line);
	    _renderer.setUseFillPaint(true);
	    _renderer.setSeriesShapesFilled(0, true);
	    _renderer.setSeriesShapesVisible(0, true);
	    _renderer.setUseOutlinePaint(true);
	    //_renderer.setSeriesOutlinePaint(0, line);
		
	}
	
	private void setSnailRenderer()
	{
		
	}
	
	private void printDataset(XYDataset xydataset)
	{
		Comparable comparable;
		int indexOf;
		for (int i = 0; i < xydataset.getSeriesCount(); i++) {

			comparable = xydataset.getSeriesKey(i);
			indexOf = xydataset.indexOf(comparable);
			for (int j = 0; j < xydataset.getItemCount(indexOf); j++) {

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
	    			    final XYSeries series = new XYSeries("Watchables between [now - snail_period; now]");
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
	    			    final XYSeries series = new XYSeries("Watchables neares to Now");
	    				for(Watchable wb: wbs)
	    				{
	    					final Double x_coord = new Double(_calc.getDistance(line, wb));
    		        		final Double y_coord = new Double(wb.getDepth());
	    					series.add(x_coord, y_coord);    		        		
	    				}
	    				//TODO: remove hard-coded values
	    				series.add(0.0350068580566741, 0.0);
	    				_dataset = new XYSeriesCollection(series);
	    				setDiscreteRenderer(wlist.getColor());
	    			}
		    }		    
	    	if (!(next instanceof WatchableList))
	    		walkThrough(next, line);
	    }
	}
	
	private final class MyRenderer extends XYLineAndShapeRenderer 
	{
		 Color _color;

	     MyRenderer(boolean lines, boolean shapes, Color color) 
	     {
	        	
	        super(lines, shapes);
	        _color = color;
	     }

	        @Override
	        public Paint getItemFillPaint(int row, int column) 
	        {
	            return _color;
	        }
	}
	
	

}
