package org.mwc.debrief.lite.util;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.mwc.debrief.lite.DebriefLiteApp;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;

public class DoSave extends DoSaveAs
{


  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public DoSave(Session session)
  {
    super(session);
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
    if(DebriefLiteApp.currentFileName==null) {
      JFileChooser fileChooser = new JFileChooser();
      FileFilter filter = new FileNameExtensionFilter("dpf file","dpf");
      fileChooser.setFileFilter(filter);
      fileChooser.showSaveDialog(null);
      targetFile = fileChooser.getSelectedFile();
      if(targetFile!=null) {
        outputFileName = targetFile.getAbsolutePath();
      }
    }
    else {
      if(fileName!=null) {
        targetFile = new File(fileName);
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
          if(DebriefLiteApp.currentFileName == null)
           {
            DebriefLiteApp.currentFileName = outputFileName;
            DebriefLiteApp.setTitle(targetFile.getName());
           }
        }
        catch (IOException e1)
        {
          //ignore
        }
      }
    }
  }
}
