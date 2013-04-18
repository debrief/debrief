package com.planetmayo.debrief.satc.model.generator.jobs;

public abstract class Job<T, P>
{	
	private final String name;	
	
	private volatile T result = null;
	
	private volatile Throwable exception = null;
	
	private volatile boolean complete = false;

	public Job(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public final T getResult() 
	{
		if (! complete) 
		{
			return null;
		}
		return result;
	}
	
	public final boolean isComplete() 
	{
		return complete;
	}
	
	public final boolean isFinishedCorrectly() 
	{
		return complete && getException() == null;
	}
	
	public final Throwable getException()
	{
		if (! complete) 
		{
			return null;
		}		
		return exception;
	}

	/**
	 * Runs the job, must be invoked from IJobsManager implementation  
	 * 
	 * @throws InterruptedException when job is canceled
	 */
	public final <E> void startJob(ProgressMonitor monitor, Job<P, E> previous) throws InterruptedException
	{
		try 
		{
			if (previous != null && ! previous.isFinishedCorrectly()) 
			{
				throw previous.getException();
			}
			if (monitor.isCanceled()) 
			{
				throw new InterruptedException();
			}
			result = run(monitor, previous);
			if (monitor.isCanceled()) 
			{
				result = null;
				throw new InterruptedException();
			}			
		} 
		catch (Throwable e) 
		{
			exception = e;
			if (e instanceof RuntimeException) 
			{
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e);
		} 
		finally 
		{
			complete = true;
			onComplete();
		}
	}
	
	/**
	 * Every job must override this method and put there job logic 
	 * 
	 * @throws InterruptedException when job is canceled
	 */
	protected abstract <E> T run(ProgressMonitor monitor, Job<P, E> previous) throws InterruptedException;
	
	/**
	 * this method executes when jobManager processed the job (job goes to complete state)
	 *
	 * Override this method in case you need to know when this job was processed,	   
	 * this is necessary, because Job.run method may not be executed in some cases 
	 * (for example previous to this job was canceled or finished with errors), 
	 * but you still need to do some work after job was processed 
	 * (for example: close resources) 
	 * 
	 */
	protected void onComplete() 
	{
	}
}
