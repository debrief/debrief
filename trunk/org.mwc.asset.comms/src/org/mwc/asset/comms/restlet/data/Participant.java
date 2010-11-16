package org.mwc.asset.comms.restlet.data;

import ASSET.ParticipantType;
import ASSET.Participants.Category;


public class Participant
{
	final private String _name;
	final private Integer _id;
	final private Category _category;
	public Participant(String name, Integer id, Category category)
	{
		_name = name;
		_id = id;
		_category = category;
	}
	public Participant(ParticipantType thisP)
	{
		this(thisP.getName(), thisP.getId(), thisP.getCategory());
	}
	public String getName()
	{
		return _name;
	}
	public Integer getId()
	{
		return _id;
	}
	public Category getCategory()
	{
		return _category;
	}

}
