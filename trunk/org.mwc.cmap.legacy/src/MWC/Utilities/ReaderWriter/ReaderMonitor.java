// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ReaderMonitor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: ReaderMonitor.java,v $
// Revision 1.2  2004/05/24 16:24:41  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:51  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:05+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:56+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:55+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 13:03:26+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-08-06 12:34:29+01  administrator
// Initial revision
//


package MWC.Utilities.ReaderWriter;


import java.io.*;
import javax.swing.*;

public class ReaderMonitor extends BufferedReader
{
  private int _length;
  private float _counter;
  private int _progress;
  ProgressMonitor _pm;
  private JFrame _tmpFrame;
  private Thread _myThread;

  public ReaderMonitor(Reader r, int length, String fileName)
  {
    super(r);
   _length = length;
    _counter = 0;
    _progress = 0;
    _myThread = new showMonitor(fileName);
    _myThread.start();
  }

  protected class showMonitor extends Thread
  {
    String _name;
    public showMonitor(String name)
    {
      super();
      _name = name;
    }
    public void run()
    {
      java.io.File fl = new java.io.File(_name);
      _pm = new ProgressMonitor(null, "Reading file:" + fl.getName(), "blank", 0, 99);
      _pm.setMillisToPopup(200);
    }
  }

  /** override the readLine method, to tell us were at a new line
   *
   */
  public String readLine()
          throws IOException
  {
    _counter++;
    float prog = (_counter / _length * 100);
    _progress = (int) prog;


    if(_pm != null)
    {
      _pm.setProgress(_progress);
      _pm.setNote("" + _progress + "% complete");

      if(_progress >= 99)
      {
        _pm.close();
        _pm = null;
      }

    }

    return super.readLine();
  }

  /** finalise, time to close the progress bar
   *
   */
  protected void finalize()
              throws Throwable
  {
    super.finalize();
    _tmpFrame.dispose();
    _tmpFrame = null;
    if(_pm != null)
    {
      _pm.close();
      _pm = null;
    }

  }

}
