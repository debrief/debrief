package com.planetmayo.debrief.satc.model.generator;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.SolutionGenerator.ISolutionsReadyListener;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.manager.mock.MockVehicleTypesManager;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.support.TestSupport;

@SuppressWarnings("deprecation")
public class GenerateCandidatesTest extends ModelTestBase
{

	private IBoundsManager boundsManager;
	private BearingMeasurementContribution bearingMeasurementContribution;
	private CourseForecastContribution courseForecastContribution;

	@Before
	public void prepareBoundsManager()
	{
		boundsManager = new BoundsManager();

		VehicleType type = new MockVehicleTypesManager().getAllTypes().get(0);
		boundsManager.setVehicleType(type);
		
		bearingMeasurementContribution = new BearingMeasurementContribution();
		bearingMeasurementContribution.loadFrom(TestSupport.getLongData());
		bearingMeasurementContribution.setAutoDetect(false);
		boundsManager.addContribution(bearingMeasurementContribution);

		courseForecastContribution = new CourseForecastContribution();
		courseForecastContribution.setStartDate(new Date(110, 0, 12, 12, 15, 0));
		courseForecastContribution.setFinishDate(new Date(110, 0, 12, 12, 20, 0));
		courseForecastContribution.setMinCourse(Math.toRadians(110d));
		courseForecastContribution.setMaxCourse(Math.toRadians(300d));
		boundsManager.addContribution(courseForecastContribution);

		StraightLegForecastContribution sl = new StraightLegForecastContribution();
		sl.setStartDate(new Date(110, 0, 12, 12, 15, 0));
		sl.setFinishDate(new Date(110, 0, 12, 12, 18, 0));
		sl.setName("Straight leg 1");
		boundsManager.addContribution(sl);
		
		sl = new StraightLegForecastContribution();
		sl.setStartDate(new Date(110, 0, 12, 12, 25, 0));
		sl.setFinishDate(new Date(110, 0, 12, 12, 34, 0));
		sl.setName("Straight leg 2");
		boundsManager.addContribution(sl);

		sl = new StraightLegForecastContribution();
		sl.setStartDate(new Date(110, 0, 12, 12, 42, 0));
		sl.setFinishDate(new Date(110, 0, 12, 12, 56, 0));
		sl.setName("Straight leg 3");
		boundsManager.addContribution(sl);
		
		boundsManager.addContribution(new LocationAnalysisContribution());
		boundsManager.addContribution(new CourseAnalysisContribution());

	}

	@Test
	public void testExtractLegs()
	{
		boundsManager.run();
		
		// ok, let's look at the legs
		Collection<BoundedState> theStates = boundsManager.getSpace().states();
		HashMap<String, CoreLeg> theLegs = SolutionGenerator.getTheLegs(theStates);
		
		assertNotNull("got some legs", theLegs);
		assertEquals("got 7 (3 straight, 4 turns) legs",7, theLegs.size());
	}

	private static boolean called = false;
	
	@Test
	public void testWholeCycle()
	{
		boundsManager.run();

		// ok, let's look at the legs
		Collection<BoundedState> theStates = boundsManager.getSpace().states();

		// ok, check the locations
		for (Iterator<BoundedState> iterator = theStates.iterator(); iterator.hasNext();)
		{
			BoundedState boundedState = (BoundedState) iterator.next();
//			System.out.println("time:" + boundedState.getTime() + " loc:" +( boundedState.getLocation() != null)
//					+ " crse:" + (boundedState.getCourse() != null)
//					 + " spd:" + (boundedState.getSpeed() != null));
			
//			if(boundedState.getCourse() != null)
//				System.out.println(" " + boundedState.getCourse().toDebugString());
		}	
		
		SolutionGenerator genny = new SolutionGenerator();
		genny.addReadyListener(new ISolutionsReadyListener()
		{
			
			@Override
			public void solutionsReady(CompositeRoute[] routes)
			{
				called = true;
			}
		});
		
		// ok, get it to run
		assertFalse("not called yet", called);
		
		// ok, get firing
		genny.complete(boundsManager);
		
		// ok, get it to run
		assertTrue("now called yet", called);
	}

	
	

}
