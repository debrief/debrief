
package ASSET.Util.XML.Control;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ASSET.Scenario.Observers.ScenarioObserver;

abstract public class StandaloneObserverListHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {
	public final static String type = "StandaloneObserverList";

	public static void exportThis(final Vector<ScenarioObserver> list, final Element parent, final Document doc) {
		// create ourselves
		final Element sens = doc.createElement(type);

		// and the list
		ASSET.Util.XML.Control.Observers.ObserverListHandler.exportThis(list, sens, doc);

		parent.appendChild(sens);

	}

	private Vector<ScenarioObserver> _myList;

	public StandaloneObserverListHandler() {
		// inform our parent what type of class we are
		super(type);

		addHandler(new ASSET.Util.XML.Control.Observers.ObserverListHandler() {
			@Override
			public void setObserverList(final Vector<ScenarioObserver> list) {
				_myList = new Vector<ScenarioObserver>(list);
			}
		});
	}

	@Override
	public void elementClosed() {
		setObserverList(_myList);

		_myList = null;
	}

	abstract public void setObserverList(Vector<ScenarioObserver> list);

}