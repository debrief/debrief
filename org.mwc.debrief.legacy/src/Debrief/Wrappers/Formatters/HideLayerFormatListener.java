/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package Debrief.Wrappers.Formatters;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

public class HideLayerFormatListener extends PlainWrapper implements
    INewItemListener
{

  // ///////////////////////////////////////////////////////////
  // info class
  // //////////////////////////////////////////////////////////
  final public class HideLayerInfo extends Editable.EditorType implements
      Serializable
  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HideLayerInfo(final HideLayerFormatListener data)
    {
      super(data, data.getName(), "");
    }

    @Override
    final public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {displayProp("Name", "Name", "Name for this formatter"), displayProp(
            "Visible", "Active", "Whether this formatter is active")};

        return res;
      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  public static class TestMe extends TestCase
  {
    private FixWrapper createFix(final int time)
    {
      final Fix newF = new Fix(new HiResDate(time), new WorldLocation(2, 2, 0),
          22, 33);
      final FixWrapper fw = new FixWrapper(newF);
      return fw;

    }

    public void testNameMatch()
    {
      final HideLayerFormatListener cf = new HideLayerFormatListener("Test",
          new String[]
          {"Name4"});
      final TrackWrapper tw = new TrackWrapper();
      tw.setName("Name4");

      cf.newItem(tw, null, null);

      assertFalse("not visible", tw.getVisible());

    }

    public void testNameNotMatch()
    {
      final HideLayerFormatListener cf = new HideLayerFormatListener("Test",
          new String[]
          {"Name"});
      final TrackWrapper tw = new TrackWrapper();
      tw.setName("JamJar");
      final FixWrapper f1 = createFix(4000);
      final FixWrapper f2 = createFix(5000);
      final FixWrapper f3 = createFix(9000);
      final FixWrapper f4 = createFix(10000);
      final FixWrapper f5 = createFix(14100);

      cf.newItem(tw, f1, null);
      cf.newItem(tw, f2, null);
      cf.newItem(tw, f3, null);
      cf.newItem(tw, f4, null);
      cf.newItem(tw, f5, null);

      assertTrue("is visible", tw.getVisible());

    }

    public void testNoNames()
    {
      final HideLayerFormatListener cf = new HideLayerFormatListener("Test",
          null);
      final TrackWrapper tw = new TrackWrapper();
      tw.setName("Name");
      final FixWrapper f1 = createFix(4000);
      final FixWrapper f2 = createFix(5000);
      final FixWrapper f3 = createFix(9000);
      final FixWrapper f4 = createFix(10000);
      final FixWrapper f5 = createFix(14100);

      cf.newItem(tw, f1, null);
      cf.newItem(tw, f2, null);
      cf.newItem(tw, f3, null);
      cf.newItem(tw, f4, null);
      cf.newItem(tw, f5, null);

      assertTrue("is visible", tw.getVisible());
    }
  }

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private String _formatName;
  private EditorType _myEditor;
  private final String[] _layers;

  public HideLayerFormatListener(final String name, final String[] layers)
  {
    _formatName = name;
    _layers = layers;
  }

  @Override
  public void fileComplete()
  {
  }

  @Override
  public WorldArea getBounds()
  {
    return null;
  }

  @Override
  public EditorType getInfo()
  {
    if (_myEditor == null)
    {
      _myEditor = new HideLayerInfo(this);
    }
    return _myEditor;
  }

  public String[] getLayers()
  {
    return _layers;
  }

  @Override
  public String getName()
  {
    return _formatName;
  }

  @Override
  public boolean hasEditor()
  {
    return true;
  }

  @Override
  public void newItem(final Layer parent, final Editable item,
      final String symbology)
  {
    // are we active
    if (!getVisible())
    {
      return;
    }

    // is this a new layer?
    if (item == null)
    {
      // ok, do we have a set of track names?
      if (_layers == null || _layers.length == 0)
      {
        // nope, just hide it
        parent.setVisible(false);
      }
      else
      {
        // check if this is one of our tracks
        for (int i = 0; i < _layers.length; i++)
        {
          final String thisT = _layers[i];
          if (thisT.equals(parent.getName()))
          {
            parent.setVisible(false);
          }
        }
      }
    }
  }

  @Override
  public void paint(final CanvasType dest)
  {
    // don't bother, it can't be plotted
  }

  @Override
  public void reset()
  {
    // ignore
  }

  @Override
  public void setName(final String name)
  {
    _formatName = name;
  }

  @Override
  public String toString()
  {
    return getName();
  }
}