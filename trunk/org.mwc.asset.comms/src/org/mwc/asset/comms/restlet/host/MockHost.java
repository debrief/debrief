/**
 * 
 */
package org.mwc.asset.comms.restlet.host;

import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.mwc.asset.comms.restlet.data.Participant;
import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.Sensor;

import ASSET.ScenarioType;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;

public class MockHost extends BaseHost
{
	@Override
	public Vector<Scenario> getScenarios()
	{
		Vector<Scenario> res = new Vector<Scenario>();
		res.add(new Scenario("Scott", 434));
		res.add(new Scenario("Scott", 22));
		res.add(new Scenario("Scott", 33));
		res.add(new Scenario("Scott", 11));

		return res;
	}

	public ScenarioType getScenario(int scenarioId){
		return null;
	}
	
	@Override
	public DemandedStatus getDemandedStatus(int scenario, int participant)
	{
		DemandedStatus _thisD;
		Status status = new Status(12, 34);
		{
			_thisD = new SimpleDemandedStatus(participant, status);
		}
		return _thisD;
	}

	@Override
	public void setDemandedStatus(int scenario, int participant,
			DemandedStatus demState)
	{
		System.out.println("in scenario:" + scenario + " participant:"
				+ participant + "new state is:" + demState);
	}



	@Override
	public List<Participant> getParticipantsFor(int scenarioId)
	{
		List<Participant> theParts = new Vector<Participant>();
		Category theCat = new Category(Category.Force.BLUE, Category.Environment.SURFACE,
				Category.Type.FRIGATE);
		theParts.add(new Participant("aba", 12, theCat));
		theParts.add(new Participant("BBB", 31, new Category(Category.Force.RED, Category.Environment.SURFACE,
				Category.Type.FRIGATE)));
		theParts.add(new Participant("CCC", 15, new Category(Category.Force.GREEN, Category.Environment.SURFACE,
				Category.Type.FRIGATE)));
		theParts.add(new Participant("ddd", 18, new Category(Category.Force.BLUE, Category.Environment.AIRBORNE,
				Category.Type.FRIGATE)));
		return theParts;
	}

	@Override
	public void deleteParticipantDecisionListener(int scenarioId,
			int participantId, int theId)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int newParticipantDecisionListener(int scenarioId, int participantId,
			URL listener)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Sensor> getSensorsFor(int scenarioId, int participantId)
	{
		// TODO Auto-generated method stub
		return null;
	}

}