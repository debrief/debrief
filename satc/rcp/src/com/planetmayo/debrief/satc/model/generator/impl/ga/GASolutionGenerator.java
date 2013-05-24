package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.ListCrossover;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.ElapsedTime;
import org.uncommons.watchmaker.framework.termination.Stagnation;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.impl.AbstractSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.jobs.Job;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;
import com.vividsolutions.jts.geom.Point;

public class GASolutionGenerator extends AbstractSolutionGenerator
{
	private static final String GA_GENERATOR_GROUP = "gaGeneratorGroup"; 
	
	private volatile List<CoreLeg> legs;

	private volatile Job<Void, Void> mainJob;
	
	public GASolutionGenerator(IContributions contributions, IJobsManager jobsManager, SafeProblemSpace problemSpace) 
	{
		super(contributions, jobsManager, problemSpace);
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
		if (legs != null)
		{
			if (legs != null)
			{
				legs.clear();
				legs = null;
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
		if (fullRerun || legs == null) 
		{
			previous = jobsManager.schedule(new Job<Void, Void>("Generate Legs", GA_GENERATOR_GROUP)
				{

				@Override
				protected <E> Void run(IProgressMonitor monitor, Job<Void, E> previous)
						throws InterruptedException
					{
					legs = getTheLegs(problemSpaceView.states(), monitor);
					for (CoreLeg leg : legs)
					{
						leg.generatePoints(_myPrecision);
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
		List<EvolutionaryOperator<List<Point>>> operators = new ArrayList<EvolutionaryOperator<List<Point>>>();
		operators.add(new ListCrossover<Point>(new NumberGenerator<Integer>()
		{
			Random rng = new MersenneTwisterRNG();

			@Override
			public Integer nextValue()
			{
				return rng.nextInt(2) + 1;
			}			
		}));
		operators.add(new PointsMutation(legs, new Probability(0.15)));
		
		EvolutionEngine<List<Point>> engine = new GenerationalEvolutionEngine<List<Point>>(
				new RoutesCandidateFactory(legs), 
				new EvolutionPipeline<List<Point>>(operators),
				new RoutesFitnessEvaluator(legs, contributions),
				new RouletteWheelSelection(), 				
				new MersenneTwisterRNG()
		);
		TerminationCondition progressMonitorCondition = new TerminationCondition()
		{			
			@Override
			public boolean shouldTerminate(PopulationData<?> populationData)
			{
				return progressMonitor.isCanceled();
			}
		}; 
		List<Point> solution = engine.evolve(500, 30, new Stagnation(50, false), new ElapsedTime(30000), progressMonitorCondition);
		if (progressMonitor.isCanceled())
		{
			throw new InterruptedException();
		}
		
		List<CoreRoute> routes = new ArrayList<CoreRoute>();
		int i = 0;
		for (CoreLeg leg : legs)
		{
			if (leg.getType() == LegType.STRAIGHT) 
			{
				routes.add(leg.createRoute("", solution.get(i), solution.get(i + 1)));
			}
			i += 2;
		}
		fireSolutionsReady(new CompositeRoute[] {new CompositeRoute(routes)});
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
		if (legs != null) 
		{
			generateSolutions(true);
		}
	}	
}
