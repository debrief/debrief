package org.mwc.cmap.xyplot.views;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.widgets.Composite;

import Debrief.Wrappers.FixWrapper;
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
	
	//TODO: get the units
	ILocationCalculator _calc = new LocationCalculator();
	/**
	 * "discrete" points for non-snail mode
	 */
	Map<HiResDate, List<Pair<Double, Double>>> _distances = new HashMap<HiResDate, List<Pair<Double,Double>>>();
	Map<HiResDate, List<Pair<Double, Double>>> _snails = new HashMap<HiResDate, List<Pair<Double,Double>>>();
	
	protected CrossSectionViewer(Composite parent)
	{
		// TODO Auto-generated method stub
	}
	
	public void drawDiagram(final Layers theLayers, final LineShape line)
	{
		if (theLayers == null || line == null)
			return;
		walkThrough(theLayers, line);
		//TODO implement
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
	    			    final List<Pair<Double, Double>> distances = new ArrayList<Pair<Double,Double>>();
	    		        while (itr.hasNext()) 
	    		        {
	    		        	final Editable ed = itr.next();
	    		        	if (ed instanceof Watchable) 
	    		        	{
	    		        		final Double x_coord = new Double(_calc.getDistance(line, (Watchable) ed));
	    		        		final Double y_coord = new Double(((Watchable) ed).getDepth());
	    		        		distances.add(new Pair(x_coord, y_coord));
	    		        	}	    		        		
	    		        	_snails.put(start_date, distances);
	    		        }	    		        
	    			}
	    			else
	    			{
	    				final Watchable[] wbs = wlist.getNearestTo(now);
	    				final List<Pair<Double, Double>> distances = new ArrayList<Pair<Double,Double>>();
	    				for(Watchable wb: wbs)
	    				{
	    					final Double x_coord = new Double(_calc.getDistance(line, wb));
    		        		final Double y_coord = new Double(wb.getDepth());
    		        		distances.add(new Pair(x_coord, y_coord));
	    				}
	    				_distances.put(now, distances);
	    			}
		    }		    
	    	if (!(next instanceof WatchableList))
	    		walkThrough(next, line);
	    }
	}
	
	final class Pair<T, U> 
	{         
	    public final T t;
	    public final U u;

	    Pair(T t, U u) 
	    {         
	        this.t= t;
	        this.u= u;
	    }
	}
    

}
