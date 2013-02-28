package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;

public class SpeedAnalysisTest extends ModelTestBase
{
	ProblemSpace space;
	private BoundedState state1;
	private BoundedState state2;
	private BoundedState state3;
	private BoundedState state4;
	private BoundedState state5;
	private BoundedState state6;
	private BoundedState state7;

	@Before
	public void createProblemSpace() throws IncompatibleStateException
	{
		state1 = new BoundedState(new Date(100000));
		state2 = new BoundedState(new Date(105000));
		state3 = new BoundedState(new Date(115000));
		state4 = new BoundedState(new Date(120000));
		state5 = new BoundedState(new Date(125000));
		state6 = new BoundedState(new Date(130000));
		state7 = new BoundedState(new Date(135000));
		space = new ProblemSpace();
		space.add(state1);
		space.add(state2);
		space.add(state3);
		space.add(state4);
		space.add(state5);
		space.add(state6);
		space.add(state7);
		VehicleType vType = new VehicleType("UK Ferry", 10, 500, Math.toRadians(0),
				Math.toRadians(1), 1, 3, 1, 5);
		space.setVehicleType(vType);
	}
	
	@Test
	public void testFwdBwd() throws IncompatibleStateException
	{
		// set some course data
		SpeedRange cr = new SpeedRange(200, 300);
		state2.constrainTo(cr);

		assertNull(" speed constraint empty", state1.getSpeed());
		assertNull(" speed constraint empty", state3.getSpeed());

		SpeedAnalysisContribution cac = new SpeedAnalysisContribution();
		cac.actUpon(space);
		
		// ok, check 1 and 3 have speeds
		assertNotNull(" speed constraint empty", state1.getSpeed());
		assertNotNull(" speed constraint empty", state3.getSpeed());

		// have a look at the new min/max speeds
		SpeedRange sp2 = state1.getSpeed();
		assertEquals("new min speed valid", 185, sp2.getMin(), EPS);
		assertEquals("new max speed valid", 325, sp2.getMax(), EPS);

		// have a look at the new min/max speeds
		SpeedRange sp3 = state3.getSpeed();
		assertEquals("new min speed valid", 150, sp3.getMin(), EPS);
		assertEquals("new max speed valid", 330, sp3.getMax(), EPS);

		
	}

	/**
	 * first straight leg
	 * 
	 * @throws IncompatibleStateException
	 */
	@Test
	public void testStraightLeg1() throws IncompatibleStateException
	{
		// set some course data
		SpeedRange cr = new SpeedRange(140, 180);
		state1.constrainTo(cr);

		// and some straight leg data
		state2.setMemberOf("the_leg");
		state3.setMemberOf("the_leg");
		state4.setMemberOf("the_leg");

		assertNull(" speed constraint empty", state2.getSpeed());
		assertNull(" speed constraint empty", state3.getSpeed());

		SpeedAnalysisContribution cac = new SpeedAnalysisContribution();
		cac.actUpon(space);

		// ok, the first state should have the new constraint.
		assertNotNull(" course constriant not empty", state1.getSpeed());
		assertNotNull(" course constriant not empty", state2.getSpeed());
		assertNotNull(" course constriant not empty", state3.getSpeed());
		assertNotNull(" course constriant not empty", state4.getSpeed());
		assertNotNull(" course constriant not empty", state5.getSpeed());
		assertNotNull(" course constriant not empty", state6.getSpeed());
		assertNotNull(" course constriant not empty", state7.getSpeed());

		// have a look at the new min/max course
		SpeedRange sp2 = state2.getSpeed();
		assertEquals("new min speed valid", 115, sp2.getMin(), EPS);
		assertEquals("new max speed valid", 195, sp2.getMax(), EPS);

		// check we're still on the same speed
		SpeedRange sp3 = state3.getSpeed();
		assertEquals("new min speed valid", 115, sp3.getMin(), EPS);
		assertEquals("new max speed valid", 195, sp3.getMax(), EPS);
	}

	/**
	 * check missing speed constraint for first leg
	 * 
	 * @throws IncompatibleStateException
	 */
	@Test
	public void testStraightLeg2() throws IncompatibleStateException
	{
		// set some course data
		SpeedRange cr = new SpeedRange(140, 180);
		state2.constrainTo(cr);

		// and some straight leg data
		state2.setMemberOf("the_leg");
		state3.setMemberOf("the_leg");
		state4.setMemberOf("the_leg");

		assertNull(" speed constraint empty", state1.getSpeed());
		assertNotNull(" speed constraint empty", state2.getSpeed());
		assertNull(" speed constraint empty", state3.getSpeed());

		SpeedAnalysisContribution cac = new SpeedAnalysisContribution();
		cac.actUpon(space);

		// ok, the first state should have the new constraint.
		assertNotNull(" course constriant not empty", state1.getSpeed());
		assertNotNull(" course constriant not empty", state2.getSpeed());
		assertNotNull(" course constriant not empty", state3.getSpeed());
		assertNotNull(" course constriant not empty", state4.getSpeed());
		assertNotNull(" course constriant not empty", state5.getSpeed());
		assertNotNull(" course constriant not empty", state6.getSpeed());
		assertNotNull(" course constriant not empty", state7.getSpeed());

	//	dumpSpeeds();
		
		// have a look at the new min/max course
		SpeedRange sp2 = state2.getSpeed();
		assertEquals("new min speed valid", 140, sp2.getMin(), EPS);
		assertEquals("new max speed valid", 180, sp2.getMax(), EPS);

		// check we're still on the same speed
		SpeedRange sp3 = state3.getSpeed();
		assertEquals("new min speed valid", 140, sp3.getMin(), EPS);
		assertEquals("new max speed valid", 180, sp3.getMax(), EPS);
	}


