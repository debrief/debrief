/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.timebar.painter;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.mwc.debrief.timebar.model.IEventEntry;

import MWC.GUI.Editable;

public interface ITimeBarsPainter {
	final static Color TIME_LINE_COLOR = new Color(null, 0, 255, 0);
	final static int TIME_LINE_WIDTH = 3;
	final static int TIME_LINE_STYLE = SWT.LINE_SOLID;

	public void addListener(ITimeBarsPainterListener listener);

	/**
	 * Clears the diagram.
	 */
	public void clear();

	public void drawBar(IEventEntry modelEntry);

	/**
	 * Draws a vertical line corresponding to the current Debrief date.
	 *
	 * @param oldTime - the time to erase
	 */
	public void drawDebriefTime(Date oldTime, Date currTime);

	public void drawSpot(IEventEntry modelEntry);

	/**
	 * Zooms in/out to show all of the available data, in the highest possible zoom.
	 */
	public void fitToWindow();

	public boolean isDisposed();

	/**
	 * Move chart start date to the earliest event.
	 */
	public void jumpToBegin();

	public void removeListener(ITimeBarsPainterListener listener);

	public void selectTimeBar(Editable editable);

	public void setFocus();

	public void zoomIn();

	public void zoomOut();
}
