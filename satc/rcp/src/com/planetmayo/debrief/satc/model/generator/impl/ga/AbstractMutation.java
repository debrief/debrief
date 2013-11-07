package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.PopulationData;

import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.vividsolutions.jts.geom.Point;

public abstract class AbstractMutation implements EvolutionaryOperator<List<StraightRoute>>,
														EvolutionObserver<List<StraightRoute>>
{
	protected final List<StraightLeg> legs;
	protected final NumberGenerator<Probability> mutationProbability;
	
	protected volatile int iteration;

	public AbstractMutation(List<StraightLeg> legs,
			Probability mutationProbability)
	{
		this.legs = legs;
		this.mutationProbability = new ConstantGenerator<Probability>(mutationProbability);
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

	protected List<StraightRoute> mutate(List<StraightRoute> candidate, Random rng) 
	{
		int length = candidate.size();
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
				
				StraightRoute newRoute;				
				do
				{
					newRoute = (StraightRoute) leg.createRoute("",
							mutatePoint(route.getStartPoint(), leg, false, rng),
							mutatePoint(route.getEndPoint(), leg, true, rng));
					leg.decideAchievableRoute(newRoute);
				}
				while (!newRoute.isPossible());
				
				result.set(i, newRoute);
			}
    }
    return result == null ? candidate : result;		
	}
	
	@Override
	public void populationUpdate(
			PopulationData<? extends List<StraightRoute>> data)
	{
		iteration = data.getGenerationNumber();		
	}

	protected abstract Point mutatePoint(Point current, StraightLeg leg, boolean useEndPoint, Random rng);
}
