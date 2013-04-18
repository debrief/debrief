package com.planetmayo.debrief.satc.gwt.client.jobs;

import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.jobs.Job;
import com.planetmayo.debrief.satc.model.generator.jobs.ProgressMonitor;
import com.planetmayo.debrief.satc.support.LogService;
import com.planetmayo.debrief.satc.support.SupportServices;

public class GWTJobsManager implements IJobsManager {

	@Override
	public <T, P> Job<T, P> schedule(Job<T, P> job) {
		return scheduleAfter(job, null);
	}

	@Override
	public <T, P, E> Job<T, P> scheduleAfter(Job<T, P> job, Job<P, E> previous) {
		LogService log = SupportServices.INSTANCE.getLog();
		if (previous != null && ! previous.isComplete()) 
		{
			throw new IllegalArgumentException("The job " + previous.getName() + " wasn't scheduled");
		}
		try 
		{
			job.startJob(new EmptyMonitor(), previous);
		} 
		catch (InterruptedException ex) 
		{
			log.warn("Job: " + job.getName() + " is canceled");
		}
		catch (Exception e) 
		{
			log.error(e.getMessage(), e);
		}		
		return job;
	}
	
	public static class EmptyMonitor implements ProgressMonitor 
	{

		@Override
		public void beginTask(String name, int totalWork) {	}

		@Override
		public void done() {}

		@Override
		public void internalWorked(double work) {}

		@Override
		public boolean isCanceled() { return false; }

		@Override
		public void setCanceled(boolean value) {}

		@Override
		public void setTaskName(String name) {}

		@Override
		public void subTask(String name) {}

		@Override
		public void worked(int work) {}
	}
}
