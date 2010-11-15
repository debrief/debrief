/**
 * 
 */
package org.mwc.asset.comms.restlet.host;

import java.net.URL;
import java.util.Vector;

import org.mwc.asset.comms.restlet.data.Scenario;

import ASSET.Models.Movement.SimpleDemandedStatus;
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
	public void deleteParticipantListener(int scenarioId, int listenerId)
	{
		_participantListeners.remove(_participantCounter);
	}

	@Override
	public int newParticipantListener(int scenario, int participant, URL url)
	{
		_participantListeners.put(_participantCounter++, url);
		return _participantCounter;
	}

}