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

import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;

public class SimulatedAnnealing {
	private final IProgressMonitor progressMonitor;
	private final IContributions contributions;
	private final StraightLeg leg;
	private final Random rnd;
	private final SAParameters parameters;

	private volatile PointsGenerator start;
	private volatile PointsGenerator end;

	public SimulatedAnnealing(final IProgressMonitor progressMonitor, final SAParameters parameters,
			final StraightLeg leg, final IContributions contributions, final Random rnd) {
		this.progressMonitor = progressMonitor;
		this.contributions = contributions;
		this.leg = leg;
		this.rnd = rnd;
		this.parameters = parameters;

		initialize();
	}

	protected double error(final CoreRoute route) {
		double sum = 0;
		for (final BaseContribution contribution : contributions) {
			sum += contribution.calculateErrorScoreFor(route);
		}
		route.setScore(sum);
		return sum;
	}

	protected void initialize() {
		start = new PointsGenerator(leg.getFirst().getLocation().getGeometry(), rnd, parameters);
		end = new PointsGenerator(leg.getLast().getLocation().getGeometry(), rnd, parameters);
	}

	public CoreRoute simulateAnnealing(final CoreRoute startRoute) throws InterruptedException {
		double min = Double.MAX_VALUE;
		CoreRoute result = null;

		CoreRoute current = startRoute;
		if (current == null) {
			current = leg.createRoute(start.startPoint(), end.startPoint(), null);
		}
		double eCurrent = error(current);

		double t = parameters.getStartTemperature();
		int i = 0;
		while (t > parameters.getEndTemperature()) {
			CoreRoute newRoute;
			while (true) {
				if (progressMonitor.isCanceled()) {
					throw new InterruptedException();
				}
				newRoute = leg.createRoute(start.newPoint(current.getStartPoint(), t),
						end.newPoint(current.getEndPoint(), t), null);
				leg.decideAchievableRoute(newRoute);
				if (newRoute.isPossible()) {
					break;
				}
			}
			final double eNew = error(newRoute);
			if (eNew > eCurrent) {
				final double h = parameters.getSaFunctions().probabilityToAcceptWorse(parameters, t, eCurrent, eNew);
				if (rnd.nextDouble() < h) {
					current = newRoute;
					eCurrent = eNew;
				}
			} else {
				current = newRoute;
				eCurrent = eNew;
				if (min > eNew) {
					min = eNew;
					result = newRoute;
				}
			}
			i++;
			t = parameters.getSaFunctions().changeTemprature(parameters, t, i);
		}
		result.setScore(min);
		return result;
	}
}
