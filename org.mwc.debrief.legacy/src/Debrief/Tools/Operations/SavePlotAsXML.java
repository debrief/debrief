/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package Debrief.Tools.Operations;

import Debrief.GUI.Frames.Session;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.Utilities.Errors.Trace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SavePlotAsXML extends MWC.GUI.Tools.Operations.Save
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  private Session _theSession = null;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public SavePlotAsXML(final ToolParent theParent,
                       final Session theSession)
  {
    this(theParent, theSession, "Save Plot As...", "images/saveas.gif");
  }

  public SavePlotAsXML(final ToolParent theParent,
                       final Session theSession,
                       final String theTitle,
                       final String theImage)
  {
    super(theParent, theTitle, "*.xml", theImage);

    // store the session parameter
    _theSession = theSession;

    // see if we have an old directory to retrieve
    if (_lastDirectory.equals(""))
    {
      final String val = getParent().getProperty("XML_Directory");
      if (val != null)
        _lastDirectory = val;
    }
  }

  /////////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////////

  /** save the plot to file.  If we are over-writing an existing file we write to a new file-name, then we rename
   * to the existing file if it all goes ok
   *
   * @param fileName
   * @return
   */
  protected final Action doSave(final String fileName)
  {
    Action res = null;

    // ok, we do a clever save method here.  We save to a temp filename, and only over-write the
    // real-one if the save is successful.


    // check the output filename
    String theFileName = fileName;
    final int idx = theFileName.toLowerCase().indexOf(".xml");
    if (idx == -1)
    {
      theFileName += ".xml";
    }

    // the name of the temp filename, if we need one.
    String tmpFileName = null;

    // the name of the file which we actually store to
    String outputFileName = theFileName;


    // does this file exist
    final File targetFile = new File(theFileName);
    if (targetFile.exists())
    {
      // yes, the file already exists.  Write to a temp filename
      tmpFileName = targetFile.getName();

      // ok, prepend with a marker
      tmpFileName = "~" + tmpFileName;

      // and a tmp suffix
      tmpFileName = tmpFileName.replaceAll(".xml", ".tmp");

      // and replace the dir markers
      tmpFileName = targetFile.getParent() + "\\" + tmpFileName;

      outputFileName = tmpFileName;

    }

    // did it all go ok?
    boolean saveWorked = false;

    // now save session to this file
    try
    {
      // open the file
      final OutputStream os = new FileOutputStream(outputFileName);

      try
      {
        // pass all of this to the XML exporter
        Debrief.ReaderWriter.XML.DebriefXMLReaderWriter.exportThis(_theSession, os);

        // ok - remember it went ok
        saveWorked = true;
      }
      catch (final java.lang.OutOfMemoryError me)
      {
        MWC.Utilities.Errors.Trace.trace(me, " Ran out of memory whilst saving plot, try adding -Xmx256m to the command line");
      }

      // close the stream, even if the operation failed.  We need to close the stream so that we can delete
      // the temp file when necessary
      os.close();
    }
    catch (final IOException e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }

    // did it work?
    if (saveWorked)
    {
      // ok, are we over-writing an existing file?
      if (tmpFileName != null)
      {
        // yes.  delete the original file
        final File oldFile = new File(theFileName);
        oldFile.delete();

        // and rename our new file
        final File tmpFile = new File(tmpFileName);
        final boolean renameChanged = tmpFile.renameTo(oldFile);
        
        if(!renameChanged)
        {
        	Trace.trace("File-save failed.  Old file still present with '.tmp' suffix", true);
        }
        
      }

      // inform the session of it's filename
      _theSession.setFileName(theFileName);

      // store the action
      res = new SavePlotAction(_theSession.getName());

      // put the filename into the MRU
      Debrief.GUI.Frames.Application.addToMru(theFileName);

    }
    else
    {
      // the save failed - try to delete the working file
      final File scrapFile = new File(outputFileName);

      if (scrapFile.exists())
      {
        final boolean deleted = scrapFile.delete();
        System.out.println("deleted:" + deleted);
      }
    }

    return res;
  }


  final Session getSession()
  {
    return _theSession;
  }

  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  protected final static class SavePlotAction implements Action
  {
    /**
     * store the name of the session we have saved
     */
    final String _theSessionName;

    public SavePlotAction(final String theName)
    {
      _theSessionName = theName;
    }

    public final boolean isRedoable()
    {
      return false;
    }


    public final boolean isUndoable()
    {
      return false;
    }

    public final String toString()
    {
      return "Save " + _theSessionName;
    }

    public final void undo()
    {
      // delete the plottables from the Application object
    }

    public final void execute()
    {
    }

  }


  public final void close()
  {
    super.close();

    _theSession = null;
  }


  public static void main(final String[] args)
  {
    final String fName = "c:\\test2.txt";
    final File fD = new File(fName);
    //    File f2 = new File("c:\\test2.txt");
    //    fD.renameTo(f2);
    final boolean deleted = fD.delete();
    System.out.println("res:" + deleted);

  }
}
