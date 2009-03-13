// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AbstractPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AbstractPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:28:40  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:23  Ian.Mayo
// Initial import
//
// Revision 1.1  2002-11-01 14:42:29+00  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-28 09:25:44+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:39+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:36+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:48+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-12 12:05:33+01  novatech
// white space only
//
// Revision 1.2  2001-07-09 13:58:26+01  novatech
// Add ymf format, and custom SwingControl for editing date format
//
// Revision 1.1  2001-01-03 13:42:46+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:10  ianmayo
// initial version
//
// Revision 1.1  2000-12-01 10:13:35+00  ian_mayo
// Initial revision
//
// Revision 1.1  2000-09-26 10:53:10+01  ian_mayo
// Initial revision
//


package MWC.GUI.Properties;

import java.beans.*;

abstract public class AbstractPropertyEditor extends PropertyEditorSupport
{

  /** the current value from the list of tags
   *
   */
  protected int _currentlySelectedValue;

  /** retrieve the list of tags we display
   *
   * @return the list of options
   */
  abstract public String[] getTags();


  /** return the currently selected string
   *
   * @return
   */
  public Object getValue()
  {
    return new Integer(_currentlySelectedValue);
  }

  /** get the int index of the currently selected item
   *
   * @return the index
   */
  public int getIndex()
  {
    return _currentlySelectedValue;
  }

  /** select this vlaue
   *
   * @param p1
   */
  public void setValue(Object p1)
  {
    if(p1 instanceof String)
    {
      String val = (String) p1;
      setAsText(val);
    }
    if(p1 instanceof Integer)
    {
      _currentlySelectedValue = ((Integer)p1).intValue();
    }
  }

  /** set the index of the current selection
   *
   * @param val
   */
  public void setIndex(int val)
  {
    _currentlySelectedValue = val;
  }

  public void setAsText(String val)
  {
    // loop through the tags to match this
    String [] theTags = getTags();
    for(int i=0;i<theTags.length;i++)
    {
      if(theTags[i].equals(val))
      {
        _currentlySelectedValue = i;
        break;
      }
    }
  }

  public String getAsText()
  {
    return getTags()[_currentlySelectedValue];
  }
}

