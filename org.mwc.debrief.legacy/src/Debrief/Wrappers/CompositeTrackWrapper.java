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
package Debrief.Wrappers;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import Debrief.Wrappers.Track.PlanningSegment;
import Debrief.Wrappers.Track.PlanningSegment.ClosingSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PlanningLegCalcModelPropertyEditor;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * class that represents a series of track legs, but a single start time/location
 * 
 * @author ian
 * 
 */
public class CompositeTrackWrapper extends TrackWrapper implements
    GriddableSeriesMarker, NeedsToKnowAboutLayers
{

  public static interface GiveMeALeg
  {
    public void createLegFor(Layer parent);
  }

  /**
   * class containing editable details of a track
   */
  public final class CompositeTrackInfo extends Editable.EditorType implements
      Editable.DynamicDescriptors
  {

    /**
     * constructor for this editor, takes the actual track as a parameter
     * 
     * @param data
     *          track being edited
     */
    public CompositeTrackInfo(final CompositeTrackWrapper data)
    {
      super(data, data.getName(), "");
    }

    @Override
    public final MethodDescriptor[] getMethodDescriptors()
    {
      // just add the reset color field first
      final Class<CompositeTrackWrapper> c = CompositeTrackWrapper.class;

      final MethodDescriptor[] mds =
          {
              method(c, "addLeg", null, "Add new leg"),
              method(c, "addClosingLeg", null, "Add closing leg"),
              method(c, "exportThis", null, "Export Shape"),
              method(c, "appendReverse", null,
                  "Append reverse version of segments"),};

      return mds;
    }

    @Override
    public final String getName()
    {
      return super.getName();
    }

    @Override
    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
            {
                expertProp("Origin", "where this track starts", FORMAT),
                displayExpertProp("StartDate", "Start date",
                    "the time this track starts", FORMAT),
                displayExpertLongProp("LabelFrequency", "Label frequency",
                    "the label frequency", TEMPORAL, 
                    MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
                displayExpertLongProp("SymbolFrequency", "Symbol frequency",
                    "the symbol frequency",TEMPORAL, 
                    MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
                displayExpertLongProp("ResampleDataAt", "Resample data at",
                    "the data sample rate",TEMPORAL, 
                    MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
                displayExpertLongProp("ArrowFrequency", "Arrow frequency",
                    "the direction marker frequency",TEMPORAL, 
                    MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
                displayExpertProp("SymbolColor", "Symbol color",
                    "the color of the symbol (when used)", FORMAT),
                displayExpertProp("TrackFont", "Track font",
                    "the track label font", FORMAT),
                displayExpertProp("NameVisible", "Name visible",
                    "show the track label", VISIBILITY),
                displayExpertProp("NameAtStart", "Name at start",
                    "whether to show the track name at the start (or end)",
                    VISIBILITY),
                expertProp("Name", "the track name", FORMAT),
                expertProp("Color", "the track color", FORMAT),
                displayExpertLongProp("SymbolType", "Symbol type",
                    "the type of symbol plotted for this label", FORMAT,
                    MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor.class),

            };
        return res;
      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }

  private static GiveMeALeg _triggerNewLeg;

  private static ToolParent _toolParent;

  private HiResDate _startDate;
  private WorldLocation _origin;

  public CompositeTrackWrapper(final HiResDate startDate,
      final WorldLocation centre)
  {
    super();

    _startDate = startDate;
    if (centre != null)
      _origin = new WorldLocation(centre);

    // we don't store a track-level color, just at leg level, so set it to null
    this.setColor(null);

    // give us a neater set of intervals
    this.setSymbolFrequency(new HiResDate(0,
        TimeFrequencyPropertyEditor._5_MINS));
    this.setLabelFrequency(new HiResDate(0,
        TimeFrequencyPropertyEditor._15_MINS));
  }

  @Override
  public Color getColor()
  {
    return super.getColor();
  }

  @Override
  public void findNearestHotSpotIn(final Point cursorPos,
      final WorldLocation cursorLoc, final ComponentConstruct currentNearest,
      final Layer parentLayer)
  {
    // initialise thisDist, since we're going to be over-writing it
    WorldDistance thisDist = new WorldDistance(0, WorldDistance.DEGS);

    final CompositeTrackWrapper thisTrack = this;
    
    final Enumeration<Editable> numer = getSegments().elements();
    while (numer.hasMoreElements())
    {
      final PlanningSegment thisSeg = (PlanningSegment) numer.nextElement();
      if (thisSeg.getVisible())
      {
        // produce a location for the end
        final FixWrapper endFix = (FixWrapper) thisSeg.last();
        if (endFix != null)
        {

          // how far away is it?
          thisDist = endFix.getLocation().rangeFrom(cursorLoc, thisDist);

          final WorldLocation fixLocation =
              new WorldLocation(endFix.getLocation())
              {
                private static final long serialVersionUID = 1L;

                /**
                 * the delta that we jump our dragged angles to
                 * 
                 */
                private static final double ANGLE_DELTA = 5d;

                @Override
                public void addToMe(final WorldVector delta)
                {
                  super.addToMe(delta);

                  // so, what's the bearing back to the leg start?
                  final double brgRads =
                      super
                          .bearingFrom(thisSeg.first().getBounds().getCentre());

                  // ok, off to degrees
                  double brgDegs =
                      MWC.Algorithms.Conversions.Rads2Degs(brgRads);

                  // trim it to being positive
                  if (brgDegs < 0)
                    brgDegs += 360;

                  // limit the bearing to the nearest tidy angle
                  final int m = (int) Math.round(brgDegs / ANGLE_DELTA);
                  double newBearing = m * ANGLE_DELTA;

                  // done
                  thisSeg.setCourse(newBearing);
                  
                  // fire an update. We need to do this, because
                  // the course change actually resulted in new fixes
                  // being created. It's only the EXTENDED event
                  // that re-scans the positions
                  firePropertyChange(EXTENDED, null, thisTrack);
                }
              };
          // try range
          currentNearest
              .checkMe(this, thisDist, null, parentLayer, fixLocation);
        }
      }
    }
  }

  @Override
  public void shift(final WorldVector vector)
  {
    this.getOrigin().addToMe(vector);
    recalculate();
  }

  public HiResDate getStartDate()
  {
    return _startDate;
  }

  @FireExtended
  public void setStartDate(final HiResDate startDate)
  {
    this._startDate = startDate;
    recalculate();
  }

  public WorldLocation getOrigin()
  {
    return _origin;
  }

  public void setOrigin(final WorldLocation origin)
  {
    this._origin = origin;
    recalculate();
  }

  /**
   * the editable details for this track
   * 
   * @return the details
   */
  @Override
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new CompositeTrackInfo(this);

    return _myEditor;
  }

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  @Override
  public Enumeration<Editable> contiguousElements()
  {
    return this.getSegments().elements();
  }

  /**
   * popup a dialog to add a new leg
   * 
   */
  public void addLeg()
  {
    if (_triggerNewLeg != null)
      _triggerNewLeg.createLegFor(this);
    else
    {
      if (_toolParent != null)
        _toolParent.logError(ToolParent.ERROR,
            "CompositeTrackWrapper does not have leg-trigger helper", null);
      else
        throw new RuntimeException(
            "CompositeTrackWrapper has not been configured in app start");
    }
  }

  /**
   * popup a dialog to add a new leg
   * 
   */
  @FireExtended
  public void addClosingLeg()
  {
    this.add(new ClosingSegment("Closing segment", 45, new WorldSpeed(12,
        WorldSpeed.Kts), new WorldDistance(2, WorldDistance.NM)));
  }

  @FireExtended
  public void appendReverse()
  {
    // ok, get the legs
    final SegmentList list = super.getSegments();
    final ArrayList<PlanningSegment> holder = new ArrayList<PlanningSegment>();
    final Enumeration<Editable> iterator = list.elements();
    while (iterator.hasMoreElements())
    {
      // put this element at the start
      holder.add(0, (PlanningSegment) iterator.nextElement());
    }

    // now run the legs back in reverse
    final Iterator<PlanningSegment> iter2 = holder.iterator();
    while (iter2.hasNext())
    {
      final PlanningSegment pl = iter2.next();

      try
      {
        final PlanningSegment pl2 = (PlanningSegment) pl.clone();

        // now reverse it
        double newCourse = pl2.getCourse() + 180d;
        if (newCourse > 360)
          newCourse -= 360;
        pl2.setCourse(newCourse);

        // show the name as reversed
        pl2.setName(pl2.getName() + "(R)");

        // ok, now add it
        this.add(pl2);
      }
      catch (final CloneNotSupportedException e)
      {
        e.printStackTrace();
      }

    }

    // ok, better throw in a recalculate
    this.recalculate();
  }

  @Override
  public void add(final Editable point)
  {
    if (point instanceof PlanningSegment)
    {

      PlanningSegment segment = (PlanningSegment) point;
      
      // hmm, does it already have positions?
      final boolean resetFormatting;
      if(segment.isEmpty())
      {
        resetFormatting = true;
      }
      else
      {
        resetFormatting = false;
      }
      
      // hey, is this a closing segment?
      if (point instanceof ClosingSegment)
      {
        // do we already have one?
        if (this.getSegments().last() instanceof ClosingSegment)
        {
          // skip....
          _toolParent.logError(ToolParent.WARNING,
              "Already have closing segment", null);
        }
      }

      // take a copy of the name, to stop it getting manmgled
      final String name = segment.getName();

      super.add(segment);

      if (segment.getName() != name)
        segment.setName(name);

      // better do a recalc, aswell
      recalculate();
      
      if (resetFormatting)
      {
        // ok, show all symbols in the segment
        Enumeration<Editable> iter = segment.elements();
        while (iter.hasMoreElements())
        {
          FixWrapper thisFix = (FixWrapper) iter.nextElement();
          thisFix.setSymbolShowing(true);
          thisFix.resetName();
          thisFix.resetLabelLocation();
        }
      }
    }
    else
    {
      throw new RuntimeException(
          "can't add this type to a composite track wrapper");
    }
  }

  @Override
  public void addFix(final FixWrapper theFix)
  {
    throw new RuntimeException("can't add a fix to this composite track");
  }

  @Override
  public void append(final Layer other)
  {
    throw new RuntimeException(
        "can't add another layer to this composite track");
  }

  public void recalculate()
  {
    WorldLocation thisOrigin = getOrigin();
    if (thisOrigin == null)
      return;

    HiResDate thisDate = getStartDate();
    final Enumeration<Editable> numer = getSegments().elements();
    while (numer.hasMoreElements())
    {
      final Editable editable = (Editable) numer.nextElement();
      final PlanningSegment seg = (PlanningSegment) editable;

      PlanningCalc theCalc = null;
      final int model = seg.getCalculation();
      switch (model)
      {
      case PlanningLegCalcModelPropertyEditor.RANGE_SPEED:
        theCalc = new FromRangeSpeed();
        break;
      case PlanningLegCalcModelPropertyEditor.RANGE_TIME:
        theCalc = new FromRangeTime();
        break;
      case PlanningLegCalcModelPropertyEditor.SPEED_TIME:
      default:
        theCalc = new FromSpeedTime();
        break;
      }

      // see if this is the closing segment
      if (seg instanceof ClosingSegment)
      {
        // what's the range and bearing back to the origin
        final WorldVector offset = getOrigin().subtract(thisOrigin);

        // and store it.
        seg.setSpeedSilent(new WorldSpeed(12, WorldSpeed.Kts));
        seg.setDistanceSilent(new WorldDistance(offset.getRange(),
            WorldDistance.DEGS));
        seg.setCourseSilent(MWC.Algorithms.Conversions.Rads2Degs(offset
            .getBearing()));
        seg.setDepthSilent(new WorldDistance(offset.getDepth(),
            WorldDistance.METRES));

      }

      theCalc.construct(seg, thisOrigin, thisDate);

      // did we generate anything?
      if (!seg.isEmpty())
      {
        // ok, now update the date/location
        thisOrigin = seg.last().getBounds().getCentre();
        thisDate = seg.endDTG();
      }
    }
  }

  private abstract static class PlanningCalc
  {
    void construct(final PlanningSegment seg, final WorldLocation origin,
        final HiResDate date)
    {
      // check we have some data
      if (date == null || origin == null)
        return;

      final double timeTravelledMillis = getSecsTravelled(seg) * 1000;
      WorldLocation theOrigin = origin;

      // remember the existing items, so we
      // can preserve the formatting
      seg.cacheExistingElements();

      // ditch the existing items
      seg.removeAllElements();

      // ok build for this segment
      final double courseDegs = seg.getCourse();
      final double courseRads =
          MWC.Algorithms.Conversions.Degs2Rads(courseDegs);

      final long timeMillis = date.getDate().getTime();
      final long timeStepMillis;
      final long ONE_MIN = 60 * 1000;
      final long ONE_HOUR = 60 * ONE_MIN;
      final long ONE_DAY = 24 * ONE_HOUR;

      // use a time step appropriate to how long we're generating the track for
      if (timeTravelledMillis <= 4 * ONE_HOUR)
        timeStepMillis = ONE_MIN;
      else if (timeTravelledMillis <= 12 * ONE_HOUR)
        timeStepMillis = 10 * ONE_MIN;
      else if (timeTravelledMillis <= 2 * ONE_DAY)
        timeStepMillis = 30 * ONE_MIN;
      else
        timeStepMillis = ONE_HOUR;

      // now work out how far he will have travelled in a time step
      final double distPerMinute = getMinuteDelta(seg);
      double distPerStep = distPerMinute * (timeStepMillis / ONE_MIN);
      WorldVector vec =
          new WorldVector(courseRads, new WorldDistance(distPerStep,
              WorldDistance.METRES), null);

      long tNow = timeMillis;
      while (tNow <= (timeMillis + timeTravelledMillis))
      {
        if (timeStepMillis > timeTravelledMillis)
        {
          double remaining = timeTravelledMillis;
          distPerStep = distPerMinute * ((remaining) / ONE_MIN);
          vec =
              new WorldVector(courseRads, new WorldDistance(distPerStep,
                  WorldDistance.METRES), null);
        }
        theOrigin = addFix(seg, theOrigin, courseRads, vec, tNow);
        double remaining = timeMillis + timeTravelledMillis - tNow;
        if (remaining > timeStepMillis)
        {
          tNow += timeStepMillis;
          if (remaining - timeStepMillis < timeStepMillis)
          {
            // calculate distance for fractional step
            distPerStep =
                distPerMinute * ((remaining - timeStepMillis) / ONE_MIN);
            vec =
                new WorldVector(courseRads, new WorldDistance(distPerStep,
                    WorldDistance.METRES), null);
          }
        }
        else
        {
          if (remaining > 0)
          {
            // add the last fix
            tNow += remaining;
            theOrigin = addFix(seg, theOrigin, courseRads, vec, tNow);

          }
          break;
        }
      }
      
      // and ditch any cached fixes
      seg.clearCachedFixes();
    }

    private WorldLocation addFix(final PlanningSegment seg,
        WorldLocation theOrigin, final double courseRads,
        final WorldVector vec, long tNow)
    {
      final HiResDate thisDtg = new HiResDate(tNow);

      // ok, do this fix
      final Fix thisF =
          new Fix(thisDtg, theOrigin, courseRads, seg.getSpeed().getValueIn(
              WorldSpeed.ft_sec) / 3);

      // override the depth
      thisF.getLocation().setDepth(
          seg.getDepth().getValueIn(WorldDistance.METRES));

      final FixWrapper fw = new FixWrapper(thisF);
      
      // reset the name, we're not going to use a human generated one
      fw.resetName();

      // and store it
      seg.add(fw);

      // produce a new position
      theOrigin = theOrigin.add(vec);
      return theOrigin;
    }

    protected abstract double getSecsTravelled(PlanningSegment seg);

    abstract double getMinuteDelta(PlanningSegment seg);
  }

  private static class FromRangeSpeed extends PlanningCalc
  {

    @Override
    double getMinuteDelta(final PlanningSegment seg)
    {
      // find out how far it travels
      final double distPerMinute =
          seg.getSpeed().getValueIn(WorldSpeed.M_sec) * 60d;
      return distPerMinute;
    }

    @Override
    protected double getSecsTravelled(final PlanningSegment seg)
    {
      // how long does it take to travel this distance?
      final double secsTaken =
          seg.getDistance().getValueIn(WorldDistance.METRES)
              / seg.getSpeed().getValueIn(WorldSpeed.M_sec);

      // sort out the leg length
      seg.setDurationSilent(new Duration(secsTaken, Duration.SECONDS));

      return secsTaken;
    }
  }

  private static class FromRangeTime extends PlanningCalc
  {

    @Override
    double getMinuteDelta(final PlanningSegment seg)
    {
      // home long to travel along it (secs)
      final double travelSecs = seg.getDuration().getValueIn(Duration.SECONDS);
      final double metresPerSec =
          seg.getDistance().getValueIn(WorldDistance.METRES) / travelSecs;

      final double metresPerMin = metresPerSec * 60d;

      // update the speed, so it makes sense in the fix
      final WorldSpeed speedMtrs =
          new WorldSpeed(metresPerSec, WorldSpeed.M_sec);
      final WorldSpeed speedKTs =
          new WorldSpeed(speedMtrs.getValueIn(WorldSpeed.Kts), WorldSpeed.Kts);
      seg.setSpeedSilent(speedKTs);

      return metresPerMin;
    }

    @Override
    protected double getSecsTravelled(final PlanningSegment seg)
    {
      return seg.getDuration().getValueIn(Duration.SECONDS);
    }
  }

  private static class FromSpeedTime extends PlanningCalc
  {

    @Override
    protected double getSecsTravelled(final PlanningSegment seg)
    {
      return seg.getDuration().getValueIn(Duration.SECONDS);
    }

    @Override
    double getMinuteDelta(final PlanningSegment seg)
    {
      // how far will we travel in time?
      final double metresPerSec = seg.getSpeed().getValueIn(WorldSpeed.M_sec);
      final double metresPerMin = metresPerSec * 60d;

      final double distanceM = metresPerSec * getSecsTravelled(seg);
      final WorldDistance wd =
          new WorldDistance(distanceM, WorldDistance.METRES);
      seg.setDistanceSilent(new WorldDistance(wd.getValueIn(WorldDistance.NM),
          WorldDistance.NM));

      return metresPerMin;
    }
  }

  /**
   * store helps that will aid us in creating a leg - it's an RCP thing, not a legacy thing
   * 
   * @param triggerNewLeg
   */
  public static void setNewLegHelper(final GiveMeALeg triggerNewLeg)
  {
    _triggerNewLeg = triggerNewLeg;
  }

  /**
   * learn about the tool-parent that we're to use
   * 
   * @param toolParent
   */
  public static void initialise(final ToolParent toolParent)
  {
    _toolParent = toolParent;
  }

  /**
   * indicate that planning segments have an order
   * 
   */
  @Override
  public boolean hasOrderedChildren()
  {
    return true;
  }

  @Override
  public Enumeration<Editable> elements()
  {
    /**
     * just return the track segments, we don't contain any other data...
     * 
     */
    return _thePositions.elements();
  }

  @Override
  public Editable getSampleGriddable()
  {
    final String name = "new leg";
    final double courseDegs = 45d;
    final WorldSpeed worldSpeed = new WorldSpeed(10, WorldSpeed.Kts);
    final WorldDistance worldDistance =
        new WorldDistance(5, WorldDistance.MINUTES);
    return new PlanningSegment(name, courseDegs, worldSpeed, worldDistance);
  }

  @Override
  public TimeStampedDataItem makeCopy(final TimeStampedDataItem item)
  {
    // make a copy
    final PlanningSegment newSeg = new PlanningSegment((PlanningSegment) item);
    return newSeg;
  }

  @Override
  public boolean supportsAddRemove()
  {
    return false;
  }

  @Override
  public boolean requiresManualSave()
  {
    return false;
  }

  @Override
  public void doSave(final String message)
  {
  }

  @Override
  public void setLayers(final Layers parent)
  {
    // ok, we've been pasted. just double check that our children know who is
    // the boss
    final Enumeration<Editable> numer = getSegments().elements();
    while (numer.hasMoreElements())
    {
      final Editable editable = (Editable) numer.nextElement();
      final PlanningSegment seg = (PlanningSegment) editable;
      seg.setWrapper(this);
    }
  }

}
