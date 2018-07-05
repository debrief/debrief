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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */

package org.mwc.debrief.core.editors.painters.highlighters;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Canvas.MetafileCanvas;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * Interface for classes which are able to draw a highlight at a particular point in time
 * 
 * @author IAN MAYO
 * @version 1
 */
public interface SWTPlotHighlighter extends Editable
{

  /**
   * Draw a highlight around this watchable
   * 
   * @param proj
   *          the current projection
   * @param dest
   *          the place to draw this highlight
   * @param watch
   *          the current data point
   * @param isPrimary
   *          whether this is the primary track
   */
  void highlightIt(MWC.Algorithms.PlainProjection proj, CanvasType dest,
      MWC.GenericData.WatchableList list, MWC.GenericData.Watchable watch,
      boolean isPrimary);

  // ////////////////////////////////////////////////////////////////////
  // embedded class which just shows rectangular highlight around current
  // point
  // ////////////////////////////////////////////////////////////////////
  /**
   * A simple (rectangular) highlighter
   */
  public final class RectangleHighlight implements SWTPlotHighlighter
  {

    public static final String DEFAULT_HIGHLIGHT = "Default Highlight";
    private Color _myColor = Color.gray;
    private int _mySize = 5;

    /**
     * Draw a highlight around this watchable
     * 
     * @param proj
     *          the current projection
     * @param dest
     *          the place to draw this highlight
     * @param watch
     *          the current data point
     */
    public final void highlightIt(final MWC.Algorithms.PlainProjection proj,
        final CanvasType dest, final MWC.GenericData.WatchableList list,
        MWC.GenericData.Watchable watch, final boolean isPrimary)
    {
      // check that our graphics context is still valid -
      // we can't, so we will just have to trap any exceptions it raises
      try
      {

        // sort out if this is an item that we plot
        if (watch instanceof Editable.DoNotHighlightMe)
        {
          // hey, don't bother...
          return;
        }

        // see if we're painting to WMF, in which case we don't want a
        // highlight.
        if (dest instanceof MetafileCanvas)
        {
          return;
        }

        Rectangle _areaCovered = null;

        // special case = check we're not trying to
        // plot a fix that isn't yet visible
        if (watch instanceof FixWrapper)
        {
          FixWrapper fw = (FixWrapper) watch;
          TrackWrapper tw = fw.getTrackWrapper();
          if (tw != null)
          {

            HiResDate dtg = fw.getTime();

            // trim to visible period if its a track
            TimePeriod visP = tw.getVisiblePeriod();
            if(visP != null && !visP.contains(dtg))
            {
              // ok, before or after?
              if (visP.getStartDTG().greaterThan(dtg))
              {
                dtg = visP.getStartDTG();
                watch = (FixWrapper) tw.getNearestTo(dtg)[0];
              }
              else if (visP.getEndDTG().lessThan(dtg))
              {
                dtg = visP.getEndDTG();
                watch = (FixWrapper) tw.getNearestTo(dtg)[0];
              }
            }
          }
        }

        // handle empty track
        if(watch == null)
        {
          return;
        }
        
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

        // represent this area as a rectangle
        java.awt.Rectangle thisR = new Rectangle(x, y, wid, ht);

        _areaCovered = thisR;

        // plot the rectangle
        dest.drawRect(x, y, wid, ht);

        // and the array centre
        plotArrayCentre(dest, watch, _areaCovered, _mySize);

      }
      catch (final IllegalStateException e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }

    }

