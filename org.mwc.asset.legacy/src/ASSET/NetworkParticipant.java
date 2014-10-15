/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET;

import ASSET.Participants.Category;
import ASSET.Participants.Status;

public interface NetworkParticipant
{

	/**
	 * the status of this participant
	 */
	public abstract Status getStatus();

	/**
	 * the name of this participant
	 */
	public abstract String getName();

	/**
	 * get the id of this participant
	 */
	public abstract int getId();

	/**
	 * return what this participant is currently doing
	 */
	public abstract String getActivity();

	/**
	 * return the category of the target
	 */
	public abstract Category getCategory();

}