package org.mwc.cmap.TimeController.recorders;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.TimeController.wizards.ExportPPTDialog;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.debrief.core.preferences.PrefsPage;

import Debrief.GUI.Views.CoreCoordinateRecorder;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Layers;

public class CoordinateRecorder extends CoreCoordinateRecorder

{
  public CoordinateRecorder(final Layers layers,
      final PlainProjection plainProjection,
      final TimeControlPreferences timePreferences)
  {
    super(layers, plainProjection, timePreferences.getAutoInterval()
        .getMillis(), timePreferences.getSmallStep().getMillis());
  }

  private String getMasterTemplateFile()
  {
    String templateFile = CorePlugin.getDefault().getPreferenceStore()
        .getString(PrefsPage.PreferenceConstants.PPT_TEMPLATE);
    if (templateFile == null || templateFile.isEmpty())
    {
      templateFile = CorePlugin.getDefault().getPreferenceStore()
          .getDefaultString(PrefsPage.PreferenceConstants.PPT_TEMPLATE);
    }
    return templateFile;
  }

  private String getNewFileName(final String fileName,
      final String recordingStartTime)
  {
    String newName = fileName;
    final String[] fileNameParts = fileName.split("-");
    if (fileNameParts.length > 0)
    {
      newName = fileNameParts[0] + "-" + recordingStartTime;
    }
    if (fileName.matches("^.*_\\d+$"))
    {
      int fileNameIncr = Integer.valueOf(fileName.substring(fileName
          .lastIndexOf("_") + 1));
      newName += "_" + (++fileNameIncr);
    }
    else
    {
      newName += "_1";
    }
    return newName;
  }

  @Override
  protected void openFile(final String filename)
  {
    CorePlugin.logError(IStatus.INFO, "Opening file:" + filename, null);
    final boolean worked = Program.launch(filename);
    CorePlugin.logError(IStatus.INFO, "Open file result:" + worked, null);
  }

  @Override
  public ExportDialogResult showExportDialog()
  {
    final ExportDialogResult retVal = new ExportDialogResult();
    Display.getDefault().syncExec(new Runnable()
    {

      @Override
      public void run()
      {
        final ExportPPTDialog exportDialog = new ExportPPTDialog(Display
            .getDefault().getActiveShell());

        // fix the filename
        final String exportLocation = exportDialog.getExportLocation();
        String fileName = exportDialog.getFileName() + "-" + startTime;

        if (exportLocation != null && !"".equals(exportLocation))
        {
          final String filePath = exportDialog.getFileToExport(fileName);
          final File f = new File(filePath);
          if (f.exists())
          {
            fileName = getNewFileName(fileName, startTime);
          }
        }
        exportDialog.setFileName(fileName);

        // clear startTime text, we don't need it any more
        startTime = null;

        // show the dialog
        if (exportDialog.open() == Window.OK)
        {
          final String exportFile = exportDialog.getFileToExport(null);
          final String masterTemplateFile = getMasterTemplateFile();
          retVal.setMasterTemplate(masterTemplateFile);
          retVal.setFileName(fileName);
          retVal.setOpenOnComplete(exportDialog.getOpenOncomplete());
          retVal.setSelectedFile(exportFile);
          retVal.setStatus(true);
        }
      }
    });
    return retVal;
  }

  @Override
  protected void showMessageDialog(final String message)
  {
    MessageDialog.open(MessageDialog.INFORMATION, Display.getDefault()
        .getActiveShell(), "Export", message, MessageDialog.INFORMATION);
  }
}
