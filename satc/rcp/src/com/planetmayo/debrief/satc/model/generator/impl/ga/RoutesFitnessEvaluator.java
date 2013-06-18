package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.vividsolutions.jts.geom.Point;

public class RoutesFitnessEvaluator implements FitnessEvaluator<List<Point>>
{	
	private final List<LegOperations> legs;
	private final IContributions contributions;
	
	public RoutesFitnessEvaluator(List<LegOperations> legs, IContributions contributions)
	{
		super();
		this.legs = legs;
		this.contributions = contributions;
	}

	@Override
	public double getFitness(List<Point> candidate,	List<? extends List<Point>> population)
	{
		List<CoreRoute> candidateRoutes = new ArrayList<CoreRoute>(legs.size());
		int length = candidate.size();
		Iterator<LegOperations> legsIterator = legs.iterator();
		
		double error = 0;
		for (int i = 0; i < length; i += 2)
		{
			LegOperations leg = legsIterator.next();
			// brute force algorithm doesn't use altering legs to detect achievable routes, 
			// we won't use them too
			if (leg.getLeg().getType() == LegType.STRAIGHT)
			{
				CoreRoute route = leg.getLeg().createRoute("", candidate.get(i),
						candidate.get(i + 1));
				leg.getLeg().decideAchievableRoute(route);
				boolean possible = route.isPossible();
				if (possible)
				{
					candidateRoutes.add(route);
				}
				else
				{
					error = Double.MAX_VALUE;;
				}
				leg.collectStart(route.getStartPoint(), possible);
				leg.collectEnd(route.getEndPoing(), possible);
			}
		}
		if (error == Double.MAX_VALUE)
		{
			return error;
		}
		for (CoreRoute route : candidateRoutes)
		{
			for (BaseContribution contribution : contributions)
			{
				error += contribution.calculateErrorScoreFor(route);
			}
		}
		return error;
	}

	@Override
	public boolean isNatural()
	{
		return false;
	}
}
