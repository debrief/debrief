package com.planetmayo.debrief.satc.gwt.client.ui;

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
import com.planetmayo.debrief.satc.gwt.client.Gwt;
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
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.ISolver;

public class MaintainContributionsView extends Composite implements IContributionsChangedListener
{

	interface ManageSolutionsViewUiBinder extends
			UiBinder<Widget, MaintainContributionsView>
	{
	}

	private static ManageSolutionsViewUiBinder uiBinder = GWT
			.create(ManageSolutionsViewUiBinder.class);

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
	
	private ISolver solver;

	private HashMap<BaseContribution, IsWidget> _uiInstances = new HashMap<BaseContribution, IsWidget>();

	private List<VehicleType> _theVehicleTypes;

	public MaintainContributionsView()
	{
		solver = Gwt.getInstance().getBoundsManager();
		initWidget(uiBinder.createAndBindUi(this));
		header.setCellWidth(active, "20%");
		header.setCellWidth(estimate, "30%");
		header.setCellWidth(hardConstraints, "30%");
		header.setCellWidth(weighting, "20%");
		
		populateContributionList(Gwt.getInstance().getContributionsManager().getAvailableContributions());
		populatePrecisionsList(Precision.values());
		populateVehicleTypesList(Gwt.getInstance().getVehicleTypesManager().getAllTypes());
		solver.getContributions().addContributionsChangedListener(this);
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
		else if (contribution instanceof SpeedAnalysisContribution)
			res = new AnalysisContributionView();
		else if (contribution instanceof CourseAnalysisContribution)
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
		solver.setVehicleType(vType);		
	}

	@UiHandler("add")
	void onClick(ClickEvent e)
	{
		contextMenu.showRelativeTo(add);
	}

	public void populateContributionList(List<ContributionBuilder> items)
	{
		// clear the list to start wtih
		contextMenuList.clear();
		
		for (final ContributionBuilder contributionBuilder : items)
		{
			Label contribution = new Label(contributionBuilder.getDescription());
			contribution.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					solver.getContributions().addContribution(contributionBuilder.create());
					contextMenu.hide();
				}
			});
			
			contextMenuList.add(contribution);
		}
	
	}

	public void populatePrecisionsList(Precision[] precisions)
	{
		// TODO Auto-generated method stub

	}

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
}
