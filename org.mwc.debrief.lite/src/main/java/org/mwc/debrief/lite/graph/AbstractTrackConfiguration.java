package org.mwc.debrief.lite.graph;

import java.beans.PropertyChangeListener;
import java.util.List;

import Debrief.Wrappers.TrackWrapper;

public interface AbstractTrackConfiguration
{
  public void setTracks(final List<TrackWrapper> tracks);
  
  public List<TrackWrapper> getActiveTrack();
  
  public void setActiveTrack(final TrackWrapper track, final boolean check);
  
  public TrackWrapper getPrimaryTrack();
  
  public void setPrimaryTrack(final TrackWrapper track);
  
  public void addPropertyChangeListener(final PropertyChangeListener listener);
}
