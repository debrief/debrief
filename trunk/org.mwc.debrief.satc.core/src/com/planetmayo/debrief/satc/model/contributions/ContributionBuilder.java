package com.planetmayo.debrief.satc.model.contributions;

public abstract class ContributionBuilder
{
	private String description;

	public ContributionBuilder(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return this.description;
	}

	public abstract BaseContribution create();
}
