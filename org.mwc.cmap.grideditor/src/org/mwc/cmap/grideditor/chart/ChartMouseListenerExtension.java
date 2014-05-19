package org.mwc.cmap.grideditor.chart;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;

public interface ChartMouseListenerExtension extends ChartMouseListener {

	/**
	 * Notified by chart when mouseUp even is broadcasted by SWT. Allows to
	 * handle, e.g, dragging the items on chart.
	 * 
	 * Listener is allowed to call event.getTrigger().consume() in order to veto
	 * the default processing of the mouseUp.
	 */
	public void chartMouseReleased(ChartMouseEvent event);

}
