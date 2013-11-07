package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;

public class RoutesFitnessEvaluator implements FitnessEvaluator<List<StraightRoute>>
{	
	private final List<StraightLeg> legs;
	private final IContributions contributions;
	
	public RoutesFitnessEvaluator(List<StraightLeg> legs, IContributions contributions)
	{
		super();
		this.legs = legs;
		this.contributions = contributions;
	}

	@Override
	public double getFitness(List<StraightRoute> candidate,	List<? extends List<StraightRoute>> population)
	{
		int length = candidate.size();
		double error = 0;
		boolean impossible = false;
		for (int i = 0; i < length; i++)
		{
			StraightRoute route = candidate.get(i);
			StraightLeg leg = legs.get(i);
			if (route.isPossible())
			{
				if (route.getScore() == 0.)
				{
					leg.decideAchievableRoute(route);
					if (route.isPossible())
					{
						error += calculateRouteScore(route);
					}
					else 
					{
						impossible = true;
					}
				}
				else
				{
					error += route.getScore();
				}
			}
			else
			{
				impossible = true;
			}
		}
		if (impossible)
		{
			return Double.MAX_VALUE;
		}
		return error;
	}
	
	public double calculateRouteScore(StraightRoute route)
	{
		double score = 0;
		for (BaseContribution contribution : contributions)
		{
			score += contribution.calculateErrorScoreFor(route);
		}
		route.setScore(score);
		return score;
	}

	@Override
	public boolean isNatural()
	{
		return false;
	}
}
