package org.mwc.debrief.lite.gui.custom;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.mwc.debrief.lite.gui.custom.AbstractTrackConfiguration.TrackWrapperSelect;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;


public class JSelectTrack extends JPopupMenu
{
  /**
   * Generated UID
   */
  private static final long serialVersionUID = -1490664356576661371L;
  private final JSelectTrackModel _model;
  private final TreeMap<TrackWrapper, JCheckBox> _displayComponents;
  private final TreeMap<TrackWrapper, JRadioButton> _relatedToComponents;

  private final JLabel _displayLabel = new JLabel("Display");
  private final JLabel _inRelationToLabel = new JLabel("In Relation to");
  
  
  
  public JSelectTrack(final JSelectTrackModel model)
  {
    super();
    this._model = model;
    
    _displayComponents = new TreeMap<>();
    _relatedToComponents = new TreeMap<>();
    
    initializeComponents();
    
    this._model.addPropertyChangeListener(new PropertyChangeListener()
    {
      
      @Override
      public void propertyChange(PropertyChangeEvent evt)
      {
        if ( JSelectTrackModel.PRIMARY_CHANGED.equals(evt.getPropertyName()) )
        {
          final TrackWrapper newPrimary = (TrackWrapper) evt.getNewValue();
          JRadioButton component = _relatedToComponents.get(newPrimary);
          if ( component != null )
          {
            component.setSelected(true);
          }
        }else if ( JSelectTrackModel.TRACK_SELECTION.equals(evt.getPropertyName()) )
        {
          final TrackWrapper track = (TrackWrapper)evt.getSource();
          JCheckBox component = _displayComponents.get(track);
          if ( component != null )
          {
            component.setSelected((Boolean) evt.getNewValue());
          }
        }else if ( JSelectTrackModel.TRACK_LIST_CHANGED.equals(evt.getPropertyName()) )
        {
          initializeComponents();
        }
      }
    });
  }
  
  public void initializeComponents()
  {
    this.removeAll();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    _displayComponents.clear();
    _relatedToComponents.clear();
    
    add(_displayLabel);
    Font previousFont = _displayLabel.getFont();
    _displayLabel.setFont(new Font(previousFont.getName(), previousFont.getStyle(), 9));
    _inRelationToLabel.setFont(new Font(previousFont.getName(), previousFont.getStyle(), 9));
    
    for ( final TrackWrapperSelect track : _model.getTracks() )
    {
      final JCheckBox displayCheckBox = new JCheckBox(track.track.getName());
      displayCheckBox.addChangeListener(new ChangeListener()
      {
        
        @Override
        public void stateChanged(ChangeEvent e)
        {
          _model.setActiveTrack(track.track, displayCheckBox.isSelected());
        }
      });
      add(displayCheckBox);
    }

    add(_inRelationToLabel);
    final ButtonGroup relativeToGroup = new ButtonGroup();
    for ( final TrackWrapperSelect track : _model.getTracks() )
    {
      final JRadioButton relativeToradioButton = new JRadioButton(track.track.getName());
      relativeToradioButton.addChangeListener(new ChangeListener()
      {
        
        @Override
        public void stateChanged(ChangeEvent e)
        {
          _model.setPrimaryTrack(track.track);
        }
      });
      relativeToGroup.add(relativeToradioButton);
      add(relativeToradioButton);
    }
  }

  
}
