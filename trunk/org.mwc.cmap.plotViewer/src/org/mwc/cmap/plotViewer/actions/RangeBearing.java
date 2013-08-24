/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.plotViewer.editors.chart.CoreTracker;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import MWC.Algorithms.Conversions;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * @author ian.mayo
 */
final public class RangeBearing extends CoreDragAction
{
	
	/**
	 * embedded class that handles the range/bearing measurement
	 * 
	 * @author Ian
	 */
	final public static class RangeBearingMode extends SWTChart.PlotMouseDragger
	{
		/**
		 * the start point, in world coordinates (so we don't have to calculate it
		 * as often)
		 */
		WorldLocation _startLocation;

		/**
		 * the start point, in screen coordinates - where we started our drag
		 */
		Point _startPoint;

		/**
		 * the last rectangle drawn, so we can erase it on the next update
		 */
		Rectangle _lastRect;

		/**
		 * the canvas we're updating..
		 */
		SWTCanvas _myCanvas;

		@SuppressWarnings("deprecation")
		final public void doMouseDrag(final Point pt, final int JITTER, final Layers theLayers,
				final SWTCanvas theCanvas)
		{
			if (_startPoint != null)
			{
				final GC gc = new GC(_myCanvas.getCanvas());

				// This is the same as a !XOR
				gc.setXORMode(true);
				gc.setForeground(gc.getBackground());

				// Erase existing rectangle
				if (_lastRect != null)
					plotUpdate(gc);

				final int dx = pt.x - _startPoint.x;
				final int dy = pt.y - _startPoint.y;

				// Draw selection rectangle
				_lastRect = new Rectangle(_startPoint.x, _startPoint.y, dx, dy);

				try
				{
					// update the range/bearing text
					plotUpdate(gc);
				} 
				catch(final Exception e)
				{	
					e.printStackTrace();
				}
				gc.dispose();

			} else
			{
				// System.out.println("no point.");
			}

		}

		@SuppressWarnings("deprecation")
		final public void doMouseUp(final Point point, final int keyState)
		{
			final GC gc = new GC(_myCanvas.getCanvas());

			// This is the same as a !XOR
			gc.setXORMode(true);
			gc.setForeground(gc.getBackground());

			// Erase existing rectangle
			if (_lastRect != null)
			{
				// hmm, we've finished plotting. see if the ctrl button is
				// down
				if ((keyState & SWT.CTRL) == 0)
					try
					{
						plotUpdate(gc);
					} 
					catch(final Exception e)
					{	
						e.printStackTrace();
					}
					gc.dispose();
			}

			_startPoint = null;
			_lastRect = null;
			_myCanvas = null;
			_startLocation = null;
		}

		final public void mouseDown(final Point point, final SWTCanvas canvas,
				final PlainChart theChart)
		{
			_startPoint = point;
			_myCanvas = canvas;
			_startLocation = new WorldLocation(_myCanvas.getProjection().toWorld(
					new java.awt.Point(point.x, point.y)));
		}

		final private void plotUpdate(final GC dest)
		{

			final java.awt.Point endPoint = new java.awt.Point(_lastRect.x
					+ _lastRect.width, _lastRect.y + _lastRect.height);

			dest.setForeground(new Color(Display.getDefault(), 111, 111, 111));
			dest.setLineWidth(2);
			dest.drawLine(_lastRect.x, _lastRect.y, _lastRect.x + _lastRect.width,
					_lastRect.y + _lastRect.height);

			// also put in a text-label
			final WorldLocation endLocation = _myCanvas.getProjection().toWorld(endPoint);
			final WorldVector sep = endLocation.subtract(_startLocation);

			final String myUnits = CorePlugin.getToolParent().getProperty(
					MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY);

			final double rng = Conversions.convertRange(sep.getRange(), myUnits);
			double brg = sep.getBearing();
			brg = brg * 180 / Math.PI;
			if (brg < 0)
				brg += 360;
			final DecimalFormat df = new DecimalFormat("0.00");
			final String numComponent = df.format(rng);
			final String txt = "[" + numComponent + myUnits + " " + (int) brg + "\u00b0"  + "]";

			// decide the mid-point
			final java.awt.Point loc = new java.awt.Point(
					_lastRect.x + _lastRect.width / 2, _lastRect.y + _lastRect.height / 2);

			// find out how big the text is
			final FontMetrics fm = dest.getFontMetrics();

			loc.translate(0, fm.getHeight() / 2);
			loc.translate(-txt.length() / 2 * fm.getAverageCharWidth(), 0);

			// ok, do the write operation
			dest.setForeground(new Color(Display.getDefault(), 200, 200, 200));
			dest.drawText(txt, loc.x, loc.y, SWT.DRAW_TRANSPARENT);
			
			// also get the RangeTracker to display the range/bearing
			CoreTracker.write(txt);

		}
	}

	public PlotMouseDragger getDragMode()
	{
		return new RangeBearingMode();
	}


}