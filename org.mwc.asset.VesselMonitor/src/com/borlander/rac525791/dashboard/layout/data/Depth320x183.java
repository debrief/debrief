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

package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import com.borlander.rac525791.dashboard.layout.ControlUIModel;

class Depth320x183 implements ControlUIModel {

	@Override
	public Point getControlCenter() {
		return new Point(256, 118);
	}

	@Override
	public Dimension getMaximumMark() {
		return new Dimension(12, 44);
	}

	@Override
	public int getRedSectorRadius() {
		return 42;
	}

	@Override
	public Dimension getUnitsAndMultipliersSize() {
		return new Dimension(28, 11);
	}

	@Override
	public Point getUnitsPosition() {
		return new Point(-28, 15);

	}

	@Override
	public Point getValueTextPosition() {
		return new Point(-28, -8);
	}

	@Override
	public Dimension getValueTextSize() {
		return new Dimension(19, 17);
	}

	@Override
	public Dimension getZeroMark() {
		return new Dimension(-28, -37);
	}

	@Override
	public boolean isFullCircleMapped() {
		return false;
	}

}
