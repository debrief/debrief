package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class CourseForecastContributionTest extends ForecastContributionTestBase
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
