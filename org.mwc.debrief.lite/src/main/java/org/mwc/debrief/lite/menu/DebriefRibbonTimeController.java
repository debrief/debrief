package org.mwc.debrief.lite.menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
import org.mwc.debrief.lite.properties.PropertiesDialog;
import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.FlamingoCommand.FlamingoCommandToggleGroup;
import org.pushingpixels.flamingo.api.common.RichTooltip.RichTooltipBuilder;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import Debrief.GUI.Tote.Painters.SnailPainter;
import Debrief.GUI.Tote.Painters.TotePainter;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI.ToolbarOwner;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.TacticalData.temporal.ControllablePeriod;
import MWC.TacticalData.temporal.PlotOperations;
import MWC.TacticalData.temporal.TimeManager;

public class DebriefRibbonTimeController
{

  /**
   * Class that binds the Time Filter and Time Label.
   * It is used to update the date formatting.
   *
   */
  protected static class DateFormatBinder
  {
    protected LiteStepControl stepControl;
    protected JLabel minimumValue;
    protected JLabel maximumValue;
    protected RangeSlider slider;
    protected TimeManager timeManager;

    public void updateTimeDateFormat(final String format)
    {
      stepControl.setDateFormat(format);
      timeManager.fireTimePropertyChange();
      updateFilterDateFormat();
    }

    public void updateFilterDateFormat()
    {
      Date low = RangeSlider.toDate(slider.getValue()).getTime();
      Date high = RangeSlider.toDate(slider.getUpperValue()).getTime();

      final SimpleDateFormat formatter = new SimpleDateFormat(stepControl
          .getDateFormat());
      minimumValue.setText(formatter.format(low));
      maximumValue.setText(formatter.format(high));
    }

  }

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

  /**
   * utility class to handle converting between slider range and time values
   *
   * @author ian
   *
   */
  private static class SliderConverter
  {
    private int range;
    private long origin;
    // have one minute steps
    private final int step = 1000 * 60;

    public int getCurrentAt(final long now)
    {
      return (int) ((now - origin) / step);
    }

    public int getEnd()
    {
      return range;
    }

    public int getStart()
    {
      return 0;
    }

    public long getTimeAt(final int position)
    {
      return origin + (position * step);
    }

    public void init(final long start, final long end)
    {
      origin = start;
      range = (int) ((end - start) / step);
    }
  }

  private static SliderConverter converter = new SliderConverter();

  private static DateFormatBinder formatBinder = new DateFormatBinder();
  static JPopupMenu menu;

  protected static void addTimeControllerTab(final JRibbon ribbon,
      final GeoToolMapRenderer _geoMapRenderer,
      final LiteStepControl stepControl, final TimeManager timeManager,
      final PlotOperations operations, final Layers layers,
      final UndoBuffer undoBuffer)
  {
    final JRibbonBand displayMode = createDisplayMode(stepControl);

    final JRibbonBand control = createControl(stepControl, timeManager, layers,
        undoBuffer);

    final JRibbonBand filterToTime = createFilterToTime(stepControl, operations,
        timeManager);

    final RibbonTask timeTask = new RibbonTask("Time", displayMode, control,
        filterToTime);
    ribbon.addTask(timeTask);
  }

