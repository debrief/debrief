package com.planetmayo.debrief.satc_rcp.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.ChangeListener;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc_rcp.manager.MaintainContributions;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.contributions.AnalystContributionPanel;
import com.planetmayo.debrief.satc_rcp.ui.contributions.BearingMeasurementContributionPanel;
import com.planetmayo.debrief.satc_rcp.ui.contributions.CourseContributionPanel;
import com.planetmayo.debrief.satc_rcp.ui.contributions.LocationAnalysisContributionPanel;
import com.planetmayo.debrief.satc_rcp.ui.contributions.LocationContributionPanel;
import com.planetmayo.debrief.satc_rcp.ui.contributions.RangeForecastContributionPanel;
import com.planetmayo.debrief.satc_rcp.ui.contributions.SpeedContributionPanel;

/**
 * mock class to test high level application flows
 * 
 * @author ian
 * 
 */
public class MockMaintainContributionsView extends ViewPart implements
		MaintainContributions.MyView
{
	public static final String ID = "com.planetmayo.debrief.satc.views.MaintainContributionsView";

	private Composite main;

	private Button displayBoundedStates;
	private Button displaySolutions;
	private ComboViewer precisionsCombo;
	private ComboViewer vehiclesCombo;

	private Composite _contList;

	private MaintainContributions _manager;

	/**
	 * remember which contributions we're displaying
	 * 
	 */
	private HashMap<BaseContribution, AnalystContributionPanel> _myControls = new HashMap<BaseContribution, AnalystContributionPanel>();

	@Override
	public void createPartControl(Composite parent)
	{

		// build the UI
		initUI(parent);

		// create the manager
		_manager = new MaintainContributions(this);

	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		if (adapter.equals(TrackGenerator.class))
			return _manager.getGenerator();
		else
			return super.getAdapter(adapter);
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
				.createGridLayoutWithoutMargins(3, true), new GridData(
				GridData.FILL_HORIZONTAL));
		UIUtils.createLabel(headerNested, SWT.CENTER, "Active", new GridData(
				GridData.FILL_HORIZONTAL));
		UIUtils.createLabel(headerNested, SWT.CENTER, "Hard constraints",
				new GridData(GridData.FILL_HORIZONTAL));
		UIUtils.createLabel(headerNested, SWT.CENTER, "Estimate", new GridData(
				GridData.FILL_HORIZONTAL));
		UIUtils.createLabel(header, SWT.RIGHT, "Weight", new GridData(40,
				SWT.DEFAULT));

		// // create a sample speed forecast contribution
		// BaseContribution speedContribution =
		// SpeedForecastContribution.getSample();
		//
		// // and a UI to display it
		// new SpeedContributionPanel(parent, speedContribution)
		// .setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
		// | GridData.GRAB_HORIZONTAL));
		//
		// // create a sample course forecast contribution
		// CourseForecastContribution courseContribution =
		// CourseForecastContribution
		// .getSample();
		//
		// // and a UI to display it
		// new CourseContributionPanel(parent, courseContribution)
		// .setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
		// | GridData.GRAB_HORIZONTAL));
		//
		// // and a sample location
		// LocationForecastContribution locationContribution =
		// LocationForecastContribution
		// .getSample();
		//
		// // and a UI to display it
		// new LocationContributionPanel(parent, locationContribution)
		// .setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
		// | GridData.GRAB_HORIZONTAL));
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);

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
		_contList = UIUtils.createScrolledBody(scrolled, SWT.NONE);
		_contList.setLayout(new GridLayout(1, false));

		fillAnalystContributionsGroup(_contList);
		scrolled.addListener(SWT.Resize, new Listener()
		{

			@Override
			public void handleEvent(Event e)
			{
				scrolled.setMinSize(_contList.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});
		scrolled.setAlwaysShowScrollBars(true);
		scrolled.setContent(_contList);
		scrolled.setMinSize(_contList.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);
	}

	private void initPreferencesGroup(Composite parent)
	{
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		GridLayout layout = new GridLayout(2, true);
		group.setLayoutData(gridData);
		group.setLayout(layout);
		group.setText("Preferences");

		displayBoundedStates = new Button(group, SWT.CHECK);
		displayBoundedStates.setText("Display Bounded States");
		displayBoundedStates.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));

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

		displaySolutions = new Button(group, SWT.CHECK);
		displaySolutions.setText("Display Solutions");
		displaySolutions.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
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
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void added(BaseContribution contribution)
	{
		// ok, create a wrapper for this
		AnalystContributionPanel panel = null;

		if (contribution instanceof CourseForecastContribution)
			panel = new CourseContributionPanel(_contList, contribution);
		else if (contribution instanceof LocationForecastContribution)
			panel = new LocationContributionPanel(_contList, contribution);
		else if (contribution instanceof SpeedForecastContribution)
			panel = new SpeedContributionPanel(_contList, contribution);
		else if (contribution instanceof BearingMeasurementContribution)
			panel = new BearingMeasurementContributionPanel(_contList, contribution);
		else if (contribution instanceof RangeForecastContribution)
			panel = new RangeForecastContributionPanel(_contList, contribution);
		else if (contribution instanceof LocationAnalysisContribution)
			panel = new LocationAnalysisContributionPanel(_contList, contribution);

		// did we fail to find a panel?
		if (panel == null)
		{
			System.err.println("Failed to generate panel for " + contribution);
		}
		else
		{
			// sort out the layout
			panel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
					| GridData.GRAB_HORIZONTAL));

			// and rememeber it
			_myControls.put(contribution, panel);

			// ok, redo the layout...
			_contList.layout();
		}
	}

	@Override
	public void removed(BaseContribution contribution)
	{
		// get the panel
		AnalystContributionPanel panel = _myControls.get(contribution);

		// did we find it?
		if (panel != null)
		{
			// and remove it
			panel.getControl().dispose();

			// and forget it
			_myControls.remove(contribution);
		}
		else
		{
			System.err.println("failed to find UI for:" + contribution);
		}
	}

	@Override
	public void populateContributionList(ArrayList<Object> items)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void populateVehicleTypesList(List<VehicleType> vehicles)
	{
		vehiclesCombo.setInput(vehicles);
	}

	@Override
	public void populatePrecisionsList(Precision[] precisions)
	{
		precisionsCombo.setInput(precisions);
		// and set an initial value
		precisionsCombo.setSelection(new StructuredSelection(precisions[0]));
	}

	@Override
	public void setRemoveContributionListener(ChangeListener listener)
	{
		// TODO: support removing an item from the list
	}

	@Override
	public void setAddContributionListener(ChangeListener listener)
	{
		// TODO: tie in the add button to this
	}

	@Override
	public void setVehicleChangeListener(ChangeListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPrecisionChangeListener(ChangeListener listener)
	{
		// TODO Auto-generated method stub

	}

}
