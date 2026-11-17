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

package org.mwc.cmap.xyplot.views;

import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

public class LocationCalculator implements ILocationCalculator {

	private int _units;

	public LocationCalculator() {
		this(WorldDistance.DEGS);
	}

	public LocationCalculator(final int units) {
		setUnits(units);
	}

	@Override
	public double getDistance(final LineShape line, final Watchable watchable) {
		return getDistance(line.getLine_Start(), line.getLineEnd(), watchable.getLocation());
	}

	/**
	 * find the distance from start point to the perpendicular line meeting the
	 * watch point
	 *
	 * @param start
	 * @param end
	 * @param watchableLocation
	 * @return
	 */
	double getDistance(final WorldLocation start, final WorldLocation end,
			final WorldLocation watchableLocation) {
		final double rangeDegs = watchableLocation.rangeFrom(start);
		final double hyp = new WorldDistance(rangeDegs, WorldDistance.DEGS).getValueIn(_units);

		// angle from start to end
		final double lineBrg = end.bearingFrom(start);
		final double pointBrg = watchableLocation.bearingFrom(start);
		final double delta = pointBrg - lineBrg;

		// how far along x-section?
		final double along = hyp * Math.cos(delta);

		return along;
	}

	public void setUnits(final int units) {
		this._units = units;
	}
}
