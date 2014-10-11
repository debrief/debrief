/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ASSET.Participants.Category;

/**
 * The server side implementation of the Restlet resource.
 */
public class ParticipantServerResource extends ServerResource implements
		ParticipantResource
{

	private static volatile Participant _scenario = new Participant("Scott", 5,
			new Category(Category.Force.BLUE, Category.Environment.SURFACE,
					Category.Type.FRIGATE));

	@Get
	public Participant retrieve()
	{
		// do we have an id?
		final Object theS = super.getRequestAttributes().get("scenario");
		final Object theP = super.getRequestAttributes().get("participant");
		if (theS != null)
		{
			System.out.println("scen is:" + theS + " part is:" + theP);
		}
		return _scenario;
	}

}