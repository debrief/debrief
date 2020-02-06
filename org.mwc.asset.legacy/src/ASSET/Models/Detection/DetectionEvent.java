
package ASSET.Models.Detection;

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

import java.io.Serializable;

import ASSET.NetworkParticipant;
import ASSET.Models.SensorType;
import ASSET.Participants.Category;
import MWC.GUI.Properties.AbstractPropertyEditor;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class DetectionEvent implements java.util.Comparator<DetectionEvent>, Serializable {

	public static class DetectionStatePropertyEditor extends AbstractPropertyEditor {

		////////////////////////////////////////////////////
		// member objects
		////////////////////////////////////////////////////
		private final String _stringTags[] = { UNDETECTED_STR, DETECTED_STR, CLASSIFIED_STR, IDENTIFIED_STR, };

		////////////////////////////////////////////////////
		// member methods
		////////////////////////////////////////////////////
		@Override
		public String[] getTags() {
			return _stringTags;
		}

	}

	public static final String IDENTIFIED_STR = "Identified";
	public static final String CLASSIFIED_STR = "Classified";
	public static final String DETECTED_STR = "Detected";

	public static final String UNDETECTED_STR = "Undetected";

	////////////////////////////////////////////////////////////
	// set of (ascending) detection states
	////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * we're not in contact with this participant (and there won't be a detection -
	 * but lets keep it in here anyway)
	 */
	public static final int UNDETECTED = 0;

	/**
	 * we've only just about detected this participant, but we've no idea what it is
	 */
	public static final int DETECTED = 1;

	/**
	 * we know what type of target it is - the category
	 */
	public static final int CLASSIFIED = 2;

	/**
	 * we know exactly what it is!
	 */
	public static final int IDENTIFIED = 3;

	/**
	 * the current detection state for this target
	 */
	private int _detectionState;

	/**
	 * store the sensor location for this detection. This has been introduced mainly
	 * to support remote sensors. A helo could hear be informed of a bearing
	 * detection from a sonar buoy but needs to know the sonar buoy location find
	 * out where the bearing line spans from
	 */
	private WorldLocation _sensorLocation;

	/**
	 * time of this detection
	 */
	private long _time;

	/**
	 * sensor which made detection
	 */
	private int _sensorId;

	/**
	 * range of detection (yds)
	 */
	private WorldDistance _range;

	/**
	 * the estimated range, where the actual range isn't available
	 */
	private WorldDistance _estimatedRange;

	/**
	 * relative bearing to target (degs)
	 */
	protected Float _relBearing;

	/**
	 * bearing of detection (degs)
	 */
	private Float _bearing;

	/**
	 * strength of detection (%)
	 */
	private Float _strength;

	/**
	 * observed frequency
	 *
	 */
	private Float _freq = null;

	/**
	 * the type of this target
	 */
	private Category _target_type;

	/**
	 * the course of the target (degs)
	 */
	private Float _course;

	/**
	 * the speed of the target (knots)
	 */
	private Float _speed;

	/**
	 * the host vessel for this detection
	 */
	private int _hostId;

	/**
	 * a textual message desribing this detection
	 */
	private String _myMessage;

	/**
	 * the id of the target
	 */
	private Integer _targetId;

	////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////

	/**
	 * store the ambigous bearing
	 *
	 */
	private Float _ambigBrg = null;;

	/**
	 * no-op constructor created in support of Kryo networking
	 *
	 */
	protected DetectionEvent() {
	}

	/**
	 * Constructor
	 *
	 * @param time     the time this detection was made
	 * @param sensor   sensor which made detection
	 * @param range    range in yards
	 * @param bearing  bearing in degrees
	 * @param strength strength 1..100
	 */
	public DetectionEvent(final long time, final int host, final WorldLocation sensorLocation, final SensorType sensor,
			final WorldDistance range, final WorldDistance estimatedRange, final Float bearing, final Float relBearing,
			final Float strength, final Category target_type, final Float speedKts, final Float course,
			final NetworkParticipant target) {

		_time = time;
		_range = range;
		_estimatedRange = estimatedRange;
		_bearing = bearing;
		_relBearing = relBearing;
		_strength = strength;
		_target_type = target_type;
		_course = course;
		_speed = speedKts;
		_sensorId = sensor.getId();
		_hostId = host;
		_sensorLocation = sensorLocation;

		if (target != null)
			_targetId = new Integer(target.getId());
		else
			_targetId = null;

		// write the message
		if (target == null)
			throw new RuntimeException("Failed to specify target");

		_myMessage = target.getName() + " held on " + sensor.getName();
	}

	/**
	 * Constructor
	 *
	 * @param time     the time this detection was made
	 * @param sensor   sensor which made detection
	 * @param range    range in yards
	 * @param bearing  bearing in degrees
	 * @param strength strength 1..100
	 */
	public DetectionEvent(final long time, final int host, final WorldLocation sensorLocation, final SensorType sensor,
			final WorldDistance range, final WorldDistance estimatedRange, final Float bearing, final Float relBearing,
			final Float strength, final Category target_type, final WorldSpeed speed, final Float course,
			final NetworkParticipant target, final int detectionState) {

		this(time, host, sensorLocation, sensor, range, estimatedRange, bearing, relBearing, strength, target_type,
				new Float(speed.getValueIn(WorldSpeed.Kts)), course, target);

		_detectionState = detectionState;

	}

	/**
	 * ****************************************************************** support
	 * for comparator interface
	 * ******************************************************************
	 */

	@Override
	public int compare(final DetectionEvent d1, final DetectionEvent d2) {
		int res = 0;

		if (d1.getTime() < d2.getTime())
			res = -1;
		else if (d1.getTime() > d2.getTime())
			res = 1;

		return res;
	}

	@Override
	public boolean equals(final Object obj) {
		final DetectionEvent d2 = (DetectionEvent) obj;

		return (this == d2);

	}

	public double getAmbiguousBearing() {
		return _ambigBrg;
	}

	/**
	 * get the bearing (degs)
	 */
	public Float getBearing() {
		return _bearing;
	}

	/**
	 * get the target course (Degs)
	 */
	public Float getCourse() {
		return _course;
	}

	/**
	 * get the detection state for this detection
	 *
	 * @see DetectionEvent.UNDETECTED
	 */
	public int getDetectionState() {
		return _detectionState;
	}

	/**
	 * get the estimated range (WorldDistance) for when actual range isn't known
	 */
	public WorldDistance getEstimatedRange() {
		WorldDistance res;
		if (_estimatedRange == null)
			res = _range;
		else
			res = _estimatedRange;

		return res;
	}

	public Float getFreq() {
		return _freq;
	}

	public int getHost() {
		return _hostId;
	}

	/**
	 * get the range (yds)
	 */
	public WorldDistance getRange() {
		return _range;
	}

	public int getSensor() {
		return _sensorId;
	}

	public WorldLocation getSensorLocation() {
		return _sensorLocation;
	}

	/**
	 * get the target speed (kts)
	 */
	public Float getSpeed() {
		return _speed;
	}

	public Float getStrength() {
		return _strength;
	}

	public int getTarget() {
		return _targetId.intValue();
	}

	public Category getTargetType() {
		return _target_type;
	}

	public long getTime() {
		return _time;
	}

	public boolean isAmbiguous() {
		return _ambigBrg != null;
	}

	public void setAmbiguousBearing(final Float ambiBrg) {
		_ambigBrg = ambiBrg;
	}

	public void setBearing(final Float brg) {
		_bearing = brg;
	}

	/**
	 * set the detection state for this detection
	 *
	 * @see DetectionEvent.UNDETECTED
	 */
	public void setDetectionState(final int state) {
		_detectionState = state;
	}

	public void setFreq(final Float freq) {
		_freq = freq;
	}

	/**
	 * set the id of the target we're looking at
	 *
	 * @param val the new id
	 */
	public void setTarget(final int val) {
		_targetId = new Integer(val);
	}

	@Override
	public String toString() {
		return _myMessage;
	}
}