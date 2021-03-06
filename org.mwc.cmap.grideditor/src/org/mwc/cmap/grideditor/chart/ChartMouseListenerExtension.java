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

package org.mwc.cmap.grideditor.chart;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;

public interface ChartMouseListenerExtension extends ChartMouseListener {

	/**
	 * Notified by chart when mouseUp even is broadcasted by SWT. Allows to handle,
	 * e.g, dragging the items on chart.
	 *
	 * Listener is allowed to call event.getTrigger().consume() in order to veto the
	 * default processing of the mouseUp.
	 */
	public void chartMouseReleased(ChartMouseEvent event);

}
