package org.mwc.cmap.TimeController.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import MWC.GenericData.*;
import MWC.Utilities.TextFormatting.FullFormatDateTime;

import com.borlander.rac353542.bislider.*;

//import com.visutools.nav.bislider.BiSlider;
//import com.visutools.nav.bislider.BiSliderPresentation.FormatLong;

public class DTGBiSlider extends Composite
{

	/** our slider control
	 * 
	 */
	BiSlider _mySlider;
	
	/** the minimum value
	 * 
	 */
	HiResDate _minVal;
	
	/** and the maximum value
	 * 
	 */
	HiResDate _maxVal;
	
	/** the step size we apply to the slider (the size of the smallest increment, in millis)
	 * 
	 */
	long _stepSize = 1000 * 60;

	private BiSliderUIModelImpl _sliderUI;

	private BiSliderDataModelImpl _sliderData;
	
	private BiSliderLabelProvider _labelProvider;
	
	/** constructor - get things going
	 * 
	 * @param parent
	 * @param style
	 */
	public DTGBiSlider(Composite parent, FormatLong formatter)
	{
		super(parent, SWT.EMBEDDED);

		_labelProvider = new BiSliderLabelProvider(){

			public String getLabel(double value)
			{
				long time = (long) value;
				String res = FullFormatDateTime.toString(time);
				return res;
			}};
			
		_sliderData = new BiSliderDataModelImpl();
		_sliderUI = new BiSliderUIModelImpl(){
			public RGB getMaximumRGB()
			{
				return new RGB(55, 55, 55);
			}

			public RGB getMinimumRGB()
			{
				return new RGB(55,55,55);
			}
			
		};
		
		
		// ok, insert our fresh new control
		_mySlider = new BiSlider(parent, SWT.NONE, _sliderData, _sliderUI)
		{
			public BiSliderLabelProvider getLabelProvider()
			{
				return _labelProvider;
			}
			
		};
		
		_sliderData.setTotalRange(0, 100);
		_sliderData.setSegmentLength(10);
		
//		_mySlider.setMinimumValue(0);
//		_mySlider.setMaximumValue(100);
//		_mySlider.setSegmentSize(10);
//		_mySlider.setMinimumColor(java.awt.Color.GRAY);
//		_mySlider.setMaximumColor(java.awt.Color.GRAY);
//		_mySlider.setBackground(ColorHelper.convertColor(this.getBackground().getRGB()));
//		_mySlider.setUnit("");
//		_mySlider.setPrecise(true);		

		_mySlider.setVisible(true);
		
		// listen out for mouse release - so we can get updated values
//		_sliderData.addListener(new BiSliderDataModel.Listener(){
//
//			public void dataModelChanged(BiSliderDataModel dataModel)
//			{
//				outputValues();
//			}});


		// and catch the mouse-release event
		_mySlider.addMouseListener(new MouseAdapter(){
			public void mouseUp(MouseEvent e)
			{
				outputValues();
			}});
		
		
	}
	
	public void updateOuterRanges(TimePeriod period)
	{
		_minVal = period.getStartDTG();
		_maxVal = period.getEndDTG();
		
		long microRange = _maxVal.getMicros() - _minVal.getMicros();
		long milliRange = microRange / 1000;
		
		long workingRange = milliRange / _stepSize;
		
		_sliderData.setTotalRange(0, workingRange);
//		_mySlider.setMinimumValue(0);
//		_mySlider.setMaximumValue(workingRange);
		
		// try for units of 10 * the current step
		_sliderData.setSegmentLength(1);
	}
	
	
	
	/** outside object has requested repaint get on with it..
	 * 
	 */
	public void update()
	{
		super.update();
		
		// and get the widget to repaint
		_mySlider.update();
		//repaint();
	}

	public void updateSelectedRanges(HiResDate minSelectedDate, HiResDate maxSelectedDate)
	{
		
	}

	/** ok fire data-changed event
	 * 
	 *
	 */
	protected void outputValues()
	{
		// get how many micros it is
		
		long minVal = (long) _mySlider.getDataModel().getUserMinimum();
		minVal *= _stepSize;
		
		long maxVal = (long) _mySlider.getDataModel().getUserMaximum();// getMaximumColoredValue();
		maxVal *= _stepSize;
		
		HiResDate lowDTG = new HiResDate((long) minVal, _minVal.getMicros());
		HiResDate highDTG = new HiResDate((long) maxVal, _minVal.getMicros());
		
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
	 * @param size The _stepSize to set (millis)
	 */
	public void setStepSize(long size)
	{
		_stepSize = size;
	}
	

	//////////////////////////////////////////////////////
	// utility class that returns a string from a long value
	//////////////////////////////////////////////////////
	public static class FormatLong
	{
		public String format(long val)
		{
			return "" + val;
		}
	}	
	
}
