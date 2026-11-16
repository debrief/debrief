
package ASSET.Scenario;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

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

import ASSET.ParticipantType;

public class MultiForceScenario extends CoreScenario {

	/**
	 * **************************************************************** wrapper to
	 * make MultiForce act like normal Scenario
	 * ****************************************************************
	 */
	private class ScenarioBlueWrapper extends MultiForceScenario {
		private final MultiForceScenario _scenario;

		public ScenarioBlueWrapper(final MultiForceScenario scenario) {
			_scenario = scenario;
		}

		/**
		 * Provide a list of id numbers of Participant we contain
		 *
		 * @return list of ids of Participant we contain
		 */
		@Override
		public Integer[] getListOfParticipants() {
			return _scenario.getListOfParticipants();
		}

		/**
		 * Return a particular Participant - so that the Participant can be controlled
		 * directly. Listeners added/removed. Participants added/removed, etc.
		 */
		@Override
		public ParticipantType getThisParticipant(final int id) {
			return _scenario.getThisParticipant(id);
		}
	}

	/**
	 * **************************************************************** wrapper to
	 * make MultiForce act like normal Scenario
	 * ****************************************************************
	 */
	private class ScenarioRedWrapper extends MultiForceScenario {
		private final MultiForceScenario _scenario;

		public ScenarioRedWrapper(final MultiForceScenario scenario) {
			_scenario = scenario;
		}

		/**
		 * Provide a list of id numbers of Participant we contain
		 *
		 * @return list of ids of Participant we contain
		 */
		@Override
		public Integer[] getListOfParticipants() {
			return _scenario.getListOfBlueParticipants();
		}

		/**
		 * Return a particular Participant - so that the Participant can be controlled
		 * directly. Listeners added/removed. Participants added/removed, etc.
		 */
		@Override
		public ParticipantType getThisParticipant(final int id) {
			return _scenario.getThisParticipant(id);
		}
	}

	/**
	 * the name of this scenario type
	 */
	public static final String TYPE = "SuperSearch";

	/**
	 * keep track of the blue force, the CoreScenario stores the red force
	 */
	private HashMap<Integer, ParticipantType> _blueForce;

	/**
	 * keep track of the "full" participants, from both sides (which we use for the
	 * Blue stepping forward)
	 */
	private final HashMap<Integer, ParticipantType> _fullForce;

	/**
	 * the list of blue participants changed listeners
	 */
	private Vector<ParticipantsChangedListener> _blueParticipantListeners;

	/**
	 * our blue wrapper, to make this look like a normal scenario for red
	 * participants
	 */
	private ScenarioBlueWrapper _blueWrapper = null;

	/**
	 * our red wrapper, to make this look like a normal scenario for red
	 * participants
	 */
	private ScenarioRedWrapper _redWrapper = null;

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */

	public MultiForceScenario() {
		_fullForce = new HashMap<Integer, ParticipantType>();
	}

	/***********************************************************************
	 *
	 ***********************************************************************/

	/**
	 * method to add a participant to the "blue" force
	 */
	public void addBlueParticipant(int index, final ASSET.ParticipantType participant) {

		if (index == 0)
			index = ASSET.Util.IdNumber.generateInt();

		// do we have list?
		if (_blueForce == null) {
			_blueForce = new java.util.HashMap<Integer, ParticipantType>();
		}

		// store it
		_blueForce.put(new Integer(index), participant);

		// also add it to the "full force" listing
		_fullForce.put(new Integer(index), participant);

		// fire new scenario event
		this.fireBlueParticipantChanged(index, true);
	}

	/////////////////////////////////////////////////////
	// manage our blue listeners
	/////////////////////////////////////////////////////
	public void addBlueParticipantsChangedListener(final ParticipantsChangedListener list) {
		if (_blueParticipantListeners == null)
			_blueParticipantListeners = new Vector<ParticipantsChangedListener>(1, 2);

		_blueParticipantListeners.add(list);
	}

	/**
	 * back door, for reloading a scenario from file)
	 */
	@Override
	public void addParticipant(int index, final ParticipantType participant) {
		if (index == 0)
			index = ASSET.Util.IdNumber.generateInt();

		// and add it to our "full" listing
		_fullForce.put(new Integer(index), participant);

		// add the particicpant to the parents (red) listing
		super.addParticipant(index, participant);
	}

	/**
	 * create the red force using the characteristics provided
	 */
	public void createRedForce(final String file, final String variance) {
		final ASSET.Scenario.SuperSearch.SuperSearch generator = new ASSET.Scenario.SuperSearch.SuperSearch();
		generator.setTemplate(file);
		generator.setVariance(variance);
		generator.setIdStart(1000);

		final ASSET.ParticipantType[] lst = generator.build();

		// store the time step
		this.setScenarioStepTime(generator.getTimeStep());

		// now add these participants to the red force
		for (int i = 0; i < lst.length; i++) {
			final ASSET.ParticipantType thisP = lst[i];
			thisP.setName(thisP.getName() + ":" + i);

			// initialise the time in the new participant
			thisP.getStatus().setTime(this.getTime());
			addParticipant(thisP.getId(), thisP);

		}
	}

