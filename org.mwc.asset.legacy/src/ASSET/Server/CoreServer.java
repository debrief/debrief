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

package ASSET.Server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.ServerType;

/**
 * The real implementation of a server
 */
public class CoreServer implements ServerType {

	/**
	 * create new, unique id for this participant
	 */
	private static int getId() {
		return ASSET.Util.IdNumber.generateInt();
	}

	////////////////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////
	// methods
	////////////////////////////////////////////////////////////////////////


	/**
	 */
	private HashMap<Integer, ScenarioType> _myScenarios;

	/**
	 * our list of listeners
	 */
	private Vector<ScenarioCreatedListener> _myCreatedListeners;

	/**
	 * add object as a listener for this type of event
	 */
	@Override
	public void addScenarioCreatedListener(final ScenarioCreatedListener listener) {
		// check we have the list
		if (_myCreatedListeners == null)
			_myCreatedListeners = new Vector<ScenarioCreatedListener>(0, 1);

		// add this listener
		_myCreatedListeners.add(listener);
	}

	/**
	 * destroy a scenario (calls the close() method on the scenario, which triggers
	 * the close)
	 */
	@Override
	public void closeScenario(final int index) {
		// check we have the scenario
		final ScenarioType sc = _myScenarios.get(new Integer(index));

		if (sc != null) {
			sc.close();
			_myScenarios.remove(new Integer(index));
		}

		// fire the destroyed event
		fireCreatedDestroyed(false, index);

	}

	/**
	 * Create a new scenario. The external client can then request the scenario
	 * itself to perform any edits
	 *
	 * @param scenario_type the type of scenario the client wants
	 * @return the id of the new scenario
	 */
	@Override
	public int createNewScenario(final String scenario_type) {
		// what we should do, is to create the scenario from the scenario type name,
		// using the specified scenario_type in the classloader

		ScenarioType newS = null;

		// check out which type we want
		if (scenario_type.equals(ASSET.Scenario.MultiForceScenario.TYPE)) {
			newS = new ASSET.Scenario.MultiForceScenario();
		} else {
			newS = new ASSET.Scenario.CoreScenario();
		}

		if (_myScenarios == null)
			_myScenarios = new java.util.HashMap<Integer, ScenarioType>();

		// get the id
		final int id = getId();

		// store it in our list
		_myScenarios.put(new Integer(id), newS);

		// fire new scenario event
		fireCreatedDestroyed(true, id);

		// return it's index
		return id;
	}

	/**
	 * fire the scenario created/destroyed event
	 */
	private void fireCreatedDestroyed(final boolean created, final int index) {
		if (_myCreatedListeners == null)
			return;

		final Iterator<ScenarioCreatedListener> it = _myCreatedListeners.iterator();
		while (it.hasNext()) {
			final ScenarioCreatedListener list = it.next();
			if (created)
				list.scenarioCreated(index);
			else
				list.scenarioDestroyed(index);
		}
	}

	/**
	 * Provide a list of id numbers of scenarios we contain
	 *
	 * @return list of ids of scenarios we contain
	 */
	@Override
	public Integer[] getListOfScenarios() {
		Integer[] res = new Integer[0];

		if (_myScenarios != null) {
			final java.util.Collection<Integer> vals = _myScenarios.keySet();
			res = vals.toArray(res);
		}

		return res;
	}

	/**
	 * Return a particular scenario - so that the scenario can be controlled
	 * directly. Listeners added/removed. Participants added/removed, etc.
	 */
	@Override
	public ScenarioType getThisScenario(final int id) {
		ScenarioType res = null;
		if (_myScenarios != null)
			res = _myScenarios.get(new Integer(id));

		return res;
	}

	/**
	 * remove listener for this type of event
	 */
	@Override
	public void removeScenarioCreatedListener(final ScenarioCreatedListener listener) {
		_myCreatedListeners.remove(listener);
	}

}
