/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.generator.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.impl.bf.BFSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.impl.ga.GASolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.impl.sa.SASolutionGenerator;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class SwitchableSolutionGenerator implements ISolutionGenerator
{
	private final IContributions contributions;
	private final IJobsManager jobsManager;
	private final SafeProblemSpace problemSpace;
	
	private Set<IGenerateSolutionsListener> listeners;
	private ISolutionGenerator currentGenerator;
	
	public SwitchableSolutionGenerator(IContributions contributions, IJobsManager jobsManager, SafeProblemSpace problemSpace)
	{
		this.contributions = contributions;
		this.jobsManager = jobsManager;
		this.problemSpace = problemSpace;
		this.listeners = Collections.synchronizedSet(
				new HashSet<IGenerateSolutionsListener>());
		switchToGA();
	}

	/** whether insignificant cuts should be suppressed (only in mid-low)
	 * 
	 * @param autoSuppress yes/no
	 */
	public void setAutoSuppress(boolean autoSuppress)
	{
		if(currentGenerator != null)
			currentGenerator.setAutoSuppress(autoSuppress);
	}
	
	/** whether insignificant cuts should be suppressed (only in mid-low)
	 * 
	 * @return yes/no
	 */
	public boolean getAutoSuppress()
	{
		boolean res = false;
		if(currentGenerator != null)
			res = currentGenerator.getAutoSuppress();
		
		return res;
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
		Precision precision = currentGenerator == null ? Precision.LOW : getPrecision();
		synchronized (listeners)
		{
			for (IGenerateSolutionsListener listener : listeners)
			{
				if (currentGenerator != null)
				{
					currentGenerator.removeReadyListener(listener);
				}
				generator.addReadyListener(listener);
			}
		}
		generator.setPrecision(precision);
		currentGenerator = generator;
	}
	
	public void switchToBF() 
	{
		switchGenerator(new BFSolutionGenerator(contributions, jobsManager, problemSpace));
	}
	
	public void switchToGA() 
	{
		switchGenerator(new GASolutionGenerator(contributions, jobsManager, problemSpace));
	}

	public void switchToSA() 
	{
		switchGenerator(new SASolutionGenerator(contributions, jobsManager, problemSpace));
	}
	
	public ISolutionGenerator getCurrentGenerator() 
	{
		return currentGenerator;
	}
}
