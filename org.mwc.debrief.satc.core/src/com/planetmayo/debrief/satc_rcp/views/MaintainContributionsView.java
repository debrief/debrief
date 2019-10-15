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
package com.planetmayo.debrief.satc_rcp.views;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.debrief.track_shift.controls.ZoneChart;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.controls.ZoneChart.ZoneChartConfig;
import org.mwc.debrief.track_shift.zig_detector.Precision;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IBarSeries;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.ATBForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.HostState;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.MDAResultsListener;
import com.planetmayo.debrief.satc.model.contributions.CompositeStraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.ContributionBuilder;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.Range1959ForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.generator.SteppingAdapter;
import com.planetmayo.debrief.satc.model.generator.impl.ga.IGASolutionsListener;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.manager.IContributionsManager;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc.model.manager.ISolversManagerListener;
import com.planetmayo.debrief.satc.model.manager.IVehicleTypesManager;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
import com.planetmayo.debrief.satc.zigdetector.LegOfData;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.planetmayo.debrief.satc_rcp.ui.UIListener;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.contributions.ATBForecastContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.BaseContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.BearingMeasurementContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.CompositeStraightLegForecastContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.CourseContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.FrequencyMeasurementContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.LocationForecastContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.RangeForecastContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.Ranging1959ContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.SpeedContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.StraightLegForecastContributionView;

import MWC.GUI.Properties.DebriefColors;
import MWC.Utilities.TextFormatting.GMTDateFormat;

/**
 * high level application flows
 *
 * @author ian
 *
 */
@SuppressWarnings("deprecation")
public class MaintainContributionsView extends ViewPart
{

  static final class TextTransfer implements ClipboardOwner
  {
    private TextTransfer()
    {
    }

    @Override
    public void lostOwnership(final Clipboard aClipboard,
        final Transferable aContents)
    {
      // do nothing
    }

    /**
     * Place a String on the clipboard, and make this class the owner of the Clipboard's contents.
     */
    public void setClipboardContents(final String aString)
    {
      final StringSelection stringSelection = new StringSelection(aString);
      final Clipboard clipboard = Toolkit.getDefaultToolkit()
          .getSystemClipboard();
      clipboard.setContents(stringSelection, this);
    }

  }

  private static final String PERFORMANCE_TITLE = "Overall score:";

  private static final String TITLE = "Maintain Contributions";
  private static final Map<Class<? extends BaseContribution>, Class<? extends BaseContributionView<?>>> CONTRIBUTION_PANELS;

  static
  {
    CONTRIBUTION_PANELS =
        new HashMap<Class<? extends BaseContribution>, Class<? extends BaseContributionView<?>>>();
    CONTRIBUTION_PANELS.put(ATBForecastContribution.class,
        ATBForecastContributionView.class);
    CONTRIBUTION_PANELS.put(BearingMeasurementContribution.class,
        BearingMeasurementContributionView.class);
    CONTRIBUTION_PANELS.put(FrequencyMeasurementContribution.class,
        FrequencyMeasurementContributionView.class);
    CONTRIBUTION_PANELS.put(CourseForecastContribution.class,
        CourseContributionView.class);
    CONTRIBUTION_PANELS.put(LocationForecastContribution.class,
        LocationForecastContributionView.class);
    CONTRIBUTION_PANELS.put(Range1959ForecastContribution.class,
        Ranging1959ContributionView.class);
    CONTRIBUTION_PANELS.put(RangeForecastContribution.class,
        RangeForecastContributionView.class);
    CONTRIBUTION_PANELS.put(SpeedForecastContribution.class,
        SpeedContributionView.class);
    CONTRIBUTION_PANELS.put(CompositeStraightLegForecastContribution.class,
        CompositeStraightLegForecastContributionView.class);
    CONTRIBUTION_PANELS.put(StraightLegForecastContribution.class,
        StraightLegForecastContributionView.class);
  }

  private static List<Zone> getZones(final IContributions conts)
  {
    final ArrayList<Zone> zones = new ArrayList<Zone>();
    final Iterator<BaseContribution> iter = conts.iterator();
    while (iter.hasNext())
    {
      final BaseContribution cont = iter.next();
      if (cont instanceof StraightLegForecastContribution)
      {
        final StraightLegForecastContribution slf =
            (StraightLegForecastContribution) cont;
        final java.awt.Color legColor = slf.getColor();
        final java.awt.Color zoneColor = legColor != null ? legColor
            : java.awt.Color.RED;
        final Zone thisZone = new Zone(slf.getStartDate().getTime(), slf
            .getFinishDate().getTime(), zoneColor);
        zones.add(thisZone);
      }
    }
    return zones;
  }

  /** UI fields */
  private DataBindingContext context;
  private Composite main;
  private Button liveConstraints;
  private Button recalculate;
  private Button cancelGeneration;
  private Button suppressCuts;

  private ComboViewer precisionsCombo;

  private Composite contList;
  private transient HashMap<BaseContribution, Color> assignedColors;

  /** Contribution -> Contribution view mappings */
  private final HashMap<BaseContribution, BaseContributionView<?>> contributionsControl =
      new HashMap<BaseContribution, BaseContributionView<?>>();

  private ISolver activeSolver;
  private ISolversManager solversManager;
  /**
   * current listeners
   */
  private Binding liveRunningBinding;
  private IContributionsChangedListener contributionsChangedListener;
  private IGASolutionsListener generateSolutionsListener;
  private ISolversManagerListener solverManagerListener;

