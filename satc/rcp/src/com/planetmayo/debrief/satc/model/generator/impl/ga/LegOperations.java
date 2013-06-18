package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.Arrays;
import java.util.Random;

import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.vividsolutions.jts.geom.Point;

public class LegOperations
{	
	private final CoreLeg leg;
	private final Random random;

	private final int[] startAvgSelection;
	private final int[] endAvgSelection;
	private final int[] startAvgAchievable;
	private final int[] endAvgAchievable;
	private final int[] startCounts;
	private final int[] endCounts;	
	private final int[] startAchievable;
	private final int[] endAchievable;	
	private final int[] startProbabilities;
	private final int[] endProbabilities;
	private int startMax;
	private int endMax;
	private int iterations;
	
	public LegOperations(CoreLeg leg, Random random)
	{
		this.leg = leg;
		this.random = random;
		startAvgSelection = new int[leg.getStartPoints().size()];
		endAvgSelection = new int[leg.getEndPoints().size()];
		startAvgAchievable = new int[leg.getStartPoints().size()];
		endAvgAchievable = new int[leg.getEndPoints().size()];		
		startCounts = new int[leg.getStartPoints().size()];
		endCounts = new int[leg.getEndPoints().size()];
		startAchievable = new int[leg.getStartPoints().size()];
		endAchievable = new int[leg.getEndPoints().size()];		
		startProbabilities = new int[leg.getStartPoints().size()];
		endProbabilities = new int[leg.getEndPoints().size()];
		Arrays.fill(startProbabilities, 1);
		Arrays.fill(endProbabilities, 1);
		startMax = startProbabilities.length;
		endMax = endProbabilities.length;
	}
	
	public Point getNextStartPoint() 
	{
		int rnd = random.nextInt(startMax);
		int sum = 0;
		for (int i = 0; i < startProbabilities.length; i++)
		{
			sum += startProbabilities[i];
			if (sum > rnd) 
			{
				return leg.getStartPoints().get(i);
			}
		}
		return leg.getStartPoints().get(0);
	}
	
	public Point getNextEndPoint() 
	{
		int rnd = random.nextInt(endMax);
		int sum = 0;
		for (int i = 0; i < endProbabilities.length; i++)
		{
			sum += endProbabilities[i];
			if (sum > rnd) 
			{
				return leg.getEndPoints().get(i);
			}
		}
		return leg.getEndPoints().get(0);
	}	
	
	public void collectStart(Point point, boolean achievable) 
	{
		int i = leg.getStartPoints().indexOf(point);
		startCounts[i]++;
		if (achievable) 
		{
			startAchievable[i]++;
		}
	}
	
	public void collectEnd(Point point, boolean achievable) 
	{
		int i = leg.getEndPoints().indexOf(point);
		endCounts[i]++;
		if (achievable) 
		{
			endAchievable[i]++;
		}
	}

	public CoreLeg getLeg()
	{
		return leg;
	}
	
	private int recalculateProbabilitiesBody(int iterationsIncrement, int[] averages, int[] averageAchievables, 
			int[] currentCounts, int[] currentAchievables, int[] probabilities)
	{
		int nextIterations = iterations + iterationsIncrement;
		for (int i = 0; i < averages.length; i++)
		{
			averages[i] = (int) (((long) averages[i] * (long) iterations + (long) currentCounts[i]) / (long) nextIterations);
			averageAchievables[i] = (int) (((long) averageAchievables[i] * (long) iterations + (long) currentAchievables[i]) / (long) nextIterations);
		}
		int max = 0;
		for (int i = 0; i < averageAchievables.length; i++)
		{
			probabilities[i] = 1 + averageAchievables[i];
			probabilities[i] += Math.max(0, iterationsIncrement * 10 - averages[i]);
			max += probabilities[i];
		}
		return max;
	}
	
	public void recalculateProbabilities(int incrementIterations) 
	{
		startMax = recalculateProbabilitiesBody(incrementIterations, startAvgSelection, startAvgAchievable, 
				startCounts, startAchievable, startProbabilities);
		endMax = recalculateProbabilitiesBody(incrementIterations, endAvgSelection, endAvgAchievable, 
				endCounts, endAchievable, endProbabilities);		
		iterations += incrementIterations;
	}
}
