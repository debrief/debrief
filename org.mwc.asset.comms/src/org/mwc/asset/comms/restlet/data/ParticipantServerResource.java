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