  private IConstrainSpaceListener constrainSpaceListener;
  private Chart performanceChart;
  private final java.awt.Color[] defaultColors = new java.awt.Color[]
  {DebriefColors.RED, DebriefColors.GREEN, DebriefColors.YELLOW,
      DebriefColors.BLUE, DebriefColors.CYAN, DebriefColors.MAGENTA,
      DebriefColors.DARK_GRAY, DebriefColors.ORANGE, DebriefColors.PINK,
      DebriefColors.LIGHT_GRAY};
  private PropertyChangeListener _legListener;
  private TabItem performanceTab;
  private TabFolder graphTabs;

  private MDAResultsListener _sliceListener;

  private Action _exportBtn;

  private Color _colorBlack;

  private ZoneChart zoneChart;

  private TimeSeries measuredBearingsForZones;

  private TabItem zoneTab;

  public void addContribution(final BaseContribution contribution,
      final boolean doLayout)
  {
    // ok, create a wrapper for this
    BaseContributionView<?> panel = null;
    if (!CONTRIBUTION_PANELS.containsKey(contribution.getClass()))
    {
      return;
    }
    try
    {
      final Class<?> viewClass = CONTRIBUTION_PANELS.get(contribution
          .getClass());
      panel = (BaseContributionView<?>) viewClass.getConstructor(
          Composite.class, contribution.getClass(), IContributions.class)
          .newInstance(contList, contribution, activeSolver.getContributions());
      panel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
          | GridData.GRAB_HORIZONTAL));

      // see if we can give it a default color
      panel.setDefaultColor(colorFor(contribution));

      // now store it
      contributionsControl.put(contribution, panel);
      if (doLayout)
      {
        contList.layout();
      }

