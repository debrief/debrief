package org.mwc.debrief.lite.map;

import java.awt.Point;

import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;

import MWC.GUI.Layers;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class DragElementTool extends GenericDragTool
{

  public DragElementTool(final Layers layers,
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
  public void onMouseDragged(MapMouseEvent ev)
  {
    if (panning)
    {
      Point pos = mouseDelta(ev.getPoint());

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

          _hoverTarget.shift(_hoverComponent, offset);
          _mapPane.repaint();
        }
        panePos = pos;
      }

    }
  }
}
