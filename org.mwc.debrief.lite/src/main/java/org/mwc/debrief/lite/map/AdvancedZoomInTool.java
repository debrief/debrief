package org.mwc.debrief.lite.map;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapViewport;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.ZoomInTool;

import com.vividsolutions.jts.geom.Coordinate;

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class AdvancedZoomInTool extends ZoomInTool
{

  private Point startPosDevice;
  private final Point2D startPosWorld;
  private boolean dragged;

  public AdvancedZoomInTool()
  {
    super();
    startPosDevice = new Point();
    startPosWorld = new DirectPosition2D();
    dragged = false;
  }

  @Override
  public void onMousePressed(MapMouseEvent ev)
  {
    startPosDevice.setLocation(ev.getPoint());
    startPosWorld.setLocation(ev.getWorldPos());
    super.onMousePressed(ev);
  }

  /**
   * Records that the mouse is being dragged
   *
   * @param ev
   *          the mouse event
   */
  @Override
  public void onMouseDragged(MapMouseEvent ev)
  {
    dragged = true;
    super.onMouseDragged(ev);
  }

  public void performZoomOut(MapMouseEvent ev)
  {
    MapViewport view = ev.getSource().getMapContent().getViewport();
    ReferencedEnvelope existingArea = view.getBounds();
    DirectPosition2D startWorld = new DirectPosition2D(startPosWorld);
    Envelope2D selectedArea = new Envelope2D(startWorld, ev.getMapPosition());
    final DirectPosition2D desiredCenter = new DirectPosition2D(selectedArea
        .getCenterX(), selectedArea.getCenterY());
    Coordinate centerC = ev.getSource().getMapContent().getViewport()
        .getBounds().centre();
    final DirectPosition2D actualCenter = new DirectPosition2D(centerC.x,
        centerC.y);
//    final double deltaX = actualCenter.getX() - desiredCenter.getX();
//    final double deltaY = actualCenter.getY() - desiredCenter.getY();

//    double scale = view.getWorldToScreen().getScaleX();
//    scale = Math.min(1000, scale);
//    double newScale = scale;
    

 //   Rectangle paneArea = view.getScreenArea();

    final double scaleVal = Math.sqrt((existingArea.getHeight() * existingArea.getWidth())
        / (selectedArea.height * selectedArea.width));
    
//    final double deltaX2 = selectedArea.getMaxX() - desiredCenter.x;
//    final double deltaY2 = selectedArea.getMinY() - desiredCenter.y;
    
    final double deltaX3 = existingArea.getMaxX() - actualCenter.getX();
    final double deltaY3 = existingArea.getMinY() - actualCenter.getY();
    
    final DirectPosition2D corner = new DirectPosition2D(desiredCenter.x + deltaX3 * scaleVal, 
        desiredCenter.y + deltaY3 * scaleVal);
    
//        selectedArea.getMaxX() - selected  desiredCenter.getX()
//        - 0.5d * existingArea.getWidth() / newScale, desiredCenter.getY() + 0.5d
//            * existingArea.getHeight() / newScale);
//
    final Envelope2D newMapArea = new Envelope2D();
    newMapArea.setFrameFromCenter(actualCenter, corner);

//    final double height = newMapArea.getHeight();
//    final double width = newMapArea.getWidth();
//    // translate
//    newMapArea.setFrameFromDiagonal(newMapArea.getBounds().x - deltaX
//        * scaleVal, newMapArea.getBounds().y + deltaY + scaleVal, newMapArea
//            .getBounds().x + deltaX * scaleVal, newMapArea.getBounds().y
//                - deltaY * scaleVal);
    
    ev.getSource().setDisplayArea(newMapArea);

  }

  public void onMouseReleased(MapMouseEvent ev)
  {
    if (dragged && !ev.getPoint().equals(startPosDevice))
    {
      final int overallX = ev.getX() - startPosDevice.x;
      final int overallY = ev.getY() - startPosDevice.y;

      // if the drag was from TL to BR
      if (overallX >= 0 || overallY >= 0)
      {
        super.onMouseReleased(ev);
      }
      else
      {
        performZoomOut(ev);
      }
    }
  }
}
