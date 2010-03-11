package Debrief.GUI.Tote.Painters;

import java.awt.*;
import java.util.*;

import Debrief.Wrappers.*;
import MWC.GUI.Editable;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GenericData.*;

/** class to draw a 'back-track' of points backwards from the current
 * datapoint for the indicated period.
 *
 * Internally, the class retrieves the list of included points from the
 * track itself and stores them in the HashTable indexed by the current fix.
 * So, when we are asked to plot a point, we look in the HashTable first --
 * if we have a vector of points for this fix we re-plot these and then
 * remove them from the hashtable.
 * If we don't find a vector of points for this Fix then we retrieve
 * the list from the track and then insert the list into our HashTable
 * Ta-Da!
 *
 */
final class SnailDrawTrack
{

  /** the size of points to draw
   */
  private int _pointSize;

  /** the length of trail to draw (microseconds)
   */
  private long _trailLength;

  /** whether to join fixes
   */
  private boolean _joinPoints;

  /** our list of Vectors of points
   */
  private final java.util.Hashtable<FixWrapper, Collection<Editable>> _fixLists;

  /** whether to fade out the track and symbols
   */
  private boolean _fadePoints;

  ///////////////////////////////////
  // constructor
  //////////////////////////////////
  public SnailDrawTrack()
  {
    setJoinPositions(true);
    setFadePoints(true);
    setTrailLength(new Long(15 * 1000 * 1000* 60 )); // 15 minutes
    setPointSize(5);

    _fixLists = new java.util.Hashtable<FixWrapper, Collection<Editable>>();
  }


  ///////////////////////////////////
  // member functions
  //////////////////////////////////
  public final java.awt.Rectangle drawMe(MWC.Algorithms.PlainProjection proj,
                                         java.awt.Graphics dest,
                                         Watchable watch,
                                         SnailPainter parent,
                                         HiResDate dtg,
                                         Color backColor)
  {

    //dest.setPaintMode();
    dest.setXORMode(backColor);

    // represent this area as a rectangle
    java.awt.Rectangle thisR  = null;

    // get the fix and the track
    final FixWrapper theFix = (FixWrapper)watch;
    TrackWrapper trk = theFix.getTrackWrapper();

    // declare the Vector of track points we are using
    final Collection<Editable> dotPoints;

    // do we have these points already?
    Collection<Editable> myList = _fixLists.get(theFix);

    // did we find it?
    if(myList != null)
    {
      // cast it back to the vector
      dotPoints = myList;

      // we only remove this list from our hashtable if
      // we are not in a repaint operation
      if(!parent.isRepainting())
      {

        // and remove it from the list
        _fixLists.remove(theFix);

      }
      else
      {
        // we are in a repaint, which means we _do_ want to draw
        // in the correctly coloured tail
      }
    }
    else
    {
      // retrieve the points in range
      dotPoints = trk.getUnfilteredItems(new HiResDate(0, dtg.getMicros() - _trailLength), new HiResDate(0, dtg.getMicros()+2));

      // check that we found some points for this track
      if(dotPoints != null)
      {
        // and put them into the list
        _fixLists.put(theFix, dotPoints);
      }
    }



    // see if there are any points
    if(dotPoints != null)
    {
      if(dotPoints.size()>0)
      {

        // get the colour of the track
        /** NOTE that we are working in floats for all of the color
         * stuff - if we were to work in ints, then when we
         * want more than 255 shades, the deltas become zero
         * and the track dissappears.  By working in floats we
         * can provide very fine deltas, allowing very large numbers
         * of points to be tidily plotted in the track
         */
        float red, green, blue;
        final Color mainCol = trk.getColor();
        red = mainCol.getRed();
        green = mainCol.getGreen();
        blue = mainCol.getBlue();

        // sort out the r,g,b components of the background colour
        float backR, backG, backB;
        backR = backColor.getRed();
        backG = backColor.getGreen();
        backB = backColor.getBlue();

        // now switch r,g,b to their deltas from the back ground colour
        red -= backR;
        green -= backG;
        blue -= backB;

        // how many shades to we need?
        final float numShades = (float)dotPoints.size()+1;

        // what are the deltas?
        float dRed, dGreen, dBlue;
        dRed = red / numShades;
        dGreen = green / numShades;
        dBlue = blue / numShades;

        // give our shades valid start values
        red = backR;
        green = backG;
        blue = backB;

        // remember the last location
        Point lastLoc=null;

        Iterator<Editable> iter = dotPoints.iterator();
        while(iter.hasNext())
        {

          final Color newCol;

          // see if we are fading to black
          if(_fadePoints)
          {
            // produce the next colour
            red += dRed;
            green += dGreen;
            blue += dBlue;

            newCol = new  Color((int)red, (int)green, (int) blue);
          }
          else
          {
            // just use the normal track colour
            newCol = trk.getColor();
          }


          // update the colour for this segment
          dest.setColor(newCol);

          // get this fix
          FixWrapper gw = (FixWrapper)iter.next();

          // get the location
          WorldLocation loc = gw.getLocation();

          // get the screen location
          Point screenP = new Point(proj.toScreen(loc));

          // initialise the area, if we have to
          if(thisR == null)
            thisR = new Rectangle(screenP);

          // see if this fix is visible
          if(gw.getSymbolShowing())
          {

            // and draw the dot
            drawDot(screenP,
                    dest,
                    _pointSize,
                    thisR);
          }

          // see if we are joining them
          if(lastLoc == null)
          {
            lastLoc = screenP;
          }
          else
          {
            // see if we are joining the points
            if(_joinPoints)
            {
              dest.drawLine(lastLoc.x, lastLoc.y, screenP.x, screenP.y);
            }

            lastLoc = screenP;
          }

          // see if we are plotting the DTG
          if(gw.getLabelShowing())
          {
            // set the font to the current font for the fix (so that we get the metrics right)
            dest.setFont(gw.getFont());

            // get the text itself, again for the metrics
            String msg = gw.getName();

            // wrap our Graphics object in a canvas, so we can paint to it
            CanvasAdaptor cad = new CanvasAdaptor(proj, dest);

            // and get the label to paint itself
            gw.paintLabel(cad, newCol);

            // ditch the canvas
            cad.endDraw(null);

            // somehow we need to include this extended area
            FontMetrics fm = dest.getFontMetrics();

            //
            int sWid = fm.stringWidth(msg);

            // shift from the start of the string (using a copy of the point)
            Point newP = new Point(screenP);
            newP.translate(sWid, 0);

            // and add to the limits rectangle
            thisR.add(newP);

          }
        }
      }
    }

    return thisR;
  }


