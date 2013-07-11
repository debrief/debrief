/**
 * 
 */
package org.mwc.debrief.timebar.model;

import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.graphics.Color;

public interface IEventEntry 
{
	public boolean isVisible();
	
	public Object getSource();
	
	public Calendar getStart();
	
	public Calendar getEnd();
	
	public String getName();
	
	public Color getColor();
	
	/**
	 * Returns series of events, for example narrative entries for a narrative wrapper.
	 */
	public List<IEventEntry> getChildren();
	
}
