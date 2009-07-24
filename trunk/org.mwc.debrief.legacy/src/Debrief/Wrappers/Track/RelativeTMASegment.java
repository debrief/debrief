package Debrief.Wrappers.Track;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.SortedSet;

import Debrief.Tools.Tote.Watchable;
import Debrief.Tools.Tote.WatchableList;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * extension of track segment that represents a single TMA solution as a series
 * of fixes - based on an offset from a specified other track
 * 
 * @author Ian Mayo
 * 
 */
public class RelativeTMASegment extends CoreTMASegment {
	/**
	 * class containing editable details of a track
	 */
	public final class TMASegmentInfo extends TrackSegmentInfo {

		private final static String OFFSET = "Offset";

		private final static String SOLUTION = "Solution";

		/**
		 * constructor for this editor, takes the actual track as a parameter
		 * 
		 * @param data
		 *            track being edited
		 */
		public TMASegmentInfo(final TrackSegment data) {
			super(data);
		}

		@Override
		public final PropertyDescriptor[] getPropertyDescriptors() {
			// start off with the parent
			PropertyDescriptor[] parent = super.getPropertyDescriptors();
			PropertyDescriptor[] mine = null;

			try {
				PropertyDescriptor[] res = {
						expertProp("Course", "Course of this TMA Solution",
								SOLUTION),
						expertProp("BaseFrequency",
								"The base frequency of this TMA segment",
								SOLUTION),
						expertProp("Speed", "Speed of this TMA Solution",
								SOLUTION),
						expertProp(
								"HostName",
								"Name of the track from which range/bearing measured",
								OFFSET),
						expertProp("OffsetRange",
								"Distance to start point on host track", OFFSET),
						expertProp(
								"OffsetBearing",
								"Beraing from host track to start of this solution",
								OFFSET) };
				mine = res;
			} catch (final IntrospectionException e) {
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
	private WatchableList _referenceTrack;

	/**
	 * the layers we look at to find our host
	 * 
	 */
	private final Layers _theLayers;

	/**
	 * base constructor - sorts out the obvious
	 * 
	 * @param courseDegs
	 * @param speed
	 * @param offset
	 * @param theLayers
	 */
	public RelativeTMASegment(final double courseDegs, final WorldSpeed speed,
			final WorldVector offset, Layers theLayers) {
		super(courseDegs, speed);
		_theLayers = theLayers;
		_offset = offset;
	}

	public RelativeTMASegment(RelativeTMASegment relevantSegment,
			SortedSet<Editable> theItems, WorldVector theOffset) {
		// start off with the obvious bits
		this(relevantSegment._courseDegs, relevantSegment._speed, theOffset,
				relevantSegment._theLayers);

		// now the other bits
		this._referenceTrack = relevantSegment._referenceTrack;
		this._hostName = relevantSegment._hostName;

		// lastly, insert the fixes
		getData().addAll(theItems);

		// now sort out the name
		sortOutDate();

	}

	/**
	 * build up a solution from the supplied sensor data
	 * 
	 * @param observations
	 *            create a single position for the DTG of each solution
	 * @param offset
	 *            the range/brg from the host's position at the DTG of the first
	 *            observation
	 * @param speed
	 *            the initial target speed estimate
	 * @param courseDegs
	 *            the initial target course estimate
	 */
	public RelativeTMASegment(SensorContactWrapper[] observations,
			WorldVector offset, WorldSpeed speed, double courseDegs,
			Layers theLayers) {
		this(courseDegs, speed, offset, theLayers);

		// sort out the origin
		SensorContactWrapper scw = observations[0];
		_referenceTrack = scw.getSensor().getHost();

		// create the points
		createPointsFrom(observations);
	}

	/**
	 * build up a solution from the supplied sensor data
	 * 
	 * @param observations
	 *            create a single position for the DTG of each solution
	 * @param offset
	 *            the range/brg from the host's position at the DTG of the first
	 *            observation
	 * @param speed
	 *            the initial target speed estimate
	 * @param courseDegs
	 *            the initial target course estimate
	 */
	public RelativeTMASegment(SensorWrapper sw, WorldVector offset,
			WorldSpeed speed, double courseDegs, Layers theLayers) {
		this(courseDegs, speed, offset, theLayers);

		// sort out the origin
		_referenceTrack = sw.getHost();

		// create the points
		createPointsFrom(sw);
	}

	private void createPointsFrom(SensorContactWrapper[] observations) {
		System.err
				.println("about to create:" + observations.length + " points");

		// better start looping
		for (int i = 0; i < observations.length; i++) {
			SensorContactWrapper thisS = observations[i];
			FixWrapper newFix = createPointFor(thisS);
			newFix.setSymbolShowing(true);
			addFix(newFix);
		}
	}

	/**
	 * create a fix at the specified dtg
	 * 
	 * @param thisS
	 * @return
	 */
	protected FixWrapper createPointFor(SensorContactWrapper thisS) {
		long theTime = thisS.getDTG().getDate().getTime();
		return createFixAt(theTime);
	}

	private void createPointsFrom(SensorWrapper sw) {
		Enumeration<Editable> obs = sw.elements();
		while (obs.hasMoreElements()) {
			SensorContactWrapper thisS = (SensorContactWrapper) obs
					.nextElement();
			FixWrapper newFix = createPointFor(thisS);
			newFix.setSymbolShowing(true);
			addFix(newFix);
		}
	}

	public double getDetectionBearing() {
		return MWC.Algorithms.Conversions.Rads2Degs(_offset.getBearing());
	}

	public WorldDistance getDetectionRange() {
		return new WorldDistance(_offset.getRange(), WorldDistance.DEGS);
	}

	public WorldLocation getHostLocation() {
		WorldLocation res = null;

		// have we sorted out our reference track yet?
		if (_referenceTrack == null) {
			identifyReferenceTrack();
		}

		if (_referenceTrack != null) {
			Watchable[] pts = _referenceTrack.getNearestTo(startDTG());
			if (pts.length > 0) {
				res = pts[0].getLocation();
			}
		}
		return res;
	}

	public String getHostName() {
		// just check we have some data
		if (_hostName == null) {
			if (_referenceTrack == null)
				identifyReferenceTrack();

			_hostName = _referenceTrack.getName();
		}

		return _hostName;
	}

	@Override
	public EditorType getInfo() {
		return new TMASegmentInfo(this);
	}

	public WorldVector getOffset() {
		return _offset;
	}

	public double getOffsetBearing() {
		return MWC.Algorithms.Conversions.Rads2Degs(_offset.getBearing());
	}

	public WorldDistance getOffsetRange() {
		return new WorldDistance(_offset.getRange(), WorldDistance.DEGS);
	}

	public WatchableList getReferenceTrack() {
		return _referenceTrack;
	}

	/**
	 * get the start of this tma segment
	 * 
	 * @return
	 */
	@Override
	public WorldLocation getTrackStart() {
		WorldLocation res = getHostLocation();
		if (res != null) {
			res = res.add(_offset);
		}
		return res;
	}

	/**
	 * find the reference track for this relative solution
	 * 
	 */
	private void identifyReferenceTrack() {
		_referenceTrack = (WatchableList) _theLayers.findLayer(_hostName);
	}

	@Override
	public void rotate(double brg, final WorldLocation origin) {
		brg = -brg;

		// right - we just rotate about the ends, and we use different
		// processing depending on which end is being shifted.
		FixWrapper first = (FixWrapper) this.getData().iterator().next();
		if (first.getLocation().equals(origin)) {
			// right, we're dragging around the last point. Couldn't be easier,
			// just change our course
			double brgDegs = MWC.Algorithms.Conversions.Rads2Degs(brg);
			double newBrg = this.getCourse() + brgDegs;
			// right, the start is the origin, so we just set our course to the
			// bearing
			this.setCourse(newBrg);
		} else {
			// right, we've got to shift the start point to the relevant
			// location,
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
			this.setCourse(MWC.Algorithms.Conversions.Rads2Degs(vec
					.getBearing()));

		}

		// tell the segment it's being stretched
		int newCourse = (int) getCourse();
		if (newCourse < 0)
			newCourse += 360;
		_dragMsg = "[" + newCourse + "\u00B0]";

	}

	public void setDetectionBearing(double detectionBearing) {
		_offset = new WorldVector(MWC.Algorithms.Conversions
				.Degs2Rads(detectionBearing), new WorldDistance(_offset
				.getRange(), WorldDistance.DEGS), null);
	}

	public void setDetectionRange(WorldDistance detectionRange) {
		_offset = new WorldVector(_offset.getBearing(), detectionRange, null);
	}

	/**
	 * temporarily store the hostname, until we've finished loading and we can
	 * sort it out for real.
	 * 
	 * @param hostName
	 */
	public void setHostName(final String hostName) {
		// better trim what we've recived
		String name = hostName.trim();

		// have we got meaningful data?
		if (name.length() > 0) {
			// right, see if we can find it
			if (_theLayers != null) {
				Layer tgt = _theLayers.findLayer(name);
				if (tgt != null) {
					// clear the reference item we're currently looking at
					_referenceTrack = null;

					// now remember the new name
					_hostName = hostName;
				}
			}

		}

	}

	public void setOffsetBearing(double offsetBearing) {
		_offset.setValues(MWC.Algorithms.Conversions.Degs2Rads(offsetBearing),
				_offset.getRange(), _offset.getDepth());
	}

	public void setOffsetRange(WorldDistance offsetRange) {
		_offset.setValues(_offset.getBearing(), offsetRange
				.getValueIn(WorldDistance.DEGS), _offset.getDepth());
	}

	@Override
	public void shear(WorldLocation cursor, final WorldLocation origin) {
		WorldVector offset = cursor.subtract(origin);
		double rngDegs = offset.getRange();

		// make it always +ve, we'll just overwrite ourselves anyway
		rngDegs = Math.abs(rngDegs);

		double newCourse;

		// right - we just stretch about the ends, and we use different
		// processing depending on which end is being shifted.
		FixWrapper first = (FixWrapper) this.getData().iterator().next();
		if (first.getLocation().equals(origin)) {
			// set the new course
			newCourse = MWC.Algorithms.Conversions.Rads2Degs(offset
					.getBearing());
		} else {
			// reverse the course the course
			offset = origin.subtract(cursor);
			newCourse = MWC.Algorithms.Conversions.Rads2Degs(offset
					.getBearing());

			// right, we've got to shift the start point to the relevant
			// location,
			// and fix the bearing

			// find out the offset from the origin
			WorldVector newOffset = cursor.subtract(getHostLocation());

			// update the offset to the new start location
			this.setOffsetBearing(MWC.Algorithms.Conversions
					.Rads2Degs(newOffset.getBearing()));
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

		// tidy the course
		while (newCourse < 0)
			newCourse += 360;

		this.setCourse(newCourse);

		// tell the segment it's being stretched
		_dragMsg = "[" + (int) newSpeed.getValueIn(WorldSpeed.Kts) + " kts "
				+ (int) newCourse + "\u00B0]";

	}

	@Override
	public void shift(WorldVector vector) {
		// really, we just need to add this vector to our orign
		WorldLocation tmpOrigin = new WorldLocation(getTrackStart());
		tmpOrigin.addToMe(_offset);
		tmpOrigin.addToMe(vector);

		_offset = tmpOrigin.subtract(getTrackStart());

		// clear the drag message, there's nothing to show message
		_dragMsg = null;
	}

	/**
	 * stretch this whole track to the supplied distance
	 * 
	 * @param rngDegs
	 *            distance to stretch through (degs)
	 * @param origin
	 *            origin of stretch, probably one end of the track
	 */
	public void stretch(double rngDegs, final WorldLocation origin) {
		// make it always +ve, we'll just overwrite ourselves anyway
		rngDegs = Math.abs(rngDegs);

		// right - we just stretch about the ends, and we use different
		// processing depending on which end is being shifted.
		FixWrapper first = (FixWrapper) this.getData().iterator().next();
		if (first.getLocation().equals(origin)) {
			// right, we're dragging around the last point. Couldn't be easier,
			// just change our speed
		} else {
			// right, we've got to shift the start point to the relevant
			// location,
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

}