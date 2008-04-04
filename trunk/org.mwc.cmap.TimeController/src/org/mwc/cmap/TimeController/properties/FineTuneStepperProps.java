package org.mwc.cmap.TimeController.properties;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.mwc.cmap.TimeController.controls.DTGBiSlider;

import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;

public class FineTuneStepperProps implements Editable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** the slider we're controlling
	 * 
	 */
	DTGBiSlider _slider;
	
	/** whether we'er editing the start or finish marker
	 * 
	 */
	boolean _doMin;
	
	/** create the editable properties for this slider
	 * 
	 * @param rangeSlider
	 * @param doMinVal
	 */
	public FineTuneStepperProps(DTGBiSlider rangeSlider, boolean doMinVal)
	{
		_slider = rangeSlider;
		_doMin = doMinVal;
	}
	
	public HiResDate getValue()
	{
		HiResDate currentVal;
		if(_doMin)
			currentVal = _slider.getPeriod().getStartDTG();
		else
			currentVal = _slider.getPeriod().getEndDTG();
		return currentVal;
	}
	public void setValue(HiResDate dtg)
	{
		if(_doMin)
			_slider.updateSelectedRanges(dtg, _slider.getPeriod().getEndDTG());
		else
			_slider.updateSelectedRanges(_slider.getPeriod().getStartDTG(), dtg);
	}
	
	/////////////////////////////////////////////////////////////////
	// editable support	
	/////////////////////////////////////////////////////////////////
	
	Editable.EditorType _myInfo;
	
	public EditorType getInfo()
	{
		if(_myInfo == null)
			 _myInfo = new StepperInfo(this);
		
		return null;
	}

	public String getName()
	{
		return "Time slider marker";
	}

	public boolean hasEditor()
	{
		return true;
	}	


  //////////////////////////////////////////////////////
  // bean info for this class
  /////////////////////////////////////////////////////
  public final class StepperInfo extends Editable.EditorType
  {

    public StepperInfo(final FineTuneStepperProps data)
    {
      super(data, "Stepper", "Time stepper");
    }

    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try{
        final PropertyDescriptor[] res={
          prop("Value", "the exact value of the time perid marker")         
        };
        return res;
      }catch(IntrospectionException e){
        return super.getPropertyDescriptors();
      }
    }
  }



	
}
