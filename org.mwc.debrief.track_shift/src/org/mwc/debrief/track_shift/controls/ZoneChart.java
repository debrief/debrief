package org.mwc.debrief.track_shift.controls;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.Layer;
import org.mwc.cmap.core.CorePlugin;

public class ZoneChart extends Composite
{
  /**
   * helper class to provide color for zones
   * 
   * @author Ian
   * 
   */
  public interface ColorProvider
  {
    java.awt.Color getColorFor(final Zone zone);
  }

  /**
   * helper class to slice data into zones
   * 
   * @author Ian
   * 
   */
  public interface ZoneSlicer
  {
    ArrayList<Zone> performSlicing();
  }

  public enum EditMode
  {
    MOVE, EDIT
  }

  private List<Zone> zones = new ArrayList<Zone>();
  private Map<Zone, IntervalMarker> zoneMarkers =
      new HashMap<ZoneChart.Zone, IntervalMarker>();

  private EditMode mode = EditMode.MOVE;

  private volatile List<ZoneListener> listeners =
      new ArrayList<ZoneChart.ZoneListener>(1);

  private final Image handImg16 = CorePlugin.getImageDescriptor(
      "/icons/16/hand.png").createImage();
  private final Image addImg16 = CorePlugin.getImageDescriptor(
      "/icons/16/add.png").createImage();
  private final Image removeImg16 = CorePlugin.getImageDescriptor(
      "/icons/16/remove.png").createImage();
  private final Image handFistImg16 = CorePlugin.getImageDescriptor(
      "/icons/16/hand_fist.png").createImage();

  /** 24px images for the buttons */
  private final Image handImg24 = CorePlugin.getImageDescriptor(
      "/icons/24/hand.png").createImage();
  private final Image addImg24 = CorePlugin.getImageDescriptor(
      "/icons/24/add.png").createImage();
  private final Image removeImg24 = CorePlugin.getImageDescriptor(
      "/icons/24/remove.png").createImage();
  private final Image fitToWin24 = CorePlugin.getImageDescriptor(
      "/icons/24/fit_to_win.png").createImage();
  private final Image calculator24 = CorePlugin.getImageDescriptor(
      "/icons/24/calculator.png").createImage();

  private final Cursor handCursor = new Cursor(Display.getDefault(), handImg16
      .getImageData(), 0, 0);
  private final Cursor addCursor = new Cursor(Display.getDefault(), addImg16
      .getImageData(), 0, 0);
  private final Cursor removeCursor = new Cursor(Display.getDefault(),
      removeImg16.getImageData(), 0, 0);
  private final Cursor handCursorDrag = new Cursor(Display.getDefault(),
      handFistImg16.getImageData(), 0, 0);
  private final Cursor resizeCursor = new Cursor(Display.getDefault(),
      SWT.CURSOR_SIZEWE);

  private final JFreeChart chart;

  private ChartComposite chartComposite;

  final List<Zone> dragZones = new ArrayList<Zone>();

  // DnD---

  private double dragStartX = -1;
  private boolean onDrag = false;
  private boolean move = false;
  private boolean resizeStart = false;
  private boolean resizeEnd = false;
  private Zone adding = null;
  private long[] timeValues;
  private final ColorProvider colorProvider;
  private final ZoneSlicer zoneSlicer;

  private ZoneChart(Composite parent, JFreeChart xylineChart,
      final Zone[] zones, final long[] timeValues, ColorProvider colorProvider,
      ZoneSlicer zoneSlicer)
  {
    super(parent, SWT.NONE);
    this.chart = xylineChart;
    this.timeValues = timeValues;
    buildUI(xylineChart);
    this.zones.addAll(Arrays.asList(zones));
    this.zoneMarkers.clear();
    xylineChart.setAntiAlias(false);
    this.colorProvider = colorProvider;
    this.zoneSlicer = zoneSlicer;

    XYPlot plot = (XYPlot) xylineChart.getPlot();
    for (Zone zone : zones)
    {
      addZone(plot, zone);
    }
  }

  protected class CustomChartComposite extends ChartComposite
  {
    private CustomChartComposite(final Composite parent, final JFreeChart chart)
    {
      super(parent, SWT.NONE, chart, 400, 600, 300, 100, 1800, 1800, true,
          false, false, false, false, true);
    }

