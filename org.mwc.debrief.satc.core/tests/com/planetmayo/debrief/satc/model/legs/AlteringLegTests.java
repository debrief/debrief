/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.legs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@SuppressWarnings("deprecation")
public class AlteringLegTests extends ModelTestBase
{
	private BearingMeasurementContribution bearingMeasurementContribution;
	private CourseForecastContribution courseForecastContribution;

	@Before
	public void prepareBoundsManager()
	{
		bearingMeasurementContribution = new BearingMeasurementContribution();
		bearingMeasurementContribution.loadFrom(TestSupport.getShortData());

		courseForecastContribution = new CourseForecastContribution();
		courseForecastContribution.setStartDate(new Date(110, 0, 12, 12, 15, 0));
		courseForecastContribution.setFinishDate(new Date(110, 0, 12, 12, 20, 0));
		courseForecastContribution.setMinCourse(50d);
		courseForecastContribution.setMaxCourse(100d);
	}

/*	@Test
	public void testAchievableTest() throws ParseException,
			IncompatibleStateException
	{
		ArrayList<BoundedState> sList1 = createStates(3, 22, false);
		AlteringLeg leg = new AlteringLeg("alter 1", sList1);

		// check we have no routes
		assertNull("no routes yet", leg.getRoutes());

		// generate them
		leg.generateRoutes(Precision.LOW);

		// check they have arrived
		assertEquals("have routes", 504, leg.getRoutes().length
				* leg.getRoutes()[0].length);

		// ok,
		leg.decideAchievableRoutes();

		// how many are achievalbe?
		assertEquals("correct achievable", 73, leg.getNumAchievable());
	}*/

	private ArrayList<BoundedState> createStates(double minS, double maxS,
			boolean reverseOrder) throws ParseException, IncompatibleStateException
	{
		Date startA = new Date(2012, 5, 5, 12, 0, 0);
		Date startB = new Date(2012, 5, 5, 14, 0, 0);
		Date startC = new Date(2012, 5, 5, 15, 0, 0);
		Date startD = new Date(2012, 5, 5, 17, 0, 0);
		BoundedState bA = new BoundedState(startA);
		BoundedState bB = new BoundedState(startB);
		BoundedState bC = new BoundedState(startC);
		BoundedState bD = new BoundedState(startD);

		// apply location bounds
		WKTReader wkt = new WKTReader();
		LocationRange locA = new LocationRange(
				wkt.read("POLYGON ((0 3, 2 4, 4 4, 2 3, 0 3))"));
		LocationRange locB = new LocationRange(
				wkt.read("POLYGON ((2.63 2.56, 3.5 3.16, 4.11 3.42, 3.33 2.3, 2.63 2.56))"));
		LocationRange locC = new LocationRange(
				wkt.read("POLYGON ((3.32 1.99,3.93 2.71,4.64 2.87,3.81 1.78, 3.32 1.99))"));
		LocationRange locD = new LocationRange(
				wkt.read("POLYGON ((5 1, 5.5 2, 6 2, 6 1, 5 1))"));

		if (!reverseOrder)
		{
			bA.constrainTo(locA);
			bB.constrainTo(locB);
			bC.constrainTo(locC);
			bD.constrainTo(locD);
		}
		else
		{
			bA.constrainTo(locD);
			bB.constrainTo(locC);
			bC.constrainTo(locB);
			bD.constrainTo(locA);

		}

		// apply speed bounds
		SpeedRange sr = new SpeedRange(minS, maxS);
		bA.constrainTo(sr);
		bD.constrainTo(sr);

		ArrayList<BoundedState> sList = new ArrayList<BoundedState>();
		sList.add(bA);
		sList.add(bB);
		sList.add(bC);
		sList.add(bD);
		return sList;
	}

}
