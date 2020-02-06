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

package ASSET.Models.Decision.Responses;

import ASSET.Models.Decision.Conditions.Condition;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

/**
 * response which is capable of switching sensors on and off, as decided by the
 * medium they receive/transmit
 *
 * @see ASSET.Models.Environment.EnvironmentType
 * @see ASSET.Util.XML.Decisions.Responses.ChangeSensorLineUpHandler
 */
public class ChangeSensorLineUp extends Response.CoreResponse {
	////////////////////////////////////////////////////
	// member objects
	////////////////////////////////////////////////////

	/**
	 * ************************************************* editor support
	 * *************************************************
	 */

	static public class ChangeSensorInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public ChangeSensorInfo(final ChangeSensorLineUp data) {
			super(data, data.getName(), "");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this response"),
						prop("Medium", "the medium of the sensor to change"),
						prop("SwitchOn", "whether to switch on or off"), };
				res[1].setPropertyEditorClass(EnvironmentType.MediumPropertyEditor.class);
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	//////////////////////////////////////////////////
	// testing support
	//////////////////////////////////////////////////
	public static class SensorLineupTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new ChangeSensorLineUp(2, true);
		}
	}

	////////////////////////////////////////////////////
	// member constructor
	////////////////////////////////////////////////////

	/**
	 * the medium of sensors we change
	 *
	 * @see ASSET.Models.Environment.EnvironmentType
	 */
	private int _medium;

	////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////

	/**
	 * whether to switch on or off
	 */
	private boolean _switchOn = true;

	public ChangeSensorLineUp(final int medium, final boolean switchOn) {
		this._medium = medium;
		this._switchOn = switchOn;
	}

	////////////////////////////////////////////////////
	// accessors
	////////////////////////////////////////////////////

	/**
	 * produce the required response
	 *
	 * @param conditionResult the result from the condition test
	 * @param status          the current status
	 * @param detections      the current set of detections
	 * @param monitor         the object monitoring us(for add/remove participants,
	 *                        detonations, etc)
	 * @param time            the current time
	 * @return
	 * @see Condition
	 */
	@Override
	public DemandedStatus direct(final Object conditionResult, final Status status, final DemandedStatus demStat,
			final DetectionList detections, final ScenarioActivityMonitor monitor, final long time) {
		// ok, request the sensor change
		final DemandedSensorStatus ds = new DemandedSensorStatus(_medium, _switchOn);

		// and produce the demanded status
		final DemandedStatus dem = new SimpleDemandedStatus(time, status);
		dem.add(ds);

		// done
		return dem;
	}

	/**
	 * get the description of what we're doing
	 */
	@Override
	public String getActivity() {
		String res;
		if (_switchOn)
			res = "on";
		else
			res = "off";

		return "Switch " + res;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new ChangeSensorInfo(this);

		return _myEditor;
	}

	/**
	 * the medium of the sensor we are changing
	 *
	 * @return the medium
	 * @see ASSET.Models.Environment.EnvironmentType
	 */
	public int getMedium() {
		return _medium;
	}

	public boolean getSwitchOn() {
		return _switchOn;
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

	/**
	 * reset the local data
	 */
	@Override
	public void restart() {
	}

	/**
	 * change the medium of the sensor we are changing
	 *
	 * @param medium the medium
	 * @see ASSET.Models.Environment.EnvironmentType
	 */
	public void setMedium(final int medium) {
		this._medium = medium;
	}

	public void setSwitchOn(final boolean switchOn) {
		this._switchOn = switchOn;
	}

}
