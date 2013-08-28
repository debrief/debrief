/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tracker;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Tools.Action;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * @author ian.mayo
 */
public class ZoomIn extends CoreDragAction
{
	// TODO: since this mode does both zoom in and zoom out 
	// depending on the drag rectangle location,
	// maybe we should rename it?
	public static class ZoomInMode extends SWTChart.PlotMouseDragger
	{
		Point _startPoint;

		SWTCanvas _myCanvas;

		private PlainChart _myChart;

		@Override
		public void doMouseDrag(final Point pt, final int JITTER,
				final Layers theLayers, final SWTCanvas theCanvas)
		{
			// just do a check that we have our start point (it may have been cleared
			// at the end of the move operation)
			if (_startPoint != null)
			{
				int deltaX = _startPoint.x - pt.x;
				int deltaY = _startPoint.y - pt.y;

				Tracker _dragTracker = new Tracker((Composite) _myCanvas.getCanvas(),
						SWT.RESIZE);
				final Rectangle rect = new Rectangle(_startPoint.x, _startPoint.y, deltaX,
						deltaY);
				_dragTracker.setRectangles(new Rectangle[] { rect });
				final boolean dragResult = _dragTracker.open();
				if (dragResult)
				{
					final Rectangle[] rects = _dragTracker.getRectangles();
					final Rectangle res = rects[0];
					// get world area
					final java.awt.Point tl = new java.awt.Point(res.x, res.y);
					final java.awt.Point br = new java.awt.Point(res.x + res.width, res.y
							+ res.height);

					if (res.width > JITTER || res.height > JITTER)
					{

						final WorldLocation locA = new WorldLocation(_myCanvas.getProjection()
								.toWorld(tl));
						final WorldLocation locB = new WorldLocation(_myCanvas.getProjection()
								.toWorld(br));
						final WorldArea area = new WorldArea(locA, locB);

						final WorldArea oldArea = _myCanvas.getProjection().getDataArea();
						Action theAction = null;
						// if the drag was from TL to BR
						if (deltaX <= 0 && deltaY <=0 )
						{
							// then zoom in
							theAction = new MWC.GUI.Tools.Chart.ZoomIn.ZoomInAction(
									_myChart, oldArea, area);
						}
						// if the drag was from BR to TL
						else 
						{
							// then zoom out
							if (deltaX == 0)
								deltaX = 1;
							if (deltaY == 0)
								deltaY = 1;
							final double scale = deltaX*deltaY;
							theAction = new MWC.GUI.Tools.Chart.ZoomOut.ZoomOutAction(
									_myChart, oldArea, scale);
						}

						// and wrap it
						final DebriefActionWrapper daw = new DebriefActionWrapper(theAction,
								theLayers, null);

						// and add it to the clipboard
						CorePlugin.run(daw);

					}

					_dragTracker = null;
					_startPoint = null;
					_myChart = null;
					_myCanvas = null;
				}
			}
		}

		@Override
		public void doMouseUp(final Point point, final int keyState)
		{
			_startPoint = null;
		}

		@Override
		public void mouseDown(final Point point, final SWTCanvas canvas, final PlainChart theChart)
		{
			_startPoint = point;
			_myCanvas = canvas;
			_myChart = theChart;
		}

	}

	@Override
	public PlotMouseDragger getDragMode()
	{
		// TODO Auto-generated method stub
		return new ZoomInMode();
	}
}