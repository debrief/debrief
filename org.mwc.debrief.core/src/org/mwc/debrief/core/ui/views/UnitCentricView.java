package org.mwc.debrief.core.ui.views;

import java.awt.Color;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Layers.OperateFunction;
import MWC.GUI.Chart.Painters.LocalGridPainter;
import MWC.GUI.Shapes.RangeRingShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.TrackDataProvider;

public class UnitCentricView extends ViewPart
{

  private class PeriodAction extends Action
  {

    private final long _period;
    private final PeriodOperation _operation;

    public PeriodAction(final String title, final long period,
        final PeriodOperation operation)
    {
      super(title);
      _period = period;
      _myOverviewChart.repaint();
      _operation = operation;
    }

    @Override
    public void run()
    {
      _operation.selected(_period);
      _myOverviewChart.update();
    }
  }

  private class DistanceAction extends Action
  {

    private final WorldDistance _distance;
    private final DistanceOperation _operation;

    public DistanceAction(final String title, final WorldDistance distance,
        final DistanceOperation operation)
    {
      super(title);
      _distance = distance;
      _myOverviewChart.repaint();
      _operation = operation;
    }

    @Override
    public void run()
    {
      _operation.selected(_distance);
      _myOverviewChart.update();
    }
  }

  private static interface PeriodOperation
  {
    public void selected(long period);
  }

  private static interface DistanceOperation
  {
    public void selected(WorldDistance distance);
  }

  public static interface IOperateOnMatch
  {
    /**
     * process this single data object
     *
     * @param rawSec
     *          the fix we're looking at
     * @param offsetLocation
     *          unit-centric version of the location
     * @param proportion
     *          how far back through the time period we are
     */
    void doItTo(final FixWrapper rawSec, final WorldLocation offsetLocation,
        final double proportion);

    /**
     * process the secondary track position that's nearest to the required time
     *
     * @param nearestInTime
     * @param nearestOffset
     */
    void processNearest(final FixWrapper nearestInTime,
        final WorldLocation nearestOffset);
  }

  private class UnitCentricChart extends SWTChart
  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Point oldEnd;

    public UnitCentricChart(final Composite parent)
    {
      super(null, parent, _myProjection);
    }

    @Override
    public void chartFireSelectionChanged(final ISelection sel)
    {
      // just ignore it
    }

    private void checkDataCoverage(final Layers theLayers)
    {

      // check if we have null data area
      if ((_myOverviewChart.getCanvas().getProjection().getDataArea() == null)
          && (_trackDataProvider != null))
      {
        final WatchableList primary = _trackDataProvider.getPrimaryTrack();
        if (primary != null && primary instanceof WatchableList)
        {
          final WorldLocation origin = new WorldLocation(0d, 0d, 0d);
          final WorldArea area = new WorldArea(origin, origin);
          final IOperateOnMatch getBounds = new IOperateOnMatch()
          {

            @Override
            public void doItTo(final FixWrapper rawSec,
                final WorldLocation offsetLocation, final double proportion)
            {
              area.extend(offsetLocation);
            }

            @Override
            public void processNearest(final FixWrapper nearestInTime,
                final WorldLocation nearestOffset)
            {
              // ok, ignore
            }
          };
          walkTree(theLayers, primary, _timeProvider
              .getTime(), getBounds, getSnailLength());

          // ok, store the data area
          _myOverviewChart.getCanvas().getProjection().setDataArea(area);
        }
      }
    }

