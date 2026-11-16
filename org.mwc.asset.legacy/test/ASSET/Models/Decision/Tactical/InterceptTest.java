package ASSET.Models.Decision.Tactical;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Tactical.Intercept.*;
import ASSET.Models.Decision.Tactical.Intercept.*;
import ASSET.NetworkParticipant;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.UserControl;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class InterceptTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	protected static DemandedStatus _demStat = null;


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final TargetType theTgt = new TargetType(Category.Force.RED);
		final Intercept followBlue = new Intercept(theTgt, true);
		return followBlue;
	}

	@Test
	public void testCalc() {
		final Status myStat = new Status(12, 12);
		final Status hisStat = new Status(12, 12);
		final WorldLocation locA = new WorldLocation(0, 0, 0);
		final WorldLocation locB = locA.add(new WorldVector(Math.toRadians(240),
				new WorldDistance(2200, WorldDistance.METRES), new WorldDistance(0, WorldDistance.METRES)));
		myStat.setSpeed(new WorldSpeed(80, WorldSpeed.M_sec));
		hisStat.setSpeed(new WorldSpeed(22, WorldSpeed.M_sec));
		hisStat.setCourse(330);
		myStat.setLocation(locA);
		hisStat.setLocation(locB);

		SimpleDemandedStatus res = calculateInterceptCourseFor(myStat, hisStat, 1200);
		assertEquals(255.90, res.getCourse(), 0.1, "wrong demanded status calculated");

		// also try it with an unachievable intercept
		myStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
		res = calculateInterceptCourseFor(myStat, hisStat, 1200);
		assertNull(res, "shouldn't have generated demanded state");
	}

	@Test
	public void testWorking() {
		// ok, set up the participants
		final Surface searcher = new Surface(12);
		searcher.setName("Searcher");
		searcher.setCategory(
				new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE));
		searcher.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
		final Surface ffta = new Surface(13);
		ffta.setName("FFTA");
		ffta.setCategory(new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE));
		ffta.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

		// states
		final Status statA = new Status(12, 12);
		statA.setLocation(SupportTesting.createLocation(0, 0));
		statA.getLocation().setDepth(-400);
		statA.setSpeed(new WorldSpeed(10, WorldSpeed.M_sec));

		final Status statB = new Status(12, 12);
		statB.setLocation(SupportTesting.createLocation(1000, 1000));
		statB.setSpeed(new WorldSpeed(5, WorldSpeed.M_sec));

		searcher.setStatus(statA);
		ffta.setStatus(statB);

		// behaviours
		final Waterfall wA = new Waterfall("searcher behaviour");
		final TargetType theTargetType = new TargetType();
		theTargetType.addTargetType(Category.Force.RED);
		final Intercept iA = new Intercept(theTargetType, false) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public DemandedStatus decide(final Status status, final MovementCharacteristics chars,
					final DemandedStatus demStatus, final DetectionList detections,
					final ScenarioActivityMonitor monitor, final long time) {
				final DemandedStatus res = super.decide(status, chars, demStatus, detections, monitor, time); // To
																												// change
																												// body
																												// of
																												// overridden
				// methods use File | Settings | File
				// Templates.
				_demStat = res;
				return res;
			}
		};
		wA.insertAtFoot(iA);
		searcher.setDecisionModel(wA);

		final Waterfall wB = new Waterfall("su behaviour");
		final UserControl userBehaviour = new UserControl(330, new WorldSpeed(3, WorldSpeed.Kts),
				new WorldDistance(0, WorldDistance.METRES));
		wB.insertAtFoot(userBehaviour);
		ffta.setDecisionModel(wB);

		// sensors
		final OpticLookupSensor optic = new OpticLookupSensor(12, 1.0, 1000, 2, 0.8,
				new Duration(2, Duration.SECONDS), 0.6, new Duration(1, Duration.SECONDS));
		searcher.getSensorFit().add(optic);

		// and the scenario
		final CoreScenario cs = new CoreScenario() {
			/**
			 * Move the scenario through a single step
			 */
			@Override
			public void step() {
				super.step();
				// SimpleDemandedStatus sds = (SimpleDemandedStatus)
				// searcher.getDemandedStatus();
				// System.out.println("searcher course:" +
				// searcher.getStatus().getCourse() + " dem:" + sds.getCourse() +
				// " tgt:" + ffta.getStatus().getCourse());
			}
		};
		cs.setSeed(new Integer(12300));
		cs.addParticipant(searcher.getId(), searcher);
		cs.addParticipant(ffta.getId(), ffta);
		cs.setScenarioStepTime(new Duration(5, Duration.SECONDS));
		cs.setTime(0);
		final SimpleEnvironment simpleEnv = new SimpleEnvironment(EnvironmentType.CLEAR, 1,
				EnvironmentType.DAYLIGHT);
		cs.setEnvironment(simpleEnv);

		// start with the sensor off
		optic.setWorking(false);

		// check it's working
		cs.step();

		// ok. with our new scenario stepping, it fires itself once to represent
		// zero tome.
		assertEquals(0, cs.getTime(), 0, "we haven't performed a step at zero time");

		// and now move forward a little
		cs.step();
		assertEquals(5000, cs.getTime(), 0, "we haven't moved forward");

		// check intercept didn't produce anything
		assertNull(_demStat, "we shouldn't have produced dem status - not in contact");

		optic.setWorking(true);

		// ok, move forward with working sensor
		cs.step();

		// detection comes after decision, so we need another cycle for the cuts
		// to get to the decision model

		// ok, move forward with working sensor
		cs.step();

		assertEquals(1, searcher.getNewDetections().size(), 0, "we haven't detected target");
		// assertNotNull("we haven't received a new demanded status", _demStat);
		// assertEquals(
		// "we haven't reported properly",
		// "searcher behaviour:Intercept:Switching to new intercept course on
		// target:FFTA",
		// searcher.getActivity());

		// ok, move forward a couple more (until we're on the demanded course
		cs.step();
		cs.step();
		cs.step();
		cs.step();

		// surely we're on course now.

		cs.step();
		cs.step();
		cs.step();
		cs.step();
		cs.step();

		assertEquals(1, searcher.getNewDetections().size(), 0, "we haven't detected target");
		assertNotNull(_demStat, "we haven't received a new demanded status");
		assertEquals("searcher behaviour:Intercept:Continuing to intercept:FFTA", searcher.getActivity(), "we still haven't reported properly");

		// double oldDemCourse = ((SimpleDemandedStatus)
		// searcher.getDemandedStatus()).getCourse();

		// ok, change the target course & see if we have to change intercept
		// course
		userBehaviour.setCourse(66);
		ffta.getStatus().setCourse(66);

		cs.step();
		// double newDemCourse = ((SimpleDemandedStatus)
		// searcher.getDemandedStatus()).getCourse();
		cs.step();

		assertEquals(1, searcher.getNewDetections().size(), 0, "we haven't detected target");
		assertNotNull(_demStat, "we haven't received a new demanded status");
		assertEquals("searcher behaviour:Intercept:Continuing to intercept:FFTA", searcher.getActivity(), "we haven't reported properly");

		// now check we don't try to intercept target travelling too fast
		searcher.getStatus().setSpeed(new WorldSpeed(2, WorldSpeed.M_sec));

		cs.step();
		cs.step();

		assertEquals(1, searcher.getNewDetections().size(), 0, "we haven't detected target");
		// assertNull("we received a new demanded status", _demStat);
		// assertEquals("we haven't reported properly", "inactive", searcher
		// .getActivity());
		//
		// // ok, allow speed changes and then see if try to intercept
		// iA.setSpeedChangeAllowed(true);
		//
		// cs.step();
		// cs.step();
		//
		// assertEquals("we haven't detected target", 1,
		// searcher.getNewDetections()
		// .size(), 0);
		// assertNotNull("we haven't received a new demanded status", _demStat);
		//
		// // give us back a proper speed
		// // now check we don't try to intercept target travelling too fast
		// searcher.getStatus().setSpeed(new WorldSpeed(18, WorldSpeed.M_sec));
		//
		// for (int i = 0; i < 30; i++)
		// {
		// cs.step();
		// }
		// for (int i = 0; i < 30; i++)
		// {
		// cs.step();
		// }

	}

}
