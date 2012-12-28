package com.planetmayo.debrief.satc.model.contributions;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
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


	@Test
	public void testActUpon() throws IncompatibleStateException, IOException
	{
		BearingMeasurementContribution sc = new BearingMeasurementContribution();
		
		// check it's as we expect it to be
		assertFalse("should be empty",sc.hasData() );
		
		// can we see any data?
	//	assertTrue("can see datafile",new File(THE_PATH).exists());
		
		// ok, load some data		
		sc.loadFrom(SupportServices.INSTANCE.getIOService().readLinesFrom(THE_SHORT_PATH));

		// check it's as we expect it to be
		assertTrue("should not be empty",sc.hasData() );

		// did the dates get updated?
		assertEquals("correct start date", new Date(110,00,12,12,13,29), sc.getStartDate());
		assertEquals("correct finish date", new Date(110,00,12,13,05,29), sc.getFinishDate());
		
		// hmm, how about a polygon?
		ProblemSpace ps = new ProblemSpace();
		
		// go for it!
		sc.actUpon(ps);
		
		Iterator<BoundedState> iter = ps.states().iterator();
		int ctr = 0;
		while (iter.hasNext())
		{
			BoundedState newState = (BoundedState) iter.next();
			
			// ok, output it.
			Geometry geo = newState.getLocation().getGeometry();
			Coordinate[] coords = geo.getCoordinates();
			for (int i = 0; i <=4; i++)
			{
				Coordinate coordinate = coords[i];
				assertNotNull("we should have a coordinate", coordinate);
							}

			ctr++;
		}
		
		// did we find all of them?
		assertEquals("read in all lines", 57, ctr);
		
	}


}