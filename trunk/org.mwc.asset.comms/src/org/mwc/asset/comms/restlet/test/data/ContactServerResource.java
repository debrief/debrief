package org.mwc.asset.comms.restlet.test.data;

import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * The server side implementation of the Restlet resource.
 */
public class ContactServerResource extends ServerResource implements
		ContactResource
{

	public ContactServerResource()
	{
//		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	//	getVariants().add(new Variant(MediaType.));
	}
	
	private static volatile Contact contact = new Contact("Scott", "Tiger",
			new Address("10 bd Google", null, "20010", "Mountain View", "USA"), 40);

	@Delete
	public void remove()
	{
		contact = null;
	}

	@Get
	public Contact retrieve()
	{
		return contact;
	}

	@Put
	public void store(Contact contact)
	{
		ContactServerResource.contact = contact;
	}

//	@Get
//	public Representation getJson() throws ResourceException
//	{
//		// get Profile object matching the request
//		JSONObject jsonProfile = new JSONObject(contact);
//		getResponse().setStatus(Status.SUCCESS_OK);
//		return new JsonRepresentation(jsonProfile);
//	}
}