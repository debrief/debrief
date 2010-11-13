/**
 * 
 */
package org.mwc.asset.comms.restlet.host;

import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import org.mwc.asset.comms.restlet.data.Scenario;

import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;

class MockHost implements ASSETHost
{
	HashMap<Integer, URL> _scenarioListeners = new HashMap<Integer, URL>();
	int _scenarioCounter = 1;
	HashMap<String, URL> _participantListeners = new HashMap<String, URL>();
	int _participantCounter = 1;

	@Override
	public void deleteScenarioListener(int scenario, int listenerId)
	{
		_scenarioListeners.remove(listenerId);
	}

	@Override
	public int newScenarioListener(int scenario, URL url)
	{
		_scenarioListeners.put(scenario, url);
		return _scenarioCounter++;
	}

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
	}

	@Override
	public int newParticipantListener(int scenario, int participant, URL url)
	{
		_participantListeners.put(scenario + "-" + participant, url);
		return _participantCounter++;
	}

}