package org.mwc.debrief.limpet_integration.handlers;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.LineType;
import info.limpet.stackedcharts.model.MarkerStyle;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.StackedChartsView;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;

import Debrief.Tools.Tote.Calculations.atbCalc;
import Debrief.Tools.Tote.Calculations.bearingCalc;
import Debrief.Tools.Tote.Calculations.plainCalc;
import Debrief.Tools.Tote.Calculations.rangeCalc;
import Debrief.Tools.Tote.Calculations.relBearingCalc;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ShowTacticalOverview extends AbstractHandler
{

  // standard line thickness
  final static private float THICKNESS = 3;

  /**
   * The constructor.
   */
  public ShowTacticalOverview()
  {
  }

  /**
   * 
   * @param factory
   * @param pri
   * @param sec
   * @param string
   * @param rangeAxis
   * @param soloSeconary
   * @return
   */
  private Chart createRelativeChartFor(final StackedchartsFactory factory,
      final WatchableList pri, final WatchableList sec, final String string,
      final DependentAxis rangeAxis, final boolean soloSeconary)
  {
    final String name = pri.getName() + " vs " + sec.getName();
    final Color color = sec.getColor();

    final Chart chart = factory.createChart();
    chart.setName(name);

    // prepare our axes
    final DependentAxis bearingAxis = factory.createDependentAxis();
    bearingAxis.setAxisType(factory.createNumberAxis());
    bearingAxis.setName("Bearing");
    chart.getMinAxes().add(bearingAxis);

    final DependentAxis relBearingAxis = factory.createDependentAxis();
    relBearingAxis.setName("Rel Bearing");
    relBearingAxis.setAxisType(factory.createNumberAxis());
    chart.getMinAxes().add(relBearingAxis);

    // prepare our datasets
    final Dataset bearingData = factory.createDataset();
    bearingData.setName(sec.getName() + " Bearing (\u00b0)");
    final PlainStyling bearingStyle = factory.createPlainStyling();
    bearingStyle.setColor(color.darker().darker());
    bearingStyle.setLineStyle(LineType.DOTTED);
    bearingStyle.setLineThickness(THICKNESS);
    bearingData.setStyling(bearingStyle);
    bearingStyle.setMarkerStyle(MarkerStyle.NONE);
    bearingAxis.getDatasets().add(bearingData);

    final Dataset relBearingData = factory.createDataset();
    relBearingData.setName(sec.getName() + " Rel Bearing (\u00b0)");
    final PlainStyling relBearingStyle = factory.createPlainStyling();
    relBearingStyle.setColor(color);
    relBearingStyle.setLineThickness(THICKNESS);
    relBearingStyle.setLineStyle(LineType.DASHED);
    relBearingStyle.setMarkerStyle(MarkerStyle.NONE);
    relBearingData.setStyling(relBearingStyle);
    relBearingAxis.getDatasets().add(relBearingData);

    final Dataset atbData = factory.createDataset();
    atbData.setName(sec.getName() + " ATB (\u00b0)");
    final PlainStyling relStyle = factory.createPlainStyling();
    relStyle.setColor(color.brighter().brighter().brighter());
    relStyle.setLineThickness(THICKNESS);
    relStyle.setMarkerStyle(MarkerStyle.NONE);
    atbData.setStyling(relStyle);
    relBearingAxis.getDatasets().add(atbData);

    final Dataset rangeData = factory.createDataset();
    final String rangeName;
    if (soloSeconary)
    {
      rangeName = "Range (m)";
    }
    else
    {
      rangeName = sec + " Range (m)";
    }
    rangeData.setName(rangeName);
    final PlainStyling rangeStyle = factory.createPlainStyling();
    rangeStyle.setColor(sec.getColor());
    rangeStyle.setIncludeInLegend(false);
    rangeStyle.setMarkerStyle(MarkerStyle.NONE);
    rangeStyle.setLineThickness(THICKNESS);
    rangeData.setStyling(rangeStyle);
    rangeAxis.getDatasets().add(rangeData);

    // get the calculators
    final plainCalc range = new rangeCalc();
    final plainCalc brg = new bearingCalc();
    final plainCalc relB = new relBearingCalc();
    final plainCalc atb = new atbCalc();

    // now for the actual data
    final Collection<Editable> priItems =
        pri.getItemsBetween(pri.getStartDTG(), pri.getEndDTG());
    for (final Iterator<Editable> iterator = priItems.iterator(); iterator
        .hasNext();)
    {
      final Watchable thisPri = (Watchable) iterator.next();
      final HiResDate thisTime = thisPri.getTime();
      final long thisT = thisPri.getTime().getDate().getTime();

      // ok, and the sec?
      final Watchable[] thisSec = sec.getNearestTo(thisPri.getTime());
      if (thisSec != null && thisSec.length == 1)
      {
        DataItem item = factory.createDataItem();
        item.setDependentVal(brg.calculate(thisPri, thisSec[0], thisTime));
        item.setIndependentVal(thisT);
        bearingData.getMeasurements().add(item);

        item = factory.createDataItem();
        item.setDependentVal(relB.calculate(thisPri, thisSec[0], thisTime));
        item.setIndependentVal(thisT);
        relBearingData.getMeasurements().add(item);

        item = factory.createDataItem();
        item.setDependentVal(atb.calculate(thisPri, thisSec[0], thisTime));
        item.setIndependentVal(thisT);
        atbData.getMeasurements().add(item);

        item = factory.createDataItem();
        item.setDependentVal(range.calculate(thisPri, thisSec[0], thisTime));
        item.setIndependentVal(thisT);
        rangeData.getMeasurements().add(item);
      }

    }

    return chart;
  }

  private Chart createStateChartFor(final StackedchartsFactory factory,
      final WatchableList track)
  {
    final String name = track.getName();
    final Color color = track.getColor();

    final Chart chart = factory.createChart();
    chart.setName(name + " = State");

    // prepare our axes
    final DependentAxis courseAxis = factory.createDependentAxis();
    courseAxis.setName("Course");
    chart.getMinAxes().add(courseAxis);

    final DependentAxis speedAxis = factory.createDependentAxis();
    speedAxis.setName("Speed");
    chart.getMaxAxes().add(speedAxis);

    final DependentAxis depthAxis = factory.createDependentAxis();
    depthAxis.setName("Depth");
    boolean hasDepth = false;

    // prepare our datasets
    final Dataset courseData = factory.createDataset();
    courseData.setName("Course (\u00b0)");
    final PlainStyling courseStyle = factory.createPlainStyling();
    courseStyle.setColor(color.brighter());
    courseStyle.setIncludeInLegend(false);
    courseData.setStyling(courseStyle);
    courseStyle.setMarkerStyle(MarkerStyle.NONE);
    courseAxis.getDatasets().add(courseData);

    final Dataset speedData = factory.createDataset();
    speedData.setName("Speed (kts)");
    final PlainStyling speedStyle = factory.createPlainStyling();
    speedStyle.setColor(color.darker());
    speedStyle.setMarkerStyle(MarkerStyle.NONE);
    speedStyle.setIncludeInLegend(false);
    speedData.setStyling(speedStyle);
    speedAxis.getDatasets().add(speedData);

    final Dataset depthData = factory.createDataset();
    depthData.setName("Depth (m)");
    final PlainStyling depthStyle = factory.createPlainStyling();
    depthStyle.setColor(color);
    depthStyle.setMarkerStyle(MarkerStyle.NONE);
    depthStyle.setIncludeInLegend(false);
    depthData.setStyling(depthStyle);
    depthAxis.getDatasets().add(depthData);

    // now for the actual data
    final Collection<Editable> items =
        track.getItemsBetween(track.getStartDTG(), track.getEndDTG());

    for (final Iterator<Editable> iterator = items.iterator(); iterator
        .hasNext();)
    {
      final Watchable thisF = (Watchable) iterator.next();
      final long thisT = thisF.getTime().getDate().getTime();

      final DataItem course = factory.createDataItem();
      course.setDependentVal(Math.toDegrees(thisF.getCourse()));
      course.setIndependentVal(thisT);
      courseData.getMeasurements().add(course);

      final DataItem speed = factory.createDataItem();
      speed.setDependentVal(thisF.getSpeed());
      speed.setIndependentVal(thisT);
      speedData.getMeasurements().add(speed);

      final DataItem depth = factory.createDataItem();
      final double theDepth = thisF.getDepth();
      if (theDepth != 0)
      {
        hasDepth = true;
        depth.setDependentVal(theDepth);
        depth.setIndependentVal(thisT);
        depthData.getMeasurements().add(depth);
      }
    }

    // did we find depth?
    if (hasDepth)
    {
      chart.getMaxAxes().add(depthAxis);
    }

    return chart;
  }

  /**
   * the command has been executed, so extract extract the needed information from the application
   * context.
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException
  {

    // ok, get the active editor
    final IWorkbenchWindow window =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window == null)
    {
      // handle case where application is closing
      return null;
    }
    final IWorkbenchPage page = window.getActivePage();
    final IEditorPart editor = page.getActiveEditor();

    // get the tactical data manager
    final TrackDataProvider data =
        (TrackDataProvider) editor.getAdapter(TrackDataProvider.class);

    // just check we've got it
    if (data == null)
    {
      CorePlugin.logError(IStatus.INFO,
          "Failed to open Tactical Overview,  data not present", null);
      return null;
    }

    // do we have primary?
    final WatchableList pri = data.getPrimaryTrack();

    if (pri == null)
    {
      CorePlugin.showMessage("Tactical Overview",
          "A primary track must be assigned on the Track Tote");
      CorePlugin.logError(IStatus.INFO,
          "Failed to open Tactical Overview,  primary track not present", null);
      return null;
    }

    // do we have secondaries?
    final WatchableList[] secs = data.getSecondaryTracks();

    // ok, produce the chartset model
    final ChartSet charts = produceChartSet(pri, secs);

    // create a composite name, to use as the id
    String viewId = pri.getName();
    if (secs != null)
    {
      for (int i = 0; i < secs.length; i++)
      {
        final WatchableList watchableList = secs[i];
        viewId += watchableList.getName();
      }
    }

    // create a new instance of the Tactical Overview
    final String ID = StackedChartsView.ID;
    try
    {
      page.showView(ID, viewId, IWorkbenchPage.VIEW_ACTIVATE);
    }
    catch (final PartInitException e)
    {
      e.printStackTrace();
    }

    // send over the data
    final IViewReference viewRef = page.findViewReference(ID, viewId);
    if (viewRef != null)
    {
      final IViewPart theView = viewRef.getView(true);

      // double check it's what we're after
      if (theView instanceof StackedChartsView)
      {
        final StackedChartsView cv = (StackedChartsView) theView;

        if (charts != null)
        {
          // set follow selection to off
          cv.setModel(charts);

          // see if we have a time provider
          final TimeProvider timeProv =
              (TimeProvider) editor.getAdapter(TimeProvider.class);
          if (timeProv != null)
          {
            final PropertyChangeListener evt = new PropertyChangeListener()
            {

              @Override
              public void propertyChange(final PropertyChangeEvent evt)
              {
                final HiResDate hd = (HiResDate) evt.getNewValue();
                if (hd != null)
                {
                  final Date newDate = new Date(hd.getDate().getTime());
                  cv.updateTime(newDate);
                }
              }
            };
            timeProv.addListener(evt, TimeProvider.TIME_CHANGED_PROPERTY_NAME);

            // we also need to listen for it closing, to remove the listner
            cv.addRunOnCloseCallback(new Runnable()
            {

              @Override
              public void run()
              {
                timeProv.removeListener(evt,
                    TimeProvider.TIME_CHANGED_PROPERTY_NAME);
              }
            });
          }
        }
      }
    }

    return null;
  }

  private void processTrack(final WatchableList track,
      final DependentAxis courseAxis, final DependentAxis speedAxis,
      final DependentAxis depthAxis, final StackedchartsFactory factory)
  {
    // now for the actual data
    final Collection<Editable> items =
        track.getItemsBetween(track.getStartDTG(), track.getEndDTG());

    final Dataset courseData = factory.createDataset();
    courseData.setName(track.getName() + " Course");
    courseAxis.getDatasets().add(courseData);
    final PlainStyling courseStyle = factory.createPlainStyling();
    courseStyle.setColor(track.getColor());
    courseStyle.setMarkerStyle(MarkerStyle.NONE);
    courseStyle.setIncludeInLegend(false);
    courseStyle.setLineThickness(THICKNESS);
    courseData.setStyling(courseStyle);
    final Dataset speedData = factory.createDataset();
    speedData.setName(track.getName() + " Speed");
    final PlainStyling speedStyle = factory.createPlainStyling();
    speedStyle.setColor(track.getColor().brighter());
    speedStyle.setMarkerStyle(MarkerStyle.NONE);
    speedStyle.setIncludeInLegend(false);
    speedStyle.setLineThickness(THICKNESS);
    speedData.setStyling(speedStyle);
    speedAxis.getDatasets().add(speedData);
    final Dataset depthData = factory.createDataset();
    depthData.setName(track.getName() + " Depth");
    final PlainStyling depthStyle = factory.createPlainStyling();
    depthStyle.setColor(track.getColor().darker().darker());
    depthStyle.setLineThickness(THICKNESS);
    depthStyle.setMarkerStyle(MarkerStyle.NONE);
    depthStyle.setIncludeInLegend(false);
    depthData.setStyling(depthStyle);

    boolean hasDepth = false;

    for (final Iterator<Editable> iterator = items.iterator(); iterator
        .hasNext();)
    {
      final Watchable thisF = (Watchable) iterator.next();
      final long thisT = thisF.getTime().getDate().getTime();

      final DataItem course = factory.createDataItem();
      course.setDependentVal(Math.toDegrees(thisF.getCourse()));
      course.setIndependentVal(thisT);
      courseData.getMeasurements().add(course);

      final DataItem speed = factory.createDataItem();
      speed.setDependentVal(thisF.getSpeed());
      speed.setIndependentVal(thisT);
      speedData.getMeasurements().add(speed);

      final DataItem depth = factory.createDataItem();
      final double theDepth = thisF.getDepth();
      if (theDepth != 0)
      {
        hasDepth = true;
        depth.setDependentVal(theDepth);
        depth.setIndependentVal(thisT);
        depthData.getMeasurements().add(depth);
      }
    }

    if (hasDepth)
    {
      depthAxis.getDatasets().add(depthData);
    }
  }

  private ChartSet produceChartSet(final WatchableList pri,
      final WatchableList[] secs)
  {
    // produce the ChartSet
    final StackedchartsFactory factory = StackedchartsFactoryImpl.init();
    final ChartSet charts = factory.createChartSet();
    charts.setOrientation(Orientation.VERTICAL);

    // sort out the time axis
    final IndependentAxis ia = factory.createIndependentAxis();
    ia.setAxisType(factory.createDateAxis());
    ia.setName("Time");
    charts.setSharedAxis(ia);

    // producePerPlatformCharts(pri, secs, factory, charts);

    producePerStateCharts(pri, secs, factory, charts);

    // produce the range/sensor chart
    final Chart sensorChart = factory.createChart();
    sensorChart.setName("Contact");
    final DependentAxis rangeAxis = factory.createDependentAxis();
    rangeAxis.setName("Range");
    rangeAxis.setAxisType(factory.createNumberAxis());
    sensorChart.getMinAxes().add(rangeAxis);

    // produce the sensor coverage
    // no, don't bother
//    produceSensorCoverage(pri, ia, sensorChart, factory);

    // produce the relative chart
    if (secs != null)
    {
      // ok, we have some secondaries. how many?
      if (secs.length == 1)
      {
        // ok, single secondary - we just need on relative plot
        final Chart thisChart =
            createRelativeChartFor(factory, pri, secs[0], "Relative State",
                rangeAxis, true);
        charts.getCharts().add(thisChart);
      }
      else
      {
        // multiple secondaries - we need multiple relative plots
        // loop through them
        for (int i = 0; i < secs.length; i++)
        {
          // ok, single secondary - we just need on relative plot
          final Chart thisChart =
              createRelativeChartFor(factory, pri, secs[i], secs[i].getName()
                  + " vs " + pri.getName(), rangeAxis, false);
          charts.getCharts().add(thisChart);
        }
      }
    }

    // put the sensor chart at the bottom of the stack
    charts.getCharts().add(sensorChart);

    return charts;
  }

  @SuppressWarnings("unused")
  private void producePerPlatformCharts(final WatchableList pri,
      final WatchableList[] secs, final StackedchartsFactory factory,
      final ChartSet charts)
  {
    // produce the state charts
    Chart thisChart = createStateChartFor(factory, pri);
    charts.getCharts().add(thisChart);

    // and the secondaries
    for (int i = 0; i < secs.length; i++)
    {
      // ok, single secondary - we just need on relative plot
      thisChart = createStateChartFor(factory, secs[i]);
      charts.getCharts().add(thisChart);
    }
  }

  private void producePerStateCharts(final WatchableList pri,
      final WatchableList[] secs, final StackedchartsFactory factory,
      final ChartSet charts)
  {
    // right, create the charts
    final Chart speedChart = factory.createChart();
    speedChart.setName("Speed & Depth");
    final DependentAxis speedAxis = factory.createDependentAxis();
    speedAxis.setAxisType(factory.createNumberAxis());
    speedAxis.setName("Speed (Kts)");
    speedChart.getMinAxes().add(speedAxis);
    charts.getCharts().add(speedChart);

    // ok, loop through the calculations
    final Chart courseChart = factory.createChart();
    final DependentAxis courseAxis = factory.createDependentAxis();
    courseChart.setName("Course");
    courseAxis.setAxisType(factory.createNumberAxis());
    courseAxis.setName("Course (\u00b0)");
    courseChart.getMinAxes().add(courseAxis);
    charts.getCharts().add(courseChart);

    final DependentAxis depthAxis = factory.createDependentAxis();
    depthAxis.setAxisType(factory.createNumberAxis());
    depthAxis.setName("Depth (m)");
    // don't add it - we won't bother until we have depth
    // speedChart.getMaxAxes().add(speedAxis);

    // do the primary
    processTrack(pri, courseAxis, speedAxis, depthAxis, factory);

    for (int i = 0; i < secs.length; i++)
    {
      final WatchableList sec = secs[i];
      processTrack(sec, courseAxis, speedAxis, depthAxis, factory);
    }
  }

  @SuppressWarnings("unused")
  private void produceSensorCoverage(final WatchableList pri,
      final IndependentAxis ia, final Chart thisChart,
      final StackedchartsFactory factory)
  {
    ScatterSet scatter = null;

    // see what contact we have
    if (pri instanceof TrackWrapper)
    {
      final TrackWrapper track = (TrackWrapper) pri;
      final BaseLayer sensors = track.getSensors();
      if (sensors.size() > 0)
      {
        final Enumeration<Editable> numer = sensors.elements();
        while (numer.hasMoreElements())
        {
          final SensorWrapper thisS = (SensorWrapper) numer.nextElement();
          if (thisS.getVisible())
          {
            final Collection<Editable> matches =
                thisS.getItemsBetween(track.getStartDTG(), track.getEndDTG());

            // did we find any?
            if (matches != null)
            {
              final Iterator<Editable> sEnum = matches.iterator();
              while (sEnum.hasNext())
              {
                final SensorContactWrapper thisC =
                    (SensorContactWrapper) sEnum.next();
                if (thisC.getVisible())
                {
                  // represent it as a datum
                  final Datum datum = factory.createDatum();
                  datum.setVal(thisC.getDTG().getDate().getTime());
                  datum.setColor(thisC.getColor());

                  // ok, add it.
                  if (scatter == null)
                  {
                    final SelectiveAnnotation sel =
                        factory.createSelectiveAnnotation();
                    sel.getAppearsIn().add(thisChart);
                    scatter = factory.createScatterSet();
                    scatter.setColor(thisS.getColor());
                    scatter.setName(thisS.getName());
                    sel.setAnnotation(scatter);
                    ia.getAnnotations().add(sel);
                  }

                  scatter.getDatums().add(datum);
                }
              }
            }
          }
        }
      }
    }
  }
}
