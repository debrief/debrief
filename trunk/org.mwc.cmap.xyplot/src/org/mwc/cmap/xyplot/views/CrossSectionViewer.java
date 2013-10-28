package org.mwc.cmap.xyplot.views;

import java.util.Enumeration;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.property_support.EditableWrapper;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;

public class CrossSectionViewer implements ISelectionProvider
{
	
	/**
	 *  current line annotation being selected 
	 */
	private LineShape _line;
	
	/**
	 * the current layers
	 */
	private Layers _layers;
	
	/**
	 * The current selection for this provider
	 */
    ISelection _theSelection = null;
	
	protected CrossSectionViewer(Composite parent)
	{
		// TODO Auto-generated method stub
	}
	
	public void drawDiagram(final Layers theLayers)
	{
		//TODO implement
		walkThrough(theLayers);    	
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ISelection getSelection() 
	{
		return _theSelection;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelection(final ISelection sel) 
	{
		_theSelection = sel;
		// just check that this is something we can work with
		if (sel instanceof StructuredSelection) 
		{
			final StructuredSelection str = (StructuredSelection) sel;

			// hey, is there a payload?
			if (str.getFirstElement() != null) 
			{
				// sure is. we only support single selections, so get the first
				// element
				final Object first = str.getFirstElement();
				//TODO: compare the selection with the current line,
				// repaint if necessary
									
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
