/**
 * 
 */
package org.mwc.debrief.track_shift.views;

import java.awt.Color;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.Track.TMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.Algorithms.Conversions;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public final class Doublet
{
	private final SensorContactWrapper _sensor;

	private final FixWrapper _fix;

	private final TrackSegment _parent;
	
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
			final FixWrapper fix, TrackSegment parent)
	{
		_sensor = sensor;
		_fix = fix;
		_parent = parent;
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
		_workingTargetLocation.copy(_fix.getLocation());

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
	
	/** get the base frequency of this track participant, if it has one
	 * 
	 * @return
	 */
	public double getBaseFrequency()
	{
		double res = 0d;
		if(_parent instanceof TMASegment)
		{
			TMASegment tma = (TMASegment) _parent;
			res = tma.getBaseFrequency();
		}
		
		return res;
	}
	
	
	public double getMeasuredFrequency()
	{
		return _sensor.getFrequency();
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

	/** calculate the corrected frequency (take out ownship doppler)
	 * 
	 * @return
	 */
	public double getCorrectedFrequency()
	{
		return 65 + _fix.getCourse() / 10;
	}

	/** calculate what the frequency of the target should be (base freq plus both dopplers)
	 * 
	 * @return
	 */
	public double getPredictedFrequency()
	{
		return 65 - Math.sin(_fix.getCourse())  * 6;
	}
}