package org.mwc.debrief.core.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.preferences.SelectionHelper;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.ui.views.UnitCentricChart.UnitDataProvider;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Layers.OperateFunction;
import MWC.GUI.Properties.ClassWithProperty;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.TrackDataProvider;

public class UnitCentricView extends ViewPart implements PropertyChangeListener,
    UnitDataProvider
{
  private static final String IMG_SNAIL = "icons/16/snail.png";
  private static final String IMG_SNAIL_SELECTED = "icons/16/snail_selected.png";
  private static final String IMG_NORMAL = "icons/16/normal.png";
  private static final String IMG_NORMAL_SELECTED = "icons/16/normal_selected.png";
  private static final String IMG_RINGS_SELECTED = "icons/16/rings_selected.png";
  private static final String IMG_RINGS = "icons/16/range_rings.png";
  private static final String IMG_GRID_SELECTED = "icons/16/grid_selected.png";
  private static final String IMG_GRID = "icons/16/local_grid.png";
  

  /** combine a selected distance with an application using that distance
   * 
   * @author ian
   *
   */
  private static class DistanceAction extends Action
  {
    private final WorldDistance _distance;
    private final DistanceOperation _operation;
    private final UnitCentricChart _myOverviewChart;

    public DistanceAction(final String title, final WorldDistance distance,
        final DistanceOperation operation, UnitCentricChart myOverviewChart)
    {
      super(title);
      _distance = distance;
      _myOverviewChart = myOverviewChart;
//      _myOverviewChart.repaint();
      _operation = operation;
    }

    @Override
    public void run()
    {
      _operation.selected(_distance);
      _myOverviewChart.update();
    }
  }

  /** a distance related operation, populated from
   * drop-down list of distances
   * @author ian
   *
   */
  private static interface DistanceOperation
  {
    public void selected(WorldDistance distance);
  }

  /** while walking the tree, some matches have been found,
   * operate on them
   * @author ian
   *
   */
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
     * render the primary track
     *
     * @param primary
     *          the primary track
     * @param origin
     *          the point we use as origin (typically 0,0,0)
     */
    void handlePrimary(final WatchableList primary, final WorldLocation origin);

    /**
     * process the secondary track position that's nearest to the required time
     *
     * @param nearestInTime
     *          the nearest point in time on this secondary track
     * @param nearestOffset
     *          the relative location of this secondary track
     * @param primaryHeadingDegs
     *          current heading of primary track
     */
    void processNearest(final FixWrapper nearestInTime,
        final WorldLocation nearestOffset, double primaryHeadingDegs);
  }

  private static class PeriodAction extends Action
  {
    private final long _period;
    private final PeriodOperation _operation;
    private final UnitCentricChart _myOverviewChart;

    public PeriodAction(final String title, final long period,
        final PeriodOperation operation, final UnitCentricChart chart)
    {
      super(title);
      _period = period;
      _myOverviewChart = chart;
      _operation = operation;
    }

    @Override
    public void run()
    {
      _operation.selected(_period);
      _myOverviewChart.update();
    }
  }

  private static interface PeriodOperation
  {
    public void selected(long period);
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

  public static void walkTree(final Layers theLayers, final WatchableList primary,
      final HiResDate subjectTime, final IOperateOnMatch doIt,
      final long snailLength)
  {
    final WorldLocation origin = new WorldLocation(0d, 0d, 0d);

    final OperateFunction checkIt = new OperateFunction()
    {

      @Override
      public void operateOn(final Editable item)
      {
        final LightweightTrackWrapper other = (LightweightTrackWrapper) item;
        if (!other.getVisible())
          return;

        // is it the primary?
        if (other.equals(primary))
        {
          doIt.handlePrimary(primary, origin);
        }
        else
        {
          // keep track of the fix nearest to the required DTG
          FixWrapper nearestInTime = null;
          WorldLocation relativeLocation = null;
          double primaryHeading = Double.MIN_VALUE;
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
                    relativeLocation = processOffset(priFix, thisF
                        .getLocation(), origin);
                    primaryHeading = priFix.getCourseDegs();
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
            doIt.processNearest(nearestInTime, relativeLocation,
                primaryHeading);
          }
        }
      }
    };
    theLayers.walkVisibleItems(LightweightTrackWrapper.class, checkIt);
  }

  /**
   * helper - to let the user edit us
   */
  private final SelectionHelper _selectionHelper;

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

  final private PropertyChangeListener _timeChangeListener;

  private Action _normalPaint;

  private Action _snailPaint;

  private Action _showRings;

  private Action _showGrid;

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

    // sort out the selection helper
    _selectionHelper = new SelectionHelper();
  }

  private void contributeToActionBars()
  {
    final IActionBars bars = getViewSite().getActionBars();
    fillLocalToolBar(bars.getToolBarManager());
  }

  @Override
  public void createPartControl(final Composite parent)
  {
    // declare our context sensitive help
    CorePlugin.declareContextHelp(parent, "org.mwc.debrief.help.OverviewChart");

    // hey, first create the chart
    _myOverviewChart = new UnitCentricChart(parent, this)
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

    // and the selection provider bits
    getSite().setSelectionProvider(_selectionHelper);

    makeActions();
    contributeToActionBars();

    watchMyParts();
  }

  @Override
  public void dispose()
  {
    super.dispose();

    // force us to stop listening to shape
    stopListeningIfDifferentTo(null, _selectionHelper, this);

    // cancel any listeners
    if (_myPartMonitor != null)
    {
      _myPartMonitor.ditch();
    }
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
    _myOverviewChart.getCanvas().getProjection().setDataArea(null);

    // now, redraw our rectable
    _myOverviewChart.repaint();
  }

  /**
   * open the provided item in the properties view
   *
   * @param _rangeRings2
   */
  private void formatItem(final ClassWithProperty toFormat)
  {
    // also grab focus, so we're the current selection provider
    setFocus();

    // get editable perspective on this item
    final Editable editable = (Editable) toFormat;

    // do we have any data?
    if (editable.hasEditor() && editable.getInfo()
        .getPropertyDescriptors() != null)
    {

      // ok, see if we're already listening to something
      stopListeningIfDifferentTo(editable, _selectionHelper, this);

      // ok, start listening to the new item
      toFormat.addPropertyListener(this);

      // now fire the selection
      final EditableWrapper wrappedEditable = new EditableWrapper(editable);
      final StructuredSelection _propsAsSelection = new StructuredSelection(
          wrappedEditable);

      _selectionHelper.fireNewSelection(_propsAsSelection);
    }
    else
    {
      CorePlugin.logError(IStatus.WARNING, "No editable properties found for:"
          + editable, null);
    }
  }

  @Override
  public PlainProjection getProjection()
  {
    return _myProjection;
  }

  @Override
  public long getSnailLength()
  {
    final boolean doSnail = _myOverviewChart.isSnailMode();
    if (doSnail)
    {
      return _snailLength;
    }
    else
    {
      return Long.MAX_VALUE;
    }
  }

  @Override
  public TimeProvider getTimeProvider()
  {
    return _timeProvider;
  }

  @Override
  public TrackDataProvider getTrackDataProvider()
  {
    return _trackDataProvider;
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

    _normalPaint = new ToggleAction("Normal Painter", SWT.RADIO, IMG_NORMAL,IMG_NORMAL_SELECTED)
    {
      

      @Override
      public void run()
      {
        _snailPaint.setChecked(false);
        _normalPaint.setChecked(true);
        _myOverviewChart.setSnailMode(false);
        // and repaint
        _myOverviewChart.update();
      }

    };
    _normalPaint.setChecked(true);
    _normalPaint.setImageDescriptor(CorePlugin.getImageDescriptor(IMG_NORMAL_SELECTED));

    _snailPaint = new ToggleAction("Snail Painter", SWT.RADIO,IMG_SNAIL,IMG_SNAIL_SELECTED)
    {
      @Override
      public void run()
      {
        
        _normalPaint.setChecked(false);
        _snailPaint.setChecked(true);

        _myOverviewChart.setSnailMode(true);
        // and repaint
        _myOverviewChart.update();
      }
    };
    SnailDropDownMenuCreator snailDropDownMenu = new SnailDropDownMenuCreator();
    _snailPaint.setMenuCreator(snailDropDownMenu);
    _snailPaint.setChecked(false);
    _snailPaint.setImageDescriptor(CorePlugin.getImageDescriptor(IMG_SNAIL));
    _showRings = new ToggleAction("Show range rings", SWT.CHECK,IMG_RINGS,IMG_RINGS_SELECTED)
    {
      @Override
      public void run()
      {

        _showRings.setChecked(!_showRings.isChecked());
        _myOverviewChart.getRings().setVisible(_showRings.isChecked());
        _myOverviewChart.update();
      }
    };
    ShowRingsMenuCreator ringRadiiMenuCreator = new ShowRingsMenuCreator();
    _showRings.setMenuCreator(ringRadiiMenuCreator);
    _showRings.setChecked(true);
    _showRings.setImageDescriptor(CorePlugin.getImageDescriptor(IMG_RINGS_SELECTED));

    _showGrid = new ToggleAction("Show local grid", SWT.CHECK,IMG_GRID,IMG_GRID_SELECTED)
    {
      @Override
      public void run()
      {
        _showGrid.setChecked(!_showGrid.isChecked());
        _myOverviewChart.getGrid().setVisible(_showGrid.isChecked());
        _myOverviewChart.update();
      }
    };
    GridMenuCreator gridMenuCreator = new GridMenuCreator();
    _showGrid.setMenuCreator(gridMenuCreator);
    _showGrid.setChecked(true);
    _showGrid.setImageDescriptor(CorePlugin.getImageDescriptor(IMG_GRID_SELECTED));
    

  }
  
  
  
  private static abstract class ToggleAction extends Action{
    private boolean checked;
    private ImageDescriptor _checkedImage;
    private ImageDescriptor _defaultImage;
    public ToggleAction(String title,int style,String defaultImage,String selectedImage) {
      super(title,style);
      _defaultImage = CorePlugin.getImageDescriptor(defaultImage);
      _checkedImage = CorePlugin.getImageDescriptor(selectedImage);
    }
    @Override
    public void setChecked(boolean checked)
    {
      super.setChecked(checked);
      this.checked=checked;
      if (checked) {
        setImageDescriptor(_checkedImage);
        firePropertyChange(CHECKED, Boolean.FALSE, Boolean.TRUE);
      } else {
        setImageDescriptor(_defaultImage);
        firePropertyChange(CHECKED, Boolean.TRUE, Boolean.FALSE);
      }
    }
    @Override
    public boolean isChecked()
    {
      return checked;
    }
  }
  
  private class SnailDropDownMenuCreator implements IMenuCreator{
    @Override
    public Menu getMenu(Menu parent)
    {
      return parent;
    }
    private ActionContributionItem createAction(String name, long period, long existingPeriod,
        PeriodOperation setRings, UnitCentricChart myOverviewChart)
    {
      final PeriodAction periodAction = new PeriodAction(name, period, setRings,myOverviewChart);
      ActionContributionItem action = new ActionContributionItem(periodAction);
      if(period==existingPeriod)
      {
        periodAction.setChecked(true);
      }
      return action;
    }
    @Override
    public Menu getMenu(Control parent)
    {
      Menu snailMenu = new Menu(parent);
      
     final PeriodOperation setSnail = new PeriodOperation()
      {
        @Override
        public void selected(final long period)
        {
          _snailLength = period;
        }
      };
      int ctr=0;
      ActionContributionItem pa1 = createAction("5 Mins", 1000 * 60 * 5,_snailLength, setSnail,_myOverviewChart);
      pa1.fill(snailMenu, ctr++);
      ActionContributionItem pa2 = createAction("15 Mins", 1000 * 60 * 15,_snailLength, setSnail,_myOverviewChart);
      pa2.fill(snailMenu, ctr++);
      ActionContributionItem pa3 = createAction("30 Mins", 1000 * 60 * 30,_snailLength, setSnail,_myOverviewChart);
      pa3.fill(snailMenu, ctr++);
      ActionContributionItem pa4 = createAction("1 Hour", 1000 * 60 * 60,_snailLength, setSnail,_myOverviewChart);
      pa4.fill(snailMenu, ctr++);
      ActionContributionItem pa5 = createAction("2 Hours", 1000 * 60 * 60*2,_snailLength, setSnail,_myOverviewChart);
      pa5.fill(snailMenu, ctr++);
      return snailMenu;
    }
    @Override
    public void dispose()
    {
      //no need to dispose not dynamic
    }
  }
  
  private class GridMenuCreator implements IMenuCreator{
    @Override
    public Menu getMenu(Menu parent)
    {
      return parent;
    }
    private ActionContributionItem createAction(String name, WorldDistance distance, WorldDistance existingDistance,
        DistanceOperation setGrid, UnitCentricChart myOverviewChart)
    {
      final DistanceAction distanceAction = new DistanceAction(name, distance, setGrid,myOverviewChart);
      ActionContributionItem action = new ActionContributionItem(distanceAction);
      if(distance.equals(existingDistance))
      {
        distanceAction.setChecked(true);
      }
      return action;
    }
    
    @Override
    public Menu getMenu(Control parent)
    {
      Menu gridMenu = new Menu(parent);
      
      final DistanceOperation setGrid = new DistanceOperation()
      {
        @Override
        public void selected(final WorldDistance distance)
        {
          _myOverviewChart.getGrid().setDelta(distance);
        }
      };
      int ctr=0;
      WorldDistance currentLen = _myOverviewChart.getRings().getRingWidth();
      ActionContributionItem pa1 = createAction("100m", new WorldDistance(100,WorldDistance.METRES), currentLen, setGrid, _myOverviewChart);
      pa1.fill(gridMenu, ctr++);
      ActionContributionItem pa2 = createAction("500m", new WorldDistance(500,
          WorldDistance.METRES), currentLen,setGrid,_myOverviewChart);
      pa2.fill(gridMenu, ctr++);
      ActionContributionItem pa3 = createAction("1 km", new WorldDistance(1,
          WorldDistance.KM),currentLen, setGrid,_myOverviewChart);
      pa3.fill(gridMenu, ctr++);
      ActionContributionItem pa4 = createAction("1 nm", new WorldDistance(1,
          WorldDistance.NM), currentLen,setGrid,_myOverviewChart);
      pa4.fill(gridMenu, ctr++);
      ActionContributionItem pa5 = createAction("5 nm", new WorldDistance(5,
          WorldDistance.NM), currentLen,setGrid,_myOverviewChart);
      pa5.fill(gridMenu, ctr++);
      ActionContributionItem pa6 = createAction("10 nm", new WorldDistance(10,
          WorldDistance.NM), currentLen,setGrid,_myOverviewChart);
      pa6.fill(gridMenu, ctr++);
      ActionContributionItem pa7 = new ActionContributionItem(new Action("Format grid")
      {
        @Override
        public void run()
        {
          formatItem(_myOverviewChart.getGrid());
        }
      });
      pa7.fill(gridMenu, ctr++);

      return gridMenu;
    }
    @Override
    public void dispose()
    {
      //no need to dispose not dynamic
    }
  }

  private class ShowRingsMenuCreator implements IMenuCreator{
    @Override
    public Menu getMenu(Menu parent)
    {
      return parent;
    }
    
    private ActionContributionItem createAction(String name, WorldDistance distance, WorldDistance existingDistance,
        DistanceOperation setRings, UnitCentricChart myOverviewChart)
    {
      final DistanceAction distanceAction = new DistanceAction(name, distance, setRings,myOverviewChart);
      ActionContributionItem action = new ActionContributionItem(distanceAction);
      if(distance.equals(existingDistance))
      {
        distanceAction.setChecked(true);
      }
      return action;
    }
    
    @Override
    public Menu getMenu(Control parent)
    {
      Menu ringsMenu = new Menu(parent);
      
      final DistanceOperation setRings = new DistanceOperation()
      {
        @Override
        public void selected(final WorldDistance distance)
        {
          _myOverviewChart.getRings().setRingWidth(distance);
        }
      };
      
      WorldDistance currentLen = _myOverviewChart.getRings().getRingWidth();
      
      int ctr=0;
      ActionContributionItem pa1 = createAction("100m", new WorldDistance(100,
          WorldDistance.METRES),currentLen, setRings,_myOverviewChart);
      pa1.fill(ringsMenu, ctr++);
      ActionContributionItem pa2 = createAction("500m", new WorldDistance(500,
          WorldDistance.METRES),currentLen, setRings,_myOverviewChart);
      pa2.fill(ringsMenu, ctr++);
      ActionContributionItem pa3 = createAction("1 km", new WorldDistance(1,
          WorldDistance.KM),currentLen, setRings,_myOverviewChart);
      pa3.fill(ringsMenu, ctr++);
      ActionContributionItem pa4 = createAction("1 nm", new WorldDistance(1,
          WorldDistance.NM), currentLen, setRings,_myOverviewChart);
      pa4.fill(ringsMenu, ctr++);
      ActionContributionItem pa5 = createAction("5 nm", new WorldDistance(5,
          WorldDistance.NM),currentLen, setRings,_myOverviewChart);
      pa5.fill(ringsMenu, ctr++);
      ActionContributionItem pa6 = createAction("10 nm", new WorldDistance(10,
          WorldDistance.NM),currentLen, setRings,_myOverviewChart);
      pa6.fill(ringsMenu, ctr++);
      ActionContributionItem pa7 = new ActionContributionItem(new Action("Format rings")
      {
        @Override
        public void run()
        {
          formatItem(_myOverviewChart.getRings());
        }
      });
      pa7.fill(ringsMenu, ctr++);

      return ringsMenu;
    }
    @Override
    public void dispose()
    {
      //no need to dispose not dynamic
    }
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

    // and stop listening
    stopListeningIfDifferentTo(null, _selectionHelper, this);
  }

  @Override
  public void propertyChange(final PropertyChangeEvent evt)
  {
    // ok, update the plot
    _myOverviewChart.update();
  }

  @Override
  public void setFocus()
  {
    _myOverviewChart.getCanvasControl().setFocus();
  }

  /**
   * if the selection is different to this, stop listening to it.
   *
   * @param editable
   *          the new item (or null to clear the listeners anyway)
   */
  private static void stopListeningIfDifferentTo(final Editable editable, final SelectionHelper helper,
      final PropertyChangeListener listener)
  {
    final ISelection sel = helper.getSelection();
    if (sel != null && sel instanceof StructuredSelection)
    {
      final StructuredSelection struct = (StructuredSelection) sel;
      final Object firstItem = struct.getFirstElement();
      if (firstItem instanceof EditableWrapper)
      {
        final EditableWrapper wrapper = (EditableWrapper) firstItem;
        final Editable oldEd = wrapper.getEditable();
        final boolean editableHasChanged = !oldEd.equals(editable);
        if (editableHasChanged && oldEd instanceof ClassWithProperty)
        {
          // ok, stop listening to it
          final ClassWithProperty shape = (ClassWithProperty) oldEd;
          shape.removePropertyListener(listener);
        }
      }
    }
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
            if (!provider.equals(_targetLayers))
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
            if (part.equals(_targetLayers))
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
            if (!provider.equals(_trackDataProvider))
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
            if (provider.equals(_trackDataProvider))
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
            if (!provider.equals(_timeProvider))
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
            if (provider.equals(_timeProvider) && _timeProvider != null)
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
