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

import java.math.BigDecimal;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.planetmayo.debrief.satc.model.contributions.Range1959ForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.converters.MinMaxLimitObservable;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.KtsToMSec;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.MSecToKts;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.MeterToYds;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.UnitConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.YdsToMeter;

public class Ranging1959ContributionView extends BaseContributionView<Range1959ForecastContribution> {

	private class DoubleVerifier implements VerifyListener {

		@Override
		public void verifyText(final VerifyEvent e) {
			if (e.text.isEmpty()) {
				return;
			}
			final String s = e.text;
			final char[] chars = new char[s.length()];
			s.getChars(0, chars.length, chars, 0);
			for (int i = 0; i < chars.length; i++) {
				if (!('0' <= chars[i] && chars[i] <= '9')) {
					if (chars[i] != '.') {
						Display.getCurrent().beep();
						e.doit = false;
						return;
					}
				}
			}
			if (e.widget instanceof Text) {
				final Text text = (Text) e.widget;
				final String oldS = text.getText();
				final String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
				final int index = newS.indexOf(".");
				if (index >= 0) {
					final int i2 = newS.indexOf(".", index + 1);
					if (i2 >= 0) {
						Display.getCurrent().beep();
						e.doit = false;
						return;
					}
				}

			}
		}

	}

	private class KtsToMsConverter implements IConverter {
		final DecimalFormat df = new DecimalFormat("0.0");
		final KtsToMSec converter = new KtsToMSec();

		@Override
		public Object convert(final Object fromObject) {
			if (fromObject == null) {
				return null;
			}
			final Double kts = (Double) fromObject;
			final Double ms = (Double) converter.convert(kts);
			final String res = df.format(ms);
			return res;
		}

		@Override
		public Object getFromType() {
			return Double.class;
		}

		@Override
		public Object getToType() {
			return String.class;
		}
	}

	private class ModelToUIFNoughtConverter implements IConverter {
		@Override
		public Object convert(final Object fromObject) {
			if (fromObject == null) {
				return null;
			}
			final double value = ((Double) fromObject).doubleValue();
			final BigDecimal temp = BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_HALF_UP);
			return temp.toString();
		}

		@Override
		public Object getFromType() {
			return Double.class;
		}

