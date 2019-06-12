/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.lite.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.swing.MapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.ZoomInTool;
import org.geotools.swing.tool.ZoomOutTool;

/**
 * @author Ayesha
 *
 */
public class MyZoomoutTool extends ZoomOutTool
{
  private final Point startPosDevice;
  private final Point2D startPosWorld;
  private boolean dragged;
  private boolean reverseDragged;
  private MapPane _map;
  
  public MyZoomoutTool(MapPane map)
  {
    super();
    startPosDevice = new Point();
    startPosWorld = new DirectPosition2D();
    dragged = false;
    reverseDragged=false;
    _map = map;
  }
  @Override
  public boolean drawDragBox()
  {
    return true;
  }
  
  
  
  public void onMousePressed(MapMouseEvent ev) {
    startPosDevice.setLocation(ev.getPoint());
    startPosWorld.setLocation(ev.getWorldPos());
  }
  
  public void onMouseClicked(MapMouseEvent ev) {
    
    Rectangle paneArea = ((JComponent) getMapPane()).getVisibleRect();
    System.out.println("Rectangle:"+paneArea.x+","+paneArea.y+","+paneArea.width+","+paneArea.height);
    DirectPosition2D mapPos = ev.getWorldPos();

    double scale = getMapPane().getWorldToScreenTransform().getScaleX();
    double newScale = scale / zoom;

    DirectPosition2D corner =
            new DirectPosition2D(
                    mapPos.getX() - 0.5d * paneArea.getWidth() / newScale,
                    mapPos.getY() + 0.5d * paneArea.getHeight() / newScale);

    Envelope2D newMapArea = new Envelope2D();
    newMapArea.setFrameFromCenter(mapPos, corner);
    getMapPane().setDisplayArea(newMapArea);
}
  
  /**
   * Records that the mouse is being dragged
   *
   * @param ev the mouse event
   */
  @Override
  public void onMouseDragged(MapMouseEvent ev) {
      dragged = true;
      reverseDragged=false;
      
      
  }
  public boolean isReverseDragged()
  {
    return reverseDragged;
  }
  public void onMouseReleased(MapMouseEvent ev) {
    if (dragged && !ev.getPoint().equals(startPosDevice)) {
      final int overallX = ev.getX() - startPosDevice.x;
      final int overallY = ev.getY()- startPosDevice.y;

      // if the drag was from TL to BR
      if (overallX >= 0 || overallY >= 0)
      {
        reverseDragged=false;
        ZoomInTool tool = new MyZoominTool(_map);
        _map.setCursorTool(tool);
        tool.onMouseReleased(ev);
      }
      else {
        reverseDragged = true;
        _map.setCursorTool(this);
        super.onMouseClicked(ev);
      }
}
 
  
}
}
