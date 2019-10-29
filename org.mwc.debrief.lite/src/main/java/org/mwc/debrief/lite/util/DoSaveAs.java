package org.mwc.debrief.lite.util;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.menu.DebriefRibbonFile;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

import Debrief.GUI.Frames.Session;

public class DoSaveAs extends AbstractAction implements CommandAction
{

  protected static final String DEFAULT_FILENAME = "Debrief_Plot";
  
  public static final String LAST_FILE_LOCATION = "last_file_location";
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  protected final Session _session;
  protected final JFrame _theFrame;

  public DoSaveAs(final Session session,final JFrame frame)
  {
    _session = session;
    _theFrame = frame;
  }
  
  protected String getLastFileLocation()
  {
    return DebriefLiteApp.getDefault().getProperty(LAST_FILE_LOCATION);
  }

  @Override
  public void actionPerformed(final ActionEvent e)
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
      final File directory;
      String lastFileLocation = getLastFileLocation();
      if(lastFileLocation!=null) 
      {
        directory = new File(lastFileLocation);
      }
      else {
        directory = null;
      }
      outputFile = showSaveDialog(directory, DEFAULT_FILENAME);
    }
    if(outputFile!=null) {
      DebriefRibbonFile.saveChanges(outputFile, _session, _theFrame);
    }
  }

  protected String getFileName(final String fileName)
  {
    return fileName.substring(0,fileName.lastIndexOf("."));
  }

  public final static String showSaveDialog(final File parentDirectory,
      final String initialName)
  {
    final String outputFileName;
    final JFileChooser fileChooser = new JFileChooser();
    final FileFilter filter = new FileNameExtensionFilter("dpf file", "dpf");
    fileChooser.setFileFilter(filter);
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    if (initialName != null)
    {
      fileChooser.setSelectedFile(new File(initialName + ".dpf"));
    }
    if (parentDirectory != null)
    {
      fileChooser.setCurrentDirectory(parentDirectory);
    }
    final int res = fileChooser.showSaveDialog(null);
    if (res == JOptionPane.OK_OPTION)
    {
      final File targetFile = fileChooser.getSelectedFile();
      if (targetFile != null)
      {
        if (targetFile.exists())
        {
          int yesNo = JOptionPane.showConfirmDialog(DebriefLiteApp.getInstance().getApplicationFrame(), targetFile.getName()
              + " already exists. Do you want to overwrite?");
          if (JOptionPane.YES_OPTION == yesNo)
          {
            outputFileName = targetFile.getAbsolutePath();
          }
          else
          {
            outputFileName = null;
          }
          // let the user try again otherwise
        }
        else
        {
          outputFileName = targetFile.getAbsolutePath();
        }
      }
      else
      {
        outputFileName = null;
      }
    }
    else
    {
      outputFileName = null;
    }
    return outputFileName;
  }

  @Override
  public void commandActivated(CommandActionEvent e)
  {
    actionPerformed(e);
    
  }

}
