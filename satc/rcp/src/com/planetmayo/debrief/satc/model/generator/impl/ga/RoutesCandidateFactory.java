package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.CandidateFactory;

import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.vividsolutions.jts.geom.Point;

public class RoutesCandidateFactory implements CandidateFactory<List<StraightRoute>>
{
	private List<StraightLeg> straightLegs;
	
	public RoutesCandidateFactory(List<StraightLeg> legs) 
	{
		this.straightLegs = new ArrayList<StraightLeg>();
		for (StraightLeg leg : legs)
		{
			if (leg.getType() == LegType.STRAIGHT)
			{
				this.straightLegs.add(leg);
			}
		}
	}

	@Override
	public List<List<StraightRoute>> generateInitialPopulation(int populationSize, Random rng)
	{
		ArrayList<Points> points = new ArrayList<Points>();
		for (StraightLeg leg : straightLegs)
		{
			points.add(new Points(leg.getStartPoints()));
			points.add(new Points(leg.getEndPoints()));
		}
		List<List<StraightRoute>> population = new ArrayList<List<StraightRoute>>(populationSize);
		for (int i = 0; i < populationSize; i++)
		{
			List<StraightRoute> solution = new ArrayList<StraightRoute>(points.size());
			int j = 0;
			for (StraightLeg leg : straightLegs) 
			{
				Point start = points.get(j).getNext(rng);
				Point end = points.get(j + 1).getNext(rng);				
				solution.add((StraightRoute) leg.createRoute("", start, end));
				j += 2;
			}
			population.add(solution);
		}
		return Collections.unmodifiableList(population);
	}

	@Override
	public List<List<StraightRoute>> generateInitialPopulation(int populationSize, 
			Collection<List<StraightRoute>> seedCandidates, Random rng)
	{
		ArrayList<List<StraightRoute>> population = new ArrayList<List<StraightRoute>>(seedCandidates);
		if (population.size() < populationSize)
		{
			population.addAll(generateInitialPopulation(populationSize - population.size(), rng));
		}
		return Collections.unmodifiableList(population);
	}

	@Override
	public List<StraightRoute> generateRandomCandidate(Random rng)
	{
		ArrayList<StraightRoute> candidate = new ArrayList<StraightRoute>();
		for (StraightLeg leg : straightLegs) 
		{
			Point start =  leg.getStartPoints().get(rng.nextInt(leg.getStartPoints().size()));
			Point end =  leg.getStartPoints().get(rng.nextInt(leg.getStartPoints().size()));
			candidate.add((StraightRoute) leg.createRoute("", start, end));
		}
		return candidate;
	}
	
	private class Points 
	{
		private final List<Point> points;
		private final boolean[] used;
		
		public Points(List<Point> points)
		{
			this.points = points;
			used = new boolean[points.size()];
		}
		
		public Point getNext(Random rng) 
		{
			int nextPoint = rng.nextInt(used.length);
			int i = nextPoint;
			while (used[i]) 
			{
				i++;
				if (i == used.length) 
				{
					i = 0;
				}
				if (i == nextPoint) 
				{
					Arrays.fill(used, false);
				}
			}
			used[i] = true;
			return points.get(i);
		}		
	}
}
