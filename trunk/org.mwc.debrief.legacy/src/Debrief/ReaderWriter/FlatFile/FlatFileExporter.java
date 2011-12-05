package Debrief.ReaderWriter.FlatFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * exporter class to replicate old Strand export format
 * 
 * @author ianmayo
 * 
 */
public class FlatFileExporter
{

	// ////////////////////////////////////////////////////////////////
	// TEST THIS CLASS
	// ////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{

		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}

		private void dumpToFile(final String str, final String filename)
		{
			final File outFile = new File(filename);
			FileWriter out = null;
			try
			{
				out = new FileWriter(outFile);
				out.write(str);
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					out.close();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		private String getTestData()
		{
			String res = null;
			try
			{
				res = readFileAsString("src/Debrief/ReaderWriter/FlatFile/fakedata.txt");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			return res;
		}

		private String readFileAsString(final String filePath)
				throws java.io.IOException
		{
			final StringBuffer fileData = new StringBuffer(1000);
			final BufferedReader reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1)
			{
				final String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();
			return fileData.toString();
		}

		public void testAgainstSample() throws IOException
		{
			final String TARGET_STR = getTestData();
			assertNotNull("test data found", TARGET_STR);
			final FlatFileExporter fa = new FlatFileExporter();
			String res = null;
			try
			{
				res = fa.testExport("V1.0");
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
			assertNotNull("produced string", res);
			assertEquals("correct string", TARGET_STR, res);

			dumpToFile(res, "src/Debrief/ReaderWriter/FlatFile/data_out.txt");

		}

		public void testDateFormat() throws ParseException
		{
			final Date theDate = dateFrom("01:45:00	22/12/2002");
			final String val = formatThis(theDate);
			assertEquals("correct start date", "01:45:00	22/12/2002", val);
		}

		public void testExport()
		{

		}
	}

	/**
	 * header line
	 * 
	 */
	private static final String HEADER_LINE = "Time	OS_Status	OS_X	OS_Y	OS_Speed	OS_Heading	Sensor_Status	Sensor_X	Sensor_Y	Sensor_Brg	Sensor_Bacc	Sensor_Freq	Sensor_Facc	Sensor_Speed	Sensor_Heading	Sensor_Type	Msd_Status	Msd_X	Msd_Y	Msd_Speed	Msd_Heading	Prd_Status	Prd_X	Prd_Y	Prd_Brg	Prd_Brg_Acc	Prd_Range	Prd_Range_Acc	Prd_Course	Prd_Cacc	Prd_Speed	Prd_Sacc	Prd_Freq	Prd_Freq_Acc";

	/**
	 * Count the number of instances of substring within a string.
	 * 
	 * @param string
	 *          String to look for substring in.
	 * @param substring
	 *          Sub-string to look for.
	 * @return Count of substrings in string.
	 */
	private static int count(final String string, final String substring)
	{
		int count = 0;
		int idx = 0;

		while ((idx = string.indexOf(substring, idx)) != -1)
		{
			idx++;
			count++;
		}

		return count;
	}

	/**
	 * extract a date from the supplied string, expecting date in the following
	 * format: HH:mm:ss dd/MM/yyyy
	 * 
	 * @param dateStr
	 *          date to convert
	 * @return string as java date
	 * @throws ParseException
	 *           if the string doesn't match
	 */
	private static Date dateFrom(final String dateStr) throws ParseException
	{
		final DateFormat df = new SimpleDateFormat("HH:mm:ss	dd/MM/yyyy");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date res = null;
		res = df.parse(dateStr);
		return res;
	}

	/**
	 * format this date in the prescribed format
	 * 
	 * @param val
	 *          the date to format
	 * @return the formatted date
	 */
	static protected String formatThis(final Date val)
	{
		final DateFormat df = new SimpleDateFormat("HH:mm:ss	dd/MM/yyyy");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(val);
	}

	private static FixWrapper getFixAt(final TrackWrapper primaryTrack,
			final HiResDate thisDTG)
	{
		FixWrapper priFix = null;
		final Watchable[] priMatches = primaryTrack.getNearestTo(thisDTG);
		if (priMatches.length > 0)
		{
			priFix = (FixWrapper) priMatches[0];
		}
		return priFix;
	}

	/**
	 * get the first visible sensor
	 * 
	 * @param pTrack
	 *          the track to search for sensors
	 * @return
	 */
	public static SensorWrapper getSubjectSensor(final TrackWrapper pTrack)
	{
		final Vector<SensorWrapper> mySensors = new Vector<SensorWrapper>(); // the
																																					// final
		// solution

		// loop through collecting cuts from visible sensors
		final Enumeration<Editable> sensors = pTrack.getSensors().elements();
		while (sensors.hasMoreElements())
		{
			final SensorWrapper thisS = (SensorWrapper) sensors.nextElement();
			if (thisS.getVisible())
			{
				mySensors.add(thisS);
			}
		}

		SensorWrapper mySensor = null;
		if (mySensors.size() >= 1)
			mySensor = mySensors.firstElement();

		return mySensor;
	}

	/*
	 * line break
	 */
	private final String BRK = "" + (char) 13 + (char) 10;

	/**
	 * convenience object to store tab
	 * 
	 */
	final String tab = "\t";

	/**
	 * produce a tab-separated line of data
	 * 
	 * @return
	 */
	private String collateLine(final int secs, final int osStat,
			final double osX_yds, final double osY_yds, final double spdKts,
			final double headDegs, final int sensorStat, final double sensorX_yds,
			final double sensorY_yds, final double sensorBrg,
			final double sensorBacc, final double sensorFreq,
			final double sensorFacc, final double sensorSpdKts,
			final double sensorHdg, final String sensorType, final int msdStat,
			final double msdX_yds, final double msdY_yds, final double msdSpdKts,
			final double msdHdg, final int prdStat, final double prdX_yds,
			final double prdY_yds, final double prdBrg, final double prdBrgAcc,
			final int prdRangeYds, final int prdRangeAcc, final double prdCourse,
			final double prdCourseAcc, final double prdSpdKts,
			final double prdSpdAcc, final double prdFreq, final double prdFreqAcc)
	{

		final NumberFormat dp2 = new DecimalFormat("0.00");
		final NumberFormat dp1 = new DecimalFormat("0.0");
		final NumberFormat dp0 = new DecimalFormat("0");

		String res = null;
		res = secs + tab + osStat + tab + dp2.format(osX_yds) + tab
				+ dp2.format(osY_yds) + tab + dp2.format(spdKts) + tab
				+ dp2.format(headDegs) + tab + sensorStat + tab
				+ dp0.format(sensorX_yds) + tab + dp0.format(sensorY_yds) + tab
				+ dp1.format(sensorBrg) + tab + sensorBacc + tab + sensorFreq + tab
				+ sensorFacc + tab + dp1.format(sensorSpdKts) + tab
				+ dp1.format(sensorHdg) + tab + sensorType + tab + msdStat + tab
				+ dp1.format(msdX_yds) + tab + dp1.format(msdY_yds) + tab
				+ dp1.format(msdSpdKts) + tab + dp2.format(msdHdg) + tab + prdStat
				+ tab + prdX_yds + tab + prdY_yds + tab + prdBrg + tab + prdBrgAcc
				+ tab + prdRangeYds + tab + prdRangeAcc + tab + prdCourse + tab
				+ prdCourseAcc + tab + prdSpdKts + tab + prdSpdAcc + tab + prdFreq
				+ tab + prdFreqAcc;

		return res;
	}

	/**
	 * append indicated number of tabs
	 * 
	 * @param num
	 *          how many tabs to create
	 * @return the series of tabs
	 */
	private String createTabs(final int num)
	{
		final StringBuffer res = new StringBuffer();
		for (int i = 0; i < num; i++)
		{
			res.append("\t");
		}
		return res.toString();
	}

	/**
	 * export the dataset to a string
	 * 
	 * @param primaryTrack
	 *          the ownship track
	 * @param secondaryTracks
	 *          sec tracks = presumed to be just one
	 * @param period
	 *          the time period to export
	 * @param sensor1Type
	 *          what type was specified for sensor 2
	 * @param sensor2Type
	 *          what type was specified for sensor 2 
	 * @param fileVersion 
	 * 					which SAM version to use
	 * @param protMarking
	 * 					the protective marking on the data
	 * @return
	 */
	public String export(final WatchableList primaryTrack,
			final WatchableList[] secondaryTracks, final TimePeriod period,
			final String sensor1Type, String sensor2Type, String fileVersion, String protMarking)
	{
		String res = null;

		final TrackWrapper pTrack = (TrackWrapper) primaryTrack;

		// find the names of visible sensors
		String sensorName = null;
		final Enumeration<Editable> sensors = pTrack.getSensors().elements();
		while (sensors.hasMoreElements())
		{
			final SensorWrapper sw = (SensorWrapper) sensors.nextElement();
			if (sw.getVisible())
			{
				if (sensorName == null)
					sensorName = sw.getName();
				else
					sensorName += "_" + sw.getName();
			}
		}

		// and the secondary track
		final TrackWrapper secTrack = (TrackWrapper) secondaryTracks[0];

		// now the body bits
		final String body = this.getBody(pTrack, secTrack, period, sensor1Type, fileVersion);

		// count how many items we found
		final int numRows = count(body, BRK);

		// start off with the header bits
		final String header = this.getHeader(primaryTrack.getName(), primaryTrack
				.getName(), sensorName, secTrack.getName(), period.getStartDTG()
				.getDate(), period.getEndDTG().getDate(), numRows, 0, 0, fileVersion);

		// and collate it
		res = header + body;

		return res;
	}

	/**
	 * produce a body listing from the supplied data
	 * 
	 * @param primaryTrack
	 * @param secTrack
	 * @param period
	 * @param sensorType
	 * @param fileVersion : which SAM file to support
	 * @return
	 */
	private String getBody(final TrackWrapper primaryTrack,
			final TrackWrapper secTrack, final TimePeriod period,
			final String sensorType, String fileVersion)
	{
		final StringBuffer buffer = new StringBuffer();

		// right, we're going to loop through the two tracks producing positions
		// at all the specified times

		// remember the primary interpolation
		final boolean primaryInterp = primaryTrack.getInterpolatePoints();
		final boolean secInterp = secTrack.getInterpolatePoints();

		// switch in the interpolation
		primaryTrack.setInterpolatePoints(true);
		secTrack.setInterpolatePoints(true);

		WorldLocation origin = null;

		// sort out the sensor
		final SensorWrapper sensor = getSubjectSensor(primaryTrack);

		for (long dtg = period.getStartDTG().getDate().getTime(); dtg < period
				.getEndDTG().getDate().getTime(); dtg += 1000)
		{			
			FixWrapper priFix = null, secFix = null;
			WorldLocation sensorLoc = null;

			// create a time
			final HiResDate thisDTG = new HiResDate(dtg);

			// first the primary track
			priFix = getFixAt(primaryTrack, thisDTG);

			// right, we only do this if we have primary data - skip forward a second
			// if we're missing this pos
			if (priFix == null)
				continue;

			secFix = getFixAt(secTrack, thisDTG);

			// right, we only do this if we have secondary data - skip forward a
			// second if we're missing this pos
			if (secFix == null)
				continue;

			sensorLoc = primaryTrack.getBacktraceTo(thisDTG,
					sensor.getSensorOffset(), sensor.getWormInHole());

			// see if we have a sensor cut at the right time
			final SensorContactWrapper theCut = nearestCutTo(sensor, thisDTG);

			if (origin == null)
				origin = priFix.getLocation();

			// now sort out the spatial components
			final WorldVector priVector = new WorldVector(priFix.getLocation()
					.subtract(origin));
			final WorldVector secVector = new WorldVector(secFix.getLocation()
					.subtract(origin));
			final WorldVector senVector = new WorldVector(sensorLoc.subtract(origin));

			final double priRange = MWC.Algorithms.Conversions.Degs2Yds(priVector
					.getRange());
			final double secRange = MWC.Algorithms.Conversions.Degs2Yds(secVector
					.getRange());
			final double senRange = MWC.Algorithms.Conversions.Degs2Yds(senVector
					.getRange());

			final double priX = (Math.sin(priVector.getBearing()) * priRange);
			final double priY = Math.cos(priVector.getBearing()) * priRange;
			final double secX = (Math.sin(secVector.getBearing()) * secRange);
			final double secY = (Math.cos(secVector.getBearing()) * secRange);
			final double senX = (Math.sin(senVector.getBearing()) * senRange);
			final double senY = (Math.cos(senVector.getBearing()) * senRange);

			// do the calc as long, in case it's massive...
			final long longSecs = (thisDTG.getMicros() - period.getStartDTG()
					.getMicros()) / 1000000;
			final int secs = (int) longSecs;

			// and the freq
			double senFreq = -999.9;
			if ((theCut != null) && (theCut.getHasFrequency()))
				senFreq = theCut.getFrequency();

			final int osStat = 7;
			int senStat;
			if (theCut == null)
				senStat = 0;
			else if (theCut.getHasFrequency())
				senStat = 63;
			else
				senStat = 59;
			double theBearing = -999;
			double senSpd = -999.9;
			double senHeading = -999.9;
			if (theCut != null)
			{
				theBearing = theCut.getBearing();
				senSpd = priFix.getSpeed();
				senHeading = priFix.getCourseDegs();

			}

			final int msdStat = 1 + 2 + 4;
			final int prdStat = 0;

			final double PRD_FREQ_ACC = -999.9;

			// Time OS_Status OS_X OS_Y OS_Speed OS_Heading Sensor_Status Sensor_X
			// Sensor_Y Sensor_Brg Sensor_Bacc Sensor_Freq Sensor_Facc Sensor_Speed
			// Sensor_Heading Sensor_Type Msd_Status Msd_X Msd_Y Msd_Speed
			// Msd_Heading
			// Prd_Status Prd_X Prd_Y Prd_Brg Prd_Brg_Acc Prd_Range Prd_Range_Acc
			// Prd_Course Prd_Cacc Prd_Speed Prd_Sacc Prd_Freq Prd_Freq_Acc";

			final double msdXyds = secX;
			final double msdYyds = secY;
			final double msdSpdKts = secFix.getSpeed();
			final double msdCourseDegs = secFix.getCourseDegs();

			final double prdFreq = -999.9;
			final double prdSpdAcc = -999.9;
			final double prdSpdKts = -999.9;
			final double prdCourseAcc = -999.9;
			final double prdCourse = -999.9;
			final int prdRangeAcc = -999;
			final int prdRangeYds = -999;
			final double prdBrgAcc = -999.9;
			final double prdBrg = -999.9;
			final double prdYYds = -999.9;
			final double prdXYds = -999.9;
			final double sensorFacc = -999.9;
			final double sensorBacc = -999.9;

			final String nextLine = collateLine(secs, osStat, priX, priY,
					priFix.getSpeed(), priFix.getCourseDegs(), senStat, senX, senY,
					theBearing, sensorBacc, senFreq, sensorFacc, senSpd, senHeading,
					sensorType, msdStat, msdXyds, msdYyds, msdSpdKts, msdCourseDegs,
					prdStat, prdXYds, prdYYds, prdBrg, prdBrgAcc, prdRangeYds,
					prdRangeAcc, prdCourse, prdCourseAcc, prdSpdKts, prdSpdAcc, prdFreq,
					PRD_FREQ_ACC);

			buffer.append(nextLine);
			buffer.append(BRK);

		}

		// restore the primary track interpolation
		primaryTrack.setInterpolatePoints(primaryInterp);
		secTrack.setInterpolatePoints(secInterp);

		return buffer.toString();
	}

	/**
	 * provide a formatted header block using the supplied params
	 * 
	 * @param OWNSHIP
	 * @param OS_TRACK_NAME
	 * @param SENSOR_NAME
	 * @param TGT_NAME
	 * @param startDate
	 * @param endDate
	 * @param NUM_RECORDS
	 * @param X_ORIGIN_YDS
	 * @param Y_ORIGIN_YDS
	 * @param fileVersion which SAM format to use
	 * @return
	 */
	private String getHeader(final String OWNSHIP, final String OS_TRACK_NAME,
			final String SENSOR_NAME, final String TGT_NAME, final Date startDate,
			final Date endDate, final int NUM_RECORDS, final int X_ORIGIN_YDS,
			final int Y_ORIGIN_YDS, final String fileVersion)
	{

		final String header = "STRAND Scenario Report " + fileVersion + createTabs(33) + BRK
				+ "MISSION_NAME" + createTabs(33) + BRK + OWNSHIP + createTabs(33)
				+ BRK + OS_TRACK_NAME + createTabs(33) + BRK + SENSOR_NAME
				+ createTabs(33) + BRK + TGT_NAME + createTabs(33) + BRK + TGT_NAME
				+ createTabs(33) + BRK + formatThis(startDate) + createTabs(32) + BRK
				+ formatThis(endDate) + createTabs(32) + BRK + "0" + createTabs(33)
				+ BRK + "0" + createTabs(33) + BRK + "0" + createTabs(33) + BRK
				+ NUM_RECORDS + createTabs(33) + BRK + X_ORIGIN_YDS + "	"
				+ Y_ORIGIN_YDS + createTabs(32) + BRK + HEADER_LINE + BRK;
		return header;
	}

	/**
	 * produce a body of test data lines
	 * 
	 * @return 5 lines of test data - to match supplied sample
	 */
	private String getTestBody()
	{
		final String body = collateLine(0, 7, 6.32332, -5555.55, 2.7, 200.1, 0,
				-999, -999, -999.9, -999.9, -999.9, -999.9, -999.9, -999.9, "-999", 6,
				-999.9, -999.9, 1.1, 11.12, 0, -999.9, -999.9, -999.9, -999.9, -999,
				-999, -999.9, -999.9, -999.9, -999.9, -999.9, -999.9)
				+ BRK
				+ collateLine(1, 7, 6.32332, -5555.551, 2.7, 200, 0, -999, -999,
						-999.9, -999.9, -999.9, -999.9, -999.9, -999.9, "-999", 6, -999.9,
						-999.9, 1.1, 11.12, 0, -999.9, -999.9, -999.9, -999.9, -999, -999,
						-999.9, -999.9, -999.9, -999.9, -999.9, -999.9)
				+ BRK
				+ collateLine(2, 7, 6.32332, -5555.55, 2.7, 200, 0, -999, -999, -999.9,
						-999.9, -999.9, -999.9, -999.9, -999.9, "-999", 6, -999.9, -999.9,
						1.1, 11.12, 0, -999.9, -999.9, -999.9, -999.9, -999, -999, -999.9,
						-999.9, -999.9, -999.9, -999.9, -999.9)
				+ BRK
				+ collateLine(3, 7, 6.32332, -5521.2, 4.6, 200, 0, -999, -999, -999.9,
						-999.9, -999.9, -999.9, -999.9, -999.9, "-999", 6, -999.9, -999.9,
						1.1, 11.12, 0, -999.9, -999.9, -999.9, -999.9, -999, -999, -999.9,
						-999.9, -999.9, -999.9, -999.9, -999.9)
				+ BRK
				+ collateLine(4, 7, 6.32332, -5555.32, 4.7, 200, 0, -999, -999, -999.9,
						-999.9, -999.9, -999.9, -999.9, -999.9, "-999", 6, -999.9, -999.9,
						1.1, 11.12, 0, -999.9, -999.9, -999.9, -999.9, -999, -999, -999.9,
						-999.9, -999.9, -999.9, -999.9, -999.9)
				+ BRK
				+ collateLine(5, 7, 6.32332, -5543.73, 4.8, 200.1, 0, -999, -999,
						-999.9, -999.9, -999.9, -999.9, -999.9, -999.9, "-999", 6, -999.9,
						-999.9, 1.1, 11.12, 0, -999.9, -999.9, -999.9, -999.9, -999, -999,
						-999.9, -999.9, -999.9, -999.9, -999.9, -999.9);

		return body;
	}

	/**
	 * find the sensor cut nearest to the supplied time
	 * 
	 * @param hostTrack
	 * @param target
	 * @return
	 */
	protected SensorContactWrapper nearestCutTo(final SensorWrapper sw,
			final HiResDate target)
	{
		SensorContactWrapper res = null;
		if (sw.getStartDTG().greaterThan(target) || sw.getEndDTG().lessThan(target))
		{
			// nope, it's out of our data period
		}
		else
		{
			final Enumeration<Editable> contents = sw.elements();
			while (contents.hasMoreElements())
			{
				final SensorContactWrapper thisCut = (SensorContactWrapper) contents
						.nextElement();
				final long thisDate = thisCut.getDTG().getDate().getTime();
				final long thisOffset = Math.abs(thisDate - target.getDate().getTime());
				if (thisOffset == 0)
				{
					res = thisCut;
				}
			}
		}
		return res;
	}

	private String testExport(String fileVersion) throws ParseException
	{
		final String StartTime = "04:45:00	20/04/2009";
		final Date startDate = dateFrom(StartTime);
		final String endTime = "04:45:05	20/04/2009";
		final Date endDate = dateFrom(endTime);
		String res = getHeader("Vessel", "OS track 0100-0330",
				"GapsFatBowBTH_5-4-04", "tla", startDate, endDate, 5, -123456, -654321,fileVersion );
		res += getTestBody();
		return res;
	}

}
