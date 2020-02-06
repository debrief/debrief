/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package org.mwc.debrief.lite;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import org.geotools.swing.JMapPane;
import org.mwc.debrief.lite.gui.FitToWindow;

import MWC.Algorithms.PlainProjection;
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
  
  final PlainProjection _projection;

  public LiteChart(final Layers theLayers, final CanvasType canvas,
      final JMapPane mapPane, final PlainProjection projection)
  {
    super(theLayers);
    _myCanvas = canvas;
    _map = mapPane;
    _projection = projection;
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

    FitToWindow.fitToWindow(_theLayers, _map, _projection);
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