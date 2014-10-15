/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.editors.painters;

import java.awt.Color;
import java.awt.Point;
import java.beans.PropertyDescriptor;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;

/**
 * painter which plots all data, and draws a square rectangle around tactical
 * items at the current dtg
 * 
 * @author ian.mayo
 */
public class PlainHighlighter implements TemporalLayerPainter
{
	private Color _myColor = Color.white;

	private int _mySize = 5;

	public final void highlightIt(final MWC.Algorithms.PlainProjection proj,
			final CanvasType dest, final MWC.GenericData.Watchable watch)
	{
		// check that our graphics context is still valid -
		// we can't, so we will just have to trap any exceptions it raises
		try
		{

			// set the highlight colour
			dest.setColor(_myColor);
			// get the current area of the watchable
			final WorldArea wa = watch.getBounds();
			// convert to screen coordinates
			final Point tl = proj.toScreen(wa.getTopLeft());

			final int tlx = tl.x;
			final int tly = tl.y;

			final Point br = proj.toScreen(wa.getBottomRight());
			// get the width
			final int x = tlx - _mySize;
			final int y = tly - _mySize;
			final int wid = (br.x - tlx) + _mySize * 2;
			final int ht = (br.y - tly) + _mySize * 2;

			boolean lineStyleOverridden = false;
			
			// hey, just see if this is an interpolated data item
			if(watch instanceof PlainWrapper.InterpolatedData)
			{
				// make the line dotted
				dest.setLineStyle(CanvasType.DOTTED);
				
				lineStyleOverridden = true;
			}

			// plot the rectangle
			dest.drawRect(x, y, wid, ht);
			
			// and restore
			if(lineStyleOverridden)
				dest.setLineStyle(CanvasType.SOLID);
		}
		catch (final IllegalStateException e)
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
	public void paintThisLayer(final Layer theLayer, final CanvasType dest, final HiResDate dtg)
	{
		// paint it, to start off with
		theLayer.paint(dest);

		// now think about the highlight

		// do we have a dtg?
//		if (dtg != null)
//		{
//			if (theLayer instanceof TrackWrapper)
//			{
//				TrackWrapper tw = (TrackWrapper) theLayer;
//				Watchable[] list = tw.getNearestTo(dtg);
//				if (list != null)
//				{
//					for (int i = 0; i < list.length; i++)
//					{
//						Watchable thisW = list[i];
//
//						highlightIt(dest.getProjection(), dest, thisW);
//					}
//				}
//			}
//		}
	}

	
	
	public Color getColor()
	{
		return _myColor;
	}

	public void setColor(final Color color)
	{
		_myColor = color;
	}

	public BoundedInteger getSize()
	{
		return new BoundedInteger(_mySize, 1, 10);
	}

	public void setSize(final BoundedInteger size)
	{
		_mySize = size.getCurrent();
	}

	public String toString()
	{
		return "Normal";
	}

	public String getName()
	{
		return toString();
	}

	public boolean hasEditor()
	{
		return true;
	}

	public EditorType getInfo()
	{
		return new PlainHighlighterInfo(this);
	}

	 /////////////////////////////////////////////////////////////
  // nested class describing how to edit this class
  ////////////////////////////////////////////////////////////
/** the set of editable details for the painter
 */
  public static final class PlainHighlighterInfo extends Editable.EditorType
  {

/** constructor for editable
 * @param data the object we are editing
 */
    public PlainHighlighterInfo(final PlainHighlighter data)
    {
      super(data, "Normal", "");
    }

/** the set of descriptions for this object
 * @return the properties
 */
    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try{
        final PropertyDescriptor[] res={
          prop("Color", "Color to paint highlight"),
          prop("Size", "size to paint highlight (pixels"),
        };
        return res;
      }
      catch(final Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
        return super.getPropertyDescriptors();
      }

    }
  }	
}
