package com.planetmayo.debrief.satc.model.manager;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.ContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.services.VehicleTypesRepository;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class MaintainContributions
{

	/**
	 * our track generator
	 * 
	 */
	TrackGenerator _genny;

	public MaintainContributions(MyView myView)
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
		ArrayList<Object> items = getContributions();
		myView.populateContributionList(items);
		myView.populatePrecisionsList(getPrecisions());
		VehicleTypesRepository vehiclesRepository = SATC_Activator.getDefault()
				.getService(VehicleTypesRepository.class, true);
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

	private Precision[] getPrecisions()
	{
		return Precision.values();
	}

	private ArrayList<Object> getContributions()
	{
		ArrayList<Object> res = new ArrayList<Object>();
		res.add(CourseForecastContribution.class);
		res.add(SpeedForecastContribution.class);
		res.add(BearingMeasurementContribution.class);

		return res;
	}

	public static interface MyView extends ContributionsChangedListener
	{

		/**
		 * show the list of available contributions
		 * 
		 * @param _items
		 */
		public void populateContributionList(ArrayList<Object> items);

		/**
		 * populate the list of vehicle types
		 * 
		 * @param vehicles
		 */
		public void populateVehicleTypesList(List<VehicleType> vehicles);

		/**
		 * populate the list of precisions
		 * 
		 * @param vehicles
		 */
		public void populatePrecisionsList(Precision[] precisions);

		/**
		 * allow us to listen to user asking to remove a contribution
		 * 
		 * @param listener
		 */
		public void setRemoveContributionListener(ChangeListener listener);

		/**
		 * allow us to listen to user asking to remove a contribution
		 * 
		 * @param listener
		 */
		public void setAddContributionListener(ChangeListener listener);

		/**
		 * allow us to listen to user asking to remove a contribution
		 * 
		 * @param listener
		 */
		public void setVehicleChangeListener(ChangeListener listener);

		/**
		 * allow us to listen to user asking to remove a contribution
		 * 
		 * @param listener
		 */
		public void setPrecisionChangeListener(ChangeListener listener);

	}

	public TrackGenerator getGenerator()
	{
		return _genny;
	}
}
