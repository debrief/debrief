package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

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

	@Test
	public void testErrorCalc()
	{
		SpeedForecastContribution speed = (SpeedForecastContribution) createContribution();
		speed.setMaxSpeed(GeoSupport.kts2MSec(200d));
		speed.setEstimate(GeoSupport.kts2MSec(60));
		speed.setActive(true);

		// ok, create the route at about the right speed
		Point startP = GeoSupport.getFactory().createPoint(new Coordinate(1, 0));
		Date startTime = new Date(112, 11, 27, 1, 00);
		Point endP = GeoSupport.getFactory().createPoint(new Coordinate(2, 0));
		Date endTime = new Date(112, 11, 27, 2, 00);
		StraightRoute theGoodRoute = new StraightRoute("goodR", startP, startTime,
				endP, endTime);

		// ok, create the route at a much slower
		startP = GeoSupport.getFactory().createPoint(new Coordinate(1, 0));
		startTime = new Date(112, 11, 27, 1, 00);
		endP = GeoSupport.getFactory().createPoint(new Coordinate(1.1, 0));
		endTime = new Date(112, 11, 27, 2, 00);
		StraightRoute theSlowRoute = new StraightRoute("slowR", startP, startTime,
				endP, endTime);

		// ok, and the route at a faster slower
		startP = GeoSupport.getFactory().createPoint(new Coordinate(1, 0));
		startTime = new Date(112, 11, 27, 1, 00);
		endP = GeoSupport.getFactory().createPoint(new Coordinate(4, 0));
		endTime = new Date(112, 11, 27, 2, 00);
		StraightRoute theFastRoute = new StraightRoute("fastR", startP, startTime,
				endP, endTime);


		// we'll need some states, so the route can correctly segment itself
		ArrayList<BoundedState> states = new ArrayList<BoundedState>();
		states.add(new BoundedState(new Date(112, 11, 27, 0, 30)));
		states.add(new BoundedState(new Date(112, 11, 27, 1, 00)));
		states.add(new BoundedState(new Date(112, 11, 27, 1, 15)));
		states.add(new BoundedState(new Date(112, 11, 27, 1, 30)));
		states.add(new BoundedState(new Date(112, 11, 27, 1, 45)));
		states.add(new BoundedState(new Date(112, 11, 27, 2, 00)));
		states.add(new BoundedState(new Date(112, 11, 27, 2, 15)));

		// ok, dice up the route
		theGoodRoute.generateSegments(states);
		theSlowRoute.generateSegments(states);
		theFastRoute.generateSegments(states);

		assertEquals("good speed is low", 0.0148, speed.calculateErrorScoreFor(theGoodRoute), 0.0001);
		assertEquals("slow score is high", 9.2592, speed.calculateErrorScoreFor(theSlowRoute), 0.0001);
		assertEquals("fast score is high", 20.600, speed.calculateErrorScoreFor(theFastRoute), 0.0001);
		
	}
}
