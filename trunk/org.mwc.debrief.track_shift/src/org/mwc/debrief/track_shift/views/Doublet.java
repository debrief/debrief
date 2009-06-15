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
	public double calculateError(double measuredBearing, double calcBearing)
	{
		double theError = measuredBearing - calcBearing;

		while (theError > 180)
			theError -= 360.0;

		while (theError < -180)
			theError += 360.0;

		return theError;
	}
	
	public double getMeasuredBearing()
	{
		return _sensor.getBearing();
	}

	public double getCalculatedBearing(final WorldVector sensorOffset,
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
		double calcBearing = error.getBearing();
		calcBearing = Conversions.Rads2Degs(calcBearing);
		
		if(calcBearing < 0)
			calcBearing += 360;
		
		return calcBearing;
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