	/**
	 * check missing speed constraint for first leg
	 * 
	 * @throws IncompatibleStateException
	 */
	@Test
	public void testStraightLeg3() throws IncompatibleStateException
	{
		// set some course data
		state1.constrainTo(new SpeedRange(140, 180));
		state5.constrainTo(new SpeedRange(150, 170));

		// and some straight leg data
		state2.setMemberOf("the_leg");
		state3.setMemberOf("the_leg");
		state4.setMemberOf("the_leg");
		
		SpeedAnalysisContribution cac = new SpeedAnalysisContribution();
		cac.actUpon(space);

		// ok, the first state should have the new constraint.
		assertNotNull(" course constriant not empty", state1.getSpeed());
		assertNotNull(" course constriant not empty", state2.getSpeed());
		assertNotNull(" course constriant not empty", state3.getSpeed());
		assertNotNull(" course constriant not empty", state4.getSpeed());
		assertNotNull(" course constriant not empty", state5.getSpeed());
		assertNotNull(" course constriant not empty", state6.getSpeed());
		assertNotNull(" course constriant not empty", state7.getSpeed());

		dumpSpeeds();
		
		// have a look at the new min/max course

		SpeedRange sp2 = state2.getSpeed();
		assertEquals("new min speed valid", 135, sp2.getMin(), EPS);
		assertEquals("new max speed valid", 195, sp2.getMax(), EPS);

		// check we're still on the same speed
		SpeedRange sp3 = state3.getSpeed();
		assertEquals("new min speed valid", 135, sp3.getMin(), EPS);
		assertEquals("new max speed valid", 195, sp3.getMax(), EPS);

		// check we're still on the same speed
		SpeedRange sp4 = state4.getSpeed();
		assertEquals("new min speed valid", 135, sp4.getMin(), EPS);
		assertEquals("new max speed valid", 195, sp4.getMax(), EPS);

		
	}
	
	@Test
	public void testTurningLegs() throws IncompatibleStateException
	{
		// set some course data
		state1.constrainTo(new SpeedRange(140, 180));
		state5.constrainTo(new SpeedRange(150, 170));

		SpeedAnalysisContribution cac = new SpeedAnalysisContribution();
		cac.actUpon(space);

		// ok, the first state should have the new constraint.
		assertNotNull(" course constriant not empty", state1.getSpeed());
		assertNotNull(" course constriant not empty", state2.getSpeed());
		assertNotNull(" course constriant not empty", state3.getSpeed());
		assertNotNull(" course constriant not empty", state4.getSpeed());
		assertNotNull(" course constriant not empty", state5.getSpeed());
		assertNotNull(" course constriant not empty", state6.getSpeed());
		assertNotNull(" course constriant not empty", state7.getSpeed());

		dumpSpeeds();
		
		// have a look at the new min/max course
		SpeedRange sp2 = state2.getSpeed();
		assertEquals("new min speed valid", 115, sp2.getMin(), EPS);
		assertEquals("new max speed valid", 195, sp2.getMax(), EPS);
		SpeedRange sp3 = state3.getSpeed();
		assertEquals("new min speed valid", 120, sp3.getMin(), EPS);
		assertEquals("new max speed valid", 220, sp3.getMax(), EPS);
		
		SpeedRange sp7 = state7.getSpeed();
		assertEquals("new min speed valid", 100, sp7.getMin(), EPS);
		assertEquals("new max speed valid", 200, sp7.getMax(), EPS);

		
	}
	
	private void dumpSpeeds()
	{
		Collection<BoundedState> states = space.getBoundedStatesBetween(state1.getTime(), state7.getTime());
		for (Iterator<BoundedState> iterator = states.iterator(); iterator.hasNext();)
		{
			BoundedState state = (BoundedState) iterator.next();
			SpeedRange sp = state.getSpeed();
			if(sp == null)
				System.out.println("time:" + state.getTime().getTime() + " speed unset");
			else
			System.out.println("time:" + state.getTime().getTime() + " speed:" + sp.getMin() + " - " + sp.getMax());
		}
	}
}
