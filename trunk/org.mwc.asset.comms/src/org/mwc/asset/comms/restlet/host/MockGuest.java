package org.mwc.asset.comms.restlet.host;

import java.util.Date;

import ASSET.Participants.Status;

public class MockGuest implements ASSETGuest
{

	@Override
	public void newParticipantState(Status newState)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void newScenarioStatus(int scenario, String event, long time, String description)
	{
		Date date = new Date(time);
		System.out.println("at:" + date.toString() + " got:" + description);
	}

}
