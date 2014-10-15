/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.ListCrossover;
import org.uncommons.watchmaker.framework.operators.SplitEvolution;
import org.uncommons.watchmaker.framework.selection.SigmaScaling;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;

public class RCPIslandEvolution
{
	private final GASolutionGenerator solutionGenerator;
	private final List<StraightLeg> straightLegs;
	private final Random rng;
	private final List<EvolutionEngine<List<StraightRoute>>> islands;
	private final EvolutionEngine<List<StraightRoute>> continent;

	private final Set<EvolutionEngine<List<StraightRoute>>> finishedIslands =
			Collections
					.synchronizedSet(new HashSet<EvolutionEngine<List<StraightRoute>>>());

	private List<TerminationCondition> satisfiedTerminationConditions;

	public RCPIslandEvolution(GASolutionGenerator solutionGenerator,
			List<StraightLeg> straightLegs, int islandCount, Random rng)
	{
		this.solutionGenerator = solutionGenerator;
		this.straightLegs = straightLegs;
		this.rng = rng;
		islands = new ArrayList<EvolutionEngine<List<StraightRoute>>>(islandCount);
		for (int i = 0; i < islandCount; i++)
		{
			islands.add(createIsland(i % 2 != 0));
		}
		continent = createContinient();
	}

	protected EvolutionEngine<List<StraightRoute>> createIsland(
			boolean useAlterings)
	{
		List<EvolutionaryOperator<List<StraightRoute>>> operators =
				new ArrayList<EvolutionaryOperator<List<StraightRoute>>>();
		PointsCrossover pointsCrossover =
				new PointsCrossover(straightLegs, new Probability(1));

		AdaptivePointMutation adaptiveMutation;
		RoutesCandidateFactory factory = new RoutesCandidateFactory(straightLegs);
		SplitEvolution<List<StraightRoute>> crossovers =
				new SplitEvolution<List<StraightRoute>>(
						new ListCrossover<StraightRoute>(), pointsCrossover, 0.4);
		SplitEvolution<List<StraightRoute>> mutatuions =
				new SplitEvolution<List<StraightRoute>>(adaptiveMutation =
						new AdaptivePointMutation(straightLegs, new Probability(0.3)),
						new RandomMutation(straightLegs, new Probability(0.4), factory),
						0.7);
		operators.add(crossovers);
		operators.add(mutatuions);

		final GenerationalEvolutionEngine<List<StraightRoute>> island =
				new GenerationalEvolutionEngine<List<StraightRoute>>(
						new RoutesCandidateFactory(straightLegs),
						new EvolutionPipeline<List<StraightRoute>>(operators),
						new RoutesFitnessEvaluator(straightLegs, useAlterings
								&& solutionGenerator.getParameters().isUseAlteringLegs(),
								solutionGenerator.getContributions(),
								solutionGenerator.getProblemSpace()), new SigmaScaling(), rng);
		island.setSingleThreaded(true);
		island.addEvolutionObserver(adaptiveMutation);
		island.addEvolutionObserver(new EvolutionObserver<List<StraightRoute>>()
		{

			@Override
			public void populationUpdate(
					PopulationData<? extends List<StraightRoute>> data)
			{
				if (data.getFitnessStandardDeviation() < 0.001)
				{
					finishedIslands.add(island);
				}
			}
		});
		return island;
	}

	protected EvolutionEngine<List<StraightRoute>> createContinient()
	{
		List<EvolutionaryOperator<List<StraightRoute>>> operators =
				new ArrayList<EvolutionaryOperator<List<StraightRoute>>>();
		PointsCrossover pointsCrossover =
				new PointsCrossover(straightLegs, new Probability(1));
		SplitEvolution<List<StraightRoute>> crossovers =
				new SplitEvolution<List<StraightRoute>>(
						new ListCrossover<StraightRoute>(), pointsCrossover, 0.4);
		AdaptivePointMutation mutation =
				new AdaptivePointMutation(straightLegs, new Probability(0.25));
		operators.add(crossovers);
		operators.add(mutation);

		final GenerationalEvolutionEngine<List<StraightRoute>> continent =
				new GenerationalEvolutionEngine<List<StraightRoute>>(
						new RoutesCandidateFactory(straightLegs),
						new EvolutionPipeline<List<StraightRoute>>(operators),
						new RoutesFitnessEvaluator(straightLegs, solutionGenerator
								.getParameters().isUseAlteringLegs(),
								solutionGenerator.getContributions(),
								solutionGenerator.getProblemSpace()), new SigmaScaling(), rng);
		continent.setSingleThreaded(true);
		continent.addEvolutionObserver(pointsCrossover);
		continent.addEvolutionObserver(mutation);
		return continent;
	}

