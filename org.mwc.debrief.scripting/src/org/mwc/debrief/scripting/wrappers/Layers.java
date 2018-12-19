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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;

import org.eclipse.ease.modules.ScriptParameter;
import org.eclipse.ease.modules.WrapToScript;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.OperateFunction;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

/**
 * Layer Class that exposes operations related with Layers
 * 
 * @see MWC.GUI.Layers
 * @author Ian Mayo
 *
 */
public class Layers
{

  public static class DLayers
  {
    private final MWC.GUI.Layers _layers;

    /**
     * Constructor of the Layer Wrapper.
     * 
     * @param layers
     *          Initial Layers to be added.
     */
    public DLayers(final MWC.GUI.Layers layers)
    {
      _layers = layers;
    }

    /**
     * Add the layer given to the list of layers.
     * 
     * @param layer
     *          Layer to be added.
     */
    @WrapToScript
    public void add(final Layer layer)
    {
      _layers.addThisLayer(layer);
    }

    /**
     * Removes all the layers.
     */
    @WrapToScript
    public void clear()
    {
      _layers.clear();
    }
    
    /**  get the names of the loaded top level tracks
     * 
     * @return an array of track names
     */
    public String[] getTrackNames()
    {
      final LightweightTrackWrapper[] tracks = getTracks();
      final String[] res = new String[tracks.length];
      int ctr = 0;
      for(LightweightTrackWrapper t: tracks)
      {
        res[ctr++] = t.getName();
      }      
      return res;
    }
    
    /** get the names of the loaded layers
     * 
     * @return an array of layer names
     */
    public String[] getLayerNames()
    {
      final String[] res = new String[size()];
      int ctr = 0;
      final Enumeration<Editable> ele = _layers.elements();
      while(ele.hasMoreElements())
      {
        final Editable l = ele.nextElement();
        res[ctr++] = l.getName();
      }
      return res;
    }


    /**
     * Create (and store) a new layer with the specified name
     * 
     * @see MWC.GUI.Layer
     * @param name
     *          Name of the new layer.
     * @return Layer with the specified name
     */
    @WrapToScript
    public Layer createLayer(final String name)
    {
      // do we already have it?
      Layer newLayer = _layers.findLayer(name);
      if (newLayer == null)
      {
        newLayer = new BaseLayer();
        newLayer.setName(name);
        _layers.addThisLayer(newLayer);
      }
      return newLayer;
    }

    /**
     * Method that returns the layer with the specified name.
     * 
     * @see MWC.GUI.Layer
     * @param name
     *          Name of the layer to find.
     * @return Layer with the specified name.
     */
    @WrapToScript
    public Layer findLayer(final String name)
    {
      return _layers.findLayer(name, true);
    }

    /**
     * descend the tree looking for an item with the specified name
     *
     * @param name
     *          what we're looking for
     * @return the matching item (or null)
     */
    @WrapToScript
    public Editable findThis(final String name)
    {
      Editable res = null;
      if (name != null)
      {
        final Enumeration<Editable> ele = _layers.elements();
        while (ele.hasMoreElements() && res == null)
        {
          final Layer next = (Layer) ele.nextElement();
          if (name.equals(next.getName()))
          {
            res = next;
            break;
          }
          else
          {
            final Enumeration<Editable> items = next.elements();
            while (items.hasMoreElements() && res == null)
            {
              final Editable item = items.nextElement();
              if (name.equals(item.getName()))
              {
                res = item;
                break;
              }
              else if (item instanceof Layer)
              {
                final Layer subLayer = (Layer) item;
                final Enumeration<Editable> subItems = subLayer.elements();
                while (subItems.hasMoreElements())
                {
                  final Editable subItem = subItems.nextElement();
                  if (name.equals(subItem.getName()))
                  {
                    res = subItem;
                    break;
                  }
                }
              }
            }
          }
        }
      }

      return res;
    }

