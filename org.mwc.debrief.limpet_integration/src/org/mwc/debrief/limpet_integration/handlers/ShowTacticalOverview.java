package org.mwc.debrief.limpet_integration.handlers;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.MarkerStyle;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.StackedChartsView;

import java.awt.Color;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
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
  /**
   * The constructor.
   */
  public ShowTacticalOverview()
  {
  }

  /**
   * the command has been executed, so extract extract the needed information from the application
   * context.
   */
  public Object execute(ExecutionEvent event) throws ExecutionException
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
    TrackDataProvider data =
        (TrackDataProvider) editor.getAdapter(TrackDataProvider.class);

    // just check we've got it
    if (data == null)
    {
      CorePlugin.logError(Status.INFO,
          "Failed to open Tactical Overview,  data not present", null);
      return null;
    }

    // do we have primary?
    WatchableList pri = data.getPrimaryTrack();

    if (pri == null)
    {
      CorePlugin.showMessage("Tactical Overview",
          "A primary track must be assigned on the Track Tote");
      CorePlugin.logError(Status.INFO,
          "Failed to open Tactical Overview,  primary track not present", null);
      return null;
    }

    // do we have secondaries?
    WatchableList[] secs = data.getSecondaryTracks();
    
    // ok, produce the chartset model
    ChartSet charts = produceChartSet(pri, secs);

    // create a new instance of the Tactical Overview
    String ID = StackedChartsView.ID;
    String title = editor.getTitle() + " - Tactical Overview";
    try
    {
      page.showView(ID, title, IWorkbenchPage.VIEW_ACTIVATE);
    }
    catch (PartInitException e)
    {
      e.printStackTrace();
    }

    // send over the data
    IViewReference viewRef = page.findViewReference(ID, title);
    if (viewRef != null)
    {
      IViewPart theView = viewRef.getView(true);

      // double check it's what we're after
      if (theView instanceof StackedChartsView)
      {
        StackedChartsView cv = (StackedChartsView) theView;

        if (charts != null)
        {
          // set follow selection to off
          cv.setModel(charts);
        }
      }
    }

    return null;
  }

  private ChartSet produceChartSet(WatchableList pri, WatchableList[] secs)
  {
    // produce the ChartSet
    StackedchartsFactory factory = StackedchartsFactoryImpl.init();
    ChartSet charts = factory.createChartSet();
    charts.setOrientation(Orientation.VERTICAL);

    // sort out the time axis
    IndependentAxis ia = factory.createIndependentAxis();
    ia.setAxisType(factory.createDateAxis());
    ia.setName("Time");
    charts.setSharedAxis(ia);

//    producePerPlatformCharts(pri, secs, factory, charts);

    producePerStateCharts(pri, secs, factory, charts);
    
    Chart thisChart = null;
    
    // produce the relative chart
    if (secs != null)
    {
      // ok, we have some secondaries. how many?
      if (secs.length == 1)
      {
        // ok, single secondary - we just need on relative plot
        thisChart =
            createRelativeChartFor(factory, pri, secs[0], "Relative State");
        charts.getCharts().add(thisChart);
      }
      else
      {
        // multiple secondaries - we need multiple relative plots

        // loop through them
        for (int i = 0; i < secs.length; i++)
        {
          // ok, single secondary - we just need on relative plot
          thisChart =
              createRelativeChartFor(factory, pri, secs[i], secs[i].getName()
                  + " vs " + pri.getName());
          charts.getCharts().add(thisChart);
        }
      }
    }

    // produce the sensor coverage
    produceSensorCoverage(pri, ia, thisChart, factory);
    return charts;
  }

  private void produceSensorCoverage(WatchableList pri, 
      IndependentAxis ia, Chart thisChart, StackedchartsFactory factory)
  {
    ScatterSet scatter = null;
    
    // see what contact we have
    if(pri instanceof TrackWrapper)
    {
      TrackWrapper track = (TrackWrapper) pri;
      BaseLayer sensors = track.getSensors();
      if(sensors.size() > 0)
      {
        Enumeration<Editable> numer = sensors.elements();
        while (numer.hasMoreElements())
        {
          SensorWrapper thisS = (SensorWrapper) numer.nextElement();
          if(thisS.getVisible())
          {
            Collection<Editable> matches = thisS.getItemsBetween(track.getStartDTG(), track.getEndDTG());
            Iterator<Editable> sEnum = matches.iterator();
            while (sEnum.hasNext())
            {
              SensorContactWrapper thisC = (SensorContactWrapper) sEnum.next();
              if(thisC.getVisible())
              {
                // represent it as a datum
                Datum datum = factory.createDatum();
                datum.setVal(thisC.getDTG().getDate().getTime());

                // ok, add it.
                if(scatter == null)
                {
                  SelectiveAnnotation sel = factory.createSelectiveAnnotation();
                  sel.getAppearsIn().add(thisChart);
                  scatter = factory.createScatterSet();
                  scatter.setColor(thisS.getColor());
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

  private void producePerStateCharts(WatchableList pri, WatchableList[] secs,
      StackedchartsFactory factory, ChartSet charts)
  {
    // ok, loop through the calculations
    Chart courseChart = factory.createChart();
    DependentAxis courseAxis = factory.createDependentAxis();
    courseAxis.setAxisType(factory.createNumberAxis());
    courseAxis.setName("Course (\u00b0)");
    courseChart.getMinAxes().add(courseAxis);
    charts.getCharts().add(courseChart);
    
    Chart speedChart = factory.createChart();
    DependentAxis speedAxis = factory.createDependentAxis();
    speedAxis.setAxisType(factory.createNumberAxis());
    speedAxis.setName("Speed (Kts)");
    speedChart.getMinAxes().add(speedAxis);
    charts.getCharts().add(speedChart);

    DependentAxis depthAxis = factory.createDependentAxis();
    depthAxis.setAxisType(factory.createNumberAxis());
    depthAxis.setName("Depth (m)");
    // don't add it - we won't bother until we have depth
//    speedChart.getMaxAxes().add(speedAxis);

    // do the primary
    processTrack(pri, courseAxis, speedAxis, depthAxis, factory);
    
    for (int i = 0; i < secs.length; i++)
    {
      WatchableList sec = secs[i];
      processTrack(sec, courseAxis, speedAxis, depthAxis, factory);
    }
  }

  private void processTrack(WatchableList track, DependentAxis courseAxis,
      DependentAxis speedAxis, DependentAxis depthAxis,
      StackedchartsFactory factory)
  {
    // now for the actual data
    final Collection<Editable> items =
        track.getItemsBetween(track.getStartDTG(), track.getEndDTG());

    Dataset courseData = factory.createDataset();
    courseData.setName(track.getName() + "- Course");
    courseAxis.getDatasets().add(courseData);
    PlainStyling courseStyle = factory.createPlainStyling();
    courseStyle.setColor(track.getColor());
    Dataset speedData = factory.createDataset();
    speedData.setName(track.getName() + "- Speed");
    PlainStyling speedStyle = factory.createPlainStyling();
    speedStyle.setColor(track.getColor().brighter());
    speedData.setStyling(speedStyle);    
    speedAxis.getDatasets().add(speedData);
    Dataset depthData = factory.createDataset();
    depthData.setName(track.getName() + "- Depth");
    PlainStyling depthStyle = factory.createPlainStyling();
    depthStyle.setColor(track.getColor().darker().darker());
    depthData.setStyling(depthStyle);
    
    boolean hasDepth = false;
    
    for (Iterator<Editable> iterator = items.iterator(); iterator.hasNext();)
    {
      Watchable thisF = (Watchable) iterator.next();
      final long thisT = thisF.getTime().getDate().getTime();

      DataItem course = factory.createDataItem();
      course.setDependentVal(Math.toDegrees(thisF.getCourse()));
      course.setIndependentVal(thisT);
      courseData.getMeasurements().add(course);

      DataItem speed = factory.createDataItem();
      speed.setDependentVal(thisF.getSpeed());
      speed.setIndependentVal(thisT);
      speedData.getMeasurements().add(speed);

      DataItem depth = factory.createDataItem();
      final double theDepth = thisF.getDepth();
      if (theDepth != 0)
      {
        hasDepth = true;
        depth.setDependentVal(theDepth);
        depth.setIndependentVal(thisT);
        depthData.getMeasurements().add(depth);
      }
    }
    
    if(hasDepth)
    {
      depthAxis.getDatasets().add(depthData);
    }
  }

  @SuppressWarnings("unused")
  private void producePerPlatformCharts(WatchableList pri,
      WatchableList[] secs, StackedchartsFactory factory, ChartSet charts)
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

  private Chart createStateChartFor(StackedchartsFactory factory,
      WatchableList track)
  {
    final String name = track.getName();
    final Color color = track.getColor();

    Chart chart = factory.createChart();
    chart.setName(name + " = State");

    // prepare our axes
    DependentAxis courseAxis = factory.createDependentAxis();
    courseAxis.setName("Course");
    chart.getMinAxes().add(courseAxis);

    DependentAxis speedAxis = factory.createDependentAxis();
    speedAxis.setName("Speed");
    chart.getMaxAxes().add(speedAxis);

    DependentAxis depthAxis = factory.createDependentAxis();
    depthAxis.setName("Depth");
    boolean hasDepth = false;

    // prepare our datasets
    Dataset courseData = factory.createDataset();
    courseData.setName("Course (\u00b0)");
    PlainStyling courseStyle = factory.createPlainStyling();
    courseStyle.setColor(color.brighter());
    courseData.setStyling(courseStyle);
    courseStyle.setMarkerStyle(MarkerStyle.CIRCLE);
    courseAxis.getDatasets().add(courseData);

    Dataset speedData = factory.createDataset();
    speedData.setName("Speed (kts)");
    PlainStyling speedStyle = factory.createPlainStyling();
    speedStyle.setColor(color.darker());
    speedStyle.setMarkerStyle(MarkerStyle.CROSS);
    speedData.setStyling(speedStyle);
    speedAxis.getDatasets().add(speedData);

    Dataset depthData = factory.createDataset();
    depthData.setName("Depth (m)");
    PlainStyling depthStyle = factory.createPlainStyling();
    depthStyle.setColor(color);
    depthStyle.setMarkerStyle(MarkerStyle.NONE);
    depthData.setStyling(depthStyle);
    depthAxis.getDatasets().add(depthData);

    // now for the actual data
    final Collection<Editable> items =
        track.getItemsBetween(track.getStartDTG(), track.getEndDTG());

    for (Iterator<Editable> iterator = items.iterator(); iterator.hasNext();)
    {
      Watchable thisF = (Watchable) iterator.next();
      final long thisT = thisF.getTime().getDate().getTime();

      DataItem course = factory.createDataItem();
      course.setDependentVal(Math.toDegrees(thisF.getCourse()));
      course.setIndependentVal(thisT);
      courseData.getMeasurements().add(course);

      DataItem speed = factory.createDataItem();
      speed.setDependentVal(thisF.getSpeed());
      speed.setIndependentVal(thisT);
      speedData.getMeasurements().add(speed);

      DataItem depth = factory.createDataItem();
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

  private Chart createRelativeChartFor(StackedchartsFactory factory,
      WatchableList pri, WatchableList sec, String string)
  {
    final String name = pri.getName() + " vs " + sec.getName();
    final Color color = sec.getColor();

    Chart chart = factory.createChart();
    chart.setName(name);

    // prepare our axes
    DependentAxis bearingAxis = factory.createDependentAxis();
    bearingAxis.setAxisType(factory.createNumberAxis());
    bearingAxis.setName("Bearing");
    chart.getMinAxes().add(bearingAxis);

    DependentAxis relBearingAxis = factory.createDependentAxis();
    relBearingAxis.setName("Rel Bearing");
    relBearingAxis.setAxisType(factory.createNumberAxis());
    chart.getMinAxes().add(relBearingAxis);

    DependentAxis rangeAxis = factory.createDependentAxis();
    rangeAxis.setName("Range");
    rangeAxis.setAxisType(factory.createNumberAxis());
    chart.getMaxAxes().add(rangeAxis);

    // prepare our datasets
    Dataset bearingData = factory.createDataset();
    bearingData.setName("Bearing (\u00b0)");
    PlainStyling bearingStyle = factory.createPlainStyling();
    bearingStyle.setColor(color.darker().darker());
    bearingData.setStyling(bearingStyle);
    bearingStyle.setMarkerStyle(MarkerStyle.DIAMOND);
    bearingAxis.getDatasets().add(bearingData);

    Dataset relBearingData = factory.createDataset();
    relBearingData.setName("Rel Bearing (\u00b0)");
    PlainStyling speedStyle = factory.createPlainStyling();
    speedStyle.setColor(color);
    speedStyle.setMarkerStyle(MarkerStyle.CROSS);
    relBearingData.setStyling(speedStyle);
    relBearingAxis.getDatasets().add(relBearingData);

    Dataset atbData = factory.createDataset();
    atbData.setName("ATB (\u00b0)");
    PlainStyling relStyle = factory.createPlainStyling();
    relStyle.setColor(color.brighter().brighter());
    relStyle.setMarkerStyle(MarkerStyle.CIRCLE);
    atbData.setStyling(relStyle);
    relBearingAxis.getDatasets().add(atbData);

    Dataset rangeData = factory.createDataset();
    rangeData.setName("Range (m)");
    PlainStyling rangeStyle = factory.createPlainStyling();
    rangeStyle.setColor(Color.green);
    rangeStyle.setMarkerStyle(MarkerStyle.NONE);
    rangeData.setStyling(rangeStyle);
    rangeAxis.getDatasets().add(rangeData);

    // get the calculators
    plainCalc range = new rangeCalc();
    plainCalc brg = new bearingCalc();
    plainCalc relB = new relBearingCalc();
    plainCalc atb = new atbCalc();

    // now for the actual data
    final Collection<Editable> priItems =
        pri.getItemsBetween(pri.getStartDTG(), pri.getEndDTG());
    for (Iterator<Editable> iterator = priItems.iterator(); iterator.hasNext();)
    {
      Watchable thisPri = (Watchable) iterator.next();
      HiResDate thisTime = thisPri.getTime();
      final long thisT = thisPri.getTime().getDate().getTime();

      // ok, and the sec?
      Watchable[] thisSec = sec.getNearestTo(thisPri.getTime());
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
}
