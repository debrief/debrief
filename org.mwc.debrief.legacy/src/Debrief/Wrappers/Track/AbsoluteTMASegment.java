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
import java.util.SortedSet;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * extension of track segment that represents a single TMA solution as a series
 * of fixes - based at a fixed origin
 * 
 * @author Ian Mayo
 * 
 */
public class AbsoluteTMASegment extends CoreTMASegment
{

	/**
	 * class containing editable details of a track
	 */
	public final class TMASegmentInfo extends TrackSegmentInfo
	{

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
						displayExpertProp("Time_Start", "Start time",  "Start time for this TMA Solution",
								SOLUTION),
						displayExpertProp("TimeEnd", "End time", "End time for this TMA Solution", SOLUTION),
						expertProp("Course", "Course of this TMA Solution", SOLUTION),
						displayExpertProp("BaseFrequency", "Base frequency",
								"The base frequency of this TMA segment", SOLUTION),
						expertProp("Speed", "Speed of this TMA Solution", SOLUTION) };
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

	protected HiResDate _startTime;

	protected HiResDate _endTime;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private WorldLocation _origin;

	/**
	 * copy contructor - build ourselves from the provided data
	 * 
	 * @param theTMA
	 * @param p1
	 * @param location
	 * @param startTime
	 * @param endTime
	 */
	public AbsoluteTMASegment(final AbsoluteTMASegment relevantSegment,
			final SortedSet<Editable> theItems, final WorldLocation location,
			final HiResDate startTime, final HiResDate endTime)
	{
		// start off with the obvious bits
		super(relevantSegment._courseDegs, relevantSegment._speed);
		_origin = relevantSegment._origin;
		_startTime = startTime;
		_endTime = endTime;

		// lastly, insert the fixes
		getData().addAll(theItems);

		// now sort out the name
		sortOutDate(null);

	}

	/**
	 * base constructor - sorts out the obvious
	 * 
	 * @param courseDegs
	 * @param speed
	 * @param origin
	 */
	public AbsoluteTMASegment(final double courseDegs, final WorldSpeed speed,
			final WorldLocation origin, final HiResDate startTime, final HiResDate endTime)
	{
		super(courseDegs, speed);
		_origin = origin;
		_startTime = startTime;
		_endTime = endTime;

		// do we have time limits?
		if (startTime != null)
		{
			createDataFrom(startTime, endTime);
		}
		else
		{
			// leave them empty
		}

		// now sort out the name
		sortOutDate(null);
	}

	/**
	 * create a track for the indicated time period
	 * 
	 * @param startTime
	 * @param endTime
	 */
	private void createDataFrom(final HiResDate startTime, final HiResDate endTime)
	{
		// ditch any existing data
		this.removeAllElements();

		// and stick some fresh ones in
		final long tStart = startTime.getDate().getTime();
		final long tEnd = endTime.getDate().getTime();
		final long interval = 60 * 1000;
		for (long tNow = tStart; tNow <= tEnd; tNow += interval)
		{
			final FixWrapper newFix = createFixAt(tNow, tStart);
			newFix.setSymbolShowing(true);
			this.addFix(newFix);
		}
	}
	
	/**
	 * create a nice shiny fix at the indicated time
	 * 
	 * @param theTime
	 * @return the new fix, with valid course and speed
	 */
	protected FixWrapper createFixAt(final long theTime, final long startTime)
	{
		
		// find out how far we've travelled
		double distM = _speed.getValueIn(WorldSpeed.M_sec) * (theTime - startTime) / 1000;		
		double courseRads = MWC.Algorithms.Conversions.Degs2Rads(_courseDegs);
		
		WorldDistance theDist = new WorldDistance(distM, WorldDistance.METRES);
		WorldLocation newLoc = this._origin.add(new WorldVector(courseRads, theDist, 
				new WorldDistance(0, WorldDistance.METRES)));
		
		final Fix fix = new Fix(new HiResDate(theTime), newLoc,
				courseRads,
				_speed.getValueIn(WorldSpeed.ft_sec) / 3);

		final FixWrapper newFix = new FixWrapper(fix);
		newFix.resetName();
		newFix.setLabelFormat("HHmm.ss");
		return newFix;
	}


