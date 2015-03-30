/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.converters.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.UnitConverter;

public class BearingMeasurementContributionView extends BaseContributionView<BearingMeasurementContribution>
{
	private Scale errorSlider;
	private Label errorLabel;
	private Button errorActiveCheckbox;
	private Button runSliceOsBtn;
	private Button runSliceTgtBtn;

	public BearingMeasurementContributionView(Composite parent,
			BearingMeasurementContribution contribution, final IContributions contributions)
	{
		super(parent, contribution, contributions);
		initUI();
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		PrefixSuffixLabelConverter labelConverter = new PrefixSuffixLabelConverter(Object.class, "+/- ", " degs");
		labelConverter.setNestedUnitConverter(UnitConverter.ANGLE_DEG.getModelToUI());
		IObservableValue errorValue = BeansObservables.observeValue(
				contribution, BearingMeasurementContribution.BEARING_ERROR);
		IObservableValue observationNumberValue = BeansObservables.observeValue(
				contribution, BearingMeasurementContribution.OBSERVATIONS_NUMBER);		
		bindCommonHeaderWidgets(context, errorValue, observationNumberValue, 
				new PrefixSuffixLabelConverter(Object.class, " Measurements"), labelConverter);
		bindCommonDates(context);

		bindSliderLabelCheckbox(context, errorValue, errorSlider, errorLabel, errorActiveCheckbox,
				labelConverter, new BooleanToNullConverter<Double>(0d), UnitConverter.ANGLE_DEG);
		
		// connect up the MDA toggle (note - we've switched from a toggle to a button
//		IObservableValue autoValue = BeansObservables.observeValue(contribution,
//				BearingMeasurementContribution.RUN_MDA);
//		IObservableValue autoButton = WidgetProperties.selection().observe(
//				runMDACheckbox);
//		context.bindValue(autoButton, autoValue);
		
		// connect the checkbox to the run MDA event
		runSliceOsBtn.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{

				// ok - run the MDA generator
				contribution.sliceOwnship(getContributions());
				
				// ok, done - enable the second btn
				runSliceTgtBtn.setEnabled(true);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		// connect the checkbox to the run MDA event
		runSliceTgtBtn.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// ok - run the MDA generator
				contribution.runMDA(getContributions());
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		
	}
	
	@Override
	protected void createLimitAndEstimateSliders()
	{
		UIUtils.createLabel(bodyGroup, "Error: ", new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		Composite group = new Composite(bodyGroup, SWT.NONE);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		group.setLayout(UIUtils.createGridLayoutWithoutMargins(2, false));
		errorActiveCheckbox = new Button(group, SWT.CHECK);
		errorLabel = UIUtils.createSpacer(group, new GridData(GridData.FILL_HORIZONTAL));
		
		errorSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		errorSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// and now the MDA components
		UIUtils.createLabel(bodyGroup, "MDA: ", new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		Composite group2 = new Composite(bodyGroup, SWT.NONE);
		group2.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		group2.setLayout(UIUtils.createGridLayoutWithoutMargins(5, false));
		runSliceOsBtn = new Button(group2, SWT.PUSH);
		runSliceOsBtn.setText("1. Slice O/S legs");
		runSliceTgtBtn = new Button(group2, SWT.PUSH);
		runSliceTgtBtn.setText("2. Slice Tgt legs");
		UIUtils.createLabel(bodyGroup, "Auto-detect target manoeuvres", new GridData(GridData.HORIZONTAL_ALIGN_FILL));
	}
	
	@Override
	protected void initializeWidgets()
	{
		startDate.setEnabled(false);
		startTime.setEnabled(false);
		endDate.setEnabled(false);
		endTime.setEnabled(false);
		
		runSliceOsBtn.setEnabled(true);
		runSliceTgtBtn.setEnabled(false);
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Bearing Measurement - ";
	}
}
