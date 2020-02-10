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

package com.borlander.rac353542.bislider;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.borlander.rac353542.bislider.cdata.CalendarDateSuite;
import com.borlander.rac353542.bislider.cdata.DataObjectLabelProvider;
import com.borlander.rac353542.bislider.cdata.LongDataSuite.LongDataModel;

import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class Snippet {

	private static void createDateBiSlider(final Composite parent) {
		final Calendar calendar = Calendar.getInstance();
		final long nowMillis = System.currentTimeMillis();
		calendar.setTimeInMillis(nowMillis);
		calendar.add(Calendar.YEAR, -1);
		final Date yearAgo = calendar.getTime();

		calendar.setTimeInMillis(nowMillis);
		calendar.add(Calendar.MONTH, -3);
		final Date threeMonthesAgo = calendar.getTime();

		calendar.setTimeInMillis(nowMillis);
		calendar.add(Calendar.MONTH, +4);
		final Date fourMonthesFromNow = calendar.getTime();

		calendar.setTimeInMillis(nowMillis);
		calendar.add(Calendar.YEAR, +1);
		final Date yearFromNow = calendar.getTime();

		final BiSlider result = BiSliderFactory.getInstance().createCalendarDateBiSlider(parent, yearAgo, yearFromNow,
				null);

		final CalendarDateSuite.CalendarDateModel model = (CalendarDateSuite.CalendarDateModel) result.getDataModel();
		model.setUserMinimum(threeMonthesAgo);
		model.setUserMaximum(fourMonthesFromNow);
		model.setSegmentCount(100);

		final DefaultBiSliderUIModel uiConfig = (DefaultBiSliderUIModel) result.getUIModel();
		uiConfig.setBiSliderForegroundRGB(new RGB(255, 0, 255)); // ugly, I know
		uiConfig.setMaximumRGB(new RGB(255, 0, 0));
		uiConfig.setMinimumRGB(new RGB(255, 255, 0));
		uiConfig.setColorInterpolation(new ColorInterpolation.INTERPOLATE_CENTRAL(new RGB(0, 0, 127)));
		uiConfig.setContentsDataProvider(BiSliderContentsDataProvider.NORMAL_DISTRIBUTION);
		uiConfig.setHasLabelsAboveOrLeft(true);
		uiConfig.setHasLabelsBelowOrRight(false);
		uiConfig.setLabelInsets(65);

		final DataObjectLabelProvider customLabelProvider = new DataObjectLabelProvider(model.getMapper()) {
//            private final Date todayMidnight = (Date)CalendarDateSuite.CALENDAR_DATE.double2object(nowMillis);

			@Override
			public String getLabel(final Object value) {
				// ok, convert to date
				final Date theDate = (Date) value;

				final String res = FormatRNDateTime.toString(theDate.getTime());
				return res;
			}
//
//            public String getLabel(Object dataObject) {
//                Date date = (Date)dataObject;
//                long deltaMillis = date.getTime() - todayMidnight.getTime();
//                long deltaInDays = deltaMillis / (1000L * 60 * 60 * 24);
//                if (deltaInDays == 0){
//                    return "Today";
//                }
//                if (deltaInDays > 0){
//                    return "+" + String.valueOf(deltaInDays) + " days" ;
//                } else {
//                    return String.valueOf(deltaInDays) + " days" ;
//                }
//            }
		};

		uiConfig.setLabelProvider(customLabelProvider);
	}

	public static BiSlider createLongBiSlider(final Composite parent) {
		final long base = 1000000000L;
		final BiSlider result = BiSliderFactory.getInstance().createLongBiSlider(parent, base + 0, base + 100, null);
		// the safety of these casts is guarranteed -- see BiSliderFactory
		final LongDataModel dataModel = (LongDataModel) result.getDataModel();
		dataModel.setUserMaximum(new Long(base + 70));
		dataModel.setUserMinimum(new Long(base + 20));
		final DefaultBiSliderUIModel uiConfig = (DefaultBiSliderUIModel) result.getUIModel();

		uiConfig.setLabelInsets(80);
		uiConfig.setVerticalLabels(true);
		uiConfig.setHasLabelsAboveOrLeft(false);
		uiConfig.setHasLabelsBelowOrRight(true);
//
//        uiConfig.setLabelInsets(SWT.DEFAULT);
//        uiConfig.setVerticalLabels(true);
//        uiConfig.setHasLabelsAboveOrLeft(true);
//        uiConfig.setHasLabelsBelowOrRight(true);

		return result;
	}

	private static void decorateShell(final Shell shell) {
		shell.setText("Bi-Slider demo (543)");
		shell.setLayout(new FillLayout(SWT.VERTICAL));

		final Group simple = new Group(shell, SWT.NONE);
		simple.setText("All defaults");
		simple.setLayout(new FillLayout());
		final BiSlider simpleSlider = BiSliderFactory.getInstance().createBiSlider(simple, null);

		/*
		 * simpleSlider.getDataModel().addListener(new BiSliderDataModel.Listener() {
		 * private boolean myInCompositeUpdate;
		 *
		 * public void dataModelChanged(BiSliderDataModel dataModel, boolean
		 * moreChangesExpectedInNearFuture) { if (moreChangesExpectedInNearFuture &&
		 * myInCompositeUpdate){ return; } myInCompositeUpdate =
		 * moreChangesExpectedInNearFuture;
		 * System.err.println("dataModelChanges: isComposite: " +
		 * moreChangesExpectedInNearFuture); } });
		 */

		simpleSlider.getWritableDataModel().setSegmentLength(30);

		final DefaultBiSliderUIModel uiConfig = (DefaultBiSliderUIModel) simpleSlider.getUIModel();
		uiConfig.setHasLabelsAboveOrLeft(true);
		uiConfig.setHasLabelsBelowOrRight(true);

		final Group longGroup = new Group(shell, SWT.NONE);
		longGroup.setText("Long values");
		longGroup.setLayout(new FillLayout());
		createLongBiSlider(longGroup);

		final Group dateGroup = new Group(shell, SWT.NONE);
		dateGroup.setText("Date values");
		dateGroup.setLayout(new FillLayout());
		createDateBiSlider(dateGroup);

		shell.setSize(1000, 1000);
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		decorateShell(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
