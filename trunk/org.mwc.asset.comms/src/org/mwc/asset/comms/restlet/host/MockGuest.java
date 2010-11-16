package org.mwc.asset.comms.restlet.host;

import java.util.Date;

import org.mwc.asset.comms.restlet.data.DecisionResource.DecidedEvent;
import org.mwc.asset.comms.restlet.data.DetectionResource.DetectionEvent;

import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;

public class MockGuest implements ASSETGuest
{

	@Override
	public void newParticipantState(int scenarioId, int participantId,
			Status newState)
	{
		Date date = new Date(newState.getTime());
		System.out.println("at:" + date.toString() + " has state:" + newState);
	}

	@Override
	public void newScenarioStatus(long time, String eventName, String description)
	{
		Date date = new Date(time);
		System.out.println("at:" + date.toString() + " got:" + description);
	}

	@Override
	public void newParticipantDecision(int scenarioId, int participantId,
			DecidedEvent decision)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void newParticipantDetection(int scenarioId, int participantId,
			DetectionEvent event)
	{
		// TODO Auto-generated method stub
		
	}

}
