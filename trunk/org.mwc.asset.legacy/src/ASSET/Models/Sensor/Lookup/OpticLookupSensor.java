package ASSET.Models.Sensor.Lookup;

import java.io.*;
import java.util.Vector;

import ASSET.*;
import ASSET.Models.SensorType;
import ASSET.Models.Decision.*;
import ASSET.Models.Decision.Tactical.LadderSearch;
import ASSET.Models.Detection.*;
import ASSET.Models.Environment.*;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Vessels.*;
import ASSET.Participants.*;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.*;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 14-Jan-2004 Time: 21:37:30 To
 * change this template use Options | File Templates.
 */
public class OpticLookupSensor extends LookupSensor
{

	// //////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	OpticEnvironment _defaultLookups = null;

	// //////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	/**
	 * constructor
	 * 
	 * @param VDR
	 *          variability in detection range
	 * @param TBDO
	 *          time between detection opportunities (millis)
	 * @param MRF
	 *          maximum range factor
	 * @param CRF
	 *          classification range factor
	 * @param CTP
	 *          classification time period
	 * @param IRF
	 *          identification range factor
	 * @param ITP
	 *          identification time period
	 */
	public OpticLookupSensor(int id, String myName, double VDR, long TBDO, double MRF,
			double CRF, Duration CTP, double IRF, Duration ITP)
	{
		super(id, myName, VDR, TBDO, MRF, CRF, CTP, IRF, ITP, "Optic Lookup");
	}

	protected OpticEnvironment getDefaultLookups()
	{
		// produce the visual attenuation lookup
		IntegerLookup attenuation = new IntegerLookup(new int[] { EnvironmentType.VERY_CLEAR,
				EnvironmentType.CLEAR, EnvironmentType.LIGHT_HAZE, EnvironmentType.HAZE,
				EnvironmentType.MIST, EnvironmentType.FOG }, new double[] { 8e-5, 2e-4, 5e-4,
				1e-3, 2e-3, 4e-3 });

		// and the target visibility
		StringLookup visibility = new StringLookup(new String[] { Category.Type.CARRIER,
				Category.Type.FRIGATE, Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL },
				new double[] { 0.2, 0.2, 0.12, 0.16 }, new Double(1));

		visibility.setUnknownResult(new Double(0.001));

		Vector<NamedList> states = new Vector<NamedList>(0, 1);
		states.add(new NamedList(Category.Type.CARRIER, new double[] { 1.00, 1.00, 1.00,
				1.00, 1.00, 0.95, 0.90 }));
		states.add(new NamedList(Category.Type.CARRIER, new double[] { 1.00, 1.00, 1.00,
				1.00, 1.00, 1.00, 0.95 }));
		states.add(new NamedList(Category.Type.SUBMARINE, new double[] { 1.00, 1.00, 0.80,
				0.75, 0.70, 0.50, 0.30 }));
		states.add(new NamedList(Category.Type.FISHING_VESSEL, new double[] { 1.00, 1.00,
				0.96, 0.80, 0.75, 0.70, 0.50, 0.50 }));

		IntegerTargetTypeLookup sea_states = new IntegerTargetTypeLookup(states, new Double(
				1.0));

		// and lastly the light level
		IntegerLookup lightLevel = new IntegerLookup(new int[] { EnvironmentType.DAYLIGHT,
				EnvironmentType.DUSK, EnvironmentType.MOON_NIGHT, EnvironmentType.DARK_NIGHT },
				new double[] { 1, 0.4, 0.3, 0.05 });

		OpticEnvironment defaultEnv = new OpticEnvironment(attenuation, lightLevel, "sample",
				sea_states, visibility);

		return defaultEnv;
	}

