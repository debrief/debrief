package org.mwc.debrief.lite;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import org.geotools.swing.JMapPane;

import MWC.GUI.CanvasType;
import MWC.GUI.HasEditables;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GenericData.WorldArea;

public class LiteChart extends PlainChart
{

  private final CanvasType _myCanvas;
  private final JMapPane _map;

  public LiteChart(Layers theLayers, CanvasType canvas, JMapPane mapPane)
  {
    super(theLayers);
    _myCanvas = canvas;
    _map = mapPane;
  }

  
  
  @Override
  public WorldArea getProjectionArea()
  {
    WorldArea bounds = getLayers().getBounds();
    return bounds;
  }



  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public void rescale()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void update()
  {
    _map.repaint();
  }

  @Override
  public void update(HasEditables changedLayer)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void repaint()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void repaintNow(Rectangle rect)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Dimension getScreenSize()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public CanvasType getCanvas()
  {
    return _myCanvas;
  }

  @Override
  public Component getPanel()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
