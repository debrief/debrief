package org.mwc.asset.comms.restlet.data;

import java.io.Serializable;

import ASSET.ParticipantType;
import ASSET.Participants.Category;

public class Participant implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
