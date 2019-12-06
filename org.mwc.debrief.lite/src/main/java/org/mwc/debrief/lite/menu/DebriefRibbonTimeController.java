/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.custom.JRibbonLabel;
import org.mwc.debrief.lite.custom.JRibbonRangeDisplayPanel;
import org.mwc.debrief.lite.custom.JRibbonRangeSlider;
import org.mwc.debrief.lite.custom.JRibbonSlider;
import org.mwc.debrief.lite.custom.LabelComponentContentModel;
import org.mwc.debrief.lite.custom.RangeDisplayComponentContentModel;
import org.mwc.debrief.lite.custom.RibbonLabelProjection;
import org.mwc.debrief.lite.custom.RibbonRangeDisplayPanelProjection;
import org.mwc.debrief.lite.custom.RibbonRangeSliderProjection;
import org.mwc.debrief.lite.custom.RibbonSliderProjection;
import org.mwc.debrief.lite.custom.SliderComponentContentModel;
import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.gui.LiteStepControl.SliderControls;
import org.mwc.debrief.lite.gui.LiteStepControl.TimeLabel;
import org.mwc.debrief.lite.gui.custom.RangeSlider;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.properties.PropertiesDialog;
import org.mwc.debrief.lite.util.ResizableIconFactory;
import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;
import org.pushingpixels.flamingo.api.common.CommandButtonPresentationState;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.model.Command;
import org.pushingpixels.flamingo.api.common.model.CommandGroup;
import org.pushingpixels.flamingo.api.common.model.CommandStripPresentationModel;
import org.pushingpixels.flamingo.api.common.model.CommandToggleGroupModel;
import org.pushingpixels.flamingo.api.common.projection.CommandButtonProjection;
import org.pushingpixels.flamingo.api.common.projection.CommandStripProjection;
import org.pushingpixels.flamingo.api.common.projection.Projection;
import org.pushingpixels.flamingo.api.common.projection.Projection.ComponentSupplier;
import org.pushingpixels.flamingo.api.ribbon.JFlowRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand.PresentationPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;
import org.pushingpixels.flamingo.api.ribbon.synapse.projection.ComponentProjection;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.StepperListener;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI.ToolbarOwner;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.TacticalData.SliderConverter;
import MWC.TacticalData.temporal.ControllablePeriod;
import MWC.TacticalData.temporal.PlotOperations;
import MWC.TacticalData.temporal.TimeManager;

public class DebriefRibbonTimeController
{
  /**
   * Class that binds the Time Filter and Time Label. It is used to update the date formatting.
   *
   */
  protected static class DateFormatBinder
  {
    protected LiteStepControl stepControl;
//    protected JLabel minimumValue;
//    protected JLabel maximumValue;
    protected RangeDisplayComponentContentModel rangeDisplayModel;
    protected RangeSlider slider;
    protected TimeManager timeManager;

    public String getDateFormat()
    {
      return stepControl.getDateFormat();
    }

    public void reset()
    {
      rangeDisplayModel.setMinValueText(" ");
      rangeDisplayModel.setMaxValueText(" ");
      slider.setMinimum(0);
      slider.setMaximum(0);
    }

    public void updateFilterDateFormat()
    {
      final Date low = RangeSlider.toDate(slider.getValue()).getTime();
      final Date high = RangeSlider.toDate(slider.getUpperValue()).getTime();

      final SimpleDateFormat formatter = new SimpleDateFormat(stepControl
          .getDateFormat());
      formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
      rangeDisplayModel.setMinValueText(formatter.format(low));
      rangeDisplayModel.setMaxValueText(formatter.format(high));
    }

    public void updateTimeDateFormat(final String format,
        final boolean updateTimeLabel, final boolean updateFilters)
    {
      if (updateTimeLabel)
      {
        stepControl.setDateFormat(format);
      }
      if (updateFilters)
      {
        updateFilterDateFormat();
      }
    }
  }

  private static class LiteSliderControls implements SliderControls
  {
    private final RangeSlider slider;

    private LiteSliderControls(final RangeSlider slider)
    {
      this.slider = slider;
    }

    @Override
    public HiResDate getToolboxEndTime()
    {
      final long val = slider.getUpperDate().getTimeInMillis();
      return new HiResDate(val);
    }

    @Override
    public HiResDate getToolboxStartTime()
    {
      final long val = slider.getLowerDate().getTimeInMillis();
      return new HiResDate(val);
    }

