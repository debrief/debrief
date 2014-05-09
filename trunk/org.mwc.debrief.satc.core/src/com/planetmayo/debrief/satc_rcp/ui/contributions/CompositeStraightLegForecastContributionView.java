package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.planetmayo.debrief.satc.model.contributions.CompositeStraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class CompositeStraightLegForecastContributionView extends
		BaseContributionView<CompositeStraightLegForecastContribution>
{

	public CompositeStraightLegForecastContributionView(Composite parent,
			CompositeStraightLegForecastContribution contribution,
			IContributions contributions)
	{
		super(parent, contribution, contributions);
		initUI();
	}

	
	
	@SuppressWarnings("unused")
	@Override
	protected void initUI()
	{
		// do the parent bits now
		super.initUI();
		
		Group others = new Group(bodyGroup, SWT.SHADOW_ETCHED_IN);
		others.setLayout(new FillLayout(SWT.VERTICAL));
		others.setText("Others");
		
		// add the speed controls
		RowLayout speedLayout = new RowLayout();
		Group speed = new Group(others, SWT.SHADOW_ETCHED_IN);
		speed.setLayout(speedLayout);
		speed.setText("Speed");
		UIUtils.createLabel(speed, "Min:", null);
		UIUtils.createSpacer(speed, null);
		Text minSpeed = new Text(speed, SWT.BORDER);
		UIUtils.createLabel(speed, "Max:",null);
		UIUtils.createSpacer(speed,null);
		Text maxSpeed = new Text(speed, SWT.BORDER);
		UIUtils.createLabel(speed, "Estimate:",null);
		UIUtils.createSpacer(speed,null);
		Text speedEstimate = new Text(speed, SWT.BORDER);

		// now add the course
		RowLayout layout = new RowLayout();
		Group course = new Group(others, SWT.SHADOW_ETCHED_IN);
		course.setLayout(layout);
		course.setText("Course");
		UIUtils.createLabel(course, "Min:", null);
		UIUtils.createSpacer(course, null);
		Text minCrse = new Text(course, SWT.BORDER);
		UIUtils.createLabel(course, "Max:",null);
		UIUtils.createSpacer(course,null);
		Text maxCrse = new Text(course, SWT.BORDER);
		UIUtils.createLabel(course, "Estimate:",null);
		UIUtils.createSpacer(course,null);
		Text crseEstimate = new Text(course, SWT.BORDER);
	}



	@Override
	protected void createLimitAndEstimateSliders()
	{
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Straight Leg Forecast - ";
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		bindCommonHeaderWidgets(context, null, null, null);
		bindCommonDates(context);
	}

	@Override
	protected void initializeWidgets()
	{
		hardConstraintLabel.setText("n/a");
		estimateLabel.setText("n/a");
	}
}
