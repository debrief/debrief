package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.islands.IslandEvolution;
import org.uncommons.watchmaker.framework.islands.RingMigration;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.ListCrossover;
import org.uncommons.watchmaker.framework.operators.SplitEvolution;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
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
		parameters.setStagnationSteps(250);
		parameters.setTopRoutes(10);
		parameters.setTimeoutBetweenIterations(0);
		parameters.setTimeout(30000);
		parameters.setRecalculatePointsProbs(10);
		parameters.setCheckReachability(25);
		parameters.setExtendBestPoints(33);
	}
	
	public GAParameters getParameters()
	{
		return parameters;
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
							leg.generatePoints(_myPrecision.getNumPoints());
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
		List<EvolutionaryOperator<List<StraightRoute>>> operators = new ArrayList<EvolutionaryOperator<List<StraightRoute>>>();
		
		PointsCrossover pointsCrossover = new PointsCrossover(straightLegs, new Probability(1));
		SplitEvolution<List<StraightRoute>> crossovers = new SplitEvolution<List<StraightRoute>>(
				new ListCrossover<StraightRoute>(),
				pointsCrossover,
				0.4
		);
		PointsMutationToVertexes mutation = new PointsMutationToVertexes(straightLegs, new Probability(parameters.getMutationProbability()), 20);
		operators.add(crossovers);
		operators.add(mutation);
		
		/*final GAEngine engine = new GAEngine(
				new RoutesCandidateFactory(straightLegs), 
				new EvolutionPipeline<List<StraightRoute>>(operators),
				new RoutesFitnessEvaluator(straightLegs, contributions),
				new TournamentSelection(new Probability(1)), 				
				rng
		);*/
		final IslandEvolution<List<StraightRoute>> engine = new IslandEvolution<List<StraightRoute>>(
				4,
				new RingMigration(), 
				new RoutesCandidateFactory(straightLegs), 
				new EvolutionPipeline<List<StraightRoute>>(operators),
				new RoutesFitnessEvaluator(straightLegs, contributions),
				new TournamentSelection(new Probability(1)), 				
				rng
		);
		engine.addEvolutionObserver(mutation);
		engine.addEvolutionObserver(pointsCrossover);
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
				5,
				progressMonitorCondition,
				new ElapsedTime(parameters.getTimeout()),
				progressMonitorCondition,
				new Stagnation(parameters.getStagnationSteps())
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
	
	private class GAEngine extends GenerationalEvolutionEngine<List<StraightRoute>> 
	{
		private double topRoutesScore;
		
		public GAEngine(CandidateFactory<List<StraightRoute>> candidateFactory,
				EvolutionaryOperator<List<StraightRoute>> evolutionScheme,
				FitnessEvaluator<? super List<StraightRoute>> fitnessEvaluator,
				SelectionStrategy<? super List<StraightRoute>> selectionStrategy, Random rng)
		{
			super(candidateFactory, evolutionScheme, fitnessEvaluator, selectionStrategy,
					rng);
		}
		
		public double getTopRoutesScore()
		{
			return topRoutesScore;
		}

		@Override
		protected List<EvaluatedCandidate<List<StraightRoute>>> nextEvolutionStep(
				List<EvaluatedCandidate<List<StraightRoute>>> evaluatedPopulation, int eliteCount,
				Random rng)
		{			
			List<EvaluatedCandidate<List<StraightRoute>>> result = super.nextEvolutionStep(evaluatedPopulation, eliteCount, rng);
			EvolutionUtils.sortEvaluatedPopulation(result, false);
			List<CompositeRoute> routes = new ArrayList<CompositeRoute>(parameters.getTopRoutes());
			for (int i = 0; i < parameters.getTopRoutes(); i++) 
			{
				if (i >= result.size()) 
				{
					break;
				}
				routes.add(solutionToRoute(result.get(i).getCandidate(), false));
			}
			fireIterationComputed(routes);
			if (parameters.getTimeoutBetweenIterations() > 0) 
			{
				try 
				{
					Thread.sleep(parameters.getTimeoutBetweenIterations());
				}
				catch (InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				}
			}
			int topCounts = Math.min(15, result.size());
			topRoutesScore = 0;
			for (int i = 0; i < topCounts; i++) 
			{
				if (result.get(i).getFitness() == Double.MAX_VALUE) 
				{
					topRoutesScore = Double.MAX_VALUE;
					break;
				}
				topRoutesScore += result.get(i).getFitness();
			}
			if (topRoutesScore != Double.MAX_VALUE) 
			{
				topRoutesScore = topRoutesScore / topCounts;
			}			
			return result;
		}	
	}
}
