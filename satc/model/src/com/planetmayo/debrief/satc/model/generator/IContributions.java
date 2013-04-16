package com.planetmayo.debrief.satc.model.generator;

import java.beans.PropertyChangeListener;
import java.util.SortedSet;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

public interface IContributions extends Iterable<BaseContribution>
{
	
	/**
	 * add contribution which will be used in constraint phase
	 */	
	void addContribution(BaseContribution contribution);
	
	/**
	 * remove contribution from constraint phase
	 */	
	void removeContribution(BaseContribution contribution);
	
	/**
	 * subscribe to contribution set events
	 */	
	void addContributionsChangedListener(IContributionsChangedListener listener);
	
	/**
	 * unsubscribe from contribution set events
	 */
	void removeContributionsChangedListener(IContributionsChangedListener listener);
	
	/**
	 * add property listener to all contributions 
	 */
	void addPropertyListener(String property, PropertyChangeListener listener);

	/**
	 * remove property listener from all contributions 
	 */	
	void removePropertyListener(String property, PropertyChangeListener listener);
	
	/**
	 * clear contributions set
	 */
	void clear();
	
	/**
	 * get contributions set 
	 */
	SortedSet<BaseContribution> getContributions();
	
	BaseContribution nextContribution(BaseContribution current);
	
	int size();
}
