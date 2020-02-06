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

package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.generator.jobs.Job;

public interface IJobsManager {

	/**
	 * cancels job
	 */
	<T, P> void cancel(Job<T, P> job);

	/**
	 * cancels all jobs from specified group
	 */
	void cancelGroup(String group);

	/**
	 * schedules job to be executed immediately
	 * 
	 * @param job
	 * @return scheduled job
	 */
	<T, P> Job<T, P> schedule(Job<T, P> job);

	/**
	 * schedules job to be executed after previous job
	 *
	 * if previous job is null or already completed job is scheduled to be executed
	 * immediately
	 *
	 * @param job
	 * @param previous
	 * @return scheduled job
	 */
	<T, P, E> Job<T, P> scheduleAfter(Job<T, P> job, Job<P, E> previous);

	/**
	 * waits until specified job is finished
	 */
	<T, P> void waitFor(Job<T, P> job) throws InterruptedException;
}
