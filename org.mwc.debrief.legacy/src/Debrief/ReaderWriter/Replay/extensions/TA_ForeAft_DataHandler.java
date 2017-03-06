package Debrief.ReaderWriter.Replay.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
      final String str = ";TA_FORE_AFT: 100112 120000 \"SENSOR ALPHA\" \"TA ARRAY\" [12.75 3.00] [14.75 10.00]";
      TA_ForeAft_DataHandler ff = new TA_ForeAft_DataHandler(){

        @Override
        protected void storeMeasurement(String platform_name,
            String sensor_name, String folder, String dataset, final String units,
            HiResDate theDate, double measurement)
        {
          super.storeMeasurement(platform_name, sensor_name, folder, dataset, units, theDate,
              measurement);
          _messages.add("stored");
        }
        
      };
      ff.readThisLine(str);
      
      assertEquals("found items", 2, _messages.size());
      
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
    try
    {
      // ok, parse the rest of the line
      String remainingText = st.nextToken("");

      Pattern pattern = Pattern.compile("\\[(.*?)\\]");
      Matcher matcher = pattern.matcher(remainingText);

      int ctr = 1;

      while (matcher.find())
      {

        String block = matcher.group(1);
        final StringTokenizer blockT = new StringTokenizer(block);

        final double depth = Double.valueOf(blockT.nextToken());
        final double heading = Double.valueOf(blockT.nextToken());

        final String datasetName = nameForRow(ctr);
        
        // ok, try to store the measurement
        storeMeasurement(platform_name, sensor_name,
            _datasetName + " / " + datasetName, "Heading", "\u00b0", theDate, heading);
        storeMeasurement(platform_name, sensor_name,
            _datasetName + " / " + datasetName, "Depth", "m", theDate, depth);

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

  protected String nameForRow(int ctr)
  {
    final String datasetName;
    
    if (ctr == 1)
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
