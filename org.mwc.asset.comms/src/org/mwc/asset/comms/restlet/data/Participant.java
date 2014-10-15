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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.asset.comms.restlet.data;

import ASSET.ParticipantType;
import ASSET.Participants.Category;

public class Participant
{
	final private Category _category;
	final private Integer _id;
	final private String _name;

	public Participant(final ParticipantType thisP)
	{
		this(thisP.getName(), thisP.getId(), thisP.getCategory());
	}

	public Participant(final String name, final Integer id,
			final Category category)
	{
		_name = name;
		_id = id;
		_category = category;
	}

	public Category getCategory()
	{
		return _category;
	}

	public Integer getId()
	{
		return _id;
	}

	public String getName()
	{
		return _name;
	}

}
