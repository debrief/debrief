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

import java.util.Vector;

import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GUI.Editable;

/**
 * condition which is successful when the scenario is equal or after the
 * indicated time
 */

public class OrCondition extends Condition.CoreCondition {

	////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////

	/**
	 * ************************************************* editor support
	 * *************************************************
	 */

	static public class OrConditionInfo extends Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public OrConditionInfo(final OrCondition data) {
			super(data, data.getName(), "OR condition");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this condition"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	////////////////////////////////////////////////////
	// condition fields
	////////////////////////////////////////////////////

	/**
	 * the list of conditions we are after
	 */
	private Vector<Condition> _myConditions;

	/**
	 *
	 */
	protected OrCondition() {
		super("OR condition");
	}

	////////////////////////////////////////////////////
	// editor fields
	////////////////////////////////////////////////////

	protected void addCondition(final Condition newCondition) {
		if (_myConditions == null)
			_myConditions = new Vector<Condition>(0, 1);

		_myConditions.add(newCondition);
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new OrConditionInfo(this);

		return _myEditor;
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
	 * restart the condition
	 */
	@Override
	public void restart() {
		// ignore, we don't bother it
	}

	@Override
	public Object test(final Status status, final DetectionList detections, final long time,
			final ScenarioActivityMonitor monitor) {

		Object res = null;

		// pass through the conditions
		for (int i = 0; i < _myConditions.size(); i++) {
			final Condition condition = _myConditions.elementAt(i);
			res = condition.test(status, detections, time, null);
			if (res != null)
				break;
		}

		return res;
	}

}
