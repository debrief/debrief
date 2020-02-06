
package ASSET.Models.Sensor.Lookup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Vessels.Helo;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.StopOnElapsedObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

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

public abstract class LookupSensor extends CoreSensor {

	// //////////////////////////////////////////////////////////
	// standard lookup parameters
	// //////////////////////////////////////////////////////////

	/**
	 * base class providing unknown result functionality
	 */
	protected static class BaseLookup {
		/**
		 * the value to return for an unknown target type
		 */
		protected Double _unknownResult;

		/**
		 * return the unknown result value, if we have to
		 *
		 * @param val the value to check against
		 * @return either the valid result, or our unknown value
		 */
		protected Double checkResult(Double val, final Double unknownResult) {
			if (val == null)
				val = unknownResult;

			return val;
		}

		public Double getUnknownResult() {
			return _unknownResult;
		}

		/**
		 * specify what value to return for an unknown target type
		 *
		 * @param unknownResult value to return.
		 */
		public void setUnknownResult(final Double unknownResult) {
			this._unknownResult = unknownResult;
		}
	}

	// //////////////////////////////////////////////////////////
	// embedded class which provides a lookup table of integer values, returning
	// a double value for the matching string
	// //////////////////////////////////////////////////////////
	public static class IntegerLookup {
		/**
		 * store the list of double values indexed by an integer
		 */
		private final HashMap<Integer, Double> _myTable;

		/**
		 * constructor - create our array
		 */
		public IntegerLookup() {
			_myTable = new HashMap<Integer, Double>();
		}

		/**
		 * constructor
		 *
		 * @param indices list of integer indices
		 * @param vals    list of double values
		 */
		public IntegerLookup(final int[] indices, final double[] vals) {
			this();
			if (indices.length != vals.length) {
				System.err.println("INTEGER LOOKUP PARAMETERS OF UNEQUAL LENGTH!!!");
			}

			for (int i = 0; i < indices.length; i++) {
				final Integer key = new Integer(indices[i]);
				final double val = vals[i];
				_myTable.put(key, new Double(val));
			}
		}

		public void add(final int index, final double value) {
			_myTable.put(new Integer(index), new Double(value));
		}

		/**
		 * find the double value at the supplied index
		 *
		 * @param index the integer to index against
		 * @return the matching double value (or null)
		 */
		public Double find(final int index) {
			final Integer indexVal = new Integer(index);

			Double res = null;
			final Iterator<Integer> iterator = _myTable.keySet().iterator();
			while (iterator.hasNext() && res == null) {
				final Integer s = iterator.next();
				if (s.equals(indexVal)) {
					res = _myTable.get(indexVal);
					break;
				}
			}

			return res;
		}

		public Collection<Integer> indices() {
			return _myTable.keySet();
		}
	}

	// //////////////////////////////////////////////////////////
	// embedded class which provides a two-dimensional lookup table of string
	// values against sea-state, returning
	// a double value for the matching string
	// //////////////////////////////////////////////////////////
	public static class IntegerTargetTypeLookup {
		/**
		 * store the list of double values indexed by string
		 */
		private final Vector<NamedList> _datums;

		/**
		 * the default value to use if none of the types are recognised
		 */
		private Double _defaultValue;

		// /**
		// * constructor
		// *
		// * @param states list of integer indices
		// * @param vals list of double values
		// * @param defaultValue the value to use if the target type hasn't been
		// represented in the table
		// */
		// public IntegerTargetTypeLookup(int[] states, StringLookup[] vals, Double
		// defaultValue)
		// {
		// if (states.length != vals.length)
		// {
		// System.err.println("INTEGER LOOKUP PARAMETERS OF UNEQUAL LENGTH!!!");
		// }
		//
		// _defaultValue = defaultValue;
		//
		// _myTable = new HashMap();
		// for (int i = 0; i < states.length; i++)
		// {
		// Integer key = new Integer(states[i]);
		// _myTable.put(key, vals[i]);
		// }
		// }

		public IntegerTargetTypeLookup(final Vector<NamedList> datums, final Double value) {
			_defaultValue = value;

			// and store the datums
			_datums = datums;
		}

		/**
		 * find the double value at the supplied index
		 *
		 * @param index the integer to index against
		 * @return the matching double value (or our null value)
		 */
		public Double find(final int index, final String type) {
			Double res = _defaultValue;
			// right, first get the correct set of datums
			for (final Iterator<NamedList> iterator = _datums.iterator(); iterator.hasNext();) {
				// get the next series
				final NamedList thisList = iterator.next();

				// is this the correct one
				if (thisList._myType.equals(type)) {
					// cool, here we are, get the correct type
					final Double val = thisList._myValues.get(index);
					if (val != null)
						res = val;
					break;
				}
			}

			return res;
		}

		public Collection<String> getNames() {
			final Vector<String> res = new Vector<String>(0, 1);
			for (final Iterator<NamedList> iterator = _datums.iterator(); iterator.hasNext();) {
				// get the next series
				final NamedList thisList = iterator.next();
				// and remember it
				res.add(thisList._myType);
			}

			return res;
		}

		public NamedList getThisSeries(final String name) {
			NamedList thisSet = null;
			// right, first get the correct set of datums
			for (final Iterator<NamedList> iterator = _datums.iterator(); iterator.hasNext();) {
				// get the next series
				thisSet = iterator.next();

				// is this the correct one
				if (thisSet._myType == name)
					break;
			}

			return thisSet;

		}

		/**
		 * find out the default value
		 */
		public Double getUnknownResult() {
			return _defaultValue;
		}

		/**
		 * specify what value to return for an unknown target type
		 *
		 * @param unknownResult value to return.
		 */
		public void setUnknownResult(final Double unknownResult) {
			this._defaultValue = unknownResult;
		}
	}

