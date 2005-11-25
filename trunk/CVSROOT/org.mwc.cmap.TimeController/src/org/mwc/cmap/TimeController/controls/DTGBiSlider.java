package org.mwc.cmap.TimeController.controls;

import java.util.*;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Composite;

import MWC.GenericData.*;
import MWC.Utilities.TextFormatting.*;

import com.borlander.rac353542.bislider.*;
import com.borlander.rac353542.bislider.cdata.CalendarDateSuite;
import com.borlander.rac353542.bislider.cdata.CalendarDateSuite.CalendarDateModel;

public class DTGBiSlider
{

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
	 * @param parent
	 * @param style
	 */
	public DTGBiSlider(Composite parentControl)
	{

		// sort out some demo dates
		final Calendar calendar = Calendar.getInstance();
		final long nowMillis = System.currentTimeMillis();
		calendar.setTimeInMillis(nowMillis);
		calendar.add(Calendar.YEAR, -1);
		Date yearAgo = calendar.getTime();

		calendar.setTimeInMillis(nowMillis);
		calendar.add(Calendar.MONTH, -3);
		Date threeMonthesAgo = calendar.getTime();

		calendar.setTimeInMillis(nowMillis);
		calendar.add(Calendar.MONTH, +4);
		Date fourMonthesFromNow = calendar.getTime();

		calendar.setTimeInMillis(nowMillis);
		calendar.add(Calendar.YEAR, +1);
		Date yearFromNow = calendar.getTime();

		// sort out the data model
		CalendarDateSuite suite = new CalendarDateSuite();
		_dateModel = suite.createDataModel(yearAgo,
				yearFromNow, threeMonthesAgo, fourMonthesFromNow);

		// sort out the UI model
		_uiModel = (DefaultBiSliderUIModel) suite.createUIModel();

		// great, now it's ready for the actual BiSlider control
		_mySlider = BiSliderFactory.getInstance().createBiSlider(parentControl, _dateModel,
				_uiModel);

		// update the UI labels
		_uiModel.setLabelProvider(new BiSliderLabelProvider()
		{

			public String getLabel(double value)
			{
				// ok, convert to date
				long millis = (long) value;
				String res = FormatRNDateTime.toString(millis);
				return res;
			}
		});
		_uiModel.setHasLabelsBelowOrRight(true);

		// and catch the mouse-release event
		_mySlider.addMouseListener(new MouseAdapter()
		{
			public void mouseUp(MouseEvent e)
			{
				outputValues();
			}
		});

	}

	public Composite getControl()
	{
		return _mySlider;
	}

	public void updateOuterRanges(TimePeriod period)
	{
		_minVal = period.getStartDTG();
		_maxVal = period.getEndDTG();

		Date firstDate = _minVal.getDate();
		Date lastDate = _maxVal.getDate();
		_dateModel.setTotalRange(firstDate, lastDate);

		// try for units of 10 * the current step
		_dateModel.setSegmentCount(10);
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

	public void updateSelectedRanges(HiResDate minSelectedDate, HiResDate maxSelectedDate)
	{

	}

	/**
	 * ok fire data-changed event
	 */
	protected void outputValues()
	{
		// ok - determine the times
		HiResDate lowDTG = new HiResDate(_dateModel.getUserMinimumDate());
		HiResDate highDTG = new HiResDate(_dateModel.getUserMaximumDate());

		// and send out the update
		rangeChanged(new TimePeriod.BaseTimePeriod(lowDTG, highDTG));
	}

	public void rangeChanged(TimePeriod period)
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
	public void setStepSize(long size)
	{
		_stepSize = size;
	}

}
