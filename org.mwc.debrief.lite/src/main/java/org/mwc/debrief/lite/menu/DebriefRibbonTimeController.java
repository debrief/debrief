package org.mwc.debrief.lite.menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.gui.LiteStepControl.SliderControls;
import org.mwc.debrief.lite.gui.LiteStepControl.TimeLabel;
import org.mwc.debrief.lite.gui.custom.RangeSlider;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.FlamingoCommand.FlamingoCommandToggleGroup;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.temporal.TimeManager;

public class DebriefRibbonTimeController
{
  private static SliderConverter converter = new SliderConverter();
  
  protected static class ShowFormatAction extends AbstractAction
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      // Get the event source
      final Component component = (Component) e.getSource();

      menu.show(component, 0, 0);

      // Get the location of the point 'on the screen'
      final Point p = component.getLocationOnScreen();

      menu.setLocation(p.x, p.y + component.getHeight());
    }
  }

  static JPopupMenu menu;

  protected static void addTimeControllerTab(final JRibbon ribbon,
      final GeoToolMapRenderer _geoMapRenderer, final LiteStepControl stepControl, TimeManager timeManager)
  {
    final JRibbonBand displayMode = createDisplayMode();

    final JRibbonBand control = createControl(stepControl, timeManager);

    final JRibbonBand filterToTime = createFilterToTime(stepControl);

    final RibbonTask timeTask = new RibbonTask("Time", displayMode, control,
        filterToTime);
    ribbon.addTask(timeTask);
  }

  private static JRibbonBand createControl(final LiteStepControl stepControl, final TimeManager timeManager)
  {
    final JRibbonBand control = new JRibbonBand("Control", null);

    final JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
    controlPanel.setPreferredSize(new Dimension(500, 80));

    final JPanel topButtonsPanel = new JPanel();
    topButtonsPanel.setLayout(new BoxLayout(topButtonsPanel, BoxLayout.X_AXIS));

    final JCommandButton behindCommandButton = MenuUtils.addCommandButton(
        "Behind", "icons/24/media_beginning.png", new AbstractAction() {

          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(ActionEvent e)
          {
            timeManager.setTime(control, timeManager.getPeriod().getStartDTG(), true);
          }},
        CommandButtonDisplayState.SMALL);

    final JCommandButton rewindCommandButton = MenuUtils.addCommandButton(
        "Rewind", "icons/24/media_rewind.png", new AbstractAction() {

          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(ActionEvent e)
          {
            stepControl.doStep(false, true);
          }},
        CommandButtonDisplayState.SMALL);

    final JCommandButton backCommandButton = MenuUtils.addCommandButton("Back",
        "icons/24/media_back.png", new AbstractAction() {

          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(ActionEvent e)
          {
            stepControl.doStep(false, false);
          }},
        CommandButtonDisplayState.SMALL);

    final JCommandButton playCommandButton = MenuUtils.addCommandButton("Play",
        "icons/24/media_play.png", new AbstractAction() {

          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(ActionEvent e)
          {
            // what state are we in?
            
            // switch the icon
            
            // start/stop the timer
          }},
        CommandButtonDisplayState.SMALL);

    final JCommandButton recordCommandButton = MenuUtils.addCommandButton(
        "Record", "icons/24/media_record.png", new MenuUtils.TODOAction(),
        CommandButtonDisplayState.SMALL);

    final JCommandButton forwardCommandButton = MenuUtils.addCommandButton(
        "Forward", "icons/24/media_forward.png", new AbstractAction() {

          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(ActionEvent e)
          {
            stepControl.doStep(true, false);
          }},
        CommandButtonDisplayState.SMALL);

    final JCommandButton fastForwardCommandButton = MenuUtils.addCommandButton(
        "Fast Forward", "icons/24/media_fast_forward.png",
        new AbstractAction() {

          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(ActionEvent e)
          {
            stepControl.doStep(true, true);
          }}, CommandButtonDisplayState.SMALL);

    final JCommandButton endCommandButton = MenuUtils.addCommandButton("End",
        "icons/24/media_end.png", new AbstractAction() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        timeManager.setTime(control, timeManager.getPeriod().getEndDTG(), true);
      }
    },
        CommandButtonDisplayState.SMALL);

    final JCommandButton propertiesCommandButton = MenuUtils.addCommandButton(
        "Properties", "icons/16/properties.png", new MenuUtils.TODOAction(),
        CommandButtonDisplayState.SMALL);

    final JCommandButton formatCommandButton = MenuUtils.addCommandButton(
        "Format", "icons/24/gears_view.png", new ShowFormatAction(),
        CommandButtonDisplayState.SMALL);

    final JLabel timeLabel = new JLabel("       95/12/12 07:45       ");
    timeLabel.setSize(200, 60);
    timeLabel.setPreferredSize(new Dimension(200, 60));
    
    // TODO: couldn't get black bkgnd to show, so switching fore-color
    timeLabel.setForeground(new Color(0, 0, 0));
