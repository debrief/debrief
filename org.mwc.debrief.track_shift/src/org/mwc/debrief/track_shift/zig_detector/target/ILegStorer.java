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
package org.mwc.debrief.track_shift.zig_detector.target;


/** interface for listener class that is told when a new leg is detected
 * 
 * @author ian
 *
 */
public interface ILegStorer
{

	/**
	 * register this leg of data
	 * 
	 * @param scenario
	 * @param tStart
	 * @param tEnd
	 * @param rms - the %age error from the RMS for the whole leg
	 */
	void storeLeg(String scenarioName, long tStart, long tEnd,
			double rms);	
}