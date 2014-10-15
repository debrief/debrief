/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI;

import MWC.GenericData.HiResDate;

public interface StepperListener
{
	/**
	 * the mode for stepping has changed
	 */
	void steppingModeChanged(boolean on);

	/**
	 * the current time has changed
	 */
	void newTime(HiResDate oldDTG, HiResDate newDTG, CanvasType canvas);

	/**
	 * the name of this listener
	 */
	String toString();

	/**
	 * embedded interface for an actual stepper controller (such as the Debrief
	 * Step Control)
	 */
	static public interface StepperController
	{
		/**
		 * add a new listener
		 */
		public void addStepperListener(StepperListener listener);

		/**
		 * remove a listener
		 */
		public void removeStepperListener(StepperListener listener);

		/**
		 * instruct the stepper to move forwards, backwards
		 */
		public void doStep(boolean forward, boolean large_step);

		/**
		 * find out what the current time is
		 */
		public HiResDate getCurrentTime();

		/**
		 * determine the zero-time or anchor for the current dataset
		 */
		public HiResDate getTimeZero();
	}
}
