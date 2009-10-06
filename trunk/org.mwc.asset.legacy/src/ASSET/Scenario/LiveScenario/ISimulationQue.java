package ASSET.Scenario.LiveScenario;

import java.util.Vector;

import MWC.Algorithms.LiveData.IAttribute;

public interface ISimulationQue
{

	/**
	 * access my collection of simulations
	 * 
	 * @return
	 */
	public abstract Vector<ISimulation> getSimulations();

	/**
	 * common set of attributes for all of the simulations
	 * 
	 * @return
	 */
	public abstract Iterable<IAttribute> getAttributes();

	/**
	 * start the que running
	 * 
	 */
	public abstract void startQue();

	/**
	 * whether the que is running
	 * 
	 * @return yes/no
	 */
	public abstract boolean isRunning();

	/**
	 * stop the que
	 * 
	 */
	public abstract void stopQue();

}