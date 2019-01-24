package org.mwc.debrief.lite.menu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
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

import org.mwc.debrief.lite.gui.custom.RangeSlider;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

public class DebriefRibbonTimeController
{
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
      final GeoToolMapRenderer _geoMapRenderer)
  {
    final JRibbonBand displayMode = createDisplayMode();

    final JRibbonBand control = createControl();

    final JRibbonBand filterToTime = createFilterToTime();

    final RibbonTask timeTask = new RibbonTask("Time", displayMode, control,
        filterToTime);
    ribbon.addTask(timeTask);
  }

  private static JRibbonBand createControl()
  {
    final JRibbonBand control = new JRibbonBand("Control", null);

    final JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
    controlPanel.setPreferredSize(new Dimension(500, 80));

    final JPanel topButtonsPanel = new JPanel();
    topButtonsPanel.setLayout(new BoxLayout(topButtonsPanel, BoxLayout.X_AXIS));

    final JCommandButton behindCommandButton = MenuUtils.addCommandButton(
        "Behind", "icons/24/media_beginning.png", new MenuUtils.TODOAction(),
        CommandButtonDisplayState.SMALL);

    final JCommandButton rewindCommandButton = MenuUtils.addCommandButton(
        "Rewind", "icons/24/media_rewind.png", new MenuUtils.TODOAction(),
        CommandButtonDisplayState.SMALL);

    final JCommandButton backCommandButton = MenuUtils.addCommandButton("Back",
        "icons/24/media_back.png", new MenuUtils.TODOAction(),
        CommandButtonDisplayState.SMALL);

    final JCommandButton playCommandButton = MenuUtils.addCommandButton("Play",
        "icons/24/media_play.png", new MenuUtils.TODOAction(),
        CommandButtonDisplayState.SMALL);

    final JCommandButton recordCommandButton = MenuUtils.addCommandButton(
        "Record", "icons/24/media_record.png", new MenuUtils.TODOAction(),
        CommandButtonDisplayState.SMALL);

    final JCommandButton forwardCommandButton = MenuUtils.addCommandButton(
        "Forward", "icons/24/media_forward.png", new MenuUtils.TODOAction(),
        CommandButtonDisplayState.SMALL);

    final JCommandButton fastForwardCommandButton = MenuUtils.addCommandButton(
        "Fast Forward", "icons/24/media_fast_forward.png",
        new MenuUtils.TODOAction(), CommandButtonDisplayState.SMALL);

    final JCommandButton endCommandButton = MenuUtils.addCommandButton("End",
        "icons/24/media_end.png", new MenuUtils.TODOAction(),
        CommandButtonDisplayState.SMALL);

    final JCommandButton propertiesCommandButton = MenuUtils.addCommandButton(
        "Properties", "icons/16/properties.png", new MenuUtils.TODOAction(),
        CommandButtonDisplayState.SMALL);

    final JCommandButton formatCommandButton = MenuUtils.addCommandButton(
        "Format", "icons/24/gears_view.png", new ShowFormatAction(),
        CommandButtonDisplayState.SMALL);

    final JLabel timeLabel = new JLabel("95/12/12 07:45");

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
    topButtonsPanel.add(propertiesCommandButton);
    topButtonsPanel.add(timeLabel);
    topButtonsPanel.add(formatCommandButton);

    controlPanel.add(topButtonsPanel);
    final JSlider timeSlider = new JSlider();
    timeSlider.setPreferredSize(new Dimension(420, 30));
    // controlPanel.add(timeSlider);

    control.addRibbonComponent(new JRibbonComponent(topButtonsPanel));
    control.addRibbonComponent(new JRibbonComponent(timeSlider));

    control.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        control));
    return control;
  }

  private static JRibbonBand createDisplayMode()
  {
    final JRibbonBand displayMode = new JRibbonBand("Display Mode", null);
    MenuUtils.addCommand("Normal", "icons/48/normal.png",
        new MenuUtils.TODOAction(), displayMode, RibbonElementPriority.TOP);
    MenuUtils.addCommand("Snail", "icons/48/snail.png",
        new MenuUtils.TODOAction(), displayMode, RibbonElementPriority.TOP);

    displayMode.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        displayMode));

    return displayMode;
  }

  private static JRibbonBand createFilterToTime()
  {
    final JRibbonBand timePeriod = new JRibbonBand("Filter to time", null);

    // Now we create the components for the sliders
    final JLabel minimumValue = new JLabel(" ");
    final JLabel maximumValue = new JLabel(" ");
    final RangeSlider slider = new RangeSlider(new GregorianCalendar(2013, 0,
        0), new GregorianCalendar(2013, 1, 15));
    slider.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(final ChangeEvent e)
      {
        // TODO Do we represent the filter using the format specified by user?
        final RangeSlider slider = (RangeSlider) e.getSource();
        final SimpleDateFormat formatter = new SimpleDateFormat("MMddyy");

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

    return timePeriod;
  }
}
