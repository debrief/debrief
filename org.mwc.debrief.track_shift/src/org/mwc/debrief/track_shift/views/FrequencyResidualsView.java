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
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.mwc.debrief.track_shift.views.StackedDotHelper.SetBackgroundShade;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GenericData.WatchableList;
import MWC.Utilities.TextFormatting.GeneralFormat;

public class FrequencyResidualsView extends BaseStackedDotsView
{
  private static final int NUM_DOPPLER_STEPS = 30;
  private Action calcBaseFreq;

  public FrequencyResidualsView()
  {
    super(false, true);
  }

  @Override
  protected void addToolbarExtras(final IToolBarManager toolBarManager)
  {
    super.addToolbarExtras(toolBarManager);

    toolBarManager.add(calcBaseFreq);
  }

  private List<WatchableList> getPotentialSources()
  {
    return null;
  }

  @Override
  protected void addPullDownExtras(IMenuManager manager)
  {
    super.addPullDownExtras(manager);

    System.out.println("in pulldown");

    // ok, can we add a combo box?
    manager.add(new Separator());
    final MenuManager newMenu = new MenuManager("Acoustic Source");
    newMenu.setRemoveAllWhenShown(true);
    newMenu.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager manager)
      {
        List<WatchableList> sources = getPotentialSources();
        String date = new Date().toString();
        Action deleteAction2 = new Action("Placeholder at: " + date)
        {
          public void run()
          {
            System.out.println("new date pressed");
          }
        };
        newMenu.add(deleteAction2);
        if (sources != null)
        {
          for (WatchableList track : sources)
          {
            Action deleteAction = new Action(track.getName() + " " + date)
            {
              public void run()
              {
                System.out.println("new date pressed");
              }
            };
            newMenu.add(deleteAction);
          }
        }
      }
    });
    manager.add(newMenu);

    // }
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

  private TimeSeries calcSeries(final IDopplerCurve curve,
      final DateRange curRange)
  {
    final TimeSeries res = new TimeSeries("Fitted curve");
    final long start = curRange.getLowerMillis();
    final long end = curRange.getUpperMillis();
    final long step = (end - start) / NUM_DOPPLER_STEPS;
    for (long t = start; t <= end; t += step)
    {
      res.add(new TimeSeriesDataItem(new FixedMillisecond(t), curve.valueAt(
          t)));
    }
    return res;
  }

  protected void calculateBaseFreq()
  {
    // get the currently visible data
    final TimeSeriesCollection lineData = (TimeSeriesCollection) _linePlot
        .getDataset();
    final TimeSeries measured = lineData.getSeries(
        StackedDotHelper.MEASURED_DATASET);

    if (measured != null)
    {
      final ArrayList<Long> times = new ArrayList<Long>();
      final ArrayList<Double> freqs = new ArrayList<Double>();

      // get the visible time range
      final DateRange curRange = (DateRange) _linePlot.getDomainAxis()
          .getRange();

      // put the data into the storage structures
      final SensorWrapper subjectSensor = collateData(measured, times, freqs,
          curRange);

      if (!times.isEmpty())
      {
        // generate the doppler curve object
        final IDopplerCurve curve = new DopplerCurveFinMath(times, freqs);

        // plot the curve
        final TimeSeries calculatedData = calcSeries(curve, curRange);
        lineData.addSeries(calculatedData);

        // get the base freq
        final double baseFreq = curve.inflectionFreq();

        // and the marker at the new value
        final Marker target = createFreqMarker(baseFreq);
        _linePlot.addRangeMarker(target);

        // what's the current frequency?
        final double oldFreq;

        final String freqTxt = GeneralFormat.formatTwoDecimalPlaces(baseFreq);
        String message;
        final String fMessage = "F-Nought is:" + freqTxt;
        if (subjectSensor != null)
        {
          oldFreq = subjectSensor.getBaseFrequency();
          message = fMessage + "\n" + "Updating base frequency for "
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

  private SensorWrapper collateData(final TimeSeries measured,
      final ArrayList<Long> times, final ArrayList<Double> freqs,
      final DateRange curRange)
  {
    SensorWrapper subjectSensor = null;

    // loop through the measured data
    final Iterator<?> items = measured.getItems().iterator();
    while (items.hasNext())
    {
      final ColouredDataItem next = (ColouredDataItem) items.next();
      if (curRange.contains(next.getPeriod().getMiddleMillisecond()))
      {
        times.add(next.getPeriod().getMiddleMillisecond());
        freqs.add(next.getValue().doubleValue());

        if (subjectSensor == null)
        {
          final SensorContactWrapper cut = (SensorContactWrapper) next
              .getPayload();
          subjectSensor = cut.getSensor();
        }
      }
    }
    return subjectSensor;
  }

  private Marker createFreqMarker(final double baseFreq)
  {
    final Marker target = new ValueMarker(baseFreq);
    target.setPaint(Color.DARK_GRAY);
    target.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND, 1.0f, new float[]
        {10.0f, 6.0f}, 0.0f));
    target.setLabel("Target Price");
    target.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
    target.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
    return target;
  }

  @Override
  protected String formatValue(final double value)
  {
    return GeneralFormat.formatTwoDecimalPlaces(value);
  }

  @Override
  protected ZoneSlicer getOwnshipZoneSlicer(final ColorProvider blueProv)
  {
    // don't bother, it's for bearing data
    return null;
  }

  @Override
  protected String getType()
  {
    return "Frequency";
  }

  @Override
  protected String getUnits()
  {
    return "Hz";
  }

  @Override
  protected void makeActions()
  {
    super.makeActions();

    // now frequency calculator
    calcBaseFreq = new Action("Calculate base frequency",
        IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void run()
      {
        super.run();

        calculateBaseFreq();
      }
    };
    calcBaseFreq.setChecked(true);
    calcBaseFreq.setToolTipText(
        "Calculate the base frequency of the visible frequency cuts");
    calcBaseFreq.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/24/f_nought.png"));
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

  @Override
  protected void updateData(final boolean updateDoublets)
  {
    // note: we now ignore the parent doublets value.
    // this is because we need to regenerate the target fix each time.
    // For frequency data the target fix is interpolated - this means
    // it doesn't update as we drag the solution. We need to
    // regenerate the interpolated fix, to ensure we're using up-to-date
    // values
    final boolean updateDoubletsVal = true;

    final TimeSeriesCollection errorData = (TimeSeriesCollection) _dotPlot
        .getDataset();
    final TimeSeriesCollection lineData = (TimeSeriesCollection) _linePlot
        .getDataset();

    final SetBackgroundShade backgroundShader = new SetBackgroundShade()
    {

      @Override
      public void setShade(Paint errorColor)
      {
        _dotPlot.setBackgroundPaint(errorColor);
      }
    };

    // have we been created?
    if (_holder == null || _holder.isDisposed())
    {
      return;
    }

    // update the current datasets
    _myHelper.updateFrequencyData(errorData, lineData,
        _switchableTrackDataProvider, _onlyVisible.isChecked(), this,
        updateDoubletsVal, backgroundShader,
        (ColourStandardXYItemRenderer) _linePlot.getRenderer());
  }

}
