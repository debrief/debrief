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
//		Geometry geom = wkt.read("POLYGON ((6010902.111 2119413.551, 6011149.257 2119630.788, 6011224.818 2119772.465, 6011319.269 2119797.652, 6011399.552 2119734.684, 6011341.308 2119585.137, 6011328.714 2119435.59, 6011229.541 2119369.474, 6011155.554 2119478.092, 6010982.394 2119418.274, 6010898.962 2119367.9, 6010902.111 2119413.551))");

		// cool, what's the area:
		double area = geom.getArea();
		
		// home many points?
		final int num = 1000;
		
		double interval = 2* area/num;
		
		// ok, try the tesselate function
		
		
		long start = System.currentTimeMillis();
		
		ArrayList<Geometry> pts = tesselate.ST_Tile(geom, interval, interval,
				2);
		
		long elapsed = System.currentTimeMillis() - start;
		System.out.println("elapsed:" + elapsed);
		
		assertNotNull("something returned", pts);
		
		
	//	assertEquals("correct num", 15, pts.size());
//		Iterator<Geometry> iter = pts.iterator();
//		while(iter.hasNext())
//		{
//			Geometry ge = iter.next();
//			Point po = (Point) ge;
//			System.out.println(po.getX() + "\t" + po.getY());
//		}
	}

}