    protected Color colorFor(final Color color, final float proportion,
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

    @Override
    public void paintMe(final CanvasType dest)
    {
      if (_theLayers == null)
      {
        CorePlugin.logError(IStatus.WARNING,
            "Unit centric view is missing layers", null);
        return;
      }

      if (_trackDataProvider == null)
      {
        CorePlugin.logError(IStatus.WARNING,
            "Unit centric view is missing track data provider", null);
      }

      // ok, check we have primary track
      if (_trackDataProvider.getPrimaryTrack() == null)
      {
        CorePlugin.logError(IStatus.WARNING,
            "Unit centric view is missing primary track", null);
        CorePlugin.showMessage("Unit Centric View",
            "Please assign a primary track");
      }

      if (_timeProvider == null)
      {
        CorePlugin.logError(IStatus.WARNING,
            "Unit centric view is missing time provider", null);
      }

      checkDataCoverage(_theLayers);

      final WatchableList primary = _trackDataProvider.getPrimaryTrack();

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

      if (_showGrid.isChecked())
      {
        _localGrid.paint(dest);
      }

      if (_showRings.isChecked())
      {
        _rangeRings.paint(dest);
      }

      // get the time
      final boolean isSnail = _snailPaint.isChecked();
      final HiResDate subjectTime = _timeProvider.getTime();
      final IOperateOnMatch paintIt;
      if (isSnail)
      {
        paintIt = new IOperateOnMatch()
        {
          @Override
          public void doItTo(final FixWrapper rawSec,
              final WorldLocation offsetLocation, final double proportion)
          {
            dest.setLineWidth(3f);

            // sort out the color
            final Color newCol = colorFor(rawSec.getColor(), (float) proportion,
                _myOverviewChart.getCanvas().getBackgroundColor());

            dest.setColor(newCol);

            rawSec.paintMe(dest, offsetLocation, rawSec.getColor());

            // and the line
            final Point newEnd = dest.toScreen(offsetLocation);
            if (oldEnd != null)
            {
              dest.drawLine(oldEnd.x, oldEnd.y, newEnd.x, newEnd.y);
            }
            oldEnd = new Point(newEnd);
          }

          @Override
          public void processNearest(final FixWrapper nearestInTime,
              final WorldLocation nearestOffset)
          {
            // reset the last object pointer
            oldEnd = null;
          }
        };
      }
      else
      {
        paintIt = new IOperateOnMatch()
        {
          @Override
          public void doItTo(final FixWrapper rawSec,
              final WorldLocation offsetLocation, final double proportion)
          {
            dest.setLineWidth(2f);
            dest.setColor(rawSec.getColor());

            rawSec.paintMe(dest, offsetLocation, rawSec.getColor());

            // and the line
            final Point newEnd = dest.toScreen(offsetLocation);
            if (oldEnd != null)
            {
              dest.drawLine(oldEnd.x, oldEnd.y, newEnd.x, newEnd.y);
            }
            //
            oldEnd = new Point(newEnd);
          }

          @Override
          public void processNearest(final FixWrapper nearestInTime,
              final WorldLocation nearestOffset)
          {
            dest.setLineWidth(3);
            dest.setColor(Color.DARK_GRAY);
            final Point pt = dest.toScreen(nearestOffset);
            dest.drawRect(pt.x - 3, pt.y - 3, 7, 7);

            // reset the last object pointer
            oldEnd = null;
          }
        };
      }

      walkTree(_theLayers, primary, subjectTime, paintIt, getSnailLength());

      // draw in the ownship marker last, so it's on top
      dest.setLineWidth(2f);
      final Point pt = _myOverviewChart.getCanvas().getProjection().toScreen(
          new WorldLocation(0d, 0d, 0d));
      dest.setColor(primary.getColor());
      dest.drawOval(pt.x - 4, pt.y - 4, 8, 8);
      dest.drawLine(pt.x, pt.y - 12, pt.x, pt.y + 5);

      if (priTrack != null)
      {
        // restore interpolation on the primary track
        priTrack.setInterpolatePoints(oldInterp);
      }
    }
  }

  /**
   * convert an absolute location into a location relative to a primary track
   *
   * @param primary
   * @param other
   * @param origin
   * @return
   */
  private static WorldLocation processOffset(final FixWrapper primary,
      final WorldLocation other, final WorldLocation origin)
  {
    // ok, work out offset from this
    final WorldVector delta = other.subtract(primary.getLocation());

    // we now have to rotate the delta, according to O/S course
    final double curBearing = delta.getBearing();

    // work out the bearing relative to O/S head
    final double newBearing = curBearing - primary.getCourse();

    // update the bearing
    final WorldVector newDelta = new WorldVector(newBearing, delta.getRange(),
        0d);

    final WorldLocation pos = origin.add(newDelta);

    return pos;
  }

