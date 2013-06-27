package org.mwc.debrief.timebar.controls;

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
import org.eclipse.nebula.widgets.ganttchart.DefaultSettings;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttEventListenerAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.timebar.model.IChartItemDrawable;
import org.mwc.debrief.timebar.model.TimeBar;
import org.mwc.debrief.timebar.model.TimeSpot;

import Debrief.Wrappers.TrackWrapper;
//import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;

public class TimeBarControl implements ISelectionProvider {
	
	/**
	 * the people listening to us
	 */
	List<ISelectionChangedListener> _listeners = new ArrayList<ISelectionChangedListener>();

	/**
	 * The current selection for this provider
	 */
    ISelection _theSelection = null;
    
    private Layers _myLayers;
    
    GanttChart _chart;
    
    List<IChartItemDrawable> _timeBars = new ArrayList<IChartItemDrawable>(); 
    List<IChartItemDrawable> _timeSpots = new ArrayList<IChartItemDrawable>();
    
    public TimeBarControl(Composite parent, final Layers theLayers)
    {
    	_myLayers = theLayers;
    	_chart = new GanttChart(parent, SWT.MULTI, new GanttChartSettings());
    	
    	_chart.addGanttEventListener(new GanttEventListenerAdapter()
    	{
    		private boolean fireSelectionChanged(GanttEvent event,
    				List<IChartItemDrawable> eventEntries)
    		{
    			for (IChartItemDrawable eventEntry: eventEntries)
    			{
	    			if (eventEntry.getPresentation().equals(event))
					{
						if (eventEntry.getSource() instanceof Editable)
						{
							Editable ed = (Editable) eventEntry.getSource();    					
							setSelection(new StructuredSelection(new EditableWrapper(ed, null, _myLayers)));
						}
						return true;
					}
    			}
    			return false;
    		}
    		
    		@Override
    		public void eventSelected(GanttEvent event, List allSelectedEvents,
    				MouseEvent me) 
    		{
    			super.eventSelected(event, allSelectedEvents, me);
    			if (!fireSelectionChanged(event, _timeBars))
    				fireSelectionChanged(event, _timeSpots);    				
    		}
    	});
    }
    
    
    public void setFocus()
    {
    	_chart.setFocus();
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
    	if (_chart.isDisposed())
    		return;
    	
    	walkThrough(theLayers);    	
    	for(IChartItemDrawable barEvent: _timeBars)
    		barEvent.draw(_chart);
    	for(IChartItemDrawable spotEvent: _timeSpots)
    		spotEvent.draw(_chart);
    	// move chart start date to the earliest event
    	_chart.getGanttComposite().jumpToEarliestEvent();
    }
    
    private void walkThrough(Object root)
    {
    	Enumeration<Editable> numer; 
    	if (root instanceof Layer)
    		numer = ((Layer) root).elements();
    	else if (root instanceof Layers)
    		numer = ((Layers) root).elements();
    	else return;
    	
    	while(numer.hasMoreElements())  {
    		Editable next = numer.nextElement();
    		if (next instanceof WatchableList)
	    	{
	    		_timeBars.add(new TimeBar((WatchableList) next));
	    	}
	    	else if (next instanceof Watchable)
	    	{
	    		_timeSpots.add(new TimeSpot((Watchable) next));
	    	}
	    	else if (next instanceof TrackWrapper)
	    	{
	    		_timeBars.add(new TimeBar(((TrackWrapper) next).getSolutions()));
	    		_timeBars.add(new TimeBar(((TrackWrapper) next).getSensors()));
	    		_timeBars.add(new TimeBar(((TrackWrapper) next).getSegments()));
	    	}
    		if (!(next instanceof WatchableList))
    			walkThrough(next);
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
	
	public void setSelectionToWidget(StructuredSelection selection)
	{
		Object o = selection.getFirstElement();
		if (!(o instanceof EditableWrapper))
			return;
		EditableWrapper element = (EditableWrapper) o;
		Editable selectedItem = element.getEditable();
		if (!selectGanttEvent(selectedItem, _timeBars))
			selectGanttEvent(selectedItem, _timeSpots);
	}
	
	private boolean selectGanttEvent(Object item, List<IChartItemDrawable> events)
	{
		for (IChartItemDrawable event: events)
		{
			if (item.equals(event.getSource()))
			{ 				
				_chart.getGanttComposite().setSelection(event.getPresentation());
				return true;
			}
		}
		return false;
	}

}


class GanttChartSettings extends DefaultSettings
{
	
	@Override
	public boolean allowArrowKeysToScrollChart() 
	{
		return true;
	}
	
	@Override
	public boolean allowInfiniteHorizontalScrollBar() 
	{
		return false;
	}
	
	@Override
	public boolean drawEventsDownToTheHourAndMinute() 
	{	
		return true;
	}
	
	@Override
	public boolean drawFullPercentageBar() 
	{
		return false;
	}
	
	@Override
	public int getInitialZoomLevel() 
	{
		return ZOOM_HOURS_NORMAL;
	}
	
	@Override
	public String getTextDisplayFormat() 
	{
		return "#name#";
	}
}	

