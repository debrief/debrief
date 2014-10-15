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
package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;

import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.vividsolutions.jts.geom.Point;

public class NonAdaptivePointMutation extends AbstractMutation
{

	public NonAdaptivePointMutation(List<StraightLeg> legs,	Probability mutationProbability)
	{
		super(legs, mutationProbability);
	}

	@Override
	protected Point mutatePoint(int iteration, Point current, int legIndex,
			boolean useEndPoint, Random rng)
	{
		return MathUtils.calculateBezier(0.3 * rng.nextDouble(), current, nextVertex(legIndex, useEndPoint, rng), null);
	}
}
