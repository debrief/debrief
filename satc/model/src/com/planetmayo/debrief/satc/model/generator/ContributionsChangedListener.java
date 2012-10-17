package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

public interface ContributionsChangedListener
{
	/**
	 * a contribution has been added
	 * 
	 * @param contribution
	 */
	public void added(BaseContribution contribution);

	/**
	 * a contribution has been removed
	 * 
	 * @param contribution
	 */
	public void removed(BaseContribution contribution);

}
