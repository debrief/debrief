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

public class Plot
{
  private final PlotEditor _plot;

  public Plot(final PlotEditor plot)
  {
    _plot = plot;
  }

  public void fitToWindow()
  {
    _plot.getChart().rescale();
    getLayers().fireModified();
  }

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

  public PlotEditor getPlot()
  {
    return _plot;
  }

  public HiResDate getTime()
  {
    final TimeProvider time = (TimeProvider) _plot.getAdapter(
        TimeProvider.class);
    return time.getTime();
  }

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

  public Tote getTote()
  {
    if (_plot != null)
    {
      final TrackManager trackManager = (TrackManager) _plot.getAdapter(
          TrackManager.class);
      if (trackManager != null)
      {
        return new Tote(trackManager);
      }
    }
    return null;
  }
}
