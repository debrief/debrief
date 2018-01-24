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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.ZoneSlicer;
import org.mwc.debrief.track_shift.freq.DopplerCurveFinMath;
import org.mwc.debrief.track_shift.freq.IDopplerCurve;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.Utilities.TextFormatting.GeneralFormat;

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

      SensorWrapper subjectSensor = null;

      // loop through the measured data
      Iterator<?> items = measured.getItems().iterator();
      while (items.hasNext())
      {
        ColouredDataItem next = (ColouredDataItem) items.next();
        if (curRange.contains(next.getPeriod().getMiddleMillisecond()))
        {
          times.add(next.getPeriod().getMiddleMillisecond());
          freqs.add(next.getValue().doubleValue());

          if (subjectSensor == null)
          {
            SensorContactWrapper cut = (SensorContactWrapper) next.getPayload();
            subjectSensor = cut.getSensor();
          }
        }
      }

      if (!times.isEmpty())
      {
        // generate the doppler curve object
        final IDopplerCurve curve = new DopplerCurveFinMath(times, freqs);

        // plot the curve
        TimeSeries calculatedData = calcSeries(curve, curRange);
        lineData.addSeries(calculatedData);

        // get the base freq
        double baseFreq = curve.inflectionFreq();

        // and the marker at the new value
        final Marker target = new ValueMarker(baseFreq);
        target.setPaint(Color.DARK_GRAY);
        target.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 1.0f, new float[]
            {10.0f, 6.0f}, 0.0f));
        target.setLabel("Target Price");
        target.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        target.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
        _linePlot.addRangeMarker(target);

        // what's the current frequency?
        final double oldFreq;

        String freqTxt = GeneralFormat.formatTwoDecimalPlaces(baseFreq);
        String message;
        String fMessage = "F-Nought is:" + freqTxt;
        if (subjectSensor != null)
        {
          oldFreq = subjectSensor.getBaseFrequency();
          message =
              fMessage + "\n" + "Updating base frequency for "
                  + subjectSensor.getName();
        }
        else
        {
          oldFreq = Double.NaN;
          message = fMessage;
        }
        showMessage("Calculate f-nought", message);

        // update the sensor
        final boolean freqChanged;
        if (subjectSensor != null && baseFreq != oldFreq)
        {
          subjectSensor.setBaseFrequency(baseFreq);
          freqChanged = true;
        }
        else
        {
          freqChanged = false;
        }

        // and remove the curves
        lineData.removeSeries(calculatedData);
        _linePlot.removeRangeMarker(target);

        // force recalculation, if we've changed the freq
        if (freqChanged)
        {
          updateData(true);
        }
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

  private TimeSeries calcSeries(IDopplerCurve curve, DateRange curRange)
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
