
package MWC.Utilities.ReaderWriter.XML.Util;

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

import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class XMLTimeRangeHandler extends MWCXMLReader {

	private static final String MY_TYPE = "timeRange";
	private static final String START = "Start";
	private static final String END = "End";

	public static void exportThis(final HiResDate start, final HiResDate end, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		exportThis(start, end, parent, doc, MY_TYPE);
	}

	public static void exportThis(final HiResDate start, final HiResDate end, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc, final String type) {
		boolean useful = false;
		final org.w3c.dom.Element eTime = doc.createElement(type);
		if (start != MWC.GenericData.TimePeriod.INVALID_DATE) {
			eTime.setAttribute(START, writeThisInXML(start.getDate()));
			useful = true;
		}
		if (end != MWC.GenericData.TimePeriod.INVALID_DATE) {
			eTime.setAttribute(END, writeThisInXML(end.getDate()));
			useful = true;
		}

		if (useful) {
			parent.appendChild(eTime);
		}
	}

	private HiResDate _start = MWC.GenericData.TimePeriod.INVALID_DATE;

	private HiResDate _end = MWC.GenericData.TimePeriod.INVALID_DATE;

	public XMLTimeRangeHandler() {
		this(MY_TYPE);
	}

	public XMLTimeRangeHandler(final String type) {
		// inform our parent what type of class we are
		super(type);

		// sort out the handlers
		addAttributeHandler(new HandleDateTimeAttribute(START) {
			@Override
			public void setValue(final String name, final long val) {
				_start = new HiResDate(val);
			}
		});
		// sort out the handlers
		addAttributeHandler(new HandleDateTimeAttribute(END) {
			@Override
			public void setValue(final String name, final long val) {
				_end = new HiResDate(val);
			}
		});
	}

	@Override
	public void elementClosed() {
		setTimeRange(_start, _end);

		_start = _end = MWC.GenericData.TimePeriod.INVALID_DATE;
	}

	abstract public void setTimeRange(HiResDate start, HiResDate end);

}