  private static void walkTree(final Layers theLayers,
      final WatchableList primary, final HiResDate subjectTime,
      final IOperateOnMatch doIt, final long snailLength)
  {
    final WorldLocation origin = new WorldLocation(0d, 0d, 0d);

    OperateFunction checkIt = new OperateFunction()
    {

      @Override
      public void operateOn(Editable item)
      {
        final LightweightTrackWrapper other = (LightweightTrackWrapper) item;
        if (!other.getVisible())
          return;

        // is it the primary?
        if (other != primary)
        {
          // keep track of the fix nearest to the required DTG
          FixWrapper nearestInTime = null;
          WorldLocation nearestOffset = null;
          long nearestDelta = Long.MAX_VALUE;

          // ok, run back through the data
          final Enumeration<Editable> pts = other.getPositionIterator();
          while (pts.hasMoreElements())
          {
            final FixWrapper thisF = (FixWrapper) pts.nextElement();

            final HiResDate hisD = thisF.getDTG();

            final boolean useIt;
            if (subjectTime == null)
            {
              useIt = true;
            }
            else
            {
              if (snailLength == Long.MAX_VALUE)
              {
                useIt = true;
              }
              else
              {
                final long offset = subjectTime.getDate().getTime() - hisD
                    .getDate().getTime();
                useIt = offset > 0 && offset < snailLength;
              }
            }

            if (useIt)
            {
              final Watchable[] nearest = primary.getNearestTo(hisD);
              if (nearest != null && nearest.length > 0)
              {
                final Watchable nItem = nearest[0];
                if (nItem instanceof FixWrapper)
                {
                  final FixWrapper priFix = (FixWrapper) nItem;
                  final long diff = Math.abs(hisD.getDate().getTime()
                      - subjectTime.getDate().getTime());

                  if (nearestInTime == null || diff < nearestDelta)
                  {
                    nearestInTime = thisF;
                    nearestDelta = diff;
                    nearestOffset = processOffset(priFix, thisF.getLocation(),
                        origin);
                  }

                  final WorldLocation pos = processOffset(priFix, thisF
                      .getLocation(), origin);

                  // work out how far back down the leg we are
                  final long age = subjectTime.getDate().getTime() - thisF
                      .getDTG().getDate().getTime();
                  final double proportion = age / (double) snailLength;

                  doIt.doItTo(thisF, pos, proportion);
                }
              }
            }
          }
          if (nearestInTime != null)
          {
            doIt.processNearest(nearestInTime, nearestOffset);
          }
        }
      }
    };
    theLayers.walkVisibleItems(LightweightTrackWrapper.class, checkIt);
  }

  private UnitCentricChart _myOverviewChart;

  private final FlatProjection _myProjection;

  private long _snailLength = 1000 * 60 * 30;

  /**
   * helper application to help track creation/activation of new plots
   */
  private PartMonitor _myPartMonitor;

  protected Layers _targetLayers;

  private Action _fitToWindow;

  protected TrackDataProvider _trackDataProvider;

  protected TimeProvider _timeProvider;

  protected PropertyChangeListener _timeChangeListener;

  private Action _normalPaint;

  private Action _snailPaint;

  private Action _showRings;

  private Action _showGrid;

  private final LocalGridPainter _localGrid;

  private final RangeRingShape _rangeRings;

  public UnitCentricView()
  {
    _myProjection = new FlatProjection();

    _timeChangeListener = new PropertyChangeListener()
    {

      @Override
      public void propertyChange(final PropertyChangeEvent evt)
      {
        // ok, trigger repaint
        _myOverviewChart.update();
      }
    };

    _localGrid = new LocalGridPainter();
    _localGrid.setDelta(new WorldDistance(30, WorldDistance.KM));
    _localGrid.setOrigin(new WorldLocation(0d, 0d, 0d));

    _rangeRings = new RangeRingShape(new WorldLocation(0d, 0d, 0d), 5,
        new WorldDistance(5, WorldDistance.KM));

  }

