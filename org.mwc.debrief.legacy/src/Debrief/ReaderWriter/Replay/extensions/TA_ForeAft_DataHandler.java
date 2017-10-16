package Debrief.ReaderWriter.Replay.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public class TA_ForeAft_DataHandler extends Core_TA_Handler
{

  final private String _datasetName;

  public TA_ForeAft_DataHandler()
  {
    this("TA_FORE_AFT", "Fore & Aft");
  }

  public TA_ForeAft_DataHandler(String title, String datasetName)
  {
    super(title);

    _datasetName = datasetName;
  }

  public static class TestMe extends TestCase
  {
    //
    private List<String> _messages = new ArrayList<String>();

    public void testImport()
    {
      final String str =
          ";TA_FORE_AFT: 100112 120000 \"SENSOR ALPHA\" \"TA ARRAY\" 12.75 3.00\t14.75 10.00";
      TA_ForeAft_DataHandler ff = new TA_ForeAft_DataHandler()
      {

        @Override
        protected void storeMeasurement(String platform_name,
            String sensor_name, String folder, String dataset,
            final String units, HiResDate theDate, double measurement)
        {
          super.storeMeasurement(platform_name, sensor_name, folder, dataset,
              units, theDate, measurement);
          String outStr = "stored:" + dataset + " value of:" + measurement;
          _messages.add(outStr);

        }

      };
      ff.readThisLine(str);

      assertEquals("found items", 4, _messages.size());

    }
  }

  @Override
  public Object readThisLine(String theLine)
  {

    // should look like:
    // ;TA_FORE_AFT: 100112 120000 SENSOR TA_ARRAY [12.75 0.00] [12.75 0.00]

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    final HiResDate theDate;
    final String platform_name;
    final String sensor_name;

    // skip the comment identifier
    st.nextToken();

    // combine the date, a space, and the time
    final String dateToken = st.nextToken();
    final String timeToken = st.nextToken();

    // and extract the date
    theDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);

    // trouble - the track name may have been quoted, in which case we will
    // pull
    // in the remaining fields aswell
    platform_name = AbstractPlainLineImporter.checkForQuotedName(st).trim();
    sensor_name = AbstractPlainLineImporter.checkForQuotedName(st).trim();

    // extract the measuremetns
    // ok, parse the rest of the line
    String remainingText = st.nextToken("").trim();

    // now split this into tab separated tokens
    String[] tokens = remainingText.split("\\s+");

    // SPECIAL CASE: for a modules entry, the line will either contain a set of values, then zeros,
    // or a set of zeroes, then values.
    // Introduce test, to check
    final boolean isLong = checkIfLong(tokens);

    int ctr = 0;
    for (final String str : tokens)
    {
      // hmm, depth or heading value?
      final boolean isDepth = (ctr % 2) == 0;

      // extract the double
      final double thisVal = Double.valueOf(str.trim());

      if (isLong && ctr > 7 || !isLong && ctr < 8)
      {

        // sort out if we're fore or aft
        final String datasetName = nameForRow(ctr, isLong);

        // ok, try to store the measurement
        if (isDepth)
        {
          storeMeasurement(platform_name, sensor_name, _datasetName + " / "
              + datasetName, "Depth", "m", theDate, thisVal);
        }
        else
        {
          storeMeasurement(platform_name, sensor_name, _datasetName + " / "
              + datasetName, "Heading", "\u00b0", theDate, thisVal);
        }
      }

      // and increment
      ctr++;
    }

    return null;

  }

  private boolean checkIfLong(String[] tokens)
  {
    boolean res = true;
    for (int i = 0; i < 8; i++)
    {
      double val = Double.valueOf(tokens[i]);
      if (val != 0d)
      {
        res = false;
        break;
      }
    }
    return res;
  }

  protected String nameForRow(int ctr, boolean isLong)
  {
    final String datasetName;

    if (ctr <= 1)
    {
      datasetName = "Fore";
    }
    else
    {
      datasetName = "Aft";
    }

    return datasetName;
  }

}
