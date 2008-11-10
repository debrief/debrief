// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DialogFactory.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: DialogFactory.java,v $
// Revision 1.5  2004/11/29 16:03:33  Ian.Mayo
// Handle user cancelling entry of symbol frequency
//
// Revision 1.4  2004/11/26 11:32:43  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.3  2004/11/24 16:05:26  Ian.Mayo
// Switch to hi-res timers
//
// Revision 1.2  2004/05/25 15:23:24  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:16  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:13  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:40+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:05+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:11+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:26+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:58+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:44:27  ianmayo
// initial version
//
// Revision 1.9  2000-11-24 11:52:20+00  ian_mayo
// removing unnecessary comments
//
// Revision 1.8  2000-11-08 11:50:32+00  ian_mayo
// change modal status
//
// Revision 1.7  2000-10-09 13:35:54+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.6  2000-08-07 14:06:38+01  ian_mayo
// remove d-lines
//
// Revision 1.5  2000-05-22 10:11:06+01  ian_mayo
// provide method to get integer value from user
//
// Revision 1.4  2000-02-02 14:23:54+00  ian_mayo
// Workarounds to allow use of original Swing fileChooser, because of problems experienced when using IBM jre (also so that both types of dialog [open/save] return File objects rather  than just pathnames)
//
// Revision 1.3  1999-11-25 13:33:31+00  ian_mayo
// changed, to support returning multiple file names, and to allow different GUI implemetnations
//
// Revision 1.2  1999-11-18 11:08:12+00  ian_mayo
// added swing support
//
// Revision 1.1  1999-10-12 15:36:59+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:46+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-23 14:03:51+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.1  1999-07-07 11:10:06+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:58+01  sm11td
// Initial revision
//
// Revision 1.1  1999-02-01 16:08:55+00  sm11td
// Initial revision
//
// Revision 1.1  1999-01-31 13:33:08+00  sm11td
// Initial revision
//
package MWC.GUI.Dialogs;

import MWC.GUI.Dialogs.AWT.AWTFile;
import MWC.GUI.Dialogs.AWT.MessageDialog;
import MWC.GUI.Dialogs.Swing.SwingFile;

import java.awt.*;

public class DialogFactory
{

  protected static boolean _useSwing = false;

  static SwingFile _swinger = new SwingFile();
  static AWTFile _awter = new AWTFile();

  static public void useSwing(boolean val)
  {
    _useSwing = val;
  }

  /**
   * GUI-independent function to get filename of existing file
   *
   * @return the filename as a string, or null if cancelled
   */
  static public java.io.File[] getOpenFileName(String filter,
                                               String description,
                                               String directory)
  {

    java.io.File[] res = null;
    if (_useSwing)
      res = _swinger.getExistingFile(filter,
                                     description,
                                     directory);
    else
      res = _awter.getExistingFile(filter,
                                   description,
                                   directory);

    return res;
  }

  /**
   * GUI-independent function to get filename of file to
   * use in save operation
   *
   * @param filter      string containing wildcard to use
   * @param description a String describing the type of file we are opening
   * @param directory   the initial directory to open up in
   * @return the filename as a string, or null if cancelled
   */
  static public java.io.File getNewFile(String filter,
                                        String description,
                                        String directory)
  {
    if (_useSwing)
      return _swinger.getNewFile(filter,
                                 description,
                                 directory);
    else
      return _awter.getNewFile(filter,
                               description,
                               directory);
  }

  static public void showMessage(String title, String msg)
  {
    // create duff frame, that message dialog can appear
    // in centre of
    Frame tmp = new Frame();
    // and put the frame in the centre of the screen
    Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
    tmp.setLocation(sz.width / 2,
                    sz.height / 2);
    // open the dialog
    MessageDialog md = new MessageDialog(tmp,
                                         null,
                                         title,
                                         msg,
                                         null,
                                         false);
    md.setVisible(true);
  }


  public static void main(String[] args)
  {
    showMessage("test", "duff");
    System.exit(0);
  }

  public interface FileGetter
  {
    public java.io.File[] getExistingFile(String filter,
                                          String description,
                                          String lastDirectory);

    public java.io.File getNewFile(String filter,
                                   String description,
                                   String lastDirectory);
  }

  /**
   * popup a dialog to retrieve an integer value from the user.
   *
   * @return the integer in an Integer object, or a null value
   */
  static public Integer getInteger(String title,
                                   String message,
                                   int defaultVal)
  {
    Integer res = null;

    javax.swing.JOptionPane myMessage = new javax.swing.JOptionPane();
    myMessage.setInputValue("" + defaultVal);

    String s = myMessage.showInputDialog(message, new String("" + defaultVal));

    if (s == null)
    {
      res = null;
    }
    else
    {
      try
      {
        res = Integer.valueOf(s);
      }
      catch (java.lang.NumberFormatException e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
        showMessage("Integer entry", "Sorry, invalid integer entered");
      }
    }

    return res;
  }

  /**
   * popup a dialog to retrieve an integer value from the user.
   *
   * @return the integer in an Integer object, or a null value
   */
  static public Double getDouble(String title,
                                 String message,
                                 int defaultVal)
  {
    Double res = null;

    javax.swing.JOptionPane myMessage = new javax.swing.JOptionPane();
    myMessage.setInitialSelectionValue("" + defaultVal);

    String s = myMessage.showInputDialog(message);

    if (s == null)
    {
      res = null;
    }
    else
    {
      if (s.length() > 0)
      {
        try
        {
          res = Double.valueOf(s);
        }
        catch (java.lang.NumberFormatException e)
        {
          MWC.Utilities.Errors.Trace.trace(e);
          showMessage("Integer entry", "Sorry, invalid double entered");
        }
      }
    }

    return res;
  }


}