    /**
     * Method that returns a LightweightTrackWrapper given its name.
     * 
     * @see Debrief.Wrappers.Track.LightweightTrackWrapper
     * @param name
     *          Name to be found.
     * @return LightweightTrackWrapper that contains the track of the name given.
     */
    @WrapToScript
    public LightweightTrackWrapper findTrack(@ScriptParameter(
        defaultValue = "unset") final String name)
    {
      // special handling. if a track isn't provided, return the first
      // one
      LightweightTrackWrapper res = null;
      if ("unset".equals(name) || name == null)
      {
        // ok, just return the first one
        final Enumeration<Editable> ele = _layers.elements();
        while (ele.hasMoreElements() && res == null)
        {
          final Editable nextE = ele.nextElement();
          if (nextE instanceof LightweightTrackWrapper)
          {
            res = (LightweightTrackWrapper) nextE;
          }
        }
      }
      else
      {
        final Layer match = _layers.findLayer(name);
        if (match instanceof LightweightTrackWrapper)
        {
          res = (LightweightTrackWrapper) match;
        }
      }
      return res;
    }

    /**
     * Method needed to update the plot.
     */
    @WrapToScript
    public void fireModified()
    {
      _layers.fireExtended();
    }

    /**
     * Method that returns the track in the layers
     * 
     * @see Debrief.Wrappers.Track.LightweightTrackWrapper
     * @return Array containing the tracks in the layer as LightweightTrackWrapper
     */
    @WrapToScript
    public LightweightTrackWrapper[] getTracks()
    {
      final ArrayList<LightweightTrackWrapper> items =
          new ArrayList<LightweightTrackWrapper>();

      final OperateFunction function = new OperateFunction()
      {

        @Override
        public void operateOn(final Editable item)
        {
          items.add((LightweightTrackWrapper) item);
        }
      };
      _layers.walkVisibleItems(LightweightTrackWrapper.class, function);

      return items.toArray(new LightweightTrackWrapper[]
      {null});
    }

    /**
     * Remove the given layer of the current instance.
     * 
     * @param layer
     *          Layer to be removed.
     */
    @WrapToScript
    public void remove(final Layer layer)
    {
      _layers.removeThisLayer(layer);
    }

    /**
     * Amount of layers in the current instance.
     * 
     * @return Amount of layers in the current instance.
     */
    @WrapToScript
    public int size()
    {
      return _layers.size();
    }
  }

  public static class TestLayers extends TestCase
  {
    public void testFindItem()
    {
      final MWC.GUI.Layers layers = new MWC.GUI.Layers();
      final BaseLayer shapes = new BaseLayer();
      shapes.setName("shapes");
      layers.addThisLayer(shapes);

      final WorldLocation loc1 = new WorldLocation(1d, 2d, 3d);
      final WorldLocation loc2 = new WorldLocation(1d, 2d, 3d);

      final ShapeWrapper lineShape = new ShapeWrapper("line", new LineShape(
          loc1, loc2), Color.red, null);
      final ShapeWrapper rectShape = new ShapeWrapper("rectangle",
          new RectangleShape(loc1, loc2), Color.red, null);
      shapes.add(lineShape);
      shapes.add(rectShape);

      final TrackWrapper track = new TrackWrapper();
      track.setName("track");
      layers.addThisLayer(track);

      final FixWrapper newFix = new FixWrapper(new Fix(new HiResDate(100000),
          loc1, 0d, 0d));
      track.addFix(newFix);
      newFix.setName("fix");

      final DLayers dl = new DLayers(layers);
      assertEquals("found it", lineShape, dl.findThis("line"));
      assertEquals("found it", shapes, dl.findThis("shapes"));
      assertEquals("found it", track, dl.findThis("track"));
      assertEquals("found it", newFix, dl.findThis("fix"));
      assertEquals("found it", null, dl.findThis("fezzig"));

    }
  }
}
