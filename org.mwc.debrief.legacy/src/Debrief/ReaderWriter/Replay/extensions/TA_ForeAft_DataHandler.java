package Debrief.ReaderWriter.Replay.extensions;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public class TA_ForeAft_DataHandler extends Core_TA_Handler
{

  public TA_ForeAft_DataHandler()
  {
    super("TA_FORE_AFT");
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
    try
    {
      // ok, parse the rest of the line
      String remainingText = st.nextToken("");

      Pattern pattern = Pattern.compile("<(.*?)>");
      Matcher matcher = pattern.matcher(remainingText);

      int ctr = 1;

      while (matcher.find())
      {

        String block = matcher.group(1);
        final StringTokenizer blockT = new StringTokenizer(block);

        final double depth = Double.valueOf(blockT.nextToken());
        final double heading = Double.valueOf(blockT.nextToken());

        final String datasetName;
        if (ctr == 1)
        {
          datasetName = "Fore";
        }
        else
        {
          datasetName = "Aft";
        }

        // ok, try to store the measurement
        storeMeasurement(platform_name, sensor_name,
            "Fore & Aft" + datasetName, "Heading", theDate, heading);
        storeMeasurement(platform_name, sensor_name,
            "Fore & Aft" + datasetName, "Depth", theDate, depth);

        ctr++;
      }

      return null;

    }
    catch (final NumberFormatException pe)
    {
      MWC.Utilities.Errors.Trace.trace(pe, "Whilst importing measured data");
      return null;
    }
  }

}
