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
package org.mwc.debrief.lite.gui.custom;

import java.beans.PropertyChangeListener;
import java.util.List;

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationHolder;
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

  public CalculationHolder getOperation();

  public TrackWrapper getPrimaryTrack();

  public List<TrackWrapperSelect> getTracks();

  public void setActiveTrack(final TrackWrapper track, final boolean check);

  public void setOperation(final CalculationHolder calculation);

  public void setPrimaryTrack(final TrackWrapper track);

  /**
   * 
   * @param tracks
   *          Tracks to assign
   * @return true if it was actually assigned. If they are the same, they are not assigned.
   */
  public boolean setTracks(final List<TrackWrapper> tracks);
  
  public boolean isRelativeEnabled();
}
