package org.mwc.debrief.lite.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Debrief.GUI.Tote.StepControl;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

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
  }

  private SliderControls _slider;
  private TimeLabel _timeLabel;
  
  public LiteStepControl(ToolParent parent)
  {
    super(parent);
    // TODO Auto-generated constructor stub
  }
  
  public void setPeriod(TimePeriod period, HiResDate curTime)
  {
    _slider.setToolboxStartTime(period.getStartDTG());
    _slider.setToolboxEndTime(period.getEndDTG());
    _timeLabel.setValue(getNewTime(curTime));
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
    DateFormat df = new SimpleDateFormat("yymmdd hhMMss");
    String str = df.format(new Date(DTG.getDate().getTime()));
    _timeLabel.setValue(str);
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
