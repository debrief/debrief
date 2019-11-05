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
package Debrief.GUI.Tote.Painters;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import Debrief.GUI.Tote.Painters.SnailPainter2.ColorFadeCalculator;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;

/**
 * class to draw a 'back-track' of points backwards from the current datapoint for the indicated
 * period.
 *
 * Internally, the class retrieves the list of included points from the track itself and stores them
 * in the HashTable indexed by the current fix. So, when we are asked to plot a point, we look in
 * the HashTable first -- if we have a vector of points for this fix we re-plot these and then
 * remove them from the hashtable. If we don't find a vector of points for this Fix then we retrieve
 * the list from the track and then insert the list into our HashTable Ta-Da!
 *
 */
final class SnailDrawTrack2
{

  private static void drawDot(final Point loc, final java.awt.Graphics dest,
      final int size, final Rectangle area)
  {
    final int wid = size / 2;
    dest.fillOval(loc.x - wid, loc.y - wid, size, size);
    area.add(loc.x - size - 2, loc.y - size - 2);
    area.add(loc.x + size + 2, loc.y + size + 2);
  }

  /**
   * the size of points to draw
   */
  private int _pointSize;

  /**
   * the length of trail to draw (microseconds)
   */
  private long _trailLength;

  /**
   * whether to join fixes
   */
  private boolean _joinPoints;

  /**
   * our list of Vectors of points
   */
  private final java.util.Hashtable<FixWrapper, Collection<Editable>> _fixLists;

  /**
   * whether to fade out the track and symbols
   */
  private boolean _fadePoints;

  ///////////////////////////////////
  // constructor
  //////////////////////////////////
  public SnailDrawTrack2()
  {
    setJoinPositions(true);
    setFadePoints(true);
    setTrailLength(new Long(15 * 1000 * 1000 * 60)); // 15 minutes
    setPointSize(5);

    _fixLists = new java.util.Hashtable<FixWrapper, Collection<Editable>>();
  }

  ///////////////////////////////////
  // member functions
  //////////////////////////////////
  public final Rectangle drawMe(final MWC.Algorithms.PlainProjection proj,
      final java.awt.Graphics dest, final Watchable watch,
      final TotePainter parent, final HiResDate dtg,
      final ColorFadeCalculator fader)
  {
    // represent this area as a rectangle
    java.awt.Rectangle thisR = null;

    // get the fix and the track
    final FixWrapper theFix = (FixWrapper) watch;
    final WatchableList trk = theFix.getTrackWrapper();

    // declare the Vector of track points we are using
    final Collection<Editable> dotPoints;

    // do we have these points already?
    final Collection<Editable> myList = _fixLists.get(theFix);

    // did we find it?
    if (myList != null)
    {
      // cast it back to the vector
      dotPoints = myList;

      // we only remove this list from our hashtable if
      // we are not in a repaint operation
      if (!parent.isRepainting())
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
      if (trk instanceof LightweightTrackWrapper)
      {
        final LightweightTrackWrapper track = (LightweightTrackWrapper) trk;
        final HiResDate trailTime = new HiResDate(0, dtg.getMicros()
            - _trailLength);

        // Let's add at the beginning the tail if we are interpolating.
        dotPoints = new Vector<Editable>(0, 1);

        if (track instanceof TrackWrapper && ((TrackWrapper) track)
            .getInterpolatePoints())
        {
          // Are we interpolating? Then create the snail tail
          // from the interpolation
          final Watchable[] nearest = track.getNearestTo(trailTime);
          if (nearest != null && nearest.length > 0)
          {
            dotPoints.add((Editable) nearest[0]);
          }
        }
        dotPoints.addAll(track.getUnfilteredItems(trailTime, new HiResDate(0,
            dtg.getMicros() + 2000)));
        dotPoints.add(theFix);
      }
      else
      {
        dotPoints = null;
      }

      // check that we found some points for this track
      if (dotPoints != null)
      {
        // and put them into the list
        _fixLists.put(theFix, dotPoints);
      }
    }

    thisR = drawTrailFromPoints(proj, dest, fader, thisR, trk, dotPoints);

    return thisR;
  }

