package org.mwc.asset.comms.restlet.host;

import org.restlet.resource.ServerResource;

public abstract class ASSETResource extends ServerResource
{

	public ASSETResource()
	{
		super();
	}

	public int getScenarioId()
	{
		String scen = (String) getRequest().getAttributes().get("scenario");
		int scenario = Integer.parseInt(scen);
		return scenario;
	}

	public int getSensorId()
	{
		String sens = (String) getRequest().getAttributes().get("sensor");
		int sensor = Integer.parseInt(sens);
		return sensor;
	}

	public int getParticipantId()
	{
		String part = (String) getRequest().getAttributes().get("participant");
		int participant = Integer.parseInt(part);
		return participant;
	}

}