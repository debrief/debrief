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

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor.IntegerTargetTypeLookup;
import ASSET.Models.Sensor.Lookup.LookupSensor.NamedList;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 26-Oct-2004 Time: 09:29:10 To
 * change this template use File | Settings | File Templates.
 */
abstract public class IntegerTargetTypeLookupHandler extends MWCXMLReader {
	public static final String DATUM = "TargetAspectDatum";

	private static final String UNKNOWN_TYPE = "UnknownType";

	public static void exportThis(final String target_sea_state_set, final String target_sea_state_datum,
			final String[] sea_state_headings, final IntegerTargetTypeLookup states, final Element envElement,
			final Document doc) {
		// ok, put us into the element
		final org.w3c.dom.Element itt = doc.createElement(target_sea_state_set);

		// get on with the name attribute
		final Double unknown = states.getUnknownResult();
		if (unknown != null)
			itt.setAttribute(UNKNOWN_TYPE, writeThis(unknown.doubleValue()));

		// now the matrix of sea states
		final Collection<String> keys = states.getNames();
		for (final Iterator<String> iter = keys.iterator(); iter.hasNext();) {
			final String thisN = iter.next();

			// ok, cycle through the sea states for this participant
			final NamedList thisList = states.getThisSeries(thisN);
			exportThisSeries(thisN, target_sea_state_datum, thisList, sea_state_headings, itt, doc);
		}

		envElement.appendChild(itt);
	}

	private static void exportThisSeries(final String name, final String target_sea_state_datum,
			final NamedList thisList, final String[] sea_state_headings, final Element itt, final Document doc) {
		// ok, put us into the element
		final org.w3c.dom.Element datum = doc.createElement(target_sea_state_datum);

		datum.setAttribute("Type", name);

		// and step through its values
		final Collection<Double> indices = thisList.getValues();
		int ctr = 0;
		for (final Iterator<Double> iter = indices.iterator(); iter.hasNext();) {
			final Double val = iter.next();
			if (val != null) {
				datum.setAttribute(sea_state_headings[ctr], writeThis(val.doubleValue()));
				ctr++;
			} else
				break;
		}

		itt.appendChild(datum);
	}

	Vector<NamedList> _myDatums = null;

	Double _defaultValue;

	public IntegerTargetTypeLookupHandler(final String myType, final String mySubType, final String[] myHeadings) {
		super(myType);

		addHandler(new TargetIntegerDatumHandler(mySubType, myHeadings) {
			@Override
			public void setDatum(final LookupSensor.NamedList value) {
				addDatum(value);
			}
		});

		addAttributeHandler(new HandleDoubleAttribute(UNKNOWN_TYPE) {
			@Override
			public void setValue(final String name, final double value) {
				_defaultValue = new Double(value);
			}
		});

	}

	/**
	 * store this new datum
	 *
	 * @param value
	 */
	void addDatum(final LookupSensor.NamedList value) {
		if (_myDatums == null)
			_myDatums = new Vector<LookupSensor.NamedList>(1, 1);
		_myDatums.add(value);
	}

	@Override
	public void elementClosed() {

		final LookupSensor.IntegerTargetTypeLookup res = new LookupSensor.IntegerTargetTypeLookup(_myDatums,
				_defaultValue);

		// and store it
		setLookup(res);

		// ditch gash
		_myDatums = null;
	}

	/**
	 * pass details back to calling class
	 *
	 * @param val
	 */
	abstract public void setLookup(LookupSensor.IntegerTargetTypeLookup val);

}
