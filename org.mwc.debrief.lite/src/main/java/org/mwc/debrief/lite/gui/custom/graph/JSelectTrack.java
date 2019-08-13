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
package org.mwc.debrief.lite.gui.custom.graph;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;

import org.mwc.debrief.lite.gui.custom.AbstractSelection;

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationHolder;
import Debrief.Wrappers.TrackWrapper;

public class JSelectTrack extends JPopupMenu
{
  /**
   * Generated UID
   */
  private static final long serialVersionUID = -1490664356576661371L;
  private final AbstractTrackConfiguration _model;
  private final TreeMap<TrackWrapper, JCheckBox> _displayComponents;
  private final TreeMap<TrackWrapper, JRadioButton> _relatedToComponentsMap;
  private final HashSet<Component> _relativeToComponents;

  private final JLabel _displayLabel = new JLabel("Display");
  private final JLabel _inRelationToLabel = new JLabel("In Relation to");

  public JSelectTrack(final AbstractTrackConfiguration model)
  {
    super();
    this._model = model;

    _displayComponents = new TreeMap<>();
    _relatedToComponentsMap = new TreeMap<>();
    _relativeToComponents = new HashSet<>();

    initializeComponents();

    this._model.addPropertyChangeListener(new PropertyChangeListener()
    {

      @Override
      public void propertyChange(final PropertyChangeEvent evt)
      {
        if (JSelectTrackModel.PRIMARY_CHANGED.equals(evt.getPropertyName()))
        {
          final TrackWrapper newPrimary = (TrackWrapper) evt.getNewValue();
          final JRadioButton component = _relatedToComponentsMap.get(
              newPrimary);
          if (component != null)
          {
            component.setSelected(true);
          }
          for (final JCheckBox checkbox : _displayComponents.values())
          {
            checkbox.setEnabled(true);
          }
          final JCheckBox checkBoxComponent = _displayComponents.get(
              newPrimary);
          checkBoxComponent.setEnabled(false);
        }
        else if (JSelectTrackModel.TRACK_SELECTION.equals(evt
            .getPropertyName()))
        {
          final TrackWrapper track = (TrackWrapper) evt.getSource();
          final JCheckBox component = _displayComponents.get(track);
          if (component != null)
          {
            component.setSelected((Boolean) evt.getNewValue());
          }
        }
        else if (JSelectTrackModel.TRACK_LIST_CHANGED.equals(evt
            .getPropertyName()))
        {
          initializeComponents();
        }
        else if (JSelectTrackModel.OPERATION_CHANGED.equals(evt
            .getPropertyName()))
        {
          final CalculationHolder newOperation = (CalculationHolder) evt
              .getNewValue();
          for (final Component comp : _relativeToComponents)
          {
            comp.setVisible(newOperation.isARelativeCalculation());
          }
          if (!newOperation.isARelativeCalculation())
          {
            TrackWrapper primary = model.getPrimaryTrack();
            if (primary != null)
            {
              _displayComponents.get(primary).setEnabled(true);
            }
            else
            {
              // We don't have a primary track selected. Maybe it is not really needed
              for (final JCheckBox checkbox : _displayComponents.values())
              {
                checkbox.setEnabled(true);
              }
            }
          }
          else
          {
            TrackWrapper primary = model.getPrimaryTrack();
            if (primary != null)
            {
              _displayComponents.get(primary).setEnabled(false);
            }
          }
        }
      }
    });
  }

  public void initializeComponents()
  {
    this.removeAll();
    final BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
    setLayout(layout);
    _displayComponents.clear();
    _relatedToComponentsMap.clear();

    add(_displayLabel);
    final Font previousFont = _displayLabel.getFont();
    _displayLabel.setFont(new Font(previousFont.getName(), previousFont
        .getStyle(), 9));
    _inRelationToLabel.setFont(new Font(previousFont.getName(), previousFont
        .getStyle(), 9));

    for (final AbstractSelection<TrackWrapper> track : _model.getTracks())
    {
      final JCheckBox displayCheckBox = new JCheckBox(track.getItem().getName(), track.isSelected());
      displayCheckBox.addItemListener(new ItemListener()
      {

        @Override
        public void itemStateChanged(final ItemEvent e)
        {
          _model.setActiveTrack(track.getItem(), displayCheckBox.isSelected());
        }
      });
      add(displayCheckBox);
      _displayComponents.put(track.getItem(), displayCheckBox);
    }

    if (_model.isRelativeEnabled())
    {
      add(_inRelationToLabel);
      _relativeToComponents.add(_inRelationToLabel);
      final ButtonGroup relativeToGroup = new ButtonGroup();
      for (final AbstractSelection<TrackWrapper> track : _model.getTracks())
      {
        final JRadioButton relativeToradioButton = new JRadioButton(track.getItem()
            .getName());
        relativeToradioButton.addItemListener(new ItemListener()
        {

          @Override
          public void itemStateChanged(final ItemEvent e)
          {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
              _model.setPrimaryTrack(track.getItem());
            }
          }
        });
        relativeToGroup.add(relativeToradioButton);
        add(relativeToradioButton);
        _relativeToComponents.add(relativeToradioButton);
        _relatedToComponentsMap.put(track.getItem(), relativeToradioButton);
      }
      for (final Component comp : _relativeToComponents)
      {
        comp.setVisible(_model.getOperation().isARelativeCalculation());
      }
    }
  }

}
