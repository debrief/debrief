package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: LineLocationPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: LineLocationPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:29:00  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:24  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:43+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:41+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:58+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 14:01:41+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-10 13:23:16+01  administrator
// improved implementation which stores strings in array
//
// Revision 1.0  2001-08-10 11:15:18+01  administrator
// Initial revision
//


import java.beans.PropertyEditorSupport;

public class LineLocationPropertyEditor extends PropertyEditorSupport
{

  final static public int START = 0;
  final static public int MIDDLE = 1;
  final static public int END = 2;

  String [] _myTags;

  protected Integer _myLineLocation;

  public String[] getTags()
  {
    if(_myTags == null)
    {
      _myTags = new String[] {"Start",
                       "Middle",
                       "End"};
    }
    return _myTags;
  }

  public Object getValue()
  {
    return _myLineLocation;
  }



  public void setValue(Object p1)
  {
    if(p1 instanceof Integer)
    {
      _myLineLocation = (Integer)p1;
    }
    if(p1 instanceof String)
    {
      String val = (String) p1;
      setAsText(val);
    }
  }

  public void setAsText(String val)
  {
    for(int i=0;i<getTags().length;i++)
    {
      String thisStr = getTags()[i];
      if(thisStr.equals(val))
        _myLineLocation = new Integer(i);
    }
  }

  public String getAsText()
  {
    String res = null;
    int index = _myLineLocation.intValue();
    res = getTags()[index];
    return res;
  }
}

