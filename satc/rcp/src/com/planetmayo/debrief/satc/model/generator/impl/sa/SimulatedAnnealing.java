package com.planetmayo.debrief.satc.model.generator.impl.sa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class SimulatedAnnealing implements Callable<CoreRoute>
{
	private final IContributions contributions;
	private final StraightLeg leg;
	private final SafeProblemSpace problemSpace;
	private final Random rnd;
	private final int iterations;
	
	private volatile PointsGenerator start;
	private volatile PointsGenerator end;
	private volatile List<BoundedState> states;
	
	public SimulatedAnnealing(StraightLeg leg, int iterations, 
			IContributions contributions, SafeProblemSpace problemSpace, Random rnd)
	{
		this.contributions = contributions;
		this.leg = leg;
		this.problemSpace = problemSpace;
		this.rnd = rnd;
		this.iterations = iterations;
		
		initialize();
	}
	
	protected void initialize() 
	{
		Collection<BoundedState> states = problemSpace.getBoundedStatesBetween(leg.getFirst().getTime(), leg.getLast().getTime());
		
		start = new PointsGenerator(leg.getFirst().getLocation().getGeometry(), rnd);
		end = new PointsGenerator(leg.getLast().getLocation().getGeometry(), rnd);
		this.states = new ArrayList<BoundedState>(states);
	}
	
	protected double error(CoreRoute route) 
	{
		double sum = 0;
		for (BaseContribution contribution : contributions)			
		{
			sum += contribution.calculateErrorScoreFor(route);
		}
		return sum;
	}	
	
	public CoreRoute simulateAnnealing() 
	{
		double min = Double.MAX_VALUE;
		CoreRoute result = null;
	
		for (int k = 0; k < iterations; k++)
		{
			CoreRoute current = leg.createRoute("", start.startPoint(),
					end.startPoint());
			current.generateSegments(states);
			
			double eCurrent = error(current);
			
			double t = 2.0;
			int i = 0;
			while (t > 0.1)
			{
				CoreRoute newRoute;
				while (true) {
					newRoute = leg.createRoute("",
						start.newPoint(current.getStartPoint(), t),
						end.newPoint(current.getEndPoint(), t));				
					newRoute.generateSegments(states);
					leg.decideAchievableRoute(newRoute);
					if (newRoute.isPossible()) 
					{
						break;
					}
				}
				double eNew = error(newRoute);
				if (eNew > eCurrent)
				{
					double h = 1 - 1 / (1 + Math.exp(1 / Math.pow(t, 3)));
					if (rnd.nextDouble() > h)
					{
						current = newRoute;
						eCurrent = eNew;
					}
				}
				else
				{
					current = newRoute;
					eCurrent = eNew;
					if (min > eNew) 
					{
						min = eNew;
						result = newRoute;
					}
				}
				i++;
				t = 2.0 * Math.exp(-1.1 * Math.pow(i, 0.18));
			}
		}
		result.setScore(min);
		return result;
	}
	
	@Override
	public CoreRoute call() throws Exception
	{
		return simulateAnnealing();
	}

	@Override
	public SimulatedAnnealing clone() {
		return new SimulatedAnnealing(leg, iterations, contributions, problemSpace, rnd);
	}
}
