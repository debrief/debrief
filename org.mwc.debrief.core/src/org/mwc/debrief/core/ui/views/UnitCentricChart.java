package org.mwc.debrief.core.ui.views;

import java.awt.Color;
import java.awt.Point;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.debrief.core.ui.views.UnitCentricView.IOperateOnMatch;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanPlotFaded;
import MWC.GUI.CanvasType;
import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.LocalGridPainter;
import MWC.GUI.Shapes.RangeRingShape;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.TrackDataProvider;

class UnitCentricChart extends SWTChart
{

  abstract private class CorePaintOperation implements IOperateOnMatch
  {
    protected final CanvasType dest;

    private CorePaintOperation(final CanvasType dest)
    {
      this.dest = dest;
    }

    @Override
    public void handlePrimary(final WatchableList primary,
        final WorldLocation origin)
    {
      final PlainSymbol sym = primary.getSnailShape();
      if (sym != null)
      {
        sym.paint(dest, origin);
      }
    }

    @Override
    public void processNearest(final Watchable nearest,
        final WorldLocation nearestOffset, final double primaryHeadingDegs)
    {
      if (nearest instanceof FixWrapper)
      {
        FixWrapper nearestInTime = (FixWrapper) nearest;
        final double hisCourseDegs = nearestInTime.getCourseDegs();
        // sort out the secondary's relative heading
        final double relativeHeading = (360 - primaryHeadingDegs)
            + hisCourseDegs;

        // draw the snail marker
        final WatchableList track = nearestInTime.getTrackWrapper();
        final PlainSymbol sym = track.getSnailShape();

        sym.setColor(track.getColor());

        sym.paint(dest, nearestOffset, MWC.Algorithms.Conversions.Degs2Rads(
            relativeHeading));
      }
      else if (nearest instanceof LabelWrapper)
      {
        LabelWrapper nearestInTime = (LabelWrapper) nearest;

        // draw the snail marker
        final PlainSymbol sym = nearestInTime.getSnailShape();
        sym.setColor(nearestInTime.getColor());
        sym.paint(dest, nearestOffset, MWC.Algorithms.Conversions.Degs2Rads(
            0d));
      }

      // reset the last object pointer
      oldEnd = null;
    }

  }

  /**
   * implement the normal painter (all points)
   *
   * @author ian
   *
   */
  private class NormalPaintOperation extends CorePaintOperation
  {
    private NormalPaintOperation(final CanvasType theDest)
    {
      super(theDest);
    }

    @Override
    public void doItTo(final Watchable rawSec,
        final WorldLocation offsetLocation, final double proportion)
    {
      dest.setLineWidth(2f);
      dest.setColor(rawSec.getColor());

      if (rawSec instanceof CanPlotFaded)
      {
        CanPlotFaded faded = (CanPlotFaded) rawSec;
        faded.paintMe(dest, offsetLocation, rawSec.getColor());
      }
      else
      {
        CorePlugin.logError(Status.WARNING,
            "Expect all items here to implement CanPlotFaded interface", null);
      }

      // and the line
      final Point newEnd = dest.toScreen(offsetLocation);
      if (oldEnd != null)
      {
        dest.drawLine(oldEnd.x, oldEnd.y, newEnd.x, newEnd.y);
      }
      //
      oldEnd = new Point(newEnd);
    }
  }

  /**
   * implement the snail painter operation
   *
   * @author ian
   *
   */
  private class SnailPaintOperation extends CorePaintOperation
  {

    private SnailPaintOperation(final CanvasType theDest)
    {
      super(theDest);
    }

    @Override
    public void doItTo(final Watchable secondary,
        final WorldLocation offsetLocation, final double proportion)
    {
      if (secondary instanceof FixWrapper)
      {
        FixWrapper fix = (FixWrapper) secondary;
        // try to use the same line width
        final LightweightTrackWrapper track = (LightweightTrackWrapper) fix
            .getTrackWrapper();
        if (track != null)
        {
          final float lineWidth;
          lineWidth = track.getLineThickness();
          dest.setLineWidth(lineWidth);
        }
      }
      else
      {
        dest.setLineWidth(1.0f);
      }

      // sort out the color
      final Color newCol = colorFor(secondary.getColor(), (float) proportion,
          getCanvas().getBackgroundColor());

      dest.setColor(newCol);
      
      if (secondary instanceof CanPlotFaded)
      {
        CanPlotFaded faded = (CanPlotFaded) secondary;
        faded.paintMe(dest, offsetLocation, newCol);
      }
      else
      {
        CorePlugin.logError(Status.WARNING,
            "Expect all items here to implement CanPlotFaded interface", null);
      }

      // and the line
      final Point newEnd = dest.toScreen(offsetLocation);
      if (oldEnd != null)
      {
        dest.drawLine(oldEnd.x, oldEnd.y, newEnd.x, newEnd.y);
      }
      oldEnd = new Point(newEnd);
    }
  }

  /**
   * helper class that provides background data
   *
   * @author ian
   *
   */
  public static interface UnitDataProvider
  {

    PlainProjection getProjection();

    long getSnailLength();

    TimeProvider getTimeProvider();

    TrackDataProvider getTrackDataProvider();

  }

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  protected static Color colorFor(final Color color, final float proportion,
      final Color backgroundColor)
  {
    // merge the foreground to the background
    final int red = backgroundColor.getRed() - color.getRed();
    final int green = backgroundColor.getGreen() - color.getGreen();
    final int blue = backgroundColor.getBlue() - color.getBlue();

    final float newRed = color.getRed() + red * proportion;
    final float newGreen = color.getGreen() + green * proportion;
    final float newBlue = color.getBlue() + blue * proportion;
    return new Color((int) newRed, (int) newGreen, (int) newBlue);
  }

