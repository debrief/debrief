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

import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 13-Aug-2003 Time: 15:01:47 To
 * change this template use Options | File Templates.
 */
public class SSMovementCharacteristics extends ThreeDimMovementCharacteristics {
	// ////////////////////////////////////////////////
	// member objects
	// ////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * old-constructor, mainly to support testing
	 */
	public static SSMovementCharacteristics generateDebug(final String myName, final double accelRate,
			final double decelRate, final double fuel_usage_rate, final double maxSpeed, final double minSpeed,
			final double turningCircleDiam, final double defaultClimbRate, final double defaultDiveRate,
			final double maxDepth, final double minDepth) {
		return new SSMovementCharacteristics(myName, new WorldAcceleration(accelRate, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(decelRate, WorldAcceleration.M_sec_sec), fuel_usage_rate,
				new WorldSpeed(maxSpeed, WorldSpeed.M_sec), new WorldSpeed(minSpeed, WorldSpeed.M_sec),
				new WorldDistance(turningCircleDiam, WorldDistance.METRES),
				new WorldSpeed(defaultClimbRate, WorldSpeed.M_sec), new WorldSpeed(defaultDiveRate, WorldSpeed.M_sec),
				new WorldDistance(maxDepth, WorldDistance.METRES), new WorldDistance(minDepth, WorldDistance.METRES));
	}

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	/**
	 * get a sample set of characteristics, largely for testing
	 *
	 * @return
	 */
	public static SSMovementCharacteristics getSampleSSChars() {
		return new SSMovementCharacteristics("test", new WorldAcceleration(12, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(3, WorldAcceleration.M_sec_sec), 12, new WorldSpeed(12, WorldSpeed.M_sec),
				new WorldSpeed(2, WorldSpeed.M_sec), new WorldDistance(12, WorldDistance.METRES),
				new WorldSpeed(12, WorldSpeed.M_sec), new WorldSpeed(12, WorldSpeed.M_sec),
				new WorldDistance(12, WorldDistance.METRES), new WorldDistance(12, WorldDistance.METRES));
	}

	/**
	 * the turning circle for this vessel
	 */
	private final WorldDistance _turningCircleDiameter;

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	public SSMovementCharacteristics(final String myName, final WorldAcceleration accelRate,
			final WorldAcceleration decelRate, final double fuel_usage_rate, final WorldSpeed maxSpeed,
			final WorldSpeed minSpeed, final WorldDistance turningCircleDiam, final WorldSpeed defaultClimbRate,
			final WorldSpeed defaultDiveRate, final WorldDistance maxDepth, final WorldDistance minDepth) {
		super(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate, defaultDiveRate,
				maxDepth, minDepth);
		_turningCircleDiameter = turningCircleDiam;
	}

	/**
	 * get the turning circle diameter (m) at this speed (in m/sec)
	 */
	@Override
	public double getTurningCircleDiameter(final double m_sec) {
		return _turningCircleDiameter.getValueIn(WorldDistance.METRES);
	}

}
