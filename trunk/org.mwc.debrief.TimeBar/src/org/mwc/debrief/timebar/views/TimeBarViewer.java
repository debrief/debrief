package org.mwc.debrief.timebar.views;

import java.util.ArrayList;
import java.util.Date;
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
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.mwc.cmap.TimeController.views.TimeController;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.timebar.model.IEventEntry;
import org.mwc.debrief.timebar.model.TimeBar;
import org.mwc.debrief.timebar.model.TimeSpot;
import org.mwc.debrief.timebar.painter.ITimeBarsPainter;
import org.mwc.debrief.timebar.painter.ITimeBarsPainterListener;
import org.mwc.debrief.timebar.painter.NebulaGanttPainter;

import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;


public class TimeBarViewer implements ISelectionProvider, ITimeBarsPainterListener {
	
	/**
	 * the people listening to us
	 */
	List<ISelectionChangedListener> _listeners = new ArrayList<ISelectionChangedListener>();

	/**
	 * The current selection for this provider
	 */
    ISelection _theSelection = null;
    
    private final Layers _myLayers;
    
   // GanttChart _chart;
    
    List<IEventEntry> _timeBars = new ArrayList<IEventEntry>(); 
    List<IEventEntry> _timeSpots = new ArrayList<IEventEntry>();
   
    ITimeBarsPainter _painter;
    
    public TimeBarViewer(final Composite parent, final Layers theLayers)
    {
    	_myLayers = theLayers;
    	_painter = new NebulaGanttPainter(parent);    
    	_painter.addListener(this);
    }
    
    
    public void setFocus()
    {
    	_painter.setFocus();
    }
    
    public void zoomIn()
    {
    	_painter.zoomIn();
    }
    
    public void zoomOut()
    {
    	_painter.zoomOut();    	
    }
    
    public void fitToWindow()
    {
    	_painter.fitToWindow();
    }
    
    
    /**
     * Runs through the layers, extracts the required elements:
     *  track segments, sensor wrappers for a track, annotations/shapes with the time.
     *  Draw these elements as Gantt Events (time bars) on the GanttChart control.
     *  Extracts narrative entries and annotations/shapes with single time 
     *  to display them as point markers. 
     * @param theLayers - Debrief data.
     */
    public void drawDiagram(final Layers theLayers, final boolean jumpToBegin)
    {   
    	_timeBars.clear();
    	_timeSpots.clear();
    	
    	_painter.clear();
    	
    	walkThrough(theLayers);    	
    	for(final IEventEntry barEvent: _timeBars)
    		_painter.drawBar(barEvent);
    	for(final IEventEntry spotEvent: _timeSpots)
    		_painter.drawSpot(spotEvent);
    	// move chart start date to the earliest event
    	if (jumpToBegin)
    		_painter.jumpToBegin();
    }
    
    public boolean isDisposed()
    {
    	return _painter.isDisposed();
    }
    
    public void drawDiagram(final Layers theLayers)
    {
    	this.drawDiagram(theLayers, false);
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
    		if (next instanceof PlainWrapper)
    		{
				((PlainWrapper) next).addPropertyChangeListener(
						PlainWrapper.VISIBILITY_CHANGED, _painter);
			}
    		
    		if (next instanceof WatchableList)
	    	{
    			final WatchableList wlist = (WatchableList) next;
    			if (wlist.getStartDTG() != null)
    			{
    				if (wlist.getEndDTG() != null)
    				{
    					if (wlist instanceof TrackWrapper)
    					{
    						_timeBars.add(new TimeBar((TrackWrapper) next));
    					}
    					else
    						_timeBars.add(new TimeBar(wlist));
    				}
    				else
    					_timeSpots.add(new TimeSpot(wlist));
    			}
	    	}
	    	else if (next instanceof Watchable)
	    	{	    		
	    		final Watchable wb = (Watchable) next;
	    		if (wb.getTime() != null)
	    			_timeSpots.add(new TimeSpot(wb));
	    	}
	    	if(next instanceof NarrativeWrapper)
	    	{
	    		_timeBars.add(new TimeBar((NarrativeWrapper) next));
	    	}	    	
    		if (!(next instanceof WatchableList))
    			walkThrough(next);
    	}
    }  
    
	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) 
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
			final ISelectionChangedListener listener) 
	{
		_listeners.remove(listener);		
	}
	
	public void setSelectionToObject(final Object modelEntry)
	{
		if (modelEntry instanceof Editable)
		{
			final Editable ed = (Editable) modelEntry;    					
			setSelection(new StructuredSelection(new EditableWrapper(ed, null, _myLayers)));
		}
	}

	@Override
	public void setSelection(final ISelection selection) 
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
	
	public void setSelectionToWidget(final StructuredSelection selection)
	{
		final Object o = selection.getFirstElement();
		if (!(o instanceof EditableWrapper))
			return;
		final EditableWrapper element = (EditableWrapper) o;
		final Editable selectedItem = element.getEditable();
		_painter.selectTimeBar(selectedItem);		
	}


	@Override
	public void chartDoubleClicked(final Date clickedAt) 
	{
		final HiResDate newDTG = new HiResDate(clickedAt);
		final IViewPart part = CorePlugin.findView(CorePlugin.TIME_CONTROLLER);
		if (part != null)
		{
			((TimeController) part).fireNewTime(newDTG);
		}
	}


	@Override
	public void eventDoubleClicked(final Object eventEntry) 
	{
		CorePlugin.openView(CorePlugin.LAYER_MANAGER);
		CorePlugin.openView(IPageLayout.ID_PROP_SHEET);		
	}


	@Override
	public void eventSelected(final Object eventEntry) 
	{
		setSelectionToObject(eventEntry);		
	}
	
}




