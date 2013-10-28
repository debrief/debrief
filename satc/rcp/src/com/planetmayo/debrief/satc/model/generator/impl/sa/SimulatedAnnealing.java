package com.planetmayo.debrief.satc.model.generator.impl.sa;

import java.util.Random;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class SimulatedAnnealing implements Callable<CoreRoute>
{
	private final IProgressMonitor progressMonitor;
	private final IContributions contributions;
	private final StraightLeg leg;
	private final SafeProblemSpace problemSpace;
	private final Random rnd;
	private final SAParameters parameters;
	
	private volatile PointsGenerator start;
	private volatile PointsGenerator end;
	
	public SimulatedAnnealing(IProgressMonitor progressMonitor, SAParameters parameters, 
			StraightLeg leg, IContributions contributions, SafeProblemSpace problemSpace, Random rnd)
	{
		this.progressMonitor = progressMonitor;
		this.contributions = contributions;
		this.leg = leg;
		this.problemSpace = problemSpace;
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
		return sum;
	}	
	
	public CoreRoute simulateAnnealing() throws InterruptedException
	{
		double min = Double.MAX_VALUE;
		CoreRoute result = null;
	
		CoreRoute current = null;
		double eCurrent = 0;
		for (int k = 0; k < parameters.getIterationsInThread(); k++)
		{
			if (k == 0 || ! parameters.isJoinedIterations()) 
			{
				current = leg.createRoute("", start.startPoint(),	end.startPoint());
				eCurrent = error(current);
			}
			
			double t = parameters.getStartTemperature();
			int i = 0;
			while (t > parameters.getEndTemperature())
			{
				CoreRoute newRoute;
				while (true) {
					if (progressMonitor.isCanceled()) 
					{
						throw new InterruptedException();
					}
					newRoute = leg.createRoute("",
						start.newPoint(current.getStartPoint(), t),
						end.newPoint(current.getEndPoint(), t));				
					leg.decideAchievableRoute(newRoute);
					if (newRoute.isPossible()) 
					{
						break;
					}
				}
				double eNew = error(newRoute);
				if (eNew > eCurrent)
				{
					double h = parameters.getSaFunctions()
							.probabilityToAcceptWorse(parameters, t, eCurrent, eNew);
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
		return new SimulatedAnnealing(progressMonitor, parameters, leg, contributions, problemSpace, rnd);
	}
}
