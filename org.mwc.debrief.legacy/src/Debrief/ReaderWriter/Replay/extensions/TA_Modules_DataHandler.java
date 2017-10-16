package Debrief.ReaderWriter.Replay.extensions;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import MWC.GenericData.HiResDate;


public class TA_Modules_DataHandler extends TA_ForeAft_DataHandler
{

  public static class TestMe extends TestCase
  {
    //
    private List<String> _messages = new ArrayList<String>();

    public void testImportLong()
    {
      final String str =
          ";TA_MODULES: 100112 120000 \"SENSOR ALPHA\" \"TA ARRAY\" 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 14.75 3.00 14.75 10.00  12.75 3.00 14.75 10.00 12.75 3.00 14.75 10.00  12.75 3.00 14.75 10.00";
      TA_ForeAft_DataHandler ff = new TA_Modules_DataHandler()
      {
  
        @Override
        protected void storeMeasurement(String platform_name,
            String sensor_name, String folder, String dataset,
            final String units, HiResDate theDate, double measurement)
        {
          super.storeMeasurement(platform_name, sensor_name, folder, dataset,
              units, theDate, measurement);
          String outStr = "stored//" + dataset + "//value of//" + measurement + "//in//" + folder;
          _messages.add(outStr);
        }
      };
      ff.readThisLine(str);
  
      assertEquals("found items", 16, _messages.size());
      String message1 = _messages.get(0);
      testResults(message1, "Depth", "14.75", "Acoustic Modules / 1");
      String message2 = _messages.get(15);
      testResults(message2, "Heading", "10.0", "Acoustic Modules / 8");
    }
  
    public void setup()
    {
      _messages.clear();
    }

    public void testImportShort()
    {
      final String str =
          ";TA_MODULES: 100112 120000 \"SENSOR ALPHA\" \"TA ARRAY\" 12.75 3.00 14.75 10.00  12.75 3.00 14.75 10.00 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0";
      TA_ForeAft_DataHandler ff = new TA_Modules_DataHandler()
      {
  
        @Override
        protected void storeMeasurement(String platform_name,
            String sensor_name, String folder, String dataset,
            final String units, HiResDate theDate, double measurement)
        {
          super.storeMeasurement(platform_name, sensor_name, folder, dataset,
              units, theDate, measurement);
          String outStr = "stored//" + dataset + "//value of//" + measurement + "//in//" + folder;
          _messages.add(outStr);
        }
      };
      ff.readThisLine(str);
  
      assertEquals("found items", 8, _messages.size());
      
      // have a look at one
      String message = _messages.get(0);
      testResults(message, "Depth", "12.75", "Acoustic Modules / 1");
      String message2 = _messages.get(7);
      testResults(message2, "Heading", "10.0", "Acoustic Modules / 4");
    }
    
    public void testResults(final String message, final String dataset, final String measurement, final String folder)
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
  protected String nameForRow(int ctr, final boolean isLong)
  {
    return isLong ? "" + (1 + (int)((ctr-8)/2)) : "" + (1 + (ctr/2)); 
  }
}
