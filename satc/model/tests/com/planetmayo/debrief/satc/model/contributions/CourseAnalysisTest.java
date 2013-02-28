package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.util.GeoSupport;

import static org.junit.Assert.*;

public class CourseAnalysisTest extends ModelTestBase
{
	ProblemSpace space;
	BoundedState firstState;
	BoundedState secondState;
	BoundedState thirdState;

	@Before
	public void createProblemSpace() throws IncompatibleStateException
	{
		firstState = new BoundedState(new Date(100000));
		secondState = new BoundedState(new Date(105000));
		thirdState = new BoundedState(new Date(115000));
		space = new ProblemSpace();
		space.add(firstState);
		space.add(secondState);
		space.add(thirdState);
		VehicleType vType = new VehicleType("UK Ferry", GeoSupport.kts2MSec(2),
				GeoSupport.kts2MSec(30), Math.toRadians(0), Math.toRadians(1), 0.2,
				0.4, 0.2, 0.4);
		space.setVehicleType(vType);
	}

	@Test
	public void testBoundary() throws IncompatibleStateException
	{
		// set some course data
		CourseRange cr = new CourseRange(Math.toRadians(45), Math.toRadians(110));
		firstState.constrainTo(cr);

		assertNull(" course constraint empty", secondState.getCourse());
		assertNull(" course constraint empty", thirdState.getCourse());

		CourseAnalysisContribution cac = new CourseAnalysisContribution();
		cac.actUpon(space);

		assertNotNull(" course constriant not empty", secondState.getCourse());
		assertNotNull(" course constriant not empty", thirdState.getCourse());

		// have a look at the new min/max course
		assertEquals("new min course valid", 40d,
				Math.toDegrees(secondState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 115d,
				Math.toDegrees(secondState.getCourse().getMax()), EPS);

		// have a look at the new min/max course once we've run for a little longer
		assertEquals("new min course valid", 30d,
				Math.toDegrees(thirdState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 125d,
				Math.toDegrees(thirdState.getCourse().getMax()), EPS);
	}


	@Test
	public void testFwdBwd() throws IncompatibleStateException
	{
		// set some course data
		CourseRange cr = new CourseRange(Math.toRadians(45), Math.toRadians(110));
		secondState.constrainTo(cr);

		assertNull(" course constraint empty", firstState.getCourse());
		assertNull(" course constraint empty", thirdState.getCourse());

		CourseAnalysisContribution cac = new CourseAnalysisContribution();
		cac.actUpon(space);

		assertNotNull(" course constriant not empty", secondState.getCourse());
		assertNotNull(" course constriant not empty", thirdState.getCourse());

		// have a look at the new min/max course
		assertEquals("new min course valid", 40d,
				Math.toDegrees(firstState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 115d,
				Math.toDegrees(firstState.getCourse().getMax()), EPS);

		// have a look at the new min/max course once we've run for a little longer
		assertEquals("new min course valid", 35d,
				Math.toDegrees(thirdState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 119.999999d,
				Math.toDegrees(thirdState.getCourse().getMax()), EPS);
	}

	
	/**
	 * test that we trim the range to 0-360, and never go all the way around a
	 * circle
	 * 
	 * @throws IncompatibleStateException
	 */
	@Test
	public void testWrapAround() throws IncompatibleStateException
	{
		// set some course data
		CourseRange cr = new CourseRange(Math.toRadians(5), Math.toRadians(350));
		firstState.constrainTo(cr);

		assertNull(" course constraint empty", secondState.getCourse());
		assertNull(" course constraint empty", thirdState.getCourse());

		CourseAnalysisContribution cac = new CourseAnalysisContribution();
		cac.actUpon(space);

		assertNotNull(" course constriant not empty", secondState.getCourse());
		assertNotNull(" course constriant not empty", thirdState.getCourse());

		// have a look at the new min/max course
		assertEquals("new min course valid", 00d,
				Math.toDegrees(secondState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 355d,
				Math.toDegrees(secondState.getCourse().getMax()), EPS);

		// have a look at the new min/max course once we've run for a little longer
		assertEquals("new min course valid", 0d,
				Math.toDegrees(thirdState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 360d,
				Math.toDegrees(thirdState.getCourse().getMax()), EPS);
	}

	/**
	 * test that we allow a very large range to expand into a full circle
	 * 
	 * @throws IncompatibleStateException
	 */
	@Test
	public void testOverlap() throws IncompatibleStateException
	{
		// set some course data
		CourseRange cr = new CourseRange(Math.toRadians(115), Math.toRadians(90));
		firstState.constrainTo(cr);

		assertNull(" course constraint empty", secondState.getCourse());
		assertNull(" course constraint empty", thirdState.getCourse());

		CourseAnalysisContribution cac = new CourseAnalysisContribution();
		cac.actUpon(space);

		assertNotNull(" course constriant not empty", secondState.getCourse());
		assertNotNull(" course constriant not empty", thirdState.getCourse());

		// have a look at the new min/max course
		assertEquals("new min course valid", 110d,
				Math.toDegrees(secondState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 360d,
				Math.toDegrees(secondState.getCourse().getMax()), EPS);

		// have a look at the new min/max course once we've run for a little longer
		assertEquals("new min course valid", 0d,
				Math.toDegrees(thirdState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 360d,
				Math.toDegrees(thirdState.getCourse().getMax()), EPS);
	}

	/**
	 * test that we allow a very large range to expand into a full circle
	 * 
	 * @throws IncompatibleStateException
	 */
	@Test
	public void testStraightLeg1() throws IncompatibleStateException
	{
		// set some course data
		CourseRange cr = new CourseRange(Math.toRadians(114), Math.toRadians(87));
		firstState.constrainTo(cr);

		// and some straight leg data
		secondState.setMemberOf("the_leg");
		thirdState.setMemberOf("the_leg");

		assertNull(" course constraint empty", secondState.getCourse());
		assertNull(" course constraint empty", thirdState.getCourse());

		CourseAnalysisContribution cac = new CourseAnalysisContribution();
		cac.actUpon(space);

		// ok, the first state should have the new constraint.

		assertNotNull(" course constriant not empty", secondState.getCourse());
		assertNotNull(" course constriant not empty", thirdState.getCourse());

		// have a look at the new min/max course
		assertEquals("new min course valid", 109d,
				Math.toDegrees(secondState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 91.9999999d,
				Math.toDegrees(secondState.getCourse().getMax()), EPS);

		// have a look at the new min/max course once we've run for a little longer
		assertEquals("new min course valid", 109d,
				Math.toDegrees(thirdState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 91.999999d,
				Math.toDegrees(thirdState.getCourse().getMax()), EPS);

	}
	/**
	 * test that we allow a very large range to expand into a full circle
	 * 
	 * @throws IncompatibleStateException
	 */
	@Test
	public void testStraightLeg2() throws IncompatibleStateException
	{
		// set some course data
		CourseRange cr = new CourseRange(Math.toRadians(87), Math.toRadians(114));
		firstState.constrainTo(cr);

		// and some straight leg data
		secondState.setMemberOf("the_leg");

		assertNull(" course constraint empty", secondState.getCourse());
		assertNull(" course constraint empty", thirdState.getCourse());

		CourseAnalysisContribution cac = new CourseAnalysisContribution();
		cac.actUpon(space);

		// ok, the first state should have the new constraint.

		assertNotNull(" course constriant not empty", secondState.getCourse());
		assertNotNull(" course constriant not empty", thirdState.getCourse());

		// have a look at the new min/max course
		assertEquals("new min course valid", 82,
				Math.toDegrees(secondState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 119,
				Math.toDegrees(secondState.getCourse().getMax()), EPS);

		// have a look at the new min/max course once we've run for a little longer
		assertEquals("new min course valid", 72,
				Math.toDegrees(thirdState.getCourse().getMin()), EPS);
		assertEquals("new max course valid", 129,
				Math.toDegrees(thirdState.getCourse().getMax()), EPS);

	}

	@Test
	public void testRelaxNoCourseBoundsAtAll() throws IncompatibleStateException
	{
		assertNull(" course constraint empty", firstState.getCourse());
		assertNull(" course constraint empty", secondState.getCourse());
		assertNull(" course constraint empty", thirdState.getCourse());

		// keep track of the last course
		BoundedState lastStateWithCourse = null;

		CourseAnalysisContribution cac = new CourseAnalysisContribution();
		VehicleType theV = space.getVehicleType();
		lastStateWithCourse = cac.applyRelaxedRangeBounds(lastStateWithCourse,
				firstState, theV);
		
		assertNull("last course is still null", lastStateWithCourse);

		
		lastStateWithCourse = cac.applyRelaxedRangeBounds(lastStateWithCourse,
				secondState, theV);

		assertNull("last course is still null", lastStateWithCourse);

		lastStateWithCourse = cac.applyRelaxedRangeBounds(lastStateWithCourse,
				secondState, theV);

		assertNull("last course is still null", lastStateWithCourse);
	}
	

	@Test
	public void testRelax_InitialCourseBoundsProvided() throws IncompatibleStateException
	{
		// set some course data
		CourseRange cr = new CourseRange(Math.toRadians(87), Math.toRadians(114));
		firstState.constrainTo(cr);

		assertNotNull(" course constraint not empty", firstState.getCourse());
		assertNull(" course constraint empty", secondState.getCourse());
		assertNull(" course constraint empty", thirdState.getCourse());

		// keep track of the last course
		BoundedState lastStateWithCourse = null;

		CourseAnalysisContribution cac = new CourseAnalysisContribution();
		VehicleType theV = space.getVehicleType();
		lastStateWithCourse = cac.applyRelaxedRangeBounds(lastStateWithCourse,
				firstState, theV);
		
		assertNotNull("value of last course should be null", lastStateWithCourse);
		assertEquals("correct min",87, Math.toDegrees( lastStateWithCourse.getCourse().getMin()), 0.1);

		
		lastStateWithCourse = cac.applyRelaxedRangeBounds(lastStateWithCourse,
				secondState, theV);

		assertNotNull("last course is still null", lastStateWithCourse);
		assertEquals("correct min",82, Math.toDegrees( lastStateWithCourse.getCourse().getMin()), 0.1);

		lastStateWithCourse = cac.applyRelaxedRangeBounds(lastStateWithCourse,
				thirdState, theV);

		assertNotNull("last course is still null", lastStateWithCourse);
		assertEquals("correct min",72, Math.toDegrees( lastStateWithCourse.getCourse().getMin()), 0.1);
	}
	

	@Test
	public void testRelax_LaterCourseBoundsPresent() throws IncompatibleStateException
	{
		// set some course data
		CourseRange cr = new CourseRange(Math.toRadians(87), Math.toRadians(114));
		secondState.constrainTo(cr);

		assertNull(" course constraint empty", firstState.getCourse());
		assertNotNull(" course constraint not empty", secondState.getCourse());
		assertNull(" course constraint empty", thirdState.getCourse());

		// keep track of the last course
		BoundedState lastStateWithCourse = null;

		CourseAnalysisContribution cac = new CourseAnalysisContribution();
		VehicleType theV = space.getVehicleType();
		lastStateWithCourse = cac.applyRelaxedRangeBounds(lastStateWithCourse,
				firstState, theV);
		
		assertNull("value of last course should be null", lastStateWithCourse);
	
		lastStateWithCourse = cac.applyRelaxedRangeBounds(lastStateWithCourse,
				secondState, theV);

		assertNotNull("last course is still null", lastStateWithCourse);
		assertEquals("correct min",87, Math.toDegrees( lastStateWithCourse.getCourse().getMin()), 0.001);

		lastStateWithCourse = cac.applyRelaxedRangeBounds(lastStateWithCourse,
				thirdState, theV);

		assertNotNull("last course is still null", lastStateWithCourse);
		assertEquals("correct min",77, Math.toDegrees( lastStateWithCourse.getCourse().getMin()), 0.001);
	}
	
	
}
