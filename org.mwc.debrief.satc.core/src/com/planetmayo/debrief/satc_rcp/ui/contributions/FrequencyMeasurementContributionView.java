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

public class FrequencyMeasurementContributionView extends BaseContributionView<FrequencyMeasurementContribution> {

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

	private class RoundMUIConverter implements IConverter {
		final DecimalFormat df = new DecimalFormat("0.0");

		@Override
		public Object convert(final Object fromObject) {
			if (fromObject == null) {
				return null;
			}
			final Double kts = (Double) fromObject;
			final String res = df.format(kts);
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

	private class RoundUIMConverter implements IConverter {
		@Override
		public Object convert(final Object fromObject) {
			final String s = (String) fromObject;
			if (fromObject == null || s.isEmpty()) {
				return null;
			}

			return new Double((String) fromObject);
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

	private Text speedSoundText;

	private Text fNoughtText;

	public FrequencyMeasurementContributionView(final Composite parent,
			final FrequencyMeasurementContribution contribution, final IContributions contributions) {
		super(parent, contribution, contributions);
		initUI();
	}

	private void bindFNought(final DataBindingContext context) {
		final IObservableValue fNoughtValue = BeansObservables.observeValue(contribution,
				FrequencyMeasurementContribution.F_NOUGHT);
		final ISWTObservableValue fNoughtTextValue = WidgetProperties.text(SWT.FocusOut).observe(fNoughtText);

		// converter rounding the value to 1 decimal place
		final IConverter modelToUI = new RoundMUIConverter();
		final IConverter uiToModel = new RoundUIMConverter();

		context.bindValue(fNoughtTextValue, fNoughtValue, UIUtils.converterStrategy(uiToModel),
				UIUtils.converterStrategy(modelToUI));
	}

	@Override
	protected void bindMaxMinEstimate(final IObservableValue estimate, final IObservableValue min,
			final IObservableValue max) {

	}

	private void bindSpeed(final DataBindingContext context) {
		final IObservableValue soundValue = BeansObservables.observeValue(contribution,
				FrequencyMeasurementContribution.SOUND_SPEED);
		final ISWTObservableValue soundTextValue = WidgetProperties.text(SWT.FocusOut).observe(speedSoundText);

		// converter rounding the value to 1 decimal place
		final IConverter modelToUI = new RoundMUIConverter();
		final IConverter uiToModel = new RoundUIMConverter();

		context.bindValue(soundTextValue, soundValue, UIUtils.converterStrategy(uiToModel),
				UIUtils.converterStrategy(modelToUI));
	}

	@Override
	protected void bindValues(final DataBindingContext context) {
		final IObservableValue observationNumberValue = BeansObservables.observeValue(contribution,
				CoreMeasurementContribution.OBSERVATIONS_NUMBER);
		bindCommonHeaderWidgets(context, null, observationNumberValue,
				new PrefixSuffixLabelConverter(Object.class, " Measurements"), null);
		bindCommonDates(context);

		// bind SpeedSound
		bindSpeed(context);

		// bind FNought
		bindFNought(context);
	}

	@Override
	protected void createLimitAndEstimateSliders() {
	}

	@Override
	protected String getTitlePrefix() {
		return "Frequency Measurement - ";
	}

	@Override
	protected void initializeWidgets() {
		hardConstraintLabel.setText("n/a");
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
		gd = new GridData(110, SWT.DEFAULT);
		UIUtils.createLabel(speed, "Base Freq (Hz):", gd);

		fNoughtText = new Text(speed, SWT.BORDER | SWT.TRAIL);
		gd = new GridData(100, SWT.DEFAULT);
		fNoughtText.setLayoutData(gd);
		fNoughtText.addVerifyListener(new DoubleVerifier());

		// and tie the values together
		context = new DataBindingContext();
		bindValues(context);
	}
}
