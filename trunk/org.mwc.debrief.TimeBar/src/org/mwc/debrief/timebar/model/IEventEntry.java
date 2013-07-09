/**
 * 
 */
package org.mwc.debrief.timebar.model;

import java.util.Calendar;

import org.eclipse.swt.graphics.Color;

public interface IEventEntry 
{
	public boolean isVisible();
	
	public Object getSource();
	
	public Calendar getStart();
	
	public Calendar getEnd();
	
	public String getName();
	
	public Color getColor();
	
	public boolean isBoldText();
	
}
