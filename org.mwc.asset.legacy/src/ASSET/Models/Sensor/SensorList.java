
package ASSET.Models.Sensor;

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

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import ASSET.Models.SensorType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

public class SensorList implements ASSET.Models.SensorType, java.io.Serializable {

	////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the object
	////////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public class SensorFitInfo extends CoreSensor.BaseSensorInfo {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public SensorFitInfo(final SensorList data) {
			super(data);
		}

		/**
		 * return a description of this bean, also specifies the custom editor we use
		 *
		 * @return the BeanDescriptor
		 */
		@Override
		public java.beans.BeanDescriptor getBeanDescriptor() {
			final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(SensorList.class,
					ASSET.GUI.Editors.Sensors.SensorFitEditor.class);
			bp.setDisplayName("Sensor Fit");
			return bp;
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
						prop("Working", "whether this set of sensors are active"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	//////////////////////////////////////////////////
	// test suport
	//////////////////////////////////////////////////
	public static class SensorListTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new SensorList();
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * our editor
	 */
	protected MWC.GUI.Editable.EditorType _myEditor = null;

	/**
	 * our sensors
	 */
	private final HashMap<Integer, SensorType> _mySensors = new HashMap<Integer, SensorType>();

	/**
	 * whether we are currently active or not
	 */
	private boolean _isWorking = true;

	/**
	 * our name
	 */
	private String _myName;

	/**
	 * our historic list of detections
	 */
	private final ASSET.Models.Detection.DetectionList _myDetections = new DetectionList();

	////////////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////////////

	/**
	 * people listening out for my detections
	 */
	private Vector<ParticipantDetectedListener> _participantDetectedListeners;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////
	public SensorList() {
		_myName = "Sensor List";
	}

	/**
	 * add this new sensor
	 *
	 * @param sensor
	 */
	public void add(final SensorType sensor) {
		// does the sensor have an id?
		if (sensor.getId() <= 0) {
			throw new RuntimeException("missing sensor id for sensor:" + sensor + ". Unable to store");
		}

		// check if we already hold this sensor
		final Integer thisId = new Integer(sensor.getId());

		if (_mySensors.get(thisId) != null) {
			throw new RuntimeException("none-unique sensor id created");
		}

		_mySensors.put(new Integer(sensor.getId()), sensor);
	}

	/**********************************************************************
	 * sensor data provider methods
	 *********************************************************************/
	/**
	 * somebody wants to stop listening to us
	 *
	 * @param listener
	 */
	@Override
	public void addParticipantDetectedListener(final ParticipantDetectedListener listener) {
		if (_participantDetectedListeners == null)
			_participantDetectedListeners = new Vector<ParticipantDetectedListener>(1, 2);

		_participantDetectedListeners.add(listener);
	}

	/**
	 * allow somebody to start listening to the components of our calculation
	 *
	 * @param listener
	 */
	@Override
	public void addSensorCalculationListener(final PropertyChangeListener listener) {
		// ignore - let the sensors do it themselves
	}

	/**
	 * switch one of our sensors on or off
	 *
	 * @param medium   the medium to use
	 * @param switchOn whether to switch on or off
	 * @see ASSET.Models.Environment.EnvironmentType
	 */
	public void changeSensorLineup(final int medium, final boolean switchOn) {
		// find any sensors of this medium
		// step through our sensors
		final Iterator<SensorType> it = _mySensors.values().iterator();

		while (it.hasNext()) {
			final SensorType st = it.next();

			if (st.getMedium() == medium) {
				// ok, found one. do it!
				st.setWorking(switchOn);
			}
		}
	}

	/**
	 * see if we detect any other vessels
	 */
	@Override
	public void detects(final ASSET.Models.Environment.EnvironmentType environment,
			final DetectionList existingDetections, final ASSET.ParticipantType ownship,
			final ASSET.ScenarioType scenario, final long time) {
		// see if we are working
		if (_isWorking) {
			// don't worry about clearing out the old detections, the sensors themselves do
			// it.
			Vector<DetectionEvent> oldDetections;
			oldDetections = new Vector<DetectionEvent>(existingDetections);

			// step through the sensors
			final Iterator<SensorType> it = _mySensors.values().iterator();

			while (it.hasNext()) {
				final SensorType st = it.next();

				// do the detections for this sensor (they will add themselves to the
				// existingDetetions
				// object
				st.detects(environment, existingDetections, ownship, scenario, time);
			}

			// did we find any new detections
			if (!existingDetections.equals(oldDetections)) {
				// well, we must have done, find out what they are
				final DetectionList newCopy = new DetectionList(existingDetections);
				newCopy.removeAll(oldDetections);

				// add the new ones to our list
				for (int i = 0; i < newCopy.size(); i++) {
					final DetectionEvent event = newCopy.elementAt(i);
					_myDetections.add(event);
				}

				// this must be the new list.
				fireNewDetections(newCopy);
			}
		}

		// did
	}

	/**
	 * fire off the list of new detections
	 *
	 * @param list
	 */
	private void fireNewDetections(final DetectionList list) {
		if (_participantDetectedListeners != null) {
			for (int i = 0; i < _participantDetectedListeners.size(); i++) {
				final ParticipantDetectedListener listener = _participantDetectedListeners.elementAt(i);
				listener.newDetections(list);
			}
		}
	}

	/**
	 * find the list of all detections for this participant since the start of the
	 * scenario
	 */
	@Override
	public DetectionList getAllDetections() {
		return _myDetections;
	}

	/**
	 * the estimated range for a detection of this type (where applicable)
	 */
	@Override
	public WorldDistance getEstimatedRange() {
		return null;
	}

	/**
	 * get the id of this sensor list
	 *
	 * @return
	 */
	@Override
	public int getId() {
		return -1;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null) {
			_myEditor = new SensorFitInfo(this);
		}

		return _myEditor;
	}

	/**
	 * get the medium for this list. Hey, it doesn't make sense so throw a wobbly if
	 * we're asked to do it
	 *
	 * @return
	 */
	@Override
	public int getMedium() {
		final boolean IamTrue = true;
		if (IamTrue)
			throw new RuntimeException("Shouldn't be asking SensorFit for medium");

		return 1;
	}

	@Override
	public String getName() {
		return _myName;
	}

	/**
	 * find out how many sensors we hold
	 */
	public int getNumSensors() {
		return _mySensors.size();
	}

	/**
	 * find out the total radiated energy on this medium
	 */
	public double getRadiatedNoiseFor(final int medium) {
		// step through our sensors
		final Iterator<SensorType> it = _mySensors.values().iterator();
		double res = 0;

		while (it.hasNext()) {
			final SensorType st = it.next();

			// is this of the medium we're looking at?
			if (st.getMedium() == medium) {
				// is it an active sensor?
				if (st instanceof SensorType.ActiveSensor)
					// and is it switched on?
					if (st.isWorking()) {
						// is this the correct type of medium
						final SensorType.ActiveSensor at = (SensorType.ActiveSensor) st;
						res += at.getSourceLevel();
					}
			}
		}

		return res;
	}

	/**
	 * get the specific sensor at the indicated index
	 */
	public ASSET.Models.SensorType getSensor(final int index) {
		return _mySensors.get(new Integer(index));
	}

	/**
	 * get the list of sensors we contain
	 *
	 * @return
	 */
	public Collection<SensorType> getSensors() {
		return _mySensors.values();
	}

	//////////////////////////////////////////////////
	// layer support (for drill-down)
	//////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// utility methods
	//////////////////////////////////////////////////

	/**
	 * get the sensor with the supplied id (not index)
	 *
	 * @param sensorId the id of the sensor to retrieve
	 * @return that sensor
	 */
	public ASSET.Models.SensorType getSensorWithId(final int sensorId) {
		SensorType res = null;
		for (final Iterator<SensorType> iter = this.getSensors().iterator(); iter.hasNext();) {
			final SensorType se = iter.next();
			if (se.getId() == sensorId) {
				res = se;
				break;
			}
		}
		return res;
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
	 * handle the demanded change in sensor lineup
	 *
	 * @param status
	 */
	@Override
	public void inform(final DemandedSensorStatus status) {
		_isWorking = status.getSwitchOn();
	}

	/**
	 * determine the state of this sensor
	 *
	 * @return yes/no for if it's working
	 */
	@Override
	public boolean isWorking() {
		return _isWorking;
	}

	/**
	 * @param medium the medium we're looking at
	 * @return yes/no
	 */
	public boolean radiatesThisMedium(final int medium) {
		// step through our sensors
		final Iterator<SensorType> it = _mySensors.values().iterator();
		boolean res = false;

		while (it.hasNext()) {
			final SensorType st = it.next();

			if (st instanceof SensorType.ActiveSensor)
				if (st.isWorking()) {
					res = true;
				}
		}

		return res;
	}

	/**
	 * somebody wants to start listening to us
	 *
	 * @param listener
	 */
	@Override
	public void removeParticipantDetectedListener(final ParticipantDetectedListener listener) {
		_participantDetectedListeners.remove(listener);
	}

	/**
	 * remove a calculation listener
	 *
	 * @param listener
	 */
	@Override
	public void removeSensorCalculationListener(final PropertyChangeListener listener) {
		// ignore - let the sensors do it themselves }
	}

	/**
	 * reset all of the sensors
	 */
	@Override
	public void restart() {
		// step through the sensors
		final Iterator<SensorType> it = _mySensors.values().iterator();

		while (it.hasNext()) {
			final SensorType st = it.next();

			// ok, now restart it
			st.restart();
		}

		// and clear our list
		_myDetections.removeAllElements();

	}

	@Override
	public void setName(final String val) {
		_myName = val;
	}

	/**
	 * control the state of this sensor
	 *
	 * @param switchOn whether to switch it on or off.
	 */
	@Override
	public void setWorking(final boolean switchOn) {
		_isWorking = switchOn;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * if this sensor has a dynamic behaviour, update it according to the demanded
	 * status
	 *
	 * @param myDemandedStatus
	 * @param myStatus
	 * @param newTime
	 */
	@Override
	public void update(final DemandedStatus myDemandedStatus, final Status myStatus, final long newTime) {
		// don't bother. let classes over-ride as necessary
	}

}