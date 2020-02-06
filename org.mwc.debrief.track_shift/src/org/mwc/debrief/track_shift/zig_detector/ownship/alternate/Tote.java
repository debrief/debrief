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
package org.mwc.debrief.track_shift.zig_detector.ownship.alternate;

public class Tote {

	public int index; // index of tote in vector. (start with 1; the index of first one is 1)

	public String sdate;
	public String stime;

	public double dabsolute_time; // absolute time from 1970.0 i.e seconds from 1970/01/01 0h 0min 0.0sec

//  public double dlong;            // longitude (decimal degrees; from 0 to +-180; +E -W))
//  public double dlat;             // latitude (decimal degrees; from 0 to +-90); +N -S)

	public double dheading; // azimuth (decimal degrees; from 0 to 360; NESW i.e. clockwise))
	public double dspeed; // speed in knots
}
