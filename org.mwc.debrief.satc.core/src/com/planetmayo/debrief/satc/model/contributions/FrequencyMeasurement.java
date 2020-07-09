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

package com.planetmayo.debrief.satc.model.contributions;

import java.awt.Color;
import java.util.Date;

import com.planetmayo.debrief.satc.model.GeoPoint;

/**
 * utility class for storing a measurement
 *
 * @author ian
 *
 */
public class FrequencyMeasurement {
	private final double freq;
	private final Date time;
	private Color color;
	private boolean isActive;
	private final GeoPoint origin;

	public FrequencyMeasurement(final Date time, final GeoPoint theLoc, final double freq) {
		this.time = time;
		this.color = MWC.GUI.Properties.DebriefColors.RED;
		this.isActive = true;
		this.origin = theLoc;
		this.freq = freq;
	}

	public java.awt.Color getColor() {
		return color;
	}

	public Date getDate() {
		return time;
	}

	public double getFrequency() {
		return freq;
	}

	public GeoPoint getLocation() {
		return origin;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(final boolean active) {
		this.isActive = active;
	}

	public void setColor(final java.awt.Color color) {
		this.color = color;
	}

}