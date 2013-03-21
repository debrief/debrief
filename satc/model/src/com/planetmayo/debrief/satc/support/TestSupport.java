package com.planetmayo.debrief.satc.support;

import java.util.ArrayList;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisTest;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class TestSupport
{

	private IBoundsManager _genny;

	private IBoundsManager getGenerator()
	{
		return _genny;
	}

	public static ArrayList<String> getLongData()
	{
		final ArrayList<String> rows = new ArrayList<String>();

		rows.add(";;IGNORE	YYMMDD	HHMMSS	IGNORE	IGNORE	LAT_DEG	LAT_MIN	LAT_SEC	LAT_HEM	LONG_DEG	LONG_MIN	LONG_SEC	LONG_HEM	BEARING	MAX_RNG");
		rows.add(";SENSOR:	100112	121329	SENSOR	@A	0	3	57.38	S	30	0	8.65	W	1.5	15000");
		rows.add(";SENSOR:	100112	121359	SENSOR	@A	0	3	52.31	S	30	0	11.09	W	1.1	15000");
		rows.add(";SENSOR:	100112	121429	SENSOR	@A	0	3	51.7	S	30	0	16.99	W	1.4	15000");
		rows.add(";SENSOR:	100112	121459	SENSOR	@A	0	3	51.7	S	30	0	22.99	W	1.8	15000");
		rows.add(";SENSOR:	100112	121529	SENSOR	@A	0	3	51.7	S	30	0	28.99	W	2.2	15000");
		rows.add(";SENSOR:	100112	121559	SENSOR	@A	0	3	51.7	S	30	0	34.99	W	2.5	15000");
		rows.add(";SENSOR:	100112	121629	SENSOR	@A	0	3	51.7	S	30	0	40.99	W	2.9	15000");
		rows.add(";SENSOR:	100112	121644	SENSOR	@A	0	3	51.7	S	30	0	43.99	W	3.1	15000");
		rows.add(";SENSOR:	100112	121744	SENSOR	@A	0	3	51.7	S	30	0	55.99	W	3.8	15000");
		rows.add(";SENSOR:	100112	121814	SENSOR	@A	0	3	51.7	S	30	1	1.99	W	4.2	15000");
		rows.add(";SENSOR:	100112	121929	SENSOR	@A	0	3	42.55	S	30	1	10.68	W	3.7	15000");
		rows.add(";SENSOR:	100112	122029	SENSOR	@A	0	3	30.74	S	30	1	12.76	W	2	15000");
		rows.add(";SENSOR:	100112	122129	SENSOR	@A	0	3	16.96	S	30	1	15.19	W	0.3	15000");
		rows.add(";SENSOR:	100112	122229	SENSOR	@A	0	3	3.18	S	30	1	17.62	W	-1.8	15000");
		rows.add(";SENSOR:	100112	122329	SENSOR	@A	0	2	49.39	S	30	1	20.05	W	-4.1	15000");
		rows.add(";SENSOR:	100112	122429	SENSOR	@A	0	2	35.6	S	30	1	22.48	W	-6.9	15000");
		rows.add(";SENSOR:	100112	122529	SENSOR	@A	0	2	21.82	S	30	1	24.91	W	-10.1	15000");
		rows.add(";SENSOR:	100112	122629	SENSOR	@A	0	2	8.03	S	30	1	27.34	W	-14	15000");
		rows.add(";SENSOR:	100112	122729	SENSOR	@A	0	1	54.24	S	30	1	29.78	W	-18.6	15000");
		rows.add(";SENSOR:	100112	122829	SENSOR	@A	0	1	40.45	S	30	1	32.21	W	-24.2	15000");
		rows.add(";SENSOR:	100112	122929	SENSOR	@A	0	1	26.46	S	30	1	32.28	W	-32	15000");
		rows.add(";SENSOR:	100112	123029	SENSOR	@A	0	1	12.46	S	30	1	32.28	W	-41	15000");
		rows.add(";SENSOR:	100112	123129	SENSOR	@A	0	1	0.29	S	30	1	36.37	W	-48.4	15000");
		rows.add(";SENSOR:	100112	123229	SENSOR	@A	0	0	53.29	S	30	1	48.49	W	-50.6	15000");
		rows.add(";SENSOR:	100112	123329	SENSOR	@A	0	0	46.29	S	30	2	0.61	W	-55.3	15000");
		rows.add(";SENSOR:	100112	123429	SENSOR	@A	0	0	39.29	S	30	2	12.74	W	-62.5	15000");
		rows.add(";SENSOR:	100112	123529	SENSOR	@A	0	0	32.29	S	30	2	24.86	W	-73	15000");
		rows.add(";SENSOR:	100112	123629	SENSOR	@A	0	0	25.29	S	30	2	36.99	W	-88.1	15000");
		rows.add(";SENSOR:	100112	123729	SENSOR	@A	0	0	18.29	S	30	2	49.11	W	-108	15000");
		rows.add(";SENSOR:	100112	123829	SENSOR	@A	0	0	11.29	S	30	3	1.24	W	-129.2	15000");
		rows.add(";SENSOR:	100112	123929	SENSOR	@A	0	0	5.39	S	30	3	13.71	W	-146.5	15000");
		rows.add(";SENSOR:	100112	124029	SENSOR	@A	0	0	5.39	S	30	3	27.71	W	-158.7	15000");
		rows.add(";SENSOR:	100112	124129	SENSOR	@A	0	0	5.39	S	30	3	41.71	W	-169.3	15000");
		rows.add(";SENSOR:	100112	124229	SENSOR	@A	0	0	5.39	S	30	3	49.87	W	-172.3	15000");
		rows.add(";SENSOR:	100112	124329	SENSOR	@A	0	0	5.39	S	30	3	57.87	W	-174.6	15000");
		rows.add(";SENSOR:	100112	124429	SENSOR	@A	0	0	8.65	S	30	4	4.46	W	-175.1	15000");
		rows.add(";SENSOR:	100112	124529	SENSOR	@A	0	0	16.62	S	30	4	4.73	W	-169.8	15000");
		rows.add(";SENSOR:	100112	124629	SENSOR	@A	0	0	24.62	S	30	4	4.73	W	-164.2	15000");
		rows.add(";SENSOR:	100112	124729	SENSOR	@A	0	0	36.96	S	30	4	4.73	W	-157.1	15000");
		rows.add(";SENSOR:	100112	124829	SENSOR	@A	0	0	50.96	S	30	4	4.73	W	-148.6	15000");
		rows.add(";SENSOR:	100112	124929	SENSOR	@A	0	1	4.96	S	30	4	4.73	W	-139	15000");
		rows.add(";SENSOR:	100112	125029	SENSOR	@A	0	1	18.96	S	30	4	4.73	W	-129	15000");
		rows.add(";SENSOR:	100112	125129	SENSOR	@A	0	1	32.96	S	30	4	4.73	W	-119	15000");
		rows.add(";SENSOR:	100112	125229	SENSOR	@A	0	1	46.96	S	30	4	4.73	W	-109.6	15000");
		rows.add(";SENSOR:	100112	125329	SENSOR	@A	0	2	0.96	S	30	4	4.73	W	-101.2	15000");
		rows.add(";SENSOR:	100112	125429	SENSOR	@A	0	2	12.21	S	30	4	12.68	W	-97	15000");
		rows.add(";SENSOR:	100112	125529	SENSOR	@A	0	2	22.94	S	30	4	21.68	W	-93.1	15000");
		rows.add(";SENSOR:	100112	125629	SENSOR	@A	0	2	33.66	S	30	4	30.68	W	-88.8	15000");
		rows.add(";SENSOR:	100112	125744	SENSOR	@A	0	2	49.98	S	30	4	36.93	W	-80.5	15000");
		rows.add(";SENSOR:	100112	125829	SENSOR	@A	0	2	59.84	S	30	4	40.52	W	-75.9	15000");
		rows.add(";SENSOR:	100112	125914	SENSOR	@A	0	3	9.71	S	30	4	44.11	W	-71.7	15000");
		rows.add(";SENSOR:	100112	130014	SENSOR	@A	0	3	22.42	S	30	4	49.71	W	-66.6	15000");
		rows.add(";SENSOR:	100112	130129	SENSOR	@A	0	3	35.82	S	30	5	0.96	W	-61	15000");
		rows.add(";SENSOR:	100112	130229	SENSOR	@A	0	3	46.55	S	30	5	9.96	W	-56.5	15000");
		rows.add(";SENSOR:	100112	130329	SENSOR	@A	0	3	57.27	S	30	5	18.95	W	-52	15000");
		rows.add(";SENSOR:	100112	130429	SENSOR	@A	0	4	8	S	30	5	27.95	W	-47.5	15000");
		rows.add(";SENSOR:	100112	130529	SENSOR	@A	0	4	18.72	S	30	5	36.95	W	-43.2	15000");

		return rows;
	}

	public static ArrayList<String> getShortData()
	{
		// and put them into an array list
		final ArrayList<String> rows = new ArrayList<String>();
		rows.add(";;IGNORE	YYMMDD	HHMMSS	IGNORE	IGNORE	LAT_DEG	LAT_MIN	LAT_SEC	LAT_HEM	LONG_DEG	LONG_MIN	LONG_SEC	LONG_HEM	BEARING	MAX_RNG");
		rows.add(";SENSOR:	100112	121329	SENSOR	@A	0	3	57.38	S	30	0	8.65	W	1.5	15000");
//		rows.add(";SENSOR:	100112	121459	SENSOR	@A	0	3	51.7	S	30	0	22.99	W	1.8	15000");
		rows.add(";SENSOR:	100112	121529	SENSOR	@A	0	3	51.7	S	30	0	28.99	W	2.2	15000");
//		rows.add(";SENSOR:	100112	121644	SENSOR	@A	0	3	51.7	S	30	0	43.99	W	3.1	15000");
		rows.add(";SENSOR:	100112	121744	SENSOR	@A	0	3	51.7	S	30	0	55.99	W	3.8	15000");
//		rows.add(";SENSOR:	100112	122029	SENSOR	@A	0	3	30.74	S	30	1	12.76	W	2	15000");
		rows.add(";SENSOR:	100112	122129	SENSOR	@A	0	3	16.96	S	30	1	15.19	W	0.3	15000");
		rows.add(";SENSOR:	100112	122429	SENSOR	@A	0	2	35.6	S	30	1	22.48	W	-6.9	15000");

		return rows;
	}

	public void loadSampleData(boolean useLong)
	{
		// clear the geneartor first
		getGenerator().clear();

		// now load some data
		BearingMeasurementContribution bmc = new BearingMeasurementContribution();
		bmc.setName("Measured bearing");
		bmc.setAutoDetect(true);
		RangeForecastContribution rangeF = new RangeForecastContribution();
		rangeF.setName("Measured range");
		ArrayList<String> rows;
		if (useLong)
			rows = getLongData();
		// thePath = BearingMeasurementContributionTest.THE_PATH;
		else
			rows = getShortData();
		// thePath = BearingMeasurementContributionTest.THE_SHORT_PATH;

		try
		{
			// populate the bearing data
			bmc.loadFrom(rows);
			getGenerator().addContribution(bmc);

			// and populate the range data
			rangeF.loadFrom(rows);
			getGenerator().addContribution(rangeF);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		SpeedForecastContribution speed = new SpeedForecastContribution();
		speed.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss", "100112 121331"));
		speed.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 122025"));
		speed.setMinSpeed(GeoSupport.kts2MSec(12d));
		speed.setMaxSpeed(GeoSupport.kts2MSec(25d));
		speed.setName("Initial speed obs");
		getGenerator().addContribution(speed);

		// try a location forecast
		LocationForecastContribution locF = new LocationForecastContribution();
		locF.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss", "100112 121300"));
		locF.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 121700"));
		locF.setLocation(new GeoPoint(0.03, -30.0));
		locF.setLimit(3000d);
		locF.setName("Last known location");
		locF.setActive(false);
		getGenerator().addContribution(locF);
		
		// hey, how about a time-bounded course constraint?
		CourseForecastContribution course = new CourseForecastContribution();
		course.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss", "100112 121231"));
		course.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 122525"));
		course.setMinCourse(Math.toRadians(225));
		course.setMaxCourse(Math.toRadians(315));
		course.setName("Last known course");
		getGenerator().addContribution(course);

		// hey, how about a time-bounded course constraint?
		SpeedForecastContribution speed2 = new SpeedForecastContribution();
		speed2.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 122500"));
		speed2.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 123100"));
		speed2.setMinSpeed(GeoSupport.kts2MSec(8d));
		speed2.setMaxSpeed(GeoSupport.kts2MSec(27d));
		speed2.setName("Later speed obs");
		getGenerator().addContribution(speed2);
		
		// that's nothing - we can now do straight leg forecasts
		StraightLegForecastContribution st = new StraightLegForecastContribution();
		st.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 122100"));
		st.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 122800"));
		st.setName("Straight prediction");
		getGenerator().addContribution(st);

		// and our analysis contributions
		SpeedAnalysisContribution speedA = new SpeedAnalysisContribution();
		speedA.setActive(false);
		getGenerator().addContribution(speedA);
		CourseAnalysisContribution courseA = new CourseAnalysisContribution();
		courseA.setActive(false);
		getGenerator().addContribution(courseA);
		getGenerator().addContribution(new LocationAnalysisContribution());
	
		// ok, and get it to go for it
		getGenerator().run();
	}


	public void loadGoodData()
	{
		// clear the geneartor first
		getGenerator().clear();

		// now load some data
		BearingMeasurementContribution bmc = new BearingMeasurementContribution();
		bmc.setName("Measured bearing");
		bmc.setAutoDetect(false);
		RangeForecastContribution rangeF = new RangeForecastContribution();
		rangeF.setName("Measured range");
		ArrayList<String> rows = getLongData();

		try
		{
			// populate the bearing data
			bmc.loadFrom(rows);
			getGenerator().addContribution(bmc);

			// and populate the range data
			rangeF.loadFrom(rows);
			getGenerator().addContribution(rangeF);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// sort out the legs
		StraightLegForecastContribution st1 = new StraightLegForecastContribution();
		st1.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 121330"));
		st1.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 123029"));
		st1.setName("Straight leg one");
		getGenerator().addContribution(st1);

		StraightLegForecastContribution st2 = new StraightLegForecastContribution();
		st2.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 123329"));
		st2.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 124829"));
		st2.setName("Straight leg two");
		getGenerator().addContribution(st2);


		StraightLegForecastContribution st3 = new StraightLegForecastContribution();
		st3.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 125100"));
		st3.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 130429"));
		st3.setName("Straight leg three");
		getGenerator().addContribution(st3);

		SpeedForecastContribution speed = new SpeedForecastContribution();
		speed.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss", "100112 121330"));
		speed.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 130429"));
		speed.setMinSpeed(GeoSupport.kts2MSec(4d));
		speed.setMaxSpeed(GeoSupport.kts2MSec(25d));
		speed.setEstimate(GeoSupport.kts2MSec(8d));
		speed.setName("Initial speed forecast");
		getGenerator().addContribution(speed);
				
//
//		// try a location forecast
//		LocationForecastContribution locF = new LocationForecastContribution();
//		locF.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss", "100112 121300"));
//		locF.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 121700"));
//		locF.setLocation(new GeoPoint(0.03, -30.0));
//		locF.setLimit(3000d);
//		locF.setName("Last known location");
//		locF.setActive(false);
//		getGenerator().addContribution(locF);
//		
//		// hey, how about a time-bounded course constraint?
//		CourseForecastContribution course = new CourseForecastContribution();
//		course.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss", "100112 121231"));
//		course.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 122525"));
//		course.setMinCourse(Math.toRadians(225));
//		course.setMaxCourse(Math.toRadians(315));
//		course.setName("Last known course");
//		getGenerator().addContribution(course);
//
//		// hey, how about a time-bounded course constraint?
//		SpeedForecastContribution speed2 = new SpeedForecastContribution();
//		speed2.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 122500"));
//		speed2.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 123100"));
//		speed2.setMinSpeed(GeoSupport.kts2MSec(8d));
//		speed2.setMaxSpeed(GeoSupport.kts2MSec(27d));
//		speed2.setName("Later speed obs");
//		getGenerator().addContribution(speed2);

		// and our analysis contributions
		SpeedAnalysisContribution speedA = new SpeedAnalysisContribution();
		speedA.setActive(true);
		getGenerator().addContribution(speedA);
		CourseAnalysisContribution courseA = new CourseAnalysisContribution();
		courseA.setActive(true);
		getGenerator().addContribution(courseA);
		getGenerator().addContribution(new LocationAnalysisContribution());
	
		// ok, and get it to go for it
		getGenerator().run();

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
			e.printStackTrace();
		}
	}

	public void setGenerator(IBoundsManager genny)
	{
		_genny = genny;
	}

}