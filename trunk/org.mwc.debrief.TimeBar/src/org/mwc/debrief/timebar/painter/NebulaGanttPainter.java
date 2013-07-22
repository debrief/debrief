package org.mwc.debrief.timebar.painter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.nebula.widgets.ganttchart.AbstractPaintManager;
import org.eclipse.nebula.widgets.ganttchart.AdvancedTooltip;
import org.eclipse.nebula.widgets.ganttchart.DefaultSettings;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttComposite;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttEventListenerAdapter;
import org.eclipse.nebula.widgets.ganttchart.GanttGroup;
import org.eclipse.nebula.widgets.ganttchart.GanttImage;
import org.eclipse.nebula.widgets.ganttchart.IColorManager;
import org.eclipse.nebula.widgets.ganttchart.ISettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.mwc.debrief.timebar.Activator;
import org.mwc.debrief.timebar.model.IEventEntry;
import org.mwc.debrief.timebar.model.TimeSpot;

import MWC.GUI.Editable;



public class NebulaGanttPainter implements ITimeBarsPainter, PropertyChangeListener
{
	GanttChart _chart;
	Map<IEventEntry, GanttEvent> _eventEntries = new HashMap<IEventEntry, GanttEvent>();	
	List<ITimeBarsPainterListener> _listeners = new ArrayList<ITimeBarsPainterListener>();
	GanttEvent _earliestEvent = null;
	GanttEvent _latestEvent = null;
	
	public NebulaGanttPainter(Composite parent)
	{
		_chart = new GanttChart(parent, SWT.MULTI, new GanttChartSettings(),
				null /* color manager */, 
				new GanttPaintManager(), null /* language manager */);		
		
		_chart.getGanttComposite().addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {}
			
			@Override
			public void mouseDown(MouseEvent e) {}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) 
			{
				//TODO: do not do anything if an event was double-clicked
				Date clickedAt = _chart.getGanttComposite().getDateAt(e.x).getTime();
				if (_earliestEvent.getStartDate().getTime().compareTo(clickedAt) > 0)
				{
					// it is too early
					return;
				}
				if (_latestEvent.getEndDate().getTime().compareTo(clickedAt) < 0)
				{
					//it is too late
					return;
				}
					
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
		if (!modelEntry.isVisible())
			return;
			
		GanttEvent evt = new GanttEvent(_chart, modelEntry.getName(), 
				modelEntry.getStart(), modelEntry.getEnd(), 0);
		if (modelEntry.getColor() !=null)
			evt.setStatusColor(modelEntry.getColor());
		
		if (modelEntry.getChildren().size() > 0)
		{
			evt.setScope(true);
			evt.setTextFont(new Font(null, "Arial", 12, SWT.BOLD ));
			evt.setStatusColor(modelEntry.getColor());
			GanttGroup group = new GanttGroup(_chart);
			for (IEventEntry entry: modelEntry.getChildren())
			{
				if (entry instanceof TimeSpot)
				{
					drawSpot(entry);					
					group.addEvent(_eventEntries.get(entry));
				}
				else
				{
					drawBar(entry);
				}
				evt.addScopeEvent(_eventEntries.get(entry));
			}
		}
		addEvent(evt, modelEntry);		
	}

	@Override
	public void drawSpot(IEventEntry modelEntry) 
	{		
		if (!modelEntry.isVisible())
			return;
		GanttImage evt = new GanttImage(_chart, "", modelEntry.getStart(),
				Activator.getImageDescriptor("icons/sample.gif").createImage());
		evt.setAdvancedTooltip(new AdvancedTooltip("", modelEntry.getToolTipText()));
		if (modelEntry.getColor() !=null)
			evt.setStatusColor(modelEntry.getColor());
		addEvent(evt, modelEntry);
	}
	