		@Override
		public Object getToType() {
			return String.class;
		}
	}

	private class ModelToUIRangeConverter implements IConverter {
		@Override
		public Object convert(final Object fromObject) {
			if (fromObject == null) {
				return null;
			}
			// int value = ((Double) fromObject).intValue();
			final int value = new MeterToYds().safeConvert((Double) fromObject).intValue();
			return new String(new Integer(value).toString());
		}

		@Override
		public Object getFromType() {
			return Double.class;
		}

		@Override
		public Object getToType() {
			return String.class;
		}
	}

	private class MsToKtsConverter implements IConverter {
		final MSecToKts converter = new MSecToKts();

		@Override
		public Object convert(final Object fromObject) {
			final String s = (String) fromObject;
			if (fromObject == null || s.isEmpty()) {
				return null;
			}

			final double ms = new Double((String) fromObject).doubleValue();
			final Double kts = (Double) converter.convert(ms);
			return new Double(kts);
		}

		@Override
		public Object getFromType() {
			return String.class;
		}

		@Override
		public Object getToType() {
			return Double.class;
		}

	}

	private class UIToModelRangeConverter implements IConverter {

		@Override
		public Object convert(final Object fromObject) {
			final String s = (String) fromObject;
			if (fromObject == null || s.isEmpty()) {
				return null;
			}
			// double value = new Double((String)fromObject).doubleValue();
			Double value = new Double((String) fromObject);
			value = new YdsToMeter().safeConvert(value);
			return new Double(value);
		}

		@Override
		public Object getFromType() {
			return String.class;
		}

		@Override
		public Object getToType() {
			return Double.class;
		}

	}

	private static final DecimalFormat rangeFormat = new DecimalFormat("0");

	private Text speedSoundText;

	private Text fNoughtText;

	private Text rangeText;

	private Text rangeBoundsText;

	public Ranging1959ContributionView(final Composite parent, final Range1959ForecastContribution contribution,
			final IContributions contributions) {
		super(parent, contribution, contributions);
		initUI();
	}

	private void bindFNought(final DataBindingContext context) {
		final IObservableValue fNoughtValue = BeansObservables.observeValue(contribution,
				Range1959ForecastContribution.F_NOUGHT);
		final ISWTObservableValue fNoughtTextValue = WidgetProperties.text(SWT.FocusOut).observe(fNoughtText);

		final IConverter modelToUI = new ModelToUIFNoughtConverter();

		final IConverter uiToModel = new MsToKtsConverter();

		context.bindValue(fNoughtTextValue, fNoughtValue, UIUtils.converterStrategy(uiToModel),
				UIUtils.converterStrategy(modelToUI));
	}

	@Override
	protected void bindMaxMinEstimate(final IObservableValue estimate, final IObservableValue min,
			final IObservableValue max) {

	}

	private void bindRange(final DataBindingContext context) {
		final IObservableValue rangeValue = BeansObservables.observeValue(contribution,
				Range1959ForecastContribution.RANGE);
		final ISWTObservableValue rangeTextValue = WidgetProperties.text(SWT.FocusOut).observe(rangeText);

		final IConverter modelToUI = new ModelToUIRangeConverter();

		final IConverter uiToModel = new UIToModelRangeConverter();

		context.bindValue(rangeTextValue, rangeValue, UIUtils.converterStrategy(uiToModel),
				UIUtils.converterStrategy(modelToUI));
	}

	private void bindRangeBounds(final DataBindingContext context) {
		final PrefixSuffixLabelConverter minMaxConverter = new PrefixSuffixLabelConverter(Object.class, "", "",
				rangeFormat);
		minMaxConverter.setNestedUnitConverter(UnitConverter.RANGE_YDS.getModelToUI());

		final IObservableValue minRangeValue = BeansObservables.observeValue(contribution,
				Range1959ForecastContribution.MIN_RANGE);
		final IObservableValue maxRangeValue = BeansObservables.observeValue(contribution,
				Range1959ForecastContribution.MAX_RANGE);

		final IObservableValue rangeBoundsValue = new MinMaxLimitObservable(minRangeValue, maxRangeValue,
				minMaxConverter);
		final ISWTObservableValue rangeBoundsTextValue = WidgetProperties.text(SWT.FocusOut).observe(rangeBoundsText);
		context.bindValue(rangeBoundsTextValue, rangeBoundsValue);
	}

	private void bindSpeed(final DataBindingContext context) {
		final IObservableValue soundValue = BeansObservables.observeValue(contribution,
				Range1959ForecastContribution.SOUND_SPEED);
		final ISWTObservableValue soundTextValue = WidgetProperties.text(SWT.FocusOut).observe(speedSoundText);

		final IConverter modelToUI = new KtsToMsConverter();

		final IConverter uiToModel = new MsToKtsConverter();

		context.bindValue(soundTextValue, soundValue, UIUtils.converterStrategy(uiToModel),
				UIUtils.converterStrategy(modelToUI));
	}

	@Override
	protected void bindValues(final DataBindingContext context) {
		final PrefixSuffixLabelConverter labelConverter = new PrefixSuffixLabelConverter(Object.class, "", " Yds",
				rangeFormat);
		labelConverter.setNestedUnitConverter(UnitConverter.RANGE_YDS.getModelToUI());

		final IObservableValue errorValue = BeansObservables.observeValue(contribution,
				Range1959ForecastContribution.RANGE);
		final IObservableValue observationNumberValue = BeansObservables.observeValue(contribution,
				Range1959ForecastContribution.OBSERVATIONS_NUMBER);
		bindCommonHeaderWidgets(context, errorValue, observationNumberValue,
				new PrefixSuffixLabelConverter(Object.class, " Measurements"), labelConverter);
		bindCommonDates(context);

		// bind SpeedSound
		bindSpeed(context);

		// bind FNought
		bindFNought(context);

		// bind range
		bindRange(context);

		// bind range bounds
		bindRangeBounds(context);
	}

	@Override
	protected void createLimitAndEstimateSliders() {
	}

	@Override
	protected String getTitlePrefix() {
		return "1959 Ranging Forecast - ";
	}

	@Override
	protected void initializeWidgets() {
	}

	@Override
	protected void initUI() {
		final GridLayout layout = UIUtils.createGridLayoutWithoutMargins(1, false);
		layout.verticalSpacing = 0;
		mainGroup = new Group(controlParent, SWT.SHADOW_ETCHED_IN);
		mainGroup.setLayout(layout);
		mainGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		createHeader(mainGroup);
		createBody(mainGroup);

		titleChangeListener = attachTitleChangeListener(contribution, getTitlePrefix());
		initializeWidgets();

		// add sound speed
		UIUtils.createLabel(bodyGroup, "Sound Speed (m/s):", new GridData(130, SWT.DEFAULT));
		UIUtils.createSpacer(bodyGroup, new GridData(45, SWT.DEFAULT));

		// add the speed
		final Composite speed = new Composite(bodyGroup, SWT.NONE);
		speed.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		speed.setLayout(new GridLayout(4, false));

		speedSoundText = new Text(speed, SWT.BORDER | SWT.TRAIL);
		GridData gd = new GridData(100, SWT.DEFAULT);
		speedSoundText.setLayoutData(gd);
		speedSoundText.addVerifyListener(new DoubleVerifier());

		// add FNought
		gd = new GridData(90, SWT.DEFAULT);
		UIUtils.createLabel(speed, "F0 (Hz):", gd);

		fNoughtText = new Text(speed, SWT.BORDER | SWT.TRAIL);
		gd = new GridData(100, SWT.DEFAULT);
		fNoughtText.setLayoutData(gd);
		fNoughtText.addVerifyListener(new DoubleVerifier());

		// now add the range
		UIUtils.createLabel(bodyGroup, "1959 Range(Yds):", new GridData(120, SWT.DEFAULT));
		UIUtils.createSpacer(bodyGroup, new GridData(1, SWT.DEFAULT));

		final Composite range = new Composite(bodyGroup, SWT.NONE);
		range.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		range.setLayout(new GridLayout(3, false));

		rangeText = new Text(range, SWT.BORDER | SWT.READ_ONLY | SWT.TRAIL);
		gd = new GridData(60, SWT.DEFAULT);
		rangeText.setLayoutData(gd);

		rangeBoundsText = new Text(range, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
		gd = new GridData(140, SWT.DEFAULT);
		rangeBoundsText.setLayoutData(gd);

		final Label lastLabel = new Label(range, SWT.LEFT);
		lastLabel.setText("Calculated data - read only");
		gd = new GridData(220, SWT.DEFAULT);
		lastLabel.setLayoutData(gd);

		context = new DataBindingContext();
		bindValues(context);
	}
}
