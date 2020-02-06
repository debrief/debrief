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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.uncommons.maths.random.MersenneTwisterRNG;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.impl.AbstractSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.jobs.Job;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class SASolutionGenerator extends AbstractSolutionGenerator {
	private static final String SA_GENERATOR_GROUP = "saGeneratorGroup";

	public static final String NAME = "Simulated Annealing";

	private final SAParameters parameters;

	private volatile Job<Void, Void> mainJob;

	private volatile List<CoreLeg> legs;

	public SASolutionGenerator(final IContributions contributions, final IJobsManager jobsManager,
			final SafeProblemSpace problemSpace) {
		super(contributions, jobsManager, problemSpace);
		parameters = new SAParameters();
		parameters.setStartTemperature(2.0);
		parameters.setEndTemperature(0.03);
		parameters.setParallelThreads(4);
		parameters.setIterationsInThread(2);
		parameters.setJoinedIterations(true);
		parameters.setStartOnCenter(true);
		parameters.setSaFunctions(new SAFunctions() {
			@Override
			public double changeTemprature(final SAParameters parameters, final double T, final int step) {
				return parameters.getStartTemperature() * Math.exp(-0.85 * Math.pow(step, 0.25));
			}

			@Override
			public double neighborDistance(final SAParameters parameters, final Random rnd, final double T) {
				return Math.signum(rnd.nextDouble() - 0.5) * T * (Math.pow(1 + 1 / T, 2 * rnd.nextDouble() - 1) - 1);
			}

			@Override
			public double probabilityToAcceptWorse(final SAParameters parameters, final double T, final double eCur,
					final double eNew) {
				if (T < 0.3) {
					return 0;
				}
				return 1 / (1 + Math.exp((eNew - eCur) / T));
			}
		});
	}

	@Override
	public void cancel() {
		final Job<?, ?> job = mainJob;
		if (job != null) {
			jobsManager.cancelGroup(SA_GENERATOR_GROUP);
		}
	}

	@Override
	public void clear() {
		final Job<?, ?> job = mainJob;
		if (job != null) {
			jobsManager.cancelGroup(SA_GENERATOR_GROUP);
			try {
				jobsManager.waitFor(job);
			} catch (final InterruptedException ex) {
				LogFactory.getLog().error("Thread was interrupted", ex);
			}
		}
		if (legs != null) {
			legs.clear();
			legs = null;
		}
	}

	protected CoreRoute findRouteForLeg(final IProgressMonitor progressMonitor, final StraightLeg leg,
			final int legIndex, final Random rnd) throws InterruptedException {
		final SimulatedAnnealing simulator = new SimulatedAnnealing(progressMonitor, parameters, leg, contributions,
				rnd);
		CoreRoute min = null;
		final boolean joined = parameters.isJoinedIterations();
		progressMonitor.beginTask("Leg " + legIndex, parameters.getIterationsInThread());
		for (int k = 0; k < parameters.getIterationsInThread(); k++) {
			progressMonitor.worked(1);
			progressMonitor.subTask("Iteration " + (k + 1));
			final CoreRoute newResult = simulator.simulateAnnealing(joined ? min : null);
			if (min == null || newResult.getScore() < min.getScore()) {
				min = newResult;
			}
		}
		progressMonitor.done();
		return min;
	}

	@Override
	public synchronized void generateSolutions(final boolean fullRerun) {
		Job<Void, Void> generateLegs = null;
		fireStartingGeneration();
		if (fullRerun || legs == null) {
			generateLegs = jobsManager.schedule(new Job<Void, Void>("Generate legs", SA_GENERATOR_GROUP) {

				@Override
				protected <E> Void run(final IProgressMonitor monitor, final Job<Void, E> previous)
						throws InterruptedException {
					legs = getTheLegs(problemSpaceView.states(), monitor);
					return null;
				}
			});
		}
		mainJob = jobsManager.scheduleAfter(new Job<Void, Void>("Calculate SA", SA_GENERATOR_GROUP) {

			@Override
			protected void onComplete() {
				synchronized (SASolutionGenerator.this) {
					mainJob = null;
				}
				fireFinishedGeneration(getException());
			}

			@Override
			protected <E> Void run(final IProgressMonitor monitor, final Job<Void, E> previous)
					throws InterruptedException {
				runSA(monitor);
				return null;
			}
		}, generateLegs);
	}

	public SAParameters getParameters() {
		return parameters;
	}

	protected void runSA(final IProgressMonitor monitor) throws InterruptedException {
		final Job<List<CoreRoute>, Void>[] jobs = startSAJobs(monitor);
		List<CoreRoute> results = new ArrayList<CoreRoute>(jobs[0].getResult());
		final int length = jobs.length;
		final int legsCount = results.size();
		for (int i = 1; i < length; i++) {
			final List<CoreRoute> jobResult = jobs[i].getResult();
			for (int j = 0; j < legsCount; j++) {
				if (results.get(j).getScore() > jobResult.get(j).getScore()) {
					results.set(j, jobResult.get(j));
				}
			}
		}
		results = generateAlteringRoutes(results);
		fireSolutionsReady(new CompositeRoute[] { new CompositeRoute(results) });
	}

	protected Job<List<CoreRoute>, Void>[] startSAJobs(final IProgressMonitor monitor) throws InterruptedException {
		@SuppressWarnings("unchecked")
		final Job<List<CoreRoute>, Void>[] jobs = new Job[parameters.getParallelThreads()];

		final Random rnd = new MersenneTwisterRNG();
		final Semaphore semaphore = new Semaphore(-parameters.getParallelThreads() + 1);
		final AtomicBoolean hasException = new AtomicBoolean(false);

		for (int i = 0; i < parameters.getParallelThreads(); i++) {
			jobs[i] = jobsManager
					.schedule(new Job<List<CoreRoute>, Void>("SA job thread " + (i + 1), SA_GENERATOR_GROUP) {
						@Override
						protected void onComplete() {
							if (!isFinishedCorrectly()) {
								hasException.set(true);
							}
							semaphore.release();
						}

						@Override
						protected <E> List<CoreRoute> run(final IProgressMonitor monitor, final Job<Void, E> previous)
								throws InterruptedException {
							final List<CoreRoute> result = new ArrayList<CoreRoute>();
							int legIndex = 1;
							for (final CoreLeg leg : legs) {
								if (leg.getType() == LegType.STRAIGHT) {
									result.add(findRouteForLeg(monitor, (StraightLeg) leg, legIndex, rnd));
									legIndex++;
								}
							}
							return result;
						}
					});
		}
		while (!semaphore.tryAcquire(2, TimeUnit.SECONDS)) {
			if (monitor.isCanceled() || hasException.get()) {
				jobsManager.cancelGroup(SA_GENERATOR_GROUP);
				throw new InterruptedException();
			}
		}
		if (hasException.get()) {
			throw new InterruptedException();
		}
		return jobs;
	}
}
