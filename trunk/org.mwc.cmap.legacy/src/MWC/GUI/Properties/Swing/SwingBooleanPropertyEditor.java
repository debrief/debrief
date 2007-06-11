package MWC.GUI.Properties.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingBooleanPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SwingBooleanPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:29:32  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:26  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:47+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:33+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:25+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-02-19 20:23:03+00  administrator
// Set GUI component names to assist JFCUnit testing
//
// Revision 1.1  2002-02-01 16:17:06+00  administrator
// Provide getTags & related methods, so we can use this editor from the new "Format Positions" toolbox operation
//
// Revision 1.0  2001-07-17 08:43:30+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:38+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:35  ianmayo
// initial version
//
// Revision 1.3  2000-08-18 10:06:52+01  ian_mayo
// <>
//
// Revision 1.2  1999-11-23 11:05:04+00  ian_mayo
// further introduction of SWING components
//
// Revision 1.1  1999-11-16 16:03:22+00  ian_mayo
// Initial revision
//
// Revision 1.1  1999-11-16 16:02:10+00  ian_mayo
// Initial revision
//
// Revision 1.1  1999-10-12 15:36:48+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:05:48+01  administrator
// Initial revision
//

import java.beans.*;
import MWC.GenericData.*;
import MWC.GUI.*;
import javax.swing.*;
import java.awt.event.*;

public class SwingBooleanPropertyEditor extends 
           PropertyEditorSupport 
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  boolean _myVal;
  JCheckBox _theBox;
  
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /** indicate that we can't just be painted, we've got to be edited
   */
  public boolean isPaintable()
  {
    return false;
  }

  public java.awt.Component getCustomEditor()
  {
    _theBox = new JCheckBox();
    _theBox.setName("boolean");
    _theBox.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
     {   _myVal = _theBox.isSelected();     }    
    });
    resetData();
    return _theBox;
  }

  public void setValue(Object p1)
  {
    if(p1 instanceof Boolean)
    {
      Boolean val = (Boolean)p1;
      _myVal = val.booleanValue();
    }
    else
      return;
  }

  public void setAsText(String text) throws IllegalArgumentException
  {
    if(text.equals("Yes"))
      _myVal = true;
    else
      _myVal = false;
  }

  public boolean supportsCustomEditor()
  {
    return true;
  }

  public Object getValue()
  {
    return new Boolean(_myVal);
  }

  public void resetData()
  {
    if(_theBox != null)
      _theBox.setSelected(_myVal);
  }

  public String[] getTags()
  {
    return new String[]{"Yes", "No"};
  }

  public String getAsText()
  {
    String res = null;
    if(_myVal == true)
      res = "Yes";
    else
      res = "No";

    return res;
  }
}
