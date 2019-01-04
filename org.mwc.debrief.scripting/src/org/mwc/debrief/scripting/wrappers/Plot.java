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

import org.eclipse.ease.modules.WrapToScript;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.scripting.wrappers.Layers.DLayers;

import MWC.Algorithms.PlainProjection;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * Plot class that exposes method related with the Plot/Editor.
 * 
 * @author Ian Mayo
 *
 */
public class Plot
{
  private final PlotEditor _plot;

  /**
   * Constructor that receives the plot editor
   * 
   * @see org.mwc.debrief.core.editors.PlotEditor
   * 
   * @param plot
   *          Plot reference
   */
  public Plot(final PlotEditor plot)
  {
    _plot = plot;
  }

  /**
   * Method that selects a zoom that shows all visible data
   */
  @WrapToScript
  public void fitToWindow()
  {
    _plot.getChart().rescale();
    getLayers().fireModified();
  }

  /**
   * Method that returns the currently selected data area
   * 
   * @see MWC.GenericData.WorldArea
   * @return Currently selected data area. <br />
   *         // @type MWC.GenericData.WorldArea
   * 
   */
  @WrapToScript
  public WorldArea getArea()
  {
    if (_plot != null)
    {
      final PlainProjection proj = (PlainProjection) _plot.getAdapter(
          PlainProjection.class);
      return proj.getDataArea();
    }
    return null;
  }

  /**
   * Function to determine the center of the area
   * 
   * @see MWC.GenericData.WorldLocation
   * @return Center of the area. <br />
   *         // @type MWC.GenericData.WorldLocation
   * 
   */
  @WrapToScript
  public WorldLocation getCentre()
  {
    final WorldArea area = getArea();
    if (area != null)
    {
      return area.getCentre();
    }
    else
    {
      return null;
    }
  }

  /**
   * Method that returns the layers of the current plot
   * 
   * @see org.mwc.debrief.scripting.wrappers.Layers.DLayers
   * @return Layers of the current plot. <br />
   *         // @type org.mwc.debrief.scripting.wrappers.Layers.DLayers
   */
  @WrapToScript
  public DLayers getLayers()
  {
    if (_plot != null)
    {
      final MWC.GUI.Layers layers = (MWC.GUI.Layers) _plot.getAdapter(
          MWC.GUI.Layers.class);
      if (layers != null)
      {
        return new DLayers(layers);
      }
    }
    return null;

  }

  /**
   * Method that returns the plain projection of the current plot
   * 
   * @see MWC.Algorithms.PlainProjection
   * @return Plain projection of the current plot. <br />
   *         // @type MWC.Algorithms.PlainProjection
   * 
   */
  @WrapToScript
  public PlainProjection getMap()
  {
    if (_plot != null)
    {
      final PlainProjection map = (PlainProjection) _plot.getAdapter(
          PlainProjection.class);
      if (map != null)
      {
        return map;
      }
    }
    return null;
  }

  /**
   * Method that returns the outline of the current plot.
   * 
   * @see org.mwc.debrief.scripting.wrappers.Outline
   * @return Outline of the current plot. <br />
   *         // @type org.mwc.debrief.scripting.wrappers.Outline
   * 
   */
  @WrapToScript
  public Outline getOutline()
  {
    if (_plot != null)
    {
      final IContentOutlinePage outline = (IContentOutlinePage) _plot
          .getAdapter(IContentOutlinePage.class);
      if (outline != null)
      {
        return new Outline(outline);
      }
    }
    return null;
  }

  /**
   * Method that returns the raw plot editor.
   * 
   * @see org.mwc.debrief.core.editors.PlotEditor
   * @return Plot Editor. <br />
   *         // @type org.mwc.debrief.core.editors.PlotEditor
   * 
   */
  @WrapToScript
  public PlotEditor getPlot()
  {
    return _plot;
  }

  /**
   * Retrieves the title of the current plot.
   * 
   * @return Title of the plot
   */
  @WrapToScript
  public String getTitle()
  {
    return _plot.getTitle();
  }

  /**
   * Method that returns the current time of the plot.
   * 
   * @see MWC.GenericData.HiResDate
   * @return Current time of the plot. <br />
   *         // @type MWC.GenericData.HiResDate
   * 
   */
  @WrapToScript
  public HiResDate getTime()
  {
    final TimeProvider time = (TimeProvider) _plot.getAdapter(
        TimeProvider.class);
    return time.getTime();
  }

  /**
   * Method that returns the Time Control Preferences. This object can change the step intervals,
   * small steps, etc.
   * 
   * @see org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences
   * @return Time Control Preferences. <br />
   *         // @type org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences
   * 
   */
  @WrapToScript
  public TimeControlPreferences getTimeControlPreferences()
  {
    if (_plot != null)
    {
      final TimeControlPreferences timeControlPreferences =
          (TimeControlPreferences) _plot.getAdapter(
              TimeControlPreferences.class);
      if (timeControlPreferences != null)
      {
        return timeControlPreferences;
      }
    }
    return null;
  }

  /**
   * Method that returns the Time Manager of the current plot.
   * 
   * @see org.mwc.cmap.core.DataTypes.Temporal.TimeManager
   * @return Time Manager of the current plot. <br />
   *         // @type org.mwc.cmap.core.DataTypes.Temporal.TimeManager
   * 
   */
  @WrapToScript
  public TimeManager getTimeManager()
  {
    if (_plot != null)
    {
      final TimeManager timeManager = (TimeManager) _plot.getAdapter(
          ControllableTime.class);
      if (timeManager != null)
      {
        return timeManager;
      }
    }
    return null;
  }

  /**
   * Method that returns a reference to the Tote of the plot given
   * 
   * @see org.mwc.debrief.scripting.wrappers.Tote
   * @return Reference to the Tote of the plot given <br />
   *         // @type org.mwc.debrief.scripting.wrappers.Tote
   * 
   */
  @WrapToScript
  public Tote getTote()
  {
    if (_plot != null)
    {
      final TrackManager trackManager = (TrackManager) _plot.getAdapter(
          TrackManager.class);
      if (trackManager != null)
      {
        Tote answer = new Tote();
        answer.setTrackManager(trackManager);
        return answer;
      }
    }
    return null;
  }
}
