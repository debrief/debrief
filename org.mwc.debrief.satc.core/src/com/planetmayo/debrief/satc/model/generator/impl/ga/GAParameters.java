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

package com.planetmayo.debrief.satc.model.generator.impl.ga;

import org.mwc.debrief.track_shift.zig_detector.Precision;

import com.planetmayo.debrief.satc.model.ModelObject;

public class GAParameters extends ModelObject {
	private static final long serialVersionUID = 1L;

	public static final String POPULATION_SIZE = "populationSize";
	public static final String ELITIZM = "elitizm";
	public static final String STAGNATION_STEPS = "stagnationSteps";
	public static final String TIMEOUT = "timeout";
	public static final String TIMEOUT_BETWEEN_ITERATIONS = "timeoutBetweenIterations";
	public static final String MUTATION_PROBABILITY = "mutationProbability";
	public static final String TOP_ROUTES = "topRoutes";
	public static final String USE_ALTERING_LEGS = "useAlteringLegs";
	public static final String EPOCH_LENGTH = "epochLength";

	private int timeoutBetweenIterations;
	private int topRoutes;
	private double mutationProbability;
	private boolean useAlteringLegs;
	private Precision precision = Precision.LOW;

	public int getElitizm() {
		final int stagnationSteps;
		switch (precision) {
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

	public int getEpochLength() {
		final int res;
		switch (precision) {
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

	public double getMutationProbability() {
		return mutationProbability;
	}

	public int getPopulationSize() {
		final int res;
		switch (precision) {
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

	public Precision getPrecision() {
		return precision;
	}

	public int getStagnationSteps() {
		final int stagnationSteps;
		switch (precision) {
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

	public int getTimeout() {
		final int res;
		switch (precision) {
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

	public int getTimeoutBetweenIterations() {
		return timeoutBetweenIterations;
	}

	public int getTopRoutes() {
		return topRoutes;
	}

	public boolean isUseAlteringLegs() {
		return useAlteringLegs;
	}

	public void setMutationProbability(final double mutationProbability) {
		final double old = this.mutationProbability;
		this.mutationProbability = mutationProbability;
		firePropertyChange(MUTATION_PROBABILITY, old, mutationProbability);
	}

	public void setPrecision(final Precision precision) {
		this.precision = precision;

		// fire any property listeners to tell them about the new value
		firePropertyChange(POPULATION_SIZE, null, getPopulationSize());
		firePropertyChange(TIMEOUT, null, getTimeout());
		firePropertyChange(STAGNATION_STEPS, null, getStagnationSteps());
		firePropertyChange(ELITIZM, null, getElitizm());
		firePropertyChange(EPOCH_LENGTH, null, getEpochLength());
	}

	public void setTimeoutBetweenIterations(final int timeoutBetweenIterations) {
		final int old = this.timeoutBetweenIterations;
		this.timeoutBetweenIterations = timeoutBetweenIterations;
		firePropertyChange(TIMEOUT_BETWEEN_ITERATIONS, old, timeoutBetweenIterations);
	}

	public void setTopRoutes(final int topRoutes) {
		final int old = this.topRoutes;
		this.topRoutes = topRoutes;
		firePropertyChange(TOP_ROUTES, old, topRoutes);
	}

	public void setUseAlteringLegs(final boolean useAlteringLegs) {
		final boolean old = this.useAlteringLegs;
		this.useAlteringLegs = useAlteringLegs;
		firePropertyChange(USE_ALTERING_LEGS, old, useAlteringLegs);
	}

}
