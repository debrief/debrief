/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.swt.graphics.Point;
import org.mwc.cmap.plotViewer.editors.chart.*;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import MWC.GUI.Layers;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
public class RangeBearing extends CoreDragAction
{
	
	public static class RangeBearingMode extends SWTChart.PlotMouseDragger
	{
		WorldLocation _startLocation;
		SWTCanvas _myCanvas;
		
		public void doMouseMove(Point pt, int JITTER, Layers theLayers)
		{
			// convert to world locations
			java.awt.Point newPoint = new java.awt.Point(pt.x, pt.y);
			WorldLocation newEnd = _myCanvas.getProjection().toWorld(newPoint);
			
			WorldVector sep = newEnd.subtract(_startLocation);
			
			// draw in the new line
			
			double theRange = sep.getRange();
			double theBrg = sep.getBearing();
			
			String msg = "Rng:" + theRange + " Brg:" + theBrg;
			System.out.println(msg);
		}

		public void doMouseUp(Point point)
		{
			_startLocation = null;
		}

		public void mouseDown(Point point, SWTCanvas canvas)
		{
			_myCanvas = canvas;
			_startLocation = new WorldLocation(_myCanvas.getProjection().toWorld(new java.awt.Point(point.x, point.y)));
		}
		
	}

	public PlotMouseDragger getDragMode()
	{
		return new RangeBearingMode();
	}
}