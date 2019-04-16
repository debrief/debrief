package org.mwc.debrief.lite.gui.custom;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;

import org.mwc.debrief.lite.gui.custom.AbstractTrackConfiguration.TrackWrapperSelect;

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
          final CalculationHolder newOperation = (CalculationHolder) evt.getNewValue();
          for ( Component comp : _relativeToComponents)
          {
            comp.setVisible(newOperation.isARelativeCalculation());
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

    for (final TrackWrapperSelect track : _model.getTracks())
    {
      final JCheckBox displayCheckBox = new JCheckBox(track.track.getName());
      displayCheckBox.addItemListener(new ItemListener()
      {

        @Override
        public void itemStateChanged(ItemEvent e)
        {
          _model.setActiveTrack(track.track, displayCheckBox.isSelected());
        }
      });
      add(displayCheckBox);
      _displayComponents.put(track.track, displayCheckBox);
    }

    add(_inRelationToLabel);
    _relativeToComponents.add(_inRelationToLabel);
    final ButtonGroup relativeToGroup = new ButtonGroup();
    for (final TrackWrapperSelect track : _model.getTracks())
    {
      final JRadioButton relativeToradioButton = new JRadioButton(track.track
          .getName());
      relativeToradioButton.addItemListener(new ItemListener()
      {

        @Override
        public void itemStateChanged(ItemEvent e)
        {
          if (e.getStateChange() == ItemEvent.SELECTED)
          {
            _model.setPrimaryTrack(track.track);
          }
        }
      });
      relativeToGroup.add(relativeToradioButton);
      add(relativeToradioButton);
      _relativeToComponents.add(relativeToradioButton);
      _relatedToComponentsMap.put(track.track, relativeToradioButton);
    }
    for ( Component comp : _relativeToComponents)
    {
      comp.setVisible(_model.getOperation().isARelativeCalculation());
    }
  }

}