  private final LocalGridPainter _localGrid;

  private final RangeRingShape _rangeRings;

  private boolean _snailMode = false;

  private Point oldEnd;

  private final UnitDataProvider _provider;

  public UnitCentricChart(final Composite parent,
      final UnitDataProvider provider)
  {
    super(null, parent, provider.getProjection());
    _provider = provider;

    _localGrid = new LocalGridPainter();
    _localGrid.setDelta(new WorldDistance(30, WorldDistance.KM));
    _localGrid.setOrigin(new WorldLocation(0d, 0d, 0d));

    _rangeRings = new RangeRingShape(new WorldLocation(0d, 0d, 0d), 5,
        new WorldDistance(5, WorldDistance.KM));
  }

  @Override
  public boolean supportsRightClick()
  {
    // no - unit centric view doesn't support right clik
    return false;
  }

  @Override
  public void chartFireSelectionChanged(final ISelection sel)
  {
    // just ignore it
  }

  /**
   * check we have a valid data area for the layers
   *
   * @param theLayers
   */
  private void checkDataCoverage(final Layers theLayers)
  {

    // check if we have null data area
    if ((getCanvas().getProjection().getDataArea() == null) && (_provider
        .getTrackDataProvider() != null))
    {
      final WatchableList primary = _provider.getTrackDataProvider()
          .getPrimaryTrack();
      if (primary != null && primary instanceof WatchableList)
      {
        final WorldLocation origin = new WorldLocation(0d, 0d, 0d);
        final WorldArea area = new WorldArea(origin, origin);
        final IOperateOnMatch getBounds = new IOperateOnMatch()
        {

          @Override
          public void doItTo(final Watchable rawSec,
              final WorldLocation offsetLocation, final double proportion)
          {
            area.extend(offsetLocation);
          }

          @Override
          public void handlePrimary(final WatchableList primary,
              final WorldLocation origin)
          {
            // ok, ignore
          }

          @Override
          public void processNearest(final Watchable nearestInTime,
              final WorldLocation nearestOffset,
              final double primaryHeadingDegs)
          {
            // ok, ignore
          }
        };
        UnitCentricView.walkTree(theLayers, primary, _provider.getTimeProvider()
            .getTime(), getBounds, _provider.getSnailLength(), false);

        // ok, store the data area
        getCanvas().getProjection().setDataArea(area);
      }
    }
  }

  private void doPaint(final CanvasType dest)
  {
    checkDataCoverage(_theLayers);

    final WatchableList primary = _provider.getTrackDataProvider()
        .getPrimaryTrack();

    // is it a track?
    final TrackWrapper priTrack = primary instanceof TrackWrapper
        ? (TrackWrapper) primary : null;

    // remember if we've overridden the interpolation
    final boolean oldInterp;
    if (priTrack != null)
    {
      oldInterp = priTrack.getInterpolatePoints();
      priTrack.setInterpolatePoints(true);
    }
    else
    {
      oldInterp = false;
    }

    // reset the last point we were looking at
    oldEnd = null;

    // do we draw local grid
    dest.setLineWidth(0f);

    _localGrid.paint(dest);
    _rangeRings.paint(dest);

    // get the time
    final HiResDate subjectTime = _provider.getTimeProvider().getTime();

    // check the primary is visible
    if (primary.getStartDTG().lessThan(subjectTime) && primary.getEndDTG()
        .greaterThanOrEqualTo(subjectTime))
    {
      final IOperateOnMatch paintIt;
      if (_snailMode)
      {
        paintIt = new SnailPaintOperation(dest);
      }
      else
      {
        paintIt = new NormalPaintOperation(dest);
      }

      UnitCentricView.walkTree(_theLayers, primary, subjectTime, paintIt,
          _provider.getSnailLength(), _snailMode);
    }

    if (priTrack != null)
    {
      // restore interpolation on the primary track
      priTrack.setInterpolatePoints(oldInterp);
    }
  }

  public LocalGridPainter getGrid()
  {
    return _localGrid;
  }

  public RangeRingShape getRings()
  {
    return _rangeRings;
  }

  public boolean isSnailMode()
  {
    return _snailMode;
  }

  @Override
  public void paintMe(final CanvasType dest)
  {
    if (_theLayers == null)
    {
      CorePlugin.logError(IStatus.WARNING,
          "Unit centric view is missing layers", null);
      return;
    }

    if (_provider.getTrackDataProvider() == null)
    {
      CorePlugin.logError(IStatus.WARNING,
          "Unit centric view is missing track data provider", null);
      return;
    }

    // ok, check we have primary track
    if (_provider.getTrackDataProvider().getPrimaryTrack() == null)
    {
      CorePlugin.logError(IStatus.WARNING,
          "Unit centric view is missing primary track", null);
      dest.setColor(new Color(200, 0, 0));
      dest.drawText("Please assign a primary track", 50, 50);
      return;
    }

    if (_provider.getTimeProvider() == null)
    {
      CorePlugin.logError(IStatus.WARNING,
          "Unit centric view is missing time provider", null);
      return;
    }

    // ok, checks done. Now get on with paint
    doPaint(dest);
  }

  public void setSnailMode(final boolean val)
  {
    _snailMode = val;
  }
}