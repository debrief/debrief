
package ASSET.Models.Movement;

import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

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

/**
 * a set of characteristics describing the movement of a particular type of
 * platform
 */

public abstract class MovementCharacteristics implements MWC.GUI.Editable, java.io.Serializable {

	//////////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// testing
	//////////////////////////////////////////////////
	public static class MoveCharsTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return MovementCharacteristics.getSampleChars();
		}
	}

	static public class MovementInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public MovementInfo(final MovementCharacteristics data) {
			super(data, data.getName(), "Movement", "images/icons/MoveChars.gif");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						prop("AccelRate", "the rate of acceleration for this vessel (kts/sec)"),
						prop("FuelUsageRate", "the rate of fuel usage for this vessel (%/kt/sec)"),
						prop("MaxHeight", "the maximum Height which this vessel travels to (m)"),
						prop("MaxSpeed", "the rate of acceleration for this vessel (kts/sec)"),
						prop("Name", "the name of this vessel"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * distance representing the sea surface = for skimmers
	 */
	private static final WorldDistance SURFACE_HEIGHT = new WorldDistance(0, WorldDistance.METRES);

	/**
	 * speed representing depth change rate
	 */
	private static final WorldSpeed SURFACE_CHANGE = new WorldSpeed(0, WorldSpeed.M_sec);

	public static MovementCharacteristics getSampleChars() {
		return new MovementCharacteristics("the moves", new WorldAcceleration(1, WorldAcceleration.Kts_sec),
				new WorldAcceleration(1, WorldAcceleration.Kts_sec), 0.0000001, new WorldSpeed(30, WorldSpeed.Kts),
				new WorldSpeed(1, WorldSpeed.Kts)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * get the turning circle diameter (m) at this speed (in m/sec)
			 */
			@Override
			public double getTurningCircleDiameter(final double m_sec) {
				return 12;
			}
		};
	}

	/**
	 * maximum speed (m/sec)
	 */
	private WorldSpeed _maxSpeed = null;

	/**
	 * minimum speed (m/sec)
	 */
	private WorldSpeed _minSpeed = null;

	/**
	 * acceleration rate (m/sec/sec)
	 */
	private WorldAcceleration _accelRate = null;

	/**
	 * deceleration rate (m/sec/sec)
	 */
	private WorldAcceleration _decelRate = null;

	//////////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////////

	/**
	 * name of this characteristic type
	 */
	private String _myName = "unset";

	/**
	 * how much fuel we use (/kt/sec)
	 */
	private double _fuel_usage_rate = 0.00;

	/**
	 * our editor
	 */
	private MWC.GUI.Editable.EditorType _myEditor = null;

	//////////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////////

	public MovementCharacteristics() {
		this("default");
	}

	public MovementCharacteristics(final String name) {
		_myName = name;
	}

	public MovementCharacteristics(final String myName, final WorldAcceleration accelRate,
			final WorldAcceleration decelRate, final double fuel_usage_rate, final WorldSpeed maxSpeed,
			final WorldSpeed minSpeed) {
		this._accelRate = accelRate;
		this._decelRate = decelRate;
		this._fuel_usage_rate = fuel_usage_rate;
		this._maxSpeed = maxSpeed;
		this._minSpeed = minSpeed;
		this._myName = myName;
	}

	/**
	 * calculate the turn rate at this speed/turning circle
	 *
	 * @param curSpeed_m_sec
	 * @return turn rate (degs/sec)
	 */
	public double calculateTurnRate(final double curSpeed_m_sec) {
		final double turnRadius = getTurningCircleDiameter(curSpeed_m_sec) / 2;
		final double res = MWC.Algorithms.Conversions.Rads2Degs(curSpeed_m_sec / turnRadius);
		return res;
	}

	/**
	 * calculate how long it takes to move to make the specified course change
	 *
	 * @param mean_speed    the mean speed through the turn (m/sec)
	 * @param course_change the change in course required (degs)
	 * @return the time taken (seconds)
	 */
	public double calculateTurnTime(final double mean_speed, final double course_change) {
		double turn_time;
		final double course_change_rads = MWC.Algorithms.Conversions.Degs2Rads(course_change);

		final double turn_rate_rads = MWC.Algorithms.Conversions.Degs2Rads(calculateTurnRate(mean_speed));

		// calculate the turn time
		if (turn_rate_rads == 0)
			turn_time = 0;
		else
			turn_time = Math.abs(course_change_rads) / turn_rate_rads;
		return turn_time;
	}

	/**
	 * return the rate of acceleration (m/sec/sec)
	 */
	public WorldAcceleration getAccelRate() {
		return _accelRate;
	}

	/**
	 * get the climb rate
	 *
	 * @return climb rate (m/sec/sec)
	 */
	public WorldSpeed getClimbRate() {
		return SURFACE_CHANGE;
	}

	/**
	 * return the rate of deceleration (m/sec/sec)
	 */
	public WorldAcceleration getDecelRate() {
		return _decelRate;
	}

	/**
	 * get the dive rate
	 *
	 * @return dive rate (m/sec/sec)
	 */
	public WorldSpeed getDiveRate() {
		return SURFACE_CHANGE;
	}

	/**
	 * get the rate at which the participant uses fuel (per knot per second)
	 */
	public double getFuelUsageRate() {
		return _fuel_usage_rate;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new MovementInfo(this);

		return _myEditor;
	}

	/**
	 * get the min Height at which this platform operates
	 *
	 * @return max Height(m)
	 */
	public WorldDistance getMaxHeight() {
		return SURFACE_HEIGHT;
	}

	/**
	 * get the maximum speed for this participant (m/sec)
	 *
	 * @return max speed (m/sec)
	 */
	public WorldSpeed getMaxSpeed() {
		return _maxSpeed;
	}

	//////////////////////////////////////////////////
	// Height related accessors
	//////////////////////////////////////////////////
	/**
	 * get the min Height at which this platform operates
	 *
	 * @return min Height(m)
	 */
	public WorldDistance getMinHeight() {
		return SURFACE_HEIGHT;
	}

	/**
	 * get the maximum speed for this participant (m/sec)
	 *
	 * @return max speed (m/sec)
	 */
	public WorldSpeed getMinSpeed() {
		return _minSpeed;
	}

	@Override
	public String getName() {
		return _myName;
	}

	/**
	 * get the turning circle diameter (m) at this speed (in m/sec)
	 */
	abstract public double getTurningCircleDiameter(double m_sec);

	//////////////////////////////////////////////////////////////////////
	// editable data
	//////////////////////////////////////////////////////////////////////
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
	 * set the acceleration rate of this vehicle (in m/sec/second)
	 */

	public void setAccelRate(final WorldAcceleration m_sec_sec) {
		_accelRate = m_sec_sec;
	}

	/**
	 * get the climb rate
	 *
	 * @param val climb rate (m/sec/sec)
	 */
	public void setClimbRate(final double val) {
	}

	/**
	 * set the deceleration rate of this vehicle (in m_sec/second)
	 */
	public void setDecelRate(final WorldAcceleration m_sec_sec) {
		_decelRate = m_sec_sec;
	}

	/**
	 * get the dive rate
	 *
	 * @param val dive rate (m/sec/sec)
	 */
	public void setDiveRate(final double val) {
	}

	/**
	 * set the rate at which the participant uses fuel (per knot per second)
	 */
	public void setFuelUsageRate(final double val) {
		_fuel_usage_rate = val;
	}

	/**
	 * get the min Height at which this platform operates
	 *
	 * @param val max Height(m)
	 */
	public void setMaxHeight(final WorldDistance val) {
	}

	/**
	 * set the maximum speed for this participant (m/sec)
	 *
	 * @param val max speed (m/sec)
	 */
	public void setMaxSpeed(final WorldSpeed val) {
		_maxSpeed = val;
	}

	/**
	 * get the min Height at which this platform operates
	 *
	 * @param val min Height(m)
	 */
	public void setMinHeight(final WorldDistance val) {
	}

	/**
	 * set the maximum speed for this participant (m/sec)
	 *
	 * @param val max speed (m/sec)
	 */
	public void setMinSpeed(final WorldSpeed val) {
		_minSpeed = val;
	}

	public void setName(final String val) {
		_myName = val;
	}

	@Override
	public String toString() {
		return getName();
	}

}