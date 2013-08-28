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
		final String scen = (String) getRequest().getAttributes().get("scenario");
		final int scenario = Integer.parseInt(scen);
		return scenario;
	}

	public int getSensorId()
	{
		final String sens = (String) getRequest().getAttributes().get("sensor");
		final int sensor = Integer.parseInt(sens);
		return sensor;
	}

	public int getParticipantId()
	{
		final String part = (String) getRequest().getAttributes().get("participant");
		final int participant = Integer.parseInt(part);
		return participant;
	}

}