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
package org.mwc.debrief.scripting.wrappers;

import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;

import MWC.GenericData.WatchableList;

public class Tote
{
  private final TrackManager trackManager;

  public Tote(final TrackManager _trackManager)
  {
    trackManager = _trackManager;
  }

  public WatchableList getPrimaryTrack()
  {
    return trackManager.getPrimaryTrack();
  }

  public WatchableList[] getSecondaryTracks()
  {
    return trackManager.getSecondaryTracks();
  }

  public void setPrimaryTrack(final WatchableList primary)
  {
    trackManager.setPrimary(primary);
  }

  public void setSecondary(final WatchableList secondary)
  {
    trackManager.setSecondary(secondary);
  }
  
  public void addSecondary(final WatchableList secondary)
  {
    trackManager.addSecondary(secondary);
  }
}
