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

package ASSET.Models.Movement;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 13-Aug-2003 Time: 15:14:31 To
 * change this template use Options | File Templates.
 */
public abstract class ThreeDimMovementCharacteristics extends MovementCharacteristics {
	//////////////////////////////////////////////////
	// editable properties
	//////////////////////////////////////////////////
	static public class ThreeDimMovementCharacteristicsInfo extends EditorType {

		/**
		 * constructor for editable details
		 *
		 * @param data the object we're going to edit
		 */
		public ThreeDimMovementCharacteristicsInfo(final ThreeDimMovementCharacteristics data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = {
						prop("AccelRate", "the rate of acceleration for this vessel (kts/sec)"),
						prop("DecelRate", "the rate of acceleration for this vessel (kts/sec)"),
						prop("FuelUsageRate", "the rate of fuel usage for this vessel (%/kt/sec)"),
						prop("MaxHeight", "the maximum Height which this vessel travels to (m)"),
						prop("MaxSpeed", "the rate of acceleration for this vessel (kts/sec)"),
						prop("DefaultClimbRate", "the normal rate at which the participant climbs"),
						prop("DefaultDiveRate", "the normal rate at which the participant dives"),
						prop("Name", "the name of this set of movement characteristics"), };
				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	//////////////////////////////////////////////////
	// property testing
	//////////////////////////////////////////////////
	public static class ThreeDMoveCharsTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new ThreeDimMovementCharacteristics("", null, null, 12, null, null, null, null, null, null) {
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
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * get a set of movement chars
	 *
	 * @param i the turning circle to use.
	 * @return
	 */
	public static MovementCharacteristics getSampleChars(final float i) {
		return new ThreeDimMovementCharacteristics("the moves", new WorldAcceleration(1, WorldAcceleration.Kts_sec),
				new WorldAcceleration(1, WorldAcceleration.Kts_sec), 0.0000001, new WorldSpeed(30, WorldSpeed.Kts),
				new WorldSpeed(1, WorldSpeed.Kts), new WorldSpeed(1, WorldSpeed.M_sec),
				new WorldSpeed(1, WorldSpeed.M_sec), new WorldDistance(300, WorldSpeed.M_sec),
				new WorldDistance(1, WorldSpeed.M_sec)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * get the turning circle diameter (m) at this speed (in m/sec)
			 */
			@Override
			public double getTurningCircleDiameter(final double m_sec) {
				return i;
			}
		};
	}

	/**
	 * min Height at which we can travel
	 */
	protected WorldDistance _minHeight;

	/**
	 * max Height at which we travel
	 */
	protected WorldDistance _maxHeight;

	protected WorldSpeed _defaultClimbRate;

	protected WorldSpeed _defaultDiveRate;

	private EditorType _myEditor;

	public ThreeDimMovementCharacteristics(final String myName, final WorldAcceleration accelRate,
			final WorldAcceleration decelRate, final double fuel_usage_rate, final WorldSpeed maxSpeed,
			final WorldSpeed minSpeed, final WorldSpeed defaultClimbRate, final WorldSpeed defaultDiveRate,
			final WorldDistance maxHeight, final WorldDistance minHeight) {
		super(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed);
		this._defaultClimbRate = defaultClimbRate;
		this._defaultDiveRate = defaultDiveRate;
		this._maxHeight = maxHeight;
		this._minHeight = minHeight;
	}

	@Override
	public WorldSpeed getClimbRate() {
		return _defaultClimbRate;
	}

	public WorldSpeed getDefaultClimbRate() {
		return _defaultClimbRate;
	}

	public WorldSpeed getDefaultDiveRate() {
		return _defaultDiveRate;
	}

	@Override
	public WorldSpeed getDiveRate() {
		return _defaultDiveRate;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new ThreeDimMovementCharacteristicsInfo(this);

		return _myEditor;
	}

	@Override
	public WorldDistance getMaxHeight() {
		return _maxHeight;
	}

	//////////////////////////////////////////////////
	// property editing
	//////////////////////////////////////////////////

	@Override
	public WorldDistance getMinHeight() {
		return _minHeight;
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

	public void setDefaultClimbRate(final WorldSpeed _defaultClimbRate) {
		this._defaultClimbRate = _defaultClimbRate;
	}

	public void setDefaultDiveRate(final WorldSpeed _defaultDiveRate) {
		this._defaultDiveRate = _defaultDiveRate;
	}

	@Override
	public void setMaxHeight(final WorldDistance _maxHeight) {
		this._maxHeight = _maxHeight;
	}

	@Override
	public void setMinHeight(final WorldDistance _minHeight) {
		this._minHeight = _minHeight;
	}

}
