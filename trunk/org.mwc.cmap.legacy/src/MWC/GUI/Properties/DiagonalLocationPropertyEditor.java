package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DiagonalLocationPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: DiagonalLocationPropertyEditor.java,v $
// Revision 1.3  2005/01/28 09:33:45  Ian.Mayo
// Make the property indexes work correctly
//
// Revision 1.2  2004/05/25 15:28:49  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:23  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:44+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:40+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:37+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:49+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:47+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:10  ianmayo
// initial version
//
// Revision 1.3  2000-11-24 11:51:26+00  ian_mayo
// correct typo
//
// Revision 1.2  2000-11-22 10:37:12+00  ian_mayo
// allow use of strings without spaces (for XML files)
//
// Revision 1.1  2000-09-26 10:52:04+01  ian_mayo
// Initial revision
//

import java.beans.PropertyEditorSupport;

public class DiagonalLocationPropertyEditor extends PropertyEditorSupport
{

  final static public int TOP_LEFT = 0;
  final static public int TOP_RIGHT = 1;
  final static public int BOTTOM_LEFT = 2;
  final static public int BOTTOM_RIGHT = 3;

  protected Integer _myDiagonalLocation;

  public String[] getTags()
  {
    String tags[] = {"Top Left",
                     "Top Right",
                     "Bottom Left",
                     "Bottom Right"};
    return tags;
  }

  public Object getValue()
  {
    return _myDiagonalLocation;
  }



  public void setValue(Object p1)
  {
    if(p1 instanceof Integer)
    {
      _myDiagonalLocation = (Integer)p1;
    }
    if(p1 instanceof String)
    {
      String val = (String) p1;
      setAsText(val);
    }
  }

  public void setAsText(String val)
  {
    if(val.equals("Top Left")||val.equals("TopLeft"))
      _myDiagonalLocation = new Integer(TOP_LEFT);
    if(val.equals("Bottom Left")||val.equals("BottomLeft"))
      _myDiagonalLocation = new Integer(BOTTOM_LEFT);
    if(val.equals("Top Right")||val.equals("TopRight"))
      _myDiagonalLocation = new Integer(TOP_RIGHT);
    if(val.equals("Bottom Right")||val.equals("BottomRight"))
      _myDiagonalLocation = new Integer(BOTTOM_RIGHT);
  }

  public String getAsAbbreviatedText()
  {
    String res = null;
    switch(_myDiagonalLocation.intValue())
    {
    case(TOP_LEFT):
      res = "TopLeft";
      break;
    case(BOTTOM_LEFT):
      res = "BottomLeft";
      break;
    case(TOP_RIGHT):
      res = "TopRight";
      break;
    case(BOTTOM_RIGHT):
      res = "BottomRight";
      break;
    }
    return res;
  }


  public String getAsText()
  {
    String res = null;
    switch(_myDiagonalLocation.intValue())
    {
    case(TOP_LEFT):
      res = "Top Left";
      break;
    case(BOTTOM_LEFT):
      res = "Bottom Left";
      break;
    case(TOP_RIGHT):
      res = "Top Right";
      break;
    case(BOTTOM_RIGHT):
      res = "Bottom Right";
      break;
    }
    return res;
  }
}

