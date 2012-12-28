package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.support.SupportServices;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class BearingMeasurementContributionTest extends ModelTestBase
{
	public static final String THE_PATH = "tests/com/planetmayo/debrief/satc/model/contributions/data/bearing_measurement_data.txt";
	public static final String THE_SHORT_PATH = "tests/com/planetmayo/debrief/satc/model/contributions/data/short_bearing_measurement_data.txt";

	private BearingMeasurementContribution contribution;	
	
	@Test
	public void testLoadFrom() throws Exception 
	{
		contribution = new BearingMeasurementContribution();
		assertFalse("should be empty", contribution.hasData());
		
		contribution.loadFrom(SupportServices.INSTANCE.getIOService().readLinesFrom(THE_SHORT_PATH));
		assertTrue("should not be empty", contribution.hasData());	
		assertEquals("correct start date", new Date(110, 00, 12, 12, 13, 29), contribution.getStartDate());
		assertEquals("correct finish date", new Date(110, 00, 12, 12, 24, 29), contribution.getFinishDate());
		assertEquals(Math.toRadians(3d), contribution.getBearingError(), EPS);
	}

	@Test
	public void testActUpon() throws Exception
	{
		testLoadFrom();
		ProblemSpace ps = new ProblemSpace();
		
		contribution.actUpon(ps);		
		for (BoundedState state : ps.states())
		{
			Geometry geo = state.getLocation().getGeometry();
			Coordinate[] coords = geo.getCoordinates();
			for (int i = 0; i <= 4; i++)
			{
				Coordinate coordinate = coords[i];
				assertNotNull("we should have a coordinate", coordinate);
			}
		}
		assertEquals("read in all lines", 8, ps.size());
	}
}