	public List<StraightRoute> evolve(int populationSize, int eliteCount,
			int epochLength, int migrantCount, TerminationCondition... conditions)
	{
		// allocate one thread per island
		ExecutorService threadPool =
				Executors.newFixedThreadPool(islands.size() + 1);
		
		// create the empty islands
		List<List<List<StraightRoute>>> islandPopulations =
				new ArrayList<List<List<StraightRoute>>>(islands.size() + 1);
		
		// give each island an empty set of routes
		for (int i = 0; i <= islands.size(); i++)
		{
			islandPopulations.add(new ArrayList<List<StraightRoute>>());
		}

		// initial data
		PopulationData<List<StraightRoute>> data = null;
		List<TerminationCondition> satisfiedConditions = null;
		int currentEpochIndex = 0;
		long startTime = System.currentTimeMillis();
		
		// keep looping until a TerminationCondition is successful
		while (satisfiedConditions == null)
		{
			List<Callable<List<EvaluatedCandidate<List<StraightRoute>>>>> epochs =
					createEpochTasks(populationSize, eliteCount, epochLength,
							islandPopulations);
			try
			{
				// get each island to do an evolution, in its own thread
				List<Future<List<EvaluatedCandidate<List<StraightRoute>>>>> futures =
						threadPool.invokeAll(epochs);
				
				// create a placeholder for the finished population from each island
				List<List<EvaluatedCandidate<List<StraightRoute>>>> evaluatedPopulations =
						new ArrayList<List<EvaluatedCandidate<List<StraightRoute>>>>(
								islands.size());

				// loop through the evolution tasks 
				for (Future<List<EvaluatedCandidate<List<StraightRoute>>>> future : futures)
				{
					//  
					List<EvaluatedCandidate<List<StraightRoute>>> evaluatedIslandPopulation =
							future.get();
					
					// ok, store this island's population
					evaluatedPopulations.add(evaluatedIslandPopulation);
				}

				// get an overview of the best population
				data =
						EvolutionUtils.getPopulationData(evaluatedPopulations.get(0),
								false, eliteCount, currentEpochIndex, startTime);
				
				// clear this set of generations
				islandPopulations.clear();

				// create a new list, using the 
				for (List<EvaluatedCandidate<List<StraightRoute>>> evaluatedPopulation : evaluatedPopulations)
				{
					islandPopulations.add(toCandidateList(evaluatedPopulation));
				}

				// migrate the best individuals from the simple/advanced islands
				// to the Elite Island
				migrate(islandPopulations);

				// increment the epoch (generation counter)
				++currentEpochIndex;

				ArrayList<CompositeRoute> compositeRoutes =
						new ArrayList<CompositeRoute>();
				for (int i = 0; i < Math.min(40, islandPopulations.get(0).size()); i++)
				{
					compositeRoutes.add(solutionGenerator.solutionToRoute(
							islandPopulations.get(0).get(i), false));
				}
				
				// broadcast the completed routes
				solutionGenerator.fireIterationComputed(compositeRoutes, data.getBestCandidateFitness());
				
				// create a fresh set of islands
				renewIslands(islandPopulations);
			}
			catch (InterruptedException ex)
			{
				Thread.currentThread().interrupt();
			}
			catch (ExecutionException ex)
			{
				throw new IllegalStateException(ex);
			}
			satisfiedConditions = EvolutionUtils.shouldContinue(data, conditions);
		}
		threadPool.shutdownNow();

		this.satisfiedTerminationConditions = satisfiedConditions;
		return islandPopulations.get(0).get(0);
	}

