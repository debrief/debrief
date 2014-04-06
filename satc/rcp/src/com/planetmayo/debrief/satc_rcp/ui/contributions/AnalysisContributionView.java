package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedAnalysisContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class AnalysisContributionView extends BaseContributionView<BaseContribution>
{

	private String title;
	
	public AnalysisContributionView(Composite parent,	LocationAnalysisContribution contribution, 
			final IContributions contributions)
	{
		super(parent, contribution, contributions);
		title = "Location Analysis - ";
		initUI();
	}
	
	public AnalysisContributionView(Composite parent,	SpeedAnalysisContribution contribution, 
			final IContributions contributions)
	{
		super(parent, contribution, contributions);
		title = "Speed Analysis - ";
		initUI();
	}
	
	public AnalysisContributionView(Composite parent,	CourseAnalysisContribution contribution, 
			final IContributions contributions)
	{
		super(parent, contribution, contributions);
		title = "Course Analysis - ";
		initUI();
	}


	@Override
	protected void bindValues(DataBindingContext context)
	{
		bindCommonHeaderWidgets(context, null, null, null);

	}
	
	@Override
	protected void createBody(Composite parent)
	{
		GridData layoutData = new GridData();
		layoutData.horizontalIndent = 15;
		layoutData.exclude = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.verticalAlignment = SWT.FILL;
		
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		
		bodyGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		bodyGroup.setLayoutData(layoutData);
		bodyGroup.setText("Adjust");
		bodyGroup.setLayout(layout);
		
		UIUtils.createLabel(bodyGroup, "N/A", null);
	}
	
	@Override
	protected void initializeWidgets()
	{
		hardConstraintLabel.setText("n/a");
		estimateLabel.setText("n/a");
	}

	@Override
	protected String getTitlePrefix()
	{
		return title;
	}
}
