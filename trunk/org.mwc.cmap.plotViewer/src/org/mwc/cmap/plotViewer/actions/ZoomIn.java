/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
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
	
	public static class ZoomInMode extends SWTChart.PlotMouseDragger
	{
		Point _startPoint;

		SWTCanvas _myCanvas;

		private PlainChart _myChart;

		private Rectangle res;

		private int JITTER;

		private Layers layers;
		
		private boolean dragResult;

		private KeyListener listener = new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.keyCode == SWT.ESC) {
					dragResult = false;
				}
			}
			
		};

		@Override
		public void doMouseDrag(final Point pt, final int JITTER,
				final Layers theLayers, SWTCanvas theCanvas)
		{
			// redraw canvas
			_myCanvas.getCanvas().redraw();
			Display.getCurrent().update();
			// just do a check that we have our start point (it may have been cleared
			// at the end of the move operation)
			if (_startPoint != null)
			{
				int deltaX = _startPoint.x - pt.x;
				int deltaY = _startPoint.y - pt.y;

				this.JITTER = JITTER;
				this.layers = theLayers;
				Rectangle rect = new Rectangle(_startPoint.x, _startPoint.y, deltaX,
						deltaY);
				res = rect;
				GC gc = new GC(_myCanvas.getCanvas());
				Color fc = new Color(Display.getDefault(), 255, 255, 255);
				gc.setForeground(fc);
				gc.setXORMode(true);
				gc.setLineAttributes(new LineAttributes(2, SWT.CAP_FLAT, SWT.JOIN_MITER, SWT.LINE_SOLID, null, 0, 10));
				gc.drawRectangle(rect);
				fc.dispose();
				gc.dispose();
			}

		}

		@Override
		public void doMouseUp(Point point, int keyState)
		{
			run();
			_myCanvas.getCanvas().removeKeyListener(listener);
			_myCanvas.getCanvas().redraw();
			Display.getCurrent().update();
			_myChart = null;
			_myCanvas = null;
			_startPoint = null;
		}

		@Override
		public void mouseDown(Point point, SWTCanvas canvas, PlainChart theChart)
		{
			_startPoint = point;
			_myCanvas = canvas;
			_myChart = theChart;
			_myCanvas.getCanvas().addKeyListener(listener);
			dragResult = true;
		}

		private void run() {
			if (dragResult) {
				// get world area
				java.awt.Point tl = new java.awt.Point(res.x, res.y);
				java.awt.Point br = new java.awt.Point(res.x + res.width, res.y
						+ res.height);
				if (res.width > JITTER || res.height > JITTER) {
					WorldLocation locA = new WorldLocation(_myCanvas
							.getProjection().toWorld(tl));
					WorldLocation locB = new WorldLocation(_myCanvas
							.getProjection().toWorld(br));
					WorldArea area = new WorldArea(locA, locB);

					WorldArea oldArea = _myCanvas.getProjection().getDataArea();
					Action theAction = new MWC.GUI.Tools.Chart.ZoomIn.ZoomInAction(
							_myChart, oldArea, area);
					// and wrap it
					DebriefActionWrapper daw = new DebriefActionWrapper(
							theAction, layers, null);
					// and add it to the clipboard
					CorePlugin.run(daw);
				}
			}
		}

	}

	@Override
	public PlotMouseDragger getDragMode()
	{
		return new ZoomInMode();
	}
}