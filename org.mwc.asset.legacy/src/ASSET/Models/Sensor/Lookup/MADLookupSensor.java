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

package ASSET.Models.Sensor.Lookup;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.SensorType;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Decision.Tactical.PatternSearch_Ladder;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.CoreEnvironment;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Vessels.Helo;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 14-Jan-2004 Time: 21:37:30 To
 * change this template use Options | File Templates.
 */
public class MADLookupSensor extends LookupSensor {

	////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// structure to contain optic lookup set
	//////////////////////////////////////////////////
	public static class MADEnvironment {
		protected StringLookup _targetSize;
		protected String _name;

		public MADEnvironment(final String name, final StringLookup visibility) {
			_name = name;
			_targetSize = visibility;
		}

		public String getName() {
			return _name;
		}

		public StringLookup getTargetVisibility() {
			return _targetSize;
		}

		public void setName(final String name) {
			_name = name;
		}

	}

	////////////////////////////////////////////////////
	// the editor object
	////////////////////////////////////////////////////
	static public class MADLookupInfo extends BaseSensorInfo {
		/**
		 * @param data the Layers themselves
		 */
		public MADLookupInfo(final MADLookupSensor data) {
			super(data);
		}
	}

	////////////////////////////////////////////////////////////
	// test the optic sensor
	////////////////////////////////////////////////////////////
	static public final class MADLookupTest extends ASSET.Util.SupportTesting.EditableTesting {
		static public class MyEnvironment extends CoreEnvironment {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;
			protected int _lightLevel = 1;
			protected int _seaState = 4;
			protected int _atten = EnvironmentType.VERY_CLEAR;

			/**
			 * get the atmospheric attenuation
			 *
			 * @param time     current time
			 * @param location place to get data for
			 * @return one of the atmospheric attenuation factors
			 */
			@Override
			public int getAtmosphericAttentuationFor(final long time, final WorldLocation location) {
				return _atten;
			}

			/**
			 * get the light level at this location
			 *
			 * @param time     the time we're talking about
			 * @param location the location we're talking about
			 * @return the current light level
			 */
			@Override
			public int getLightLevelFor(final long time, final WorldLocation location) {
				return _lightLevel;
			}

			/**
			 * get the sea state
			 *
			 * @param time     current time
			 * @param location place to get data for
			 * @return sea state, from 0 to 10
			 */
			@Override
			public int getSeaStateFor(final long time, final WorldLocation location) {
				return _seaState;
			}

			/**
			 * over-ride the sea state
			 *
			 * @param seaState the new sea-state to use
			 */
			public void setSeaState(final int seaState) {
				this._seaState = seaState;
			}
		}

		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public static MADLookupSensor getTestMADSensor() {
			return new MADLookupSensor(12, 0.05, 10, 1.05, 0.8, new Duration(20, Duration.SECONDS), 0, null);
		}

		protected boolean detectedOther = false;

		protected boolean targetLost = false;

		protected int maxClassification = 0;

		public MADLookupTest(final String val) {
			super(val);
		}

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return getTestMADSensor();
		}

		public void testDefaultValues() {
			// set the sensor
			final MADLookupSensor sensor = getTestMADSensor();

			// and set the environment
			final MADEnvironment env = sensor.getDefaultLookups();

			// now check that a real value gets return
			Double res = null;

			// and for the vis table
			res = env._targetSize.find(Category.Type.CARRIER);
			assertNotNull("didn't find result which we should have done");
			assertEquals("didn't find correct lookup value", 3000d, res.doubleValue(), 0d);

			// and for the vis table
			res = env._targetSize.find(Category.Type.DESTROYER);
			assertNotNull("didn't find result which we should have done");
			assertEquals("didn't find correct lookup value", 600d, res.doubleValue(), 0d);

			// now try to remove the default value, and check that exception gets thrown

			env._targetSize.setUnknownResult(null);

			boolean visThrown = false;

			try {
				// and for the vis table
				res = env._targetSize.find(Category.Type.CARRIER);
				assertNotNull("didn't find result which we should have done");
				assertEquals("didn't find correct lookup value", 3000d, res.doubleValue(), 0d);
			} catch (final RuntimeException re) {
				fail("exception shouldn't have been thrown for known types");
			}

			try {
				res = env._targetSize.find(Category.Type.DESTROYER);
				fail("should have thrown exception");
			} catch (final RuntimeException re) {
				visThrown = true;
			}

			assertTrue("should have fired missing item for missing type in visibility test", visThrown);

		}