      // hmm, is this a new straight leg?
      if (contribution instanceof StraightLegForecastContribution)
      {
        // ok - listen out for period changes
        final StraightLegForecastContribution slf =
            (StraightLegForecastContribution) contribution;
        if (_legListener == null)
          _legListener = new PropertyChangeListener()
          {

            @Override
            public void propertyChange(final PropertyChangeEvent evt)
            {
              redoStraightLegs();
            }
          };
        startListeningTo(slf);

        // ok - chuck in a graph update
        redoStraightLegs();
      }
      else if (contribution instanceof BearingMeasurementContribution)
      {
        final BearingMeasurementContribution bmc =
            (BearingMeasurementContribution) contribution;
        if (_sliceListener == null)
        {
          _sliceListener =
              new BearingMeasurementContribution.MDAResultsListener()
              {

                @Override
                public void ownshipLegs(final String contName,
                    final ArrayList<BMeasurement> bearings,
                    final List<LegOfData> ownshipLegs,
                    final ArrayList<HostState> hostStates)
                {
                  // clear the domain markers, this is a new dataset
                  // legPlot.clearDomainMarkers();

                  // and show the ownship states
                  zoneChart.clearZones();
                }

                @Override
                public void sliced(final String contName,
                    final ArrayList<StraightLegForecastContribution> arrayList)
                {
                  // ok, now display the target legs
                  redoStraightLegs();
                }

                @Override
                public void startingSlice(final String contName)
                {
                  startSlicingOwnshipLegs(contName);
                }
              };
        }
        bmc.addSliceListener(_sliceListener);
      }

    }
    catch (final Exception ex)
    {
      LogFactory.getLog().error("Failed to generate panel for " + contribution);
      SATC_Activator.getDefault().getLog().log(new Status(IStatus.ERROR,
          SATC_Activator.PLUGIN_ID, ex.getMessage(), ex));
    }
  }

  private void addNewPerformanceScore(final double value,
      final List<CompositeRoute> topRoutes)
  {
    // remember each contribution's set of scores
    final HashMap<BaseContribution, HashMap<Date, Double>> stackedSeries =
        new HashMap<BaseContribution, HashMap<Date, Double>>();

    // remember the times for which we have states
    final ArrayList<Date> valueTimes = new ArrayList<Date>();

    // ok - have a look at the scores
    final Iterator<CoreRoute> legIter = topRoutes.get(0).getLegs().iterator();
    while (legIter.hasNext())
    {
      final CoreRoute route = legIter.next();
      final Iterator<State> states = route.getStates().iterator();
      while (states.hasNext())
      {
        final State state = states.next();
        final HashMap<BaseContribution, Double> scores = state.getScores();
        final Iterator<BaseContribution> contributions = scores.keySet()
            .iterator();
        while (contributions.hasNext())
        {
          final BaseContribution cont = contributions.next();

          // get the score
          final Double score = scores.get(cont);
          if (score > 0)
          {

            HashMap<Date, Double> thisSeries = stackedSeries.get(cont);
            if (thisSeries == null)
            {
              thisSeries = new HashMap<Date, Double>();
              stackedSeries.put(cont, thisSeries);
              final IBarSeries series = (IBarSeries) performanceChart
                  .getSeriesSet().createSeries(SeriesType.BAR, cont.getName());
              series.setBarColor(colorFor(cont));
              // series.enableStack(true);
            }
            thisSeries.put(state.getTime(), scores.get(cont));

            // store the time of this value
            if (!valueTimes.contains(state.getTime()))
            {
              valueTimes.add(state.getTime());
            }
          }
        }
      }
    }

    // ok, now loop through the series
    final Iterator<BaseContribution> conts = stackedSeries.keySet().iterator();
    while (conts.hasNext())
    {
      final BaseContribution cont = conts.next();
      final HashMap<Date, Double> vals = stackedSeries.get(cont);
      if (vals.size() > 0)
      {
        final IBarSeries series = (IBarSeries) performanceChart.getSeriesSet()
            .getSeries(cont.getName());

        // ok, we need to produce a value for each value time
        final double[] valArr = new double[valueTimes.size()];

        final Iterator<Date> iter2 = valueTimes.iterator();
        int ctr = 0;
        while (iter2.hasNext())
        {
          final Date date = iter2.next();
          final Double thisV = vals.get(date);
          final double res;
          if (thisV != null)
            res = thisV;
          else
            res = 0;

          valArr[ctr++] = res;
        }

        series.setYSeries(valArr);
        // series.enableStack(true);
      }
    }

    // prepare the category labels
    final String[] labels = new String[valueTimes.size()];
    final Iterator<Date> vIter = valueTimes.iterator();

    // get our date formatter ready
    final SimpleDateFormat sdf = new GMTDateFormat("HH:mm:ss");

    // determine frequency f (trim to 1)
    final int wid = performanceChart.getBounds().width;
    final int allowed = wid / 90;
    final int freq = Math.max(labels.length / allowed, 1);

    int ctr = 0;
    while (vIter.hasNext())
    {
      final Date date = vIter.next();
      final String str;
      if (ctr % freq == 0)
        str = sdf.format(date);
      else
        str = "";
      labels[ctr++] = str;
    }

    // set category labels
    performanceChart.getAxisSet().getXAxis(0).enableCategory(true);
    performanceChart.getAxisSet().getXAxis(0).setCategorySeries(labels);

    final ISeries[] series = performanceChart.getSeriesSet().getSeries();
    if (series.length == 2 && series[0] instanceof IBarSeries
        && series[1] instanceof IBarSeries)
    {
      performanceChart.getLegend().setVisible(true);
      performanceChart.getLegend().setPosition(SWT.RIGHT);
      final IBarSeries barSeries1 = (IBarSeries) series[0];
      final IBarSeries barSeries2 = (IBarSeries) series[1];
      // enable stack series
      barSeries1.enableStack(false);
      barSeries2.enableStack(false);
      barSeries1.enableStack(true);
      barSeries2.enableStack(true);

    }

    // and resize the axes
    performanceChart.getAxisSet().adjustRange();

    final String perfString;
    if (value > 200d)
      perfString = "Unachievable";
    else
      perfString = PERFORMANCE_TITLE + (int) value;

    performanceChart.getTitle().setText(perfString);

    //
    performanceChart.redraw();
  }

  private void clearPerformanceGraph()
  {
    // hmm, have we already ditched?
    if (performanceChart.isDisposed())
      return;

    final ISeries[] sets = performanceChart.getSeriesSet().getSeries();
    for (int i = 0; i < sets.length; i++)
    {
      final ISeries iSeries = sets[i];
      performanceChart.getSeriesSet().deleteSeries(iSeries.getId());
    }

    // ySeries.setYSeries(newYVals);

    performanceChart.getAxisSet().getXAxis(0).adjustRange();
    performanceChart.getAxisSet().getYAxis(0).adjustRange();
    performanceChart.getAxisSet().getYAxis(0).enableLogScale(true);
    performanceChart.redraw();

  }

  private void clearZoneGraph()
  {
    if (zoneChart == null || zoneChart.isDisposed())
      return;

    zoneChart.clearZones();

    // also clear the bearing timeseries
    measuredBearingsForZones.clear();
  }

  private Color colorFor(final BaseContribution contribution)
  {

    if (assignedColors == null)
    {
      assignedColors = new HashMap<BaseContribution, Color>();
    }

    // have we already assigned this one?
    Color res = assignedColors.get(contribution);

    if (res == null)
    {
      final int index = assignedColors.size() % defaultColors.length;
      final java.awt.Color newCol = defaultColors[index];
      res = new Color(Display.getDefault(), newCol.getRed(), newCol.getGreen(),
          newCol.getBlue());
      assignedColors.put(contribution, res);
    }

    return res;

  }

  @Override
  public void createPartControl(final Composite parent)
  {
    context = new DataBindingContext();
    final IContributionsManager contributionsManager = SATC_Activator
        .getDefault().getService(IContributionsManager.class, true);
    final IVehicleTypesManager vehicleManager = SATC_Activator.getDefault()
        .getService(IVehicleTypesManager.class, true);
    solversManager = SATC_Activator.getDefault().getService(
        ISolversManager.class, true);

    initUI(parent);
    populateContributionList(contributionsManager.getAvailableContributions());
    populatePrecisionsList(Precision.values());
    populateVehicleTypesList(vehicleManager.getAllTypes());

    initListeners(parent);
    solversManager.addSolversManagerListener(solverManagerListener);

    // also, set the help context
    PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
        "org.mwc.debrief.help.SATC");

    setActiveSolver(solversManager.getActiveSolver());

  }

  @Override
  public void dispose()
  {
    context.dispose();
    setActiveSolver(null);
    solversManager.removeSolverManagerListener(solverManagerListener);

    // ditch the colors
    if (assignedColors != null)
    {
      final Iterator<Color> cIter = assignedColors.values().iterator();
      while (cIter.hasNext())
      {
        final org.eclipse.swt.graphics.Color entry = cIter.next();
        entry.dispose();
      }
      assignedColors = null;
    }

    // and our SWT black shade
    if (!_colorBlack.isDisposed())
      _colorBlack.dispose();

    super.dispose();
  }

  /**
   * copy the SATC scenario to the clipboard
   *
   */
  protected void exportSATC()
  {
    // - ok, really we just export the state & bearing data
    if (activeSolver != null)
    {
      final StringBuffer res = new StringBuffer();
      final String newLine = System.getProperty("line.separator");
      final SimpleDateFormat sdf = new GMTDateFormat("yyyy/MMM/dd HH:mm:ss");
      final Date dateLead = new Date(100, 7, 7);

      final Iterator<BaseContribution> conts = activeSolver.getContributions()
          .iterator();
      while (conts.hasNext())
      {
        final BaseContribution baseC = conts.next();
        if (baseC instanceof BearingMeasurementContribution)
        {
          final BearingMeasurementContribution bmc =
              (BearingMeasurementContribution) baseC;

          // ok - sort out the date offset
          final Date startDate = bmc.getStartDate();
          final long offset = startDate.getTime() - dateLead.getTime();

          // get ready for the offset
          Point2D origin = null;

          // get ready to calculate offsetes
          final GeodeticCalculator calc = GeoSupport.createCalculator();

          // ok, first the states
          res.append("//X, Y, Time, Course Degs, Speed Kts" + newLine);
          final Iterator<HostState> states = bmc.getHostState().iterator();
          while (states.hasNext())
          {
            final BearingMeasurementContribution.HostState hostState = states
                .next();

            // sort out the X,Y offset
            double x, y;
            if (origin == null)
            {
              x = 0;
              y = 0;
              origin = new Point2D.Double(hostState.dLong, hostState.dLat);
            }
            else
            {
              // ok, calc a new XY, from the origin
              final Point2D.Double thisP = new Point2D.Double(hostState.dLong,
                  hostState.dLat);
              calc.setStartingGeographicPoint(origin);
              calc.setDestinationGeographicPoint(thisP);
              final double angle = calc.getAzimuth();
              final double dist = calc.getOrthodromicDistance();

              // and the new x,y coords
              x = Math.sin(Math.toRadians(angle)) * dist;
              y = Math.cos(Math.toRadians(angle)) * dist;

            }

            res.append(x + ", " + y + ", " + sdf.format(new Date(hostState.time
                - offset)) + "," + hostState.courseDegs + ","
                + hostState.speedKts + newLine);
          }

          // now the cuts
          res.append("//Time, Bearing Degs" + newLine);
          final Iterator<BMeasurement> cuts = bmc.getMeasurements().iterator();
          while (cuts.hasNext())
          {
            final BearingMeasurementContribution.BMeasurement cut = cuts.next();
            res.append(sdf.format(new Date(cut.getDate().getTime() - offset))
                + "," + Math.toDegrees(cut.getBearingRads()) + newLine);
          }
        }
      }

      // hmm, did we find anything
      if (res.length() > 0)
      {
        // ok, put it on the clipboard.
        new TextTransfer().setClipboardContents(res.toString());
      }
    }
  }

  private void fillAnalystContributionsGroup(final Composite parent)
  {
    final GridLayout layout = new GridLayout(4, false);
    layout.marginHeight = 2;
    layout.marginWidth = 2;
    final Composite header = UIUtils.createEmptyComposite(parent, layout,
        new GridData(GridData.FILL_HORIZONTAL));
    UIUtils.createSpacer(header, new GridData(50, SWT.DEFAULT));
    final Composite headerNested = UIUtils.createEmptyComposite(header, UIUtils
        .createGridLayoutWithoutMargins(4, true), new GridData(
            GridData.FILL_HORIZONTAL));
    UIUtils.createLabel(headerNested, SWT.CENTER, "Active", new GridData(
        GridData.FILL_HORIZONTAL));
    UIUtils.createLabel(headerNested, SWT.CENTER, "Hard constraints",
        new GridData(GridData.FILL_HORIZONTAL));
    UIUtils.createLabel(headerNested, SWT.CENTER, "Estimate", new GridData(
        GridData.FILL_HORIZONTAL));
    UIUtils.createLabel(headerNested, SWT.CENTER, "Weighting", new GridData(
        GridData.HORIZONTAL_ALIGN_END));
    UIUtils.createLabel(header, SWT.CENTER, "Delete", new GridData(
        GridData.HORIZONTAL_ALIGN_END));
    UIUtils.createSpacer(header, new GridData(20, SWT.DEFAULT));
  }

  @Override
  protected void finalize() throws Throwable
  {

    // clear our listeners
    final Iterator<BaseContribution> conts = activeSolver.getContributions()
        .iterator();
    while (conts.hasNext())
    {
      final BaseContribution contribution = conts.next();
      // aaah, is it a straight leg?
      if (contribution instanceof StraightLegForecastContribution)
      {
        stopListeningTo(contribution);
      }
      else if (contribution instanceof BearingMeasurementContribution)
      {
        final BearingMeasurementContribution bmc =
            (BearingMeasurementContribution) contribution;
        bmc.removeSliceListener(_sliceListener);
      }
    }

    // let the parent shut down
    super.finalize();
  }

  private void initAddContributionGroup(final Composite parent)
  {
    // GridData gridData = new GridData();
    // gridData.horizontalAlignment = SWT.FILL;
    // gridData.grabExcessHorizontalSpace = true;
    //
    // Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
    // FillLayout fillLayout = new FillLayout();
    // fillLayout.marginWidth = 5;
    // fillLayout.marginHeight = 5;
    // group.setLayout(fillLayout);
    // group.setLayoutData(gridData);
    // group.setText("New Contribution");
    //
    // addContributionMenu = new Menu(group);
    // final ToolBar toolBar = new ToolBar(group, SWT.NONE);
    // toolBar.setBounds(50, 50, 50, 50);
    // addContributionButton = new ToolItem(toolBar, SWT.DROP_DOWN);
    // addContributionButton.setText("Add...");
    // addContributionButton.addListener(SWT.Selection, new Listener()
    // {
    // @Override
    // public void handleEvent(Event event)
    // {
    // if (event.detail == SWT.ARROW)
    // {
    // Rectangle rect = addContributionButton.getBounds();
    // Point pt = new Point(rect.x, rect.y + rect.height);
    // pt = toolBar.toDisplay(pt);
    // addContributionMenu.setLocation(pt.x, pt.y);
    // addContributionMenu.setVisible(true);
    // }
    // }
    // });

  }

  private void initAnalystContributionsGroup(final Composite parent)
  {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.verticalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;

    final Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
    group.setLayout(new GridLayout(1, false));
    group.setLayoutData(gridData);
    group.setText("Analyst Contributions");
    fillAnalystContributionsGroup(group);

    final ScrolledComposite scrolled = new ScrolledComposite(group, SWT.V_SCROLL
        | SWT.H_SCROLL);
    scrolled.setLayoutData(new GridData(GridData.FILL_BOTH));
    contList = UIUtils.createScrolledBody(scrolled, SWT.NONE);
    contList.setLayout(new GridLayout(1, false));

    scrolled.addListener(SWT.Resize, new Listener()
    {

      @Override
      public void handleEvent(final Event e)
      {
        scrolled.setMinSize(contList.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      }
    });
    scrolled.setAlwaysShowScrollBars(true);
    scrolled.setContent(contList);
    scrolled.setMinSize(contList.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    scrolled.setExpandHorizontal(true);
    scrolled.setExpandVertical(true);
  }

  private void initGraphTabs(final Composite parent)
  {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.heightHint = 200;

    graphTabs = new TabFolder(parent, SWT.BORDER);
    final FillLayout fillLayout = new FillLayout();
    fillLayout.marginWidth = 5;
    fillLayout.marginHeight = 5;
    // tabs.setLayout(fillLayout);
    graphTabs.setLayoutData(gridData);

    // legTab = new TabItem(graphTabs, SWT.NONE);
    // legTab.setText("Ownship && Target Legs");
    // legGraphComposite = initLegGraph(graphTabs);
    // legTab.setControl(legGraphComposite);

    performanceTab = new TabItem(graphTabs, SWT.NONE);
    performanceTab.setText("Performance");
    final Group perfG2 = initPerformanceGraph(graphTabs);
    performanceTab.setControl(perfG2);

    // Zone Test
    zoneTab = new TabItem(graphTabs, SWT.NONE);
    zoneTab.setText("Straight Legs");

    final ZoneChart.ColorProvider blueProvider = new ZoneChart.ColorProvider()
    {
      @Override
      public java.awt.Color getZoneColor()
      {
        return java.awt.Color.blue;
      }
    };
    final ZoneChartConfig zoneConfig = new ZoneChart.ZoneChartConfig(
        "Target Legs", "Course", DebriefColors.BLUE, true);

    // for the new API signature we need to put the data into JFReeChart TimeSeries
    // objects;
    measuredBearingsForZones = new TimeSeries("Measured Bearings");
    final TimeSeriesCollection[] otherSeries = new TimeSeriesCollection[]
    {};

    // build up the zones
    final ZoneChart.Zone[] zones = new Zone[]
    {};

    zoneChart = ZoneChart.create(zoneConfig, null, graphTabs, zones,
        measuredBearingsForZones, otherSeries, null, blueProvider, null, null,
        null, null);

    zoneChart.addZoneListener(new ZoneChart.ZoneAdapter()
    {

      @Override
      public void added(final Zone zone)
      {
        // ok, create the new zone
        final StraightLegForecastContribution newS =
            new StraightLegForecastContribution();
        newS.setStartDate(new Date(zone.getStart()));
        newS.setFinishDate(new Date(zone.getEnd()));
        newS.setName(new Date(zone.getStart()).toGMTString());
        activeSolver.getContributions().addContribution(newS);
      }

      @Override
      public void deleted(final Zone zone)
      {
        final long tStart = zone.getStart();
        final long tEnd = zone.getEnd();
        // ok, find the matching zone
        if (activeSolver != null)
        {
          final Iterator<BaseContribution> cIter = activeSolver
              .getContributions().iterator();
          while (cIter.hasNext())
          {
            final BaseContribution base = cIter.next();
            if (base instanceof StraightLegForecastContribution)
            {
              final StraightLegForecastContribution st =
                  (StraightLegForecastContribution) base;
              if (st.getStartDate().getTime() == tStart || st.getFinishDate()
                  .getTime() == tEnd)
              {
                // ok, delete this contribution
                activeSolver.getContributions().removeContribution(st);
                return;
              }
            }
          }
        }
        System.err.println("Failed to find zone on deletion for:" + zone);
      }

      @Override
      public void moved(final Zone zone)
      {
        System.out.println(
            "MaintainContributionsView.initGraphTabs(...).new ZoneAdapter() {...}.moved()");
      }

      @Override
      public void resized(final Zone zone)
      {
        final long tStart = zone.getStart();
        final long tEnd = zone.getEnd();
        // ok, find the matching zone
        if (activeSolver != null)
        {
          final Iterator<BaseContribution> cIter = activeSolver
              .getContributions().iterator();
          while (cIter.hasNext())
          {
            final BaseContribution base = cIter.next();
            if (base instanceof StraightLegForecastContribution)
            {
              final StraightLegForecastContribution st =
                  (StraightLegForecastContribution) base;
              if (st.getStartDate().getTime() == tStart)
              {
                // ok, update the end value
                st.setFinishDate(new Date(tEnd));
                return;
              }
              else if (st.getFinishDate().getTime() == tEnd)
              {
                st.setStartDate(new Date(tStart));
                return;
              }
            }
          }
        }
        System.err.println("Failed to find zone on drag for:" + zone);
      }
    });

    zoneTab.setControl(zoneChart);
  }

  private void initListeners(final Composite parent)
  {
    contributionsChangedListener = UIListener.wrap(parent.getDisplay(),
        IContributionsChangedListener.class, new IContributionsChangedListener()
        {

          @Override
          public void added(final BaseContribution contribution)
          {
            addContribution(contribution, true);
          }

          @Override
          public void modified()
          {
          }

          @Override
          public void removed(final BaseContribution contribution)
          {
            removeContribution(contribution, true);
          }
        });
    generateSolutionsListener = UIListener.wrap(parent.getDisplay(),
        IGASolutionsListener.class, new SteppingAdapter()
        {
          Control focused = null;

          @Override
          public void finishedGeneration(final Throwable error)
          {
            UIUtils.setEnabled(parent, true);
            cancelGeneration.setVisible(false);

            // we've encountered an instance during file-load where
            // focused
            // is
            // null, better check it
            if (focused != null)
              focused.setFocus();
          }

          @Override
          public void iterationComputed(final List<CompositeRoute> topRoutes,
              final double topScore)
          {
            addNewPerformanceScore(topScore, topRoutes);
          }

          @Override
          public void startingGeneration()
          {
            focused = parent.getDisplay().getFocusControl();
            UIUtils.setEnabled(parent, false);
            cancelGeneration.setVisible(true);
            cancelGeneration.setEnabled(true);

            // ok, clear the graph
            clearPerformanceGraph();
          }
        });
    solverManagerListener = UIListener.wrap(parent.getDisplay(),
        ISolversManagerListener.class, new ISolversManagerListener()
        {

          @Override
          public void activeSolverChanged(final ISolver activeSolver)
          {
            setActiveSolver(activeSolver);
          }

          @Override
          public void solverCreated(final ISolver solver)
          {

          }
        });

    constrainSpaceListener = UIListener.wrap(parent.getDisplay(),
        IConstrainSpaceListener.class, new IConstrainSpaceListener()
        {

          @Override
          public void error(final IBoundsManager boundsManager,
              final IncompatibleStateException ex)
          {
          }

          @Override
          public void restarted(final IBoundsManager boundsManager)
          {
          }

          @Override
          public void statesBounded(final IBoundsManager boundsManager)
          {
            // minimum steps to get the contributions list to redraw
            contList.setSize(0, 0);
          }

          @Override
          public void stepped(final IBoundsManager boundsManager,
              final int thisStep, final int totalSteps)
          {
          }
        });
  }

  private Group initPerformanceGraph(final Composite parent)
  {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.heightHint = 200;

    final Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
    final FillLayout fillLayout = new FillLayout();
    fillLayout.marginWidth = 5;
    fillLayout.marginHeight = 5;
    group.setLayout(fillLayout);
    group.setLayoutData(gridData);
    // group.setText("Performance");

    // we need the color black several times
    _colorBlack = new Color(Display.getCurrent(), 0, 0, 0);

    // generate the chart
    performanceChart = new Chart(group, SWT.NONE);

    // format the chart
    performanceChart.getLegend().setVisible(false);
    performanceChart.getTitle().setVisible(true);
    performanceChart.setForeground(_colorBlack);
    performanceChart.getTitle().setText(PERFORMANCE_TITLE + " Pending");
    performanceChart.getTitle().setForeground(_colorBlack);

    // now give the chart our data series
    // ok, now for the x axis
    final IAxis xAxis = performanceChart.getAxisSet().getXAxis(0);
    xAxis.getTitle().setVisible(false);
    xAxis.adjustRange();
    xAxis.getTick().setForeground(_colorBlack);

    // and the y axis
    final IAxis yAxis = performanceChart.getAxisSet().getYAxis(0);
    yAxis.adjustRange();
    yAxis.enableLogScale(true);
    yAxis.getTick().setForeground(_colorBlack);
    yAxis.getTitle().setForeground(_colorBlack);
    yAxis.getTitle().setText("Weighted error");

    return group;
  }

  private void initPreferencesGroup(final Composite parent)
  {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;

    final Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
    final GridLayout layout = new GridLayout(1, false);
    group.setLayoutData(gridData);
    group.setLayout(layout);
    group.setText("Preferences");

    final ScrolledComposite scrolled = new ScrolledComposite(group,
        SWT.H_SCROLL);
    scrolled.setLayoutData(new GridData(GridData.FILL_BOTH));
    final Composite preferencesComposite = UIUtils.createScrolledBody(scrolled,
        SWT.NONE);
    preferencesComposite.setLayout(new GridLayout(6, false));

    scrolled.addListener(SWT.Resize, new Listener()
    {

      @Override
      public void handleEvent(final Event e)
      {
        scrolled.setMinSize(preferencesComposite.computeSize(SWT.DEFAULT,
            SWT.DEFAULT));
      }
    });
    scrolled.setAlwaysShowScrollBars(true);
    scrolled.setContent(preferencesComposite);
    scrolled.setMinSize(preferencesComposite.computeSize(SWT.DEFAULT,
        SWT.DEFAULT));
    scrolled.setExpandHorizontal(true);
    scrolled.setExpandVertical(true);

    liveConstraints = new Button(preferencesComposite, SWT.TOGGLE);
    liveConstraints.setText("Auto-Recalc of Constraints");
    liveConstraints.setEnabled(false);
    liveConstraints.setLayoutData(new GridData(
        GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));

    recalculate = new Button(preferencesComposite, SWT.DEFAULT);
    recalculate.setText("Calculate Solution");
    recalculate.setEnabled(false);
    recalculate.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        if (activeSolver != null)
        {
          // ok - make sure the performance tab is open
          graphTabs.setSelection(performanceTab);

          activeSolver.run(true, true);
          main.setSize(0, 0);
          main.getParent().layout(true, true);
        }
      }
    });
    recalculate.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
        | GridData.VERTICAL_ALIGN_CENTER));

    cancelGeneration = new Button(preferencesComposite, SWT.PUSH);
    cancelGeneration.setText("Cancel");
    cancelGeneration.setVisible(false);
    cancelGeneration.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        if (activeSolver != null)
        {
          activeSolver.cancel();
        }
      }
    });

    suppressCuts = new Button(preferencesComposite, SWT.CHECK);
    suppressCuts.setText("Suppress Cuts");
    suppressCuts.setVisible(true);
    suppressCuts.setEnabled(false);
    suppressCuts.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        if (activeSolver != null)
        {
          final boolean doSuppress = suppressCuts.getSelection();
          activeSolver.setAutoSuppress(doSuppress);
        }
      }
    });

    final Composite precisionPanel = new Composite(preferencesComposite,
        SWT.NONE);
    precisionPanel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END
        | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));

    final GridLayout precisionLayout = new GridLayout(2, false);
    precisionLayout.horizontalSpacing = 5;
    precisionPanel.setLayout(precisionLayout);

    final Label precisionLabel = new Label(precisionPanel, SWT.NONE);
    precisionLabel.setText("Precision:");
    precisionLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));

    precisionsCombo = new ComboViewer(precisionPanel);
    precisionsCombo.getCombo().setEnabled(false);
    precisionsCombo.setContentProvider(new ArrayContentProvider());
    precisionsCombo.setLabelProvider(new LabelProvider()
    {

      @Override
      public String getText(final Object element)
      {
        return ((Precision) element).getLabel();
      }
    });
    precisionsCombo.addSelectionChangedListener(new ISelectionChangedListener()
    {

      @Override
      public void selectionChanged(final SelectionChangedEvent event)
      {
        final ISelection sel = precisionsCombo.getSelection();
        final IStructuredSelection cSel = (IStructuredSelection) sel;
        final Precision precision = (Precision) cSel.getFirstElement();
        if (activeSolver != null)
        {
          activeSolver.setPrecision(precision);
        }
      }
    });
  }

  private void initUI(final Composite parent)
  {
    parent.setLayout(new FillLayout());
    final SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
    sashForm.SASH_WIDTH = 15;
    sashForm.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));

    main = new Composite(sashForm, SWT.NONE);
    final GridLayout gridLayout = new GridLayout(1, true);
    gridLayout.verticalSpacing = 2;
    gridLayout.marginLeft = 5;
    gridLayout.marginRight = 5;
    main.setLayout(gridLayout);

    initPreferencesGroup(main);
    initVehicleGroup(main);
    initAnalystContributionsGroup(main);
    initAddContributionGroup(main);

    final Composite lowerSection = new Composite(sashForm, SWT.NONE);
    lowerSection.setLayout(new FillLayout());

    // ok - the next section needs to be in a sash - so we can resize it
    initGraphTabs(lowerSection);

    // set the relative sizes in the sash
    sashForm.setWeights(new int[]
    {3, 1});

    // also sort out the header controls
    final IActionBars bars = getViewSite().getActionBars();
    final IToolBarManager manager = bars.getToolBarManager();
    manager.add(SATC_Activator.createOpenHelpAction("org.mwc.debrief.help.SATC",
        null, this));

    _exportBtn = new Action("Export SATC dataset", IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void runWithEvent(final Event event)
      {
        exportSATC();
      }
    };
    _exportBtn.setToolTipText("Export SATC scenario to clipboard");
    _exportBtn.setImageDescriptor(SATC_Activator.getImageDescriptor(
        "icons/export.png"));
    manager.add(_exportBtn);

  }

  private void initVehicleGroup(final Composite parent)
  {
  }

  public void populateContributionList(final List<ContributionBuilder> items)
  {
  }

  public void populatePrecisionsList(final Precision[] precisions)
  {
    precisionsCombo.setInput(precisions);
    precisionsCombo.setSelection(new StructuredSelection(precisions[0]));
  }

  public void populateVehicleTypesList(final List<VehicleType> vehicles)
  {
  }

  protected void redoStraightLegs()
  {
    // ok, clear any leg markers
    if (zoneTab != null)
    {
      if (!graphTabs.isDisposed())
        graphTabs.setSelection(zoneTab);
      updateZoneChart(activeSolver.getContributions());
    }
  }

  protected void removeContribution(final BaseContribution contribution,
      final boolean doLayout)
  {
    final BaseContributionView<?> panel = contributionsControl.get(
        contribution);
    if (panel != null)
    {
      panel.dispose();
      contributionsControl.remove(contribution);
      if (doLayout)
      {
        contList.layout();
      }
    }

    // aaah, is it a straight leg?
    if (contribution instanceof StraightLegForecastContribution)
    {
      stopListeningTo(contribution);

      // ok, better update the legs too. AAh, not if the form is closing
      if (doLayout)
      {
        redoStraightLegs();
      }
    }
    else if (contribution instanceof BearingMeasurementContribution)
    {
      final BearingMeasurementContribution bmc =
          (BearingMeasurementContribution) contribution;
      bmc.removeSliceListener(_sliceListener);
    }
  }

  @SuppressWarnings("unchecked")
  public void setActiveSolver(final ISolver solver)
  {

    // just double check that we aren't already looking at this solver
    if (solver != activeSolver)
    {

      // other UI mgt
      if (activeSolver != null)
      {
        // cancel listeners
        activeSolver.getContributions().removeContributionsChangedListener(
            contributionsChangedListener);
        activeSolver.getSolutionGenerator().removeReadyListener(
            generateSolutionsListener);
        activeSolver.getBoundsManager().removeConstrainSpaceListener(
            constrainSpaceListener);

        liveRunningBinding.dispose();
      }

      // drop the contributions
      final List<BaseContribution> contributions =
          new ArrayList<BaseContribution>(contributionsControl.keySet());
      for (final BaseContribution contribution : contributions)
      {
        removeContribution(contribution, false);
      }
      if (!contList.isDisposed())
      {
        contList.layout();
      }

      // clear the charts - just in case
      clearPerformanceGraph();
      clearZoneGraph();
      // clearLegGraph();

      activeSolver = solver;
      final boolean hasSolver = activeSolver != null;
      if (hasSolver)
      {
        activeSolver.getContributions().addContributionsChangedListener(
            contributionsChangedListener);
        activeSolver.getSolutionGenerator().addReadyListener(
            generateSolutionsListener);
        activeSolver.getBoundsManager().addConstrainSpaceListener(
            constrainSpaceListener);

        for (final BaseContribution contribution : activeSolver
            .getContributions())
        {
          addContribution(contribution, false);
        }
        contList.layout();

        // vehiclesCombo.setSelection(new
        // StructuredSelection(activeSolver
        // .getVehicleType()));
        precisionsCombo.setSelection(new StructuredSelection(activeSolver
            .getPrecision()));
        suppressCuts.setSelection(activeSolver.getAutoSuppress());
        liveRunningBinding = context.bindValue(WidgetProperties.selection()
            .observe(liveConstraints), BeansObservables.observeValue(
                activeSolver, ISolver.LIVE_RUNNING));
        setPartName(TITLE + " - " + activeSolver.getName());

        // also update the zone chart
        updateZoneChart(activeSolver.getContributions());
      }
      else
      {
        setPartName(TITLE);
      }
      if (!contList.isDisposed())
      {
        precisionsCombo.getCombo().setEnabled(hasSolver);
        liveConstraints.setEnabled(hasSolver);
        recalculate.setEnabled(hasSolver);
        performanceChart.setEnabled(hasSolver);
        suppressCuts.setEnabled(hasSolver);

        // vehiclesCombo.getCombo().setEnabled(hasSolver);
        // addContributionButton.setEnabled(hasSolver);
      }
    }
  }

  @Override
  public void setFocus()
  {
  }

  private void startListeningTo(final StraightLegForecastContribution slf)
  {
    slf.addPropertyChangeListener(BaseContribution.START_DATE, _legListener);
    slf.addPropertyChangeListener(BaseContribution.FINISH_DATE, _legListener);
    slf.addPropertyChangeListener(BaseContribution.NAME, _legListener);
    slf.addPropertyChangeListener(BaseContribution.ACTIVE, _legListener);
  }

  protected void startSlicingOwnshipLegs(final String contName)
  {
    // hey, let's also ditch any straight leg forecasts
    final Iterator<BaseContribution> conts = activeSolver.getContributions()
        .iterator();
    final ArrayList<BaseContribution> toRemove =
        new ArrayList<BaseContribution>();
    while (conts.hasNext())
    {
      final BaseContribution baseC = conts.next();
      if (baseC.isActive())
        if (baseC instanceof StraightLegForecastContribution)
        {
          toRemove.add(baseC);
        }
    }

    // did we find any?
    if (!toRemove.isEmpty())
    {
      final Iterator<BaseContribution> iter = toRemove.iterator();
      while (iter.hasNext())
      {
        final BaseContribution baseContribution = iter.next();
        activeSolver.getContributions().removeContribution(baseContribution);
      }
    }

  }

  private void stopListeningTo(final BaseContribution slf)
  {
    slf.removePropertyChangeListener(BaseContribution.ACTIVE, _legListener);
    slf.removePropertyChangeListener(BaseContribution.START_DATE, _legListener);
    slf.removePropertyChangeListener(BaseContribution.FINISH_DATE,
        _legListener);
    slf.removePropertyChangeListener(BaseContribution.NAME, _legListener);
  }

  private void updateZoneBearings(final IContributions contributions,
      final TimeSeries measuredBearingsForZones2)
  {
    // clear out any existing datasets
    measuredBearingsForZones.clear();

    final Iterator<BaseContribution> iter = contributions.iterator();
    while (iter.hasNext())
    {
      final BaseContribution cont = iter.next();
      if (cont instanceof BearingMeasurementContribution)
      {
        final BearingMeasurementContribution bearings =
            (BearingMeasurementContribution) cont;
        final ArrayList<BMeasurement> measurements = bearings.getMeasurements();
        final Iterator<BMeasurement> mIter = measurements.iterator();
        while (mIter.hasNext())
        {
          final BMeasurement bm = mIter.next();
          double degs = MWC.Algorithms.Conversions.Rads2Degs(bm
              .getBearingRads());
          final long time = bm.getDate().getTime();
          if (degs < 0)
          {
            degs += 360;
          }
          measuredBearingsForZones.addOrUpdate(new TimeSeriesDataItem(
              new FixedMillisecond(time), degs));
        }
      }
    }
  }

  private void updateZoneChart(final IContributions contributions)
  {
    // ok, we need the list of straight legs
    final List<Zone> zones = getZones(contributions);

    // ok, now give these to the ZoneChart
    zoneChart.clearZones();
    zoneChart.setZones(zones);

    // we also need the list of bearings
    updateZoneBearings(contributions, measuredBearingsForZones);
  }

}
