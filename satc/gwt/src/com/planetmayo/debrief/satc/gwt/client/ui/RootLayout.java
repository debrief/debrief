/**
 * 
 */
package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.generator.BoundsManager;
import com.planetmayo.debrief.satc.model.manager.MaintainContributions;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.support.mock.MockVehicleTypesRepository;

/**
 * @author Akash-Gupta
 * 
 */
public class RootLayout extends Composite
{

	interface RootLayoutUiBinder extends UiBinder<Widget, RootLayout>
	{
	}

	@UiField
	TestHarness testHarness;

	@UiField
	TrackStates tabularStates;

	@UiField
	SpatialView spatialStates;

	@UiField
	MaintainContributionsView manageSolutionsView;

	private static RootLayoutUiBinder uiBinder = GWT
			.create(RootLayoutUiBinder.class);

	public RootLayout()
	{
		initWidget(uiBinder.createAndBindUi(this));

		// HERE YOU CAN PUT YOUR LOGIC
		MaintainContributions maintainP = new MaintainContributions(
				manageSolutionsView, new MockVehicleTypesRepository());
		final BoundsManager genny = maintainP.getGenerator();
		TestSupport testP = new TestSupport();
		testP.setGenerator(genny);

		testHarness.setGenerator(genny);
		testHarness.setTestSupport(testP);

		genny.addBoundedStateListener(tabularStates);
		genny.addBoundedStateListener(spatialStates);

	}

}
