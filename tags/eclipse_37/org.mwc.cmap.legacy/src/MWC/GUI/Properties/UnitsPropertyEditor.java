package MWC.GUI.Properties;

import java.beans.PropertyEditorSupport;

/**
* ////////////////////////////////////
* class providing a drop-down list of units types
*/

public class UnitsPropertyEditor extends PropertyEditorSupport
{

  public static final String UNITS_PROPERTY = "RNG_UNITS";
  public static final String YDS_UNITS = "yd";
  public static final String KYD_UNITS = "kyd";
  public static final String METRES_UNITS = "m";
  public static final String KM_UNITS = "km";
  public static final String NM_UNITS = "nm";

  public static final String OLD_YDS_UNITS = "yds";
  public static final String OLD_KYD_UNITS = "kyds";


  /**
   * the user's current selection
   */
  protected String _myUnits;

  /**
   * get the list of String we provide editing for
   *
   * @return list of units types
   */
  public String[] getTags()
  {
    String tags[] = {YDS_UNITS, KYD_UNITS, NM_UNITS, KM_UNITS, METRES_UNITS};
    return tags;
  }

  public Object getValue()
  {
    return _myUnits;
  }

  public void setValue(Object p1)
  {
    if(p1 instanceof String)
    {
      String val = (String) p1;
      setAsText(val);
    }
  }

  public void setAsText(String val)
  {
    _myUnits = val;
  }

  public String getAsText()
  {
    return _myUnits;
  }
}