  private static JRibbonBand createControl(final LiteStepControl stepControl,
      final TimeManager timeManager, final Layers layers,
      final UndoBuffer undoBuffer)
  {
    final JRibbonBand control = new JRibbonBand("Control", null);

    final JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
    controlPanel.setPreferredSize(new Dimension(500, 80));

    final JPanel topButtonsPanel = new JPanel();
    topButtonsPanel.setLayout(new BoxLayout(topButtonsPanel, BoxLayout.X_AXIS));

    final JCommandButton behindCommandButton = MenuUtils.addCommandButton(
        "Behind", "icons/24/media_beginning.png", new AbstractAction()
        {

          /**
           *
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e)
          {
            timeManager.setTime(control, timeManager.getPeriod().getStartDTG(),
                true);
          }
        }, CommandButtonDisplayState.SMALL, "Move to start time");

    final JCommandButton rewindCommandButton = MenuUtils.addCommandButton(
        "Rewind", "icons/24/media_rewind.png", new AbstractAction()
        {

          /**
           *
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e)
          {
            stepControl.doStep(false, true);
          }
        }, CommandButtonDisplayState.SMALL, "Large step backwards");

    final JCommandButton backCommandButton = MenuUtils.addCommandButton("Back",
        "icons/24/media_back.png", new AbstractAction()
        {

          /**
           *
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e)
          {
            stepControl.doStep(false, false);
          }
        }, CommandButtonDisplayState.SMALL, "Small step backwards");

    final JCommandButton playCommandButton = MenuUtils.addCommandButton("Play",
        "icons/24/media_play.png", new AbstractAction()
        {

          /**
           *
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e)
          {
            // ignore, we define the action once we've finished creating the button
          }
        }, CommandButtonDisplayState.SMALL, "Start playing");

    playCommandButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        // what state are we in?
        final boolean isPlaying = stepControl.isPlaying();

        stepControl.startStepping(!isPlaying);

        final String image;
        if (isPlaying)
          image = "icons/24/media_play.png";
        else
          image = "icons/24/media_stop.png";

        final String tooltip = isPlaying ? "Stop playing" : "Start playing";

        RichTooltipBuilder builder = new RichTooltipBuilder();
        RichTooltip richTooltip = builder.setTitle("Timer")
            .addDescriptionSection(tooltip).build();
        playCommandButton.setActionRichTooltip(richTooltip);

        // switch the icon
        final Image zoominImage = MenuUtils.createImage(image);
        final ImageWrapperResizableIcon imageIcon = ImageWrapperResizableIcon
            .getIcon(zoominImage, MenuUtils.ICON_SIZE_16);

        playCommandButton.setIcon(imageIcon);
      }
    });

    final JCommandButton recordCommandButton = MenuUtils.addCommandButton(
        "Record", "icons/24/media_record.png", new AbstractAction()
        {

          /**
           *
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e)
          {
            JOptionPane.showMessageDialog(null,
                "Record to PPT not yet implemented.");

          }
        }, CommandButtonDisplayState.SMALL, "Start recording");

    final JCommandButton forwardCommandButton = MenuUtils.addCommandButton(
        "Forward", "icons/24/media_forward.png", new AbstractAction()
        {

          /**
           *
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e)
          {
            stepControl.doStep(true, false);
          }
        }, CommandButtonDisplayState.SMALL, "Small step forwards");

    final JCommandButton fastForwardCommandButton = MenuUtils.addCommandButton(
        "Fast Forward", "icons/24/media_fast_forward.png", new AbstractAction()
        {

          /**
           *
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e)
          {
            stepControl.doStep(true, true);
          }
        }, CommandButtonDisplayState.SMALL, "Large step forwards");

    final JCommandButton endCommandButton = MenuUtils.addCommandButton("End",
        "icons/24/media_end.png", new AbstractAction()
        {
          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e)
          {
            timeManager.setTime(control, timeManager.getPeriod().getEndDTG(),
                true);
          }
        }, CommandButtonDisplayState.SMALL, "Move to end time");

    final JCommandButton propertiesCommandButton = MenuUtils.addCommandButton(
        "Properties", "icons/16/properties.png", new AbstractAction()
        {
          /**
           * 
           */
          private static final long serialVersionUID = 1973993003498667463L;

          @Override
          public void actionPerformed(ActionEvent arg0)
          {
            ToolbarOwner owner = null;
            ToolParent parent = stepControl.getParent();
            if (parent instanceof ToolbarOwner)
            {
              owner = (ToolbarOwner) parent;
            }

            PropertiesDialog dialog = new PropertiesDialog(stepControl, layers,
                undoBuffer, parent, owner);
            dialog.setSize(400, 500);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
          }
        }, CommandButtonDisplayState.SMALL, "Edit time-step properties");

    final JCommandButton formatCommandButton = MenuUtils.addCommandButton(
        "Format", "icons/24/gears_view.png", new ShowFormatAction(),
        CommandButtonDisplayState.SMALL, "Format time control");

    final JLabel timeLabel = new JLabel("YY/MM/dd hh:mm:ss")
    {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      @Override
      protected void paintComponent(final Graphics g)
      {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
      }
    };
    timeLabel.setSize(200, 60);
    timeLabel.setPreferredSize(new Dimension(200, 60));
    timeLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

    timeLabel.setForeground(new Color(0, 255, 0));

    menu = new JPopupMenu();

    final ActionListener selfAssignFormat = new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        formatBinder.updateTimeDateFormat(e.getActionCommand());
      }
    };

    final String[] timeFormats = new String[]
    {"yy/MM/dd hh:mm:ss", "  yy/MM/dd HH:mm ", "    mm:ss.SSS    ",
        "    ddHHmm:ss    ", "     HHmm.ss     ", "      ddHHmm     ",
        "       HHmm      ",};
    for (final String format : timeFormats)
    {
      JMenuItem menuItem = new JMenuItem(format);
      menuItem.addActionListener(selfAssignFormat);
      menu.add(menuItem);
    }

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
    timeSlider.setEnabled(false);

    final TimeLabel label = new TimeLabel()
    {

      @Override
      public void setRange(final long start, final long end)
      {
        // ok, we can use time slider
        timeSlider.setEnabled(true);

        // and we can use the buttons
        setButtonsEnabled(topButtonsPanel, true);

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
        timeLabel.setText(text);
      }
    };
    stepControl.setTimeLabel(label);

    // we also need to listen to the slider
    timeSlider.addChangeListener(new ChangeListener()
    {

      @Override
      public void stateChanged(final ChangeEvent e)
      {
        final int pos = timeSlider.getValue();
        final long time = converter.getTimeAt(pos);
        timeManager.setTime(timeSlider, new HiResDate(time), true);
      }
    });

    // ok, start off with the buttons disabled
    setButtonsEnabled(topButtonsPanel, false);

    control.addRibbonComponent(new JRibbonComponent(topButtonsPanel));
    control.addRibbonComponent(new JRibbonComponent(timeSlider));

    control.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        control));
    return control;
  }

  private static JRibbonBand createDisplayMode(final LiteStepControl stepControl)
  {
    final JRibbonBand displayMode = new JRibbonBand("Display Mode", null);
    final FlamingoCommandToggleGroup displayModeGroup =
        new FlamingoCommandToggleGroup();
    ActionListener selectNormal = new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        stepControl.setPainter(TotePainter.NORMAL_PAINTER);
      }
    };
    ActionListener selectSnail= new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        stepControl.setPainter(SnailPainter.SNAIL_NAME);
      }
    };
    MenuUtils.addCommandToggleButton("Normal", "icons/48/normal.png",
        selectNormal, displayMode, RibbonElementPriority.TOP,
        true, displayModeGroup, true);
    MenuUtils.addCommandToggleButton("Snail", "icons/48/snail.png",
        selectSnail, displayMode, RibbonElementPriority.TOP,
        true, displayModeGroup, false);

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
    final JLabel minimumValue = new JLabel();
    final JLabel maximumValue = new JLabel();
    final RangeSlider slider = new RangeSlider(start, end);

    formatBinder.stepControl = stepControl;
    formatBinder.maximumValue = maximumValue;
    formatBinder.minimumValue = minimumValue;
    formatBinder.slider = slider;
    formatBinder.timeManager = timeManager;

    formatBinder.updateFilterDateFormat();
    slider.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(final ChangeEvent e)
      {
        final RangeSlider slider = (RangeSlider) e.getSource();

        Date low = RangeSlider.toDate(slider.getValue()).getTime();
        Date high = RangeSlider.toDate(slider.getUpperValue()).getTime();
        formatBinder.updateFilterDateFormat();

        operations.setPeriod(new TimePeriod.BaseTimePeriod(new HiResDate(low),
            new HiResDate(high)));

        operations.performOperation(ControllablePeriod.FILTER_TO_TIME_PERIOD);
      }
    });
    slider.setEnabled(false);
    slider.setPreferredSize(new Dimension(250, 200));

    final JPanel sliderPanel = new JPanel();
    sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
    sliderPanel.setPreferredSize(new Dimension(250, 200));

    // Label's panel
    final JPanel valuePanel = new JPanel();
    valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.X_AXIS));

    valuePanel.add(minimumValue);
    valuePanel.add(Box.createGlue());
    valuePanel.add(maximumValue);
    valuePanel.setPreferredSize(new Dimension(250, 200));

    timePeriod.addRibbonComponent(new JRibbonComponent(slider));
    timePeriod.addRibbonComponent(new JRibbonComponent(valuePanel));

    // tie in to the stepper
    final SliderControls iSlider = new LiteStepControl.SliderControls()
    {

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

      @Override
      public void setEnabled(boolean enabled)
      {
        slider.setEnabled(enabled);
      }
    };

    stepControl.setSliderControls(iSlider);

    return timePeriod;
  }

  /**
   * convenience class to bulk enable/disable controls in a panel
   *
   * @param panel
   * @param enabled
   */
  private static void setButtonsEnabled(final JPanel panel,
      final boolean enabled)
  {
    final Component[] items = panel.getComponents();
    for (final Component item : items)
    {
      final boolean state = item.isEnabled();
      if (state != enabled)
      {
        item.setEnabled(enabled);
      }
    }
  }
}