    @Override
    public void setEnabled(final boolean enabled)
    {
      slider.setEnabled(enabled);
    }

    @Override
    public void setToolboxEndTime(final HiResDate val)
    {
      final GregorianCalendar cal = new GregorianCalendar();
      cal.setTimeInMillis(val.getDate().getTime());
      slider.setMaximum(cal);
      slider.setUpperDate(cal);
    }

    @Override
    public void setToolboxStartTime(final HiResDate val)
    {
      final GregorianCalendar cal = new GregorianCalendar();
      cal.setTimeInMillis(val.getDate().getTime());
      slider.setMinimum(cal);
      slider.setLowerDate(cal);
    }
  }

  private static abstract class LiteStepperListener implements StepperListener
  {
    private final AbstractCommandButton _playBtn;

    private LiteStepperListener(final AbstractCommandButton playCommandButton)
    {
      _playBtn = playCommandButton;
    }

    @Override
    public void newTime(final HiResDate oldDTG, final HiResDate newDTG,
        final CanvasType canvas)
    {
      // ignore
    }

    @Override
    public void steppingModeChanged(final boolean on)
    {
      if (_playBtn != null)
      {
        updatePlayBtnUI(_playBtn, !on);
      }
    }
  }

  protected static class ShowFormatAction implements CommandAction
  {
    private final JPopupMenu menu;

    private ShowFormatAction(final JPopupMenu theMenu)
    {
      this.menu = theMenu;
    }

    @Override
    public void commandActivated(CommandActionEvent e)
    {
      // Get the event source
      final Component component = (Component) e.getSource();

      menu.show(component, 0, 0);

      // Get the location of the point 'on the screen'
      final Point p = component.getLocationOnScreen();

      menu.setLocation(p.x, p.y + component.getHeight());
    }
  }

  private static class SliderListener implements ChangeListener
  {
    final private PlotOperations operations;
    final private TimeManager timeManager;
    final private LiteStepControl stepcontrol;

    private SliderListener(final PlotOperations operations,
        final TimeManager time, final LiteStepControl step)
    {
      this.operations = operations;
      this.timeManager = time;
      this.stepcontrol = step;
    }

    @Override
    public void stateChanged(final ChangeEvent e)
    {
      final RangeSlider slider = (RangeSlider) e.getSource();

      final Date low = RangeSlider.toDate(slider.getValue()).getTime();
      final Date high = RangeSlider.toDate(slider.getUpperValue()).getTime();
      formatBinder.slider = slider;
      formatBinder.updateFilterDateFormat();

      operations.setPeriod(new TimePeriod.BaseTimePeriod(new HiResDate(low),
          new HiResDate(high)));

      final HiResDate currentTime = timeManager.getTime();
      if (currentTime != null)
      {
        Date oldTime = currentTime.getDate();
        if (oldTime.before(low))
        {
          oldTime = low;
        }
        if (oldTime.after(high))
        {
          oldTime = high;
        }
        stepcontrol.setEndTime(new HiResDate(high));
        label.setRange(low.getTime(), high.getTime());
        label.setValue(oldTime.getTime());

        // and enable those buttons
      }

      operations.performOperation(ControllablePeriod.FILTER_TO_TIME_PERIOD);
    }
  }

  private static final String START_TEXT = "Start playing";

  private static final String STOP_TEXT = "Stop playing";

  private static final String STOP_IMAGE = "icons/24/media_stop.png";

  private static final String PLAY_IMAGE = "icons/24/media_play.png";

  private static final String[] timeFormats = new String[]
  {"mm:ss.SSS", "HHmm.ss", "HHmm", "ddHHmm", "ddHHmm:ss", "yy/MM/dd HH:mm",
      "yy/MM/dd HH:mm:ss"};

  private static SliderConverter converter = new SliderConverter();

  private static DateFormatBinder formatBinder = new DateFormatBinder();

  private static TimeLabel label;

  private static JCheckBoxMenuItem[] _menuItem;

  /**
   * track snail mode
   *
   */
  private static boolean _isNormal = true;

  public static List<Command> topButtonCommands;


  private static CommandButtonProjection<Command> formatCommandButton;

  private static SliderComponentContentModel timeFilterRangeModel;
  
  private static SliderComponentContentModel timeModel;

  private static Command playCommand;


