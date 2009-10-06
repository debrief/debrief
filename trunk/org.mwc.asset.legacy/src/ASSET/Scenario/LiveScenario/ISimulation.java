package ASSET.Scenario.LiveScenario;

import java.util.Vector;

import MWC.Algorithms.LiveData.IAttribute;

public interface ISimulation
{
	/**
	 * series of state indicators
	 * 
	 */
	public final String WAITING = "Waiting";
	public final String RUNNING = "Running";
	public final String COMPLETE = "Complete";
	public final String TERMINATED = "Terminated";

	/**
	 * name of this simulation instance
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * collection of visible attributes for this instance
	 * 
	 * @return
	 */
	public Vector<IAttribute> getAttributes();

	/**
	 * instruct the simulation to start/resume moving forward
	 * 
	 */
	public void start();

	/**
	 * prematurely terminate the simulation
	 * 
	 */
	public void stop();

	/**
	 * retrieve the current model time
	 * 
	 * @return
	 */
	public long getTime();

	/**
	 * accessor for the state of this simulation
	 * 
	 * @return
	 */
	public IAttribute getState();

	/**
	 * move this scenario forward
	 * 
	 */
	public void step();

}
