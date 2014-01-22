package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.util.MathUtils;

public class PointsCrossover extends AbstractCrossover<List<StraightRoute>> 
								implements EvolutionObserver<List<StraightRoute>>
{
	private final Probability crossoverProbability;
	
	private final List<StraightLeg> legs;
	
	private final ThreadLocal<Double> currentDeriviation = new ThreadLocal<Double>();
	
	public PointsCrossover(List<StraightLeg> legs, Probability crossoverProbability) 
	{
		super(1);
		this.crossoverProbability = crossoverProbability;
		this.legs = legs;
	}
	
	@Override
	protected List<List<StraightRoute>> mate(List<StraightRoute> parent1, List<StraightRoute> parent2,
			int numberOfCrossoverPoints, Random rng)
	{
		ArrayList<List<StraightRoute>> wrapper = new ArrayList<List<StraightRoute>>();		
		if (! crossoverProbability.nextEvent(rng))
		{
			wrapper.add(parent1);
			wrapper.add(parent2);
			return wrapper;
		}
		for (int k = 0; k < 2; k++)
		{
			int length = parent1.size();
			List<StraightRoute> result = new ArrayList<StraightRoute>();
			for (int i = 0; i < length; i++)
			{
				StraightRoute route1 = parent1.get(i);
				StraightRoute route2 = parent2.get(i);
				if (route1.isPossible() && route2.isPossible())
				{
					StraightRoute newRoute;
					if (route1.getScore() < route2.getScore())
					{
						newRoute = route2;
						route2 = route1;
						route1 = newRoute;
					}
					double scoreDiff = route1.getScore() - route2.getScore();

					StraightLeg leg = legs.get(i);
					newRoute = (StraightRoute) leg.createRoute(
							"",
							MathUtils.calculateBezier(getDistance(scoreDiff, rng),
									route1.getStartPoint(), route2.getStartPoint(), null),
							MathUtils.calculateBezier(getDistance(scoreDiff, rng),
									route1.getEndPoint(), route2.getEndPoint(), null));
					leg.decideAchievableRoute(newRoute);
					result.add(newRoute);
				}
				else if (route1.isPossible())
				{
					result.add(route1);
				}
				else
				{
					result.add(route2);
				}
			}
			wrapper.add(result);
		}
		return wrapper;
	}
	
	private double getDistance(double scoreDiff, Random rng) 
	{
		if (currentDeriviation.get() == null) 
		{
			return 1.2 * rng.nextDouble() - 0.1; 
		}
		double diff = Math.abs(2 * rng.nextGaussian() * currentDeriviation.get());
		if (diff >= scoreDiff) 
		{
			return rng.nextDouble();
		}
		return diff / scoreDiff;
	}

	@Override
	public void populationUpdate(
			PopulationData<? extends List<StraightRoute>> data)
	{
		currentDeriviation.set(data.getFitnessStandardDeviation());
	}
}
