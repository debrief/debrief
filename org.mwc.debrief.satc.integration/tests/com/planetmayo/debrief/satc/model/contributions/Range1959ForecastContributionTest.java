package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import MWC.GUI.Layers;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.util.ObjectUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class Range1959ForecastContributionTest
{

	private Range1959ForecastContribution _freq;
	private Layers _layers;

	@Before
	public void setUp() throws Exception
	{
		_freq = new Range1959ForecastContribution();
	}

	@Test
	public void testLoadFromOne() throws Exception
	{
		assertFalse("should be empty", _freq.hasData());

		_freq.loadFrom(TestSupport.getFreqDataOne());
		assertTrue("should not be empty", _freq.hasData());
		assertEquals("correct start date", new Date(110, 00, 12, 13, 10, 00),
				_freq.getStartDate());
		assertEquals("correct finish date", new Date(110, 00, 12, 13, 20, 00),
				_freq.getFinishDate());

		// and apply it to a problem space
		assertEquals("correct freq", -0.0239, _freq.calculateFreqRate(), 0.0001);
		ProblemSpace space = new ProblemSpace();
		// and add the bearings
		addBearing(space, "100112", "131000", -99.53);
		addBearing(space, "100112", "131050", -97.18);
		addBearing(space, "100112", "131140", -94.8);
		addBearing(space, "100112", "131230", -92.42);
		addBearing(space, "100112", "131320", -90.04);
		addBearing(space, "100112", "131410", -87.66);
		addBearing(space, "100112", "131500", -85.3);
		addBearing(space, "100112", "131550", -82.96);
		addBearing(space, "100112", "131640", -80.65);
		addBearing(space, "100112", "131730", -78.37);
		addBearing(space, "100112", "131820", -76.14);
		addBearing(space, "100112", "131910", -73.95);
		addBearing(space, "100112", "132000", -71.82);

		assertEquals("correct bearing", Math.toRadians(2.7869), _freq.calculateBearingRate(space),
				0.0001);
	}

	private void addBearing(ProblemSpace space, String date, String time,
			double bearingDegs) throws IncompatibleStateException
	{
		Date theDate = ObjectUtils.safeParseDate(new SimpleDateFormat(
				"yyMMdd HHmmss"), date + " " + time);

		BoundedState state = new BoundedState(theDate);
		state.setBearingValue(Math.toRadians(bearingDegs));
		space.add(state);
	}

	@Test
	public void testLoadFromTwo() throws Exception
	{
		assertFalse("should be empty", _freq.hasData());

		_freq.loadFrom(TestSupport.getFreqDataTwo());
		assertTrue("should not be empty", _freq.hasData());
		assertEquals("correct start date", new Date(110, 00, 12, 13, 35, 00),
				_freq.getStartDate());
		assertEquals("correct finish date", new Date(110, 00, 12, 13, 40, 00),
				_freq.getFinishDate());

		// and apply it to a problem space
		assertEquals("correct freq", -0.0019, _freq.calculateFreqRate(), 0.0001);
		ProblemSpace space = new ProblemSpace();
		// and add the bearings
		addBearing(space, "100112", "133500", -48.41);
		addBearing(space, "100112", "133550", -47.79);
		addBearing(space, "100112", "133640", -47.19);
		addBearing(space, "100112", "133730", -46.6);
		addBearing(space, "100112", "133820", -46.03);
		addBearing(space, "100112", "133910", -45.48);
		addBearing(space, "100112", "134000", -44.94);
		addBearing(space, "100112", "134050", -44.42);

		assertEquals("correct bearing", Math.toRadians(0.6938), _freq.calculateBearingRate(space),
				0.0001);
	}

	@Test
	public void testActUpon() throws Exception
	{
		testLoadFromOne();
		ProblemSpace ps = new ProblemSpace();

		_freq.actUpon(ps);
		for (BoundedState state : ps.states())
		{
			Geometry geo = state.getLocation().getGeometry();
			Coordinate[] coords = geo.getCoordinates();
			for (int i = 0; i <= 4; i++)
			{
				Coordinate coordinate = coords[i];
				assertNotNull("we should have a coordinate", coordinate);
			}
		}
		assertEquals("read in all lines", 5, ps.size());
	}

}
