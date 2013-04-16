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
import com.planetmayo.debrief.satc.model.states.ProblemSpaceView;

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
	private volatile boolean _liveRunning = true;
	
	private volatile boolean autoGenerateSolutions = false;

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
		/*LiveRunningListener liveRunningListener = new LiveRunningListener();
		String[] properties = { BaseContribution.ACTIVE, BaseContribution.START_DATE,
				BaseContribution.FINISH_DATE, BaseContribution.HARD_CONSTRAINTS };
		for (String property : properties) 
		{
			contributions.addPropertyListener(property, liveRunningListener);
		}
		contributions.addContributionsChangedListener(liveRunningListener);*/
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
		_liveRunning = checked;
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
		return _liveRunning;
	}
	
	@Override
	public ISolutionGenerator getSolutionGenerator()
	{
		return solutionGenerator;
	}

	@Override
	public ProblemSpaceView getProblemSpace()
	{
		return new ProblemSpaceView(problemSpace);
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
		contributions.clear();
		boundsManager.restart();
		solutionGenerator.clear();
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
			if (_liveRunning) 
			{
				boundsManager.restart();
				boundsManager.run();
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