	private void migrate(List<List<List<StraightRoute>>> populations)
	{
		List<List<StraightRoute>> elitePopulation = populations.get(0);
		int index = Math.min(70, elitePopulation.size()) - 1;
		for (int i = 1; i < populations.size(); i++)
		{
			List<List<StraightRoute>> islandPopulation = populations.get(i);
			for (int j = 0; j < 5; j++, index--)
			{
				elitePopulation.set(index, islandPopulation.get(j));
			}
		}
	}

	private void renewIslands(List<List<List<StraightRoute>>> populations)
	{
		for (EvolutionEngine<List<StraightRoute>> island : finishedIslands)
		{
			int index = islands.indexOf(island);
			if (index > -1)
			{
				islands.set(index, createIsland(index % 2 != 0));
				populations.set(index + 1, new ArrayList<List<StraightRoute>>());
			}
		}
		finishedIslands.clear();
	}

	/**
	 * Create the concurrently-executed tasks that perform evolution on each
	 * island.
	 */
	private List<Callable<List<EvaluatedCandidate<List<StraightRoute>>>>> createEpochTasks(
			int populationSize, int eliteCount, int epochLength,
			List<List<List<StraightRoute>>> populations)
	{
		TerminationCondition condition = new GenerationCount(epochLength);
		List<Callable<List<EvaluatedCandidate<List<StraightRoute>>>>> epochs =
				new ArrayList<Callable<List<EvaluatedCandidate<List<StraightRoute>>>>>(
						islands.size() + 1);

		epochs.add(new Epoch(continent, populationSize, eliteCount, populations
				.get(0), condition));
		for (int i = 0; i < islands.size(); i++)
		{
			final EvolutionEngine<List<StraightRoute>> island = islands.get(i);
			epochs.add(new Epoch(island, populationSize, eliteCount, populations
					.get(i + 1), condition, new Stagnation(epochLength - 1)
			{

				@Override
				public boolean shouldTerminate(PopulationData<?> populationData)
				{
					boolean result = super.shouldTerminate(populationData);
					if (result)
					{
						finishedIslands.add(island);
					}
					return result;
				}

			}));
		}

		return epochs;
	}

	private static List<List<StraightRoute>> toCandidateList(
			List<EvaluatedCandidate<List<StraightRoute>>> evaluatedCandidates)
	{
		List<List<StraightRoute>> candidates =
				new ArrayList<List<StraightRoute>>(evaluatedCandidates.size());
		for (EvaluatedCandidate<List<StraightRoute>> evaluatedCandidate : evaluatedCandidates)
		{
			candidates.add(evaluatedCandidate.getCandidate());
		}
		return candidates;
	}

	public List<TerminationCondition> getSatisfiedTerminationConditions()
	{
		if (satisfiedTerminationConditions == null)
		{
			throw new IllegalStateException("EvolutionEngine has not terminated.");
		}
		else
		{
			return Collections.unmodifiableList(satisfiedTerminationConditions);
		}
	}

	private class Epoch implements
			Callable<List<EvaluatedCandidate<List<StraightRoute>>>>
	{
		private final EvolutionEngine<List<StraightRoute>> island;
		private final int populationSize;
		private final int eliteCount;
		private final List<List<StraightRoute>> seedCandidates;
		private final TerminationCondition[] terminationConditions;

		Epoch(EvolutionEngine<List<StraightRoute>> island, int populationSize,
				int eliteCount, List<List<StraightRoute>> seedCandidates,
				TerminationCondition... terminationConditions)
		{
			this.island = island;
			this.populationSize = populationSize;
			this.eliteCount = eliteCount;
			this.seedCandidates = seedCandidates;
			this.terminationConditions = terminationConditions;
		}

		public List<EvaluatedCandidate<List<StraightRoute>>> call()
				throws Exception
		{
			return island.evolvePopulation(populationSize, eliteCount,
					seedCandidates, terminationConditions);
		}
	}
}
