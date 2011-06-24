package ASSET.Scenario.LiveScenario;

import MWC.Algorithms.LiveData.Attribute;

public abstract class Simulation implements ISimulation
{
	
	/** the current (watchable) time
	 * 
	 */
	protected Attribute _time;

	/**
	 * name of this simulation
	 * 
	 */
	private String _name;

	/**
	 * the current simulation time
	 * 
	 */
	private long _currentTime;
	
	protected boolean _isRunning = false;

	public Simulation(String name)
	{
		_name = name;

		_time = new Attribute("Time", "n/a", true);
		_time.fireUpdate(this, getTime(), 0);

	}

	public String getName()
	{
		return _name;
	}

	public void start()
	{
		_isRunning = true;
	}

	public void stop()
	{
		_isRunning = false;
	}

	/**
	 * simulation is complete, inform listeners
	 * 
	 */
	protected void complete()
	{
		_isRunning = false;
	}
	
	public boolean isRunning()
	{
		return _isRunning;
	}

	public long getTime()
	{
		return _currentTime;
	}

	@Override
	public String toString()
	{
		return this.getName();
	}

}
