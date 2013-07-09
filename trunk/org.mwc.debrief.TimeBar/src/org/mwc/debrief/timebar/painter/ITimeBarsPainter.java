package org.mwc.debrief.timebar.painter;

import java.beans.PropertyChangeListener;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.mwc.debrief.timebar.model.IEventEntry;

import MWC.GUI.Editable;

public interface ITimeBarsPainter extends PropertyChangeListener
{
	final static Color TIME_LINE_COLOR = new Color(null, 0, 255, 0);
	final static int TIME_LINE_WIDTH = 3;
	final static int TIME_LINE_STYLE = SWT.LINE_SOLID;
	
	public void drawBar(IEventEntry modelEntry);
	
	public void drawSpot(IEventEntry modelEntry);
	
	/**
	 * Draws a vertical line corresponding to the current Debrief date.
	 * @param oldTime - the time to erase
	 */
	public void drawDebriefTime(Date oldTime, Date currTime);
	
	public void selectTimeBar(Editable editable);

	/**
	 * Move chart start date to the earliest event.
	 */
	public void jumpToBegin();
	
	/**
	 * Clears the diagram.
	 */
	public void clear();
	
	public void setFocus();
	
	public void zoomIn();
	
	public void zoomOut();
	
	/**
	 *  Zooms out to show all of the available data, 
	 *  in the highest possible zoom.
	 */
	public void fitToWindow();
	
	/**
	 *  Zooms in to show all of the available data, 
	 *  in the highest possible zoom.
	 */
	public void fitToSize();
	
	public void addListener(ITimeBarsPainterListener listener);
	
	public void removeListener(ITimeBarsPainterListener listener);
}
