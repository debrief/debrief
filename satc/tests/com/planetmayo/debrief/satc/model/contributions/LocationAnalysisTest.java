package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;

import junit.framework.TestCase;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class LocationAnalysisTest extends TestCase
{
	public void testBoundary() throws IncompatibleStateException
	{
		// ok, create the state
		Date oldDate= new Date(100000);
		Date newDate = new Date(160000);
		BoundedState bs = new BoundedState(oldDate);
		
		
		Coordinate[] coords = new Coordinate[]{new Coordinate(2,4),
				new Coordinate(4,5), 
				new Coordinate(5,2), 
				new Coordinate(3,1), 
				new Coordinate(1,2),
				new Coordinate(2,4)};
		LinearRing outer = GeoSupport.getFactory().createLinearRing(coords );
		Polygon area = GeoSupport.getFactory().createPolygon(outer, null);
		// and the location
		LocationRange lr = new LocationRange(area );
		bs.constrainTo(lr);

		// get ready to analyse
		LocationAnalysisContribution lac = new LocationAnalysisContribution();

		// give it a course
		CourseRange cRange = new CourseRange(20,60);
		bs.constrainTo(cRange);
		
		// and a speed
		SpeedRange sRange = new SpeedRange(2, 12);
		bs.constrainTo(sRange);
		
		// try the speed
		LinearRing speedRegion = lac.getSpeedRing(sRange, newDate.getTime() - oldDate.getTime());
//		assertNotNull("course not generated", speedRegion);
		
		// ok, try the off with the course
		LinearRing courseRegion = lac.getCourseRing(cRange);
//		assertNotNull("course not generated", courseRegion);
		
		
		//
		LocationRange newB = lac.getRangeFor(bs, newDate);
		
		// did it work?
	//	assertNotNull("Should have created location", newB);
	}
}
