
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


import org.w3c.dom.Document;

import ASSET.Models.Decision.Responses.ManoeuvreToLocation;
import ASSET.Models.Decision.Responses.Response;
import ASSET.Util.XML.Utils.ASSETLocationHandler;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

public class ManoeuvreToLocationHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "ManoeuvreToLocation";
	private final static String SPEED = "Speed";

	private final static String LOCATION = "Location";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ManoeuvreToLocation bb = (ManoeuvreToLocation) toExport;

		// output it's attributes
		thisPart.setAttribute("Name", bb.getName());

		// and the speed, if we have it
		if (bb.getSpeed() != null) {
			WorldSpeedHandler.exportSpeed(SPEED, bb.getSpeed(), thisPart, doc);
		}

		ASSETLocationHandler.exportLocation(bb.getLocation(), LOCATION, thisPart, doc);

		parent.appendChild(thisPart);

	}

	WorldLocation _myLocation;

	String _name;

	WorldSpeed _mySpeed;

	public ManoeuvreToLocationHandler() {
		super("ManoeuvreToLocation");

		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String val) {
				_name = val;
			}
		});

		addHandler(new WorldSpeedHandler() {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_mySpeed = res;
			}
		});

		addHandler(new ASSETLocationHandler(LOCATION) {
			@Override
			public void setLocation(final WorldLocation res) {
				_myLocation = res;
			}
		});
	}

	@Override
	public void elementClosed() {
		final Response ml = new ManoeuvreToLocation(_myLocation, _mySpeed);
		ml.setName(_name);

		// finally output it
		setResponse(ml);

		// and reset
		_myLocation = null;
		_mySpeed = null;
		_name = null;
	}

	public void setResponse(final Response dec) {
	}

}