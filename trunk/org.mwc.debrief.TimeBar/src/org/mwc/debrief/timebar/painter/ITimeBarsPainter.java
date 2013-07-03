package org.mwc.debrief.timebar.painter;

import java.util.Date;

import org.eclipse.swt.graphics.Color;
import org.mwc.debrief.timebar.model.IEventEntry;

import MWC.GUI.Editable;

public interface ITimeBarsPainter 
{
	final static Color TIME_LINE_COLOR = new Color(null, 0, 255, 0);
	
	public void drawBar(IEventEntry modelEntry);
	
	public void drawSpot(IEventEntry modelEntry);
	
	/**
	 * Draws a vertical line corresponding to the current Debrief date
	 * @param oldTime - the time to erase
	 */
	public void drawDebriefTime(Date oldTime, Date currTime);
	
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
