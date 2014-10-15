/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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