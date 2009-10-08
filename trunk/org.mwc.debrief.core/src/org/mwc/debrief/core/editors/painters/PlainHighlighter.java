/**
 * 
 */
package org.mwc.debrief.core.editors.painters;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
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

	public final void highlightIt(MWC.Algorithms.PlainProjection proj,
			CanvasType dest, MWC.GenericData.Watchable watch)
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

	public void setColor(Color color)
	{
		_myColor = color;
	}

	public BoundedInteger getSize()
	{
		return new BoundedInteger(_mySize, 1, 10);
	}

	public void setSize(BoundedInteger size)
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
      catch(Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
        return super.getPropertyDescriptors();
      }

    }
  }	
}
