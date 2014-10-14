/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.states;

import java.util.Date;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.util.GeoSupport;

import static org.junit.Assert.*;

public class BoundedStateTest extends ModelTestBase
{

	@Test
	public void testCreate() throws Exception
	{
		Date date = new Date();
		BoundedState state = new BoundedState(date);
		assertNull(state.getCourse());
		assertNull(state.getSpeed());
		assertNull(state.getLocation());
		assertEquals(date, state.getTime());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalCreate() throws Exception
	{
		new BoundedState(null);
	}

	@Test
	public void testConstraintTo() throws Exception
	{
		BoundedState state = new BoundedState(new Date());
		CourseRange courseRange = new CourseRange(0.1, 0.2);
		SpeedRange speedRange = new SpeedRange(1, 2);
		SpeedRange speedRange2 = new SpeedRange(1.5, 2.5);
		LocationRange locationRange = new LocationRange(
				GeoSupport.createPoint(0, 0).buffer(2, 3));

		state.constrainTo(courseRange);
		assertEquals(courseRange, state.getCourse());
		assertNull(state.getSpeed());
		assertNull(state.getLocation());

		state.constrainTo(speedRange);
		assertEquals(courseRange, state.getCourse());
		assertEquals(speedRange, state.getSpeed());
		assertNull(state.getLocation());

		state.constrainTo(locationRange);
		assertEquals(courseRange, state.getCourse());
		assertEquals(speedRange, state.getSpeed());
		assertEquals(locationRange, state.getLocation());

		state.constrainTo(courseRange);
		state.constrainTo(speedRange2);
		state.constrainTo(locationRange);
		assertEquals(courseRange, state.getCourse());
		assertEquals(new SpeedRange(1.5, 2), state.getSpeed());
		assertEquals(locationRange, state.getLocation());
	}

	@Test
	public void testConstraintToBoundedState() throws Exception
	{
		BoundedState state = new BoundedState(new Date());
		BoundedState state2 = new BoundedState(new Date());

		CourseRange courseRange = new CourseRange(0.1, 0.2);
		SpeedRange speedRange = new SpeedRange(1, 2);
		SpeedRange speedRange2 = new SpeedRange(1.5, 2.5);
		LocationRange locationRange = new LocationRange(GeoSupport.createPoint(0, 0).buffer(2, 3));

		state2.constrainTo(courseRange);
		state2.constrainTo(speedRange);
		state2.constrainTo(locationRange);
		state.constrainTo(speedRange2);

		state.constrainTo(state2);
		assertEquals(courseRange, state.getCourse());
		assertEquals(new SpeedRange(1.5, 2), state.getSpeed());
		assertEquals(locationRange, state.getLocation());
	}
}
