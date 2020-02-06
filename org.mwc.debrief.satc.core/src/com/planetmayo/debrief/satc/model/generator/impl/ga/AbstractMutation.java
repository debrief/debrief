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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.PopulationData;

import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public abstract class AbstractMutation
		implements EvolutionaryOperator<List<StraightRoute>>, EvolutionObserver<List<StraightRoute>> {
	protected final List<StraightLeg> legs;
	protected final NumberGenerator<Probability> mutationProbability;

	protected final AtomicInteger iteration = new AtomicInteger(0);

	protected final List<double[]> startVertexProbabilities;
	protected final List<double[]> endVertexProbabilities;

	public AbstractMutation(final List<StraightLeg> legs, final Probability mutationProbability) {
		this.legs = legs;
		this.mutationProbability = new ConstantGenerator<Probability>(mutationProbability);
		startVertexProbabilities = new ArrayList<double[]>(legs.size());
		endVertexProbabilities = new ArrayList<double[]>(legs.size());
		for (final StraightLeg leg : legs) {
			startVertexProbabilities.add(calculateVertexProbabilities(leg, false));
			endVertexProbabilities.add(calculateVertexProbabilities(leg, true));
		}
	}

	@Override
	public List<List<StraightRoute>> apply(final List<List<StraightRoute>> selectedCandidates, final Random rng) {
		final ArrayList<List<StraightRoute>> result = new ArrayList<List<StraightRoute>>();
		for (final List<StraightRoute> candidate : selectedCandidates) {
			result.add(mutate(candidate, rng));
		}
		return result;
	}

	protected double calcProbability(final Coordinate current, final Coordinate next, final Coordinate prev) {
		return MathUtils.calcAbsoluteValue(current.x - next.x, current.y - next.y)
				+ MathUtils.calcAbsoluteValue(current.x - prev.x, current.y - prev.y);
	}

	protected double[] calculateVertexProbabilities(final StraightLeg leg, final boolean useEndPoint) {
		final BoundedState state = useEndPoint ? leg.getLast() : leg.getFirst();
		final Coordinate[] vertexes = state.getLocation().getGeometry().getCoordinates();
		final int length = vertexes.length - 1;
		final double[] probabilities = new double[length];
		double sum = 0;
		for (int i = 1; i < length - 1; i++) {
			sum += probabilities[i] = calcProbability(vertexes[i], vertexes[i + 1], vertexes[i - 1]);
		}
		sum += probabilities[0] = calcProbability(vertexes[0], vertexes[1], vertexes[length - 1]);
		sum += probabilities[length - 1] = calcProbability(vertexes[length - 1], vertexes[0], vertexes[length - 2]);

		for (int i = 0; i < length; i++) {
			probabilities[i] = probabilities[i] / sum;
		}
		return probabilities;
	}

	protected List<StraightRoute> mutate(final List<StraightRoute> candidate, final Random rng) {
		final int length = candidate.size();
		final int iteration = this.iteration.get();
		List<StraightRoute> result = null;
		for (int i = 0; i < length; i++) {
			final StraightRoute route = candidate.get(i);
			if (!route.isPossible() || mutationProbability.nextValue().nextEvent(rng)) {
				if (result == null) {
					result = new ArrayList<StraightRoute>(candidate);
				}
				final StraightLeg leg = legs.get(i);

				StraightRoute newRoute = null;
				int repeats;
				final int possibleRepeats = 5;// iteration != 0 ? 5 : 50;
				for (repeats = 0; repeats < possibleRepeats; repeats++) {
					newRoute = (StraightRoute) leg.createRoute(
							mutatePoint(iteration, route.getStartPoint(), i, false, rng),
							mutatePoint(iteration, route.getEndPoint(), i, true, rng), null);
					leg.decideAchievableRoute(newRoute);
					if (newRoute.isPossible()) {
						break;
					}
				}
				result.set(i, newRoute);
			}
		}
		return result == null || result.isEmpty() ? candidate : result;
	}

	protected Point mutatePoint(final int iteration, final Point current, final int legIndex, final boolean useEndPoint,
			final Random rng) {
		return current;
	}

	protected Point nextVertex(final int legIndex, final boolean useEndPoint, final Random rng) {
		final StraightLeg leg = legs.get(legIndex);
		final BoundedState state = useEndPoint ? leg.getLast() : leg.getFirst();
		final double[] probabilities = useEndPoint ? endVertexProbabilities.get(legIndex)
				: startVertexProbabilities.get(legIndex);

		final Coordinate[] vertexes = state.getLocation().getGeometry().getCoordinates();
		final double random = rng.nextDouble();
		int index = 0;
		double sum = probabilities[0];
		while (sum <= random) {
			index++;
			sum += probabilities[index];
		}
		return GeoSupport.getFactory().createPoint(vertexes[index]);
	}

	@Override
	public void populationUpdate(final PopulationData<? extends List<StraightRoute>> data) {
		iteration.incrementAndGet();
		iteration.compareAndSet(200, 0);
	}
}
