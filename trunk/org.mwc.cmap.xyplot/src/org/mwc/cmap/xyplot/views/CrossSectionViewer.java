package org.mwc.cmap.xyplot.views;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.widgets.Composite;

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
	
	protected CrossSectionViewer(Composite parent)
	{
		// TODO Auto-generated method stub
	}
	
	public void drawDiagram(final Layers theLayers, LineShape line)
	{
		if (theLayers == null || line == null)
			return;
		//TODO implement
		walkThrough(theLayers);    	
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

	private void walkThrough(final Object root)
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
	    			Watchable[] wbs = wlist.getNearestTo(now);
	    			// TODO: do the calculations for the current line
		    }		    
	    	if (!(next instanceof WatchableList))
	    		walkThrough(next);
	    }
	}
    

}
