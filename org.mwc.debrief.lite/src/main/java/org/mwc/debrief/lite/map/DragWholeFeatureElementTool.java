package org.mwc.debrief.lite.map;

import java.awt.Point;

import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GUI.Shapes.FindNearest;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class DragWholeFeatureElementTool extends GenericDragTool
{
  
  /**
   * the thing we're currently hovering over
   */
  protected DraggableItem _hoverTarget;

  public DragWholeFeatureElementTool(final Layers layers,
      final GeoToolMapProjection projection, final JMapPane mapPane)
  {
    super(layers, projection, mapPane);
  }

  



  /**
   * Respond to a mouse dragged event. Calls {@link org.geotools.swing.MapPane#moveImage()}
   *
   * @param ev
   *          the mouse event
   */
  @Override
  public void onMouseDragged(final MapMouseEvent ev)
  {
    if (panning)
    {
      final Point pos = mouseDelta(ev.getPoint());

      if (!pos.equals(panePos))
      {
        final WorldLocation cursorLoc = _projection.toWorld(panePos);

        if (_hoverTarget != null)
        {
          final WorldLocation newLocation = new WorldLocation(_projection
              .toWorld(pos));

          // now work out the vector from the last place plotted to the current
          // place
          final WorldVector offset = newLocation.subtract(cursorLoc);

          _hoverTarget.shift(offset);
          _mapPane.repaint();
        }
        panePos = pos;
      }
    }
  }

  /**
   * Respond to a mouse button press event from the map mapPane. This may signal the start of a
   * mouse drag. Records the event's window position.
   *
   * @param ev
   *          the mouse event
   */
  @Override
  public void onMousePressed(final MapMouseEvent ev)
  {
    super.onMousePressed(ev);
    if (!panning)
    {
      panePos = mouseDelta(ev.getPoint());

      final WorldLocation cursorLoc = _projection.toWorld(panePos);
      // find the nearest editable item
      final LocationConstruct currentNearest = new LocationConstruct();
      final int num = layers.size();
      for (int i = 0; i < num; i++)
      {
        final Layer thisL = layers.elementAt(i);
        if (thisL.getVisible())
        {
          // find the nearest items, this method call will recursively pass down
          // through
          // the layers
          // final Layer thisLayer,
          FindNearest.findNearest(thisL, cursorLoc, panePos, currentNearest,
              null, layers);
        }
      }

      // did we find anything?
      if (currentNearest.populated())
      {
        final double distance = currentNearest._distance.getValue();
        if (distance < JITTER)
        {
          panning = true;

          _hoverTarget = currentNearest._object;
          _parentLayer = currentNearest._topLayer;
        }
      }
    }
  }
}
