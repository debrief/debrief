/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc.model.generator.impl.sa;

import com.planetmayo.debrief.satc.model.ModelObject;

public class SAParameters extends ModelObject {
	private static final long serialVersionUID = 1L;

	public static final String START_TEMPRATURE = "startTemperature";
	public static final String END_TEMPRATURE = "endTemperature";
	public static final String PARALLEL_THREADS = "parallelThreads";
	public static final String ITERATIONS_IN_THREAD = "iterationsInThread";
	public static final String START_ON_CENTER = "startOnCenter";
	public static final String JOINED_ITERATIONS = "joinedIterations";

	/**
	 * Initial temperature on each iteration
	 */
	private volatile double startTemperature;

	/**
	 * When we should stop one iteration of our algorithm
	 */
	private volatile double endTemperature;

	/**
	 * How many parallel threads we will use for computations
	 */
	private volatile int parallelThreads;

	/**
	 * How many times should we repeat each route calculation in each thread
	 */
	private volatile int iterationsInThread;

	/**
	 * Should we start our algorithm on center or on random point
	 */
	private volatile boolean startOnCenter;

	/**
	 * Should we start each iteration in thread from new start point or previous
	 * best result
	 */
	private volatile boolean joinedIterations;

	private SAFunctions saFunctions;

	public double getEndTemperature() {
		return endTemperature;
	}

	public int getIterationsInThread() {
		return iterationsInThread;
	}

	public int getParallelThreads() {
		return parallelThreads;
	}

	public SAFunctions getSaFunctions() {
		return saFunctions;
	}

	public double getStartTemperature() {
		return startTemperature;
	}

	public boolean isJoinedIterations() {
		return joinedIterations;
	}

	public boolean isStartOnCenter() {
		return startOnCenter;
	}

	public void setEndTemperature(final double endTemperature) {
		final double old = this.endTemperature;
		this.endTemperature = endTemperature;
		firePropertyChange(END_TEMPRATURE, old, endTemperature);
	}

	public void setIterationsInThread(final int iterationsInThread) {
		final int old = this.iterationsInThread;
		this.iterationsInThread = iterationsInThread;
		firePropertyChange(ITERATIONS_IN_THREAD, old, iterationsInThread);
	}

	public void setJoinedIterations(final boolean joinedIterations) {
		final boolean old = this.joinedIterations;
		this.joinedIterations = joinedIterations;
		firePropertyChange(JOINED_ITERATIONS, old, joinedIterations);
	}

	public void setParallelThreads(final int parallelThreads) {
		final int old = this.parallelThreads;
		this.parallelThreads = parallelThreads;
		firePropertyChange(PARALLEL_THREADS, old, parallelThreads);
	}

	public void setSaFunctions(final SAFunctions saFunctions) {
		this.saFunctions = saFunctions;
	}

	public void setStartOnCenter(final boolean startOnCenter) {
		final boolean old = this.startOnCenter;
		this.startOnCenter = startOnCenter;
		firePropertyChange(START_ON_CENTER, old, startOnCenter);
	}

	public void setStartTemperature(final double startTemperature) {
		final double old = this.startTemperature;
		this.startTemperature = startTemperature;
		firePropertyChange(START_TEMPRATURE, old, startTemperature);
	}
}