    protected static void plotArrayCentre(final CanvasType dest,
        MWC.GenericData.Watchable watch, Rectangle areaCovered, int mySize)
    {
      java.awt.Rectangle thisR;
      java.awt.Color sensorColor = null;
      // just see if we've got sensor data, so we can plot the array
      // centre
      if (watch instanceof FixWrapper)
      {
        FixWrapper fw = (FixWrapper) watch;
        TrackWrapper tw = fw.getTrackWrapper();

        if (tw != null && tw.getPlotArrayCentre())
        {

          final Enumeration<Editable> enumer = tw.getSensors().elements();
          while (enumer.hasMoreElements())
          {
            final SensorWrapper sw = (SensorWrapper) enumer.nextElement();

            // is this sensor visible?
            if (sw.getVisible())
            {
              // ok, use a lighter color
              if (sensorColor == null)
              {
                sensorColor = fw.getColor().brighter();
                dest.setColor(sensorColor);
              }

              final WorldLocation centre =
                  sw.getArrayCentre(fw.getTime(), watch.getLocation(), tw);

              // have we managed it?
              if (centre != null)
              {
                final Point pt = dest.toScreen(centre);
                dest.drawLine(pt.x - mySize, pt.y - mySize, pt.x + mySize, pt.y
                    + mySize);
                dest.drawLine(pt.x + mySize, pt.y - mySize, pt.x - mySize, pt.y
                    + mySize);

                // store the new screen update area
                thisR =
                    new Rectangle(pt.x - mySize, pt.y - mySize, mySize, mySize);
                if (areaCovered != null)
                {
                  areaCovered.add(thisR);
                }

                thisR =
                    new Rectangle(pt.x + mySize, pt.y - mySize, mySize, mySize);
                if (areaCovered != null)
                {
                  areaCovered.add(thisR);
                }
              }
              else
              {
                Application.logStack2(Application.ERROR,
                    "Unable to determine array centre for:" + sw.getName());
              }
            }

          }
        }
      }
    }

    /**
     * the name of this object
     * 
     * @return the name of this editable object
     */
    public final String getName()
    {
      return DEFAULT_HIGHLIGHT;
    }

    /**
     * the name of this object
     * 
     * @return the name of this editable object
     */
    public final String toString()
    {
      return getName();
    }

    /**
     * whether there is any edit information for this item this is a convenience function to save
     * creating the EditorType data first
     * 
     * @return yes/no
     */
    public final boolean hasEditor()
    {
      return true;
    }

    /**
     * get the editor for this item
     * 
     * @return the BeanInfo data for this editable object
     */
    public final Editable.EditorType getInfo()
    {
      return new RectangleHighlightInfo(this);
    }

    /**
     * change the colour of the highlight
     * 
     * @param val
     *          the new colour
     */
    public final void setColor(final Color val)
    {
      if (val != null)
        _myColor = val;
    }

    /**
     * change the size of the highlight to plot
     * 
     * @param val
     *          the new size (stored with its constraints)
     */
    public final void setSize(final BoundedInteger val)
    {
      _mySize = val.getCurrent();
    }

    /**
     * easy access for the actual size value (used to restore plot preferences)
     * 
     * @param size
     */
    public final void setRawSize(final int size)
    {
      _mySize = size;
    }

    /**
     * return the current highlight colour
     * 
     * @return the colour
     */
    public final Color getColor()
    {
      return _myColor;
    }

    /**
     * return the current size of the highlight
     * 
     * @return current size, stored with it's constraints
     */
    public final BoundedInteger getSize()
    {
      return new BoundedInteger(_mySize, 0, 20);
    }

    // ///////////////////////////////////////////////////////////
    // nested class describing how to edit this class
    // //////////////////////////////////////////////////////////
    /**
     * the set of editable details for the painter
     */
    public final class RectangleHighlightInfo extends Editable.EditorType
    {

      /**
       * constructor for editable
       * 
       * @param data
       *          the object we are editing
       */
      public RectangleHighlightInfo(final RectangleHighlight data)
      {
        super(data, DEFAULT_HIGHLIGHT, "");
      }

      /**
       * the set of descriptions for this object
       * 
       * @return the properties
       */
      public final java.beans.PropertyDescriptor[] getPropertyDescriptors()
      {
        try
        {
          final java.beans.PropertyDescriptor[] res =
              {prop("Color", "Color to paint highlight"),
                  prop("Size", "size to paint highlight (pixels)"),};
          return res;
        }
        catch (final Exception e)
        {
          MWC.Utilities.Errors.Trace.trace(e);
          return super.getPropertyDescriptors();
        }

      }
    }

  }

}
