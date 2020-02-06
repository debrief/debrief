
package ASSET.Util.XML.Decisions;

import ASSET.Models.Decision.TargetType;

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

import ASSET.Models.Decision.Movement.Evade;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class EvadeHandler extends CoreDecisionHandler {

	private final static String type = "Evade";

	private final static String HEIGHT = "FleeHeight";
	private final static String SPEED = "FleeSpeed";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Decision.Movement.Evade bb = (ASSET.Models.Decision.Movement.Evade) toExport;

		// first the parent
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// output it's attributes
		DurationHandler.exportDuration(bb.getFleePeriod(), thisPart, doc);
		ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetType(), thisPart, doc);

		WorldSpeedHandler.exportSpeed(SPEED, bb.getFleeSpeed(), thisPart, doc);
		WorldDistanceHandler.exportDistance(HEIGHT, bb.getFleeHeight(), thisPart, doc);

		parent.appendChild(thisPart);

	}

	Duration _fleePeriod;
	WorldSpeed _fleeSpeed;
	WorldDistance _fleeHeight;

	TargetType _myTargetType;

	public EvadeHandler() {
		super(type);

		addHandler(new WorldDistanceHandler(HEIGHT) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_fleeHeight = res;
			}
		});

		addHandler(new WorldSpeedHandler(SPEED) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_fleeSpeed = res;
			}
		});

		addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler() {
			@Override
			public void setTargetType(final ASSET.Models.Decision.TargetType type) {
				_myTargetType = type;
			}
		});
		addHandler(new DurationHandler() {
			@Override
			public void setDuration(final Duration res) {
				_fleePeriod = res;
			}
		});
	}

	@Override
	public void elementClosed() {
		final Evade ev = new Evade(_fleePeriod, _fleeSpeed, _fleeHeight, _myTargetType);

		super.setAttributes(ev);

		// finally output it
		setModel(ev);

		_fleePeriod = null;
		_fleeSpeed = null;
		_fleeHeight = null;
		_myTargetType = null;
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}