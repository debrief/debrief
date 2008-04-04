package org.mwc.cmap.TimeController.controls;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

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
		_dateModel = suite.createDataModel(yearAgo, yearFromNow, threeMonthesAgo,
				fourMonthesFromNow);
//		_dateModel.setSegmentCount(3);
//		_dateModel.setSegmentLength(1000* 60 * 60);

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
			public String getLabel(double value)
			{
				// ok, convert to date
				long millis = (long) value;
				String res = FormatRNDateTime.toMediumString(millis);
				return res;
			}
		});

		// now some date fiddling
		_dateModel.addListener(new BiSliderDataModel.Listener()
		{
			private boolean myInCompositeUpdate;

			public void dataModelChanged(BiSliderDataModel dataModel,
					boolean moreChangesExpectedInNearFuture)
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
		
		DoFineControl fc = new DoFineControl()
		{
			public void adjust(boolean isMax)
			{
				doFineControl(isMax);
			}
			
		};

		
		// great, now it's ready for the actual BiSlider control
		_mySlider = BiSliderFactory.getInstance().createBiSlider(parentControl, _dateModel,
				_uiModel, fc);
		
	}
	
	public static interface DoFineControl
	{
		public void adjust(boolean isMax);
	}
	
	protected void doFineControl(boolean doMinVal)
	{
		MessageBox mb = new MessageBox(Display.getCurrent().getActiveShell());
		mb.setMessage("doing fine tune, max is:" + doMinVal);
		mb.open();
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
		Date firstDate = minSelectedDate.getDate();
		Date lastDate = maxSelectedDate.getDate();
		_dateModel.setUserMinimum(firstDate);
		_dateModel.setUserMaximum(lastDate);
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

	public void setSegmentSize(long size)
	{
		_dateModel.setSegmentLength(size);
	}
	
	/** the currently indicated time period (or null for no selection)
	 * 
	 * @return
	 */
	public TimePeriod getPeriod()
	{
		TimePeriod res = new TimePeriod.BaseTimePeriod(new HiResDate(_dateModel.getUserMinimumDate()), 
				new HiResDate(_dateModel.getUserMaximumDate()));
		
		return res;
	}
}
