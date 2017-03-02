package Debrief.ReaderWriter.Replay.extensions;

import java.util.StringTokenizer;

import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public class TA_COG_REL_DataHandler extends Core_TA_Handler
{

  public TA_COG_REL_DataHandler()
  {
    super("TA_COG_REL");
  }

  @Override
  public Object readThisLine(String theLine)
  {
    // should look like:
    // ;TA_COG_REL: 100112 134500 SENSOR TA_ARRAY 1143.83 617.55 82.23885109681223

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    @SuppressWarnings("unused")
    final HiResDate theDate;
    @SuppressWarnings("unused")
    final String platform_name;
    @SuppressWarnings("unused")
    final String sensor_name;
    @SuppressWarnings("unused")
    final double x;
    @SuppressWarnings("unused")
    final double y;
    @SuppressWarnings("unused")
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

      x = Double.valueOf(st.nextToken());
      y = Double.valueOf(st.nextToken());
      depth = Double.valueOf(st.nextToken());

      // ok, try to store the measurement

      return null;

    }
    catch (final NumberFormatException pe)
    {
      MWC.Utilities.Errors.Trace.trace(pe, "Whilst importing measured data");
      return null;
    }
  }

  @Override
  public String getSymbology()
  {
    return null;
  }
}
