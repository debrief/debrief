package org.mwc.debrief.timebar.painter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.nebula.widgets.ganttchart.DefaultSettings;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttCheckpoint;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttEventListenerAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPageLayout;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.timebar.model.IEventEntry;
import org.mwc.debrief.timebar.views.TimeBarViewer;

import MWC.GUI.Editable;



public class NebulaGanttPainter implements ITimeBarsPainter 
{
	GanttChart _chart;
	TimeBarViewer _viewer;
	Map<GanttEvent, IEventEntry> eventEntries = new HashMap<GanttEvent, IEventEntry>();
	
	public NebulaGanttPainter(Composite parent, final TimeBarViewer viewer)
	{
		_viewer = viewer;
		_chart = new GanttChart(parent, SWT.MULTI, new GanttChartSettings());
		
		_chart.addGanttEventListener(new GanttEventListenerAdapter()
    	{
    		@Override
    		@SuppressWarnings("rawtypes")
    		public void eventSelected(GanttEvent event, List allSelectedEvents,
    				MouseEvent me) 
    		{
    			super.eventSelected(event, allSelectedEvents, me);
    			for (Map.Entry<GanttEvent, IEventEntry> entry: eventEntries.entrySet())
    			{
	    			if (entry.getKey().equals(event))
					{
						_viewer.setSelectionToObject(entry.getValue().getSource());
						return;
					}
    			}				
    		}  
    		
    		@Override
    		public void eventDoubleClicked(GanttEvent event, MouseEvent me) 
    		{
    			super.eventDoubleClicked(event, me);
    			CorePlugin.openView(CorePlugin.LAYER_MANAGER);
				CorePlugin.openView(IPageLayout.ID_PROP_SHEET);	
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
		eventEntries.put(evt, modelEntry);		
	}

	@Override
	public void drawSpot(IEventEntry modelEntry) 
	{
		GanttEvent evt = new GanttCheckpoint(_chart, modelEntry.getName(), modelEntry.getStart());	
		if (modelEntry.getColor() !=null)
			evt.setStatusColor(modelEntry.getColor());
		eventEntries.put(evt, modelEntry);
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
		for (Map.Entry<GanttEvent, IEventEntry> entry: eventEntries.entrySet())
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