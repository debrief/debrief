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

package ASSET.Util.XML.Utils.LookupEnvironment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.MADLookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 26-Oct-2004 Time: 09:24:08 To
 * change this template use File | Settings | File Templates.
 */
abstract public class MADLookupTableHandler extends MWCXMLReader {
	private static final String PRED_RANGE_SET = "PredictedRangeSet";
	private static final String PRED_RANGE_DATUM = "PredictedRangeDatum";

	private static final String NAME_ATTRIBUTE = "Name";

	private static final String VISIBLILITY = "PredictedRange";

	public static void exportThis(final String type, final OpticLookupSensor.OpticEnvironment optic,
			final Element parent, final Document doc) {
		// ok, put us into the element
		final Element envElement = doc.createElement(type);

		// get on with the name attribute
		envElement.setAttribute(NAME_ATTRIBUTE, optic.getName());

		// now the child bits
		System.err.println("EXPORT OF MAD TABLE NOT IMPLEMENTED");

		// and hang us off the parent
		parent.appendChild(envElement);

	}

	LookupSensor.StringLookup _visibility;

	String _myName = null;

	public MADLookupTableHandler(final String myType) {
		super(myType);

		addAttributeHandler(new HandleAttribute(NAME_ATTRIBUTE) {
			@Override
			public void setValue(final String name, final String value) {
				_myName = value;
			}
		});

		addHandler(new StringSetHandler(PRED_RANGE_SET, PRED_RANGE_DATUM, VISIBLILITY) {
			@Override
			public void setDatums(final LookupSensor.StringLookup myValues) {
				_visibility = myValues;
			}
		});

	}

	@Override
	public void elementClosed() {
		final MADLookupSensor.MADEnvironment res = new MADLookupSensor.MADEnvironment(_myName, _visibility);

		setMADEnvironment(res);
		_visibility = null;
		_myName = null;
	}

	abstract public void setMADEnvironment(MADLookupSensor.MADEnvironment env);
}
