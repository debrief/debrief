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
	<T, P, E> Job<T, P> scheduleAfter(Job<T, P> job, Job<P, E> previous);
	
	/**
	 * cancels job
	 */
	<T, P> void cancel(Job<T, P> job);
	
	/**
	 * cancels all jobs from specified group 
	 */
	void cancelGroup(String group); 
	
	
	/**
	 *  waits until specified job is finished
	 */
	<T, P> void waitFor(Job<T, P> job) throws InterruptedException;	
}
