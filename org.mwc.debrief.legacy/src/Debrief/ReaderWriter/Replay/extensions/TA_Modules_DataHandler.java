package Debrief.ReaderWriter.Replay.extensions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import MWC.GenericData.HiResDate;

public class TA_Modules_DataHandler extends TA_ForeAft_DataHandler
{

  public static class TestMe extends TestCase
  {
    //
    private final List<String> _messages = new ArrayList<String>();

    public void setup()
    {
      _messages.clear();
    }

    public void testImportLong() throws ParseException
    {
      final String str =
          ";TA_MODULES: 100112 120000 \"SENSOR ALPHA\" \"TA ARRAY\" 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 14.75 3.00 14.75 10.00  12.75 3.00 14.75 10.00 12.75 3.00 14.75 10.00  12.75 3.00 14.75 10.00";
      final TA_ForeAft_DataHandler ff = new TA_Modules_DataHandler()
      {

        @Override
        protected void storeMeasurement(final String platform_name,
            final String sensor_name, final String folder,
            final String dataset, final String units, final HiResDate theDate,
            final double measurement)
        {
          super.storeMeasurement(platform_name, sensor_name, folder, dataset,
              units, theDate, measurement);
          final String outStr =
              "stored//" + dataset + "//value of//" + measurement + "//in//"
                  + folder;
          _messages.add(outStr);
        }
      };
      ff.readThisLine(str);

      assertEquals("found items", 16, _messages.size());
      final String message1 = _messages.get(0);
      testResults(message1, "Depth", "14.75", "Acoustic Modules / 1");
      final String message2 = _messages.get(15);
      testResults(message2, "Heading", "10.0", "Acoustic Modules / 8");
    }

    public void testImportShort() throws ParseException
    {
      final String str =
          ";TA_MODULES: 100112 120000 \"SENSOR ALPHA\" \"TA ARRAY\" 12.75 3.00 14.75 10.00  12.75 3.00 14.75 10.00 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0";
      final TA_ForeAft_DataHandler ff = new TA_Modules_DataHandler()
      {

        @Override
        protected void storeMeasurement(final String platform_name,
            final String sensor_name, final String folder,
            final String dataset, final String units, final HiResDate theDate,
            final double measurement)
        {
          super.storeMeasurement(platform_name, sensor_name, folder, dataset,
              units, theDate, measurement);
          final String outStr =
              "stored//" + dataset + "//value of//" + measurement + "//in//"
                  + folder;
          _messages.add(outStr);
        }
      };
      ff.readThisLine(str);

      assertEquals("found items", 8, _messages.size());

      // have a look at one
      final String message = _messages.get(0);
      testResults(message, "Depth", "12.75", "Acoustic Modules / 1");
      final String message2 = _messages.get(7);
      testResults(message2, "Heading", "10.0", "Acoustic Modules / 4");
    }

    public void testResults(final String message, final String dataset,
        final String measurement, final String folder)
    {
      final String items[] = message.split("//");
      assertEquals(items[1], dataset);
      assertEquals(items[3], measurement);
      assertEquals(items[5], folder);

    }
  }

  public TA_Modules_DataHandler()
  {
    super("TA_MODULES", "Acoustic Modules");
  }

  @Override
  protected boolean checkIfLong(final String[] tokens)
  {
    boolean res = true;
    for (int i = 0; i < 8; i++)
    {
      final String nextT = tokens[i];
      if (isNull(nextT))
      {
        res = false;
        break;
      }
      else
      {
        final double val = Double.valueOf(tokens[i]);
        if (val != 0d)
        {
          res = false;
          break;
        }
      }
    }
    return res;
  }

  @Override
  protected String nameForRow(final int ctr, final boolean isLong)
  {
    return isLong ? "" + (1 + (ctr - 8) / 2) : "" + (1 + (ctr / 2));
  }
}
