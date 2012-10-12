package com.planetmayo.debrief.satc.gwt.client.ui;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.gwt.client.contributions.CourseForecastContributionView;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.model.manager.MaintainContributions;
import com.planetmayo.debrief.satc.model.manager.MaintainContributions.MyView;
import com.planetmayo.debrief.satc.support.mock.MockVehicleTypesRepository;

public class ManageSolutionsView extends Composite implements MyView
{

	private static ManageSolutionsViewUiBinder uiBinder = GWT
			.create(ManageSolutionsViewUiBinder.class);

	interface ManageSolutionsViewUiBinder extends
			UiBinder<Widget, ManageSolutionsView>
	{
	}

	private MaintainContributions _manager;

	private static TrackGenerator _stepper;

	public static TrackGenerator getGenerator()
	{
		return _stepper;
	}

	public ManageSolutionsView()
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

	@UiHandler("add")
	void onClick(ClickEvent e)
	{
		contextMenu.showRelativeTo(add);
	}

	@UiHandler(value =
	{ "courseForecastContribution", "speedForecast", "locationForecast" })
	void handleClick(ClickEvent e)
	{
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

	@Override
	public void added(BaseContribution contribution)
	{
		Composite res = null
				;
		// what type is it?
		if (contribution instanceof com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution)
			res = new CourseForecastContributionView();

		// did we find one?
		if (res != null)
			analystContributions.add(res);

	}

	@Override
	public void removed(BaseContribution contribution)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void populateContributionList(ArrayList<String> items)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void populateVehicleTypesList(List<VehicleType> vehicles)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void populatePrecisionsList(Precision[] precisions)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setRemoveContributionListener(PropertyChangeListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setAddContributionListener(PropertyChangeListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setVehicleChangeListener(PropertyChangeListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPrecisionChangeListener(PropertyChangeListener listener)
	{
		// TODO Auto-generated method stub

	}

}
