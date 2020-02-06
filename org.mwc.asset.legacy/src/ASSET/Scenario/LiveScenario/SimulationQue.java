/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

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
public class SimulationQue implements ISimulationQue {

	/**
	 * class that handles running the simulations
	 *
	 */
	private class runThread extends Thread {

		@Override
		public void run() {
			boolean worthRunning = true;

			// is it worth us bothering?
			while (worthRunning) {
				final Iterator<ISimulation> iter = _mySimulations.iterator();
				while (iter.hasNext()) {
					worthRunning = false;

					// get the next simulation
					final ISimulation thisS = iter.next();

					// what's its state?
					final String thisState = _theState.getCurrent(thisS).getValue().toString();

					// check the state
					if (thisState.equals(ISimulation.RUNNING)) {
						worthRunning = true;
						// right there's one running, let's just leave it
						break;
					} else if (thisState.equals(ISimulation.WAITING)) {
						worthRunning = true;
						// right this one's waiting to start - get it going
						thisS.start();

						// done. start from the beginning of the scenarios again
						break;
					}

				}

				// give ourselves a rest
				try {
					Thread.sleep(200);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * convenient little listener that tracks the state of simulations - mostly just
	 * for testing
	 *
	 * @author ianmayo
	 *
	 */
	private static class StateListener implements PropertyChangeListener {
		private final ISimulation _thisSim;

		public StateListener(final ISimulation thisSim) {
			_thisSim = thisSim;
		}

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			final DataDoublet data = (DataDoublet) evt.getNewValue();
			System.out.println(_thisSim.getName() + " is now " + data.getValue());
		}

	}

	private static void dumpThis(final IAttribute theAttribute, final Object index) {
		System.out.println("================");
		final Vector<DataDoublet> list = theAttribute.getHistoricValues(index);
		for (final Iterator<DataDoublet> iterator = list.iterator(); iterator.hasNext();) {
			final DataDoublet thisOne = iterator.next();
			if (thisOne != null)
				System.out.println(" at " + thisOne.getTime() + " value of " + theAttribute.getName() + " is "
						+ thisOne.getValue() + " " + theAttribute.getUnits());
		}
	}

	/**
	 * sample implementation of simulation que
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		System.out.println("Working");

		final long runTime = 6000;

		final Vector<IAttribute> attrs = new Vector<IAttribute>();
		final IAttribute att1 = new Attribute("Height", "m", true);
		attrs.add(att1);
		final IAttribute att2 = new Attribute("Speed", "kts", false);
		attrs.add(att2);

		final Vector<ISimulation> shortQue = new Vector<ISimulation>();
		for (int i = 0; i < 5; i++) {
			final MockSimulation m1 = new MockSimulation("sim_" + i, runTime, attrs);
			shortQue.add(m1);
		}

		// create the que
		final ISimulationQue que = new SimulationQue(shortQue, attrs);

		// listen out for changes
		for (final ISimulation iSimulation : shortQue) {
			que.getState().addPropertyChangeListener(new StateListener(iSimulation));
		}

		// get it going
		que.startQue(null);

		// wait until the simulation is complete
		while (que.isRunning()) {
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		// have a look at the data
		dumpThis(att1, shortQue.elementAt(0));
		dumpThis(att1, shortQue.elementAt(1));
		dumpThis(att2, shortQue.elementAt(1));
	}

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

	private final Vector<IAttribute> _myAttrs;

	public SimulationQue(final Vector<ISimulation> simulations, final Vector<IAttribute> attrs) {
		_mySimulations = simulations;
		_myAttrs = attrs;
		_theState = new Attribute("State", "n/a", true);

		// store the simulations
		for (final ISimulation sim : simulations) {
			// put it in it's initial state
			_theState.fireUpdate(sim, sim.getTime(), ISimulation.WAITING);

			// and listen out for it
			final CoreScenario scen = (CoreScenario) sim;
			scen.addScenarioRunningListener(new ScenarioRunningListener() {

				@Override
				public void finished(final long elapsedTime, final String reason) {
					_theState.fireUpdate(sim, elapsedTime, ISimulation.COMPLETE);
				}

				@Override
				public void newScenarioStepTime(final int val) {
				}

				@Override
				public void newStepTime(final int val) {
				}

				@Override
				public void paused() {
					_theState.fireUpdate(sim, sim.getTime(), ISimulation.TERMINATED);

				}

				@Override
				public void restart(final ScenarioType scenario) {
				}

				@Override
				public void started() {
					_theState.fireUpdate(sim, sim.getTime(), ISimulation.RUNNING);

				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ASSET.Scenario.LiveScenario.ISimulationQue#getAttributes()
	 */
	@Override
	public Vector<IAttribute> getAttributes() {
		return _myAttrs;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ASSET.Scenario.LiveScenario.ISimulationQue#getSimulations()
	 */
	@Override
	public Vector<ISimulation> getSimulations() {
		return _mySimulations;
	}

	@Override
	public IAttribute getState() {
		return _theState;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ASSET.Scenario.LiveScenario.ISimulationQue#isRunning()
	 */
	@Override
	public boolean isRunning() {
		return runThread.isAlive();
	}

	@Override
	public int nowRun(final PrintStream out, final PrintStream err, final InputStream in,
			final NewScenarioListener scenarioListener) {
		startQue(scenarioListener);
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ASSET.Scenario.LiveScenario.ISimulationQue#startQue()
	 */
	@Override
	public void startQue(final NewScenarioListener listener) {
		runThread = new runThread();
		runThread.start();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ASSET.Scenario.LiveScenario.ISimulationQue#stopQue()
	 */
	@Override
	public void stopQue() {
		runThread.interrupt();
	}

}