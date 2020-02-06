/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package MWC.GUI.Properties;

public class NullableLocationPropertyEditor extends LocationPropertyEditor
{
  /** don't try to use a specific label location,
   * automatically calculate the best location (according
   * to the location of other properties, especially
   * auto-located fix DTG labels)
   */
  final static public int AUTO = -1;

  public String[] getTags()
  {
    final String tags[] = {"Top",
                     "Bottom",
                     "Left",
                     "Right",
                     "Centre",
                     "Auto"};
    return tags;
  }
  
  public void setAsText(final String val)
  {
    super.setAsText(val);
    
    if("Auto".equals(val))
      _myLocation = new Integer(AUTO);
      
  }

  public String getAsText()
  {
    String res = null;
    switch(_myLocation.intValue())
    {
    case(TOP):
      res = "Top";
      break;
    case(BOTTOM):
      res = "Bottom";
      break;
    case(LEFT):
      res = "Left";
      break;
    case(RIGHT):
      res = "Right";
      break;
    case(CENTRE):
      res = "Centre";
      break;
    case(AUTO):
    default:
      res = "Auto";
      break;
    }
    return res;
  }
}
