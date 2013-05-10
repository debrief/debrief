package com.planetmayo.debrief.satc.model.manager;

import java.util.List;

import com.planetmayo.debrief.satc.model.contributions.ContributionBuilder;

public interface IContributionsManager
{
	
	List<ContributionBuilder> getAvailableContributions();
}
