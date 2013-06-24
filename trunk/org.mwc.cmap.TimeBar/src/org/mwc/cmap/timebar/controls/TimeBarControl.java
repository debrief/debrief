package org.mwc.cmap.timebar.controls;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.widgets.ganttchart.DefaultSettings;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import MWC.GUI.Editable;
import MWC.GUI.ITimeBarDrawable;
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
    	_chart = new GanttChart(parent, SWT.MULTI, new GanttChartSettings());		
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
    	Enumeration<Editable> numer = theLayers.elements();
    	while (numer.hasMoreElements())
		{
    		//TODO: refactoring - recursive call
			Layer thisL = (Layer) numer.nextElement();
			if (thisL instanceof ITimeBarDrawable)
			{
				Enumeration<Editable> numerInner = thisL.elements();
				while (numerInner.hasMoreElements())
				{
					Editable inner = numerInner.nextElement();
					if (inner instanceof Layer){
						Enumeration<Editable> numerInnerInner = ((Layer)inner).elements();
						while (numerInnerInner.hasMoreElements()){
							Editable next = numerInnerInner.nextElement();
							if (next instanceof ITimeBarDrawable)
							{	
								((ITimeBarDrawable) next).draw(_chart);
							}
						}
					}
					
				}
				
			}
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
//		final SelectionChangedEvent e = new SelectionChangedEvent(this, selection);
//        
//        for (final ISelectionChangedListener l: _listeners) {
//            SafeRunner.run(new SafeRunnable() {
//                public void run() {
//                    l.selectionChanged(e);
//                }
//            });
//		}
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
	public Calendar getStartupCalendarDate() 
	{
		//TODO: this is hard coded
		Calendar cal = Calendar.getInstance();
		cal.set(2009, 4, 15);
		return cal;
	}
	
	@Override
	public String getTextDisplayFormat() 
	{
		return "#name#";
	}
}	