    @Override
    public void mouseDown(MouseEvent event)
    {
      dragZones.clear();
      dragStartX = event.x;// findDomainX(this, event.x);

      switch (mode)
      {
      case MOVE:
      {
        for (Zone zone : zones)
        {
          // find the drag area zones
          if (findPixelX(this, zone.start) <= dragStartX
              && findPixelX(this, zone.end) >= dragStartX)
          {
            dragZones.add(zone);
            resizeStart = isResizeStart(zone, dragStartX);
            resizeEnd = isResizeStart(zone, dragStartX);
            move = !(resizeStart || resizeEnd);

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
      case EDIT:
      {
        for (Zone zone : zones)
        {
          // find the drag area zones

          if (findPixelX(this, zone.start) <= dragStartX
              && findPixelX(this, zone.end) >= dragStartX)
          {
            onDrag = true;
            resizeStart = isResizeStart(zone, dragStartX);
            resizeEnd = isResizeEnd(zone, dragStartX);
            dragZones.add(zone);

            break;
          }
        }

        if (dragZones.isEmpty())
        {
          XYPlot plot = (XYPlot) chart.getPlot();
          adding =
              new Zone((long) findDomainX(this, dragStartX),
                  (long) findDomainX(this, dragStartX + 5));

          addZone(plot, adding);
        }

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

      double currentX = event.x;// findDomainX(this, event.x);
      if (!onDrag)
      {
        switch (mode)
        {
        case MOVE:
        {
          this.setCursor(null);
          for (Zone zone : zones)
          {
            // find the drag area zones
            if (findPixelX(this, zone.start) <= currentX
                && findPixelX(this, zone.end) >= currentX)
            {
              this.setCursor(isResizeStart(zone, currentX)
                  || isResizeEnd(zone, currentX) ? resizeCursor : handCursor);
              break;
            }

          }
          break;
        }
        case EDIT:
        {
          if (adding == null)
          {
            

            this.setCursor(addCursor);
            for (Zone zone : zones)
            {
              // find the drag area zones

              if (findPixelX(this, zone.start) <= currentX
                  && findPixelX(this, zone.end) >= currentX)
              {
                resizeStart = isResizeStart(zone, currentX);
                resizeEnd = isResizeEnd(zone, currentX);
                if (resizeStart || resizeEnd)
                {
                  this.setCursor(resizeCursor);
                }
                else if(isDelete(zone, currentX))
                  this.setCursor(removeCursor);
                else
                {
                  this.setCursor(null);
                }
                break;
              }

            }
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
                z.start =
                    (long) findDomainX(this, findPixelX(this, z.start) + diff);
                z.end =
                    (long) findDomainX(this, findPixelX(this, z.end) + diff);

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
      case EDIT:
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
        else if (resizeStart || resizeEnd)
        {
          double diff = Math.round(currentX - dragStartX);
          if (diff != 0)
          {
            dragStartX = currentX;
            for (Zone z : dragZones)
            {

              resize(z, dragStartX, diff);

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

      default:
        break;
      }

    }

    private boolean isResizeStart(Zone zone, double x)
    {

      long pixelXStart = findPixelX(this, zone.start);
      return (x - pixelXStart) < 5 && (x - pixelXStart) >= -1;
    }
    private boolean isDelete(Zone zone, double x)
    {
      
      long pixelXStart = findPixelX(this, zone.start);
      long pixelXEnd = findPixelX(this, zone.end);
      return ((x - pixelXStart) > 8 && (x - pixelXStart) >= 0) && ((pixelXEnd - x) >8 && (pixelXEnd - x) >= 0);
    }

    private boolean isResizeEnd(Zone zone, double x)
    {
      long pixelXEnd = findPixelX(this, zone.end);
      return (pixelXEnd - x) < 5 && (pixelXEnd - x) >= -1;
    }

    private void resize(Zone zone, double startx, double diff)
    {
      long pixelXStart = findPixelX(this, zone.start);
      long pixelXEnd = findPixelX(this, zone.end);
      if (resizeStart)
      {
        // use start
        if ((pixelXStart + diff) < pixelXEnd)
          zone.start = (long) findDomainX(this, pixelXStart + diff);

      }
      else
      {
        // use end
        if ((pixelXEnd + diff) > pixelXStart)
          zone.end = (long) findDomainX(this, pixelXEnd + diff);
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

      case EDIT:
      {

        if (adding != null)
        {

          zones.add(adding);
          fireZoneAdded(adding);
        }
        
        {
          XYPlot plot = (XYPlot) chart.getPlot();
          for (Zone z : dragZones)
          {
            if(isDelete(z, event.x))
            {
              IntervalMarker intervalMarker = zoneMarkers.get(z);
              plot.removeDomainMarker(intervalMarker);
              zoneMarkers.remove(z);
              zones.remove(z);
              fireZoneRemoved(z);
            }
            else if (isResizeStart(z, event.x)|| isResizeEnd(z, event.x))
            { 
              fireZoneResized(z);
              
            }
          }
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
      resizeStart = false;
      resizeEnd = false;
      super.mouseUp(event);
    }

  }

  void buildUI(JFreeChart xylineChart)
  {
    setLayout((new GridLayout(2, false)));

    chartComposite = new CustomChartComposite(this, xylineChart);

    chartComposite.setDomainZoomable(true);
    chartComposite.setRangeZoomable(true);

    GridData data =
        new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
            | GridData.GRAB_VERTICAL);
    data.verticalSpan = 5;
    chartComposite.setLayoutData(data);
    createToolbar();
  }

  protected void createToolbar()
  {
    {// mode buttons
      final Button add = new Button(this, SWT.TOGGLE);
      add.setImage(addImg24);// TODO ADD EDIT ICON
      add.setToolTipText("Add new zones/Remove zones");
      add.setLayoutData(new GridData(GridData.FILL_VERTICAL));

      final Button move = new Button(this, SWT.TOGGLE);
      move.setImage(handImg24);
      move.setLayoutData(new GridData(GridData.FILL_VERTICAL));
      move.setSelection(true);
      move.setToolTipText("Resize zones");

      final Button fitToWin = new Button(this, SWT.PUSH);
      fitToWin.setImage(fitToWin24);
      fitToWin.setLayoutData(new GridData(GridData.FILL_VERTICAL));
      fitToWin.setToolTipText("Show all data");

      final Button calculate = new Button(this, SWT.PUSH);
      calculate.setImage(calculator24);
      calculate.setLayoutData(new GridData(GridData.FILL_VERTICAL));
      calculate.setToolTipText("Slice legs");

      add.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          add.setSelection(true);

          move.setSelection(false);
          setMode(ZoneChart.EditMode.EDIT);
        }
      });
      move.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          add.setSelection(false);

          move.setSelection(true);
          setMode(ZoneChart.EditMode.MOVE);
        }
      });

      fitToWin.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          XYPlot plot = (XYPlot) chart.getPlot();

          // NOTE: for some reason, we have to do the domain before the
          // range to get the full resize
          plot.getDomainAxis().setAutoRange(true);
          plot.getRangeAxis().setAutoRange(true);
        }
      });
      calculate.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          if (zoneSlicer == null)
          {
            CorePlugin.showMessage("Manage legs", "Slicing happens here");
          }
          else
          {
            // ok, do the slicing
            List<Zone> newZones = zoneSlicer.performSlicing();

            final XYPlot thePlot = (XYPlot) chart.getPlot();

            // and ditch the intervals
            for (Zone thisZone : zones)
            {
              // remove this marker
              IntervalMarker thisM = zoneMarkers.get(thisZone);
              thePlot.removeDomainMarker(thisM, Layer.FOREGROUND);
            }

            // ok, now ditch the old zone lists
            zones.clear();
            zoneMarkers.clear();

            // store the zones
            zones.addAll(newZones);

            // and create the new intervals
            for (Zone thisZone : newZones)
            {
              addZone(thePlot, thisZone);
            }
          }
        }
      });
    }
  }

