package org.mwc.debrief.lite.graph;

import java.awt.Font;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Debrief.Wrappers.TrackWrapper;


public class JSelectTrack extends JPanel
{
  /**
   * Generated UID
   */
  private static final long serialVersionUID = -1490664356576661371L;
  private final JSelectTrackModel _model;
  private final TreeMap<TrackWrapper, JCheckBox> _displayComponents;
  private final TreeMap<TrackWrapper, JCheckBox> _relatedToComponents;

  private final JLabel _selectTracksLabel = new JLabel("Select Tracks");
  private final JLabel _displayLabel = new JLabel("Display");
  private final JLabel _inRelationToLabel = new JLabel("In Relation to");
  
  public JSelectTrack(final JSelectTrackModel model)
  {
    super();
    this._model = model;
    
    _displayComponents = new TreeMap<>();
    _relatedToComponents = new TreeMap<>();
    
    initializeComponents();
  }
  
  public void initializeComponents()
  {

    _displayComponents.clear();
    _relatedToComponents.clear();
    
    Font previousFont = _displayLabel.getFont();
    _displayLabel.setFont(new Font(previousFont.getName(), previousFont.getStyle(), 6));
    _inRelationToLabel.setFont(new Font(previousFont.getName(), previousFont.getStyle(), 6));
    
    
  }

  
}