		public final void testFirst() {
			final MADLookupSensor ts = getTestMADSensor();
			final Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			final Helo alpha = new Helo(12);
			alpha.setName("Alpha");
			alpha.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
			final WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			statA.getLocation().setDepth(-90);
			alpha.setStatus(statA);
			final Surface bravo = new Surface(11);
			final Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			bravo.setName("Bravo");
			bravo.setCategory(
					new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
			final WorldLocation originB = SupportTesting.createLocation(6000, 6000);
			statB.setLocation(originB);
			statB.getLocation().setDepth(-20);
			bravo.setStatus(statB);

			alpha.addSensor(ts);
			final CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(alpha.getId(), alpha);
			scenario.addParticipant(bravo.getId(), bravo);

			scenario.setScenarioStepTime(1000);

			// create our dummy environment object
			final MyEnvironment env = new MyEnvironment();

			// ok, start off with us miles apart - and check we can't see each other.
			long time = 1000;
			final DetectionList res = new DetectionList();
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("null detections returned", 0, res.size());

			// ok. move the targets a little closer (but still out of range)
			statB.setLocation(SupportTesting.createLocation(800, 700));
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("null detections returned", 0, res.size());

			// and closer still (to gain contact)
			statB.setLocation(SupportTesting.createLocation(200, 200));
			time += 1000;
			ts.detects(env, res, alpha, scenario, time);
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// check that the parameters have stayed the same
			statB.setLocation(SupportTesting.createLocation(200, 200));
			time += 1000;
			ts.detects(env, res, alpha, scenario, time);
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// stay close, move forward enough elapsed time so that we can move to
			// classified
			time += 21000;
			ts.detects(env, res, alpha, scenario, time);
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			// change one of the parameters
			alpha.getStatus().getLocation().setDepth(-900);

			// check that the parameters have changed
			time += 1000;
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("null detections returned", 0, res.size());
		}

		public void testRead() {
			String fileName = "lookup_test_scenario.xml";
			fileName = "../org.mwc.asset.legacy/src/ASSET/Models/Sensor/Lookup/" + fileName;

			final CoreScenario scen = new CoreScenario();
			try {
				final FileInputStream is = new FileInputStream(fileName);
				final InputStream bi = new BufferedInputStream(is);
				ASSET.Util.XML.ASSETReaderWriter.importThis(scen, fileName, bi);
			} catch (final FileNotFoundException e) {
				e.printStackTrace(); // To change body of catch statement use Options | File Templates.
			}

			// ok, try to check that the data got loaded.
			final Integer[] participants = scen.getListOfParticipants();

			final NetworkParticipant partA = scen.getThisParticipant(participants[0].intValue());
			final NetworkParticipant partB = scen.getThisParticipant(participants[1].intValue());
			NetworkParticipant part;
			if (partA instanceof Helo)
				part = partA;
			else
				part = partB;

			assertTrue("we've loaded and found the helo", part instanceof Helo);
			final Helo helo = (Helo) part;
			final SensorList theSensors = helo.getSensorFit();
			assertEquals("has sensors loaded", 3, theSensors.getNumSensors());
			final SensorType firstSensor = theSensors.getSensor(987);
			assertNotNull("found first sensor", firstSensor);
			assertTrue(firstSensor instanceof MADLookupSensor);
			final MADLookupSensor opticS = (MADLookupSensor) firstSensor;

			// check the values
			assertEquals("VDR correct", 0.05, opticS.VDR, 0.0);
			assertEquals("name correct", "MAD sensor", opticS.getName());
			assertEquals("MRF correct", 1.0, opticS.MRF, 0.0);
			assertEquals("CRF correct", 0.6, opticS.CRF, 0.0);
			assertEquals("IRF correct", 0, opticS.IRF, 0.0);

			assertEquals("tbdo correct", 1000, opticS.getTimeBetweenDetectionOpportunities(), 0);
			assertEquals("ctp correct", 1000, opticS.CTP.getMillis(), 0);
			assertEquals("itp correct", 0, opticS.ITP.getMillis(), 0);

			// also stick in our tests for the ladder search
			final Waterfall wat = (Waterfall) helo.getDecisionModel();
			final Sequence seq = (Sequence) wat.getModels().elementAt(1);
			assertEquals("all loaded", 3, seq.getModels().size());
			assertTrue("ladder loaded", seq.getModels().elementAt(2) instanceof PatternSearch_Ladder);

			helo.addParticipantDetectedListener(new ParticipantDetectedListener() {
				@Override
				public void newDetections(final DetectionList detections) {
					if (detections.size() > 0) {
						detectedOther = true;

						final DetectionEvent det = detections.getDetection(0);

						maxClassification = Math.max(maxClassification, det.getDetectionState());
					} else {
						if (detectedOther)
							targetLost = true;
					}
				}

				@Override
				public void restart(final ScenarioType scenario) {
				}
			});

			// DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp",
			// "optic_lookup.rep", true,
			// "test observer", true);
			// dro.setup(scen);

			// hey, let's run through it a little
			for (int i = 0; i < 1800; i++) {
				scen.step();
			}

			// dro.tearDown(scen);

			assertTrue("yes, we made a detection", detectedOther);
			assertTrue("yes, we also lost contact", targetLost);
			assertEquals("managed to identify", 2, maxClassification, 0);

		}
	}

	////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final static int VIS_INDEX = 0;

	MADEnvironment _defaultLookups = null;

