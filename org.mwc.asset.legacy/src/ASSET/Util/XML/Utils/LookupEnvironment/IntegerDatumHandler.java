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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor.IntegerLookup;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 26-Oct-2004 Time: 11:42:47 To
 * change this template use File | Settings | File Templates.
 */
abstract public class IntegerDatumHandler extends MWCXMLReader {
	public static void exportThis(final String type, final LookupSensor.IntegerLookup lightLevel, final Element parent,
			final Document doc, final String[] headings) {
		// ok, put us into the element
		final org.w3c.dom.Element envElement = doc.createElement(type);

		// now the child bits
		final Collection<Integer> indices = lightLevel.indices();
		for (final Iterator<Integer> iter = indices.iterator(); iter.hasNext();) {
			final Integer thisIndex = iter.next();
			final Double val = lightLevel.find(thisIndex.intValue());
			// ok, export it
			envElement.setAttribute(headings[thisIndex.intValue()], writeThisLong(val.doubleValue()));
		}

		// and hang us off the parent
		parent.appendChild(envElement);
	}

	LookupSensor.IntegerLookup _res;

	// remember our list of text strings
	final protected String[] _theCategories;

	public IntegerDatumHandler(final String myType, final String[] categories) {
		super(myType);

		_theCategories = categories;

		for (int i = 0; i < categories.length; i++) {
			final int index = i;
			final String heading = categories[i];
			addAttributeHandler(new HandleDoubleAttribute(heading) {
				@Override
				public void setValue(final String name, final double value) {
					addValue(index, value, heading);
				}
			});
		}
	}

	protected void addValue(final int type, final double value, final String category) {
		if (_res == null) {
			_res = new IntegerLookup();
		}

		_res.add(type, value);
	}

	@Override
	public void elementClosed() {
		setDatums(_res);
		_res = null;
	}

	abstract public void setDatums(LookupSensor.IntegerLookup res);
}
