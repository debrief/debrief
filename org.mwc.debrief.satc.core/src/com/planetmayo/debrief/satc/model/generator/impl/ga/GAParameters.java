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

import com.planetmayo.debrief.satc.model.ModelObject;
import com.planetmayo.debrief.satc.model.Precision;

public class GAParameters extends ModelObject
{
	private static final long serialVersionUID = 1L;

	public static final String POPULATION_SIZE = "populationSize";
	public static final String ELITIZM = "elitizm";
	public static final String STAGNATION_STEPS = "stagnationSteps";
	public static final String TIMEOUT = "timeout";
	public static final String TIMEOUT_BETWEEN_ITERATIONS =
			"timeoutBetweenIterations";
	public static final String MUTATION_PROBABILITY = "mutationProbability";
	public static final String TOP_ROUTES = "topRoutes";
	public static final String USE_ALTERING_LEGS = "useAlteringLegs";
	public static final String EPOCH_LENGTH = "epochLength";

	private int timeoutBetweenIterations;
	private int topRoutes;
	private double mutationProbability;
	private boolean useAlteringLegs;
	private Precision precision = Precision.LOW;

	public Precision getPrecision()
	{
		return precision;
	}
	
	public void setPrecision(Precision precision)
	{
		this.precision = precision;

		// fire any property listeners to tell them about the new value
		firePropertyChange(POPULATION_SIZE, null, getPopulationSize());
		firePropertyChange(TIMEOUT, null, getTimeout());
		firePropertyChange(STAGNATION_STEPS, null, getStagnationSteps());
		firePropertyChange(ELITIZM, null, getElitizm());
		firePropertyChange(EPOCH_LENGTH, null, getEpochLength());
	}

	public int getElitizm()
	{
		final int stagnationSteps;
		switch (precision)
		{
		case LOW:
			stagnationSteps = 10;
			break;
		case MEDIUM:
			stagnationSteps = 12;
			break;
		case HIGH:
		default:
			stagnationSteps = 20;
			break;
		}
		return stagnationSteps;
	}

	public int getStagnationSteps()
	{
		final int stagnationSteps;
		switch (precision)
		{
		case LOW:
			stagnationSteps = 8;
			break;
		case MEDIUM:
			stagnationSteps = 12;
			break;
		case HIGH:
		default:
			stagnationSteps = 20;
			break;
		}
		return stagnationSteps;
	}
	
	public int getEpochLength()
	{
		final int res;
		switch (precision)
		{
		case LOW:
			res = 15;
			break;
		case MEDIUM:
			res = 20;
			break;
		case HIGH:
		default:
			res = 25;
			break;
		}
		return res;
	}


	public int getPopulationSize()
	{
		final int res;
		switch (precision)
		{
		case LOW:
			res = 45;
			break;
		case MEDIUM:
			res = 70;
			break;
		case HIGH:
		default:
			res = 120;
			break;
		}
		return res;
	}

	public int getTimeout()
	{
		final int res;
		switch (precision)
		{
		case LOW:
			res = 5000;
			break;
		case MEDIUM:
			res = 15000;
			break;
		case HIGH:
		default:
			res = 30000;
			break;
		}
		return res;
	}

	public double getMutationProbability()
	{
		return mutationProbability;
	}

	public void setMutationProbability(double mutationProbability)
	{
		double old = this.mutationProbability;
		this.mutationProbability = mutationProbability;
		firePropertyChange(MUTATION_PROBABILITY, old, mutationProbability);
	}

	public int getTopRoutes()
	{
		return topRoutes;
	}

	public void setTopRoutes(int topRoutes)
	{
		int old = this.topRoutes;
		this.topRoutes = topRoutes;
		firePropertyChange(TOP_ROUTES, old, topRoutes);
	}

	public int getTimeoutBetweenIterations()
	{
		return timeoutBetweenIterations;
	}

	public void setTimeoutBetweenIterations(int timeoutBetweenIterations)
	{
		int old = this.timeoutBetweenIterations;
		this.timeoutBetweenIterations = timeoutBetweenIterations;
		firePropertyChange(TIMEOUT_BETWEEN_ITERATIONS, old,
				timeoutBetweenIterations);
	}

	public boolean isUseAlteringLegs()
	{
		return useAlteringLegs;
	}

	public void setUseAlteringLegs(boolean useAlteringLegs)
	{
		boolean old = this.useAlteringLegs;
		this.useAlteringLegs = useAlteringLegs;
		firePropertyChange(USE_ALTERING_LEGS, old, useAlteringLegs);
	}

}
