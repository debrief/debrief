
package ASSET.Util.XML.Vessels.Util;

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

import java.text.ParseException;

import ASSET.Models.Movement.SimpleDemandedStatus;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class DemandedStatusHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "DemandedStatus";

	static public void exportThis(final ASSET.Participants.DemandedStatus toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		final SimpleDemandedStatus sds = (SimpleDemandedStatus) toExport;

		// check we've got a data value
		if (toExport != null) {
			// create the element
			final org.w3c.dom.Element stat = doc.createElement(type);

			// set the attributes
			stat.setAttribute("Course", writeThis(sds.getCourse()));
			stat.setAttribute("Id", writeThis(toExport.getId()));
			stat.setAttribute("Time", writeThis(new java.util.Date(toExport.getTime())));

			WorldSpeedHandler.exportSpeed("Speed", new WorldSpeed(sds.getSpeed(), WorldSpeed.M_sec), stat, doc);
			WorldDistanceHandler.exportDistance("Height", new WorldDistance(sds.getHeight(), WorldDistance.METRES),
					stat, doc);

			// add to parent
			parent.appendChild(stat);
		}

	}

	double _myCourse;
	WorldSpeed _mySpeed;
	WorldDistance _myHeight;
	long _myTime;

	long _myId;

	public DemandedStatusHandler() {
		super(type);

		super.addAttributeHandler(new HandleAttribute("Course") {
			@Override
			public void setValue(final String name, final String val) {
				try {
					_myCourse = MWCXMLReader.readThisDouble(val);
				} catch (final ParseException e) {
					MWC.Utilities.Errors.Trace.trace(e);
				}
			}
		});

		addHandler(new WorldSpeedHandler("Speed") {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_mySpeed = res;
			}
		});
		addHandler(new WorldDistanceHandler("Height") {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_myHeight = res;
			}
		});

		addAttributeHandler(new HandleAttribute("Time") {
			@Override
			public void setValue(final String name, final String value) {
				try {
					// the DTD specified that a default time value is zero, trap it
					if (value.equals("0"))
						return;

					final java.util.Date dtg = getRNDateFormatter().parse(value);
					_myTime = dtg.getTime();
				} catch (final java.text.ParseException e) {
					System.err.println("error occured trying to parse:" + value);
					e.printStackTrace();
				}
			}
		});
		super.addAttributeHandler(new HandleAttribute("Id") {
			@Override
			public void setValue(final String name, final String val) {
				_myId = Long.parseLong(val);
			}
		});
	}

	@Override
	public void elementClosed() {
		// create the category
		final SimpleDemandedStatus stat = new SimpleDemandedStatus(_myId, _myTime);
		stat.setCourse(_myCourse);
		stat.setSpeed(_mySpeed);
		stat.setHeight(_myHeight);

		setDemandedStatus(stat);

		_myHeight = null;
		_mySpeed = null;
	}

	abstract public void setDemandedStatus(ASSET.Participants.DemandedStatus stat);

}