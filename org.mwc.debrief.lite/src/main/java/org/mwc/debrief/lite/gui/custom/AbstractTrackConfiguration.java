package org.mwc.debrief.lite.gui.custom;

import java.beans.PropertyChangeListener;
import java.util.List;

import Debrief.Wrappers.TrackWrapper;

public interface AbstractTrackConfiguration
{
  static class TrackWrapperSelect
  {
    public TrackWrapper track;
    public Boolean selected;

    public TrackWrapperSelect(final TrackWrapper track, final Boolean selected)
    {
      super();
      this.track = track;
      this.selected = selected;
    }
  }

  public void addPropertyChangeListener(final PropertyChangeListener listener);

  public TrackWrapper getPrimaryTrack();

  public List<TrackWrapperSelect> getTracks();

  public void setActiveTrack(final TrackWrapper track, final boolean check);

  public void setPrimaryTrack(final TrackWrapper track);

  public void setTracks(final List<TrackWrapper> tracks);
}
