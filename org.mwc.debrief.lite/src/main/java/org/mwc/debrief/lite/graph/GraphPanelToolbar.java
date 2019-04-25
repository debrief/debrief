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
package org.mwc.debrief.lite.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.TextAnchor;
import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.gui.custom.AbstractTrackConfiguration;
import org.mwc.debrief.lite.gui.custom.AbstractTrackConfiguration.TrackWrapperSelect;
import org.mwc.debrief.lite.gui.custom.JSelectTrack;
import org.mwc.debrief.lite.gui.custom.JSelectTrackModel;
import org.mwc.debrief.lite.gui.custom.SimpleEditablePropertyPanel;

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3;
import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationHolder;
import Debrief.Tools.Tote.Calculations.atbCalc;
import Debrief.Tools.Tote.Calculations.bearingCalc;
import Debrief.Tools.Tote.Calculations.bearingRateCalc;
import Debrief.Tools.Tote.Calculations.courseCalc;
import Debrief.Tools.Tote.Calculations.depthCalc;
import Debrief.Tools.Tote.Calculations.rangeCalc;
import Debrief.Tools.Tote.Calculations.relBearingCalc;
import Debrief.Tools.Tote.Calculations.speedCalc;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.JFreeChart.BearingRateFormatter;
import MWC.GUI.JFreeChart.CourseFormatter;
import MWC.GUI.JFreeChart.DepthFormatter;
import MWC.GUI.JFreeChart.RelBearingFormatter;
import MWC.GUI.JFreeChart.formattingOperation;
import MWC.GenericData.WatchableList;
import MWC.Utilities.XYPlot.XYPlotUtilities;

public class GraphPanelToolbar extends JPanel
{

  /**
   *
   */
  private static final long serialVersionUID = 8529947841065977007L;

  public static final String ACTIVE_STATE = "ACTIVE";

  public static final String INACTIVE_STATE = "INACTIVE";

  public static final String STATE_PROPERTY = "STATE";

  private ShowTimeVariablePlot3 _xytool;

  private final LiteStepControl _stepControl;

  private final List<JComponent> componentsToDisable = new ArrayList<>();

  private AbstractTrackConfiguration selectTrackModel;

  private String _state = INACTIVE_STATE;

  private final SimpleEditablePropertyPanel _xyPanel;

  public PropertyChangeListener enableDisableButtons =
      new PropertyChangeListener()
      {

        @Override
        public void propertyChange(final PropertyChangeEvent event)
        {
          final boolean isActive = ACTIVE_STATE.equals(event.getNewValue());
          for (final JComponent component : componentsToDisable)
          {
            component.setEnabled(isActive);
          }
        }
      };

  private final ArrayList<PropertyChangeListener> stateListeners;

  public GraphPanelToolbar(final LiteStepControl stepControl,
      final SimpleEditablePropertyPanel xyPanel)
  {
    super(new FlowLayout(FlowLayout.LEFT));
    _stepControl = stepControl;
    _xyPanel = xyPanel;
    init();

    stateListeners = new ArrayList<>(Arrays.asList(enableDisableButtons));

    setState(INACTIVE_STATE);
  }

