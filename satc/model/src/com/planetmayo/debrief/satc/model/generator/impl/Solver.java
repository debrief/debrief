package com.planetmayo.debrief.satc.model.generator.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import com.planetmayo.debrief.satc.model.Precision;
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
	
	private LiveRunningListener liveRunningListener;
	private AutoGenerateSolutionsListener autoGenerateListener;
	
	/**
	 * the set of contribution properties that we're interested in
	 * 
	 */
	private final String[] propertiesToRestartBoundsManager =
	{ BaseContribution.ACTIVE, BaseContribution.START_DATE,
			BaseContribution.FINISH_DATE, BaseContribution.HARD_CONSTRAINTS };	
	private final String[] propertiesToRestartSolutionGenerator =
	{ BaseContribution.WEIGHT, BaseContribution.ESTIMATE };
	
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
		liveRunningListener = new LiveRunningListener();
		contributions.addContributionsChangedListener(liveRunningListener);
		for (String property : propertiesToRestartBoundsManager) 
		{
			contributions.addPropertyListener(property, liveRunningListener);
		}
		
		autoGenerateListener = new AutoGenerateSolutionsListener();
		for (String property : propertiesToRestartSolutionGenerator) 
		{
			contributions.addPropertyListener(property, autoGenerateListener);
		}		
		boundsManager.addConstrainSpaceListener(autoGenerateListener);
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
		return problemSpace.getVehicleType();
	}
	
	@Override
	public void setPrecision(Precision precision)
	{
		solutionGenerator.setPrecision(precision);
	}

	@Override
	public Precision getPrecision()
	{
		return solutionGenerator.getPrecision();
	}

	@Override
	public synchronized void clear()
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
	public synchronized void cancel()
	{
		solutionGenerator.cancel();
	}

	@Override
	public synchronized void run()
	{
		boundsManager.run();
	}
	
  @Override
	public synchronized void save(Writer writer)
	{
  	List<BaseContribution> contributionsList = new ArrayList<BaseContribution>(
  			contributions.getContributions());
  	writer.writeContributions(contributionsList);
  	writer.writePrecision(solutionGenerator.getPrecision());
  	writer.writeVehicleType(getVehicleType());  	
	}

	@Override
	public synchronized void load(Reader reader)
	{
		try 
		{
			boundsManager.removeConstrainSpaceListener(autoGenerateListener);
			contributions.removeContributionsChangedListener(liveRunningListener);

			clear();			
			for (BaseContribution contribution : reader.readContributions()) 
			{
				contributions.addContribution(contribution);
			}
			solutionGenerator.setPrecision(reader.readPrecision());
			setVehicleType(reader.readVehicleType());
		}
		finally 
		{
			boundsManager.addConstrainSpaceListener(autoGenerateListener);
			contributions.addContributionsChangedListener(liveRunningListener);
		}
	}

	private class LiveRunningListener  
				implements PropertyChangeListener, IContributionsChangedListener 
	{
		private void run() 
		{
			synchronized (Solver.this) 
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
	
	private class AutoGenerateSolutionsListener extends SteppingAdapter implements PropertyChangeListener
	{

		@Override
		public void statesBounded(IBoundsManager boundsManager)
		{
			if (autoGenerateSolutions) 
			{
				solutionGenerator.generateSolutions(true);
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

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (autoGenerateSolutions && boundsManager.isCompleted()) 
			{
				solutionGenerator.generateSolutions(false);
			}			
		}
	}
}
