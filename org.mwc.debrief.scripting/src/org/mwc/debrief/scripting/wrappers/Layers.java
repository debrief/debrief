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

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
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
