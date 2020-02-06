
package Debrief.Tools.Tote.Calculations;


import java.text.DecimalFormat;

import MWC.Algorithms.FrequencyCalcs;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

public class dopplerCalc extends plainCalc
{

	/**
	 * f-nought
	 */
	private double _fNought = 150;

	/** speed of sound (kts)
	 * 
	 */
	private double _C = 3032;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public dopplerCalc()
	{
		super(new DecimalFormat("0.000"), "Doppler", "Hz");
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	public double calculate(final Watchable primary, final Watchable secondary,
			final HiResDate thisTime)
	{
		double freq = 0.0;
		if ((primary != null) && (secondary != null) && (primary != secondary))
		{
	
			double rxSpeedKts = primary.getSpeed();
			double txSpeedKts = secondary.getSpeed();;
			
			double rxCourseDegs = Math.toDegrees(primary.getCourse());
			double txCourseDegs = Math.toDegrees(secondary.getCourse());
			
			double bearingDegs = Math.toDegrees(primary.getLocation().bearingFrom(secondary.getLocation()));

			// what's the observed freq?
			freq = FrequencyCalcs.getPredictedFreq(_fNought, _C, rxSpeedKts, rxCourseDegs, 
					txSpeedKts, txCourseDegs, bearingDegs);
		}
		return freq;
	}
	
	/**
	 * does this calculation require special bearing handling (prevent wrapping
	 * through 360 degs)
	 */
	public final boolean isWrappableData()
	{
		return false;
	}

	public String update(final Watchable primary, final Watchable secondary,
			final HiResDate time)
	{
		// check we have data
		if((primary == null) || (secondary == null))
			return NOT_APPLICABLE;
		
    return _myPattern.format(
       calculate(primary, secondary, time));
	}

	public void setFNought(double value)
	{
		_fNought = value;
	}
	public double getFNought()
	{
		return _fNought;
	}

	public void setSpeedOfSound(double value)
	{
		_C = value;
	}
	
	public double getSpeedOfSound()
	{
		return _C;
	}
	

}
