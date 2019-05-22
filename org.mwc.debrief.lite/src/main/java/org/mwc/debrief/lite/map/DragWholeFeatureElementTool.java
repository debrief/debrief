package org.mwc.debrief.lite.map;

import java.awt.Point;

import org.geotools.swing.event.MapMouseEvent;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.FindNearest;
import MWC.GUI.Shapes.HasDraggableComponents.ComponentConstruct;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class DragWholeFeatureElementTool extends GenericDragTool
{

  public DragWholeFeatureElementTool(final Layers layers,
      final GeoToolMapProjection projection)
  {
    super(layers, projection);
  }

  /**
   * Respond to a mouse dragged event. Calls {@link org.geotools.swing.MapPane#moveImage()}
   *
   * @param ev
   *          the mouse event
   */
  @Override
  public void onMouseDragged(MapMouseEvent ev)
  {
    if (panning)
    {
      Point pos = ev.getPoint();
      System.out.println(pos.x + "," + pos.y);

      if (!pos.equals(panePos))
      {
        final java.awt.Point cursorPt = new java.awt.Point(pos.x, pos.y);
        final WorldLocation cursorLoc = _projection.toWorld(cursorPt);
        // find the nearest editable item
        final ComponentConstruct currentNearest = new ComponentConstruct();
        final int num = layers.size();
        for (int i = 0; i < num; i++)
        {
          final Layer thisL = layers.elementAt(i);
          if (thisL.getVisible())
          {
            // find the nearest items, this method call will recursively pass down
            // through
            // the layers
            FindNearest.findNearest(thisL, cursorLoc, cursorPt, currentNearest,
                null);
          }
        }

        // right, how did we get on?
        boolean highlightShown = false;

        // did we find anything?
        if (currentNearest.populated())
        {
          // generate a screen point from the cursor pos plus our distnace
          // NOTE: we're not basing this on the target location - we may not have
          // a
          // target location as such for a strangely shaped object
          final WorldLocation tgtPt = cursorLoc.add(new WorldVector(Math.PI / 2,
              currentNearest._distance, null));

          // is it close enough
          final java.awt.Point tPoint = _projection.toScreen(tgtPt);

          final double scrDist = tPoint.distance(new java.awt.Point(pos.x,
              pos.y));

          if (scrDist <= JITTER)
          {
            highlightShown = true;

            _hoverTarget = currentNearest._object;
            _hoverComponent = currentNearest._draggableComponent;
            _parentLayer = currentNearest._topLayer;

          }
        }

        if (!highlightShown)
        {
          // nope, we haven't found anything. clear our settings
          _hoverTarget = null;
          _hoverComponent = null;
          _parentLayer = null;
        }

        if (_hoverTarget != null)
        {
          final WorldLocation newLocation = new WorldLocation(_projection
              .toWorld(pos));

          // now work out the vector from the last place plotted to the current
          // place
          final WorldVector offset = newLocation.subtract(newLocation);

          System.out.println("Moviendo a " + offset);

          _hoverTarget.shift(_hoverComponent, offset);
        }
        // getMapPane().moveImage(pos.x - panePos.x, pos.y - panePos.y);
        panePos = pos;
      }

    }
  }
}