//    timeLabel.setForeground(new Color(0, 255, 0));
//    timeLabel.setBackground(Color.BLACK);
       
    menu = new JPopupMenu();

    final JMenuItem item1 = new JMenuItem("mm:ss.SSS");
    final JMenuItem item2 = new JMenuItem("HHmm.ss");
    final JMenuItem item3 = new JMenuItem("HHmm");
    final JMenuItem item4 = new JMenuItem("ddHHmm");
    final JMenuItem item5 = new JMenuItem("ddHHmm:ss");
    final JMenuItem item6 = new JMenuItem("yy/MM/dd HH:mm");

    menu.add(item1);
    menu.add(item2);
    menu.add(item3);
    menu.add(item4);
    menu.add(item5);
    menu.add(item6);

    topButtonsPanel.add(behindCommandButton);
    topButtonsPanel.add(rewindCommandButton);
    topButtonsPanel.add(backCommandButton);
    topButtonsPanel.add(playCommandButton);
    topButtonsPanel.add(recordCommandButton);
    topButtonsPanel.add(forwardCommandButton);
    topButtonsPanel.add(fastForwardCommandButton);
    topButtonsPanel.add(endCommandButton);
    topButtonsPanel.add(new JLabel(" | "));
    topButtonsPanel.add(propertiesCommandButton);
    topButtonsPanel.add(timeLabel);
    topButtonsPanel.add(formatCommandButton);

    controlPanel.add(topButtonsPanel);
    final JSlider timeSlider = new JSlider();
    timeSlider.setPreferredSize(new Dimension(420, 30));
    // controlPanel.add(timeSlider);
    

    TimeLabel label = new TimeLabel() {

      @Override
      public void setValue(String text)
      {
        timeLabel.setText(text);
      }

      @Override
      public void setValue(long time)
      {
        // find the value
        final int value = converter.getCurrentAt(time);
        timeSlider.setValue(value);
      }

      @Override
      public void setRange(long start, long end)
      {
        converter.init(start, end);
        timeSlider.setMinimum(converter.getStart());
        timeSlider.setMaximum(converter.getEnd());
      }};
    stepControl.setTimeLabel(label);
    
    // we also need to listen to the slider
    timeSlider.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e)
      {
        final int pos = timeSlider.getValue();
        long time = converter.getTimeAt(pos);
        timeManager.setTime(timeSlider, new HiResDate(time), true);
      }});
    
    control.addRibbonComponent(new JRibbonComponent(topButtonsPanel));
    control.addRibbonComponent(new JRibbonComponent(timeSlider));

    control.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        control));
    return control;
  }
  
  private static class SliderConverter
  {
    private int range;
    private long origin;
    private final int step = 1000 * 60;

    public void init(long start, long end)
    {
      origin = start;
      range = (int) ((end - start) / step);
    }
    
    public int getStart()
    {
      return 0;
    }
    
    public int getEnd()
    {
      return range;
    }
    
    public int getCurrentAt(long now)
    {
      return (int) ((now - origin) / step);
    }
    
    public long getTimeAt(int position)
    {
      return origin + (position * step);
    }
  }

  private static JRibbonBand createDisplayMode()
  {
    final JRibbonBand displayMode = new JRibbonBand("Display Mode", null);
    FlamingoCommandToggleGroup displayModeGroup =
        new FlamingoCommandToggleGroup();
    MenuUtils.addCommandToggleButton("Normal", "icons/48/normal.png",
        new MenuUtils.TODOAction(), displayMode, RibbonElementPriority.TOP,
        true, displayModeGroup, true);
    MenuUtils.addCommandToggleButton("Snail", "icons/48/snail.png",
        new MenuUtils.TODOAction(), displayMode, RibbonElementPriority.TOP,
        true, displayModeGroup, false);

    displayMode.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        displayMode));

    return displayMode;
  }

  private static JRibbonBand createFilterToTime(final LiteStepControl stepControl)
  {
    final JRibbonBand timePeriod = new JRibbonBand("Filter to time", null);

    final SimpleDateFormat formatter = new SimpleDateFormat("MMddyy");
    
    Calendar start = new GregorianCalendar(2013, 0, 1);
    Calendar end = new GregorianCalendar(2013, 1, 15);
    // Now we create the components for the sliders
    final JLabel minimumValue = new JLabel(formatter.format(start.getTime()));
    final JLabel maximumValue = new JLabel(formatter.format(end.getTime()));
    final RangeSlider slider = new RangeSlider(start, end);
    slider.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(final ChangeEvent e)
      {
        // TODO Do we represent the filter using the format specified by user?
        final RangeSlider slider = (RangeSlider) e.getSource();

        minimumValue.setText(formatter.format(new Date(slider.getValue()
            * 1000L)));
        maximumValue.setText(formatter.format(new Date(slider.getUpperValue()
            * 1000L)));
      }
    });

    final JPanel sliderPanel = new JPanel();
    sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
    sliderPanel.setPreferredSize(new Dimension(200, 200));

    // Label's panel
    final JPanel valuePanel = new JPanel();
    valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.X_AXIS));

    valuePanel.add(minimumValue);
    valuePanel.add(Box.createGlue());
    valuePanel.add(maximumValue);
    valuePanel.setPreferredSize(new Dimension(200, 200));

    timePeriod.addRibbonComponent(new JRibbonComponent(slider));
    timePeriod.addRibbonComponent(new JRibbonComponent(valuePanel));
    
    // tie in to the stepper
    SliderControls iSlider = new LiteStepControl.SliderControls()
    {

      @Override
      public HiResDate getToolboxStartTime()
      {
        long val = slider.getLowerDate().getTimeInMillis();
        return new HiResDate(val);
      }

      @Override
      public HiResDate getToolboxEndTime()
      {
        long val = slider.getUpperDate().getTimeInMillis();
        return new HiResDate(val);
      }

      @Override
      public void setToolboxStartTime(HiResDate val)
      {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(val.getDate().getTime());
        slider.setLowerDate(cal);
      }

      @Override
      public void setToolboxEndTime(HiResDate val)
      {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(val.getDate().getTime());
        slider.setUpperDate(cal);
      }
    };
    
    stepControl.setSliderControls(iSlider);

    return timePeriod;
  }
}
