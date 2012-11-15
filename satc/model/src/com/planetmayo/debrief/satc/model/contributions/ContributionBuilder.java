package com.planetmayo.debrief.satc.model.contributions;

public abstract class ContributionBuilder
{
	private String _description;
	
	public ContributionBuilder(String description) 
	{
		_description = description;
	}
	
	public String getDescription() 
	{
		return _description;
	}
	
	public abstract BaseContribution create();
}
