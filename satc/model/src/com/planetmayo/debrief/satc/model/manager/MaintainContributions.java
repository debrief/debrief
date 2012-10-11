package com.planetmayo.debrief.satc.model.manager;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.support.SupportServices;
import com.planetmayo.debrief.satc.support.VehicleTypesRepository;

public class MaintainContributions {

	/**
	 * our track generator
	 * 
	 */
	TrackGenerator _genny;

	public MaintainContributions(MaintainContributionsView myView,
			VehicleTypesRepository vehiclesRepository) {
		// sort out our generator
		_genny = new TrackGenerator();

		// ok, let the view start listening to the genny
		_genny.addContributionsListener(myView);

		// ok, config the view
		myView.setRemoveContributionListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				BaseContribution theCont = (BaseContribution) arg0.getSource();
				_genny.removeContribution(theCont);
			}
		});

		// populate the dropdowns in the view
		ArrayList<String> items = getContributions();
		myView.populateContributionList(items);
		myView.populatePrecisionsList(getPrecisions());
		myView.populateVehicleTypesList(vehiclesRepository.getAllTypes());

		// ok, now start listening to the view
		myView.setAddContributionListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				@SuppressWarnings("unused")
				String theCont = (String) arg0.getSource();

				// TODO: find out how to create a new contribution from this
				// name
				CourseForecastContribution newCont = new CourseForecastContribution();

				// ok, create a new one of these
				_genny.addContribution(newCont);
			}
		});
		myView.setPrecisionChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO support precision change
			}
		});
		myView.setVehicleChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO support vehicle change
			}
		});

	}

	private com.planetmayo.debrief.satc.model.Precision[] getPrecisions() {
		return Precision.values();
	}

	private ArrayList<String> getContributions() {
		ArrayList<String> res = new ArrayList<String>();
		res.add("Course Forecast");
		res.add("Speed Forecast");
		res.add("Location Forecast");
		res.add("Location Analysis");

		// note: the next two don't get added from the manage panel, since they
		// require external data,
		// so they are triggered from the UI that holds the data
		// res.add("Range Forecast");
		// res.add("Bearing Measurement");

		return res;
	}

	public TrackGenerator getGenerator() {
		return _genny;
	}

	public void addContribution(final String thisCont) {
		// ok, what type is it?
		if (thisCont.equals("Course Forecast"))
			_genny.addContribution(new CourseForecastContribution());
		else if (thisCont.equals("Speed Forecast"))
			_genny.addContribution(new SpeedForecastContribution());
		else if (thisCont.equals("Location Forecast"))
			_genny.addContribution(new LocationForecastContribution());
		else if (thisCont.equals("Range Forecast"))
			_genny.addContribution(new RangeForecastContribution());
		else if (thisCont.equals("Bearing Measurement"))
			_genny.addContribution(new BearingMeasurementContribution());
		else if (thisCont.equals("Location Analysis"))
			_genny.addContribution(new LocationAnalysisContribution());
		else
			SupportServices.INSTANCE.getLog().info(
					"Could not find contribution for:" + thisCont);
	}
}
