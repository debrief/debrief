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

import java.text.DecimalFormat;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.planetmayo.debrief.satc.model.contributions.CoreMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;

public class FrequencyMeasurementContributionView extends BaseContributionView<FrequencyMeasurementContribution>
{

	private Text speedSoundText;
	private Text fNoughtText;

	public FrequencyMeasurementContributionView(Composite parent, FrequencyMeasurementContribution contribution,
			IContributions contributions)
	{
		super(parent, contribution, contributions);
		initUI();
	}

	@Override
	protected void initUI()
	{
		GridLayout layout = UIUtils.createGridLayoutWithoutMargins(1, false);
		layout.verticalSpacing = 0;
		mainGroup = new Group(controlParent, SWT.SHADOW_ETCHED_IN);
		mainGroup.setLayout(layout);
		mainGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		createHeader(mainGroup);
		createBody(mainGroup);

		titleChangeListener =
				attachTitleChangeListener(contribution, getTitlePrefix());
		initializeWidgets();
		
		// add sound speed
		UIUtils.createLabel(bodyGroup, "Sound Speed (m/s):", new GridData(130, SWT.DEFAULT));
		UIUtils.createSpacer(bodyGroup, new GridData(45, SWT.DEFAULT));
		
		// add the speed
		Composite speed = new Composite(bodyGroup, SWT.NONE);
		speed.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		speed.setLayout(new GridLayout(4, false));
		
		speedSoundText = new Text(speed, SWT.BORDER|SWT.TRAIL);
		GridData gd = new GridData(100, SWT.DEFAULT);
		speedSoundText.setLayoutData(gd);
		speedSoundText.addVerifyListener(new DoubleVerifier());
		
		// add FNought
		gd = new GridData(90, SWT.DEFAULT);
		UIUtils.createLabel(speed, "F0 (Hz):", gd);
		
		fNoughtText = new Text(speed, SWT.BORDER|SWT.TRAIL);
		gd = new GridData(100,SWT.DEFAULT);
		fNoughtText.setLayoutData(gd);
		fNoughtText.addVerifyListener(new DoubleVerifier());
		
		// and tie the values together
		context = new DataBindingContext();
		bindValues(context);
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{	
		IObservableValue observationNumberValue = BeansObservables.observeValue(
				contribution, CoreMeasurementContribution.OBSERVATIONS_NUMBER);		
		bindCommonHeaderWidgets(context, null, observationNumberValue, 
				new PrefixSuffixLabelConverter(Object.class, " Measurements"), null);
		bindCommonDates(context);

		// bind SpeedSound
		bindSpeed(context);
		
		// bind FNought
		bindFNought(context);
	}

	private void bindFNought(DataBindingContext context)
	{
		IObservableValue fNoughtValue = BeansObservables.observeValue(contribution,
				FrequencyMeasurementContribution.F_NOUGHT);
		ISWTObservableValue fNoughtTextValue = WidgetProperties.text(SWT.FocusOut)
				.observe(fNoughtText);
		
		// converter rounding the value to 1 decimal place
		IConverter modelToUI = new RoundMUIConverter();
		IConverter uiToModel = new RoundUIMConverter();

		context.bindValue(fNoughtTextValue, fNoughtValue,
				UIUtils.converterStrategy(uiToModel),
				UIUtils.converterStrategy(modelToUI));
	}

	private void bindSpeed(DataBindingContext context)
	{
		IObservableValue soundValue = BeansObservables.observeValue(contribution,
				FrequencyMeasurementContribution.SOUND_SPEED);
		ISWTObservableValue soundTextValue = WidgetProperties.text(SWT.FocusOut)
				.observe(speedSoundText);
		
		// converter rounding the value to 1 decimal place
		IConverter modelToUI = new RoundMUIConverter();
		IConverter uiToModel = new RoundUIMConverter();
		
		context.bindValue(soundTextValue, soundValue,
				UIUtils.converterStrategy(uiToModel),
				UIUtils.converterStrategy(modelToUI));
	}
	
	@Override
	protected void initializeWidgets()
	{
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Frequency Measurement - ";
	}

	@Override
	protected void bindMaxMinEstimate(IObservableValue estimate,
			IObservableValue min, IObservableValue max)
	{
		
	}

	@Override
	protected void createLimitAndEstimateSliders()
	{
	}
	
	private class RoundMUIConverter implements IConverter
	{
		final DecimalFormat df = new DecimalFormat("0.0");
		
		@Override
		public Object getToType()
		{
			return String.class;
		}
	
		@Override
		public Object getFromType()
		{
			return Double.class;
		}
	
		@Override
		public Object convert(Object fromObject)
		{
			if (fromObject == null)
			{
				return null;
			}
			Double kts = (Double) fromObject;
			String res = df.format(kts);
			return res;
		}
	}
	
	private class RoundUIMConverter implements IConverter
	{
		@Override
		public Object getToType()
		{
			return Double.class;
		}
		
		@Override
		public Object getFromType()
		{
			return String.class;
		}
		
		@Override
		public Object convert(Object fromObject)
		{
			String s = (String) fromObject;
			if (fromObject == null || s.isEmpty()) {
				return null;
			}
			
			return new Double((String)fromObject);
		}

	}
	
	private class DoubleVerifier implements VerifyListener
	{

		@Override
		public void verifyText(VerifyEvent e)
		{
			if (e.text.isEmpty())
			{
				return;
			}
			String s = e.text;
			char[] chars = new char[s.length()];
			s.getChars(0, chars.length, chars, 0);
			for (int i = 0; i < chars.length; i++)
			{
				if (!('0' <= chars[i] && chars[i] <= '9'))
				{
					if (chars[i] != '.')
					{
						Display.getCurrent().beep();
						e.doit = false;
						return;
					}
				}
			}
			if (e.widget instanceof Text)
			{
				Text text = (Text) e.widget;
				final String oldS = text.getText();
				final String newS = oldS.substring(0, e.start) + e.text
						+ oldS.substring(e.end);
				int index = newS.indexOf(".");
				if (index >= 0)
				{
					int i2 = newS.indexOf(".", index + 1);
					if (i2 >= 0)
					{
						Display.getCurrent().beep();
						e.doit = false;
						return;
					}
				}
				
			}
		}

	}
}
