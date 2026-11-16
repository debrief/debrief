package ASSET.Models.Sensor.Initial;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Sensor.Initial.OpticSensor.*;
import ASSET.Models.Sensor.Initial.OpticSensor.*;
import ASSET.NetworkParticipant;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Mediums.Optic;
import ASSET.Models.Sensor.SensorList;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class OpticSensorTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new OpticSensor(12);
	}

	@Test
	public void testOpticSensor() {

		// reset the earth model
		WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

		// ok, create the sensor
		final OpticSensor os = new OpticSensor(12);

		// now the objects
		final WorldLocation l1 = new WorldLocation(0, 0, 0);
		WorldLocation l2 = l1.add(new WorldVector(0, MWC.Algorithms.Conversions.m2Degs(2000), 0));

		final ASSET.Models.Vessels.Surface su = new ASSET.Models.Vessels.Surface(12);
		final Status theStat = new Status(12, 0);
		theStat.setLocation(l1);
		theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		su.setStatus(theStat);
		final SensorList sl = new SensorList();
		sl.add(os);

		final ASSET.Models.Vessels.SSN ssn = new ASSET.Models.Vessels.SSN(14);
		final Status otherStat = new Status(theStat);
		otherStat.setLocation(l2);
		otherStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics rc = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
		final Optic opticRadNoise = new Optic(2, new WorldDistance(2, WorldDistance.METRES));
		rc.add(EnvironmentType.VISUAL, opticRadNoise);
		ssn.setRadiatedChars(rc);
		ssn.setStatus(otherStat);

		final EnvironmentType env = new SimpleEnvironment(1, 1, 1);

		// first check the components
		assertTrue(os.canIdentifyTarget(), "knows what target is");

		// can we do detection when we're dived
		su.getStatus().getLocation().setDepth(40);
		assertTrue(!os.canDetectThisType(su, ssn, env), "we can't do detection when we're dived");
		su.getStatus().getLocation().setDepth(0);
		assertTrue(os.canDetectThisType(su, ssn, env), "we can do detection when we're dived");
		ssn.getStatus().getLocation().setDepth(40);
		assertTrue(!os.canDetectThisType(su, ssn, env), "we can't do detection when he's dived");

		// put us both back on the surface
		ssn.getStatus().getLocation().setDepth(0);

		// find the loss
		double loss = os.getLoss(env, os.getLocationFor(ssn), os.getLocationFor(su));
		assertTrue(OpticSensor._LOSS_IN_LINE_OF_SIGHT > loss, "we are in contact when close");
		assertTrue(0 <= loss, "we are in contact when close");

		// put them further away
		l2 = new WorldLocation(
				su.getStatus().getLocation().add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(19), 0)));
		ssn.getStatus().setLocation(l2);
		loss = os.getLoss(env, os.getLocationFor(ssn), os.getLocationFor(su));
		assertEquals(OpticSensor._LOSS_NOT_IN_LINE_OF_SIGHT, loss, 0.001, "we not are in contact when further away, loss:" + loss);

		// put it a bit higher
		l2 = new WorldLocation(
				su.getStatus().getLocation().add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(19), -80)));
		su.getStatus().setLocation(l2);
		loss = os.getLoss(env, os.getLocationFor(ssn), os.getLocationFor(su));
		assertTrue(loss < OpticSensor._LOSS_IN_LINE_OF_SIGHT, "we are back in contact when higher up");

		// ok, we're in range - now start looking at the rad noise
		double radNoise = os.getTgtNoise(ssn, 0);
		assertEquals(2, radNoise, 0.01, "check we've got the correct rad noise");

		// just check he's not transmitting it when he's deep
		ssn.getStatus().getLocation().setDepth(100);
		radNoise = os.getTgtNoise(ssn, 0);
		assertEquals(0, radNoise, 0.01, "check he's not trasnmitting when deep");

		// back to normal
		su.getStatus().setLocation(new WorldLocation(0, 0, 0));
		ssn.getStatus().setLocation(
				su.getStatus().getLocation().add(new WorldVector(0, MWC.Algorithms.Conversions.m2Degs(500), 12)));

		// ok, do the detection
		final DetectionEvent de = os.detectThis(env, su, ssn, 200, null);

		assertNotNull(de, "Valid detection produced");

	}
}
