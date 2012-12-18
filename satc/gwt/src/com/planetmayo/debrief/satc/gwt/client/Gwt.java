package com.planetmayo.debrief.satc.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.RootPanel;
import com.planetmayo.debrief.satc.gwt.client.services.GWTUtilsService;
import com.planetmayo.debrief.satc.gwt.client.services.GWTIOService;
import com.planetmayo.debrief.satc.gwt.client.services.GWTLogService;
import com.planetmayo.debrief.satc.gwt.client.ui.RootLayout;
import com.planetmayo.debrief.satc.model.generator.BoundsManager;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.manager.IContributionsManager;
import com.planetmayo.debrief.satc.model.manager.IVehicleTypesManager;
import com.planetmayo.debrief.satc.model.manager.impl.ContributionsManagerImpl;
import com.planetmayo.debrief.satc.model.manager.mock.MockVehicleTypesManager;
import com.planetmayo.debrief.satc.support.SupportServices;

public class Gwt implements EntryPoint {

	private static Gwt instance;
	
	public static Gwt getInstance() {
		return instance;
	}
	
	private final EventBus eventBus = new SimpleEventBus();
	private IVehicleTypesManager vehicleTypesManager;
	private IContributionsManager contributionsManager;
	private IBoundsManager boundsManager;	
	
	private void initializeManagers() 
	{
		vehicleTypesManager = new MockVehicleTypesManager();
		contributionsManager = new ContributionsManagerImpl();
		boundsManager = new BoundsManager();
	}
	
	public EventBus getEventBus()
	{
		return eventBus;
	}

	public IVehicleTypesManager getVehicleTypesManager()
	{
		return vehicleTypesManager;
	}

	public IContributionsManager getContributionsManager()
	{
		return contributionsManager;
	}

	public IBoundsManager getBoundsManager()
	{
		return boundsManager;
	}

	@Override
	public void onModuleLoad() {
		instance = this;
		initializeManagers();
		RootPanel.get().add(new RootLayout());

		SupportServices.INSTANCE.initialize(new GWTLogService(),
				new GWTUtilsService(), new GWTIOService());

		/*
		 * 
		 * // the MaintainContributions is the only 'presenter' in the app. we
		 * create its view, // so it can receive the view in the constructor
		 * 
		 * // so, create the view MaintainContributionsView maintainV = new
		 * MaintainContributionsView();
		 * 
		 * // now pass it to the presenter MaintainContribitions maintain = new
		 * MaintainContributions(maintainV, new MockVehicleRepository());
		 * 
		 * // retrieve the generator, since the test harness needs it
		 * StepperGenerator generator = maintain.getGenerator();
		 * 
		 * // and create the test harness support class TestHarness tester = new
		 * TestHarness(generator);
		 * 
		 * // and now the test harness view, which also needs the generator
		 * TestHarnessView testV = new TestHarnessView(generator, testHarness);
		 * 
		 * // the two 'dumb' results plotters TabularStatesView tabularV = new
		 * TabularStatesView(); SpatialStatesView spatialV =
		 * SpatialStatesView();
		 * 
		 * // register the two plotters as listeners - so they get their data
		 * generator.addBoundedStateListener(tabularV);
		 * generator.addBoundedStateListener(spatialV);
		 * 
		 * // now put the views into the UI getRootpanel().add(maintainV);
		 * getRootpanel().add(testV); getRootpanel().add(tabularV);
		 * getRootpanel().add(spatialVV);
		 * 
		 * // or I suppose the root panel could have some layout methods, such
		 * as getRootpanel().addBottomLeft(maintainV);
		 * getRootpanel().addTopLeft(testV);
		 * getRootpanel().addTopRight(tabularV);
		 * getRootpanel().addBottomRight(spatialVV);
		 */
	}
}
