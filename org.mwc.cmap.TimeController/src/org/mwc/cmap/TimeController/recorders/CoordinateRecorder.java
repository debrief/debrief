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

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Views.CoreCoordinateRecorder;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Layers;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class CoordinateRecorder extends CoreCoordinateRecorder

{
  public CoordinateRecorder(final Layers layers,
      final PlainProjection plainProjection,
      final TimeControlPreferences timePreferences)
  {
    super(layers, plainProjection, timePreferences.getAutoInterval()
        .getMillis(), timePreferences.getSmallStep().getMillis(),
        timePreferences.getDTGFormat());
  }

  private static String getMasterTemplateFile()
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

  private static String getNewFileName(final String fileName,
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

        // check we don't get invalid characters in the string
        // we're using for the filename
        final String tidyName = tidyString(startTime);

        String fileName = exportDialog.getFileName() + "-" + tidyName;

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
          retVal.setScaleBarVisible(exportDialog.isScaleBarVisible());
          retVal.setScaleBarUnit(exportDialog.getScaleBarUnit());
          retVal.setSelectedFile(exportFile);
          retVal.setStatus(true);
        }
        // if cancelled, then stop recording.
        else
        {
          retVal.setStatus(false);
          retVal.setOpenOnComplete(false);
          retVal.setSelectedFile(null);
        }
      }
    });
    return retVal;
  }

  private static String tidyString(String startTime)
  {
    if (startTime != null)
    {
      return startTime.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }
    return startTime;
  }

  @Override
  protected void showMessageDialog(final String message)
  {
    Display.getDefault().asyncExec(new Runnable()
    {

      @Override
      public void run()
      {
        MessageDialog.open(MessageDialog.INFORMATION, Display.getDefault()
            .getActiveShell(), "Export", message, MessageDialog.INFORMATION);
      }
    });

  }

  public static class CoordinateRecorderTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    private static final String collingwood = "COLLINGWOOD";
    private static final String nelson = "NELSON";

    public CoordinateRecorder getRecorder()
    {
      final String testFilePath =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/offset_times.rep";

      // ok, now try to read it in
      final Layers _theLayers = new Layers();

      MWC.Utilities.ReaderWriter.ImportManager.addImporter(
          new Debrief.ReaderWriter.Replay.ImportReplay());
      // add the REP importer
      MWC.Utilities.ReaderWriter.ImportManager.importThis(testFilePath,
          _theLayers);

      /*
       * final PlainProjection projection = new org.mwc.cmap.gt2plot.proj.GtProjection();
       */
      final PlainProjection projection =
          new MWC.Algorithms.Projections.FlatProjection();
      projection.setScreenArea(new java.awt.Dimension(1443, 901));
      projection.setDataArea(new WorldArea(new WorldLocation(22.238965795584505,
          -21.928244631862952, 0), new WorldLocation(22.238965795584505,
              -21.43985414608609, 0)));

      final org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties timePreferences =
          new org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties();

      CoordinateRecorder recorder = new CoordinateRecorder(_theLayers,
          projection, timePreferences);

      return recorder;
    }

    /**
     * COLLINGWOOD starts after NELSON
     */
    public void testPrimaryStartsFirst()
    {
      CoordinateRecorder recorder = getRecorder();

      MWC.GenericData.HiResDate currentTime = new MWC.GenericData.HiResDate(
          818748540000L);

      final int AMOUNT_OF_STEPS = 3;
      doIteration(recorder, currentTime, AMOUNT_OF_STEPS);

      final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track =
          recorder._tracks;
      checkTrackSize(track);
      assertEquals("correct amount of steps skipped in collingwood", 1, track
          .get(collingwood).getStepsToSkip());
      assertEquals("correct amount of segments for collingwood", 2, track.get(
          collingwood).getPoints().size());
      assertEquals("correct amount of steps skipped for nelson", 0, track.get(
          nelson).getStepsToSkip());
      assertEquals("correct amount of tracks for nelson", 3, track.get(nelson)
          .getPoints().size());
      Application.logError2(Application.INFO,
          "Recording Test Passed (Primary Starting First)", null);
    }

    private static void checkTrackSize(
        final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track)
    {
      assertEquals("correct amount of tracks", 2, track.size());
      assertTrue("tracks contain collingwood", track.containsKey(collingwood));
      assertTrue("tracks contain nelson", track.containsKey(nelson));
    }

    private static void doIteration(CoordinateRecorder recorder,
        final MWC.GenericData.HiResDate currentTime_in,
        final int AMOUNT_OF_STEPS)
    {
      MWC.GenericData.HiResDate currentTime = currentTime_in;
      recorder.startStepping(currentTime);

      long timeDelta = 60000; // 1 min.
      for (int i = 0; i < AMOUNT_OF_STEPS; i++)
      {
        recorder.newTime(currentTime);
        currentTime = new MWC.GenericData.HiResDate(currentTime.getMicros()
            / 1000L + timeDelta);
      }
    }

    /**
     * COLLINGWOOD starts with NELSON
     */
    public void testPrimaryStartsWithSecondary()
    {
      CoordinateRecorder recorder = getRecorder();

      MWC.GenericData.HiResDate currentTime = new MWC.GenericData.HiResDate(
          818748600000L);

      final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track =
          twoStepsCheckCollingwood(recorder, currentTime);
      assertEquals("correct amount of steps skipped in nelson's track", 0, track
          .get(nelson).getStepsToSkip());
      assertEquals("correct amount of points in collingwood's segment", 2, track
          .get(nelson).getPoints().size());
      Application.logError2(Application.INFO,
          "Recording Test Passed (Starting at the same time)", null);
    }

    private static
        java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track>
        twoStepsCheckCollingwood(CoordinateRecorder recorder,
            MWC.GenericData.HiResDate currentTime)
    {
      final int AMOUNT_OF_STEPS = 2;
      doIteration(recorder, currentTime, AMOUNT_OF_STEPS);

      final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track =
          recorder._tracks;
      checkTrackSize(track);
      assertEquals("correct number of skipped steps in collingwood's track", 0,
          track.get(collingwood).getStepsToSkip());
      assertEquals("correct number of points in collingwood's segment", 2, track
          .get(collingwood).getPoints().size());
      return track;
    }

    /**
     * COLLINGWOOD ends before NELSON
     */
    public void testPrimaryEndsBeforeSecondary()
    {
      CoordinateRecorder recorder = getRecorder();

      MWC.GenericData.HiResDate currentTime = new MWC.GenericData.HiResDate(
          818764200000L);

      final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track =
          twoStepsCheckCollingwood(recorder, currentTime);

      assertEquals("correct number of skipped steps in nelson's track", 0, track
          .get(nelson).getStepsToSkip());
      assertEquals("correct number of points in track's segment", 1, track.get(
          nelson).getPoints().size());
      Application.logError2(Application.INFO,
          "Recording Test Passed (Starting at the same time)", null);
    }
  }
}
