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

  TrackWrapper _defaultTrackValue;
  private TrackDataItem _myTrack;
  private final String _fieldExplanation;

  public static class TrackDataItem implements Editable
  {

    TrackWrapper _track;

    @Override
    public String getName()
    {
      return "Track";
    }

    @Override
    public boolean hasEditor()
    {
      return false;
    }

    @Override
    public EditorType getInfo()
    {
      return null;
    }

    public TrackWrapper getTrack()
    {
      return _track;
    }

    public void setTrack(TrackWrapper _track)
    {
      this._track = _track;
    }
  }

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
  }

  public TrackWrapper getValue()
  {
    return _myTrack.getTrack();
  }

  @Override
  protected Editable createMe()
  {
    if (_myTrack == null)
    {
      _myTrack = new TrackDataItem();
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

}
