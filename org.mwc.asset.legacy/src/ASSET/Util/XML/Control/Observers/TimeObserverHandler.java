
package ASSET.Util.XML.Control.Observers;

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

import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.StopOnElapsedObserver;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

abstract class TimeObserverHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "TimeObserver";

	private final static String ACTIVE = "Active";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final StopOnElapsedObserver bb = (StopOnElapsedObserver) toExport;

		// output it's attributes
		thisPart.setAttribute("Name", bb.getName());
		thisPart.setAttribute(ACTIVE, writeThis(bb.isActive()));

		DurationHandler.exportDuration(bb.getElapsed(), thisPart, doc);

		// output it's attributes
		parent.appendChild(thisPart);

	}

	boolean _isActive;

	String _name;

	Duration _theDuration;

	public TimeObserverHandler() {
		super(type);

		addAttributeHandler(new HandleBooleanAttribute(ACTIVE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_isActive = val;
			}
		});

		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String val) {
				_name = val;
			}
		});

		addHandler(new DurationHandler() {
			@Override
			public void setDuration(final Duration res) {
				_theDuration = res;
			}
		});
	}

	@Override
	public void elementClosed() {
		// create ourselves
		final ScenarioObserver timeO = new StopOnElapsedObserver(_theDuration, _name, _isActive);

		setObserver(timeO);

		// and reset
		_name = null;
		_theDuration = null;
	}

	abstract public void setObserver(ScenarioObserver obs);

}