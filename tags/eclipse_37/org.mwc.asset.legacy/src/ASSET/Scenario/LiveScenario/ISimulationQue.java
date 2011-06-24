package ASSET.Scenario.LiveScenario;

import java.io.InputStream;
import java.io.PrintStream;
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
	public Vector<ISimulation> getSimulations();

	/**
	 * common set of attributes for all of the simulations
	 * 
	 * @return
	 */
	public Vector<IAttribute> getAttributes();

	/**
	 * how to listen for the state of the simulations changing
	 * 
	 * @return
	 */
	public IAttribute getState();

	/**
	 * start the que running
	 * 
	 */
	public void startQue(NewScenarioListener newListener);

	/**
	 * whether the que is running
	 * 
	 * @return yes/no
	 */
	public boolean isRunning();

	/**
	 * stop the que
	 * 
	 */
	public void stopQue();

	/**
	 * perform a set of runs
	 * 
	 * @param out
	 * @param err
	 * @param in
	 * @param object
	 */
	public int nowRun(PrintStream out, PrintStream err, InputStream in,
			NewScenarioListener scenarioListener);

}