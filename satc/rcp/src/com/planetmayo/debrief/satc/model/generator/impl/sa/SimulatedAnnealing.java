package com.planetmayo.debrief.satc.model.generator.impl.sa;

import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;

public class SimulatedAnnealing
{
	private final IProgressMonitor progressMonitor;
	private final IContributions contributions;
	private final StraightLeg leg;
	private final Random rnd;
	private final SAParameters parameters;
	
	private volatile PointsGenerator start;
	private volatile PointsGenerator end;
	
	public SimulatedAnnealing(IProgressMonitor progressMonitor, SAParameters parameters, 
			StraightLeg leg, IContributions contributions, Random rnd)
	{
		this.progressMonitor = progressMonitor;
		this.contributions = contributions;
		this.leg = leg;
		this.rnd = rnd;
		this.parameters = parameters;
		
		initialize();
	}
	
	protected void initialize() 
	{
		start = new PointsGenerator(leg.getFirst().getLocation().getGeometry(), rnd, parameters);
		end = new PointsGenerator(leg.getLast().getLocation().getGeometry(), rnd, parameters);
	}
	
	protected double error(CoreRoute route) 
	{
		double sum = 0;
		for (BaseContribution contribution : contributions)			
		{
			sum += contribution.calculateErrorScoreFor(route);
		}
		route.setScore(sum);
		return sum;
	}	
	
	public CoreRoute simulateAnnealing(CoreRoute startRoute) throws InterruptedException
	{
		double min = Double.MAX_VALUE;
		CoreRoute result = null;
	
		CoreRoute current = startRoute;
		if (current == null) 
		{
			current = leg.createRoute(start.startPoint(),	end.startPoint(), null);
		}
		double eCurrent = error(current);
			
		double t = parameters.getStartTemperature();
		int i = 0;
		while (t > parameters.getEndTemperature())
		{
			CoreRoute newRoute;
			while (true)
			{
				if (progressMonitor.isCanceled())
				{
					throw new InterruptedException();
				}
				newRoute = leg.createRoute(
						start.newPoint(current.getStartPoint(), t),
						end.newPoint(current.getEndPoint(), t),
						null);
				leg.decideAchievableRoute(newRoute);
				if (newRoute.isPossible())
				{
					break;
				}
			}
			double eNew = error(newRoute);
			if (eNew > eCurrent)
			{
				double h = parameters.getSaFunctions().probabilityToAcceptWorse(parameters, t, eCurrent, eNew);
				if (rnd.nextDouble() < h)
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
			t = parameters.getSaFunctions().changeTemprature(parameters, t, i);
		}
		result.setScore(min);
		return result;
	}
}