	private void addEvent(GanttEvent evt, IEventEntry modelEntry)
	{
		_eventEntries.put(modelEntry, evt);
		if (_earliestEvent == null)
		{
			_earliestEvent = evt;
		}
		else
		{
			if (evt.getStartDate().compareTo(_earliestEvent.getStartDate()) < 0)
			{
				_earliestEvent = evt;
			}
		}
		if(_latestEvent == null)
		{
			_latestEvent = evt;
		}
		else
		{
			if (evt.getEndDate().compareTo(_latestEvent.getEndDate()) > 0)
			{
				_latestEvent = evt;
			}
		}
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
		for (Map.Entry<IEventEntry, GanttEvent> entry: _eventEntries.entrySet())
		{
			if (entry.getKey().getSource().equals(editable))
			{
				_chart.getGanttComposite().jumpToEvent(entry.getValue(), true, SWT.LEFT);
				_chart.getGanttComposite().setSelection(entry.getValue());
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
		final GanttComposite parent = _chart.getGanttComposite();
		
		final int curX = parent.getXForDate(currTime);
	    if (curX == -1) 
	    	return;     
	
	    eraseDebriefTime(oldTime);
	    
	    GC gc = new GC(parent);
		gc.setLineStyle(ITimeBarsPainter.TIME_LINE_STYLE);
		gc.setLineWidth(ITimeBarsPainter.TIME_LINE_WIDTH);
		gc.setForeground(ITimeBarsPainter.TIME_LINE_COLOR);
		    
		gc.drawRectangle(curX, 0, 1, 
		    		parent.getClientArea().height);
		gc.dispose();    
	   
	}
	
	private void eraseDebriefTime(Date timeVal)
	{
		if (timeVal == null)
			return;
		
		final GanttComposite parent = _chart.getGanttComposite();
		final int oldX = parent.getXForDate(timeVal);	   
	    
	    Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{				 	
				 parent.redraw(oldX-1, 0, 1,
			    			parent.getClientArea().height, false);				 		  
			}
		});
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
		_chart.getGanttComposite().redraw();
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
		for (Map.Entry<IEventEntry, GanttEvent> entry: _eventEntries.entrySet())
		{
			if (entry.getValue().equals(event))
			{
				return entry.getKey();
			}
		}
		return null;
	}
	
	private GanttEvent findGanttEvent(Object source)
	{
		for (Map.Entry<IEventEntry, GanttEvent> entry: _eventEntries.entrySet())
		{
			if (entry.getKey().getSource().equals(source))
			{
				return entry.getValue();
			}
		}
		return null;
	}
	

	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{		
		GanttEvent event = findGanttEvent(evt.getSource());
		if (event != null)
		{
			if (new Boolean(true).equals(evt.getNewValue()))
			{
				_chart.getGanttComposite().addEvent(event);
			}
			else
			{
				_chart.getGanttComposite().removeEvent(event);				
			}
		}		
	}
	
	
	@Override
	public void fitToWindow() 
	{
		final GanttComposite composite = _chart.getGanttComposite();
		
       	Rectangle visibleBounds = composite.getBounds();
		int fullBounds_width = composite.getFullImage().getBounds().width;
		
		int idx = 0;
		if (fullBounds_width > visibleBounds.width)
		{
			while (fullBounds_width  > visibleBounds.width)
			{
				composite.zoomOut();
				idx ++;
				if (idx > 10)
					break;
				fullBounds_width = composite.getFullImage().getBounds().width;
			}
		}
		else
		{
			while(fullBounds_width < visibleBounds.width)
			{						
				composite.zoomIn();
				idx ++;
				if (idx > 10)
					break;
				fullBounds_width = composite.getFullImage().getBounds().width;
			}
			if (fullBounds_width > visibleBounds.width)
				composite.zoomOut();
		}
		composite.jumpToEarliestEvent();
				
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
	public String getDefaultAdvancedTooltipText() 
	{
		return "";
	}
	
	@Override
	public Locale getDefaultLocale()
	{
		return Locale.UK;
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
	
	@Override
	public boolean showToolTips() 
	{
		return true;
	}
	
}


class GanttPaintManager extends AbstractPaintManager
{
	@Override
	public void drawImage(GanttComposite ganttComposite, ISettings settings,
			IColorManager colorManager, GanttEvent event, GC gc, Image image,
			boolean threeDee, int dayWidth, int xLoc, int yStart,
			Rectangle fullBounds) {
		int y = yStart;
        int x = xLoc;

        // draw a cross in a box if image is null
        if (image == null) {
            gc.setForeground(colorManager.getBlack());
            gc.drawRectangle(x, y, dayWidth, settings.getEventHeight());
            gc.drawLine(x, y, x + dayWidth, y + settings.getEventHeight());
            gc.drawLine(x + dayWidth, y, x, y + settings.getEventHeight());
            return;
        }

        // can it fit?
        final Rectangle bounds = image.getBounds();
        if (settings.scaleImageToDayWidth() && bounds.width > dayWidth) {
            // shrink image
            ImageData id = image.getImageData();
            final int diff = id.width - dayWidth;
            id.width -= diff;
            id.height -= diff;
            final Image temp = new Image(Display.getDefault(), id);

            final int negY = (bounds.height - settings.getEventHeight());
            if (negY > 0) {
                y += negY / 2;
            }

            gc.drawImage(temp, x, y);
            temp.dispose();
            return;
        } else {
            // center it x-wise
        	x -= bounds.width/2;
            //x -= Math.abs(bounds.width - dayWidth) / 2;
        }

        gc.drawImage(image, x, y);

	}
}