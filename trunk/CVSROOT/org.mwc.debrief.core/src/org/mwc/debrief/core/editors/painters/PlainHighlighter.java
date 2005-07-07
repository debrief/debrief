/**
 * 
 */
package org.mwc.debrief.core.editors.painters;

import java.awt.*;

import Debrief.Tools.Tote.Watchable;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.*;
import MWC.GenericData.*;

/**
 * painter which plots all data, and draws a square rectangle around tactical
 * items at the current dtg
 * 
 * @author ian.mayo
 */
public class PlainHighlighter implements TemporalLayerPainter
{
	private static Color _myColor = Color.white;

	private static int _mySize = 5;

	public final void highlightIt(MWC.Algorithms.PlainProjection proj,
			CanvasType dest, Debrief.Tools.Tote.Watchable watch)
	{
		// check that our graphics context is still valid -
		// we can't, so we will just have to trap any exceptions it raises
		try
		{

			Rectangle _areaCovered = null;

			// set the highlight colour
			dest.setColor(_myColor);
			// get the current area of the watchable
			WorldArea wa = watch.getBounds();
			// convert to screen coordinates
			Point tl = proj.toScreen(wa.getTopLeft());

			int tlx = tl.x;
			int tly = tl.y;

			Point br = proj.toScreen(wa.getBottomRight());
			// get the width
			int x = tlx - _mySize;
			int y = tly - _mySize;
			int wid = (br.x - tlx) + _mySize * 2;
			int ht = (br.y - tly) + _mySize * 2;

			// represent this area as a rectangle
			java.awt.Rectangle thisR = new Rectangle(x, y, wid, ht);

			// keep track of the area covered
			if (_areaCovered == null)
				_areaCovered = thisR;
			else
				_areaCovered.add(thisR);

			// plot the rectangle
			dest.drawRect(x, y, wid, ht);
		}
		catch (IllegalStateException e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

	}

	/** ok, paint this layer, adding highlights where applicable
	 * 
	 * @param theLayer
	 * @param dest
	 * @param dtg
	 */
	public void paintThisLayer(Layer theLayer, CanvasType dest, HiResDate dtg)
	{
		// paint it, to start off with
		theLayer.paint(dest);

		// now think about the highlight

		// do we have a dtg?
		if (dtg != null)
		{
			if (theLayer instanceof TrackWrapper)
			{
				TrackWrapper tw = (TrackWrapper) theLayer;
				Watchable[] list = tw.getNearestTo(dtg);
				if (list != null)
				{
					for (int i = 0; i < list.length; i++)
					{
						Watchable thisW = list[i];

						highlightIt(dest.getProjection(), dest, thisW);
					}
				}
			}
		}
	}

	public String toString()
	{
		return "Normal";
	}

}
