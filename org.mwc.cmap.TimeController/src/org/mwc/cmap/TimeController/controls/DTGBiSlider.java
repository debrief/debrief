/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.TimeController.controls;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

import com.borlander.rac353542.bislider.BiSlider;
import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.BiSliderFactory;
import com.borlander.rac353542.bislider.BiSliderLabelProvider;
import com.borlander.rac353542.bislider.DefaultBiSliderUIModel;
import com.borlander.rac353542.bislider.cdata.CalendarDateSuite;
import com.borlander.rac353542.bislider.cdata.CalendarDateSuite.CalendarDateModel;

public class DTGBiSlider
{

	public static interface DoFineControl
	{
		public void adjust(boolean isMax);
	}

	/**
	 * our slider control
	 */
	BiSlider _mySlider;

	/**
	 * the minimum value
	 */
	HiResDate _minVal;

	/**
	 * and the maximum value
	 */
	HiResDate _maxVal;

	/**
	 * the step size we apply to the slider (the size of the smallest increment,
	 * in millis)
	 */
	long _stepSize = 1000 * 60;

	private CalendarDateModel _dateModel;

	private DefaultBiSliderUIModel _uiModel;

	/**
	 * constructor - get things going
	 * 
	 * @param fineControl
	 * 
	 * @param parent
	 * @param style
	 */
	public DTGBiSlider(final Composite parentControl, final DoFineControl fineControl)
	{

		// sort out some demo dates
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

		// sort out the data model
		final CalendarDateSuite suite = new CalendarDateSuite();
		_dateModel = suite.createDataModel(yearAgo, yearFromNow, threeMonthesAgo,
				fourMonthesFromNow);
		// _dateModel.setSegmentCount(3);
		// _dateModel.setSegmentLength(1000* 60 * 60);

		// sort out the UI model
		_uiModel = (DefaultBiSliderUIModel) suite.createUIModel();

		// do a bit of ui fiddling
		_uiModel.setHasLabelsAboveOrLeft(true);
		_uiModel.setHasLabelsBelowOrRight(false);

		_uiModel.setLabelInsets(50);
		_uiModel.setNonLabelInsets(20);

		// update the UI labels
		_uiModel.setLabelProvider(new BiSliderLabelProvider()
		{
			public String getLabel(final double value)
			{
				// ok, convert to date
				final long millis = (long) value;
				final String res = FormatRNDateTime.toMediumString(millis);
				return res;
			}
		});

		// now some date fiddling
		_dateModel.addListener(new BiSliderDataModel.Listener()
		{
			private boolean myInCompositeUpdate;

			public void dataModelChanged(final BiSliderDataModel dataModel,
					final boolean moreChangesExpectedInNearFuture)
			{
				// see if we're already procesing something
				if (moreChangesExpectedInNearFuture && myInCompositeUpdate)
				{
					return;
				}

				// nope, get on with it
				myInCompositeUpdate = moreChangesExpectedInNearFuture;

				// is this a "drop" event
				if (!moreChangesExpectedInNearFuture)
				{
					// yes, fire changed event
					outputValues();
				}
			}
		});

		// great, now it's ready for the actual BiSlider control
		_mySlider = BiSliderFactory.getInstance().createBiSlider(parentControl,
				_dateModel, _uiModel, fineControl);

		setShowLabels(false);
		resetMinMaxPointers();
	}

	public Composite getControl()
	{
		return _mySlider;
	}

	public void updateOuterRanges(final TimePeriod period)
	{

		_minVal = period.getStartDTG();
		_maxVal = period.getEndDTG();

		final Date firstDate = _minVal.getDate();
		final Date lastDate = _maxVal.getDate();

		// look out for special case where dates are equal
		if (!firstDate.equals(lastDate))
			_dateModel.setTotalRange(firstDate, lastDate);

	}

	/**
	 * outside object has requested repaint get on with it..
	 */
	public void update()
	{
		// super.update();

		// and get the widget to repaint
		_mySlider.update();
		// repaint();
	}

	public void updateSelectedRanges(final HiResDate minSelectedDate,
			final HiResDate maxSelectedDate)
	{
		final Date firstDate = minSelectedDate.getDate();
		final Date lastDate = maxSelectedDate.getDate();
		_dateModel.setUserMinimum(firstDate);
		_dateModel.setUserMaximum(lastDate);
	}

	/**
	 * ok fire data-changed event
	 */
	protected void outputValues()
	{
		// ok - determine the times
		final HiResDate lowDTG = new HiResDate(_dateModel.getUserMinimumDate());
		final HiResDate highDTG = new HiResDate(_dateModel.getUserMaximumDate());

		// and send out the update
		rangeChanged(new TimePeriod.BaseTimePeriod(lowDTG, highDTG));
	}

	public void rangeChanged(final TimePeriod period)
	{
		// ok, anybody can over-ride this call if they want to - to inform
		// themselves what's happening
	}

	/**
	 * @return Returns the _stepSize (millis)
	 */
	public long getStepSize()
	{
		return _stepSize;
	}

	/**
	 * @param size
	 *          The _stepSize to set (millis)
	 */
	public void setStepSize(final long size)
	{
		_stepSize = size;
	}

	public void setSegmentSize(final long size)
	{
		_dateModel.setSegmentLength(size);
	}

	/**
	 * the currently indicated time period (or null for no selection)
	 * 
	 * @return
	 */
	public TimePeriod getPeriod()
	{
		final TimePeriod res = new TimePeriod.BaseTimePeriod(new HiResDate(
				_dateModel.getUserMinimumDate()), new HiResDate(
				_dateModel.getUserMaximumDate()));

		return res;
	}
	
	public void setShowLabels(boolean showLabels) {
		_mySlider.setShowLabels(showLabels);
		redrawInternal();
	}

	private void redrawInternal()
	{
		if (_mySlider != null && !_mySlider.isDisposed()) {
			_mySlider.redraw();
		}
	}

	public void resetMinMaxPointers()
	{
		_mySlider.resetMinMaxPointers();
		redrawInternal();
		
	}
}