	// //////////////////////////////////////////////////////////
	// member methods
	// //////////////////////////////////////////////////////////
	/**
	 * determine the lookup parameters applicable to this sensor
	 * 
	 * @param ownship
	 *          us
	 * @param target
	 *          them
	 * @param scenario
	 *          the scenario
	 * @param environment
	 *          the environment
	 * @param time
	 *          current time
	 * @return the set of lookup parameters applicable to this sensor
	 */
	protected LookupSensor.LastTargetContact parametersFor(ParticipantType ownship,
			ParticipantType target, ScenarioType scenario, EnvironmentType environment,
			long time)
	{
		LookupSensor.LastTargetContact res = new LookupSensor.LastTargetContact();

		OpticEnvironment lookups = environment.getOpticEnvironment();

		if (lookups == null)
		{
			// bugger. there aren't any. have we created our defaults?
			if (_defaultLookups == null)
			{
				// nope, better create it
				_defaultLookups = getDefaultLookups();
			}

			lookups = _defaultLookups;
		}

		/**
		 * first the visual attenuation
		 * 
		 * @see EnvironmentType.VERY_CLEAR
		 */
		int currentAtten = environment.getAtmosphericAttentuationFor(time, ownship
				.getStatus().getLocation());
		Double attenVal = lookups._attenuation.find(currentAtten);

		/**
		 * now the target visibility
		 * 
		 * @see Category.Type.CARRIER
		 */
		String targetType = target.getCategory().getType();
		Double targetVis = lookups._visibility.find(targetType);

		// next the sea state value
		int seaState = environment.getSeaStateFor(time, ownship.getStatus().getLocation());
		Double seaStateVal = lookups.getSea_states().find(seaState, targetType);

		/**
		 * and lastly the light value
		 * 
		 * @see EnvironmentType.DUSK
		 */
		int lightLevel = environment
				.getLightLevelFor(time, ownship.getStatus().getLocation());
		Double lightValue = lookups.getLightLevel().find(lightLevel);

		// and store the parameters
		res.insertElementAt(attenVal, ATTEN_INDEX);
		res.insertElementAt(targetVis, VIS_INDEX);
		res.insertElementAt(seaStateVal, SEA_STATE_INDEX);
		res.insertElementAt(lightValue, LIGHT_INDEX);

		// done
		return res;
	}

	public boolean canIdentifyTarget()
	{
		return true; // To change body of implemented methods use File | Settings |
		// File Templates.
	}

	private final static int ATTEN_INDEX = 0;

	private final static int VIS_INDEX = 1;

	private final static int SEA_STATE_INDEX = 2;

	private final static int LIGHT_INDEX = 3;

	/**
	 * calculate the predicted range for this contact. Uses the visual horizon
	 * from Bowditch, The Practical navigator (1995) table 12
	 * 
	 * @param ownship
	 * @param target
	 * @param scenario
	 * @param environment
	 * @return
	 */
	protected WorldDistance calculateRP(ParticipantType ownship, ParticipantType target,
			ScenarioType scenario, EnvironmentType environment, long time,
			LookupSensor.LastTargetContact params)
	{
		WorldDistance res = null;

		// retrieve our parameters
		double attenVal = ((Double) params.get(ATTEN_INDEX)).doubleValue();
		double targetVis = ((Double) params.get(VIS_INDEX)).doubleValue();
		Double seaVal = ((Double) params.get(SEA_STATE_INDEX));
		double seaStateVal = seaVal.doubleValue();
		double lightValue = ((Double) params.get(LIGHT_INDEX)).doubleValue();

		// find out the ownship and target height
		final double EARTH_RADIUS = 6371950;
		final double B_FACTOR = 0.8279; // taken from from Bowditch, The Practical
		// navigator (1995) table 12
		double myHeight = -ownship.getStatus().getLocation().getDepth();
		double hisHeight = -target.getStatus().getLocation().getDepth();

		// do the calc
		double RP_1 = -1 / attenVal * Math.log(targetVis / seaStateVal) * lightValue;
		double RP_2 = Math.sqrt(2 * EARTH_RADIUS * myHeight / B_FACTOR)
				+ Math.sqrt(2 * EARTH_RADIUS * hisHeight / B_FACTOR);

		// and use the min value.
		double RP = Math.min(RP_1, RP_2);

		// done.
		res = new WorldDistance(RP, WorldDistance.METRES);

		return res;
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 * 
	 * @return yes/no
	 */
	public boolean hasEditor()
	{
		return true;
	}

	/**
	 * get the editor for this item
	 * 
	 * @return the BeanInfo data for this editable object
	 */
	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new OpticLookupInfo(this);

		return _myEditor;
	}

