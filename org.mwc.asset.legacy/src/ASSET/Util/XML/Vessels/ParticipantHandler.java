
package ASSET.Util.XML.Vessels;

import ASSET.NetworkParticipant;
import ASSET.ScenarioType;
import ASSET.Models.MovementType;
import ASSET.Util.XML.Decisions.WaterfallHandler;
import ASSET.Util.XML.Vessels.Util.RadiatedCharsHandler;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

abstract public class ParticipantHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	public static final String NAME = "Name";
	public static final String PAINT_DECISIONS = "PaintDecisions";
	public static final String MONTE_CARLO_TARGET = "MonteCarloTarget";
	public static final String IS_ALIVE = "isAlive";

	private static int checkId(final int theID) {
		int res = theID;

		// if the index is zero, we will create one
		if (res == ScenarioType.INVALID_ID)
			res = ASSET.Util.IdNumber.generateInt();

		return res;
	}

	static void exportThis(final Object toExport, final org.w3c.dom.Element thisElement,
			final org.w3c.dom.Document doc) {

		final ASSET.ParticipantType part = (ASSET.ParticipantType) toExport;

		thisElement.setAttribute("Name", part.getName());

		// participant category
		ASSET.Util.XML.Vessels.Util.CategoryHandler.exportThis(part.getCategory(), thisElement, doc);

		// sensor list
		ASSET.Util.XML.Sensors.SensorFitHandler.exportThis(part, thisElement, doc);

		// current status
		ASSET.Util.XML.Vessels.Util.StatusHandler.exportThis(part.getStatus(), thisElement, doc);

		// current demanded status
		ASSET.Util.XML.Vessels.Util.DemandedStatusHandler.exportThis(part.getDemandedStatus(), thisElement, doc);

		// decision model
		final ASSET.Models.DecisionType dec = part.getDecisionModel();

		if (dec instanceof ASSET.Models.Decision.Switch)
			ASSET.Util.XML.Decisions.SwitchHandler.exportThis(dec, thisElement, doc);
		else if (dec instanceof ASSET.Models.Decision.Sequence)
			ASSET.Util.XML.Decisions.SequenceHandler.exportSequence(dec, thisElement, doc);
		else if (dec instanceof ASSET.Models.Decision.Waterfall)
			ASSET.Util.XML.Decisions.WaterfallHandler.exportThis(dec, thisElement, doc);

		// radiated noise characteristics
		ASSET.Util.XML.Vessels.Util.RadiatedCharsHandler.exportThis(part.getRadiatedChars(), thisElement, doc);

		// radiated noise characteristics
		RadiatedCharsHandler.exportThis(part.getSelfNoise(), thisElement, doc);

		// movement model
		final ASSET.Models.MovementType mover = part.getMovementModel();

		// start with the most specific instance first
		if (mover instanceof ASSET.Models.Movement.SSKMovement)
			ASSET.Util.XML.Movement.SSKMovementHandler.exportThis(mover, thisElement, doc);
		else if (mover instanceof ASSET.Models.Movement.CoreMovement)
			ASSET.Util.XML.Movement.MovementHandler.exportThis(mover, thisElement, doc);

	}

	String _myName;
	boolean _isMonteCarlo = false;
	Boolean _paintDecisions = null;
	int _myId = ASSET.ScenarioType.INVALID_ID;
	ASSET.Participants.Category _myCategory;
	ASSET.Participants.Status _myStatus;
	ASSET.Participants.DemandedStatus _myDemandedStatus;
	ASSET.Models.Sensor.SensorList _mySensorList;
	ASSET.Models.DecisionType _myDecisionModel;
	ASSET.Models.Vessels.Radiated.RadiatedCharacteristics _myRads;

	ASSET.Models.Vessels.Radiated.RadiatedCharacteristics _mySelfNoise;
	boolean _isAlive = true;

	// MOVEMENT CHARACTERISTICS - retrieved by child classes
	protected ASSET.Models.Movement.MovementCharacteristics _myMoveChars;

	protected MovementType _myMovement;

	ParticipantHandler(final String type) {
		// inform our parent what type of class we are
		super(type);

		super.addAttributeHandler(new HandleAttribute("id") {
			@Override
			public void setValue(final String name, final String val) {
				_myId = Integer.parseInt(val);
			}
		});

		super.addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_myName = val;
			}
		});

		super.addAttributeHandler(new HandleBooleanAttribute(MONTE_CARLO_TARGET) {
			@Override
			public void setValue(final String name, final boolean val) {
				_isMonteCarlo = val;
			}
		});

		super.addAttributeHandler(new HandleBooleanAttribute(IS_ALIVE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_isAlive = val;
			}
		});

		super.addAttributeHandler(new HandleBooleanAttribute(PAINT_DECISIONS) {
			@Override
			public void setValue(final String name, final boolean val) {
				_paintDecisions = val;
			}
		});

		// add the readers for participant properties
		addHandler(new ASSET.Util.XML.Vessels.Util.CategoryHandler() {
			@Override
			public void setCategory(final ASSET.Participants.Category cat) {
				_myCategory = cat;
			}
		});

		addHandler(new ASSET.Util.XML.Vessels.Util.StatusHandler() {
			@Override
			public void setStatus(final ASSET.Participants.Status stat) {
				_myStatus = stat;
			}
		});
		addHandler(new ASSET.Util.XML.Vessels.Util.DemandedStatusHandler() {
			@Override
			public void setDemandedStatus(final ASSET.Participants.DemandedStatus stat) {
				_myDemandedStatus = stat;
			}
		});
		addHandler(new ASSET.Util.XML.Sensors.SensorFitHandler() {
			@Override
			public void setSensorFit(final ASSET.Models.Sensor.SensorList list) {
				_mySensorList = list;
			}
		});
		addHandler(new ASSET.Util.XML.Decisions.WaterfallHandler(WaterfallHandler.MAX_CHAIN_DEPTH) {
			@Override
			public void setModel(final ASSET.Models.DecisionType chain) {
				_myDecisionModel = chain;
			}
		});
		addHandler(new ASSET.Util.XML.Decisions.SequenceHandler(WaterfallHandler.MAX_CHAIN_DEPTH) {
			@Override
			public void setModel(final ASSET.Models.DecisionType chain) {
				_myDecisionModel = chain;
			}
		});
		addHandler(new ASSET.Util.XML.Decisions.SwitchHandler(WaterfallHandler.MAX_CHAIN_DEPTH) {
			@Override
			public void setModel(final ASSET.Models.DecisionType chain) {
				_myDecisionModel = chain;
			}
		});

		addHandler(new ASSET.Util.XML.Vessels.Util.RadiatedCharsHandler() {
			@Override
			public void setRadiation(final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics chars) {
				_myRads = chars;
			}
		});
		addHandler(new ASSET.Util.XML.Vessels.Util.SelfNoiseHandler() {
			@Override
			public void setRadiation(final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics chars) {
				_mySelfNoise = chars;
			}
		});
		addHandler(new ASSET.Util.XML.Vessels.Util.MovementHandler() {
			@Override
			public void setMovement(final ASSET.Models.MovementType movement) {
				_myMovement = movement;
			}
		});

	}

	abstract public void addThis(ASSET.ParticipantType part, boolean isMonteCarlo);

	@Override
	public void elementClosed() {
		_myId = checkId(_myId);

		// get this instance
		final ASSET.ParticipantType thisPart = getParticipant(_myId);

		// add in the attributes we have observed
		thisPart.setName(_myName);
		thisPart.setCategory(_myCategory);

		// update the id
		_myStatus.setId(_myId);

		thisPart.setStatus(_myStatus);
		thisPart.setDemandedStatus(_myDemandedStatus);
		if (_mySensorList != null)
			thisPart.setSensorFit(_mySensorList);
		thisPart.setDecisionModel(_myDecisionModel);
		thisPart.setMovementChars(_myMoveChars);
		thisPart.setRadiatedChars(_myRads);
		thisPart.setSelfNoise(_mySelfNoise);

		thisPart.setAlive(_isAlive);

		if (_paintDecisions != null)
			thisPart.setPaintDecisions(_paintDecisions);

		if (_myMovement != null)
			thisPart.setMovementModel(_myMovement);

		// allow the child classes to finish off the participant
		finishParticipant(thisPart);

		// store in the parent
		addThis(thisPart, _isMonteCarlo);

		// clear local vars
		_myId = ASSET.ScenarioType.INVALID_ID;
		_isAlive = true;
		_paintDecisions = null;
		_isMonteCarlo = false;
		_myName = null;
		_myCategory = null;
		_myStatus = null;
		_myDemandedStatus = null;
		_mySensorList = null;
		_myDecisionModel = null;
		_myMoveChars = null;
		_myRads = null;
		_mySelfNoise = null;

	}

	/**
	 * extra method provided to allow child classes to interrupt the participant
	 * creation process
	 */
	void finishParticipant(final NetworkParticipant newPart) {
	}

	abstract protected ASSET.ParticipantType getParticipant(int index);

}