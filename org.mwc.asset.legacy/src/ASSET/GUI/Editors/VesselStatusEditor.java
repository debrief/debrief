
package ASSET.GUI.Editors;

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

import ASSET.Models.Movement.SSMovementCharacteristics;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class VesselStatusEditor implements Editable {

	//////////////////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// add testing code
	//////////////////////////////////////////////////
	public static class EditorTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			final SSN theSSN = new SSN(12);
			final Status stat = new Status(12, 12);
			stat.setLocation(new WorldLocation(12, 12, 0));
			stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
			theSSN.setMovementChars(SSMovementCharacteristics.getSampleSSChars());
			theSSN.setStatus(stat);
			final VesselStatusEditor res = new VesselStatusEditor(theSSN);

			return res;
		}
	}

	static public class VesselStatusInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public VesselStatusInfo(final VesselStatusEditor data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						prop("Location", "the current location of this participant"),
						prop("Course", "the current course of this participant (degs)"),
						prop("Speed", "the current speed of this participant (kts)"),
						prop("FuelLevel", "the fuel level for this participant"), };

				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	private final ASSET.ParticipantType _myParticipant;

	//////////////////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////////////////

	private Editable.EditorType _myEditor = null;

	//////////////////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////////////////
	public VesselStatusEditor(final ASSET.ParticipantType participant) {
		// wrap the participant
		_myParticipant = participant;
	}

	public BoundedInteger getCourse() {
		// trim the course
		double crseVal = MWC.Algorithms.Conversions
				.clipRadians(MWC.Algorithms.Conversions.Degs2Rads(_myParticipant.getStatus().getCourse()));
		crseVal = MWC.Algorithms.Conversions.Rads2Degs(crseVal);
		return new BoundedInteger((int) crseVal, 0, 360);
	}

	public double getFuelLevel() {
		return _myParticipant.getStatus().getFuelLevel();
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new VesselStatusInfo(this);

		return _myEditor;
	}

	public MWC.GenericData.WorldLocation getLocation() {
		return _myParticipant.getStatus().getLocation();
	}

	@Override
	public String getName() {
		return toString();
	}

	//////////////////////////////////////////////////////////////////////
	// editable getter/setter
	//////////////////////////////////////////////////////////////////////
	public BoundedInteger getSpeed() {
		return new BoundedInteger((int) _myParticipant.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec), 0,
				(int) (_myParticipant.getMovementChars().getMaxSpeed().getValueIn(WorldDistance.METRES)));
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	public void setCourse(final BoundedInteger val) {
		_myParticipant.getStatus().setCourse(val.getCurrent());
	}

	public void setFuelLevel(final double val) {
		_myParticipant.getStatus().setFuelLevel(val);
	}

	public void setLocation(final MWC.GenericData.WorldLocation val) {
		_myParticipant.getStatus().setLocation(val);
	}

	//////////////////////////////////////////////////////////////////////
	// editable properties
	//////////////////////////////////////////////////////////////////////

	public void setSpeed(final BoundedInteger val) {
		_myParticipant.getStatus().setSpeed(new WorldSpeed(val.getCurrent(), WorldSpeed.M_sec));
	}

	@Override
	public String toString() {
		return "Status";
	}
}