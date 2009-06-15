package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: FontPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: FontPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:28:56  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:23  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:43+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:41+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:40+01  ian_mayo
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
// Revision 1.1  1999-10-12 15:36:49+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:42+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-23 14:03:57+01  administrator
// Initial revision
//

import java.awt.Font;
import java.beans.PropertyEditorSupport;

public class FontPropertyEditor extends PropertyEditorSupport
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  protected Font theFont;
  
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public FontPropertyEditor()
  {
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  


  public String getAsText()
  {
    String res = theFont.getName() + " " + theFont.getSize() + " pt";
    return res;
  }

  public String[] getTags()
  {
    String tags[] = {"6",
                   "8",
                   "10",
                   "14",
                   "18"};
    return tags;
  }

  public Object getValue()
  {
    return theFont;
  }

  public void setValue(Object p1)
  {
    if(p1 instanceof Font)
      theFont = (Font)p1;
  }
  
  

  public void setAsText(String p1)
  {
    Integer i = Integer.valueOf(p1);
    Font newF = new Font(theFont.getName(), 0, i.intValue());
    theFont = newF;
  }

}
