
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

import ASSET.Models.Decision.Responses.ManoeuvreToCourse;
import ASSET.Models.Decision.Responses.Response;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

/**
 * class which reads in a man to course response
 *
 * @see ASSET.Models.Decision.Responses.ManoeuvreToCourse
 */

public class ManoeuvreToCourseHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "ManoeuvreToCourse";
	private final static String SPEED = "Speed";
	private final static String RELATIVE_SPEED = "RelativeSpeed";
	private final static String COURSE = "Course";
	private final static String RELATIVE_COURSE = "RelativeCourse";

	private final static String Height = "Height";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ManoeuvreToCourse bb = (ManoeuvreToCourse) toExport;

		// output it's attributes
		thisPart.setAttribute("Name", bb.getName());

		// and the speed, if we have it
		if (bb.getSpeed() != null) {
			WorldSpeedHandler.exportSpeed(SPEED, bb.getSpeed(), thisPart, doc);
			thisPart.setAttribute(RELATIVE_SPEED, writeThis(bb.isRelativeSpeed()));
		}

		// and the course, if we have it
		if (bb.getCourse() != null) {
			thisPart.setAttribute(COURSE, writeThis(bb.getCourse().floatValue()));
			thisPart.setAttribute(RELATIVE_COURSE, writeThis(bb.isRelativeCourse()));
		}

		// and the Height
		if (bb.getHeight() != null) {
			WorldDistanceHandler.exportDistance(Height, bb.getHeight(), thisPart, doc);
		}

		parent.appendChild(thisPart);

	}

	/**
	 * the speed to travel at (kts)
	 */
	WorldSpeed _mySpeed = null;

	/**
	 * whether the speed change is relative
	 */
	boolean _relativeSpeed;

	/**
	 * the course to steer to (degs)
	 */
	Float _myCourse = null;

	/**
	 * whether the course change is relative
	 */
	boolean _relativeCourse;

	/**
	 * the Height to change to (m), always absolute
	 */
	WorldDistance _myHeight = null;

	String _name;

	public ManoeuvreToCourseHandler() {
		super("ManoeuvreToCourse");

		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String val) {
				_name = val;
			}
		});

		addHandler(new WorldSpeedHandler(SPEED) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_mySpeed = res;
			}
		});
		addHandler(new WorldDistanceHandler(Height) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_myHeight = res;
			}
		});

		addAttributeHandler(new HandleDoubleAttribute(COURSE) {
			@Override
			public void setValue(final String name, final double val) {
				_myCourse = new Float(val);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(RELATIVE_SPEED) {
			@Override
			public void setValue(final String name, final boolean val) {
				_relativeSpeed = val;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(RELATIVE_COURSE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_relativeCourse = val;
			}
		});
	}

	@Override
	public void elementClosed() {
		final Response ml = new ManoeuvreToCourse(_mySpeed, _relativeSpeed, _myCourse, _relativeCourse, _myHeight);
		ml.setName(_name);

		// finally output it
		setResponse(ml);

		// and reset
		_mySpeed = null;
		_myCourse = null;
		_myHeight = null;
		_name = null;
	}

	public void setResponse(final Response dec) {
	}

}