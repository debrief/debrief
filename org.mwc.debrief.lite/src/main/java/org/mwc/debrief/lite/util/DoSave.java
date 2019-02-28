package org.mwc.debrief.lite.util;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;

import Debrief.GUI.Frames.Application;
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
    File targetFile = null;
    String outputFileName = fileName;
    if(fileName == null) {
      outputFileName = showSaveDialog(null,null);
      if(outputFileName!=null) {
        targetFile = new File(outputFileName);
      }
    }
    else {
      if(fileName!=null) {
        System.out.println("Filename:"+fileName);
        if(fileName.endsWith(".dpf")) {
          targetFile = new File(fileName);
        }
        else {
          // if the file is already loaded and 
          // has a different extension than dpf,
          // then show the save dialog 
          File f = new File(fileName);
          fileName = getFileName(fileName);
          fileName = showSaveDialog(f.getParentFile(),fileName);
          if(fileName!=null) {
            targetFile = new File(fileName);
          }
        }
      }
    }

    if((targetFile!=null && targetFile.exists() && targetFile.canWrite()) 
        || (targetFile!=null && !targetFile.exists() && targetFile.getParentFile().canWrite()) ) 
    {        

      //export to this file.
      // if it already exists, check with rename/cancel
      OutputStream stream = null;
      try
      {
        stream = new FileOutputStream(targetFile.getAbsolutePath());
        performSave(stream);
      }
      catch (FileNotFoundException e1)
      {
        Application.logError2(Application.ERROR, "Can't find file", e1);
      }
      finally {
        try
        {
          stream.close();
          DebriefLiteApp.currentFileName = targetFile.getAbsolutePath();
          _theFrame.setTitle(targetFile.getName());
        }
        catch (IOException e1)
        {
          //ignore
        }
      }
    }
  }
 

}
