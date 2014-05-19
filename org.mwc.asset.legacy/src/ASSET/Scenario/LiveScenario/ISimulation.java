package ASSET.Scenario.LiveScenario;


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
	 * instruct the simulation to start/resume moving forward
	 * 
	 */
	public void start();

	/**
	 * prematurely terminate the simulation
	 * 
	 */
	public void stop();
	
	/** don't finish the scenario, just pause it
	 * 
	 */
	public void pause();

	/**
	 * retrieve the current model time
	 * 
	 * @return
	 */
	public long getTime();

	/**
	 * move this scenario forward
	 * 
	 */
	public void step();

}