  private java.awt.Rectangle drawTrailFromPoints(
      final MWC.Algorithms.PlainProjection proj, final java.awt.Graphics dest,
      final ColorFadeCalculator fader, java.awt.Rectangle thisR,
      final WatchableList trk, final Collection<Editable> dotPoints)
  {
    // see if there are any points
    if (dotPoints != null)
    {
      if (dotPoints.size() > 0)
      {
        // remember the last location
        Point lastLoc = null;

        final Iterator<Editable> iter = dotPoints.iterator();
        while (iter.hasNext())
        {
          final Color newCol;

          final FixWrapper fix = (FixWrapper) iter.next();

          // see if we are fading to black
          if (_fadePoints)
          {
            final Color trkColor = trk.getColor();
            newCol = fader.fadeColorAt(trkColor, fix.getDateTimeGroup());
          }
          else
          {
            // just use the normal track colour
            newCol = trk.getColor();
          }

          // update the colour for this segment
          dest.setColor(newCol);

          // get the location
          final WorldLocation loc = fix.getLocation();

          // get the screen location
          final Point scrPos = proj.toScreen(loc);

          final Point screenP = new Point(scrPos);

          // initialise the area, if we have to
          if (thisR == null)
            thisR = new Rectangle(screenP);

          // see if this fix is visible
          if (fix.getSymbolShowing())
          {
            // and draw the dot
            drawDot(screenP, dest, _pointSize, thisR);
          }

          // do we know the previous position?
          if (lastLoc == null)
          {
            lastLoc = screenP;
          }
          else
          {
            // see if we are joining the points
            if (_joinPoints)
            {
              dest.drawLine(lastLoc.x, lastLoc.y, screenP.x, screenP.y);
            }
            lastLoc = screenP;
          }

          // see if we are plotting the DTG
          if (fix.getLabelShowing())
          {
            // set the font to the current font for the fix (so that we get the metrics right)
            dest.setFont(fix.getFont());

            // get the text itself, again for the metrics
            final String msg = fix.getName();

            // wrap our Graphics object in a canvas, so we can paint to it
            final CanvasAdaptor cad = new CanvasAdaptor(proj, dest);

            // and get the label to paint itself
            fix.paintLabel(cad, newCol);

            // ditch the canvas
            cad.endDraw(null);

            // somehow we need to include this extended area
            final FontMetrics fm = dest.getFontMetrics();

            //
            final int sWid = fm.stringWidth(msg);

            // shift from the start of the string (using a copy of the point)
            final Point newP = new Point(screenP);
            newP.translate(sWid, 0);

            // and add to the limits rectangle
            thisR.add(newP);

          }
        }
      }
    }
    return thisR;
  }

  // public boolean canPlot(Watchable wt)
  // {
  // boolean res = false;
  //
  // if((wt instanceof Debrief.Wrappers.TrackWrapper)||(wt instanceof
  // Debrief.Wrappers.BuoyPatternWrapper))
  // {
  // res = true;
  // }
  // return res;
  // }

  public final boolean getFadePoints()
  {
    return _fadePoints;
  }

  public final boolean getJoinPositions()
  {
    return _joinPoints;
  }

  /**
   * point size of symbols (pixels)
   */
  public final int getPointSize()
  {
    return _pointSize;
  }

  /**
   * length of trail to plot (micros)
   */
  public final Long getTrailLength()
  {
    return new Long(_trailLength);
  }

  public final void setFadePoints(final boolean val)
  {
    _fadePoints = val;
  }

  public final void setJoinPositions(final boolean val)
  {
    _joinPoints = val;
  }

  /**
   * size of points to draw (pixels)
   */
  public final void setPointSize(final int val)
  {
    _pointSize = val;
  }

  /**
   * length of trail to draw (micros)
   */
  public final void setTrailLength(final Long len)
  {
    _trailLength = len.longValue();

    // and clear the lists of fixes we are using, so that they are re-calculated
    if (_fixLists != null)
      _fixLists.clear();
  }

}