	protected class LastTargetContact {

		/**
		 * our list of properties
		 */
		protected Vector<Number> _myList;

		/**
		 * the instantaneous range calculated
		 */
		WorldDistance _theInstantaneousRange;

		/**
		 * the predicted range
		 */
		WorldDistance _thePredictedRange;

		/**
		 * the last detection we obtained
		 */
		int _currentState = DetectionEvent.UNDETECTED;

		/**
		 * the time we made the transition to this state (in order to allow the
		 * time-related transition to the next)
		 */
		private long _thisTransition = -1;

		// //////////////////////////////////////////////////////////
		// constructor
		// //////////////////////////////////////////////////////////
		public LastTargetContact() {
			this._myList = new Vector<Number>(1, 1);
		}

		public Object elementAt(final int i) {
			return _myList.elementAt(i);
		}

		public Object get(final int index) {
			return _myList.get(index);
		}

		/**
		 * get the last detection
		 *
		 * @return the last detection state
		 * @see DetectionEvent.UNDETECTED for an example
		 */
		public int getDetectionState() {
			return _currentState;
		}

		/**
		 * get the instantaneous range
		 *
		 * @return
		 */
		public WorldDistance getRI() {
			return _theInstantaneousRange;
		}

		/**
		 * get the predicted range
		 *
		 * @return
		 */
		public WorldDistance getRP() {
			return _thePredictedRange;
		}

		/**
		 * get the time of the last transition
		 */
		public long getTimeOfThisTransition() {
			return _thisTransition;
		}

		public void insertElementAt(final Double value, final int index) {
			_myList.insertElementAt(value, index);
		}

		/**
		 * find out if the other set of parameters matches this one
		 *
		 * @param otherParams the other set of parameters
		 * @return yes/no
		 */
		public boolean matchesThis(final LastTargetContact otherParams) {
			boolean res = true;

			// first do "idiot test", if list are of different length they must be
			// different
			if (otherParams.size() != size())
				res = false;
			else {
				for (int i = 0; i < otherParams.size(); i++) {
					final Object otherObject = otherParams.elementAt(i);
					final Object thisObject = elementAt(i);

					// do they match?
					if (!otherObject.equals(thisObject)) {
						res = false;
						continue;
					}

				}
			}
			return res;
		}

		/**
		 * store the last detection (so we can advance the detection state if we want to
		 */
		public void setDetectionState(final int state) {
			_currentState = state;
		}

		/**
		 * set the predicted and instantaneous ranges
		 */
		public void setRanges(final WorldDistance RI, final WorldDistance RP) {
			_theInstantaneousRange = RI;
			_thePredictedRange = RP;
		}

		/**
		 * set the time of this transition
		 */
		public void setTransitionTime(final long val) {
			_thisTransition = val;
		}

		public int size() {
			return _myList.size();
		}
	}

	// //////////////////////////////////////////////////
	// embedded class for event fired after each detection step
	// //////////////////////////////////////////////////
	public static class LookupSensorComponentsEvent {
		// //////////////////////////////////////////////////
		// member objects
		// //////////////////////////////////////////////////

		/**
		 * the name of the target we're looking at
		 */
		final private String _tgtName;

		/**
		 * the current detection state (string)
		 */
		final private String _stateString;

		/**
		 * the current detection state
		 */
		final private int _state;

		/**
		 * a utility class to convert from state to text
		 */
		private final DetectionEvent.DetectionStatePropertyEditor converter = new DetectionEvent.DetectionStatePropertyEditor();

		/**
		 * the ranges we want
		 */
		final private WorldDistance _RI;

		final private WorldDistance _RP;

		final private WorldDistance _actual;

		/**
		 * the time at which this was recorded
		 *
		 */
		final private long _time;

		// //////////////////////////////////////////////////////////
		// constructor
		// //////////////////////////////////////////////////////////

		/**
		 * constructor
		 */
		public LookupSensorComponentsEvent(final long time, final int state, final WorldDistance RI,
				final WorldDistance RP, final WorldDistance actual, final String tgtName) {
			_time = time;
			_tgtName = tgtName;
			converter.setIndex(state);
			_stateString = converter.getAsText();
			_state = state;
			_actual = actual;
			_RP = RP;
			_RI = RI;
		}

		public WorldDistance getActual() {
			return _actual;
		}

		public WorldDistance getRI() {
			return _RI;
		}

		public WorldDistance getRP() {
			return _RP;
		}

		public int getState() {
			return _state;
		}

		public String getStateString() {
			return _stateString;
		}

		public String getTgtName() {
			return _tgtName;
		}

		public long getTime() {
			return _time;
		}

		@Override
		public String toString() {
			String res;

			res = _tgtName;

			return res;
		}
	}

