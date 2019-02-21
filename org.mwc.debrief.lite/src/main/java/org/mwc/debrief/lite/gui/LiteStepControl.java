package org.mwc.debrief.lite.gui;

import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;

import Debrief.GUI.Tote.StepControl;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.TacticalData.temporal.TimeProvider;

public class LiteStepControl extends StepControl
{

  public static interface SliderControls
  {
    public HiResDate getToolboxEndTime();

    public HiResDate getToolboxStartTime();

    public void setToolboxEndTime(final HiResDate val);

    public void setToolboxStartTime(final HiResDate val);
    
    public void setEnabled(final boolean enabled);
  }

  public static interface TimeLabel
  {
    void setRange(long start, long end);

    void setValue(long time);

    void setValue(String text);
  }

  private SliderControls _slider;
  private TimeLabel _timeLabel;
  private String timeFormat = "yy/MM/dd hh:mm:ss";

  public LiteStepControl(final ToolParent parent)
  {
    super(parent);
  }

  @Override
  protected void doEditPainter()
  {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  protected void formatTimeText()
  {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  protected PropertiesPanel getPropertiesPanel()
  {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public HiResDate getToolboxEndTime()
  {
    return _slider.getToolboxEndTime();
  }

  @Override
  public HiResDate getToolboxStartTime()
  {
    return _slider.getToolboxStartTime();
  }

  @Override
  protected void initForm()
  {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  protected void painterIsDefined()
  {
  }

  @Override
  public void propertyChange(final PropertyChangeEvent evt)
  {
    super.propertyChange(evt);

    if (evt.getPropertyName().equals(TimeProvider.TIME_CHANGED_PROPERTY_NAME))
    {
      final HiResDate dtg = (HiResDate) evt.getNewValue();
      updateForm(dtg);

      // hey, have we been set?
      changeTime(dtg);
    }

    if (evt.getPropertyName().equals(TimeProvider.PERIOD_CHANGED_PROPERTY_NAME))
    {
      final TimePeriod period = (TimePeriod) evt.getNewValue();
      
      // check we have a time period
      if(period != null)
      {
        _slider.setToolboxStartTime(period.getStartDTG());
        _slider.setToolboxEndTime(period.getEndDTG());
  
        setStartTime(period.getStartDTG());
        setEndTime(period.getEndDTG());
  
        _timeLabel.setRange(period.getStartDTG().getDate().getTime(), period
            .getEndDTG().getDate().getTime());
        
        // we should probably enable the slider 
        _slider.setEnabled(true);
      }
      else
      {
        // we should probably disable the slider 
        _slider.setEnabled(false);
      }
    }
  }

  public void setSliderControls(final SliderControls slider)
  {
    _slider = slider;
  }

  public void setTimeLabel(final TimeLabel label)
  {
    _timeLabel = label;
  }

  @Override
  public void setToolboxEndTime(final HiResDate val)
  {
    _slider.setToolboxEndTime(val);
  }

  @Override
  public void setToolboxStartTime(final HiResDate val)
  {
    _slider.setToolboxStartTime(val);
  }

  public void startStepping(final boolean go)
  {
    if (go)
    {
      super.startTimer();
    }
    else
    {
      super.stopTimer();
    }
  }
  
  public void setTimeFormat(final String timeFormat)
  {
    this.timeFormat = timeFormat;
  }

  @Override
  protected void updateForm(final HiResDate DTG)
  {
    final String str = new SimpleDateFormat(timeFormat).format(DTG.getDate().getTime());
    _timeLabel.setValue(str);
    _timeLabel.setValue(DTG.getDate().getTime());
  }

}
