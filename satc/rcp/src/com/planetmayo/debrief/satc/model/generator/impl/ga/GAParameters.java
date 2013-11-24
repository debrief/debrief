package com.planetmayo.debrief.satc.model.generator.impl.ga;

import com.planetmayo.debrief.satc.model.ModelObject;

public class GAParameters extends ModelObject
{
	private static final long serialVersionUID = 1L;
	
	public static final String POPULATION_SIZE = "populationSize";
	public static final String ELITIZM = "elitizm";
	public static final String STAGNATION_STEPS = "stagnationSteps";
	public static final String TIMEOUT = "timeout";
	public static final String TIMEOUT_BETWEEN_ITERATIONS = "timeoutBetweenIterations";
	public static final String MUTATION_PROBABILITY = "mutationProbability";
	public static final String TOP_ROUTES = "topRoutes";
	public static final String USE_ALTERING_LEGS = "useAlteringLegs";
	
	private int populationSize;
	private int elitizm;
	private int stagnationSteps;
	private int timeout;
	private int timeoutBetweenIterations;
	private int topRoutes;
	private double mutationProbability;
	private boolean useAlteringLegs;	
	
	public int getPopulationSize()
	{
		return populationSize;
	}
	
	public void setPopulationSize(int populationSize)
	{
		int old = this.populationSize;
		this.populationSize = populationSize;
		firePropertyChange(POPULATION_SIZE, old, populationSize);
	}
	
	public int getElitizm()
	{
		return elitizm;
	}
	
	public void setElitizm(int elitizm)
	{
		int old = this.elitizm;
		this.elitizm = elitizm;
		firePropertyChange(ELITIZM, old, elitizm);
	}
	
	public int getStagnationSteps()
	{
		return stagnationSteps;
	}
	
	public void setStagnationSteps(int stagnationSteps)
	{
		int old = this.stagnationSteps;		
		this.stagnationSteps = stagnationSteps;
		firePropertyChange(STAGNATION_STEPS, old, stagnationSteps);
	}
	
	public int getTimeout()
	{
		return timeout;
	}
	
	public void setTimeout(int timeout)
	{
		int old = this.timeout;
		this.timeout = timeout;
		firePropertyChange(TIMEOUT, old, timeout);
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
		firePropertyChange(TIMEOUT_BETWEEN_ITERATIONS, old, timeoutBetweenIterations);
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
