package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.util.MathUtils;

public class PointsCrossover extends AbstractCrossover<List<StraightRoute>>
{
	private final Probability crossoverProbability;
	private final Probability useFromFirst;
	private final Probability useFromSecond;
	
	private final List<StraightLeg> legs;
	
	public PointsCrossover(List<StraightLeg> legs, Probability crossoverProbability, Probability useFromFirst, Probability useFromSecond) 
	{
		super(1);
		this.crossoverProbability = crossoverProbability;
		this.useFromFirst = useFromFirst;
		this.useFromSecond = useFromSecond;
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
		int length = parent1.size();
		List<StraightRoute> result = new ArrayList<StraightRoute>();
		for (int i = 0; i < length; i++) 
		{
			StraightRoute route1 = parent1.get(i);
			StraightRoute route2 = parent2.get(i);
			if (route1.isPossible() && route2.isPossible())
			{
				double rnd = rng.nextDouble();
				if (rnd < useFromFirst.doubleValue())
				{
					result.add(route1);
				}
				else if (rnd < useFromFirst.doubleValue() + useFromSecond.doubleValue()) 
				{
					result.add(route2);
				}
				else
				{
					StraightLeg leg = legs.get(i);
					StraightRoute newRoute;
					do
					{
						newRoute = (StraightRoute) leg.createRoute(
								"",
								MathUtils.calculateBezier(rng.nextDouble(),
										route1.getStartPoint(), route2.getStartPoint(), null),
								MathUtils.calculateBezier(rng.nextDouble(),
										route1.getEndPoint(), route2.getEndPoint(), null));
						leg.decideAchievableRoute(newRoute);
					}
					while (!newRoute.isPossible());
					result.add(newRoute);
				}				
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
		return wrapper;
	}
}
