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
import com.planetmayo.debrief.satc.model.states.Route;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MakeGrid;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@SuppressWarnings("deprecation")
public class GenerateCandidatesTest extends ModelTestBase
{

	private IBoundsManager boundsManager;
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

		boundsManager = new BoundsManager();
		boundsManager.addContribution(bearingMeasurementContribution);
		boundsManager.addContribution(courseForecastContribution);
	}

	@Test
	public void testGridding() throws ParseException
	{

		WKTReader wkt = new WKTReader();
		Geometry geom = wkt
				.read("POLYGON ((0.0 3.0, 2.0 4.0, 4.0 4.0, 2.0 3.0, 0.0 3.0))");

		// how many points?
		final int num = 100;

		// ok, try the tesselate function
		long start = System.currentTimeMillis();
		ArrayList<Point> pts = MakeGrid.ST_Tile(geom, num, 6);
		System.out.println("elapsed:" + (System.currentTimeMillis() - start));
		assertNotNull("something returned", pts);
		assertEquals("correct num", 98, pts.size());
		Iterator<Point> iter = pts.iterator();
		while (iter.hasNext())
		{
			Point po = iter.next();
			// check the point is in the area
			assertEquals("point is in area", true, geom.contains(po));

			// send out for debug
			// System.out.println(po.getX() + "\t" + po.getY());
		}
	}

	@Test 
	public void testRoutes()
	{
		Date startD = new Date(2012,5,5,12,0,0);
		Date endD = new Date(2012,5,5,2,0,0);
		Point startP = GeoSupport.getFactory().createPoint(new Coordinate(0,0));
		Point endP = GeoSupport.getFactory().createPoint(new Coordinate(1,0));
		Route testR = new Route(startP, startD, endP, endD);
		
		assertEquals("correct course", 0, testR.getCourse(), EPS);
		assertEquals("correct speed", 30, testR.getSpeed(), EPS);
		
	}
	
	@Test
	public void testMatrixStorage() throws ParseException
	{
		WKTReader wkt = new WKTReader();
		Geometry leg1Start = wkt.read("POLYGON ((0 3, 2 4, 4 4, 2 3, 0 3))");
		Geometry leg1End = wkt.read("POLYGON ((5 1, 5.5 2,6 2,6 1, 5 1))");

		ArrayList<Point> startP = MakeGrid.ST_Tile(leg1Start, 10, 6);
		ArrayList<Point> endP = MakeGrid.ST_Tile(leg1End, 10, 6);

		assertNotNull("produced start", startP);
		assertNotNull("produced end", endP);

		int startLen = startP.size();
		int endLen = endP.size();

		Route[][] leg1 = new Route[startLen][endLen];

		Date tStart = new Date(2012, 1, 1, 11, 0, 0);
		Date tEnd = new Date(2012, 1, 1, 15, 0, 0);

		int ctr = 0;
		for (int i = 0; i < startLen; i++)
		{
			for (int j = 0; j < endLen; j++)
			{
				leg1[i][j] = new Route(startP.get(i), tStart, endP.get(j), tEnd);
				ctr++;
			}
		}

		// check we have the correct nubmer of points
		assertEquals("correct number of points", startLen * endLen, ctr);
		
	}

}
