/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.track_shift.views;

import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.ZoneSlicer;

public class FrequencyResidualsView extends BaseStackedDotsView
{
  public FrequencyResidualsView()
  {
    super(false, true);
  }

  protected String getUnits()
  {
    return "Hz";
  }

  protected String getType()
  {
    return "Frequency";
  }

  protected void updateData(final boolean updateDoublets)
  {
    // note: we now ignore the parent doublets value.
    // this is because we need to regenerate the target fix each time.
    // For frequency data the target fix is interpolated - this means
    // it doesn't update as we drag the solution.  We need to 
    // regenerate the interpolated fix, to ensure we're using up-to-date
    // values
    boolean updateDoubletsVal = true;
    
    // update the current datasets
    _myHelper.updateFrequencyData(_dotPlot, _linePlot, _myTrackDataProvider,
        _onlyVisible.isChecked(), _holder, this, updateDoubletsVal);
  }

  @Override
  protected ZoneSlicer getOwnshipZoneSlicer(ColorProvider blueProv)
  {
    // don't bother, it's for bearing data
    return null;
  }
  
  @Override
  protected String formatValue(double value)
  {
    return MWC.Utilities.TextFormatting.GeneralFormat
        .formatTwoDecimalPlaces(value);
  }

  @Override
  protected boolean allowDisplayOfTargetOverview()
  {
    return false;
  }

  @Override
  protected boolean allowDisplayOfZoneChart()
  {
    return false;
  }

}
