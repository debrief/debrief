package org.mwc.debrief.lite.util;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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
    if(DebriefLiteApp.currentFileName!=null) {
      File curDir = new File(DebriefLiteApp.currentFileName);
      String fileName = getFileName(curDir.getName());
      String outputFile = showSaveDialog(curDir.getParentFile(),fileName);
      targetFile = new File(outputFile);
    }
    else {
      String outputFile = showSaveDialog(null,null);
      targetFile = new File(outputFile);
    }
    if(targetFile!=null) {
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

  protected String getFileName(String fileName)
  {
    return fileName.substring(0,fileName.lastIndexOf("."));
  }

  protected final  String showSaveDialog(final File parentDirectory,final String initialName) {
    File targetFile = null;
    String outputFileName = null;
    JFileChooser fileChooser = new JFileChooser();
    FileFilter filter = new FileNameExtensionFilter("dpf file","dpf");
    fileChooser.setFileFilter(filter);
    if(initialName!=null) {
      fileChooser.setSelectedFile(new File(initialName+".dpf"));
    }
    if(parentDirectory!=null) {
      fileChooser.setCurrentDirectory(parentDirectory);
    }
    fileChooser.showSaveDialog(null);
    targetFile = fileChooser.getSelectedFile();
    if(targetFile!=null) {
      if(targetFile.exists()) {
        int yesNo = JOptionPane.showConfirmDialog(null, targetFile.getName()+" already exists. Do you want to overwrite?");
        if(JOptionPane.YES_OPTION == yesNo) {
          outputFileName = targetFile.getAbsolutePath();    
        }
        //let the user try again otherwise
      }
      else {
        outputFileName = targetFile.getAbsolutePath();
      }
    }
    return outputFileName;
  }

}
