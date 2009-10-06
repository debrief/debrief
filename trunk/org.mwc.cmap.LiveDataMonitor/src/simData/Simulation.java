package simData;

import java.util.Vector;

public abstract class Simulation implements ISimulation
{

	/**
	 * the state of this simulation
	 * 
	 */
	private Attribute _state;
	
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

	public Simulation(String name)
	{
		_name = name;

		// declare our state object
		_state = new Attribute("State", true);
		_state.fireUpdate(getTime(), Simulation.WAITING);
		
		_time = new Attribute("Time", true);
		_time.fireUpdate(getTime(), 0);

	}

	@Override
	public Vector<IAttribute> getAttributes()
	{
		Vector<IAttribute> res = new Vector<IAttribute>(0, 1);
		res.add(_time);
		res.add(_state);
		return res;
	}

	@Override
	public String getName()
	{
		return _name;
	}

	@Override
	public void start()
	{
		_state.fireUpdate(getTime(), RUNNING);
	}

	@Override
	public void stop()
	{
		_state.fireUpdate(getTime(), TERMINATED);
	}

	/**
	 * simulation is complete, inform listeners
	 * 
	 */
	protected void complete()
	{
		_state.fireUpdate(getTime(), COMPLETE);
	}

	@Override
	public long getTime()
	{
		return _currentTime;
	}

	@Override
	public IAttribute getState()
	{
		return _state;
	}

	@Override
	public String toString()
	{
		return this.getName();
	}

}
