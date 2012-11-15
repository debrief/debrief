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
import com.planetmayo.debrief.satc.model.contributions.ContributionBuilder;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.ContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
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
		public void populateContributionList(List<ContributionBuilder> items);

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
		ArrayList<ContributionBuilder> items = getContributions();
		myView.populateContributionList(items);
		myView.populatePrecisionsList(getPrecisions());
		myView.populateVehicleTypesList(vehiclesRepository.getAllTypes());

		// ok, now start listening to the view
		myView.setAddContributionListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent event)
			{
				ContributionBuilder builder = (ContributionBuilder) event.getSource();
				_genny.addContribution(builder.create());
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

	private ArrayList<ContributionBuilder> getContributions()
	{
		ArrayList<ContributionBuilder> result = new ArrayList<ContributionBuilder>();
		result.add(new ContributionBuilder("Course Forecast")
		{
			
			@Override
			public BaseContribution create()
			{
				return new CourseForecastContribution();
			}
		});
		result.add(new ContributionBuilder("Speed Forecast")
		{			
			@Override
			public BaseContribution create()
			{
				return new SpeedForecastContribution();
			}
		});
		result.add(new ContributionBuilder("Location Forecast")
		{			
			@Override
			public BaseContribution create()
			{
				return new LocationForecastContribution();
			}
		});
		result.add(new ContributionBuilder("Location Analysis")
		{			
			@Override
			public BaseContribution create()
			{
				return new LocationAnalysisContribution();
			}
		});
		result.add(new ContributionBuilder("Straight Leg Forecast")
		{			
			@Override
			public BaseContribution create()
			{
				return new StraightLegForecastContribution();
			}
		});
		result.add(new ContributionBuilder("Alteration Leg Forecast")
		{			
			@Override
			public BaseContribution create()
			{
				return new AlterationLegForecastContribution();
			}
		});
		result.add(new ContributionBuilder("ATB Forecast")
		{			
			@Override
			public BaseContribution create()
			{
				return new ATBForecastContribution();
			}
		});	
		return result;
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
