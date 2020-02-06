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

package com.planetmayo.debrief.satc.model;

/**
 * description of the range of performances achievable by a vehicle
 *
 * @author ian
 *
 */
public class VehicleType extends ModelObject {
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

	public VehicleType(final String name, final double minSpeedMS, final double maxSpeedMS,
			final double minTurnRate_RadperSec, final double maxTurnRate_RadperSec, final double minAccelRateMSS,
			final double maxAccelRateMSS, final double minDecelRateMSS, final double maxDecelRateMSS) {
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

	public double getMaxAccelRate() {
		return maxAccelRateMSS;
	}

	public double getMaxDecelRate() {
		return maxDecelRateMSS;
	}

	public double getMaxSpeed() {
		return maxSpeedMS;
	}

	public double getMaxTurnRate() {
		return maxTurnRate_RadperSec;
	}

	public double getMinAccelRate() {
		return minAccelRateMSS;
	}

	public double getMinDecelRate() {
		return minDecelRateMSS;
	}

	public double getMinSpeed() {
		return minSpeedMS;
	}

	public double getMinTurnRate() {
		return minTurnRate_RadperSec;
	}

	public String getName() {
		return name;
	}

}
