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
  }

  @Override
  public void update()
  {
    _map.repaint();
  }

  @Override
  public void update(HasEditables changedLayer)
  {
  }

  @Override
  public void repaint()
  {
  }

  @Override
  public void repaintNow(Rectangle rect)
  {
  }

  @Override
  public Dimension getScreenSize()
  {
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
    return null;
  }

}