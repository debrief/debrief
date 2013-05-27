package com.planetmayo.debrief.satc.model.generator.impl;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class SwitchableSolutionGenerator implements ISolutionGenerator
{	
	private Set<IGenerateSolutionsListener> listeners;
	private ISolutionGenerator currentGenerator;
	
	public SwitchableSolutionGenerator(ISolutionGenerator initialGenerator)
	{
		if (initialGenerator == null) 
		{
			throw new IllegalArgumentException("initialGenerator can't be null");
		}
		this.listeners = Collections
				.newSetFromMap(new ConcurrentHashMap<IGenerateSolutionsListener, Boolean>());
		currentGenerator = initialGenerator;
	}

	@Override
	public void addReadyListener(IGenerateSolutionsListener listener)
	{
		listeners.add(listener);
		if (currentGenerator != null)
		{
			currentGenerator.addReadyListener(listener);
		}
	}

	@Override
	public void removeReadyListener(IGenerateSolutionsListener listener)
	{
		listeners.remove(listener);
		if (currentGenerator != null)
		{
			currentGenerator.removeReadyListener(listener);
		}
	}

	@Override
	public void setPrecision(Precision precision)
	{
		currentGenerator.setPrecision(precision);		
	}

	@Override
	public Precision getPrecision()
	{
		return currentGenerator.getPrecision();
	}

	@Override
	public SafeProblemSpace getProblemSpace()
	{
		return currentGenerator.getProblemSpace();
	}

	@Override
	public void clear()
	{
		currentGenerator.clear();		
	}

	@Override
	public void generateSolutions(boolean fullRerun)
	{
		currentGenerator.generateSolutions(fullRerun);		
	}

	@Override
	public void cancel()
	{
		currentGenerator.cancel();		
	}
	
	public synchronized void switchGenerator(ISolutionGenerator generator)
	{
		if (generator == null) 
		{
			throw new IllegalArgumentException("generator can't be null");
		}
		Precision precision = getPrecision();
		for (IGenerateSolutionsListener listener : listeners)
		{
			currentGenerator.removeReadyListener(listener);
			generator.addReadyListener(listener);
		}
		generator.setPrecision(precision);
		currentGenerator = generator;
	}
	
	public ISolutionGenerator getCurrentGenerator() 
	{
		return currentGenerator;
	}
}
