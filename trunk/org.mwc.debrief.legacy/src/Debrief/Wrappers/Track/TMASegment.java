package Debrief.Wrappers.Track;

import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;

import Debrief.Tools.Tote.Watchable;
import Debrief.Tools.Tote.WatchableList;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * extension of track segment that represents a single TMA solution as a series
 * of fixes
 * 
 * @author Ian Mayo
 * 
 */
public class TMASegment extends TrackSegment
{
	/**
	 * class containing editable details of a track
	 */
	public final class TMASegmentInfo extends TrackSegmentInfo
	{

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

		private final static String SOLUTION = "Solution";
		private final static String OFFSET = "Offset";

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
						expertProp("Speed", "Speed of this TMA Solution", SOLUTION),
						expertProp("HostName",
								"Name of the track from which range/bearing measured", OFFSET),
						expertProp("OffsetRange", "Distance to start point on host track",
								OFFSET),
						expertProp("OffsetBearing",
								"Beraing from host track to start of this solution", OFFSET) };
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

	/**
	 * steady course (Degs)
	 * 
	 */
	private double _courseDegs;

	/**
	 * steady speed
	 * 
	 */
	private WorldSpeed _speed;

	/**
	 * the feature we're based on
	 * 
	 */
	private WatchableList _referenceTrack;

	/**
	 * the offset we apply to the origin
	 * 
	 */
	private WorldVector _offset;
	
	/** the base frequency (f0) for this tma segment
	 * 
	 */
	private double _baseFreq = 0;

	/**
	 * name of the watchable list we're going to use as our origin
	 * 
	 * @return
	 */
	private String _hostName;

	private final Layers _theLayers;

