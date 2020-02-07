
package com.visutools.nav.bislider;

import java.awt.Color;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public interface Colorizer {
	/**
	 * duplicate the object
	 */
	public Object clone() throws CloneNotSupportedException;

	/**
	 * @return the raw format of the color table
	 */
	public double[][] getColorArray();

	/**
	 * @return the color associated with a value by this table of colors
	 */
	public Color getColorForValue(double Value_Arg);

	/**
	 * @return the maximum colored value
	 */
	public double getMaximum();

	/**
	 * @return the maximum colored value
	 */
	public double getMinimum();

	/**
	 * @return if the value is in the range of the colorization
	 */
	public boolean isColorizable(double Value_Arg);

}
