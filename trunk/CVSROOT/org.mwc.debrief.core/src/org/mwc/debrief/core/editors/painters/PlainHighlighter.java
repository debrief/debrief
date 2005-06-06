/**
 * 
 */
package org.mwc.debrief.core.editors.painters;

import java.awt.*;
import java.util.Enumeration;

import Debrief.Tools.Tote.Watchable;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.*;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
public class PlainHighlighter
{
	private static Color _myColor = Color.white;
  private static int _mySize = 5;
  
	public static void update(HiResDate time, Layers layers, CanvasType dest)
	{

		// ok, find the tracks
		Enumeration numer = layers.elements();
		while (numer.hasMoreElements())
		{
			Layer thisLayer = (Layer) numer.nextElement();
			if (thisLayer instanceof TrackWrapper)
			{
				TrackWrapper tw = (TrackWrapper) thisLayer;
				Watchable[] list = tw.getNearestTo(time);
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

	public static final void highlightIt(MWC.Algorithms.PlainProjection proj,
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
		} catch (IllegalStateException e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

	}

}
