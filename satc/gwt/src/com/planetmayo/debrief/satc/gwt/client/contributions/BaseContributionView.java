package com.planetmayo.debrief.satc.gwt.client.contributions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.planetmayo.debrief.satc.gwt.client.Gwt;
import com.planetmayo.debrief.satc.gwt.client.event.CollapseDisclosurePanelsEvent;
import com.planetmayo.debrief.satc.gwt.client.event.CollapseDisclosurePanelsHandler;
import com.planetmayo.debrief.satc.gwt.client.ui.ContributionPanelHeader;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;

abstract public class BaseContributionView extends Composite implements
		ContributionView, PropertyChangeListener, CollapseDisclosurePanelsHandler
{

	@UiField
	public ContributionPanelHeader header;
	
	@UiField DisclosurePanel disclosurePanel;

	public BaseContributionView()
	{
		Gwt.eventBus.addHandler(CollapseDisclosurePanelsEvent.TYPE , this);
	}

	abstract protected BaseContribution getData();

	@Override
	public void initHandlers()
	{
		header.setHandlers(new ValueChangeHandler<Boolean>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event)
			{
				getData().setActive(event.getValue());

			}
		}, new ValueChangeHandler<Integer>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event)
			{
				getData().setWeight(event.getValue());

			}
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0)
	{

		final String attr = arg0.getPropertyName();

		if (attr.equals(BaseContribution.ESTIMATE))
		{
			refreshEstimate();
		}
		else if (attr.equals(BaseContribution.WEIGHT))
			header.setWeightData((Integer) arg0.getNewValue());
		else if (attr.equals(BaseContribution.ACTIVE))
			header.setActiveData((Boolean) arg0.getNewValue());

	}

	@Override
	public void setData(BaseContribution contribution)
	{

		// initialise the UI components
		refreshHardConstraints();
		refreshEstimate();
		
		// and the rest of the hearder
		header.setData(contribution.isActive(), contribution.getWeight());

		// TODO: Akash - this method should only register listeners for the
		// BaseContribution attributes that it knows about. The course-specific
		// ones should be declared in CourseForecastContribution

		contribution.addPropertyChangeListener(
				CourseForecastContribution.MIN_COURSE, this);

		contribution.addPropertyChangeListener(
				CourseForecastContribution.MAX_COURSE, this);

		contribution.addPropertyChangeListener(BaseContribution.ESTIMATE, this);

		contribution.addPropertyChangeListener(BaseContribution.NAME, this);

		contribution.addPropertyChangeListener(BaseContribution.START_DATE, this);

		contribution.addPropertyChangeListener(BaseContribution.FINISH_DATE, this);

		contribution.addPropertyChangeListener(BaseContribution.WEIGHT, this);

		contribution.addPropertyChangeListener(BaseContribution.ACTIVE, this);
	}
	
	@Override
	public void close(CollapseDisclosurePanelsEvent event)
	{
			disclosurePanel.setOpen(false);
	}

	/** an attribute that contributes to the hard constraints has changed, refresh what is displayed
	 * 
	 */
	public final void refreshHardConstraints()
	{
		header.setHardConstraints(getHardConstraintsStr());
	}
	
	/** an attribute that represents the estimate has changed, refresh what is displayed
	 * 
	 */
	public final void refreshEstimate()
	{
		header.setEstimate(getEstimateStr());
	}


	/** retrieve a user-readable description of the hard constraints
	 * 
	 * @return
	 */
	protected String getHardConstraintsStr()
	{
		return "n/a";
	}
	
  /**	retrieve a user-readable description of the estimate
   * 
   * @return
   */
	protected String getEstimateStr()
	{
		return "n/a";
	}
	

}
