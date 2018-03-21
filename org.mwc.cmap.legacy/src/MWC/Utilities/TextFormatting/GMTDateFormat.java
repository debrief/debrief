package MWC.Utilities.TextFormatting;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/** date formatter that forces use of GMT timezone
 * 
 * @author ian
 *
 */
public class GMTDateFormat extends SimpleDateFormat
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  /** pregenerate the timezone. we don't need it every time
   * 
   */
  private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
  
  public GMTDateFormat(String format)
  {
    super(format);
    
    super.setTimeZone(GMT_ZONE);
  }

  public GMTDateFormat(String pattern, Locale us)
  {
    super(pattern, us);
    super.setTimeZone(GMT_ZONE);
  }

  @Override
  final public void setTimeZone(TimeZone arg0)
  {
    throw new IllegalArgumentException("Can't override time zone. It's fixed to GMT");
  }
}
