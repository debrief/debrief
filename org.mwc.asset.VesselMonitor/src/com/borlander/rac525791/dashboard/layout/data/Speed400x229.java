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
package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import com.borlander.rac525791.dashboard.layout.ControlUIModel;

class Speed400x229 implements ControlUIModel {

	public Point getControlCenter() {
		return new Point(79, 148);
	}

	public Dimension getUnitsAndMultipliersSize() {
		return new Dimension(38, 13);
	}

	public Point getUnitsPosition() {
		return new Point(-1, 20);
	}

	public int getRedSectorRadius() {
		return 53;
	}
	
	public Dimension getZeroMark() {
		return new Dimension(-17, 55);
	}
	
	public Dimension getMaximumMark() {
		return new Dimension(35, -46);
	}
	
	public boolean isFullCircleMapped() {
		return false;
	}
	
	public Dimension getValueTextSize() {
		return new Dimension(23, 21);
	}
	
	public Point getValueTextPosition() {
		return new Point(92 - 79, 137 - 148);
	}
	
	
}

