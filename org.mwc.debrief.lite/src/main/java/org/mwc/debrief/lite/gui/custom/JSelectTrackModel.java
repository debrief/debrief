package org.mwc.debrief.lite.gui.custom;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.mwc.debrief.lite.graph.AbstractTrackConfiguration;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;

public class JSelectTrackModel implements AbstractTrackConfiguration
{

  private TrackWrapper _primaryTrack;
  
  private List<TrackWrapperSelect> _tracks;
  
  private ArrayList<PropertyChangeListener> _stateListeners = new ArrayList<>();;
  
  public static final String TRACK_SELECTION = "TRACK_STATE_CHANGED";
  
  public static final String PRIMARY_CHANGED = "PRIMARY_CHANGED";
  
  public static final String TRACK_LIST_CHANGED = "TRACK_LIST_CHANGED";
  
  public JSelectTrackModel(final List<TrackWrapper> tracks)
  {
    setTracks(tracks);
  }
  
  private void notifyListenersStateChanged(final Object source,
      final String property, final Object oldValue, final Object newValue)
  {
    for (final PropertyChangeListener event : _stateListeners)
    {
      event.propertyChange(new PropertyChangeEvent(source, property, oldValue,
          newValue));
    }
  }
  
  @Override
  public void setTracks(final List<TrackWrapper> tracks)
  {
    this._tracks = new ArrayList<>();
    for ( TrackWrapper track : tracks )
    {
      this._tracks.add(new TrackWrapperSelect(track, false));
    }
    notifyListenersStateChanged(this, TRACK_LIST_CHANGED, null, tracks);
  }

  @Override
  public void setActiveTrack(final TrackWrapper track, final boolean check)
  {
    Boolean oldValue = null;
    Boolean newValue = null;
    for ( TrackWrapperSelect currentTrack : _tracks )
    {
      if ( currentTrack.track.equals(track) )
      {
        newValue = check;
        oldValue = currentTrack.selected;
        currentTrack.selected = newValue;
      }
    }
    
    if ( newValue != null && oldValue != newValue )
    {
      // we have the element changed.
      notifyListenersStateChanged(track, TRACK_SELECTION, oldValue, check);
    }
  }

  @Override
  public TrackWrapper getPrimaryTrack()
  {
    return _primaryTrack;
  }

  @Override
  public void addPropertyChangeListener(final PropertyChangeListener listener)
  {
    this._stateListeners.add(listener);
  }

  @Override
  public void setPrimaryTrack(final TrackWrapper newPrimary)
  {
    final TrackWrapper oldPrimary = getPrimaryTrack();
    // Do we have it?
    for ( TrackWrapperSelect currentTrack : _tracks )
    {
      if ( currentTrack.track.equals(newPrimary) )
      {
        this._primaryTrack = newPrimary;
        
        if ( !currentTrack.selected )
        {
          setActiveTrack(newPrimary, true);
        }
        notifyListenersStateChanged(this, PRIMARY_CHANGED, oldPrimary, newPrimary);
        return;
      }
    }
  }

  @Override
  public List<TrackWrapperSelect> getTracks()
  {
    return _tracks;
  }
}
