package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;
import java.util.Iterator;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.Plottable;

public class DebriefCoreListener
{
  protected final java.awt.Color[] defaultColors = new java.awt.Color[]
  {java.awt.Color.red, java.awt.Color.green, java.awt.Color.blue,
      java.awt.Color.cyan, java.awt.Color.magenta, java.awt.Color.orange,
      java.awt.Color.pink};

  protected final IDISContext _context;

  public DebriefCoreListener(IDISContext context)
  {
    _context = context;
  }

  /**
   * get the default color for this name
   * 
   * @param name
   * @return
   */
  protected Color colorFor(String name)
  {
    // ok, get the hashmap
    int index = name.hashCode() % defaultColors.length;
    return defaultColors[index];
  }

  /**
   * helper interface, that provides the data for adding new items
   * 
   * @author ian
   * 
   */
  public static interface ListenerHelper
  {
    /**
     * create the parent layer
     * 
     * @return
     */
    Layer createLayer();

    /**
     * create the parent item
     * 
     * @return
     */
    Plottable createItem();
  }

  protected Layer getLayer(short eid, String name, ListenerHelper helper)
  {
    // find the narratives layer
    Layer nLayer = _context.findLayer(eid, name);
    if (nLayer == null)
    {
      nLayer = helper.createLayer();
      nLayer.setName(name);

      // and store it
      _context.addThisLayer(nLayer);

      // share the news
      Iterator<INewItemListener> iter = _context.getNewItemListeners();
      while (iter.hasNext())
      {
        Layers.INewItemListener newI = (Layers.INewItemListener) iter.next();
        newI.newItem(nLayer, null, null);
      }
    }
    return nLayer;
  }

  /**
   * add this item to the layer with the specified name
   * 
   * @param eid
   * @param layerName
   * @param item
   */
  protected void addNewItem(short eid, String layerName, ListenerHelper helper)
  {
    final Layer destination = getLayer(eid, layerName, helper);

    Plottable item = helper.createItem();

    destination.add(item);

    final Layer finalLayer = destination;

    if (_context.getLiveUpdates())
    {
      // provide null item, to prevent the Outline view trying to show it
      Plottable nullItem = null;

      _context.fireUpdate(nullItem, finalLayer);
    }

    // should we try any formatting?
    Iterator<INewItemListener> iter = _context.getNewItemListeners();
    while (iter.hasNext())
    {
      Layers.INewItemListener newI = (Layers.INewItemListener) iter.next();
      newI.newItem(finalLayer, item, null);
    }

  }

}