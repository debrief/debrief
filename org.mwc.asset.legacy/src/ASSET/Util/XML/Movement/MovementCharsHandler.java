
package ASSET.Util.XML.Movement;

import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.WorldAccelerationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

abstract public class MovementCharsHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String type = "MovementCharacteristics";

	protected final static String ACCEL = "AccelerationRate";
	protected final static String DECEL = "DecelerationRate";
	protected final static String FUEL = "FuelUsageRate";
	protected final static String MAX_SPEED = "MaxSpeed";
	protected final static String MIN_SPEED = "MinSpeed";

	static public void exportThis(final ASSET.Models.Movement.MovementCharacteristics toExport,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc) {

		// create the element
		final org.w3c.dom.Element stat = doc.createElement(type);

		stat.setAttribute("Name", toExport.getName());
		stat.setAttribute(FUEL, writeThis(toExport.getFuelUsageRate()));

		WorldAccelerationHandler.exportAcceleration(ACCEL, toExport.getAccelRate(), stat, doc);
		WorldAccelerationHandler.exportAcceleration(DECEL, toExport.getDecelRate(), stat, doc);
		WorldSpeedHandler.exportSpeed(MAX_SPEED, toExport.getMaxSpeed(), stat, doc);
		WorldSpeedHandler.exportSpeed(MIN_SPEED, toExport.getMinSpeed(), stat, doc);

		// add to parent
		parent.appendChild(stat);

	}

	protected WorldAcceleration _myAccelRate;
	protected WorldAcceleration _myDecelRate;
	protected double _myFuel;
	protected WorldSpeed _myMaxSpd;
	protected WorldSpeed _myMinSpd;

	protected String _myName;

	public MovementCharsHandler() {
		this(type);
	}

	public MovementCharsHandler(final String theType) {
		super(theType);

		addAttributeHandler(new MWCXMLReader.HandleAttribute("Name") {
			@Override
			public void setValue(final String name, final String val) {
				_myName = val;
			}
		});

		addHandler(new WorldAccelerationHandler(ACCEL) {
			@Override
			public void setAcceleration(final WorldAcceleration res) {
				_myAccelRate = res;
			}
		});
		addHandler(new WorldAccelerationHandler(DECEL) {
			@Override
			public void setAcceleration(final WorldAcceleration res) {
				_myDecelRate = res;
			}
		});
		addHandler(new WorldSpeedHandler(MAX_SPEED) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_myMaxSpd = res;
			}
		});
		addHandler(new WorldSpeedHandler(MIN_SPEED) {
			@Override
			public void setSpeed(final WorldSpeed res) {
				_myMinSpd = res;
			}
		});
		addAttributeHandler(new MWCXMLReader.HandleDoubleAttribute(FUEL) {
			@Override
			public void setValue(final String name, final double val) {
				_myFuel = val;
			}
		});

	}

	@Override
	abstract public void elementClosed();

	abstract public void setMovement(ASSET.Models.Movement.MovementCharacteristics chars);

}