  private void addZone(XYPlot plot, Zone zone)
  {
    // get the color for this zone
    Color zoneColor = colorProvider.getColorFor(zone);
    zone.setColor(zoneColor);

    IntervalMarker mrk = new IntervalMarker(zone.start, zone.end);
    mrk.setPaint(zone.getColor());
    mrk.setAlpha(0.5f);
    plot.addDomainMarker(mrk, Layer.FOREGROUND);
    zoneMarkers.put(zone, mrk);
  }

  public static ZoneChart create(String chartTitle, String yTitle,
      Composite parent, final Zone[] zones, long[] timeValues,
      long[] angleValues, ColorProvider blueProv, Color lineColor,
      ZoneSlicer zoneSlicer)
  {
    // build the jfreechart Plot
    final TimeSeries xySeries = new TimeSeries("");

    for (int i = 0; i < timeValues.length; i++)
    {
      xySeries.add(new FixedMillisecond(timeValues[i]), angleValues[i]);
    }

    return create(chartTitle, yTitle, parent, zones, xySeries, timeValues,
        blueProv, lineColor, zoneSlicer);
  }

  public static ZoneChart create(String chartTitle, String yTitle,
      Composite parent, final Zone[] zones, TimeSeries xySeries,
      long[] timeValues, ColorProvider blueProv, Color lineColor,
      ZoneSlicer zoneSlicer)
  {

    final TimeSeriesCollection dataset = new TimeSeriesCollection();
    dataset.addSeries(xySeries);

    JFreeChart xylineChart = ChartFactory.createTimeSeriesChart(chartTitle, // String
        "Time", // String timeAxisLabel
        yTitle, // String valueAxisLabel,
        dataset, false, true, false);

    final XYPlot plot = (XYPlot) xylineChart.getPlot();
    DateAxis xAxis = new DateAxis();
    plot.setDomainAxis(xAxis);

    plot.setBackgroundPaint(MWC.GUI.Properties.DebriefColors.WHITE);
    plot.setRangeGridlinePaint(MWC.GUI.Properties.DebriefColors.LIGHT_GRAY);
    plot.setDomainGridlinePaint(MWC.GUI.Properties.DebriefColors.LIGHT_GRAY);

    // and sort out the color for the line
    XYLineAndShapeRenderer renderer =
        (XYLineAndShapeRenderer) plot.getRenderer();
    Shape square = new Rectangle2D.Double(-2.0, -2.0, 3.0, 3.0);
    renderer.setSeriesPaint(0, lineColor);
    renderer.setSeriesShape(0, square);
    renderer.setSeriesShapesVisible(0, true);

    // ok, wrap it in the zone chart
    ZoneChart zoneChart =
        new ZoneChart(parent, xylineChart, zones, timeValues, blueProv,
            zoneSlicer);

    // done
    return zoneChart;
  }

