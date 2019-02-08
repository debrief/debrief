package org.mwc.debrief.lite.util;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.mwc.debrief.lite.DebriefLiteApp;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;

public class DoSaveAs extends AbstractAction
{


  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final Session _session;

  public DoSaveAs(Session session)
  {
    _session = session;
  }

  protected void performSave(final OutputStream fos)
  {
    DebriefXMLReaderWriter.exportThis(_session, fos);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    // get the current file path,
    // check we have write permission to it before starting save
    // get a new file path to use.
    File targetFile = null;
    JFileChooser fileChooser = new JFileChooser();
    FileFilter filter = new FileNameExtensionFilter("dpf file","dpf");
    fileChooser.setFileFilter(filter);
    if(DebriefLiteApp.currentFileName!=null) {
      File curDir = new File(DebriefLiteApp.currentFileName);
      fileChooser.setCurrentDirectory(curDir.getParentFile());
    }
    fileChooser.showSaveDialog(null);
    targetFile = fileChooser.getSelectedFile();
    String outputFileName = targetFile.getAbsolutePath();
    if((targetFile!=null && targetFile.exists() && targetFile.canWrite()) 
        || (targetFile!=null && !targetFile.exists() && targetFile.getParentFile().canWrite()) ) {        
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
          targetFile.renameTo(new File(outputFileName));
          DebriefLiteApp.currentFileName = outputFileName;
          DebriefLiteApp.setTitle(targetFile.getName());
        }
        catch (IOException e1)
        {
          //ignore
        }
      }
    }

  }

}
