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

package ASSET.Scenario;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Vessels.SSN;
import ASSET.Scenario.LiveScenario.ISimulation;
import ASSET.Util.RandomGenerator;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldLocation;

public class CoreScenario implements ScenarioType, ISimulation {


	/**
	 * The list of participants we maintain
	 */
	HashMap<Integer, ParticipantType> _myVisibleParticipants = new HashMap<Integer, ParticipantType>();

	/**
	 * The list of monte carlo participants we maintain
	 */
	HashMap<Integer, ParticipantType> _myInvisibleParticipants = new HashMap<Integer, ParticipantType>();

	/**
	 * the complete list of participants we store
	 */
	HashMap<Integer, ParticipantType> _completeParticipantList = new HashMap<Integer, ParticipantType>();

	/**
	 * the list of any participants which have been destroyed during a step - they
	 * are only actually removed at the end of the step - we store the index of any
	 * to be destroyed
	 */
	private final Vector<Integer> _pendingDestruction = new Vector<Integer>(0, 1);

	/**
	 * the list of any participants which have been created during a step - they are
	 * only actually added at the end of the step - we store the participant itself
	 */
	private final Vector<ParticipantType> _pendingCreation = new Vector<ParticipantType>(0, 1);

	/**
	 * the name of this scenario
	 */
	private String _myName;

	/**
	 * the case id of this scenario. The case id is used by multiple scenario
	 * generation algorithms to identify which permutation this scenario represents.
	 */
	private String _myCaseId;

	/**
	 * the step time for this scenario (millis)
	 */
	private int _myStepTime = 0;

	/**
	 * the elapsed time for this scenario (millis)
	 */
	long _myTime = 0;

	/**
	 * the initial value of time for this scenario
	 */
	private long _myStartTime = 0;

	/**
	 * the scenario time step for this scenario (millis)
	 */
	int _myScenarioStepTime = 1000;

	/**
	 * the list of stepping listeners
	 */
	private Vector<ScenarioSteppedListener> _stepListeners;

	/**
	 * the list of participants changed listeners
	 */
	private Vector<ParticipantsChangedListener> _participantListeners;

	/**
	 * the list of running listeners
	 */
	private Vector<ScenarioRunningListener> _runningListeners;

	/**
	 * the timer we are using
	 */
	private final javax.swing.Timer _myTimer;

	/**
	 * property change listener support
	 */
	private final java.beans.PropertyChangeSupport _pSupport;

	/**
	 * the environment type for our model
	 */
	private EnvironmentType _myEnvironment;

	/**
	 * the system time when we started running (to provide a report on how long it
	 * took..
	 */
	private long _systemStartTime = 0;

	/**
	 * a seed to set when first starting a scenario (or null to continue with a
	 * random series of randoms
	 */
	private Integer _mySeed = null;

	/**
	 * if we have been stopped, this is the reason why It's also a flag to indicate
	 * that somebody has triggered a scenario stop - note that we only process the
	 * stop at the end of a step
	 */
	protected String _stopReason = null;

	boolean _firstPass = true;

	/**
	 * the information we plot as a backdrop
	 *
	 */
	private BaseLayer _myBackdrop;

	private final HashMap<String, String> _displaySettings;

	// ////////////////////////////////////
	// constructor
	// ////////////////////////////////////
	public CoreScenario() {
		// initialise the timer
		_myTimer = new javax.swing.Timer(0, new java.awt.event.ActionListener() {
			private boolean stepping = false;

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
				if (stepping)
					System.err.println("STEPPING");

				stepping = true;
				step();
				stepping = false;
			}
		});

		_pSupport = new java.beans.PropertyChangeSupport(this);

		// create the environment
		_myEnvironment = createEnvironment();

		// give the scenario a random case id
		_myCaseId = "Case_" + (int) (Math.random() * 2000d);

