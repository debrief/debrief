package org.mwc.debrief.lite.gui;

import java.beans.PropertyChangeEvent;

import Debrief.GUI.Tote.StepControl;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.TacticalData.temporal.TimeProvider;
import MWC.Utilities.TextFormatting.FullFormatDateTime;

public class LiteStepControl extends StepControl
{

  
  
  public static interface SliderControls
  {
    public HiResDate getToolboxStartTime();

    public HiResDate getToolboxEndTime();

    public void setToolboxStartTime(final HiResDate val);

    public void setToolboxEndTime(final HiResDate val);
  }
  
  public static interface TimeLabel
  {
    void setValue(String text);
    void setValue(long time);
    void setRange(long start, long end);
  }
  
  private SliderControls _slider;
  private TimeLabel _timeLabel;
  
  public LiteStepControl(ToolParent parent)
  {
    super(parent);
    // TODO Auto-generated constructor stub
  }
  
  
  
  @Override
  public void propertyChange(PropertyChangeEvent evt)
  {
    super.propertyChange(evt);
    
    if(evt.getPropertyName().equals(TimeProvider.TIME_CHANGED_PROPERTY_NAME))
    {
      HiResDate dtg = (HiResDate) evt.getNewValue();
      updateForm(dtg);
      
      // hey, have we been set?
      changeTime(dtg);
    }
    
    if(evt.getPropertyName().equals(TimeProvider.PERIOD_CHANGED_PROPERTY_NAME))
    {
      TimePeriod period = (TimePeriod) evt.getNewValue();
      _slider.setToolboxStartTime(period.getStartDTG());
      _slider.setToolboxEndTime(period.getEndDTG());
      
      setStartTime(period.getStartDTG());
      setEndTime(period.getEndDTG());
      
      _timeLabel.setRange(period.getStartDTG().getDate().getTime(), period
          .getEndDTG().getDate().getTime());
    }
  }

  public void setTimeLabel(final TimeLabel label)
  {
    _timeLabel = label;
  }
  
  public void setSliderControls(SliderControls slider)
  {
    _slider = slider;
  }
  
  @Override
  protected void initForm()
  {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  protected void updateForm(HiResDate DTG)
  {
    String str = FullFormatDateTime.toString(DTG.getDate().getTime());
    _timeLabel.setValue(str);
    _timeLabel.setValue(DTG.getDate().getTime());
  }

  @Override
  protected void formatTimeText()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected PropertiesPanel getPropertiesPanel()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void doEditPainter()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void painterIsDefined()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public HiResDate getToolboxStartTime()
  {
    return _slider.getToolboxStartTime();
  }

  @Override
  public HiResDate getToolboxEndTime()
  {
    return _slider.getToolboxEndTime();
  }

  @Override
  public void setToolboxStartTime(HiResDate val)
  {
    _slider.setToolboxStartTime(val);
  }

  @Override
  public void setToolboxEndTime(HiResDate val)
  {
    _slider.setToolboxEndTime(val);
  }

}
