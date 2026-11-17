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

package ASSET.Models.Decision.Responses;

import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

/**
 * class which directs a participant to adopt a particular course, speed, or
 * depth. These are optionally assigned relative to the detected vessel/bearing
 *
 * @see ASSET.Util.XML.Decisions.Responses.ManoeuvreToCourseHandler
 * @see ManoeuvreToLocation
 */
public class ManoeuvreToCourse extends Response.CoreResponse {
	////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////

	/**
	 * ************************************************* editor support
	 * *************************************************
	 */

	static public class GotoLocationInfo extends Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public GotoLocationInfo(final ManoeuvreToCourse data) {
			super(data, data.getName(), "Location Response");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this response"),
						prop("Course", "the course to turn to (degs)"), prop("Speed", "the speed to travel at (kts)"),
						// prop("Depth", "the depth to travel at (m)"),
						prop("RelativeSpeed", "whether to change course relative to detection bearing"),
						prop("RelativeCourse", "whether to change speed relative to target speed"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * the speed to travel at (kts)
	 */
	private WorldSpeed _mySpeed;

	/**
	 * whether the speed change is relative
	 */
	private boolean _relativeSpeed;

	/**
	 * the course to steer to (degs)
	 */
	private Float _myCourse;

	////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////

	/**
	 * whether the course change is relative
	 */
	private boolean _relativeCourse;

	////////////////////////////////////////////////////
	// response object
	////////////////////////////////////////////////////

	/**
	 * the height to change to (m), always absolute
	 */
	private WorldDistance _myHeight;

	/**
	 * @param speed          the speed to adopt (or null) (kts)
	 * @param relativeSpeed  whether to make a speed change relative to the target
	 * @param course         the course to switch to, or null (degs)
	 * @param relativeCourse whether to make a relative course change (relative to
	 *                       the brg to target)
	 * @param height         the height to switch to, or null (m)
	 */
	public ManoeuvreToCourse(final WorldSpeed speed, final boolean relativeSpeed, final Float course,
			final boolean relativeCourse, final WorldDistance height) {
		this._mySpeed = speed;
		this._relativeSpeed = relativeSpeed;
		this._myCourse = course;
		this._relativeCourse = relativeCourse;
		this._myHeight = height;
	}

	/**
	 * produce the required response
	 *
	 * @param conditionResult the result from the condition test
	 * @param status          the current status
	 * @param detections      the current set of detections
	 * @param monitor         the object monitoring us(for add/remove participants,
	 *                        detonations, etc)
	 * @param time            the current time
	 * @return
	 * @see ASSET.Models.Decision.Conditions.Condition
	 */

	@Override
	public DemandedStatus direct(final Object conditionResult, final Status status, final DemandedStatus demStat,
			final DetectionList detections, final ScenarioActivityMonitor monitor, final long time) {
		final SimpleDemandedStatus ds = new SimpleDemandedStatus(time, status);

		// check if we have a valid detection
		if (conditionResult instanceof ASSET.Models.Detection.DetectionEvent) {
			final ASSET.Models.Detection.DetectionEvent de = (ASSET.Models.Detection.DetectionEvent) conditionResult;

			// do we have a course value?
			if (_myCourse != null) {
				// yes, we have a detection. see if we have any relative parameters
				if (_relativeCourse) {
					// do we know brg to target?
					if (de.getBearing() != null) {
						final double theBearing = de.getBearing().doubleValue();

						// steer a course relative to the bearing to the contact
						ds.setCourse(theBearing + _myCourse.floatValue());
					} else {
						// no, we don't know his course - don't make a course change
					}
				} else {
					// no we are not trying to set a relative course, set absolute
					ds.setCourse(_myCourse.floatValue());
				}
			}

			// do we have a Speed value?
			if (_mySpeed != null) {
				// yes, we have a detection. see if we have any relative parameters
				if (_relativeSpeed) {
					// do we know his Speed
					if (de.getSpeed() != null) {
						// steer a relative Speed
						ds.setSpeed(de.getSpeed().floatValue() + _mySpeed.getValueIn(WorldSpeed.M_sec));
					} else {
						// no, we don't know his Speed - don't make a Speed change
					}
				} else {
					// no we are not trying to set a relative Speed, set absolute
					ds.setSpeed(_mySpeed.getValueIn(WorldSpeed.M_sec));
				}
			}
		} else {
			// we don't have a detection, if there are any absolute changes, make them so
			if (!_relativeCourse) {
				if (_myCourse != null)
					ds.setCourse(_myCourse.floatValue());
			}

			if (!_relativeSpeed) {
				if (_mySpeed != null)
					ds.setSpeed(_mySpeed.getValueIn(WorldSpeed.M_sec));
			}
		}

		// now handle the depth (which can't be relative

		// do we have a depth value?
		if (_myHeight != null) {
			// no we are not trying to set a relative Depth, set absolute
			ds.setHeight(_myHeight.getValueIn(WorldDistance.METRES));
		}

		return ds;
	}

	/**
	 * get the description of what we're doing
	 */
	@Override
	public String getActivity() {
		return "Move to course";
	}

	public Float getCourse() {
		return _myCourse;
	}

	public WorldDistance getHeight() {
		return _myHeight;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new GotoLocationInfo(this);

		return _myEditor;
	}

	public WorldSpeed getSpeed() {
		return _mySpeed;
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	public boolean isRelativeCourse() {
		return _relativeCourse;
	}

	public boolean isRelativeSpeed() {
		return _relativeSpeed;
	}

	@Override
	public void restart() {
		// don't bother, we don't react to this
	}

	public void setCourse(final Float course) {
		this._myCourse = course;
	}

	public void setHeight(final WorldDistance height) {
		this._myHeight = height;
	}

	public void setRelativeCourse(final boolean RelativeCourse) {
		this._relativeCourse = RelativeCourse;
	}

	public void setRelativeSpeed(final boolean relativeSpeed) {
		this._relativeSpeed = relativeSpeed;
	}

	public void setSpeed(final WorldSpeed spd_kts) {
		this._mySpeed = spd_kts;
	}
}
