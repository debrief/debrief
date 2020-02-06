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
package MWC.GUI.Shapes;

import java.awt.Point;
import java.util.Enumeration;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GUI.Shapes.HasDraggableComponents.ComponentConstruct;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

public class FindNearest
{
  public static void findNearest(final Layer thisLayer,
      final WorldLocation cursorLoc, final Point cursorPos,
      final ComponentConstruct currentNearest, final Layer parentLayer)
  {
    //
    Layer thisParentLayer;
    if (parentLayer == null)
      thisParentLayer = thisLayer;
    else
      thisParentLayer = parentLayer;

    // so, step through this layer
    if (thisLayer.getVisible())
    {
      boolean sorted = false;

      // is this layer a track?
      if (thisLayer instanceof HasDraggableComponents)
      {
        final HasDraggableComponents dw = (HasDraggableComponents) thisLayer;

        // yup, find the distance to it's nearest point
        dw.findNearestHotSpotIn(cursorPos, cursorLoc, currentNearest,
            thisParentLayer);

        // right, this one's processed. carry on
        sorted = true;
      }

      // have we processed this item
      if (!sorted)
      {
        // nope, let's just run through it
        final Enumeration<Editable> pts = thisLayer.elements();
        while (pts.hasMoreElements())
        {
          final Plottable pt = (Plottable) pts.nextElement();

          // is this item a layer itself?
          if (pt instanceof Layer)
          {
            findNearest((Layer) pt, cursorLoc, cursorPos, currentNearest,
                thisParentLayer);
          }
          else
          {
            HasDraggableComponents draggable = null;

            // is it a shape?
            if (pt instanceof HasDraggableComponents)
            {
              draggable = (HasDraggableComponents) pt;

              // yup, find the distance to it's nearest point
              draggable.findNearestHotSpotIn(cursorPos, cursorLoc,
                  currentNearest, thisParentLayer);

              // right, this one's processed. carry on
              sorted = true;
            }
          }
        }
      }
    }
  }

  public static void findNearest(final Layer thisLayer,
      final WorldLocation cursorLoc, final Point cursorPos,
      final LocationConstruct currentNearest, final Layer parentLayer,
      final Layers theData)
  {
    //
    Layer thisParentLayer;
    if (parentLayer == null)
    {
      thisParentLayer = thisLayer;
    }
    else
    {
      thisParentLayer = parentLayer;
    }

    // so, step through this layer
    if (thisLayer.getVisible())
    {
      boolean sorted = false;

      // is this layer a track?
      if (thisLayer instanceof DraggableItem)
      {
        final DraggableItem dw = (DraggableItem) thisLayer;

        // yup, find the distance to it's nearest point
        dw.findNearestHotSpotIn(cursorPos, cursorLoc, currentNearest,
            thisParentLayer, theData);

        // right, this one's processed. carry on
        sorted = true;
      }

      // have we processed this item
      if (!sorted)
      {
        // nope, let's just run through it
        final Enumeration<Editable> pts = thisLayer.elements();
        while (pts.hasMoreElements())
        {
          final Plottable pt = (Plottable) pts.nextElement();

          if (pt.getVisible())
          {

            // is this item a layer itself?
            if (pt instanceof Layer)
            {
              findNearest((Layer) pt, cursorLoc, cursorPos, currentNearest,
                  thisParentLayer, theData);
            }
            else
            {
              DraggableItem draggable = null;

              // is it a shape?
              if (pt instanceof DraggableItem)
              {
                draggable = (DraggableItem) pt;

                // yup, find the distance to it's nearest point
                draggable.findNearestHotSpotIn(cursorPos, cursorLoc,
                    currentNearest, thisParentLayer, theData);

                // right, this one's processed. carry on
                sorted = true;
              }

              if (!sorted)
              {
                final double rngDegs = pt.rangeFrom(cursorLoc);
                if (rngDegs != -1)
                {
                  final WorldDistance thisSep = new WorldDistance(pt.rangeFrom(
                      cursorLoc), WorldDistance.DEGS);
                  currentNearest.checkMe(draggable, thisSep, null, thisLayer);
                }
              }

            }
          }
        }
      }
    }
  }
}
