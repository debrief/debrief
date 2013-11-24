package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.legs.AlteringRoute;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class RoutesFitnessEvaluator implements FitnessEvaluator<List<StraightRoute>>
{	
	private final List<StraightLeg> legs;
	private final IContributions contributions;
	private final SafeProblemSpace problemSpace;
	private final boolean useAlterings;
	
	public RoutesFitnessEvaluator(List<StraightLeg> legs, boolean useAlterings, 
			IContributions contributions, SafeProblemSpace problemSpace)
	{
		super();
		this.useAlterings = useAlterings;
		this.legs = legs;
		this.contributions = contributions;
		this.problemSpace = problemSpace;
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
						error += calculateContributionsScore(route);
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
		if (useAlterings)
		{
			for (int i = 0; i < candidate.size() - 1; i++)
			{
				StraightRoute prev = candidate.get(i);
				StraightRoute next = candidate.get(i + 1);
				AlteringRoute route = new AlteringRoute("", prev.getEndPoint(), prev.getEndTime(), next.getStartPoint(), next.getStartTime());
				route.generateSegments(problemSpace.getBoundedStatesBetween(prev.getEndTime(), next.getStartTime()));
				route.constructRoute(prev, next);
				error += calculateContributionsScore(route);
				error += calculateAlteringRouteScore(route, prev, next);
			}
		}
		return error;
	}
	
	public double calculateContributionsScore(CoreRoute route)
	{
		double score = 0;
		for (BaseContribution contribution : contributions)
		{
			score += contribution.calculateErrorScoreFor(route);
		}
		route.setScore(score);
		return score;
	}
	
	public double calculateAlteringRouteScore(AlteringRoute route, StraightRoute previous, StraightRoute next)
	{
		double startSpeed = previous.getSpeed();
		double endSpeed = next.getSpeed();
		double minAlteringSpeed = route.getMinSpeed();
		double maxAlteringSpeed = route.getMaxSpeed();
		double error = 0;
		if (route.getExtremumsCount() == 0) 
		{
			error = 0;
		} 
		else if (route.getExtremumsCount() == 1) 
		{
			double min = Math.min(startSpeed, endSpeed);
			double max = Math.max(startSpeed, endSpeed);
			if (minAlteringSpeed < min) 
			{				
				error += alteringSpeedError(min - minAlteringSpeed);
			}
			if (maxAlteringSpeed > max) 
			{
				error += alteringSpeedError(maxAlteringSpeed - max);
			}			
		} 
		else 
		{
			error += 1.5 * alteringSpeedError(maxAlteringSpeed - minAlteringSpeed);
		}
		return error;
	}
	
	public double alteringSpeedError(double speedDiff)
	{
		double x = 1 + speedDiff / problemSpace.getVehicleType().getMaxSpeed();
		return x * x - 1; 
	}
	
	@Override
	public boolean isNatural()
	{
		return false;
	}
}
