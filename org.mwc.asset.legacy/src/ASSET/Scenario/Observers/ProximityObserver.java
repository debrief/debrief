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
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Genetic.ScenarioRunner;
import ASSET.Scenario.Observers.Summary.BatchCollator;
import ASSET.Scenario.Observers.Summary.BatchCollatorHelper;
import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;
import MWC.GenericData.WorldDistance;

/**
 * listener which keeps tally of how close a particular type of vessel gets to
 * another indicated type of vessel
 */
public class ProximityObserver extends CoreObserver
		implements ScenarioObserver.ScenarioReferee, BatchCollator, IAttribute, ScenarioSteppedListener {
	// ////////////////////////////////////////////////
	// property listings
	// ////////////////////////////////////////////////
	static public class ProximityObserverInfo extends EditorType {

		/**
		 * constructor for editable details
		 *
		 * @param data the object we're going to edit
		 */
		public ProximityObserverInfo(final ProximityObserver data) {
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
				final PropertyDescriptor[] res = { prop("Name", "the name of this observer"),
						prop("TargetType", "the participant we're measuring range from"),
						prop("WatchType", "the type of participant to monitor"), };
				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////
	//
	// ////////////////////////////////////////////////
	public static class StopOnProximityObserver extends ProximityObserver {
		// ////////////////////////////////////////////////
		// editable properties
		// ////////////////////////////////////////////////
		static public class StopOnProximityObserverInfo extends EditorType {

			/**
			 * constructor for editable details
			 *
			 * @param data the object we're going to edit
			 */
			public StopOnProximityObserverInfo(final StopOnProximityObserver data) {
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
					final PropertyDescriptor[] res = { prop("Name", "the name of this observer"),
							prop("Range", "the range at which to stop"),
							prop("TargetType", "the participant we're measuring range from"),
							prop("WatchType", "the type of participant to monitor"), };
					return res;
				} catch (final IntrospectionException e) {
					e.printStackTrace();
					return super.getPropertyDescriptors();
				}
			}
		}

		/**
		 * the range within which we stop the scenario
		 */
		private WorldDistance _cutOffRange;

		private EditorType _myEditor11;

		/**
		 * constructor - get going.
		 *
		 * @param watchType   the type of vessel to monitor
		 * @param targetType  the type of vessel it's looking for
		 * @param cutOffRange the range at which we stop the scenario
		 */
		public StopOnProximityObserver(final TargetType watchType, final TargetType targetType,
				final WorldDistance cutOffRange, final String myName, final boolean isActive) {
			super(watchType, targetType, myName, isActive);

			_cutOffRange = cutOffRange;
		}

		/**
		 * get the editor for this item
		 *
		 * @return the BeanInfo data for this editable object
		 */
		@Override
		public EditorType getInfo() {
			if (_myEditor11 == null)
				_myEditor11 = new StopOnProximityObserverInfo(this);

			return _myEditor11;
		}

		public WorldDistance getRange() {
			return _cutOffRange;
		}

		// ////////////////////////////////////////////////
		// property editing
		// ////////////////////////////////////////////////

		/**
		 * ok, we know the range from this target. handle it
		 *
		 * @param rng thje current range (in degrees)
		 */
		@Override
		protected void handleThisRange(final ScenarioType scenario, final long time, final double rng) {
			// do the super case
			super.handleThisRange(scenario, time, rng);

			// hmm, are we active?
			if (this.getVisible()) {
				final double distDegs = _cutOffRange.getValueIn(WorldDistance.DEGS);

				if (rng <= distDegs) {
					// ok, call a stop
					stopScenario();
				}
			}
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

		public void setRange(final WorldDistance cutOffRange) {
			this._cutOffRange = cutOffRange;
		}

		/**
		 * stop the scenario - we've peaked.
		 */
		protected void stopScenario() {
			_myScenario.stop("Stopped on proximity:" + getName());
		}

	}

	/**
	 * running tally of fitness score
	 */
	private double _myScore = -1;

	/**
	 * the vessels we're watching
	 */
	private final Vector<ParticipantType> _watchVessels = new Vector<ParticipantType>(0, 1);

	/**
	 * the targets we're concerned about proximity to
	 */
	private final Vector<ParticipantType> _targetVessels = new Vector<ParticipantType>(0, 1);

	/**
	 * the target type for vessels we're watching
	 */
	private TargetType _watchType = null;

	// ////////////////////////////////////////////////
	// inter-scenario observers
	// ////////////////////////////////////////////////

	/**
	 * the target type for target vessels for the target we're watching
	 */
	private TargetType _targetType = null;

	/**
	 * our batch collator
	 */
	private BatchCollatorHelper _batcher = null;

	/**
	 * whether to override (cancel) writing per-scenario results to file
	 */
	private boolean _onlyBatch = true;

	private EditorType _myEditor1;

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public ProximityObserver(final TargetType watchType, final TargetType targetType, final String myType,
			final boolean isActive) {
		super(myType, isActive);
		// remember the target types
		_watchType = watchType;
		_targetType = targetType;
	}

	/**
	 * add any applicable listeners
	 */
	@Override
	protected void addListeners(final ScenarioType scenario) {
		// listen to the scenario stepping
		scenario.addScenarioSteppedListener(this);
	}

	// ////////////////////////////////////////////////
	// inter-scenario observer methods
	// ////////////////////////////////////////////////
	@Override
	public void finish() {
		if (_batcher != null) {
			// ok, get the batch thingy to do it's stuff
			_batcher.writeOutput(getHeaderInfo());
		}
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/

	/**
	 * accessor to retrieve batch processing settings
	 */
	@Override
	public BatchCollatorHelper getBatchHelper() {
		return _batcher;
	}

	/**
	 * whether to override (cancel) writing per-scenario results to file
	 *
	 * @return whether to override batch processing
	 */
	@Override
	public boolean getBatchOnly() {
		return _onlyBatch;
	}

	@Override
	public DataDoublet getCurrent(final Object index) {
		return getAttributeHelper().getCurrent(index);
	}

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
			_myEditor1 = new ProximityObserverInfo(this);

		return _myEditor1;
	}

	/**
	 * define the filename for the batch output
	 *
	 * @return
	 */
	private String getMySuffix() {
		return "csv";
	}

	/**
	 * return how well this scenario performed, according to this referee
	 */
	@Override
	public ScenarioRunner.ScenarioOutcome getOutcome() {
		final ScenarioRunner.ScenarioOutcome res = new ScenarioRunner.ScenarioOutcome();
		res.score = MWC.Algorithms.Conversions.Degs2Yds(_myScore);
		res.summary = getSummary();
		return res;
	}

	/**
	 * get a text description of the outcome
	 */
	public String getSummary() {
		return "Distance:" + (int) MWC.Algorithms.Conversions.Degs2Yds(_myScore);
	}

	/**
	 * get the types of vessel whose proximity we are checking for (targets)
	 */
	public TargetType getTargetType() {
		return _targetType;
	}

	@Override
	public String getUnits() {
		return "degs";
	}

	/**
	 * get the types of vessel we are monitoring
	 */
	public TargetType getWatchType() {
		return _watchType;
	}

	/**
	 * ok, we know the range from this target. handle it
	 *
	 * @param rng  thje current range (in degrees)
	 * @param rng2
	 */
	protected void handleThisRange(final ScenarioType scenario, final long time, final double rng) {
		// is this the first range?
		if (_myScore == -1) {
			_myScore = rng;
		} else
			_myScore = Math.min(_myScore, rng);

		// tell the attribute helper
		getAttributeHelper().newData(scenario, time, _myScore);
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
	public void initialise(final File outputDirectory) {
		// set the output directory for the batch collator
		if (_batcher != null)
			_batcher.setDirectory(outputDirectory);
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
		// do we have a batcher?
		// are we recording to batch?
		if (_batcher != null) {
			_batcher.submitResult(_myScenario.getName(), _myScenario.getCaseId(),
					(int) MWC.Algorithms.Conversions.Degs2m(_myScore));
		}
		// clear out lists
		_targetVessels.removeAllElements();
		_watchVessels.removeAllElements();

		// reset the score
		_myScore = -1;
	}

	/**
	 * we're getting up and running. The observers have been created and we've
	 * remembered the scenario
	 *
	 * @param scenario the new scenario we're looking at
	 */
	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {
		// find any vessels we're interested in which are already in the scenario
		final Integer[] lst = scenario.getListOfParticipants();
		for (int thisI = 0; thisI < lst.length; thisI++) {
			final Integer thisIndex = lst[thisI];
			if (thisIndex != null) {
				final ASSET.ParticipantType thisP = scenario.getThisParticipant(thisIndex.intValue());

				// is this of our target category?
				if (_targetType.matches(thisP.getCategory())) {
					_targetVessels.add(thisP);
				}

				// is this of our watched category?
				if (_watchType.matches(thisP.getCategory())) {
					_watchVessels.add(thisP);
				}
			}
		}
	}

	/**
	 * remove any listeners
	 */
	@Override
	protected void removeListeners(final ScenarioType scenario) {
		// stop listening to the scenario
		scenario.removeScenarioSteppedListener(this);
	}

	/**
	 * the scenario has restarted
	 */
	@Override
	public void restart(final ScenarioType scenario) {
		super.restart(scenario);

		_myScore = -1;
	}

	/**
	 * configure the batch processing
	 *
	 * @param fileName          the filename to write to
	 * @param collationMethod   how to collate the data
	 * @param perCaseProcessing whether to collate the stats on a per-case basis
	 * @param isActive          whether this collator is active
	 */
	@Override
	public void setBatchCollationProcessing(String fileName, final String collationMethod,
			final boolean perCaseProcessing, final boolean isActive) {
		_batcher = new BatchCollatorHelper(getName(), perCaseProcessing, collationMethod, isActive, "range (metres)");

		// do we have a filename?
		if (fileName == null)
			fileName = getName() + "." + getMySuffix();

		_batcher.setFileName(fileName);
	}

	/**
	 * whether to override (cancel) writing per-scenario results to file
	 *
	 * @param override
	 */
	@Override
	public void setBatchOnly(final boolean override) {
		_onlyBatch = override;
	}

	/**
	 * set the types of vessel we are looking for
	 *
	 * @param targetType
	 */
	public void setTargetType(final TargetType targetType) {
		this._targetType = targetType;
	}

	/**
	 * set the types of vessel we are monitoring
	 */
	public void setWatchType(final TargetType watchType) {
		this._watchType = watchType;
	}

	/**
	 * the scenario has stepped forward
	 */
	@Override
	public void step(final ScenarioType scenario, final long newTime) {

		// step through our watch vessels
		final Iterator<ParticipantType> thisV = _watchVessels.iterator();
		while (thisV.hasNext()) {
			final NetworkParticipant thisWatch = thisV.next();

			// and through our target vessels
			final Iterator<ParticipantType> thisW = _targetVessels.iterator();
			while (thisW.hasNext()) {
				final NetworkParticipant thisTarget = thisW.next();

				// check they're not the same vessel
				if (thisTarget != thisWatch) {
					// just double-check this target still exists
					if (scenario.getThisParticipant(thisTarget.getId()) != null) {

						// find the range
						final double rng = thisTarget.getStatus().getLocation()
								.rangeFrom(thisWatch.getStatus().getLocation());

						// ok. we know this range. handle it
						handleThisRange(scenario, newTime, rng);
					}
				}
			}
		}
	}

}