	// //////////////////////////////////////////////////////////
	// model support
	// //////////////////////////////////////////////////////////

	/**
	 * get the version details for this model.
	 * 
	 * <pre>
	 *   $Log: OpticLookupSensor.java,v $
	 *   Revision 1.3  2006/09/21 12:20:44  Ian.Mayo
	 *   Reflect introduction of default names
	 *
	 *   Revision 1.2  2006/09/14 14:11:10  Ian.Mayo
	 *   Source tidying
	 *
	 *   Revision 1.1  2006/08/08 14:21:57  Ian.Mayo
	 *   Second import
	 *  
	 *   Revision 1.1  2006/08/07 12:26:06  Ian.Mayo
	 *   First versions
	 *  
	 *   Revision 1.23  2004/11/05 15:29:20  Ian.Mayo
	 *   Correct tests to reflect new structure of test-read file
	 *  
	 *   Revision 1.22  2004/11/04 09:30:22  Ian.Mayo
	 *   Handle sensors which can't provide range/bearing
	 *   &lt;p/&gt;
	 *   Revision 1.21  2004/10/27 15:13:51  Ian.Mayo
	 *   Reflect changed structure of OpticEnvironment
	 *   &lt;p/&gt;
	 *   Revision 1.20  2004/10/27 14:07:05  Ian.Mayo
	 *   Handle default values better
	 *   &lt;p/&gt;
	 *   Revision 1.19  2004/10/27 13:30:09  Ian.Mayo
	 *   More environment handling
	 *   &lt;p/&gt;
	 *   Revision 1.18  2004/10/26 15:03:38  Ian.Mayo
	 *   Move lookup tables up to environment
	 *   &lt;p/&gt;
	 *   Revision 1.17  2004/10/25 15:30:23  Ian.Mayo
	 *   Start incorporating lookup data tables in environment
	 *   &lt;p/&gt;
	 *   Revision 1.16  2004/09/06 14:20:09  Ian.Mayo
	 *   Provide default icons &amp; properties for sensors
	 *   &lt;p/&gt;
	 *   Revision 1.15  2004/09/06 14:04:18  Ian.Mayo
	 *   Switch to supporting editables in Layer Manager, and showing icon for any editables which have one
	 *   &lt;p/&gt;
	 *   Revision 1.14  2004/09/01 15:42:58  Ian.Mayo
	 *   Extract the double values that bit sooner
	 *   &lt;p/&gt;
	 *   Revision 1.13  2004/08/31 09:37:01  Ian.Mayo
	 *   Rename inner static tests to match signature **Test to make automated testing more consistent
	 *   &lt;p/&gt;
	 *   Revision 1.12  2004/08/26 17:05:39  Ian.Mayo
	 *   Implement more editable properties
	 *   &lt;p/&gt;
	 *   Revision 1.11  2004/08/25 11:21:16  Ian.Mayo
	 *   Remove main methods which just run junit tests
	 *   &lt;p/&gt;
	 *   Revision 1.10  2004/08/23 09:12:33  Ian.Mayo
	 *   Update tests to reflect new detection list processing
	 *   &lt;p/&gt;
	 *   Revision 1.9  2004/08/23 08:06:10  Ian.Mayo
	 *   Implement clearing old detection lists, minor refactoring
	 *   &lt;p/&gt;
	 *   Revision 1.8  2004/08/20 15:08:22  Ian.Mayo
	 *   Part way through changing detection cycle so that it doesn't start afresh each time - each sensor removes it's previous calls the next time it is called (to allow for TBDO)
	 *   &lt;p/&gt;
	 *   Revision 1.7  2004/08/16 10:36:39  Ian.Mayo
	 *   Reflect changed sensor performance model
	 *   &lt;p/&gt;
	 *   Revision 1.6  2004/08/10 13:52:52  Ian.Mayo
	 *   Better comments
	 *   &lt;p/&gt;
	 *   Revision 1.5  2004/08/09 15:49:26  Ian.Mayo
	 *   Refactor category types into Force, Environment, Type sub-classes
	 *   &lt;p/&gt;
	 *   Revision 1.4  2004/05/24 15:05:30  Ian.Mayo
	 *   Commit changes conducted at home
	 *   &lt;p/&gt;
	 *   Revision 1.5  2004/04/22 21:38:19  ian
	 *   Use corrected algs
	 *   &lt;p/&gt;
	 *   Revision 1.4  2004/04/15 22:00:39  ian
	 *   Change visual detection algorithm
	 *   &lt;p/&gt;
	 *   Revision 1.3  2004/04/13 20:53:10  ian
	 *   Provide lookup results for unfound target types
	 *   &lt;p/&gt;
	 *   Revision 1.2  2004/04/08 20:27:29  ian
	 *   Restructured contructor for CoreObserver
	 *   &lt;p/&gt;
	 *   Revision 1.1.1.1  2004/03/04 20:30:54  ian
	 *   no message
	 *   &lt;p/&gt;
	 *   Revision 1.3  2004/02/18 08:59:42  Ian.Mayo
	 *   Tidying
	 *   &lt;p/&gt;
	 *   &lt;p/&gt;
	 * </pre>
	 */
	public String getVersion()
	{
		return "$Date$";
	}

