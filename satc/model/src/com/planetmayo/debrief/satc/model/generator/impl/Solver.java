package com.planetmayo.debrief.satc.model.generator.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.generator.SteppingAdapter;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;
import com.planetmayo.debrief.satc.support.SupportServices;

public class Solver implements ISolver
{
	private final IContributions contributions;
	private final IBoundsManager boundsManager;
	private final ISolutionGenerator solutionGenerator;
	private final IJobsManager jobsManager;	
	
	private final ProblemSpace problemSpace;
	
	/**
	 * whether we auto=run after each contribution change
	 * 
	 */
	private volatile boolean liveRunning = true;
	
	private volatile boolean autoGenerateSolutions = false;
	
	private volatile boolean isClear = false;
	
	/**
	 * the set of contribution properties that we're interested in
	 * 
	 */
	private final String[] propertiesToRestartBoundsManager =
	{ BaseContribution.ACTIVE, BaseContribution.START_DATE,
			BaseContribution.FINISH_DATE, BaseContribution.HARD_CONSTRAINTS };	

	public Solver(IContributions contributions, ProblemSpace problemSpace,
			IBoundsManager boundsManager,	ISolutionGenerator solutionGenerator, 
			IJobsManager jobsManager)
	{
		super();
		this.contributions = contributions;
		this.boundsManager = boundsManager;
		this.solutionGenerator = solutionGenerator;
		this.jobsManager = jobsManager;
		this.problemSpace = problemSpace;
		
		attachListeners();
	}
	
	private void attachListeners() 
	{
		LiveRunningListener liveRunningListener = new LiveRunningListener();
		contributions.addContributionsChangedListener(liveRunningListener);
		for (String property : propertiesToRestartBoundsManager) 
		{
			contributions.addPropertyListener(property, liveRunningListener);
		}
		
		AutoGenerateSolutionsListener listener = new AutoGenerateSolutionsListener();
		boundsManager.addConstrainSpaceListener(listener);
	}

	@Override
	public IContributions getContributions()
	{
		return contributions;
	}

	@Override
	public IBoundsManager getBoundsManager()
	{
		return boundsManager;
	}

	@Override
	public void setLiveRunning(boolean checked)
	{
		liveRunning = checked;
	}

	@Override
	public boolean isAutoGenerateSolutions()
	{
		return autoGenerateSolutions;
	}

	@Override
	public void setAutoGenerateSolutions(boolean autoGenerateSolutions)
	{
		this.autoGenerateSolutions = autoGenerateSolutions;
	}

	@Override
	public boolean isLiveEnabled()
	{
		return liveRunning;
	}
	
	@Override
	public ISolutionGenerator getSolutionGenerator()
	{
		return solutionGenerator;
	}

	@Override
	public SafeProblemSpace getProblemSpace()
	{
		return new SafeProblemSpace(problemSpace);
	}
	
	@Override
	public void setVehicleType(VehicleType type)
	{
		boundsManager.setVehicleType(type);
	}

	@Override
	public VehicleType getVehicleType()
	{
		// TODO: implement!
		return null;
	}
	
	@Override
	public void clear()
	{
		isClear = true;
		try 
		{
			contributions.clear();
			boundsManager.restart();
			solutionGenerator.clear();
		} 
		finally 
		{
			isClear = false;
		}
	}
	
	@Override
	public void cancel()
	{
		solutionGenerator.cancel();
	}

	@Override
	public void run()
	{
		boundsManager.run();
	}

	private class LiveRunningListener  
				implements PropertyChangeListener, IContributionsChangedListener 
	{
		private void run() 
		{
			if (isClear) 
			{
				return;
			}
			try 
			{
				boundsManager.restart();			
				if (liveRunning) 
				{
					boundsManager.run();
				}
			} 
			catch (Exception ex) 
			{
				SupportServices.INSTANCE.getLog().error(
						"Exception: " + ex.getMessage(), ex);				
			}
		}
		
		@Override
		public void added(BaseContribution contribution)
		{
			run();
		}
		
		@Override
		public void removed(BaseContribution contribution)
		{
			run();
		}

		@Override
		public void propertyChange(PropertyChangeEvent arg0)
		{
			run();
		}
	}
	
	private class AutoGenerateSolutionsListener extends SteppingAdapter 
	{

		@Override
		public void statesBounded(IBoundsManager boundsManager)
		{
			if (autoGenerateSolutions) 
			{
				solutionGenerator.generateSolutions();
			}
		}

		@Override
		public void restarted(IBoundsManager boundsManager)
		{
			solutionGenerator.clear();
		}

		@Override
		public void error(IBoundsManager boundsManager,
				IncompatibleStateException ex)
		{
			solutionGenerator.clear();
		}
	}
}
