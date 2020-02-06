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

package ASSET.GUI.Workbench.Plotters;

import java.util.Enumeration;
import java.util.HashMap;

import ASSET.ScenarioType;
import ASSET.Models.Vessels.SSN;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.Duration;

/**
 * class providing scenario plotting, together with scenario controls
 */

public class ScenarioLayer extends MWC.GUI.BaseLayer implements ASSET.Scenario.ParticipantsChangedListener {

	public static class LayerListenerTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			final SSN ssn = new SSN(12);
			final Editable listener = new ScenarioParticipantWrapper(ssn, null);
			return listener;
		}
	}

	//////////////////////////////////////////////////
	// testing support
	//////////////////////////////////////////////////
	public static class LayerTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			final ScenarioLayer layer = new ScenarioLayer();
			final CoreScenario cs = new CoreScenario();
			if (cs.getName() == null) {
				cs.setName("Scenario");
			}
			layer.setScenario(cs);
			layer.setStepTime(new Duration(12, Duration.SECONDS));
			layer.setScenarioStepTime(new Duration(12, Duration.SECONDS));
			return layer;
		}

		public void testSetScenario() {
			final ScenarioType scen1 = new CoreScenario();
			scen1.setName("test scenario");
			scen1.createNewParticipant(ASSET.Participants.Category.Type.SUBMARINE);
			scen1.createNewParticipant(ASSET.Participants.Category.Type.FRIGATE);

			super.assertEquals("Wrong number of participants", 2, scen1.getListOfParticipants().length);

			// check it doesn't have any listeners

			// sort out the listeners
			final ScenarioLayer sl = new ScenarioLayer();
			sl.setScenario(scen1);

			// check the listeners are assigned
			super.assertEquals("Wrong number of part listeners", 2, sl.getData().size());

			// and try another
			final ScenarioType scen2 = new CoreScenario();
			scen2.setName("test scenario 2");
			scen2.createNewParticipant(ASSET.Participants.Category.Type.SUBMARINE);
			scen2.createNewParticipant(ASSET.Participants.Category.Type.FRIGATE);
			scen2.createNewParticipant(ASSET.Participants.Category.Type.FRIGATE);

			sl.setScenario(scen2);

			// check there are no listeners to the old sceanrio
			super.assertEquals("Wrong number of part listeners", 3, sl.getData().size());

		}
	}

	/***************************************************************
	 * editable data for this plotter
	 ***************************************************************/
	////////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the plotter
	////////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public class ScenarioPlotterInfo extends Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public ScenarioPlotterInfo(final ScenarioLayer data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop(SHOW_SYMBOL, "show symbol for participants"),
						prop(SHOW_NAME, "show name for participants"),
						prop(SHOW_STATUS, "show the current vessel status"),
						prop(SHOW_ACTIVITY, "show current activity for participants"),
						prop("StepTime", "time interval between auto steps"),
						prop("ScenarioStepTime", "model step time"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	public static final String SHOW_SYMBOL = "ShowSymbol";

	public static final String SHOW_STATUS = "ShowStatus";

	public static final String SHOW_NAME = "ShowName";

	public static final String SHOW_ACTIVITY = "ShowActivity";

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * keep our own little register of symbols for participant types - the method to
	 * retreive the symbol for a participant type is a compleicated one
	 */
	static private HashMap<String, PlainSymbol> _mySymbolRegister = new HashMap<String, PlainSymbol>();

	/**
	 * @return the _mySymbolRegister
	 */
	public static final HashMap<String, PlainSymbol> get_mySymbolRegister() {
		return _mySymbolRegister;
	}

	/**
	 * @param symbolRegister the _mySymbolRegister to set
	 */
	public static final void set_mySymbolRegister(final HashMap<String, PlainSymbol> symbolRegister) {
		_mySymbolRegister = symbolRegister;
	}

	/***********************************************************************
	 * member variables
	 ***********************************************************************/
	/**
	 * the scenario we are plotting
	 */
	private ASSET.ScenarioType _myScenario = null;

	/**
	 * whether to plot symbols
	 */
	private boolean _plotSymbol = true;

	/**
	 * whether to plot the current behaviour
	 */
	private boolean _plotBehaviour = true;

	/**
	 * whether to plot the name of the participant
	 */
	private boolean _plotName = false;

	/**
	 * whether to plot the current status
	 */
	private boolean _plotStatus = false;

	/**
	 * ********************************************************************
	 * constructor
	 * *********************************************************************
	 */
	public ScenarioLayer() {
		super.setName("Scenario");
	}

	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new ScenarioPlotterInfo(this);

		return _myEditor;
	}

	public ScenarioType getScenario() {
		return _myScenario;
	}

	public Duration getScenarioStepTime() {
		return new Duration(_myScenario.getScenarioStepTime(), Duration.MILLISECONDS);
	}

	public boolean getShowActivity() {
		return _plotBehaviour;
	}

	public boolean getShowName() {
		return _plotName;
	}

	public boolean getShowStatus() {
		return _plotStatus;
	}

	public boolean getShowSymbol() {
		return _plotSymbol;
	}

	public Duration getStepTime() {
		return new Duration(_myScenario.getStepTime(), Duration.MILLISECONDS);
	}

	public HashMap<String, PlainSymbol> getSymbolRegister() {
		return _mySymbolRegister;
	}

	/**
	 * ********************************************************************
	 * BaseLayer support
	 * *********************************************************************
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	/**
	 * the indicated participant has been added to the scenario
	 */
	@Override
	public void newParticipant(final int index) {
		final ScenarioParticipantWrapper pl = new ScenarioParticipantWrapper(_myScenario.getThisParticipant(index),
				this);
		super.add(pl);
		pl.startListen();
	}

	/**
	 * the indicated participant has been removed from the scenario
	 */
	@Override
	public void participantRemoved(final int index) {
		final java.util.Enumeration<Editable> enumer = super.elements();

		// rememer the id
		ScenarioParticipantWrapper theListener = null;

		// find the wrapper for this participant
		while (enumer.hasMoreElements()) {
			final ScenarioParticipantWrapper pl = (ScenarioParticipantWrapper) enumer.nextElement();
			if (pl.getId() == index) {
				theListener = pl;
				break;
			}
		}

		// did we find it?
		if (theListener != null) {
			// yup, delete it.
			theListener.stopListen();
			super.removeElement(theListener);
		}

	}

	/**
	 * the scenario has restarted
	 */
	@Override
	public void restart(final ScenarioType scenario) {
		// reset our plottables
		final Enumeration<Editable> it = super.elements();
		while (it.hasMoreElements()) {
			final ScenarioParticipantWrapper pl = (ScenarioParticipantWrapper) it.nextElement();
			pl.restart(scenario);
		}
	}

	/**
	 * ******************************************************************** member
	 * variables
	 * *********************************************************************
	 */
	public void setScenario(final ASSET.ScenarioType scenario) {
		// and update our name
		this.setName(scenario.getName());

		// do we cancel listening to old scenario?
		if (_myScenario != null) {
			_myScenario.removeParticipantsChangedListener(this);

			// also stop listening to the scenario's children
			// loop through to catch any existing participants
			final Integer[] inds = _myScenario.getListOfParticipants();
			for (int i = 0; i < inds.length; i++) {
				participantRemoved(inds[i]);
			}
		}

		_myScenario = scenario;
		_myScenario.addParticipantsChangedListener(this);

		// loop through to catch any existing participants
		final Integer[] inds = _myScenario.getListOfParticipants();
		for (int i = 0; i < inds.length; i++) {
			newParticipant(inds[i]);
		}

		// see if any of the display settings have been initialised
		String doIt = scenario.getDisplaySettingFor(SHOW_ACTIVITY);
		if (doIt != null)
			_plotBehaviour = Boolean.valueOf(doIt);
		doIt = scenario.getDisplaySettingFor(SHOW_NAME);
		if (doIt != null)
			_plotName = Boolean.valueOf(doIt);
		doIt = scenario.getDisplaySettingFor(SHOW_STATUS);
		if (doIt != null)
			_plotStatus = Boolean.valueOf(doIt);
		doIt = scenario.getDisplaySettingFor(SHOW_SYMBOL);
		if (doIt != null)
			_plotSymbol = Boolean.valueOf(doIt);

	}

	public void setScenarioStepTime(final Duration val) {
		_myScenario.setScenarioStepTime((int) val.getValueIn(Duration.MILLISECONDS));
	}

	public void setShowActivity(final boolean val) {
		_plotBehaviour = val;
	}

	public void setShowName(final boolean val) {
		_plotName = val;
	}

	public void setShowStatus(final boolean plotStatus) {
		this._plotStatus = plotStatus;
	}

	public void setShowSymbol(final boolean val) {
		_plotSymbol = val;
	}

	public void setStepTime(final Duration val) {
		_myScenario.setStepTime((int) val.getValueIn(Duration.MILLISECONDS));
	}

}