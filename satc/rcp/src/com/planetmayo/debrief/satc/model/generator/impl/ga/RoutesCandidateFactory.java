package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.CandidateFactory;

import com.vividsolutions.jts.geom.Point;

public class RoutesCandidateFactory implements CandidateFactory<List<Point>>
{
	private List<LegOperations> legs;
	
	public RoutesCandidateFactory(List<LegOperations> legs) 
	{
		this.legs = legs;
	}

	@Override
	public List<List<Point>> generateInitialPopulation(int populationSize, Random rng)
	{
		ArrayList<Points> points = new ArrayList<Points>();
		for (LegOperations leg : legs)
		{
			points.add(new Points(leg.getLeg().getStartPoints()));
			points.add(new Points(leg.getLeg().getEndPoints()));
		}
		List<List<Point>> population = new ArrayList<List<Point>>(populationSize);
		for (int i = 0; i < populationSize; i++)
		{
			List<Point> solution = new ArrayList<Point>(points.size());
			for (Points point : points) 
			{
				solution.add(point.getNext(rng));
			}
			population.add(solution);
		}
		return Collections.unmodifiableList(population);
	}

	@Override
	public List<List<Point>> generateInitialPopulation(int populationSize, 
			Collection<List<Point>> seedCandidates, Random rng)
	{
		ArrayList<List<Point>> population = new ArrayList<List<Point>>(seedCandidates);
		if (population.size() < populationSize)
		{
			population.addAll(generateInitialPopulation(populationSize - population.size(), rng));
		}
		return Collections.unmodifiableList(population);
	}

	@Override
	public List<Point> generateRandomCandidate(Random rng)
	{
		ArrayList<Point> candidate = new ArrayList<Point>();
		for (LegOperations leg : legs) 
		{
			candidate.add(leg.getNextStartPoint());
			candidate.add(leg.getNextEndPoint());
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
