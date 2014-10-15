/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.RubberBanding;

import java.awt.Component;
import java.awt.Graphics;

import MWC.GUI.Rubberband;


public class RubberbandLine extends Rubberband{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public RubberbandLine() {
	}
	public RubberbandLine(final Component component) {
		super(component);
	}
	public void drawLast(final Graphics graphics) {
		graphics.drawLine(anchorPt.x, anchorPt.y, 
							lastPt.x,   lastPt.y);
	}
	public void drawNext(final Graphics graphics) {
		graphics.drawLine(anchorPt.x,    anchorPt.y, 
							stretchedPt.x, stretchedPt.y);
	}
}
