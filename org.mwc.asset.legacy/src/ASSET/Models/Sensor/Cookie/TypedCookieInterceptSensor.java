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

package ASSET.Models.Sensor.Cookie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.SensorType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor.TypedRangeDoublet;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor.TypedSensorComponentsEvent;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * Extension of the simple cookie cutter model that allows different cookie
 * cutter ranges to be specified for different target types
 *
 * @author ianmayo
 *
 */
public class TypedCookieInterceptSensor extends CoreSensor {

	// //////////////////////////////////////////////////
	// the editor object
	// //////////////////////////////////////////////////
	static public class TypedCookieInfo extends BaseSensorInfo {
		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public TypedCookieInfo(final TypedCookieInterceptSensor data) {
			super(data);
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			return super.getPropertyDescriptors();
		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Vector<TypedRangeDoublet> _rangeDoublets;

	private final HashMap<TypedRangeDoublet, DetectionList> _typedDetections;

	private Integer _detectionState = DetectionEvent.DETECTED;

	/**
	 * optional sensor medium
	 *
	 */
	private int _medium;

	public TypedCookieInterceptSensor(final int id, final Vector<TypedRangeDoublet> rangeDoublets) {
		this(id, rangeDoublets, DetectionEvent.DETECTED);
	}

	public TypedCookieInterceptSensor(final int id, final Vector<TypedRangeDoublet> rangeDoublets,
			final Integer detectionState) {
		super(id, 0, "Plain Cookie");

		_rangeDoublets = rangeDoublets;
		_typedDetections = new HashMap<TypedRangeDoublet, DetectionList>();
		_detectionState = detectionState;

	}

	protected void addRangeDoublet(final TypedRangeDoublet myRangeDoublet) {
		if (_rangeDoublets == null)
			_rangeDoublets = new Vector<TypedRangeDoublet>();

		_rangeDoublets.add(myRangeDoublet);
	}

	@Override
	protected boolean canDetectThisType(final NetworkParticipant ownship, final ParticipantType other,
			final EnvironmentType env) {
		return true;
	}

	@Override
	public boolean canIdentifyTarget() {
		return true;
	}

	@Override
	protected DetectionEvent detectThis(final EnvironmentType environment, final ParticipantType host,
			final ParticipantType target, final long time, final ScenarioType scenario) {
		DetectionEvent res = null;

		// right, what's the distance?
		WorldDistance range = null; // defer calculation until we need it
		WorldVector sep = null;

		// ok, see if this participant is radiating our noce type
		final WorldLocation hostLoc = getHostLocationFor(host);

		final SensorList sList = target.getSensorFit();
		final Collection<SensorType> coll = sList.getSensors();
		final Iterator<SensorType> iter = coll.iterator();
		while (iter.hasNext()) {
			final SensorType thisS = iter.next();
			if (thisS.isWorking()) {
				final int medium = thisS.getMedium();
				if (medium == _medium) {
					// see if we can see at this range
					// loop through our detection types
					for (final Iterator<TypedRangeDoublet> iterator = _rangeDoublets.iterator(); iterator.hasNext();) {
						boolean detected = false;

						final TypedRangeDoublet doublet = iterator.next();
						if (doublet.mayDetect(target.getCategory())) {
							// sort out the range
							if (range == null) {
								range = new WorldDistance(hostLoc.rangeFrom(target.getStatus().getLocation()),
										WorldDistance.DEGS);
							}

							// see if it's in range
							if (doublet.canDetect(range)) {

								// calculate the separation - so we can plot a bearing
								sep = target.getStatus().getLocation().subtract(hostLoc);

								detected = true;

								final double brgDegs = MWC.Algorithms.Conversions.Rads2Degs(sep.getBearing());

								// cool, in contact. write it up.
								res = new DetectionEvent(time, host.getId(), hostLoc, this, range, range,
										new Float(brgDegs),
										new Float(super.relativeBearing(host.getStatus().getCourse(), brgDegs)),
										new Float(1), target.getCategory(),
										new Float(target.getStatus().getSpeed().getValueIn(WorldSpeed.Kts)),
										new Float(target.getStatus().getCourse()), target);

								res.setDetectionState(_detectionState);

								// store this detection
								storeThisDetection(doublet, res);
							}
						}

						// ok, just see if there are any pSupport listners
						if (_pSupport != null) {
							if (_pSupport.hasListeners(SENSOR_COMPONENT_EVENT)) {
								String typeList = "";
								final Vector<String> types = doublet.getMyTypes();
								if (types != null) {
									for (final Iterator<String> iterator2 = types.iterator(); iterator2.hasNext();) {
										final String string = iterator2.next();
										typeList += string + ",";
									}
								}

								if (typeList.length() == 0)
									typeList = "Unset";

								// create the event
								final TypedSensorComponentsEvent sev = new TypedSensorComponentsEvent(time, range,
										doublet.getRange(), typeList, detected, target.getName());

								// and fire it!
								_pSupport.firePropertyChange(SENSOR_COMPONENT_EVENT, null, sev);
							}
						}

					}
				}

			}

		}

		return res;
	}

	public DetectionList getDetectionsFor(final TypedRangeDoublet doublet) {
		return _typedDetections.get(doublet);
	}

	@Override
	public WorldDistance getEstimatedRange() {
		return null;
	}

	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new TypedCookieInfo(this);
		return _myEditor;
	}

	@Override
	public int getMedium() {
		return _medium;
	}

	public Vector<TypedRangeDoublet> getRanges() {
		return _rangeDoublets;
	}

	@Override
	public String getVersion() {
		return "$Date: 2010-01-19 15:34:14 +0100  $";
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	public void setMedium(final int medium) {
		_medium = medium;
	}

	/**
	 * store this detection in our typed collections
	 *
	 * @param doublet
	 * @param detection
	 */
	private void storeThisDetection(final TypedRangeDoublet doublet, final DetectionEvent detection) {
		DetectionList dl = _typedDetections.get(doublet);
		if (dl == null) {
			dl = new DetectionList();
			_typedDetections.put(doublet, dl);
		}

		dl.add(detection);
	}

	@Override
	public void update(final DemandedStatus myDemandedStatus, final Status myStatus, final long newTime) {
		final Vector<DemandedSensorStatus> states = myDemandedStatus.getSensorStates();
		if (states != null) {
			final Iterator<DemandedSensorStatus> iter = states.iterator();
			while (iter.hasNext()) {
				final DemandedSensorStatus ds = iter.next();
				if (ds.getMedium() == getMedium()) {
					if (ds.getSwitchOn())
						this.setWorking(true);
					else
						this.setWorking(false);
				}
			}
		}
	}

}