	// ////////////////////////////////////////////////
	// structure to contain optic lookup set
	// ////////////////////////////////////////////////
	public static class OpticEnvironment
	{
		protected IntegerLookup _attenuation;

		protected StringLookup _visibility;

		protected IntegerTargetTypeLookup _sea_states;

		protected IntegerLookup _lightLevel;

		protected String _name;

		public OpticEnvironment(IntegerLookup attenuation, IntegerLookup lightLevel,
				String name, IntegerTargetTypeLookup sea_states, StringLookup visibility)
		{
			_attenuation = attenuation;
			_lightLevel = lightLevel;
			_name = name;
			_sea_states = sea_states;
			_visibility = visibility;
		}

		public IntegerLookup get_attenuation()
		{
			return _attenuation;
		}

		public IntegerLookup getLightLevel()
		{
			return _lightLevel;
		}

		public void setName(String name)
		{
			_name = name;
		}

		public String getName()
		{
			return _name;
		}

		public IntegerTargetTypeLookup getSea_states()
		{
			return _sea_states;
		}

		public StringLookup getVisibility()
		{
			return _visibility;
		}

	}

	// //////////////////////////////////////////////////
	// the editor object
	// //////////////////////////////////////////////////
	static public class OpticLookupInfo extends BaseSensorInfo
	{
		/**
		 * @param data
		 *          the Layers themselves
		 */
		public OpticLookupInfo(final OpticLookupSensor data)
		{
			super(data);
		}
	}

	// //////////////////////////////////////////////////////////
	// test the optic sensor
	// //////////////////////////////////////////////////////////
	static public final class OpticLookupTest extends SupportTesting.EditableTesting
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public OpticLookupTest(String val)
		{
			super(val);
		}

		/**
		 * get an object which we can test
		 * 
		 * @return Editable object which we can check the properties for
		 */
		public Editable getEditable()
		{
			return getTestOpticSensor();
		}

		public static OpticLookupSensor getTestOpticSensor()
		{
			return new OpticLookupSensor(12, "eyeball", 0.05, 10, 1.05, 0.8, new Duration(20,
					Duration.SECONDS), 0.2, new Duration(30, Duration.SECONDS));
		}

