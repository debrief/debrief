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
import ASSET.Models.Sensor.Lookup.LookupSensor.StringLookup;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 26-Oct-2004 Time: 11:42:47 To
 * change this template use File | Settings | File Templates.
 */
abstract public class StringSetHandler extends MWCXMLReader {
	/**
	 * ******************************************************************* embedded
	 * class which records pairs of type/value datums
	 * *******************************************************************
	 */
	abstract static public class StringDatumHandler extends MWCXMLReader {
		private static final String NAME = "Type";

		public static void exportThis(final String target_vis_datum, final String thisIndex,
				final String attribute_label, final double d, final Element envElement, final Document doc) {
			// ok, put us into the element
			final org.w3c.dom.Element datum = doc.createElement(target_vis_datum);
			datum.setAttribute(NAME, thisIndex);
			datum.setAttribute(attribute_label, writeThisLong(d));

			envElement.appendChild(datum);

		}

		String _name;

		double _value;

		protected StringDatumHandler(final String myType, final String visLabel) {
			super(myType);

			addAttributeHandler(new HandleAttribute(NAME) {
				@Override
				public void setValue(final String name, final String value) {
					_name = value;
				}
			});
			addAttributeHandler(new HandleDoubleAttribute(visLabel) {
				@Override
				public void setValue(final String name, final double value) {
					_value = value;
				}
			});

		}

		@Override
		public void elementClosed() {
			setDatum(_name, _value);
			_name = null;
		}

		abstract public void setDatum(String name, double value);

	}

	private static final String UNKNOWN_TYPE = "UnknownType";

	public static void exportThis(final String target_vis, final String target_vis_datum, final String ATTRIBUTE_LABEL,
			final StringLookup atten, final Element env, final Document doc) {
		// ok, put us into the element
		final org.w3c.dom.Element envElement = doc.createElement(target_vis);

		// get on with the name attribute
		final Double unknownVal = atten.getUnknownResult();
		if (unknownVal != null)
			envElement.setAttribute(UNKNOWN_TYPE, writeThisLong(unknownVal.doubleValue()));

		// now cycle through the elements themselves
		final Collection<String> theIndices = atten.getIndices();
		for (final Iterator<String> iter = theIndices.iterator(); iter.hasNext();) {
			final String thisIndex = iter.next();
			final Double res = atten.find(thisIndex);
			StringDatumHandler.exportThis(target_vis_datum, thisIndex, ATTRIBUTE_LABEL, res.doubleValue(), envElement,
					doc);
		}

		env.appendChild(envElement);
	}

	Vector<String> _myTypes;

	Vector<Double> _myValues;

	Double _defaultValue;

	public StringSetHandler(final String myType, final String datumName, final String dataValue) {
		super(myType);

		addHandler(new StringDatumHandler(datumName, dataValue) {
			@Override
			public void setDatum(final String name, final double value) {
				addValue(name, value);
			}
		});

		addAttributeHandler(new HandleDoubleAttribute(UNKNOWN_TYPE) {
			@Override
			public void setValue(final String name, final double value) {
				_defaultValue = new Double(value);
			}
		});

	}

	protected void addValue(final String name, final double value) {
		if (_myValues == null) {
			_myTypes = new Vector<String>(1, 1);
			_myValues = new Vector<Double>(1, 1);
		}

		_myTypes.add(name);
		_myValues.add(new Double(value));

	}

	@Override
	public void elementClosed() {
		// first the indices
		final String[] strs = new String[_myValues.size()];
		for (int i = 0; i < strs.length; i++) {
			strs[i] = _myTypes.elementAt(i);
		}

		// and now the values themselves
		final double[] vals = new double[strs.length];
		for (int i = 0; i < _myValues.size(); i++) {
			final Double aDouble = _myValues.elementAt(i);
			vals[i] = aDouble.doubleValue();
		}

		final LookupSensor.StringLookup res = new LookupSensor.StringLookup(strs, vals, _defaultValue);

		setDatums(res);

		_myValues = null;
		_defaultValue = null;
	}

	abstract public void setDatums(LookupSensor.StringLookup res);

}
