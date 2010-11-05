package org.mwc.asset.comms.restlet.data;

import java.util.List;
import java.util.Vector;

import org.mwc.asset.comms.restlet.test.data.Contact;
import org.mwc.asset.comms.restlet.test.data.ContactServerResource;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import ASSET.Participants.Category;

/**
 * The server side implementation of the Restlet resource.
 */
public class ParticipantsServerResource extends ServerResource implements
		ParticipantsResource
{
	
	@Get
	public List<Participant> retrieve()
	{
		List<Participant> theParts = new Vector<Participant>();
		theParts.add(new Participant("aaa", 12, new Category(Category.Force.BLUE, Category.Environment.SURFACE,
				Category.Type.FRIGATE)));
		theParts.add(new Participant("BBB", 31, new Category(Category.Force.RED, Category.Environment.SURFACE,
				Category.Type.FRIGATE)));
		theParts.add(new Participant("CCC", 15, new Category(Category.Force.GREEN, Category.Environment.SURFACE,
				Category.Type.FRIGATE)));
		theParts.add(new Participant("ddd", 18, new Category(Category.Force.BLUE, Category.Environment.AIRBORNE,
				Category.Type.FRIGATE)));
		return theParts;
	}
	
	
}