		public void testDefaultValues()
		{
			// set the sensor
			OpticLookupSensor sensor = getTestOpticSensor();

			// and set the environment
			OpticEnvironment env = sensor.getDefaultLookups();

			// now check that a real value gets return
			Double res = env._sea_states.find(1, Category.Type.CARRIER);
			assertNotNull("didn't find result which we should have done", res);
			assertEquals("didn't find correct lookup value", 1d, res.doubleValue(), 0d);

			// and for the vis table
			res = env._visibility.find(Category.Type.CARRIER);
			assertNotNull("didn't find result which we should have done", res);
			assertEquals("didn't find correct lookup value", 0.2d, res.doubleValue(), 0d);

			// now move on to datums which we don't have
			res = env._sea_states.find(1, Category.Type.DESTROYER);
			assertNotNull("didn't find result which we should have done", res);
			assertEquals("didn't find correct lookup value",
					env._sea_states.getUnknownResult(), res);

			// and for the vis table
			res = env._visibility.find(Category.Type.DESTROYER);
			assertNotNull("didn't find result which we should have done", res);
			assertEquals("didn't find correct lookup value",
					env._visibility.getUnknownResult(), res);

			// now try to remove the default value, and check that exception gets
			// thrown
			env._visibility.setUnknownResult(null);
			env._sea_states.setUnknownResult(null);

			boolean visThrown = false;

			try
			{

				res = env._sea_states.find(1, Category.Type.DESTROYER);
				assertNull("didn't find result which we should have done", res);

				// and for the vis table
				res = env._visibility.find(Category.Type.DESTROYER);
				assertNull("didn't find result which we should have done", res);
			}
			catch (RuntimeException re)
			{
				visThrown = true;
			}

			res = env._sea_states.find(1, Category.Type.DESTROYER);

			try
			{
				res = env._visibility.find(Category.Type.DESTROYER);
				fail("should have thrown exception");
			}
			catch (RuntimeException re)
			{
				visThrown = true;
			}

			assertTrue("should have fired missing item for missing type in visibility test",
					visThrown);

		}

		static public class MyEnvironment extends CoreEnvironment
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected int _lightLevel = 1;

			protected int _seaState = 4;

			protected int _atten = EnvironmentType.VERY_CLEAR;

			/**
			 * get the light level at this location
			 * 
			 * @param time
			 *          the time we're talking about
			 * @param location
			 *          the location we're talking about
			 * @return the current light level
			 */
			public int getLightLevelFor(long time, WorldLocation location)
			{
				return _lightLevel;
			}

			/**
			 * get the atmospheric attenuation
			 * 
			 * @param time
			 *          current time
			 * @param location
			 *          place to get data for
			 * @return one of the atmospheric attenuation factors
			 */
			public int getAtmosphericAttentuationFor(long time, WorldLocation location)
			{
				return _atten;
			}

			/**
			 * get the sea state
			 * 
			 * @param time
			 *          current time
			 * @param location
			 *          place to get data for
			 * @return sea state, from 0 to 10
			 */
			public int getSeaStateFor(long time, WorldLocation location)
			{
				return _seaState;
			}

