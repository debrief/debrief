package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.generator.jobs.Job;

public interface IJobsManager
{
	
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
	 * if previous job is null or already completed job
	 * is scheduled to be executed immediately 
	 * 
	 * @param job
	 * @param previous
	 * @return scheduled job
	 */
	<T, P, E> Job<T, P> scheduleAfter(final Job<T, P> job, final Job<P, E> previous);
}