  private void contributeToActionBars()
  {
    final IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  @Override
  public void createPartControl(final Composite parent)
  {
    // declare our context sensitive help
    CorePlugin.declareContextHelp(parent, "org.mwc.debrief.help.OverviewChart");

    // hey, first create the chart
    _myOverviewChart = new UnitCentricChart(parent)
    {

      /**
       *
       */
      private static final long serialVersionUID = 1L;

      @Override
      public void canvasResized()
      {
        // just check we have a plot
        if (_targetLayers != null)
        {
          super.canvasResized();
        }
      }
    };

    makeActions();
    contributeToActionBars();

    watchMyParts();
  }

  @Override
  public void dispose()
  {
    super.dispose();

    // cancel any listeners
    if (_myPartMonitor != null)
    {
      _myPartMonitor.ditch();
    }
  }

  private void fillLocalPullDown(final IMenuManager manager)
  {
    final DistanceOperation setRings = new DistanceOperation()
    {
      @Override
      public void selected(final WorldDistance distance)
      {
        _rangeRings.setRingWidth(distance);
      }
    };
    final MenuManager ringRadii = new MenuManager("Ring radii");
    // ringRadii.setImageDescriptor(CorePlugin.getImageDescriptor(
    // "icons/16/range_rings.png"));

    ringRadii.add(new DistanceAction("100m", new WorldDistance(100,
        WorldDistance.METRES), setRings));
    ringRadii.add(new DistanceAction("500m", new WorldDistance(500,
        WorldDistance.METRES), setRings));
    ringRadii.add(new DistanceAction("1 km", new WorldDistance(1,
        WorldDistance.KM), setRings));
    ringRadii.add(new DistanceAction("1 nm", new WorldDistance(1,
        WorldDistance.NM), setRings));
    ringRadii.add(new DistanceAction("5 nm", new WorldDistance(5,
        WorldDistance.NM), setRings));
    ringRadii.add(new DistanceAction("10 nm", new WorldDistance(10,
        WorldDistance.NM), setRings));

    manager.add(ringRadii);

    final DistanceOperation setGrid = new DistanceOperation()
    {
      @Override
      public void selected(final WorldDistance distance)
      {
        _localGrid.setDelta(distance);
      }
    };
    final MenuManager gridSize = new MenuManager("Grid size");
    gridSize.add(new DistanceAction("100m", new WorldDistance(100,
        WorldDistance.METRES), setGrid));
    gridSize.add(new DistanceAction("500m", new WorldDistance(500,
        WorldDistance.METRES), setGrid));
    gridSize.add(new DistanceAction("1 km", new WorldDistance(1,
        WorldDistance.KM), setGrid));
    gridSize.add(new DistanceAction("1 nm", new WorldDistance(1,
        WorldDistance.NM), setGrid));
    gridSize.add(new DistanceAction("5 nm", new WorldDistance(5,
        WorldDistance.NM), setGrid));
    gridSize.add(new DistanceAction("10 nm", new WorldDistance(10,
        WorldDistance.NM), setGrid));

    manager.add(gridSize);

    final PeriodOperation setSnail = new PeriodOperation()
    {
      @Override
      public void selected(final long period)
      {
        _snailLength = period;
      }
    };
    final MenuManager periodSize = new MenuManager("Snail length");
    periodSize.add(new PeriodAction("5 Mins", 1000 * 60 * 5, setSnail));
    periodSize.add(new PeriodAction("15 Mins", 1000 * 60 * 15, setSnail));
    periodSize.add(new PeriodAction("30 Mins", 1000 * 60 * 30, setSnail));
    periodSize.add(new PeriodAction("1 Hour", 1000 * 60 * 60 * 1, setSnail));
    periodSize.add(new PeriodAction("2 Hours", 1000 * 60 * 60 * 2, setSnail));

    manager.add(periodSize);
  }

  private void fillLocalToolBar(final IToolBarManager manager)
  {
    manager.add(_normalPaint);
    manager.add(_snailPaint);
    manager.add(new Separator());

    manager.add(_showRings);
    manager.add(_showGrid);

    manager.add(new Separator());
    manager.add(_fitToWindow);

    // and the help link
    manager.add(new Separator());
    manager.add(CorePlugin.createOpenHelpAction(
        "org.mwc.debrief.help.OverviewChart", null, this));
  }

  /**
   * do a fit-to-window of the target viewport
   */
  protected void fitTargetToWindow()
  {
    // TODO: resize to show all data
    _myOverviewChart.getCanvas().getProjection().setDataArea(null);

    // now, redraw our rectable
    _myOverviewChart.repaint();
  }

  private long getSnailLength()
  {
    final boolean doSnail = _snailPaint.isChecked();
    if (doSnail)
    {
      return _snailLength;
    }
    else
    {
      return Long.MAX_VALUE;
    }
  }

  private void makeActions()
  {
    _fitToWindow = new Action()
    {
      @Override
      public void run()
      {
        // ok, fit the plot to the window...
        fitTargetToWindow();
      }
    };
    _fitToWindow.setText("Fit to window");
    _fitToWindow.setToolTipText(
        "Zoom the selected plot out to show the full data");
    _fitToWindow.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/fit_to_win.png"));

    _normalPaint = new Action("Normal Painter", SWT.RADIO)
    {

      @Override
      public void run()
      {
        _snailPaint.setChecked(false);

        // and repaint
        _myOverviewChart.update();
      }

    };
    _normalPaint.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/normal.png"));
    _normalPaint.setChecked(true);

    _snailPaint = new Action("Snail Painter", SWT.RADIO)
    {
      @Override
      public void run()
      {
        _normalPaint.setChecked(false);

        // and repaint
        _myOverviewChart.update();
      }
    };
    _snailPaint.setChecked(false);
    _snailPaint.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/snail.png"));

    _showRings = new Action("Show range rings", SWT.CHECK)
    {
      @Override
      public void run()
      {
        _myOverviewChart.update();
      }
    };
    _showRings.setChecked(false);
    _showRings.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/range_rings.png"));

    _showGrid = new Action("Show local grid", SWT.CHECK)
    {
      @Override
      public void run()
      {
        _myOverviewChart.update();
      }
    };
    _showGrid.setChecked(false);
    _showGrid.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/local_grid.png"));

  }

  /**
   * ok, a new plot is selected - better show it then
   *
   * @param provider
   *          the new plot
   * @param parentPart
   *          the part containing the plot
   */
  protected void plotSelected(final Layers provider,
      final IWorkbenchPart parentPart)
  {
    // ok, clear the map area
    _myOverviewChart.getCanvas().getProjection().setDataArea(null);

    // ok - update our chart to show the indicated plot.
    _myOverviewChart.setLayers(provider);

    // and trigger repaint
    _myOverviewChart.repaint();
  }

  @Override
  public void setFocus()
  {
    // TODO Auto-generated method stub

  }

  /**
   * sort out what we're listening to...
   */
  private void watchMyParts()
  {
    _myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
        .getPartService());
    _myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final Layers provider = (Layers) part;

            // is this different to our current one?
            if (provider != _targetLayers)
            {
              // ok, start listening to the new one
              _targetLayers = provider;
              plotSelected(provider, parentPart);
            }
          }
        });
    _myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            if (part == _targetLayers)
            {
              // cancel the listeners
              plotSelected(null, null);

              _targetLayers = null;
            }
          }
        });

    // we also neeed the primary/secondary track provider
    _myPartMonitor.addPartListener(TrackDataProvider.class,
        PartMonitor.ACTIVATED, new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final TrackDataProvider provider = (TrackDataProvider) part;

            // is this different to our current one?
            if (provider != _trackDataProvider)
            {
              // ok, remember it
              _trackDataProvider = provider;

              // and trigger update
              _myOverviewChart.update();
            }
          }
        });

    _myPartMonitor.addPartListener(TrackDataProvider.class, PartMonitor.CLOSED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final TrackDataProvider provider = (TrackDataProvider) part;

            // is this our current one?
            if (provider == _trackDataProvider)
            {
              // ok, drop it
              _trackDataProvider = null;

              // and refresh
              _myOverviewChart.update();
            }
          }
        });

    _myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final TimeProvider provider = (TimeProvider) part;

            // is this different to our current one?
            if (provider != _timeProvider)
            {
              if (_timeProvider != null)
              {
                // ditch the old one
                _timeProvider.removeListener(_timeChangeListener,
                    TimeProvider.TIME_CHANGED_PROPERTY_NAME);
              }

              // ok, start listening to the new one
              _timeProvider = provider;
              _timeProvider.addListener(_timeChangeListener,
                  TimeProvider.TIME_CHANGED_PROPERTY_NAME);
            }
          }
        });

    _myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final TimeProvider provider = (TimeProvider) part;

            // is this our current one?
            if (provider == _timeProvider && _timeProvider != null)
            {
              // ditch the old one
              _timeProvider.removeListener(_timeChangeListener,
                  TimeProvider.TIME_CHANGED_PROPERTY_NAME);
            }
          }
        });

    // ok we're all ready now. just try and see if the current part is valid
    _myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
        .getActivePage());
  }

}
