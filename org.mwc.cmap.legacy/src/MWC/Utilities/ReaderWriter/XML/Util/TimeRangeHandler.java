
package MWC.Utilities.ReaderWriter.XML.Util;

import java.text.ParseException;

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

import org.xml.sax.Attributes;

import MWC.GenericData.HiResDate;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class TimeRangeHandler extends MWCXMLReader {

	private static final String MY_TYPE = "timeRange";

	public static void exportThis(final HiResDate start, final HiResDate end, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		exportThis(start, end, parent, doc, MY_TYPE);
	}

	public static void exportThis(final HiResDate start, final HiResDate end, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc, final String type) {
		boolean useful = false;
		final org.w3c.dom.Element eTime = doc.createElement(type);
		if (start != MWC.GenericData.TimePeriod.INVALID_DATE) {
			eTime.setAttribute("Start", writeThis(start));
			useful = true;
		}
		if (end != MWC.GenericData.TimePeriod.INVALID_DATE) {
			eTime.setAttribute("End", writeThis(end));
			useful = true;
		}

		if (useful) {
			parent.appendChild(eTime);
		}
	}

	private HiResDate _start = MWC.GenericData.TimePeriod.INVALID_DATE;

	private HiResDate _end = MWC.GenericData.TimePeriod.INVALID_DATE;

	public TimeRangeHandler() {
		this(MY_TYPE);
	}

	public TimeRangeHandler(final String type) {
		// inform our parent what type of class we are
		super(type);
	}

	@Override
	public void elementClosed() {
		setTimeRange(_start, _end);

		_start = _end = MWC.GenericData.TimePeriod.INVALID_DATE;
	}

	// this is one of ours, so get on with it!
	@Override
	protected void handleOurselves(final String name, final Attributes attributes) {
		_start = _end = MWC.GenericData.TimePeriod.INVALID_DATE;

		final int len = attributes.getLength();
		for (int i = 0; i < len; i++) {

			final String nm = attributes.getLocalName(i);
			final String val = attributes.getValue(i);
			if (nm.equals("Start"))
				try {
					_start = parseThisDate(val);
				} catch (final ParseException e) {
					Trace.trace(e, "While parsing start date");
				}
			else if (nm.equals("End"))
				try {
					_end = parseThisDate(val);
				} catch (final ParseException e) {
					Trace.trace(e, "While parsing end date");
				}

		}

	}

	abstract public void setTimeRange(HiResDate start, HiResDate end);

}