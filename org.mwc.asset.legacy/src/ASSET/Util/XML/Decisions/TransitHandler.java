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

package ASSET.Util.XML.Decisions;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian Mayo
 * Date: 2003
 * Log:
 *  $Log: TransitHandler.java,v $
 *  Revision 1.1  2006/08/08 14:22:50  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:26:59  Ian.Mayo
 *  First versions
 *
 *  Revision 1.9  2004/10/29 13:41:48  Ian.Mayo
 *  Implement WorkingTransit handler
 *
 *  Revision 1.8  2004/08/20 13:33:30  Ian.Mayo
 *  Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *  Revision 1.7  2004/08/17 14:54:49  Ian.Mayo
 *  Refactor to introduce parent handler class capable of storing name & isActive flag
 *
 *  Revision 1.6  2004/05/24 16:14:39  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:59  ian
 *  no message
 *
 *  Revision 1.5  2003/09/19 13:38:47  Ian.Mayo
 *  Switch to Speed and Distance objects instead of just doubles
 *
 *  Revision 1.4  2003/09/18 10:36:19  Ian.Mayo
 *  Reflect new location stucture
 *
 *  Revision 1.3  2003/09/04 14:32:35  Ian.Mayo
 *  Reflect new speed type for Transit
 *
 *
 */

import ASSET.Models.Decision.Movement.Transit;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import ASSET.Util.XML.Utils.ASSETWorldPathHandler;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class TransitHandler extends CoreDecisionHandler {

	private final static String type = "Transit";
	private final static String LOOPING = "Looping";
	private final static String SPEED = "Speed";
	private final static String REVERSE = "Reverse";
	private final static String DESTINATION = "Destination";

	protected static void exportCoreAttributes(final Transit bb, final org.w3c.dom.Element thisPart,
			final org.w3c.dom.Document doc) {
		// first the parent bits
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// output it's attributes
		thisPart.setAttribute(LOOPING, writeThis(bb.getLoop()));
		if (bb.getSpeed() != null)
			WorldSpeedHandler.exportSpeed(SPEED, bb.getSpeed(), thisPart, doc);

		ASSETWorldPathHandler.exportThis(bb.getDestinations(), thisPart, doc);
	}

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Decision.Movement.Transit bb = (ASSET.Models.Decision.Movement.Transit) toExport;

		exportCoreAttributes(bb, thisPart, doc);

		parent.appendChild(thisPart);

	}

	protected MWC.GenericData.WorldPath _myPath;
	protected boolean _looping;
	protected WorldSpeed _speed;

	protected Boolean _reverse = null;

	protected Integer _destination = null;

	public TransitHandler() {
		this(type);
	}

	public TransitHandler(final String theType) {
		super(theType);

		final MWC.Utilities.ReaderWriter.XML.MWCXMLReader hand = new ASSETWorldPathHandler() {
			@Override
			public void setPath(final MWC.GenericData.WorldPath path) {
				_myPath = path;
			}
		};

		addHandler(hand);
		addAttributeHandler(new HandleAttribute(LOOPING) {
			@Override
			public void setValue(final String name, final String val) {
				_looping = Boolean.valueOf(val).booleanValue();
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(DESTINATION) {
			@Override
			public void setValue(final String name, final int value) {
				_destination = value;
			}
		});

		addAttributeHandler(new HandleAttribute(REVERSE) {
			@Override
			public void setValue(final String name, final String val) {
				_reverse = Boolean.valueOf(val).booleanValue();
			}
		});

		addHandler(new WorldSpeedHandler(SPEED) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_speed = res;
			}
		});
	}

	@Override
	public void elementClosed() {
		final Transit tr = getBehaviour();
		super.setAttributes(tr);
		setModel(tr);
		if (_reverse != null) {
			tr.setReverse(_reverse);
		}
		if (_destination != null) {
			if (_destination < _myPath.size())
				tr.setCurrentDestination(_destination);
			else
				System.err.println("Wrong destination index: too high!");
		}

		_destination = null;
		_myPath = null;
		_speed = null;
		_reverse = null;
	}

	protected Transit getBehaviour() {
		return new Transit(_myPath, _speed, _looping);
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}