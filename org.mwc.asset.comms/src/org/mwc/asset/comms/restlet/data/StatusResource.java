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
package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Post;

import ASSET.Participants.Status;

/**
 * The resource associated to a contact.
 */
public interface StatusResource
{

	public static class MovedEvent extends AssetEvent
	{
		final public Status _status;

		public MovedEvent(final Status status)
		{
			super(status.getTime());
			_status = status;
		}
	}

	@Post
	public void accept(Status status);
}
