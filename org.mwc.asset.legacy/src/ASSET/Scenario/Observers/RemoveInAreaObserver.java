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

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Participants.Category;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Genetic.ScenarioRunner;
import ASSET.Scenario.Observers.Summary.BatchCollator;
import ASSET.Scenario.Observers.Summary.BatchCollatorHelper;
import Debrief.Wrappers.LabelWrapper;
import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;
import MWC.GUI.CanvasType;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class RemoveInAreaObserver extends CoreObserver
		implements ScenarioObserver.ScenarioReferee, BatchCollator, IAttribute, ScenarioSteppedListener {
	static public class RemoveInAreaObserverInfo extends EditorType {

		/**
		 * constructor for editable details
		 *
		 * @param data the object we're going to edit
		 */
		public RemoveInAreaObserverInfo(final RemoveInAreaObserver data) {
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
						prop("WatchType", "the type of participant to monitor"), };
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

	protected int _numDitched = 0;

	private Vector<LabelWrapper> _myDeadParts;
	private final boolean _plotTheDead = true;
	private final WorldArea _myArea;

	private TargetType _myWatch;

	/**
	 * whether to override (cancel) writing per-scenario results to file
	 */
	private boolean _onlyBatch = true;

	private EditorType _myEditor1;
	/**
	 * our batch collator
	 */
	private BatchCollatorHelper _batcher = null;
	private int _myScore;

	private final ArrayList<ParticipantType> _watchVessels;

	/***************************************************************
	 * member methods
	 ***************************************************************/

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public RemoveInAreaObserver(final TargetType watchVessel, final WorldArea targetArea, final String myName,
			final boolean isActive) {
		super(myName, isActive);
		_myArea = targetArea;
		_myWatch = watchVessel;
		_watchVessels = new ArrayList<ParticipantType>();
	}

	/**
	 * add any applicable listeners
	 */
	@Override
	protected void addListeners(final ScenarioType scenario) {
		// listen to the scenario stepping
		scenario.addScenarioSteppedListener(this);
	}

	/**
	 * ok, we know the range from this target. handle it
	 *
	 * @param rng  thje current range (in degrees)
	 * @param rng2
	 */
	protected void ditchThese(final ScenarioType scenario, final long time, final ArrayList<NetworkParticipant> parts) {
		final Iterator<NetworkParticipant> it = parts.iterator();
		while (it.hasNext()) {
			final NetworkParticipant part = it.next();
			_myScore++;
			// tell the attribute helper
			getAttributeHelper().newData(scenario, time, _myScore);

			// and remove him
			final WorldLocation loc = part.getStatus().getLocation();
			final Color hisColor = Category.getColorFor(part.getCategory());
			final LabelWrapper lw = new LabelWrapper(part.getName(), loc, hisColor);
			lw.setSymbolType("Circle");

			if (_myDeadParts == null)
				_myDeadParts = new Vector<LabelWrapper>(0, 1);

			_myDeadParts.add(lw);

			_watchVessels.remove(part);

			scenario.removeParticipant(part.getId());
		}

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
			_myEditor1 = new RemoveInAreaObserverInfo(this);

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
		res.score = _myScore;
		res.summary = getSummary();
		return res;
	}

	/**
	 * get a text description of the outcome
	 */
	public String getSummary() {
		return "Number:" + _myScore;
	}

	@Override
	public String getUnits() {
		return "participants";
	}

	/**
	 * get the types of vessel we are monitoring
	 */
	public TargetType getWatchType() {
		return _myWatch;
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

	@Override
	public void paint(final CanvasType dest) {
		if (_plotTheDead) {
			if (_myDeadParts != null) {
				final Object[] labels = _myDeadParts.toArray();
				for (int i = 0; i < labels.length; i++) {
					final LabelWrapper labelWrapper = (LabelWrapper) labels[i];
					labelWrapper.paint(dest);
				}
			}
		}
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
		_watchVessels.clear();

		// reset the score
		_myScore = 0;
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

				// is this of our watched category?
				if (_myWatch.matches(thisP.getCategory())) {
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
	 * set the types of vessel we are monitoring
	 */
	public void setWatchType(final TargetType watchType) {
		this._myWatch = watchType;
	}

	/**
	 * the scenario has stepped forward
	 */
	@Override
	public void step(final ScenarioType scenario, final long newTime) {

		ArrayList<NetworkParticipant> parts = null;

		// step through our watch vessels
		final Iterator<ParticipantType> thisV = _watchVessels.iterator();
		while (thisV.hasNext()) {
			final NetworkParticipant thisWatch = thisV.next();

			if (_myArea.contains(thisWatch.getStatus().getLocation())) {
				{
					if (parts == null)
						parts = new ArrayList<NetworkParticipant>();

					parts.add(thisWatch);
				}
			}
		}
		if (parts != null)
			ditchThese(scenario, newTime, parts);
	}

}