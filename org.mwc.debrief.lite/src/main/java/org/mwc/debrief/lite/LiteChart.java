package org.mwc.debrief.lite;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import org.geotools.swing.JMapPane;
import org.mwc.debrief.lite.gui.FitToWindow;

import MWC.GUI.CanvasType;
import MWC.GUI.HasEditables;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GenericData.WorldArea;

public class LiteChart extends PlainChart
{

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final CanvasType _myCanvas;

  private final JMapPane _map;

  public LiteChart(final Layers theLayers, final CanvasType canvas,
      final JMapPane mapPane)
  {
    super(theLayers);
    _myCanvas = canvas;
    _map = mapPane;
  }

  @Override
  public CanvasType getCanvas()
  {
    return _myCanvas;
  }

  @Override
  public Component getPanel()
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public WorldArea getProjectionArea()
  {
    final WorldArea bounds = getLayers().getBounds();
    return bounds;
  }

  @Override
  public Dimension getScreenSize()
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public void repaint()
  {
    // don't bother, we don't need it
  }

  @Override
  public void repaintNow(final Rectangle rect)
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public void rescale()
  {

    FitToWindow.fitToWindow(_theLayers, _map);
  }

  @Override
  public void update()
  {
    _map.repaint();
  }

  @Override
  public void update(final HasEditables changedLayer)
  {
    // don't bother, we don't need it
  }

}