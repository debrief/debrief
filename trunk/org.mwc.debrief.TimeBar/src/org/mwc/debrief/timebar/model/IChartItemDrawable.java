/**
 * 
 */
package org.mwc.debrief.timebar.model;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;

public interface IChartItemDrawable 
{
	public void draw(GanttChart chart);
	
	public Object getSource();
	
	public GanttEvent getPresentation();
}