  private JButton createCommandButton(final String command, final String image)
  {
    final URL imageIcon = getClass().getClassLoader().getResource(image);
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon(imageIcon);
    }
    catch (final Exception e)
    {
      System.err.println("Failed to find icon:" + image);
      e.printStackTrace();
    }
    final JButton button = new JButton(icon);
    button.setToolTipText(command);
    return button;
  }

  private JToggleButton createJToggleButton(final String command,
      final String image)
  {
    final URL imageIcon = getClass().getClassLoader().getResource(image);
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon(imageIcon);
    }
    catch (final Exception e)
    {
      System.err.println("Failed to find icon:" + image);
      e.printStackTrace();
    }
    final JToggleButton button = new JToggleButton(icon);
    button.setToolTipText(command);
    return button;
  }

  protected void init()
  {
    formattingOperation theFormatter = null;
    if (relBearingCalc.useUKFormat())
    {
      theFormatter = new RelBearingFormatter();
    }
    else
    {
      theFormatter = null;
    }

    final JComboBox<CalculationHolder> operationComboBox = new JComboBox<>(
        new CalculationHolder[]
        {new CalculationHolder(new depthCalc(), new DepthFormatter(), false, 0),
            new CalculationHolder(new courseCalc(), new CourseFormatter(),
                false, 360), new CalculationHolder(new speedCalc(), null, false,
                    0), new CalculationHolder(new rangeCalc(), null, true, 0),
            new CalculationHolder(new bearingCalc(), null, true, 180),
            new CalculationHolder(new bearingRateCalc(),
                new BearingRateFormatter(), true, 180), new CalculationHolder(
                    new relBearingCalc(), theFormatter, true, 180),
            new CalculationHolder(new atbCalc(), theFormatter, true, 180)});
    operationComboBox.setSize(50, 20);
    operationComboBox.addItemListener(new ItemListener()
    {

      @Override
      public void itemStateChanged(final ItemEvent event)
      {
        selectTrackModel.setOperation((CalculationHolder) event.getItem());
      }
    });

    final List<TrackWrapper> tracks = new ArrayList<>();
    selectTrackModel = new JSelectTrackModel(tracks,
        (CalculationHolder) operationComboBox.getSelectedItem());

    // Re-renderer listener.
    selectTrackModel.addPropertyChangeListener(new PropertyChangeListener()
    {
      @Override
      public void propertyChange(final PropertyChangeEvent arg0)
      {
        _xytool = new ShowTimeVariablePlot3(_xyPanel, _stepControl);
        final CalculationHolder operation =
            (CalculationHolder) operationComboBox.getSelectedItem();
        _xytool.setPreselectedOperation(operation);

        Vector<WatchableList> selectedTracksByUser = null;

        if (selectTrackModel != null && _stepControl != null && _stepControl
            .getStartTime() != null && _stepControl.getEndTime() != null)
        {
          _xytool.setPreselectedPrimaryTrack(selectTrackModel
              .getPrimaryTrack());
          final List<TrackWrapperSelect> tracks = selectTrackModel.getTracks();
          selectedTracksByUser = new Vector<>();
          for (final TrackWrapperSelect currentTrack : tracks)
          {
            if (currentTrack.selected)
            {
              selectedTracksByUser.add(currentTrack.track);
            }
          }

          if (!selectedTracksByUser.isEmpty() && (!operation
              .isARelativeCalculation() || selectTrackModel
                  .getPrimaryTrack() != null))
          {
            _xytool.setTracks(selectedTracksByUser);
            _xytool.setPeriod(_stepControl.getStartTime(), _stepControl
                .getEndTime());

            // _xytool
            _xytool.getData();
            setState(ACTIVE_STATE);
          }
        }
      }
    });
    
    _stepControl.getLayers().addDataExtendedListener(new DataListener()
    {
      
      @Override
      public void dataReformatted(Layers theData, Layer changedLayer)
      {
        System.out.println("Called dataReformatted");
      }
      
      @Override
      public void dataModified(Layers theData, Layer changedLayer)
      {
        System.out.println("Called dataModified");
        _xytool = new ShowTimeVariablePlot3(_xyPanel, _stepControl);
        final CalculationHolder operation =
            (CalculationHolder) operationComboBox.getSelectedItem();
        _xytool.setPreselectedOperation(operation);

        Vector<WatchableList> selectedTracksByUser = null;

        if (selectTrackModel != null && _stepControl != null && _stepControl
            .getStartTime() != null && _stepControl.getEndTime() != null)
        {
          _xytool.setPreselectedPrimaryTrack(selectTrackModel
              .getPrimaryTrack());
          final List<TrackWrapperSelect> tracks = selectTrackModel.getTracks();
          selectedTracksByUser = new Vector<>();
          for (final TrackWrapperSelect currentTrack : tracks)
          {
            if (currentTrack.selected)
            {
              selectedTracksByUser.add(currentTrack.track);
            }
          }

          if (!selectedTracksByUser.isEmpty() && (!operation
              .isARelativeCalculation() || selectTrackModel
                  .getPrimaryTrack() != null))
          {
            _xytool.setTracks(selectedTracksByUser);
            _xytool.setPeriod(_stepControl.getStartTime(), _stepControl
                .getEndTime());

            // _xytool
            _xytool.getData();
            setState(ACTIVE_STATE);
          }
        }
      }
      
      @Override
      public void dataExtended(Layers theData)
      {
        System.out.println("Called dataExtended");
      }
    });

    if (_stepControl != null && _stepControl.getLayers() != null)
    {

      final DataListener trackChangeListener = new DataListener()
      {

        @Override
        public void dataExtended(final Layers theData)
        {

        }

        @Override
        public void dataModified(final Layers theData, final Layer changedLayer)
        {
          final Enumeration<Editable> elem = _stepControl.getLayers()
              .elements();
          while (elem.hasMoreElements())
          {
            final Editable nextItem = elem.nextElement();
            if (nextItem instanceof TrackWrapper)
            {
              tracks.add((TrackWrapper) nextItem);
            }
          }
          selectTrackModel.setTracks(tracks);
        }

        @Override
        public void dataReformatted(final Layers theData,
            final Layer changedLayer)
        {

        }
      };
      _stepControl.getLayers().addDataModifiedListener(trackChangeListener);

    }

    final JSelectTrack selectTrack = new JSelectTrack(selectTrackModel);

    final JButton fixToWindowsButton = createCommandButton(
        "Scale the graph to show all data", "icons/16/fit_to_win.png");
    fixToWindowsButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        _xytool.getGeneratedChartPanel().getChart().getPlot().zoom(.0);
      }
    });

    // final JToggleButton switchAxesButton = createJToggleButton("Switch axes",
    // "icons/16/swap_axis.png");
    // switchAxesButton.addActionListener(new ActionListener()
    // {
    //
    // @Override
    // public void actionPerformed(final ActionEvent e)
    // {
    // if (switchAxesButton.isSelected())
    // {
    // _xytool.getGeneratedChartPanel().getChart().getXYPlot()
    // .setOrientation(PlotOrientation.HORIZONTAL);
    // }
    // else
    // {
    // _xytool.getGeneratedChartPanel().getChart().getXYPlot()
    // .setOrientation(PlotOrientation.VERTICAL);
    // }
    // }
    // });

    final String symbolOn = "icons/16/symbol_on.png";
    final String symbolOff = "icons/16/symbol_off.png";
    final JToggleButton showSymbolsButton = createJToggleButton("Show Symbols",
        symbolOff);
    showSymbolsButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        final boolean isSelected = showSymbolsButton.isSelected();
        _xytool.getGeneratedJFreeChart().setShowSymbols(isSelected);
        showSymbolsButton.setIcon(new ImageIcon(isSelected ? getClass()
            .getClassLoader().getResource(symbolOn) : getClass()
                .getClassLoader().getResource(symbolOff)));
        showSymbolsButton.setToolTipText(isSelected ? "Hide Symbols"
            : "Show Symbols");
      }
    });
    final JToggleButton hideCrosshair = createJToggleButton(
        "Show the crosshair from the graph (for printing)", "icons/16/fix.png");

    final XYTextAnnotation _crosshairValueText = new XYTextAnnotation(" ", 0,
        0);
    _crosshairValueText.setTextAnchor(TextAnchor.TOP_LEFT);
    _crosshairValueText.setFont(Defaults.getFont());
    _crosshairValueText.setPaint(Color.black);
    _crosshairValueText.setBackgroundPaint(Color.white);
    hideCrosshair.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent arg0)
      {
        _xytool.getGeneratedChartPanel().getChart().getXYPlot()
            .setDomainCrosshairVisible(hideCrosshair.isSelected());
        _xytool.getGeneratedChartPanel().getChart().getXYPlot()
            .setRangeCrosshairVisible(hideCrosshair.isSelected());

        if (hideCrosshair.isSelected())
        {
          _xytool.getGeneratedJFreeChart().getXYPlot().addAnnotation(
              _crosshairValueText);
          hideCrosshair.setToolTipText("Hide the crosshair from the graph (for printing)");
        }
        else
        {
          _xytool.getGeneratedJFreeChart().getXYPlot().removeAnnotation(
              _crosshairValueText);
          hideCrosshair.setToolTipText("Show the crosshair from the graph (for printing)");
        }

        _xytool.getGeneratedChartPanel().invalidate();
      }
    });
    // final JToggleButton expandButton = createJToggleButton(
    // "Expand Period covered in sync with scenario time",
    // "icons/16/clock.png");
    // expandButton.addActionListener(new ActionListener()
    // {
    //
    // @Override
    // public void actionPerformed(final ActionEvent e)
    // {
    // _xytool.getGeneratedXYPlot().setGrowWithTime(expandButton.isSelected());
    // _xytool.getGeneratedChartPanel().invalidate();
    // }
    // });
    final JButton wmfButton = createCommandButton(
        "Produce a WMF file of the graph", "icons/16/ex_2word_256_1.png");
    wmfButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent arg0)
      {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
        {
          final File dir = fileChooser.getSelectedFile();

          _xytool.getGeneratedSwingPlot().doWMF(dir.getPath());
        }
      }
    });
    final JButton placeBitmapButton = createCommandButton(
        "Place a bitmap image of the graph on the clipboard",
        "icons/16/copy_to_clipboard.png");
    placeBitmapButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        org.mwc.debrief.lite.util.ClipboardUtils.copyToClipboard(_xytool
            .getGeneratedChartPanel());
      }
    });
    final JButton copyGraph = createCommandButton(
        "Copies the graph as a text matrix to the clipboard",
        "icons/16/export.png");
    copyGraph.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        final TimeSeriesCollection dataset = (TimeSeriesCollection) _xytool
            .getGeneratedXYPlot().getDataset();
        XYPlotUtilities.copyToClipboard(_xytool.getGeneratedChartPanel()
            .getName(), dataset);
      }
    });
    final JButton propertiesButton = createCommandButton(
        "Edit Chart Properties", "icons/16/properties.png");

    propertiesButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        _xyPanel.addEditor(_xytool.getGeneratedJFreeChart().getInfo(), null);
      }
    });

    final JComboBox<String> selectTracksLabel = new JComboBox<>(new String[]
    {"Select Tracks"});
    selectTracksLabel.setEnabled(true);
    selectTracksLabel.addMouseListener(new MouseListener()
    {

      @Override
      public void mouseClicked(final MouseEvent e)
      {

      }

      @Override
      public void mouseEntered(final MouseEvent e)
      {

      }

      @Override
      public void mouseExited(final MouseEvent e)
      {

      }

      @Override
      public void mousePressed(final MouseEvent e)
      {
        if (selectTracksLabel.isEnabled())
        {
          // Get the event source
          final Component component = (Component) e.getSource();

          selectTrack.show(component, 0, 0);

          // Get the location of the point 'on the screen'
          final Point p = component.getLocationOnScreen();

          selectTrack.setLocation(p.x, p.y + component.getHeight());
        }
      }

      @Override
      public void mouseReleased(final MouseEvent e)
      {

      }
    });

    add(operationComboBox);
    add(selectTracksLabel);

    add(fixToWindowsButton);
    add(showSymbolsButton);
    add(hideCrosshair);
    add(wmfButton);
    add(placeBitmapButton);
    add(copyGraph);
    add(propertiesButton);

    componentsToDisable.addAll(Arrays.asList(new JComponent[]
    {fixToWindowsButton, showSymbolsButton, hideCrosshair, wmfButton,
        placeBitmapButton, copyGraph, propertiesButton}));
  }

  private void notifyListenersStateChanged(final Object source,
      final String property, final String oldValue, final String newValue)
  {
    for (final PropertyChangeListener event : stateListeners)
    {
      event.propertyChange(new PropertyChangeEvent(source, property, oldValue,
          newValue));
    }
  }

  public void setState(final String newState)
  {
    final String oldState = _state;
    this._state = newState;

    notifyListenersStateChanged(this, STATE_PROPERTY, oldState, newState);
  }
}