	/**
	 * base constructor - sorts out the obvious
	 * 
	 * @param courseDegs
	 * @param speed
	 * @param offset
	 * @param theLayers
	 */
	public TMASegment(final double courseDegs, final WorldSpeed speed,
			final WorldVector offset, Layers theLayers)
	{
		_theLayers = theLayers;
		_courseDegs = courseDegs;
		_speed = speed;
		_offset = offset;
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
	public TMASegment(SensorContactWrapper[] observations, WorldVector offset,
			WorldSpeed speed, double courseDegs, Layers theLayers)
	{
		this(courseDegs, speed, offset, theLayers);

		// sort out the origin
		SensorContactWrapper scw = observations[0];
		_referenceTrack = scw.getSensor().getHost();

		// create the points
		createPointsFrom(observations);
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
			WorldLocation newStart = origin.add(newLeg);

			// find out the offset from the origin
			WorldVector offset = newStart.subtract(getHostLocation());

			// update the offset to the new start location
			this.setOffsetBearing(MWC.Algorithms.Conversions.Rads2Degs(offset
					.getBearing()));
			this.setOffsetRange(new WorldDistance(offset.getRange(),
					WorldDistance.DEGS));
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

			// start with a recalculated origin
			WorldLocation hostReference = getHostLocation();
			WorldLocation startPoint = hostReference.add(_offset);

			// rotate the origin about the far end
			WorldLocation newStart = startPoint.rotatePoint(origin, -brg);

			// find out the offset from the origin
			WorldVector offset = newStart.subtract(hostReference);

			// update the offset to the new start location
			this.setOffsetBearing(MWC.Algorithms.Conversions.Rads2Degs(offset
					.getBearing()));
			this.setOffsetRange(new WorldDistance(offset.getRange(),
					WorldDistance.DEGS));

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
	public TMASegment(SensorWrapper sw, WorldVector offset, WorldSpeed speed,
			double courseDegs, Layers theLayers)
	{
		this(courseDegs, speed, offset, theLayers);

		// sort out the origin
		_referenceTrack = sw.getHost();

		// create the points
		createPointsFrom(sw);
	}

	public TMASegment(TMASegment relevantSegment, SortedSet<Editable> theItems,
			WorldVector theOffset)
	{
		// start off with the obvious bits
		this(relevantSegment._courseDegs, relevantSegment._speed, theOffset, relevantSegment._theLayers);
		
		// now the other bits
		this._referenceTrack = relevantSegment._referenceTrack;
		this._hostName = relevantSegment._hostName;
		
		// lastly, insert the fixes
		getData().addAll(theItems);

		// now sort out the name
		sortOutDate();
		
	}

	private Fix createFix(long thisT)
	{
		Fix fix = new Fix(new HiResDate(thisT), new WorldLocation(0, 0, 0),
				MWC.Algorithms.Conversions.Degs2Rads(_courseDegs), _speed
						.getValueIn(WorldSpeed.ft_sec) / 3);
		return fix;
	}

	/**
	 * create a fix at the specified dtg
	 * 
	 * @param thisS
	 * @return
	 */
	private FixWrapper createPointFor(SensorContactWrapper thisS)
	{
		FixWrapper newFix = new FixWrapper(createFix(thisS.getDTG().getDate()
				.getTime()));
		return newFix;
	}

	private void createPointsFrom(SensorContactWrapper[] observations)
	{
		System.err.println("about to create:" + observations.length + " points");

		// better start looping
		for (int i = 0; i < observations.length; i++)
		{
			SensorContactWrapper thisS = observations[i];
			FixWrapper newFix = createPointFor(thisS);
			newFix.setSymbolShowing(true);
			addFix(newFix);
		}
	}

	private void createPointsFrom(SensorWrapper sw)
	{
		Enumeration<Editable> obs = sw.elements();
		while (obs.hasMoreElements())
		{
			SensorContactWrapper thisS = (SensorContactWrapper) obs.nextElement();
			FixWrapper newFix = createPointFor(thisS);
			newFix.setSymbolShowing(true);			
			addFix(newFix);
		}
	}

	/**
	 * get the current course of this leg
	 * 
	 * @return course (degs)
	 */
	public double getCourse()
	{
		return _courseDegs;
	}

	public double getDetectionBearing()
	{
		return MWC.Algorithms.Conversions.Rads2Degs(_offset.getBearing());
	}

	public WorldDistance getDetectionRange()
	{
		return new WorldDistance(_offset.getRange(), WorldDistance.DEGS);
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
		return new TMASegmentInfo(this);
	}

	public WorldVector getOffset()
	{
		return _offset;
	}

	public double getOffsetBearing()
	{
		return MWC.Algorithms.Conversions.Rads2Degs(_offset.getBearing());
	}

	public WorldDistance getOffsetRange()
	{
		return new WorldDistance(_offset.getRange(), WorldDistance.DEGS);
	}
	

	public double getBaseFrequency()
	{
		return _baseFreq;
	}

	public void setBaseFrequency(double baseFrequency)
	{
		_baseFreq = baseFrequency;
	}


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
			Watchable[] pts = _referenceTrack.getNearestTo(startDTG());
			if (pts.length > 0)
			{
				res = pts[0].getLocation();
			}
		}
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

	@Override
	public boolean getPlotRelative()
	{
		// always return true for TMA Segments
		return true;
	}

	public WatchableList getReferenceTrack()
	{
		return _referenceTrack;
	}

	/**
	 * the constant speed of this segment
	 * 
	 * @return the current speed
	 */
	public WorldSpeed getSpeed()
	{
		return _speed;
	}

	/**
	 * message that we plot 1/2 way along segment when it's being stretched or
	 * rotated
	 * 
	 */
	private String _dragMsg;

	@Override
	public void paint(CanvasType dest)
	{
		Collection<Editable> items = getData();

		// ok - draw that line!
		Point lastPoint = null;
		WorldLocation tmaLastLoc = null;
		long tmaLastDTG = 0;
		
		// try to create a dotted line
		dest.setLineStyle(CanvasType.DOTTED);

		// remember the ends, so we can plot a point 1/2 way along them
		WorldLocation firstEnd = null;
		WorldLocation lastEnd = null;

		for (Iterator<Editable> iterator = items.iterator(); iterator.hasNext();)
		{
			FixWrapper thisF = (FixWrapper) iterator.next();

			long thisTime = thisF.getDateTimeGroup().getDate().getTime();

			// ok, is this our first location?
			if (tmaLastLoc == null)
			{
				tmaLastLoc = new WorldLocation(getTrackStart());
				firstEnd = new WorldLocation(tmaLastLoc);
			}
			else
			{
				// calculate a new vector
				long timeDelta = thisTime - tmaLastDTG;
				WorldVector thisVec = vectorFor(timeDelta, thisF.getSpeed(), thisF
						.getCourse());
				tmaLastLoc.addToMe(thisVec);

				lastEnd = new WorldLocation(tmaLastLoc);
			}

			// dump the location into the fix
			thisF.setFixLocationSilent(new WorldLocation(tmaLastLoc));

			// cool, remember the time.
			tmaLastDTG = thisTime;

			Point thisPoint = dest.toScreen(thisF.getFixLocation());

			// do we have enough for a line?
			if (lastPoint != null)
			{
				// draw that line
				dest.drawLine(lastPoint.x, lastPoint.y, thisPoint.x, thisPoint.y);
			}

			lastPoint = new Point(thisPoint);

			// also draw in a marker for this point
			dest.drawRect(lastPoint.x - 1, lastPoint.y - 1, 3, 3);
		}

		// ok, plot the 1/2 way message
		if (_dragMsg != null)
		{
			WorldArea area = new WorldArea(firstEnd, lastEnd);
			WorldLocation centre = area.getCentre();
			Point pt = dest.toScreen(centre);
			dest.setColor(java.awt.Color.red);
			dest.drawText(_dragMsg, pt.x, pt.y + 15);
		}
	}

	/**
	 * the current course
	 * 
	 * @param course
	 *          (degs)
	 */
	public void setCourse(double course)
	{
		_courseDegs = course;

		double crseRads = MWC.Algorithms.Conversions.Degs2Rads(course);
		Collection<Editable> data = getData();
		for (Iterator<Editable> iterator = data.iterator(); iterator.hasNext();)
		{
			FixWrapper fix = (FixWrapper) iterator.next();
			fix.getFix().setCourse(crseRads);
		}

		// ditch our temp vector, we've got to recalc it
		_vecTempLastDTG = -2;
	}

	public void setDetectionBearing(double detectionBearing)
	{
		_offset = new WorldVector(MWC.Algorithms.Conversions
				.Degs2Rads(detectionBearing), new WorldDistance(_offset.getRange(),
				WorldDistance.DEGS), null);
	}

	public void setDetectionRange(WorldDistance detectionRange)
	{
		_offset = new WorldVector(_offset.getBearing(), detectionRange, null);
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
		String name = hostName.trim();

		// have we got meaningful data?
		if (name.length() > 0)
		{
			// right, see if we can find it
			if (_theLayers != null)
			{
				Layer tgt = _theLayers.findLayer(name);
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

	public void setOffsetBearing(double offsetBearing)
	{
		_offset.setValues(MWC.Algorithms.Conversions.Degs2Rads(offsetBearing),
				_offset.getRange(), _offset.getDepth());
	}

	public void setOffsetRange(WorldDistance offsetRange)
	{
		_offset.setValues(_offset.getBearing(), offsetRange
				.getValueIn(WorldDistance.DEGS), _offset.getDepth());
	}

	/**
	 * find the reference track for this relative solution
	 * 
	 */
	private void identifyReferenceTrack()
	{
		_referenceTrack = (WatchableList) _theLayers.findLayer(_hostName);
	}

	/**
	 * set the constant speed of this segment
	 * 
	 * @param speed
	 *          the new speed
	 */
	public void setSpeed(WorldSpeed speed)
	{
		_speed = speed;

		double spdYps = speed.getValueIn(WorldSpeed.ft_sec) / 3;
		Collection<Editable> data = getData();
		for (Iterator<Editable> iterator = data.iterator(); iterator.hasNext();)
		{
			FixWrapper fix = (FixWrapper) iterator.next();
			fix.getFix().setSpeed(spdYps);
		}

		// ditch our temp vector, we've got to recalc it
		_vecTempLastDTG = -2;

	}

	@Override
	public void shift(WorldVector vector)
	{
		// really, we just need to add this vector to our orign
		WorldLocation tmpOrigin = new WorldLocation(getTrackStart());
		tmpOrigin.addToMe(_offset);
		tmpOrigin.addToMe(vector);

		_offset = tmpOrigin.subtract(getTrackStart());

		// clear the drag message, there's nothing to show message
		_dragMsg = null;
	}

	/**
	 * shear this whole track to the supplied destination
	 * 
	 * @param cursor
	 *          where the user's hovering
	 * @param origin
	 *          origin of stretch, probably one end of the track
	 */
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
			// reverse the course the course
			offset = origin.subtract(cursor);
			newCourse = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());

			// right, we've got to shift the start point to the relevant location,
			// and fix the bearing

			// find out the offset from the origin
			WorldVector newOffset = cursor.subtract(getHostLocation());

			// update the offset to the new start location
			this.setOffsetBearing(MWC.Algorithms.Conversions.Rads2Degs(newOffset
					.getBearing()));
			this.setOffsetRange(new WorldDistance(newOffset.getRange(),
					WorldDistance.DEGS));
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
		this.setCourse(newCourse);
		
		// tidy the course
		while(newCourse < 0)
			newCourse += 360;

		// tell the segment it's being stretched
		_dragMsg = "[" + (int) newSpeed.getValueIn(WorldSpeed.Kts) + " kts "
				+ (int) newCourse + "\u00B0]";

	}

}