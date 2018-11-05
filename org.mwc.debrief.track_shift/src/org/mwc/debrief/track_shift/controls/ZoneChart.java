package org.mwc.debrief.track_shift.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.TrackShiftActivator;
import org.mwc.debrief.track_shift.ambiguity.preferences.PreferenceConstants;
import org.mwc.debrief.track_shift.views.WrappingResidualRenderer;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

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
    Color getZoneColor();
  }

  protected class CustomChartComposite extends ChartComposite
  {

    private CustomChartComposite(final Composite parent, final JFreeChart chart)
    {
      super(parent, SWT.NONE, chart, 400, 600, 300, 100, 1800, 1800, true,
          false, false, false, false, true);
    }

    private void fitToData()
    {
      final Rectangle2D previousArea = getCurrentCoverage();
      final AbstractOperation addOp = new AbstractOperation("Show all data")
      {
        @Override
        public IStatus execute(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          CustomChartComposite.super.restoreAutoBounds();
          return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          return execute(monitor, info);
        }

        @Override
        public IStatus undo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          // restore the previous area
          setCurrentCoverage(previousArea);
          return Status.OK_STATUS;
        }
      };
      undoRedoProvider.execute(addOp);
    }

    private AbstractOperation getAddOperation(final XYPlot plot,
        final Zone affect, final IntervalMarker intervalMarker)
    {
      return new AbstractOperation("Add Zone")
      {
        @Override
        public IStatus execute(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          zones.add(affect);
          fireZoneAdded(affect);
          return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          plot.addDomainMarker(intervalMarker);
          zoneMarkers.put(affect, intervalMarker);
          zones.add(affect);
          fireZoneAdded(affect);
          return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          plot.removeDomainMarker(intervalMarker);
          zoneMarkers.remove(affect);
          zones.remove(affect);
          fireZoneRemoved(affect);
          return Status.OK_STATUS;
        }
      };
    }

    private Rectangle2D getCurrentCoverage()
    {
      // get the the xy plot
      final XYPlot plot = getChart().getXYPlot();

      // get the ranges of the two axis
      final Range rangeR = plot.getRangeAxis().getRange();
      final Range domainR = plot.getDomainAxis().getRange();

      // store the ranges in a rectangle
      final Rectangle2D res = new Rectangle2D.Double(domainR.getLowerBound(),
          rangeR.getLowerBound(), domainR.getUpperBound() - domainR
              .getLowerBound(), rangeR.getUpperBound() - rangeR
                  .getLowerBound());

      // done
      return res;
    }

    private AbstractOperation getDeleteOperation(final XYPlot plot,
        final IntervalMarker intervalMarker, final Zone affect)
    {
      return new AbstractOperation("Delete Zone")
      {
        @Override
        public IStatus execute(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          return redo(monitor, info);
        }

        @Override
        public IStatus redo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          plot.removeDomainMarker(intervalMarker);
          zoneMarkers.remove(affect);
          zones.remove(affect);
          fireZoneRemoved(affect);
          return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          plot.addDomainMarker(intervalMarker);
          zoneMarkers.put(affect, intervalMarker);
          zones.add(affect);
          fireZoneAdded(affect);
          return Status.OK_STATUS;
        }
      };
    }

    private AbstractOperation getMergeOperation(final Zone resize,
        final Zone delete, final IntervalMarker deleteIntervalMarker,
        final IntervalMarker resizeIntervalMarker, final XYPlot plot,
        final long endBefore)
    {
      return new AbstractOperation("Merge Zone")
      {
        @Override
        public IStatus execute(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          plot.removeDomainMarker(deleteIntervalMarker);
          resize.end = delete.end;
          zoneMarkers.remove(delete);
          zones.remove(delete);
          fireZoneRemoved(delete);
          assert resizeIntervalMarker != null;
          resizeIntervalMarker.setStartValue(resize.start);
          resizeIntervalMarker.setEndValue(resize.end);
          fireZoneResized(resize);
          merge_1 = null;
          merge_2 = null;
          return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          plot.removeDomainMarker(deleteIntervalMarker);
          resize.end = delete.end;
          zoneMarkers.remove(delete);
          zones.remove(delete);
          fireZoneRemoved(delete);
          assert resizeIntervalMarker != null;
          resizeIntervalMarker.setStartValue(resize.start);
          resizeIntervalMarker.setEndValue(resize.end);
          fireZoneResized(resize);
          return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          plot.addDomainMarker(deleteIntervalMarker);
          zoneMarkers.put(delete, deleteIntervalMarker);
          zones.add(delete);
          fireZoneAdded(delete);
          resize.end = endBefore;
          assert resizeIntervalMarker != null;
          resizeIntervalMarker.setStartValue(resize.start);
          resizeIntervalMarker.setEndValue(resize.end);
          fireZoneResized(resize);
          return Status.OK_STATUS;
        }
      };
    }

    private AbstractOperation getResizeOperation(final Zone affect,
        final long startBefore, final long endBefore, final long startAfter,
        final long endAfter)
    {
      return new AbstractOperation("Resize Zone")
      {
        @Override
        public IStatus execute(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          fireZoneResized(affect);
          return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
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
        public IStatus undo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
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
    }

    private AbstractOperation getSplitOperation(final Zone beforeZone,
        final XYPlot plot, final long firstZoneEnd, final long secondZoneEnd,
        final IntervalMarker beforeMarker, final Zone newZone)
    {
      return new AbstractOperation("Split Zone in two")
      {
        @Override
        public IStatus execute(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          // resize the first zone
          beforeZone.end = firstZoneEnd;
          fireZoneResized(beforeZone);
          beforeMarker.setEndValue(firstZoneEnd);

          // now create the second zone
          zones.add(newZone);
          fireZoneAdded(newZone);

          // and the marker
          addZoneMarker(plot, newZone, zoneMarkers);

          return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          // enlargen the first zone again
          beforeZone.end = firstZoneEnd;
          fireZoneResized(beforeZone);
          beforeMarker.setEndValue(firstZoneEnd);

          // put the second zone back
          zones.add(newZone);
          fireZoneAdded(newZone);

          // and the marker
          addZoneMarker(plot, newZone, zoneMarkers);

          return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          // remove the second interval marker
          final IntervalMarker afterInt = zoneMarkers.get(newZone);
          plot.removeDomainMarker(afterInt);
          zoneMarkers.remove(newZone);

          // and the second zone
          zones.remove(newZone);
          fireZoneRemoved(newZone);

          // restore the limits of the first zone
          beforeZone.end = secondZoneEnd;
          fireZoneResized(beforeZone);

          // and the marker
          beforeMarker.setEndValue(secondZoneEnd);

          return Status.OK_STATUS;
        }

      };
    }

    private boolean isDelete(final Zone zone, final double x)
    {
      final boolean res;

      // see if it's within the buffer zone
      final long pixelXStart = findPixelX(this, zone.start);
      final long pixelXEnd = findPixelX(this, zone.end);
      final boolean inArea = ((x - pixelXStart) > 8 && (x - pixelXStart) >= 0)
          && ((pixelXEnd - x) > 8 && (pixelXEnd - x) >= 0);

      if (inArea)
      {
        // ok, we can delete it
        res = true;
      }
      else
      {
        // nothing to delete
        res = false;
      }

      return res;

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
      dragZone = null;
      dragZoneStartBefore = -1L;
      dragZoneEndBefore = -1L;
      dragStartX = event.x;

      switch (mode)
      {
        case ZOOM:
        {
          break;
        }
        case SWITCH:
        {
          break;
        }
        case MERGE:
        {
          this.setCursor(null);
          for (final Zone zone : zones)
          {
            // find the drag area zones
            if (findPixelX(this, zone.start) <= dragStartX && findPixelX(this,
                zone.end) >= dragStartX)
            {
              if (merge_1 == null)
              {
                merge_1 = zone;
                break;
              }
              else if (!merge_1.equals(zone))
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
            if (findPixelX(this, zone.start) <= dragStartX && findPixelX(this,
                zone.end) >= dragStartX)
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
            final long val1 = toNearDomainValue(findDomainX(this, dragStartX),
                false, xySeries);
            final long val2 = toNearDomainValue(val1, true, xySeries);
            final Color zoneColor = colorProvider.getZoneColor();
            adding = new Zone(val1 > val2 ? val2 : val1, val1 > val2 ? val1
                : val2, zoneColor);
            addZoneMarker(plot, adding, zoneMarkers);
          }
          break;
        }
        case SPLIT:
        {
          // ok, find out if we're over a zone
          for (final Zone zone : zones)
          {
            // find the drag area zones
            if (findPixelX(this, zone.start) <= dragStartX && findPixelX(this,
                zone.end) >= dragStartX)
            {
              dragZone = zone;
              dragZoneStartBefore = zone.start;
              dragZoneEndBefore = zone.end;
              break;
            }
          }
          break;
        }
        default:
          throw new IllegalArgumentException("Mouse drag mode not supported");
      }
      if (dragZone == null)
      {
        super.mouseDown(event);
      }
    }

    @Override
    public void mouseMove(final MouseEvent event)
    {
      if (mode == EditMode.ZOOM)
      {
        // we handle this in the parent zoom event, so we don't need to handle it here
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
              if (findPixelX(this, zone.start) <= currentX && findPixelX(this,
                  zone.end) >= currentX)
              {
                setCursor(merge_1 == null || merge_1 == zone ? merge_1Cursor
                    : merge_2Cursor);
                break;
              }
            }
            break;
          }
          case SWITCH:
          {
            this.setCursor(null);
            for (final Zone zone : zones)
            {
              // find the drag area zones
              if (findPixelX(this, zone.start) <= currentX && findPixelX(this,
                  zone.end) >= currentX)
              {
                setCursor(switchCursor);
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
                if (findPixelX(this, zone.start) <= currentX && findPixelX(this,
                    zone.end) >= currentX)
                {
                  resizeStart = isResizeStart(zone, currentX);
                  resizeEnd = isResizeEnd(zone, currentX);
                  if (resizeStart || resizeEnd)
                  {
                    this.setCursor(resizeCursor);
                  }
                  else if (isDelete(zone, currentX))
                  {
                    this.setCursor(removeCursor);
                  }
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
          case SPLIT:
          {
            dragZone = null;
            for (final Zone zone : zones)
            {
              // find the drag area zones
              if (findPixelX(this, zone.start) <= currentX && findPixelX(this,
                  zone.end) >= currentX)
              {
                dragZone = zone;
              }
            }
            if (dragZone != null)
            {
              final long domainX = findDomainX(this, currentX);
              final long timeOfNearestCut = toNearDomainValue(domainX, false,
                  xySeries);
              final Zone beforeZone = dragZone;

              // determine the time window, centred on the click time
              final List<TimeSeriesDataItem> cutsInZone = cutsFor(beforeZone,
                  xySeries);
              final Zone periodToCut = periodToCutFor(cutsInZone,
                  timeOfNearestCut, colorProvider);
              if (periodToCut != null)
              {
                this.setCursor(splitCursor);
              }
              else
              {
                this.setCursor(null);
              }
            }
            else
            {
              this.setCursor(null);
            }
            break;
          }
          case ZOOM:
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
              resize(adding, currentX);
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
          {
            super.mouseMove(event);
          }
          break;
        }
        default:
        {
          break;
        }
      }
    }

    @Override
    public void mouseUp(final MouseEvent event)
    {
      // put the handling into a try block,
      // since we want to be sure to clear the
      // vars on completion
      try
      {
        switch (mode)
        {
          case SWITCH:
          {
            final double currentX = event.x;// findDomainX(this, event.x);

            for (final Zone zone : zones)
            {
              // find the drag area zones
              if (findPixelX(this, zone.start) <= currentX && findPixelX(this,
                  zone.end) >= currentX)
              {
                // ok, reverse the cuts in this zone
                final AbstractOperation switchOp = new SwitchCutsOperation(zone,
                    zoneSlicer);
                undoRedoProvider.execute(switchOp);
                break;
              }
            }
          }
          case EDIT:
          {
            if (adding != null)
            {
              final XYPlot plot = (XYPlot) chart.getPlot();
              final Zone affect = adding;
              final IntervalMarker intervalMarker = zoneMarkers.get(affect);
              final AbstractOperation addOp = getAddOperation(plot, affect,
                  intervalMarker);
              undoRedoProvider.execute(addOp);
            }
            else
            {
              final XYPlot plot = (XYPlot) chart.getPlot();
              if (dragZone != null)
              {
                if (!onDrag && isDelete(dragZone, event.x))
                {
                  final IntervalMarker intervalMarker = zoneMarkers.get(
                      dragZone);
                  final Zone affect = dragZone;
                  final AbstractOperation deleteOp = getDeleteOperation(plot,
                      intervalMarker, affect);
                  undoRedoProvider.execute(deleteOp);
                }
                else if (resizeStart || resizeEnd)
                {
                  final Zone affect = dragZone;
                  final long startBefore = dragZoneStartBefore;
                  final long endBefore = dragZoneEndBefore;
                  final long startAfter = dragZone.start;
                  final long endAfter = dragZone.end;
                  final AbstractOperation resizeOp = getResizeOperation(affect,
                      startBefore, endBefore, startAfter, endAfter);
                  undoRedoProvider.execute(resizeOp);
                }
              }
            }
            break;
          }
          case MERGE:
          {
            if (merge_1 != null && merge_2 != null && !merge_1.equals(merge_2))
            {
              final Zone resize = merge_1.start < merge_2.start ? merge_1
                  : merge_2;
              final Zone delete = merge_1.start < merge_2.start ? merge_2
                  : merge_1;
              final IntervalMarker deleteIntervalMarker = zoneMarkers.get(
                  delete);
              final IntervalMarker resizeIntervalMarker = zoneMarkers.get(
                  resize);
              final XYPlot plot = (XYPlot) chart.getPlot();
              final long endBefore = resize.end;
              final AbstractOperation mergeOp = getMergeOperation(resize,
                  delete, deleteIntervalMarker, resizeIntervalMarker, plot,
                  endBefore);
              undoRedoProvider.execute(mergeOp);
            }
            break;
          }
          case SPLIT:
          {
            final long domainX = findDomainX(this, dragStartX);
            final long timeOfNearestCut = toNearDomainValue(domainX, false,
                xySeries);
            final Zone beforeZone = dragZone;

            // determine the time window, centred on the click time
            final List<TimeSeriesDataItem> cutsInZone = cutsFor(beforeZone,
                xySeries);
            final Zone periodToCut = periodToCutFor(cutsInZone,
                timeOfNearestCut, colorProvider);

            // did we generate a zone?
            if (periodToCut != null)
            {
              // get the plot
              final XYPlot plot = (XYPlot) chart.getPlot();

              // store the period for the shortened first zone
              final long firstZoneEnd = periodToCut.getStart();
              final long secondZoneStart = periodToCut.getEnd();
              final long secondZoneEnd = beforeZone.getEnd();
              final IntervalMarker beforeMarker = zoneMarkers.get(beforeZone);
              final Color zoneColor = colorProvider.getZoneColor();
              final Zone newZone = new Zone(secondZoneStart, secondZoneEnd,
                  zoneColor);

              // generate the new zone
              final AbstractOperation mergeOp = getSplitOperation(beforeZone,
                  plot, firstZoneEnd, secondZoneEnd, beforeMarker, newZone);
              undoRedoProvider.execute(mergeOp);
            }

            break;
          }
          default:
          case ZOOM:
          {
            // we fire super.mouseUp at the end of the method, we don't
            // need to do it here
            break;
          }
        }
      }
      finally
      {
        dragStartX = -1;
        dragZone = null;
        dragZoneEndBefore = -1;
        dragZoneStartBefore = -1;
        onDrag = false;
        adding = null;
        resizeStart = false;
        resizeEnd = false;
        super.mouseUp(event);
      }
    }

    private boolean resize(final Zone zone, final double startx)
    {
      if (resizeStart)
      {
        // use start
        final long nearDomainValue = toNearDomainValue((findDomainX(this,
            startx)), false, xySeries);
        if (nearDomainValue != Long.MIN_VALUE && nearDomainValue < zone.end)
        {
          zone.start = nearDomainValue;
          return true;
        }
      }
      else
      {
        final long nearDomainValue = toNearDomainValue((findDomainX(this,
            startx)), false, xySeries);
        if (nearDomainValue != Long.MIN_VALUE && nearDomainValue > zone.start)
        {
          zone.end = nearDomainValue;
          return true;
        }
      }
      return false;
    }

    private void setCurrentCoverage(final Rectangle2D area)
    {
      // get the the xy plot
      final XYPlot plot = getChart().getXYPlot();

      // set the ranges of the two axis
      plot.getDomainAxis().setRange(area.getMinX(), area.getMaxX());
      plot.getRangeAxis().setRange(area.getMinY(), area.getMaxY());
    }

    @Override
    public void zoom(final Rectangle selection)
    {
      final Rectangle2D previousArea = getCurrentCoverage();
      final AbstractOperation addOp = new AbstractOperation("Zoom")
      {
        @Override
        public IStatus execute(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          // resize to the new area
          CustomChartComposite.super.zoom(selection);
          return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          return execute(monitor, info);
        }

        @Override
        public IStatus undo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          // display previous area
          setCurrentCoverage(previousArea);
          return Status.OK_STATUS;
        }
      };
      undoRedoProvider.execute(addOp);
    }
  }

  public enum EditMode
  {
    EDIT, ZOOM, MERGE, SPLIT, SWITCH
  }

  /**
   * capture data necessary for set of radio buttons
   * 
   * @author Ian
   * 
   */
  private class RadioEvent extends SelectionAdapter
  {

    final private List<Button> list;
    final private Button myButton;
    final private EditMode myZone;

    private RadioEvent(final List<Button> btnList, final Button selected,
        final EditMode editMode)
    {
      list = btnList;
      myButton = selected;
      myZone = editMode;
    }

    @Override
    public void widgetSelected(final SelectionEvent e)
    {
      for (final Button btn : list)
      {
        btn.setSelection(btn.equals(myButton));
      }

      setMode(myZone);
    }
  }

  private static class SortedArrayList extends ArrayList<Zone>
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean add(final Zone e)
    {
      final boolean res = super.add(e);
      sortMe();
      return res;
    }

    @Override
    public boolean addAll(final Collection<? extends Zone> c)
    {
      final boolean res = super.addAll(c);
      sortMe();
      return res;
    }

    @Override
    public boolean remove(final Object o)
    {
      final boolean res = super.remove(o);
      sortMe();
      return res;
    }

    // resort ourselves
    private void sortMe()
    {
      Collections.sort(this);
    }
  }

  private static class SwitchCutsOperation extends AbstractOperation
  {

    private final Zone _zone;
    private final ZoneSlicer _zoneSlicer;

    public SwitchCutsOperation(final Zone zone, final ZoneSlicer zoneSlicer)
    {
      super("Switch cuts for this zone");
      _zone = zone;
      _zoneSlicer = zoneSlicer;
    }

    @Override
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
    {
      return redo(monitor, info);
    }

    @Override
    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // ok, loop through the cuts in this zone.
      _zoneSlicer.switchAmbiguousCuts(_zone);
      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      _zoneSlicer.switchAmbiguousCuts(_zone);
      return Status.OK_STATUS;
    }
  }

  final public static class Zone implements Comparable<Zone>
  {
    private long start;
    private long end;
    private final Color color;

    public Zone(final long start, final long end, final Color color)
    {
      this.start = start;
      this.end = end;
      this.color = color;
    }

    @Override
    public int compareTo(final Zone z)
    {
      final Long myStart = this.start;
      final Long hisStart = z.start;
      return myStart.compareTo(hisStart);
    }

    public boolean contains(final long other)
    {
      return other >= start && other <= end;
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

    /**
     * does this match an existing zone? Typically used after one end of a zone is dragged
     * 
     * @param other
     *          the zone we're comparing ourselves to
     * @return yes/no for matching
     */
    protected boolean matches(final Zone other)
    {
      return start == other.start || end == other.end;
    }

    @Override
    public String toString()
    {
      return "Zone [start=" + new Date(start) + ", end=" + new Date(end) + "]";
    }

  }

  public static class ZoneAdapter implements ZoneListener
  {

    @Override
    public void added(final Zone zone)
    {
      // dumb adapter, implementation not necessary
    }

    @Override
    public void deleted(final Zone zone)
    {
      // dumb adapter, implementation not necessary
    }

    @Override
    public void moved(final Zone zone)
    {
      // dumb adapter, implementation not necessary
    }

    @Override
    public void resized(final Zone zone)
    {
      // dumb adapter, implementation not necessary
    }
  }

  /**
   * put some zone config items into a class, so we have to pass fewer params to create() function
   * 
   * @author Ian
   * 
   */
  public static class ZoneChartConfig
  {
    private final String _chartTitle;
    private final String _yTitle;
    private final Color _lineColor;
    private final boolean _goingHolistic;

    public ZoneChartConfig(final String chartTitle, final String yTitle,
        final Color lineColor, boolean goingHolistic)
    {
      _chartTitle = chartTitle;
      _yTitle = yTitle;
      _lineColor = lineColor;
      _goingHolistic = goingHolistic;
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
    /**
     * produce a list of slices from the current data
     * 
     * @return list of zones
     */
    List<Zone> performSlicing(final boolean wholePeriod);

    /**
     * switch over any TA bearings in this zone
     * 
     */
    void switchAmbiguousCuts(Zone zone);

    /**
     * whether the current sensor data includes ambiguous cuts
     * 
     * @return
     */
    boolean ambigDataPresent();

  }

  private static void addZoneMarker(final XYPlot plot, final Zone zone,
      final Map<Zone, IntervalMarker> zoneMarkers)
  {
    final IntervalMarker mrk = new IntervalMarker(zone.start, zone.end);
    mrk.setPaint(zone.getColor());
    mrk.setAlpha(0.2f);
    plot.addDomainMarker(mrk, org.jfree.ui.Layer.FOREGROUND);
    zoneMarkers.put(zone, mrk);
  }

  public static TimePeriod calculatePanData(final boolean backwards,
      final long outerLower, final long outerUpper, final long currentStart,
      final long currentEnd)
  {
    final long period = (currentEnd - currentStart);
    final long newStart;
    if (backwards)
    {
      newStart = Math.max(outerLower, currentStart - period);
    }
    else
    {
      final long endT = outerUpper;
      newStart = Math.min(endT - period, currentEnd);
    }

    return new TimePeriod.BaseTimePeriod(new HiResDate(newStart), new HiResDate(
        newStart + period));

  }

  public static ZoneChart create(final ZoneChartConfig config,
      final ZoneUndoRedoProvider undoRedoProviderIn, final Composite parent,
      final Zone[] zones, final TimeSeries xySeries,
      final TimeSeriesCollection[] ambigCutsColl, final TimeSeries[] otherAxisSeries,
      final ColorProvider blueProv, final ZoneSlicer zoneSlicer,
      final Runnable deleteOperation, final Runnable resolveAmbiguityOperation)
  {

    final ZoneUndoRedoProvider undoRedoProvider;
    if (undoRedoProviderIn == null)
    {
      // switch to dummy provider
      undoRedoProvider = new ZoneUndoRedoProvider()
      {
        @Override
        public void execute(final IUndoableOperation operation)
        {
          try
          {
            operation.execute(null, null);
          }
          catch (final ExecutionException e)
          {
            e.printStackTrace();
          }
        }
      };
    }
    else
    {
      undoRedoProvider = undoRedoProviderIn;
    }

    final TimeSeriesCollection dataset = new TimeSeriesCollection();
    dataset.addSeries(xySeries);

    if (ambigCutsColl != null)
    {
      for (final TimeSeriesCollection coll : ambigCutsColl)
      {
        Iterator<?> iter = coll.getSeries().iterator();
        while(iter.hasNext())
        {
          TimeSeries series = (TimeSeries) iter.next();
          dataset.addSeries(series);
        }
      }
    }

    final JFreeChart xylineChart = ChartFactory.createTimeSeriesChart(
        config._chartTitle, // String
        "Time", // String timeAxisLabel
        config._yTitle, // String valueAxisLabel,
        dataset, false, true, false);

    final XYPlot plot = (XYPlot) xylineChart.getPlot();
    final DateAxis xAxis = new DateAxis();
    plot.setDomainAxis(xAxis);

    plot.setBackgroundPaint(MWC.GUI.Properties.DebriefColors.WHITE);
    plot.setRangeGridlinePaint(MWC.GUI.Properties.DebriefColors.LIGHT_GRAY);
    plot.setDomainGridlinePaint(MWC.GUI.Properties.DebriefColors.LIGHT_GRAY);

    final WrappingResidualRenderer renderer = new WrappingResidualRenderer(null,
        null, dataset, 0, 360);

    final Shape square = new Rectangle2D.Double(-2.0, -2.0, 3.0, 3.0);
    renderer.setSeriesPaint(0, config._lineColor);
    renderer.setSeriesShape(0, square);
    renderer.setSeriesShapesVisible(0, true);
    renderer.setSeriesStroke(0, new BasicStroke(2));
    renderer.setSeriesStroke(1, new BasicStroke(2));
    renderer.setSeriesStroke(2, new BasicStroke(2));
    plot.setRenderer(0, renderer);

    // do we have data for another dataset
    if (otherAxisSeries != null)
    {
      // ok, put it into another dataset
      final TimeSeriesCollection ds2 = new TimeSeriesCollection();
      for (final TimeSeries series : otherAxisSeries)
      {
        ds2.addSeries(series);
      }
      final NumberAxis y2 = new NumberAxis("\u00b0/sec");
      plot.setDataset(1, ds2);
      plot.setRangeAxis(1, y2);
      final XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true,
          true);
      renderer2.setSeriesPaint(0, Color.BLACK);
      renderer2.setSeriesStroke(0, new BasicStroke(2));
      plot.setRenderer(1, renderer2);
      plot.mapDatasetToRangeAxis(0, 0);
      plot.mapDatasetToRangeAxis(1, 1);
    }

    // ok, wrap it in the zone chart
    final ZoneChart zoneChart = new ZoneChart(parent, xylineChart,
        undoRedoProvider, zones, blueProv, zoneSlicer, xySeries,
        deleteOperation, resolveAmbiguityOperation, config._goingHolistic);

    // done
    return zoneChart;
  }

  private static Button createButton(final Composite parent, final int mode,
      final Image image, final String text, final String description,
      final Runnable event)
  {
    final Button btn = new Button(parent, mode);
    btn.setAlignment(SWT.LEFT);
    btn.setText(text);
    btn.setToolTipText(description);

    if (event != null)
    {
      btn.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(final SelectionEvent e)
        {
          event.run();
        }
      });
    }

    //
    final GridData layout = new GridData(GridData.FILL_VERTICAL);
    layout.widthHint = 100;
    btn.setLayoutData(layout);

    if (image != null)
    {
      btn.setImage(image);
    }
    return btn;
  }

  private static List<TimeSeriesDataItem> cutsFor(final Zone outerZone,
      final TimeSeries xySeries)
  {
    @SuppressWarnings("unchecked")
    final List<TimeSeriesDataItem> items = xySeries.getItems();
    final List<TimeSeriesDataItem> res = new ArrayList<TimeSeriesDataItem>();
    for (final TimeSeriesDataItem item : items)
    {
      final long dtg = item.getPeriod().getMiddleMillisecond();
      if (outerZone.getStart() <= dtg && outerZone.getEnd() >= dtg)
      {
        res.add(item);
      }
    }
    return res;
  }

  private static long findDomainX(final ChartComposite composite,
      final double x)
  {
    final Rectangle dataArea = composite.getScreenDataArea();
    final Rectangle2D d2 = new Rectangle2D.Double(dataArea.x, dataArea.y,
        dataArea.width, dataArea.height);
    final XYPlot plot = (XYPlot) composite.getChart().getPlot();
    final double chartX = plot.getDomainAxis().java2DToValue(x, d2, plot
        .getDomainAxisEdge());

    return (long) Math.ceil(chartX);
  }

  private static long findPixelX(final ChartComposite composite, final double x)
  {
    final Rectangle dataArea = composite.getScreenDataArea();
    final Rectangle2D d2 = new Rectangle2D.Double(dataArea.x, dataArea.y,
        dataArea.width, dataArea.height);
    final XYPlot plot = (XYPlot) composite.getChart().getPlot();
    final double chartX = plot.getDomainAxis().valueToJava2D(x, d2, plot
        .getDomainAxisEdge());

    return (long) Math.ceil(chartX);
  }

  /**
   * pan the viewport left & right
   * 
   * @param chart
   *          the chart we're operating on
   * @param backwards
   *          whether we're going backwards
   */
  // private static void panViewport(final JFreeChart chart,
  // final boolean backwards)
  // {
  // // ok, find the current time coverage
  // final XYPlot plot = chart.getXYPlot();
  // final Range outerRange = plot.getDataRange(plot.getDomainAxis());
  // final ValueAxis timeAxis = plot.getDomainAxis();
  //
  // final long currentStart = (long) timeAxis.getLowerBound();
  // final long currentEnd = (long) timeAxis.getUpperBound();
  //
  // final long outerLower = (long) outerRange.getLowerBound();
  // final long outerUpper = (long) outerRange.getUpperBound();
  //
  // // get the new coverage
  // final TimePeriod newPeriod =
  // calculatePanData(backwards, outerLower, outerUpper, currentStart,
  // currentEnd);
  //
  // // and update the values
  // timeAxis.setLowerBound(newPeriod.getStartDTG().getDate().getTime());
  // timeAxis.setUpperBound(newPeriod.getEndDTG().getDate().getTime());
  // }

  private static Zone periodToCutFor(final List<TimeSeriesDataItem> cutsInZone,
      final long timeOfNearestCut, final ColorProvider colorProvider)
  {
    Zone res = null;

    // ok, check we have enough cuts
    final int numCuts = cutsInZone.size();

    if (numCuts >= 5)
    {
      // ok, we've got enough to leave some either side of the removed cut
      // find out the index of the one nearest to the cut
      int indexOfCut = 0;
      for (final TimeSeriesDataItem cut : cutsInZone)
      {
        if (cut.getPeriod().getMiddleMillisecond() == timeOfNearestCut)
        {
          break;
        }
        else
        {
          indexOfCut++;
        }
      }

      // ok, check that's not the first two or the last two
      if (indexOfCut >= 2 && indexOfCut < numCuts - 2)
      {
        // ok, we can work with it. What's the time coverage of the central portion?
        final long endOfRegion = cutsInZone.get(numCuts - 2).getPeriod()
            .getMiddleMillisecond();
        final long startOfRegion = cutsInZone.get(1).getPeriod()
            .getMiddleMillisecond();

        final long period = endOfRegion - startOfRegion;

        // check we have at least 2 minutes in centre
        final long halfInterval = 1 * 60 * 1000;
        final long interval = 2 * halfInterval;

        if (period > interval)
        {
          long endOfBefore = -1;
          long startOfAfter = -1;

          for (int i = 2; i < numCuts - 2; i++)
          {
            final long thisTime = cutsInZone.get(i).getPeriod()
                .getMiddleMillisecond();
            if (thisTime <= timeOfNearestCut - halfInterval)
            {
              endOfBefore = thisTime;
            }
            else if (thisTime >= timeOfNearestCut + halfInterval)
            {
              startOfAfter = thisTime;
              break;
            }
          }

          if (endOfBefore != -1 && startOfAfter != -1)
          {
            final Color zoneColor = colorProvider.getZoneColor();
            res = new Zone(endOfBefore, startOfAfter, zoneColor);
          }
        }
      }
    }
    return res;
  }

  private static long toNearDomainValue(final long x,
      final boolean ignoreZeroDistence, final TimeSeries xySeries)
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

  private final List<Zone> zones;
  private final Map<Zone, IntervalMarker> zoneMarkers =
      new HashMap<ZoneChart.Zone, IntervalMarker>();
  private EditMode mode = EditMode.EDIT;
  private volatile List<ZoneListener> zoneListeners =
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
  private final Image cut_1Img16 = CorePlugin.getImageDescriptor(
      "/icons/16/cut.png").createImage();
  private final Image split_Img16 = CorePlugin.getImageDescriptor(
      "/icons/16/auto_split-bw-16.png").createImage();
  private final Image switch_Img16 = CorePlugin.getImageDescriptor(
      "/icons/16/arrows.png").createImage();
  /** 24px images for the buttons */
  private final Image editImg24 = CorePlugin.getImageDescriptor(
      "/icons/24/edit.png").createImage();
  private final Image zoomInImg24 = CorePlugin.getImageDescriptor(
      "/icons/24/zoomin.png").createImage();
  private final Image mergeImg24 = CorePlugin.getImageDescriptor(
      "/icons/24/merge.png").createImage();

  private final Image splitImg24 = CorePlugin.getImageDescriptor(
      "/icons/24/split.png").createImage();
  private final Image switchImg24 = CorePlugin.getImageDescriptor(
      "/icons/24/x_section.png").createImage();
  private final Image fitToWin24 = CorePlugin.getImageDescriptor(
      "/icons/24/fit_to_win.png").createImage();
  private final Image autoSlice24 = CorePlugin.getImageDescriptor(
      "/icons/24/auto_slice.png").createImage();
  private final Image autoResolve24 = CorePlugin.getImageDescriptor(
      "/icons/24/auto_resolve.png").createImage();

  private final Image autoDelete24 = CorePlugin.getImageDescriptor(
      "/icons/24/auto_delete.png").createImage();
  private final Image clearZones24 = CorePlugin.getImageDescriptor(
      "/icons/24/Binocular.png").createImage();
  // private final Image panRight24 = CorePlugin.getImageDescriptor(
  // "/icons/24/media_fast_forward.png").createImage();
  private final Cursor handCursor = new Cursor(Display.getDefault(), handImg16
      .getImageData(), 0, 0);

  private final Cursor addCursor = new Cursor(Display.getDefault(), addImg16
      .getImageData(), 0, 0);
  private final Cursor merge_1Cursor = new Cursor(Display.getDefault(),
      merge_1Img16.getImageData(), 0, 0);
  private final Cursor merge_2Cursor = new Cursor(Display.getDefault(),
      merge_2Img16.getImageData(), 0, 0);
  private final Cursor switchCursor = new Cursor(Display.getDefault(),
      switch_Img16.getImageData(), 0, 0);
  /**
   * drag/drop cursors
   * 
   */
  private final Cursor removeCursor = new Cursor(Display.getDefault(),
      removeImg16.getImageData(), 0, 0);
  private final Cursor handCursorDrag = new Cursor(Display.getDefault(),
      handFistImg16.getImageData(), 0, 0);
  private final Cursor resizeCursor = new Cursor(Display.getDefault(),
      SWT.CURSOR_SIZEWE);

  private final Cursor splitCursor = new Cursor(Display.getDefault(),
      split_Img16.getImageData(), 0, 0);

  private final JFreeChart chart;

  private CustomChartComposite chartComposite;

  private Zone dragZone;

  private long dragZoneStartBefore = -1;

  private long dragZoneEndBefore = -1;

  private double dragStartX = -1;

  private boolean onDrag = false;

  private boolean resizeStart = false;
  private boolean resizeEnd = false;
  private Zone adding = null;

  private Zone merge_1 = null;

  private Zone merge_2 = null;

  private final ColorProvider colorProvider;

  private final ZoneSlicer zoneSlicer;

  private final TimeSeries xySeries;

  private final ZoneUndoRedoProvider undoRedoProvider;

  private final Runnable deleteEvent;

  private final Runnable resolveAmbiguityEvent;

  private final List<Button> ambigControls = new ArrayList<Button>();
  private Button sliceSome;

  private ZoneChart(final Composite parent, final JFreeChart xylineChart,
      final ZoneUndoRedoProvider undoRedoProvider, final Zone[] zones,
      final ColorProvider colorProvider, final ZoneSlicer zoneSlicer,
      final TimeSeries xySeries, final Runnable deleteEvent,
      final Runnable resolveAmbiguityOperation, final boolean goingHolistic)
  {
    super(parent, SWT.NONE);
    this.undoRedoProvider = undoRedoProvider;
    this.chart = xylineChart;
    this.deleteEvent = deleteEvent;
    this.resolveAmbiguityEvent = resolveAmbiguityOperation;
    buildUI(xylineChart, goingHolistic);

    /**
     * provide sorted instanceof array list, so we know we can easily get the first/last items
     */
    this.zones = new SortedArrayList();
    this.zones.addAll(Arrays.asList(zones));
    this.zoneMarkers.clear();
    xylineChart.setAntiAlias(false);
    this.colorProvider = colorProvider;
    this.zoneSlicer = zoneSlicer;
    this.xySeries = xySeries;

    final XYPlot plot = (XYPlot) xylineChart.getPlot();
    for (final Zone zone : zones)
    {
      addZoneMarker(plot, zone, zoneMarkers);
    }
  }

  public void addZoneListener(final ZoneListener listener)
  {
    zoneListeners.add(listener);
  }

  private void buildUI(final JFreeChart xylineChart, boolean goingHolistic)
  {
    setLayout((new GridLayout(3, false)));
    chartComposite = new CustomChartComposite(this, xylineChart);
    chartComposite.setDomainZoomable(true);
    chartComposite.setRangeZoomable(true);
    final GridData data = new GridData(GridData.FILL_BOTH
        | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);

    data.verticalSpan = 6;

    chartComposite.setLayoutData(data);
    createToolbar(this, goingHolistic);
  }

  public void clearZones()
  {
    zoneMarkers.clear();
    zones.clear();

    // and from the plot
    final XYPlot thePlot = (XYPlot) chart.getPlot();
    thePlot.clearDomainMarkers();
  }

  protected void createToolbar(final Composite col1,
      final boolean goingHolistic)
  {
    // note: even if we're going holistic, we still need leg slicing
    // if we have ambiguous data
    final boolean needSlicing = true;

    final List<Button> buttons = new ArrayList<Button>();

    if (needSlicing)
    {
      final Button edit = createButton(col1, SWT.TOGGLE, editImg24, "Edit",
          "Edit zones", null);
      final Button zoom = createButton(col1, SWT.TOGGLE, zoomInImg24, "Zoom",
          "Zoom in on plot", null);
      final Button merge = createButton(col1, SWT.TOGGLE, mergeImg24, "Merge",
          "Merge zones", null);
      final Button split = createButton(col1, SWT.TOGGLE, splitImg24, "Split",
          "Split zones", null);

      buttons.add(edit);
      buttons.add(zoom);
      buttons.add(merge);
      buttons.add(split);

      // start off in edit mode
      edit.setSelection(true);

      edit.addSelectionListener(new RadioEvent(buttons, edit, EditMode.EDIT));
      zoom.addSelectionListener(new RadioEvent(buttons, zoom, EditMode.ZOOM));
      merge.addSelectionListener(new RadioEvent(buttons, merge,
          EditMode.MERGE));
      split.addSelectionListener(new RadioEvent(buttons, split,
          EditMode.SPLIT));

    }
    else
    {
      final Button zoom = createButton(col1, SWT.TOGGLE, zoomInImg24, "Zoom",
          "Zoom in on plot", null);

      buttons.add(zoom);

      // start off in edit mode
      zoom.setSelection(true);
      zoom.addSelectionListener(new RadioEvent(buttons, zoom, EditMode.ZOOM));
      setMode(EditMode.ZOOM);
    }

    // only add switcher mode if we have an ambiguity resolver
    if (resolveAmbiguityEvent != null)
    {
      final Button switcher = createButton(col1, SWT.TOGGLE, switchImg24,
          "Switch", "Switch leg to other TA bearing", null);
      buttons.add(switcher);
      switcher.addSelectionListener(new RadioEvent(buttons, switcher,
          EditMode.SWITCH));

      ambigControls.add(switcher);

      // introduce a placeholder, to separate the mode toggles from the command ones
      @SuppressWarnings("unused")
      final Label placeHolder = new Label(col1, SWT.NONE);
    }

    createButton(col1, SWT.PUSH, fitToWin24, "Reveal", "Reveal all data",
        new Runnable()
        {
          @Override
          public void run()
          {
            chartComposite.fitToData();
            resetRangeCoverage();
          }
        });

    if (needSlicing)
    {
      // ok, now the clear buttons
      createButton(col1, SWT.PUSH, clearZones24, "Clear zones",
          "Clear zone markings", new Runnable()
          {
            @Override
            public void run()
            {
              clearZones();
            }
          });

      sliceSome = createButton(col1, SWT.PUSH, autoSlice24, "Slice some",
          "Automatically slice zones", getSliceOp(false));

      // and update the label
      updateSliceLabel();

      createButton(col1, SWT.PUSH, autoSlice24, "Slice all",
          "Automatically slice zones", getSliceOp(true));

      if (deleteEvent != null)
      {
        createButton(col1, SWT.PUSH, autoDelete24, "Delete",
            "Delete cuts not in a leg", deleteEvent);
      }
    }
    if (resolveAmbiguityEvent != null)
    {
      ambigControls.add(createButton(col1, SWT.PUSH, autoResolve24, "Resolve",
          "Resolve Ambiguity", resolveAmbiguityEvent));
    }

  }

  private void updateSliceLabel()
  {
    if (sliceSome != null && !sliceSome.isDisposed())
    {
      final int MAX_LEGS = TrackShiftActivator.getDefault().getPreferenceStore()
          .getInt(PreferenceConstants.OS_TURN_MAX_LEGS);
      final String btnName = "Slice next " + MAX_LEGS;
      sliceSome.setText(btnName);
    }
  }

  private Runnable getSliceOp(final boolean doAll)
  {
    return new Runnable()
    {
      @Override
      public void run()
      {
        if (zoneSlicer == null)
        {
          CorePlugin.showMessage("Manage legs", "Slicing happens here");
        }
        else
        {
          final Runnable wrappedItem = new Runnable()
          {

            @Override
            public void run()
            {
              // ok, do the slicing
              performSlicing(doAll);
            }
          };
          BusyIndicator.showWhile(Display.getCurrent(), wrappedItem);
        }
      }
    };
  }

  @Override
  public void dispose()
  {
    merge_1Cursor.dispose();
    merge_2Cursor.dispose();
    handCursor.dispose();
    handCursorDrag.dispose();
    resizeCursor.dispose();
    switchCursor.dispose();
    handImg16.dispose();
    handFistImg16.dispose();
    switch_Img16.dispose();
    addCursor.dispose();
    addImg16.dispose();
    removeImg16.dispose();
    removeCursor.dispose();
    merge_1Img16.dispose();
    merge_2Img16.dispose();
    cut_1Img16.dispose();
    split_Img16.dispose();
    splitCursor.dispose();

    // and the 24px images
    editImg24.dispose();
    fitToWin24.dispose();
    zoomInImg24.dispose();
    mergeImg24.dispose();
    splitImg24.dispose();
    switchImg24.dispose();
    autoSlice24.dispose();
    autoResolve24.dispose();
    autoDelete24.dispose();
    clearZones24.dispose();
    // panRight24.dispose();

    super.dispose();
  }

  private void fireZoneAdded(final Zone zone)
  {
    for (final ZoneListener listener : getZoneListeners())
    {
      listener.added(zone);
    }
  }

  private void fireZoneRemoved(final Zone zone)
  {
    for (final ZoneListener listener : getZoneListeners())
    {
      listener.deleted(zone);
    }
  }

  private void fireZoneResized(final Zone zone)
  {
    for (final ZoneListener listener : getZoneListeners())
    {
      listener.resized(zone);
    }
  }

  private static Layers getLayers()
  {
    // ok, populate the data
    final IEditorPart curEditor = PlatformUI.getWorkbench()
        .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    Layers res;
    if (curEditor instanceof IAdaptable)
    {
      res = (Layers) curEditor.getAdapter(Layers.class);
    }
    else
    {
      res = null;
    }

    return res;
  }

  private void getSomeTrackData(final ReversibleOperation reversOp)
  {
    final Layers layers = getLayers();
    if (layers != null)
    {
      @SuppressWarnings("unchecked")
      final List<TimeSeriesDataItem> undoData =
          new ArrayList<TimeSeriesDataItem>(xySeries.getItems());
      final List<TimeSeriesDataItem> data = new ArrayList<TimeSeriesDataItem>();

      // find the first track
      final Enumeration<Editable> numer = layers.elements();
      while (numer.hasMoreElements())
      {
        final Layer thisL = (Layer) numer.nextElement();
        if (thisL instanceof TrackWrapper)
        {
          // ok, go for it.
          final TrackWrapper thisT = (TrackWrapper) thisL;
          final Enumeration<Editable> posits = thisT.getPositionIterator();
          while (posits.hasMoreElements())
          {
            final FixWrapper thisF = (FixWrapper) posits.nextElement();
            final TimeSeriesDataItem newItem = new TimeSeriesDataItem(
                new FixedMillisecond(thisF.getDateTimeGroup().getDate()
                    .getTime()), thisF.getCourseDegs());
            data.add(newItem);
          }

          // and we can stop looping
          break;
        }
      }

      reversOp.add(new AbstractOperation("populate data")
      {
        @Override
        public IStatus execute(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          // ditch the zones. We're having a fresh start
          clearZones();

          // prob have some data - so we can clear the list
          xySeries.clear();
          for (final TimeSeriesDataItem item : data)
          {
            xySeries.add(item, false);
          }
          // ok, share the good news
          xySeries.fireSeriesChanged();
          return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {
          return execute(monitor, info);
        }

        @Override
        public IStatus undo(final IProgressMonitor monitor,
            final IAdaptable info) throws ExecutionException
        {

          xySeries.clear();
          for (final TimeSeriesDataItem item : undoData)
          {
            xySeries.add(item, false);
          }
          // ok, share the good news
          xySeries.fireSeriesChanged();
          return Status.OK_STATUS;
        }
      });
    }
  }

  /**
   * get the time period covered by the data in this zone chart
   * 
   * @return
   */
  public TimePeriod getVisiblePeriod()
  {
    final XYPlot plot = (XYPlot) chart.getPlot();
    final Range outerRange = plot.getDomainAxis().getRange();
    final long lower = (long) outerRange.getLowerBound();
    final long upper = (long) outerRange.getUpperBound();
    final TimePeriod res = new TimePeriod.BaseTimePeriod(new HiResDate(lower),
        new HiResDate(upper));

    return res;
  }

  private List<ZoneListener> getZoneListeners()
  {
    return new ArrayList<ZoneListener>(zoneListeners);
  }

  public List<Zone> getZones()
  {
    return zones;
  }

  /**
   * called from the UI button
   * 
   * @param wholePeriod
   * 
   */
  private void performSlicing(final boolean wholePeriod)
  {
    final ReversibleOperation reversOp = new ReversibleOperation("Slice legs");

    // do we have any data?
    if (xySeries.getItemCount() == 0)
    {
      getSomeTrackData(reversOp);
    }
    // ok, do the slicing
    final List<Zone> newZones = zoneSlicer.performSlicing(wholePeriod);
    final List<Zone> undoZones = new ArrayList<>(zones);
    final Map<Zone, IntervalMarker> undozoneMarkers =
        new HashMap<ZoneChart.Zone, IntervalMarker>(zoneMarkers);

    final XYPlot thePlot = (XYPlot) chart.getPlot();
    reversOp.add(new AbstractOperation("populate new zones")
    {
      @Override
      public IStatus execute(final IProgressMonitor monitor,
          final IAdaptable info) throws ExecutionException
      {
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

        if (newZones != null)
        {
          // store the zones
          zones.addAll(newZones);

          // and create the new intervals
          for (final Zone thisZone : newZones)
          {
            addZoneMarker(thePlot, thisZone, zoneMarkers);
          }
        }
        return Status.OK_STATUS;
      }

      @Override
      public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
          throws ExecutionException
      {
        return execute(monitor, info);
      }

      @Override
      public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
          throws ExecutionException
      {
        // and ditch the intervals
        for (final IntervalMarker marker : zoneMarkers.values())
        {
          thePlot.removeDomainMarker(marker, org.jfree.ui.Layer.FOREGROUND);
        }
        zones.clear();
        zoneMarkers.clear();

        // store the old zones
        zones.addAll(undoZones);
        zoneMarkers.putAll(undozoneMarkers);
        for (final IntervalMarker intervalMarker : zoneMarkers.values())
        {
          thePlot.addDomainMarker(intervalMarker);
        }

        return Status.OK_STATUS;
      }
    });
    undoRedoProvider.execute(reversOp);
  }

  public void removeZoneListener(final ZoneListener listener)
  {
    zoneListeners.remove(listener);
  }

  /**
   * reset the limits on the range (bearing) axis
   * 
   */
  public void resetRangeCoverage()
  {
    final XYPlot plot = (XYPlot) chart.getPlot();
    final ValueAxis rangeAxis = plot.getRangeAxis();

    // the auto-range only actual fires if we're changing it
    if (rangeAxis.isAutoRange())
    {
      // ok, it's currently set. Unset it.
      rangeAxis.setAutoRange(false);
    }

    // now trigger the resize of the bearing axis
    rangeAxis.setAutoRange(true);
  }

  public void setBearingRange(final double minVal, final double maxVal)
  {
    final XYPlot plot = this.chart.getXYPlot();
    final XYItemRenderer currentRenderer = plot.getRenderer(0);
    if (currentRenderer instanceof WrappingResidualRenderer)
    {
      final WrappingResidualRenderer rend =
          (WrappingResidualRenderer) currentRenderer;
      rend.setRange(minVal, maxVal);
    }
    else
    {
      throw new IllegalArgumentException(
          "Surely the renderer should be wrapping residual one?");
    }
  }

  private void setMode(final EditMode mode)
  {
    this.mode = mode;
  }

  public void setPeriod(final TimePeriod period)
  {
    final XYPlot plot = (XYPlot) chart.getPlot();
    // final Range outerRange = plot.getDataRange(plot.getDomainAxis());
    plot.getDomainAxis().setRange(period.getStartDTG().getDate().getTime(),
        period.getEndDTG().getDate().getTime());

  }

  final public void setZones(final List<Zone> newZones)
  {
    final XYPlot plot = (XYPlot) chart.getPlot();
    for (final Zone zone : newZones)
    {
      // do we already have this zone?
      boolean found = false;
      for (final Zone oz : zones)
      {
        if (oz.matches(zone))
        {
          // yes, we don't need to recreate it
          found = true;
          break;
        }
      }
      if (!found)
      {
        addZoneMarker(plot, zone, zoneMarkers);
        zones.add(zone);
      }
    }
  }

  /**
   * the sensor data may have changed. Double-check if we should be showing resolve-related
   * controls, or not.
   */
  public void updateControls()
  {
    // ok, check if we have ambiguous data to play with
    final boolean ambigDataPresent;
    if (zoneSlicer != null)
    {
      ambigDataPresent = zoneSlicer.ambigDataPresent();
    }
    else
    {
      ambigDataPresent = false;
    }

    // ok, now hide/reveal the controls
    for (final Button t : ambigControls)
    {
      t.setVisible(ambigDataPresent);
    }

    // hey, for good measure, let's update the slice label
    updateSliceLabel();
  }
}
