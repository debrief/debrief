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
package Debrief.Wrappers.Track;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.FixSetter;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Properties.LineWidthPropertyEditor;
import MWC.GUI.Properties.NullableLocationPropertyEditor;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor.IsTransientForChildren;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

public class LightweightTrackWrapper extends PlainWrapper implements
    WatchableList, Plottable, Layer, IsTransientForChildren
{

  public class LightweightTrackInfo extends Editable.EditorType
  {
    public LightweightTrackInfo(final LightweightTrackWrapper data)
    {
      super(data, data.getName(), "");
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {prop("Visible", "the Layer visibility", VISIBILITY), prop("Name",
            "the name of the track", FORMAT), prop("NameVisible",
                "show the name of the track", FORMAT), prop("Color",
                    "color of the track", FORMAT), displayExpertLongProp(
                        "ResampleDataAt", "Resample data at",
                        "the data sample rate", TEMPORAL,
                        TimeFrequencyPropertyEditor.class),
            displayExpertLongProp("LineThickness", "Line thickness",
                "the width to draw this track", FORMAT,
                LineWidthPropertyEditor.class), displayExpertLongProp(
                    "LabelFrequency", "Label frequency", "the label frequency",
                    TEMPORAL, TimeFrequencyPropertyEditor.class),
            displayExpertLongProp("SymbolFrequency", "Symbol frequency",
                "the symbol frequency", TEMPORAL,
                TimeFrequencyPropertyEditor.class), displayExpertLongProp(
                    "ArrowFrequency", "Arrow frequency",
                    "the direction marker frequency", TEMPORAL,
                    TimeFrequencyPropertyEditor.class), displayExpertLongProp(
                        "LineStyle", "Line style",
                        "the line style used to join track points", TEMPORAL,
                        LineStylePropertyEditor.class)};

        return res;

      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  public static class TestLightweight extends TestCase
  {
    private static interface IsValid
    {
      boolean isValid(FixWrapper fix);
    }

    private FixWrapper create(final long date, final double lat,
        final double lon)
    {
      return new FixWrapper(new Fix(new HiResDate(date), new WorldLocation(lat,
          lon, 0), 0d, 0d));
    }

    private int doCount(final LightweightTrackWrapper track,
        final IsValid aTest)
    {
      int ctr = 0;
      final Enumeration<Editable> pIter = track.getPositionIterator();
      while (pIter.hasMoreElements())
      {
        final FixWrapper fix = (FixWrapper) pIter.nextElement();
        if (aTest.isValid(fix))
        {
          ctr++;
        }
      }
      return ctr;
    }

    private LightweightTrackWrapper getTrack()
    {
      final LightweightTrackWrapper track = new LightweightTrackWrapper();
      track.setName("light");
      track.addFix(create(10000, 2d, 2d));
      track.addFix(create(20000, 2d, 2d));
      track.addFix(create(30000, 2d, 2d));
      track.addFix(create(40000, 2d, 2d));
      track.addFix(create(50000, 2d, 2d));
      track.addFix(create(60000, 2d, 2d));
      track.addFix(create(70000, 2d, 2d));
      track.addFix(create(80000, 2d, 2d));
      track.addFix(create(90000, 2d, 2d));
      track.addFix(create(100000, 2d, 2d));
      return track;
    }

    public void testResample()
    {
      LightweightTrackWrapper track = getTrack();
      track.setResampleDataAt(new HiResDate(20000));
      assertEquals("correct size", 5, track._thePositions.size());
    }

    public void testSetFreq()
    {
      final LightweightTrackWrapper track = getTrack();

      final IsValid aTest = new IsValid()
      {
        @Override
        public boolean isValid(final FixWrapper fix)
        {
          return fix.getArrowShowing();
        }
      };
      final IsValid lTest = new IsValid()
      {
        @Override
        public boolean isValid(final FixWrapper fix)
        {
          return fix.getLabelShowing();
        }
      };
      final IsValid sTest = new IsValid()
      {
        @Override
        public boolean isValid(final FixWrapper fix)
        {
          return fix.getSymbolShowing();
        }
      };

      track.setArrowFrequency(new HiResDate(20000));
      int ctr = doCount(track, aTest);
      assertEquals("correct arrows", 5, ctr);

      track.setArrowFrequency(new HiResDate(0, TimePeriod.INVALID_TIME));
      ctr = doCount(track, aTest);
      assertEquals("correct arrows", 10, ctr);

      track.setArrowFrequency(new HiResDate(15000));
      ctr = doCount(track, aTest);
      assertEquals("correct arrows", 6, ctr);

      track.setSymbolFrequency(new HiResDate(10000));
      ctr = doCount(track, sTest);
      assertEquals("correct symbols", 10, ctr);

      track.setSymbolFrequency(new HiResDate(0, TimePeriod.INVALID_TIME));
      ctr = doCount(track, sTest);
      assertEquals("correct symbols", 10, ctr);

      track.setSymbolFrequency(new HiResDate(20000));
      ctr = doCount(track, sTest);
      assertEquals("correct symbols", 5, ctr);

      track.setLabelFrequency(new HiResDate(12000));
      ctr = doCount(track, lTest);
      assertEquals("correct labels", 8, ctr);

      track.setLabelFrequency(new HiResDate(0, TimePeriod.INVALID_TIME));
      ctr = doCount(track, lTest);
      assertEquals("correct labels", 10, ctr);

      track.setLabelFrequency(new HiResDate(20000));
      ctr = doCount(track, lTest);
      assertEquals("correct labels", 5, ctr);

    }
  }

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * group these track objects into this LightWeight track
   *
   * @param target
   * @param layers
   * @param subjects
   */
  public static void groupTracks(final LightweightTrackWrapper target,
      final Layers layers, final Editable[] subjects)
  {
    // ok, loop through the subjects, adding them onto the target
    for (int i = 0; i < subjects.length; i++)
    {
      final LightweightTrackWrapper source =
          (LightweightTrackWrapper) subjects[i];
      if (source != target)
      {
        final Collection<FixWrapper> deleted = new ArrayList<FixWrapper>();

        final Enumeration<Editable> pIter = source.getPositionIterator();
        while (pIter.hasMoreElements())
        {
          deleted.add((FixWrapper) pIter.nextElement());
        }

        for (final FixWrapper t : deleted)
        {
          source.removeElement(t);
          target.addFix(t);
        }

        // and remove the object
        layers.removeThisEditable(null, source);
      }
    }
  }

  private TimePeriod _cachedPeriod;

  private long _timeCachedPeriodCalculated;

  protected HiResDate _lastDataFrequency = new HiResDate(0,
      TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY);

  private int _lineStyle;

  private WorldArea _bounds = null;

  private final Plottables _thePositions = new Plottables();

  /**
   * the width of this track
   */
  private int _lineWidth = 0;

  /**
   * whether or not to show the Positions
   */
  protected boolean _showPositions;

  /**
   * the label describing this track
   */
  private final MWC.GUI.Shapes.TextLabel _theLabel;

  private HiResDate _lastLabelFrequency = new HiResDate(0);

  private HiResDate _lastSymbolFrequency = new HiResDate(0);

  private HiResDate _lastArrowFrequency = new HiResDate(0);

  private transient EditorType _myEditor;

  public LightweightTrackWrapper()
  {
    // no-op constructor
    _theLabel = new MWC.GUI.Shapes.TextLabel(new WorldLocation(0, 0, 0), null);
    // set an initial location for the label
    setNameLocation(NullableLocationPropertyEditor.AUTO);

    // set default line-style
    setLineStyle(LineStylePropertyEditor.SOLID);
  }

  public LightweightTrackWrapper(final String name, final boolean visible,
      final boolean nameVisible, final Color color, final int lineStyle)
  {
    this();

    setName(name);
    setVisible(visible);
    setNameVisible(nameVisible);
    setColor(color);
    setLineStyle(lineStyle);
  }

  /**
   * add the indicated point to the track
   *
   * @param point
   *          the point to add
   */
  @Override
  public void add(final Editable point)
  {
    // see what type of object this is
    if (point instanceof FixWrapper)
    {
      final FixWrapper fw = (FixWrapper) point;
      addFix(fw);
    }
  }

  public void addFix(final FixWrapper e)
  {
    flushBounds();

    // tell it who's the boss
    e.setTrackWrapper(this);

    // check the label
    if (e.getLabel() == null || e.getLabel().length() == 0)
    {
      e.resetName();
    }

    // finally, store it.
    _thePositions.add(e);
  }

  /**
   * append this other layer to ourselves (although we don't really bother with it)
   *
   * @param other
   *          the layer to add to ourselves
   */
  @Override
  public void append(final Layer other)
  {
    // is it a track?
    if ((other instanceof LightweightTrackWrapper)
        || (other instanceof LightweightTrackWrapper))
    {
      // yes, break it down.
      final Enumeration<Editable> iter = other.elements();
      while (iter.hasMoreElements())
      {
        final Editable nextItem = iter.nextElement();
        if (nextItem instanceof Layer)
        {
          append((Layer) nextItem);
        }
        else
        {
          add(nextItem);
        }
      }
    }
    else
    {
      // nope, just add it to us.
      add(other);
    }
  }

  @Override
  public int compareTo(final Plottable o)
  {
    return this.getName().compareTo(o.getName());
  }

  @Override
  public Enumeration<Editable> elements()
  {
    return _thePositions.elements();
  }

  /**
   * export this track to REPLAY file
   */
  @Override
  public final void exportShape()
  {
    // call the method in PlainWrapper
    this.exportThis();
  }

  @Override
  public void filterListTo(final HiResDate start, final HiResDate end)
  {
    final TimePeriod period = new TimePeriod.BaseTimePeriod(start, end);
    final Iterator<FixWrapper> fIter = iterator();
    while (fIter.hasNext())
    {
      final FixWrapper fix = fIter.next();
      if (period.contains(fix.getDateTimeGroup()))
      {
        fix.setVisible(true);
      }
      else
      {
        fix.setVisible(false);
      }
    }
  }

  private void flushBounds()
  {
    // forget the bounds
    _bounds = null;
  }

  /**
   * ensure the cached set of raw positions gets cleared
   *
   */
  public void flushPeriodCache()
  {
    _cachedPeriod = null;
  }

  /**
   * return the arrow frequencies for the track
   *
   * @return frequency in seconds
   */
  public final HiResDate getArrowFrequency()
  {
    return _lastArrowFrequency;
  }

  @Override
  public WorldArea getBounds()
  {
    if (_bounds == null)
    {
      final Iterator<FixWrapper> itera = iterator();
      while (itera.hasNext())
      {
        final WorldLocation loc = itera.next().getLocation();
        if (_bounds == null)
        {
          _bounds = new WorldArea(loc, loc);
        }
        else
        {
          _bounds.extend(loc);
        }
      }
    }

    return _bounds;
  }

  @Override
  public Color getColor()
  {
    return getCustomColor();
  }

  public Color getCustomColor()
  {
    return _theLabel.getColor();
  }

  @Override
  public HiResDate getEndDTG()
  {
    return ((FixWrapper) _thePositions.last()).getTime();
  }

  @Override
  public EditorType getInfo()
  {
    if(_myEditor == null)
    {
      _myEditor = new LightweightTrackInfo(this);
    }
    return _myEditor;
  }

  @Override
  public Collection<Editable> getItemsBetween(final HiResDate start,
      final HiResDate end)
  {
    final TimePeriod period = new TimePeriod.BaseTimePeriod(start, end);
    final Collection<Editable> items = new Vector<Editable>();
    final Iterator<FixWrapper> iter = iterator();
    while (iter.hasNext())
    {
      final FixWrapper fix = iter.next();
      if (period.contains(fix.getDateTimeGroup()))
      {
        items.add(fix);
      }
    }
    return items;
  }

  /**
   * method to allow the setting of label frequencies for the track
   *
   * @return frequency to use
   */
  public final HiResDate getLabelFrequency()
  {
    return this._lastLabelFrequency;
  }

  protected final WorldLocation getLabelLocation()
  {
    return _theLabel.getLocation();
  }

  public int getLineStyle()
  {
    return _lineStyle;
  }

  /**
   * the line thickness (convenience wrapper around width)
   *
   * @return
   */
  @Override
  public int getLineThickness()
  {
    return _lineWidth;
  }

  /**
   * name of this Track (normally the vessel name)
   *
   * @return the name
   */
  @Override
  public final String getName()
  {
    return _theLabel.getString();
  }

  /**
   * the relative location of the label
   *
   * @return the relative location
   */
  public final Integer getNameLocation()
  {
    return _theLabel.getRelativeLocation();
  }

  /**
   * whether the track label is visible or not
   *
   * @return yes/no
   */
  public final boolean getNameVisible()
  {
    return _theLabel.getVisible();
  }

  @Override
  public Watchable[] getNearestTo(final HiResDate DTG)
  {
    final long dtg = DTG.getDate().getTime();
    FixWrapper nearest = null;

    if (_thePositions.isEmpty())
    {
      return null;
    }

    final FixWrapper myFirst = (FixWrapper) _thePositions.first();
    final FixWrapper myLast = (FixWrapper) _thePositions.last();

    if (DTG.lessThan(myFirst.getTime()) || DTG.greaterThan(myLast.getTime()))
    {
      nearest = null;
    }
    else
    {
      long distance = Long.MAX_VALUE;
      final Iterator<FixWrapper> fIter = iterator();
      while (fIter.hasNext())
      {
        final FixWrapper fix = fIter.next();
        final long dist = Math.abs(fix.getDateTimeGroup().getDate().getTime()
            - dtg);
        if (dist < distance)
        {
          nearest = fix;
          distance = dist;
        }
      }
    }

    return new Watchable[]
    {nearest};
  }

  public Enumeration<Editable> getPositionIterator()
  {
    return _thePositions.elements();
  }

  /**
   * method to allow the setting of data sampling frequencies for the track & sensor data
   *
   * @return frequency to use
   */
  public final HiResDate getResampleDataAt()
  {
    return this._lastDataFrequency;
  }

  @Override
  public PlainSymbol getSnailShape()
  {
    return null;
  }

  @Override
  public HiResDate getStartDTG()
  {
    return ((FixWrapper) _thePositions.first()).getTime();
  }

  /**
   * return the symbol frequencies for the track
   *
   * @return frequency in seconds
   */
  public final HiResDate getSymbolFrequency()
  {
    return _lastSymbolFrequency;
  }

  /**
   * font handler
   *
   * @return the font to use for the label
   */
  public final Font getTrackFont()
  {
    return _theLabel.getFont();
  }

  /**
   * get the set of fixes contained within this time period which haven't been filtered, and which
   * have valid depths. The isVisible flag indicates whether a track has been filtered or not. We
   * also have the getVisibleFixesBetween method (below) which decides if a fix is visible if it is
   * set to Visible, and it's label or symbol are visible.
   * <p/>
   * We don't have to worry about a valid depth, since 3d doesn't show points with invalid depth
   * values
   *
   * @param start
   *          start DTG
   * @param end
   *          end DTG
   * @return series of fixes
   */
  public final Collection<Editable> getUnfilteredItems(final HiResDate start,
      final HiResDate end)
  {

    // see if we have _any_ points in range
    if ((getStartDTG().greaterThan(end)) || (getEndDTG().lessThan(start)))
    {
      return null;
    }

    if (!this.getVisible())
    {
      return null;
    }

    // get ready for the output
    final Vector<Editable> res = new Vector<Editable>(0, 1);

    // put the data into a period
    final TimePeriod thePeriod = new TimePeriod.BaseTimePeriod(start, end);

    // step through our fixes
    final Enumeration<Editable> iter = getPositionIterator();
    while (iter.hasMoreElements())
    {
      final FixWrapper fw = (FixWrapper) iter.nextElement();
      if (fw.getVisible() && thePeriod.contains(fw.getTime()))
      {
        // hey, it's valid - continue
        res.add(fw);
      }
    }
    return res;
  }

  /**
   * Determine the time period for which we have visible locations
   *
   * @return
   */
  public TimePeriod getVisiblePeriod()
  {
    // ok, have we determined the visible period recently?
    final long tNow = System.currentTimeMillis();

    // how long does the cached value remain valid for?
    final long ALLOWABLE_PERIOD = 500;
    if (_cachedPeriod != null && tNow
        - _timeCachedPeriodCalculated < ALLOWABLE_PERIOD)
    {
      // still in date, use the last calculated period
    }
    else
    {
      // ok, calculate a new one
      TimePeriod res = null;
      final Enumeration<Editable> pos = getPositionIterator();
      while (pos.hasMoreElements())
      {
        final FixWrapper editable = (FixWrapper) pos.nextElement();
        if (editable.getVisible())
        {
          final HiResDate thisT = editable.getTime();
          if (res == null)
          {
            res = new TimePeriod.BaseTimePeriod(thisT, thisT);
          }
          else
          {
            res.extend(thisT);
          }
        }
      }
      // ok, store the new time period
      _cachedPeriod = res;

      // remember when it was calculated
      _timeCachedPeriodCalculated = tNow;
    }

    return _cachedPeriod;
  }

  @Override
  public boolean hasEditor()
  {
    return true;
  }

  @Override
  public boolean hasOrderedChildren()
  {
    return false;
  }

  /**
   * whether this is single point track. Single point tracks get special processing.
   *
   * @return
   */
  public boolean isSinglePointTrack()
  {
    // we want to avoid getting the size() of the list.
    // So, do fancy trick to check the first element is non-null,
    // and the second is null
    final boolean res;
    final Enumeration<Editable> elems = _thePositions.elements();
    if (elems != null && elems.hasMoreElements() && elems.nextElement() != null
        && !elems.hasMoreElements())
    {
      res = true;
    }
    else
    {
      res = false;
    }

    return res;
  }

  public boolean isVisibleAt(final HiResDate dtg)
  {
    boolean res = false;

    // special case - single track
    if (isSinglePointTrack())
    {
      // we'll assume it's never ending.
      res = true;
    }
    else
    {
      final TimePeriod visiblePeriod = getVisiblePeriod();
      if (visiblePeriod != null)
      {
        res = visiblePeriod.contains(dtg);
      }
    }

    return res;
  }

  public Iterator<FixWrapper> iterator()
  {
    final Enumeration<Editable> ele = _thePositions.elements();
    return new Iterator<FixWrapper>()
    {

      @Override
      public boolean hasNext()
      {
        return ele.hasMoreElements();
      }

      @Override
      public FixWrapper next()
      {
        return (FixWrapper) ele.nextElement();
      }
    };
  }

  /**
   * find the number of fixes in this track
   *
   * @return
   */
  public int numFixes()
  {
    return _thePositions.size();
  }

  @Override
  public synchronized void paint(final CanvasType dest)
  {
    if (!getVisible())
    {
      return;
    }

    final Color myColor = getColor();

    dest.setColor(myColor);

    final float oldWid = dest.getLineWidth();
    dest.setLineWidth(getLineThickness());

    final int len = _thePositions.size();
    final Iterator<FixWrapper> iter = iterator();
    int ctr = 0;
    final int[] xPoints = new int[len];
    final int[] yPoints = new int[len];

    WorldLocation firstLoc = null;

    // build up polyline
    while (iter.hasNext())
    {
      final FixWrapper fw = iter.next();
      if (fw.getVisible())
      {
        final WorldLocation thisLoc = fw.getLocation();
        if (firstLoc == null)
        {
          firstLoc = thisLoc;
        }
        final Point loc = dest.toScreen(thisLoc);
        xPoints[ctr] = (int) loc.getX();
        yPoints[ctr] = (int) loc.getY();
        ctr++;

        // draw the fix (including symbols)
        fw.paintMe(dest, thisLoc, myColor);
      }
    }

    // draw the line
    dest.drawPolyline(xPoints, yPoints, ctr);

    // and the track name?
    if (getNameVisible() && firstLoc != null)
    {
      final Point loc = dest.toScreen(firstLoc);
      dest.drawText(getName(), loc.x + 5, loc.y);
    }

    dest.setLineWidth(oldWid);
  }

  /**
   * get the label to paint itself
   *
   */
  protected void paintLabel(final CanvasType dest)
  {
    _theLabel.paint(dest);
  }

  @Override
  public double rangeFrom(final WorldLocation other)
  {
    double nearest = Double.MAX_VALUE;
    Enumeration<Editable> pIter = getPositionIterator();
    while(pIter.hasMoreElements())
    {
      final FixWrapper next = (FixWrapper) pIter.nextElement();
      final double dist = next.rangeFrom(other);
      nearest = Math.min(dist, nearest);
    }
    return nearest;
  }

  @Override
  public void reconnectChildObjects(final Object clonedObject)
  {
    final LightweightTrackWrapper clonedTrack =
        (LightweightTrackWrapper) clonedObject;
    final Enumeration<Editable> ele = clonedTrack.getPositionIterator();
    while (ele.hasMoreElements())
    {
      final FixWrapper fw = (FixWrapper) ele.nextElement();
      fw.setTrackWrapper(clonedTrack);
    }
  }

  @Override
  public void removeElement(final Editable point)
  {
    // see what type of object this is
    if (point instanceof FixWrapper)
    {
      final FixWrapper fw = (FixWrapper) point;
      fw.setTrackWrapper(null);
      _thePositions.removeElement(fw);
    }
  }

  /**
   * how frequently symbols are placed on the track
   *
   * @param theVal
   *          frequency in seconds
   */
  public final void setArrowFrequency(final HiResDate theVal)
  {
    this._lastArrowFrequency = theVal;

    // set the "showPositions" parameter, as long as we are
    // not setting the symbols off
    if (theVal.getMicros() != 0.0)
    {
      this.setPositionsVisible(true);
    }

    final FixSetter setSymbols = new FixSetter()
    {
      @Override
      public void execute(final FixWrapper fix, final boolean val)
      {
        fix.setArrowShowing(val);
      }
    };

    setFixes(setSymbols, theVal);
  }

  /**
   * set the colour of this track label
   *
   * @param theCol
   *          the colour
   */
  @Override
  @FireReformatted
  public final void setColor(final Color theCol)
  {
    // do the parent
    super.setColor(theCol);

    // now do our processing
    _theLabel.setColor(theCol);
  }

  /**
   * the setter function which passes through the track
   */
  private void setFixes(final FixSetter setter, final HiResDate theVal)
  {
    if (theVal == null)
    {
      return;
    }
    // do we have any positions?
    if (!getPositionIterator().hasMoreElements())
    {
      return;
    }

    final long freq = theVal.getMicros();

    // briefly check if we are revealing/hiding all times (ie if freq is 1
    // or 0)
    if (freq == TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY)
    {
      // show all of the labels
      final Enumeration<Editable> iter = getPositionIterator();
      while (iter.hasMoreElements())
      {
        final FixWrapper fw = (FixWrapper) iter.nextElement();
        setter.execute(fw, true);
      }
    }
    else
    {
      // no, we're not just blindly doing all of them. do them at the
      // correct
      // frequency

      // hide all of the labels/symbols first
      final Enumeration<Editable> enumA = getPositionIterator();
      while (enumA.hasMoreElements())
      {
        final FixWrapper fw = (FixWrapper) enumA.nextElement();
        setter.execute(fw, false);
      }
      if (freq == 0)
      {
        // we can ignore this, since we have just hidden all of the
        // points
      }
      else
      {
        if (getStartDTG() != null)
        {
          // pass through the track setting the values

          // sort out the start and finish times
          long start_time = getStartDTG().getMicros();
          final long end_time = getEndDTG().getMicros();

          // first check that there is a valid time period between start
          // time
          // and end time
          if (start_time + freq < end_time)
          {
            long num = start_time / freq;

            // we need to add one to the quotient if it has rounded down
            if (start_time % freq == 0)
            {
              // start is at our freq, so we don't need to increment
              // it
            }
            else
            {
              num++;
            }

            // calculate new start time
            start_time = num * freq;
          }
          else
          {
            // there is not one of our 'intervals' between the start and
            // the end,
            // so use the start time
          }

          long nextMarker = start_time / 1000L;
          final long freqMillis = freq / 1000L;
          final Enumeration<Editable> iter = this.getPositionIterator();
          while (iter.hasMoreElements())
          {
            final FixWrapper nextF = (FixWrapper) iter.nextElement();
            final long hisDate = nextF.getDTG().getDate().getTime();
            if (hisDate >= nextMarker)
            {
              // hmm, has there been a large jump?
              if (hisDate - nextMarker <= freqMillis)
              {
                // no. Ok, show this item. If there's a larger
                // jump, we don't automatically show this item,
                // it's better to find the next marker time.
                setter.execute(nextF, true);
              }

              // hmm, if we've just passed a huge gap, we may need to add
              // a few intervals
              while (nextMarker <= hisDate)
              {
                // carry on moving the next marker right
                nextMarker += freqMillis;
              }
            }
          }
        }
      }
    }
  }

  /**
   * set the label frequency (in seconds)
   *
   * @param theVal
   *          frequency to use
   */
  public final void setLabelFrequency(final HiResDate theVal)
  {
    this._lastLabelFrequency = theVal;

    final FixSetter setLabel = new FixSetter()
    {
      @Override
      public void execute(final FixWrapper fix, final boolean val)
      {
        fix.setLabelShowing(val);
      }
    };
    setFixes(setLabel, theVal);
  }

  protected final void setLabelLocation(final WorldLocation loc)
  {
    _theLabel.setLocation(loc);
  }

  @FireReformatted
  public void setLineStyle(final int style)
  {
    _lineStyle = style;
  }

  /**
   * the line thickness (convenience wrapper around width)
   */
  public void setLineThickness(final int val)
  {
    _lineWidth = val;
  }

  /**
   * set the name of this track (normally the name of the vessel
   *
   * @param theName
   *          the name as a String
   */
  @Override
  @FireReformatted
  public final void setName(final String theName)
  {
    _thePositions.setName(theName);
    _theLabel.setString(theName);
  }

  /**
   * the relative location of the label
   *
   * @param val
   *          the relative location
   */
  public final void setNameLocation(final Integer val)
  {
    _theLabel.setRelativeLocation(val);
  }

  /**
   * whether to show the track name
   *
   * @param val
   *          yes/no
   */
  public final void setNameVisible(final boolean val)
  {
    _theLabel.setVisible(val);
  }

  /**
   * whether to show the position fixes
   *
   * @param val
   *          yes/no
   */
  public final void setPositionsVisible(final boolean val)
  {
    _showPositions = val;
  }

  @FireExtended
  public void setResampleDataAt(final HiResDate theVal)
  {
    this._lastDataFrequency = theVal;

    // have a go at trimming the start time to a whole number of intervals
    final long interval = theVal.getMicros();

    // do we have a start time (we may just be being tested...)
    if (this.getStartDTG() == null)
    {
      return;
    }

    // just check it's not a barking frequency
    if (theVal.getDate().getTime() <= 0)
    {
      // ignore, we don't need to do anything for a zero or a -1
    }
    else
    {
      final List<Editable> newItems = new ArrayList<Editable>();

      final Enumeration<Editable> pIter = getPositionIterator();
      long nextTime = getStartDTG().getMicros();
      while (pIter.hasMoreElements())
      {
        final FixWrapper next = (FixWrapper) pIter.nextElement();
        final long thisTime = next.getDateTimeGroup().getMicros();

        if (thisTime >= nextTime)
        {
          newItems.add(next);
          nextTime += interval;
        }
      }

      // ok, clear existing items
      _thePositions.removeAllElements();

      // and store the new ones
      _thePositions.getData().addAll(newItems);

      // ok, we have to clear the bounds
      flushBounds();
    }
  }

  /**
   * how frequently symbols are placed on the track
   *
   * @param theVal
   *          frequency in seconds
   */
  public final void setSymbolFrequency(final HiResDate theVal)
  {
    this._lastSymbolFrequency = theVal;

    // set the "showPositions" parameter, as long as we are
    // not setting the symbols off
    if (theVal == null)
    {
      return;
    }
    if (theVal.getMicros() != 0.0)
    {
      this.setPositionsVisible(true);
    }

    final FixSetter setSymbols = new FixSetter()
    {
      @Override
      public void execute(final FixWrapper fix, final boolean val)
      {
        fix.setSymbolShowing(val);
      }
    };

    setFixes(setSymbols, theVal);
  }

  /**
   * font handler
   *
   * @param font
   *          the font to use for the label
   */
  public final void setTrackFont(final Font font)
  {
    _theLabel.setFont(font);
  }

  @Override
  public String toString()
  {
    return _thePositions.toString();
  }
}