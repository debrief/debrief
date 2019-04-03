package org.mwc.debrief.lite.gui;

import java.beans.PropertyChangeEvent;
import java.util.Enumeration;

import org.mwc.debrief.lite.menu.DebriefRibbonTimeController;
import org.mwc.debrief.lite.properties.PropertiesDialog;

import Debrief.GUI.Tote.StepControl;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.StepperListener;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DateFormatPropertyEditor;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI.ToolbarOwner;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.TacticalData.temporal.TimeProvider;

public class LiteStepControl extends StepControl
{

  private final ToolParent parent;

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

    void setFontSize(int newSize);
  }

  private SliderControls _slider;
  private TimeLabel _timeLabel;
  public static final String timeFormat = DateFormatPropertyEditor.DEFAULT_DATETIME_FORMAT;
  private Layers _layers;
  private UndoBuffer _undoBuffer;

  public LiteStepControl(final ToolParent _parent)
  {
    super(_parent);
    this.parent = _parent;
    setDateFormat(timeFormat);
    _largeSteps = false;
  }

  public void setLayers(Layers _layers)
  {
    this._layers = _layers;
  }

  public void setUndoBuffer(UndoBuffer _undoBuffer)
  {
    this._undoBuffer = _undoBuffer;
  }

  @Override
  protected void doEditPainter()
  {
    final StepperListener painter = this.getCurrentPainter();
    if (painter instanceof Editable)
    {
      ToolbarOwner owner = null;
      ToolParent parent = getParent();
      if (parent instanceof ToolbarOwner)
      {
        owner = (ToolbarOwner) parent;
      }

      PropertiesDialog dialog = new PropertiesDialog((Editable) painter,
          _layers, _undoBuffer, parent, owner);
      dialog.setSize(400, 500);
      dialog.setLocationRelativeTo(null);
      dialog.setVisible(true);
    }
    else
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Properties Editor",
          "Current Painter is not editable.");
    }
  }

  @Override
  protected void formatTimeText()
  {
    _timeLabel.setFontSize(_fontSize);
  }

  @Override
  protected PropertiesPanel getPropertiesPanel()
  {
    ToolbarOwner owner = null;
    ToolParent parent = getParent();
    if (parent instanceof ToolbarOwner)
    {
      owner = (ToolbarOwner) parent;
    }

    PropertiesDialog dialog = new PropertiesDialog(this.getDefaultHighlighter(),
        _layers, _undoBuffer, parent, owner);
    dialog.setSize(400, 500);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    return null;
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
  public void reset()
  {
    // let the parent do it's stuff
    super.reset();

    _slider.setEnabled(false);
    _timeLabel.setValue(timeFormat);
  }

  @Override
  protected void initForm()
  {
    /* This is not needed, because the implementation of the form initialization
     * has been done in the Ribbon.
     */
    
  }

  @Override
  protected void painterIsDefined()
  {
    // ok, ignore
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
      if (period != null)
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

    // inform the listeners
    final Enumeration<StepperListener> iter = getListeners().elements();
    while (iter.hasMoreElements())
    {
      final StepperListener l = iter.nextElement();
      l.steppingModeChanged(go);
    }
  }

  @Override
  protected void updateForm(final HiResDate DTG)
  {
    final String str = _dateFormatter.format(DTG.getDate().getTime());
    _timeLabel.setValue(str);
    _timeLabel.setValue(DTG.getDate().getTime());
    DebriefRibbonTimeController.assignThisTimeFormat(_dateFormatter.toPattern(), false, true);
  }

  public ToolParent getParent()
  {
    return parent;
  }
}
