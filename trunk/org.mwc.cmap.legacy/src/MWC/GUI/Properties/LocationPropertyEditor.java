package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: LocationPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: LocationPropertyEditor.java,v $
// Revision 1.3  2005/01/17 16:11:17  Ian.Mayo
// Put the locations in a more sensible order
//
// Revision 1.2  2004/05/25 15:29:03  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:24  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:42+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:42+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:42+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:50+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:48+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:13  ianmayo
// initial version
//
// Revision 1.2  1999-10-14 16:11:59+01  ian_mayo
// Added "CENTRE" location
//
// Revision 1.1  1999-10-12 15:36:50+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-04 09:45:30+01  administrator
// minor mods, tidying up
//
// Revision 1.1  1999-07-27 10:50:43+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-23 14:03:57+01  administrator
// Initial revision
//

import java.beans.*;
import java.awt.*;

public class LocationPropertyEditor extends PropertyEditorSupport
{
   
  final static public int TOP = 0;
  final static public int BOTTOM = 1;
  final static public int LEFT = 2;
  final static public int RIGHT = 3;
  final static public int CENTRE = 4;

  
  protected Integer _myLocation;
    
  public String[] getTags()
  {
    String tags[] = {"Top",
                     "Bottom",
                     "Left",
                     "Right",
										 "Centre"};
    return tags;
  }

  public Object getValue()
  {
    return _myLocation;
  }

  
  
  public void setValue(Object p1)
  {
    if(p1 instanceof Integer)
    {
      _myLocation = (Integer)p1;
    }
    if(p1 instanceof String)
    {
      String val = (String) p1;
      setAsText(val);
    }
  }
    
  public void setAsText(String val)
  {
    if(val.equals("Top"))
      _myLocation = new Integer(TOP);
    if(val.equals("Bottom"))
      _myLocation = new Integer(BOTTOM);
    if(val.equals("Left"))
      _myLocation = new Integer(LEFT);
    if(val.equals("Right"))
      _myLocation = new Integer(RIGHT);
		if(val.equals("Centre"))
			_myLocation = new Integer(CENTRE);
      
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
    }
    return res;
  }
}
  
