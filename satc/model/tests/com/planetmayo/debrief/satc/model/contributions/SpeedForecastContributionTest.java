package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

@SuppressWarnings("deprecation")
public class SpeedForecastContributionTest extends ForecastContributionTestBase
{

	@Override
	protected Map<String, Object> getPropertiesForTest()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SpeedForecastContribution.MAX_SPEED, 12d);
		map.put(SpeedForecastContribution.MIN_SPEED, 1d);
		map.put(SpeedForecastContribution.ACTIVE, true);
		map.put(SpeedForecastContribution.ESTIMATE, 4d);
		map.put(SpeedForecastContribution.FINISH_DATE, new Date(6));
		map.put(SpeedForecastContribution.START_DATE, new Date(2));
		map.put(SpeedForecastContribution.WEIGHT, 7);
		return map;
	}

	@Override
	protected BaseContribution createContribution()
	{
		SpeedForecastContribution contribution = new SpeedForecastContribution();
		contribution.setEstimate(2d);
		contribution.setActive(false);
		contribution.setFinishDate(new Date(112, 11, 27, 1, 25));
		contribution.setMaxSpeed(10d);
		contribution.setMinSpeed(2d);
		contribution.setStartDate(new Date(112, 11, 27, 0, 50));
		contribution.setWeight(3);
		return contribution;
	}
	
	@Test
	public void testActUpon() throws Exception 
	{
		SpeedForecastContribution contribution = (SpeedForecastContribution) createContribution();
		ProblemSpace space = createTestSpace();
		contribution.actUpon(space);
		int withSpeed = 0;
		for (BoundedState state : space.states()) 
		{
			if (state.getSpeed() != null)
			{
				assertEquals(2d, state.getSpeed().getMin(), EPS);
				assertEquals(10d, state.getSpeed().getMax(), EPS);
				withSpeed++;
			}			
		}
		assertEquals(3, withSpeed);
	}
}
