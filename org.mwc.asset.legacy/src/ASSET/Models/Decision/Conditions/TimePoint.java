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

package ASSET.Models.Decision.Conditions;

import java.util.Date;

import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

/**
 * condition which is successful when the scenario is equal or after the
 * indicated time
 */

public class TimePoint extends Condition.CoreCondition {
	////////////////////////////////////////////////////
	// member objects
	////////////////////////////////////////////////////

	/**
	 * ************************************************* editor support
	 * *************************************************
	 */

	static public class TimePointInfo extends Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public TimePointInfo(final TimePoint data) {
			super(data, data.getName(), "Time Point");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this condition"),
						prop("Time", "the time we are checking for"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////

	/**
	 * ************************************************* testing
	 * *************************************************
	 */
	static public class TimePointTest extends SupportTesting.EditableTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public TimePointTest(final String name) {
			super(name);
		}

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new TimePoint(300);
		}

		public void testIt() {
			// build it
			final TimePoint loc = new TimePoint(300);
			Object res = loc.test(null, null, 0, null);
			// test it
			assertNull("we are before time", res);

			res = loc.test(null, null, 300, null);
			assertNotNull("we are at time", res);

			res = loc.test(null, null, 400, null);
			assertNotNull("we are after time", res);
		}

	}

	////////////////////////////////////////////////////
	// condition fields
	////////////////////////////////////////////////////

	/**
	 * the time we're checking for
	 */
	private long _theTime = -1;

	/**
	 * @param theTime the time we are checking for
	 */
	public TimePoint(final long theTime) {
		super("TimePoint");

		_theTime = theTime;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new TimePointInfo(this);

		return _myEditor;
	}

	public long getTime() {
		return _theTime;
	}

	////////////////////////////////////////////////////
	// editor fields
	////////////////////////////////////////////////////

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
	 * restart the condition
	 */
	@Override
	public void restart() {
		// ignore, we don't need to restart
	}

	public void setTime(final long theTime) {
		this._theTime = theTime;
	}

	@Override
	public Object test(final Status status, final DetectionList detections, final long time,
			final ScenarioActivityMonitor monitor) {

		Date res = null;

		if (time >= _theTime) {
			res = new Date(_theTime);
		}

		return res;
	}
}
