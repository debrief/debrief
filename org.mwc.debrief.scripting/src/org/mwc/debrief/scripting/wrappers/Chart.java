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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ease.modules.WrapToScript;
import org.mwc.cmap.naturalearth.ui.InsertNaturalEarth;
import org.mwc.debrief.core.creators.chartFeatures.Insert4WGrid;
import org.mwc.debrief.core.creators.chartFeatures.InsertChartLibrary;
import org.mwc.debrief.core.creators.chartFeatures.InsertCoastline;
import org.mwc.debrief.core.creators.chartFeatures.InsertETOPO;
import org.mwc.debrief.core.creators.chartFeatures.InsertGrid;
import org.mwc.debrief.core.creators.chartFeatures.InsertLocalGrid;
import org.mwc.debrief.core.creators.chartFeatures.InsertScale;
import org.mwc.debrief.core.creators.chartFeatures.InsertTimeDisplayAbsolute;
import org.mwc.debrief.core.creators.chartFeatures.InsertTimeDisplayRelative;
import org.mwc.debrief.core.creators.chartFeatures.InsertVPFLayers;

/**
 * capabilities related to managing the chart
 *
 * @author ian
 *
 */
public class Chart
{

  /**
   * Function that adds a Scale to the current plot
   */
  @WrapToScript
  public static void addScale()
  {
    new InsertScale().execute();
  }

  /**
   * Function that adds the time display (Absolute) to the current plot.
   */
  @WrapToScript
  public static void addTimeDisplayAbsolute()
  {
    new InsertTimeDisplayAbsolute().execute();
  }

  /**
   * Function that adds the time display (Relative) to the current plot.
   */
  @WrapToScript
  public static void addTimeDisplayRelative()
  {
    new InsertTimeDisplayRelative().execute();
  }

  /**
   * Function that adds a 4WGrid to the current plot.
   */
  @WrapToScript
  public static void add4WGrid()
  {
    new Insert4WGrid().execute();
  }

  /**
   * Function that adds a Grid to the current plot.
   */
  @WrapToScript
  public static void addGrid()
  {
    new InsertGrid().execute();
  }

  /**
   * Function that adds a Local Grid to the current plot.
   */
  @WrapToScript
  public static void addLocalGrid()
  {
    new InsertLocalGrid().execute();
  }

  /**
   * Function that adds a Coastline to the current plot.
   */
  @WrapToScript
  public static void loadCoastLine()
  {
    new InsertCoastline().execute();
  }

  /**
   * Function that adds a ETOPO to the current plot.
   */
  @WrapToScript
  public static void addETOPO()
  {
    new InsertETOPO().execute();
  }

  /**
   * Function that adds the Chart Library defined in the maritime preferences to the current plot.
   */
  @WrapToScript
  public static void addChartLibrary()
  {
    new InsertChartLibrary().execute();
  }

  /**
   * Function that adds the VPT Layers to the current plot.
   */
  @WrapToScript
  public static void addVPTLayers()
  {
    new InsertVPFLayers().execute();
  }

  @WrapToScript
  public static void loadNaturalEarth() throws ExecutionException
  {
    new InsertNaturalEarth().execute(null);
  }
}
