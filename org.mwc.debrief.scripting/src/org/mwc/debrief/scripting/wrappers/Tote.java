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

import org.eclipse.ease.modules.WrapToScript;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;

import MWC.GenericData.WatchableList;

/**
 * Tote class that exposes methods related with Primary and Secondary Track.
 * 
 * @author Ian Mayo
 *
 */
public class Tote
{
  private TrackManager trackManager;

  /**
   * set the track manager
   * 
   * @return the track manager object <br />
   *         // @type org.mwc.cmap.core.DataTypes.TrackData.TrackManager
   */
  @WrapToScript
  public TrackManager getTrackManager()
  {
    return trackManager;
  }

  /**
   * get the track manager
   * 
   * @param trackManager
   *          the new manager object
   */
  @WrapToScript
  public void setTrackManager(TrackManager trackManager)
  {
    this.trackManager = trackManager;
  }

  /**
   * Method that adds a secondary track to the current plot
   * 
   * @see MWC.GenericData.WatchableList
   * 
   * @param secondary
   *          List to be added.
   */
  @WrapToScript
  public void addSecondary(final WatchableList secondary)
  {
    trackManager.addSecondary(secondary);
  }

  /**
   * Method that returns the primary track of the current plot.
   * 
   * @return Primary track of the current plot. <br />
   *         // @type MWC.GenericData.WatchableList
   * 
   */
  @WrapToScript
  public WatchableList getPrimaryTrack()
  {
    return trackManager.getPrimaryTrack();
  }

  /**
   * Method that returns the secondary tracks of the current plot.
   * 
   * @return Array of secondary list of the current plot. <br />
   *         // @type MWC.GenericData.WatchableList
   * 
   */
  @WrapToScript
  public WatchableList[] getSecondaryTracks()
  {
    return trackManager.getSecondaryTracks();
  }

  /**
   * Method that sets the primary track to the current plot.
   * 
   * @param primary
   *          New primary set
   */
  @WrapToScript
  public void setPrimaryTrack(final WatchableList primary)
  {
    trackManager.setPrimary(primary);
  }

  /**
   * Method that sets the secondary track to the current plot.
   * 
   * @param secondary
   *          New secondary track.
   */
  @WrapToScript
  public void setSecondary(final WatchableList secondary)
  {
    trackManager.setSecondary(secondary);
  }
}
