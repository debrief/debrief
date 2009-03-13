package MWC.GUI.Properties.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingDatePropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: SwingDatePropertyEditor.java,v $
// Revision 1.3  2004/11/26 11:32:48  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.2  2004/05/25 15:29:37  Ian.Mayo
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
// Revision 1.1  2002-04-11 14:01:26+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-31 10:36:55+01  administrator
// Tidied up layout, so all data is displayed when editor panel is first opened
//
// Revision 1.0  2001-07-17 08:43:31+01  administrator
// Initial revision
//
// Revision 1.4  2001-07-12 12:06:59+01  novatech
// use tooltips to show the date format
//
// Revision 1.3  2001-01-21 21:38:23+00  novatech
// handle focusGained = select all text
//
// Revision 1.2  2001-01-17 09:41:37+00  novatech
// factor generic processing to parent class, and provide support for NULL values
//
// Revision 1.1  2001-01-03 13:42:39+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:37  ianmayo
// initial version
//
// Revision 1.5  2000-10-09 13:35:47+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.4  2000-04-03 10:48:57+01  ian_mayo
// squeeze up the controls
//
// Revision 1.3  2000-02-02 14:25:07+00  ian_mayo
// correct package naming
//
// Revision 1.2  1999-11-23 11:05:03+00  ian_mayo
// further introduction of SWING components
//
// Revision 1.1  1999-11-16 16:07:19+00  ian_mayo
// Initial revision
//
// Revision 1.1  1999-11-16 16:02:29+00  ian_mayo
// Initial revision
//
// Revision 1.2  1999-11-11 18:16:09+00  ian_mayo
// new class, now working
//
// Revision 1.1  1999-10-12 15:36:48+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:05:48+01  administrator
// Initial revision
//

import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.GUI.Dialogs.DialogFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;

public class SwingDatePropertyEditor extends
  MWC.GUI.Properties.DatePropertyEditor implements java.awt.event.FocusListener
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
   * field to edit the date
   */
  JTextField _theDate;

  /**
   * field to edit the time
   */
  JTextField _theTime;

  /**
   * label to show the microsecodns
   */
  JLabel _theMicrosTxt;

  /**
   * panel to hold everything
   */
  JPanel _theHolder;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /**
   * build the editor
   */
  public java.awt.Component getCustomEditor()
  {
    _theHolder = new JPanel();

    java.awt.BorderLayout bl1 = new java.awt.BorderLayout();
    bl1.setVgap(0);
    bl1.setHgap(0);
    java.awt.BorderLayout bl2 = new java.awt.BorderLayout();
    bl2.setVgap(0);
    bl2.setHgap(0);

    JPanel lPanel = new JPanel();
    lPanel.setLayout(bl1);
    JPanel rPanel = new JPanel();
    rPanel.setLayout(bl2);

    _theHolder.setLayout(new java.awt.GridLayout(0, 2));
    _theDate = new JTextField();
    _theDate.setToolTipText("Format: " + NULL_DATE);
    _theTime = new JTextField();
    _theTime.setToolTipText("Format: " + NULL_TIME);
    lPanel.add("Center", new JLabel("Date:", JLabel.RIGHT));
    lPanel.add("East", _theDate);
    rPanel.add("Center", new JLabel("Time:", JLabel.RIGHT));
    rPanel.add("East", _theTime);

    _theHolder.add(lPanel);
    _theHolder.add(rPanel);

    // get the fields to select the full text when they're selected
    _theDate.addFocusListener(this);
    _theTime.addFocusListener(this);

    // right, just see if we are in hi-res DTG editing mode
    if (HiResDate.inHiResProcessingMode())
    {
      // ok, add a button to allow the user to enter DTG data
      JButton editMicros = new JButton("Micros");
      editMicros.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          editMicrosPressed();
        }
      });

      // ok, we'
      _theMicrosTxt = new JLabel("..");
      _theHolder.add(_theMicrosTxt);
      _theHolder.add(editMicros);
    }

    resetData();
    return _theHolder;
  }

  /**
   * user wants to edit the microseconds.  give him a popup
   */
  void editMicrosPressed()
  {
    //To change body of created methods use File | Settings | File Templates.
    Integer res = DialogFactory.getInteger("Edit microseconds", "Enter microseconds",(int) _theMicros);

    // did user enter anything?
    if(res != null)
    {
      // store the data
      _theMicros = res.intValue();

      // and update the screen
      resetData();
    }
  }

  /**
   * get the date text as a string
   */
  protected String getDateText()
  {
    return _theDate.getText();
  }

  /**
   * get the date text as a string
   */
  protected String getTimeText()
  {
    return _theTime.getText();
  }

  /**
   * set the date text in string form
   */
  protected void setDateText(String val)
  {
    if (_theHolder != null)
    {
      _theDate.setText(val);
    }
  }

  /**
   * set the time text in string form
   */
  protected void setTimeText(String val)
  {
    if (_theHolder != null)
    {
      _theTime.setText(val);
    }
  }

  /**
   * show the user how many microseconds there are
   *
   * @param val
   */
  protected void setMicroText(long val)
  {
    // output the number of microseconds
    _theMicrosTxt.setText(DebriefFormatDateTime.formatMicros(new HiResDate(0, val)) + " micros");
  }

  /////////////////////////////
  // focus listener support classes
  /////////////////////////////


  /**
   * Invoked when a component gains the keyboard focus.
   */
  public void focusGained(FocusEvent e)
  {
    java.awt.Component c = e.getComponent();
    if (c instanceof JTextField)
    {
      JTextField jt = (JTextField) c;
      jt.setSelectionStart(0);
      jt.setSelectionEnd(jt.getText().length());
    }
  }

  /**
   * Invoked when a component loses the keyboard focus.
   */
  public void focusLost(FocusEvent e)
  {
  }
}
