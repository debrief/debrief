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
  public SavePlotAsXML(ToolParent theParent,
                       Session theSession)
  {
    this(theParent, theSession, "Save Plot As...", "images/saveas.gif");
  }

  public SavePlotAsXML(ToolParent theParent,
                       Session theSession,
                       String theTitle,
                       String theImage)
  {
    super(theParent, theTitle, "*.xml", theImage);

    // store the session parameter
    _theSession = theSession;

    // see if we have an old directory to retrieve
    if (_lastDirectory == "")
    {
      String val = getParent().getProperty("XML_Directory");
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
  protected final Action doSave(String fileName)
  {
    Action res = null;

    // ok, we do a clever save method here.  We save to a temp filename, and only over-write the
    // real-one if the save is successful.


    // check the output filename
    final int idx = fileName.toLowerCase().indexOf(".xml");
    if (idx == -1)
    {
      fileName += ".xml";
    }

    // the name of the temp filename, if we need one.
    String tmpFileName = null;

    // the name of the file which we actually store to
    String outputFileName = fileName;


    // does this file exist
    File targetFile = new File(fileName);
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
      OutputStream os = new FileOutputStream(outputFileName);

      try
      {
        // pass all of this to the XML exporter
        Debrief.ReaderWriter.XML.DebriefXMLReaderWriter.exportThis(_theSession, os);

        // ok - remember it went ok
        saveWorked = true;
      }
      catch (java.lang.OutOfMemoryError me)
      {
        MWC.Utilities.Errors.Trace.trace(me, " Ran out of memory whilst saving plot, try adding -Xmx256m to the command line");
      }

      // close the stream, even if the operation failed.  We need to close the stream so that we can delete
      // the temp file when necessary
      os.close();
    }
    catch (IOException e)
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
        File oldFile = new File(fileName);
        oldFile.delete();

        // and rename our new file
        File tmpFile = new File(tmpFileName);
        boolean renameChanged = tmpFile.renameTo(oldFile);
        
        if(!renameChanged)
        {
        	Trace.trace("File-save failed.  Old file still present with '.tmp' suffix", true);
        }
        
      }

      // inform the session of it's filename
      _theSession.setFileName(fileName);

      // store the action
      res = new SavePlotAction(_theSession.getName());

      // put the filename into the MRU
      Debrief.GUI.Frames.Application.addToMru(fileName);

    }
    else
    {
      // the save failed - try to delete the working file
      File scrapFile = new File(outputFileName);

      if (scrapFile.exists())
      {
        boolean deleted = scrapFile.delete();
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
  protected final class SavePlotAction implements Action
  {
    /**
     * store the name of the session we have saved
     */
    final String _theSessionName;

    public SavePlotAction(String theName)
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


  public static void main(String[] args)
  {
    String fName = "c:\\test2.txt";
    File fD = new File(fName);
    //    File f2 = new File("c:\\test2.txt");
    //    fD.renameTo(f2);
    boolean deleted = fD.delete();
    System.out.println("res:" + deleted);

  }
}