			/**
			 * over-ride the sea state
			 * 
			 * @param seaState
			 *          the new sea-state to use
			 */
			public void setSeaState(int seaState)
			{
				this._seaState = seaState;
			}
		}

		public final void testFirst()
		{
			OpticLookupSensor ts = getTestOpticSensor();
			Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			Helo alpha = new Helo(12);
			alpha.setName("Alpha");
			alpha.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE,
					Category.Type.HELO));
			WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			statA.getLocation().setDepth(-900);
			alpha.setStatus(statA);
			Surface bravo = new Surface(11);
			Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			bravo.setName("Bravo");
			bravo.setCategory(new Category(Category.Force.BLUE, Category.Environment.SURFACE,
					Category.Type.FISHING_VESSEL));
			WorldLocation originB = SupportTesting.createLocation(6000, 6000);
			statB.setLocation(originB);
			statB.getLocation().setDepth(-20);
			bravo.setStatus(statB);

			alpha.addSensor(ts);
			CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(alpha.getId(), alpha);
			scenario.addParticipant(bravo.getId(), bravo);

			scenario.setScenarioStepTime(1000);

			// create our dummy environment object
			MyEnvironment env = new MyEnvironment();

			// ok, start off with us miles apart - and check we can't see each other.
			long time = 1000;
			DetectionList res = new DetectionList();
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("null detections returned", 0, res.size());

			// ok. move the targets a little closer (but still out of range)
			statB.setLocation(SupportTesting.createLocation(800, 700));
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("null detections returned", 0, res.size());

			// and closer still (to gain contact)
			statB.setLocation(SupportTesting.createLocation(500, 500));
			time += 1000;
			ts.detects(env, res, alpha, scenario, time);
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					((LastTargetContact) ts._pastContacts.get(bravo)).getDetectionState());

			// check that the parameters have stayed the same
			statB.setLocation(SupportTesting.createLocation(500, 500));
			time += 1000;
			ts.detects(env, res, alpha, scenario, time);
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					((LastTargetContact) ts._pastContacts.get(bravo)).getDetectionState());

			// stay close, move forward enough elapsed time so that we can move to
			// classified
			time += 21000;
			ts.detects(env, res, alpha, scenario, time);
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					((LastTargetContact) ts._pastContacts.get(bravo)).getDetectionState());

			// change one of the parameters
			env._atten = EnvironmentType.FOG;

			// check that the parameters have changed
			time += 1000;
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("null detections returned", 0, res.size());
		}

		public void testRead()
		{
			String fileName = "lookup_test_scenario.xml";
			fileName = "src/ASSET/Models/Sensor/Lookup/" + fileName;

			CoreScenario scen = new CoreScenario();
			try
			{
				FileInputStream is = new FileInputStream(fileName);
				final InputStream bi = new BufferedInputStream(is);
				ASSET.Util.XML.ASSETReaderWriter.importThis(scen, fileName, bi);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			// ok, try to check that the data got loaded.
			Integer[] participants = scen.getListOfParticipants();

			ParticipantType partA = scen.getThisParticipant(participants[0].intValue());
			ParticipantType partB = scen.getThisParticipant(participants[1].intValue());
			ParticipantType part;
			if (partA instanceof Helo)
				part = partA;
			else
				part = partB;

			assertTrue("we've loaded and found the helo", part instanceof Helo);
			Helo helo = (Helo) part;
			SensorList theSensors = helo.getSensorFit();
			assertEquals("has sensors loaded", 3, theSensors.getNumSensors());
			SensorType firstSensor = theSensors.getSensor(444);
			assertNotNull("found first sensor", firstSensor);
			assertTrue(firstSensor instanceof OpticLookupSensor);
			OpticLookupSensor opticS = (OpticLookupSensor) firstSensor;

			// check the values
			assertEquals("VDR correct", 0.05, opticS.VDR, 0.0);
			assertEquals("name correct", "Eyesight", opticS.getName());
			assertEquals("MRF correct", 1.05, opticS.MRF, 0.0);
			assertEquals("CRF correct", 0.8, opticS.CRF, 0.0);
			assertEquals("IRF correct", 0.2, opticS.IRF, 0.0);

			assertEquals("tbdo correct", 10000, opticS.getTimeBetweenDetectionOpportunities(),
					0);
			assertEquals("ctp correct", 20000, opticS.CTP.getMillis(), 0);
			assertEquals("itp correct", 30000, opticS.ITP.getMillis(), 0);

			// also stick in our tests for the ladder search
			Waterfall wat = (Waterfall) helo.getDecisionModel();
			Sequence seq = (Sequence) wat.getModels().elementAt(1);
			assertEquals("all loaded", 3, seq.getModels().size());
			assertTrue("ladder loaded", seq.getModels().elementAt(2) instanceof LadderSearch);

			helo.addParticipantDetectedListener(new ParticipantDetectedListener()
			{
				public void newDetections(DetectionList detections)
				{
					if (detections.size() > 0)
					{
						detectedOther = true;

						final DetectionEvent det = detections.getDetection(0);

						maxClassification = Math.max(maxClassification, det.getDetectionState());
					}
					else
					{
						if (detectedOther)
							targetLost = true;
					}
				}

				public void restart()
				{
				}
			});

			// DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp",
			// "optic_lookup.rep", true, "test observer", true);
			// dro.setup(scen);

			// hey, let's run through it a little
			for (int i = 0; i < 1800; i++)
			{
				scen.step();
			}

			// dro.tearDown(scen);

			assertTrue("yes, we made a detection", detectedOther);
			assertTrue("yes, we also lost contact", targetLost);
			assertEquals("managed to identify", 2, maxClassification, 0);

		}

		protected boolean detectedOther = false;

		protected boolean targetLost = false;

		protected int maxClassification = 0;
	}

}
