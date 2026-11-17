
package ASSET.Models.Decision.Tactical;

import ASSET.NetworkParticipant;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public class Intercept extends CoreDecision implements java.io.Serializable {

	// ////////////////////////////////////////////////////////////////////
	// Member Variables
	// ////////////////////////////////////////////////////////////////////

	static public class InterceptInfo extends EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public InterceptInfo(final Intercept data) {
			super(data, data.getName(), "Intercept");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this trail model"),
						prop("TargetType", "the type of vehicle to intercept"),
						prop("SpeedChangeAllowed", "whether to allow speed change to intercept target"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * utility function calculating intercept course
	 *
	 * @param ownship my status
	 * @param target  his status
	 * @param time    the current time
	 * @return demanded status object containing desired course & speed
	 */
	public static SimpleDemandedStatus calculateInterceptCourseFor(final Status ownship, final Status target,
			final long time) {

		SimpleDemandedStatus demStat = null;

		// throw in a particularly coarse can/can't test
		if (ownship.getSpeed().getValueIn(WorldSpeed.M_sec) > target.getSpeed().getValueIn(WorldSpeed.M_sec)) {
			// yes, we're travelling more quickly than him.
			// yes, a result is roughly possible
			final double oSpd = ownship.getSpeed().getValueIn(WorldSpeed.M_sec);
			final double tSpd = target.getSpeed().getValueIn(WorldSpeed.M_sec);
			final double tCourse = target.getCourse();
			final WorldVector separation = ownship.getLocation().subtract(target.getLocation());
			// double separation_metres =
			// MWC.Algorithms.Conversions.Degs2m(separation.getRange());
			double brg = MWC.Algorithms.Conversions.Rads2Degs(separation.getBearing());
			if (brg < 0)
				brg += 360;
			if (brg > 360)
				brg -= 360;

			double p1 = Math.toRadians(tCourse - brg);
			while (p1 > Math.PI)
				p1 -= 2 * Math.PI;

			final double p2 = tSpd * Math.sin(p1) / oSpd;
			final double p3 = Math.asin(p2);
			final double p4 = -Math.toDegrees(p3) + brg + 180;
			final double res = p4 % 360;

			demStat = new SimpleDemandedStatus(time, ownship);
			demStat.setCourse(res);
		}

		return demStat;
	}

	/**
	 * a local copy of our editable object
	 */
	protected EditorType _myEditor = null;

	// ////////////////////////////////////////////////////////////////////
	// Constructor
	// ////////////////////////////////////////////////////////////////////

	/**
	 * the target we are trying to intercept
	 */
	private TargetType _targetType;

	// ////////////////////////////////////////////////
	// the actual intercept calculation
	// ////////////////////////////////////////////////

	/**
	 * the current target we are tracking
	 */
	private Integer _currentTarget;

	// ////////////////////////////////////////////////////////////////////
	// Member methods
	// ////////////////////////////////////////////////////////////////////

	/**
	 * whether we let ourselves change speed to intercept the target
	 */
	private boolean _speedChangeAllowed;

	public Intercept(final TargetType targetType, final boolean speedChangeAllowed) {
		super("Intercept");

		_targetType = targetType;
		_speedChangeAllowed = speedChangeAllowed;
	}

	@Override
	public DemandedStatus decide(final Status status, final ASSET.Models.Movement.MovementCharacteristics chars,
			final DemandedStatus demStatus, final ASSET.Models.Detection.DetectionList detections,
			final ASSET.Scenario.ScenarioActivityMonitor monitor, final long time) {
		SimpleDemandedStatus res = null;
		String activity = null;

		DetectionEvent targetDetection = null;

		// right, see if we are in contact with anything
		if (detections.size() > 0) {
			final DetectionList validDets = detections.getDetectionsOf(_currentTarget, _targetType);

			if (validDets != null) {
				// remember what we're heading for
				targetDetection = validDets.getMostRecentDetection();

				// and remember which target this is
				_currentTarget = new Integer(targetDetection.getTarget());
			}
		}

		// have we anything to head for?
		if (targetDetection != null) {
			// yes, calc the course to it
			final NetworkParticipant pt = monitor.getThisParticipant(targetDetection.getTarget());
			final Status tgtStat = pt.getStatus();
			res = calculateInterceptCourseFor(status, tgtStat, time);

			// did it work?
			if (res == null) {
				// no, do we let ourselves change speed?
				if (_speedChangeAllowed) {
					final Status newStat = new Status(status);
					newStat.setSpeed(chars.getMaxSpeed());

					// and try again
					res = calculateInterceptCourseFor(newStat, tgtStat, time);

					// did this work?
					if (res != null) {
						// yes, just try to increase speed
						res.setSpeed(chars.getMaxSpeed());
					}
				}
			}

			// ok. have we managed to sort it?
			if (res != null) {
				// put in some special processing here. If the demanded course is only
				// a neglible difference from our current course, just continue on the
				// current course
				//
				// in practice it has turned out that we rarely actually get on course
				// because
				// the intercept calculation varies very slightly each time. We just
				// insert this dem course change over-ride to smooth this behaviour.

				// yes. are we changing course?
				final double courseDelta = Math.abs(res.getCourse() - status.getCourse());
				// if (courseDelta > TurnAlgorithm.COURSE_DELTA)
				if (courseDelta > 2) {
					activity = "Switching to new intercept course on target:" + pt.getName();
				} else {
					// error is negligible, continue on current course
					res.setCourse(status.getCourse());
					activity = "Continuing to intercept:" + pt.getName();
				}

				// ok, store it.
				super.setLastActivity(activity);
			}
		}

		// ok, done
		return res;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new InterceptInfo(this);

		return _myEditor;
	}

	public boolean getSpeedChangeAllowed() {
		return _speedChangeAllowed;
	}

	// ////////////////////////////////////////////////
	// getter/setter
	// ////////////////////////////////////////////////

	public TargetType getTargetType() {
		return _targetType;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: Intercept.java,v $
	 * Revision 1.1  2006/08/08 14:21:33  Ian.Mayo
	 * Second import
	 * Revision 1.1  2006/08/07 12:25:42  Ian.Mayo
	 * First versions
	 * Revision 1.10  2005/04/15 14:11:58  Ian.Mayo
	 * Update tests to reflect new scenario step cycle
	 * Revision 1.9  2004/10/20 09:17:31  Ian.Mayo
	 * Correct editable property name
	 * <p/>
	 * Revision 1.8  2004/09/27 14:54:55  Ian.Mayo
	 * Don't calculate reciprocal, correctly bring calc value back in range
	 * <p/>
	 * Revision 1.7  2004/09/23 07:47:33  Ian.Mayo
	 * Re-implement intercept calculation.  Went screwy somehow.
	 * <p/>
	 * Revision 1.6  2004/09/02 15:33:04  Ian.Mayo
	 * More tests, minor tidying.  Assume on course if delta is under 2 degs.  This is more than in turn calc, but necessary because straight section following turn always changes intercept course.
	 * <p/>
	 * Revision 1.5  2004/09/02 13:17:33  Ian.Mayo
	 * Reflect CoreDecision handling the toString method
	 * <p/>
	 * Revision 1.4  2004/09/02 10:41:23  Ian.Mayo
	 * Working implementation, now checking for able to change speed
	 * <p/>
	 * Revision 1.3  2004/09/02 09:57:55  Ian.Mayo
	 * Finish testing, handle when we're nearly on course
	 * <p/>
	 * Revision 1.2  2004/09/02 08:13:58  Ian.Mayo
	 * More implementation, part way through testing
	 * <p/>
	 * Revision 1.1  2004/08/31 15:28:01  Ian.Mayo
	 * Polish off test refactoring, start Intercept behaviour
	 * <p/>
	 * Revision 1.3  2004/08/31 09:36:14  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.2  2004/08/31 07:24:06  Ian.Mayo
	 * Complete test definition
	 * <p/>
	 * Revision 1.1  2004/08/27 15:10:41  Ian.Mayo
	 * (Partially) implement the Avoid behaviour
	 * <p/>
	 * Revision 1.15  2004/08/26 13:22:21  Ian.Mayo
	 * Correct properties description, add property editing
	 * <p/>
	 * Revision 1.14  2004/08/25 11:20:17  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.13  2004/08/20 13:32:18  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.12  2004/08/17 14:21:57  Ian.Mayo
	 * Refactor to introduce parent class capable of storing name & isActive flag
	 * <p/>
	 * Revision 1.11  2004/08/09 15:50:26  Ian.Mayo
	 * Refactor category types into Force, Environment, Type sub-classes
	 * <p/>
	 * Revision 1.10  2004/08/06 12:51:54  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.9  2004/08/06 11:14:16  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.8  2004/05/24 15:46:35  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:51  ian
	 * no message
	 * <p/>
	 * Revision 1.7  2004/02/18 08:47:12  Ian.Mayo
	 * Sync from home
	 * <p/>
	 * Revision 1.5  2003/11/05 09:20:10  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	/**
	 * indicate to this model that its execution has been interrupted by another
	 * (prob higher priority) model
	 *
	 * @param currentStatus
	 */
	@Override
	public void interrupted(final Status currentStatus) {
		// ignore.
	}

	// //////////////////////////////////////////////////////////
	// model support
	// //////////////////////////////////////////////////////////

	/**
	 * reset this decision model
	 */
	@Override
	public void restart() {
		//
	}

	// ////////////////////////////////////////////////
	// property editor support
	// ////////////////////////////////////////////////

	public void setSpeedChangeAllowed(final boolean speedChangeAllowed) {
		this._speedChangeAllowed = speedChangeAllowed;
	}

	public void setTargetType(final TargetType targetType) {
		this._targetType = targetType;
	}

}