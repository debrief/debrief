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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * extension of track segment that represents a single TMA solution as a series
 * of fixes - based on an offset from a specified other track
 * 
 * @author Ian Mayo
 * 
 */
public class RelativeTMASegment extends CoreTMASegment
{
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
				{
						expertProp("Course", "Course of this TMA Solution", SOLUTION),
						expertProp("BaseFrequency",
								"The base frequency of this TMA segment", SOLUTION),
						expertProp("Speed", "Speed of this TMA Solution", SOLUTION),
						expertProp("HostName",
								"Name of the track from which range/bearing measured", OFFSET),
						expertProp("OffsetRange", "Distance to start point on host track",
								OFFSET),
						expertProp("OffsetBearing",
								"Bearing from host track to start of this solution", OFFSET),
						expertProp("DTG_Start", "Start time for this TMA Solution",
								SOLUTION),
						expertProp("DTG_End", "End time for this TMA Solution", SOLUTION) };
				mine = res;
			}
			catch (final IntrospectionException e)
			{
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
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * name of the watchable list we're going to use as our origin
	 * 
	 * @return
	 */
	private String _hostName;

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
	 * our editable details
	 * 
	 */
	private transient TMASegmentInfo _myInfo = null;

	/**
	 * the layers we look at to find our host
	 * 
	 */
	private transient final Layers _theLayers;

	private SensorContactWrapper _firstSensorContact;

	private SensorContactWrapper _lastSensorContact;

	/**
	 * base constructor - sorts out the obvious
	 * 
	 * @param courseDegs
	 * @param speed
	 * @param offset
	 * @param theLayers
	 */
	public RelativeTMASegment(final double courseDegs, final WorldSpeed speed,
			final WorldVector offset, final Layers theLayers)
	{
		super(courseDegs, speed);
		_theLayers = theLayers;
		_offset = offset;
	}

	public RelativeTMASegment(final RelativeTMASegment relevantSegment,
			final SortedSet<Editable> theItems, final WorldVector theOffset)
	{
		// start off with the obvious bits
		this(relevantSegment._courseDegs, relevantSegment._speed, theOffset,
				relevantSegment._theLayers);

		// now the other bits
		this._referenceTrack = relevantSegment._referenceTrack;
		this._hostName = relevantSegment._hostName;

		// lastly, insert the fixes
		getData().addAll(theItems);

		// now sort out the name
		sortOutDate(null);

	}

	/**
	 * build up a solution from the supplied sensor data
	 * 
	 * @param observations
	 *          create a single position for the DTG of each solution
	 * @param offset
	 *          the range/brg from the host's position at the DTG of the first
	 *          observation
	 * @param speed
	 *          the initial target speed estimate
	 * @param courseDegs
	 *          the initial target course estimate
	 */
	public RelativeTMASegment(final SensorContactWrapper[] observations,
			final WorldVector offset, final WorldSpeed speed, final double courseDegs, final Layers theLayers)
	{
		this(courseDegs, speed, offset, theLayers);

		// sort out the origin
		final SensorContactWrapper scw = observations[0];
		_referenceTrack = scw.getSensor().getHost();

		// create the points
		createPointsFrom(observations);
	}

	/**
	 * build up a solution from the supplied sensor data
	 * 
	 * @param observations
	 *          create a single position for the DTG of each solution
	 * @param offset
	 *          the range/brg from the host's position at the DTG of the first
	 *          observation
	 * @param speed
	 *          the initial target speed estimate
	 * @param courseDegs
	 *          the initial target course estimate
	 */
	public RelativeTMASegment(final SensorWrapper sw, final WorldVector offset,
			final WorldSpeed speed, final double courseDegs, final Layers theLayers)
	{
		this(courseDegs, speed, offset, theLayers);

		// sort out the origin
		_referenceTrack = sw.getHost();

		// create the points
		createPointsFrom(sw);
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

	/**
	 * create a solution from all of the array of fixes
	 * 
	 * @param sw
	 */
	private void createPointsFrom(final SensorContactWrapper[] observations)
	{
		// better start looping
		for (int i = 0; i < observations.length; i++)
		{
			final SensorContactWrapper thisS = observations[i];

			doThisFix(thisS);
		}
	}

	/**
	 * create a solution from all of ths fixes in this sensor
	 * 
	 * @param sw
	 */
	private void createPointsFrom(final SensorWrapper sw)
	{
		final Enumeration<Editable> obs = sw.elements();
		while (obs.hasMoreElements())
		{
			final SensorContactWrapper thisS = (SensorContactWrapper) obs.nextElement();
			doThisFix(thisS);
		}
	}

	/**
	 * create a fix from this sensor item
	 * 
	 * @param thisS
	 */
	private void doThisFix(final SensorContactWrapper thisS)
	{
		// track the first & last visible sensor items
		if (_firstSensorContact == null)
			_firstSensorContact = thisS;
		_lastSensorContact = thisS;

		// and create a fix for this cut
		final FixWrapper newFix = createPointFor(thisS);
		newFix.setSymbolShowing(true);

		// store it
		addFix(newFix);
	}

	/**
	 * move the solution in or out from the ref track, maintaining the bearings to
	 * the host track (changing speed but not course)
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
			System.err
					.println("Failed to find sensor data to support fan stretch (RelativeTMASegment.fanStretch)");
			return;
		}

		// find the bearings
		final double startBrg = 2 * Math.PI + myStart.bearingFrom(hisStart);
		final double endBrg = 2 * Math.PI + myEnd.bearingFrom(hisEnd);

		double midBrg = (endBrg + startBrg) / 2;
		while (midBrg >= (2 * Math.PI))
			midBrg -= (2 * Math.PI);

		// how far has the user dragged it?
		double theRange = vector.getRange();

		// sort out which direction we're going
		final double theBrg = 2 * Math.PI + vector.getBearing();
		double theDelta = theBrg - midBrg;
		while (theDelta > 2 * Math.PI)
			theDelta -= 2 * Math.PI;

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
		final WorldLocation outerEnd = outerStart.add(new WorldVector(endBrg, _offset
				.getRange(), 0));

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
		final WorldDistance lenDegs = new WorldDistance(newLegLength, WorldDistance.DEGS);

		// and what's our speed to cover this distance?
		final double timeTakenMicros = (endDTG().getMicros() - startDTG().getMicros());
		final double timeTakenHours = timeTakenMicros / 1000 / 1000 / 60 / 60;
		final double speedKts = lenDegs.getValueIn(WorldDistance.NM) / timeTakenHours;

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

	public SensorContactWrapper getFirstSensorContact()
	{
		return _firstSensorContact;
	}

	/**
	 * the point on the host track that we're offset from
	 * 
	 * @return
	 */
	public WorldLocation getHostLocation()
	{
		WorldLocation res = null;

		// have we sorted out our reference track yet?
		if (_referenceTrack == null)
		{
			identifyReferenceTrack();
		}

		if (_referenceTrack != null)
		{
			// interpolate on the parent track
			final boolean oldInterpSetting = _referenceTrack.getInterpolatePoints();

			_referenceTrack.setInterpolatePoints(true);

			final Watchable[] pts = _referenceTrack.getNearestTo(startDTG());
			if (pts.length > 0)
			{
				res = pts[0].getLocation();
			}

			_referenceTrack.setInterpolatePoints(oldInterpSetting);
		}
		return res;
	}

	public String getHostName()
	{
		// just check we have some data
		if (_hostName == null)
		{
			if (_referenceTrack == null)
				identifyReferenceTrack();

			_hostName = _referenceTrack.getName();
		}

		return _hostName;
	}

	@Override
	public EditorType getInfo()
	{
		if (_myInfo == null)
			_myInfo = new TMASegmentInfo(this);
		return _myInfo;
	}

	public SensorContactWrapper getLastSensorContact()
	{
		return _lastSensorContact;
	}

	public WorldVector getOffset()
	{
		return _offset;
	}

	public double getOffsetBearing()
	{
		double res = 0;
		if (_offset != null)
			res = MWC.Algorithms.Conversions.Rads2Degs(_offset.getBearing());
		return res;
	}

	public WorldDistance getOffsetRange()
	{
		WorldDistance res = null;
		if (_offset != null)
			res = new WorldDistance(_offset.getRange(), WorldDistance.DEGS);
		return res;
	}

	public WatchableList getReferenceTrack()
	{
		// do we know it?
		if (_referenceTrack == null)
			identifyReferenceTrack();

		// fingers crossed it's sorted.
		return _referenceTrack;
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
		_referenceTrack = (TrackWrapper) _theLayers.findLayer(_hostName);
	}

	private void recalcPositions()
	{
		final Collection<Editable> items = getData();

		// ok - draw that line!
		WorldLocation tmaLastLoc = null;
		long tmaLastDTG = 0;

		for (final Iterator<Editable> iterator = items.iterator(); iterator.hasNext();)
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
				final WorldVector thisVec = vectorFor(timeDelta, thisF.getSpeed(),
						thisF.getCourse());
				tmaLastLoc.addToMe(thisVec);
			}

			// dump the location into the fix
			thisF.setFixLocationSilent(new WorldLocation(tmaLastLoc));

			// cool, remember the time.
			tmaLastDTG = thisTime;
		}
	}

	@Override
	public void rotate(final double brg, final WorldLocation origin)
	{
		final double theBrg = -brg;

		// right - we just rotate about the ends, and we use different
		// processing depending on which end is being shifted.
		final FixWrapper first = (FixWrapper) this.getData().iterator().next();
		if (first.getLocation().equals(origin))
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

		final Double courseVal = new Double(
				MWC.Algorithms.Conversions.Degs2Rads(this._courseDegs));
		final Double speedVal = null;
		updateCourseSpeed(courseVal, speedVal);

		// tell the segment it's being stretched
		int newCourse = (int) getCourse();
		if (newCourse < 0)
			newCourse += 360;
		_dragMsg = "[" + newCourse + "\u00B0]";

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
			res = nearestContact.getLocation();

		return res;
	}

	public void setDetectionBearing(final double detectionBearing)
	{
		_offset = new WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(detectionBearing),
				new WorldDistance(_offset.getRange(), WorldDistance.DEGS), null);
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
				return;

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
			for (final Iterator<FixWrapper> iterator = onesToRemove.iterator(); iterator
					.hasNext();)
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

			// don't worry about the location, we're going to DR it on anyway...
			final WorldLocation newLoc = null;
			final Fix newFix = new Fix(newEnd, newLoc,
					MWC.Algorithms.Conversions.Degs2Rads(this.getCourse()),
					MWC.Algorithms.Conversions.Kts2Yps(this.getSpeed().getValueIn(
							WorldSpeed.Kts)));

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

			this.add(newItem);
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
			theNewStart = this.getReferenceTrack().getStartDTG();
		}

		// ok, how far is this from the current end
		final long delta = theNewStart.getMicros() - startDTG().getMicros();

		// and what distance does this mean?
		final double deltaHrs = delta / 1000000d / 60d / 60d;
		final double distDegs = this.getSpeed().getValueIn(WorldSpeed.Kts) * deltaHrs
				/ 60;

		final double theDirection = MWC.Algorithms.Conversions
				.Degs2Rads(this.getCourse());

		// we don't need to worry about reversing the direction, since we have a -ve
		// distance

		// so what's the new origin?
		final WorldLocation currentStart = new WorldLocation(this.getTrackStart());
		final WorldLocation newOrigin = currentStart.add(new WorldVector(theDirection,
				distDegs, 0));

		// and what's the point on the host track
		final Watchable[] matches = this.getReferenceTrack().getNearestTo(theNewStart);
		final Watchable newRefPt = matches[0];
		final WorldVector newOffset = newOrigin.subtract(newRefPt.getLocation());

		// right, we know where the new track will be, see if we need to ditch any
		if (delta > 0)
		{
			// right, we're shortening the track.
			// check the end point is before the end
			if (theNewStart.getMicros() > endDTG().getMicros())
				return;

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
			for (final Iterator<FixWrapper> iterator = onesToRemove.iterator(); iterator
					.hasNext();)
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

			// don't worry about the location, we're going to DR it on anyway...
			final WorldLocation newLoc = null;
			final Fix newFix = new Fix(theNewStart, newLoc,
					MWC.Algorithms.Conversions.Degs2Rads(this.getCourse()),
					MWC.Algorithms.Conversions.Kts2Yps(this.getSpeed().getValueIn(
							WorldSpeed.Kts)));

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

			this.add(newItem);
		}

		// and sort out the new offset
		this._offset = newOffset;

		// tell any listeners that we've changed
		super.fireAdjusted();

	}

	public void setFirstSensorContact(final SensorContactWrapper firstSensorContact)
	{
		_firstSensorContact = firstSensorContact;
	}

	/**
	 * temporarily store the hostname, until we've finished loading and we can
	 * sort it out for real.
	 * 
	 * @param hostName
	 */
	public void setHostName(final String hostName)
	{
		// better trim what we've recived
		final String name = hostName.trim();

		// have we got meaningful data?
		if (name.length() > 0)
		{
			// right, see if we can find it
			if (_theLayers != null)
			{
				final Layer tgt = _theLayers.findLayer(name);
				if (tgt != null)
				{
					// clear the reference item we're currently looking at
					_referenceTrack = null;

					// now remember the new name
					_hostName = hostName;
				}
			}

		}

	}

	public void setLastSensorContact(final SensorContactWrapper lastSensorContact)
	{
		_lastSensorContact = lastSensorContact;
	}

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
		_offset.setValues(_offset.getBearing(),
				offsetRange.getValueIn(WorldDistance.DEGS), _offset.getDepth());
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
		final FixWrapper first = (FixWrapper) this.getData().iterator().next();
		if (first.getLocation().equals(origin))
		{
			// set the new course
			newCourse = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());
		}
		else
		{
			// reverse the course the course
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
		final long periodMillis = this.endDTG().getDate().getTime()
				- this.startDTG().getDate().getTime();

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
			newCourse += 360;

		this.setCourse(newCourse);

		final Double newCourseRads = new Double(
				MWC.Algorithms.Conversions.Degs2Rads(newCourse));
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
			final WorldVector newLeg = new WorldVector(thisLeg.getBearing(), theRngDegs,
					thisLeg.getDepth());

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
		final long periodMillis = this.endDTG().getDate().getTime()
				- this.startDTG().getDate().getTime();

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
	 * tell the data points that course and speed have been updated
	 * 
	 * @param courseVal
	 *          the (optional) course to update
	 * @param speedVal
	 *          the (optional) speed to update
	 */
	private void updateCourseSpeed(final Double courseValRads, final Double speedValKts)
	{
		final Enumeration<Editable> obs = this.elements();
		while (obs.hasMoreElements())
		{
			final FixWrapper thisS = (FixWrapper) obs.nextElement();
			if (courseValRads != null)
				thisS.setCourse(courseValRads.doubleValue());
			if (speedValKts != null)
				thisS.setSpeed(speedValKts.doubleValue());
		}
	}

}