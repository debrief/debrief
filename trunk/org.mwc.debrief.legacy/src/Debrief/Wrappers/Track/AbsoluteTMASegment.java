package Debrief.Wrappers.Track;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import Debrief.Wrappers.FixWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

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
			PropertyDescriptor[] parent = super.getPropertyDescriptors();
			PropertyDescriptor[] mine = null;

			try
			{
				PropertyDescriptor[] res =
				{
						expertProp("Course", "Course of this TMA Solution", SOLUTION),
						expertProp("BaseFrequency", "The base frequency of this TMA segment", SOLUTION),
						expertProp("Speed", "Speed of this TMA Solution", SOLUTION) };
				mine = res;
			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}

			// now combine them.
			PropertyDescriptor[] bigRes = new PropertyDescriptor[parent.length
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

	private WorldLocation _origin;
	
	/**
	 * base constructor - sorts out the obvious
	 * 
	 * @param courseDegs
	 * @param speed
	 * @param origin
	 */
	public AbsoluteTMASegment(final double courseDegs, final WorldSpeed speed,
			WorldLocation origin, HiResDate startTime, HiResDate endTime)
	{
		super(courseDegs, speed);
		_origin = origin;
		
		createDataFrom(startTime, endTime);
	}




	/** create a track for the indicated time period
	 * 
	 * @param startTime
	 * @param endTime
	 */
	private void createDataFrom(HiResDate startTime, HiResDate endTime) {
		long tStart = startTime.getDate().getTime();
		long tEnd  = endTime.getDate().getTime();
		long interval = 60 * 1000;
		for(long tNow = tStart;tNow <= tEnd; tNow += interval)
		{
			FixWrapper newFix = createFixAt(tNow);
			this.addFix(newFix);
		}
	}




	@Override
	public EditorType getInfo()
	{
		return new TMASegmentInfo(this);
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
	public void rotate(double brg, final WorldLocation origin)
	{
		brg = -brg;

		// right - we just rotate about the ends, and we use different
		// processing depending on which end is being shifted.
		FixWrapper first = (FixWrapper) this.getData().iterator().next();
		if (first.getLocation().equals(origin))
		{
			// right, we're dragging around the last point. Couldn't be easier,
			// just change our course
			double brgDegs = MWC.Algorithms.Conversions.Rads2Degs(brg);
			double newBrg = this.getCourse() + brgDegs;
			// right, the start is the origin, so we just set our course to the
			// bearing
			this.setCourse(newBrg);
		}
		else
		{
			// right, we've got to shift the start point to the relevant location,
			// and fix the bearing

			// rotate the origin about the far end
			WorldLocation newStart = _origin.rotatePoint(origin, -brg);

			// find out the offset from the origin
			_origin = newStart;

			// what's the course from the new start to the origin?
			WorldVector vec = origin.subtract(newStart);

			// update the course
			this.setCourse(MWC.Algorithms.Conversions.Rads2Degs(vec.getBearing()));

		}

		// tell the segment it's being stretched
		int newCourse = (int) getCourse();
		if (newCourse < 0)
			newCourse += 360;
		_dragMsg = "[" + newCourse + "\u00B0]";

	}


	@Override
	public void shear(WorldLocation cursor, final WorldLocation origin)
	{
		WorldVector offset = cursor.subtract(origin);
		double rngDegs = offset.getRange();

		// make it always +ve, we'll just overwrite ourselves anyway
		rngDegs = Math.abs(rngDegs);

		double newCourse;

		// right - we just stretch about the ends, and we use different
		// processing depending on which end is being shifted.
		FixWrapper first = (FixWrapper) this.getData().iterator().next();
		if (first.getLocation().equals(origin))
		{
			// set the new course
			newCourse = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());
		}
		else
		{
			// reverse the course, of course
			offset = origin.subtract(cursor);
			newCourse = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());

			// right, we've got to shift the start point to the relevant location,
			// and fix the bearing
			_origin = origin;
		}

		// how long do we have for the travel?
		long periodMillis = this.endDTG().getDate().getTime()
				- this.startDTG().getDate().getTime();

		// what's that in hours?
		double periodHours = periodMillis / 1000d / 60d / 60d;

		// what's distance in minutes?
		double distMins = rngDegs * 60;

		// how far must we go to sort this
		double spdKts = distMins / periodHours;

		WorldSpeed newSpeed = new WorldSpeed(spdKts, WorldSpeed.Kts);
		this.setSpeed(newSpeed);

		// tidy the course
		while(newCourse < 0)
			newCourse += 360;

		this.setCourse(newCourse);
		

		// tell the segment it's being stretched
		_dragMsg = "[" + (int) newSpeed.getValueIn(WorldSpeed.Kts) + " kts "
				+ (int) newCourse + "\u00B0]";

	}

	@Override
	public void shift(WorldVector vector)
	{
		// really, we just need to add this vector to our orign
//		WorldLocation tmpOrigin = new WorldLocation(getTrackStart());
//		tmpOrigin.addToMe(_offset);
//		tmpOrigin.addToMe(vector);

		_origin.addToMe(vector);
		// = tmpOrigin.subtract(getTrackStart());

		// clear the drag message, there's nothing to show message
		_dragMsg = null;
	}

	/**
	 * stretch this whole track to the supplied distance
	 * 
	 * @param rngDegs
	 *          distance to stretch through (degs)
	 * @param origin
	 *          origin of stretch, probably one end of the track
	 */
	public void stretch(double rngDegs, final WorldLocation origin)
	{
		// make it always +ve, we'll just overwrite ourselves anyway
		rngDegs = Math.abs(rngDegs);

		// right - we just stretch about the ends, and we use different
		// processing depending on which end is being shifted.
		FixWrapper first = (FixWrapper) this.getData().iterator().next();
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
			WorldVector thisLeg = getTrackStart().subtract(origin);

			// now change the distance
			WorldVector newLeg = new WorldVector(thisLeg.getBearing(), rngDegs,
					thisLeg.getDepth());

			// calculate the new start point
			_origin = origin.add(newLeg);
		}

		// how long do we have for the travel?
		long periodMillis = this.endDTG().getDate().getTime()
				- this.startDTG().getDate().getTime();

		// what's that in hours?
		double periodHours = periodMillis / 1000d / 60d / 60d;

		// what's distance in minutes?
		double distMins = rngDegs * 60;

		// how far must we go to sort this
		double spdKts = distMins / periodHours;

		WorldSpeed newSpeed = new WorldSpeed(spdKts, WorldSpeed.Kts);
		this.setSpeed(newSpeed);

		// tell the segment it's being stretched
		_dragMsg = "[" + (int) newSpeed.getValueIn(WorldSpeed.Kts) + " kts]";

	}

}