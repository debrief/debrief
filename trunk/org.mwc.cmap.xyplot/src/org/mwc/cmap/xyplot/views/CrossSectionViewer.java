package org.mwc.cmap.xyplot.views;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;

public class CrossSectionViewer
{
	
	/**
	 *  current line annotation being selected 
	 */
	private LineShape _line = null;
	
	/**
	 * the current layers
	 */
	private Layers _layers = null;
	
	private List<ISelectionChangedListener> _listeners = new ArrayList<ISelectionChangedListener>();
	
	protected CrossSectionViewer(Composite parent)
	{
		// TODO Auto-generated method stub
	}
	
	public void drawDiagram(final Layers theLayers)
	{
		if(_line == null || _layers == null)
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

	public void setSelection(final ISelection sel) 
	{
		// just check that this is something we can work with
		if (sel instanceof StructuredSelection) 
		{
			final StructuredSelection str = (StructuredSelection) sel;

			// hey, is there a payload?
			if (str.getFirstElement() == null)
				return;
		    // we only support single selections, 
			// so get the first element
			final Object first = str.getFirstElement();
			if (first instanceof EditableWrapper) 
			{
				final EditableWrapper ew = (EditableWrapper) first;
				final Editable eb = ew.getEditable();
				if (eb instanceof ShapeWrapper)
				{
					final PlainShape shape = ((ShapeWrapper) eb).getShape();
					if (shape instanceof LineShape && !shape.equals(_line))
					{
						_line = (LineShape) shape;
						//TODO: repaint
					}
				}
			}			
		}
	}
	
	public void setFocus()
    {
		// TODO Auto-generated method stub
    }
	
	public void setSelectionToWidget(final StructuredSelection selection)
	{
		final Object o = selection.getFirstElement();
		if (!(o instanceof EditableWrapper))
			return;
		final EditableWrapper element = (EditableWrapper) o;
		final Editable selectedItem = element.getEditable();
		// TODO: set the selection
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
