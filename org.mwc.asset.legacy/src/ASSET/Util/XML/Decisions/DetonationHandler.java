
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

import ASSET.Models.Decision.Tactical.Detonate;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract class DetonationHandler extends CoreDecisionHandler {

	private final static String type = "Detonate";

	private final static String POWER = "DetonationPower";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final Detonate bb = (Detonate) toExport;

		// first the parent attributes
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// output it's attributes
		thisPart.setAttribute(POWER, writeThis(bb.getPower()));
		ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetType(), thisPart, doc);
		WorldDistanceHandler.exportDistance(bb.getDetonationRange(), thisPart, doc);

		parent.appendChild(thisPart);

	}

	WorldDistance _theRange;
	double _thePower;

	TargetType _myTargetType;

	public DetonationHandler() {
		super(type);

		addAttributeHandler(new HandleDoubleAttribute(POWER) {
			@Override
			public void setValue(final String name, final double val) {
				_thePower = val;
			}
		});

		addHandler(new WorldDistanceHandler() {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_theRange = res;
			}
		});
		addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler() {
			@Override
			public void setTargetType(final TargetType type) {
				_myTargetType = type;
			}
		});
	}

	@Override
	public void elementClosed() {
		final Detonate ev = new Detonate(_myTargetType, _theRange, _thePower);
		super.setAttributes(ev);
		// finally output it
		setModel(ev);
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}