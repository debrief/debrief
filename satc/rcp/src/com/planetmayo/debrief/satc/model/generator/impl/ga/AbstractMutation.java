package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;

import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.vividsolutions.jts.geom.Point;

public abstract class AbstractMutation implements EvolutionaryOperator<List<StraightRoute>>,
								IslandEvolutionObserver<List<StraightRoute>>
{
	protected final List<StraightLeg> legs;
	protected final NumberGenerator<Probability> mutationProbability;
	protected final int generationsInEpoch;
	
	protected final ThreadLocal<Integer> generation = new ThreadLocal<Integer>();
	protected volatile int epoch;

	public AbstractMutation(List<StraightLeg> legs,
			Probability mutationProbability, int generationsInEpoch)
	{
		this.legs = legs;
		this.mutationProbability = new ConstantGenerator<Probability>(mutationProbability);
		this.generationsInEpoch = generationsInEpoch;
	}	
	
	@Override
	public List<List<StraightRoute>> apply(List<List<StraightRoute>> selectedCandidates, Random rng)
	{
		ArrayList<List<StraightRoute>> result = new ArrayList<List<StraightRoute>>();
		for (List<StraightRoute> candidate : selectedCandidates) 
		{
			result.add(mutate(candidate, rng));
		}
		return result;
	}
	
	protected int getIteration() 
	{
		int g = generation.get() == null ? 0 : generation.get();
		return epoch * generationsInEpoch + g;
	}
	
	protected List<StraightRoute> mutate(List<StraightRoute> candidate, Random rng) 
	{
		int length = candidate.size();
		int iteration = getIteration();
		List<StraightRoute> result = null;
    for (int i = 0; i < length; i++)
    {
    	StraightRoute route = candidate.get(i);
			if (! route.isPossible() || mutationProbability.nextValue().nextEvent(rng))
			{
				if (result == null)
				{
					result = new ArrayList<StraightRoute>(candidate);
				}
				StraightLeg leg = legs.get(i);
				
				StraightRoute newRoute = null;	
				int repeats;
				int possibleRepeats = iteration != 0 ? 5 : 50;
				for (repeats = 0; repeats < possibleRepeats; repeats++)
				{
					newRoute = (StraightRoute) leg.createRoute("",
							mutatePoint(iteration, route.getStartPoint(), leg, false, rng),
							mutatePoint(iteration, route.getEndPoint(), leg, true, rng));
					leg.decideAchievableRoute(newRoute);
					if (newRoute.isPossible()) 
					{
						break;
					}
				}				
				if (newRoute.isPossible())
				{
					result.set(i, newRoute);
				}
			}
    }
    return result == null || result.isEmpty() ? candidate : result;		
	}
	
	@Override
	public void populationUpdate(
			PopulationData<? extends List<StraightRoute>> data)
	{
		epoch = data.getGenerationNumber();
	}
	
	@Override
	public void islandPopulationUpdate(int islandIndex,
			PopulationData<? extends List<StraightRoute>> data)
	{
		generation.set(data.getGenerationNumber());		
	}

	protected abstract Point mutatePoint(int iteration, Point current, StraightLeg leg, boolean useEndPoint, Random rng);
}
