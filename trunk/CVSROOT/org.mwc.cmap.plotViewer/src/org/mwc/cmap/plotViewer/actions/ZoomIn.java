/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.editors.chart.*;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import MWC.GUI.*;
import MWC.GUI.Tools.Action;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
public class ZoomIn extends CoreDragAction
{

	public static class ZoomInMode extends SWTChart.PlotMouseDragger
	{
		Point _startPoint;

		SWTCanvas _myCanvas;

		private PlainChart _myChart;

		public void doMouseDrag(final Point pt, final int JITTER, final Layers theLayers, SWTCanvas theCanvas)
		{
			// just do a check that we have our start point (it may have been cleared at the end of the move operation)
			if (_startPoint != null)
			{
				int deltaX = _startPoint.x - pt.x;
				int deltaY = _startPoint.y - pt.y;
				if (Math.abs(deltaX) < JITTER && Math.abs(deltaY) < JITTER)
					return;
				Tracker _dragTracker = new Tracker((Composite) _myCanvas.getCanvas(), SWT.RESIZE);
				Rectangle rect = new Rectangle(_startPoint.x, _startPoint.y, deltaX, deltaY);
				_dragTracker.setRectangles(new Rectangle[] { rect });
				boolean dragResult = _dragTracker.open();
				if (dragResult)
				{
					Rectangle[] rects = _dragTracker.getRectangles();
					Rectangle res = rects[0];
					// get world area
					java.awt.Point tl = new java.awt.Point(res.x, res.y);
					java.awt.Point br = new java.awt.Point(res.x + res.width, res.y + res.height);
					WorldLocation locA = new WorldLocation(_myCanvas.getProjection().toWorld(tl));
					WorldLocation locB = new WorldLocation(_myCanvas.getProjection().toWorld(br));
					WorldArea area = new WorldArea(locA, locB);

					WorldArea oldArea = _myCanvas.getProjection().getDataArea();
					Action theAction = 	new MWC.GUI.Tools.Chart.ZoomIn.ZoomInAction(_myChart, oldArea, area);
					
					// and wrap it
					DebriefActionWrapper daw = new DebriefActionWrapper(theAction, null);
					
					// and add it to the clipboard
					CorePlugin.run(daw);

					_dragTracker = null;
					_startPoint = null;
					_myChart = null;
					_myCanvas = null;
				}
			}
		}

		public void doMouseUp(Point point, int keyState)
		{
			_startPoint = null;
		}

		public void mouseDown(Point point, SWTCanvas canvas, PlainChart theChart)
		{
			_startPoint = point;
			_myCanvas = canvas;
			_myChart = theChart;
		}

	}

	public PlotMouseDragger getDragMode()
	{
		// TODO Auto-generated method stub
		return new ZoomInMode();
	}
}