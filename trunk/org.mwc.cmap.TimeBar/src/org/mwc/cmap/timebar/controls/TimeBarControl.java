package org.mwc.cmap.timebar.controls;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.property_support.EditableWrapper;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

public class TimeBarControl implements ISelectionProvider {
	
	/**
	 * the people listening to us
	 */
	List<ISelectionChangedListener> _listeners = new ArrayList<ISelectionChangedListener>();

	/**
	 * The current selection for this provider
	 */
    ISelection _theSelection = null;
    
    GanttChart _chart;
    
    public TimeBarControl(Composite parent)
    {
    	_chart = new GanttChart(parent, SWT.MULTI);		
    }
    
    
    /**
     * Runs through the layers, extracts the required elements:
     *  track segments, sensor wrappers for a track, annotations/shapes with the time.
     *  Draw these elements as Gantt Events (time bars) on the GanttChart control.
     *  Extracts narrative entries and annotations/shapes with single time 
     *  to display them as point markers. 
     * @param theLayers - Debrief data.
     */
    public void drawDiagram(final Layers theLayers)
    {
    	//TODO: the desired items would implement some ITimeBarDrawable interface 
    	// in order to know how to draw themselves.
    	Enumeration<Editable> numer = theLayers.elements();
		while (numer.hasMoreElements())
		{
			Layer thisL = (Layer) numer.nextElement();
			//EditableWrapper wrapper = new EditableWrapper(thisL, null, theLayers);
			System.out.println(thisL.getClass());
//			Enumeration<Editable> numerInner = thisL.elements();
//			while (numerInner.hasMoreElements())
//			{
//				if (numerInner instanceof TrackWrapper) 
//				System.out.println(numerInner.getClass());
//			}
		}
    }

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) 
	{
		if (! _listeners.contains(listener))
			_listeners.add(listener);	
	}

	@Override
	public ISelection getSelection() 
	{
		return _theSelection;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) 
	{
		_listeners.remove(listener);		
	}

	@Override
	public void setSelection(ISelection selection) 
	{
		_theSelection = selection;
		final SelectionChangedEvent e = new SelectionChangedEvent(this, selection);
        
        for (final ISelectionChangedListener l: _listeners) {
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.selectionChanged(e);
                }
            });
		}
	}

}
