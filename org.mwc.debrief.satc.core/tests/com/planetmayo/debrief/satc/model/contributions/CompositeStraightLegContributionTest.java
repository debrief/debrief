package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class CompositeStraightLegContributionTest extends
		ForecastContributionTestBase
{

	private static final String TEST_NAME_1 = "test_name_1";
	private static final String TEST_NAME_2 = "test_name_2";

	@Override
	protected Map<String, Object> getPropertiesForTest()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(StraightLegForecastContribution.ACTIVE, true);
		map.put(StraightLegForecastContribution.FINISH_DATE, new Date(6));
		map.put(StraightLegForecastContribution.START_DATE, new Date(2));
		map.put(StraightLegForecastContribution.WEIGHT, 7);
		return map;
	}

	@Override
	protected BaseContribution createContribution()
	{
		CompositeStraightLegForecastContribution contribution = new CompositeStraightLegForecastContribution();
		contribution.setActive(false);
		contribution.setName(TEST_NAME_1);
		contribution.setFinishDate(new Date(112, 11, 27, 1, 50));
		contribution.setStartDate(new Date(112, 11, 27, 1, 16));
		contribution.setWeight(3);
		return contribution;
	}

	protected BaseContribution createContribution2()
	{
		CompositeStraightLegForecastContribution contribution = new CompositeStraightLegForecastContribution();
		contribution.setActive(false);
		contribution.setName(TEST_NAME_2);
		contribution.setFinishDate(new Date(112, 11, 27, 1, 53));
		contribution.setStartDate(new Date(112, 11, 27, 1, 45));
		contribution.setWeight(3);
		return contribution;
	}

	@Test
	public void testActUpon() throws Exception
	{
		StraightLegForecastContribution contribution = (StraightLegForecastContribution) createContribution();
		ProblemSpace space = createTestSpace();
		contribution.actUpon(space);
		int withCourse = 0;
		for (BoundedState state : space.states())
		{
			if (state.getMemberOf() != null)
				if (state.getMemberOf().equals(TEST_NAME_1))
					withCourse++;
		}
		assertEquals(4, withCourse);
	}

	@Test
	public void testActUpon2() throws Exception
	{
		CompositeStraightLegForecastContribution contribution = (CompositeStraightLegForecastContribution) createContribution();
		contribution.getCourse().setMinCourse(34.0);
		contribution.getCourse().setMaxCourse(null);
		contribution.getSpeed().setMaxSpeed(null);
		ProblemSpace space = createTestSpace();
		contribution.actUpon(space);
		int inLegOne = 0;
		int hasCourseBounds = 0;
		int hasSpeedBounds = 0;
		for (BoundedState state : space.states())
		{
			if (state.getMemberOf() != null)
				if (state.getMemberOf().equals(TEST_NAME_1))
					inLegOne++;
			if (state.getCourse() != null)
				hasCourseBounds++;
			if (state.getSpeed() != null)
				hasSpeedBounds++;
		}
		assertEquals(4, inLegOne);
		assertEquals(0, hasCourseBounds);
		assertEquals(0, hasSpeedBounds);

		// provide the missing course maximum
		contribution.getCourse().setMaxCourse(134.0);
		space = createTestSpace();
		contribution.actUpon(space);
		inLegOne = 0;
		hasCourseBounds = 0;
		hasSpeedBounds = 0;
		for (BoundedState state : space.states())
		{
			if (state.getMemberOf() != null)
				if (state.getMemberOf().equals(TEST_NAME_1))
					inLegOne++;
			if (state.getCourse() != null)
				hasCourseBounds++;
			if (state.getSpeed() != null)
				hasSpeedBounds++;
		}
		assertEquals(4, inLegOne);
		assertEquals(4, hasCourseBounds);
		assertEquals(0, hasSpeedBounds);

		// provide the missing speed values
		contribution.getSpeed().setMinSpeed(4.0);
		contribution.getSpeed().setMaxSpeed(13.0);
		space = createTestSpace();
		contribution.actUpon(space);
		inLegOne = 0;
		hasCourseBounds = 0;
		hasSpeedBounds = 0;
		for (BoundedState state : space.states())
		{
			if (state.getMemberOf() != null)
				if (state.getMemberOf().equals(TEST_NAME_1))
					inLegOne++;
			if (state.getCourse() != null)
				hasCourseBounds++;
			if (state.getSpeed() != null)
				hasSpeedBounds++;
		}

		assertEquals(4, inLegOne);
		assertEquals(4, hasCourseBounds);
		assertEquals(4, hasSpeedBounds);


		// lose a speed value
		contribution.getSpeed().setMinSpeed(null);
		contribution.getSpeed().setMaxSpeed(13.0);
		space = createTestSpace();
		contribution.actUpon(space);
		inLegOne = 0;
		hasCourseBounds = 0;
		hasSpeedBounds = 0;
		for (BoundedState state : space.states())
		{
			if (state.getMemberOf() != null)
				if (state.getMemberOf().equals(TEST_NAME_1))
					inLegOne++;
			if (state.getCourse() != null)
				hasCourseBounds++;
			if (state.getSpeed() != null)
				hasSpeedBounds++;
		}

		assertEquals(4, inLegOne);
		assertEquals(4, hasCourseBounds);
		assertEquals(0, hasSpeedBounds);
	}

	@Test
	public void testOverlap() throws Exception
	{
		StraightLegForecastContribution contribution = (StraightLegForecastContribution) createContribution();
		StraightLegForecastContribution contribution2 = (StraightLegForecastContribution) createContribution2();
		ProblemSpace space = createTestSpace();
		contribution.actUpon(space);
		boolean hasThrown = false;
		try
		{
			contribution2.actUpon(space);
		}
		catch (IncompatibleStateException re)
		{
			assertEquals(
					"Correct message provided",
					"We don't support overlapping legs. Old leg:test_name_1 New leg:test_name_2 state at:Thu Dec 27 01:50:00 GMT 2012",
					re.getMessage());
			hasThrown = true;
		}
		assertTrue("an exception should have been thrown, for overlapping legs",
				hasThrown);
	}

}
