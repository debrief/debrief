/**
 * 
 */
package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.gwt.client.Gwt;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.support.TestSupport;

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
	
	IBoundsManager boundsManager;

	public RootLayout()
	{
		boundsManager = Gwt.getInstance().getBoundsManager();
		initWidget(uiBinder.createAndBindUi(this));

		// HERE YOU CAN PUT YOUR LOGIC
		TestSupport testP = new TestSupport();
		testP.setGenerator(boundsManager);

		testHarness.setGenerator(boundsManager);
		testHarness.setTestSupport(testP);

		boundsManager.addConstrainSpaceListener(tabularStates);
		boundsManager.addConstrainSpaceListener(spatialStates);
	}

}
