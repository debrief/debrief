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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationHolder;
import Debrief.Wrappers.TrackWrapper;

public class JSelectTrackModel implements AbstractTrackConfiguration
{

  public static final String TRACK_SELECTION = "TRACK_STATE_CHANGED";

  public static final String PRIMARY_CHANGED = "PRIMARY_CHANGED";

  public static final String TRACK_LIST_CHANGED = "TRACK_LIST_CHANGED";

  public static final String OPERATION_CHANGED = "OPERATION CHANGED";

  private TrackWrapper _primaryTrack;

  private final List<TrackWrapperSelect> _tracks = new ArrayList<>();

  private CalculationHolder _calculation;

  private final ArrayList<PropertyChangeListener> _stateListeners =
      new ArrayList<>();

  public JSelectTrackModel(final List<TrackWrapper> tracks,
      final CalculationHolder calculation)
  {
    setTracks(tracks);
    _calculation = calculation;
  }

  @Override
  public void addPropertyChangeListener(final PropertyChangeListener listener)
  {
    this._stateListeners.add(listener);
  }

  @Override
  public CalculationHolder getOperation()
  {
    return _calculation;
  }

  @Override
  public TrackWrapper getPrimaryTrack()
  {
    return _primaryTrack;
  }

  @Override
  public List<TrackWrapperSelect> getTracks()
  {
    return _tracks;
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
  public void setActiveTrack(final TrackWrapper track, final boolean check)
  {
    Boolean oldValue = null;
    Boolean newValue = null;
    for (final TrackWrapperSelect currentTrack : _tracks)
    {
      if (currentTrack.track.equals(track))
      {
        newValue = check;
        oldValue = currentTrack.selected;
        currentTrack.selected = newValue;
      }
    }

    if (newValue != null && !oldValue.equals(newValue))
    {
      // we have the element changed.
      notifyListenersStateChanged(track, TRACK_SELECTION, oldValue, check);
    }
  }

  @Override
  public void setOperation(final CalculationHolder calculation)
  {
    final CalculationHolder oldCalculation = _calculation;
    this._calculation = calculation;
    if (calculation != null && !calculation.equals(oldCalculation))
    {
      notifyListenersStateChanged(this, OPERATION_CHANGED, oldCalculation,
          calculation);
    }
  }

  @Override
  public void setPrimaryTrack(final TrackWrapper newPrimary)
  {
    final TrackWrapper oldPrimary = getPrimaryTrack();
    // Do we have it?
    for (final TrackWrapperSelect currentTrack : _tracks)
    {
      if (currentTrack.track.equals(newPrimary))
      {
        this._primaryTrack = newPrimary;

        if (!currentTrack.selected)
        {
          setActiveTrack(newPrimary, true);
        }
        notifyListenersStateChanged(this, PRIMARY_CHANGED, oldPrimary,
            newPrimary);
        return;
      }
    }
  }

  /**
   * 
   * @param tracks
   *          Tracks to assign
   * @return true if it was actually assigned. If they are the same, they are not assigned.
   */
  @Override
  public boolean setTracks(final List<TrackWrapper> tracks)
  {
    boolean isDifferent = false;
    final List<TrackWrapperSelect> oldTracks = new ArrayList<>(this._tracks);
    final List<TrackWrapperSelect> newTracks = new ArrayList<>();
    final HashSet<TrackWrapper> oldTracksSet = new HashSet<>();
    if (oldTracks != null)
    {
      for (TrackWrapperSelect oldTrack : oldTracks)
      {
        oldTracksSet.add(oldTrack.track);
      }
    }
    for (final TrackWrapper track : tracks)
    {
      newTracks.add(new TrackWrapperSelect(track, false));
      isDifferent |= !oldTracksSet.contains(track);
    }
    if (isDifferent)
    {
      this._tracks.clear();
      this._tracks.addAll(newTracks);
      notifyListenersStateChanged(this, TRACK_LIST_CHANGED, oldTracks, tracks);
    }
    return isDifferent;
  }
}
