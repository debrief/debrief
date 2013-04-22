package com.planetmayo.debrief.satc_rcp.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.ATBForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.ContributionBuilder;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.generator.SteppingAdapter;
import com.planetmayo.debrief.satc.model.manager.IContributionsManager;
import com.planetmayo.debrief.satc.model.manager.IVehicleTypesManager;
import com.planetmayo.debrief.satc.support.SupportServices;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.planetmayo.debrief.satc_rcp.ui.UIListener;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.contributions.ATBForecastContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.AnalysisContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.BaseContributionView;
import com.planetmayo.debrief.satc_rcp.ui.contributions.BearingMeasurementContributionView;
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
public class MaintainContributionsView extends ViewPart implements
		IContributionsChangedListener
{

	public static final String ID = "com.planetmayo.debrief.satc.views.MaintainContributionsView";

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
		
// Note: these views have been commented out, since they have no analyst involvement
//		CONTRIBUTION_PANELS.put(LocationAnalysisContribution.class,
//				AnalysisContributionView.class);
//		CONTRIBUTION_PANELS.put(CourseAnalysisContribution.class,
//				AnalysisContributionView.class);
//		CONTRIBUTION_PANELS.put(SpeedAnalysisContribution.class,
//				AnalysisContributionView.class);
		CONTRIBUTION_PANELS.put(LocationForecastContribution.class,
				LocationForecastContributionView.class);
		CONTRIBUTION_PANELS.put(RangeForecastContribution.class,
				RangeForecastContributionView.class);
		CONTRIBUTION_PANELS.put(SpeedForecastContribution.class,
				SpeedContributionView.class);
		CONTRIBUTION_PANELS.put(StraightLegForecastContribution.class,
				StraightLegForecastContributionView.class);
	}

	private Composite main;

	private Button generateSolutions;
	private Button cancelGeneration;
//	private Button displaySolutions;
	private ComboViewer precisionsCombo;
	private ComboViewer vehiclesCombo;
	private Composite contList;
	private Menu _addContMenu;

	private ISolver solver;

	/**
	 * remember which contributions we're displaying
	 * 
	 */
	private HashMap<BaseContribution, BaseContributionView<?>> _myControls = new HashMap<BaseContribution, BaseContributionView<?>>();

	/** the solution generator
	 * 
	 */
	private IContributionsChangedListener contributionsChangedListener;
	
	private IGenerateSolutionsListener generateSolutionsListener;

	@Override
	public void added(BaseContribution contribution)
	{
		// ok, create a wrapper for this
		BaseContributionView<?> panel = null;
		if (!CONTRIBUTION_PANELS.containsKey(contribution.getClass()))
		{
			SupportServices.INSTANCE.getLog().error(
					"Failed to generate panel for " + contribution);
			return;
		}
		try
		{
			Class<?> viewClass = CONTRIBUTION_PANELS.get(contribution.getClass());
			panel = (BaseContributionView<?>) viewClass.getConstructor(
					Composite.class, contribution.getClass()).newInstance(contList,
					contribution);
			panel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
					| GridData.GRAB_HORIZONTAL));

			// and rememeber it
			_myControls.put(contribution, panel);

			// ok, redo the layout...
			contList.layout();
		}
		catch (Exception ex)
		{
			SupportServices.INSTANCE.getLog().error(
					"Failed to generate panel for " + contribution);
		}
	}

	@Override
	public void removed(BaseContribution contribution)
	{
		// get the panel
		BaseContributionView<?> panel = _myControls.get(contribution);

		// did we find it?
		if (panel != null)
		{
			// and remove it
			panel.dispose();

			// and forget it
			_myControls.remove(contribution);
		}
		else
		{
			System.err.println("failed to find UI for:" + contribution);
		}
		
		contList.layout(true);
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		IContributionsManager contributionsManager = SATC_Activator.getDefault()
				.getService(IContributionsManager.class, true);
		IVehicleTypesManager vehicleManager = SATC_Activator.getDefault()
				.getService(IVehicleTypesManager.class, true);
		solver = SATC_Activator.getDefault().getService(
				ISolver.class, true);

		initUI(parent);
		populateContributionList(contributionsManager.getAvailableContributions());
		populatePrecisionsList(Precision.values());
		populateVehicleTypesList(vehicleManager.getAllTypes());

		contributionsChangedListener = UIListener.wrap(parent.getDisplay(), 
				IContributionsChangedListener.class, this);
		generateSolutionsListener = UIListener.wrap(parent.getDisplay(), 
				IGenerateSolutionsListener.class, new SteppingAdapter() {

					@Override
					public void startingGeneration()
					{
						UIUtils.setEnabled(parent, false);
						cancelGeneration.setVisible(true);
						cancelGeneration.setEnabled(true);
					}

					@Override
					public void finishedGeneration()
					{
						UIUtils.setEnabled(parent, true);
						cancelGeneration.setVisible(false);
					}
		});
		solver.getContributions().addContributionsChangedListener(contributionsChangedListener);
		solver.getSolutionGenerator().addReadyListener(generateSolutionsListener);
	}

	@Override
	public void dispose()
	{
		solver.getContributions().removeContributionsChangedListener(contributionsChangedListener);
		solver.getSolutionGenerator().removeReadyListener(generateSolutionsListener);
		super.dispose();
	}

	@Override
	public void setFocus()
	{
	}

	private void fillAnalystContributionsGroup(Composite parent)
	{
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		Composite header = UIUtils.createEmptyComposite(parent, layout,
				new GridData(GridData.FILL_HORIZONTAL));
		UIUtils.createSpacer(header, new GridData(40, SWT.DEFAULT));
		Composite headerNested = UIUtils.createEmptyComposite(header, UIUtils
				.createGridLayoutWithoutMargins(4, true), new GridData(
				GridData.FILL_HORIZONTAL));
		UIUtils.createLabel(headerNested, SWT.CENTER, "Active", new GridData(
				GridData.FILL_HORIZONTAL));
		UIUtils.createLabel(headerNested, SWT.CENTER, "Hard constraints",
				new GridData(GridData.FILL_HORIZONTAL));
		UIUtils.createLabel(headerNested, SWT.CENTER, "Estimate", new GridData(
				GridData.FILL_HORIZONTAL));
		UIUtils.createLabel(headerNested, SWT.CENTER, "Weight", new GridData(
				GridData.HORIZONTAL_ALIGN_END));
		UIUtils.createSpacer(header, new GridData(40, SWT.DEFAULT));

	}

	private void initAddContributionGroup(Composite parent)
	{
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginWidth = 5;
		fillLayout.marginHeight = 5;
		group.setLayout(fillLayout);
		group.setLayoutData(gridData);
		group.setText("New Contribution");

		_addContMenu = new Menu(group);
		final ToolBar toolBar = new ToolBar(group, SWT.NONE);
		toolBar.setBounds(50, 50, 50, 50);
		final ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);
		item.setText("Add...");
		item.addListener(SWT.Selection, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				if (event.detail == SWT.ARROW)
				{
					Rectangle rect = item.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = toolBar.toDisplay(pt);
					_addContMenu.setLocation(pt.x, pt.y);
					_addContMenu.setVisible(true);
				}
			}
		});

	}

	private void initAnalystContributionsGroup(Composite parent)
	{
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new FillLayout(SWT.VERTICAL));
		group.setLayoutData(gridData);
		group.setText("Analyst Contributions");

		final ScrolledComposite scrolled = new ScrolledComposite(group,
				SWT.V_SCROLL);
		contList = UIUtils.createScrolledBody(scrolled, SWT.NONE);
		contList.setLayout(new GridLayout(1, false));

		fillAnalystContributionsGroup(contList);
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
		GridLayout layout = new GridLayout(3, false);
		group.setLayoutData(gridData);
		group.setLayout(layout);
		group.setText("Preferences");

		generateSolutions = new Button(group, SWT.CHECK);
		generateSolutions.setText("Auto generate solutions");
		generateSolutions.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{				
				solver.setAutoGenerateSolutions(generateSolutions.getSelection());
			}
			
		});
		generateSolutions.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		
		cancelGeneration = new Button(group, SWT.PUSH);
		cancelGeneration.setText("Cancel");
		cancelGeneration.setVisible(false);
		cancelGeneration.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				solver.cancel();
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
				solver.getSolutionGenerator().setPrecision(precision);
			}
		});

