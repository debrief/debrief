package MWC.Utilities.ReaderWriter.FlatFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FlatFileExporter
{
	private static final String HEADER_LINE = "Time	OS_Status	OS_X	OS_Y	OS_Speed	OS_Heading	Sensor_Status	Sensor_X	Sensor_Y	Sensor_Brg	Sensor_Bacc	Sensor_Freq	Sensor_Facc	Sensor_Speed	Sensor_Heading	Sensor_Type	Msd_Status	Msd_X	Msd_Y	Msd_Speed	Msd_Heading	Prd_Status	Prd_X	Prd_Y	Prd_Brg	Prd_Brg_Acc	Prd_Range	Prd_Range_Acc	Prd_Course	Prd_Cacc	Prd_Speed	Prd_Sacc	Prd_Freq	Prd_Freq_Acc";

	private String createTabs(int num)
	{
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < num; i++)
		{
			res.append("\t");
		}
		return res.toString();
	}

	private final String BRK = "" + (char) 13 + (char) 10;

	static protected String formatThis(Date val)
	{
		DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(val);
	}

	public String testExport()
	{
		final String StartTime = "04:45:00	20/04/2009";
		final String endTime = "04:45:05	20/04/2009";
		return exportThis("Vessel", "OS track 0100-0330", "GapsFatBowBTH_5-4-04",
				"tla", StartTime, endTime, "5", "-1.23E+04", "-654321");
	}

	public String exportThis(final String OWNSHIP, String OS_TRACK_NAME,
			String SENSOR_NAME, String TGT_NAME, String START_TIME, String END_TIME,
			String NUM_RECORDS, String X_ORIGIN, String Y_ORIGIN)
	{

		String header = "STRAND Scenario Report 1.00"
				+ createTabs(33)
				+ BRK
				+ "MISSION_NAME"
				+ createTabs(33)
				+ BRK
				+ OWNSHIP
				+ createTabs(33)
				+ BRK
				+ OS_TRACK_NAME
				+ createTabs(33)
				+ BRK
				+ SENSOR_NAME
				+ createTabs(33)
				+ BRK
				+ TGT_NAME
				+ createTabs(33)
				+ BRK
				+ TGT_NAME
				+ createTabs(33)
				+ BRK
				+ START_TIME
				+ createTabs(32)
				+ BRK
				+ END_TIME
				+ createTabs(32)
				+ BRK
				+ "0"
				+ createTabs(33)
				+ BRK
				+ "0"
				+ createTabs(33)
				+ BRK
				+ "0"
				+ createTabs(33)
				+ BRK
				+ NUM_RECORDS
				+ createTabs(33)
				+ BRK
				+ X_ORIGIN
				+ "	"
				+ Y_ORIGIN
				+ createTabs(32)
				+ BRK
				+ HEADER_LINE
				+ BRK;
		;
		String body = collateLine(0, 7, 6.32332, -5555.55, 2.7, 200.1, 0, -999, -999, -999.9, -999.9, -999.9, -999.9, -999.9, -999.9) + BRK
		+  collateLine(1, 7, 6.32332,-5555.551 , 2.7, 200, 0, -999, -999, -999.9, -999.9, -999.9, -999.9, -999.9, -999.9) + BRK
		+  collateLine(2, 7, 6.32332, -5555.55, 2.7, 200, 0, -999, -999, -999.9, -999.9, -999.9, -999.9, -999.9, -999.9) + BRK
		+  collateLine(3, 7, 6.32332, -5521.2, 4.6, 200, 0, -999, -999, -999.9, -999.9, -999.9, -999.9, -999.9, -999.9) + BRK
		+  collateLine(4, 7, 6.32332, -5555.32, 4.7, 200, 0, -999, -999, -999.9, -999.9, -999.9, -999.9, -999.9, -999.9) + BRK
		+  collateLine(5, 7, 6.32332, -5543.73, 4.8, 200.1, 0, -999, -999, -999.9, -999.9, -999.9, -999.9, -999.9, -999.9);
		
		return header + body;
	}

	private String collateLine(int secs, int osStat, double osX, double osY, double spdKts, double headDegs, int sensorStat, int sensorX, int sensorY,
			double sensorBrg, double sensorBacc, double sensorFreq, double sensorFacc, double sensorSpd, double sensorHdg)
	{
		final String tab = "\t";
		String res = null;
		switch(secs)
		{
		case 0:
			res = secs + tab + osStat + tab + osX + tab + osY + tab + spdKts + tab + headDegs + tab + 
					sensorStat + tab + sensorX + tab + sensorY + tab + sensorBrg + tab + sensorBacc + tab + 
					sensorFreq + tab + sensorFacc + tab + sensorBrg + tab + sensorBacc + tab + 
					"-999	6	-999.9	-999.9	1.1	11.12	0	-999.9	-999.9	-999.9	-999.9	-999	-999	-999.9	-999.9	-999.9	-999.9	-999.9	-999.9";
			break;
		case 1:
			res = secs + "	7	6.32332	-5555.551	2.7	200	0	-999	-999	-999.9	-999.9	-999.9	-999.9	-999.9	-999.9	-999	6	-999.9	-999.9	1.1	11.12	0	-999.9	-999.9	-999.9	-999.9	-999	-999	-999.9	-999.9	-999.9	-999.9	-999.9	-999.9";
			break;
		case 2:
			res = secs + "	7	6.32332	-5555.55	2.7	200	0	-999	-999	-999.9	-999.9	-999.9	-999.9	-999.9	-999.9	-999	6	-999.9	-999.9	1.1	11.12	0	-999.9	-999.9	-999.9	-999.9	-999	-999	-999.9	-999.9	-999.9	-999.9	-999.9	-999.9";
			break;
		case 3:
			res = secs + "	7	6.32332	-5521.2	4.6	200	0	-999	-999	-999.9	-999.9	-999.9	-999.9	-999.9	-999.9	-999	6	-999.9	-999.9	1.1	11.12	0	-999.9	-999.9	-999.9	-999.9	-999	-999	-999.9	-999.9	-999.9	-999.9	-999.9	-999.9";
			break;
		case 4:
			res = secs + "	7	6.32332	-5555.32	4.7	200	0	-999	-999	-999.9	-999.9	-999.9	-999.9	-999.9	-999.9	-999	6	-999.9	-999.9	1.1	11.12	0	-999.9	-999.9	-999.9	-999.9	-999	-999	-999.9	-999.9	-999.9	-999.9	-999.9	-999.9";
			break;
		case 5: 
			res = secs + "	7	6.32332	-5543.73	4.8	200.1	0	-999	-999	-999.9	-999.9	-999.9	-999.9	-999.9	-999.9	-999	6	-999.9	-999.9	1.1	11.12	0	-999.9	-999.9	-999.9	-999.9	-999	-999	-999.9	-999.9	-999.9	-999.9	-999.9	-999.9";
			break;
		}

		return res;
	}

	static public final class testMe extends junit.framework.TestCase
	{

		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
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

		private String getTestData()
		{
			String res = null;
			try
			{
				res = readFileAsString("src/MWC/Utilities/ReaderWriter/FlatFile/fakedata.txt");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return res;
		}

		public void testExport() throws IOException
		{
			final String TARGET_STR = getTestData();
			assertNotNull("test data found", TARGET_STR);
			assertEquals("has data", 2157, TARGET_STR.length());

			FlatFileExporter fa = new FlatFileExporter();
			String res = fa.testExport();
			assertEquals("correct string", TARGET_STR, res);

		}

		public void testDateFormat() throws ParseException
		{
			DateFormat df = new SimpleDateFormat("HH:mm:ss	dd/MM/yyyy");
			df.setTimeZone(TimeZone.getTimeZone("GMT"));

			System.err.println(df.format(new Date()));

			Date theDate = df.parse("01:45:00	22/12/2002");

			// Date theDate = new Date(2002,12,22,1,45,00);
			String val = df.format(theDate);
			assertEquals("correct start date", "01:45:00	22/12/2002", val);
		}
	}

}
