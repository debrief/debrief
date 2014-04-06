package com.planetmayo.debrief.satc.model.generator;

import java.beans.PropertyChangeListener;
import java.util.SortedSet;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

/**
 * interface to manage contributions and perfom actions on contributions set  
 *
 */
public interface IContributions extends Iterable<BaseContribution>
{
	
	/**
	 * add contribution which will be used 
	 */	
	void addContribution(BaseContribution contribution);
	
	/**
	 * remove contribution 
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
	 * removes all contributions
	 */
	void clear();
	
	/**
	 * returns contributions set 
	 */
	SortedSet<BaseContribution> getContributions();
	
	/**
	 * returns contribution which should be processed after specified one  
	 */
	BaseContribution nextContribution(BaseContribution current);
	
	/**
	 * returns how many contributions do we have now 
	 */
	int size();
}