  protected static void addTimeControllerTab(final JRibbon ribbon,
      final GeoToolMapRenderer _geoMapRenderer,
      final LiteStepControl stepControl, final TimeManager timeManager,
      final PlotOperations operations, final Layers layers,
      final UndoBuffer undoBuffer, final Runnable normalPainter,
      final Runnable snailPainter)
  {
    final JRibbonBand displayMode = createDisplayMode(normalPainter,
        snailPainter);

    final JRibbonBand filterToTime = createFilterToTime(stepControl, operations,
        timeManager);
    

    final JFlowRibbonBand control = createControl(stepControl, timeManager, layers,
        undoBuffer, operations);
//
    final RibbonTask timeTask = new RibbonTask("Time", displayMode,
        control,filterToTime);
    ribbon.addTask(timeTask);
  }

  public static void assignThisTimeFormat(final String format,
      final boolean updateTimeLabel, final boolean updateFilters)
  {
    if (_menuItem != null && format != null)
    {
      for (int i = 0; i < _menuItem.length; i++)
      {
        _menuItem[i].setSelected(format.equals(_menuItem[i].getText()));
      }
      if (formatBinder != null)
      {
        formatBinder.updateTimeDateFormat(format, updateTimeLabel,
            updateFilters);
      }
    }
  }

