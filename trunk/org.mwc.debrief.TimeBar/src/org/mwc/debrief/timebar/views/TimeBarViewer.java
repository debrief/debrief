package org.mwc.debrief.timebar.views;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.timebar.model.IEventEntry;
import org.mwc.debrief.timebar.model.TimeBar;
import org.mwc.debrief.timebar.model.TimeSpot;
import org.mwc.debrief.timebar.painter.ITimeBarsPainter;
import org.mwc.debrief.timebar.painter.NebulaGanttPainter;

import Debrief.Wrappers.TacticalDataWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
//import Debrief.Wrappers.Track.TrackSegment;

public class TimeBarViewer implements ISelectionProvider {
	
	/**
	 * the people listening to us
	 */
	List<ISelectionChangedListener> _listeners = new ArrayList<ISelectionChangedListener>();

	/**
	 * The current selection for this provider
	 */
    ISelection _theSelection = null;
    
    private Layers _myLayers;
    
   // GanttChart _chart;
    
    List<IEventEntry> _timeBars = new ArrayList<IEventEntry>(); 
    List<IEventEntry> _timeSpots = new ArrayList<IEventEntry>();
    
    ITimeBarsPainter _painter;
    
    public TimeBarViewer(Composite parent, final Layers theLayers)
    {
    	_myLayers = theLayers;
    	_painter = new NebulaGanttPainter(parent, this);    	
    }
    
    
    public void setFocus()
    {
    	_painter.setFocus();
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
    	//TODO: not clear the lists but see what changed?
    	_timeBars.clear();
    	_timeSpots.clear();
    	
    	_painter.clear();
    	
    	walkThrough(theLayers);    	
    	for(IEventEntry barEvent: _timeBars)
    		_painter.drawBar(barEvent);
    	for(IEventEntry spotEvent: _timeSpots)
    		_painter.drawSpot(spotEvent);
    	// move chart start date to the earliest event
    	//TODO: move to the layer that has been changed; to the earliest event otherwise;
    	_painter.jumpToBegin();
    }
    
    private void walkThrough(Object root)
    {
    	Enumeration<Editable> numer; 
    	if (root instanceof Layer)
    		numer = ((Layer) root).elements();
    	else if (root instanceof Layers)
    		numer = ((Layers) root).elements();
    	else return;
    	
    	while(numer.hasMoreElements())  
    	{
    		Editable next = numer.nextElement();
    		if (next instanceof WatchableList)
	    	{
	    		_timeBars.add(new TimeBar((WatchableList) next));	    		
	    	}
	    	else if (next instanceof Watchable)
	    	{
	    		_timeSpots.add(new TimeSpot((Watchable) next));
	    	}
	    	if (next instanceof TrackWrapper)
	    	{	    		
	    		BaseLayer sensors = ((TrackWrapper) next).getSensors();
	    		traverseTrackData(sensors);
	    		BaseLayer solutions = ((TrackWrapper) next).getSolutions();
	    		traverseTrackData(solutions);
	    		_timeBars.add(new TimeBar(((TrackWrapper) next).getSegments()));
	    	}
    		if (!(next instanceof WatchableList))
    			walkThrough(next);
    	}
    }  
    
    private void traverseTrackData(BaseLayer data)
    {
    	Enumeration<Editable> enumer = data.elements();
		while(enumer.hasMoreElements())
		{
			Editable solution = enumer.nextElement();
			if (solution instanceof TacticalDataWrapper)
				_timeBars.add(new TimeBar((TacticalDataWrapper) solution));
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
	
	public void setSelectionToObject(Object modelEntry)
	{
		if (modelEntry instanceof Editable)
		{
			Editable ed = (Editable) modelEntry;    					
			setSelection(new StructuredSelection(new EditableWrapper(ed, null, _myLayers)));
		}
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
	
	public void setSelectionToWidget(StructuredSelection selection)
	{
		Object o = selection.getFirstElement();
		if (!(o instanceof EditableWrapper))
			return;
		EditableWrapper element = (EditableWrapper) o;
		Editable selectedItem = element.getEditable();
		_painter.selectTimeBar(selectedItem);		
	}
	
}




