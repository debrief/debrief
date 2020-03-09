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

package ASSET.Scenario.Observers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Scenario.LiveScenario.ISimulation;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;
import MWC.GUI.Editable;

/**
 * controller observer that stops running after indicated period
 *
 * @author ianmayo
 *
 */
public class ScenarioStatusObserver extends CoreObserver implements ASSET.Scenario.ScenarioSteppedListener, IAttribute {
	//////////////////////////////////////////////////
	// editable properties
	//////////////////////////////////////////////////
	static public class StatusObserverInfo extends EditorType {

		/**
		 * constructor for editable details
		 *
		 * @param data the object we're going to edit
		 */
		public StatusObserverInfo(final ScenarioStatusObserver data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name of this observer"), };
				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	/***************************************************************
	 * member variables
	 ***************************************************************/

	//////////////////////////////////////////////////
	// property testing
	//////////////////////////////////////////////////
	public static class TimeObsTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new ScenarioStatusObserver();
		}
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	private final HashMap<ScenarioType, ScenarioRunningListener> _runnerList;

	private EditorType _myEditor1;

	/***************************************************************
	 * constructor
	 ***************************************************************/
	/**
	 * default constructor - doesn't need much
	 *
	 */
	public ScenarioStatusObserver() {
		super("Status", true);

		_runnerList = new HashMap<ScenarioType, ScenarioRunningListener>();
	}

	/**
	 * add any applicable listeners
	 */
	@Override
	protected void addListeners(final ScenarioType scenario) {
		// and become a listener
		_myScenario.addScenarioSteppedListener(this);
	}

	@Override
	public DataDoublet getCurrent(final Object index) {
		// do we know about this object yet?
		final Object val = getAttributeHelper().getCurrent(index);
		if (val == null) {
			// aah. we probably haven't heard of it yet. set it up
			final ScenarioType scen = (ScenarioType) index;
			getAttributeHelper().newData(index, scen.getTime(), ISimulation.WAITING);
		}

		return getAttributeHelper().getCurrent(index);
	}

	//////////////////////////////////////////////////
	// property editing
	//////////////////////////////////////////////////

	@Override
	public Vector<DataDoublet> getHistoricValues(final Object index) {
		return getAttributeHelper().getValuesFor(index);
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public EditorType getInfo() {
		if (_myEditor1 == null)
			_myEditor1 = new StatusObserverInfo(this);

		return _myEditor1;
	}

	@Override
	public String getUnits() {

		return "n/a";
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	@Override
	public boolean isSignificant() {
		return true;
	}

	/**
	 * right, the scenario is about to close. We haven't removed the listeners or
	 * forgotten the scenario (yet).
	 *
	 * @param scenario the scenario we're closing from
	 */
	@Override
	protected void performCloseProcessing(final ScenarioType scenario) {
		final ScenarioRunningListener runner = _runnerList.get(scenario);
		scenario.removeScenarioRunningListener(runner);
	}

	/**
	 * we're getting up and running. The observers have been created and we've
	 * remembered the scenario
	 *
	 * @param scenario the new scenario we're looking at
	 */
	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {

		// create a fresh runner for each scenario
		final ScenarioRunningListener runner = new ScenarioRunningListener() {

			@Override
			public void finished(final long elapsedTime, final String reason) {
				getAttributeHelper().newData(scenario, elapsedTime, ISimulation.COMPLETE);
			}

			@Override
			public void newScenarioStepTime(final int val) {
			}

			@Override
			public void newStepTime(final int val) {
			}

			@Override
			public void paused() {
			}

			@Override
			public void restart(final ScenarioType scenario) {
			}

			@Override
			public void started() {
				System.out.println("STARTED");
				getAttributeHelper().newData(scenario, scenario.getTime(), ISimulation.RUNNING);

			}
		};

		_runnerList.put(scenario, runner);

		scenario.addScenarioRunningListener(runner);

		// initialise
		getAttributeHelper().newData(scenario, scenario.getTime(), ISimulation.WAITING);
	}

	/**
	 * remove any listeners
	 */
	@Override
	protected void removeListeners(final ScenarioType scenario) {
		// remove ourselves as a listener
		_myScenario.removeScenarioSteppedListener(this);
	}

	/**
	 * the scenario has stepped forward
	 */
	@Override
	public void step(final ScenarioType scenario, final long newTime) {
		// getAttributeHelper().newData(scenario, newTime, newTime);
	}

}
