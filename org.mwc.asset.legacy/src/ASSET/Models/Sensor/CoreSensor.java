
package ASSET.Models.Sensor;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

import java.beans.MethodDescriptor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.MWCModel;
import ASSET.Models.SensorType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.Status;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;

/**
 * base implementation of a sensor
 */
abstract public class CoreSensor implements ASSET.Models.SensorType, java.io.Serializable, MWCModel {

	// //////////////////////////////////////////////////
	// member bariables
	// //////////////////////////////////////////////////

	// //////////////////////////////////////////////////
	// the editor object
	// //////////////////////////////////////////////////
	public static class BaseSensorInfo extends MWC.GUI.Editable.EditorType {
		/**
		 * @param data the Layers themselves
		 */
		public BaseSensorInfo(final SensorType data) {
			this(data, false);
		}

		/**
		 * @param data the Layers themselves
		 */
		public BaseSensorInfo(final SensorType data, final boolean firesReports) {
			super(data, data.getName(), "Edit", "images/icons/Sensor.gif", firesReports);
		}

		@Override
		public final MethodDescriptor[] getMethodDescriptors() {
			// just add the reset color field first
			final Class<CoreSensor> c = CoreSensor.class;
			MethodDescriptor[] res = null;
			if (_watchMethod != null) {
				final MethodDescriptor[] mds = { method(c, "watchMe", null, "Monitor Sensor"), };
				res = mds;
			}
			return res;
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this optic sensor"),
						prop("Working", "whether this sensor is in use"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	public static interface SensorOperation {
		public void run(SensorType me);
	}

	/**
	 * ************************************************* utility class
	 * *************************************************
	 */
	public static class SensorUtils {
		/**
		 * calculate the sum of the two decibel values
		 */
		static public double powerSum(final double x, final double y) {
			double res = 0;

			// move them to the normal domain
			final double xNorm = Math.pow(10, x / 10d);
			final double yNorm = Math.pow(10, y / 10d);

			// add them
			final double sum = xNorm + yNorm;

			// move back to the log domain
			res = (Math.log(sum) / Math.log(10)) * 10;

			// done
			return res;
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the event fired to show the calculations in a detection
	 */
	final public static String SENSOR_COMPONENT_EVENT = "SensorComponents";

	/**
	 * the event fired to show we have finished a detection cycle
	 */
	final public static String DETECTION_CYCLE_COMPLETE = "CycleComplete";

	/**
	 * externally suppled operation to fire when somebody wants to watch us.
	 */
	protected static SensorOperation _watchMethod;

	/**
	 * ok, we keep track of new detections in each cycle. use a static list so that
	 * we don't forget them
	 */
	private static DetectionList _newDetections;

	public static void setWatchMethod(final SensorOperation method) {
		_watchMethod = method;
	}

	/**
	 * our editor
	 */
	protected MWC.GUI.Editable.EditorType _myEditor = null;

	/**
	 * the id of this sensor
	 */
	private int _myId;

	/**
	 * our property change support
	 */
	protected java.beans.PropertyChangeSupport _pSupport;

	/**
	 * whether this sensor can produce a range value
	 *
	 */
	private boolean _canProduceRange = true;

	/**
	 * (optional) offset for sensor (-ve = behind)
	 *
	 */
	protected WorldDistance _sensorOffset;

	/**
	 * whether this sensor is active
	 */
	private boolean _isWorking = true;

	/**
	 * the name of this sensor
	 */
	private String _myName;

	/**
	 * time between detection opportunities (millis)
	 */
	private long _TBDO = -1;

	/**
	 * the time of the last scan (which we remember so that we can correctly observe
	 * the _TBDO value
	 */
	private long _lastScan = -1;

	/**
	 * our history of detections
	 */
	private final DetectionList _pastDetections = new DetectionList();

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	/**
	 * people listening out for my detections
	 */
	private Vector<ParticipantDetectedListener> _participantDetectedListeners;

	/*****************************************************************************
	 * scenario data provider methods
	 ****************************************************************************/

	/**
	 * name to use as a fallback
	 */
	private final String _defaultName;

	private final boolean _wipeDetectionsEachStep = true;

	TrackWrapper _backTrack = null;

	/**
	 * ************************************************* constructor
	 * *************************************************
	 */
	public CoreSensor(final int id, final long TBDO, final String defaultName) {
		_myId = id;
		_TBDO = TBDO;

		_defaultName = defaultName;

		// check our new detections listener is there
		if (_newDetections == null)
			_newDetections = new DetectionList();
	}

	/**
	 * somebody wants to stop listening to us
	 *
	 * @param listener
	 */
	@Override
	public void addParticipantDetectedListener(final ParticipantDetectedListener listener) {
		if (_participantDetectedListeners == null)
			_participantDetectedListeners = new Vector<ParticipantDetectedListener>(1, 2);

		_participantDetectedListeners.add(listener);
	}

	@Override
	public void addSensorCalculationListener(final java.beans.PropertyChangeListener listener) {
		if (_pSupport == null)
			_pSupport = new java.beans.PropertyChangeSupport(this);

		_pSupport.addPropertyChangeListener(SENSOR_COMPONENT_EVENT, listener);
		_pSupport.addPropertyChangeListener(DETECTION_CYCLE_COMPLETE, listener);
	}

	// allow an 'overview' test, just to check if it is worth all of the above
	// processing
	abstract protected boolean canDetectThisType(NetworkParticipant ownship, ASSET.ParticipantType other,
			EnvironmentType env);

	/**
	 * convenience method to indicate if this type of sensor is capable of
	 * identifying a target
	 *
	 * @return yes/no
	 */
	abstract public boolean canIdentifyTarget();

	/**
	 * convenience method to indicate if this sensor produces a bearing from the
	 * sensor to the target. Very few sensors do not produce bearing.
	 *
	 * @return yes/no
	 */
	protected boolean canProduceBearing() {
		return true;
	}

	/**
	 * whether this sensor produces range in its output
	 *
	 * @return yes/no
	 */
	public boolean canProduceRange() {
		return _canProduceRange;
	}

	private FixWrapper createFix(final Status status) {
		final HiResDate time = new HiResDate(status.getTime());
		final WorldLocation location = status.getLocation();
		final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(status.getCourse());
		final double speedYps = status.getSpeed().getValueIn(WorldSpeed.ft_sec) / 3d;
		final Fix newF = new Fix(time, location, courseRads, speedYps);
		final FixWrapper newW = new FixWrapper(newF);
		return newW;
	}

	/**
	 * do the detections for this step
	 *
	 * @param environment
	 * @param existingDetections
	 * @param ownship
	 * @param scenario
	 * @param time
	 */
	@Override
	final public void detects(final ASSET.Models.Environment.EnvironmentType environment,
			final DetectionList existingDetections, final ASSET.ParticipantType ownship,
			final ASSET.ScenarioType scenario, final long time) {

		// ok, see if the current time fits in between our _TBDO
		boolean canRun = false;

		// see if we are due to run
		canRun = isDueToScan(time);

		// Note: for some processing we wish to remove existing detections
		// whether the sensor runs or not.
		//
		// removeMyOldDetections(existingDetections);

		// ditch any old detections, since we're about to supercede them
		if (_wipeDetectionsEachStep)
			removeMyOldDetections(existingDetections);

		// can we?
		if (canRun && isWorking()) {

			// just check that our list is empty
			_newDetections.removeAllElements();

			// step through the participants in the scenario
			final Collection<ParticipantType> parts = scenario.getListOfVisibleParticipants();

			for (final Iterator<ParticipantType> iterator = parts.iterator(); iterator.hasNext();) {
				final ParticipantType target = iterator.next();

				// is this target alive in the scenario yet?
				if (target.isAlive()) {
					// yes
					// is this us?
					if (target != ownship) {
						// can we detect it?
						final boolean canDetectHim = canDetectThisType(ownship, target, environment);

						if (canDetectHim) {

							final DetectionEvent thisD = detectThis(environment, ownship, target, time, scenario);

							if (thisD != null) {
								// add to our current list of detections
								existingDetections.add(thisD);

								// and add to our historic list of detections
								_pastDetections.add(thisD);

								// and remember for our new list
								_newDetections.add(thisD);
							} // whether we made a detection
						} // whether we should even try to detect a participant of this
							// type
					} // of this is not us
				} // if it's alive
			} // if index was valid

			// ok, we've performed all of our detections
			if (_pSupport != null) {
				final Long theTime = new Long(time);
				_pSupport.firePropertyChange(DETECTION_CYCLE_COMPLETE, null, theTime);
			}

			// and have we got anything to report?
			if (_newDetections.size() > 0) {
				// yup, fire them off
				fireTheseDetections(_newDetections);

				// and clear the list
				_newDetections.removeAllElements();
			}
		}
	}

	// what is the detection strength for this target?
	abstract protected DetectionEvent detectThis(final ASSET.Models.Environment.EnvironmentType environment,
			final ASSET.ParticipantType host, final ASSET.ParticipantType target, final long time,
			ScenarioType scenario);

	/**
	 * fire off our detections to anybody who is interested
	 *
	 * @param thisD
	 */
	private void fireTheseDetections(final DetectionList thisD) {
		if (_participantDetectedListeners != null) {
			for (int i = 0; i < _participantDetectedListeners.size(); i++) {
				final ParticipantDetectedListener listener = _participantDetectedListeners.elementAt(i);
				listener.newDetections(thisD);
			}
		}
	}

	/**
	 * find the list of all detections for this participant since the start of the
	 * scenario
	 */
	@Override
	public DetectionList getAllDetections() {
		return _pastDetections;
	}

	/**
	 * get the target course (Degs)
	 *
	 * @param participant
	 * @return
	 */
	protected final double getHostCourseFor(final ParticipantType participant) {
		final double res;
		final WorldDistance offset = getSensorOffset();
		if (offset != null && _backTrack != null) {
			final ArrayLength len = new WorldDistance.ArrayLength(offset);
			final long time = participant.getStatus().getTime();
			final FixWrapper trackPoint = _backTrack.getBacktraceTo(new HiResDate(time), len, true);
			if (trackPoint != null) {
				res = trackPoint.getCourseDegs();
			} else {
				res = 0d;
			}

			// ok, we need to maintain a back-track of locations for this participant, so
			// we can do worm in the hole

			// final double courseRads =
			// MWC.Algorithms.Conversions.Degs2Rads(participant.getStatus().getCourse());
			// res = sensorLoc.add(new WorldVector(courseRads, offset, new WorldDistance(0,
			// WorldDistance.FT)));
		} else {
			res = participant.getStatus().getCourse();
		}

		return res;
	}

	/**
	 * get the target location
	 *
	 * @param participant
	 * @return
	 */
	protected final WorldLocation getHostLocationFor(final ParticipantType participant) {
		final WorldLocation sensorLoc = participant.getStatus().getLocation();
		final WorldLocation res;
		final WorldDistance offset = getSensorOffset();
		if (offset != null) {
			if (_backTrack == null) {
				_backTrack = new TrackWrapper();
			}

			_backTrack.addFix(createFix(participant.getStatus()));

			final ArrayLength len = new WorldDistance.ArrayLength(offset);
			final long time = participant.getStatus().getTime();
			final FixWrapper trackPoint = _backTrack.getBacktraceTo(new HiResDate(time), len, true);
			if (trackPoint != null) {
				res = trackPoint.getLocation();
			} else {
				res = null;
			}

			// ok, we need to maintain a back-track of locations for this participant, so
			// we can do worm in the hole

			// final double courseRads =
			// MWC.Algorithms.Conversions.Degs2Rads(participant.getStatus().getCourse());
			// res = sensorLoc.add(new WorldVector(courseRads, offset, new WorldDistance(0,
			// WorldDistance.FT)));
		} else {
			res = sensorLoc;
		}

		return res;
	}

	@Override
	final public int getId() {
		return _myId;
	}

	/**
	 * get the target location
	 *
	 * @param participant
	 * @return
	 */
	protected WorldLocation getLocationFor(final ASSET.ParticipantType participant) {
		return participant.getStatus().getLocation();
	}

	@Override
	final public String getName() {
		final String res;

		// do we have specific name?
		if (_myName != null)
			res = _myName;
		else {
			// no, use default name for this type of sensor
			res = _defaultName;
		}

		return res;
	}

	public WorldDistance getSensorOffset() {
		return _sensorOffset;
	}

	/**
	 * get how long between occasions where we are able to produce detection
	 *
	 * @return millis
	 */
	final public long getTimeBetweenDetectionOpportunities() {
		return _TBDO;
	}

	/**
	 * handle the demanded change in sensor lineup
	 *
	 * @param status
	 */
	@Override
	public void inform(final DemandedSensorStatus status) {
		this.setWorking(status.getSwitchOn());
	}

	/**
	 * decide if it's time for us to run agin
	 *
	 * @param time the current time
	 * @return yes/no
	 */
	private boolean isDueToScan(final long time) {
		boolean canRun = false;

		if ((_lastScan == -1) || (_TBDO <= 0)) {
			// hey, we haven't run yet. Get going.
			canRun = true;
		} else {
			// ok, find the elapsed time
			final long elapsed = time - _lastScan;

			// are we due for another?
			if (elapsed >= _TBDO) {
				canRun = true;
			}
		}

		// ok, are we going to go for it?
		if (canRun) {
			// remember the current time
			_lastScan = time;
		}

		return canRun;
	}

	/**
	 * whether this sensor is currently active
	 *
	 * @return
	 */
	@Override
	final public boolean isWorking() {
		return _isWorking;
	}

	/**
	 * calculate relative bearing of specified bearing from this vessel course
	 */
	protected double relativeBearing(final double course, final double bearing) {
		return bearing - course;
	}

	/**
	 * remove any of our past detections from the list. Detections stay valid
	 * between TBDOs, to give some degree of persistnce
	 *
	 * @param existingDetections the old list of detections
	 */
	private void removeMyOldDetections(final DetectionList existingDetections) {
		// are there any at all?
		if (existingDetections != null) {

			// keep track of the detections to remove
			final Vector<DetectionEvent> ditchThese = new Vector<DetectionEvent>(0, 1);

			// ditch any old detections we produced
			final int len = existingDetections.size();
			for (int i = 0; i < len; i++) {
				final DetectionEvent de = existingDetections.elementAt(i);
				if (de.getSensor() == this.getId()) {
					// get rid of this one
					ditchThese.add(de);
				}
			}

			// ok, were there any?
			if (ditchThese.size() > 0) {
				// yup, get on with it
				existingDetections.removeAll(ditchThese);
			}
		}
	}

	/**
	 * somebody wants to start listening to us
	 *
	 * @param listener
	 */
	@Override
	public void removeParticipantDetectedListener(final ParticipantDetectedListener listener) {
		_participantDetectedListeners.remove(listener);
	}

	@Override
	public void removeSensorCalculationListener(final java.beans.PropertyChangeListener listener) {
		_pSupport.removePropertyChangeListener(SENSOR_COMPONENT_EVENT, listener);
		_pSupport.removePropertyChangeListener(DETECTION_CYCLE_COMPLETE, listener);
	}

	/**
	 * restart this sensors
	 */
	@Override
	public void restart() {
		// clear out the past detections
		_pastDetections.removeAllElements();

		if (_backTrack != null) {
			_backTrack.clearPositions();
			_backTrack = null;
		}
	}

	public void setCanProduceRange(final boolean canProduceRange) {
		_canProduceRange = canProduceRange;
	}

	final public void setId(final int val) {
		_myId = val;
	}

	@Override
	final public void setName(final String name) {
		_myName = name;
	}

	public void setSensorOffset(final WorldDistance sensorOffset) {
		this._sensorOffset = sensorOffset;
	}

	/**
	 * set how long between occasions where we are able to produce detection
	 *
	 * @param millis
	 */
	final public void setTimeBetweenDetectionOpportunities(final long millis) {
		_TBDO = millis;
	}

	/**
	 * whether this sensor is currently working
	 *
	 * @param isWorking yes/no
	 */
	@Override
	final public void setWorking(final boolean isWorking) {
		// is the new value actually new?
		if (isWorking != _isWorking) {
			// ok, change our state
			_isWorking = isWorking;

			// and update the editor
			if (_myEditor != null) {
				_myEditor.fireChanged(this, "Working", new Boolean(!isWorking), new Boolean(isWorking));
			}
		}
	}

	@Override
	public String toString() {
		return getName();
	}

	// let the user watch this sensor
	public void watchMe() {
		if (_watchMethod != null)
			_watchMethod.run(this);
	}
}