  private static JFlowRibbonBand createControl(final LiteStepControl stepControl,
      final TimeManager timeManager, final Layers layers,
      final UndoBuffer undoBuffer, final PlotOperations operations)
  {
    final JFlowRibbonBand control = new JFlowRibbonBand("Control", null);

    final JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
  //  controlPanel.setPreferredSize(new Dimension(500, 80));

    
    final Command behindCommand = MenuUtils.createCommandObject(
        "Behind", "icons/24/media_beginning.png", new CommandAction()
        {


          @Override
          public void commandActivated(CommandActionEvent e)
          {
            timeManager.setTime(control, HiResDate.min(operations.getPeriod()
                .getStartDTG(), timeManager.getPeriod().getStartDTG()), true);
            
          }
        }, PresentationPriority.LOW,"starttime");
    behindCommand.project().buildComponent().setName("behind");
    final Command rewindCommand = MenuUtils.createCommandObject(
        "Rewind", "icons/24/media_rewind.png", new CommandAction()
        {
          @Override
          public void commandActivated(CommandActionEvent e)
          {
            stepControl.doStep(false, true);
          }
        }, PresentationPriority.LOW, "Large step backwards");
    rewindCommand.project().buildComponent().setName("rewind");
    final Command backCommand = MenuUtils.createCommandObject("Back",
        "icons/24/media_back.png", new CommandAction()
        {


          @Override
          public void commandActivated(CommandActionEvent e)
          {
            stepControl.doStep(false, false);
          }
        }, PresentationPriority.LOW, "Small step backwards");
    backCommand.project().buildComponent().setName("back");
    playCommand = MenuUtils.createCommandObject("Play",
        PLAY_IMAGE, new CommandAction()
        {


          @Override
          public void commandActivated(CommandActionEvent e)
          {
            final AbstractCommandButton playCommandButton = e.getButtonSource();
            final boolean isPlaying = stepControl.isPlaying();

            stepControl.startStepping(!isPlaying);

            // now update the play button UI
            updatePlayBtnUI(playCommandButton, isPlaying);
          }
        }, PresentationPriority.LOW, START_TEXT);
    final AbstractCommandButton playButton = playCommand.project().buildComponent();
    playButton.setName("play");;
    @SuppressWarnings("unused")
    final Command recordCommandButton = MenuUtils.createCommandObject(
        "Record", "icons/24/media_record.png", new CommandAction()
        {


          @Override
          public void commandActivated(CommandActionEvent e)
          {
            JOptionPane.showMessageDialog(null,
                "Record to PPT not yet implemented.");

          }
        }, PresentationPriority.LOW, "Start recording");
    recordCommandButton.project().buildComponent().setName("record");
    final Command forwardCommand = MenuUtils.createCommandObject(
        "Forward", "icons/24/media_forward.png", new CommandAction()
        {

          
          @Override
          public void commandActivated(CommandActionEvent e)
          {
            stepControl.doStep(true, false);
          }
        }, PresentationPriority.LOW, "Small step forwards");
    forwardCommand.project().buildComponent().setName("forward");
    
    final Command fastForwardCommand = MenuUtils.createCommandObject(
        "Fast Forward", "icons/24/media_fast_forward.png", new CommandAction()
        {


          @Override
          public void commandActivated(CommandActionEvent e)
          {
            stepControl.doStep(true, true);
          }
        }, PresentationPriority.LOW, "Large step forwards");
    fastForwardCommand.project().buildComponent().setName("fastforward");
    final Command endCommand = MenuUtils.createCommandObject("End",
        "icons/24/media_end.png", new CommandAction()
        {
          
          @Override
          public void commandActivated(CommandActionEvent e)
          {
            timeManager.setTime(control, HiResDate.max(operations.getPeriod()
                .getEndDTG(), timeManager.getPeriod().getEndDTG()), true);
          }
        }, PresentationPriority.LOW, "Move to end time");
    endCommand.project().buildComponent().setName("endtime");
    
    final Command propertiesCommand = MenuUtils.createCommandObject(
        "Properties", "icons/16/properties.png", new CommandAction()
        {
         
          @Override
          public void commandActivated(CommandActionEvent e)
          {
            ToolbarOwner owner = null;
            final ToolParent parent = stepControl.getParent();
            if (parent instanceof ToolbarOwner)
            {
              owner = (ToolbarOwner) parent;
            }
            final Layer parentLayer;
            if (parent instanceof Layer)
            {
              parentLayer = (Layer) parent;
            }
            else
            {
              parentLayer = null;
            }
            final PropertiesDialog dialog = new PropertiesDialog(stepControl
                .getInfo(), layers, undoBuffer, parent, owner, parentLayer);
            dialog.setSize(400, 500);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
          }
        }, PresentationPriority.LOW, "Edit time-step properties");
    propertiesCommand.project().buildComponent().setName("timeprops");
    // we need to give the menu to the command popup
    
    LabelComponentContentModel timeLabelModel = LabelComponentContentModel.builder().
        setText(LiteStepControl.timeFormat).
        setBorder(new LineBorder(Color.black, 5)).
        setForeground(new Color(0, 255, 0)).
        setBackground(Color.black).
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16)).
        setName("timeformatlabel").build();
    final ComponentSupplier<JRibbonLabel,
    LabelComponentContentModel, ComponentPresentationModel> jTimeLabel =
    (Projection<JRibbonLabel, LabelComponentContentModel,
        ComponentPresentationModel> projection) -> JRibbonLabel::new;
    RibbonLabelProjection timeLabelProjection = new RibbonLabelProjection(timeLabelModel,
        ComponentPresentationModel.withDefaults() , 
        jTimeLabel);
    final JLabel timeLabel = timeLabelProjection.buildComponent();
    timeLabel.setPreferredSize(new Dimension(40,18));
    
    
    final JPopupMenu menu = new JPopupMenu();

    formatCommandButton = MenuUtils.addCommandButton(
        "Format", "icons/24/gears_view.png", new ShowFormatAction(menu),
        CommandButtonPresentationState.SMALL, "Format time control");
    formatCommandButton.getContentModel().setActionEnabled(false);
    _menuItem = new JCheckBoxMenuItem[timeFormats.length];
    for (int i = 0; i < timeFormats.length; i++)
    {
      _menuItem[i] = new JCheckBoxMenuItem(timeFormats[i]);
    }

    resetDateFormat();

    final ActionListener selfAssignFormat = new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent e)
      {
        final String format = e.getActionCommand();
        assignThisTimeFormat(format, true, false);
      }
    };

    for (int i = 0; i < timeFormats.length; i++)
    {
      _menuItem[i].addActionListener(selfAssignFormat);
      menu.add(_menuItem[i]);
    }
  
    CommandStripProjection commandStripProjection = new CommandStripProjection(
        new CommandGroup(behindCommand, rewindCommand,
            backCommand, playCommand,forwardCommand,fastForwardCommand,endCommand,propertiesCommand),
        CommandStripPresentationModel.builder()
                .setOrientation(CommandStripPresentationModel.StripOrientation.HORIZONTAL)
                .setHorizontalGapScaleFactor(0.8)
                .setVerticalGapScaleFactor(1.4)
                .build());
    topButtonCommands = commandStripProjection.getContentModel().getCommands();
    setTopCommandsEnabled(topButtonCommands, false);
    control.addFlowComponent(commandStripProjection);
    control.addFlowComponent(timeLabelProjection);
    control.addFlowComponent(formatCommandButton);
    timeModel = SliderComponentContentModel.builder().
        setEnabled(false).
        setChangeListener(new ChangeListener()
        {
          @Override
          public void stateChanged(final ChangeEvent e)
          {
            if(e.getSource() instanceof JSlider) {
              final JSlider slider = (JSlider)e.getSource();
              final int pos = slider.getValue();
              final long time = converter.getTimeAt(pos);
              if (timeManager.getTime() == null || timeManager.getTime().getDate()
                  .getTime() != time)
              {
                timeManager.setTime(slider, new HiResDate(time), true);
              }
            }
          }
        }).
        build();
    //set the values for the slider here.
    final ComponentSupplier<JRibbonSlider,
    SliderComponentContentModel, ComponentPresentationModel> jribbonSlider =
    (Projection<JRibbonSlider, SliderComponentContentModel,
        ComponentPresentationModel> projection) -> JRibbonSlider::new;
    final ComponentProjection<JRibbonSlider,SliderComponentContentModel> projection = 
            new RibbonSliderProjection(timeModel, ComponentPresentationModel.withDefaults(), jribbonSlider);
    JSlider timeSlider = projection.buildComponent();
    timeSlider.setBackground(Color.DARK_GRAY);
    //timeSlider.setPreferredSize(new Dimension(820, 30));
    timeSlider.setName("timeslider");
    control.addFlowComponent(projection);
    label = new TimeLabel()
    {

      @Override
      public void setFontSize(final int newSize)
      {
        final Font originalFont = timeLabel.getFont();
        final Font newFont = new Font(originalFont.getName(), originalFont
            .getStyle(), newSize);
        timeLabel.setFont(newFont);
      }

      @Override
      public void setRange(final long start, final long end)
      {
        // ok, we can use time slider
        timeSlider.setEnabled(true);

        // and we can use the buttons
        //DebriefLiteApp.setState(DebriefLiteApp.ACTIVE_STATE);
        
        converter.init(start, end);
        timeSlider.setMinimum(converter.getStart());
        timeSlider.setMaximum(converter.getEnd());
      }

      @Override
      public void setValue(final long time)
      {
        // find the value
        final int value = converter.getCurrentAt(time);
        timeSlider.setValue(value);
      }

      @Override
      public void setValue(final String text)
      {

        final int completeSize = 17;
        final int diff = completeSize - text.length();

        String newText = text;
        for (int i = 0; i < diff / 2; i++)
        {
          newText = " " + newText + " ";
        }
        if (newText.length() < completeSize)
        {
          newText = newText + " ";
        }
        timeLabelModel.setText(newText);
      }
    };
    stepControl.setTimeLabel(label);

    // we also need to listen to the slider
    

    final DataListener updateTimeController = new DataListener()
    {

      @Override
      public void dataExtended(final Layers theData)
      {
        updateTimeController();
      }

      @Override
      public void dataModified(final Layers theData, final Layer changedLayer)
      {
        updateTimeController();
      }

      @Override
      public void dataReformatted(final Layers theData,
          final Layer changedLayer)
      {
        updateTimeController();
      }

      private void updateTimeController()
      {
        stepControl.startStepping(false);
        boolean hasItems = false;
        boolean hasNarratives = false;
        boolean hasStart = false;
        boolean hasEnd = false;

        final Enumeration<Editable> lIter = stepControl.getLayers().elements();
        while (lIter.hasMoreElements())
        {
          final Editable next = lIter.nextElement();
          if (next instanceof TrackWrapper)
          {
            hasItems = true;
            break;
          }
          else if (next instanceof BaseLayer)
          {
            // check the children, to see if they're like a track
            final BaseLayer baseL = (BaseLayer) next;
            final Enumeration<Editable> ele = baseL.elements();
            while (ele.hasMoreElements() && !hasItems)
            {
              final Editable nextE = ele.nextElement();
              hasItems |= nextE instanceof LightweightTrackWrapper
                  || nextE instanceof DynamicTrackShapeSetWrapper;
              if (!hasItems && nextE instanceof WatchableList)
              {
                hasStart |= ((WatchableList) nextE).getStartDTG() != null;
                hasEnd |= ((WatchableList) nextE).getEndDTG() != null;
              }
            }
          }
          else if (next instanceof NarrativeWrapper)
          {
            // check if we have any narrative item inside.
            final NarrativeWrapper narrativeWrapper = (NarrativeWrapper) next;
            final Enumeration<Editable> elements = narrativeWrapper.elements();
            while (elements.hasMoreElements() && !hasNarratives)
            {
              final Editable nextE = elements.nextElement();
              hasNarratives |= nextE instanceof NarrativeEntry;
            }
          }
          else if (next instanceof WatchableList)
          {
            // look at the date
            final WatchableList wl = (WatchableList) next;
            final HiResDate startDTG = wl.getStartDTG();
            final HiResDate endDTG = wl.getEndDTG();

            // is it a real date?
            hasStart |= startDTG != null;
            hasEnd |= endDTG != null;
          }
        }

        hasItems |= hasStart && hasEnd;
        DebriefLiteApp.setDirty(hasItems || hasNarratives);
        if (hasItems)
        {
          DebriefLiteApp.setState(DebriefLiteApp.ACTIVE_STATE);
          timeSlider.setEnabled(true);
          final TimePeriod period = stepControl.getLayers().getTimePeriod();
          // _myOperations.setPeriod(period);
          timeManager.setPeriod(this, period);
        }
        else
        {
          doSoftReset(timeSlider, timeManager);
        }
      }
    };

    // we also need to listen out for the stepper control mode changing
    stepControl.addStepperListener(new LiteStepperListener(playCommand.project().buildComponent())
    {

      @Override
      public void reset()
      {
        doSoftReset(timeSlider, timeManager);
      }
    });

    stepControl.getLayers().addDataExtendedListener(updateTimeController);
    stepControl.getLayers().addDataModifiedListener(updateTimeController);
    stepControl.getLayers().addDataReformattedListener(updateTimeController);

    final List<RibbonBandResizePolicy> policies = new ArrayList<>();
    policies.add(new CoreRibbonResizePolicies.FlowTwoRows(control));
    control.setResizePolicies(policies);
    return control;
  }

  private static JRibbonBand createDisplayMode(final Runnable normalPainter,
      final Runnable snailPainter)
  {
    final JRibbonBand displayMode = new JRibbonBand("Display Mode", null);
    final CommandToggleGroupModel displayModeGroup =
        new CommandToggleGroupModel();
    MenuUtils.addCommandToggleButton("Normal", "icons/48/normal.png",
        new CommandAction()
        {

          @Override
          public void commandActivated(CommandActionEvent e) {
            normalPainter.run();
            _isNormal = true;
          }
        }, displayMode, PresentationPriority.TOP, true, displayModeGroup,
        true);
    MenuUtils.addCommandToggleButton("Snail", "icons/48/snail.png",
        new CommandAction()
        {

          @Override
          public void commandActivated(CommandActionEvent e)
          {
            snailPainter.run();
            _isNormal = false;
          }
        }, displayMode, PresentationPriority.TOP, true, displayModeGroup,
        false);

    displayMode.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        displayMode));

    return displayMode;
  }

  private static JRibbonBand createFilterToTime(
      final LiteStepControl stepControl, final PlotOperations operations,
      final TimeManager timeManager)
  {
    final JRibbonBand timePeriod = new JRibbonBand("Filter to time", null);

    final Calendar start = new GregorianCalendar(1995, 11, 12);
    final Calendar end = new GregorianCalendar(1995, 11, 12);
    // Now we create the components for the sliders
    timeFilterRangeModel = SliderComponentContentModel.builder().
                                              setEnabled(false).
                                              setMinimum(start).
                                              setMaximum(end).
                                              setChangeListener(new SliderListener(operations,timeManager,stepControl)).
                                              build();
   
    final ComponentSupplier<JRibbonRangeSlider,
    SliderComponentContentModel, ComponentPresentationModel> timeRangeSlider =
    (Projection<JRibbonRangeSlider, SliderComponentContentModel,
            ComponentPresentationModel> projection) -> JRibbonRangeSlider::new;
    final ComponentProjection<JRibbonRangeSlider,SliderComponentContentModel> projection = 
        new RibbonRangeSliderProjection(timeFilterRangeModel, ComponentPresentationModel.withDefaults(), timeRangeSlider);
    RangeSlider filterTimeRangeSlider = projection.buildComponent();
    //set the values for the slider here.
    RangeDisplayComponentContentModel rangeDisplayModel = RangeDisplayComponentContentModel.builder().
        setMinValueText("xxx"+LiteStepControl.timeFormat).build();
    final ComponentSupplier<JRibbonRangeDisplayPanel,
    RangeDisplayComponentContentModel, ComponentPresentationModel> rangeLabel =
    (Projection<JRibbonRangeDisplayPanel,
        RangeDisplayComponentContentModel, ComponentPresentationModel> mxvProjection) -> JRibbonRangeDisplayPanel::new;
    RibbonRangeDisplayPanelProjection rangeDisplayProjection = new RibbonRangeDisplayPanelProjection(rangeDisplayModel,
        ComponentPresentationModel.withDefaults() , 
        rangeLabel);
    JRibbonRangeDisplayPanel panel = rangeDisplayProjection.buildComponent();
    formatBinder.stepControl = stepControl;
    formatBinder.rangeDisplayModel = rangeDisplayModel;
    formatBinder.slider = filterTimeRangeSlider;
    formatBinder.timeManager = timeManager;
    formatBinder.updateFilterDateFormat();
    filterTimeRangeSlider.setEnabled(false);

    
    timePeriod.addRibbonComponent(projection);
    timePeriod.addRibbonComponent(rangeDisplayProjection);
   // rangeDisplayModel.setBackgroundColor(Color.white);
    // tie in to the stepper
    final SliderControls iSlider = new LiteSliderControls(filterTimeRangeSlider);
    stepControl.setSliderControls(iSlider);

    // listen out for time being reset
    // we also need to listen out for the stepper control mode changing
    stepControl.addStepperListener(new LiteStepperListener(null)
    {

      @Override
      public void reset()
      {
        rangeDisplayModel.setMaxValueText(" ");
        rangeDisplayModel.setMinValueText(" ");
        
      }
    });
    return timePeriod;
  }

  public static void doSoftReset(final JSlider timeSlider,
      final TimeManager timeManager)
  {
    // move the slider to the start
    timeSlider.setValue(0);
    label.setValue(LiteStepControl.timeFormat);

    // ok, do some disabling
    DebriefLiteApp.setState(DebriefLiteApp.INACTIVE_STATE);
    timeSlider.setEnabled(false);
    timeManager.setPeriod(null, null);
    formatBinder.reset();
  }

  /**
   * track if we're in normal mode, or in snail mode
   *
   * @return
   */
  public static boolean isNormalDisplayMode()
  {
    return _isNormal;
  }

  public static void resetDateFormat()
  {
    final String defaultFormat = LiteStepControl.timeFormat;
    if (defaultFormat != null)
    {
      DebriefRibbonTimeController.assignThisTimeFormat(defaultFormat, false,
          false);

      formatBinder.stepControl.setDateFormat(defaultFormat);
      formatBinder.updateFilterDateFormat();
    }

    if (label != null)
    {
      label.setValue(defaultFormat);
    }

  }
  
  private static void setTopCommandsEnabled(List<Command> commands,
      final boolean enabled)
  {
    commands.forEach(command->command.setActionEnabled(enabled));
  }

  /**
   * convenience class to bulk enable/disable controls in a panel
   *
   * @param panel
   * @param enabled
   */
  public static void setButtonsEnabled(List<Command> commands,
      final boolean enabled)
  {
    commands.forEach(command->command.setActionEnabled(enabled));
    timeModel.setEnabled(enabled);
    timeFilterRangeModel.setEnabled(enabled);
    formatCommandButton.getContentModel().setActionEnabled(enabled);
    
  }

  public static void updatePlayBtnUI(final AbstractCommandButton playCommandButton,
      final boolean isPlaying)
  {
    final String image;
    if (isPlaying)
      image = PLAY_IMAGE;
    else
      image = STOP_IMAGE;

    final String tooltip = isPlaying ? STOP_TEXT : START_TEXT;

    final RichTooltip.Builder builder = RichTooltip.builder();
    final RichTooltip richTooltip = builder.setTitle("Timer")
        .addDescriptionSection(tooltip).build();
    playCommandButton.setActionRichTooltip(richTooltip);

    // switch the icon
    final Image playStopinImage = MenuUtils.createImage(image);
    final ImageWrapperResizableIcon imageIcon = ImageWrapperResizableIcon
        .getIcon(playStopinImage, MenuUtils.ICON_SIZE_16);

    playCommandButton.setExtraText(tooltip);
    playCommand.setIconFactory(ResizableIconFactory.factory(imageIcon));
    playCommandButton.repaint();
    
  }
}
