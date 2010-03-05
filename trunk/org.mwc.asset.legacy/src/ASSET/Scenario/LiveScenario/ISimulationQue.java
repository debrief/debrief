package ASSET.Scenario.LiveScenario;

import java.util.Vector;

import ASSET.GUI.CommandLine.NewScenarioListener;
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
	public abstract Vector<IAttribute> getAttributes();
	
	/**  how to listen for the state of the simulations changing
	 * 
	 * @return
	 */
	public IAttribute getState();

	/**
	 * start the que running
	 * 
	 */
	public abstract void startQue(NewScenarioListener newListener);

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