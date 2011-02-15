/**
 * 
 */
package ASSET.Scenario.LiveScenario;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.GUI.CommandLine.NewScenarioListener;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioRunningListener;
import MWC.Algorithms.LiveData.Attribute;
import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;

/**
 * maintain a que of simulations
 * 
 * @author ianmayo
 * 
 */
public class SimulationQue implements ISimulationQue
{

	/**
	 * the que we manage
	 * 
	 */
	Vector<ISimulation> _mySimulations;
	
	Attribute _theState;

	/**
	 * the thread that fires off the simulations
	 * 
	 */
	private runThread runThread;

	private Vector<IAttribute> _myAttrs;

	public SimulationQue(Vector<ISimulation> simulations, Vector<IAttribute> attrs)
	{
		_mySimulations = simulations;
		_myAttrs = attrs;
		_theState = new Attribute("State", "n/a", true);
		
		// store the simulations
		for (final ISimulation sim : simulations)
		{
			// put it in it's initial state
			_theState.fireUpdate(sim, sim.getTime(), ISimulation.WAITING);
			
			// and listen out for it
			CoreScenario scen = (CoreScenario) sim;
			scen.addScenarioRunningListener(new ScenarioRunningListener(){

				public void finished(long elapsedTime, String reason)
				{
					_theState.fireUpdate(sim, elapsedTime, ISimulation.COMPLETE);
				}

				public void newScenarioStepTime(int val)
				{
				}

				public void newStepTime(int val)
				{
				}

				public void paused()
				{
					_theState.fireUpdate(sim, sim.getTime(), ISimulation.TERMINATED);

				}

				public void restart(ScenarioType scenario)
				{
				}

				public void started()
				{
					_theState.fireUpdate(sim, sim.getTime(), ISimulation.RUNNING);

				}});
		}
	}

	/* (non-Javadoc)
	 * @see ASSET.Scenario.LiveScenario.ISimulationQue#getSimulations()
	 */
	public Vector<ISimulation> getSimulations()
	{
		return _mySimulations;
	}

	/* (non-Javadoc)
	 * @see ASSET.Scenario.LiveScenario.ISimulationQue#getAttributes()
	 */
	public Vector<IAttribute> getAttributes() {
		return _myAttrs;
	}

	/* (non-Javadoc)
	 * @see ASSET.Scenario.LiveScenario.ISimulationQue#startQue()
	 */
	public void startQue(NewScenarioListener listener)
	{
		runThread = new runThread();
		runThread.start();
	}

	/* (non-Javadoc)
	 * @see ASSET.Scenario.LiveScenario.ISimulationQue#isRunning()
	 */
	public boolean isRunning()
	{
		return runThread.isAlive();
	}

	/* (non-Javadoc)
	 * @see ASSET.Scenario.LiveScenario.ISimulationQue#stopQue()
	 */
	public void stopQue()
	{
		runThread.interrupt();
	}

	/**
	 * class that handles running the simulations
	 * 
	 */
	private class runThread extends Thread
	{

		@Override
		public void run()
		{
			boolean worthRunning = true;

			// is it worth us bothering?
			while (worthRunning)
			{
				Iterator<ISimulation> iter = _mySimulations.iterator();
				while (iter.hasNext())
				{
					worthRunning = false;
					
					// get the next simulation
					ISimulation thisS = iter.next();

					// what's its state?
					String thisState = _theState.getCurrent(thisS).getValue().toString();

					// check the state
					if (thisState == MockSimulation.RUNNING)
					{
						worthRunning = true;
						// right there's one running, let's just leave it
						break;
					}
					else if (thisState == MockSimulation.WAITING)
					{
						worthRunning = true;
						// right this one's waiting to start - get it going
						thisS.start();

						// done. start from the beginning of the scenarios again
						break;
					}

				}

				// give ourselves a rest
				try
				{
					Thread.sleep(200);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	/** convenient little listener that tracks the state of simulations - mostly just for testing
	 * 
	 * @author ianmayo
	 *
	 */
	private static class StateListener implements PropertyChangeListener
	{
		private ISimulation _thisSim;

		public StateListener(ISimulation thisSim)
		{
			_thisSim = thisSim;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			DataDoublet data = (DataDoublet) evt.getNewValue();
			System.out.println(_thisSim.getName() + " is now " + data.getValue());
		}

	}
	
	private static void dumpThis(IAttribute theAttribute, Object index)
	{
		System.out.println("================");
		Vector<DataDoublet> list = theAttribute.getHistoricValues(index);
		for (Iterator<DataDoublet> iterator = list.iterator(); iterator.hasNext();)
		{
			DataDoublet thisOne = iterator.next();
			if (thisOne != null)
				System.out.println(" at " + thisOne.getTime() + " value of "
						+ theAttribute.getName() + " is " + thisOne.getValue() + " " + theAttribute.getUnits());
		}
	}


	/** sample implementation of simulation que
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("Working");

		long runTime = 6000;

		Vector<IAttribute> attrs = new Vector<IAttribute>();
		IAttribute att1 = new Attribute("Height","m", true);
		attrs.add(att1);
		IAttribute att2 = new Attribute("Speed", "kts", false);
		attrs.add(att2);


		
		Vector<ISimulation> shortQue = new Vector<ISimulation>();
		for (int i = 0; i < 5; i++)
		{
			MockSimulation m1 = new MockSimulation("sim_" + i, runTime, attrs);
			shortQue.add(m1);
		}

		// create the que
		ISimulationQue que = new SimulationQue(shortQue, attrs);

		// listen out for changes
		for (ISimulation iSimulation : shortQue)
		{
			que.getState().addPropertyChangeListener(new StateListener(iSimulation));
		}

		// get it going
		que.startQue(null);

		// wait until the simulation is complete
		while (que.isRunning())
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		// have a look at the data
		dumpThis(att1, shortQue.elementAt(0));
		dumpThis(att1, shortQue.elementAt(1));
		dumpThis(att2, shortQue.elementAt(1));
	}

	public IAttribute getState()
	{
		return _theState;
	}

	public int nowRun(PrintStream out, PrintStream err, InputStream in,
			NewScenarioListener scenarioListener)
	{
		startQue(scenarioListener);
		return 0;
	}


}