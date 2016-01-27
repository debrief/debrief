package org.mwc.debrief.dis.listeners.impl;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;

public interface IDISContext
{

  /**
   * whether a new plot should be created for each new replication
   * 
   * @return
   */
  abstract public boolean getUseNewPlot();

  /**
   * whether a UI should update on each new data item
   * 
   * @return
   */
  abstract public boolean getLiveUpdates();

  /**
   * store this new layer
   * 
   * @param layer
   */
  public abstract void addThisLayer(Layer layer);

  /**
   * whether the user wants the Debrief plot to resize to show visible data
   * 
   * @return
   */
  abstract public boolean getFitToData();


  /**
   * trigger a screen update
   * 
   * @param newItem
   * @param layer
   */
  public abstract void fireUpdate(Plottable newItem, Layer layer);

  /**
   * find the specified track for this exercise
   * 
   * @param exerciseId
   * @param theName
   * @return
   */
  public abstract TrackWrapper findLayer(short exerciseId, String theName);

}