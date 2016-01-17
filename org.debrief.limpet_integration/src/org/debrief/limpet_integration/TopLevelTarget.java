/**
 * 
 */
package org.debrief.limpet_integration;

import info.limpet.IStoreItem;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Enumeration;

import org.debrief.limpet_integration.data.StoreWrapper;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * utility class to put a set of measurements in the top level layer
 * 
 * @author ian
 * 
 */
public class TopLevelTarget extends InMemoryStore implements Layer
{
  /**
   * 
   */
  public static final String LAYER_NAME = "Common Measurements";

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  final private Layers _theLayers;
  boolean _realised = false;

  public TopLevelTarget(Layers theLayers)
  {
    _theLayers = theLayers;
  }

  @Override
  public boolean add(IStoreItem results)
  {
    final boolean res = super.add(results);

    // ok, have we been realised?
    if (!_realised)
    {
      _theLayers.addThisLayer(this);
      _realised = true;
    }

    return res;
  }

  /**
   * @param b
   */
  public void setRealised(boolean b)
  {
    _realised = true;
  }

  @Override
  public String toString()
  {
    return LAYER_NAME;
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Plottable#getVisible()
   */
  @Override
  public boolean getVisible()
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Plottable#rangeFrom(MWC.GenericData.WorldLocation)
   */
  @Override
  public double rangeFrom(WorldLocation other)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Plottable o)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#hasEditor()
   */
  @Override
  public boolean hasEditor()
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#getInfo()
   */
  @Override
  public EditorType getInfo()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#exportShape()
   */
  @Override
  public void exportShape()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#append(MWC.GUI.Layer)
   */
  @Override
  public void append(Layer other)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#paint(MWC.GUI.CanvasType)
   */
  @Override
  public void paint(CanvasType dest)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#getBounds()
   */
  @Override
  public WorldArea getBounds()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#hasOrderedChildren()
   */
  @Override
  public boolean hasOrderedChildren()
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#getLineThickness()
   */
  @Override
  public int getLineThickness()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#add(MWC.GUI.Editable)
   */
  @Override
  public void add(Editable point)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#removeElement(MWC.GUI.Editable)
   */
  @Override
  public void removeElement(Editable point)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#elements()
   */
  @Override
  public Enumeration<Editable> elements()
  {
    ArrayList<Editable> items = StoreWrapper.getElementsFor(this, null);
    return new Plottables.IteratorWrapper(items.iterator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#setVisible(boolean)
   */
  @Override
  public void setVisible(boolean val)
  {
    // TODO Auto-generated method stub

  }

}