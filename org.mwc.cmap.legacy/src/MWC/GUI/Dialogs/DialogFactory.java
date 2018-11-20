/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.text.ParseException;

import javax.swing.JOptionPane;

import MWC.GUI.MessageProvider;
import MWC.GUI.Dialogs.AWT.AWTFile;
import MWC.GUI.Dialogs.Swing.SwingFile;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class DialogFactory
{

  protected static boolean _useSwing = false;

  static SwingFile _swinger = new SwingFile();
  static AWTFile _awter = new AWTFile();

  static private boolean _runHeadless = false;
  
  static public void useSwing(final boolean val)
  {
    _useSwing = val;
  }
  
  /** direct the code to run headless, so we don't 
   * try to open dialogs
   * @param runHeadless whether to run headless
   */
  static public void setRunHeadless(final boolean runHeadless)
  {
    _runHeadless = runHeadless;
  }

  /**
   * GUI-independent function to get filename of existing file
   *
   * @return the filename as a string, or null if cancelled
   */
  static public java.io.File[] getOpenFileName(final String filter,
                                               final String description,
                                               final String directory)
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
  static public java.io.File getNewFile(final String filter,
                                        final String description,
                                        final String directory)
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

  static public void showMessage(final String title, final String msg)
  {
    if (!_runHeadless)
    {
      // create duff frame, that message dialog can appear
      // in centre of
      final Frame tmp = new Frame();
      // and put the frame in the centre of the screen
      final Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
      tmp.setLocation(sz.width / 2, sz.height / 2);

      MessageProvider.Base.show(title,
          msg,
          MessageProvider.ERROR);
    }
  }


  public static void main(final String[] args)
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
  static public Integer getInteger(final String title,
                                   final String message,
                                   final int defaultVal)
  {
    Integer res = null;

    final javax.swing.JOptionPane myMessage = new javax.swing.JOptionPane();
    myMessage.setInputValue("" + defaultVal);

    final String s = JOptionPane.showInputDialog(message, new String("" + defaultVal));

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
      catch (final java.lang.NumberFormatException e)
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
  static public Double getDouble(final String title,
                                 final String message,
                                 final int defaultVal)
  {
    Double res = null;

    final javax.swing.JOptionPane myMessage = new javax.swing.JOptionPane();
    myMessage.setInitialSelectionValue("" + defaultVal);

    final String s = JOptionPane.showInputDialog(message);

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
          res = MWCXMLReader.readThisDouble(s);
        }
        catch (final ParseException e)
        {
          MWC.Utilities.Errors.Trace.trace(e);
          showMessage("Integer entry", "Sorry, invalid double entered");
        }
      }
    }

    return res;
  }


}
