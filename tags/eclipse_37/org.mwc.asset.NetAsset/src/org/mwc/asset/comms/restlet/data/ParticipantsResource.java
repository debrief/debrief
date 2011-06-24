package org.mwc.asset.comms.restlet.data;

import java.io.Serializable;
import java.util.Vector;

import org.restlet.resource.Get;

/**
 * The resource associated to a contact.
 */
public interface ParticipantsResource
{
	
	public static class ParticipantsList extends Vector<Participant> implements Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}

	@Get
	public ParticipantsList retrieve();
}
