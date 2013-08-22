package com.planetmayo.debrief.satc.model.generator.impl.sa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.core.runtime.IProgressMonitor;
import org.uncommons.maths.random.MersenneTwisterRNG;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.impl.AbstractSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.jobs.Job;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class SASolutionGenerator extends AbstractSolutionGenerator
{
	private static final String SA_GENERATOR_GROUP = "saGeneratorGroup";
	
	private final SAParameters parameters;
	
	private volatile Job<Void, Void> mainJob;	
	
	private volatile List<CoreLeg> legs;
	
	public SASolutionGenerator(IContributions contributions,
			IJobsManager jobsManager, SafeProblemSpace problemSpace)
	{
		super(contributions, jobsManager, problemSpace);
		parameters = new SAParameters();
		parameters.setStartTemprature(2.0);
		parameters.setEndTemprature(0.1);
		parameters.setParallelThreads(4);
		parameters.setIterationsInThread(2);
		parameters.setJoinedIterations(false);
		parameters.setStartOnCenter(true);
		parameters.setSaFuntions(new SAFunctions()
		{
			
			@Override
			public double neighborDistance(SAParameters parameters, Random rnd, double T)
			{
				return Math.pow(rnd.nextDouble(), 5 - T);
			}
			
			@Override
			public double changeTemprature(SAParameters parameters, double T, int step)
			{
				return parameters.getStartTemprature() * Math.exp(-1.1 * Math.pow(step, 0.18));
			}

			@Override
			public double probabilityToAcceptWorse(SAParameters parameters, double T,	double eCur, double eNew)
			{
				return 1 / (1 + Math.exp(1 / Math.pow(T, 3)));
			}
			
			
		});
	}

	@Override
	public void clear()
	{
		Job<?, ?> job = mainJob;
		if (job != null)
		{
			jobsManager.cancelGroup(SA_GENERATOR_GROUP);
			try
			{
				jobsManager.waitFor(job);
			}
			catch (InterruptedException ex)
			{
				LogFactory.getLog().error("Thread was interrupted", ex);
			}
		}
		if (legs != null)
		{
			legs.clear();
			legs = null;
		}
	}

	@Override
	public synchronized void generateSolutions(boolean fullRerun)
	{
		Job<Void, Void> generateLegs = null;
		fireStartingGeneration();
		if (fullRerun || legs == null)
		{
			generateLegs = jobsManager.schedule(new Job<Void, Void>("Generate legs") {

				@Override
				protected <E> Void run(IProgressMonitor monitor, Job<Void, E> previous)
						throws InterruptedException
				{
					legs = getTheLegs(problemSpaceView.states(), monitor);
					return null;
				}
			});
		}
		mainJob = jobsManager.scheduleAfter(new Job<Void, Void>("Calculate SA") {

			@Override
			protected <E> Void run(IProgressMonitor monitor, Job<Void, E> previous)
					throws InterruptedException
			{
				runSA(monitor);
				return null;
			}

			@Override
			protected void onComplete()
			{
				synchronized (SASolutionGenerator.this) 
				{
					mainJob = null;
				}
				fireFinishedGeneration(getException());
			}
		}, generateLegs);		
	}
	
	protected void runSA(IProgressMonitor monitor) 
	{
		Random rnd = new MersenneTwisterRNG();		
		ExecutorService executor = Executors.newFixedThreadPool(parameters.getParallelThreads());
		try 
		{
			List<CoreRoute> routes = new ArrayList<CoreRoute>();
			for (CoreLeg leg : legs)
			{
				if (leg.getType() == LegType.STRAIGHT)
				{
					routes.add(findRouteForLeg((StraightLeg) leg, rnd, executor));
				}
			}
			routes = generateAlteringRoutes(routes);
			fireSolutionsReady(new CompositeRoute[] { new CompositeRoute(routes) });
		}
		finally 
		{
			executor.shutdownNow();
		}
	}
	
	protected CoreRoute findRouteForLeg(StraightLeg leg, Random rnd, ExecutorService executor) 
	{
		SimulatedAnnealing simulator = new SimulatedAnnealing(parameters, leg, contributions, problemSpaceView, rnd);
		List<Future<CoreRoute>> results = new ArrayList<Future<CoreRoute>>(parameters.getParallelThreads());
		for (int i = 0; i < parameters.getParallelThreads(); i++) 
		{
			results.add(executor.submit(simulator.clone()));
		}
		try 
		{
			CoreRoute min = results.get(0).get();
			for (Future<CoreRoute> result : results) 
			{
				CoreRoute current = result.get();
				if (current.getScore() < min.getScore())
				{
					min = current;
				}
			}
			return min;
		}
		catch (ExecutionException ex)
		{
			LogFactory.getLog().error("Problem in SA thread", ex);					
		}		
		catch (InterruptedException ex)
		{
			LogFactory.getLog().error("SA thread was interrupted", ex);				
		}
		return null;
	}

	@Override
	public void cancel()
	{
		Job<?, ?> job = mainJob;
		if (job != null)
		{
			jobsManager.cancelGroup(SA_GENERATOR_GROUP);
		}		
	}
}
