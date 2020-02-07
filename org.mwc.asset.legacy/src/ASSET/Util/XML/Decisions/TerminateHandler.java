
package ASSET.Util.XML.Decisions;

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

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Terminate;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;

abstract public class TerminateHandler extends CoreDecisionHandler {

	private final static String type = "Terminate";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final Terminate bb = (Terminate) toExport;

		// first output the parent bits
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		parent.appendChild(thisPart);

	}

	public TerminateHandler() {
		super(type);
	}

	@Override
	public void elementClosed() {
		final CoreDecision ev = new Terminate();

		super.setAttributes(ev);

		// finally output it
		setModel(ev);
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}