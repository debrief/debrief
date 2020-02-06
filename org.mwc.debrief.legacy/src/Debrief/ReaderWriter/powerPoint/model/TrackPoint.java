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
package Debrief.ReaderWriter.powerPoint.model;

import java.util.Date;

public class TrackPoint {
	private final float latitude;
	private final float longitude;
	private final float elevation;
	private final Date time;
	private final String formattedTime;

	public TrackPoint(final float latitude, final float longitude, final float elevation, final Date time,
			final String formattedTime) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
		this.time = time;
		this.formattedTime = formattedTime;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TrackPoint other = (TrackPoint) obj;
		if (Float.floatToIntBits(elevation) != Float.floatToIntBits(other.elevation))
			return false;
		if (formattedTime == null) {
			if (other.formattedTime != null)
				return false;
		} else if (!formattedTime.equals(other.formattedTime))
			return false;
		if (Float.floatToIntBits(latitude) != Float.floatToIntBits(other.latitude))
			return false;
		if (Float.floatToIntBits(longitude) != Float.floatToIntBits(other.longitude))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	public float getElevation() {
		return elevation;
	}

	public String getFormattedTime() {
		return formattedTime;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public Date getTime() {
		return time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(elevation);
		result = prime * result + ((formattedTime == null) ? 0 : formattedTime.hashCode());
		result = prime * result + Float.floatToIntBits(latitude);
		result = prime * result + Float.floatToIntBits(longitude);
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

}
