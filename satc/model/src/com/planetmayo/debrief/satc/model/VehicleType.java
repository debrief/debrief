package com.planetmayo.debrief.satc.model;

import com.planetmayo.debrief.satc.model.ModelObject;

/**
 * description of the range of performances achievable by a vehicle
 * 
 * @author ian
 * 
 */
public class VehicleType extends ModelObject
{
	private static final long serialVersionUID = 1L;
	
	private final String name;
	private final double minSpeedMS;
	private final double maxSpeedMS;
	private final double minTurnRate_RadperSec;
	private final double maxTurnRate_RadperSec;
	private final double minAccelRateMSS;
	private final double maxAccelRateMSS;
	private final double minDecelRateMSS;
	private final double maxDecelRateMSS;

	public VehicleType(String name, double minSpeedMS, double maxSpeedMS,
			double minTurnRate_RadperSec, double maxTurnRate_RadperSec, double minAccelRateMSS,
			double maxAccelRateMSS, double minDecelRateMSS, double maxDecelRateMSS)
	{
		this.name = name;
		this.minSpeedMS = minSpeedMS;
		this.maxSpeedMS = maxSpeedMS;
		this.minTurnRate_RadperSec = minTurnRate_RadperSec;
		this.maxTurnRate_RadperSec = maxTurnRate_RadperSec;
		this.minAccelRateMSS = minAccelRateMSS;
		this.maxAccelRateMSS = maxAccelRateMSS;
		this.minDecelRateMSS = minDecelRateMSS;
		this.maxDecelRateMSS = maxDecelRateMSS;
	}

	public String getName()
	{
		return name;
	}

	public double getMinSpeed()
	{
		return minSpeedMS;
	}

	public double getMaxSpeed()
	{
		return maxSpeedMS;
	}

	public double getMinTurnRate()
	{
		return minTurnRate_RadperSec;
	}

	public double getMaxTurnRate()
	{
		return maxTurnRate_RadperSec;
	}

	public double getMinAccelRate()
	{
		return minAccelRateMSS;
	}

	public double getMaxAccelRate()
	{
		return maxAccelRateMSS;
	}

	public double getMinDecelRate()
	{
		return minDecelRateMSS;
	}

	public double getMaxDecelRate()
	{
		return maxDecelRateMSS;
	}

}
