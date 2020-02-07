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

package MWC.TacticalData.temporal;

import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;

/**
 * interface for object that stores time preferences
 *
 * @author ian.mayo
 *
 */
public interface TimeControlPreferences {
	/**
	 * get the time interval for auto-stepping
	 *
	 */
	public Duration getAutoInterval();

	/**
	 * determine how the time is to be displayed
	 *
	 * @return
	 */
	public String getDTGFormat();

	/**
	 * find out the large step size
	 *
	 * @return
	 */
	public Duration getLargeStep();

	/**
	 * get the slider end time (not necessarily the same as the time period for the
	 * data)
	 *
	 */
	public HiResDate getSliderEndTime();

	/**
	 * get the slider start time (not necessarily the same as the time period for
	 * the data)
	 *
	 */
	public HiResDate getSliderStartTime();

	/**
	 * find out the small step size
	 *
	 * @return
	 */
	public Duration getSmallStep();

	/**
	 * set the time interval for auto-stepping
	 *
	 */
	public void setAutoInterval(Duration duration);

	/**
	 * set how the time is to be displayed
	 *
	 * @param format
	 */
	public void setDTGFormat(String format);

	/**
	 * set the large step size
	 *
	 * @param step
	 */
	public void setLargeStep(Duration step);

	/**
	 * set the slider end time (not necessarily the same as the time period for the
	 * data)
	 *
	 */
	public void setSliderEndTime(HiResDate dtg);

	/**
	 * set the slider start time (not necessarily the same as the time period for
	 * the data)
	 *
	 */
	public void setSliderStartTime(HiResDate dtg);

	/**
	 * set the small step size
	 *
	 * @param step
	 */
	public void setSmallStep(Duration step);

}
