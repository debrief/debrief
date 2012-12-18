package com.planetmayo.debrief.satc.model.contributions;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import junit.framework.TestCase;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.support.SupportServices;
import com.planetmayo.debrief.satc_rcp.services.RCPUtilsService;
import com.planetmayo.debrief.satc_rcp.services.RCPIOService;
import com.planetmayo.debrief.satc_rcp.services.RCPLogService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

public class BearingMeasurementContributionTest extends TestCase
{
	public static final String THE_PATH = "tests/com/planetmayo/debrief/satc/model/contributions/data/bearing_measurement_data.txt";
	public static final String THE_SHORT_PATH = "tests/com/planetmayo/debrief/satc/model/contributions/data/short_bearing_measurement_data.txt";

	
	
	@Override
	protected void setUp() throws Exception
	{
		SupportServices.INSTANCE.initialize(new RCPLogService(), new RCPUtilsService(), new RCPIOService());
		super.setUp();
	}

	@SuppressWarnings("deprecation")
	public void testNullDateSingleState() throws Exception
	{
		BearingMeasurementContribution sc = new BearingMeasurementContribution();
		
		// check it's as we expect it to be
		assertFalse("should be empty",sc.hasData() );
		
		// can we see any data?
		assertTrue("can see datafile",new File(THE_PATH).exists());
		
		// ok, load some data		
		sc.loadFrom(IOUtils.readLines(new FileInputStream(THE_PATH)));

		// check it's as we expect it to be
		assertTrue("should not be empty",sc.hasData() );

		// did the dates get updated?
		assertEquals("correct start date", new Date(110,00,12,0,13,29), sc.getStartDate());
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
			Polygon poly = newState.getLocation().getPolygon();
			Coordinate[] coords = poly.getCoordinates();
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