package org.mwc.debrief.dis.listeners.impl;

import java.util.Iterator;

import MWC.GUI.Layer;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.Plottable;

public interface IDISContext
{

  /**
   * whether a new plot should be created for each new replication
   * 
   * @return
   */
  boolean getUseNewPlot();

  /**
   * whether a UI should update on each new data item
   * 
   * @return
   */
  boolean getLiveUpdates();

  /**
   * store this new layer
   * 
   * @param layer
   */
  void addThisLayer(Layer layer);

  /**
   * whether the user wants the Debrief plot to resize to show visible data
   * 
   * @return
   */
  boolean getFitToData();
  
  /** resize the plot to view all data
   * 
   */
  void zoomToFit();
  

  /** move the display time forward
   * 
   * @param time
   */
  public void setNewTime(long time);

  /**
   * trigger a screen update
   * 
   * @param newItem
   * @param layer
   */
  void fireUpdate(Plottable newItem, Layer layer);

  /**
   * find the specified track for this exercise
   * 
   * @param exerciseId
   * @param theName
   * @return
   */
  Layer findLayer(short exerciseId, String theName);

  /** get iterator, so we can work through the new item listeners
   * 
   * @return
   */
  Iterator<INewItemListener> getNewItemListeners();

  /** mark the scenario complete. Any new data will go into a new layer
   * 
   */
  void scenarioComplete();

  /** record the counter for the new replication
   * 
   * @param replicationCounter
   */
  void setReplicationId(long replicationCounter);
  
}