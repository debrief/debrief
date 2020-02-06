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

import java.util.ArrayList;
import java.util.Date;

public abstract class CoreMeasurementContribution<Measurement extends CoreMeasurementContribution.CoreMeasurement>
		extends BaseContribution {

	public static class CoreMeasurement {

		protected final Date time;
		private boolean isActive = true;
		/**
		 * the (optional) color for this measurement
		 *
		 */
		private java.awt.Color color = null;

		public CoreMeasurement(final Date time) {
			this.time = time;
		}

		final public java.awt.Color getColor() {
			return color;
		}

		final public Date getDate() {
			return time;
		}

		final public boolean isActive() {
			return isActive;
		}

		final public void setActive(final boolean active) {
			isActive = active;
		}

		final public void setColor(final java.awt.Color color) {
			this.color = color;
		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final String OBSERVATIONS_NUMBER = "numObservations";

	/**
	 * the set of measurements we store
	 *
	 */
	protected ArrayList<Measurement> measurements = new ArrayList<Measurement>();

	public CoreMeasurementContribution() {
		super();
	}

	/**
	 * store this new measurement
	 *
	 * @param measure
	 */
	final public void addMeasurement(final Measurement measure) {
		// extend the time period accordingly
		if (this.getStartDate() == null) {
			this.setStartDate(measure.time);
			this.setFinishDate(measure.time);
		} else {
			final long newTime = measure.time.getTime();
			if (this.getStartDate().getTime() > newTime)
				this.setStartDate(measure.time);
			if (this.getFinishDate().getTime() < newTime)
				this.setFinishDate(measure.time);
		}
		measurements.add(measure);
		firePropertyChange(OBSERVATIONS_NUMBER, measurements.size(), measurements.size());
	}

	@Override
	final public ContributionDataType getDataType() {
		return ContributionDataType.MEASUREMENT;
	}

	final public ArrayList<Measurement> getMeasurements() {
		return measurements;
	}

	final public int getNumObservations() {
		return measurements.size();
	}

	/**
	 * whether this contribution has any measurements yet
	 *
	 * @return
	 */
	final public boolean hasData() {
		return measurements.size() > 0;
	}
}