	/**
	 * constructor
	 *
	 * @param VDR  variability in detection range
	 * @param TBDO time between detection opportunities (millis)
	 * @param MRF  maximum range factor
	 * @param CRF  classification range factor
	 * @param CTP  classification time period
	 * @param IRF  identification range factor
	 * @param ITP  identification time period
	 */
	public MADLookupSensor(final int id, final double VDR, final long TBDO, final double MRF, final double CRF,
			final Duration CTP, final double IRF, final Duration ITP) {
		super(id, VDR, TBDO, MRF, CRF, CTP, IRF, ITP, "MAD Sensor");
	}

	/**
	 * calculate the predicted range for this contact. Uses the visual horizon from
	 * Bowditch, The Practical navigator (1995) table 12
	 *
	 * @param ownship
	 * @param target
	 * @param scenario
	 * @param environment
	 * @return
	 */
	@Override
	protected WorldDistance calculateRP(final NetworkParticipant ownship, final NetworkParticipant target,
			final ScenarioType scenario, final EnvironmentType environment, final long time,
			final LastTargetContact params) {
		WorldDistance res = null;

		// retrieve our parameter
		final double targetVis = ((Double) params.get(VIS_INDEX)).doubleValue();

		// and use the min value.
		final double RP = targetVis;

		// done.
		res = new WorldDistance(RP, WorldDistance.METRES);

		return res;
	}

	/**
	 * see if we can detect this class of target
	 *
	 * @param ownship
	 * @param target
	 * @param env
	 * @return
	 */
	@Override
	protected boolean canDetectThisType(final NetworkParticipant ownship, final ParticipantType target,
			final EnvironmentType env) {
		// first sort out the environment
		MADEnvironment lookups = env.getMADEnvironment();

		if (lookups == null) {
			// bugger. there aren't any. have we created our defaults?
			if (_defaultLookups == null) {
				// nope, better create it
				_defaultLookups = getDefaultLookups();
			}

			lookups = _defaultLookups;
		}

		final boolean res = lookups._targetSize.containsValueFor(target.getCategory().getType());

		return res;
	}

	/**
	 * whether a MAD sensor can be used to ID a target
	 *
	 * @return
	 */
	@Override
	public boolean canIdentifyTarget() {
		return false;
	}

	/**
	 * convenience method to indicate if this sensor produces a bearing from the
	 * sensor to the target. Very few sensors do not produce bearing.
	 *
	 * @return yes/no
	 */
	@Override
	protected boolean canProduceBearing() {
		return false;
	}

	/**
	 * whether this sensor produces range in its output
	 *
	 * @return yes/no
	 */
	@Override
	public boolean canProduceRange() {
		return false;
	}

	protected MADEnvironment getDefaultLookups() {
		// and the target visibility
		final StringLookup visibility = new StringLookup(new String[] { Category.Type.CARRIER, Category.Type.FRIGATE,
				Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL }, new double[] { 3000, 2000, 500, 400 },
				new Double(1000));

		visibility.setUnknownResult(new Double(600));
		final MADEnvironment defaultEnv = new MADEnvironment("sample", visibility);

		return defaultEnv;
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new MADLookupInfo(this);

		return _myEditor;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: MADLookupSensor.java,v $
	 * Revision 1.2  2006/09/21 12:20:44  Ian.Mayo
	 * Reflect introduction of default names
	 *
	 * Revision 1.1  2006/08/08 14:21:57  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:26:05  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.5  2004/11/05 15:29:19  Ian.Mayo
	 * Correct tests to reflect new structure of test-read file
	 *
	 * Revision 1.4  2004/11/04 09:30:22  Ian.Mayo
	 * Handle sensors which can't provide range/bearing
	 * <p/>
	 * Revision 1.3  2004/11/03 15:57:34  Ian.Mayo
	 * More MAD testing, & only produce bearing from sensor capable of it.
	 * <p/>
	 * Revision 1.2  2004/11/03 15:42:10  Ian.Mayo
	 * More support for MAD sensors, better use of canDetectThis method
	 * <p/>
	 * Revision 1.1  2004/11/02 20:51:46  ian
	 * Implement MAD sensors
	 * <p/>
	 * Revision 1.21  2004/10/27 15:13:51  Ian.Mayo
	 * Reflect changed structure of MADEnvironment
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
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

	////////////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////////////
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
	@Override
	protected LastTargetContact parametersFor(final NetworkParticipant ownship, final NetworkParticipant target,
			final ScenarioType scenario, final EnvironmentType environment, final long time) {
		final LastTargetContact res = new LastTargetContact();

		MADEnvironment lookups = environment.getMADEnvironment();

		if (lookups == null) {
			// bugger. there aren't any. have we created our defaults?
			if (_defaultLookups == null) {
				// nope, better create it
				_defaultLookups = getDefaultLookups();
			}

			lookups = _defaultLookups;
		}

		/**
		 * now the target visibility
		 *
		 * @see ASSET.Participants.Category.Type.CARRIER
		 */
		final String targetType = target.getCategory().getType();
		final Double targetVis = lookups._targetSize.find(targetType);

		// and store the parameters
		res.insertElementAt(targetVis, VIS_INDEX);

		// done
		return res;
	}

}
