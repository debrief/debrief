package com.planetmayo.debrief.satc.support;

import java.util.Date;

import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContributionTest;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisTest;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class TestSupport
{

	private TrackGenerator _genny;

	public void setGenerator(TrackGenerator genny)
	{
		_genny = genny;
	}

	private TrackGenerator getGenerator()
	{
		return _genny;
	}

	@SuppressWarnings("deprecation")
	public void loadSampleData(boolean useLong)
	{
		// TODO: replace this with deserializing an existing SteppingGenerator, once
		// we have a strategy:
		// https://bitbucket.org/ianmayo/deb_satc/issue/10

		// clear the geneartor first
		getGenerator().contributions().clear();

		// now load some data
		BearingMeasurementContribution bmc = new BearingMeasurementContribution();
		RangeForecastContribution rangeF = new RangeForecastContribution();
		final String thePath;
		if (useLong)
			thePath = BearingMeasurementContributionTest.THE_PATH;
		else
			thePath = BearingMeasurementContributionTest.THE_SHORT_PATH;

		try
		{
			// populate the bearing data
			bmc.loadFrom(SupportServices.INSTANCE.getIOService().readLinesFrom(
					thePath));
			getGenerator().addContribution(bmc);

			// and populate the range data
			rangeF.loadFrom(SupportServices.INSTANCE.getIOService().readLinesFrom(
					thePath));
			getGenerator().addContribution(rangeF);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		SpeedForecastContribution speed = new SpeedForecastContribution();
		speed.setMinSpeed(12);
		speed.setMaxSpeed(43);
		getGenerator().addContribution(speed);

		// hey, how about a time-bounded course constraint?
		CourseForecastContribution course = new CourseForecastContribution();
		course.setStartDate(new Date("2010/Jan/12 00:14:31"));
		course.setFinishDate(new Date("2010/Jan/12 00:18:25"));
		course.setMinCourse(45);
		course.setMaxCourse(81);
		getGenerator().addContribution(course);

		// hey, how about a time-bounded course constraint?
		SpeedForecastContribution speed2 = new SpeedForecastContribution();
		speed2.setStartDate(new Date("2010/Jan/12 00:25:00"));
		speed2.setFinishDate(new Date("2010/Jan/12 00:31:00"));
		speed2.setMinSpeed(8);
		speed2.setMaxSpeed(27);
		getGenerator().addContribution(speed2);

		LocationAnalysisContribution lac = new LocationAnalysisContribution();
		getGenerator().addContribution(lac);
	}

	public void nextTest()
	{
		// clear the bounded states
		LocationAnalysisTest lat = new LocationAnalysisTest();
		try
		{
			GeoSupport.clearOutput("Location Analysis");
			lat.testBoundary();
		}
		catch (IncompatibleStateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}