	@Override
	public EditorType getInfo()
	{
		return new TMASegmentInfo(this);
	}

	public HiResDate getTime_Start()
	{
		return _startTime;
	}

	public HiResDate getTimeEnd()
	{
		return _endTime;
	}

	/**
	 * get the start of this tma segment
	 * 
	 * @return
	 */
	@Override
	public WorldLocation getTrackStart()
	{
		return _origin;
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
			// right, we've got to shift the start point to the relevant location,
			// and fix the bearing

			// rotate the origin about the far end
			final WorldLocation newStart = _origin.rotatePoint(origin, -theBrg);

			// find out the offset from the origin
			_origin = newStart;

			// what's the course from the new start to the origin?
			final WorldVector vec = origin.subtract(newStart);

			// update the course
			this.setCourse(MWC.Algorithms.Conversions.Rads2Degs(vec.getBearing()));

		}

		// tell the segment it's being stretched
		int newCourse = (int) getCourse();
		if (newCourse < 0)
			newCourse += 360;
		_dragMsg = "[" + newCourse + "\u00B0]";
		
		fireAdjusted();
	}

	public void setTime_Start(final HiResDate timeStart)
	{
		_startTime = timeStart;

		// and update our data
		createDataFrom(_startTime, _endTime);
	}

	public void setTimeEnd(final HiResDate timeEnd)
	{
		_endTime = timeEnd;

		// and update our data
		createDataFrom(_startTime, _endTime);
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
		FixWrapper firstL = (FixWrapper) this.first();
		FixWrapper lastL = (FixWrapper) this.last();
		
		if (firstL.getLocation().equals(origin))
		{
			// ok - the origin our first point - so we're dragging the far end
			
			// set the new course
			newCourse = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());
		}
		else
		{
			// ok - the origin our last point - so we're dragging the origin of the segment
			
			// right, we've got to shift the start point to the relevant location,
			// and fix the bearing
			_origin = cursor;
			
			// reverse the course, of course
			offset = lastL.getLocation().subtract(cursor);
			newCourse = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());
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

		// tell the segment it's being stretched
		
		final String spdTxt = MWC.Utilities.TextFormatting.GeneralFormat
				.formatOneDecimalPlace(newSpeed.getValueIn(WorldSpeed.Kts));

		_dragMsg = "[" + spdTxt + " kts "
				+ (int) newCourse + "\u00B0]";

		super.firePropertyChange("Course", newCourse, newCourse);
		
		fireAdjusted();

	}

	@Override
	public void shift(final WorldVector vector)
	{
		// really, we just need to add this vector to our orign
		// WorldLocation tmpOrigin = new WorldLocation(getTrackStart());
		// tmpOrigin.addToMe(_offset);
		// tmpOrigin.addToMe(vector);

		_origin.addToMe(vector);
		// = tmpOrigin.subtract(getTrackStart());

		// clear the drag message, there's nothing to show message
		_dragMsg = null;
		
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
		final double rangDegs = Math.abs(rngDegs);
		
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
			// right, we've got to shift the start point to the relevant location,
			// and fix the bearing

			// calculate a new start point
			final WorldVector thisLeg = getTrackStart().subtract(origin);

			// now change the distance
			final WorldVector newLeg = new WorldVector(thisLeg.getBearing(), rangDegs,
					thisLeg.getDepth());

			// calculate the new start point
			_origin = origin.add(newLeg);
		}

		// how long do we have for the travel?
		final long periodMillis = this.endDTG().getDate().getTime()
				- this.startDTG().getDate().getTime();

		// what's that in hours?
		final double periodHours = periodMillis / 1000d / 60d / 60d;

		// what's distance in minutes?
		final double distMins = rangDegs * 60;

		// how far must we go to sort this
		final double spdKts = distMins / periodHours;

		final WorldSpeed newSpeed = new WorldSpeed(spdKts, WorldSpeed.Kts);
		this.setSpeed(newSpeed);

		// tell the segment it's being stretched
		_dragMsg = "[" + (int) newSpeed.getValueIn(WorldSpeed.Kts) + " kts]";

		fireAdjusted();

	}

}