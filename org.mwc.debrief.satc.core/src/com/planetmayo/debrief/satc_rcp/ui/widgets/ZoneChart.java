package com.planetmayo.debrief.satc_rcp.ui.widgets;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
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

import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class ZoneChart extends ChartComposite
{

  public enum EditMode
  {
    MOVE, ADD, REMOVE
  }

  private static final double OFFSET_RESIZE = 0.5;
  private List<Zone> zones = new ArrayList<Zone>();
  private Map<Zone, IntervalMarker> zoneMarkers =
      new HashMap<ZoneChart.Zone, IntervalMarker>();

  private EditMode mode = EditMode.MOVE;

  private volatile List<ZoneListener> listeners =
      new ArrayList<ZoneChart.ZoneListener>(1);

  private final Image handImg = SATC_Activator.getImageDescriptor(
      "/icons/hand.png").createImage();
  private final Image addImg = SATC_Activator.getImageDescriptor(
      "/icons/add.png").createImage();
  private final Image removeImg = SATC_Activator.getImageDescriptor(
      "/icons/remove.png").createImage();

  private final Image handFistImg = SATC_Activator.getImageDescriptor(
      "/icons/hand_fist.png").createImage();

  private final Cursor handCursor = new Cursor(Display.getDefault(), handImg
      .getImageData(), 0, 0);
  private final Cursor addCursor = new Cursor(Display.getDefault(), addImg
      .getImageData(), 0, 0);
  private final Cursor removeCursor = new Cursor(Display.getDefault(),
      removeImg.getImageData(), 0, 0);
  private final Cursor handCursorDrag = new Cursor(Display.getDefault(),
      handFistImg.getImageData(), 0, 0);
  private final Cursor resizeCursor = new Cursor(Display.getDefault(),
      SWT.CURSOR_SIZEWE);

  private final JFreeChart chart;

  private ZoneChart(Composite parent, JFreeChart xylineChart, final Zone[] zones)
  {
    super(parent, SWT.NONE, xylineChart, 400, 600, 300, 200, 1800, 1800, true,
        false, false, false, false, true);
    this.chart = xylineChart;
    this.zones.addAll(Arrays.asList(zones));
    this.zoneMarkers.clear();
    xylineChart.setAntiAlias(false);

    setDomainZoomable(false);
    setRangeZoomable(false);

    XYPlot plot = (XYPlot) xylineChart.getPlot();
    for (Zone zone : zones)
    {
      addZone(plot, zone);

    }
  }

  private void addZone(XYPlot plot, Zone zone)
  {
    IntervalMarker mrk = new IntervalMarker(zone.start, zone.end);
    plot.addDomainMarker(mrk, Layer.FOREGROUND);
    zoneMarkers.put(zone, mrk);
  }

  public static ZoneChart create(Composite parent, final Zone[] zones,
      long[] timeValues, long[] angleValues)
  {

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

    ZoneChart zoneChart = new ZoneChart(parent, xylineChart, zones);

    return zoneChart;

  }

  // DnD---

  double dragStartX = -1;
  boolean onDrag = false;
  boolean move = false;
  boolean resizeStart = true;
  Zone adding = null;

  List<Zone> dragZones = new ArrayList<Zone>();

  @Override
  public void mouseDown(MouseEvent event)
  {
    dragZones.clear();
    dragStartX = findDomainX(this, event.x);

    switch (mode)
    {
    case MOVE:
    {
      for (Zone zone : zones)
      {
        // find the drag area zones

        if (zone.start <= dragStartX && zone.end >= dragStartX)
        {
          dragZones.add(zone);
          resizeStart = isResizeStart(zone, dragStartX);
          move = !(resizeStart || isResizeEnd(zone, dragStartX));

          onDrag = true;
          if (move)
          {
            setCursor(handCursorDrag);
          }
          break;
        }
      }
      break;
    }
    case REMOVE:
    {
      for (Zone zone : zones)
      {
        // find the drag area zones

        if (zone.start <= dragStartX && zone.end >= dragStartX)
        {
          dragZones.add(zone);

          break;
        }
      }
      break;
    }
    case ADD:
    {
      for (Zone zone : zones)
      {
        // find the drag area zones

        if (zone.start <= dragStartX && zone.end >= dragStartX)
        {
          return;
        }
      }
      adding = new Zone((int) dragStartX, (int) dragStartX);
      XYPlot plot = (XYPlot) chart.getPlot();
      addZone(plot, adding);
      break;
    }

    default:
      break;
    }

    if (dragZones.isEmpty())
      super.mouseDown(event);

  }

  @Override
  public void mouseMove(MouseEvent event)
  {

    double currentX = findDomainX(this, event.x);
    if (!onDrag)
    {
      switch (mode)
      {
      case MOVE:
      {

        for (Zone zone : zones)
        {
          // find the drag area zones

          if (zone.start <= currentX && zone.end >= currentX)
          {
            this.setCursor(isResizeStart(zone, currentX)
                || isResizeEnd(zone, currentX) ? resizeCursor : handCursor);
            break;
          }
          this.setCursor(null);
        }
        break;
      }
      case REMOVE:
      {

        for (Zone zone : zones)
        {
          // find the drag area zones

          if (zone.start <= currentX && zone.end >= currentX)
          {
            this.setCursor(removeCursor);
            break;
          }
          this.setCursor(null);
        }
        break;
      }
      case ADD:
      {

        if (adding == null)
          for (Zone zone : zones)
          {
            // find the drag area zones

            if (zone.start <= currentX && zone.end >= currentX)
            {
              this.setCursor(null);
              break;
            }
            this.setCursor(addCursor);
          }
        break;
      }

      }
    }

    switch (mode)
    {
    case MOVE:
    {

      if (onDrag && !dragZones.isEmpty() && dragStartX > 0)
      {

        if (move)
        {
          setCursor(handCursorDrag);
        }

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

      break;
    }
    case ADD:
    {

      if (adding != null && dragStartX > 0)
      {

        double diff = Math.round(currentX - dragStartX);
        if (diff != 0)
        {
          dragStartX = currentX;

          resizeStart = false;
          {
            resize(adding, dragStartX, diff);
          }
          IntervalMarker intervalMarker = zoneMarkers.get(adding);
          assert intervalMarker != null;
          intervalMarker.setStartValue(adding.start);
          intervalMarker.setEndValue(adding.end);

        }

      }

      else
        super.mouseMove(event);

      break;
    }

    default:
      break;
    }

  }

  private boolean isResizeStart(Zone zone, double x)
  {
    return (x - zone.start) < OFFSET_RESIZE;
  }

  private boolean isResizeEnd(Zone zone, double x)
  {
    return (zone.end - x) < OFFSET_RESIZE;
  }

  private void resize(Zone zone, double startx, double diff)
  {
    if (resizeStart)
    {
      // use start
      if ((zone.start + diff) < zone.end)
        zone.start += diff;

    }
    else
    {
      // use end
      if ((zone.end + diff) > zone.start)
        zone.end += diff;
    }
  }

  @Override
  public void mouseUp(MouseEvent event)
  {

    switch (mode)
    {
    case MOVE:
    {
      if (onDrag)
      {
        for (Zone z : dragZones)
        {
          if (move)
          {
            fireZoneMoved(z);
          }
          else
          {
            fireZoneResized(z);
          }
        }

      }

      break;

    }

    case REMOVE:
    {
      XYPlot plot = (XYPlot) chart.getPlot();
      for (Zone z : dragZones)
      {
        IntervalMarker intervalMarker = zoneMarkers.get(z);
        plot.removeDomainMarker(intervalMarker);
        zoneMarkers.remove(z);
        zones.remove(z);
        fireZoneRemoved(z);
      }

      break;

    }
    case ADD:
    {

      if (adding != null)
      {

        zones.add(adding);
        fireZoneAdded(adding);
      }

      break;

    }

    default:
      break;
    }

    dragStartX = -1;
    dragZones.clear();
    onDrag = false;
    move = false;
    adding = null;
    super.mouseUp(event);
  }

  // ---

  @Override
  public void dispose()
  {
    handCursor.dispose();
    handCursorDrag.dispose();
    resizeCursor.dispose();
    handImg.dispose();
    handFistImg.dispose();
    addCursor.dispose();
    addImg.dispose();
    removeImg.dispose();
    removeCursor.dispose();

    super.dispose();
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

  public EditMode getMode()
  {
    return mode;
  }

  public void setMode(EditMode mode)
  {
    this.mode = mode;
  }

  public Zone[] getZones()
  {
    return zones.toArray(new Zone[zones.size()]);
  }

  public void addZoneListener(ZoneListener listener)
  {
    listeners.add(listener);
  }

  public void removeZoneListener(ZoneListener listener)
  {
    listeners.remove(listener);
  }

  public List<ZoneListener> getZoneListeners()
  {
    return new ArrayList<ZoneListener>(listeners);
  }

  void fireZoneMoved(Zone zone)
  {
    for (ZoneListener listener : getZoneListeners())
    {
      listener.moved(zone);
    }
  }

  void fireZoneResized(Zone zone)
  {
    for (ZoneListener listener : getZoneListeners())
    {
      listener.resized(zone);
    }
  }

  void fireZoneAdded(Zone zone)
  {
    for (ZoneListener listener : getZoneListeners())
    {
      listener.added(zone);
    }
  }

  void fireZoneRemoved(Zone zone)
  {
    for (ZoneListener listener : getZoneListeners())
    {
      listener.deleted(zone);
    }
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

  public static interface ZoneListener
  {
    void deleted(Zone zone);

    void added(Zone zone);

    void moved(Zone zone);

    void resized(Zone zone);
  }

  public static class ZoneAdapter implements ZoneListener
  {

    @Override
    public void deleted(Zone zone)
    {

    }

    @Override
    public void added(Zone zone)
    {

    }

    @Override
    public void moved(Zone zone)
    {

    }

    @Override
    public void resized(Zone zone)
    {

    }

  }

}
