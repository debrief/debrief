/**
 * 
 */
package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.user.client.ui.IsWidget;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

/**
 * @author Akash-Gupta
 * 
 */
public interface ContributionView extends IsWidget
{
	void initHandlers();

	void setData(BaseContribution contribution);
}
