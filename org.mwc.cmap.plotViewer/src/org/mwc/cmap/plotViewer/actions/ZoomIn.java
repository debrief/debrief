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

package org.mwc.cmap.plotViewer.actions;

import java.awt.Dimension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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

		final private KeyListener listener = new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.keyCode == SWT.ESC) {
					dragResult = false;
				}
			}
		};
		
    final private PaintListener paintListener = new PaintListener()
    {
      @SuppressWarnings("deprecation")
      public void paintControl(PaintEvent e)
      {
        //check do we have to print rect 
        if(res==null)
          return;
        
        final GC gc = e.gc;
        final Color fc = new Color(Display.getDefault(), 155, 155, 155);
        gc.setForeground(fc);
        gc.setXORMode(true);
        gc.setLineAttributes(new LineAttributes(2, SWT.CAP_FLAT, SWT.JOIN_MITER,
            SWT.LINE_SOLID, null, 0, 10));
        gc.drawRectangle(res);
        gc.setXORMode(false);
        fc.dispose();
      }
    };

		@Override
		public void doMouseDrag(final Point pt, final int JITTER,
				final Layers theLayers, final SWTCanvas theCanvas)
		{
			
			
			// just do a check that we have our start point (it may have been cleared
			// at the end of the move operation)
			if (_startPoint != null)
			{
				final int deltaX = _startPoint.x - pt.x;
				final int deltaY = _startPoint.y - pt.y;

				this.JITTER = JITTER;
				this.layers = theLayers;
				final Rectangle rect = new Rectangle(_startPoint.x, _startPoint.y, -deltaX,
						-deltaY);
				res = rect;
			}
			if(_myCanvas!=null) {
  			_myCanvas.getCanvas().redraw();
  			_myCanvas.getCanvas().update();
			}
		}

		@Override
		public void doMouseUp(Point point, int keyState)
		{
			run();
			_myCanvas.getCanvas().removeKeyListener(listener);
			_myCanvas.getCanvas().removePaintListener(paintListener);
		
			
			res = null;
      _myCanvas.getCanvas().redraw();
      _myCanvas.getCanvas().update();
      
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
			_myCanvas.getCanvas().addPaintListener(paintListener);
			dragResult = true;
		}

		private void run() {
			if (dragResult) {
				// get world area
				java.awt.Point tl = new java.awt.Point(res.x, res.y);
				java.awt.Point br = new java.awt.Point(res.x + res.width, res.y
						+ res.height);

				if (Math.abs(res.width) > JITTER || Math.abs(res.height) > JITTER)
				{

					WorldLocation locA = new WorldLocation(_myCanvas.getProjection()
							.toWorld(tl));
					WorldLocation locB = new WorldLocation(_myCanvas.getProjection()
							.toWorld(br));
					WorldArea area = new WorldArea(locA, locB);

					WorldArea oldArea = _myCanvas.getProjection().getDataArea();
					Action theAction = null;

					// find where the cursor currently is (in absolute coords, not delta coords)
					Point finalPos = Display.getCurrent().getCursorLocation();

					// the finalPos we're retrieving is in screen coords, not the coords for this panel.
					// so, get the display to give us the co-ords for inside the canvas
					final Point  mappedFinal = Display.getCurrent().map(null, _myCanvas.getCanvas(), finalPos);

					// ok, now consider the overall drag operation, just in case it started with BR->TL, but
					// ended up with TL->BR.
					final int overallX = mappedFinal.x - _startPoint.x;
					final int overallY = mappedFinal.y - _startPoint.y;

					// if the drag was from TL to BR
					if (overallX >= 0 || overallY >= 0)
					{
						// then zoom in
						theAction = new MWC.GUI.Tools.Chart.ZoomIn.ZoomInAction(_myChart,
								oldArea, area);
					}
					// if the drag was from BR to TL
					else
					{
						final Dimension screenSize = _myCanvas.getSize();

						// now, we have to root the scale, since the ZoomOutAction is expecting
						// a 'length', not an 'area'.
						final double scale = Math.sqrt((screenSize.height*screenSize.width)
								/ (res.height*res.width));
						theAction = new MWC.GUI.Tools.Chart.ZoomOut.ZoomOutAreaAction(
								_myChart, oldArea, area, scale);
					}

					// and wrap it
					DebriefActionWrapper daw = new DebriefActionWrapper(theAction,
							layers, null);

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