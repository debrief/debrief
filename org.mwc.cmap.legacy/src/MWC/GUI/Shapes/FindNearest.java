package MWC.GUI.Shapes;

import java.util.Enumeration;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;

public class FindNearest
{
  public static void findNearest(final Layer thisLayer,
      final MWC.GenericData.WorldLocation cursorLoc, final java.awt.Point cursorPos,
      final MWC.GUI.Shapes.HasDraggableComponents.ComponentConstruct currentNearest,
      final Layer parentLayer)
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
}
