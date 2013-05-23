package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class GASolutionGenerator implements ISolutionGenerator
{
	
	private Set<IGenerateSolutionsListener> listeners = Collections.newSetFromMap(
			new ConcurrentHashMap<IGenerateSolutionsListener, Boolean>());
	
	private final SafeProblemSpace problemSpace;
	private final IJobsManager jobsManager;
	
	private volatile Precision precision = Precision.LOW;

	
	public GASolutionGenerator(SafeProblemSpace problemSpace, IJobsManager jobsManager) 
	{
		this.problemSpace = problemSpace;
		this.jobsManager = jobsManager;
	}
			
	@Override
	public void addReadyListener(IGenerateSolutionsListener listener)
	{
		listeners.add(listener);		
	}

	@Override
	public void removeReadyListener(IGenerateSolutionsListener listener)
	{
		listeners.remove(listener);		
	}

	@Override
	public void setPrecision(Precision precision)
	{
		this.precision = precision;		
	}

	@Override
	public Precision getPrecision()
	{
		return precision;
	}

	@Override
	public SafeProblemSpace getProblemSpace()
	{
		return problemSpace;
	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateSolutions(boolean fullRerun)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel()
	{
		// TODO Auto-generated method stub
		
	}
}
