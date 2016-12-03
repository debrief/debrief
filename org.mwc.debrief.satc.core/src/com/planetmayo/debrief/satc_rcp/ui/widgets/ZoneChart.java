package com.planetmayo.debrief.satc_rcp.ui.widgets;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.Layer;

public class ZoneChart
{
  private static final double OFFSET_RESIZE  = 0.5;
  private Zone[] zones = new Zone[0];
  private Map<Zone, IntervalMarker> zoneMarkers =
      new HashMap<ZoneChart.Zone, IntervalMarker>();

  private long[] timeValues = new long[0];
  private long[] angleValues = new long[0];

  public ChartComposite create(Composite parent, final Zone[] zones,
      long[] timeValues, long[] angleValues)
  {
    this.zones = zones;
    this.zoneMarkers.clear();
    this.timeValues = timeValues;
    this.angleValues = angleValues;

    // build the jfreechart Plot
    final XYSeries xySeries = new XYSeries("");

    for (int i = 0; i < timeValues.length; i++)
    {
      xySeries.add(timeValues[i], angleValues[i]);
    }

    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(xySeries);

    JFreeChart xylineChart =
        ChartFactory.createXYLineChart("", "Time", "Angle", dataset,
            PlotOrientation.VERTICAL, true, true, false);

    final XYPlot plot = (XYPlot) xylineChart.getPlot();
    NumberAxis xAxis = new NumberAxis();
    xAxis.setTickUnit(new NumberTickUnit(1));
    plot.setDomainAxis(xAxis);
    for (Zone zone : zones)
    {
      IntervalMarker mrk = new IntervalMarker(zone.start, zone.end);
      plot.addDomainMarker(mrk, Layer.FOREGROUND);
      zoneMarkers.put(zone, mrk);

    }

    ChartComposite chartComposite = createChartUI(parent, zones, xylineChart);

    return chartComposite;

  }

  private ChartComposite createChartUI(Composite parent, final Zone[] zones,
      JFreeChart xylineChart)
  {

    final Cursor handCursor = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);
    final Cursor resizeCursor =
        new Cursor(Display.getDefault(), SWT.CURSOR_SIZEWE);

    final ChartComposite chartComposite =
        new ChartComposite(parent, SWT.NONE, xylineChart, 400, 600, 300, 200,
            1800, 1800, true, false, true, true, true, true)
        {
          double dragStartX = -1;
          boolean onDrag = false;
          boolean move = false;
          boolean resizeStart = true;

          List<Zone> dragZones = new ArrayList<Zone>();

          @Override
          public void mouseDown(MouseEvent event)
          {
            dragZones.clear();
            dragStartX = findDomainX(this, event.x);
            for (Zone zone : zones)
            {
              // find the drag area zones

              if (zone.start <= dragStartX && zone.end >= dragStartX)
              {
                dragZones.add(zone);
                resizeStart = isResizeStart(zone, dragStartX);
                move = !(resizeStart|| isResizeEnd(zone, dragStartX));
                
                  
                onDrag = true;
                break;
              }
            }

            if (dragZones.isEmpty())
              super.mouseDown(event);

          }

          @Override
          public void mouseMove(MouseEvent event)
          {

            double currentX = findDomainX(this, event.x);
            if (!onDrag)
              for (Zone zone : zones)
              {
                // find the drag area zones

                if (zone.start <= currentX && zone.end >= currentX)
                {
                  this.setCursor(isResizeStart(zone, currentX)||isResizeEnd(zone, currentX) ? resizeCursor
                      : handCursor);
                  break;
                }
                this.setCursor(null);
              }

            if (onDrag && !dragZones.isEmpty() && dragStartX > 0)
            {

              double diff = Math.round(currentX - dragStartX);
              if (diff != 0)
              {
                dragStartX = currentX;
                for (Zone z : dragZones)
                {
                  if (move)
                  {
                    z.start += diff;
                    z.end += diff;

                    
                  }
                  else
                  {
                    resize(z, dragStartX, diff);
                  }
                  IntervalMarker intervalMarker = zoneMarkers.get(z);
                  assert intervalMarker != null;
                  intervalMarker.setStartValue(z.start);
                  intervalMarker.setEndValue(z.end);
                }

              }

            }

            else
              super.mouseMove(event);
          }

          private boolean isResizeStart(Zone zone, double x)
          {
            return (x - zone.start) < OFFSET_RESIZE ;
          }
          private boolean isResizeEnd(Zone zone, double x)
          {
            return (zone.end - x) < OFFSET_RESIZE;
          }
          private void resize(Zone zone, double startx,double diff)
          {
            if(resizeStart)
            {
              //use start 
              if((zone.start+diff)<zone.end)
                zone.start += diff;
              
            }
            else
            {
              //use end
              if((zone.end+diff)>zone.start)
                zone.end += diff;
            }
          }

          @Override
          public void mouseUp(MouseEvent event)
          {
            dragStartX = -1;
            dragZones.clear();
            onDrag = false;
            super.mouseUp(event);
          }

        };

    xylineChart.setAntiAlias(false);

    chartComposite.setDomainZoomable(false);
    chartComposite.setRangeZoomable(false);

    chartComposite.addDisposeListener(new DisposeListener()
    {

      @Override
      public void widgetDisposed(DisposeEvent e)
      {
        handCursor.dispose();
        resizeCursor.dispose();
      }
    });
    return chartComposite;
  }

  private double findDomainX(ChartComposite composite, int x)
  {
    final Rectangle dataArea = composite.getScreenDataArea();
    final Rectangle2D d2 =
        new Rectangle2D.Double(dataArea.x, dataArea.y, dataArea.width,
            dataArea.height);
    final XYPlot plot = (XYPlot) composite.getChart().getPlot();
    final double chartX =
        plot.getDomainAxis().java2DToValue(x, d2, plot.getDomainAxisEdge());
    return chartX;
  }

  public Zone[] getZones()
  {
    return zones;
  }

  public static class Zone
  {
    int start, end;

    public Zone(int start, int end)
    {
      this.start = start;
      this.end = end;
    }

    public int getStart()
    {
      return start;
    }

    public int getEnd()
    {
      return end;
    }

    @Override
    public String toString()
    {
      return "Zone [start=" + start + ", end=" + end + "]";
    }

  }
}
