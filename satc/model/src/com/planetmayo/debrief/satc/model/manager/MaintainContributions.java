package com.planetmayo.debrief.satc.model.manager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.ATBForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.AlterationLegForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.ContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.support.SupportServices;
import com.planetmayo.debrief.satc.support.VehicleTypesRepository;

public class MaintainContributions
{

	public static interface MyView extends ContributionsChangedListener
	{

		/**
		 * show the list of available contributions
		 * 
		 * @param items
		 *          the types of contribution that the user may add
		 */
		public void populateContributionList(ArrayList<String> items);

		/**
		 * populate the list of precisions
		 * 
		 * @param vehicles
		 */
		public void populatePrecisionsList(Precision[] precisions);

		/**
		 * populate the list of vehicle types
		 * 
		 * @param vehicles
		 */
		public void populateVehicleTypesList(List<VehicleType> vehicles);

		/**
		 * allow us to listen to user asking to remove a contribution
		 * 
		 * @param listener
		 */
		public void setAddContributionListener(PropertyChangeListener listener);

		/**
		 * allow us to listen to user asking to remove a contribution
		 * 
		 * @param listener
		 */
		public void setPrecisionChangeListener(PropertyChangeListener listener);

		/**
		 * allow us to listen to user asking to remove a contribution
		 * 
		 * @param listener
		 */
		public void setRemoveContributionListener(PropertyChangeListener listener);

		/**
		 * allow us to listen to user asking to remove a contribution
		 * 
		 * @param listener
		 */
		public void setVehicleChangeListener(PropertyChangeListener listener);

	}

	/**
	 * our track generator
	 * 
	 */
	TrackGenerator _genny;

	public MaintainContributions(MyView myView,
			VehicleTypesRepository vehiclesRepository)
	{
		// sort out our generator
		_genny = new TrackGenerator();

		// ok, let the view start listening to the genny
		_genny.addContributionsListener(myView);

		// ok, config the view
		myView.setRemoveContributionListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				BaseContribution theCont = (BaseContribution) evt.getSource();
				_genny.removeContribution(theCont);
			}
		});

		// populate the dropdowns in the view
		ArrayList<String> items = getContributions();
		myView.populateContributionList(items);
		myView.populatePrecisionsList(getPrecisions());
		myView.populateVehicleTypesList(vehiclesRepository.getAllTypes());

		// ok, now start listening to the view
		myView.setAddContributionListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent arg0)
			{
				// get the string object that contains the name
				String theCont = (String) arg0.getSource();

				addContribution(theCont);
			}
		});
		myView.setPrecisionChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent e)
			{
				// TODO support precision change
			}
		});
		myView.setVehicleChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent e)
			{
				// TODO support vehicle change
			}
		});

	}

	private void addContribution(final String thisCont)
	{
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
		else if (thisCont.equals("Straight Leg Forecast"))
		  _genny.addContribution(new StraightLegForecastContribution());
		else if (thisCont.equals("Alteration Leg Forecast"))
		  _genny.addContribution(new AlterationLegForecastContribution());
		else if (thisCont.equals("ATB Forecast"))
		  _genny.addContribution(new ATBForecastContribution());		
		else
			SupportServices.INSTANCE.getLog().info(
					"Could not find contribution for:" + thisCont);
	}

	private ArrayList<String> getContributions()
	{
		ArrayList<String> res = new ArrayList<String>();
		res.add("Course Forecast");
		res.add("Speed Forecast");
		res.add("Location Forecast");
		res.add("Location Analysis");
		res.add("Straight Leg Forecast");
		res.add("Alteration Leg Forecast");
		res.add("ATB Forecast");		

		// note: the next two don't get added from the manage panel, since they
		// require external data,
		// so they are triggered from the UI that holds the data
		// res.add("Range Forecast");
		// res.add("Bearing Measurement");

		return res;
	}

	public TrackGenerator getGenerator()
	{
		return _genny;
	}

	private com.planetmayo.debrief.satc.model.Precision[] getPrecisions()
	{
		return Precision.values();
	}
}
