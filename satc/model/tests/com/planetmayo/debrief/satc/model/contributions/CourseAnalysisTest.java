package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;

import junit.framework.TestCase;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class CourseAnalysisTest extends TestCase
{

	public void testBoundary() throws IncompatibleStateException
	{
		// ok, create the state
		Date oldDate = new Date(100000);
		Date newDate1 = new Date(105000);
		Date newDate2 = new Date(115000);
		BoundedState ba = new BoundedState(oldDate);
		BoundedState bb = new BoundedState(newDate1);
		BoundedState bc = new BoundedState(newDate2);
		ProblemSpace space = new ProblemSpace();
		space.add(ba);
		space.add(bb);
		space.add(bc);
		VehicleType vType = new VehicleType("UK Ferry", GeoSupport.kts2MSec(2),
				GeoSupport.kts2MSec(30), Math.toRadians(0),
				Math.toRadians(1), 0.2, 0.4, 0.2, 0.4);
		space.setVehicleType(vType);

		// set some course data
		CourseRange cr = new CourseRange(Math.toRadians(45), Math.toRadians(110));
		ba.constrainTo(cr);
		
		assertNull(" course constraint empty",bb.getCourse());
		assertNull(" course constraint empty",bc.getCourse());
	
		CourseAnalysisContribution cac = new CourseAnalysisContribution();
		cac.actUpon(space);
		
		assertNotNull(" course constriant not empty", bb.getCourse());
		assertNotNull(" course constriant not empty", bc.getCourse());
		
		// have a look at the new min/max course
		assertEquals("new min course valid",40d, Math.toDegrees( bb.getCourse().getMin()), 0.001);
		assertEquals("new max course valid",115d, Math.toDegrees(bb.getCourse().getMax()), 0.001);
		
		// have a look at the new min/max course once we've run for a little longer
		assertEquals("new min course valid",30d, Math.toDegrees( bc.getCourse().getMin()), 0.001);
		assertEquals("new max course valid",125d, Math.toDegrees(bc.getCourse().getMax()), 0.001);
	}
	

	/** test that we trim the range to 0-360, and never go all the way around a circle
	 * 
	 * @throws IncompatibleStateException
	 */
	public void testWrapAround() throws IncompatibleStateException
	{
		// ok, create the state
		Date oldDate = new Date(100000);
		Date newDate1 = new Date(105000);
		Date newDate2 = new Date(115000);
		BoundedState ba = new BoundedState(oldDate);
		BoundedState bb = new BoundedState(newDate1);
		BoundedState bc = new BoundedState(newDate2);
		ProblemSpace space = new ProblemSpace();
		space.add(ba);
		space.add(bb);
		space.add(bc);
		VehicleType vType = new VehicleType("UK Ferry", GeoSupport.kts2MSec(2),
				GeoSupport.kts2MSec(30), Math.toRadians(0),
				Math.toRadians(1), 0.2, 0.4, 0.2, 0.4);
		space.setVehicleType(vType);

		// set some course data
		CourseRange cr = new CourseRange(Math.toRadians(5), Math.toRadians(350));
		ba.constrainTo(cr);
		
		assertNull(" course constraint empty",bb.getCourse());
		assertNull(" course constraint empty",bc.getCourse());
	
		CourseAnalysisContribution cac = new CourseAnalysisContribution();
		cac.actUpon(space);
		
		assertNotNull(" course constriant not empty", bb.getCourse());
		assertNotNull(" course constriant not empty", bc.getCourse());
		
		// have a look at the new min/max course
		assertEquals("new min course valid",00d, Math.toDegrees( bb.getCourse().getMin()), 0.001);
		assertEquals("new max course valid",355d, Math.toDegrees(bb.getCourse().getMax()), 0.001);
		
		// have a look at the new min/max course once we've run for a little longer
		assertEquals("new min course valid",0d, Math.toDegrees( bc.getCourse().getMin()), 0.001);
		assertEquals("new max course valid",360d, Math.toDegrees(bc.getCourse().getMax()), 0.001);
	}
	
}
