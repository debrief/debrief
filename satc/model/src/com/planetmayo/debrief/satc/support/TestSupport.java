package com.planetmayo.debrief.satc.support;

import java.util.Date;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContributionTest;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisTest;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution.ROrigin;
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

	@SuppressWarnings("deprecation")
	public void loadTinyData()
	{
		// TODO: replace this with deserializing an existing SteppingGenerator, once
		// we have a strategy:
		// https://bitbucket.org/ianmayo/deb_satc/issue/10

		// clear the geneartor first
		getGenerator().contributions().clear();

		// now load some data
		BearingMeasurementContribution bmc = new BearingMeasurementContribution();

		// and add some data
		bmc.addThis(new BMeasurement(new GeoPoint(0.3, 30.1), 12.2, new Date(110,
				00, 12, 12, 13, 29), 15000d));
		bmc.addThis(new BMeasurement(new GeoPoint(0.2, 30.2), 12.2, new Date(110,
				00, 12, 12, 14, 29), 15000d));
		bmc.addThis(new BMeasurement(new GeoPoint(0.1, 30.3), 12.2, new Date(110,
				00, 12, 12, 16, 29), 15000d));
		bmc.addThis(new BMeasurement(new GeoPoint(0.2, 30.2), 12.2, new Date(110,
				00, 12, 12, 17, 29), 15000d));
		bmc.addThis(new BMeasurement(new GeoPoint(0.4, 30.3), 12.2, new Date(110,
				00, 12, 12, 19, 29), 15000d));
		bmc.setBearingError(3d);
		getGenerator().addContribution(bmc);

		RangeForecastContribution rangeF = new RangeForecastContribution();
		rangeF.addThis(new ROrigin(new GeoPoint(0.3, 30.2), new Date(110, 00, 12,
				12, 13, 29)));
		rangeF.addThis(new ROrigin(new GeoPoint(0.2, 30.2), new Date(110, 00, 12,
				12, 14, 29)));
		rangeF.addThis(new ROrigin(new GeoPoint(0.1, 30.3), new Date(110, 00, 12,
				12, 15, 29)));
		rangeF.addThis(new ROrigin(new GeoPoint(0.3, 30.2), new Date(110, 00, 12,
				12, 17, 29)));
		rangeF.addThis(new ROrigin(new GeoPoint(0.3, 30.2), new Date(110, 00, 12,
				12, 19, 29)));
		rangeF.setMaxRange(3000);
		rangeF.setMinRange(500);
		getGenerator().addContribution(rangeF);

		SpeedForecastContribution speed = new SpeedForecastContribution();
		speed.setMinSpeed(12);
		speed.setMaxSpeed(43);
		speed.setEstimate(21);
		getGenerator().addContribution(speed);

		// hey, how about a time-bounded course constraint?
		CourseForecastContribution course = new CourseForecastContribution();
		course.setStartDate(new Date("2010/Jan/12 12:14:31"));
		course.setFinishDate(new Date("2010/Jan/12 12:18:25"));
		course.setMinCourse(45);
		course.setMaxCourse(81);
		course.setEstimate(75);
		getGenerator().addContribution(course);

		// hey, how about a time-bounded course constraint?
		SpeedForecastContribution speed2 = new SpeedForecastContribution();
		speed2.setStartDate(new Date("2010/Jan/12 12:25:00"));
		speed2.setFinishDate(new Date("2010/Jan/12 12:31:00"));
		speed2.setMinSpeed(8);
		speed2.setMaxSpeed(27);
		speed2.setEstimate(15);
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