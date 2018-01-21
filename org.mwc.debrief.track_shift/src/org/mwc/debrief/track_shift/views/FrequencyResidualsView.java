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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.ZoneSlicer;
import org.mwc.debrief.track_shift.freq.DopplerCurve;

import MWC.GUI.JFreeChart.ColouredDataItem;

public class FrequencyResidualsView extends BaseStackedDotsView
{
  private static final int NUM_DOPPLER_STEPS = 30;
  private Action calcBaseFreq;

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
    // it doesn't update as we drag the solution. We need to
    // regenerate the interpolated fix, to ensure we're using up-to-date
    // values
    boolean updateDoubletsVal = true;

    // update the current datasets
    _myHelper.updateFrequencyData(_dotPlot, _linePlot, _myTrackDataProvider,
        _onlyVisible.isChecked(), _holder, this, updateDoubletsVal);
  }

  @Override
  protected void addExtras(final IToolBarManager toolBarManager)
  {
    super.addExtras(toolBarManager);
    toolBarManager.add(calcBaseFreq);
  }

  @Override
  protected void makeActions()
  {
    super.makeActions();

    // now frequency calculator
    calcBaseFreq =
        new Action("Calculate base frequency", IAction.AS_PUSH_BUTTON)
        {
          @Override
          public void run()
          {
            super.run();

            calculateBaseFreq();
          }
        };
    calcBaseFreq.setChecked(true);
    calcBaseFreq
        .setToolTipText("Calculate the base frequency of the visible frequency cuts");
    calcBaseFreq.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/24/f_nought.png"));
  }

  protected void calculateBaseFreq()
  {
    // get the currently visible data
    TimeSeriesCollection lineData =
        (TimeSeriesCollection) _linePlot.getDataset();
    TimeSeries measured = lineData.getSeries(StackedDotHelper.MEASURED_DATASET);

    if (measured != null)
    {
      ArrayList<Long> times = new ArrayList<Long>();
      ArrayList<Double> freqs = new ArrayList<Double>();

      // get the visible time range
      DateRange curRange = (DateRange) _linePlot.getDomainAxis().getRange();

      // loop through the measured data
      Iterator<?> items = measured.getItems().iterator();
      while (items.hasNext())
      {
        ColouredDataItem next = (ColouredDataItem) items.next();
        if (curRange.contains(next.getPeriod().getMiddleMillisecond()))
        {
          times.add(next.getPeriod().getMiddleMillisecond());
          freqs.add(next.getValue().doubleValue());
        }
      }

      if (!times.isEmpty())
      {
        // generate the doppler curve object
        final DopplerCurve curve = new DopplerCurve(times, freqs);

        // plot the curve
        TimeSeries calculatedData = calcSeries(curve, curRange);
        lineData.addSeries(calculatedData);

        showMessage("Calculate f-nought", "F=Nought is:"
            + curve.inflectionFreq());

        // TODO: update the sensor

        // and remove the curve
        lineData.removeSeries(calculatedData);
      }
    }
  }

  public void showMessage(final String title, final String message)
  {
    Display.getDefault().syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        MessageDialog.openInformation(null, title, message);
      }
    });
  }

  private TimeSeries calcSeries(DopplerCurve curve, DateRange curRange)
  {
    TimeSeries res = new TimeSeries("Fitted curve");
    long start = curRange.getLowerMillis();
    long end = curRange.getUpperMillis();
    long step = (end - start) / NUM_DOPPLER_STEPS;
    for (long t = start; t <= end; t += step)
    {
      res.add(new TimeSeriesDataItem(new FixedMillisecond(t), curve.valueAt(t)));
    }
    return res;
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