  // ---

  @Override
  public void dispose()
  {
    handCursor.dispose();
    handCursorDrag.dispose();
    resizeCursor.dispose();
    handImg16.dispose();
    handFistImg16.dispose();
    addCursor.dispose();
    addImg16.dispose();
    removeImg16.dispose();
    removeCursor.dispose();

    // and the 24px images
    handImg24.dispose();
    addImg24.dispose();
    removeImg24.dispose();
    fitToWin24.dispose();
    calculator24.dispose();

    super.dispose();
  }

  private double findDomainX(ChartComposite composite, double x)
  {
    final Rectangle dataArea = composite.getScreenDataArea();
    final Rectangle2D d2 =
        new Rectangle2D.Double(dataArea.x, dataArea.y, dataArea.width,
            dataArea.height);
    final XYPlot plot = (XYPlot) composite.getChart().getPlot();
    final double chartX =
        plot.getDomainAxis().java2DToValue(x, d2, plot.getDomainAxisEdge());

    return Math.ceil(chartX);
  }

  private long findPixelX(ChartComposite composite, double x)
  {
    final Rectangle dataArea = composite.getScreenDataArea();
    final Rectangle2D d2 =
        new Rectangle2D.Double(dataArea.x, dataArea.y, dataArea.width,
            dataArea.height);
    final XYPlot plot = (XYPlot) composite.getChart().getPlot();
    final double chartX =
        plot.getDomainAxis().valueToJava2D(x, d2, plot.getDomainAxisEdge());

    return (long) Math.ceil(chartX);
  }

  @SuppressWarnings("unused")
  private long toNearDomainValue(double x)
  {

    long distance = Math.abs(timeValues[0] - (long) x);
    int idx = 0;
    for (int c = 1; c < timeValues.length; c++)
    {
      long cdistance = Math.abs(timeValues[c] - (long) x);
      if (cdistance < distance)
      {
        idx = c;
        distance = cdistance;
      }
    }
    return timeValues[idx];
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
    long start, end;
    private Color color;

    public Zone(long start, long end)
    {
      this.start = start;
      this.end = end;
    }

    public Color getColor()
    {
      return color;
    }

    public void setColor(Color zoneColor)
    {
      this.color = zoneColor;
    }

    public long getStart()
    {
      return start;
    }

    public long getEnd()
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
