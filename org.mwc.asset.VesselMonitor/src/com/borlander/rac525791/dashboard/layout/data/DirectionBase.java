/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
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

abstract class DirectionBase implements ControlUIModel {

	public final boolean isFullCircleMapped() {
		return true;
	}

	public final Dimension getMaximumMark() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	public final Dimension getUnitsAndMultipliersSize() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	public final Point getUnitsPosition() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	public final Point getValueTextPosition() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	public final Dimension getValueTextSize() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

	public final Dimension getZeroMark() {
		throw new UnsupportedOperationException("Not supported for Direction control");
	}

}
