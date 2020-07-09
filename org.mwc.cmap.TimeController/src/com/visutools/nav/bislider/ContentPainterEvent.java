
package com.visutools.nav.bislider;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

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

public class ContentPainterEvent extends AWTEvent implements Cloneable {

	// ---------- MODIFIERS|--------------------
	// Type|----------------------------------------------- Name = Init value
	protected final static javax.swing.text.html.parser.ParserDelegator MAXIMUM_VARIABLE_SIZE_FOR_NAME = null;

	final static long serialVersionUID = 6868457033542496338L;
	// the data
	protected Graphics Graphics1;
	protected double Min1;
	protected double Max1;
	protected Color Color1;
	protected int SegmentIndex;
	protected Rectangle Rectangle1;
	protected Rectangle BoundingRectangle1;

	/**
	 * Constructor
	 *
	 * @param Source_Arg            Source of the event. Should be the BiSlider
	 *                              instance
	 * @param Graphics_Arg          The graphics object to draw with
	 * @param Min_Arg               The minimum value of this piece of data
	 * @param Max_Arg               The maximum value of this piece of data
	 * @param Index_Arg             The index of the segment
	 * @param Color_Arg             The color normally associated with these values
	 * @param Rectangle_Arg         The rectangle to fill with painting
	 * @param BoundingRectangle_Arg The rectangle in which the other rectangle is
	 */
	public ContentPainterEvent(final Object Source_Arg, final Graphics Graphics_Arg, final double Min_Arg,
			final double Max_Arg, final int Index_Arg, final Color Color_Arg, final Rectangle Rectangle_Arg,
			final Rectangle BoundingRectangle_Arg) {

		super(Source_Arg, 0);

		SegmentIndex = Index_Arg;
		Graphics1 = Graphics_Arg;
		Min1 = Min_Arg;
		Max1 = Max_Arg;
		Color1 = Color_Arg;
		Rectangle1 = Rectangle_Arg;
		BoundingRectangle1 = BoundingRectangle_Arg;
	} // Constructor

	/**
	 * duplicate the object
	 *
	 * @return Description of the Return Value
	 * @exception CloneNotSupportedException Description of the Exception
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * @return the Bounding Rectangle, the segment in witch the rectangle is painted
	 */
	public Rectangle getBoundingRectangle() {
		return BoundingRectangle1;
	} // getRectangle()

	/**
	 * @return the default color to paint the rectangle
	 */
	public Color getColor() {
		return Color1;
	} // getColor()

	/**
	 * @return the Graphics to draw the rectangle with
	 */
	public Graphics getGraphics() {
		return Graphics1;
	} // getGraphics()

	/**
	 * @return the maximum value to paint
	 */
	public double getMaximum() {
		return Max1;
	} // getMaximum()

	/**
	 * @return the minimum value to paint
	 */
	public double getMinimum() {
		return Min1;
	} // getMinimum()

	/**
	 * @return the Rectangle, bounding box to paint something in
	 */
	public Rectangle getRectangle() {
		return Rectangle1;
	} // getRectangle()

	/**
	 * @return the Index of the segment we are drawing
	 */
	public int getSegmentIndex() {
		return SegmentIndex;
	} // getSegmentIndex()

	/**
	 * display the content of the color table
	 *
	 * @return Description of the Return Value
	 */
	@Override
	public String toString() {
		return "ContentPainterEvent@" + hashCode() + " to paint " + Rectangle1 + " with " + Color1 + " to represent "
				+ Min1 + "-" + Max1;
	} // toString()
} // ContentPainterEvent.java
