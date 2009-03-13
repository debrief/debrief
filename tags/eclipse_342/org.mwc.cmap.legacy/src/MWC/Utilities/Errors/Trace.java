// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Trace.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: Trace.java,v $
// Revision 1.4  2006/04/06 09:42:37  Ian.Mayo
// Output path of trace file
//
// Revision 1.3  2004/05/24 16:26:07  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.2  2003/08/01 10:45:26  Ian.Mayo
// Implement work-around to still show warning to user
// as requested if output file has been opened
// without user being informed
//
// Revision 1.1.1.1  2003/07/17 10:07:50  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:05+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:58+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:28+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-03-28 11:50:34+00  administrator
// Write to System.err, not System.out
//
// Revision 1.1  2001-08-01 13:01:15+01  administrator
// Add new method which allows calling method to indicate if it is worth bothering the user with this particular error
//
// Revision 1.0  2001-07-17 08:42:48+01  administrator
// Initial revision
//
// Revision 1.5  2001-06-14 11:57:22+01  novatech
// output debug code to console
//
// Revision 1.4  2001-06-04 18:18:54+01  novatech
// modified to produce dialog when first error sent to Trace file
//
// Revision 1.3  2001-02-02 15:52:48+00  novatech
// switch to low-profile debugging
//
// Revision 1.2  2001-01-16 19:28:19+00  novatech
// support Debug mode, where comments are sent to System.out
//
// Revision 1.1  2001-01-03 13:41:36+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:08  ianmayo
// initial version
//
// Revision 1.3  2000-10-26 15:38:13+01  ian_mayo
// add trace message which does not need Throwable object
//
// Revision 1.2  2000-10-09 13:35:42+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.1  2000-10-09 13:07:53+01  ian_mayo
// Initial revision
//

package MWC.Utilities.Errors;

import java.io.*;

/** Class to allow the output of stack traces to file, instead of the command line
 *
 * @author Ian.Mayo
 * @version 1
 */
public class Trace extends java.lang.Object {

  /** whether we also send output to the console
   */
  static final private boolean DEBUG = true;

    /** destination for the text
     */
    static private java.io.PrintStream _output;

    /** the filename to use */
    static private final String _outName = "trace.txt";

    /** remember the line separator on this system */
    static private String _line_separator;

    /** remember if we have already shown a warning to the user about the
     * trace file being created
     */
    static private boolean _warningShown = false;

  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

/** write this exception to the trace file
 * @param e the exception to record
 */
    static public void trace(Throwable e)
    {
      if(_output == null)
      {
        createOutput(true);
      }

      // just do an extra check to see if the user has been warned yet (if the user wants to, that-is)
      if(!_warningShown)
      {
        String msg = "Errors have occured.  An error file has been created the working directory";
        MWC.GUI.Dialogs.DialogFactory.showMessage("Errors recorded", msg);
        _warningShown = true;
      }
      //
      doHeader();

      e.printStackTrace(_output);

      if(DEBUG)
      {
        e.printStackTrace();
      }
    }


   /** write these details to the trace file, together with a message
     */
    static public void trace(String message, boolean showWarning)
    {
      if(_output == null)
      {
        createOutput(showWarning);
      }

      // just do an extra check to see if the user has been warned yet (if the user wants to, that-is)
      if(showWarning && !_warningShown)
      {
        String msg = "Errors have occured.  An error file has been created the working directory";
        MWC.GUI.Dialogs.DialogFactory.showMessage("Errors recorded", msg);
        _warningShown = true;
      }


      //
      doHeader();

      _output.print(message);
      _output.print(_line_separator);

      if(DEBUG)
      {
        System.err.println(message);
      }

    }

   /** write these details to the trace file, together with a message
     */
    static public void trace(String message)
    {
      trace(message, true);
    }

    /** write these details to the trace file, together with a message
     */
    static public void trace(Throwable e, String message)
    {
      if(_output == null)
      {
        createOutput(true);
      }

      // just do an extra check to see if the user has been warned yet
      if(!_warningShown)
      {
        String msg = "Errors have occured.  An error file has been created the working directory";
        MWC.GUI.Dialogs.DialogFactory.showMessage("Errors recorded", msg);
        _warningShown = true;
      }

      //
      doHeader();

      _output.print(message);
      _output.print(_line_separator);
      e.printStackTrace(_output);


      if(DEBUG)
      {
        System.err.println(message);
        e.printStackTrace();
      }
    }

/** create the output stream
 */
    static private void createOutput(boolean showWarning)
    {
      _line_separator = System.getProperty("line.separator");

      File f_out = new File(_outName);
      try{

        if(showWarning)
        {
          // this is the first time we have sent errors to the trace, inform the user
          String msg = "Errors have occured.  An error file has been created in " + f_out.getAbsolutePath();
          MWC.GUI.Dialogs.DialogFactory.showMessage("Errors recorded", msg);

          _warningShown = true;
        }

        _output = new PrintStream(new FileOutputStream(f_out));
        // stick in the date
        _output.print("Stack trace started at:" + new java.util.Date());

        System.err.println("Trace output to file:" + f_out.getAbsolutePath());
        
        _output.print(_line_separator);

        // also insert the current system properties
        System.getProperties().list(_output);

        doHeader();

      }
      catch(Exception e)
      {
          // don't bother
          System.err.println("Failed to create output trace, output going to sys.err");
        _output = System.err;
      }
    }


    static private void doHeader()
    {
      if(_output == null)
      {
        createOutput(true);
      }
      //
      _output.print("-------------------------------");

      _output.print(_line_separator);
    }
}
