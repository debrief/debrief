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

package ASSET.Models.Decision.Tactical;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Tactical.LaunchWeapon.*;
import ASSET.Models.Decision.Tactical.LaunchWeapon.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Models.DecisionType;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Decision.Movement.Trail;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Mediums.Optic;
import ASSET.Models.Movement.SSMovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/****************************************************
 * member variables
 ***************************************************/

/**
 * ************************************************* testing
 * *************************************************
 */
public class LaunchWeaponTest extends ASSET.Util.EditableTesting {

	public static final String launchBehaviour = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<Torpedo Name=\"SPEARFISH\">"
			+ "<Category Environment=\"SUBSURFACE\" Force=\"BLUE\" Type=\"TORPEDO\"/>" + "<SensorFit>"
			+ "<BroadbandSensor Aperture=\"180\"/>" + "</SensorFit>" + "<Status Course=\"0\" Fuel=\"0\" Id=\"0\">"
			+ "<Speed Units=\"m/s\" Value=\"20\"/>" + "<Location>"
			+ "<shortLocation Depth=\"0\" Lat=\"0\" Long=\"0\">" + "<Height Units=\"m\" Value=\"0\"/>"
			+ "</shortLocation>" + "</Location>" + "</Status>" + "<Waterfall>"
			+ "<Detonate Name=\"DestroyRed\" DetonationPower=\"90.000\" > "
			+ "<WorldDistance Units=\"yds\" Value=\"400\"/>" + "<TargetType>" + "<Type Name=\"RED\" />"
			+ "<Type Name=\"SURFACE\" />" + "</TargetType>" + "</Detonate> " + "<Trail Name=\"Chase Red\" > "
			+ "<AllowableError Units=\"yds\" Value=\"400\"/>" + "<TrailRange Units=\"yds\" Value=\"100\"/>"
			+ "<TargetType>" + "<Type Name=\"RED\" />" + "<Type Name=\"SURFACE\" />" + "</TargetType>" + "</Trail> "
			+ "<Transit Looping=\"FALSE\" Name=\"PROSECUTE\" Speed=\"30\">" + "<WorldPath>" + "<Point>"
			+ "<shortLocation Depth=\"$TGT_DEPTH$\" Lat=\"$TGT_LAT$\" Long=\"$TGT_LONG$\"/>" + "</Point>"
			+ "</WorldPath>" + "</Transit>" + "</Waterfall>" + "<RadiatedCharacteristics>"
			+ "<Broadband BaseNoiseLevel=\"40\"/>" + "</RadiatedCharacteristics>"
			+ "<SSMovementCharacteristics AccelerationRate=\"10\" DepthChangeRate=\"2\""
			+ " FuelUsageRate=\"0\" MaxDepth=\"200\" MaxSpeed=\"34\" Name=\"SPEARFISH\" >"
			+ " <MinSpeed Units=\"m/s\" Value=\"0\"/>" + " <MaxSpeed Units=\"m/s\" Value=\"16\"/>"
			+ " <MinHeight Units=\"m\" Value=\"-100\"/>" + " <MaxHeight Units=\"m\" Value=\"0\"/>"
			+ " <DefaultClimbRate Units=\"ft/s\" Value=\"20\"/>" + " <DefaultDiveRate Units=\"ft/s\" Value=\"20\"/>"
			+ " <AccelerationRate Units=\"m/s/s\" Value=\"12\"/>" + " <TurningCircle Units=\"m\" Value=\"45\"/>"
			+ " <DecelerationRate Units=\"m/s/s\" Value=\"12\"/> </SSMovementCharacteristics>" + "</Torpedo>";

	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new LaunchWeapon();
	}

	/**
	 * test case for importing a Launch command from XML (because it's fiddly)
	 */
	public void NOTtestImportXML() {

		/**
		 * create the scenario
		 *
		 */
		final CoreScenario scenario = new CoreScenario();
		scenario.setScenarioStepTime(10000);
		scenario.setTime(0);
		scenario.setName("Testing importing Launch");

		final Waterfall theWaterfall = new Waterfall();

		/**
		 * read in the file
		 *
		 */

		String root = System.getProperty("TEST_ROOT");
		if (root == null) {
			System.err.println("Don't know test root, using hard-coded.");
			root = "src";
		}

		final String fName = root + "TEST_LAUNCH.XML";

		// check it exists
		assertTrue(new java.io.File(fName).exists(), "Couldn't find tests data file");

		try {
			// ASSET.Util.XML.ASSETReaderWriter.importThis(scenario, fName, new
			// java.io.FileInputStream(fName));
			ASSET.Util.XML.ASSETReaderWriter.importThis(theWaterfall, fName, new java.io.FileInputStream(fName));
		} catch (final java.io.IOException ee) {
			ee.printStackTrace();
		}

		final Waterfall newWaterfall = (Waterfall) theWaterfall.getModels().firstElement();
		assertEquals(newWaterfall.getModels().firstElement().toString(), "Destroy Red", "Read in launch behaviour");
		final LaunchWeapon theLaunch = (LaunchWeapon) newWaterfall.getModels().firstElement();
		// final String launchType = theLaunch.getLaunchType();
		assertEquals(1719, theLaunch.getLaunchType().length(), "Behaviour is correct length");

	}

	/**
	 * test case for launching Ownship: SSN Target: SU
	 * <p/>
	 * SSN: LaunchWeapon behavior: look for SU target, launch torpedo within 5000
	 * yds Normal behaviour: transit from A to B Sensors: Visual sensor, in
	 * continuous contact
	 * <p/>
	 * SU: Behaviour: Wander around origin at surface at 4 kts
	 * <p/>
	 * Torpedo: Prosecute Behaviour: Prosecute SU, go bang. Search behaviour:
	 * Transit in indicated direction
	 */

	// todo: re-instate this test, we're having problems with the launch range

	// TODO FIX-TEST
	public void NtestLaunch() {

		final double rngToHim = 5000;
		final double crseToHim = 45;

		final WorldLocation origin = new WorldLocation(0, 0, 0);
		final WorldLocation hisLoc = origin.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(crseToHim),
				MWC.Algorithms.Conversions.Yds2Degs(rngToHim), 0));
		final Status blueStat = new Status(13, 0);
		blueStat.setLocation(origin);
		blueStat.setCourse(0);
		blueStat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		final Status redStat = new Status(blueStat);
		redStat.setLocation(hisLoc);

		// setup a couple of targets
		final ASSET.Models.Vessels.SSN ssn = new ASSET.Models.Vessels.SSN(1);
		ssn.setName("ssn");
		ssn.setMovementChars(SSMovementCharacteristics.generateDebug("scrap", 1, 1, 0, 20, 1, 300, 1, 1, 10, 100));
		ssn.setCategory(new ASSET.Participants.Category(Category.Force.BLUE, Category.Environment.SUBSURFACE,
				Category.Type.SUBMARINE));
		ssn.setStatus(blueStat);
		final ASSET.Models.Sensor.Initial.OpticSensor periscope = new ASSET.Models.Sensor.Initial.OpticSensor(12);
		ssn.addSensor(periscope);
		RadiatedCharacteristics rc = new RadiatedCharacteristics();
		rc.add(EnvironmentType.VISUAL, new Optic(12, new WorldDistance(12, WorldDistance.METRES)));
		ssn.setRadiatedChars(rc);

		final ASSET.Models.Vessels.Surface su = new ASSET.Models.Vessels.Surface(4);
		su.setName("su");
		su.setMovementChars(SurfaceMovementCharacteristics.generateDebug("scrap", 1, 1, 0.001, 14, 1, 299));
		su.setCategory(new ASSET.Participants.Category(Category.Force.RED, Category.Environment.SURFACE,
				Category.Type.CARRIER));
		su.setStatus(redStat);
		rc = new RadiatedCharacteristics();
		rc.add(EnvironmentType.VISUAL, new Optic(12, new WorldDistance(12, WorldDistance.METRES)));
		su.setRadiatedChars(rc);

		/**
		 * create the scenario
		 *
		 */
		final CoreScenario scenario = new CoreScenario();
		scenario.setScenarioStepTime(10000);
		scenario.setTime(0);
		scenario.setName("Testing weapon launch");

		// get somebody to listen to the tracks
		final ASSET.Scenario.Observers.Recording.DebriefReplayObserver debrief_writer = new ASSET.Scenario.Observers.Recording.DebriefReplayObserver(
				"�test_reports", null, true, "test observer", true);
		debrief_writer.setup(scenario);

		// DON'T BOTHER RECORDING JUST YET!
		debrief_writer.setActive(false);

		/**
		 * now the behaviours
		 *
		 */
		final Trail trailRed = new Trail(new WorldDistance(2000, WorldDistance.YARDS));
		final TargetType getRed = new TargetType();
		getRed.addTargetType(Category.Force.RED);
		trailRed.setTargetType(getRed);

		final LaunchWeapon launchWeapon = new LaunchWeapon();
		launchWeapon.setTargetType(getRed);
		launchWeapon.setLaunchRange(new WorldDistance(4000, WorldDistance.YARDS));
		launchWeapon.setCoolOffTime(new Duration(Duration.DAYS, 3)); // 24 hr cool off time
		launchWeapon.setLaunchType(launchBehaviour);

		final Waterfall blueWaterfall = new Waterfall();
		blueWaterfall.insertAtFoot(trailRed);
		blueWaterfall.insertAtHead(launchWeapon);
		ssn.setDecisionModel(blueWaterfall);

		scenario.addParticipant(1, ssn);
		scenario.addParticipant(4, su);

		// move forward a while
		boolean found = false;
		double lastRange = -1;
		double thisRange = -1;

		int counter = 0;

		while (!found && counter <= 100000) {
			scenario.step();
			final WorldVector sep = ssn.getStatus().getLocation().subtract(su.getStatus().getLocation());
			thisRange = MWC.Algorithms.Conversions.Degs2Yds(sep.getRange());

			if (lastRange == -1) {
				lastRange = thisRange;
			}

			if (ssn.getActivity().equals(LaunchWeapon.LAUNCH_MESSAGE)) {
				found = true;
				System.out.println("LAUNCHED");
			} else {
				lastRange = thisRange;
			}

			// just do "MAD" check to ensure we don't go on for ever
			counter++;
		}

		// check we didn't time out
		assertTrue(counter < 100000, " we just ran to end of loop (no fire)");

		// check we launched at the right point
		final double launchYds = launchWeapon.getLaunchRange().getValueIn(WorldDistance.YARDS);
		assertTrue(lastRange > launchYds, "didn't fire too soon");
		assertTrue(thisRange <= launchYds, "didn't fire too late, got:" + launchYds + " but wanted:" + thisRange);

		// so, we've launched the weapon - see how many participants are in the scenario
		assertEquals(3, scenario.getListOfParticipants().length, "New participant created");

		// find the weapon
		// final ParticipantType torpedo =
		// scenario.getThisParticipant(scenario.getListOfParticipants()[0].intValue());

		// check if the torpedo gets to the target
		while (scenario.getListOfParticipants().length == 3) {
			scenario.step();
		}

		// check that the torpedo destroys the SU, and they are both removed
		assertEquals(1, scenario.getListOfParticipants().length, "surface participant destroyed");

		// move a bit further just to give us some more blue track
		for (int ii = 0; ii < 20; ii++) {
			scenario.step();
		}

	}

	/**
	 * test we can read the weapon from file
	 */
	@Test
	public void testReadWeapon() {

		final WorldDistance rng = new WorldDistance(12.1, WorldDistance.YARDS);
		final Float brg = new Float(2.1f);

		final CoreSensor sensor = new ASSET.Models.Sensor.Initial.OpticSensor(12);
		final NetworkParticipant host = new ASSET.Models.Vessels.SSK(12);
		final NetworkParticipant target = new ASSET.Models.Vessels.Surface(22);

		final DetectionEvent de = new DetectionEvent(0, host.getId(), null, sensor, rng, rng, brg, null, null, null,
				null, null, target);

		final WorldLocation currentLocation = new WorldLocation(1, 1, 0);
		final Status currentStatus = new Status(12, 2000);
		currentStatus.setLocation(currentLocation);

		final ParticipantType newPart = LaunchWeapon.getWeapon(de, currentStatus, launchBehaviour, null);
		assertNotNull(newPart, "participant created");

		final DecisionType dt = newPart.getDecisionModel();
		assertEquals("Waterfall", dt.getName(), "correct model loaded");

		System.out.println("part created");
	}

	/**
	 * test we can read the weapon from file
	 */
	@Test
	public void testReplaceString() {
		final String original = "AABBCCDDCCEEFF";
		final String out1 = "AABB___DD___EEFF";
		final String swap_out = "CC";
		final String swap_in = "___";

		assertEquals(LaunchWeapon.replaceAll(original, "CA", "bb"), original, "Input not found");
		final String test4 = LaunchWeapon.replaceAll(original, swap_out, swap_in);
		assertEquals(test4, out1, "Swapping text worked");

	}

	/**
	 * test the string swapping
	 */
	@Test
	public void testSubstitution() {
		final String sample = "eerrt$BRG$sdfsdfs$RNG$sdf<TargetType/>sdd";
		final String answer = "eerrt2.1sdfsdfs12.1sdf<TargetType><Type Name=\"RED\"/></TargetType>sdd";

		final WorldDistance rng = new WorldDistance(12.1, WorldDistance.YARDS);
		final Float brg = new Float(2.1f);

		final CoreSensor sensor = new ASSET.Models.Sensor.Initial.OpticSensor(12);
		final NetworkParticipant host = new ASSET.Models.Vessels.SSK(12);
		final NetworkParticipant target = new ASSET.Models.Vessels.Surface(22);

		final DetectionEvent de = new DetectionEvent(0, host.getId(), null, sensor, rng, rng, brg, null, null, null,
				null, null, target);

		final TargetType getRed = new TargetType();
		getRed.addTargetType(Category.Force.RED);

		final WorldLocation currentLocation = new WorldLocation(1, 1, 0);
		final Status currentStatus = new Status(12, 2000);
		currentStatus.setLocation(currentLocation);

		final String res = LaunchWeapon.swapKeywords(de, currentStatus, sample, getRed);
		assertEquals(answer, res, "strings swapped");
	}
}
