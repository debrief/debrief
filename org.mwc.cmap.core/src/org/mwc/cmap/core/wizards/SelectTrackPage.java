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
package org.mwc.cmap.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;

public class SelectTrackPage extends CoreEditableWizardPage
{

  public static class TrackDataItem implements Editable
  {

    TrackWrapper _selectedTrack;
    final TrackWrapper[] _allTracksAvailable;

    public TrackDataItem(final TrackWrapper[] _allTracks)
    {
      _allTracksAvailable = _allTracks;
    }

    public TrackWrapper[] getAllTracksAvailable()
    {
      return _allTracksAvailable;
    }

    @Override
    public EditorType getInfo()
    {
      return null;
    }

    public TrackWrapper getItemByName(final String name)
    {
      for (final TrackWrapper track : _allTracksAvailable)
      {
        if (track.getName().equals(name))
        {
          return track;
        }
      }
      return null;
    }

    @Override
    public String getName()
    {
      return "Track";
    }

    public TrackWrapper getTrack()
    {
      return _selectedTrack;
    }

    @Override
    public boolean hasEditor()
    {
      return false;
    }

    public void setTrack(final TrackWrapper _track)
    {
      this._selectedTrack = _track;
    }
  }

  TrackWrapper _defaultTrackValue;
  private TrackDataItem _myTrack;
  private final String _fieldExplanation;

  private final TrackWrapper[] allTracks;

  public SelectTrackPage(final ISelection selection, final String pageName,
      final String title, final String description, final String imageName,
      final String helpContext, final boolean optional,
      final String trailingMsg, final TrackWrapper[] allTracks,
      final TrackWrapper defaultWrapper)
  {
    super(selection, pageName, title, description, imageName, helpContext,
        optional, trailingMsg);
    _fieldExplanation = description;
    _defaultTrackValue = defaultWrapper;
    this.allTracks = allTracks;
  }

  @Override
  protected Editable createMe()
  {
    if (_myTrack == null)
    {
      _myTrack = new TrackDataItem(allTracks);
      _myTrack.setTrack(_defaultTrackValue);
    }

    return _myTrack;
  }

  @Override
  protected PropertyDescriptor[] getPropertyDescriptors()
  {
    final PropertyDescriptor[] descriptors =
    {prop("Track", _fieldExplanation, getEditable())};
    return descriptors;
  }

  public TrackWrapper getValue()
  {
    return _myTrack.getTrack();
  }

}
