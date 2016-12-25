package org.mwc.debrief.track_shift.controls;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.mwc.cmap.core.CorePlugin;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

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

  protected class CustomChartComposite extends ChartComposite
  {
    private CustomChartComposite(final Composite parent, final JFreeChart chart)
    {
      super(parent, SWT.NONE, chart, 400, 600, 300, 100, 1800, 1800, true,
          false, false, false, false, true);
    }

    private boolean isDelete(final Zone zone, final double x)
    {

      final long pixelXStart = findPixelX(this, zone.start);
      final long pixelXEnd = findPixelX(this, zone.end);
      return ((x - pixelXStart) > 8 && (x - pixelXStart) >= 0)
          && ((pixelXEnd - x) > 8 && (pixelXEnd - x) >= 0);
    }

    private boolean isResizeEnd(final Zone zone, final double x)
    {
      final long pixelXEnd = findPixelX(this, zone.end);
      return (pixelXEnd - x) < 5 && (pixelXEnd - x) >= -1;
    }

    private boolean isResizeStart(final Zone zone, final double x)
    {

      final long pixelXStart = findPixelX(this, zone.start);
      return (x - pixelXStart) < 5 && (x - pixelXStart) >= -1;
    }

    @Override
    public void mouseDown(final MouseEvent event)
    {
      if (mode == EditMode.ZOOM)
      {

        super.mouseDown(event);
        return;
      }

      dragZone = null;
      dragZoneStartBefore = -1L;
      dragZoneEndBefore = -1L;

      dragStartX = event.x;// findDomainX(this, event.x);

      switch (mode)
      {

      case MERGE:
      {
        this.setCursor(null);
        for (final Zone zone : zones)
        {
          // find the drag area zones
          if (findPixelX(this, zone.start) <= dragStartX
              && findPixelX(this, zone.end) >= dragStartX)
          {
            if (merge_1 == null)
            {
              merge_1 = zone;
              break;
            }
            else if (merge_1 != zone)
            {
              merge_2 = zone;
              break;
            }

          }

        }
        break;
      }

      case EDIT:
      {
        for (final Zone zone : zones)
        {
          // find the drag area zones

          if (findPixelX(this, zone.start) <= dragStartX
              && findPixelX(this, zone.end) >= dragStartX)
          {

            resizeStart = isResizeStart(zone, dragStartX);
            resizeEnd = isResizeEnd(zone, dragStartX);
            dragZone = zone;
            dragZoneStartBefore = zone.start;
            dragZoneEndBefore = zone.end;
            onDrag = resizeStart || resizeEnd;
            break;
          }
        }

        if (dragZone == null)
        {
          final XYPlot plot = (XYPlot) chart.getPlot();
          final long val1 =
              toNearDomainValue(findDomainX(this, dragStartX), false);
          final long val2 = toNearDomainValue(val1, true);
          adding =
              new Zone(val1 > val2 ? val2 : val1, val1 > val2 ? val1 : val2);

          addZone(plot, adding);
        }

        break;
      }

      default:
        break;
      }

      if (dragZone == null)
        super.mouseDown(event);

    }

    @Override
    public void mouseMove(final MouseEvent event)
    {
      if (mode == EditMode.ZOOM)
      {

        super.mouseMove(event);
        this.setCursor(null);
        return;
      }
      final double currentX = event.x;// findDomainX(this, event.x);
      if (!onDrag)
      {
        switch (mode)
        {

        case MERGE:
        {
          this.setCursor(null);
          for (final Zone zone : zones)
          {
            // find the drag area zones
            if (findPixelX(this, zone.start) <= currentX
                && findPixelX(this, zone.end) >= currentX)
            {
              setCursor(merge_1 == null || merge_1 == zone ? merge_1Cursor
                  : merge_2Cursor);
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
            for (final Zone zone : zones)
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
                else if (isDelete(zone, currentX))
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
        case ZOOM:
          break;
        default:
          break;

        }
      }

      switch (mode)
      {

      case EDIT:
      {

        if (adding != null && dragStartX > 0)
        {

          {

            resizeStart = false;
            {
              resize(adding, currentX);
            }
            final IntervalMarker intervalMarker = zoneMarkers.get(adding);
            assert intervalMarker != null;
            intervalMarker.setStartValue(adding.start);
            intervalMarker.setEndValue(adding.end);

          }

        }
        else if (resizeStart || resizeEnd)
        {

          {
            if (dragZone != null)
            {

              resize(dragZone, currentX);
              final IntervalMarker intervalMarker = zoneMarkers.get(dragZone);
              assert intervalMarker != null;
              intervalMarker.setStartValue(dragZone.start);
              intervalMarker.setEndValue(dragZone.end);
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

    @Override
    public void mouseUp(final MouseEvent event)
    {
      if (mode == EditMode.ZOOM)
      {

        super.mouseUp(event);
        return;
      }
      switch (mode)
      {

      case EDIT:
      {

        if (adding != null)
        {
          final XYPlot plot = (XYPlot) chart.getPlot();
          final Zone affect = adding;
          final IntervalMarker intervalMarker = zoneMarkers.get(affect);

          AbstractOperation addOp = new AbstractOperation("Delete Zone")
          {

            @Override
            public IStatus execute(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException
            {
              zones.add(affect);
              fireZoneAdded(affect);
              return Status.OK_STATUS;
            }

            @Override
            public IStatus redo(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException
            {
              plot.addDomainMarker(intervalMarker);
              zoneMarkers.put(affect, intervalMarker);
              zones.add(affect);
              fireZoneAdded(affect);
              return Status.OK_STATUS;
            }

            @Override
            public IStatus undo(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException
            {
              plot.removeDomainMarker(intervalMarker);
              zoneMarkers.remove(affect);
              zones.remove(affect);
              fireZoneRemoved(affect);
              return Status.OK_STATUS;
            }

          };
          undoRedoProvider.execute(addOp);

        }
        else
        {
          final XYPlot plot = (XYPlot) chart.getPlot();
          if (dragZone != null)
          {
            if (!onDrag && isDelete(dragZone, event.x))
            {
              final IntervalMarker intervalMarker = zoneMarkers.get(dragZone);

              AbstractOperation deleteOp = new AbstractOperation("Delete Zone")
              {

                @Override
                public IStatus undo(IProgressMonitor monitor, IAdaptable info)
                    throws ExecutionException
                {
                  plot.addDomainMarker(intervalMarker);
                  zoneMarkers.put(dragZone, intervalMarker);
                  zones.add(dragZone);
                  fireZoneAdded(dragZone);
                  return Status.OK_STATUS;
                }

                @Override
                public IStatus redo(IProgressMonitor monitor, IAdaptable info)
                    throws ExecutionException
                {
                  plot.removeDomainMarker(intervalMarker);
                  zoneMarkers.remove(dragZone);
                  zones.remove(dragZone);
                  fireZoneRemoved(dragZone);
                  return Status.OK_STATUS;
                }

                @Override
                public IStatus
                    execute(IProgressMonitor monitor, IAdaptable info)
                        throws ExecutionException
                {
                  return redo(monitor, info);
                }
              };

              undoRedoProvider.execute(deleteOp);

            }
            else if (isResizeStart(dragZone, event.x)
                || isResizeEnd(dragZone, event.x))
            {


              final Zone affect = dragZone;
              final long startBefore = dragZoneStartBefore;
              final long endBefore = dragZoneEndBefore;

              final long startAfter = dragZone.start;
              final long endAfter = dragZone.end;
              AbstractOperation resizeOp = new AbstractOperation("Resize Zone")
              {

                @Override
                public IStatus
                    execute(IProgressMonitor monitor, IAdaptable info)
                        throws ExecutionException
                {
                  fireZoneResized(affect);
                  return Status.OK_STATUS;
                }

                @Override
                public IStatus redo(IProgressMonitor monitor, IAdaptable info)
                    throws ExecutionException
                {
                  final IntervalMarker intervalMarker = zoneMarkers.get(affect);
                  affect.start = startAfter;
                  affect.end = endAfter;
                  
                  intervalMarker.setStartValue(affect.start);
                  intervalMarker.setEndValue(affect.end);
                  fireZoneResized(affect);
                  return Status.OK_STATUS;
                }

                @Override
                public IStatus undo(IProgressMonitor monitor, IAdaptable info)
                    throws ExecutionException
                {
                  final IntervalMarker intervalMarker = zoneMarkers.get(affect);
                  affect.start = startBefore;
                  affect.end = endBefore;
                  
                  intervalMarker.setStartValue(affect.start);
                  intervalMarker.setEndValue(affect.end);
                  fireZoneResized(affect);
                  return Status.OK_STATUS;
                }

              };
              undoRedoProvider.execute(resizeOp);

            }
          }
        }

        break;

      }
      case MERGE:
      {

        if (merge_1 != null && merge_2 != null && merge_1 != merge_2)
        {
          final Zone resize = merge_1.start < merge_2.start ? merge_1 : merge_2;
          final Zone delete = merge_1.start < merge_2.start ? merge_2 : merge_1;
          final XYPlot plot = (XYPlot) chart.getPlot();
          {
            final IntervalMarker intervalMarker = zoneMarkers.get(delete);
            plot.removeDomainMarker(intervalMarker);
            zoneMarkers.remove(delete);
            zones.remove(delete);
            fireZoneRemoved(delete);
          }

          if (resize.end < delete.end)
            resize.end = delete.end;
          {

            final IntervalMarker intervalMarker = zoneMarkers.get(resize);
            assert intervalMarker != null;
            intervalMarker.setStartValue(resize.start);
            intervalMarker.setEndValue(resize.end);
          }
          fireZoneResized(resize);
          merge_1 = null;
          merge_2 = null;
        }

        break;

      }

      default:
        break;
      }

      dragStartX = -1;
      dragZone = null;
      dragZoneEndBefore = -1;
      dragZoneStartBefore = -1;
      onDrag = false;
      move = false;
      adding = null;
      resizeStart = false;
      resizeEnd = false;

      super.mouseUp(event);
    }

    private boolean resize(final Zone zone, final double startx)
    {

      if (resizeStart)
      {
        // use start

        final long nearDomainValue =
            toNearDomainValue((findDomainX(this, startx)), false);

        if (nearDomainValue != Long.MIN_VALUE && nearDomainValue < zone.end)
        {
          zone.start = nearDomainValue;
          return true;
        }

      }
      else
      {

        final long nearDomainValue =
            toNearDomainValue((findDomainX(this, startx)), false);
        if (nearDomainValue != Long.MIN_VALUE && nearDomainValue > zone.start)
        {
          zone.end = nearDomainValue;
          return true;
        }

      }
      return false;
    }

  }

  public enum EditMode
  {
    EDIT, ZOOM, MERGE
  }

  public static class Zone
  {
    long start, end;
    private Color color;

    public Zone(final long start, final long end)
    {
      this.start = start;
      this.end = end;
    }

    public Color getColor()
    {
      return color;
    }

    public long getEnd()
    {
      return end;
    }

    public long getStart()
    {
      return start;
    }

    public void setColor(final Color zoneColor)
    {
      this.color = zoneColor;
    }

    @Override
    public String toString()
    {
      return "Zone [start=" + start + ", end=" + end + "]";
    }

  }

  public static class ZoneAdapter implements ZoneListener
  {

    @Override
    public void added(final Zone zone)
    {

    }

    @Override
    public void deleted(final Zone zone)
    {

    }

    @Override
    public void moved(final Zone zone)
    {

    }

    @Override
    public void resized(final Zone zone)
    {

    }

  }

  public static interface ZoneListener
  {
    void added(Zone zone);

    void deleted(Zone zone);

    void moved(Zone zone);

    void resized(Zone zone);
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

  public static ZoneChart create(final ZoneUndoRedoProvider undoRedoProvider,
      final String chartTitle, final String yTitle, final Composite parent,
      final Zone[] zones, final long[] timeValues, final long[] angleValues,
      final ColorProvider blueProv, final Color lineColor,
      final ZoneSlicer zoneSlicer)
  {
    // build the jfreechart Plot
    final TimeSeries xySeries = new TimeSeries("");

    for (int i = 0; i < timeValues.length; i++)
    {
      xySeries.add(new FixedMillisecond(timeValues[i]), angleValues[i]);
    }

    return create(undoRedoProvider, chartTitle, yTitle, parent, zones,
        xySeries, timeValues, blueProv, lineColor, zoneSlicer);
  }

  public static ZoneChart create(ZoneUndoRedoProvider undoRedoProvider,
      final String chartTitle, final String yTitle, final Composite parent,
      final Zone[] zones, final TimeSeries xySeries, final long[] timeValues,
      final ColorProvider blueProv, final Color lineColor,
      final ZoneSlicer zoneSlicer)
  {

    if (undoRedoProvider == null)
    {
      // switch to dummy provider
      undoRedoProvider = new ZoneUndoRedoProvider()
      {

        @Override
        public void execute(IUndoableOperation operation)
        {
          try
          {
            operation.execute(null, null);
          }
          catch (ExecutionException e)
          {
            e.printStackTrace();
          }

        }
      };
    }

    final TimeSeriesCollection dataset = new TimeSeriesCollection();
    dataset.addSeries(xySeries);

    final JFreeChart xylineChart =
        ChartFactory.createTimeSeriesChart(chartTitle, // String
            "Time", // String timeAxisLabel
            yTitle, // String valueAxisLabel,
            dataset, false, true, false);

    final XYPlot plot = (XYPlot) xylineChart.getPlot();
    final DateAxis xAxis = new DateAxis();
    plot.setDomainAxis(xAxis);

    plot.setBackgroundPaint(MWC.GUI.Properties.DebriefColors.WHITE);
    plot.setRangeGridlinePaint(MWC.GUI.Properties.DebriefColors.LIGHT_GRAY);
    plot.setDomainGridlinePaint(MWC.GUI.Properties.DebriefColors.LIGHT_GRAY);

    // and sort out the color for the line
    final XYLineAndShapeRenderer renderer =
        (XYLineAndShapeRenderer) plot.getRenderer();
    final Shape square = new Rectangle2D.Double(-2.0, -2.0, 3.0, 3.0);
    renderer.setSeriesPaint(0, lineColor);
    renderer.setSeriesShape(0, square);
    renderer.setSeriesShapesVisible(0, true);

    // ok, wrap it in the zone chart
    final ZoneChart zoneChart =
        new ZoneChart(parent, xylineChart, undoRedoProvider, zones, blueProv,
            zoneSlicer, xySeries);

    // done
    return zoneChart;
  }

  private final List<Zone> zones = new ArrayList<Zone>();
  private final Map<Zone, IntervalMarker> zoneMarkers =
      new HashMap<ZoneChart.Zone, IntervalMarker>();
  private EditMode mode = EditMode.ZOOM;
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
  private final Image merge_1Img16 = CorePlugin.getImageDescriptor(
      "/icons/16/merge_1.png").createImage();
  private final Image merge_2Img16 = CorePlugin.getImageDescriptor(
      "/icons/16/merge_2.png").createImage();
  /** 24px images for the buttons */
  private final Image editImg24 = CorePlugin.getImageDescriptor(
      "/icons/24/edit.png").createImage();
  private final Image zoomInImg24 = CorePlugin.getImageDescriptor(
      "/icons/24/zoomin.png").createImage();
  private final Image mergeImg24 = CorePlugin.getImageDescriptor(
      "/icons/24/merge.png").createImage();
  private final Image fitToWin24 = CorePlugin.getImageDescriptor(
      "/icons/24/fit_to_win.png").createImage();
  private final Image calculator24 = CorePlugin.getImageDescriptor(
      "/icons/24/calculator.png").createImage();

  private final Cursor handCursor = new Cursor(Display.getDefault(), handImg16
      .getImageData(), 0, 0);

  private final Cursor addCursor = new Cursor(Display.getDefault(), addImg16
      .getImageData(), 0, 0);

  private final Cursor merge_1Cursor = new Cursor(Display.getDefault(),
      merge_1Img16.getImageData(), 0, 0);

  private final Cursor merge_2Cursor = new Cursor(Display.getDefault(),
      merge_2Img16.getImageData(), 0, 0);

  // DnD---

  private final Cursor removeCursor = new Cursor(Display.getDefault(),
      removeImg16.getImageData(), 0, 0);
  private final Cursor handCursorDrag = new Cursor(Display.getDefault(),
      handFistImg16.getImageData(), 0, 0);
  private final Cursor resizeCursor = new Cursor(Display.getDefault(),
      SWT.CURSOR_SIZEWE);
  private final JFreeChart chart;
  private ChartComposite chartComposite;
  private Zone dragZone;
  long dragZoneStartBefore = -1;
  long dragZoneEndBefore = -1;
  private double dragStartX = -1;
  private boolean onDrag = false;
  private boolean move = false;
  private boolean resizeStart = false;

  private boolean resizeEnd = false;

  private Zone adding = null;

  private Zone merge_1 = null;

  private Zone merge_2 = null;

  private final ColorProvider colorProvider;

  private final ZoneSlicer zoneSlicer;

  private final TimeSeries xySeries;

  private final ZoneUndoRedoProvider undoRedoProvider;

  private ZoneChart(final Composite parent, final JFreeChart xylineChart,
      ZoneUndoRedoProvider undoRedoProvider, final Zone[] zones,
      final ColorProvider colorProvider, final ZoneSlicer zoneSlicer,
      final TimeSeries xySeries)
  {
    super(parent, SWT.NONE);
    this.undoRedoProvider = undoRedoProvider;
    this.chart = xylineChart;
    buildUI(xylineChart);
    this.zones.addAll(Arrays.asList(zones));
    this.zoneMarkers.clear();
    xylineChart.setAntiAlias(false);
    this.colorProvider = colorProvider;
    this.zoneSlicer = zoneSlicer;
    this.xySeries = xySeries;

    final XYPlot plot = (XYPlot) xylineChart.getPlot();
    for (final Zone zone : zones)
    {
      addZone(plot, zone);
    }
  }

  // ---

  private void addZone(final XYPlot plot, final Zone zone)
  {
    // get the color for this zone
    final Color zoneColor = colorProvider.getColorFor(zone);
    zone.setColor(zoneColor);

    final IntervalMarker mrk = new IntervalMarker(zone.start, zone.end);
    mrk.setPaint(zone.getColor());
    mrk.setAlpha(0.5f);
    plot.addDomainMarker(mrk, org.jfree.ui.Layer.FOREGROUND);
    zoneMarkers.put(zone, mrk);
  }

  public void addZoneListener(final ZoneListener listener)
  {
    listeners.add(listener);
  }

  void buildUI(final JFreeChart xylineChart)
  {
    setLayout((new GridLayout(2, false)));

    chartComposite = new CustomChartComposite(this, xylineChart);

    chartComposite.setDomainZoomable(true);
    chartComposite.setRangeZoomable(true);

    final GridData data =
        new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
            | GridData.GRAB_VERTICAL);
    data.verticalSpan = 5;
    chartComposite.setLayoutData(data);
    createToolbar();
  }

  protected void createToolbar()
  {
    {// mode buttons
      final Button edit = new Button(this, SWT.TOGGLE);
      edit.setImage(editImg24);
      edit.setToolTipText("Edit zones");
      edit.setLayoutData(new GridData(GridData.FILL_VERTICAL));

      final Button zoom = new Button(this, SWT.TOGGLE);
      zoom.setImage(zoomInImg24);
      zoom.setLayoutData(new GridData(GridData.FILL_VERTICAL));
      zoom.setSelection(true);
      zoom.setToolTipText("Zoom");

      final Button mrege = new Button(this, SWT.TOGGLE);
      mrege.setImage(mergeImg24);
      mrege.setLayoutData(new GridData(GridData.FILL_VERTICAL));

      mrege.setToolTipText("Merge");

      final Button fitToWin = new Button(this, SWT.PUSH);
      fitToWin.setImage(fitToWin24);
      fitToWin.setLayoutData(new GridData(GridData.FILL_VERTICAL));
      fitToWin.setToolTipText("Show all data");

      final Button calculate = new Button(this, SWT.PUSH);
      calculate.setImage(calculator24);
      calculate.setLayoutData(new GridData(GridData.FILL_VERTICAL));
      calculate.setToolTipText("Slice legs");

      edit.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(final SelectionEvent e)
        {
          edit.setSelection(true);

          zoom.setSelection(false);
          mrege.setSelection(false);
          setMode(ZoneChart.EditMode.EDIT);
        }
      });
      zoom.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(final SelectionEvent e)
        {
          edit.setSelection(false);
          mrege.setSelection(false);

          zoom.setSelection(true);
          setMode(ZoneChart.EditMode.ZOOM);
        }
      });
      mrege.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(final SelectionEvent e)
        {
          edit.setSelection(false);
          mrege.setSelection(true);

          zoom.setSelection(false);
          setMode(ZoneChart.EditMode.MERGE);
        }
      });

      fitToWin.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(final SelectionEvent e)
        {
          final XYPlot plot = (XYPlot) chart.getPlot();

          // NOTE: for some reason, we have to do the domain before the
          // range to get the full resize
          plot.getDomainAxis().setAutoRange(true);
          plot.getRangeAxis().setAutoRange(true);
        }
      });
      calculate.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(final SelectionEvent e)
        {
          if (zoneSlicer == null)
          {
            CorePlugin.showMessage("Manage legs", "Slicing happens here");
          }
          else
          {
            // do we have any data?
            // if(xySeries == null || xySeries.getItemCount() == 0)
            // {
            // ok, populate the data
            IEditorPart curEditor =
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().getActiveEditor();
            if (curEditor instanceof IAdaptable)
            {
              Layers layers = (Layers) curEditor.getAdapter(Layers.class);
              if (layers != null)
              {
                // prob have some data - so we can clear the list
                xySeries.clear();

                // find the first track
                Enumeration<Editable> numer = layers.elements();
                while (numer.hasMoreElements())
                {
                  Layer thisL = (Layer) numer.nextElement();
                  if (thisL instanceof TrackWrapper)
                  {
                    // ok, go for it.
                    TrackWrapper thisT = (TrackWrapper) thisL;
                    Enumeration<Editable> posits = thisT.getPositions();
                    while (posits.hasMoreElements())
                    {
                      FixWrapper thisF = (FixWrapper) posits.nextElement();
                      TimeSeriesDataItem newItem =
                          new TimeSeriesDataItem(new FixedMillisecond(thisF
                              .getDateTimeGroup().getDate().getTime()), thisF
                              .getCourseDegs());
                      xySeries.add(newItem, false);
                    }

                    // ok, share the good news
                    xySeries.fireSeriesChanged();

                    // and we can stop looping
                    break;
                  }
                }
              }
            }
          }

          // ok, do the slicing
          final List<Zone> newZones = zoneSlicer.performSlicing();

          final XYPlot thePlot = (XYPlot) chart.getPlot();

          // and ditch the intervals
          for (final Zone thisZone : zones)
          {
            // remove this marker
            final IntervalMarker thisM = zoneMarkers.get(thisZone);
            thePlot.removeDomainMarker(thisM, org.jfree.ui.Layer.FOREGROUND);
          }

          // ok, now ditch the old zone lists
          zones.clear();
          zoneMarkers.clear();

          // store the zones
          zones.addAll(newZones);

          // and create the new intervals
          for (final Zone thisZone : newZones)
          {
            addZone(thePlot, thisZone);
          }
          // }
        }
      });
    }
  }

  @Override
  public void dispose()
  {
    merge_1Cursor.dispose();
    merge_2Cursor.dispose();
    handCursor.dispose();
    handCursorDrag.dispose();
    resizeCursor.dispose();
    handImg16.dispose();
    handFistImg16.dispose();
    addCursor.dispose();
    addImg16.dispose();
    removeImg16.dispose();
    removeCursor.dispose();
    merge_1Img16.dispose();
    merge_2Img16.dispose();

    // and the 24px images
    editImg24.dispose();
    fitToWin24.dispose();
    calculator24.dispose();
    zoomInImg24.dispose();
    mergeImg24.dispose();
    super.dispose();
  }

  private long findDomainX(final ChartComposite composite, final double x)
  {
    final Rectangle dataArea = composite.getScreenDataArea();
    final Rectangle2D d2 =
        new Rectangle2D.Double(dataArea.x, dataArea.y, dataArea.width,
            dataArea.height);
    final XYPlot plot = (XYPlot) composite.getChart().getPlot();
    final double chartX =
        plot.getDomainAxis().java2DToValue(x, d2, plot.getDomainAxisEdge());

    return (long) Math.ceil(chartX);
  }

  private long findPixelX(final ChartComposite composite, final double x)
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

  void fireZoneAdded(final Zone zone)
  {
    for (final ZoneListener listener : getZoneListeners())
    {
      listener.added(zone);
    }
  }

  void fireZoneMoved(final Zone zone)
  {
    for (final ZoneListener listener : getZoneListeners())
    {
      listener.moved(zone);
    }
  }

  void fireZoneRemoved(final Zone zone)
  {
    for (final ZoneListener listener : getZoneListeners())
    {
      listener.deleted(zone);
    }
  }

  void fireZoneResized(final Zone zone)
  {
    for (final ZoneListener listener : getZoneListeners())
    {
      listener.resized(zone);
    }
  }

  public EditMode getMode()
  {
    return mode;
  }

  public List<ZoneListener> getZoneListeners()
  {
    return new ArrayList<ZoneListener>(listeners);
  }

  public Zone[] getZones()
  {
    return zones.toArray(new Zone[zones.size()]);
  }

  public void removeZoneListener(final ZoneListener listener)
  {
    listeners.remove(listener);
  }

  public void setMode(final EditMode mode)
  {
    this.mode = mode;
  }

  private long
      toNearDomainValue(final long x, final boolean ignoreZeroDistence)
  {
    long distance = Long.MAX_VALUE;
    int idx = -1;
    for (int c = 0; c < xySeries.getItemCount(); c++)
    {
      final RegularTimePeriod timePeriod = xySeries.getTimePeriod(c);

      final long cdistance = Math.abs(timePeriod.getLastMillisecond() - x);
      if ((!ignoreZeroDistence || cdistance != 0) && cdistance < distance)
      {
        idx = c;
        distance = cdistance;
      }
    }

    return idx == -1 ? Long.MIN_VALUE : xySeries.getTimePeriod(idx)
        .getLastMillisecond();
  }

}
