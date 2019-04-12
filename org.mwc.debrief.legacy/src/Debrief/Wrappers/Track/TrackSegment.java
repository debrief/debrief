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
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Vector;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.BaseItemLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsWrappingInLayerManager;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import junit.framework.TestCase;

/**
 * a single collection of track points
 *
 * @author Administrator
 *
 */
public class TrackSegment extends BaseItemLayer implements DraggableItem,
    GriddableSeriesMarker, NeedsWrappingInLayerManager
{

  public static class testListMgt extends TestCase
  {

    private static class MockParent implements ToolParent
    {
      final private List<String> log;

      public MockParent(final List<String> log)
      {
        this.log = log;
      }

      @Override
      public void addActionToBuffer(final Action theAction)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public Map<String, String> getPropertiesLike(final String pattern)
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public String getProperty(final String name)
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public void logError(final int status, final String text,
          final Exception e)
      {
        log.add(text);
      }

      @Override
      public void logError(final int status, final String text,
          final Exception e, final boolean revealLog)
      {
        log.add(text);
      }

      @Override
      public void logStack(final int status, final String text)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void restoreCursor()
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void setCursor(final int theCursor)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void setProperty(final String name, final String value)
      {
        // TODO Auto-generated method stub

      }
    }

    private TrackSegment getDummyList()
    {
      final TrackSegment ts0 = new TrackSegment(false);
      final FixWrapper newFix1 = new FixWrapper(new Fix(new HiResDate(10000),
          new WorldLocation(1, -1, 3), 1, 2));
      final FixWrapper newFix2 = new FixWrapper(new Fix(new HiResDate(20000),
          new WorldLocation(1, 0, 3), 1, 2));
      final FixWrapper newFix3 = new FixWrapper(new Fix(new HiResDate(30000),
          new WorldLocation(1, 1, 3), 1, 2));
      final FixWrapper newFix4 = new FixWrapper(new Fix(new HiResDate(40000),
          new WorldLocation(1, 2, 3), 1, 2));
      ts0.addFix(newFix1);
      ts0.addFix(newFix2);
      ts0.addFix(newFix3);
      ts0.addFix(newFix4);
      return ts0;
    }

    private FixWrapper getFix(final long dtg, final double course,
        final double speed)
    {
      final Fix theFix = new Fix(new HiResDate(dtg), new WorldLocation(2, 2, 2),
          course, speed);
      final FixWrapper res = new FixWrapper(theFix);

      return res;
    }

    public void testResample()
    {
      final TrackWrapper tw = new TrackWrapper();
      tw.setName("Some name");

      final List<String> log = new ArrayList<String>();

      tw.add(getFix(0, 2, 4));
      tw.add(getFix(60000, 2, 4));
      tw.add(getFix(120000, 2, 4));
      tw.add(getFix(180000, 2, 4));
      tw.add(getFix(240000, 2, 4));
      tw.add(getFix(300000, 2, 4));

      final TrackSegment t1 = (TrackSegment) tw.getSegments().elements()
          .nextElement();
      assertEquals("right length", 6, t1.size());

      // do resample
      t1.setResampleDataAt(new HiResDate(120000));
      assertEquals("right length", 3, t1.size());

      t1.setResampleDataAt(new HiResDate(12000));
      assertEquals("right length", 21, t1.size());

      // also try it for a TMA segment
      tw.removeElement(t1);

      Application.initialise(new MockParent(log));

      final AbsoluteTMASegment as = new AbsoluteTMASegment(12, new WorldSpeed(4,
          WorldSpeed.Kts), new WorldLocation(2, 2, 2), new HiResDate(100000),
          new HiResDate(1000000));
      tw.add(as);
      assertEquals("right length", 16, as.size());
      assertEquals("empty log", 0, log.size());

      as.setResampleDataAt(new HiResDate(120000));

      assertEquals("still right length", 16, as.size());
      assertEquals("non-empty log", 1, log.size());

    }

    public void testTrim()
    {
      TrackSegment ts0 = getDummyList();
      TimePeriod newP = new TimePeriod.BaseTimePeriod(new HiResDate(30000),
          new HiResDate(40000));
      assertEquals("correct len", 4, ts0.size());
      ts0.trimTo(newP);
      assertEquals("correct new len", 2, ts0.size());

      ts0 = getDummyList();
      newP = new TimePeriod.BaseTimePeriod(new HiResDate(35000), new HiResDate(
          40000));
      assertEquals("correct len", 4, ts0.size());
      ts0.trimTo(newP);
      assertEquals("correct new len", 1, ts0.size());

      ts0 = getDummyList();
      newP = new TimePeriod.BaseTimePeriod(new HiResDate(15000), new HiResDate(
          40000));
      assertEquals("correct len", 4, ts0.size());
      ts0.trimTo(newP);
      assertEquals("correct new len", 3, ts0.size());

      ts0 = getDummyList();
      newP = new TimePeriod.BaseTimePeriod(new HiResDate(45000), new HiResDate(
          50000));
      assertEquals("correct len", 4, ts0.size());
      ts0.trimTo(newP);
      assertEquals("correct new len", 0, ts0.size());

    }

  }

  /**
   * class containing editable details of a track
   */
  public class TrackSegmentInfo extends Editable.EditorType
  {

    /**
     * constructor for this editor, takes the actual track as a parameter
     *
     * @param data
     *          track being edited
     */
    public TrackSegmentInfo(final TrackSegment data)
    {
      super(data, data.getName(), "");
    }

    @Override
    public final MethodDescriptor[] getMethodDescriptors()
    {

      // just add the reset color field first
      final Class<TrackSegment> c = TrackSegment.class;
      MethodDescriptor[] newMeds =
      {method(c, "revealAllPositions", null, "Reveal All Positions")};

      final MethodDescriptor[] mds = super.getMethodDescriptors();
      // we now need to combine the two sets
      if (mds != null)
      {
        final MethodDescriptor resMeds[] = new MethodDescriptor[mds.length
            + newMeds.length];
        System.arraycopy(mds, 0, resMeds, 0, mds.length);
        System.arraycopy(newMeds, 0, resMeds, mds.length, newMeds.length);
        newMeds = resMeds;
      }
      return newMeds;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {expertProp("Visible", "whether this layer is visible", FORMAT),
            displayExpertProp("LineStyle", "Line style",
                "how to plot this line", FORMAT), expertProp("Name",
                    "Name of this track segment", FORMAT),
            displayExpertLongProp("ResampleDataAt", "Resample data at",
                "the data sample rate", TEMPORAL,
                MWC.GUI.Properties.TimeFrequencyPropertyEditor.class)};
        res[1].setPropertyEditorClass(LineStylePropertyEditor.class);
        return res;
      }
      catch (final IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }
  }

  /**
   * someone to share life's troubles with
   *
   */
  protected transient static ToolParent _myParent;

  /**
  	 * 
  	 */
  private static final long serialVersionUID = 1L;

  public static final String TMA_LEADER = "TMA_";

  public static final boolean RELATIVE = true;

  public static final boolean ABSOLUTE = false;

  /**
   * learn about the shared trouble reporter...
   *
   * @param toolParent
   */
  public static void initialise(final ToolParent toolParent)
  {
    _myParent = toolParent;
  }

  /**
   * define the length of the stalk we plot when dragging
   *
   */
  private final int STALK_LEN = 200;

  /**
   * whether to determine this track's positions using DR calculations
   *
   */
  final boolean _plotRelative;

  private transient WorldVector _vecTempLastVector = null;

  protected long _vecTempLastDTG = -2;

  /**
   * how to plot this line
   *
   */
  private int _lineStyle = CanvasType.SOLID;
  private HiResDate _lastDataFrequency;

  public TrackSegment(final boolean plotRelative)
  {
    _plotRelative = plotRelative;
  }

  /**
   * constructor that builds a plain track segment from a tma segment - an operation we must do when
   * we try to merge track segments
   *
   * @param tma
   */
  public TrackSegment(final CoreTMASegment tma)
  {
    this(tma.getPlotRelative());

    setName(tma.getName());
    setVisible(tma.getVisible());
    setWrapper(tma.getWrapper());

    // add the elements from the target
    final Enumeration<Editable> points = tma.elements();
    while (points.hasMoreElements())
    {
      final Editable next = points.nextElement();
      add(next);
    }
  }

  /**
   * create a segment based on the suppplied items
   *
   * @param theItems
   */
  public TrackSegment(final SortedSet<Editable> theItems)
  {
    this(false);

    // add the items individually, so we can update the segment
    for (final Editable item : theItems)
    {
      add(item);
    }

    // now sort out the name
    sortOutDateLabel(null);
  }

  @Override
  public void add(final Editable item)
  {
    if (item instanceof FixWrapper)
    {
      addFix((FixWrapper) item);
    }
    else
    {
      System.err.println("SHOULD NOT BE ADDING NORMAL ITEM TO TRACK SEGMENT");
    }
  }

  public void addFix(final FixWrapper fix)
  {
    // remember the fix
    this.addFixSilent(fix);

    // override the name, just in case this point is earlier
    sortOutDateLabel(null);
  }

  public void addFixSilent(final FixWrapper fix)
  {
    super.add(fix);

    // store this segment in the fix
    fix.setSegment(this);

    // and register the listener (if we know our track)
    if (_myTrack != null)
    {
      // tell it about our daddy
      fix.setTrackWrapper(_myTrack);
      fix.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED, _myTrack
          .getLocationListener());
    }
  }

  /**
   * add the elements in the indicated layer to us.
   *
   */
  @Override
  public void append(final Layer other)
  {
    // get the other track's elements
    final Enumeration<Editable> enumer = other.elements();

    // have a look and see if we're a DR track
    if (this.getPlotRelative())
    {
      // right, we've got to make sure our last point is correctly
      // pointing to
      // the first point of this new track
      // - sort it out
      final FixWrapper first = (FixWrapper) enumer.nextElement();
      final FixWrapper myLast = (FixWrapper) this.last();
      final WorldVector offset = first.getLocation().subtract(myLast
          .getLocation());

      final double courseRads = offset.getBearing();
      final double timeSecs = (first.getTime().getDate().getTime() - myLast
          .getTime().getDate().getTime()) / 1000;
      // start off with the course

      // and now the speed
      final double distYds = new WorldDistance(offset.getRange(),
          WorldDistance.DEGS).getValueIn(WorldDistance.YARDS);
      final double spdYps = distYds / timeSecs;
      final double thisSpeedKts = MWC.Algorithms.Conversions.Yps2Kts(spdYps);

      myLast.setCourse(courseRads);
      myLast.setSpeed(thisSpeedKts);

      // and add this one
      addFix(first);
    }

    // ok, pass through and add the remaining items
    while (enumer.hasMoreElements())
    {
      final FixWrapper pl = (FixWrapper) enumer.nextElement();

      addFix(pl);
    }
  }

  @Override
  protected String collectiveName()
  {
    return "positions";
  }

  /**
   * sort the items in ascending order
   *
   */
  @Override
  public int compareTo(final Plottable arg0)
  {
    int res = 0;
    if (arg0 instanceof TrackSegment)
    {
      // sort them in dtg order
      final TrackSegment other = (TrackSegment) arg0;
      if ((startDTG() != null) && (other.startDTG() != null))
      {
        res = startDTG().compareTo(other.startDTG());
      }
      else
      {
        res = getName().compareTo(arg0.getName());
      }
    }
    else
    {
      // just use string comparison
      res = getName().compareTo(arg0.getName());
    }
    return res;
  }

  /**
   * switch the sample rate of this track to the supplied frequency
   *
   * @param theVal
   */
  public void decimate(final HiResDate theVal, final TrackWrapper parentTrack,
      final long startTime)
  {
    final Vector<FixWrapper> newItems = new Vector<FixWrapper>();
    final boolean oldInterpolateState = parentTrack.getInterpolatePoints();

    // switch on interpolation
    parentTrack.setInterpolatePoints(true);

    // right, are we a relative or absolute track?
    if (this.getPlotRelative())
    {
      decimateRelative(theVal, parentTrack, startTime, newItems);
    }
    else
    {
      decimateAbsolute(theVal, parentTrack, startTime, newItems);
    }

    // ditch our positions
    this.removeAllElements();

    // store the new positions
    for (final Iterator<FixWrapper> iterator = newItems.iterator(); iterator
        .hasNext();)
    {
      final FixWrapper fix = iterator.next();
      this.addFixSilent(fix);

      // and reset the label on the fix
      fix.resetName();
    }

    // re-instate the interpolate status
    parentTrack.setInterpolatePoints(oldInterpolateState);
  }

  private void decimateAbsolute(final HiResDate theVal,
      final TrackWrapper parentTrack, final long startTime,
      final Vector<FixWrapper> newItems)
  {
    // hey, our relative process actually works for absolute, too.
    decimatePointsTrack(theVal, parentTrack, startTime, newItems, false);
  }

  private void decimatePointsTrack(final HiResDate theVal,
      final TrackWrapper parentTrack, final long theStartTime,
      final Vector<FixWrapper> newItems, final boolean fixRelative)
  {
    long requiredTime = theStartTime;
    final long interval = theVal.getDate().getTime();
    FixWrapper previousPosition = null;

    final Enumeration<Editable> iter = parentTrack.getPositionIterator();
    while (iter.hasMoreElements())
    {
      final FixWrapper currentPosition = (FixWrapper) iter.nextElement();

      final long thisTime = currentPosition.getDateTimeGroup().getDate()
          .getTime();

      if (previousPosition == null)
      {
        // if this is on or after our time, we should use it
        if (thisTime >= requiredTime)
        {
          // and move forwards
          requiredTime += interval;

          // we should also store this, as the first position
          final FixWrapper storeMe = new FixWrapper(currentPosition.getFix());

          // and add it
          newItems.add(storeMe);
        }
      }
      else
      {
        // ok, we've got a before. Generate points while we're after the required time
        while (thisTime >= requiredTime)
        {
          // ok, we need to generate a position at the new time
          final FixWrapper newPos;
          if (thisTime == requiredTime)
          {
            // ok, we can just use this one
            newPos = new FixWrapper(currentPosition.getFix());
          }
          else
          {
            // ok, we need to generate
            newPos = FixWrapper.interpolateFix(previousPosition,
                currentPosition, new HiResDate(requiredTime));
          }

          // store the color for this item
          final Color hisColor = currentPosition.getActualColor();
          if (hisColor != null)
          {
            newPos.setColor(hisColor);
          }

          // do we need to fix the track to ensure a DR reconstruction
          if (fixRelative)
          {
            // start off with the course
            final WorldVector offset = newPos.getLocation().subtract(
                previousPosition.getLocation());
            newPos.getFix().setCourse(offset.getBearing());

            // and now the speed
            final double distYds = new WorldDistance(offset.getRange(),
                WorldDistance.DEGS).getValueIn(WorldDistance.YARDS);
            final double timeSecs = (requiredTime - previousPosition.getTime()
                .getDate().getTime()) / 1000d;
            final double spdYps = distYds / timeSecs;
            newPos.getFix().setSpeed(spdYps);
          }

          // do we correct the name?
          if (newPos.getName().equals(FixWrapper.INTERPOLATED_FIX))
          {
            // reset the name
            newPos.resetName();
          }

          // add to our working list
          newItems.add(newPos);

          // and move fowards
          requiredTime += interval;
        }

      }
      // and move our marker forward
      previousPosition = currentPosition;
    }
  }

  private void decimateRelative(final HiResDate theVal,
      final TrackWrapper parentTrack, final long startTime,
      final Vector<FixWrapper> newItems)
  {
    long theStartTime = startTime;

    // get the time interval
    final long interval = theVal.getMicros();

    // round myStart time to the supplied interval
    long myStart = this.startDTG().getMicros();
    myStart = (myStart / interval) * interval;

    // back to millis
    myStart /= 1000L;

    // set the start time to be the later of our start time and the provided
    // time
    theStartTime = Math.max(theStartTime, myStart);

    if (this instanceof CoreTMASegment)
    {
      decimateRelativeTMA(theVal, newItems, theStartTime);
    }
    else
    {
      decimatePointsTrack(theVal, parentTrack, theStartTime, newItems, true);
    }
  }

  private void decimateRelativeTMA(final HiResDate theVal,
      final Vector<FixWrapper> newItems, final long theStartTime)
  {
    long tNow;
    final CoreTMASegment tma = (CoreTMASegment) this;

    final long startMicros = theStartTime * 1000L;

    // hey, it's a TMA segment - on steady course/speed. cool
    final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(tma
        .getCourse());
    final double speedYps = tma.getSpeed().getValueIn(WorldSpeed.ft_sec) / 3;

    // find the new start location - after we've slipped
    final WorldLocation myStartLocation = new WorldLocation(tma
        .getTrackStart());

    // right - sort out what time period we're working through
    for (tNow = startMicros; tNow <= endDTG().getMicros(); tNow += theVal
        .getMicros())
    {
      final Fix theFix = new Fix(new HiResDate(0, tNow), new WorldLocation(
          myStartLocation), courseRads, speedYps);
      final FixWrapper newFix = new FixWrapper(theFix);
      newFix.setSymbolShowing(true);

      // also give it a name
      newFix.resetName();

      newItems.add(newFix);
    }

    // right, if it's a relative segment, then we need to shift the
    // offset to
    // reflect the new relationship
    if (tma instanceof RelativeTMASegment)
    {
      final FixWrapper myStarter = (FixWrapper) tma.first();
      final FixWrapper myEnder = (FixWrapper) tma.last();
      final HiResDate startDTG = new HiResDate(0, theStartTime);
      final FixWrapper newStarter = FixWrapper.interpolateFix(myStarter,
          myEnder, startDTG);
      final WorldLocation newStartLoc = newStarter.getLocation();

      final RelativeTMASegment rel = (RelativeTMASegment) tma;
      final Watchable[] newHost = rel.getReferenceTrack().getNearestTo(
          startDTG);
      if (newHost.length > 0)
      {
        final WorldLocation newOrigin = newHost[0].getLocation();
        final WorldVector newOffset = newStartLoc.subtract(newOrigin);
        rel.setOffset(newOffset);
      }

      // is our name date-oriented?
      if (rel.getName().startsWith(TMA_LEADER))
      {
        // yes, calculate a new one

        // lastly, reset the track name
        rel.sortOutDateLabel(startDTG);

        // and change the track name
        rel._myTrack.setName(rel.getName());
      }
    }
  }

  @Override
  public void doSave(final String message)
  {
    throw new RuntimeException(
        "should not have called manual save for Track Segment");
  }

  protected void drawMyStalk(final CanvasType dest, final Point lastPoint,
      final Point thisPoint, final boolean forwards)
  {
    // yup, we've now got just two points. plot a 'back-trace'
    final double xDelta = thisPoint.x - lastPoint.x;
    final double yDelta = thisPoint.y - lastPoint.y;

    final double gradient = xDelta / yDelta;

    int myLen = STALK_LEN;
    if (!forwards)
    {
      myLen = -STALK_LEN;
    }

    final Point backPoint = new Point(lastPoint.x + (int) (myLen * gradient),
        lastPoint.y + myLen);
    dest.setLineStyle(2);
    dest.drawLine(lastPoint.x, lastPoint.y, backPoint.x, backPoint.y);

    // hey, chuck in a circle
    final int radius = 10;
    dest.drawOval(lastPoint.x - radius - (int) xDelta, lastPoint.y - radius
        - (int) yDelta, radius * 2, radius * 2);

    dest.setLineStyle(CanvasType.SOLID);
  }

  public HiResDate endDTG()
  {
    HiResDate res = null;
    final Collection<Editable> items = getData();
    if ((items != null && (!items.isEmpty())))
    {
      final SortedSet<Editable> sortedItems = (SortedSet<Editable>) items;
      final Editable last = sortedItems.last();
      final FixWrapper fw = (FixWrapper) last;
      res = fw.getDateTimeGroup();
    }
    return res;
  }

  @Override
  public void findNearestHotSpotIn(final Point cursorPos,
      final WorldLocation cursorLoc, final LocationConstruct currentNearest,
      final Layer parentLayer, final Layers theData)
  {
  }

  @Override
  public Editable.EditorType getInfo()
  {
    return new TrackSegmentInfo(this);
  }

  /**
   * how this line is plotted
   *
   * @return
   */
  public int getLineStyle()
  {
    return _lineStyle;
  }

  public boolean getPlotRelative()
  {
    return _plotRelative;
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
  public Editable getSampleGriddable()
  {
    final HiResDate theTime = new HiResDate(10000000);
    final WorldLocation theLocation = new WorldLocation(1, 1, 1);
    final double courseRads = 3;
    final double speedKts = 5;
    final Fix newFix = new Fix(theTime, theLocation, courseRads, speedKts);
    final FixWrapper res = new FixWrapper(newFix);
    return res;
  }

  /**
   * get the start of this segment (it's the location of the first point).
   *
   * @return
   */
  public WorldLocation getTrackStart()
  {
    final FixWrapper firstW = (FixWrapper) getData().iterator().next();
    return firstW.getFixLocation();
  }

  @Override
  public TimeStampedDataItem makeCopy(final TimeStampedDataItem item)
  {
    if (false == item instanceof FixWrapper)
    {
      throw new IllegalArgumentException(
          "I am expecting a position, don't know how to copy " + item);
    }
    final FixWrapper template = (FixWrapper) item;
    final FixWrapper result = new FixWrapper(template.getFix().makeCopy());
    result.setLabelShowing(template.getLabelShowing());
    result.setLineShowing(template.getLineShowing());
    result.setSymbolShowing(template.getSymbolShowing());
    result.setArrowShowing(template.getArrowShowing());
    result.setLabelLocation(template.getLabelLocation());

    final Color col = template.getActualColor();
    if (col != null)
    {
      result.setColor(col);
    }

    return result;
  }

  @Override
  public void paint(final CanvasType dest)
  {
    final Collection<Editable> items = getData();

    // ok - draw that line!
    Point lastPoint = null;
    Point lastButOne = null;
    for (final Iterator<Editable> iterator = items.iterator(); iterator
        .hasNext();)
    {
      final FixWrapper thisF = (FixWrapper) iterator.next();

      final Point thisPoint = dest.toScreen(thisF.getFixLocation());

      // do we have enough for a line?
      if (lastPoint != null)
      {
        // draw that line
        dest.drawLine(lastPoint.x, lastPoint.y, thisPoint.x, thisPoint.y);

        // are we at the start of the line?
        if (lastButOne == null)
        {
          drawMyStalk(dest, lastPoint, thisPoint, false);
        }
      }

      lastButOne = lastPoint;
      lastPoint = new Point(thisPoint);

      // also draw in a marker for this point
      dest.drawRect(lastPoint.x - 1, lastPoint.y - 1, 3, 3);
    }

    // lastly 'plot on' from the last points
    drawMyStalk(dest, lastPoint, lastButOne, true);

  }

  @Override
  public double rangeFrom(final WorldLocation other)
  {
    double res = Plottable.INVALID_RANGE;

    // have we got data?
    final WorldLocation firstLoc = this.getTrackStart();

    // do we have a start point?
    if (firstLoc != null && !this.isEmpty())
    {
      // yes, sort range
      res = firstLoc.rangeFrom(other);

      // and try for the track end
      final Plottable lastP = this.last();
      // do we have an end point?
      if (lastP != null)
      {
        final FixWrapper lastF = (FixWrapper) lastP;
        final WorldLocation lastLoc = lastF.getLocation();
        final double otherRng = lastLoc.rangeFrom(other);
        res = Math.min(otherRng, res);
      }
    }
    return res;
  }

  @Override
  public void removeElement(final Editable p)
  {
    // disconnect this fix from us
    final FixWrapper fw = (FixWrapper) p;
    fw.setSegment(null);

    // and let the list do its stuff
    super.removeElement(p);

    // override the name, just in case we've deleted the first point
    sortOutDateLabel(null);
  }

  @Override
  public boolean requiresManualSave()
  {
    return false;
  }

  /**
   * utility method to reveal all positions in a track
   *
   */
  @FireReformatted
  public void revealAllPositions()
  {
    final Enumeration<Editable> theEnum = elements();
    while (theEnum.hasMoreElements())
    {
      final Editable editable = theEnum.nextElement();
      final FixWrapper fix = (FixWrapper) editable;
      fix.setVisible(true);
    }
  }

  /**
   * rotate this whole track around the supplied origin
   *
   * @param brg
   *          angle to rotate through (Radians)
   * @param origin
   *          origin of rotation, probably one end of the track
   */
  public void rotate(final double brg, final WorldLocation origin)
  {
    // add this vector to all my points.
    final Collection<Editable> items = getData();
    for (final Iterator<Editable> iterator = items.iterator(); iterator
        .hasNext();)
    {
      final FixWrapper thisFix = (FixWrapper) iterator.next();

      // is this us?
      if (thisFix.getLocation() == origin)
      {
        // ignore, it's the origin
      }
      else
      {
        final WorldLocation newLoc = thisFix.getLocation().rotatePoint(origin,
            brg);
        thisFix.setFixLocation(newLoc);
      }
    }
  }

  /**
   * specify how this line is to be plotted
   *
   * @param lineStyle
   */
  public void setLineStyle(final int lineStyle)
  {
    _lineStyle = lineStyle;
  }

  /**
   * set the data frequency (in seconds) for the track & sensor data
   *
   * @param theVal
   *          frequency to use
   */
  @FireExtended
  public final void setResampleDataAt(final HiResDate theVal)
  {
    this._lastDataFrequency = theVal;

    // just check that we're not a TMA segment. We can't do TMA tracks from here
    // because it's the top level TrackWrapper that is able to re-connect the legs.
    if (this instanceof CoreTMASegment)
    {
      Application.logError2(ErrorLogger.WARNING, "Can't resample TMA track",
          null);
      return;
    }

    // have a go at trimming the start time to a whole number of intervals
    final long intervalMicros = theVal.getMicros();

    // do we have a start time (we may just be being tested...)
    if (!this.elements().hasMoreElements())
    {
      return;
    }

    // get the first item
    final FixWrapper first = (FixWrapper) elements().nextElement();

    // sort out the start time & time steps
    final long currentStartMicros = first.getTime().getMicros();
    long startTimeMicros = (currentStartMicros / intervalMicros)
        * intervalMicros;

    // just check we're in the range
    if (startTimeMicros < currentStartMicros)
    {
      startTimeMicros += intervalMicros;
    }

    // back into millis
    final long startTime = startTimeMicros / 1000L;

    // just check it's not a barking frequency
    if (theVal.getDate().getTime() <= 0)
    {
      // ignore, we don't need to do anything for a zero or a -1
    }
    else
    {
      // get the parent track
      final TrackWrapper parent = this.getWrapper();

      // and do the decimate
      decimate(theVal, parent, startTime);
    }
  }

  @Override
  public void setWrapper(final TrackWrapper wrapper)
  {

    // is it different?
    if (wrapper == _myTrack)
    {
      return;
    }

    // ok, and clear the property change listeners, if necessary
    if (_myTrack != null)
    {
      // work through our fixes
      final Collection<Editable> items = getData();
      for (final Iterator<Editable> iterator = items.iterator(); iterator
          .hasNext();)
      {
        final FixWrapper fix = (FixWrapper) iterator.next();
        // now clear this property listener
        fix.removePropertyChangeListener(PlainWrapper.LOCATION_CHANGED, _myTrack
            .getLocationListener());
      }
    }

    // store the value
    super.setWrapper(wrapper);

    if (wrapper != null)
    {
      // update our segments
      final Collection<Editable> items = getData();
      for (final Iterator<Editable> iterator = items.iterator(); iterator
          .hasNext();)
      {
        final FixWrapper fix = (FixWrapper) iterator.next();
        fix.setTrackWrapper(_myTrack);
        // and let the track wrapper listen to location changed events
        fix.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED, wrapper
            .getLocationListener());
      }
    }
  }

  @Override
  public void shift(final WorldVector vector)
  {
    // add this vector to all my points.
    final Collection<Editable> items = getData();
    for (final Iterator<Editable> iterator = items.iterator(); iterator
        .hasNext();)
    {
      final FixWrapper thisFix = (FixWrapper) iterator.next();

      final WorldLocation copiedLoc = new WorldLocation(thisFix.getFix()
          .getLocation());
      copiedLoc.addToMe(vector);

      // and replace the location (this method updates all 3 location
      // contained
      // in the fix wrapper
      thisFix.setFixLocation(copiedLoc);
    }
  }

  /**
   * move the whole of the track be the provided offset
   */
  public final void shiftTrack(final Enumeration<Editable> theEnum,
      final WorldVector offset)
  {
    Enumeration<Editable> enumA = theEnum;
    if (enumA == null)
    {
      enumA = elements();
    }

    while (enumA.hasMoreElements())
    {
      final Object thisO = enumA.nextElement();
      if (thisO instanceof FixWrapper)
      {
        final FixWrapper fw = (FixWrapper) thisO;

        final WorldLocation copiedLoc = new WorldLocation(fw.getFix()
            .getLocation());
        copiedLoc.addToMe(offset);

        // and replace the location (this method updates all 3 location
        // contained
        // in the fix wrapper
        fw.setFixLocation(copiedLoc);

        // ok - job well done
      }
    }
  }

  public void sortOutDateLabel(final HiResDate startDTG)
  {
    HiResDate theStartDTG = startDTG;
    if (!getData().isEmpty())
    {
      if (theStartDTG == null)
      {
        theStartDTG = startDTG();
      }

      setName(FormatRNDateTime.toString(theStartDTG.getDate().getTime()));
    }
  }

  /**
   * find the start time of each segment
   *
   * @return
   */
  public HiResDate startDTG()
  {
    HiResDate res = null;
    final Collection<Editable> items = getData();
    final SortedSet<Editable> sortedItems = (SortedSet<Editable>) items;
    if ((sortedItems != null) && (!sortedItems.isEmpty()))
    {
      final Editable first = sortedItems.first();
      final FixWrapper fw = (FixWrapper) first;
      res = fw.getDateTimeGroup();
    }
    return res;
  }

  @Override
  public boolean supportsAddRemove()
  {
    return true;
  }

  public void trimTo(final TimePeriod period)
  {
    final java.util.SortedSet<Editable> newList =
        new java.util.TreeSet<Editable>();

    Iterator<Editable> iter = getData().iterator();
    while (iter.hasNext())
    {
      final FixWrapper thisE = (FixWrapper) iter.next();
      if (period.contains(thisE.getTime()))
      {
        newList.add(thisE);
      }
    }

    // clear the existing list
    super.removeAllElements();

    // ok, copy over the new list
    iter = newList.iterator();
    while (iter.hasNext())
    {
      final Editable editable = iter.next();
      super.add(editable);
    }
  }

  @SuppressWarnings("unused")
  @Deprecated
  /**
   * we've deprecated this, since it gets very compuationally expensive when processing a very long
   * track
   * 
   * @param theVal
   * @param parentTrack
   * @param theStartTime
   * @param newItems
   */
  private void usingNearest(final HiResDate theVal,
      final TrackWrapper parentTrack, final long theStartTime,
      final Vector<FixWrapper> newItems)
  {
    long tNow;
    FixWrapper lastPositionStored = null;
    FixWrapper currentPosition = null;
    tNow = 0;
    //
    // right - sort out what time period we're working through
    for (tNow = theStartTime; tNow <= endDTG().getMicros(); tNow += theVal
        .getMicros())
    {

      // find hte new datum
      final Watchable[] matches = parentTrack.getNearestTo(new HiResDate(0,
          tNow));
      if (matches.length > 0)
      {
        // remember the last position - we;re going to be
        // calculating future
        // courses and speeds from it
        lastPositionStored = currentPosition;

        currentPosition = (FixWrapper) matches[0];

        // is this our first point?
        if (lastPositionStored != null)
        {
          // start off with the course
          final WorldVector offset = currentPosition.getLocation().subtract(
              lastPositionStored.getLocation());
          lastPositionStored.getFix().setCourse(offset.getBearing());

          // and now the speed
          final double distYds = new WorldDistance(offset.getRange(),
              WorldDistance.DEGS).getValueIn(WorldDistance.YARDS);
          final double timeSecs = (tNow - lastPositionStored.getTime()
              .getMicros()) / 1000000d;
          final double spdYps = distYds / timeSecs;
          lastPositionStored.getFix().setSpeed(spdYps);

          // do we correct the name?
          if (lastPositionStored.getName().equals(FixWrapper.INTERPOLATED_FIX))
          {
            // reset the name
            lastPositionStored.resetName();
          }
          // add to our working list
          newItems.add(lastPositionStored);
        }
      }
    }
  }

  /**
   * represent the named leg as a DR vector
   *
   * @param fw
   *          the leg we're looking at
   * @param period
   *          how long it's travelling for (millis)
   * @return a vector representing the subject
   */
  public WorldVector vectorFor(final long period, final double speedKts,
      final double courseRads)
  {
    // have we already looked for this
    if (period != _vecTempLastDTG)
    {
      // nope better calc it
      final double timeHrs = period / (1000d * 60d * 60d);
      final double distanceNm = speedKts * timeHrs;
      final WorldDistance dist = new WorldDistance(distanceNm,
          WorldDistance.NM);
      _vecTempLastVector = new WorldVector(courseRads, dist, null);
    }

    return _vecTempLastVector;
  }

  @Override
  public Layer wrapMe(final Layers layers)
  {
    // right, put the segment into a TrackWrapper
    final TrackWrapper newTrack = new TrackWrapper();
    newTrack.setName(this.getName());
    newTrack.setColor(Color.red);
    newTrack.add(this);

    return newTrack;
  }

}