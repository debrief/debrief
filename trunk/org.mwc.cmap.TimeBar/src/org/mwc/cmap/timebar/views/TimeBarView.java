package org.mwc.cmap.timebar.views;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TimeBarView extends ViewPart {
	
	@Override
	public void createPartControl(Composite parent) 
	{
		GanttChart ganttChart = new GanttChart(parent, SWT.MULTI);
		Calendar start = GregorianCalendar.getInstance();
		Calendar end = GregorianCalendar.getInstance();
		end.add(Calendar.DATE, 5);
		new GanttEvent(ganttChart, null, "Event_1", start, end, start, end, 0);
		start = GregorianCalendar.getInstance();
		end = GregorianCalendar.getInstance();
		start.add(Calendar.DATE, 6);
		end.add(Calendar.DATE, 8);
		new GanttEvent(ganttChart, null, "Event_2", start, end, start, end, 0);		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
