package org.mwc.debrief.lite.util;

import java.awt.event.ActionEvent;
import java.io.File;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.menu.DebriefRibbonFile;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;

import Debrief.GUI.Frames.Session;

public class DoSave extends DoSaveAs
{


  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public DoSave(Session session,JRibbonFrame theFrame)
  {
    super(session,theFrame);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    // get the current file path,
    // check we have write permission to it before starting save
    // get a new file path to use.
    String fileName = DebriefLiteApp.currentFileName;
    final File targetFile;
    String outputFileName = fileName;
    if (fileName == null)
    {
      final File location;
      final String lastFileLocation = DebriefLiteApp.getDefault().getProperty(
          DoSaveAs.LAST_FILE_LOCATION);
      if(lastFileLocation!=null) 
      {
        location = new File(lastFileLocation);
      }
      else {
        location = null;
      }
      outputFileName = showSaveDialog(location, DEFAULT_FILENAME);
      if (outputFileName != null)
      {
        targetFile = new File(outputFileName);
      }
      else
      {
        targetFile = null;
      }
    }
    else
    {
      if (fileName.endsWith(".dpf"))
      {
        targetFile = new File(fileName);
      }
      else
      {
        // if the file is already loaded and
        // has a different extension than dpf,
        // then show the save dialog
        File f = new File(fileName);
        fileName = getFileName(fileName);
        fileName = showSaveDialog(f.getParentFile(), fileName);
        if (fileName != null)
        {
          targetFile = new File(fileName);
        }
        else
        {
          targetFile = null;
        }
      }
    }
    if (targetFile != null)
    {
      DebriefRibbonFile.saveChanges(targetFile.getAbsolutePath(), _session,
          _theFrame);
    }
  }
}
