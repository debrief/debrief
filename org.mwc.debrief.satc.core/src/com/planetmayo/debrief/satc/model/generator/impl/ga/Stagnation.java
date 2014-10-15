/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.planetmayo.debrief.satc.model.generator.impl.ga;

import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;

public class Stagnation implements TerminationCondition
{
	private final int generationLimit;
	private double bestFitness;
	private int fittestGeneration;

	public Stagnation(int generationLimit)
	{
		this.generationLimit = generationLimit;
	}

	public boolean shouldTerminate(PopulationData<?> populationData)
	{
		double fitness = populationData.getBestCandidateFitness();
		if (populationData.getGenerationNumber() == 0
				|| hasFitnessImproved(fitness))
		{
			bestFitness = fitness;
			fittestGeneration = populationData.getGenerationNumber();
		}

		return populationData.getGenerationNumber() - fittestGeneration >= generationLimit;
	}

	private boolean hasFitnessImproved(double fitness)
	{
		if (Math.abs(fitness - bestFitness) < 0.01) 
		{
			return false;
		}
		return fitness < bestFitness;
	}
}
