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
 * mock class to test high level application flows
 * 
 * @author ian
 * 
 */
@SuppressWarnings("deprecation")
public class MaintainContributionsView extends ViewPart
{

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

  /** UI fields */
  private DataBindingContext context;
  private Composite main;
  private Button liveConstraints;
  private Button recalculate;
  private Button cancelGeneration;
  private Button suppressCuts;
  private ComboViewer precisionsCombo;
  // private ComboViewer vehiclesCombo;
  private Composite contList;
  // private Menu addContributionMenu;
  // private ToolItem addContributionButton;

  private transient HashMap<BaseContribution, Color> assignedColors;

  /** Contribution -> Contribution view mappings */
  private HashMap<BaseContribution, BaseContributionView<?>> contributionsControl =
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
  {DebriefColors.RED,
      DebriefColors.GREEN,
      DebriefColors.YELLOW,
      DebriefColors.BLUE,
      DebriefColors.CYAN,
      DebriefColors.MAGENTA,
      DebriefColors.DARK_GRAY,
      DebriefColors.ORANGE,
      DebriefColors.PINK,
      DebriefColors.LIGHT_GRAY};
//  private XYPlot legPlot;
//  private Composite legGraphComposite;
  private PropertyChangeListener _legListener;
  private TabItem performanceTab;
//  private TabItem legTab;
  private TabFolder graphTabs;
  private MDAResultsListener _sliceListener;
  private Action _exportBtn;

  private Color _colorBlack;

  private ZoneChart zoneChart;

  private TimeSeries measuredBearingsForZones;

  private TabItem zoneTab;

  @Override
  public void createPartControl(final Composite parent)
  {
    context = new DataBindingContext();
    IContributionsManager contributionsManager =
        SATC_Activator.getDefault().getService(IContributionsManager.class,
            true);
    IVehicleTypesManager vehicleManager =
        SATC_Activator.getDefault()
            .getService(IVehicleTypesManager.class, true);
    solversManager =
        SATC_Activator.getDefault().getService(ISolversManager.class, true);

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
      Iterator<Color> cIter = assignedColors.values().iterator();
      while (cIter.hasNext())
      {
        org.eclipse.swt.graphics.Color entry = cIter.next();
        entry.dispose();
      }
      assignedColors = null;
    }

    // and our SWT black shade
    if (!_colorBlack.isDisposed())
      _colorBlack.dispose();

