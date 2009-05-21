/**
 * 
 */
package org.mwc.debrief.track_shift.views;

import java.awt.Color;

import Debrief.Wrappers.SensorContactWrapper;
import MWC.Algorithms.Conversions;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public final class Doublet
{
	private final SensorContactWrapper _sensor;

	private final WorldLocation _targetLocation;

	// ////////////////////////////////////////////////
	// working variables to help us along.
	// ////////////////////////////////////////////////
	private final WorldLocation _workingSensorLocation = new WorldLocation(
			0.0, 0.0, 0.0);

	private final WorldLocation _workingTargetLocation = new WorldLocation(
			0.0, 0.0, 0.0);

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////
	Doublet(final SensorContactWrapper sensor,
			final WorldLocation targetLocation)
	{
		_sensor = sensor;
		_targetLocation = targetLocation;
	}

	/**
	 * ok find what the current bearing error is for this track
	 * 
	 * @param sensorOffset
	 *          if the sensor track has been dragged
	 * @param targetOffset
	 *          if the target track has been dragged
	 * @return
	 */
	public double calculateError(final WorldVector sensorOffset,
			final WorldVector targetOffset)
	{
		// copy our locations
		_workingSensorLocation.copy(_sensor.getCalculatedOrigin(null));
		_workingTargetLocation.copy(_targetLocation);

		// apply the offsets
		if (sensorOffset != null)
			_workingSensorLocation.addToMe(sensorOffset);
		if (targetOffset != null)
			_workingTargetLocation.addToMe(targetOffset);

		// calculate the current bearing
		final WorldVector error = _workingTargetLocation
				.subtract(_workingSensorLocation);
		double thisError = error.getBearing();
		thisError = Conversions.Rads2Degs(thisError);

		// and calculate the bearing error
		final double measuredBearing = _sensor.getBearing();
		thisError = measuredBearing - thisError;

		while (thisError > 180)
			thisError -= 360.0;

		while (thisError < -180)
			thisError += 360.0;

		return thisError;
	}

	/**
	 * get the colour of this sensor fix
	 */
	public Color getColor()
	{
		return _sensor.getColor();
	}

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////
	/**
	 * get the DTG of this contact
	 * 
	 * @return the DTG
	 */
	public HiResDate getDTG()
	{
		return _sensor.getDTG();
	}
}