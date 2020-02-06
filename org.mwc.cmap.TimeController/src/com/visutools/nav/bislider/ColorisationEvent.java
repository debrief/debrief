
package com.visutools.nav.bislider;

import java.awt.AWTEvent;
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

public class ColorisationEvent extends AWTEvent implements Colorizer, Cloneable {
	static final long serialVersionUID = 6868457033542496338L;

	// the data
	private double[][] ColorArray = null;

	/**
	 * Constructor
	 *
	 * @param Source_Arg     Description of the Parameter
	 * @param ColorArray_Arg Description of the Parameter
	 */
	public ColorisationEvent(final Object Source_Arg, final double[][] ColorArray_Arg) {
		super(Source_Arg, 0);
		ColorArray = ColorArray_Arg;
	} // Constructor

	/**
	 * duplicate the object
	 **/
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * @return the raw format of the color table
	 */
	@Override
	public double[][] getColorArray() {
		return ColorArray;
	} // getColorArray()

	/**
	 * @param Value_Arg Description of the Parameter
	 * @return the color associated with a value by this table of colors
	 */
	@Override
	public Color getColorForValue(final double Value_Arg) {

		if (Value_Arg >= ColorArray[0][0] && Value_Arg <= ColorArray[ColorArray.length - 1][1]) {
			for (int k = 0; k < ColorArray.length; k++) {
				if (Value_Arg >= ColorArray[k][0] && Value_Arg <= ColorArray[k][1]) {
					return new Color((int) ColorArray[k][2]);
				}
			}
		}

		return null;
	} // getColorForValue()

	/**
	 * @return the maximum value to colorize
	 */
	@Override
	public double getMaximum() {
		return ColorArray[ColorArray.length - 1][1];
	} // getMaximum()

	/**
	 * @return the minimum value to colorize
	 */
	@Override
	public double getMinimum() {
		return ColorArray[0][0];
	} // getMinimum()

	/**
	 * @param Value_Arg Description of the Parameter
	 * @return if the value is in the range of the colorization
	 */
	@Override
	public boolean isColorizable(final double Value_Arg) {
		return (Value_Arg >= ColorArray[0][0] && Value_Arg <= ColorArray[ColorArray.length - 1][1]);
	} // isColorizable()

	/**
	 * display the content of the color table
	 *
	 * @return Description of the Return Value
	 */
	@Override
	public String toString() {
		final StringBuffer Text1 = new StringBuffer("\n");

		for (int k = 0; k < ColorArray.length; k++) {
			final Color Color1 = new Color((int) ColorArray[k][2]);
			Text1.append(ColorArray[k][0] + "<->" + ColorArray[k][1] + " with R=" + Color1.getRed() + ", G="
					+ Color1.getGreen() + ", B=" + Color1.getBlue() + "\n");
		}

		return Text1.toString();
	} // toString()

}
/*
 * ColorisationEvent.java
 */
