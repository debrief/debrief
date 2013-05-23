package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.vividsolutions.jts.geom.Point;

public class PointsMutation implements EvolutionaryOperator<List<Point>>
{	
	
	private final List<CoreLeg> legs;
  private final NumberGenerator<Probability> mutationProbability;
  
	public PointsMutation(List<CoreLeg> legs,
			Probability mutationProbability)
	{
		this.legs = legs;
		this.mutationProbability = new ConstantGenerator<Probability>(mutationProbability);
	}

	@Override
	public List<List<Point>> apply(List<List<Point>> selectedCandidates, Random rng)
	{
		List<List<Point>> processed = new ArrayList<List<Point>>();
		for (List<Point> candidate : selectedCandidates)
		{
			processed.add(mutateCandidate(candidate, rng));
		}
		return processed;
	}

	private List<Point> mutateCandidate(List<Point> candidate, Random rng) 
	{
		int length = candidate.size();
		List<Point> result = null;
    for (int i = 0; i < length; i++)
    {
        if (mutationProbability.nextValue().nextEvent(rng))
        {
        	if (result == null) 
        	{
        		result = new ArrayList<Point>(candidate);
        	}
        	CoreLeg leg = legs.get(i / 2);
        	List<Point> availablePoints = i % 2 == 0 ? leg.getStartPoints() : leg.getEndPoints();
        	Point newPoint = availablePoints.get(rng.nextInt(availablePoints.size()));
        	result.set(i, newPoint);
        }
    }
    return result == null ? candidate : result;
	}
	
}
