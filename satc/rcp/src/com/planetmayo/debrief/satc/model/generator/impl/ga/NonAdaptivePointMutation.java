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
