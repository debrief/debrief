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
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TimeZone;

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
	/**
	 * header line
	 * 
	 */
	private static final String HEADER_LINE = "Time	OS_Status	OS_X	OS_Y	OS_Speed	OS_Heading	Sensor_Status	Sensor_X	Sensor_Y	Sensor_Brg	Sensor_Bacc	Sensor_Freq	Sensor_Facc	Sensor_Speed	Sensor_Heading	Sensor_Type	Msd_Status	Msd_X	Msd_Y	Msd_Speed	Msd_Heading	Prd_Status	Prd_X	Prd_Y	Prd_Brg	Prd_Brg_Acc	Prd_Range	Prd_Range_Acc	Prd_Course	Prd_Cacc	Prd_Speed	Prd_Sacc	Prd_Freq	Prd_Freq_Acc";

	/*
	 * line break
	 */
	private final String BRK = "" + (char) 13 + (char) 10;

	/**
	 * convenience object to store tab
	 * 
	 */
	final String tab = "\t";

	public String export(final WatchableList primaryTrack,
			final WatchableList[] secondaryTracks, final TimePeriod period,
			final String sensorType)
	{
		String res = null;

		TrackWrapper pTrack = (TrackWrapper) primaryTrack;

		// get the sensor
		Enumeration<SensorWrapper> sensors = pTrack.getSensors();
		SensorWrapper mySensor = null;
		while (sensors.hasMoreElements())
		{
			SensorWrapper thisS = sensors.nextElement();
			if (thisS.getVisible())
			{
				mySensor = thisS;
				break;
			}
		}

		if (mySensor == null)
			return null;

		// and the secondary track
		TrackWrapper secTrack = (TrackWrapper) secondaryTracks[0];

		// sort the cuts out
		Collection<Editable> theCuts = mySensor.getItemsBetween(period
				.getStartDTG(), period.getEndDTG());

		// start off with the header bits
		String header = this.getHeader(primaryTrack.getName(), primaryTrack
				.getName(), mySensor.getName(), secTrack.getName(), period
				.getStartDTG().getDate(), period.getEndDTG().getDate(), theCuts.size(),
				0, 0);

		// now the body bits
		String body = this.getBody(pTrack, theCuts, secTrack, period, sensorType);

		// and collate it
		res = header + body;

		return res;
	}

	/**
	 * produce a body listing from the supplied data
	 * 
	 * @param primaryTrack
	 * @param theCuts
	 * @param secTrack
	 * @param period
	 * @return
	 */
	private String getBody(final TrackWrapper primaryTrack,
			final Collection<Editable> theCuts, final TrackWrapper secTrack,
			final TimePeriod period, final String sensorType)
	{
		StringBuffer buffer = new StringBuffer();

		boolean primaryInterp = primaryTrack.getInterpolatePoints();

		WorldLocation origin = null;

		// ok, better start stepping - we're going to step at each sensor cut
		Iterator<Editable> cutLoop = theCuts.iterator();
		while (cutLoop.hasNext())
		{
			SensorContactWrapper scw = (SensorContactWrapper) cutLoop.next();
			// get the host fix
			final HiResDate thisDTG = scw.getDTG();
			Watchable[] priMatches = primaryTrack.getNearestTo(thisDTG);
			Watchable[] secMatches = secTrack.getNearestTo(thisDTG);

			if ((priMatches == null) || (priMatches.length == 0))
				throw new RuntimeException("No matching primary fixes for" + thisDTG);

			if ((secMatches == null) || (secMatches.length == 0))
				throw new RuntimeException("No matching secondary fixes for" + thisDTG);

			FixWrapper priF = (FixWrapper) priMatches[0];
			FixWrapper secF = (FixWrapper) secMatches[0];

			if (origin == null)
				origin = priF.getLocation();

			// now sort out the spatial components
			WorldVector priVector = new WorldVector(priF.getLocation().subtract(
					origin));
			WorldVector secVector = new WorldVector(secF.getLocation().subtract(
					origin));
			WorldVector senVector = new WorldVector(scw.getLocation()
					.subtract(origin));

			double priRange = MWC.Algorithms.Conversions.Degs2Yds(priVector
					.getRange());
			double secRange = MWC.Algorithms.Conversions.Degs2Yds(secVector
					.getRange());
			double senRange = MWC.Algorithms.Conversions.Degs2Yds(senVector
					.getRange());

			double priX = (Math.sin(priVector.getBearing()) * priRange);
			double secX = (Math.sin(secVector.getBearing()) * secRange);
			double senX = (Math.sin(senVector.getBearing()) * senRange);
			double priY = Math.cos(priVector.getBearing()) * priRange;
			double secY = (Math.cos(secVector.getBearing()) * secRange);
			double senY = (Math.cos(senVector.getBearing()) * senRange);

			// do the calc as long, in case it's massive...
			long longSecs = (thisDTG.getMicros() - period.getStartDTG().getMicros()) / 1000000;
			int secs = (int) longSecs;

			// and the freq
			double senFreq = -999.9;
			if (scw.getHasFrequency())
				senFreq = scw.getFrequency();

			int osStat = 7;
			int senStat;
			if (scw.getHasFrequency())
				senStat = 63;
			else
				senStat = 59;

			int msdStat = 7;
			int prdStat = 0;

			String nextLine = collateLine(secs, osStat, priX, priY, priF.getSpeed(),
					priF.getCourseDegs(), senStat, senX, senY, scw.getBearing(), -999.9,
					senFreq, -999.9, priF.getSpeed(), priF.getCourseDegs(), sensorType,
					msdStat, secX, secY, secF.getSpeed(), secF.getCourseDegs(), prdStat,
					-999.9, -999.9, -999.9, -999.9, -999, -999, -999.9,
					-999.9, -999.9, -999.9, -999.9, -999.9);

			buffer.append(nextLine);
			buffer.append(BRK);

		}

		// restore the primary track interpolation
		primaryTrack.setInterpolatePoints(primaryInterp);

		return buffer.toString();
	}

	/**
	 * append indicated number of tabs
	 * 
	 * @param num
	 *          how many tabs to create
	 * @return the series of tabs
	 */
	private String createTabs(int num)
	{
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < num; i++)
		{
			res.append("\t");
		}
		return res.toString();
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
	public static Date dateFrom(String dateStr) throws ParseException
	{
		DateFormat df = new SimpleDateFormat("HH:mm:ss	dd/MM/yyyy");
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
	static protected String formatThis(Date val)
	{
		DateFormat df = new SimpleDateFormat("HH:mm:ss	dd/MM/yyyy");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(val);
	}

	public String testExport() throws ParseException
	{
		final String StartTime = "04:45:00	20/04/2009";
		final Date startDate = dateFrom(StartTime);
		final String endTime = "04:45:05	20/04/2009";
		final Date endDate = dateFrom(endTime);
		String res = getHeader("Vessel", "OS track 0100-0330",
				"GapsFatBowBTH_5-4-04", "tla", startDate, endDate, 5, -123456, -654321);
		res += getTestBody();
		return res;
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
	 * @return
	 */
	public String getHeader(final String OWNSHIP, String OS_TRACK_NAME,
			String SENSOR_NAME, String TGT_NAME, Date startDate, Date endDate,
			int NUM_RECORDS, int X_ORIGIN_YDS, int Y_ORIGIN_YDS)
	{

		String header = "STRAND Scenario Report 1.00" + createTabs(33) + BRK
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
	public String getTestBody()
	{
		String body = collateLine(0, 7, 6.32332, -5555.55, 2.7, 200.1, 0, -999,
				-999, -999.9, -999.9, -999.9, -999.9, -999.9, -999.9, "-999", 6,
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
	 * produce a tab-separated line of data
	 * 
	 * @return
	 */
	private String collateLine(int secs, int osStat, double osX_yds,
			double osY_yds, double spdKts, double headDegs, int sensorStat,
			double sensorX_yds, double sensorY_yds, double sensorBrg,
			double sensorBacc, double sensorFreq, double sensorFacc,
			double sensorSpdKts, double sensorHdg, String sensorType, int msdStat,
			double msdX_yds, double msdY_yds, double msdSpdKts, double msdHdg,
			int prdStat, double prdX_yds, double prdY_yds, double prdBrg,
			double prdBrgAcc, int prdRangeYds, int prdRangeAcc, double prdCourse,
			double prdCourseAcc, double prdSpdKts, double prdSpdAcc, double prdFreq,
			double prdFreqAcc)
	{

		NumberFormat nf = new DecimalFormat("0.00");

		String res = null;
		res = secs + tab + osStat + tab + nf.format(osX_yds) + tab
				+ nf.format(osY_yds) + tab + nf.format(spdKts) + tab
				+ nf.format(headDegs) + tab + sensorStat + tab + nf.format(sensorX_yds)
				+ tab + nf.format(sensorY_yds) + tab + nf.format(sensorBrg) + tab
				+ sensorBacc + tab + sensorFreq + tab + sensorFacc + tab
				+ nf.format(sensorSpdKts) + tab + nf.format(sensorHdg) + tab
				+ sensorType + tab + msdStat + tab + nf.format(msdX_yds) + tab
				+ nf.format(msdY_yds) + tab + nf.format(msdSpdKts) + tab
				+ nf.format(msdHdg) + tab + prdStat + tab + prdX_yds + tab + prdY_yds
				+ tab + prdBrg + tab + prdBrgAcc + tab + prdRangeYds + tab
				+ prdRangeAcc + tab + prdCourse + tab + prdCourseAcc + tab + prdSpdKts
				+ tab + prdSpdAcc + tab + prdFreq + tab + prdFreqAcc;

		return res;
	}

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

		public void testExport()
		{

		}

		private String readFileAsString(String filePath) throws java.io.IOException
		{
			StringBuffer fileData = new StringBuffer(1000);
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1)
			{
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();
			return fileData.toString();
		}

		private void dumpToFile(String str, String filename)
		{
			File outFile = new File(filename);
			FileWriter out;
			try
			{
				out = new FileWriter(outFile);
				out.write(str);
				out.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		private String getTestData()
		{
			String res = null;
			try
			{
				res = readFileAsString("src/Debrief/ReaderWriter/FlatFile/fakedata.txt");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return res;
		}

		public void testAgainstSample() throws IOException
		{
			final String TARGET_STR = getTestData();
			assertNotNull("test data found", TARGET_STR);
			FlatFileExporter fa = new FlatFileExporter();
			String res = null;
			try
			{
				res = fa.testExport();
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
			assertNotNull("produced string", res);
			assertEquals("correct string", TARGET_STR, res);

			dumpToFile(res, "src/Debrief/ReaderWriter/FlatFile/data_out.txt");

		}

		public void testDateFormat() throws ParseException
		{
			Date theDate = dateFrom("01:45:00	22/12/2002");
			String val = formatThis(theDate);
			assertEquals("correct start date", "01:45:00	22/12/2002", val);
		}
	}

}
