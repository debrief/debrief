package com.planetmayo.debrief.satc.model.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.util.MakeGrid;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

@SuppressWarnings("deprecation")
public class GenerateCandidatesTest extends ModelTestBase {

	private IBoundsManager boundsManager;
	private BearingMeasurementContribution bearingMeasurementContribution;
	private CourseForecastContribution courseForecastContribution;

	@Before
	public void prepareBoundsManager() {
		bearingMeasurementContribution = new BearingMeasurementContribution();
		bearingMeasurementContribution.loadFrom(TestSupport.getShortData());

		courseForecastContribution = new CourseForecastContribution();
		courseForecastContribution
				.setStartDate(new Date(110, 0, 12, 12, 15, 0));
		courseForecastContribution
				.setFinishDate(new Date(110, 0, 12, 12, 20, 0));
		courseForecastContribution.setMinCourse(50d);
		courseForecastContribution.setMaxCourse(100d);

		boundsManager = new BoundsManager();
		boundsManager.addContribution(bearingMeasurementContribution);
		boundsManager.addContribution(courseForecastContribution);
	}

	@Test
	public void testGridding() throws Exception {

		WKTReader wkt = new WKTReader();
		Geometry geom = wkt.read("POLYGON ((0.0 3.0, 2.0 4.0, 4.0 4.0, 2.0 3.0, 0.0 3.0))");

		// how many points?
		final int num = 1000;
		
		// ok, try the tesselate function
		long start = System.currentTimeMillis();
		ArrayList<Geometry> pts = MakeGrid.ST_Tile(geom, num,6);
		System.out.println("elapsed:" + (System.currentTimeMillis()-start));
		assertNotNull("something returned", pts);
		assertEquals("correct num", 98, pts.size());
		Iterator<Geometry> iter = pts.iterator();
		while(iter.hasNext())
		{
			Geometry ge = iter.next();
			Point po = (Point) ge;
			// check the point is in the area
			assertEquals("point is in area",true, geom.contains(po));

			// send out for debug
			System.out.println(po.getX() + "\t" + po.getY());
		}
	}

}
