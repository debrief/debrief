/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

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

public class AnalysisContributionView extends BaseContributionView<BaseContribution> {

	private final String title;

	public AnalysisContributionView(final Composite parent, final CourseAnalysisContribution contribution,
			final IContributions contributions) {
		super(parent, contribution, contributions);
		title = "Course Analysis - ";
		initUI();
	}

	public AnalysisContributionView(final Composite parent, final LocationAnalysisContribution contribution,
			final IContributions contributions) {
		super(parent, contribution, contributions);
		title = "Location Analysis - ";
		initUI();
	}

	public AnalysisContributionView(final Composite parent, final SpeedAnalysisContribution contribution,
			final IContributions contributions) {
		super(parent, contribution, contributions);
		title = "Speed Analysis - ";
		initUI();
	}

	@Override
	protected void bindValues(final DataBindingContext context) {
		bindCommonHeaderWidgets(context, null, null, null);

	}

	@Override
	protected void createBody(final Composite parent) {
		final GridData layoutData = new GridData();
		layoutData.horizontalIndent = 15;
		layoutData.exclude = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.verticalAlignment = SWT.FILL;

		final RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.marginWidth = 5;
		layout.marginHeight = 5;

		bodyGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		bodyGroup.setLayoutData(layoutData);
		bodyGroup.setText("Adjust");
		bodyGroup.setLayout(layout);

		UIUtils.createLabel(bodyGroup, "N/A", null);
	}

	@Override
	protected String getTitlePrefix() {
		return title;
	}

	@Override
	protected void initializeWidgets() {
		hardConstraintLabel.setText("n/a");
		estimateLabel.setText("n/a");
	}
}
