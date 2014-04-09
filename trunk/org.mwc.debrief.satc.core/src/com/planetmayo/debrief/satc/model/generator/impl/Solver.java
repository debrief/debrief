package com.planetmayo.debrief.satc.model.generator.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.ModelObject;
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

public class Solver extends ModelObject implements ISolver
{
	private static final long serialVersionUID = 1L;

	private String name;
	
	private final IContributions contributions;
	private final IBoundsManager boundsManager;
	private final ISolutionGenerator solutionGenerator;
	@SuppressWarnings("unused")
	private final IJobsManager jobsManager;	
	
	private final ProblemSpace problemSpace;
	
	/**
	 * whether we auto=run after each contribution change
	 * 
	 */
	private volatile boolean liveRunning = true;
	
	private volatile boolean isClear = false;
	
	private LiveRunningListener liveRunningListener;
	private BoundsManagerToSolutionGeneratorBinding boundsManagerListener;
	
	/**
	 * the set of contribution properties that we're interested in
	 * 
	 */
	private final String[] propertiesToRestartBoundsManager =
	{ BaseContribution.ACTIVE, BaseContribution.START_DATE,
			BaseContribution.FINISH_DATE, BaseContribution.HARD_CONSTRAINTS };	
	
	public Solver(String name, IContributions contributions, ProblemSpace problemSpace,
			IBoundsManager boundsManager,	ISolutionGenerator solutionGenerator, 
			IJobsManager jobsManager)
	{
		super();
		this.name = name;
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
		boundsManagerListener = new BoundsManagerToSolutionGeneratorBinding();
		boundsManager.addConstrainSpaceListener(boundsManagerListener);
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public void setName(String name)
	{
		this.name = name;
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
		boolean old = liveRunning;
		liveRunning = checked;
		firePropertyChange(LIVE_RUNNING, old, checked);
	}

	@Override
	public boolean isLiveRunning()
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
		VehicleType old = type;
		boundsManager.setVehicleType(type);
		firePropertyChange(VEHICLE_TYPE, old, type);		
	}

	@Override
	public VehicleType getVehicleType()
	{
		return problemSpace.getVehicleType();
	}
	
	@Override
	public void setPrecision(Precision precision)
	{
		Precision old = precision;
		solutionGenerator.setPrecision(precision);
		firePropertyChange(PRECISION, old, precision);
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
	public synchronized void run(boolean constraint, boolean generate)
	{
		if (constraint) 
		{
			boundsManager.restart();
			boundsManager.run();
		}
		if (generate && boundsManager.isCompleted())
		{
			solutionGenerator.generateSolutions(true);
		}
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
			boundsManager.removeConstrainSpaceListener(boundsManagerListener);
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
			boundsManager.addConstrainSpaceListener(boundsManagerListener);
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
					LogFactory.getLog().error(
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
	
	private class BoundsManagerToSolutionGeneratorBinding extends SteppingAdapter
	{

		@Override
		public void statesBounded(IBoundsManager boundsManager)
		{
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
