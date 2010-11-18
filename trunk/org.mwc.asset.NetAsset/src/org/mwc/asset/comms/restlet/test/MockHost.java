package org.mwc.asset.comms.restlet.test;

import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.Scenario.ScenarioList;
import org.mwc.asset.comms.restlet.host.BaseHost;

import ASSET.ScenarioType;
import ASSET.Models.Decision.Movement.Wander;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

public class MockHost extends BaseHost
{
	CoreScenario _myScenario;
	private static int _ctr = 0;

	public static int SCENARIO_ID = 512;
	public static int PLAT1_ID = 33;
	public static int PLAT2_ID = 44;

	public MockHost()
	{
		_myScenario = new CoreScenario();
		_myScenario.setName("Scenario_" + ++_ctr);
		_myScenario.setScenarioStepTime(60 * 1000);
		_myScenario.setStepTime(5 * 1000);

		Wander wander = new Wander("just wander around");
		wander.setOrigin(new WorldLocation(0.05, 0.04, 0));
		wander.setRange(new WorldDistance(12, WorldDistance.NM));
		Wander wander2 = new Wander("just wander around 3");
		wander2.setOrigin(new WorldLocation(0.04, 0.05, 0));
		wander2.setRange(new WorldDistance(12, WorldDistance.NM));
		
		Status curStat = new Status(12, 0);
		curStat.setLocation(new WorldLocation(0, 0, 0));
		DemandedStatus demStat = null;
		Surface _platform1 = new Surface(PLAT1_ID, curStat, demStat, "Platform One");
		_platform1.setDecisionModel(wander);
		_platform1.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

		Status otherStat = new Status(curStat);
		otherStat.setLocation(new WorldLocation(0.1, 0.2, 0));
		Surface _platform2 = new Surface(PLAT2_ID, otherStat, demStat,
				"Platform One");
		_platform2.setDecisionModel(wander2);
		_platform2.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

		_myScenario.addParticipant(_platform1.getId(), _platform1);
		_myScenario.addParticipant(_platform2.getId(), _platform2);
	}


	@Override
	public ScenarioType getScenario(int scenarioId)
	{
		return _myScenario;
	}

	@Override
	public ScenarioList getScenarios()
	{
		ScenarioList res = new ScenarioList();
		res.add(new Scenario(_myScenario.getName(), SCENARIO_ID));
		return res;
	}

}
