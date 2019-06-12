package org.mwc.debrief.lite.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.geotools.swing.JMapPane;

public class ZoomIn extends AbstractAction
{
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final JMapPane _map;

  private MyZoominTool tool;
  public ZoomIn(final JMapPane map)
  {
    _map = map;
   tool = new MyZoominTool(map);
   
  }

  @Override
  public void actionPerformed(final ActionEvent e)
  {
    _map.setCursorTool(tool);
  }
  
  

}