//		displaySolutions = new Button(group, SWT.CHECK);
//		displaySolutions.setText("Display Solutions");
//		displaySolutions.setLayoutData(new GridData(
//				GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
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
	}

	private void initVehicleGroup(Composite parent)
	{
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginWidth = 5;
		fillLayout.marginHeight = 5;
		group.setLayout(fillLayout);
		group.setLayoutData(gridData);
		group.setText("Vehicle");

		vehiclesCombo = new ComboViewer(group);
		vehiclesCombo.setContentProvider(new ArrayContentProvider());
		vehiclesCombo.setLabelProvider(new LabelProvider()
		{

			@Override
			public String getText(Object element)
			{
				return ((VehicleType) element).getName();
			}
		});
		vehiclesCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{

			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				if (solver != null)
				{
					ISelection selection = event.getSelection();
					if (selection instanceof StructuredSelection)
					{
						VehicleType type = (VehicleType) ((StructuredSelection) selection)
								.getFirstElement();
						solver.setVehicleType(type);
					}
				}
			}
		});
	}

	public void populateContributionList(List<ContributionBuilder> items)
	{
		for (final ContributionBuilder item : items)
		{
			MenuItem menuItem = new MenuItem(_addContMenu, SWT.PUSH);
			menuItem.setText(item.getDescription());
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent arg0)
				{
					solver.getContributions().addContribution(item.create());
				}
			});
		}
	}

	public void populatePrecisionsList(Precision[] precisions)
	{
		precisionsCombo.setInput(precisions);
		// and set an initial value
		precisionsCombo.setSelection(new StructuredSelection(precisions[0]));
	}

	public void populateVehicleTypesList(List<VehicleType> vehicles)
	{
		vehiclesCombo.setInput(vehicles);

		// ok, try to set the first one
		if (vehicles.size() > 0)
			vehiclesCombo.setSelection(new StructuredSelection(vehicles.iterator()
					.next()));
	}
}
