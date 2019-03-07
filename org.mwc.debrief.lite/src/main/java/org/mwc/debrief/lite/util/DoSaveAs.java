package org.mwc.debrief.lite.util;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.menu.DebriefRibbonFile;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;

import Debrief.GUI.Frames.Session;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;

public class DoSaveAs extends AbstractAction
{


  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  protected final Session _session;
  protected JRibbonFrame _theFrame;

  public DoSaveAs(Session session,JRibbonFrame frame)
  {
    _session = session;
    _theFrame = frame;
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
    final String outputFile;
    if (DebriefLiteApp.currentFileName != null)
    {
      File curDir = new File(DebriefLiteApp.currentFileName);
      String fileName = getFileName(curDir.getName());
      outputFile = showSaveDialog(curDir.getParentFile(), fileName);
    }
    else
    {
      outputFile = showSaveDialog(null, null);
    }

    DebriefRibbonFile.saveChanges(outputFile, _session, _theFrame);
  }

  protected String getFileName(String fileName)
  {
    return fileName.substring(0,fileName.lastIndexOf("."));
  }

  public final static String showSaveDialog(final File parentDirectory,
      final String initialName)
  {
    String outputFileName = null;
    final JFileChooser fileChooser = new JFileChooser();
    final FileFilter filter = new FileNameExtensionFilter("dpf file", "dpf");
    fileChooser.setFileFilter(filter);
    if (initialName != null)
    {
      fileChooser.setSelectedFile(new File(initialName + ".dpf"));
    }
    if (parentDirectory != null)
    {
      fileChooser.setCurrentDirectory(parentDirectory);
    }
    int res = fileChooser.showSaveDialog(null);
    if (res == JOptionPane.OK_OPTION)
    {
      final File targetFile = fileChooser.getSelectedFile();
      if (targetFile != null)
      {
        if (targetFile.exists())
        {
          int yesNo = JOptionPane.showConfirmDialog(null, targetFile.getName()
              + " already exists. Do you want to overwrite?");
          if (JOptionPane.YES_OPTION == yesNo)
          {
            outputFileName = targetFile.getAbsolutePath();
          }
          // let the user try again otherwise
        }
        else
        {
          outputFileName = targetFile.getAbsolutePath();
        }
      }
    }
    else
    {
      outputFileName = null;
    }
    return outputFileName;
  }

}
