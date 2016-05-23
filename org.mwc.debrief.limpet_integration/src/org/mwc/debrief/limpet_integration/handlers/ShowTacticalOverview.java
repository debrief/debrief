package org.mwc.debrief.limpet_integration.handlers;

import info.limpet.stackedcharts.model.AxisType;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.MarkerStyle;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.StackedChartsView;

import java.awt.Color;
import java.util.Collection;
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

import MWC.GUI.Editable;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ShowTacticalOverview extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public ShowTacticalOverview() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
	  
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
	  TrackDataProvider data = (TrackDataProvider) editor.getAdapter(TrackDataProvider.class);

	  // just check we've got it
	  if(data == null)
	  {
	    CorePlugin.logError(Status.INFO, "Failed to open Tactical Overview,  data not present", null);
	    return null;
	  }
	  
	  // do we have primary?
	  WatchableList pri = data.getPrimaryTrack();

	  if(pri == null)
	  {
	    CorePlugin.showMessage("Tactical Overview", "A primary track must be assigned on the Track Tote");
      CorePlugin.logError(Status.INFO, "Failed to open Tactical Overview,  primary track not present", null);
      return null;
	  }
	  
	  // do we have secondaries?
	  WatchableList[] secs = data.getSecondaryTracks();
	  
	  // produce the ChartSet
	  StackedchartsFactory factory = StackedchartsFactoryImpl.init();
	  ChartSet charts = factory.createChartSet();
	  charts.setOrientation(Orientation.VERTICAL);
	  
	  // sort out the time axis
	  IndependentAxis ia = factory.createIndependentAxis();
	  ia.setAxisType(AxisType.TIME);
	  ia.setName("Time");
	  charts.setSharedAxis(ia);
	  
	  // produce the relative chart
	  if(secs != null)
	  {
//	    // ok, we have some secondaries. how many?
//	    if(secs.length == 1)
//	    {
//	      // ok, single secondary - we just need on relative plot
//	      Chart thisChart = createRelativeChartFor(factory, pri, secs[0], "Relative State");
//	      charts.getCharts().add(thisChart);
//	    }
//	    else
//	    {
//	      // multiple secondaries - we need multiple relative plots
//	      
//	      // loop through them
//	      for (int i = 0; i < secs.length; i++)
//        {
//          // ok, single secondary - we just need on relative plot
//          Chart thisChart = createRelativeChartFor(factory, pri, secs[i], secs[i].getName() + " vs " + pri.getName());
//          charts.getCharts().add(thisChart);
//        }
//	    }
	  }
	  
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
	  
	  // produce the sensor coverage
	  
	  // create a new instance of the Tactical Overview
    
    String ID = StackedChartsView.ID;
    
    // TODO: produce unique name?
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

  private Chart createStateChartFor(StackedchartsFactory factory,
      WatchableList track)
  {
    final Collection<Editable> items = track.getItemsBetween(track.getStartDTG(), track.getEndDTG());
    final String name = track.getName();
    final Color color = track.getColor();
    
    Chart chart = factory.createChart();
    chart.setName(name + " = State");
    
    // prepare our axes
    DependentAxis courseAxis = factory.createDependentAxis();
    courseAxis.setName("Course");
    chart.getMaxAxes().add(courseAxis);

    DependentAxis speedAxis = factory.createDependentAxis();
    speedAxis.setName("Speed");
    chart.getMinAxes().add(speedAxis);

    DependentAxis depthAxis = factory.createDependentAxis();
    depthAxis.setName("Depth");
    chart.getMinAxes().add(depthAxis);

    // prepare our datasets
    Dataset courseData = factory.createDataset();
    courseData.setName("Course");
    PlainStyling courseStyle = factory.createPlainStyling();
    courseStyle.setColor(getHTMLColorString(color.brighter()));
    courseData.setStyling(courseStyle);
    courseStyle.setMarkerStyle(MarkerStyle.CIRCLE);
    courseAxis.getDatasets().add(courseData);
    
    Dataset speedData = factory.createDataset();
    speedData.setName("Speed (kts)");
    PlainStyling speedStyle = factory.createPlainStyling();
    speedStyle.setColor(getHTMLColorString(color.darker()));
    speedStyle.setMarkerStyle(MarkerStyle.CROSS);
    speedData.setStyling(speedStyle);
    speedAxis.getDatasets().add(speedData);

    Dataset depthData = factory.createDataset();
    depthData.setName("Depth (m)");
    PlainStyling depthStyle = factory.createPlainStyling();
    depthStyle.setColor(getHTMLColorString(color));
    depthStyle.setMarkerStyle(MarkerStyle.NONE);
    depthData.setStyling(depthStyle);
    speedAxis.getDatasets().add(depthData);    
    
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
      depth.setDependentVal(thisF.getDepth());
      depth.setIndependentVal(thisT);
      depthData.getMeasurements().add(depth);
    }
    
    return chart;
  }

  private Chart createRelativeChartFor(StackedchartsFactory factory, WatchableList pri,
      WatchableList sec, String string)
  {
    return factory.createChart();
  }
  
  private static String getHTMLColorString(Color color) {
    String red = Integer.toHexString(color.getRed());
    String green = Integer.toHexString(color.getGreen());
    String blue = Integer.toHexString(color.getBlue());

    return "#" + 
            (red.length() == 1? "0" + red : red) +
            (green.length() == 1? "0" + green : green) +
            (blue.length() == 1? "0" + blue : blue);        
}
}
