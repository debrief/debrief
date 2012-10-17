package com.planetmayo.debrief.satc.model;

public class VehicleType extends ModelObject
{

	private static final long serialVersionUID = 1L;

	/**
	 * name of this vehicle type
	 * 
	 */
	private final String _name;

	/*
	 * minimum speed for the vehicle type (m/sec)
	 */
	private final double _minSpd;

	/*
	 * maximum speed for the vehicle type (m/sec)
	 */
	private final double _maxSpd;

	/*
	 * minimum turning circle for this vehicle type (m)
	 */
	private final double _minTurnCircle;

	/*
	 * maximum turning circle for this vehicle type (m)
	 */
	private final double _maxTurnCircle;

	/*
	 * acceleration rate for the vehicle type (m/sec/sec)
	 */
	private final double _accelRate;

	/*
	 * deceleration for the vehicle type (m/sec/sec)
	 */
	private final double _decelRate;

	public VehicleType(String name, double minSpd, double maxSpd,
			double minTurnCircle, double maxTurnCircle, double accelRate,
			double decelRate)
	{
		_name = name;
		_minSpd = minSpd;
		_maxSpd = maxSpd;
		_minTurnCircle = minTurnCircle;
		_maxTurnCircle = maxTurnCircle;
		_accelRate = accelRate;
		_decelRate = decelRate;
	}

	/*
	 * acceleration rate for the vehicle type (m/sec/sec)
	 */
	public double getAccelRate()
	{
		return _accelRate;
	}

	/*
	 * deceleration for the vehicle type (m/sec/sec)
	 */
	public double getDecelRate()
	{
		return _decelRate;
	}

	/*
	 * maximum speed for the vehicle type (m/sec)
	 */
	public double getMaxSpd()
	{
		return _maxSpd;
	}

	/*
	 * maximum turning circle for this vehicle type (m)
	 */
	public double getMaxTurnCircle()
	{
		return _maxTurnCircle;
	}

	/*
	 * minimum speed for the vehicle type (m/sec)
	 */
	public double getMinSpd()
	{
		return _minSpd;
	}

	/*
	 * minimum turning circle for this vehicle type (m)
	 */
	public double getMinTurnCircle()
	{
		return _minTurnCircle;
	}

	/**
	 * name for this vehicle type
	 * 
	 */
	public String getName()
	{
		return _name;
	}

}
