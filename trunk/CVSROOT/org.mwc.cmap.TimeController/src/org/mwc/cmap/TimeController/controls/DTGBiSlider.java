package org.mwc.cmap.TimeController.controls;

import java.awt.event.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.property_support.ColorHelper;

import MWC.GenericData.*;

import com.visutools.nav.bislider.BiSlider;
import com.visutools.nav.bislider.BiSliderPresentation.FormatLong;

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
	
	/** constructor - get things going
	 * 
	 * @param parent
	 * @param style
	 */
	public DTGBiSlider(Composite parent, FormatLong formatter)
	{
		super(parent, SWT.EMBEDDED);
		
		// ok, insert our fresh new control
		java.awt.Frame sliderFrame = SWT_AWT.new_Frame(this);
		_mySlider = new BiSlider(formatter);
		sliderFrame.add(_mySlider);
		_mySlider.setVisible(true);
		_mySlider.setMinimumValue(0);
		_mySlider.setMaximumValue(100);
		_mySlider.setSegmentSize(10);
		_mySlider.setMinimumColor(java.awt.Color.GRAY);
		_mySlider.setMaximumColor(java.awt.Color.GRAY);
		_mySlider.setBackground(ColorHelper.convertColor(this.getBackground().getRGB()));
		_mySlider.setUnit("");
		_mySlider.setPrecise(true);		
		
		// listen out for mouse release - so we can get updated values
		_mySlider.addMouseListener(new MouseAdapter(){

			/**
			 * @param e
			 */
			public void mouseReleased(MouseEvent e)
			{
				outputValues();
			}
			
		});
		
		
	}
	
	public void updateOuterRanges(TimePeriod period)
	{
		_minVal = period.getStartDTG();
		_maxVal = period.getEndDTG();
		
		long microRange = _maxVal.getMicros() - _minVal.getMicros();
		long milliRange = microRange / 1000;
		
		_mySlider.setMinimumValue(0);
		_mySlider.setMaximumValue(milliRange);
		
		// try for hours
		_mySlider.setSegmentSize(1000 * 60 * 15);
	}
	
	
	
	/** outside object has requested repaint get on with it..
	 * 
	 */
	public void update()
	{
		super.update();
		
		// and get the widget to repaint
		_mySlider.repaint();
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
		HiResDate lowDTG = new HiResDate((long) _mySlider.getMinimumColoredValue(), _minVal.getMicros());
		HiResDate highDTG = new HiResDate((long) _mySlider.getMaximumColoredValue(), _minVal.getMicros());
		
		rangeChanged(new TimePeriod.BaseTimePeriod(lowDTG, highDTG));
	}

	public void rangeChanged(TimePeriod period)
	{
		// ok, anybody can over-ride this call if they want to - to inform
		// themselves what's happening
	}
	
}
