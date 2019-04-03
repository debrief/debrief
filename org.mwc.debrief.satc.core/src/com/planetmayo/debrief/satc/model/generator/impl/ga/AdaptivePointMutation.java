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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;

import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.util.MathUtils;
import org.locationtech.jts.geom.Point;

public class AdaptivePointMutation extends AbstractMutation
{
	public AdaptivePointMutation(List<StraightLeg> legs, Probability mutationProbability)
	{
		super(legs, mutationProbability);
	}

	private double distance(double T, Random rng) {
		double tmp =  Math.signum(rng.nextDouble() - 0.5) * T *
				(Math.pow(1 + 1 / T, 2 * rng.nextDouble() - 1) - 1);
		return tmp;
	}	
	
	@Override
	protected Point mutatePoint(int iteration, Point current, int legIndex,
			boolean useEndPoint, Random rng)
	{
		double T = 4 * Math.exp(-0.85 * Math.pow(iteration, 0.25));
		T = Math.max(0.01, T);
		T = T * T;

		return MathUtils.calculateBezier(distance(T, rng), current, nextVertex(legIndex, useEndPoint, rng), null);
	}
}
