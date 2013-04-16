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

	public final <E> void runInternal(ProgressMonitor monitor, Job<P, E> previous) throws InterruptedException
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
	
	protected abstract <E> T run(ProgressMonitor monitor, Job<P, E> previous) throws InterruptedException;
	
	protected void onComplete() 
	{
	}
}
