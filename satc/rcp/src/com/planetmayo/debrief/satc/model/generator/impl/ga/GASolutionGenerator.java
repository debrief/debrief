package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.ElapsedTime;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.impl.AbstractSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.jobs.Job;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class GASolutionGenerator extends AbstractSolutionGenerator
{
	private static final String GA_GENERATOR_GROUP = "gaGeneratorGroup"; 
	
	public static final String NAME = "Genetic Algorithm";
	
	private volatile List<StraightLeg> straightLegs;

	private volatile Job<Void, Void> mainJob;
	
	private final GAParameters parameters;
	
	public GASolutionGenerator(IContributions contributions, IJobsManager jobsManager, SafeProblemSpace problemSpace) 
	{
		super(contributions, jobsManager, problemSpace);
		parameters = new GAParameters();
		parameters.setElitizm(10);
		parameters.setMutationProbability(0.25);
		parameters.setPopulationSize(70);
		parameters.setStagnationSteps(20);
		parameters.setTopRoutes(10);
		parameters.setTimeoutBetweenIterations(0);
		parameters.setTimeout(30000);
		parameters.setUseAlteringLegs(true);
	}
	
	public GAParameters getParameters()
	{
		return parameters;
	}
	
	protected IContributions getContributions() 
	{
		return contributions;
	}

	@Override
	public void clear()
	{
		Job<?, ?> job = mainJob;
		if (job != null)
		{
			jobsManager.cancelGroup(GA_GENERATOR_GROUP);
			try
			{
				jobsManager.waitFor(job);
			}
			catch (InterruptedException ex)
			{
				LogFactory.getLog().error("Thread was interrupted", ex);
			}
		}
		if (straightLegs != null)
		{
			if (straightLegs != null)
			{
				straightLegs.clear();
				straightLegs = null;
			}
		}
	}

	@Override
	public synchronized void generateSolutions(boolean fullRerun)
	{		
		if (mainJob != null)
		{
			return;
		}
		fireStartingGeneration();
		Job<Void, Void> previous = null;
		if (fullRerun || straightLegs == null) 
		{
			previous = jobsManager.schedule(new Job<Void, Void>("Generate Legs", GA_GENERATOR_GROUP)
				{

				@Override
				protected <E> Void run(IProgressMonitor monitor, Job<Void, E> previous)
						throws InterruptedException
					{
					List<CoreLeg> rawLegs = getTheLegs(problemSpaceView.states(), monitor);
					straightLegs = new ArrayList<StraightLeg>();
					for (CoreLeg leg : rawLegs)
					{
						if (leg.getType() == LegType.STRAIGHT) 
						{
							straightLegs.add((StraightLeg) leg);
						}
					}					
					return null;
				}
			});
		}
		mainJob = jobsManager.scheduleAfter(new Job<Void, Void>("Calculate GA", GA_GENERATOR_GROUP)
		{

			@Override
			protected <E> Void run(IProgressMonitor monitor, Job<Void, E> previous)
					throws InterruptedException
			{
				runGA(monitor);
				return null;
			}

			@Override
			protected void onComplete()
			{
				synchronized (GASolutionGenerator.this) 
				{
					mainJob = null;
				}
				fireFinishedGeneration(getException());
			}
						
		}, previous);
		if (mainJob != null && mainJob.isComplete()) 
		{
			mainJob = null;
		}
	}
	
	private void runGA(final IProgressMonitor progressMonitor) throws InterruptedException
	{	
		Random rng = new MersenneTwisterRNG();
		final RCPIslandEvolution engine = new RCPIslandEvolution(
				this,
				straightLegs,
				4,
				rng
		);
		TerminationCondition progressMonitorCondition = new TerminationCondition()
		{			
			@Override
			public boolean shouldTerminate(PopulationData<?> populationData)
			{
				return progressMonitor.isCanceled();
			}
		};
		List<StraightRoute> solution = engine.evolve(
				parameters.getPopulationSize(), 
				parameters.getElitizm(),
				20,
				1,
				progressMonitorCondition,
				new ElapsedTime(parameters.getTimeout()),
				new Stagnation(parameters.getStagnationSteps())
				//new TargetFitness(0.07, false)
		);
		if (progressMonitor.isCanceled())
		{
			throw new InterruptedException();
		}
		fireSolutionsReady(new CompositeRoute[] {solutionToRoute(solution, true)});
	}
	
	protected CompositeRoute solutionToRoute(List<StraightRoute> solution, boolean createAltering) 
	{
		@SuppressWarnings({"rawtypes", "unchecked"})
		List<CoreRoute> routes = (List) solution;
		if (createAltering)
		{
			routes = generateAlteringRoutes(routes);
		}
		return new CompositeRoute(routes);
		
	}
	
	@Override
	public void cancel()
	{
		Job<?, ?> job = mainJob;
		if (job != null)
		{
			jobsManager.cancelGroup(GA_GENERATOR_GROUP);
		}		
	}
	
	@Override
	public void setPrecision(Precision precision)
	{
		super.setPrecision(precision);
		if (straightLegs != null) 
		{
			generateSolutions(true);
		}
	}	
	
	protected void fireIterationComputed(List<CompositeRoute> topRoutes) 
	{		
		for (IGenerateSolutionsListener listener : _readyListeners)
		{
			if (listener instanceof IGASolutionsListener) 
			{
				((IGASolutionsListener) listener).iterationComputed(new ArrayList<CompositeRoute>(topRoutes));
			}
		}		
	}
}
