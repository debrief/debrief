/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.satc_interface.data;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jfree.util.ReadOnlyIterator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.satc_interface.data.wrappers.BMC_Wrapper;
import org.mwc.debrief.satc_interface.data.wrappers.ContributionWrapper;
import org.mwc.debrief.satc_interface.data.wrappers.CourseForecastWrapper;
import org.mwc.debrief.satc_interface.data.wrappers.FMC_Wrapper;
import org.mwc.debrief.satc_interface.data.wrappers.StraightLegWrapper;
import org.mwc.debrief.satc_interface.utilities.conversions;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.ISecondaryTrack;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.NeedsToBeInformedOfRemove;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GUI.Canvas.CanvasTypeUtilities;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.HiResDate;
import MWC.GenericData.NonColoredWatchable;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.TacticalData.TrackDataProvider;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc.model.contributions.ContributionDataType;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.generator.impl.ga.IGASolutionsListener;
import com.planetmayo.debrief.satc.model.legs.AlteringRoute;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.BoundedState.BoundedStateType;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.vividsolutions.jts.geom.Coordinate;

public class SATC_Solution extends BaseLayer implements
    NeedsToBeInformedOfRemove, NeedsToKnowAboutLayers, WatchableList,
    BaseLayer.ProvidesRange, ISecondaryTrack, NonColoredWatchable
{

  /**
   * utility class to work through a route
   * 
   * @author ian
   * 
   */
  private static class DoPaint implements RouteStepper
  {
    private static final double LEG_LABEL_CLIPPING_THRESHOLD = 1.1;
    private Point lastPt = null;
    private final CanvasType _dest;
    private final float oldWid;
    private final Color myColor;

    public DoPaint(final CanvasType dest, final Color theColor)
    {
      _dest = dest;
      oldWid = _dest.getLineWidth();
      _dest.setLineWidth(5.0f);
      myColor = theColor;
    }

    @Override
    public void finish()
    {
      _dest.setLineWidth(oldWid);
      _dest.setLineStyle(CanvasType.SOLID);
    }

    @Override
    public void legComplete(final CoreRoute thisRoute)
    {
      // is it straight? or altering
      if (thisRoute.getType() == LegType.STRAIGHT)
      {
        final StraightRoute straight = (StraightRoute) thisRoute;

        // get the first point
        final State firstState = thisRoute.getStates().get(0);
        final State lastState =
            thisRoute.getStates().get(thisRoute.getStates().size() - 1);

        final Color theColor;

        if (firstState.getColor() != null)
          theColor = firstState.getColor();
        else
          theColor = Color.red;

        final Font theFont = LEG_NAME_FONT;
        final WorldLocation firstLoc =
            conversions.toLocation(firstState.getLocation().getCoordinate());
        final WorldLocation lastLoc =
            conversions.toLocation(lastState.getLocation().getCoordinate());

        CanvasTypeUtilities.drawLabelOnLine(_dest, thisRoute.getName(),
            theFont, theColor, firstLoc, lastLoc, LEG_LABEL_CLIPPING_THRESHOLD,
            true);

        final String vectorDescription =
            String.format("%.1f", new WorldSpeed(straight.getSpeed(),
                WorldSpeed.M_sec).getValueIn(WorldSpeed.Kts))
                + " kts "
                + String.format("%.0f", Math.toDegrees(MathUtils
                    .normalizeAngle(straight.getCourse()))) + "\u00B0";

        CanvasTypeUtilities.drawLabelOnLine(_dest, vectorDescription, theFont,
            theColor, firstLoc, lastLoc, LEG_LABEL_CLIPPING_THRESHOLD, false);
      }

    }

    @Override
    public void reset()
    {
      lastPt = null;
    }

    @Override
    public void step(final CoreRoute thisRoute, final State thisState)
    {
      final com.vividsolutions.jts.geom.Point loc = thisState.getLocation();
      // convert to screen
      final WorldLocation wLoc = conversions.toLocation(loc.getCoordinate());

      final Point screenPt = _dest.toScreen(wLoc);

      if (lastPt != null)
      {
        // is it straight? or altering
        if (thisRoute.getType() == LegType.STRAIGHT)
          _dest.setLineStyle(CanvasType.SOLID);
        else
          _dest.setLineStyle(CanvasType.DOTTED);

        // does this state have a color?
        if (thisState.getColor() != null)
          _dest.setColor(thisState.getColor());
        else
          _dest.setColor(myColor);

        // draw the line
        _dest.drawLine(lastPt.x, lastPt.y, screenPt.x, screenPt.y);
      }

      lastPt = screenPt;
    }
  }

  private static class MeasureRange implements RouteStepper
  {

    final private WorldLocation origin;
    double minRange = Double.MAX_VALUE;

    public MeasureRange(final WorldLocation origin)
    {
      this.origin = origin;
    }

    @Override
    public void finish()
    {
    }

    public final double getMinRange()
    {
      return minRange;
    }

    @Override
    public void legComplete(final CoreRoute thisRoute)
    {
    }

    @Override
    public void reset()
    {
    }

    @Override
    public void step(final CoreRoute thisRoute, final State thisState)
    {
      final com.vividsolutions.jts.geom.Point loc = thisState.getLocation();
      // convert to our coord system
      final WorldLocation wLoc = conversions.toLocation(loc.getCoordinate());
      final double range = wLoc.rangeFrom(origin);
      minRange = Math.min(range, minRange);
    }
  }

  private static interface RouteStepper
  {

    public abstract void finish();

    public abstract void legComplete(CoreRoute thisRoute);

    public abstract void reset();

    public abstract void step(CoreRoute thisRoute, State thisState);

  }

  // ///////////////////////////////////////////////////////////
  // info class
  // //////////////////////////////////////////////////////////
  public class SATC_Info extends Editable.EditorType implements Serializable
  {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;

    public SATC_Info(final SATC_Solution data)
    {
      super(data, data.getName(), "");
    }

    @Override
    public final MethodDescriptor[] getMethodDescriptors()
    {
      final Class<SATC_Solution> c = SATC_Solution.class;

      final MethodDescriptor[] mds =
          {
              method(c, "convertToLegs", null,
                  "Convert to Composite Track (legs)"),
              method(c, "convertToTrack", null, "Convert to Standalone Track"),
              method(c, "recalculate", null, "Recalculate solutions")};

      return mds;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
            {
                displayProp("ShowLocationConstraints",
                    "Show location constraints",
                    "whether to display location constraints", FORMAT),
                displayProp("ShowAlterationStates", "Show alteration states",
                    "whether to states during alteration", FORMAT),
                displayProp("OnlyPlotLegEnds", "Only plot leg ends",
                    "whether to only plot location bounds at leg ends", FORMAT),
                displayProp("ShowSolutions", "Show solutions",
                    "whether to display solutions", FORMAT),
                prop("Name", "the name for this solution", EditorType.FORMAT),
                prop("Color", "the color to display this solution",
                    EditorType.FORMAT),
                prop("Visible", "whether to plot this solution", VISIBILITY)};

        return res;
      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }

  protected static class WrappedState implements Watchable, Editable
  {

    private final State state;
    private WorldLocation loc;
    private boolean isVis = true;

    public WrappedState(final State state)
    {
      this.state = state;
    }

    @Override
    public WorldArea getBounds()
    {
      return new WorldArea(getLocation(), getLocation());
    }

    @Override
    public Color getColor()
    {
      return state.getColor();
    }

    @Override
    public double getCourse()
    {
      return state.getCourse();
    }

    @Override
    public double getDepth()
    {
      return 0;
    }

    @Override
    public EditorType getInfo()
    {
      return null;
    }

    @Override
    public WorldLocation getLocation()
    {
      if (loc == null)
        loc = conversions.toLocation(state.getLocation().getCoordinate());

      return loc;
    }

    @Override
    public String getName()
    {
      return DebriefFormatDateTime.toString(state.getTime().getTime());
    }

    @Override
    public double getSpeed()
    {
      return MWC.Algorithms.Conversions.Mps2Kts(state.getSpeed());
    }

    @Override
    public HiResDate getTime()
    {
      return new HiResDate(state.getTime().getTime());
    }

    @Override
    public boolean getVisible()
    {
      return isVis;
    }

    @Override
    public boolean hasEditor()
    {
      return false;
    }

    @Override
    public void setVisible(final boolean val)
    {
      isVis = val;
    }

  }

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  private final ISolver _mySolver;

  private Color _myColor = Color.green;

  /**
   * the plain font we use as a base
   */
  static final Font LEG_NAME_FONT = Defaults.getFont();

  private Layers _myLayers = null;

  private boolean _showLocationBounds = false;

  private boolean _onlyPlotLegEnds = false;

  private boolean _showAlteringBounds = false;

  private boolean _showSolutions = true;

  /**
   * the last set of bounded states that we know about
   * 
   */
  protected Collection<BoundedState> _lastStates;

  /**
   * any solutions returned by hte algorithm
   * 
   */
  protected CompositeRoute[] _newRoutes;

  private IContributionsChangedListener _contributionsListener;

  private IConstrainSpaceListener _constrainListener;

  private IGASolutionsListener _gaStepListener;

  /**
   * we remember the most recent score, to decide if it should be presented to the user
   * 
   */
  protected Double _currentScore;

  private PlainSymbol mySymbol;

  /**
   * high level property change listener, used to mark the plot as dirty
   */
  private final PropertyChangeListener _globalListener;

  /**
   * whether we interpolate our points during a getNearest() call
   * 
   */
  private boolean _interpolatePoints = false;

  /**
   * the timestamps of bearing data used to develop this solution
   */
  protected long[] _timeStamps;

  /**
   * wrap the provided solution
   * 
   * @param newSolution
   */
  public SATC_Solution(final ISolver newSolution)
  {
    super.setName(newSolution.getName());

    _globalListener = new PropertyChangeListener()
    {

      @Override
      public void propertyChange(final PropertyChangeEvent evt)
      {
        fireModified();
      }
    };

    _mySolver = newSolution;

    // clear the solver, just to be sure
    _mySolver.getContributions().clear();

    // and listen for changes
    listenToSolver(_mySolver);
  }

  @Override
  public void add(final Editable editable)
  {
    if (!(editable instanceof ContributionWrapper))
    {
      // ingore it
      return;
    }
    else
    {
      final ContributionWrapper cw = (ContributionWrapper) editable;
      final BaseContribution cont = cw.getContribution();
      // do we need to pass this to the parent?
      if (!_mySolver.getContributions().contains(cont))
      {
        _mySolver.getContributions().addContribution(cont);
      }
    }

    super.add(editable);
  }

  public void addContribution(final BaseContribution cont)
  {
    // do we need to pass this to the parent?
    if (!_mySolver.getContributions().contains(cont))
    {
      _mySolver.getContributions().addContribution(cont);
    }

    // just check it isn't an analysis contribution
    if (cont.getDataType() != ContributionDataType.ANALYSIS)
    {
      // ok, we're going to wrap it, to put it into the Layer Manager
      ContributionWrapper thisW;
      if (cont instanceof BearingMeasurementContribution)
        thisW = new BMC_Wrapper((BearingMeasurementContribution) cont);
      else if (cont instanceof FrequencyMeasurementContribution)
        thisW = new FMC_Wrapper((FrequencyMeasurementContribution) cont);
      else if (cont instanceof StraightLegForecastContribution)
        thisW = new StraightLegWrapper(cont);
      else if (cont instanceof CourseForecastContribution)
        thisW = new CourseForecastWrapper((CourseForecastContribution) cont);
      else
        thisW = new ContributionWrapper(cont);
      super.add(thisW);
    }
  }

  @Override
  public void beingRemoved()
  {
    // get the manager
    final ISolversManager mgr =
        SATC_Activator.getDefault().getService(ISolversManager.class, true);

    mgr.deactivateSolverIfActive(_mySolver);
  }

  /**
   * whether this type of BaseLayer is able to have shapes added to it
   * 
   * @return
   */
  @Override
  public boolean canTakeShapes()
  {
    return false;
  }

  /**
   * convert this solution into a set of SATC legs
   * 
   */
  public void convertToLegs()
  {
    // check if we have any solutions
    if ((_newRoutes == null) || (_newRoutes.length == 0))
    {
      CorePlugin.errorDialog("Convert solution to track",
          "Sorry, this solution contains no generated routes");
    }
    else
    {

      for (int i = 0; i < _newRoutes.length; i++)
      {
        final CompositeRoute thisR = _newRoutes[i];

        // the output track
        final TrackWrapper newT = new TrackWrapper();
        newT.setColor(Color.red);
        newT.setName(getName() + "_" + i);

        // helper objects, so we can create dynamic infills.
        TrackSegment lastLeg = null;
        TrackSegment pendingAlteration = null;

        final Iterator<CoreRoute> legs = thisR.getLegs().iterator();
        while (legs.hasNext())
        {

          final CoreRoute thisLeg = legs.next();
          if (thisLeg instanceof StraightRoute)
          {
            final StraightRoute straight = (StraightRoute) thisLeg;

            // ok - produce a TMA leg
            final double courseDegs = Math.toDegrees(straight.getCourse());
            final WorldSpeed speed =
                new WorldSpeed(straight.getSpeed(), WorldSpeed.M_sec);
            final WorldLocation origin =
                conversions
                    .toLocation(straight.getStartPoint().getCoordinate());
            final HiResDate startTime =
                new HiResDate(straight.getStartTime().getTime());
            final HiResDate endTime =
                new HiResDate(straight.getEndTime().getTime());

            final AbsoluteTMASegment abs =
                new AbsoluteTMASegment(courseDegs, speed, origin, startTime,
                    endTime);

            // remember this leg
            lastLeg = abs;

            // // quick check to see if we have some frequency data
            // IContributions conts = _mySolver.getContributions();
            // Iterator<BaseContribution> iter = conts.iterator();
            // while (iter.hasNext())
            // {
            // BaseContribution cont = (BaseContribution) iter.next();
            // if (cont instanceof Range1959ForecastContribution)
            // {
            // // ok, does it overlap this leg?
            // if (cont.getStartDate().before(straight.getEndTime())
            // && cont.getFinishDate().after(straight.getStartTime()))
            // {
            // // ok, retrieve the frequency
            // Range1959ForecastContribution freqC = (Range1959ForecastContribution) cont;
            // double freq = freqC.getfNought();
            // abs.setBaseFrequency(freq);
            // }
            // }
            // }

            // ok, do we have a pending alteartion?
            if (pendingAlteration != null)
            {
              // right, "abs" doesn't yet have any positions

              // ok, stick in a dynamic infill
              final DynamicInfillSegment infill =
                  new DynamicInfillSegment(pendingAlteration, abs);
              newT.add(infill);
              pendingAlteration = null;
            }

            abs.setName(straight.getName());
            newT.add(abs);
            abs.setName(straight.getName());

          }
          else if (thisLeg instanceof AlteringRoute)
          {
            // remember the previous straight leg.
            pendingAlteration = lastLeg;

            // COMMENTED OUT THIS NEXT BLOCK - WE'RE NOT GOING TO USE THE DERIVED ALTERATION,
            // WE'LL JUST USE OUR DYNAMIC INFILL SEGMENTS
            // final TrackSegment segment = new TrackSegment();
            // segment.setName(altering.getName());
            //
            // final ArrayList<State> states = altering.getStates();
            // for (final State thisS : states)
            // {
            // final double theCourse = thisS.getCourse();
            // final WorldSpeed theSpeed = new WorldSpeed(thisS.getSpeed(),
            // WorldSpeed.M_sec);
            // final WorldLocation theLocation = conversions.toLocation(thisS
            // .getLocation().getCoordinate());
            // final HiResDate theTime = new HiResDate(thisS.getTime().getTime());
            //
            // final Fix theFix = new Fix(theTime, theLocation, theCourse,
            // theSpeed.getValueIn(WorldSpeed.ft_sec) / 3d);
            // final FixWrapper newFix = new FixWrapper(theFix);
            // newFix.resetName();
            // segment.addFix(newFix);
            // }
            //
            // // make it dotted, that's our way of doing it.
            // segment.setLineStyle(CanvasType.DOTTED);
            //
            // newT.add(segment);
          }
          else
            DebriefPlugin.logError(IStatus.ERROR,
                "Unexpected type of route encountered:" + thisLeg, null);
        }

        // and store it
        _myLayers.addThisLayer(newT);
        
        // and hide ourselves
        setVisible(false);

        // see if we can set this track as secondary
        final IEditorPart editor = CorePlugin.getActivePage().getActiveEditor();
        if(editor != null)
        {
          final TrackManager provider = (TrackManager) editor.getAdapter(TrackManager.class);
          if(provider != null)
          {
            provider.setSecondary(newT);
          }
        }
      }
    }
  }

  /**
   * convert this solution into a formal track
   * 
   */
  public void convertToTrack()
  {
    // check if we have any solutions
    if ((_newRoutes == null) || (_newRoutes.length == 0))
    {
      CorePlugin.errorDialog("Convert solution to track",
          "Sorry, this solution contains no generated routes");
    }
    else
    {
      for (int i = 0; i < _newRoutes.length; i++)
      {
        final CompositeRoute thisR = _newRoutes[i];

        // the output track
        final TrackWrapper newT = new TrackWrapper();
        newT.setName(getName() + "_" + i);

        // loop through the legs
        final Iterator<CoreRoute> legs = thisR.getLegs().iterator();
        while (legs.hasNext())
        {
          final CoreRoute thisLeg = legs.next();

          // ok, loop through the states
          final Iterator<State> iter = thisLeg.getStates().iterator();
          while (iter.hasNext())
          {
            final State state = iter.next();
            final WorldLocation theLoc =
                conversions.toLocation(state.getLocation().getCoordinate());
            final double theCourse = state.getCourse();
            final double theSpeed =
                new WorldSpeed(state.getSpeed(), WorldSpeed.M_sec)
                    .getValueIn(WorldSpeed.ft_sec / 3);
            final Fix newF =
                new Fix(new HiResDate(state.getTime().getTime()), theLoc,
                    theCourse, theSpeed);
            final FixWrapper newFW = new FixWrapper(newF);

            // reset the label
            newFW.resetName();

            newT.addFix(newFW);
          }
        }

        // and store it
        _myLayers.addThisLayer(newT);

        // and hide ourselves
        setVisible(false);
      }
    }
  }

  @Override
  public void filterListTo(final HiResDate start, final HiResDate end)
  {
  }

  @Override
  protected void finalize() throws Throwable
  {
    super.finalize();

    _mySolver.getSolutionGenerator().removeReadyListener(_gaStepListener);
    _mySolver.getContributions().removeContributionsChangedListener(
        _contributionsListener);
    _mySolver.getBoundsManager().removeConstrainSpaceListener(
        _constrainListener);
    _mySolver.removePropertyChangeListener(_globalListener);
    _myLayers = null;
  }

  protected void fireModified()
  {
    if (_myLayers != null)
      _myLayers.fireModified(this);
  }

  protected void fireRepaint()
  {
    super.firePropertyChange(SupportsPropertyListeners.FORMAT, null, this);
  }

  protected void fireTrackShifted()
  {
    final WatchableList wl = this;
    Display.getDefault().asyncExec(new Runnable()
    {

      @Override
      public void run()
      {
        // if the current editor is a track data provider,
        // tell it that we've shifted
        final IWorkbench wb = PlatformUI.getWorkbench();
        final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        if (win != null)
        {
          final IWorkbenchPage page = win.getActivePage();
          final IEditorPart editor = page.getActiveEditor();
          if (editor != null)
          {
            final TrackDataProvider dataMgr =
                (TrackDataProvider) editor.getAdapter(TrackDataProvider.class);
            // is it one of ours?
            if (dataMgr != null)
            {
              dataMgr.fireTrackShift(wl);
            }
          }
        }

      }
    });

  }

  @Override
  public WorldArea getBounds()
  {
    WorldArea res = null;

    // check if we have any solutions
    if ((_newRoutes != null) && (_newRoutes.length >= 0))
    {
      // ok, collate some data
      final CompositeRoute route = _newRoutes[0];

      final Collection<CoreRoute> legs = route.getLegs();
      for (final Iterator<CoreRoute> iterator = legs.iterator(); iterator
          .hasNext();)
      {
        final CoreRoute thisRoute = iterator.next();

        // get the end points for this leg
        final WorldLocation start =
            conversions.toLocation(thisRoute.getStartPoint().getCoordinate());
        final WorldLocation end =
            conversions.toLocation(thisRoute.getEndPoint().getCoordinate());

        // is this the first area?
        if (res == null)
        {
          res = new WorldArea(start, end);
        }
        else
        {
          res.extend(new WorldArea(start, end));
        }
      }
    }

    return res;
  }

  @Override
  public Color getColor()
  {
    return _myColor;
  }

  @Override
  public HiResDate getEndDTG()
  {
    HiResDate endD = null;
    final Iterator<BaseContribution> iter =
        _mySolver.getContributions().iterator();
    while (iter.hasNext())
    {
      final BaseContribution cont = iter.next();
      final Date thisFinish = cont.getFinishDate();
      if (thisFinish != null
          && (endD == null)
          || (thisFinish != null && thisFinish.getTime() > endD.getDate()
              .getTime()))
        endD = new HiResDate(thisFinish.getTime());
    }

    return endD;
  }

  @Override
  public EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new SATC_Info(this);

    return _myEditor;
  }

  @Override
  public final boolean getInterpolatePoints()
  {
    return _interpolatePoints;
  }

  @Override
  public Collection<Editable> getItemsBetween(final HiResDate start,
      final HiResDate end)
  {
    final Collection<Editable> items = new ArrayList<Editable>();
    final ArrayList<State> states = new ArrayList<State>();

    final long startT = start.getDate().getTime();
    final long finishT = end.getDate().getTime();

    // check if we have any solutions
    if ((_newRoutes != null) && (_newRoutes.length >= 0))
    {
      // ok, collate some data
      final CompositeRoute route = _newRoutes[0];

      final Iterator<CoreRoute> legs = route.getLegs().iterator();
      while (legs.hasNext())
      {
        final CoreRoute thisLeg = legs.next();
        final Iterator<State> theStates = thisLeg.getStates().iterator();
        while (theStates.hasNext())
        {
          final State state = theStates.next();

          // does it even have a location?
          if (state.getLocation() != null)
          {
            final long thisTime = state.getTime().getTime();
            if ((thisTime >= startT) && (thisTime <= finishT))
            {
              // check we haven't just stored a state at this
              // time,
              // JFReeChart plotting doesn't like it.
              if (states.size() > 0)
              {
                final Date lastTime = states.get(states.size() - 1).getTime();
                if (lastTime.getTime() != thisTime)
                {
                  states.add(state);
                }
              }
              else
                states.add(state);
            }
          }
        }
      }
    }

    // ok, wrap the states
    final Iterator<State> iter = states.iterator();
    while (iter.hasNext())
    {
      final State state = iter.next();
      items.add(wrapThis(state));
    }

    Collection<Editable> res = null;
    if (items.size() > 0)
    {
      res = items;
    }
    return res;
  }

  @Override
  public Watchable[] getNearestTo(final HiResDate DTG)
  {
    final ArrayList<Watchable> items = new ArrayList<Watchable>();

    final long time = DTG.getDate().getTime();

    // check if we have any solutions
    if ((_newRoutes != null) && (_newRoutes.length >= 0))
    {
      // ok, collate some data
      final CompositeRoute route = _newRoutes[0];
      State before = null;

      final Iterator<CoreRoute> legs = route.getLegs().iterator();
      while (legs.hasNext())
      {
        final CoreRoute thisLeg = legs.next();
        final Iterator<State> states = thisLeg.getStates().iterator();
        while (states.hasNext())
        {
          final State state = states.next();

          // does it even have a location?
          if (state.getLocation() != null)
          {
            if (state.getTime().getTime() >= time)
            {

              Watchable wrapped = null;

              // should we be interpolating?
              if (getInterpolatePoints())
              {
                // yes. do we have a previous location?
                if (before != null)
                {
                  // yes, get interpolating
                  final WrappedState beforeWrapped = wrapThis(before);
                  final WrappedState thisWrapped = wrapThis(state);
                  wrapped =
                      FixWrapper
                          .interpolateFix(beforeWrapped, thisWrapped, DTG);
                }
                else
                {
                  // this is our first item, just wrap it
                  wrapped = wrapThis(state);
                }
              }
              else
              {
                // nope, we're done :-)
                wrapped = wrapThis(state);
              }

              items.add(wrapped);
              return items.toArray(new Watchable[]
              {});
            }

            before = state;
          }
        }
      }

    }

    return items.toArray(new Watchable[]
    {});
  }

  public boolean getOnlyPlotLegEnds()
  {
    return _onlyPlotLegEnds;
  }

  public boolean getShowAlterationStates()
  {
    return _showAlteringBounds;
  }

  public boolean getShowLocationConstraints()
  {
    return _showLocationBounds;
  }

  public boolean getShowSolutions()
  {
    return _showSolutions;
  }

  @Override
  public PlainSymbol getSnailShape()
  {
    if (mySymbol == null)
      mySymbol = SymbolFactory.createSymbol(SymbolFactory.DEFAULT_SYMBOL_TYPE);

    return mySymbol;
  }

  public ISolver getSolver()
  {
    return _mySolver;
  }

  @Override
  public HiResDate getStartDTG()
  {
    HiResDate startD = null;
    final Iterator<BaseContribution> iter =
        _mySolver.getContributions().iterator();
    while (iter.hasNext())
    {
      final BaseContribution cont = iter.next();
      if (cont.getStartDate() != null)
      {
        if ((startD == null)
            || (cont.getStartDate().getTime() < startD.getDate().getTime()))
          startD = new HiResDate(cont.getStartDate().getTime());
      }
    }

    return startD;
  }

  @Override
  public boolean hasEditor()
  {
    return true;
  }

  @Override
  public boolean hasOrderedChildren()
  {
    return true;
  }

  @Override
  public boolean isBuffered()
  {
    return false;
  }

  private void listenToSolver(final ISolver solver)
  {
    _gaStepListener = new IGASolutionsListener()
    {
      @Override
      public void finishedGeneration(final Throwable error)
      {
      }

      @Override
      public void iterationComputed(final List<CompositeRoute> topRoutes,
          final double topScore)
      {
        // store the most recent score - so we can
        // decide whether to bother showing a result to the user
        _currentScore = topScore;
      }

      @Override
      public void solutionsReady(final CompositeRoute[] routes)
      {
        // store the routes
        _newRoutes = routes;

        // tell the layer manager that we've changed
        fireTrackShifted();

        // hey, trigger repaint
        fireRepaint();
      }

      @Override
      public void startingGeneration()
      {
        // ditch any existing routes
        _newRoutes = null;

        // clear the top score counter
        _currentScore = null;
      }
    };

    final SATC_Solution parentThis = this;

    _contributionsListener = new IContributionsChangedListener()
    {

      @Override
      public void added(final BaseContribution contribution)
      {
        // fireRepaint();
        fireExtended();

        // aah, we need to add this, if we haven't already!
        addContribution(contribution);
      }

      public void fireExtended()
      {
        firePropertyChange(SupportsPropertyListeners.EXTENDED, null, parentThis);
      }

      @Override
      public void modified()
      {
        fireModified();
      }

      @Override
      public void removed(final BaseContribution contribution)
      {

        // hey, are we still storing this?
        Editable toBeRemoved = null;

        // get read-only version of elements
        final ReadOnlyIterator rIter =
            new ReadOnlyIterator(getData().iterator());
        while (rIter.hasNext())
        {
          final Editable editable = (Editable) rIter.next();
          final ContributionWrapper cw = (ContributionWrapper) editable;
          final BaseContribution thisCont = cw.getContribution();
          if (thisCont == contribution)
          {
            // _mySolver.getContributions().removeContribution(contribution);
            toBeRemoved = cw;
          }
        }

        if (toBeRemoved != null)
        {
          // ditch it from the parent (but don't trigger the remote updates to
          // fire)
          SATC_Solution.super.removeElement(toBeRemoved);
        }
        else
        {
          // don't worry - we may have removed it from the LayerManager rather than the
          // maintain contributions view
          // SATC_Activator
          // .log(
          // IStatus.ERROR,
          // "We were asked to remove a contribution, but we didn't have it stored in the Layer",
          // null);
        }

        fireExtended();
      }
    };

    _constrainListener = new IConstrainSpaceListener()
    {
      @Override
      public void error(final IBoundsManager boundsManager,
          final IncompatibleStateException ex)
      {
        _lastStates = null;
      }

      @Override
      public void restarted(final IBoundsManager boundsManager)
      {
        _lastStates = null;
        _newRoutes = null;
      }

      @Override
      public void statesBounded(final IBoundsManager boundsManager)
      {
        // ok, better to plot them then!
        _lastStates = _mySolver.getProblemSpace().states();

        // see if there were bearing contributions
        final List<Long> timeStamps = new ArrayList<Long>();
        final IContributions conts = _mySolver.getContributions();
        for (final BaseContribution cont : conts)
        {
          // is it a bearing contribution?
          if (cont instanceof BearingMeasurementContribution)
          {
            final BearingMeasurementContribution bmc =
                (BearingMeasurementContribution) cont;
            final ArrayList<BMeasurement> brgs = bmc.getMeasurements();
            // ok, get the times of the cuts
            for (final BMeasurement b : brgs)
            {
              timeStamps.add(b.getDate().getTime());
            }
          }
        }

        // sort them into ascending order
        Collections.sort(timeStamps, new Comparator<Long>()
        {
          @Override
          public int compare(final Long arg0, final Long arg1)
          {
            return arg0.compareTo(arg1);
          }
        });

        // move them to long array
        _timeStamps = new long[timeStamps.size()];
        for (int i = 0; i < timeStamps.size(); i++)
        {
          _timeStamps[i] = timeStamps.get(i);
        }

        fireRepaint();
      }

      @Override
      public void stepped(final IBoundsManager boundsManager,
          final int thisStep, final int totalSteps)
      {
      }
    };

    // also listen for any other changes
    solver.addPropertyChangeListener(_globalListener);

    solver.getSolutionGenerator().addReadyListener(_gaStepListener);
    solver.getContributions().addContributionsChangedListener(
        _contributionsListener);
    solver.getBoundsManager().addConstrainSpaceListener(_constrainListener);
  }

  @Override
  public void paint(final CanvasType dest)
  {
    if (getVisible())
    {
      dest.setColor(_myColor);
      if (_lastStates != null)
      {
        if (_showLocationBounds)
          paintThese(dest, _lastStates);
      }

      dest.setColor(_myColor);
      if (_newRoutes != null)
      {
        if (_showSolutions)
          paintThese(dest, _newRoutes);
      }
    }
  }

  private void paintThese(final CanvasType dest,
      final Collection<BoundedState> states)
  {
    // keep track of the leg name of the previous leg - we
    // use it to track which leg we're in.
    String lastName = null;

    // work through the location bounds
    for (final Iterator<BoundedState> iterator = states.iterator(); iterator
        .hasNext();)
    {
      final BoundedState thisS = iterator.next();

      // we don't plot altering states
      if (!getShowAlterationStates()
          && (thisS.getStateType() == BoundedStateType.ALTERING))
        continue;

      // do some fancy tests for if users only want the
      // states that appear at leg ends
      final boolean isLastOne = !iterator.hasNext();

      // note, we don't use .equals in the next line, since
      // getMemberOf could legitimately return an null
      final boolean isDifferentLeg = thisS.getMemberOf() != lastName;
      final boolean isLegEnd = isLastOne || isDifferentLeg;

      final boolean plotThisOne = !_onlyPlotLegEnds // users want all of them
          || (_onlyPlotLegEnds && isLegEnd); // users only want
      // leg ends, and this is one

      if (plotThisOne && thisS.getLocation() != null)
      {
        // get the color for this state
        Color thisCol = thisS.getColor();

        // do we have one? if not, use the color for the whole solution
        if (thisCol == null)
          thisCol = this.getColor();

        // ok, make the color a little darker
        final Color newCol = thisCol.darker();
        dest.setColor(newCol);

        lastName = thisS.getMemberOf();

        final LocationRange theLoc = thisS.getLocation();
        final Coordinate[] pts = theLoc.getGeometry().getCoordinates();

        final int[] xPoints = new int[pts.length];
        final int[] yPoints = new int[pts.length];

        // collate polygon
        for (int i = 0; i < pts.length; i++)
        {
          final Coordinate thisC = pts[i];
          final WorldLocation thisLocation = conversions.toLocation(thisC);
          final Point pt = dest.toScreen(thisLocation);
          xPoints[i] = pt.x;
          yPoints[i] = pt.y;
        }

        // fill in the polygons, if we can
        if (dest instanceof ExtendedCanvasType)
        {
          final ExtendedCanvasType extended = (ExtendedCanvasType) dest;
          extended.semiFillPolygon(xPoints, yPoints, pts.length);
        }

        // and a border
        if (isLegEnd)
          dest.setLineStyle(CanvasType.SOLID);
        else
          dest.setLineStyle(CanvasType.DOTTED);

        dest.setLineWidth(0.0f);
        dest.drawPolygon(xPoints, yPoints, xPoints.length);
      }
    }
  }

  private void paintThese(final CanvasType dest, final CompositeRoute[] routes)
  {
    final RouteStepper painter = new DoPaint(dest, _myColor);
    walkRoute(routes, painter);
  }

  @Override
  public double rangeFrom(final WorldLocation other)
  {
    double res = -1;
    if ((_newRoutes != null) && (_newRoutes.length > 0))
    {
      final MeasureRange mr = new MeasureRange(other);
      walkRoute(_newRoutes, mr);
      res = mr.getMinRange();
    }
    return res;
  }

  public void recalculate()
  {
    _mySolver.run(true, true);
  }

  @Override
  public void removeElement(final Editable p)
  {
    // ditch it from the parent
    super.removeElement(p);

    // get the ocntribution itself
    final ContributionWrapper cw = (ContributionWrapper) p;
    final BaseContribution comp = cw.getContribution();

    // also remove it from the manager component
    _mySolver.getContributions().removeContribution(comp);
  }

  /**
   * return our legs as a series of track segments - for the bearing residuals plot
   * 
   */
  @Override
  public Enumeration<Editable> segments()
  {
    final Vector<Editable> res = new Vector<Editable>();

    // ok, loop through the legs, representing each one as a track segment
    // check if we have any solutions
    if ((_newRoutes == null) || (_newRoutes.length == 0))
    {
    }
    else
    {
      final CompositeRoute thisR = _newRoutes[0];

      // To enable the SATC solution to be viewed in the Residuals plots,
      // we wish to create points (fixes) at the same time as the bearing cuts.
      // So, when we generate a leg of data, we can provide a series of time-stamps
      // to indicate when the positions should be created at.
      final long[] thisStamps;
      if (_timeStamps.length != 0)
      {
        // ok, we have some pre-prepared, use them.
        thisStamps = _timeStamps;
      }
      else
      {
        // aaah, we don't have any. We'll use the last-states object
        // we learned about, even though some cuts may have
        // been skipped
        thisStamps = new long[_lastStates.size()];
        int ctr = 0;
        for (final BoundedState state : _lastStates)
        {
          thisStamps[ctr++] = state.getTime().getTime();
        }
      }

      // loop through the legs
      final Iterator<CoreRoute> legs = thisR.getLegs().iterator();
      while (legs.hasNext())
      {
        final CoreRoute thisLeg = legs.next();

        TrackSegment ts;

        if (thisLeg instanceof StraightRoute)
        {
          final StraightRoute straight = (StraightRoute) thisLeg;

          // ok - produce a TMA leg
          final double courseDegs = Math.toDegrees(straight.getCourse());
          final WorldSpeed speed =
              new WorldSpeed(straight.getSpeed(), WorldSpeed.M_sec);
          final WorldLocation origin =
              conversions.toLocation(straight.getStartPoint().getCoordinate());
          final HiResDate startTime =
              new HiResDate(straight.getStartTime().getTime());
          final HiResDate endTime =
              new HiResDate(straight.getEndTime().getTime());

          final AbsoluteTMASegment abs =
              new AbsoluteTMASegment(courseDegs, speed, origin, startTime,
                  endTime, thisStamps);

          // // quick check to see if we have some frequency data
          // IContributions conts = _mySolver.getContributions();
          // Iterator<BaseContribution> iter = conts.iterator();
          // while (iter.hasNext())
          // {
          // BaseContribution cont = (BaseContribution) iter.next();
          // if (cont instanceof Range1959ForecastContribution)
          // {
          // // ok, does it overlap this leg?
          // if (cont.getStartDate().before(straight.getEndTime())
          // && cont.getFinishDate().after(straight.getStartTime()))
          // {
          // // ok, retrieve the frequency
          // Range1959ForecastContribution freqC = (Range1959ForecastContribution) cont;
          // double freq = freqC.getfNought();
          // abs.setBaseFrequency(freq);
          // }
          // }
          // }

          abs.setName(straight.getName());
          ts = abs;

        }
        else
        {
          // make the segment absolute, which SATC tracks are
          ts = new TrackSegment(TrackSegment.ABSOLUTE);

          // ok, loop through the states
          final Iterator<State> iter = thisLeg.getStates().iterator();
          while (iter.hasNext())
          {
            final State state = iter.next();
            final WorldLocation theLoc =
                conversions.toLocation(state.getLocation().getCoordinate());
            final double theCourse = state.getCourse();
            final double theSpeed =
                new WorldSpeed(state.getSpeed(), WorldSpeed.M_sec)
                    .getValueIn(WorldSpeed.ft_sec / 3);
            final Fix newF =
                new Fix(new HiResDate(state.getTime().getTime()), theLoc,
                    theCourse, theSpeed);
            final FixWrapper newFW = new FixWrapper(newF)
            {
              /**
						 * 
						 */
              private static final long serialVersionUID = 1L;

              @Override
              public String getMultiLineName()
              {
                return super.getName();
              }

            };
            final Color thisCol;

            if (state.getColor() == null)
              thisCol = Color.red;
            else
              thisCol = state.getColor();
            newFW.setColor(thisCol);
            ts.addFix(newFW);
          }
        }
        res.add(ts);
      }
    }

    return res.elements();
  }

  /**
   * the Solver has been populated from XML. Now we have to scan it, to make it visible as Debrief
   * layers
   */
  public void selfScan()
  {
    final IContributions container = _mySolver.getContributions();
    final SortedSet<BaseContribution> conts = container.getContributions();
    for (final Iterator<BaseContribution> iterator = conts.iterator(); iterator
        .hasNext();)
    {
      final BaseContribution baseC = iterator.next();
      ContributionWrapper wrapped = null;

      // we don't add analysis contributions
      if (!baseC.getDataType().equals(ContributionDataType.ANALYSIS))
      {
        if (baseC instanceof BearingMeasurementContribution)
        {
          final BearingMeasurementContribution bmc =
              (BearingMeasurementContribution) baseC;
          wrapped = new BMC_Wrapper(bmc);
        }
        else if (baseC instanceof FrequencyMeasurementContribution)
        {
          final FrequencyMeasurementContribution bmc =
              (FrequencyMeasurementContribution) baseC;
          wrapped = new FMC_Wrapper(bmc);
        }
        else if (baseC instanceof StraightLegForecastContribution)
        {
          wrapped = new StraightLegWrapper(baseC);
        }
        else if (baseC instanceof CourseForecastContribution)
        {
          wrapped =
              new CourseForecastWrapper((CourseForecastContribution) baseC);
        }
        else
        {
          // we don't add analysis contributions - they're in there
          // already
          wrapped = new ContributionWrapper(baseC);
        }

        this.add(wrapped);
      }
    }
  }

  public void setColor(final Color color)
  {
    this._myColor = color;
  }

  @Override
  public final void setInterpolatePoints(final boolean val)
  {
    _interpolatePoints = val;
  }

  @Override
  public void setLayers(final Layers parent)
  {
    _myLayers = parent;
  }

  @Override
  @FireReformatted
  public void setName(final String theName)
  {
    super.setName(theName);
    _mySolver.setName(theName);

    // also trigger a refresh in maintain contributions
    // get the manager
    final ISolversManager mgr =
        SATC_Activator.getDefault().getService(ISolversManager.class, true);
    mgr.setActiveSolver(_mySolver);

  }

  public void setOnlyPlotLegEnds(final boolean onlyPlotLegEnds)
  {
    this._onlyPlotLegEnds = onlyPlotLegEnds;
  }

  public void setShowAlterationStates(final boolean showAlteringBounds)
  {
    this._showAlteringBounds = showAlteringBounds;
  }

  @FireReformatted
  public void setShowLocationConstraints(final boolean showLocationBounds)
  {
    _showLocationBounds = showLocationBounds;
  }

  public void setShowSolutions(final boolean showSolutions)
  {
    _showSolutions = showSolutions;
  }

  private void walkRoute(final CompositeRoute[] routes,
      final RouteStepper stepper)
  {
    for (int i = 0; i < routes.length; i++)
    {
      final CompositeRoute thisComposite = routes[i];
      final Iterator<CoreRoute> legs = thisComposite.getLegs().iterator();

      while (legs.hasNext())
      {
        stepper.reset();
        final CoreRoute thisRoute = legs.next();
        final ArrayList<State> states = thisRoute.getStates();
        if (states != null)
        {
          final Iterator<State> stateIter = states.iterator();
          while (stateIter.hasNext())
          {
            final State thisState = stateIter.next();
            stepper.step(thisRoute, thisState);
          }
        }

        stepper.legComplete(thisRoute);

      }

    }
    stepper.finish();
  }

  private WrappedState wrapThis(final State state)
  {
    return new WrappedState(state);
  }
}
