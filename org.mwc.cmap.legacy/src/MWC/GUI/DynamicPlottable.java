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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI;

/** interface for time-sensitive objects that need to know the time in order to plot
 * 
 * @author ian
 *
 */
public interface DynamicPlottable
{

	/** paint this object to the specified canvas
	 * @param dest current destination
	 * @param time milliseconds
   */
  public void paint(CanvasType dest, long time);
}
