package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
	
	@Override
	protected void initUI()
	{
		// do the parent bits now
		super.initUI();
				
		UIUtils.createLabel(bodyGroup, "Speed:", new GridData(70, SWT.DEFAULT));
		UIUtils.createSpacer(bodyGroup, new GridData(95, SWT.DEFAULT));
		
		// add the speed
		Composite speed = new Composite(bodyGroup, SWT.NONE);
		speed.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		speed.setLayout(new GridLayout(9, false));
		UIUtils.createLabel(speed, "Min:", null);
		UIUtils.createSpacer(speed, null);
		Text minSpeed = new Text(speed, SWT.BORDER);
		minSpeed.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		UIUtils.createLabel(speed, "Max:",null);
		UIUtils.createSpacer(speed,null);
		Text maxSpeed = new Text(speed, SWT.BORDER);
		maxSpeed.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		UIUtils.createLabel(speed, "Estimate:",null);
		UIUtils.createSpacer(speed,null);
		Text speedEstimate = new Text(speed, SWT.BORDER);
		speedEstimate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// now add the course
		UIUtils.createLabel(bodyGroup, "Course:", new GridData(70, SWT.DEFAULT));
		UIUtils.createSpacer(bodyGroup, new GridData(95, SWT.DEFAULT));
		
		Composite course = new Composite(bodyGroup, SWT.NONE);
		course.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		course.setLayout(new GridLayout(9, false));
		UIUtils.createLabel(course, "Min:", null);
		UIUtils.createSpacer(course, null);
		Text minCrse = new Text(course, SWT.BORDER);
		minCrse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		UIUtils.createLabel(course, "Max:",null);
		UIUtils.createSpacer(course,null);
		Text maxCrse = new Text(course, SWT.BORDER);
		maxCrse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		UIUtils.createLabel(course, "Estimate:",null);
		UIUtils.createSpacer(course,null);
		Text crseEstimate = new Text(course, SWT.BORDER);
		crseEstimate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
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
