package com.planetmayo.debrief.satc.gwt.client.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.gwt.client.contributions.AnalysisContributionView;
import com.planetmayo.debrief.satc.gwt.client.contributions.BearingMeasurementContributionView;
import com.planetmayo.debrief.satc.gwt.client.contributions.ContributionView;
import com.planetmayo.debrief.satc.gwt.client.contributions.CourseForecastContributionView;
import com.planetmayo.debrief.satc.gwt.client.contributions.LocationForecastContributionView;
import com.planetmayo.debrief.satc.gwt.client.contributions.RangeForecastContributionView;
import com.planetmayo.debrief.satc.gwt.client.contributions.SpeedForecastContributionView;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.ContributionBuilder;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.BoundsManager;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc.model.manager.MaintainContributions;
import com.planetmayo.debrief.satc.model.manager.MaintainContributions.MyView;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.support.mock.MockVehicleTypesRepository;

public class MaintainContributionsView extends Composite implements MyView,
		IContributionsChangedListener
{

	interface ManageSolutionsViewUiBinder extends
			UiBinder<Widget, MaintainContributionsView>
	{
	}

	private static ManageSolutionsViewUiBinder uiBinder = GWT
			.create(ManageSolutionsViewUiBinder.class);

	private MaintainContributions _manager;

	private static BoundsManager _stepper;

	public static BoundsManager getGenerator()
	{
		return _stepper;
	}

	@UiField
	ListBox vehicleTypes;

	@UiField
	HorizontalPanel header;

	@UiField
	Anchor active;

	@UiField
	Anchor estimate;

	@UiField
	Anchor hardConstraints;

	@UiField
	Anchor weighting;

	@UiField
	Button add;

	@UiField
	PopupPanel contextMenu;
	
	@UiField HTMLPanel contextMenuList;

	@UiField
	HTMLPanel analystContributions;

	private PropertyChangeListener _addListener;

	private HashMap<BaseContribution, IsWidget> _uiInstances = new HashMap<BaseContribution, IsWidget>();

	private PropertyChangeListener _vehicleListener;

	private List<VehicleType> _theVehicleTypes;

	public MaintainContributionsView()
	{

		initWidget(uiBinder.createAndBindUi(this));
		header.setCellWidth(active, "20%");
		header.setCellWidth(estimate, "30%");
		header.setCellWidth(hardConstraints, "30%");
		header.setCellWidth(weighting, "20%");

		// now the the data object
		_manager = new MaintainContributions(this, new MockVehicleTypesRepository());
		_stepper = _manager.getGenerator();
	}


	@Override
	public void added(BaseContribution contribution)
	{
		ContributionView res = null;

		// what type is it?
		if (contribution instanceof CourseForecastContribution)
			res = new CourseForecastContributionView();
		else if (contribution instanceof SpeedForecastContribution)
			res = new SpeedForecastContributionView();
		else if (contribution instanceof LocationForecastContribution)
			res = new LocationForecastContributionView();
		else if (contribution instanceof RangeForecastContribution)
			res = new RangeForecastContributionView();
		else if (contribution instanceof BearingMeasurementContribution)
			res = new BearingMeasurementContributionView();
		else if (contribution instanceof LocationAnalysisContribution)
			res = new AnalysisContributionView();

		// did we find one?
		if (res != null)
		{
			// give the contribution to the viewer
			res.setData(contribution);

			// TODO refactor the contribution views, so that they have a setter for
			// the contribution.
			// in there, they show the cont's data values, and listen out for
			// contribution changes.
			// if you have a go at CourseContributionView - we can discuss the
			// implementation

			// remember this UI with the contribution
			_uiInstances.put(contribution, res);

			// and display it
			analystContributions.add(res);
		}
		else
		{
			System.err.println("Contribution view missing for:" + contribution);
		}

	}

	@UiHandler("vehicleTypes")
	void onChange(ChangeEvent evt)
	{
		// get the particular type
		VehicleType vType = _theVehicleTypes.get(vehicleTypes.getSelectedIndex());
		PropertyChangeEvent pce = new PropertyChangeEvent(vehicleTypes,
				ProblemSpace.VEHICLE_TYPE, null, vType);
		_vehicleListener.propertyChange(pce);
	}

	@UiHandler("add")
	void onClick(ClickEvent e)
	{
		contextMenu.showRelativeTo(add);
	}

	@Override
	public void populateContributionList(List<ContributionBuilder> items)
	{
		for (final ContributionBuilder contributionBuilder : items)
		{
			Label contribution = new Label(contributionBuilder.getDescription());
			contribution.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					_addListener.propertyChange(new PropertyChangeEvent(contributionBuilder, null, null, contributionBuilder));
					contextMenu.hide();
				}
			});
			
			contextMenuList.add(contribution);
		}
	
	}

	@Override
	public void populatePrecisionsList(Precision[] precisions)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void populateVehicleTypesList(List<VehicleType> vehicles)
	{
		_theVehicleTypes = vehicles;
		vehicleTypes.clear();
		Iterator<VehicleType> iter = vehicles.iterator();
		while (iter.hasNext())
		{
			VehicleType vType = (VehicleType) iter.next();
			vehicleTypes.addItem(vType.getName());
		}
	}

	@Override
	public void removed(BaseContribution contribution)
	{
		// lookup the UI for this contribution, and delete it

		// remember this UI with the contribution
		IsWidget thisWidget = _uiInstances.get(contribution);

		// and display it
		if (thisWidget != null)
			analystContributions.remove(thisWidget);
		else
			System.err.println("failed to find contriubtion for:" + contribution);
	}

	@Override
	public void setAddContributionListener(PropertyChangeListener listener)
	{
		_addListener = listener;
	}

	@Override
	public void setPrecisionChangeListener(PropertyChangeListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setRemoveContributionListener(PropertyChangeListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setVehicleChangeListener(PropertyChangeListener listener)
	{
		_vehicleListener = listener;
	}

}
