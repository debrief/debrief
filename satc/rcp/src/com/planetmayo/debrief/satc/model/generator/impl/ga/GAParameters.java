package com.planetmayo.debrief.satc.model.generator.impl.ga;

import com.planetmayo.debrief.satc.model.ModelObject;

public class GAParameters extends ModelObject
{
	private static final long serialVersionUID = 1L;
	
	public static final String POPULATION_SIZE = "populationSize";
	public static final String ELITIZM = "elitizm";
	public static final String STAGNATION_STEPS = "stagnationSteps";
	public static final String TIMEOUT = "timeout";
	public static final String MUTATION_PROBABILITY = "mutationProbability";
	
	private int populationSize;
	private int elitizm;
	private int stagnationSteps;
	private int timeout;
	private double mutationProbability;
	
	public int getPopulationSize()
	{
		return populationSize;
	}
	
	public GAParameters setPopulationSize(int populationSize)
	{
		int old = this.populationSize;
		this.populationSize = populationSize;
		firePropertyChange(POPULATION_SIZE, old, populationSize);
		return this;		
	}
	
	public int getElitizm()
	{
		return elitizm;
	}
	
	public GAParameters setElitizm(int elitizm)
	{
		int old = this.elitizm;
		this.elitizm = elitizm;
		firePropertyChange(ELITIZM, old, elitizm);
		return this;		
	}
	
	public int getStagnationSteps()
	{
		return stagnationSteps;
	}
	
	public GAParameters setStagnationSteps(int stagnationSteps)
	{
		int old = this.stagnationSteps;		
		this.stagnationSteps = stagnationSteps;
		firePropertyChange(STAGNATION_STEPS, old, stagnationSteps);
		return this;		
	}
	
	public int getTimeout()
	{
		return timeout;
	}
	
	public GAParameters setTimeout(int timeout)
	{
		int old = this.timeout;
		this.timeout = timeout;
		firePropertyChange(TIMEOUT, old, timeout);
		return this;		
	}
	
	public double getMutationProbability()
	{
		return mutationProbability;
	}
	
	public GAParameters setMutationProbability(double mutationProbability)
	{
		double old = this.mutationProbability;
		this.mutationProbability = mutationProbability;
		firePropertyChange(MUTATION_PROBABILITY, old, mutationProbability);		
		return this;
	}
}
