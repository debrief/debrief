/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
    final String tags[] = {YDS_UNITS, KYD_UNITS, NM_UNITS, KM_UNITS, METRES_UNITS};
    return tags;
  }

  public Object getValue()
  {
    return _myUnits;
  }

  public void setValue(final Object p1)
  {
    if(p1 instanceof String)
    {
      final String val = (String) p1;
      setAsText(val);
    }
  }

  public void setAsText(final String val)
  {
    _myUnits = val;
  }

  public String getAsText()
  {
    return _myUnits;
  }
}
