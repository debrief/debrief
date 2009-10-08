/*
 * PlotHighlighter.java
 *
 * Created on 29 September 2000, 10:36
 */

package org.mwc.debrief.core.editors.painters.highlighters;

import Debrief.GUI.Tote.Painters.SnailPainter.DoNotHighlightMe;
import MWC.GUI.*;
import java.awt.*;
import MWC.GenericData.*;
import MWC.GUI.Canvas.MetafileCanvas;
import MWC.GUI.Properties.BoundedInteger;

/** Interface for classes which are able to draw a highlight at a particular
 * point in time
 * @author IAN MAYO
 * @version 1
 */
public interface SWTPlotHighlighter extends Editable  {


/** Draw a highlight around this watchable
 * @param proj the current projection
 * @param dest the place to draw this highlight
 * @param watch the current data point
 * @param isPrimary whether this is the primary track
 */
  void highlightIt(MWC.Algorithms.PlainProjection proj,
                   CanvasType dest,
                   MWC.GenericData.WatchableList list,
                   MWC.GenericData.Watchable watch, boolean isPrimary);

  //////////////////////////////////////////////////////////////////////
  // embedded class which just shows rectangular highlight around current point
  //////////////////////////////////////////////////////////////////////
/** A simple (rectangular) highlighter
 */
  public final class RectangleHighlight implements SWTPlotHighlighter, Editable
  {

    private Color _myColor = Color.white;
    private int _mySize = 5;

    /** Draw a highlight around this watchable
     * @param proj the current projection
     * @param dest the place to draw this highlight
     * @param watch the current data point
     */
    public final void highlightIt(MWC.Algorithms.PlainProjection proj,
    		CanvasType dest,
                            MWC.GenericData.WatchableList list,
                            MWC.GenericData.Watchable watch, boolean isPrimary)
    {
      // check that our graphics context is still valid -
      // we can't, so we will just have to trap any exceptions it raises
      try
      {
      	
      	// sort out if this is an item that we plot
      	if(watch instanceof DoNotHighlightMe)
      	{
      		// hey, don't bother...
      		return;
      	}
      	
      	// see if we're painting to WMF, in which case we don't want a highlight.
      	if(dest instanceof MetafileCanvas)
      	{
      		return;
      	}
      	
      	
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
        if(_areaCovered == null)
          _areaCovered = thisR;
        else
          _areaCovered.add(thisR);

        // plot the rectangle
        dest.drawRect(x , y, wid, ht);
      }
      catch(IllegalStateException e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }

    }

    /** the name of this object
     * @return the name of this editable object
     */
    public final String getName () {
      return "Default Highlight";
    }

    /** the name of this object
     * @return the name of this editable object
     */
    public final String toString(){
      return getName();
    }

    /** whether there is any edit information for this item
     * this is a convenience function to save creating the EditorType data
     * first
     * @return yes/no
     */
    public final boolean hasEditor () {
      return true;
    }

    /** get the editor for this item
     * @return the BeanInfo data for this editable object
     */
    public final Editable.EditorType getInfo () {
      return new RectangleHighlightInfo(this);
    }

    /** change the colour of the highlight
     * @param val the new colour
     */
    public final void setColor(final Color val)
    {_myColor = val;}
    /** change the size of the highlight to plot
     * @param val the new size (stored with its constraints)
     */
    public final void setSize(final BoundedInteger val)
    { _mySize = val.getCurrent(); }
    /** return the current highlight colour
     * @return the colour
     */
    public final Color getColor()
    { return _myColor; }
    /** return the current size of the highlight
     * @return current size, stored with it's constraints
     */
    public final BoundedInteger getSize()
    { return new BoundedInteger(_mySize, 1, 20);  }


    /////////////////////////////////////////////////////////////
    // nested class describing how to edit this class
    ////////////////////////////////////////////////////////////
    /** the set of editable details for the painter
     */
    public final class RectangleHighlightInfo extends Editable.EditorType
    {

      /** constructor for editable
       * @param data the object we are editing
       */
      public RectangleHighlightInfo(final RectangleHighlight data)
      {
        super(data, "Default Highlight", "");
      }

      /** the set of descriptions for this object
       * @return the properties
       */
      public final java.beans.PropertyDescriptor[] getPropertyDescriptors()
      {
        try{
          final java.beans.PropertyDescriptor[] res={
            prop("Color", "Color to paint highlight"),
            prop("Size", "size to paint highlight (pixels)"),
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


}

