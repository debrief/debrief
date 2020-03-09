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

package com.planetmayo.debrief.satc.model.legs;

import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.vividsolutions.jts.geom.Point;

public class AlteringLeg extends CoreLeg {

	public AlteringLeg(final String name) {
		super(name);
	}

	@Override
	public CoreRoute createRoute(final Point start, final Point end, String name) {
		name = name == null ? getName() : name;
		final AlteringRoute route = new AlteringRoute(name, start, getFirst().getTime(), end, getLast().getTime());
		route.generateSegments(_states);
		return route;
	}

	/**
	 * use a simple speed/time decision to decide if it's possible to navigate a
	 * route
	 */
	@Override
	public void decideAchievableRoute(final CoreRoute r) {
		if (!(r instanceof AlteringRoute)) {
			return;
		}
		final AlteringRoute route = (AlteringRoute) r;
		final SpeedRange speedRange = _states.get(0).getSpeed();
		final double distance = route.getDirectDistance();
		final double elapsed = route.getElapsedTime();
		final double speed = distance / elapsed;

		if (!speedRange.allows(speed)) {
			route.setImpossible();
		}
	}

	@Override
	public LegType getType() {
		return LegType.ALTERING;
	}
}