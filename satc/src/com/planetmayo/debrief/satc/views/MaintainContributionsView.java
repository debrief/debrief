package com.planetmayo.debrief.satc.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.planetmayo.debrief.satc.Activator;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.services.VehicleTypesRepository;
import com.planetmayo.debrief.satc.ui.contributions.CourseContributionPanel;
import com.planetmayo.debrief.satc.ui.contributions.LocationContributionPanel;
import com.planetmayo.debrief.satc.ui.contributions.SpeedContributionPanel;

public class MaintainContributionsView extends ViewPart
{
	public static final String ID = "com.planetmayo.debrief.satc.views.MaintainContributionsView";

	private Composite main;

	private Button displayBoundedStates;
	private Button displaySolutions;
	private ComboViewer precisionsCombo;
	private ComboViewer vehiclesCombo;

	private VehicleTypesRepository vehiclesRepository;

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);
		vehiclesRepository = Activator.getDefault().getService(
				VehicleTypesRepository.class, true);
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

	private void initAnalystContributionsGroup(Composite parent)
	{
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		GridLayout fillLayout = new GridLayout(1, false);
		group.setLayout(fillLayout);
		group.setLayoutData(gridData);
		group.setText("Analyst Contributions");

		// create a sample speed forecast contribution
		SpeedForecastContribution speedContribution = SpeedForecastContribution
				.getSample();

		// and a UI to display it
		new SpeedContributionPanel(group, speedContribution)
				.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
						| GridData.GRAB_HORIZONTAL));

		// create a sample course forecast contribution
		CourseForecastContribution courseContribution = CourseForecastContribution
				.getSample();

		// and a UI to display it
		new CourseContributionPanel(group, courseContribution)
				.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
						| GridData.GRAB_HORIZONTAL));

		// and a sample location
		LocationForecastContribution locationContribution = LocationForecastContribution
				.getSample();
		
		// and a UI to display it
		new LocationContributionPanel(group, locationContribution).setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
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

	private void initValues()
	{
		precisionsCombo.setInput(Precision.values());
		precisionsCombo.setSelection(new StructuredSelection(Precision.FINE));

		vehiclesCombo.setInput(vehiclesRepository.getAllTypes().toArray());
	}

	@Override
	public void createPartControl(Composite parent)
	{
		initUI(parent);
		initValues();
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

}
