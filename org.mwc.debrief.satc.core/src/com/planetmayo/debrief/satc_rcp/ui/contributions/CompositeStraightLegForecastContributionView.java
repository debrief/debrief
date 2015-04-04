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

import java.math.BigDecimal;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CompositeStraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class CompositeStraightLegForecastContributionView extends
		BaseContributionView<CompositeStraightLegForecastContribution>
{

	private static final String BOTH_MAX_MIN_VALUES_MUST_BE_PRESENT = "Both max/min values must be present";
	private Text minSpeed;
	private Text maxSpeed;
	private Text speedEstimate;
	private Text minCrse;
	private Text maxCrse;
	private Text crseEstimate;
	
	private ControlDecoration cdMinSpeed;
	private ControlDecoration cdMaxSpeed;
	private ControlDecoration cdSpeedEstimate;
	private ControlDecoration cdMinCrse;
	private ControlDecoration cdMaxCrse;
	private ControlDecoration cdCrseEstimate;

	public CompositeStraightLegForecastContributionView(Composite parent,
			CompositeStraightLegForecastContribution contribution,
			IContributions contributions)
	{
		super(parent, contribution, contributions);
		initUI();
	}

	@Override
	public void dispose()
	{
		super.dispose();
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
		
				
		UIUtils.createLabel(bodyGroup, "Speed: (0-40kts)", new GridData(120, SWT.DEFAULT));
		UIUtils.createSpacer(bodyGroup, new GridData(30, SWT.DEFAULT));
		
		// add the speed
		Composite speed = new Composite(bodyGroup, SWT.NONE);
		speed.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		speed.setLayout(new GridLayout(9, false));
		
		minSpeed = createSpeed(speed, "Min:");
		cdMinSpeed = createDecoration(minSpeed);
		maxSpeed = createSpeed(speed, "Max:");
		cdMaxSpeed = createDecoration(maxSpeed);
		speedEstimate = createSpeed(speed, "Estimate:");
		cdSpeedEstimate = createDecoration(speedEstimate);

		// now add the course
		UIUtils.createLabel(bodyGroup, "Course: (0-360°)", new GridData(120, SWT.DEFAULT));
		UIUtils.createSpacer(bodyGroup, new GridData(30, SWT.DEFAULT));
		
		Composite course = new Composite(bodyGroup, SWT.NONE);
		course.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		course.setLayout(new GridLayout(9, false));
		
		minCrse = createCourse(course, "Min:");
		cdMinCrse = createDecoration(minCrse);
		maxCrse = createCourse(course, "Max:");
		cdMaxCrse = createDecoration(maxCrse);
		crseEstimate = createCourse(course, "Estimate:");
		cdCrseEstimate = createDecoration(crseEstimate);
		
		context = new DataBindingContext();
		bindValues(context);
	}

	private ControlDecoration createDecoration(Text text)
	{
		ControlDecoration cd = new ControlDecoration(text, SWT.TOP | SWT.LEFT);
		Image image = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
		cd.setImage(image);
		return cd;
	}

	private Text createSpeed(Composite speed, String label)
	{
		UIUtils.createLabel(speed, label, null);
		UIUtils.createSpacer(speed, null);
		Text text = new Text(speed, SWT.BORDER|SWT.TRAIL);
		text.setLayoutData(new GridData(100, SWT.DEFAULT));
		text.addVerifyListener(new SpeedVerifier());
		return text;
	}

	private Text createCourse(Composite course, String label)
	{
		UIUtils.createLabel(course, label, null);
		UIUtils.createSpacer(course, null);
		Text text = new Text(course, SWT.BORDER|SWT.TRAIL);
		text.setLayoutData(new GridData(100, SWT.DEFAULT));
		text.addVerifyListener(new CourseVerifier());
		return text;
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

		IObservableValue estimateCourseValue = BeansObservables.observeValue(
				contribution, CompositeStraightLegForecastContribution.COURSE + "." + BaseContribution.ESTIMATE);
		IObservableValue minCourseValue = BeansObservables.observeValue(
				contribution, CompositeStraightLegForecastContribution.COURSE + "." + CourseForecastContribution.MIN_COURSE);
		IObservableValue maxCourseValue = BeansObservables.observeValue(
				contribution, CompositeStraightLegForecastContribution.COURSE + "." + CourseForecastContribution.MAX_COURSE);		
				
		IObservableValue estimateSpeedValue = BeansObservables.observeValue(
				contribution, CompositeStraightLegForecastContribution.SPEED + "." + BaseContribution.ESTIMATE);
		IObservableValue minSpeedValue = BeansObservables.observeValue(
				contribution, CompositeStraightLegForecastContribution.SPEED + "." + SpeedForecastContribution.MIN_SPEED);
		IObservableValue maxSpeedValue = BeansObservables.observeValue(
				contribution, CompositeStraightLegForecastContribution.SPEED + "." + SpeedForecastContribution.MAX_SPEED);
				
		ISWTObservableValue minSpeedTextValue = bindSpeedValue(context, minSpeed, minSpeedValue);
		
		ISWTObservableValue maxSpeedTextValue = bindSpeedValue(context, maxSpeed, maxSpeedValue);
		
		ISWTObservableValue estimateSpeedTextValue = bindSpeedValue(context, speedEstimate, estimateSpeedValue);
		
		ISWTObservableValue minCourseTextValue = bindCourseValue(context, minCrse, minCourseValue);
		
		ISWTObservableValue maxCourseTextValue = bindCourseValue(context, maxCrse, maxCourseValue);
		
		ISWTObservableValue estimateCourseTextValue = bindCourseValue(context, crseEstimate, estimateCourseValue);
		
		
		ModifyListener modifyListener = new ModifyListener()
		{
			
			@Override
			public void modifyText(ModifyEvent e)
			{
				validate();
			}
		};
		minSpeed.addModifyListener(modifyListener );
		maxSpeed.addModifyListener(modifyListener );
		speedEstimate.addModifyListener(modifyListener );
		minCrse.addModifyListener(modifyListener );
		maxCrse.addModifyListener(modifyListener );
		crseEstimate.addModifyListener(modifyListener );
		
//		ControlDecorationSupport.create(new Validator(
//				minSpeedTextValue, maxSpeedTextValue, estimateSpeedTextValue,
//				"Both max/min values must be present",
//				"Estimate value must be between min and max"), 
//				SWT.LEFT | SWT.TOP);
//		
//		ControlDecorationSupport.create(new Validator(
//				minCourseTextValue, maxCourseTextValue, estimateCourseTextValue,
//				"Both max/min values must be present",
//				"Estimate value must be between min and max"), 
//				SWT.LEFT | SWT.TOP);
		validate();
		
	}

	protected void validate()
	{
		hideDecorations();
		if (speedEstimate.getText().isEmpty())
		{
			if ((minSpeed.getText().isEmpty() && !maxSpeed.getText().isEmpty()))
			{
				cdMinSpeed.setDescriptionText(BOTH_MAX_MIN_VALUES_MUST_BE_PRESENT);
				cdMinSpeed.show();

			}
			if ((maxSpeed.getText().isEmpty() && !minSpeed.getText().isEmpty()))
			{
				cdMaxSpeed.setDescriptionText(BOTH_MAX_MIN_VALUES_MUST_BE_PRESENT);
				cdMaxSpeed.show();
			}
		}
		else
		{
			if (minSpeed.getText().isEmpty())
			{
				cdMinSpeed.setDescriptionText(BOTH_MAX_MIN_VALUES_MUST_BE_PRESENT);
				cdMinSpeed.show();
			}
			if (maxSpeed.getText().isEmpty())
			{
				cdMaxSpeed.setDescriptionText(BOTH_MAX_MIN_VALUES_MUST_BE_PRESENT);
				cdMaxSpeed.show();
			}
		}

		if (crseEstimate.getText().isEmpty())
		{
			if ((minCrse.getText().isEmpty() && !maxCrse.getText().isEmpty()))
			{
				cdMinCrse.setDescriptionText(BOTH_MAX_MIN_VALUES_MUST_BE_PRESENT);
				cdMinCrse.show();
			}
			if ((maxCrse.getText().isEmpty() && !minCrse.getText().isEmpty()))
			{
				cdMaxCrse.setDescriptionText(BOTH_MAX_MIN_VALUES_MUST_BE_PRESENT);
				cdMaxCrse.show();
			}
		}
		else
		{
			if (minCrse.getText().isEmpty())
			{
				cdMinCrse.setDescriptionText(BOTH_MAX_MIN_VALUES_MUST_BE_PRESENT);
				cdMinCrse.show();
			}
			if (maxCrse.getText().isEmpty())
			{
				cdMaxCrse.setDescriptionText(BOTH_MAX_MIN_VALUES_MUST_BE_PRESENT);
				cdMaxCrse.show();
			}
		}
	}

	private void hideDecorations()
	{
		cdMinSpeed.hide();
		cdMaxSpeed.hide();
		cdSpeedEstimate.hide();
		cdMinCrse.hide();
		cdMaxCrse.hide();
		cdCrseEstimate.hide();
	}

	private ISWTObservableValue bindSpeedValue(DataBindingContext context,
			Text speed, IObservableValue observableSpeedValue)
	{
		ISWTObservableValue speedTextValue = WidgetProperties.text(SWT.FocusOut)
				.observe(speed);
		IConverter modelToUI = new ModelToUISpeedConverter();
		
		IConverter uiToModel = new UIToModelSpeedConverter();
		
		context.bindValue(speedTextValue, observableSpeedValue,
				UIUtils.converterStrategy(uiToModel),
				UIUtils.converterStrategy(modelToUI));
		
		return speedTextValue;
	}
	
	private ISWTObservableValue bindCourseValue(DataBindingContext context,
			Text course, IObservableValue observableCourseValue)
	{
		ISWTObservableValue courseTextValue = WidgetProperties.text(SWT.FocusOut)
				.observe(course);
		IConverter modelToUI = new ModelToUICourseConverter();
		
		IConverter uiToModel = new UIToModelCourseConverter();
		
		context.bindValue(courseTextValue, observableCourseValue,
				UIUtils.converterStrategy(uiToModel),
				UIUtils.converterStrategy(modelToUI));
		
		return courseTextValue;
	}

	@Override
	protected void initializeWidgets()
	{
		hardConstraintLabel.setText("");
		estimateLabel.setText("");
	}
	
	private class CourseVerifier implements VerifyListener
	{

		@Override
		public void verifyText(VerifyEvent e)
		{
			if (e.text.isEmpty()) {
				return;
			}
			if (e.widget instanceof Text)
			{
				Text text = (Text) e.widget;
				final String oldS = text.getText();
				final String newS = oldS.substring(0, e.start) + e.text
						+ oldS.substring(e.end);

				try
				{
					Integer value = new Integer(newS);
					if (value.intValue() < 0 || value.intValue() > 360)
					{
						// value is out of range
						Display.getCurrent().beep();
						e.doit = false;
					}
				}
				catch (final NumberFormatException numberFormatException)
				{
					// value is not integer
					Display.getCurrent().beep();
					e.doit = false;
				}
			}
		}

	}

	private class SpeedVerifier implements VerifyListener
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
				
				if (newS.length() > 4)
				{
					Display.getCurrent().beep();
					e.doit = false;
					return;
				}
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

				if (index > 0 && index < newS.length() - 2)
				{
					Display.getCurrent().beep();
					e.doit = false;
					return;
				}
				try
				{
					Double value = new Double(newS);
					if (value.doubleValue() < 0 || value.doubleValue() > 40)
					{
						// value is out of range
						Display.getCurrent().beep();
						e.doit = false;
					}
				}
				catch (final NumberFormatException numberFormatException)
				{
					// value is not integer
					Display.getCurrent().beep();
					e.doit = false;
				}
			}
		}

	}
		
	private class Validator extends MultiValidator
	{

		private IObservableValue minValue;
		private IObservableValue maxValue;
		private IObservableValue estimateValue;
		private String bothPresentMessage;
		private String estimateInRangeMessage;

		public Validator(IObservableValue minValue,
				IObservableValue maxValue, IObservableValue estimateValue,
				String bothPresentMessage, String estimateInRangeMessage)
		{
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.estimateValue = estimateValue;
			this.bothPresentMessage = bothPresentMessage;
			this.estimateInRangeMessage = estimateInRangeMessage;
		}

		@Override
		protected IStatus validate()
		{
			String min = (String) minValue.getValue();
			String max = (String) maxValue.getValue();
			String estimate = (String) estimateValue.getValue();
			if (estimate != null && !estimate.isEmpty())
			{
				if (min == null || min.isEmpty())
				{
					return ValidationStatus.error(bothPresentMessage);
				}
				if (max == null || max.isEmpty())
				{
					return ValidationStatus.error(bothPresentMessage);
				}
			}
			else
			{
				if ((max != null && !max.isEmpty()) && (min == null || min.isEmpty()))
				{
					return ValidationStatus.error(bothPresentMessage);
				}
				if ((min != null && !min.isEmpty()) && (max == null || max.isEmpty()))
				{
					return ValidationStatus.error(bothPresentMessage);
				}
			}
			
//			if (max != null && !max.isEmpty() && min != null && !min.isEmpty())
//			{
//				Double dMin = new Double(min);
//				Double dMax = new Double(max);
//				if (estimate != null && !estimate.isEmpty()) {
//					Double dEstimate = new Double(estimate);
//					Double mMin = Math.min(dMin,  dMax);
//					Double mMax = Math.max(dMin, dMax);
//					if (dEstimate < mMin || dEstimate > mMax) {
//						return ValidationStatus.error(estimateInRangeMessage);
//					}
//				}
//			}
			return ValidationStatus.ok();
		}

	}
	
	private class ModelToUISpeedConverter implements IConverter
	{
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
			double msec = ((Double) fromObject).doubleValue();
			double kts = GeoSupport.MSec2kts(msec);
			BigDecimal temp = BigDecimal.valueOf(kts).setScale(1, BigDecimal.ROUND_HALF_UP);
			return temp.toString();
		}
	}

	private class ModelToUICourseConverter implements IConverter
	{
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
			double rad = ((Double) fromObject).doubleValue();
			double degree = Math.toDegrees(rad);
			BigDecimal temp = BigDecimal.valueOf(degree).setScale(0, BigDecimal.ROUND_HALF_UP);
			return temp.toString();
		}
	}
	private class UIToModelSpeedConverter implements IConverter
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
			double kts = new Double((String)fromObject).doubleValue();
			double msec = GeoSupport.kts2MSec(kts);
			return new Double(msec);
		}

	}
	
	private class UIToModelCourseConverter implements IConverter
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
			double degree = new Double((String)fromObject).doubleValue();
			double rad = Math.toRadians(degree);
			return new Double(rad);
		}

	}

	
}