	private void fireBlueParticipantChanged(final int index, final boolean added) {
		if (_blueParticipantListeners != null) {
			final Iterator<ParticipantsChangedListener> it = _blueParticipantListeners.iterator();
			while (it.hasNext()) {
				final ParticipantsChangedListener pcl = it.next();
				if (added)
					pcl.newParticipant(index);
				else
					pcl.participantRemoved(index);
			}
		}
	}

	/**
	 * Provide a list of id numbers of Participant we contain
	 *
	 * @return list of ids of Participant we contain
	 */
	protected Integer[] getListOfBlueParticipants() {
		Integer[] res = new Integer[0];

		if (_blueForce != null) {
			final java.util.Collection<Integer> vals = _blueForce.keySet();
			res = vals.toArray(res);
		}

		return res;
	}

	/**
	 * Provide a list of id numbers of Participant we contain
	 *
	 * @return list of ids of Participant we contain
	 */
	@Override
	public Integer[] getListOfParticipants() {
		Integer[] res = new Integer[_fullForce.size()];

		if (_blueForce != null) {
			final java.util.Collection<Integer> vals = _fullForce.keySet();
			res = vals.toArray(res);
		}

		return res;
	}

	/**
	 * Provide a list of id numbers of Participant we contain
	 *
	 * @return list of ids of Participant we contain
	 */
	protected Integer[] getListOfRedParticipants() {
		return super.getListOfParticipants();
	}

	////////////////////////////////////////////////////////
	// participant-related
	////////////////////////////////////////////////////////
	/**
	 * Return a particular Participant - so that the Participant can be controlled
	 * directly. Listeners added/removed. Participants added/removed, etc.
	 */
	@Override
	public ParticipantType getThisParticipant(final int id) {
		ParticipantType res = null;
		if (_fullForce != null)
			res = _fullForce.get(new Integer(id));

		return res;
	}

	/**
	 * Return a particular Participant - so that the Participant can be controlled
	 * directly. Listeners added/removed. Participants added/removed, etc.
	 */
	public ParticipantType getThisRedParticipant(final int id) {
		return super.getThisParticipant(id);
	}

	public void removeBlueParticipantsChangedListener(final ParticipantsChangedListener list) {
		_blueParticipantListeners.remove(list);
	}

	/**
	 * remove the indicated participant
	 */
	@Override
	public void removeParticipant(final int index) {
		// try to do it from the parent
		super.removeParticipant(index);

		// and try to do it from our full force

		// now remove it (try the full force first)
		final Integer bigI = new Integer(index);
		_fullForce.remove(bigI);

		// now remove it (try the blue force)
		final Object res2 = _blueForce.remove(bigI);
		if (res2 != null) {
			// fire new scenario event
			this.fireBlueParticipantChanged(index, false);
		}
	}

	/**
	 * Move the scenario through a single step
	 */
	@Override
	public void step() {

		if (_blueWrapper == null) {
			_blueWrapper = new ScenarioBlueWrapper(this);
			_redWrapper = new ScenarioRedWrapper(this);
		}

		final long oldTime = _myTime;

		// move time forward
		_myTime += _myScenarioStepTime;

		// move the blue participants forward
		if (_blueForce != null) {
			//////////////////////////////////////////////////
			// first the decision
			//////////////////////////////////////////////////
			java.util.Iterator<ParticipantType> iter = _blueForce.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				// pass it the parent's listof participants
				pt.doDecision(oldTime, _myTime, _blueWrapper);
			}

			//////////////////////////////////////////////////
			// now the movement
			//////////////////////////////////////////////////
			iter = _blueForce.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				// pass it the parent's listof participants
				pt.doMovement(oldTime, _myTime, _blueWrapper);
			}

			//////////////////////////////////////////////////
			// now the detection
			//////////////////////////////////////////////////
			iter = _blueForce.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				// pass it the parent's listof participants
				pt.doDetection(oldTime, _myTime, _blueWrapper);
			}
		}

		// now move the red participants forward
		if (_myVisibleParticipants != null) {
			//////////////////////////////////////////////////
			// first the decision
			//////////////////////////////////////////////////
			java.util.Iterator<ParticipantType> iter = _myVisibleParticipants.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				pt.doDecision(oldTime, _myTime, _redWrapper);
			}

			//////////////////////////////////////////////////
			// now the movement
			//////////////////////////////////////////////////
			iter = _myVisibleParticipants.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				pt.doMovement(oldTime, _myTime, _redWrapper);
			}

			//////////////////////////////////////////////////
			// now the detection
			//////////////////////////////////////////////////
			iter = _myVisibleParticipants.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				pt.doDetection(oldTime, _myTime, _redWrapper);
			}
		}

		// fire messages
		this.fireScenarioStepped(_myTime);
	}

}