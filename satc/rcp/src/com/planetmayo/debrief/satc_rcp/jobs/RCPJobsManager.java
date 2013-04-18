package com.planetmayo.debrief.satc_rcp.jobs;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.jobs.Job;
import com.planetmayo.debrief.satc.model.generator.jobs.ProgressMonitor;
import com.planetmayo.debrief.satc.support.SupportServices;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class RCPJobsManager implements IJobsManager
{
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<Job, org.eclipse.core.runtime.jobs.Job> jobs = new ConcurrentHashMap<Job,  org.eclipse.core.runtime.jobs.Job>();

	private ProgressMonitor wrap(final IProgressMonitor progressMonitor) 
	{
		return new ProgressMonitor() {

			@Override
			public void beginTask(String name, int totalWork)
			{
				progressMonitor.beginTask(name, totalWork);
			}

			@Override
			public void done()
			{
				progressMonitor.done();				
			}

			@Override
			public void internalWorked(double work)
			{
				progressMonitor.internalWorked(work);				
			}

			@Override
			public boolean isCanceled()
			{
				return progressMonitor.isCanceled();
			}

			@Override
			public void setCanceled(boolean value)
			{
				progressMonitor.setCanceled(value);
			}

			@Override
			public void setTaskName(String name)
			{
				progressMonitor.setTaskName(name);
			}

			@Override
			public void subTask(String name)
			{
				progressMonitor.subTask(name);
			}

			@Override
			public void worked(int work)
			{
				progressMonitor.worked(work);
			}
		};
	}
	
	@Override
	public <T, P> Job<T, P> schedule(Job<T, P> job)
	{
		return scheduleAfter(job, null);
	}

	@Override
	public synchronized <T, P, E> Job<T, P> scheduleAfter(final Job<T, P> job, final Job<P, E> previous)
	{
		if (job == null) 
		{
			throw new IllegalArgumentException("job can't be null");
		}
		org.eclipse.core.runtime.jobs.Job eclipseJob;
		eclipseJob = new org.eclipse.core.runtime.jobs.Job(job.getName())
		{
			
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				org.eclipse.core.runtime.jobs.Job oldEclipseJob = 
						previous == null ? null : jobs.get(previous);
				try 
				{		
					if (oldEclipseJob != null) 
					{
						oldEclipseJob.join();
					}					
					if (previous != null && !previous.isComplete()) 
					{
						SupportServices.INSTANCE.getLog().error("Previous job: " + previous.getName() + " wasn't scheduled");
						monitor.setCanceled(true);
					}
					job.startJob(wrap(monitor), previous);
					return Status.OK_STATUS;
				} 
				catch (InterruptedException ex) 
				{
					return Status.CANCEL_STATUS;
				}	
				catch (Throwable e) 
				{
					return new Status(IStatus.ERROR, SATC_Activator.PLUGIN_ID, e.getMessage(), e);
				}
				finally 
				{
					jobs.remove(job);
				}				
			}
		};
		jobs.put(job, eclipseJob);
		eclipseJob.schedule();
		return job;
	}
	
	
}