		// initialise the display settings
		_displaySettings = new HashMap<String, String>();

	}

	/**
	 * store the specified display setting
	 *
	 * @param key
	 * @param value
	 */
	public void addDisplaySetting(final String key, final String value) {
		_displaySettings.put(key, value);
	}

	/**
	 * handle inserting a monte carlo participant
	 *
	 * @param index
	 * @param participant
	 */
	public void addMonteCarloParticipant(final int index, final ASSET.ParticipantType participant) {
		addParticipantToThisList(index, participant, _myInvisibleParticipants);

		// and the complete list
		addParticipantToThisList(index, participant, _completeParticipantList);

		// fire new scenario event
		this.fireParticipantChanged(index, true);
	}

	/**
	 * back door, for reloading a scenario from file)
	 */
	public void addParticipant(final int index, final ASSET.ParticipantType participant) {
		addParticipantToThisList(index, participant, _myVisibleParticipants);

		// and the complete list
		addParticipantToThisList(index, participant, _completeParticipantList);

		// fire new scenario event
		this.fireParticipantChanged(index, true);
	}

	// ///////////////////////////////////////////////////
	// manage our listeners
	// ///////////////////////////////////////////////////
	@Override
	public void addParticipantsChangedListener(final ParticipantsChangedListener list) {
		if (_participantListeners == null)
			_participantListeners = new Vector<ParticipantsChangedListener>(1, 2);

		_participantListeners.add(list);
	}

	/**
	 * convenience method for adding a participant to a hashmap
	 */
	private void addParticipantToThisList(int index, final ASSET.ParticipantType participant,
			final HashMap<Integer, ParticipantType> map) {
		// if the index is zero, we will create one
		if (index == INVALID_ID) {
			index = ASSET.Util.IdNumber.generateInt();

			// put this index into the state, if there is one
			if (participant.getStatus() != null)
				participant.getStatus().setId(index);
		}

		// do we contain this one already
		if (map.get(new Integer(index)) != null)
			System.err.println("DUPLICATE ENTITY BEING ADDED:!" + index);

		// store it
		map.put(new Integer(index), participant);

	}

	@Override
	public void addPropertyChangeListener(final String property_name, final PropertyChangeListener listener) {
		if (property_name == null)
			_pSupport.addPropertyChangeListener(listener);
		else
			_pSupport.addPropertyChangeListener(property_name, listener);
	}

	@Override
	public void addScenarioRunningListener(final ScenarioRunningListener listener) {
		if (_runningListeners == null)
			_runningListeners = new Vector<ScenarioRunningListener>(1, 2);

		// right, insert the new running listener at the head of the chain. This is
		// to overcome a problem with
		// the Loader class - which adds itself as a scenario listener before it's
		// children. When we STOP
		// the loader goes in and calls all of the tearDown methods for observers
		// which it has loaded, so ditching the _myScenario object - even though the
		// child observer actually
		// may want to listen out for the Stop event itself in order to output it's
		// batch (or other) data
		_runningListeners.add(0, listener);
	}

	@Override
	public void addScenarioSteppedListener(final ScenarioSteppedListener listener) {
		if (_stepListeners == null)
			_stepListeners = new Vector<ScenarioSteppedListener>(1, 2);

		_stepListeners.add(listener);
	}

	/**
	 * Shut down this scenario. Close the participants, etc
	 */
	@Override
	public void close() {
		//
		_myEnvironment = null;
		if (_myBackdrop != null)
			_myBackdrop.removeAllElements();
		if (_myInvisibleParticipants != null)
			_myInvisibleParticipants.clear();
		if (_myVisibleParticipants != null)
			_myVisibleParticipants.clear();
		_myName = "empty";
	}

	// ////////////////////////////////////
	// methods
	// ////////////////////////////////////
	private EnvironmentType createEnvironment() {
		return new SimpleEnvironment(1, 1, 1);
	}

	/**
	 * Create a new Participant. The external client can then request the
	 * Participant itself to perform any edits
	 *
	 * @param participant_type the type of Participant the client wants
	 * @return the id of the new Participant (or INVALID_ID for failure)
	 */
	@Override
	public int createNewParticipant(final String participant_type) {
		// what we should do, is to create the scenario from the scenario type name,
		// using the specified scenario_type in the classloader

		// get the id
		int id = ASSET.Util.IdNumber.generateInt();

		// create the correct type of participant
		ParticipantType newS = null;
		if (participant_type.equals(ASSET.Participants.Category.Type.SUBMARINE))
			newS = new ASSET.Models.Vessels.SSN(id);
		else if (participant_type.equals(ASSET.Participants.Category.Type.FRIGATE)) {
			newS = new ASSET.Models.Vessels.Surface(id);
			newS.getCategory().setType(ASSET.Participants.Category.Type.FRIGATE);
		} else if (participant_type.equals(ASSET.Participants.Category.Type.DESTROYER)) {
			newS = new ASSET.Models.Vessels.Surface(id);
			newS.getCategory().setType(ASSET.Participants.Category.Type.DESTROYER);
		} else if (participant_type.equals("SSK"))
			newS = new ASSET.Models.Vessels.SSK(id);

		// check it worked
		if (newS != null) {
			// give it a default name
			newS.setName(participant_type + "_" + id);

			// add it
			addParticipant(id, newS);
		} else {
			MWC.Utilities.Errors.Trace.trace("Vessel type not matched");
			id = INVALID_ID;
		}

		// return it's index
		return id;
	}

	/**
	 * method to add a new participant
	 *
	 * @param newPart the new participant
	 */
	@Override
	public void createParticipant(final ParticipantType newPart) {
		this._pendingCreation.add(newPart);
	}

	/**
	 * the detonation event itself
	 *
	 * @param loc   the location of the detonation
	 * @param power the strength of the detonation
	 */
	@Override
	public void detonationAt(final int WeaponID, final WorldLocation loc, final double power) {
		// @@ CREATE EXPLOSION MODEL

		// see if any participants are within some explosion radius (500 yds)
		final double EXPLOSION_RADIUS = 500;

		// move the participants forward
		if (_completeParticipantList != null) {
			final java.util.Iterator<ParticipantType> iter = _completeParticipantList.values().iterator();
			while (iter.hasNext()) {
				final NetworkParticipant pt = iter.next();
				// how far away is it?
				final double rng_degs = pt.getStatus().getLocation().rangeFrom(loc);
				final double rng_yds = MWC.Algorithms.Conversions.Degs2Yds(rng_degs);
				if (rng_yds < EXPLOSION_RADIUS) {
					this._pendingDestruction.add(new Integer(pt.getId()));
				}
			}
		}

	}

	/**
	 * Stop the scenario from auto-stepping
	 */
	private void doStop() {
		// remember the reason
		final String thisReason = _stopReason;

		// forget our local one, to be sure we aren't called twice
		_stopReason = null;

		// and store how long it took
		final long finishTime = System.currentTimeMillis();

		// is timer running
		if (_myTimer.isRunning())
			_myTimer.stop();

		// work out how long it took
		final long elapsedTime = finishTime - _systemStartTime;

		// fire event
		this.fireScenarioStopped(elapsedTime, thisReason);

		System.out.println("Scenario stopped:" + thisReason);
	}

	/**
	 * clear out the participants
	 *
	 */
	public void emptyParticipants() {
		// get their indices
		final Integer[] parts = getListOfParticipants();
		for (int i = 0; i < parts.length; i++) {
			removeParticipant(parts[i]);
		}
	}

	private void fireNewScenarioStepSize(final int val) {

		if (_runningListeners != null) {
			final Iterator<ScenarioRunningListener> it = _runningListeners.iterator();
			while (it.hasNext()) {
				final ScenarioRunningListener pcl = it.next();
				pcl.newScenarioStepTime(val);
			}
		}
	}

	private void fireNewStepSize(final int val) {

		if (_runningListeners != null) {
			final Iterator<ScenarioRunningListener> it = _runningListeners.iterator();
			while (it.hasNext()) {
				final ScenarioRunningListener pcl = it.next();
				pcl.newStepTime(val);
			}
		}
	}

	private void fireParticipantChanged(final int index, final boolean added) {
		if (_participantListeners != null) {
			final Iterator<ParticipantsChangedListener> it = _participantListeners.iterator();
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
	 * pass the message out to the listeners
	 */
	private void fireScenarioPaused() {

		if (_runningListeners != null) {
			final Iterator<ScenarioRunningListener> it = _runningListeners.iterator();
			while (it.hasNext()) {
				final ScenarioRunningListener pcl = it.next();
				pcl.paused();
			}
		}
	}

	private void fireScenarioStarted() {

		if (_runningListeners != null) {
			final Iterator<ScenarioRunningListener> it = _runningListeners.iterator();
			while (it.hasNext()) {
				final ScenarioRunningListener pcl = it.next();
				pcl.started();
			}
		}

	}

	void fireScenarioStepped(final long time) {
		if (_stepListeners != null) {
			// take copy of step listeners, in case it gets modified mid-step
			final Vector<ScenarioSteppedListener> copyStepListeners = new Vector<ScenarioSteppedListener>(
					_stepListeners);
			final Iterator<ScenarioSteppedListener> it = copyStepListeners.iterator();
			while (it.hasNext()) {
				final ScenarioSteppedListener pcl = it.next();
				try {
					pcl.step(this, time);
				} catch (final Exception e) {
					System.out.println("time:" + time + " pcl was:" + pcl);
					e.printStackTrace(); // To change body of catch statement use Options
											// | File Templates.
				}
			}
		}
	}

	/**
	 * pass the message out to the listeners
	 *
	 * @param timeTaken how long we ran for
	 * @param reason    the reason for stopping
	 */
	private void fireScenarioStopped(final long timeTaken, final String reason) {

		if (_runningListeners != null) {
			// take a deep copy of the running listeners - when we're cycling through
			// the list there's the chance that
			// a listener may try and remove itself. bugger
			Vector<ScenarioRunningListener> copyListeners = new Vector<ScenarioRunningListener>(0, 1);
			copyListeners.addAll(_runningListeners);

			final Iterator<ScenarioRunningListener> it = copyListeners.iterator();
			while (it.hasNext()) {
				final ScenarioRunningListener pcl = it.next();
				pcl.finished(timeTaken, reason);
			}

			// and ditch our spare copy.
			copyListeners.removeAllElements();
			copyListeners = null;
		}
	}

	@Override
	public Layer getBackdrop() {
		return _myBackdrop;
	}

	@Override
	public String getCaseId() {
		return _myCaseId;
	}

	@Override
	public String getDisplaySettingFor(final String key) {
		return _displaySettings.get(key);
	}

	/**
	 * get the environment for this model
	 */
	@Override
	public EnvironmentType getEnvironment() {
		return _myEnvironment;
	}

	/**
	 * Provide a list of id numbers of Participant we contain
	 *
	 * @return list of ids of Participant we contain
	 */
	@Override
	public Integer[] getListOfParticipants() {
		Integer[] res = new Integer[0];

		if (_completeParticipantList != null) {
			final java.util.Collection<Integer> vals = _completeParticipantList.keySet();
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
	public Collection<ParticipantType> getListOfVisibleParticipants() {
		Collection<ParticipantType> res = null;

		if (_myVisibleParticipants != null) {
			res = _myVisibleParticipants.values();
		}

		return res;
	}

	/**
	 * get the name of this scenaro
	 */
	@Override
	public String getName() {
		return _myName;
	}

	/**
	 * @return the scenario time step
	 */
	@Override
	public int getScenarioStepTime() {
		return _myScenarioStepTime;
	}

	/**
	 * @return the step time for auto-stepping (or zero to run on)
	 */
	@Override
	public int getStepTime() {
		return _myStepTime;
	}

	// //////////////////////////////////////////////////////
	// participant-related
	// //////////////////////////////////////////////////////
	/**
	 * Return a particular Participant - so that the Participant can be controlled
	 * directly. Listeners added/removed. Participants added/removed, etc.
	 */
	@Override
	public ParticipantType getThisParticipant(final int id) {
		ParticipantType res = null;
		if (_completeParticipantList != null)
			res = _completeParticipantList.get(new Integer(id));

		return res;
	}

	/**
	 * set the current time of the scenario (millis)
	 */
	@Override
	public long getTime() {
		return _myTime;
	}

	/**
	 * find out if the timer is currently auto-stepping
	 *
	 * @return
	 */
	@Override
	public boolean isRunning() {
		return _myTimer.isRunning();
	}

	/**
	 * pause the automatic execution (but don't assume that the scenario is
	 * complete)
	 */
	@Override
	public void pause() {
		// is timer running
		if (_myTimer.isRunning())
			_myTimer.stop();

		// fire event
		this.fireScenarioPaused();
	}

	/**
	 * remove the indicated participant
	 */
	@Override
	public void removeParticipant(final int index) {
		final Object found = _completeParticipantList.remove(new Integer(index));

		// fire new scenario event
		if (found != null) {
			// try to remove it from the other lists
			_myVisibleParticipants.remove(new Integer(index));
			_myInvisibleParticipants.remove(new Integer(index));

			// and fire the informant
			this.fireParticipantChanged(index, false);
		}
	}

	@Override
	public void removeParticipantsChangedListener(final ParticipantsChangedListener list) {
		_participantListeners.remove(list);
	}

	@Override
	public void removePropertyChangeListener(final String property_name, final PropertyChangeListener listener) {
		try {
			if (property_name == null)
				_pSupport.removePropertyChangeListener(listener);
			else
				_pSupport.removePropertyChangeListener(property_name, listener);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeScenarioRunningListener(final ScenarioRunningListener listener) {
		_runningListeners.remove(listener);
	}

	@Override
	public void removeScenarioSteppedListener(final ScenarioSteppedListener listener) {
		_stepListeners.remove(listener);
	}

	/**
	 * restart the scenario
	 */
	@Override
	public void restart() {
		// reset our local time values
		_myTime = _myStartTime;

		// clear the 'dead' flag
		_stopReason = null;

		// step through the participants
		// move the participants forward
		if (_completeParticipantList != null) {
			final java.util.Iterator<ParticipantType> iter = _completeParticipantList.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				pt.restart(this);
			}
		}

		// reset the observers

		// reset the listeners
		if (_participantListeners != null) {
			final Vector<ParticipantsChangedListener> tmpList = new Vector<ParticipantsChangedListener>(
					_participantListeners);
			final Iterator<ParticipantsChangedListener> it = tmpList.iterator();
			while (it.hasNext()) {
				final ParticipantsChangedListener pcl = it.next();
				pcl.restart(this);
			}
		}

		if (_runningListeners != null) {
			// work with a copy of the running listeners, to prevent concurrent
			// changes (since in the restart
			// a listener may remove then re-insert itself as a listener
			final Vector<ScenarioRunningListener> copyRunningListeners = new Vector<ScenarioRunningListener>(
					_runningListeners);
			final Iterator<ScenarioRunningListener> it = copyRunningListeners.iterator();
			while (it.hasNext()) {
				final ScenarioRunningListener prl = it.next();
				prl.restart(this);
			}
		}

		if (_stepListeners != null) {
			final Vector<ScenarioSteppedListener> copyStepListeners = new Vector<ScenarioSteppedListener>(
					_stepListeners);
			final Iterator<ScenarioSteppedListener> it = copyStepListeners.iterator();
			while (it.hasNext()) {
				final ScenarioSteppedListener ssl = it.next();
				ssl.restart(this);
			}
		}

	}

	@Override
	public void setBackdrop(final BaseLayer layer) {
		_myBackdrop = layer;
	}

	@Override
	public void setCaseId(final String myCaseId) {
		this._myCaseId = myCaseId;
	}

	/**
	 * and set the environment.
	 */
	@Override
	public void setEnvironment(final EnvironmentType theEnv) {
		_myEnvironment = theEnv;
	}

	/**
	 * set the name of this scenario
	 */
	@Override
	public void setName(final String val) {
		// store old value

		String oldVal = null;
		if (_myName != null)
			oldVal = new String(_myName);

		// fire the event
		_pSupport.firePropertyChange(NAME, oldVal, val);

		// do the update
		_myName = val;

	}

	/**
	 * set the scenario time step
	 *
	 * @param step_size time to step scenario forward at each step
	 */
	@Override
	public void setScenarioStepTime(final Duration step_size) {
		this.setScenarioStepTime((int) step_size.getValueIn(Duration.MILLISECONDS));
	}

	/**
	 * set the scenario time step
	 *
	 * @param step_size millis to step scenario forward at each step
	 */
	@Override
	public void setScenarioStepTime(final int step_size) {
		_myScenarioStepTime = step_size;

		// fire the new value
		fireNewScenarioStepSize(step_size);

	}

	/**
	 * set the seed to use for running this scenario
	 */
	@Override
	public void setSeed(final Integer seed) {
		_mySeed = seed;
	}

	/**
	 * set the size of the time delay (or zero to run to completion)
	 *
	 * @param step_size time to pause before step (or zero to run)
	 */
	@Override
	public void setStepTime(final Duration step_size) {
		this.setStepTime((int) step_size.getValueIn(Duration.MILLISECONDS));
	}

	/**
	 * set the size of the time delay (or zero to run to completion)
	 *
	 * @param step_size millis to pause before step (or zero to run)
	 */
	@Override
	public void setStepTime(final int step_size) {
		// store the new value
		_myStepTime = step_size;

		// update the timer
		_myTimer.setDelay(step_size);

		// fire the new value
		fireNewStepSize(step_size);
	}

	/**
	 * set the initial time of the scenario (millis)
	 */
	@Override
	public void setTime(final long time) {
		_myTime = time;
		_myStartTime = time;
	}

	/**
	 * Start the scenario auto-stepping through itself. If the step time is set to
	 * zero, it will automatically step after the completion of the previous step.
	 */
	@Override
	public void start() {
		// store the delay time
		_myTimer.setDelay(_myStepTime);

		// initialse the seed (if we have one)
		if (_mySeed != null)
			RandomGenerator.seed(_mySeed.intValue());

		// store the system start time
		_systemStartTime = System.currentTimeMillis();

		// fire a stepped event to let the recorders store the initial state
		// No, don't - it's causing the status to go out twice
		// fireScenarioStepped(this.getTime());

		if (!_myTimer.isRunning()) {
			// get it going
			_myTimer.start();
		}

		this.fireScenarioStarted();

	}

	/**
	 * Move the scenario through a single step
	 */
	@Override
	public void step() {

		// process a stop, if anybody has asked for it.
		if (_stopReason != null) {
			doStop();

			// and now drop out
			return;
		}

		final long oldTime = _myTime;

		if (!_firstPass) {
			// move time forward
			_myTime += _myScenarioStepTime;
		}

		// move the participants forward
		if (_completeParticipantList != null) {

			_firstPass = false;

			// ////////////////////////////////////////////////
			// first the decision cycle
			// ////////////////////////////////////////////////
			java.util.Iterator<ParticipantType> iter = _completeParticipantList.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();

				// note: we aren't using the isAlive test to decide whether to do decisions,
				// since it's the decision that brings it to life
				pt.doDecision(oldTime, _myTime, this);
			}

			// ////////////////////////////////////////////////
			// now the movement cycle
			// ////////////////////////////////////////////////
			iter = _completeParticipantList.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				if (pt.isAlive())
					pt.doMovement(oldTime, _myTime, this);
			}

			// ////////////////////////////////////////////////
			// now the detection cycle
			// ////////////////////////////////////////////////
			final HashMap<Integer, ParticipantType> copiedList = new HashMap<Integer, ParticipantType>(
					_completeParticipantList);
			iter = copiedList.values().iterator();
			while (iter.hasNext()) {
				final ParticipantType pt = iter.next();
				if (pt.isAlive())
					pt.doDetection(oldTime, _myTime, this);
			}
		}

		// now add any recently created participants
		for (int ic = 0; ic < this._pendingCreation.size(); ic++) {
			final ParticipantType thisPart = this._pendingCreation.elementAt(ic);
			addParticipant(thisPart.getId(), thisPart);
		}
		// and clear the list
		_pendingCreation.clear();

		// finally, delete the destroyed participants
		for (int i = 0; i < this._pendingDestruction.size(); i++) {
			final Integer thisIndex = this._pendingDestruction.elementAt(i);
			removeParticipant(thisIndex.intValue());
		}

		// and clear the list waiting to be destroyed
		_pendingDestruction.clear();

		// fire messages
		this.fireScenarioStepped(_myTime);

	}

	@Override
	public void stop() {
	}

	/**
	 * let anybody request that the scenario stop, though note that we only process
	 * this at the end of a step.
	 * <p/>
	 * It's the doStop method which actually stops us
	 *
	 * @param reason
	 */
	@Override
	public void stop(final String reason) {
		_stopReason = reason;
	}
}
