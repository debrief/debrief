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

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.SensorList;
import ASSET.Participants.Category;
import ASSET.Participants.Category.Environment;
import ASSET.Participants.Category.Force;
import ASSET.Participants.Category.Type;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
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
public class TypedCookieSensor extends CoreSensor {

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class PlainCookieTest extends SupportTesting.EditableTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public PlainCookieTest(final String val) {
			super(val);
		}

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new PlainCookieSensor(1, new WorldDistance(12, WorldDistance.DEGS));
		}

		public void testPlainSensor() {

			// reset the earth model
			WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

			// ok, create the sensor
			final PlainCookieSensor ps = new PlainCookieSensor(12, new WorldDistance(12, WorldDistance.DEGS));

			// now the objects
			final WorldLocation l1 = new WorldLocation(0, 0, 0);
			final WorldLocation l2 = l1.add(new WorldVector(0, 10, 0));
			final WorldLocation l3 = l1.add(new WorldVector(0, 13, 0));

			final ASSET.Models.Vessels.Surface su1 = new ASSET.Models.Vessels.Surface(12);
			final Status theStat1 = new Status(12, 0);
			theStat1.setLocation(l1);
			theStat1.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			su1.setStatus(theStat1);
			final SensorList sl = new SensorList();
			sl.add(ps);

			final ASSET.Models.Vessels.Surface su2 = new ASSET.Models.Vessels.Surface(14);
			final Status theStat2 = new Status(12, 0);
			theStat2.setLocation(l2);
			theStat2.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			su2.setStatus(theStat2);

			final EnvironmentType env = new SimpleEnvironment(1, 1, 1);

			DetectionEvent de = ps.detectThis(env, su1, su2, 1000, null);

			assertNotNull("Made a detection", de);

			su2.getStatus().setLocation(l3);

			de = ps.detectThis(env, su1, su2, 1000, null);

			assertNull("Not made a detection", de);

		}

		public void testRangeDoublet() {
			final Vector<String> listOne = new Vector<String>();
			listOne.add(Category.Force.BLUE);
			listOne.add(Category.Force.GREEN);
			listOne.add(Category.Environment.AIRBORNE);
			listOne.add(Category.Type.FISHING_VESSEL);

			final WorldDistance dist = new WorldDistance(2, WorldDistance.NM);

			final TypedRangeDoublet td = new TypedRangeDoublet(listOne, dist);
			final WorldDistance tRange = new WorldDistance(1, WorldDistance.NM);
			final WorldDistance longRange = new WorldDistance(3, WorldDistance.NM);

			Category tCat = new Category(Force.RED, Environment.AIRBORNE, Type.HELO);
			assertTrue("Matches item", td.mayDetect(tCat));
			assertFalse("too far", td.canDetect(longRange));

			tCat = new Category(Force.RED, Environment.SURFACE, Type.HELO);
			assertFalse("doesn't match item", td.mayDetect(tCat));

			tCat = new Category(Force.RED, Environment.SURFACE, Type.FISHING_VESSEL);
			assertTrue("Matches item", td.mayDetect(tCat));
			assertTrue("Matches item", td.canDetect(tRange));

			tCat = new Category(Force.BLUE, Environment.SURFACE, Type.HELO);
			assertTrue("doesn't match item", td.mayDetect(tCat));
		}
	}

	// //////////////////////////////////////////////////
	// the editor object
	// //////////////////////////////////////////////////
	static public class TypedCookieInfo extends BaseSensorInfo {
		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public TypedCookieInfo(final TypedCookieSensor data) {
			super(data);
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				// get the parent attributes
				final PropertyDescriptor[] parentAttributes = super.getPropertyDescriptors();

				// get my attributes
				final PropertyDescriptor[] myAttributes = {
						prop("ProducesRange", "whether this sensor can produce range values") };

				// ok, now try to combine the two
				final PropertyDescriptor[] res = new PropertyDescriptor[parentAttributes.length + myAttributes.length];
				// copy the arrays into it
				System.arraycopy(parentAttributes, 0, res, 0, parentAttributes.length);
				System.arraycopy(myAttributes, 0, res, parentAttributes.length, myAttributes.length);

				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	/**
	 * store the detection distance for the specified category types
	 *
	 * @author ianmayo
	 *
	 */
	public static class TypedRangeDoublet {
		/**
		 * the types this range applies to
		 *
		 */
		private final Vector<String> _types;

		/**
		 * this detection range
		 *
		 */
		private WorldDistance _range;

		/**
		 * how long to hold the target before it reaches the detection level
		 *
		 */
		private Duration _period;

		public TypedRangeDoublet(final Vector<String> types, final WorldDistance range) {
			_types = types;
			_range = range;
		}

		/**
		 * see if we can detect the target
		 *
		 * @param targetCat   the type of target we're looking at
		 * @param targetRange the range to the target
		 * @return
		 */
		public boolean canDetect(final WorldDistance targetRange) {
			boolean res = false;

			// right, we can detect it - see if we are in range
			if (targetRange.lessThan(_range))
				res = true;
			else
				res = false;

			return res;
		}

		public Vector<String> getMyTypes() {
			return _types;
		}

		public Duration getPeriod() {
			return _period;
		}

		public WorldDistance getRange() {
			return _range;
		}

		/**
		 * see if we can detect the target
		 *
		 * @param targetCat   the type of target we're looking at
		 * @param targetRange the range to the target
		 * @return
		 */
		public boolean mayDetect(final Category targetCat) {
			boolean res = false;

			// do we have our own types object
			if (_types == null) {
				// nope, in that case, use this as a default and just use the range item
				res = true;
			} else {

				// loop through our types
				for (final Iterator<String> iterator = _types.iterator(); iterator.hasNext();) {
					final String thisType = iterator.next();
					// right, does this match
					if (targetCat.getType().equals(thisType))
						res = true;
					else if (targetCat.getEnvironment().equals(thisType))
						res = true;
					else if (targetCat.getForce().equals(thisType))
						res = true;
				}
			}

			return res;
		}

		public void setPeriod(final Duration period) {
			_period = period;
		}

		public void setRange(final WorldDistance range) {
			_range = range;
		}
	}

	// //////////////////////////////////////////////////
	// embedded class for event fired after each detection step
	// //////////////////////////////////////////////////
	public static class TypedSensorComponentsEvent {
		// //////////////////////////////////////////////////
		// member objects
		// //////////////////////////////////////////////////
		final private String _tgtName;
		final private long _time;
		private final WorldDistance _detRange;
		private final WorldDistance _tgtRange;
		private final String _typeCriteria;
		private final boolean _detected;

		/**
		 * constructor
		 */
		public TypedSensorComponentsEvent(final long time, final WorldDistance detRange, final WorldDistance tgtRange,
				final String typeCriteria, final boolean detected, final String tgtName) {

			_detRange = detRange;
			_tgtRange = tgtRange;
			_typeCriteria = typeCriteria;
			_tgtName = tgtName;
			_time = time;
			_detected = detected;
		}

		public boolean getDetected() {
			return _detected;
		}

		public WorldDistance getDetRange() {
			return _detRange;
		}

		public String getTgtName() {
			return _tgtName;
		}

		public WorldDistance getTgtRange() {
			return _tgtRange;
		}

		public long getTime() {
			return _time;
		}

		public String getTypeCriteria() {
			return _typeCriteria;
		}

		@Override
		public String toString() {
			String res;

			res = _typeCriteria + " det:" + (int) _detRange.getValueIn(WorldDistance.YARDS) + "actual:"
					+ (int) _tgtRange.getValueIn(WorldDistance.YARDS) + " " + _tgtName;

			return res;
		}
	}

	/**
	 * mean of data in data supplied by Iain Mc, Autumn 2016
	 *
	 */
	private static final double MEAN = 0.052;

	/**
	 * SD of data in data supplied by Iain Mc, Autumn 2016
	 *
	 */
	private static final double STANDARD_DEVIATION = 0.4;

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Vector<TypedRangeDoublet> _rangeDoublets;

	private final HashMap<TypedRangeDoublet, DetectionList> _typedDetections;

	private Integer _detectionState = DetectionEvent.DETECTED;
	//
	// private double[] _noise = new double[]
	// {0.27, 0.96, 0.20, 0.14, 0.82, 0.49, -0.19, 0.09, 0.00, 0.36, 0.00, -0.13,
	// 0.46, 0.79, 0.01, -0.05, -0.51, 0.51, 0.81, 0.84, 1.03, 0.62, -0.31,
	// 0.06, 0.35, 0.63, 0.01, 0.01, 0.34, 0.60, -1.82, 0.59, 0.49, 0.12, -0.48,
	// -0.05, 0.05, -0.23, 0.39, -0.22, 0.17, 0.12, -0.07, -0.86, -0.61, -0.27,
	// 0.13, 0.13, 0.09, -0.16, -0.60, -0.03, 0.30, -0.18, 0.06, -1.16, -0.43,
	// 0.25, 0.45, -0.11, 0.38, 0.04, -0.69, 0.11, 0.08, 0.04, 0.04, -0.39,
	// -0.23, -0.16, 0.67, -0.14, -0.31, 0.26, -0.56, 0.30, -0.99, -0.19, -0.01,
	// 0.36, 0.13, 0.07, 0.40, 0.63, -0.14, 0.74, -0.14, 0.45, 0.08, 0.55,
	// -0.19, -0.36, -0.25, -0.08, 0.13, 2.27, 0.35};

	/**
	 * optional sensor medium
	 *
	 */
	private int _medium;

	/**
	 * whether this sensor is capable of producing range data
	 *
	 */
	private boolean _produceRange = true;

	/**
	 * whether to apply noise to the measurements
	 *
	 */
	private boolean _applyNoise = false;

	private HashMap<Integer, Long> _timesGained = null;

	public TypedCookieSensor(final int id, final Vector<TypedRangeDoublet> rangeDoublets) {
		this(id, rangeDoublets, DetectionEvent.DETECTED);
	}

	public TypedCookieSensor(final int id, final Vector<TypedRangeDoublet> rangeDoublets,
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
		// note, we don't do our type checking here, since in the detectThis method
		// we will still have to loop through all our types in order to ge the
		// relevant
		// range doublet.
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

		final WorldLocation hostLoc = getHostLocationFor(host);

		// loop through our detection types
		for (final Iterator<TypedRangeDoublet> iterator = _rangeDoublets.iterator(); iterator.hasNext();) {
			boolean detected = false;

			final TypedRangeDoublet doublet = iterator.next();
			if (doublet.mayDetect(target.getCategory())) {
				// ok, it's worth sorting out the range
				if (range == null) {
					range = host.rangeFrom(target.getStatus().getLocation());
				}

				detected = doublet.canDetect(range);

				if (!detected) {
					// aah. if we've lost a contact, we clear the in-contact time
					if (_timesGained != null) {
						// hey, we're tracking time gained. forget about this contact, if we were
						// tracking it.
						// _timesGained.remove(target.getId());
					}
				}

				if (detected) {

					// just check if the doublet has a time duration
					final Duration detectionPeriod = doublet.getPeriod();
					if (detectionPeriod != null) {

						if (_timesGained == null)
							_timesGained = new HashMap<Integer, Long>();

						// ok, we have to track how long we have held this contact for
						final Long timeGained = _timesGained.get(target.getId());

						if (timeGained == null) {
							// initial contact, store it
							_timesGained.put(target.getId(), time);

							// we certainly haven't reached the correct time period
							detected = false;
						} else {
							// ok, has the time elapsed?
							final long elapsed = time - timeGained;
							final long required = detectionPeriod.getMillis();

							if (elapsed < required) {
								// just need to give it a little longer
								detected = false;
							}
						}
					}

					// second pass on detected (including duration)
					if (detected) {
						// calculate the separation - so we can plot a bearing
						sep = target.getStatus().getLocation().subtract(hostLoc);

						double brgDegs = MWC.Algorithms.Conversions.Rads2Degs(sep.getBearing());

						// do we need to apply noise?
						if (getApplyNoise()) {
							// long index = time % _noise.length;
							// brgDegs += _noise[(int) index];
							final Random rnd = new Random();
							brgDegs += MEAN + rnd.nextGaussian() * STANDARD_DEVIATION;
						}

						final WorldDistance rangeToUse;
						if (_produceRange)
							rangeToUse = range;
						else
							rangeToUse = null;

						// cool, in contact. write it up.
						res = new DetectionEvent(time, host.getId(), hostLoc, this, rangeToUse, rangeToUse,
								new Float(brgDegs),
								new Float(super.relativeBearing(host.getStatus().getCourse(), brgDegs)), new Float(1),
								target.getCategory(),
								new Float(target.getStatus().getSpeed().getValueIn(WorldSpeed.Kts)),
								new Float(target.getStatus().getCourse()), target);

						res.setDetectionState(_detectionState);

						// store this detection
						storeThisDetection(doublet, res);
					}
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

		return res;
	}

	public boolean getApplyNoise() {
		return _applyNoise;
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

	/**
	 * whether this sensor is capable of producing range values
	 *
	 * @return yes/no
	 */
	public boolean getProducesRange() {
		return _produceRange;
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

	/**
	 * whether to add noise to measurements
	 *
	 * @param val
	 */
	public void setApplyNoise(final boolean val) {
		_applyNoise = val;
	}

	public void setMedium(final int medium) {
		_medium = medium;
	}

	/**
	 * whether this sensor is capable of producing range values
	 *
	 * @param val yes/no
	 */
	public void setProducesRange(final boolean val) {
		_produceRange = val;
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
