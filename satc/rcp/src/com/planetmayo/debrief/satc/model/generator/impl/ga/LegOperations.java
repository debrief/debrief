package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.vividsolutions.jts.geom.Point;

public class LegOperations
{	
	private final CoreLeg leg;
	private final Random random;

	private final Map<Point, PointExtension> extensions = new IdentityHashMap<Point, PointExtension>();
	private int[] startAvgSelection;
	private int[] endAvgSelection;
	private int[] startAvgAchievable;
	private int[] endAvgAchievable;
	private int[] startCounts;
	private int[] endCounts;	
	private int[] startAchievable;
	private int[] endAchievable;	
	private int[] startProbabilities;
	private int[] endProbabilities;
	private int startMax;
	private int endMax;
	private int iterations;	
	private int startOffset;
	private int endOffset;
	
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
		int rnd = startOffset + random.nextInt(startMax - startOffset);
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
		int rnd = endOffset + random.nextInt(endMax - endOffset);
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
	
	public boolean hasNoAchievablePoints() 
	{
		if (leg.getType() == LegType.ALTERING)
		{
			return false;
		}
		boolean noAchievablePoints = true;
		for (int point : startAvgAchievable)
		{
			if (point > 0)
			{
				noAchievablePoints = false;
			}
		}
		if (! noAchievablePoints) 
		{
			for (int point : endAvgAchievable)
			{
				if (point > 0)
				{
					return false;
				}
			}			
		}
		return true;
	}
	
	public void extendBestPoints(int bestCount, boolean useNewPointsForNextRandoms) 
	{
		int start = doExtendBestPoints(bestCount, true);
		int end = doExtendBestPoints(bestCount, false);
		if (useNewPointsForNextRandoms)
		{
			startOffset = start;
			endOffset = end;
		}
	}
	
	public void useAllPoints() 
	{
		startOffset = 0;
		endOffset = 0;
	}
	
	private int doExtendBestPoints(int bestCount, boolean processStartPoints)
	{
		int[] probabilities = processStartPoints ? startProbabilities : endProbabilities;		
		PriorityQueue<BestOne> bestOnes = new PriorityQueue<BestOne>(bestCount);		
		for (int i = 0; i < probabilities.length; i++)
		{			
			if (probabilities[i] <= 2)
			{
				continue;
			}
			if (bestOnes.size() < bestCount)
			{
				bestOnes.add(new BestOne(i, probabilities[i]));
			}
			else
			{
				BestOne last = bestOnes.peek();
				if (last.probability < probabilities[i])
				{
					bestOnes.poll();
					bestOnes.add(new BestOne(i, probabilities[i]));
				}
			}
		}
		if (bestOnes.isEmpty())
		{
			return 0;
		}
		int oldPointProbabilities = processStartPoints ? startMax : endMax;
		List<Point> newPoints = new ArrayList<Point>();
		List<Integer> newPointsCount = new ArrayList<Integer>(bestCount);
		List<Point> currentPoints = processStartPoints ? leg.getStartPoints() : leg.getEndPoints();
		for (BestOne bestOne : bestOnes)
		{
			Point point = currentPoints.get(bestOne.index);
			if (! extensions.containsKey(point)) 
			{
				extensions.put(point, new PointExtension(point, leg.getCurrentGridPrecision()));
			}
			PointExtension extension = extensions.get(point);
			List<Point> newPointsForThis = extension.extend();
			newPoints.addAll(newPointsForThis);
			newPointsCount.add(newPointsForThis.size());			
		}
		if (processStartPoints) 
		{			
			startAchievable = extendArray(startAchievable, .125, bestOnes, newPoints, newPointsCount);
			startCounts = extendArray(startCounts, .125, bestOnes, newPoints, newPointsCount);
			startProbabilities = extendArray(startProbabilities, .125, bestOnes, newPoints, newPointsCount);
			startAvgAchievable = extendArray(startAvgAchievable, .125, bestOnes, newPoints, newPointsCount);
			startAvgSelection = extendArray(startAvgSelection, .125, bestOnes, newPoints, newPointsCount);
			startMax = 0;
			for (int prob : startProbabilities) 
			{
				startMax += prob;
			}
			leg.addStartPoints(newPoints);
		}
		else 
		{
			endAchievable = extendArray(endAchievable, .125, bestOnes, newPoints, newPointsCount);
			endCounts = extendArray(endCounts, .125, bestOnes, newPoints, newPointsCount);
			endProbabilities = extendArray(endProbabilities, .125, bestOnes, newPoints, newPointsCount);
			endAvgAchievable = extendArray(endAvgAchievable, .125, bestOnes, newPoints, newPointsCount);
			endAvgSelection = extendArray(endAvgSelection, .125, bestOnes, newPoints, newPointsCount);
			endMax = 0;
			for (int prob : endProbabilities) 
			{
				endMax += prob;
			}
			leg.addEndPoints(newPoints);			
		}	
		return oldPointProbabilities;
	}
	
	private int[] extendArray(int[] array, double factor, Collection<BestOne> bestOnes, List<Point> newPoints, List<Integer> newPointsCount) 
	{
		int[] result = Arrays.copyOf(array, array.length + newPoints.size());
		int index = 0;
		int arrayIndex = array.length;
		for (BestOne bestOne : bestOnes)
		{
			for (int i = 0; i < newPointsCount.get(index); i++)
			{
				result[arrayIndex] = (int) Math.floor(result[bestOne.index] * factor);
				arrayIndex++;
			}
			index++;
		}
		return result;
	}	
	
	
	private static class BestOne implements Comparable<BestOne> 
	{
		public final int index;
		public final int probability;
		
		public BestOne(int index, int probability)
		{
			super();
			this.index = index;
			this.probability = probability;
		}

		@Override
		public int compareTo(BestOne o)
		{
			return probability - o.probability;
		}

		@Override
		public String toString()
		{
			return "BestOne [index=" + index + ", probability=" + probability + "]";
		}
	}
}
