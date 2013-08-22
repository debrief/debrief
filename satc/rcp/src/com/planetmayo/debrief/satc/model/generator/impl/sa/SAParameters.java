package com.planetmayo.debrief.satc.model.generator.impl.sa;

import com.planetmayo.debrief.satc.model.ModelObject;

public class SAParameters extends ModelObject
{
	private static final long serialVersionUID = 1L;
	
	public static final String START_TEMPRATURE = "startTemprature";
	public static final String END_TEMPRATURE = "endTemprature";
	public static final String PARALLEL_THREADS = "parallelThreads";
	public static final String ITERATIONS_IN_THREAD = "iterationsInThread";
	public static final String START_ON_CENTER = "startOnCenter";
	public static final String JOINED_ITERATIONS = "joinedIterations";	
	
	/**
	 * Initial temperature on each iteration
	 */
	private volatile double startTemprature;
	
	/**
	 * When we should stop one iteration of our algorithm
	 */
	private volatile double endTemprature;
	
	/**
	 * How many parallel threads we will use for computations
	 */
	private volatile int parallelThreads;
	
	/**
	 * How many separate iterations should we do in each threads
	 */
	private volatile int iterationsInThread;
	
	/**
	 * Should we start our algorithm on center or on random point
	 */
	private volatile boolean startOnCenter;
	
	/**
	 * Should we start each iteration in thread from new start point or 
	 * previous best result
	 */
	private volatile boolean joinedIterations;
	
	private SAFunctions saFuntions;
	
	public double getStartTemprature()
	{
		return startTemprature;
	}
	
	public void setStartTemprature(double startTemprature)
	{
		double old = this.startTemprature;
		this.startTemprature = startTemprature;
		firePropertyChange(START_TEMPRATURE, old, startTemprature);
	}
	
	public double getEndTemprature()
	{
		return endTemprature;
	}
	
	public void setEndTemprature(double endTemprature)
	{
		double old = this.endTemprature;
		this.endTemprature = endTemprature;
		firePropertyChange(END_TEMPRATURE, old, endTemprature);
	}
	
	public int getParallelThreads()
	{
		return parallelThreads;
	}
	
	public void setParallelThreads(int parallelThreads)
	{
		int old = this.parallelThreads;
		this.parallelThreads = parallelThreads;
		firePropertyChange(PARALLEL_THREADS, old, parallelThreads);
	}
	
	public int getIterationsInThread()
	{
		return iterationsInThread;
	}
	
	public void setIterationsInThread(int iterationsInThread)
	{
		int old = this.iterationsInThread;
		this.iterationsInThread = iterationsInThread;
		firePropertyChange(ITERATIONS_IN_THREAD, old, iterationsInThread);
	}

	public boolean isStartOnCenter()
	{
		return startOnCenter;
	}

	public void setStartOnCenter(boolean startOnCenter)
	{
		boolean old = this.startOnCenter;
		this.startOnCenter = startOnCenter;
		firePropertyChange(START_ON_CENTER, old, startOnCenter);
	}

	public boolean isJoinedIterations()
	{
		return joinedIterations;
	}

	public void setJoinedIterations(boolean joinedIterations)
	{
		boolean old = this.joinedIterations;
		this.joinedIterations = joinedIterations;
		firePropertyChange(JOINED_ITERATIONS, old, joinedIterations);
	}

	public SAFunctions getSaFuntions()
	{
		return saFuntions;
	}

	public void setSaFuntions(SAFunctions saFuntions)
	{
		this.saFuntions = saFuntions;
	}
}

