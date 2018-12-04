/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.scripting.wrappers;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.eclipse.ease.modules.WrapToScript;

import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.CoastPainter;
import MWC.GUI.Chart.Painters.ETOPOPainter;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Chart.Painters.ScalePainter;
import MWC.GUI.Coast.Coastline;

public class Chart
{

  @WrapToScript
  public static ScalePainter createScale()
  {
    return new ScalePainter();
  }

  @WrapToScript
  public static GridPainter createGrid()
  {
    return new GridPainter();
  }

  @WrapToScript
  public static CoastPainter createCoastPainter()
  {
    return new CoastPainter();
  }

  @WrapToScript
  public static ETOPOPainter createETOPOPainter(final String pathName,
      final Layers parentLayers)
  {
    return new ETOPOPainter(pathName, parentLayers);
  }

  @WrapToScript
  public static Coastline loadCoastline(final InputStream inputStream)
      throws IOException, ParseException
  {
    return new Coastline(inputStream);
  }
  
  /*@WrapToScript
  public*/ 
}
