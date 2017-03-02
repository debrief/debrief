package Debrief.ReaderWriter.Replay.extensions;

import java.util.StringTokenizer;

import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;


public class TA_COG_ABS_DataHandler extends Core_TA_Handler 
{

  public TA_COG_ABS_DataHandler()
  {
    super("TA_COG_ABS");
  }

  @Override
  public Object readThisLine(String theLine)
  {
    // should look like:
    // ;TA_COG_ABS: 100112 120230 SENSOR TA_ARRAY 60.187961627128395 0.2243122833234192 19.02

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    final HiResDate theDate;
    final String platform_name;
    final String sensor_name;
    final double dLat;
    final double dLong;
    final double depth;

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
      dLat = Double.valueOf(st.nextToken());
      dLong = Double.valueOf(st.nextToken());
      depth = Double.valueOf(st.nextToken());

      // ok, try to store the measurement
      storeMeasurement(platform_name, sensor_name, CENTRE_OF_GRAVITY, "Lat", theDate, dLat);
      storeMeasurement(platform_name, sensor_name, CENTRE_OF_GRAVITY, "Long", theDate, dLong);
      storeMeasurement(platform_name, sensor_name, CENTRE_OF_GRAVITY, "Depth", theDate, depth);

      return null;

    }
    catch (final NumberFormatException pe)
    {
      MWC.Utilities.Errors.Trace.trace(pe, "Whilst importing measured data");
      return null;
    }
  }

}
