package org.mwc.debrief.timebar.painter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.nebula.widgets.ganttchart.DefaultSettings;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttCheckpoint;
import org.eclipse.nebula.widgets.ganttchart.GanttComposite;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttEventListenerAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.mwc.debrief.timebar.model.IEventEntry;

import MWC.GUI.Editable;



public class NebulaGanttPainter implements ITimeBarsPainter 
{
	GanttChart _chart;
	Map<GanttEvent, IEventEntry> _eventEntries = new HashMap<GanttEvent, IEventEntry>();	
	List<ITimeBarsPainterListener> _listeners = new ArrayList<ITimeBarsPainterListener>();
	
	public NebulaGanttPainter(Composite parent)
	{
		_chart = new GanttChart(parent, SWT.MULTI, new GanttChartSettings());
		
		_chart.getGanttComposite().addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {}
			
			@Override
			public void mouseDown(MouseEvent e) {}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) 
			{
				Date clickedAt = _chart.getGanttComposite().getDateAt(e.x).getTime();
				chartDoubleClicked(clickedAt);				
			}
		});
		
		_chart.addGanttEventListener(new GanttEventListenerAdapter()
    	{
    		@Override
    		@SuppressWarnings("rawtypes")
    		public void eventSelected(GanttEvent event, List allSelectedEvents,
    				MouseEvent me) 
    		{
    			super.eventSelected(event, allSelectedEvents, me);
    			IEventEntry eventEntry = findEventEntry(event);
    			if (eventEntry != null)
    				eventIsSelected(eventEntry.getSource());
    		}  
    		
    		@Override
    		public void eventDoubleClicked(GanttEvent event, MouseEvent me) 
    		{    			
    			super.eventDoubleClicked(event, me);
    			IEventEntry eventEntry = findEventEntry(event);
    			if (eventEntry != null)
    				eventIsDoubleClicked(eventEntry.getSource());	
    		}
    	});
	
	}
	
	@Override
	public void drawBar(IEventEntry modelEntry) 
	{
		GanttEvent evt = new GanttEvent(_chart, modelEntry.getName(), 
				modelEntry.getStart(), modelEntry.getEnd(), 0);
		if (modelEntry.getColor() !=null)
			evt.setStatusColor(modelEntry.getColor());
		_eventEntries.put(evt, modelEntry);		
	}

	@Override
	public void drawSpot(IEventEntry modelEntry) 
	{		
		GanttEvent evt = new GanttCheckpoint(_chart, modelEntry.getName(), modelEntry.getStart());	
		if (modelEntry.getColor() !=null)
			evt.setStatusColor(modelEntry.getColor());
		_eventEntries.put(evt, modelEntry);
	}

	@Override
	public void jumpToBegin() 
	{
		_chart.getGanttComposite().jumpToEarliestEvent();		
	}

	@Override
	public void clear() 
	{
		_chart.getGanttComposite().clearChart();		
	}

	@Override
	public void selectTimeBar(Editable editable) 
	{
		for (Map.Entry<GanttEvent, IEventEntry> entry: _eventEntries.entrySet())
		{
			if (entry.getValue().getSource().equals(editable))
			{
				_chart.getGanttComposite().jumpToEvent(entry.getKey(), true, SWT.LEFT);
				_chart.getGanttComposite().setSelection(entry.getKey());
				return;
			}
		}	
		
	}

	@Override
	public void setFocus() 
	{
		_chart.setFocus();		
	}
	
	public void zoomIn()
	{
		_chart.getGanttComposite().zoomIn();
	}
	
	@Override
	public void zoomOut()
	{
		_chart.getGanttComposite().zoomOut();
	}

	@Override
	public void drawDebriefTime(Date oldTime, Date currTime) 
	{
		GanttComposite parent = _chart.getGanttComposite();
		
		int curX = parent.getXForDate(currTime);
	    if (curX == -1) 
	    	return; 
	    
	    int oldX = parent.getXForDate(oldTime);
	    if (oldX != curX)
	    {
	    	//TODO: redraw marker trace
	    	//parent.redraw(oldX, 0, oldX, _chart.getBounds().height, false);	
	    }
	
	    GC gc = new GC(parent);
	    gc.setLineStyle(SWT.LINE_SOLID);
	    gc.setLineWidth(2);
	    gc.setForeground(ITimeBarsPainter.TIME_LINE_COLOR);
	    gc.drawLine(curX, 0, curX, _chart.getBounds().height);
	    gc.dispose();    
	   
	}

	@Override
	public void addListener(ITimeBarsPainterListener listener) 
	{
		if (!_listeners.contains(listener))		
			_listeners.add(listener);		
	}

	@Override
	public void removeListener(ITimeBarsPainterListener listener) 
	{
		_listeners.remove(listener);		
	}	
	
	public void chartDoubleClicked(final Date clickedAt)
	{
		for (final ITimeBarsPainterListener l: _listeners) {
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.chartDoubleClicked(clickedAt);
                }
            });
		}
	}
	
	public void eventIsDoubleClicked(final Object eventEntry)
	{
		for (final ITimeBarsPainterListener l: _listeners) {
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.eventDoubleClicked(eventEntry);
                }
            });
		}
	}
	
	public void eventIsSelected(final Object eventEntry)
	{
		for (final ITimeBarsPainterListener l: _listeners) {
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.eventSelected(eventEntry);
                }
            });
		}
	}
	
	private IEventEntry findEventEntry(GanttEvent event)
	{
		for (Map.Entry<GanttEvent, IEventEntry> entry: _eventEntries.entrySet())
		{
			if (entry.getKey().equals(event))
			{
				return entry.getValue();
			}
		}
		return null;
	}

}



class GanttChartSettings extends DefaultSettings
{
	//TODO: tweak tooltips
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