	// //////////////////////////////////////////////////////////
	// TESTING CODE
	// //////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class LookupSensorTest extends SupportTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		private static TestSensor getTestSensor(final double rp_m, final double ri_m, final double cr_m,
				final double ir_m, final double max_m) {
			return new TestSensor(0.05, 1000, 1.05, 0.8, new Duration(20, Duration.SECONDS), 0.2,
					new Duration(30, Duration.SECONDS), rp_m, ri_m, cr_m, ir_m, max_m);
		}

		public LookupSensorTest(final String val) {
			super(val);
		}

		public void testClearingOldDetections() {
			// create the sensors
			final OpticLookupSensor os = OpticLookupSensor.OpticLookupTest.getTestOpticSensor();
			os.setTimeBetweenDetectionOpportunities(5000);
			final RadarLookupSensor rs = RadarLookupSensor.RadarLookupTest.getTestRadarSensor(4000, 3100, 2500, 1900,
					4700, 600);
			rs.setTimeBetweenDetectionOpportunities(8000);

			// and the participants
			final Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			final Helo alpha = new Helo(12);
			alpha.setName("Alpha");
			alpha.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
			final WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			statA.getLocation().setDepth(-900);
			alpha.setStatus(statA);
			final Surface bravo = new Surface(11);
			final Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			bravo.setName("Bravo");
			bravo.setCategory(
					new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
			final WorldLocation originB = SupportTesting.createLocation(5000, 5000);
			statB.setLocation(originB);
			statB.getLocation().setDepth(-20);
			bravo.setStatus(statB);
			final Surface charlie = new Surface(22);
			final Status statC = new Status(12, 0);
			statC.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			charlie.setName("charlie");
			charlie.setCategory(
					new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
			final WorldLocation originC = SupportTesting.createLocation(5050, 5000);
			statC.setLocation(originC);
			statC.getLocation().setDepth(-20);
			charlie.setStatus(statC);

			final SensorList sl = new SensorList();
			sl.add(os);
			sl.add(rs);

			alpha.setSensorFit(sl);

			// lastly the scenario
			final CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(alpha.getId(), alpha);
			scenario.addParticipant(bravo.getId(), bravo);
			scenario.addParticipant(charlie.getId(), charlie);

			scenario.setScenarioStepTime(1000);

			// create our dummy environment object
			final OpticLookupSensor.OpticLookupTest.MyEnvironment env = new OpticLookupSensor.OpticLookupTest.MyEnvironment();

			// ok. create the empty detectionList
			final DetectionList dets = new DetectionList();

			// make env sensor friendly
			env.setSeaState(1);

			long curTime = 1000;

			// do we get anything?
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that list still empty
			assertEquals("Obtained first detections", dets.size(), 2);

			// right, get the detections
			DetectionEvent d1 = dets.elementAt(0);
			DetectionEvent d2 = dets.elementAt(1);

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that we still have the old detections
			assertFalse("last contact deleted", dets.contains(d1));
			assertFalse("last contact deleted", dets.contains(d2));

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that we still have the old detections
			assertFalse("last contact deleted", dets.contains(d1));
			assertFalse("last contact deleted", dets.contains(d2));

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that we still have the old detections
			assertFalse("last contact deleted", dets.contains(d1));
			assertFalse("last contact deleted", dets.contains(d2));

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that we still have the old detections
			assertFalse("last contact deleted", dets.contains(d1));
			assertFalse("last contact deleted", dets.contains(d2));

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that we have removed the old detections
			assertFalse("should have ditched last contact", dets.contains(d1));
			assertFalse("should have ditched last contact", dets.contains(d2));
			d1 = dets.elementAt(0);
			d2 = dets.elementAt(1);

			// check that we still have the old detections
			assertTrue("still have last contact", dets.contains(d1));
			assertTrue("still have last contact", dets.contains(d2));

			// check that we have new contacts
			assertEquals("have new contacts", 2, dets.size());

			// change range so that we know we won't get detection
			alpha.getStatus().setLocation(SupportTesting.createLocation(90000, 90000));

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 3000);

			// check that we have removed the old detections and that we still have
			// the old detections
			assertFalse("last contact deleted", dets.contains(d1));
			assertFalse("last contact deleted", dets.contains(d2));
			assertEquals("should have kept contacts, havne't done next scan yet", 0, dets.size());

			// keep moving forward,
			curTime += 3000;
			sl.detects(env, dets, alpha, scenario, curTime += 3000);

			// right, passed time for next scan. check that old detections removed,
			// and no new ones added.
			assertFalse("still have last contact", dets.contains(d1));
			assertFalse("still have last contact", dets.contains(d2));
			assertEquals("should have lost contacts", 0, dets.size());

		}

		public final void testFirst() {
			final TestSensor ts = getTestSensor(4000, 3100, 2500, 1900, 4700);
			long time = 1000;
			final Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			final Helo alpha = new Helo(12);
			alpha.setName("Alpha");
			final WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			alpha.setStatus(statA);
			final Surface bravo = new Surface(11);
			final Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			bravo.setName("Bravo");
			final WorldLocation originB = SupportTesting.createLocation(6000, 6000);
			statB.setLocation(originB);
			bravo.setStatus(statB);

			alpha.addSensor(ts);
			final CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(alpha.getId(), alpha);
			scenario.addParticipant(bravo.getId(), bravo);

			final EnvironmentType env = new SimpleEnvironment(1, 1, 1);

			final Vector<Integer> newParams = new Vector<Integer>();
			newParams.add(new Integer(12));
			TestSensor.myParams = newParams;

			final DetectionList res = new DetectionList();
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("null detections returned", 0, res.size());

			// ok. move the targets a little closer
			statB.setLocation(SupportTesting.createLocation(800, 800));

			// just do a check that no returns are produced if it's not time f
			// for another scan
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("zero detections returned (because it's not time yet)", 0, res.size());

			time += 1000;
			ts.detects(env, res, alpha, scenario, time);

			// ok, now move forward and try again
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// and move closer still!!
			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// do another check for when we haven't moved forward enough
			time += 100;
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("old detections wiped", 0, res.size());

			// now move forward in time though
			time += 1000;

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// time should be elapsed by now!!!
			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, alpha, scenario, time);
			time += 9000;
			assertEquals("detections returned", 1, res.size());
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertEquals("detections returned", 1, res.size());
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.IDENTIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			// INCREASE THE RANGE AGAIN!
			statB.setLocation(SupportTesting.createLocation(900, 900));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertEquals("detections returned", 1, res.size());
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.IDENTIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			// AND AGAIN
			statB.setLocation(SupportTesting.createLocation(1200, 1200));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertEquals("detections returned", 1, res.size());
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.IDENTIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			// AND YET AGAIN (out of range!)
			statB.setLocation(SupportTesting.createLocation(5200, 5200));
			ts.detects(env, res, alpha, scenario, time);
			time += 5000;
			assertEquals("no detections returned (out of range)", 0, res.size());

			// Let's go into range again!
			statB.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertEquals("detections returned", 1, res.size());
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

		}

		public void testForceProtection() {
			final Status start = new Status(12, 0);
			final WorldLocation origin = SupportTesting.createLocation(0, 0);
			origin.setDepth(-500);
			start.setLocation(origin);
			start.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));

		}

		public void testMultipleContacts() {

			final TestSensor ts = getTestSensor(4000, 3100, 2500, 1900, 4700);
			long time = 1000;
			final Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			final Helo test2alpha = new Helo(12);
			test2alpha.setName("Alpha");
			final WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			test2alpha.setStatus(statA);

			final Surface test2bravo = new Surface(11);
			final Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			test2bravo.setName("Bravo");
			final WorldLocation originB = SupportTesting.createLocation(6000, 6000);
			statB.setLocation(originB);
			test2bravo.setStatus(statB);

			final Surface test2charlie = new Surface(21);
			final Status statC = new Status(12, 0);
			statC.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			test2charlie.setName("Charlie");
			final WorldLocation originC = SupportTesting.createLocation(4000, 4000);
			statC.setLocation(originC);
			test2charlie.setStatus(statC);

			test2alpha.addSensor(ts);
			final CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(test2alpha.getId(), test2alpha);
			scenario.addParticipant(test2bravo.getId(), test2bravo);
			scenario.addParticipant(test2charlie.getId(), test2charlie);

			final EnvironmentType env = new SimpleEnvironment(1, 1, 1);

			final Vector<Integer> newParams = new Vector<Integer>();
			newParams.add(new Integer(12));
			TestSensor.myParams = newParams;

			final DetectionList res = new DetectionList();
			ts.detects(env, res, test2alpha, scenario, time);
			assertEquals("zero detections returned (because it's not time yet)", 0, res.size());
			time += 1000;

			// ok. move the targets a little closer
			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());

			// and move closer still!!
			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(5800, 5800));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(4800, 4800));
			statC.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(400, 400));
			statC.setLocation(SupportTesting.createLocation(200, 200));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(400, 400));
			statC.setLocation(SupportTesting.createLocation(200, 200));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.IDENTIFIED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(2400, 2400));
			statC.setLocation(SupportTesting.createLocation(2200, 2200));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.IDENTIFIED,
					ts._pastContacts.get(test2charlie).getDetectionState());

		}

		public final void testPersistentData() {
			final TestSensor ts = getTestSensor(4000, 3100, 2500, 1900, 4700);
			long time = 1000;
			final Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			final Helo alpha = new Helo(12);
			alpha.setName("Alpha");
			final WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			alpha.setStatus(statA);
			final Surface bravo = new Surface(11);
			final Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			bravo.setName("Bravo");
			final WorldLocation originB = SupportTesting.createLocation(6000, 6000);
			statB.setLocation(originB);
			bravo.setStatus(statB);

			alpha.addSensor(ts);
			final CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(alpha.getId(), alpha);
			scenario.addParticipant(bravo.getId(), bravo);

			final EnvironmentType env = new SimpleEnvironment(1, 1, 1);

			final Vector<Integer> newParams = new Vector<Integer>();
			newParams.add(new Integer(12));
			TestSensor.myParams = newParams;

			final DetectionList res = new DetectionList();
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("zero detections returned (because it's not time yet)", 0, res.size());
			time += 1000;

			// ok. move the targets a little closer
			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// and move closer still!!
			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// change the detection parameters
			newParams.add(new Integer(32));
			TestSensor.myParams = newParams;
			ts._ri_m = 3300;

			// find out what the current RI is.
			LastTargetContact tc = ts._pastContacts.get(bravo);
			final WorldDistance lastRI = tc.getRI();
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			// check that the RI has changed
			tc = ts._pastContacts.get(bravo);
			final WorldDistance newRI = tc.getRI();

			final boolean isSame = newRI.getValueIn(WorldDistance.METRES) == lastRI.getValueIn(WorldDistance.METRES);
			assertTrue("RI has changed", !isSame);
			assertNotNull("detections returned", res);
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

		}

		public void testThresholds() {
			final int RP__M = 4000;
			final int ri_m = 3100;
			final int cr_m = 2500;
			final int ir_m = 1900;
			final int max_m = 4700;
			final TestSensor ts = getTestSensor(RP__M, ri_m, cr_m, ir_m, max_m);
			final Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			statA.setCourse(28);
			final Helo alpha = new Helo(12);
			alpha.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
			alpha.setName("Alpha");
			final WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			alpha.setStatus(statA);
			final Surface bravo = new Surface(11);
			bravo.setCategory(new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE));
			final Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			statB.setCourse(225);
			bravo.setName("Bravo");
			final WorldLocation originB = SupportTesting.createLocation(6000, 6000);
			statB.setLocation(originB);
			bravo.setStatus(statB);

			alpha.setMovementChars(HeloMovementCharacteristics.getSampleChars());
			bravo.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

			alpha.addSensor(ts);
			final CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(alpha.getId(), alpha);
			scenario.addParticipant(bravo.getId(), bravo);

			scenario.setStepTime(5000);
			scenario.setScenarioStepTime(1000);

			alpha.addParticipantDetectedListener(new ParticipantDetectedListener() {

				int lastState = DetectionEvent.UNDETECTED;

				public void checkDetectionThreshold(final int newState, final double range_m) {
					switch (newState) {
					case (DetectionEvent.DETECTED):
						assertEquals("detection range", range_m, ri_m, 30);
						break;
					case (DetectionEvent.CLASSIFIED):
						assertEquals("detection range", range_m, cr_m, 30);
						break;
					case (DetectionEvent.IDENTIFIED):
						assertEquals("detection range", range_m, ir_m, 30);
						break;
					case (DetectionEvent.UNDETECTED):
						assertEquals("detection range", range_m, max_m, 30);
						break;
					}

				}

				@Override
				public void newDetections(final DetectionList detections) {
					if (detections.size() > 0) {
						final DetectionEvent de = detections.getDetection(0);

						if (de.getDetectionState() != lastState) {
							lastState = de.getDetectionState();
							final WorldDistance wd = LookupSensor.calculateSlantRangeFor(
									alpha.getStatus().getLocation(), bravo.getStatus().getLocation());
							checkDetectionThreshold(de.getDetectionState(), wd.getValueIn(WorldDistance.METRES));
						}
					}
				}

				@Override
				public void restart(final ScenarioType scenario) {
				}

			});

			final ASSET.Scenario.Observers.Recording.DebriefReplayObserver dr = new DebriefReplayObserver(
					"test_reports", null, true, "record file", true);
			dr.setup(scenario);

			for (int i = 0; i < 4000; i++) {
				if (scenario.getTime() == 690000)
					System.out.println("here");

				scenario.step();
			}

			dr.tearDown(scenario);

		}

		public void testTutorialScenario() {
			final CoreScenario cs = new CoreScenario();
			try {
				final String fName = "../org.mwc.asset.core.feature/root_installs/AssetData/Samples/legacy/lookup_tutorial_scenario.xml";
				final File tFile = new File(fName);
				assertTrue("sceanrio not found", tFile.exists());
				final java.io.FileInputStream fis = new java.io.FileInputStream(fName);
				ASSET.Util.XML.ASSETReaderWriter.importThis(cs, fName, fis);
			} catch (final FileNotFoundException e) {
				e.printStackTrace(); // To change body of catch statement use File |
										// Settings | File Templates.
			}

			// so, do we have our scenario?
			assertNotNull("scenario loaded", cs);

			// hmm, have all the vessels loaded?
			assertEquals("all parts loaded", 4, cs.getListOfParticipants().length, 0);

			// run through for a few hours, recording to file
			final DebriefReplayObserver dro = new DebriefReplayObserver("./test_reports", "test_tutorial.rep", true,
					"test observer", true);
			dro.setup(cs);

			final ASSET.Scenario.Observers.StopOnElapsedObserver to = new StopOnElapsedObserver(0, 3, 0, 0,
					"Test time observer", true);
			to.setup(cs);

			while (!to.hasStopped()) {
				cs.step();
			}

			to.tearDown(cs);
			dro.tearDown(cs);

		}
	}

	// ////////////////////////////////////////////////
	// class to hold a series of values for a single named type
	// ////////////////////////////////////////////////
	public static class NamedList {
		final protected String _myType;

		final protected Vector<Double> _myValues;

		public NamedList(final String type, final double[] vals) {
			_myType = type;
			_myValues = new Vector<Double>(0, 1);
			for (int i = 0; i < vals.length; i++) {
				final Double thisD = new Double(vals[i]);
				_myValues.add(thisD);
			}
		}

		public NamedList(final String type, final Vector<Double> values) {
			_myType = type;
			_myValues = values;
		}

		public String getName() {
			return _myType;
		}

		public Collection<Double> getValues() {
			return _myValues;
		}
	}

	// //////////////////////////////////////////////////////////
	// embedded class which provides a lookup table of string values, returning
	// a double value for the matching string
	// //////////////////////////////////////////////////////////
	public static class StringLookup extends BaseLookup {
		/**
		 * store the list of double values indexed by string
		 */
		private final HashMap<String, Double> _myTable;

		/**
		 * constructor
		 *
		 * @param strs list of string indices
		 * @param vals list of double values
		 */
		private StringLookup(final String[] strs, final double[] vals) {
			if (strs.length != vals.length) {
				System.err.println("STRING LOOKUP PARAMETERS OF UNEQUAL LENGTH!!!");
			}

			_myTable = new HashMap<String, Double>();
			for (int i = 0; i < strs.length; i++) {
				final String str = strs[i];
				final double val = vals[i];
				_myTable.put(str, new Double(val));
			}
		}

		/**
		 * @param strs          the string values to compare against
		 * @param vals          the double values to return for them
		 * @param unknownResult what to return if we don't match the string
		 */
		public StringLookup(final String[] strs, final double[] vals, final Double unknownResult) {
			this(strs, vals);

			super.setUnknownResult(unknownResult);
		}

		/**
		 * determine if we have an index for the supplied value
		 *
		 * @param index
		 * @return
		 */
		public boolean containsValueFor(final String index) {
			return _myTable.keySet().contains(index);
		}

		/**
		 * find the double value at the supplied index
		 *
		 * @param index the string to index against
		 * @return the matching double value (or null)
		 */
		public Double find(final String index) {
			return find(index, this._unknownResult);
		}

		/**
		 * find the double value at the supplied index
		 *
		 * @param index        the string to index against
		 * @param defaultValue - the value to return if a matching one isn't found.
		 *                     Throws runtime error if matching type isn't found and a
		 *                     default value isn's supplied
		 * @return the matching double value (or null)
		 */
		public Double find(final String index, final Double defaultValue) {
			Double res = null;
			final Iterator<String> iterator = _myTable.keySet().iterator();
			while (iterator.hasNext() && res == null) {
				final String s = iterator.next();
				if (s.equals(index)) {
					res = _myTable.get(index);
					break;
				}

			}
			// did we find anything?
			res = checkResult(res, defaultValue);

			// did it work?
			if (res == null) {
				throw new RuntimeException("Lookup sensor: data value not supplied for:" + index);
			}

			return res;
		}

		/**
		 * provide the indexes we currently hold
		 *
		 * @return
		 */
		public Collection<String> getIndices() {
			return _myTable.keySet();
		}

	}

	// //////////////////////////////////////////////////////////
	// test instance to prove class
	// //////////////////////////////////////////////////////////
	private static class TestSensor extends LookupSensor {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public static Vector<Integer> myParams = new Vector<Integer>();

		double _rp_m;

		double _ri_m;

		double _cr_m;

		double _ir_m;

		double _max_m;

		public TestSensor(final double VDR, final long TBDO, final double MRF, final double CRF, final Duration CTP,
				final double IRF, final Duration ITP, final double rp_m, final double ri_m, final double cr_m,
				final double ir_m, final double max_m) {
			super(12, VDR, TBDO, MRF, CRF, CTP, IRF, ITP, "Test sensor");
			_rp_m = rp_m;
			_ri_m = ri_m;
			_cr_m = cr_m;
			_ir_m = ir_m;
			_max_m = max_m;
		}

		@Override
		protected WorldDistance calculateCR(final WorldDistance RP, final double VDRa) {
			return new WorldDistance(_cr_m, WorldDistance.METRES);
		}

		@Override
		protected WorldDistance calculateIR(final WorldDistance RP, final double VDRa) {
			return new WorldDistance(_ir_m, WorldDistance.METRES);
		}

		@Override
		protected WorldDistance calculateMaxRange(final WorldDistance RP, final double VDRa) {
			return new WorldDistance(_max_m, WorldDistance.METRES);
		}

		@Override
		protected WorldDistance calculateRI(final WorldDistance RP, final double VDRa) {
			return new WorldDistance(_ri_m, WorldDistance.METRES);
		}

		@Override
		protected WorldDistance calculateRP(final NetworkParticipant ownship, final NetworkParticipant target,
				final ScenarioType scenario, final EnvironmentType environment, final long time,
				final LastTargetContact params) {
			return new WorldDistance(_rp_m, WorldDistance.METRES);
		}

		// allow an 'overview' test, just to check if it is worth all of the above
		// processing
		@Override
		protected boolean canDetectThisType(final NetworkParticipant ownship, final ParticipantType other,
				final EnvironmentType env) {
			return true; // To change body of implemented methods use File | Settings
							// | File Templates.
		}

		@Override
		public boolean canIdentifyTarget() {
			return true; // To change body of implemented methods use File | Settings
							// | File Templates.
		}

		/**
		 * get the editor for this item
		 *
		 * @return the BeanInfo data for this editable object
		 */
		@Override
		public EditorType getInfo() {
			return null; // To change body of implemented methods use File | Settings
							// | File Templates.
		}

		@Override
		public String getVersion() {
			return "not applicable";
		}

		/**
		 * whether there is any edit information for this item this is a convenience
		 * function to save creating the EditorType data first
		 *
		 * @return yes/no
		 */
		@Override
		public boolean hasEditor() {
			return false; // To change body of implemented methods use File | Settings
							// | File Templates.
		}

		@Override
		protected LastTargetContact parametersFor(final NetworkParticipant ownship, final NetworkParticipant target,
				final ScenarioType scenario, final EnvironmentType environment, final long time) {
			final LastTargetContact res = new LastTargetContact();
			if (myParams != null)
				res._myList.addAll(myParams);
			return res;
		}

	}

	// //////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// //////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the minimum height at which this sensor is operable (m)
	 */
	private final static double MIN_HEIGHT = -18;

	protected static WorldDistance calculateSlantRangeFor(final WorldLocation ownship, final WorldLocation target) {
		WorldDistance res = null;
		final WorldVector flat = ownship.subtract(target);

		// ok. what's the range
		final double flatM = MWC.Algorithms.Conversions.Degs2m(flat.getRange());
		final double heightM = Math.abs(target.getDepth() - ownship.getDepth());

		// and apply the slant
		res = new WorldDistance(Math.sqrt(flatM * flatM + heightM * heightM), WorldDistance.METRES);

		// ok, done.
		return res;
	}

	/**
	 * see if we are ready to make the transition to the next state
	 *
	 * @param actualRange   the current range
	 * @param nextThreshold the range at which detection can pass to the next state
	 * @param newParameters the current situation
	 * @param time          the current time
	 * @param waitingPeriod how long we have to wait before moving to the next state
	 * @param newState      the next State we will move to
	 * @see DetectionEvent.UNDETECTED
	 */
	private static void checkAndMakeTransition(final WorldDistance actualRange, final WorldDistance nextThreshold,
			final LastTargetContact newParameters, final long time, final Duration waitingPeriod, final int newState) {
		// ok. see if we are within the classification range factor
		// WorldDistance classRng = new
		// WorldDistance(oldParameters.getRP().getValueIn(WorldDistance.METRES) *
		// rangeFactor,
		// WorldDistance.METRES);

		if (!actualRange.greaterThan(nextThreshold)) {
			// hey, less than or equal to the identification range -
			// -- check if we've passed the identification period
			final long lastTransitionTime = newParameters.getTimeOfThisTransition();
			final long identPeriod = (long) waitingPeriod.getValueIn(Duration.MILLISECONDS);
			final long elapsedTime = time - lastTransitionTime;

			if (elapsedTime > identPeriod) {
				// yup - make the transition!
				newParameters.setTransitionTime(lastTransitionTime + identPeriod);
				newParameters.setDetectionState(newState);
			} // whether sufficient time elapsed
		} // whether within range
	}

	/**
	 * our record of past detections
	 */
	protected Hashtable<ParticipantType, LastTargetContact> _pastContacts;

	/**
	 * variability in detection range
	 */
	protected double VDR = 0;

	/**
	 * max range factor
	 */
	protected double MRF = 0;

	/**
	 * classification range factor
	 */
	protected double CRF = 0;

	/**
	 * classification time period (secs)
	 */
	protected Duration CTP = null;

	/**
	 * identification range factor
	 */
	protected double IRF = 0;

	/**
	 * identification time period (secs)
	 */
	protected Duration ITP = null;

	/**
	 * constructor for a lookup sensor
	 *
	 * @param VDR  variability in detection range
	 * @param TBDO time between detection opportunities (millis)
	 * @param MRF  maximum range factor
	 * @param CRF  classification range factor
	 * @param CTP  classification time period
	 * @param IRF  identification range factor
	 * @param ITP  identification time period
	 */
	public LookupSensor(final int id, final double VDR, final long TBDO, final double MRF, final double CRF,
			final Duration CTP, final double IRF, final Duration ITP, final String defaultName) {
		super(id, TBDO, defaultName);
		this.VDR = VDR;
		this.MRF = MRF;
		this.CRF = CRF;
		this.CTP = CTP;
		this.IRF = IRF;
		this.ITP = ITP;

		_pastContacts = new Hashtable<ParticipantType, LastTargetContact>();
	}

	@Override
	public void addSensorCalculationListener(final java.beans.PropertyChangeListener listener) {
		if (_pSupport == null)
			_pSupport = new java.beans.PropertyChangeSupport(this);

		_pSupport.addPropertyChangeListener(SENSOR_COMPONENT_EVENT, listener);
		_pSupport.addPropertyChangeListener(DETECTION_CYCLE_COMPLETE, listener);
	}

	protected WorldDistance calculateCR(final WorldDistance RP, final double CRFa) {
		final WorldDistance classRng = new WorldDistance(RP.getValueIn(WorldDistance.METRES) * CRFa,
				WorldDistance.METRES);
		return classRng;
	}

	protected WorldDistance calculateIR(final WorldDistance RP, final double IRFa) {
		final WorldDistance identRng = new WorldDistance(RP.getValueIn(WorldDistance.METRES) * IRFa,
				WorldDistance.METRES);
		return identRng;
	}

	protected WorldDistance calculateMaxRange(final WorldDistance RP, final double MRFa) {
		final WorldDistance MaxRange = new WorldDistance(RP.getValueIn(WorldDistance.METRES) * MRFa,
				WorldDistance.METRES);
		return MaxRange;
	}

	protected WorldDistance calculateRI(final WorldDistance RP, final double VDRa) {
		final double newRandom = RandomGenerator.generateNormalValue(VDRa);
		final double res = RP.getValueIn(WorldDistance.METRES) * (1 + newRandom);
		final WorldDistance RI = new WorldDistance(res, WorldDistance.METRES);
		return RI;
	}

	/**
	 * calculate the predicted range for this contact
	 *
	 * @param ownship
	 * @param target
	 * @param scenario
	 * @param environment
	 * @return
	 */
	abstract protected WorldDistance calculateRP(NetworkParticipant ownship, NetworkParticipant target,
			ScenarioType scenario, EnvironmentType environment, long time, LastTargetContact params);

	/**
	 * allow an 'overview' test, just to check if it is worth all of the above
	 * processing
	 *
	 * @param ownship
	 * @param target
	 * @param env
	 * @return
	 */
	@Override
	protected boolean canDetectThisType(final NetworkParticipant ownship, final ParticipantType target,
			final EnvironmentType env) {
		final double height = -target.getStatus().getLocation().getDepth();
		return height > MIN_HEIGHT;
	}

	// what is the detection strength for this target?
	@Override
	protected DetectionEvent detectThis(final EnvironmentType environment, final ParticipantType ownship,
			final ParticipantType target, final long time, final ScenarioType scenario) {
		DetectionEvent res = null;

		// store the range for later on, if we want to.
		WorldDistance actualRange = null;

		final WorldLocation hostLoc = getHostLocationFor(ownship);

		// //////////////////////////////////////////////////////////
		// LOOK FOR ANY EXISTING CONTACT WITH THIS PARTICIPANT
		// //////////////////////////////////////////////////////////

		// have we already detected this?
		LastTargetContact oldParameters = _pastContacts.get(target);

		// //////////////////////////////////////////////////////////
		// DETERMINE THE CURRENT SET OF LOOKUP PARAMETERS
		// //////////////////////////////////////////////////////////

		// sort out the new set of parameters
		LastTargetContact newParameters = parametersFor(ownship, target, scenario, environment, time);

		// //////////////////////////////////////////////////////////
		// ESTABLISH IF OUR CONTACT PARAMETERS HAVE CHANGED
		// //////////////////////////////////////////////////////////

		// do we have any old parameters? (have we detected this target before?)
		if (oldParameters != null) {
			// fill in the known fields from the last dataset
			newParameters.setDetectionState(oldParameters.getDetectionState());
			newParameters.setTransitionTime(oldParameters.getTimeOfThisTransition());

			// do they match
			if (!oldParameters.matchesThis(newParameters)) {
				// nope, they've changed. Clear the value
				oldParameters = null;
			} else {
				// yup, they sure to match, make a copy of them
				newParameters.setRanges(oldParameters.getRI(), oldParameters.getRP());
			}
		}

		// //////////////////////////////////////////////////////////
		// CALCULATE NEW RANGES IF NECESSARY
		// //////////////////////////////////////////////////////////

		// ok, do we need to recalculate our ranges?
		if (oldParameters == null) {
			// calculate the RP/RI first
			final WorldDistance RP = calculateRP(ownship, target, scenario, environment, time, newParameters);

			// and now calculate the RI
			final WorldDistance RI = calculateRI(RP, VDR);

			// and remember them
			newParameters.setRanges(RI, RP);
		}

		// //////////////////////////////////////////////////////////
		// CALCULATE THE ACTUAL RANGE
		// //////////////////////////////////////////////////////////

		// ok, now calculate the slant range
		actualRange = calculateSlantRangeFor(hostLoc, target.getStatus().getLocation());

		final WorldVector offset = target.getStatus().getLocation().subtract(hostLoc);
		final double bearing = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());
		final double relBearing = bearing - ownship.getStatus().getCourse();

		// store the last detection state
		final int currentState = newParameters.getDetectionState();

		// how does this compare with the IR
		final WorldDistance RI = newParameters.getRI();
		if (!actualRange.greaterThan(RI)) {
			// yes - in range - handle the classification.

			// handle our current detection state
			switch (currentState) {
			case DetectionEvent.IDENTIFIED: {
				// hey - it just couldn't get any better.
				break;
			}
			case DetectionEvent.CLASSIFIED: {
				final WorldDistance nextThreshold = calculateIR(newParameters.getRP(), IRF);
				checkAndMakeTransition(actualRange, nextThreshold, newParameters, time, ITP, DetectionEvent.IDENTIFIED);
				break;
			}
			case DetectionEvent.DETECTED: {
				final WorldDistance nextThreshold = calculateCR(newParameters.getRP(), CRF);
				checkAndMakeTransition(actualRange, nextThreshold, newParameters, time, CTP, DetectionEvent.CLASSIFIED);
				break;
			}
			case DetectionEvent.UNDETECTED: {
				newParameters.setDetectionState(DetectionEvent.DETECTED);
				newParameters.setTransitionTime(time);
				break;
			}
			}

			// only produce bearing data if this sensor is capable of it.
			Float bearingVal = null;
			Float relBearingVal = null;
			if (canProduceBearing()) {
				bearingVal = new Float(bearing);
				relBearingVal = new Float(relBearing);
			}

			// and only produce range if we're capable of it
			WorldDistance theRange = null;
			if (canProduceRange()) {
				theRange = actualRange;
			} else {
				theRange = null;
			}

			// and create the detection
			res = new DetectionEvent(time, ownship.getId(), hostLoc, this, theRange, null, bearingVal, relBearingVal,
					null, target.getCategory(), target.getStatus().getSpeed(),
					new Float(target.getStatus().getCourse()), target, newParameters.getDetectionState());

		} else {

			// hmm, are we in contact at least?
			if (newParameters.getDetectionState() == DetectionEvent.UNDETECTED) {
				// hey, forget about it. We're even in contact. No chance
				newParameters = null;
			} else {

				// so. we're outside the instantaneous range, but if we're already in
				// contact it can stretch
				// out to the max range - let it be.

				// no, we're not in range. See if we have passed the maximum range
				final WorldDistance MaxRange = calculateMaxRange(newParameters.getRP(), MRF);

				if (actualRange.greaterThan(MaxRange)) {
					// NO CONTACT. Dead. Forget about the new parameters
					newParameters = null;

				} else {
					// hey - we're on the hairy edge of being in contact. Things aren't
					// going to get any
					// better here. Create a detection with the new relative data, but
					// maintaining the same
					// detection state
					res = new DetectionEvent(time, ownship.getId(), ownship.getStatus().getLocation(), this,
							actualRange, null, new Float(bearing), new Float(relBearing), null, target.getCategory(),
							target.getStatus().getSpeed(), new Float(target.getStatus().getCourse()), target,
							currentState);

				}
			}
		}

		// ok. do we have a contact?
		if (newParameters != null) {
			// ok, done - remember how we got on
			_pastContacts.put(target, newParameters);

			// ok, just see if there are any pSupport listners
			if (_pSupport != null) {
				if (_pSupport.hasListeners(SENSOR_COMPONENT_EVENT)) {
					// create the event
					final LookupSensorComponentsEvent sev = new LookupSensorComponentsEvent(time,
							newParameters.getDetectionState(), newParameters.getRI(), newParameters.getRP(),
							actualRange, target.getName());

					// and fire it!
					_pSupport.firePropertyChange(SENSOR_COMPONENT_EVENT, null, sev);
				}
			}

		} else {
			_pastContacts.remove(target);
		}

		return res;
	}

	// //////////////////////////////////////////////////////////
	// embedded utility class which contains a set of lookup parameters for the
	// lookup
	// sensor in question
	// //////////////////////////////////////////////////////////

	@Override
	public WorldDistance getEstimatedRange() {
		return null;
	}

	@Override
	public int getMedium() {
		return 0;
	}

	/**
	 * decide if this sensor is operable (if it's out of the water
	 *
	 * @param target the current target location
	 * @return yes/no
	 */
	protected boolean isOperable(final WorldLocation target) {
		final double height = -target.getDepth();
		return height > MIN_HEIGHT;
	}

	/**
	 * determine the lookup parameters applicable to this sensor
	 *
	 * @param ownship     us
	 * @param target      them
	 * @param scenario    the scenario
	 * @param environment the environment
	 * @param time        current time
	 * @return the set of lookup parameters applicable to this sensor
	 */
	protected abstract LastTargetContact parametersFor(NetworkParticipant ownship, NetworkParticipant target,
			ScenarioType scenario, EnvironmentType environment, long time);

	@Override
	public void removeSensorCalculationListener(final java.beans.PropertyChangeListener listener) {
		_pSupport.removePropertyChangeListener(SENSOR_COMPONENT_EVENT, listener);
		_pSupport.removePropertyChangeListener(DETECTION_CYCLE_COMPLETE, listener);
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