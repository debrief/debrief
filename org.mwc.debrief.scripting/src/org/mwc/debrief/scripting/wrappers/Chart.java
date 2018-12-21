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

/** capabilities related to managing the chart
 * 
 * @author ian
 *
 */
public class Chart
{

  
  /**
   * Function that creates a coast Painter object instance
   * 
   * @see MWC.GUI.Chart.Painters.CoastPainter
   * @return CoastPainter object created.
   * 		<br />
   * 		// @type MWC.GUI.Chart.Painters.CoastPainter
   * 
   */
  @WrapToScript
  public static CoastPainter createCoastPainter()
  {
    return new CoastPainter();
  }

  
  /**
   * Function that creates a ETOPOPainter object instance.
   * 
   * @see MWC.GUI.Chart.Painters.ETOPOPainter
   * @param pathName
   *          path of the ETOPOPainter object.
   * @param parentLayers
   *          Parent Layers of the ETOPOPainter.
   * @return ETOPOPainter object created.
   * 		<br />
   * 		// @type MWC.GUI.Chart.Painters.ETOPOPainter
   * 
   */
  @WrapToScript
  public static ETOPOPainter createETOPOPainter(final String pathName,
      final Layers parentLayers)
  {
    return new ETOPOPainter(pathName, parentLayers);
  }

  
  /**
   * Function that creates a GridPainter object instance
   * 
   * @see MWC.GUI.Chart.Painters.GridPainter
   * @return GridPainter object created.
   * 		<br />
   * 		// @type MWC.GUI.Chart.Painters.GridPainter
   * 
   */
  @WrapToScript
  public static GridPainter createGrid()
  {
    return new GridPainter();
  }

  
  /**
   * Function that creates a ScalePainter object instance
   * 
   * @see MWC.GUI.Chart.Painters.ScalePainter
   * @return ScalePainter object created.
   * 		<br />
   * 		// @type MWC.GUI.Chart.Painters.ScalePainter
   * 
   */
  @WrapToScript
  public static ScalePainter createScale()
  {
    return new ScalePainter();
  }

  
  /**
   * Function that creates a Coastline from an inputStream
   * 
   * @param inputStream
   *          Stream to read the information from
   * @see MWC.GUI.Coast.Coastline
   * @return Coastline object created.
   * 		<br />
   * 		// @type MWC.GUI.Coast.Coastline
   * 
   * @throws IOException
   *           Exception in case the stream is not available or corrupted.
   * @throws ParseException
   *           Exception in case the data has a wrong format.
   */
  @WrapToScript
  public static Coastline loadCoastline(final InputStream inputStream)
      throws IOException, ParseException
  {
    return new Coastline(inputStream);
  }
}
