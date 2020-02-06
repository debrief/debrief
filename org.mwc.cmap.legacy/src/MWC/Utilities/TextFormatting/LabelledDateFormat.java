
// @Author : Ian Mayo
// @Project: Debrief 3
// @File   : FormatRNDateTime.java

package MWC.Utilities.TextFormatting;

import java.text.DateFormat;

public class LabelledDateFormat 
{
  static public String toString(final long theVal)
  {
    
    final java.util.Date theTime = new java.util.Date(theVal);
    String res;
    final DateFormat df = new GMTDateFormat("dd'd HH'h mm'm ss's");
    res = df.format(theTime);

    return res;
  }

}


