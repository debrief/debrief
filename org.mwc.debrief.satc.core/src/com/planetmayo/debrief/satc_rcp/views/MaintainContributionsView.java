package com.planetmayo.debrief.satc_rcp.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
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
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ISeriesSet;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.ATBForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CompositeStraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.ContributionBuilder;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
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
import com.planetmayo.debrief.satc.model.manager.IContributionsManager;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc.model.manager.ISolversManagerListener;
import com.planetmayo.debrief.satc.model.manager.IVehicleTypesManager;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.planetmayo.debrief.satc_rcp.ui.UIListener;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.contributions.ATBForecastContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.BaseContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.BearingMeasurementContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.CompositeStraightLegForecastContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.CourseContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.LocationForecastContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.RangeForecastContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.SpeedContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.StraightLegForecastContributionView;

/**
 * mock class to test high level application flows
 * 
 * @author ian
 * 
 */
public class MaintainContributionsView extends ViewPart
{

	private static final int ERROR_SCORE_THRESHOLD = 1000000;

	/**
	 * name we use for the performance graph
	 * 
	 */
	private static final String SERIES_NAME = "line series";

	public static final String ID = "com.planetmayo.debrief.satc.views.MaintainContributionsView";
	private static final String TITLE = "Maintain Contributions";