  private static void drawDot(final Point loc,
                              final java.awt.Graphics dest,
                              final int size,
                              final Rectangle area)
  {
    final int wid = size / 2;
    dest.fillOval(loc.x - wid, loc.y - wid, size, size);
    area.add(loc.x - size - 2, loc.y - size - 2);
    area.add(loc.x + size + 2, loc.y + size + 2);
  }


//	public boolean canPlot(Watchable wt)
//	{
//		boolean res = false;
//
//		if((wt instanceof Debrief.Wrappers.TrackWrapper)||(wt instanceof Debrief.Wrappers.BuoyPatternWrapper))
//		{
//			res = true;
//		}
//		return res;
//	}

  public final void setJoinPositions(final boolean val)
  {
    _joinPoints = val;
  }

  public final boolean getJoinPositions()
  {
    return _joinPoints;
  }

  public final void setFadePoints(final boolean val)
  {
    _fadePoints = val;
  }

  public final boolean getFadePoints()
  {
    return _fadePoints;
  }

  /** point size of symbols (pixels)
   */
  public final int getPointSize()
  {
    return _pointSize;
  }

  /** length of trail to plot (micros)
   */
  public final Long getTrailLength()
  {
    return new Long(_trailLength);
  }

  /** size of points to draw (pixels)
   */
  public final void setPointSize(final int val)
  {
    _pointSize = val;
  }

  /** length of trail to draw (micros)
   */
  public final void setTrailLength(final Long len)
  {
    _trailLength = len.longValue();

    // and clear the lists of fixes we are using, so that they are re-calculated
    if(_fixLists != null)
      _fixLists.clear();
  }


}

