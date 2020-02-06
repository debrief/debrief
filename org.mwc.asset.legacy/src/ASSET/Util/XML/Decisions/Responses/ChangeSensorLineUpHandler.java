
package ASSET.Util.XML.Decisions.Responses;

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

import ASSET.Models.Decision.Responses.ChangeSensorLineUp;
import ASSET.Models.Decision.Responses.Response;
import ASSET.Models.Environment.EnvironmentType;

public class ChangeSensorLineUpHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "ChangeSensorLineUp";

	private final static String MEDIUM = "Medium";
	private final static String SWITCH_ON = "SwitchOn";

	public static EnvironmentType.MediumPropertyEditor _myEditor = new EnvironmentType.MediumPropertyEditor();

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ChangeSensorLineUp bb = (ChangeSensorLineUp) toExport;

		// output it's attributes
		thisPart.setAttribute("Name", bb.getName());
		thisPart.setAttribute(SWITCH_ON, writeThis(bb.getSwitchOn()));

		_myEditor.setIndex(bb.getMedium());
		thisPart.setAttribute(MEDIUM, _myEditor.getAsText());

		parent.appendChild(thisPart);

	}

	int _medium;

	String _name;

	boolean _switchOn;

	public ChangeSensorLineUpHandler() {
		super("ChangeSensorLineUp");

		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String val) {
				_name = val;
			}
		});

		addAttributeHandler(new HandleAttribute(MEDIUM) {
			@Override
			public void setValue(final String name, final String val) {
				_myEditor.setValue(val);
				_medium = _myEditor.getIndex();
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SWITCH_ON) {
			@Override
			public void setValue(final String name, final boolean val) {
				_switchOn = val;
			}
		});

	}

	@Override
	public void elementClosed() {
		final Response ml = new ASSET.Models.Decision.Responses.ChangeSensorLineUp(_medium, _switchOn);
		ml.setName(_name);

		// finally output it
		setResponse(ml);

		// and reset
		_name = null;
	}

	public void setResponse(final Response dec) {
	}

}