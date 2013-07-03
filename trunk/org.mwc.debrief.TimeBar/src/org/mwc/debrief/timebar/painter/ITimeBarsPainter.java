package org.mwc.debrief.timebar.painter;

import java.util.Date;

import org.mwc.debrief.timebar.model.IEventEntry;

import MWC.GUI.Editable;

public interface ITimeBarsPainter 
{
	
	public void drawBar(IEventEntry modelEntry);
	
	public void drawSpot(IEventEntry modelEntry);
	
	/**
	 * Draws a vertical line corresponding to the current Debrief date
	 * @param date
	 */
	public void drawDebriefTime(Date date);
	
	public void selectTimeBar(Editable editable);

	/**
	 * Move chart start date to the earliest event
	 */
	public void jumpToBegin();
	
	/**
	 * Clears the diagram
	 */
	public void clear();
	
	public void setFocus();
	
	public void zoomIn();
	
	public void zoomOut();
}
