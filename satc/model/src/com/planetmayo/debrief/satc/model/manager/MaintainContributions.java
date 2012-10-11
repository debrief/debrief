package com.planetmayo.debrief.satc.model.manager;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.support.VehicleTypesRepository;

public class MaintainContributions
{

	/**
	 * our track generator
	 * 
	 */
	TrackGenerator _genny;

	public MaintainContributions(MaintainContributionsView myView, VehicleTypesRepository vehiclesRepository)
	{
		// sort out our generator
		_genny = new TrackGenerator();

		// ok, let the view start listening to the genny
		_genny.addContributionsListener(myView);

		// ok, config the view
		myView.setRemoveContributionListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				BaseContribution theCont = (BaseContribution) arg0.getSource();
				_genny.removeContribution(theCont);
			}
		});

		// populate the dropdowns in the view
		ArrayList<Class<? extends BaseContribution>> items = getContributions();
		myView.populateContributionList(items);
		myView.populatePrecisionsList(getPrecisions());
		myView.populateVehicleTypesList(vehiclesRepository.getAllTypes());

		// ok, now start listening to the view
		myView.setAddContributionListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				@SuppressWarnings("unused")
				String theCont = (String) arg0.getSource();

				// TODO: find out how to create a new contribution from this name
				CourseForecastContribution newCont = new CourseForecastContribution();

				// ok, create a new one of these
				_genny.addContribution(newCont);
			}
		});
		myView.setPrecisionChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				// TODO support precision change
			}
		});
		myView.setVehicleChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				// TODO support vehicle change
			}
		});

	}

	private com.planetmayo.debrief.satc.model.Precision[] getPrecisions()
	{
		return Precision.values();
	}

	private ArrayList<Class<? extends BaseContribution>> getContributions()
	{
		ArrayList<Class<? extends BaseContribution>> res = new ArrayList<Class<? extends BaseContribution>>();
		res.add(CourseForecastContribution.class);
		res.add(SpeedForecastContribution.class);
		res.add(BearingMeasurementContribution.class);

		return res;
	}

	public TrackGenerator getGenerator()
	{
		return _genny;
	}
}
