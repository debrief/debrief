/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class CourseForecastContributionTest extends
		ForecastContributionTestBase
{

	@Override
	protected Map<String, Object> getPropertiesForTest()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CourseForecastContribution.ACTIVE, true);
		map.put(CourseForecastContribution.ESTIMATE, 4d);
		map.put(CourseForecastContribution.FINISH_DATE, new Date(6));
		map.put(CourseForecastContribution.START_DATE, new Date(2));
		map.put(CourseForecastContribution.WEIGHT, 7);
		map.put(CourseForecastContribution.MAX_COURSE, Math.PI / 2);
		map.put(CourseForecastContribution.MIN_COURSE, 1d);
		return map;
	}

	@Override
	protected BaseContribution createContribution()
	{
		CourseForecastContribution contribution = new CourseForecastContribution();
		contribution.setEstimate(2d);
		contribution.setActive(false);
		contribution.setFinishDate(new Date(112, 11, 27, 1, 50));
		contribution.setStartDate(new Date(112, 11, 27, 1, 16));
		contribution.setWeight(3);
		contribution.setMaxCourse(Math.PI);
		contribution.setMinCourse(0d);
		return contribution;
	}

	@Test
	public void testCalcError1() throws Exception
	{
		CourseRange cr = new CourseRange(Math.toRadians(315), Math.toRadians(45));
		assertEquals("correct range", 0, cr.calcErrorFor(0, 0), 0.001);
		assertEquals("correct range", 1, cr.calcErrorFor(0, Math.toRadians(315)),
				0.001);
		assertEquals("correct range", 1, cr.calcErrorFor(0, Math.toRadians(-45)),
				0.001);

		cr = new CourseRange(Math.toRadians(-45), Math.toRadians(45));
		assertEquals("correct range", 0, cr.calcErrorFor(0, 0), 0.001);
		assertEquals("correct range", 1, cr.calcErrorFor(0, Math.toRadians(315)),
				0.001);
		assertEquals("correct range", 1, cr.calcErrorFor(0, Math.toRadians(-45)),
				0.001);
	}

	@Test
	public void testCalcError2() throws Exception
	{
		CourseForecastContribution contribution = (CourseForecastContribution) createContribution();

		// start off with an easy one
		contribution.setMinCourse(Math.toRadians(45));
		contribution.setMaxCourse(Math.toRadians(135));
		contribution.setEstimate(Math.toRadians(90));

		assertEquals("easy one right", 0, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(90), 0)), 0.001);
		assertEquals("easy one right", 1, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(45), 0)), 0.001);
		assertEquals("easy one right", 1, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(135), 0)),
				0.001);

		// and some mid=way points
		assertEquals("easy one right", 0.2, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(99), 0)), 0.001);
		assertEquals("easy one right", 0.2, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(81), 0)), 0.001);

		// put us in another cycle domain
		contribution.setMinCourse(Math.toRadians(225));
		contribution.setMaxCourse(Math.toRadians(315));
		contribution.setEstimate(Math.toRadians(270));

		assertEquals("easy one right", 0, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(270), 0)),
				0.001);
		assertEquals("easy one right", 1, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(225), 0)),
				0.001);
		assertEquals("easy one right", 1, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(315), 0)),
				0.001);

		// and some mid=way points
		assertEquals("easy one right", 0.2, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(279), 0)),
				0.001);
		assertEquals("easy one right", 0.2, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(261), 0)),
				0.001);

		// and some bad-cycle values
		assertEquals("easy one right", 0, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(-90), 0)),
				0.001);
		assertEquals("easy one right", 1, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(-135), 0)),
				0.001);
		assertEquals("easy one right", 1, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(-45), 0)),
				0.001);

		// make us span North
		contribution.setMinCourse(Math.toRadians(315));
		contribution.setMaxCourse(Math.toRadians(45));
		contribution.setEstimate(Math.toRadians(0));

		assertEquals("easy one right", 0, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(0), 0)), 0.001);
		assertEquals("easy one right", 1, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(315), 0)),
				0.001);
		assertEquals("easy one right", 1, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(45), 0)), 0.001);

		// and some mid=way points
		assertEquals("easy one right", 0.2, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(9), 0)), 0.001);
		assertEquals("easy one right", 0.2, contribution.calcError(new State(
				new Date(120, 12, 34, 44, 12, 33), null, Math.toRadians(351), 0)),
				0.001);

	}

	@Test
	public void testActUpon() throws Exception
	{
		CourseForecastContribution contribution = (CourseForecastContribution) createContribution();
		ProblemSpace space = createTestSpace();
		contribution.actUpon(space);
		int withCourse = 0;
		for (BoundedState state : space.states())
		{
			if (state.getCourse() != null)
			{
				assertEquals(0d, state.getCourse().getMin(), EPS);
				assertEquals(Math.PI, state.getCourse().getMax(), EPS);
				withCourse++;
			}
		}
		assertEquals(4, withCourse);
	}
}
