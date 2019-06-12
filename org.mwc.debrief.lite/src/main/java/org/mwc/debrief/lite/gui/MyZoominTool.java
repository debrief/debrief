package org.mwc.debrief.lite.gui;


import java.awt.Point;
import java.awt.geom.Point2D;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.swing.MapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.ZoomInTool;
import org.geotools.swing.tool.ZoomOutTool;

public class MyZoominTool extends ZoomInTool
  {
    private  Point startPosDevice;
    private final Point2D startPosWorld;
    private boolean dragged;
    private boolean reverseDragged;
    private MapPane _map;
    public MyZoominTool(MapPane map)
    {
      super();
      _map = map;
      startPosDevice = new Point();
      startPosWorld = new DirectPosition2D();
      dragged = false;
      reverseDragged=false;
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
     * @param ev the mouse event
     */
    @Override
    public void onMouseDragged(MapMouseEvent ev) {
        dragged = true;
        reverseDragged=false;
        super.onMouseDragged(ev);
        
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
          _map.setCursorTool(this);
          super.onMouseReleased(ev);
        }
        else {
          reverseDragged = true;
          ZoomOutTool tool = new MyZoomoutTool(_map);
          _map.setCursorTool(tool);
          tool.onMouseReleased(ev);
        }
          /*Envelope2D env = new Envelope2D();
          env.setFrameFromDiagonal(startPosWorld, ev.getWorldPos());
          dragged = false;
          getMapPane().setDisplayArea(env);*/
      }
  }
   
    
  }