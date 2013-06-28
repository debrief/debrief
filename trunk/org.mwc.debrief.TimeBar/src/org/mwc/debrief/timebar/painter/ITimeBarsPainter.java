package org.mwc.debrief.timebar.painter;

import org.mwc.debrief.timebar.model.IEventEntry;

import MWC.GUI.Editable;

public interface ITimeBarsPainter 
{
	
	public void drawBar(IEventEntry modelEntry);
	
	public void drawSpot(IEventEntry modelEntry);
	
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
	

}
