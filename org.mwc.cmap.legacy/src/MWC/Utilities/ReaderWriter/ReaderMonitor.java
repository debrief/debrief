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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.ProgressMonitor;

import MWC.Utilities.ReaderWriter.PlainImporter.MonitorProvider;

public class ReaderMonitor extends BufferedReader
{
  private int _counter;

  private MonitorProvider _provider;

  public ReaderMonitor(final Reader r, final int length, final String fileName,
      MonitorProvider provider)
  {
    super(r);
    _counter = 0;
    this._provider = provider;
    _provider.init(fileName, length);
  }

  public ReaderMonitor(final Reader r, final int length, final String fileName)
  {
    this(r, length, fileName, new SwingProvider());
  }

  /**
   * override the readLine method, to tell us were at a new line
   *
   */
  public String readLine() throws IOException
  {
    _counter++;
    if(_provider!=null)
      _provider.progress(_counter);
    return super.readLine();
  }

  /**
   * finalise, time to close the progress bar
   *
   */
  protected void finalize() throws Throwable
  {
    super.finalize();
    if(_provider!=null)
      _provider.done();

  }
  
  @Override
  public void close() throws IOException
  {
    super.close();
    if(_provider!=null)
      _provider.done();
  }

  private static class SwingProvider implements MonitorProvider
  {

    private ProgressMonitor _pm;
    private Thread _myThread;
    private int _length;

    @Override
    public void init(final String fileName, final int length)
    {
      _length = length;
      _myThread = new showMonitor(fileName, length);
      _myThread.start();

    }

    protected class showMonitor extends Thread
    {
      private final String _name;
      private final int _length;

      public showMonitor(final String name, final int length)
      {
        super();
        _name = name;
        _length = length;
      }

      public void run()
      {
        final java.io.File fl = new java.io.File(_name);
        _pm = new ProgressMonitor(null, "Reading file:" + fl.getName(), "blank",
            0, _length - 1);
        _pm.setMillisToPopup(0);
        _pm.setMillisToDecideToPopup(0);
      }
    }

    @Override
    public void progress(final int progress)
    {
      if (_pm != null)
      {
        try
        {
          _pm.setNote("" + (progress * 100 / _length) + "% complete");
          _pm.setProgress(progress);
        }
        catch (Exception e)
        {
          // This shouldn't happen. Let's leave it just in case.
        }
      }
    }
    
    @Override
    public void done()
    {
      // Nothing to do here :)
    }
  }
}
