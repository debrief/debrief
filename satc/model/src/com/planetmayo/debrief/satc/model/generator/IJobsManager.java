package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.generator.jobs.Job;

public interface IJobsManager
{
	
	<T, P> Job<T, P> schedule(Job<T, P> job);
	
	<T, P, E> Job<T, P> scheduleAfter(final Job<T, P> job, final Job<P, E> previous);
}
