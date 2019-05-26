package org.mwc.debrief.lite.map;

import java.awt.Point;

import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;

import MWC.GUI.Layers;

public class DragWholeFeatureElementTool extends GenericDragTool
{

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
  public void onMouseDragged(MapMouseEvent ev)
  {
    if (panning)
    {
      Point pos = ev.getPoint();
      System.out.println(pos.x + "," + pos.y);
      /*
       * if (!pos.equals(panePos)) { getMapPane().moveImage(pos.x - panePos.x, pos.y - panePos.y);
       * panePos = pos; }
       */
    }
  }
}
