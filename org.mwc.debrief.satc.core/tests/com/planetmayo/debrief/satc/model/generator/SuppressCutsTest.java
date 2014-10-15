/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.impl.AbstractSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.impl.BoundsManager;
import com.planetmayo.debrief.satc.model.generator.impl.Contributions;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.support.TestSupport;

/**
 * @author ian
 *
 */
public class SuppressCutsTest
{

	private BoundsManager boundsManager;
	private BearingMeasurementContribution bearingMeasurementContribution;
	private CourseForecastContribution courseForecastContribution;
	private ProblemSpace space;

	@SuppressWarnings("deprecation")
	final Date start1 = new Date(110, 0, 12, 12, 15, 0);
	@SuppressWarnings("deprecation")
	final Date end1 = new Date(110, 0, 12, 12, 18, 0);
	@SuppressWarnings("deprecation")
	final Date start3 = new Date(110, 0, 12, 12, 42, 0);
	@SuppressWarnings("deprecation")
	final Date end3 = new Date(110, 0, 12, 12, 56, 0);

	
	/**
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception
	{
		IContributions contributions = new Contributions();
		space = new ProblemSpace();
		boundsManager = new BoundsManager(contributions, space);

		bearingMeasurementContribution = new BearingMeasurementContribution();
		bearingMeasurementContribution.loadFrom(TestSupport.getLongData());
		bearingMeasurementContribution.setAutoDetect(false);
		contributions.addContribution(bearingMeasurementContribution);

		courseForecastContribution = new CourseForecastContribution();
		courseForecastContribution.setStartDate(start1);
		courseForecastContribution.setFinishDate(new Date(110, 0, 12, 12, 20, 0));
		courseForecastContribution.setMinCourse(Math.toRadians(110d));
		courseForecastContribution.setMaxCourse(Math.toRadians(300d));
		contributions.addContribution(courseForecastContribution);

		StraightLegForecastContribution sl = new StraightLegForecastContribution();
		sl.setStartDate(start1);
		sl.setFinishDate(end1);
		sl.setName("Straight leg 1");
		contributions.addContribution(sl);

		sl = new StraightLegForecastContribution();
		sl.setStartDate(new Date(110, 0, 12, 12, 25, 0));
		sl.setFinishDate(new Date(110, 0, 12, 12, 34, 0));
		sl.setName("Straight leg 2");
		contributions.addContribution(sl);

		sl = new StraightLegForecastContribution();
		sl.setStartDate(start3);
		sl.setFinishDate(end3);
		sl.setName("Straight leg 3");
		contributions.addContribution(sl);

		contributions.addContribution(new LocationAnalysisContribution());
		contributions.addContribution(new CourseAnalysisContribution());
		
		// run the constraints
		boundsManager.run();
	}

	@Test
	public void testNormalProcessing()
	{
		Collection<BoundedState> states = space.states();
		
		// check the data is there before-hand
		assertEquals("got states", 64, states.size());
		
    Collection<BoundedState> res = AbstractSolutionGenerator.suppressCuts(states, 32);
    
    // and how many are there now?
		assertEquals("trimmed states", 32, res.size());
		
		// check the leg-start/end constraints are still in there
		assertNotNull("got start of leg 1", space.getBoundedStateAt(start1));
		assertNotNull("got end of leg 1", space.getBoundedStateAt(end1));
		assertNotNull("got start of leg 3", space.getBoundedStateAt(start3));
		assertNotNull("got end of leg 3", space.getBoundedStateAt(end3));
	}

	@Test
	public void testTooShort()
	{
		Collection<BoundedState> states = space.states();
		
		// check the data is there before-hand
		assertEquals("got states", 64, states.size());
		
    Collection<BoundedState> res = AbstractSolutionGenerator.suppressCuts(states, 5);
    
    // and how many are there now?
		assertEquals("trimmed states", 22, res.size());
		
		// check the leg-start/end constraints are still in there
		assertNotNull("got start of leg 1", space.getBoundedStateAt(start1));
		assertNotNull("got end of leg 1", space.getBoundedStateAt(end1));
		assertNotNull("got start of leg 3", space.getBoundedStateAt(start3));
		assertNotNull("got end of leg 3", space.getBoundedStateAt(end3));
	}

}
