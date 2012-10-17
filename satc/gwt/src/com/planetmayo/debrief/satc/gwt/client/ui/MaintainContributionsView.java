package com.planetmayo.debrief.satc.gwt.client.ui;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
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
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.ContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.model.manager.MaintainContributions;
import com.planetmayo.debrief.satc.model.manager.MaintainContributions.MyView;
import com.planetmayo.debrief.satc.support.mock.MockVehicleTypesRepository;

public class MaintainContributionsView extends Composite implements MyView,
		ContributionsChangedListener
{

	interface ManageSolutionsViewUiBinder extends
			UiBinder<Widget, MaintainContributionsView>
	{
	}

	private static ManageSolutionsViewUiBinder uiBinder = GWT
			.create(ManageSolutionsViewUiBinder.class);

	private MaintainContributions _manager;

	private static TrackGenerator _stepper;

	public static TrackGenerator getGenerator()
	{
		return _stepper;
	}

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

	@UiField
	Label courseForecastContribution;

	@UiField
	Label speedForecast;

	@UiField
	Label locationForecast;

	@UiField
	HTMLPanel analystContributions;

	@SuppressWarnings("unused")
	private PropertyChangeListener _addListener;

	private HashMap<BaseContribution, IsWidget> _uiInstances = new HashMap<BaseContribution, IsWidget>();

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

	@UiHandler(value =
	{ "courseForecastContribution", "speedForecast", "locationForecast" })
	void handleClick(ClickEvent e)
	{
		// TODO: create custom handler for the 'add' dropdown menu. Handle it by
		// passing
		// the String to the _addListener as PropertyChangeEvent(theString, null,
		// null, theString);

		contextMenu.hide();
		if ((Label) e.getSource() == courseForecastContribution)
		{
			analystContributions.add(new CourseForecastContributionView());
		}
		else if ((Label) e.getSource() == speedForecast)
		{
			Window.alert(((Label) e.getSource()).getText());

		}
		else if ((Label) e.getSource() == locationForecast)
		{
			Window.alert(((Label) e.getSource()).getText());

		}
	}

	@UiHandler("add")
	void onClick(ClickEvent e)
	{
		contextMenu.showRelativeTo(add);
	}

	@Override
	public void populateContributionList(ArrayList<String> items)
	{
		// TODO use these strings to populate the drop-down list of new contribution
		// types

	}

	@Override
	public void populatePrecisionsList(Precision[] precisions)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void populateVehicleTypesList(List<VehicleType> vehicles)
	{
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

}