    super.dispose();
  }

  @Override
  public void setFocus()
  {
  }

  private void fillAnalystContributionsGroup(Composite parent)
  {
    GridLayout layout = new GridLayout(4, false);
    layout.marginHeight = 2;
    layout.marginWidth = 2;
    Composite header =
        UIUtils.createEmptyComposite(parent, layout, new GridData(
            GridData.FILL_HORIZONTAL));
    UIUtils.createSpacer(header, new GridData(50, SWT.DEFAULT));
    Composite headerNested =
        UIUtils.createEmptyComposite(header, UIUtils
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

  private void initAddContributionGroup(Composite parent)
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

  private void initAnalystContributionsGroup(Composite parent)
  {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.verticalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;

    Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
    group.setLayout(new GridLayout(1, false));
    group.setLayoutData(gridData);
    group.setText("Analyst Contributions");
    fillAnalystContributionsGroup(group);

    final ScrolledComposite scrolled =
        new ScrolledComposite(group, SWT.V_SCROLL | SWT.H_SCROLL);
    scrolled.setLayoutData(new GridData(GridData.FILL_BOTH));
    contList = UIUtils.createScrolledBody(scrolled, SWT.NONE);
    contList.setLayout(new GridLayout(1, false));

    scrolled.addListener(SWT.Resize, new Listener()
    {

      @Override
      public void handleEvent(Event e)
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

  private void initPreferencesGroup(Composite parent)
  {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;

    Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
    GridLayout layout = new GridLayout(1, false);
    group.setLayoutData(gridData);
    group.setLayout(layout);
    group.setText("Preferences");

    final ScrolledComposite scrolled =
        new ScrolledComposite(group, SWT.H_SCROLL);
    scrolled.setLayoutData(new GridData(GridData.FILL_BOTH));
    final Composite preferencesComposite =
        UIUtils.createScrolledBody(scrolled, SWT.NONE);
    preferencesComposite.setLayout(new GridLayout(6, false));

    scrolled.addListener(SWT.Resize, new Listener()
    {

      @Override
      public void handleEvent(Event e)
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
      public void widgetSelected(SelectionEvent e)
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
      public void widgetSelected(SelectionEvent e)
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
      public void widgetSelected(SelectionEvent e)
      {
        if (activeSolver != null)
        {
          boolean doSuppress = suppressCuts.getSelection();
          activeSolver.setAutoSuppress(doSuppress);
        }
      }
    });

    Composite precisionPanel = new Composite(preferencesComposite, SWT.NONE);
    precisionPanel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END
        | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));

    GridLayout precisionLayout = new GridLayout(2, false);
    precisionLayout.horizontalSpacing = 5;
    precisionPanel.setLayout(precisionLayout);

    Label precisionLabel = new Label(precisionPanel, SWT.NONE);
    precisionLabel.setText("Precision:");
    precisionLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));

    precisionsCombo = new ComboViewer(precisionPanel);
    precisionsCombo.getCombo().setEnabled(false);
    precisionsCombo.setContentProvider(new ArrayContentProvider());
    precisionsCombo.setLabelProvider(new LabelProvider()
    {

      @Override
      public String getText(Object element)
      {
        return ((Precision) element).getLabel();
      }
    });
    precisionsCombo.addSelectionChangedListener(new ISelectionChangedListener()
    {

      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        ISelection sel = precisionsCombo.getSelection();
        IStructuredSelection cSel = (IStructuredSelection) sel;
        Precision precision = (Precision) cSel.getFirstElement();
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
    GridLayout gridLayout = new GridLayout(1, true);
    gridLayout.verticalSpacing = 2;
    gridLayout.marginLeft = 5;
    gridLayout.marginRight = 5;
    main.setLayout(gridLayout);

    initPreferencesGroup(main);
    initVehicleGroup(main);
    initAnalystContributionsGroup(main);
    initAddContributionGroup(main);

    Composite lowerSection = new Composite(sashForm, SWT.NONE);
    lowerSection.setLayout(new FillLayout());

    // ok - the next section needs to be in a sash - so we can resize it
    initGraphTabs(lowerSection);

    // set the relative sizes in the sash
    sashForm.setWeights(new int[]
    {3, 1});

    // also sort out the header controls
    final IActionBars bars = getViewSite().getActionBars();
    IToolBarManager manager = bars.getToolBarManager();
    manager.add(SATC_Activator.createOpenHelpAction(
        "org.mwc.debrief.help.SATC", null, this));

    _exportBtn = new Action("Export SATC dataset", Action.AS_PUSH_BUTTON)
    {
      public void runWithEvent(final Event event)
      {
        exportSATC();
      }
    };
    _exportBtn.setToolTipText("Export SATC scenario to clipboard");
    _exportBtn.setImageDescriptor(SATC_Activator
        .getImageDescriptor("icons/export.png"));
    manager.add(_exportBtn);

  }

  private void initGraphTabs(Composite parent)
  {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.heightHint = 200;

    graphTabs = new TabFolder(parent, SWT.BORDER);
    FillLayout fillLayout = new FillLayout();
    fillLayout.marginWidth = 5;
    fillLayout.marginHeight = 5;
    // tabs.setLayout(fillLayout);
    graphTabs.setLayoutData(gridData);

//    legTab = new TabItem(graphTabs, SWT.NONE);
//    legTab.setText("Ownship && Target Legs");
//    legGraphComposite = initLegGraph(graphTabs);
//    legTab.setControl(legGraphComposite);

    performanceTab = new TabItem(graphTabs, SWT.NONE);
    performanceTab.setText("Performance");
    Group perfG2 = initPerformanceGraph(graphTabs);
    performanceTab.setControl(perfG2);

    // Zone Test
    zoneTab = new TabItem(graphTabs, SWT.NONE);
    zoneTab.setText("Straight Legs");

    ZoneChart.ColorProvider blueProvider = new ZoneChart.ColorProvider()
    {
      @Override
      public java.awt.Color getZoneColor()
      {
        return java.awt.Color.blue;
      }
    };
    ZoneChartConfig zoneConfig =
        new ZoneChart.ZoneChartConfig("Target Legs", "Course",
            DebriefColors.BLUE, true);
    
    // for the new API signature we need to put the data into JFReeChart TimeSeries
    // objects;
    measuredBearingsForZones = new TimeSeries("Measured Bearings");
    final TimeSeriesCollection[] otherSeries = new TimeSeriesCollection[] {};
    
    // build up the zones
    final ZoneChart.Zone[] zones = new Zone[] {};
    
    zoneChart = ZoneChart.create(zoneConfig, null, graphTabs, zones,
        measuredBearingsForZones, otherSeries, null, blueProvider, null,
        null, null, null);

    zoneChart.addZoneListener(new ZoneChart.ZoneAdapter()
    {

      
      
      @Override
      public void added(Zone zone)
      {
        // ok, create the new zone
        StraightLegForecastContribution newS = new
            StraightLegForecastContribution();
        newS.setStartDate(new Date(zone.getStart()));
        newS.setFinishDate(new Date(zone.getEnd()));
        newS.setName(new Date(zone.getStart()).toGMTString());
        activeSolver.getContributions().addContribution(newS);
      }

      @Override
      public void deleted(Zone zone)
      {
        final long tStart = zone.getStart();
        final long tEnd = zone.getEnd();
        // ok, find the matching zone
        if(activeSolver != null)
        {
          Iterator<BaseContribution> cIter = activeSolver.getContributions().iterator();
          while(cIter.hasNext())
          {
            BaseContribution base = cIter.next();
            if(base instanceof StraightLegForecastContribution)
            {
              StraightLegForecastContribution st = (StraightLegForecastContribution) base;
              if(st.getStartDate().getTime() == tStart || st.getFinishDate().getTime() == tEnd)
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
      public void moved(Zone zone)
      {
        System.out
            .println("MaintainContributionsView.initGraphTabs(...).new ZoneAdapter() {...}.moved()");
      }
      
      @Override
      public void resized(Zone zone)
      {
        final long tStart = zone.getStart();
        final long tEnd = zone.getEnd();
        // ok, find the matching zone
        if(activeSolver != null)
        {
          Iterator<BaseContribution> cIter = activeSolver.getContributions().iterator();
          while(cIter.hasNext())
          {
            BaseContribution base = cIter.next();
            if(base instanceof StraightLegForecastContribution)
            {
              StraightLegForecastContribution st = (StraightLegForecastContribution) base;
              if(st.getStartDate().getTime() == tStart)
              {
                // ok, update the end value
                st.setFinishDate(new Date(tEnd));
                return;
              }
              else if(st.getFinishDate().getTime() == tEnd)
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

  private static List<Zone> getZones(final IContributions conts)
  {
    final ArrayList<Zone> zones = new ArrayList<Zone>();
    final Iterator<BaseContribution> iter = conts.iterator();
    while(iter.hasNext())
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

  private Group initPerformanceGraph(Composite parent)
  {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.heightHint = 200;

    Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
    FillLayout fillLayout = new FillLayout();
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
    IAxis xAxis = performanceChart.getAxisSet().getXAxis(0);
    xAxis.getTitle().setVisible(false);
    xAxis.adjustRange();
    xAxis.getTick().setForeground(_colorBlack);

    // and the y axis
    IAxis yAxis = performanceChart.getAxisSet().getYAxis(0);
    yAxis.adjustRange();
    yAxis.enableLogScale(true);
    yAxis.getTick().setForeground(_colorBlack);
    yAxis.getTitle().setForeground(_colorBlack);
    yAxis.getTitle().setText("Weighted error");

    return group;
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
//  public Composite initLegGraph(final Composite parent)
//  {
//
//    legChart = ChartFactory.createTimeSeriesChart("Ownship & Target Legs", // String
//        "Time", // String timeAxisLabel
//        null, // String valueAxisLabel,
//        null, // XYDataset dataset,
//        true, // include legend
//        true, // tooltips
//        false); // urls
//
//    legPlot = (XYPlot) legChart.getPlot();
//    legPlot.setDomainCrosshairVisible(true);
//    legPlot.setRangeCrosshairVisible(true);
//    final DateAxis axis = (DateAxis) legPlot.getDomainAxis();
//    axis.setDateFormatOverride(new GMTDateFormat("HH:mm:ss"));
//
//    legPlot.setBackgroundPaint(MWC.GUI.Properties.DebriefColors.WHITE);
//    legPlot.setRangeGridlinePaint(MWC.GUI.Properties.DebriefColors.LIGHT_GRAY);
//    legPlot.setDomainGridlinePaint(MWC.GUI.Properties.DebriefColors.LIGHT_GRAY);
//
//    // format the cross hairs, when they're clicked
//    legPlot.setDomainCrosshairVisible(true);
//    legPlot.setRangeCrosshairVisible(true);
//    legPlot.setDomainCrosshairPaint(MWC.GUI.Properties.DebriefColors.GRAY);
//    legPlot.setRangeCrosshairPaint(MWC.GUI.Properties.DebriefColors.GRAY);
//    legPlot.setDomainCrosshairStroke(new BasicStroke(1));
//    legPlot.setRangeCrosshairStroke(new BasicStroke(1));
//
//    // and the plot object to display the cross hair value
//    final XYTextAnnotation annot = new XYTextAnnotation("-----", 0, 0);
//    annot.setTextAnchor(TextAnchor.TOP_LEFT);
//    annot.setPaint(MWC.GUI.Properties.DebriefColors.BLACK);
//    annot.setBackgroundPaint(MWC.GUI.Properties.DebriefColors.WHITE);
//    legPlot.addAnnotation(annot);
//
//    legChart.addProgressListener(new ChartProgressListener()
//    {
//      public void chartProgress(final ChartProgressEvent cpe)
//      {
//        if (cpe.getType() != ChartProgressEvent.DRAWING_FINISHED)
//          return;
//
//        // double-check our label is still in the right place
//        final double xVal = legPlot.getRangeAxis().getUpperBound();
//        final double yVal = legPlot.getDomainAxis().getLowerBound();
//
//        boolean annotChanged = false;
//        if (annot.getX() != yVal)
//        {
//          annot.setX(yVal);
//          annotChanged = true;
//        }
//        if (annot.getY() != xVal)
//        {
//          annot.setY(xVal);
//          annotChanged = true;
//        }
//
//        // and write the text
//        final NumberFormat _oneDPFormat =
//            new DecimalFormat("0.0", new java.text.DecimalFormatSymbols(
//                java.util.Locale.UK));
//        final String numA =
//            _oneDPFormat.format(legPlot.getRangeCrosshairValue());
//        final Date newDate = new Date((long) legPlot.getDomainCrosshairValue());
//        final SimpleDateFormat _df = new GMTDateFormat("HHmm:ss");
//        final String dateVal = _df.format(newDate);
//        final String theMessage = " [" + dateVal + "," + numA + "]";
//        if (!theMessage.equals(annot.getText()))
//        {
//          annot.setText(theMessage);
//          annotChanged = true;
//        }
//
//        // aah, now we have to add and then remove the annotation in order
//        // for the new text value to be displayed. Watch and learn...
//        if (annotChanged)
//        {
//          legPlot.removeAnnotation(annot);
//          legPlot.addAnnotation(annot);
//        }
//
//      }
//    });
//
//    ChartComposite chartFrame =
//        new ChartComposite(parent, SWT.NONE, legChart, true)
//        {
//          @Override
//          public void mouseUp(MouseEvent event)
//          {
//            super.mouseUp(event);
//            JFreeChart c = getChart();
//            if (c != null)
//            {
//              c.setNotify(true); // force redraw
//            }
//          }
//        };
//    chartFrame.setDisplayToolTips(true);
//    chartFrame.setHorizontalAxisTrace(false);
//    chartFrame.setVerticalAxisTrace(false);
//
//    return chartFrame;
//  }

  /**
   * clear the data on the leg graph
   * 
   */
//  private void clearLegGraph()
//  {
//    if (legPlot == null)
//      return;
//
//    if ((legGraphComposite == null) || (legGraphComposite.isDisposed()))
//      return;
//
//    legPlot.setDataset(0, null);
//    legPlot.setDataset(1, null);
//    legPlot.setDataset(2, null);
//    legPlot.setDataset(3, null);
//
//    legPlot.clearDomainMarkers();
//  }

  private void clearPerformanceGraph()
  {
    // hmm, have we already ditched?
    if (performanceChart.isDisposed())
      return;

    ISeries[] sets = performanceChart.getSeriesSet().getSeries();
    for (int i = 0; i < sets.length; i++)
    {
      ISeries iSeries = sets[i];
      performanceChart.getSeriesSet().deleteSeries(iSeries.getId());
    }

    // ySeries.setYSeries(newYVals);

    performanceChart.getAxisSet().getXAxis(0).adjustRange();
    performanceChart.getAxisSet().getYAxis(0).adjustRange();
    performanceChart.getAxisSet().getYAxis(0).enableLogScale(true);
    performanceChart.redraw();

  }

  private Color colorFor(BaseContribution contribution)
  {

    if (assignedColors == null)
    {
      assignedColors = new HashMap<BaseContribution, Color>();
    }

    // have we already assigned this one?
    Color res = assignedColors.get(contribution);

    if (res == null)
    {
      int index = assignedColors.size() % defaultColors.length;
      java.awt.Color newCol = defaultColors[index];
      res =
          new Color(Display.getDefault(), newCol.getRed(), newCol.getGreen(),
              newCol.getBlue());
      assignedColors.put(contribution, res);
    }

    return res;

  }

  private void addNewPerformanceScore(double value,
      List<CompositeRoute> topRoutes)
  {
    // remember each contribution's set of scores
    HashMap<BaseContribution, HashMap<Date, Double>> stackedSeries =
        new HashMap<BaseContribution, HashMap<Date, Double>>();

    // remember the times for which we have states
    ArrayList<Date> valueTimes = new ArrayList<Date>();

    // ok - have a look at the scores
    Iterator<CoreRoute> legIter = topRoutes.get(0).getLegs().iterator();
    while (legIter.hasNext())
    {
      CoreRoute route = legIter.next();
      Iterator<State> states = route.getStates().iterator();
      while (states.hasNext())
      {
        State state = states.next();
        HashMap<BaseContribution, Double> scores = state.getScores();
        Iterator<BaseContribution> contributions = scores.keySet().iterator();
        while (contributions.hasNext())
        {
          BaseContribution cont = contributions.next();

          // get the score
          Double score = scores.get(cont);
          if (score > 0)
          {

            HashMap<Date, Double> thisSeries = stackedSeries.get(cont);
            if (thisSeries == null)
            {
              thisSeries = new HashMap<Date, Double>();
              stackedSeries.put(cont, thisSeries);
              final IBarSeries series =
                  (IBarSeries) performanceChart.getSeriesSet().createSeries(
                      SeriesType.BAR, cont.getName());
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
    Iterator<BaseContribution> conts = stackedSeries.keySet().iterator();
    while (conts.hasNext())
    {
      BaseContribution cont = conts.next();
      HashMap<Date, Double> vals = stackedSeries.get(cont);
      if (vals.size() > 0)
      {
        final IBarSeries series =
            (IBarSeries) performanceChart.getSeriesSet().getSeries(
                cont.getName());

        // ok, we need to produce a value for each value time
        double[] valArr = new double[valueTimes.size()];

        Iterator<Date> iter2 = valueTimes.iterator();
        int ctr = 0;
        while (iter2.hasNext())
        {
          Date date = iter2.next();
          Double thisV = vals.get(date);
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
    String[] labels = new String[valueTimes.size()];
    Iterator<Date> vIter = valueTimes.iterator();

    // get our date formatter ready
    SimpleDateFormat sdf = new GMTDateFormat("HH:mm:ss");

    // determine frequency f (trim to 1)
    int wid = performanceChart.getBounds().width;
    int allowed = wid / 90;
    int freq = Math.max(labels.length / allowed, 1);

    int ctr = 0;
    while (vIter.hasNext())
    {
      Date date = vIter.next();
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

    ISeries[] series = performanceChart.getSeriesSet().getSeries();
    if (series.length == 2 && series[0] instanceof IBarSeries
        && series[1] instanceof IBarSeries)
    {
      performanceChart.getLegend().setVisible(true);
      performanceChart.getLegend().setPosition(SWT.RIGHT);
      IBarSeries barSeries1 = (IBarSeries) series[0];
      IBarSeries barSeries2 = (IBarSeries) series[1];
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

  private void initVehicleGroup(Composite parent)
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
    // group.setText("Vehicle");
    //
    // vehiclesCombo = new ComboViewer(group);
    // vehiclesCombo.setContentProvider(new ArrayContentProvider());
    // vehiclesCombo.setLabelProvider(new LabelProvider()
    // {
    //
    // @Override
    // public String getText(Object element)
    // {
    // return ((VehicleType) element).getName();
    // }
    // });
    // vehiclesCombo.addSelectionChangedListener(new
    // ISelectionChangedListener()
    // {
    //
    // @Override
    // public void selectionChanged(SelectionChangedEvent event)
    // {
    // if (activeSolver != null)
    // {
    // ISelection selection = event.getSelection();
    // if (selection instanceof StructuredSelection)
    // {
    // VehicleType type =
    // (VehicleType) ((StructuredSelection) selection)
    // .getFirstElement();
    // activeSolver.setVehicleType(type);
    // }
    // }
    // }
    // });
  }

  public void populateContributionList(List<ContributionBuilder> items)
  {
    // for (final ContributionBuilder item : items)
    // {
    // MenuItem menuItem = new MenuItem(addContributionMenu, SWT.PUSH);
    // menuItem.setText(item.getDescription());
    // menuItem.addSelectionListener(new SelectionAdapter()
    // {
    // @Override
    // public void widgetSelected(SelectionEvent arg0)
    // {
    // if (activeSolver != null)
    // {
    // activeSolver.getContributions().addContribution(item.create());
    // }
    // }
    // });
    // }
  }

  public void populatePrecisionsList(Precision[] precisions)
  {
    precisionsCombo.setInput(precisions);
    precisionsCombo.setSelection(new StructuredSelection(precisions[0]));
  }

  public void populateVehicleTypesList(List<VehicleType> vehicles)
  {
    // vehiclesCombo.setInput(vehicles);
    //
    // // ok, try to set the first one
    // if (vehicles.size() > 0)
    // {
    // vehiclesCombo.setSelection(new
    // StructuredSelection(vehicles.iterator()
    // .next()));
    // }
  }

  private void initListeners(final Composite parent)
  {
    contributionsChangedListener =
        UIListener.wrap(parent.getDisplay(),
            IContributionsChangedListener.class,
            new IContributionsChangedListener()
            {

              @Override
              public void removed(BaseContribution contribution)
              {
                removeContribution(contribution, true);
              }

              @Override
              public void added(BaseContribution contribution)
              {
                addContribution(contribution, true);
              }

              @Override
              public void modified()
              {
              }
            });
    generateSolutionsListener =
        UIListener.wrap(parent.getDisplay(), IGASolutionsListener.class,
            new SteppingAdapter()
            {
              Control focused = null;

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

              @Override
              public void iterationComputed(List<CompositeRoute> topRoutes,
                  double topScore)
              {
                addNewPerformanceScore(topScore, topRoutes);
              }

              @Override
              public void finishedGeneration(Throwable error)
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
            });
    solverManagerListener =
        UIListener.wrap(parent.getDisplay(), ISolversManagerListener.class,
            new ISolversManagerListener()
            {

              @Override
              public void solverCreated(final ISolver solver)
              {

              }

              @Override
              public void activeSolverChanged(final ISolver activeSolver)
              {
                setActiveSolver(activeSolver);
              }
            });

    constrainSpaceListener =
        UIListener.wrap(parent.getDisplay(), IConstrainSpaceListener.class,
            new IConstrainSpaceListener()
            {

              @Override
              public void stepped(IBoundsManager boundsManager, int thisStep,
                  int totalSteps)
              {
              }

              @Override
              public void statesBounded(IBoundsManager boundsManager)
              {
                // minimum steps to get the contributions list to redraw
                contList.setSize(0, 0);
              }

              @Override
              public void restarted(IBoundsManager boundsManager)
              {
              }

              @Override
              public void error(IBoundsManager boundsManager,
                  IncompatibleStateException ex)
              {
              }
            });
  }

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
      List<BaseContribution> contributions =
          new ArrayList<BaseContribution>(contributionsControl.keySet());
      for (BaseContribution contribution : contributions)
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
   //   clearLegGraph();

      activeSolver = solver;
      boolean hasSolver = activeSolver != null;
      if (hasSolver)
      {
        activeSolver.getContributions().addContributionsChangedListener(
            contributionsChangedListener);
        activeSolver.getSolutionGenerator().addReadyListener(
            generateSolutionsListener);
        activeSolver.getBoundsManager().addConstrainSpaceListener(
            constrainSpaceListener);

        for (BaseContribution contribution : activeSolver.getContributions())
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
        liveRunningBinding =
            context.bindValue(WidgetProperties.selection().observe(
                liveConstraints), BeansObservables.observeValue(activeSolver,
                ISolver.LIVE_RUNNING));
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

  private void clearZoneGraph()
  {
    if (zoneChart == null)
      return;

    zoneChart.clearZones();
  }

  private void updateZoneChart(final IContributions contributions)
  {
    // ok, we need the list of straight legs
    List<Zone> zones = getZones(contributions);
    
    // ok, now give these to the ZoneChart
    zoneChart.clearZones();
    zoneChart.setZones(zones);
    
    // we also need the list of bearings
    updateZoneBearings(contributions, measuredBearingsForZones);
  }

  private void updateZoneBearings(IContributions contributions, final TimeSeries measuredBearingsForZones2)
  {
    // clear out any existing datasets
    measuredBearingsForZones.clear();
    
    final Iterator<BaseContribution> iter = contributions.iterator();
    while(iter.hasNext())
    {
      final BaseContribution cont = iter.next();
      if(cont instanceof BearingMeasurementContribution)
      {
        final BearingMeasurementContribution bearings = (BearingMeasurementContribution) cont;
        final ArrayList<BMeasurement> measurements = bearings.getMeasurements();
        final Iterator<BMeasurement> mIter = measurements.iterator();
        while(mIter.hasNext())
        {
          final BMeasurement bm = mIter.next();
          double degs = MWC.Algorithms.Conversions.Rads2Degs(bm.getBearingRads());
          final long time = bm.getDate().getTime();
          if(degs < 0)
          {
            degs += 360;
          }
          measuredBearingsForZones.addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(time), degs));
        }
      }
    }
  }

  public void addContribution(BaseContribution contribution, boolean doLayout)
  {
    // ok, create a wrapper for this
    BaseContributionView<?> panel = null;
    if (!CONTRIBUTION_PANELS.containsKey(contribution.getClass()))
    {
      return;
    }
    try
    {
      Class<?> viewClass = CONTRIBUTION_PANELS.get(contribution.getClass());
      panel =
          (BaseContributionView<?>) viewClass.getConstructor(Composite.class,
              contribution.getClass(), IContributions.class).newInstance(
              contList, contribution, activeSolver.getContributions());
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
        StraightLegForecastContribution slf =
            (StraightLegForecastContribution) contribution;
        if (_legListener == null)
          _legListener = new PropertyChangeListener()
          {

            @Override
            public void propertyChange(PropertyChangeEvent evt)
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
        BearingMeasurementContribution bmc =
            (BearingMeasurementContribution) contribution;
        if (_sliceListener == null)
        {
          _sliceListener =
              new BearingMeasurementContribution.MDAResultsListener()
              {

                @Override
                public void startingSlice(String contName)
                {
                  startSlicingOwnshipLegs(contName);
                }

                @Override
                public void
                    ownshipLegs(String contName,
                        ArrayList<BMeasurement> bearings,
                        List<LegOfData> ownshipLegs,
                        ArrayList<HostState> hostStates)
                {
                  // clear the domain markers, this is a new dataset
                  // legPlot.clearDomainMarkers();

                  // and show the ownship states
                  zoneChart.clearZones();
                }

                @Override
                public void sliced(String contName,
                    ArrayList<StraightLegForecastContribution> arrayList)
                {
                  // ok, now display the target legs
                  redoStraightLegs();
                }
              };
        }
        bmc.addSliceListener(_sliceListener);
      }

    }
    catch (Exception ex)
    {
      LogFactory.getLog().error("Failed to generate panel for " + contribution);
      SATC_Activator.getDefault().getLog().log(
          new Status(IStatus.ERROR, SATC_Activator.PLUGIN_ID, ex.getMessage(),
              ex));
    }
  }

  protected void startSlicingOwnshipLegs(String contName)
  {
    // hey, let's also ditch any straight leg forecasts
    Iterator<BaseContribution> conts =
        activeSolver.getContributions().iterator();
    ArrayList<BaseContribution> toRemove = new ArrayList<BaseContribution>();
    while (conts.hasNext())
    {
      BaseContribution baseC = conts.next();
      if (baseC.isActive())
        if (baseC instanceof StraightLegForecastContribution)
        {
          toRemove.add(baseC);
        }
    }

    // did we find any?
    if (!toRemove.isEmpty())
    {
      Iterator<BaseContribution> iter = toRemove.iterator();
      while (iter.hasNext())
      {
        BaseContribution baseContribution = iter.next();
        activeSolver.getContributions().removeContribution(baseContribution);
      }
    }

    // ok, clear any leg markers
//    if (legPlot != null)
//    {
//      graphTabs.setSelection(legTab);
//
//      legPlot.clearDomainMarkers();
//    }

  }

//  protected void redoOwnshipStates()
//  {
//    if (legPlot == null)
//      return;
//
//    boolean showCourses = true;
//    if (showOSCourse != null)
//      showCourses = showOSCourse.getSelection();
//
//    java.awt.Color courseCol =
//        MWC.GUI.Properties.DebriefColors.BLUE.darker().darker();
//    java.awt.Color speedCol =
//        MWC.GUI.Properties.DebriefColors.BLUE.brighter().brighter();
//
//    // ok, now loop through and set them
//    long startTime = Long.MAX_VALUE;
//    long endTime = Long.MIN_VALUE;
//
//    // clear any datasets
//    legPlot.setDataset(0, null);
//    legPlot.setDataset(1, null);
//
//    // hmm, actually we have to remove any target leg markers
//    @SuppressWarnings("unchecked")
//    Collection<IntervalMarker> markers =
//        legPlot.getDomainMarkers(Layer.BACKGROUND);
//    if (markers != null)
//    {
//      ArrayList<IntervalMarker> markersToDelete =
//          new ArrayList<IntervalMarker>(markers);
//      Iterator<IntervalMarker> mIter = markersToDelete.iterator();
//      while (mIter.hasNext())
//      {
//        IntervalMarker im = mIter.next();
//        legPlot.removeDomainMarker(im);
//      }
//    }
//
//    // hey, does it have any ownship legs?
//    TimeSeriesCollection tscC = new TimeSeriesCollection();
//    TimeSeriesCollection tscS = new TimeSeriesCollection();
//    TimeSeriesCollection tscCLegs = new TimeSeriesCollection();
//    TimeSeriesCollection tscSLegs = new TimeSeriesCollection();
//    TimeSeries courses = new TimeSeries("Course");
//    TimeSeries bearings = new TimeSeries("Bearings");
//    TimeSeries speeds = new TimeSeries("Speed");
//    TimeSeries courseLegs = new TimeSeries("Course (leg)");
//    TimeSeries speedLegs = new TimeSeries("Speed (leg)");
//
//    Iterator<BaseContribution> conts =
//        activeSolver.getContributions().iterator();
//    while (conts.hasNext())
//    {
//      BaseContribution baseC = conts.next();
//      if (baseC.isActive())
//        if (baseC instanceof BearingMeasurementContribution)
//        {
//          BearingMeasurementContribution bmc =
//              (BearingMeasurementContribution) baseC;
//
//          Iterator<LegOfData> lIter = null;
//          LegOfData thisLeg = null;
//
//          if (bmc.getOwnshipLegs() != null)
//          {
//            lIter = bmc.getOwnshipLegs().iterator();
//            thisLeg = lIter.next();
//          }
//
//          List<HostState> hostStates = bmc.getHostState();
//          if (hostStates != null)
//          {
//            Iterator<HostState> stateIter = hostStates.iterator();
//            while (stateIter.hasNext())
//            {
//              BearingMeasurementContribution.HostState hostState =
//                  stateIter.next();
//              long thisTime = hostState.time;
//              double thisCourse = hostState.courseDegs;
//              if (showCourses)
//                courses.add(new FixedMillisecond(thisTime), thisCourse);
//              double thisSpeed = hostState.speedKts;
//              speeds.add(new FixedMillisecond(thisTime), thisSpeed);
//              startTime = Math.min(thisTime, startTime);
//              endTime = Math.max(thisTime, endTime);
//
//              // sort out if this is in a leg or not
//              if (thisLeg != null)
//              {
//                if (thisTime > thisLeg.getEnd() && lIter.hasNext())
//                {
//                  thisLeg = lIter.next();
//                }
//                else
//                {
//                  if (thisTime >= thisLeg.getStart())
//                  {
//                    speedLegs.add(new FixedMillisecond(thisTime), thisSpeed);
//                    if (showCourses)
//                      courseLegs
//                          .add(new FixedMillisecond(thisTime), thisCourse);
//                  }
//                }
//              }
//            }
//          }
//
//          // also, we wish to show the bearings from the BMC
//          Iterator<BMeasurement> cuts = bmc.getMeasurements().iterator();
//          while (cuts.hasNext())
//          {
//            BearingMeasurementContribution.BMeasurement measurement =
//                cuts.next();
//            if (measurement.isActive())
//            {
//              long thisT = measurement.getDate().getTime();
//
//              // TODO: we're currently putting all of the measurements into one
//              // time-series (so we have to use addOrUpdate ) in case there are
//              // multiple measurements at one time-stamp.
//              // a more refined implementation would be to use multiple TimeSeries
//              // objects
//              bearings.addOrUpdate(new FixedMillisecond(thisT), Math.toDegrees(
//                  Math.abs(measurement.getBearingRads())));
//            }
//          }
//
//        }
//    }
//
//    // HEY, also shade the ownship legs
//    conts = activeSolver.getContributions().iterator();
//    while (conts.hasNext())
//    {
//      BaseContribution baseC = conts.next();
//      if (baseC.isActive())
//      {
//        if (baseC instanceof BearingMeasurementContribution)
//        {
//          BearingMeasurementContribution bmc =
//              (BearingMeasurementContribution) baseC;
//
//          Iterator<LegOfData> lIter = null;
//          if (bmc.getOwnshipLegs() != null)
//          {
//            int ctr = 1;
//            lIter = bmc.getOwnshipLegs().iterator();
//            while (lIter.hasNext())
//            {
//              LegOfData thisL = lIter.next();
//              long thisStart = thisL.getStart();
//              long thisFinish = thisL.getEnd();
//
//              java.awt.Color transCol = new java.awt.Color(0, 0, 255, 12);
//
//              final Marker bst =
//                  new IntervalMarker(thisStart, thisFinish, transCol,
//                      new BasicStroke(2.0f), null, null, 1.0f);
//              bst.setLabel("O/S-" + ctr++);
//              bst.setLabelAnchor(RectangleAnchor.TOP_LEFT);
//              bst.setLabelFont(new Font("SansSerif", Font.ITALIC + Font.BOLD,
//                  10));
//              bst.setLabelTextAnchor(TextAnchor.TOP_LEFT);
//              legPlot.addDomainMarker(bst, Layer.BACKGROUND);
//            }
//          }
//        }
//      }
//    }
//
//    tscS.addSeries(speeds);
//    tscSLegs.addSeries(speedLegs);
//    tscC.addSeries(bearings);
//
//    if (showCourses)
//    {
//      tscC.addSeries(courses);
//      tscCLegs.addSeries(courseLegs);
//    }
//
//    legPlot.setDataset(0, null);
//    legPlot.setDataset(1, null);
//    legPlot.setDataset(2, null);
//    legPlot.setDataset(3, null);
//    legPlot.setDataset(0, tscC);
//    legPlot.setDataset(1, tscS);
//    legPlot.setDataset(2, tscCLegs);
//    legPlot.setDataset(3, tscSLegs);
//
//    final NumberAxis axis2 = new NumberAxis("Speed (Kts)");
//    legPlot.setRangeAxis(1, axis2);
//    legPlot.mapDatasetToRangeAxis(1, 1);
//    legPlot.mapDatasetToRangeAxis(3, 1);
//
//    legPlot.getRangeAxis(0).setLabel("Crse/Brg (Degs)");
//    legPlot.mapDatasetToRangeAxis(0, 0);
//    legPlot.mapDatasetToRangeAxis(2, 0);
//
//    final XYLineAndShapeRenderer lineRenderer1 =
//        new XYLineAndShapeRenderer(true, true);
//    lineRenderer1.setSeriesPaint(1, courseCol);
//    lineRenderer1.setSeriesShape(1, ShapeUtilities.createDiamond(0.1f));
//    lineRenderer1.setSeriesPaint(0, MWC.GUI.Properties.DebriefColors.RED);
//    lineRenderer1.setSeriesShape(0, ShapeUtilities.createDiamond(2f));
//
//    final XYLineAndShapeRenderer lineRenderer2 =
//        new XYLineAndShapeRenderer(true, false);
//    lineRenderer2.setSeriesPaint(0, speedCol);
//
//    final XYLineAndShapeRenderer lineRenderer3 =
//        new XYLineAndShapeRenderer(false, true);
//    lineRenderer3.setSeriesPaint(0, courseCol);
//    lineRenderer3.setSeriesShape(0, ShapeUtilities.createUpTriangle(2f));
//
//    final XYLineAndShapeRenderer lineRenderer4 =
//        new XYLineAndShapeRenderer(false, true);
//    lineRenderer4.setSeriesPaint(0, speedCol);
//    lineRenderer4.setSeriesShape(0, ShapeUtilities.createDownTriangle(2f));
//
//    // ok, and store them
//    legPlot.setRenderer(0, lineRenderer1);
//    legPlot.setRenderer(1, lineRenderer2);
//    legPlot.setRenderer(2, lineRenderer3);
//    legPlot.setRenderer(3, lineRenderer4);
//
//    if (startTime != Long.MAX_VALUE)
//      legPlot.getDomainAxis().setRange(startTime, endTime);
//
//    // ok - get the straight legs to sort themselves out
//    // redoStraightLegs();
//  }

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

  protected void removeContribution(BaseContribution contribution,
      boolean doLayout)
  {
    BaseContributionView<?> panel = contributionsControl.get(contribution);
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
      BearingMeasurementContribution bmc =
          (BearingMeasurementContribution) contribution;
      bmc.removeSliceListener(_sliceListener);
    }
  }

  @Override
  protected void finalize() throws Throwable
  {

    // clear our listeners
    Iterator<BaseContribution> conts =
        activeSolver.getContributions().iterator();
    while (conts.hasNext())
    {
      BaseContribution contribution = conts.next();
      // aaah, is it a straight leg?
      if (contribution instanceof StraightLegForecastContribution)
      {
        stopListeningTo(contribution);
      }
      else if (contribution instanceof BearingMeasurementContribution)
      {
        BearingMeasurementContribution bmc =
            (BearingMeasurementContribution) contribution;
        bmc.removeSliceListener(_sliceListener);
      }
    }

    // let the parent shut down
    super.finalize();
  }

  private void startListeningTo(final StraightLegForecastContribution slf)
  {
    slf.addPropertyChangeListener(BaseContribution.START_DATE, _legListener);
    slf.addPropertyChangeListener(BaseContribution.FINISH_DATE, _legListener);
    slf.addPropertyChangeListener(BaseContribution.NAME, _legListener);
    slf.addPropertyChangeListener(BaseContribution.ACTIVE, _legListener);
  }

  private void stopListeningTo(final BaseContribution slf)
  {
    slf.removePropertyChangeListener(BaseContribution.ACTIVE, _legListener);
    slf.removePropertyChangeListener(BaseContribution.START_DATE, _legListener);
    slf.removePropertyChangeListener(BaseContribution.FINISH_DATE, _legListener);
    slf.removePropertyChangeListener(BaseContribution.NAME, _legListener);
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
      StringBuffer res = new StringBuffer();
      final String newLine = System.getProperty("line.separator");
      SimpleDateFormat sdf = new GMTDateFormat("yyyy/MMM/dd HH:mm:ss");
      Date dateLead = new Date(100, 7, 7);

      Iterator<BaseContribution> conts =
          activeSolver.getContributions().iterator();
      while (conts.hasNext())
      {
        BaseContribution baseC = conts.next();
        if (baseC instanceof BearingMeasurementContribution)
        {
          BearingMeasurementContribution bmc =
              (BearingMeasurementContribution) baseC;

          // ok - sort out the date offset
          Date startDate = bmc.getStartDate();
          long offset = startDate.getTime() - dateLead.getTime();

          // get ready for the offset
          Point2D origin = null;

          // get ready to calculate offsetes
          GeodeticCalculator calc = GeoSupport.createCalculator();

          // ok, first the states
          res.append("//X, Y, Time, Course Degs, Speed Kts" + newLine);
          Iterator<HostState> states = bmc.getHostState().iterator();
          while (states.hasNext())
          {
            BearingMeasurementContribution.HostState hostState = states.next();

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
              Point2D.Double thisP =
                  new Point2D.Double(hostState.dLong, hostState.dLat);
              calc.setStartingGeographicPoint(origin);
              calc.setDestinationGeographicPoint(thisP);
              double angle = calc.getAzimuth();
              double dist = calc.getOrthodromicDistance();

              // and the new x,y coords
              x = Math.sin(Math.toRadians(angle)) * dist;
              y = Math.cos(Math.toRadians(angle)) * dist;

            }

            res.append(x + ", " + y + ", "
                + sdf.format(new Date(hostState.time - offset)) + ","
                + hostState.courseDegs + "," + hostState.speedKts + newLine);
          }

          // now the cuts
          res.append("//Time, Bearing Degs" + newLine);
          Iterator<BMeasurement> cuts = bmc.getMeasurements().iterator();
          while (cuts.hasNext())
          {
            BearingMeasurementContribution.BMeasurement cut = cuts.next();
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

  static final class TextTransfer implements ClipboardOwner
  {
    private TextTransfer()
    {
    }

    @Override
    public void lostOwnership(Clipboard aClipboard, Transferable aContents)
    {
      // do nothing
    }

    /**
     * Place a String on the clipboard, and make this class the owner of the Clipboard's contents.
     */
    public void setClipboardContents(String aString)
    {
      StringSelection stringSelection = new StringSelection(aString);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, this);
    }

  }

}
