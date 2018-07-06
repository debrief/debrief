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
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;

import junit.framework.TestCase;
import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.FireExtended;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * extension of track segment that represents a single TMA solution as a series of fixes - based on
 * an offset from a specified other track
 * 
 * @author Ian Mayo
 * 
 */
public class RelativeTMASegment extends CoreTMASegment implements
    NeedsToKnowAboutLayers
{

  public static class TestMe extends TestCase
  {
    public final void testMyParams()
    {
      RelativeTMASegment rs = new RelativeTMASegment(0, new WorldSpeed(12,
          WorldSpeed.Kts), new WorldVector(1d, 1d, 0d), null, null, null);
      editableTesterSupport.testParams(rs, this);
      rs = null;
    }
  }

  /**
   * class containing editable details of a track
   */
  public final class TMASegmentInfo extends TrackSegmentInfo
  {

    private final static String OFFSET = "Offset";

    private final static String SOLUTION = "Solution";

    /**
     * constructor for this editor, takes the actual track as a parameter
     * 
     * @param data
     *          track being edited
     */
    public TMASegmentInfo(final TrackSegment data)
    {
      super(data);
    }

    @Override
    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      // start off with the parent
      final PropertyDescriptor[] parent = super.getPropertyDescriptors();
      PropertyDescriptor[] mine;

      try
      {
        final PropertyDescriptor[] res =
        {expertProp("Course", "Course of this TMA Solution", SOLUTION),
            expertProp("Speed", "Speed of this TMA Solution", SOLUTION),
            displayExpertProp("OffsetRange", "Offset range",
                "Distance to start point on host track", OFFSET),
            displayExpertProp("OffsetBearing", "Offset bearing",
                "Bearing from host track to start of this solution", OFFSET),
            displayExpertProp("DTG_Start", "DTG Start",
                "Start time for this TMA Solution", SOLUTION),
            displayExpertProp("DTG_End", "DTG End",
                "End time for this TMA Solution", SOLUTION)};
        mine = res;
      }
      catch (final IntrospectionException e)
      {
        Application.logError2(ToolParent.ERROR,
            "Failed to create properties for RelativeTMASegment", e);
        return super.getPropertyDescriptors();
      }

      // now combine them.
      final PropertyDescriptor[] bigRes = new PropertyDescriptor[parent.length
          + mine.length];
      System.arraycopy(parent, 0, bigRes, 0, parent.length);
      System.arraycopy(mine, 0, bigRes, parent.length, mine.length);

      return bigRes;
    }
  }

  /**
   * preference name for the cut-off value for shading error plot (when REL TMA is being dragged)
   * 
   */
  public static final String CUT_OFF_VALUE_DEGS = "SHADING_CUT_OFF_VALUE_DEGS";

  /**
   * preference name for the cut-off value for shading error plot (when REL TMA is being dragged)
   * 
   */
  public static final String CUT_OFF_VALUE_HZ = "SHADING_CUT_OFF_VALUE_HZ";

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * name of the watchable list we're going to use as our origin. We need to support storing this as
   * a string so that we can defer finding the actual object pointer until after file-load is
   * complete
   * 
   * @return
   */
  private final String _referenceTrackName;

  /**
   * name of the sensor we're going to use as our origin
   * 
   */
  private final String _referenceSensorName;

  /**
   * the offset we apply to the origin
   * 
   */
  private WorldVector _offset;

  /**
   * the feature we're based on
   * 
   */
  private TrackWrapper _referenceTrack;

  /**
   * the sensor that we're using as an origin (particularly relevant for an offset sensor like a
   * towed array
   */
  private SensorWrapper _referenceSensor;

  /**
   * our editable details
   * 
   */
  private transient TMASegmentInfo _myInfo = null;

  /**
   * the layers we look at to find our host
   * 
   */
  private transient Layers _theLayers;

  /**
   * we listen out for the parent track moving, here's our listener
   */
  private transient PropertyChangeListener _refTrackMovedListener;

  /**
   * base constructor - sorts out the obvious
   * 
   * @param courseDegs
   *          our course
   * @param speed
   *          our speed
   * @param offset
   *          offset from ownship track at start time
   * @param theLayers
   * @param trackName
   *          name of the track we're relative to
   * @param sensorName
   *          name of the track sensor that is holding us
   */
  public RelativeTMASegment(final double courseDegs, final WorldSpeed speed,
      final WorldVector offset, final Layers theLayers, final String trackName,
      final String sensorName)
  {
    super(courseDegs, speed, TrackSegment.RELATIVE);
    _referenceTrackName = trackName;
    _referenceSensorName = sensorName;
    _theLayers = theLayers;
    _offset = offset;

    checkMoveListener();
  }

  /**
   * kind of copy constructor. take the settings from the segment, but store the supplied cuts
   * 
   * @param relevantSegment
   * @param theItems
   * @param theOffset
   */
  public RelativeTMASegment(final RelativeTMASegment relevantSegment,
      final SortedSet<Editable> theItems, final WorldVector theOffset)
  {
    // start off with the obvious bits
    this(relevantSegment._courseDegs, relevantSegment._speed, theOffset,
        relevantSegment._theLayers, relevantSegment._referenceTrackName,
        relevantSegment._referenceSensorName);

    // now the other bits
    setTrack(relevantSegment._referenceTrack);

    // lastly, insert the fixes
    for (final Editable item : theItems)
    {
      add(item);
    }

    // now sort out the name
    sortOutDateLabel(null);

  }

  /**
   * build up a solution from the supplied sensor data
   * 
   * @param observations
   *          create a single position for the DTG of each solution
   * @param offset
   *          the range/brg from the host's position at the DTG of the first observation
   * @param speed
   *          the initial target speed estimate
   * @param courseDegs
   *          the initial target course estimate
   * @param theLayers
   * @param override
   *          optional, whether to force usage of a track color. If null, the relevant sensor cut
   *          color will be used
   */
  public RelativeTMASegment(final SensorContactWrapper[] observations,
      final WorldVector offset, final WorldSpeed speed, final double courseDegs,
      final Layers theLayers, final Color override)
  {
    this(courseDegs, speed, offset, theLayers, observations[0].getSensor()
        .getHost().getName(), observations[0].getSensor().getName());

    // sort out the origin
    final SensorContactWrapper scw = observations[0];

    // set the track (which will also set the sensor, from the name);
    setTrack(scw.getSensor().getHost());

    // create the points
    createPointsFrom(observations, override);
  }

  /**
   * 
   * @param sensor
   * @param offset
   * @param speed
   * @param course
   * @param dataPoints
   * @param layers
   * @param track
   * @param startTime
   * @param endTime
   */
  public RelativeTMASegment(final SensorWrapper sensor,
      final WorldVector offset, final WorldSpeed speed, final double course,
      final Collection<Editable> dataPoints, final Layers layers,
      final TrackWrapper track, final HiResDate startTime,
      final HiResDate endTime)
  {
    this(course, speed, offset, layers, sensor.getHost().getName(), sensor
        .getName());

    // ok, now generate the points
    setTrack(sensor.getHost());

    for (final Iterator<Editable> iterator = dataPoints.iterator(); iterator
        .hasNext();)
    {
      final FixWrapper thisF = (FixWrapper) iterator.next();
      final FixWrapper newF = createFixAt(thisF.getDateTimeGroup().getDate()
          .getTime());
      newF.setSymbolShowing(true);
      addFixSilent(newF);
    }
  }

  private void addFix(final FixWrapper theLoc, final long thisT)
  {
    final HiResDate newTime = new HiResDate(thisT);

    // don't worry about the location, we're going to DR it on anyway...
    final WorldLocation newLoc = null;
    final Fix newFix = new Fix(newTime, newLoc, MWC.Algorithms.Conversions
        .Degs2Rads(this.getCourse()), MWC.Algorithms.Conversions.Kts2Yps(this
            .getSpeed().getValueIn(WorldSpeed.Kts)));

    // and apply the stretch
    final FixWrapper newItem = new FixWrapper(newFix);

    // set some other bits
    newItem.setTrackWrapper(this._myTrack);
    newItem.setColor(theLoc.getActualColor());
    newItem.setSymbolShowing(theLoc.getSymbolShowing());
    newItem.setArrowShowing(theLoc.getArrowShowing());
    newItem.setLabelShowing(theLoc.getLabelShowing());
    newItem.setLabelLocation(theLoc.getLabelLocation());
    newItem.setLabelFormat(theLoc.getLabelFormat());
    newItem.resetName();

    this.add(newItem);
  }

  /**
   * create the movement listener, if we need to. We need to be able to regenerate the listener for
   * instances where the segment has been copy/pated between plots
   */
  private void checkMoveListener()
  {
    if (_refTrackMovedListener == null)
    {
      _refTrackMovedListener = new PropertyChangeListener()
      {

        @Override
        public void propertyChange(final PropertyChangeEvent evt)
        {
          // ok, our reference track has moved. recalculate ourselves
          recalcPositions();

          // also tell anybody that's interested that we've moved
          // (especially dynamic infill segments)
          firePropertyChange(CoreTMASegment.ADJUSTED, null, System
              .currentTimeMillis());
        }
      };
    }
  }

  /**
   * create a fix at the specified dtg
   * 
   * @param thisS
   * @return
   */
  protected FixWrapper createPointFor(final SensorContactWrapper thisS)
  {
    final long theTime = thisS.getDTG().getDate().getTime();
    return createFixAt(theTime);
  }

  // /**
  // * create a solution from all of ths fixes in this sensor
  // *
  // * @param sw
  // */
  // private void createPointsFrom(final SensorWrapper sw)
  // {
  // final Enumeration<Editable> obs = sw.elements();
  // while (obs.hasMoreElements())
  // {
  // final SensorContactWrapper thisS = (SensorContactWrapper) obs
  // .nextElement();
  // doThisFix(thisS, null);
  // }
  // }

  /**
   * @param observations
   *          the cuts to create from
   * @param override
   *          optional fixed color to set positions
   */
  private void createPointsFrom(final SensorContactWrapper[] observations,
      final Color override)
  {
    // better start looping
    for (int i = 0; i < observations.length; i++)
    {
      final SensorContactWrapper thisS = observations[i];

      doThisFix(thisS, override);
    }
  }

  /**
   * create a fix from this sensor item
   * 
   * @param thisS
   * @param override
   */
  private void doThisFix(final SensorContactWrapper thisS, final Color override)
  {
    // and create a fix for this cut
    final FixWrapper newFix = createPointFor(thisS);
    newFix.setSymbolShowing(true);

    final Color fixColor;
    if (override != null)
    {
      fixColor = override;
    }
    else
    {
      fixColor = thisS.getColor();
    }

    newFix.setColor(fixColor);

    // store it
    addFix(newFix);
  }

  /**
   * move the solution in or out from the ref track, maintaining the bearings to the host track
   * (changing speed but not course)
   * 
   * @param vector
   *          how far to push it.
   */
  public void fanStretch(final WorldVector vector)
  {
    // find our locations
    final WorldLocation myStart = this.getTrackStart();
    final WorldLocation myEnd = ((FixWrapper) this.last()).getLocation();
    final WorldLocation hisStart = sensorOriginAt(this.startDTG());
    final WorldLocation hisEnd = sensorOriginAt(this.endDTG());

    // drop out if we don't have sensor data
    if ((hisStart == null) || (hisEnd == null))
    {
      System.err.println(
          "Failed to find sensor data to support fan stretch (RelativeTMASegment.fanStretch)");
      return;
    }

    // find the bearings
    final double startBrg = 2 * Math.PI + myStart.bearingFrom(hisStart);
    final double endBrg = 2 * Math.PI + myEnd.bearingFrom(hisEnd);

    double midBrg = (endBrg + startBrg) / 2;
    while (midBrg >= (2 * Math.PI))
    {
      midBrg -= (2 * Math.PI);
    }

    // how far has the user dragged it?
    double theRange = vector.getRange();

    // sort out which direction we're going
    final double theBrg = 2 * Math.PI + vector.getBearing();
    double theDelta = theBrg - midBrg;
    while (theDelta > 2 * Math.PI)
    {
      theDelta -= 2 * Math.PI;
    }

    // right, see if we're going in or out
    if (Math.abs(theDelta) > Math.PI / 2)
    {
      theRange *= -1;
    }

    // whats the distance to the sensor origin?
    final double currSensorDist = myStart.subtract(hisStart).getRange();

    // create a new distance by moving out along the sensor bearing
    final WorldLocation newStart = hisStart.add(new WorldVector(startBrg,
        currSensorDist + theRange, 0));

    // and calculate the new offset (relative to a fix on the host position
    // track)
    _offset = newStart.subtract(getHostLocation());

    // re-sort out the locations, once we've updated the offset
    recalcPositions();

    // recalculate the dragged track
    final WorldLocation myNewStart = this.getTrackStart();
    final WorldLocation myNewEnd = ((FixWrapper) this.last()).getLocation();

    // sort out points on the line we have to meet
    final WorldLocation outerStart = hisEnd;
    final WorldLocation outerEnd = outerStart.add(new WorldVector(endBrg,
        _offset.getRange(), 0));

    // perform the line intersect
    // taken from
    // http://stackoverflow.com/questions/385305/efficient-maths-algorithm-to-calculate-intersections
    final double x1 = myNewStart.getLong(), y1 = myNewStart.getLat();
    final double x2 = myNewEnd.getLong(), y2 = myNewEnd.getLat();

    final double x3 = outerStart.getLong(), y3 = outerStart.getLat();
    final double x4 = outerEnd.getLong(), y4 = outerEnd.getLat();

    final double x12 = x1 - x2;
    final double x34 = x3 - x4;
    final double y12 = y1 - y2;
    final double y34 = y3 - y4;

    final double c = x12 * y34 - y12 * x34;

    double intersectX = 0, intersectY = 0;

    if (Math.abs(c) < 0.00000001)
    {
      // No intersection
      return;
    }
    else
    {
      // Intersection
      final double a = x1 * y2 - y1 * x2;
      final double b = x3 * y4 - y3 * x4;

      intersectX = (a * x34 - b * x12) / c;
      intersectY = (a * y34 - b * y12) / c;
    }

    // calculate the distance delta (for how much longer the track will have to
    // be
    final WorldLocation newEnd = new WorldLocation(intersectY, intersectX, 0);
    final double newLegLength = newEnd.subtract(myNewStart).getRange();
    final WorldDistance lenDegs = new WorldDistance(newLegLength,
        WorldDistance.DEGS);

    // and what's our speed to cover this distance?
    final double timeTakenMicros = (endDTG().getMicros() - startDTG()
        .getMicros());
    final double timeTakenHours = timeTakenMicros / 1000 / 1000 / 60 / 60;
    final double speedKts = lenDegs.getValueIn(WorldDistance.NM)
        / timeTakenHours;

    // and change the speed proportionately
    this.setSpeed(new WorldSpeed(speedKts, WorldSpeed.Kts));

    // re-sort out the locations, once we've updated the speed
    recalcPositions();

    // tell the segment it's being stretched
    final String spdTxt = MWC.Utilities.TextFormatting.GeneralFormat
        .formatOneDecimalPlace(this.getSpeed().getValueIn(WorldSpeed.Kts));

    _dragMsg = "[" + spdTxt + " kts " + (int) this.getCourse() + "\u00B0]";

    // tell any listeners that we've moved
    fireAdjusted();
  }

  public double getDetectionBearing()
  {
    return MWC.Algorithms.Conversions.Rads2Degs(_offset.getBearing());
  }

  public WorldDistance getDetectionRange()
  {
    return new WorldDistance(_offset.getRange(), WorldDistance.DEGS);
  }

  public HiResDate getDTG_End()
  {
    return this.endDTG();
  }

  public HiResDate getDTG_Start()
  {
    return this.startDTG();
  }

  /**
   * the point on the host track that we're offset from
   * 
   * @return
   */
  public WorldLocation getHostLocation()
  {
    WorldLocation res = null;

    final HiResDate startDTG = startDTG();
    if (this.isEmpty())
    {
      Application.logError2(ErrorLogger.ERROR,
          "Empty track, can't find host location", null);
      return null;
    }

    // have we sorted out our reference track yet?
    if (_referenceTrack == null)
    {
      identifyReferenceTrack();
    }

    if (_referenceTrack != null)
    {
      // hmm, are we drawn from a sensor?
      if (_referenceSensor != null)
      {
        // ok, we don't need to worry about interpolating positions, since
        // our time stamps match those of the sensor.
        final Watchable[] items = _referenceSensor.getNearestTo(startDTG);
        final Watchable hostLocation;

        // hmm,
        if (items.length > 0)
        {
          // cool get the location of the sensor
          hostLocation = items[0];
        }
        else
        {
          // hmm, we don't have a cut at this location. Try to get the location
          // of the host
          final TrackWrapper host = _referenceSensor.getHost();
          if (host != null)
          {
            @SuppressWarnings("deprecation")
            final FixWrapper hostFix = host.getBacktraceTo(startDTG,
                _referenceSensor.getSensorOffset(), _referenceSensor
                    .getWormInHole());
            if (hostFix != null)
            {
              hostLocation = hostFix;
            }
            else
            {
              hostLocation = null;
            }
          }
          else
          {
            hostLocation = null;
          }
        }

        // did we find a location?
        if (hostLocation != null)
        {
          res = hostLocation.getLocation();
        }
        else
        {
          Application.logError2(ErrorLogger.ERROR,
              "Unable to find host location for this segment", null);
        }
      }
      else
      {
        // interpolate on the parent track
        final boolean oldInterpSetting = _referenceTrack.getInterpolatePoints();

        _referenceTrack.setInterpolatePoints(true);

        final Watchable[] pts = _referenceTrack.getNearestTo(startDTG);
        if (pts.length > 0)
        {
          res = pts[0].getLocation();
        }

        _referenceTrack.setInterpolatePoints(oldInterpSetting);
      }
    }

    return res;
  }

  public String getHostName()
  {
    final String res = _referenceTrack != null ? _referenceTrack.getName()
        : _referenceTrackName;

    return res;
  }

  @Override
  public EditorType getInfo()
  {
    if (_myInfo == null)
    {
      _myInfo = new TMASegmentInfo(this);
    }
    return _myInfo;
  }

  /**
   * provide access to the layers object (necessary in support of moving tracks between layers)
   * 
   * @return
   */
  public Layers getLayers()
  {
    return _theLayers;
  }

  public WorldVector getOffset()
  {
    return _offset;
  }

  public double getOffsetBearing()
  {
    double res = 0;
    if (_offset != null)
    {
      res = MWC.Algorithms.Conversions.Rads2Degs(_offset.getBearing());
    }
    return res;
  }

  public WorldDistance getOffsetRange()
  {
    WorldDistance res = null;
    if (_offset != null)
    {
      res = new WorldDistance(_offset.getRange(), WorldDistance.DEGS);
    }
    return res;
  }

  public SensorWrapper getReferenceSensor()
  {
    // do we know it?
    if (_referenceTrack == null)
    {
      identifyReferenceTrack();
    }

    // fingers crossed it's sorted.
    return _referenceSensor;
  }

  public WatchableList getReferenceTrack()
  {
    // do we know it?
    if (_referenceTrack == null)
    {
      identifyReferenceTrack();
    }

    // fingers crossed it's sorted.
    return _referenceTrack;
  }

  public String getSensorName()
  {
    final String res = _referenceSensor != null ? _referenceSensor.getName()
        : _referenceSensorName;

    return res;
  }

  /**
   * get the start of this tma segment
   * 
   * @return
   */
  @Override
  public WorldLocation getTrackStart()
  {
    WorldLocation res = getHostLocation();
    if (res != null)
    {
      res = res.add(_offset);
    }
    return res;
  }

  /**
   * find the reference track for this relative solution
   * 
   */
  private void identifyReferenceTrack()
  {
    // check we're not looking for ourselves
    if(this.getWrapper() != null && this.getWrapper().getName().equals(_referenceTrackName))
    {
      Application.logError2(ErrorLogger.ERROR,
          "Data error - this TMA is trying to reference itself:"
              + _referenceTrackName, null);

      // ok - something bad has gone down. Drop out.
      return;
    }
    
    final TrackWrapper theTrack = (TrackWrapper) _theLayers.findLayer(
        _referenceTrackName);

    if (theTrack == null)
    {
    }

    // ok, we can't work, we'll delete ourselves
    if (theTrack != null)
    {
      final TimePeriod hisPeriod = new TimePeriod.BaseTimePeriod(theTrack
          .getStartDTG(), theTrack.getEndDTG());
      final HiResDate myStart = getDTG_Start();
      if (myStart == null)
      {
        Application.logError2(ErrorLogger.ERROR, "This track contains no fixes",
            null);
      }
      else
      {
        if (myStart.lessThan(hisPeriod.getStartDTG()) || getDTG_End()
            .greaterThan(hisPeriod.getEndDTG()))
        {
          if (this.getWrapper() != null)
          {
            final TrackWrapper wrapper = this.getWrapper();
            wrapper.removeElement(this);
            wrapper.firePropertyChange(EXTENDED, null, this);
            _myParent.logError(ToolParent.WARNING, "Deleting leg " + this
                .getName() + ",  host data not available", null);
          }
        }
        else
        {
          // ok, store it (including clearing any previous one)
          setTrack(theTrack);
        }
      }
    }
  }

  private void recalcPositions()
  {
    final Collection<Editable> items = getData();

    // ok - draw that line!
    WorldLocation tmaLastLoc = null;
    long tmaLastDTG = 0;

    for (final Iterator<Editable> iterator = items.iterator(); iterator
        .hasNext();)
    {
      final FixWrapper thisF = (FixWrapper) iterator.next();

      final long thisTime = thisF.getDateTimeGroup().getDate().getTime();

      // ok, is this our first location?
      if (tmaLastLoc == null)
      {
        tmaLastLoc = new WorldLocation(getTrackStart());
      }
      else
      {
        // calculate a new vector
        final long timeDelta = thisTime - tmaLastDTG;
        final WorldVector thisVec = vectorFor(timeDelta, thisF.getSpeed(), thisF
            .getCourse());
        tmaLastLoc.addToMe(thisVec);
      }

      // dump the location into the fix
      thisF.setFixLocationSilent(new WorldLocation(tmaLastLoc));

      // cool, remember the time.
      tmaLastDTG = thisTime;
    }
  }

  @Override
  public void removeElement(final Editable p)
  {
    // check if its' the first fix
    final boolean firstRemoved = p.equals(this.elements().nextElement());

    // let the parent remove the item
    super.removeElement(p);

    // did we remove the first element?
    // if we did, we have to re-generate the offset from the host track
    // - and check we still have elements
    if (firstRemoved && !this.isEmpty())
    {
      // ok, generate a new offset

      // where is the new start point
      final FixWrapper newStart = (FixWrapper) this.elements().nextElement();

      // the host location will have changed, since we're related to a new DTG
      final WorldLocation hostLoc = getHostLocation();

      // and store the new location
      _offset = newStart.getLocation().subtract(hostLoc);
    }

  }

  @Override
  public void rotate(final double brg, final WorldLocation origin)
  {
    final double theBrg = -brg;

    // right - we just stretch about the ends, and we use different
    // processing depending on which end is being shifted.
    final SortedSet<Editable> data = (SortedSet<Editable>) this.getData();
    final FixWrapper first = (FixWrapper) data.first();
    final FixWrapper last = (FixWrapper) data.last();

    // SPECIAL HANDLING: due to the DR rendering of
    // relative segements, we can get some very minor shift
    // from the origin after a shift operation.
    // We can't use the WorldLocation.equals operation, since it
    // use micrometer accuracy (1.0E-11). Let's use a slightly
    // relaxed test to determine if the drag origin
    // is the same as the segment origin
    final double distFromOrigin = first.getLocation().rangeFrom(origin);
    final double distFromEnd = last.getLocation().rangeFrom(origin);

    if (distFromOrigin < distFromEnd)
    {
      // right, we're dragging around the last point. Couldn't be easier,
      // just change our course
      final double brgDegs = MWC.Algorithms.Conversions.Rads2Degs(theBrg);
      final double newBrg = this.getCourse() + brgDegs;
      // right, the start is the origin, so we just set our course to the
      // bearing
      this.setCourse(newBrg);
    }
    else
    {
      // right, we've got to shift the start point to the relevant
      // location,
      // and fix the bearing

      // start with a recalculated origin
      final WorldLocation hostReference = getHostLocation();
      final WorldLocation startPoint = hostReference.add(_offset);

      // rotate the origin about the far end
      final WorldLocation newStart = startPoint.rotatePoint(origin, -theBrg);

      // find out the offset from the origin
      final WorldVector offset = newStart.subtract(hostReference);

      // update the offset to the new start location
      this.setOffsetBearing(MWC.Algorithms.Conversions.Rads2Degs(offset
          .getBearing()));
      this.setOffsetRange(new WorldDistance(offset.getRange(),
          WorldDistance.DEGS));

      // what's the course from the new start to the origin?
      final WorldVector vec = origin.subtract(newStart);

      // update the course
      this.setCourse(MWC.Algorithms.Conversions.Rads2Degs(vec.getBearing()));

    }

    final Double courseVal = new Double(MWC.Algorithms.Conversions.Degs2Rads(
        this._courseDegs));
    final Double speedVal = null;
    updateCourseSpeed(courseVal, speedVal);

    // tell the segment it's being stretched
    int newCourse = (int) getCourse();
    if (newCourse < 0)
    {
      newCourse += 360;
    }
    _dragMsg = "[" + newCourse + "\u00B0] ";

    // tell any listeners that we've moved
    fireAdjusted();

  }

  /**
   * convenience method to find the location of the sensor at the specified time
   * 
   * @param dtg
   *          the time we're hunting for
   * @return the location of the first sensor cut visible at that time
   */
  private WorldLocation sensorOriginAt(final HiResDate dtg)
  {
    // store the nearest item, and the time delta
    SensorContactWrapper nearestContact = null;
    WorldLocation res = null;

    // right, get the sensors for our reference track
    final TrackWrapper tw = (TrackWrapper) getReferenceTrack();
    final Enumeration<Editable> sensors = tw.getSensors().elements();
    while (sensors.hasMoreElements())
    {
      final SensorWrapper thisS = (SensorWrapper) sensors.nextElement();
      if (thisS.getVisible())
      {
        final Watchable[] matches = thisS.getNearestTo(dtg);
        for (int i = 0; i < matches.length; i++)
        {
          final SensorContactWrapper scw = (SensorContactWrapper) matches[i];
          if (scw.getDTG().equals(dtg))
          {
            nearestContact = scw;
            continue;
          }
        }
      }
    }

    // did we find anything?
    if (nearestContact != null)
    {
      res = nearestContact.getLocation();
    }

    return res;
  }

  public void setDetectionBearing(final double detectionBearing)
  {
    _offset = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(
        detectionBearing), new WorldDistance(_offset.getRange(),
            WorldDistance.DEGS), null);
  }

  public void setDetectionRange(final WorldDistance detectionRange)
  {
    _offset = new WorldVector(_offset.getBearing(), detectionRange, null);
  }

  @FireExtended
  public void setDTG_End(final HiResDate newEnd)
  {
    // ok, how far is this from the current end
    final long delta = newEnd.getMicros() - endDTG().getMicros();

    // right, do we need to prune a few off?
    if (delta < 0)
    {
      // right, we're shortening the track.
      // check the end point is after the start
      if (newEnd.getMicros() < startDTG().getMicros())
      {
        return;
      }

      // ok, it's worth bothering with. get ready to store ones we'll lose
      final Vector<FixWrapper> onesToRemove = new Vector<FixWrapper>();

      final Iterator<Editable> iter = this.getData().iterator();
      while (iter.hasNext())
      {
        final FixWrapper thisF = (FixWrapper) iter.next();
        if (thisF.getTime().greaterThan(newEnd))
        {
          onesToRemove.add(thisF);
        }
      }

      // and ditch them
      for (final Iterator<FixWrapper> iterator = onesToRemove
          .iterator(); iterator.hasNext();)
      {
        final FixWrapper thisFix = iterator.next();
        this.removeElement(thisFix);
      }
    }

    // right, we may have pruned off too far. See if we need to put a bit back
    // in...
    if (newEnd.greaterThan(endDTG()))
    {

      // right, we if we have to add another
      // find the current last point
      final FixWrapper theLoc = (FixWrapper) this.last();

      // note: we don't want one large leap. So, insert a few points
      final long oldEndT = endDTG().getDate().getTime();
      final long newEndT = newEnd.getDate().getTime();

      // see if we've managed to find some cuts to use
      final boolean handled;

      // If we have a reference sensor, take visible cuts
      // from that sensor
      if (this.getReferenceSensor() != null)
      {
        SensorWrapper ref = this.getReferenceSensor();
        Collection<Editable> cuts = ref.getItemsBetween(endDTG(), newEnd);
        if (cuts != null)
        {
          handled = true;
          for (Editable t : cuts)
          {
            SensorContactWrapper cut = (SensorContactWrapper) t;
            addFix(theLoc, cut.getDTG().getDate().getTime());
          }
        }
        else
        {
          handled = false;
        }
      }
      else
      {
        handled = false;
      }

      // are we sorted?
      if (!handled)
      {
        // ok, we don't have sensor. just add some at "nice" sizes
        final long typicalDelta = typicalTimeStep();
        long thisT = oldEndT + typicalDelta;

        while (thisT < newEndT)
        {
          addFix(theLoc, thisT);
          thisT += typicalDelta;
        }
      }

      // and create one at the end time
      addFix(theLoc, newEndT);
    }

    // tell any listeners that we've changed
    super.fireAdjusted();

  }

  @FireExtended
  public void setDTG_Start(final HiResDate newStart)
  {
    HiResDate theNewStart = newStart;
    // check that we're still after the start of the host track
    if (theNewStart.lessThan(this.getReferenceTrack().getStartDTG()))
    {
      theNewStart = new HiResDate(this.getReferenceTrack().getStartDTG());
    }

    // ok, how far is this from the current end
    final long delta = theNewStart.getMicros() - startDTG().getMicros();

    // and what distance does this mean?
    final double deltaHrs = delta / 1000000d / 60d / 60d;
    final double distDegs = this.getSpeed().getValueIn(WorldSpeed.Kts)
        * deltaHrs / 60;

    final double theDirection = MWC.Algorithms.Conversions.Degs2Rads(this
        .getCourse());

    // we don't need to worry about reversing the direction, since we have a -ve
    // distance

    // so what's the new origin?
    final WorldLocation currentStart = new WorldLocation(this.getTrackStart());
    final WorldLocation newOrigin = currentStart.add(new WorldVector(
        theDirection, distDegs, 0));

    // and what's the point on the host track
    @SuppressWarnings("deprecation")
    final FixWrapper hostFix = this._referenceTrack.getBacktraceTo(theNewStart,
        _referenceSensor.getSensorOffset(), _referenceSensor.getWormInHole());

    final WorldVector newOffset = newOrigin.subtract(hostFix.getLocation());

    // right, we know where the new track will be, see if we need to ditch any
    if (delta > 0)
    {
      // right, we're shortening the track.
      // check the end point is before the end
      if (theNewStart.getMicros() > endDTG().getMicros())
      {
        return;
      }

      // ok, it's worth bothering with. get ready to store ones we'll lose
      final Vector<FixWrapper> onesToRemove = new Vector<FixWrapper>();

      final Iterator<Editable> iter = this.getData().iterator();
      while (iter.hasNext())
      {
        final FixWrapper thisF = (FixWrapper) iter.next();
        if (thisF.getTime().lessThan(theNewStart))
        {
          onesToRemove.add(thisF);
        }
      }

      // and ditch them
      for (final Iterator<FixWrapper> iterator = onesToRemove
          .iterator(); iterator.hasNext();)
      {
        final FixWrapper thisFix = iterator.next();
        this.removeElement(thisFix);
      }
    }

    // right, we may have pruned off too far. See if we need to put a bit back
    // in...
    if (theNewStart.lessThan(startDTG()))
    {

      // right, we if we have to add another
      // find the current last point
      final FixWrapper theLoc = (FixWrapper) this.first();

      // note: we don't want one large leap. So, insert a few points
      final long oldStartT = startDTG().getDate().getTime();
      final long newStartT = theNewStart.getDate().getTime();

      // see if we've managed to find some cuts to use
      final boolean handled;

      // If we have a reference sensor, take visible cuts
      // from that sensor
      if (this.getReferenceSensor() != null)
      {
        SensorWrapper ref = this.getReferenceSensor();
        Collection<Editable> cuts = ref.getItemsBetween(theNewStart,
            startDTG());
        if (cuts != null)
        {
          handled = true;
          for (Editable t : cuts)
          {
            SensorContactWrapper cut = (SensorContactWrapper) t;
            addFix(theLoc, cut.getDTG().getDate().getTime());
          }
        }
        else
        {
          handled = false;
        }
      }
      else
      {
        handled = false;
      }

      // are we sorted?
      if (!handled)
      {
        // ok, we don't have sensor. just add some at "nice" sizes
        final long typicalDelta = typicalTimeStep();

        // ok, insert new fix at the new start time
        addFix(theLoc, newStartT);

        // now walk forwards until we meet the old start time
        long thisT = newStartT + typicalDelta;

        while (thisT < oldStartT)
        {
          addFix(theLoc, thisT);
          thisT += typicalDelta;
        }
      }
    }

    // and sort out the new offset
    this._offset = newOffset;

    // tell any listeners that we've changed
    super.fireAdjusted();

  }

  @Override
  public void setLayers(final Layers layers)
  {
    if (layers != _theLayers)
    {
      _theLayers = layers;

      // ok, we've moved.
      // We'd better re-generate our references
      identifyReferenceTrack();
    }
  }

  // /**
  // * temporarily store the hostname, until we've finished loading and we can
  // * sort it out for real.
  // *
  // * @param hostName
  // */
  // public void setHostName(final String hostName)
  // {
  // // better trim what we've recived
  // final String name = hostName.trim();
  //
  // // have we got meaningful data?
  // if (name.length() > 0)
  // {
  // // right, see if we can find it
  // if (_theLayers != null)
  // {
  // final Layer tgt = _theLayers.findLayer(name);
  // if (tgt != null)
  // {
  // // clear any existing track
  // setTrack(null);
  //
  // // now remember the new name
  // _referenceTrackName = hostName;
  // }
  // }
  // }
  // }

  public void setOffset(final WorldVector newOffset)
  {
    _offset = newOffset;
  }

  /**
   * manage the offset bearing (in degrees)
   * 
   * @param offsetBearing
   */
  public void setOffsetBearing(final double offsetBearing)
  {
    _offset.setValues(MWC.Algorithms.Conversions.Degs2Rads(offsetBearing),
        _offset.getRange(), _offset.getDepth());
  }

  /**
   * manage the offset range (in degrees)
   * 
   * @param offsetRange
   */
  public void setOffsetRange(final WorldDistance offsetRange)
  {
    _offset.setValues(_offset.getBearing(), offsetRange.getValueIn(
        WorldDistance.DEGS), _offset.getDepth());
  }

  private void setTrack(final TrackWrapper newTrack)
  {
    checkMoveListener();

    // do we have an existing one?
    if (_referenceTrack != null)
    {
      _referenceTrack.removePropertyChangeListener(
          PlainWrapper.LOCATION_CHANGED, _refTrackMovedListener);
      _referenceTrack = null;
    }

    if (_referenceSensor != null)
    {
      _referenceSensor.removePropertyChangeListener(
          PlainWrapper.LOCATION_CHANGED, _refTrackMovedListener);
      _referenceSensor = null;
    }

    // hmm, also try to find the sensor
    if (newTrack != null)
    {
      _referenceTrack = newTrack;

      final Enumeration<Editable> sensors = _referenceTrack.getSensors()
          .elements();
      while (sensors.hasMoreElements())
      {
        final SensorWrapper sensor = (SensorWrapper) sensors.nextElement();
        if (sensor.getName().equals(_referenceSensorName))
        {
          _referenceSensor = sensor;

          // and listen to it
          _referenceSensor.addPropertyChangeListener(
              PlainWrapper.LOCATION_CHANGED, _refTrackMovedListener);
          break;
        }
      }

      // did it work?
      if (_referenceSensor == null)
      {
        Application.logError2(ErrorLogger.ERROR,
            "Unable to find host sensor named:" + _referenceSensorName, null);
      }

      // we should also listen for the reference track moving
      _referenceTrack.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED,
          _refTrackMovedListener);
    }
  }

  @Override
  public void shear(final WorldLocation cursor, final WorldLocation origin)
  {
    WorldVector offset = cursor.subtract(origin);
    double rngDegs = offset.getRange();

    // make it always +ve, we'll just overwrite ourselves anyway
    rngDegs = Math.abs(rngDegs);

    double newCourse;

    // right - we just stretch about the ends, and we use different
    // processing depending on which end is being shifted.
    final SortedSet<Editable> data = (SortedSet<Editable>) this.getData();
    final FixWrapper first = (FixWrapper) data.first();
    final FixWrapper last = (FixWrapper) data.last();

    // SPECIAL HANDLING: due to the DR rendering of
    // relative segements, we can get some very minor shift
    // from the origin after a shift operation.
    // We can't use the WorldLocation.equals operation, since it
    // use micrometer accuracy (1.0E-11). Let's use a slightly
    // relaxed test to determine if the drag origin
    // is the same as the segment origin
    final double distFromOrigin = first.getLocation().rangeFrom(origin);
    final double distFromEnd = last.getLocation().rangeFrom(origin);

    if (distFromOrigin < distFromEnd)
    {
      // set the new course
      newCourse = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());
    }
    else
    {
      // reverse the course
      offset = origin.subtract(cursor);
      newCourse = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());

      // right, we've got to shift the start point to the relevant
      // location,
      // and fix the bearing

      // find out the offset from the origin
      final WorldVector newOffset = cursor.subtract(getHostLocation());

      // update the offset to the new start location
      this.setOffsetBearing(MWC.Algorithms.Conversions.Rads2Degs(newOffset
          .getBearing()));
      this.setOffsetRange(new WorldDistance(newOffset.getRange(),
          WorldDistance.DEGS));
    }

    // how long do we have for the travel?
    final long periodMillis = this.endDTG().getDate().getTime() - this
        .startDTG().getDate().getTime();

    // what's that in hours?
    final double periodHours = periodMillis / 1000d / 60d / 60d;

    // what's distance in minutes?
    final double distMins = rngDegs * 60;

    // how far must we go to sort this
    final double spdKts = distMins / periodHours;

    final WorldSpeed newSpeed = new WorldSpeed(spdKts, WorldSpeed.Kts);
    this.setSpeed(newSpeed);

    // tidy the course
    while (newCourse < 0)
    {
      newCourse += 360;
    }

    this.setCourse(newCourse);

    final Double newCourseRads = new Double(MWC.Algorithms.Conversions
        .Degs2Rads(newCourse));
    final Double newSpeedKts = new Double(newSpeed.getValueIn(WorldSpeed.Kts));
    updateCourseSpeed(newCourseRads, newSpeedKts);

    final String spdTxt = MWC.Utilities.TextFormatting.GeneralFormat
        .formatOneDecimalPlace(newSpeed.getValueIn(WorldSpeed.Kts));

    // tell the segment it's being stretched
    _dragMsg = "[" + spdTxt + " kts " + (int) newCourse + "\u00B0]";

    // tell any listeners that we've moved
    fireAdjusted();
  }

  @Override
  public void shift(final WorldVector vector)
  {
    // really, we just need to add this vector to our orign
    final WorldLocation tmpOrigin = new WorldLocation(getTrackStart());
    tmpOrigin.addToMe(_offset);
    tmpOrigin.addToMe(vector);

    _offset = tmpOrigin.subtract(getTrackStart());

    // clear the drag message, there's nothing to show message
    _dragMsg = null;

    // tell any listeners that we've moved
    fireAdjusted();
  }

  /**
   * stretch this whole track to the supplied distance
   * 
   * @param rngDegs
   *          distance to stretch through (degs)
   * @param origin
   *          origin of stretch, probably one end of the track
   */
  @Override
  public void stretch(final double rngDegs, final WorldLocation origin)
  {
    // make it always +ve, we'll just overwrite ourselves anyway
    final double theRngDegs = Math.abs(rngDegs);

    // right - we just stretch about the ends, and we use different
    // processing depending on which end is being shifted.
    final FixWrapper first = (FixWrapper) this.getData().iterator().next();
    if (first.getLocation().equals(origin))
    {
      // right, we're dragging around the last point. Couldn't be easier,
      // just change our speed
    }
    else
    {
      // right, we've got to shift the start point to the relevant
      // location,
      // and fix the bearing

      // calculate a new start point
      final WorldVector thisLeg = getTrackStart().subtract(origin);

      // now change the distance
      final WorldVector newLeg = new WorldVector(thisLeg.getBearing(),
          theRngDegs, thisLeg.getDepth());

      // calculate the new start point
      final WorldLocation newStart = origin.add(newLeg);

      // find out the offset from the origin
      final WorldVector offset = newStart.subtract(getHostLocation());

      // update the offset to the new start location
      this.setOffsetBearing(MWC.Algorithms.Conversions.Rads2Degs(offset
          .getBearing()));
      this.setOffsetRange(new WorldDistance(offset.getRange(),
          WorldDistance.DEGS));
    }

    // how long do we have for the travel?
    final long periodMillis = this.endDTG().getDate().getTime() - this
        .startDTG().getDate().getTime();

    // what's that in hours?
    final double periodHours = periodMillis / 1000d / 60d / 60d;

    // what's distance in minutes?
    final double distMins = theRngDegs * 60;

    // how far must we go to sort this
    final double spdKts = distMins / periodHours;

    final double newSpeedKts = new Double(spdKts);
    updateCourseSpeed(null, newSpeedKts);

    final WorldSpeed newSpeed = new WorldSpeed(spdKts, WorldSpeed.Kts);
    this.setSpeed(newSpeed);

    // tell the segment it's being stretched
    final String spdTxt = MWC.Utilities.TextFormatting.GeneralFormat
        .formatOneDecimalPlace(newSpeed.getValueIn(WorldSpeed.Kts));

    _dragMsg = "[" + spdTxt + " kts]";

    // tell any listeners that we've moved
    fireAdjusted();
  }

  /**
   * get the mean interval between the time steps
   * 
   * @return
   */
  private long typicalTimeStep()
  {
    final long res;
    final FixWrapper[] dArr = this.getData().toArray(new FixWrapper[]
    {});
    if (dArr.length < 2)
    {
      // special case, return useful gap
      res = 5000;
    }
    else
    {
      int count = 0;
      long sum = 0;
      long lastTime = 0;
      for (int i = 0; i < dArr.length; i++)
      {
        final FixWrapper fixWrapper = dArr[i];
        final long thisTime = fixWrapper.getDateTimeGroup().getDate().getTime();
        if (i > 0)
        {
          final long thisDiff = thisTime - lastTime;
          sum += thisDiff;
          count++;
        }

        // ok, remember time
        lastTime = thisTime;
      }

      // ok, and now the mean
      res = sum / count;
    }

    return res;
  }

  /**
   * tell the data points that course and speed have been updated
   * 
   * @param courseVal
   *          the (optional) course to update
   * @param speedVal
   *          the (optional) speed to update
   */
  private void updateCourseSpeed(final Double courseValRads,
      final Double speedValKts)
  {
    final Enumeration<Editable> obs = this.elements();
    while (obs.hasMoreElements())
    {
      final FixWrapper thisS = (FixWrapper) obs.nextElement();
      if (courseValRads != null)
      {
        thisS.setCourse(courseValRads.doubleValue());
      }
      if (speedValKts != null)
      {
        thisS.setSpeed(speedValKts.doubleValue());
      }
    }
  }
}