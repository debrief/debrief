package com.planetmayo.debrief.satc.model.generator.impl.sa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
	
	
	public static final String NAME = "Simulated Annealing";

	
	private final SAParameters parameters;
	
	private volatile Job<Void, Void> mainJob;	
	
	private volatile List<CoreLeg> legs;
	
	public SASolutionGenerator(IContributions contributions,
			IJobsManager jobsManager, SafeProblemSpace problemSpace)
	{
		super(contributions, jobsManager, problemSpace);
		parameters = new SAParameters();
		parameters.setStartTemperature(2.0);
		parameters.setEndTemperature(0.03);
		parameters.setParallelThreads(4);
		parameters.setIterationsInThread(2);
		parameters.setJoinedIterations(true);
		parameters.setStartOnCenter(true);
		parameters.setSaFunctions(new SAFunctions()
		{
			@Override
			public double neighborDistance(SAParameters parameters, Random rnd, double T)
			{
				return Math.signum(rnd.nextDouble() - 0.5) * T *
						(Math.pow(1 + 1 / T, 2 * rnd.nextDouble() - 1) - 1);
			}
			
			@Override
			public double changeTemprature(SAParameters parameters, double T, int step)
			{
				return parameters.getStartTemperature() * Math.exp(-0.85 * Math.pow(step, 0.25));
			}

			@Override
			public double probabilityToAcceptWorse(SAParameters parameters, double T,	double eCur, double eNew)
			{
				if (T < 0.3) 
				{
					return 0;
				}
				return 1 / (1 + Math.exp((eNew - eCur) / T));
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
			generateLegs = jobsManager.schedule(new Job<Void, Void>("Generate legs", SA_GENERATOR_GROUP) {

				@Override
				protected <E> Void run(IProgressMonitor monitor, Job<Void, E> previous)
						throws InterruptedException
				{
					legs = getTheLegs(problemSpaceView.states(), monitor);
					return null;
				}
			});
		}
		mainJob = jobsManager.scheduleAfter(new Job<Void, Void>("Calculate SA", SA_GENERATOR_GROUP) {

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
	
	public SAParameters getParameters()
	{
		return parameters;
	}

	protected void runSA(IProgressMonitor monitor) throws InterruptedException
	{
		Job<List<CoreRoute>, Void>[] jobs = startSAJobs(monitor);
		List<CoreRoute> results = new ArrayList<CoreRoute>(jobs[0].getResult());
		int length = jobs.length;
		int legsCount = results.size();
		for (int i = 1; i < length; i++) 
		{
			List<CoreRoute> jobResult = jobs[i].getResult();
			for (int j = 0; j < legsCount; j++) 
			{
				if (results.get(j).getScore() > jobResult.get(j).getScore()) 
				{
					results.set(j, jobResult.get(j));
				}
			}
		}
		results = generateAlteringRoutes(results);
		new CompositeRoute(results);
		fireSolutionsReady(new CompositeRoute[] { new CompositeRoute(results) });
	}
	
	protected Job<List<CoreRoute>, Void>[] startSAJobs(IProgressMonitor monitor) throws InterruptedException 
	{
		@SuppressWarnings("unchecked")
		final Job<List<CoreRoute>, Void>[] jobs = new Job[parameters.getParallelThreads()];
		
		final Random rnd = new MersenneTwisterRNG();		
		final Semaphore semaphore = new Semaphore(-parameters.getParallelThreads() + 1);
		final AtomicBoolean hasException = new AtomicBoolean(false);
		
		for (int i = 0; i < parameters.getParallelThreads(); i++)
		{
			jobs[i] = jobsManager.schedule(new Job<List<CoreRoute>, Void>("SA job thread " + (i + 1), SA_GENERATOR_GROUP)
			{
				@Override
				protected <E> List<CoreRoute> run(IProgressMonitor monitor, Job<Void, E> previous)
						throws InterruptedException
				{					
					List<CoreRoute> result = new ArrayList<CoreRoute>();
					int legIndex = 1;
					for (CoreLeg leg : legs)
					{
						if (leg.getType() == LegType.STRAIGHT)
						{
							result.add(findRouteForLeg(monitor, (StraightLeg) leg, legIndex,
									rnd));
							legIndex++;
						}
					}
					return result;
				}

				@Override
				protected void onComplete()
				{
					if (! isFinishedCorrectly())
					{
						hasException.set(true);
					}
					semaphore.release();
				}
			});
		}
		while (! semaphore.tryAcquire(2, TimeUnit.SECONDS)) 
		{
			if (monitor.isCanceled() || hasException.get()) 
			{
				jobsManager.cancelGroup(SA_GENERATOR_GROUP);
				throw new InterruptedException();
			}
		}
		if (hasException.get()) 
		{
			throw new InterruptedException();			
		}
		return jobs;
	}
	
	protected CoreRoute findRouteForLeg(IProgressMonitor progressMonitor, StraightLeg leg, int legIndex, Random rnd) throws InterruptedException 
	{
		SimulatedAnnealing simulator = new SimulatedAnnealing(progressMonitor, parameters, leg, 
				contributions, rnd);
		CoreRoute min = null;
		boolean joined = parameters.isJoinedIterations();
		progressMonitor.beginTask("Leg " + legIndex, parameters.getIterationsInThread());
		for (int k = 0; k < parameters.getIterationsInThread(); k++)			
		{
			progressMonitor.worked(1);
			progressMonitor.subTask("Iteration " + (k + 1));
			CoreRoute newResult = simulator.simulateAnnealing(joined ? min : null);
			if (min == null || newResult.getScore() < min.getScore()) 
			{
				min = newResult;
			}
		}
		progressMonitor.done();
		return min;
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