	private static final Map<Class<? extends BaseContribution>, Class<? extends BaseContributionView<?>>> CONTRIBUTION_PANELS;
	static
	{
		CONTRIBUTION_PANELS = new HashMap<Class<? extends BaseContribution>, Class<? extends BaseContributionView<?>>>();
		CONTRIBUTION_PANELS.put(ATBForecastContribution.class,
				ATBForecastContributionView.class);
		CONTRIBUTION_PANELS.put(BearingMeasurementContribution.class,
				BearingMeasurementContributionView.class);
		CONTRIBUTION_PANELS.put(CourseForecastContribution.class,
				CourseContributionView.class);
		CONTRIBUTION_PANELS.put(LocationForecastContribution.class,
				LocationForecastContributionView.class);
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

	/** Contribution -> Contribution view mappings */
	private HashMap<BaseContribution, BaseContributionView<?>> contributionsControl = new HashMap<BaseContribution, BaseContributionView<?>>();
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

	@Override
	public void createPartControl(final Composite parent)
	{
		context = new DataBindingContext();
		IContributionsManager contributionsManager = SATC_Activator.getDefault()
				.getService(IContributionsManager.class, true);
		IVehicleTypesManager vehicleManager = SATC_Activator.getDefault()
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
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(parent, "org.mwc.debrief.help.SATC");

		setActiveSolver(solversManager.getActiveSolver());

	}

	@Override
	public void dispose()
	{
		context.dispose();
		setActiveSolver(null);
		solversManager.removeSolverManagerListener(solverManagerListener);
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
		Composite header = UIUtils.createEmptyComposite(parent, layout,
				new GridData(GridData.FILL_HORIZONTAL));
		UIUtils.createSpacer(header, new GridData(50, SWT.DEFAULT));
		Composite headerNested = UIUtils.createEmptyComposite(header, UIUtils
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

		final ScrolledComposite scrolled = new ScrolledComposite(group,
				SWT.V_SCROLL);
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
		GridLayout layout = new GridLayout(5, false);
		group.setLayoutData(gridData);
		group.setLayout(layout);
		group.setText("Preferences");

		liveConstraints = new Button(group, SWT.TOGGLE);
		liveConstraints.setText("Auto-Recalc of Constraints");
		liveConstraints.setEnabled(false);
		liveConstraints.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));

		recalculate = new Button(group, SWT.DEFAULT);
		recalculate.setText("Calculate Solution");
		recalculate.setEnabled(false);
		recalculate.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (activeSolver != null)
				{
					activeSolver.run(true, true);
					main.setSize(0, 0);
					main.getParent().layout(true, true);
				}
			}
		});
		recalculate.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
				| GridData.VERTICAL_ALIGN_CENTER));

		cancelGeneration = new Button(group, SWT.PUSH);
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

		suppressCuts = new Button(group, SWT.CHECK);
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

		Composite precisionPanel = new Composite(group, SWT.NONE);
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

	private void initUI(Composite parent)
	{
		main = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.verticalSpacing = 2;
		gridLayout.marginLeft = 5;
		gridLayout.marginRight = 5;
		main.setLayout(gridLayout);

		initPreferencesGroup(main);
		initVehicleGroup(main);
		initAnalystContributionsGroup(main);
		initAddContributionGroup(main);
		initPerformanceGraph(main);

		// also sort out the header controls
		final IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		manager.add(SATC_Activator.createOpenHelpAction(
				"org.mwc.debrief.help.SATC", null, this));

	}

	private void initPerformanceGraph(Composite parent)
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
		group.setText("Performance");

		// we need the color black several times
		final Color colorBlack = new Color(Display.getCurrent(), 0, 0, 0);

		// generate the chart
		performanceChart = new Chart(group, SWT.NONE);

		// format the chart
		performanceChart.getLegend().setVisible(false);
		performanceChart.getTitle().setVisible(false);
		performanceChart.setForeground(colorBlack);
		ISeriesSet seriesSet = performanceChart.getSeriesSet();

		// now give the chart our data series
		double[] ySeries =
		{};
		final ILineSeries series = (ILineSeries) seriesSet.createSeries(
				SeriesType.LINE, SERIES_NAME);
		series.enableArea(true);
		series.setYSeries(ySeries);
		series.setLineColor(colorBlack);

		// ok, now for the x axis
		IAxis xAxis = performanceChart.getAxisSet().getXAxis(0);
		xAxis.getTitle().setVisible(false);
		xAxis.adjustRange();
		xAxis.getTick().setForeground(colorBlack);

		// and the y axis
		IAxis yAxis = performanceChart.getAxisSet().getYAxis(0);
		yAxis.adjustRange();
		yAxis.enableLogScale(true);
		yAxis.getTick().setForeground(colorBlack);
		yAxis.getTitle().setForeground(colorBlack);
		yAxis.getTitle().setText("Error Sum");
	}

	private void clearPerformanceGraph()
	{
		ILineSeries ySeries = (ILineSeries) performanceChart.getSeriesSet()
				.getSeries(SERIES_NAME);
		double[] newYVals = new double[]
		{};

		ySeries.setYSeries(newYVals);

		performanceChart.getAxisSet().getXAxis(0).adjustRange();
		performanceChart.getAxisSet().getYAxis(0).adjustRange();
		performanceChart.getAxisSet().getYAxis(0).enableLogScale(true);
		performanceChart.redraw();

	}

	private void addNewPerformanceScore(double value)
	{
		ILineSeries ySeries = (ILineSeries) performanceChart.getSeriesSet()
				.getSeries(SERIES_NAME);
		double[] yVals = ySeries.getYSeries();

		double[] newYVals = new double[yVals.length + 1];
		System.arraycopy(yVals, 0, newYVals, 0, yVals.length);

		if (value < ERROR_SCORE_THRESHOLD)
		{
			newYVals[newYVals.length - 1] = value;
		}
		else
		{
			newYVals[newYVals.length - 1] = 0;
		}

		ySeries.setYSeries(newYVals);

		performanceChart.getAxisSet().getXAxis(0).adjustRange();
		performanceChart.getAxisSet().getYAxis(0).adjustRange();

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
		contributionsChangedListener = UIListener.wrap(parent.getDisplay(),
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
				});
		generateSolutionsListener = UIListener.wrap(parent.getDisplay(),
				IGASolutionsListener.class, new SteppingAdapter()
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
						// check it's roughly suitable
						// if (topScore < 1000000)
						addNewPerformanceScore(topScore);
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
		solverManagerListener = UIListener.wrap(parent.getDisplay(),
				ISolversManagerListener.class, new ISolversManagerListener()
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

		constrainSpaceListener = UIListener.wrap(parent.getDisplay(),
				IConstrainSpaceListener.class, new IConstrainSpaceListener()
				{

					@Override
					public void stepped(IBoundsManager boundsManager, int thisStep,
							int totalSteps)
					{
					}

					@Override
					public void statesBounded(IBoundsManager boundsManager)
					{
						// TODO: force redraw of the items in the container list. The color has now changed!
						contList.redraw();
						contList.update();
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
				activeSolver.getContributions().removeContributionsChangedListener(
						contributionsChangedListener);
				activeSolver.getSolutionGenerator().removeReadyListener(
						generateSolutionsListener);
				activeSolver.getBoundsManager().removeConstrainSpaceListener(
						constrainSpaceListener);

				liveRunningBinding.dispose();
			}
			List<BaseContribution> contributions = new ArrayList<BaseContribution>(
					contributionsControl.keySet());
			for (BaseContribution contribution : contributions)
			{
				removeContribution(contribution, false);
			}
			if (!contList.isDisposed())
			{
				contList.layout();
			}

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
				liveRunningBinding = context.bindValue(WidgetProperties.selection()
						.observe(liveConstraints), BeansObservables.observeValue(
						activeSolver, ISolver.LIVE_RUNNING));
				setPartName(TITLE + " - " + activeSolver.getName());
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
			panel = (BaseContributionView<?>) viewClass.getConstructor(
					Composite.class, contribution.getClass(), IContributions.class)
					.newInstance(contList, contribution, activeSolver.getContributions());
			panel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
					| GridData.GRAB_HORIZONTAL));

			// and rememeber it
			contributionsControl.put(contribution, panel);
			if (doLayout)
			{
				contList.layout();
			}
		}
		catch (Exception ex)
		{
			LogFactory.getLog().error("Failed to generate panel for " + contribution);
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
	}
}
