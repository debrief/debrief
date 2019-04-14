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

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.gui.custom.JSelectTrack;
import org.mwc.debrief.lite.gui.custom.JSelectTrackModel;

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PlainPropertyEditor;

public class GraphPanelToolbar extends JPanel implements
    PlainPropertyEditor.EditorUsesToolParent
{

  /**
   *
   */
  private static final long serialVersionUID = 8529947841065977007L;

  public static final String ACTIVE_STATE = "ACTIVE";

  public static final String INACTIVE_STATE = "INACTIVE";

  public static final String STATE_PROPERTY = "STATE";

  /**
   * Busy cursor
   */
  private ToolParent _theParent;
  private ShowTimeVariablePlot3 _xytool;

  private final LiteStepControl _stepControl;

  private final List<JComponent> componentsToDisable = new ArrayList<>();

  private String _state = INACTIVE_STATE;

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

  public GraphPanelToolbar(final LiteStepControl stepControl)
  {
    super(new FlowLayout(FlowLayout.LEFT));
    _stepControl = stepControl;
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

    final JComboBox<String> operationComboBox = new JComboBox<>(new String[]
    {"Depth", "Course", "Speed", "Range", "Bearing", "Bearing Rate Calculation",
        "Rel Brg", "ATB"});
    operationComboBox.setSize(50, 20);

    final List<TrackWrapper> tracks = new ArrayList<>();
    final JSelectTrackModel model = new JSelectTrackModel(tracks);

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
          model.setTracks(tracks);
        }

        @Override
        public void dataReformatted(final Layers theData,
            final Layer changedLayer)
        {

        }
      };
      _stepControl.getLayers().addDataModifiedListener(trackChangeListener);

    }

    final JSelectTrack selectTrack = new JSelectTrack(model);

    final JButton createXYPlotButton = createCommandButton("View XY-Plot",
        "icons/16/sensor_contact.png");
    createXYPlotButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        _xytool = new ShowTimeVariablePlot3(null, _stepControl);

        setState(ACTIVE_STATE);
        _xytool.getData();
      }
    });

    final JButton fixToWindowsButton = createCommandButton(
        "Scale the graph to show all data", "icons/16/fit_to_win.png");
    final JButton viewGridButton = createCommandButton("Switch axes",
        "icons/16/swap_axis.png");
    final JButton viewTimeButton = createCommandButton("Show symbols",
        "icons/16/open.png");
    final JButton hideCrosshair = createCommandButton(
        "Hide the crosshair from the graph (for printing)", "icons/16/fix.png");
    final JButton expandButton = createCommandButton(
        "Expand Period covered in sync with scenario time",
        "icons/16/clock.png");
    final JButton wmfButton = createCommandButton(
        "Produce a WMF file of the graph", "icons/16/ex_2word_256_1.png");
    final JButton placeBitmapButton = createCommandButton(
        "Place a bitmap image of the graph on the clipboard",
        "icons/16/copy_to_clipboard.png");
    final JButton copyGraph = createCommandButton(
        "Copies the graph as a text matrix to the clipboard",
        "icons/16/export.png");
    final JButton propertiesButton = createCommandButton(
        "Change editable properties for this chart", "icons/16/properties.png");
    final JToggleButton autosyncButton = createJToggleButton(
        "Auto-sync with calculated track data", "icons/16/direction.png");
    final JLabel selectTracksLabel = new JLabel("Select Tracks");
    selectTracksLabel.addMouseListener(new MouseListener()
    {

      @Override
      public void mouseClicked(final MouseEvent e)
      {
        // Get the event source
        final Component component = (Component) e.getSource();

        selectTrack.show(component, 0, 0);

        // Get the location of the point 'on the screen'
        final Point p = component.getLocationOnScreen();

        selectTrack.setLocation(p.x, p.y + component.getHeight());
      }

      @Override
      public void mouseEntered(final MouseEvent e)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void mouseExited(final MouseEvent e)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void mousePressed(final MouseEvent e)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void mouseReleased(final MouseEvent e)
      {
        // TODO Auto-generated method stub

      }
    });

    add(createXYPlotButton);
    add(operationComboBox);
    add(selectTracksLabel);

    add(fixToWindowsButton);
    add(viewGridButton);
    add(viewTimeButton);
    add(hideCrosshair);
    add(expandButton);
    add(wmfButton);
    add(placeBitmapButton);
    add(copyGraph);
    add(propertiesButton);
    add(autosyncButton);

    componentsToDisable.addAll(Arrays.asList(new JComponent[]
    {fixToWindowsButton, viewGridButton, viewTimeButton, hideCrosshair,
        expandButton, wmfButton, placeBitmapButton, copyGraph, propertiesButton,
        autosyncButton}));
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

  @Override
  public void setParent(final ToolParent theParent)
  {
    _theParent = theParent;
  }

  public void setState(final String newState)
  {
    final String oldState = _state;
    this._state = newState;

    notifyListenersStateChanged(this, STATE_PROPERTY, oldState, newState);
  }
}
