package org.mwc.cmap.TimeController.recorders;

import java.io.File;
import java.util.Date;

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

    public CoordinateRecorder getRecorder()
    {
      final File testFile = new File(
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/offset_times.rep");

      // ok, now try to read it in
      final Layers _theLayers = new Layers();
      final File[] _theFiles = new File[]
      {testFile};

      // add the REP importer
      MWC.Utilities.ReaderWriter.ImportManager.addImporter(
          new Debrief.ReaderWriter.Replay.ImportReplay());

      // get our thread to import this
      final MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller reader =
          new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(
              _theFiles, _theLayers)
          {
            // handle completion of the full import process
            @Override
            public void allFilesFinished(final File[] fNames,
                final Layers newData)
            {

            }

            // handle the completion of each file
            @Override
            public void fileFinished(final File fName, final Layers newData)
            {

            }
          };

      // and start it running
      reader.start();

      // wait for the results
      while (reader.isAlive())
      {
        try
        {
          Thread.sleep(100);
        }
        catch (final java.lang.InterruptedException e)
        {
        }
      }

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

      recorder.startStepping(currentTime);

      long timeDelta = 60000; // 1 min.
      final int AMOUNT_OF_STEPS = 3;
      for (int i = 0; i < AMOUNT_OF_STEPS; i++)
      {
        recorder.newTime(currentTime);
        currentTime = new MWC.GenericData.HiResDate(currentTime.getMicros()
            / 1000L + timeDelta);
      }

      currentTime = new MWC.GenericData.HiResDate(currentTime.getMicros()
          / 1000L + timeDelta);

      final String collingwood = "COLLINGWOOD";
      final String nelson = "NELSON";

      final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track =
          recorder._tracks;
      assertTrue(track.size() == 2);
      assertTrue(track.containsKey(collingwood));
      assertTrue(track.containsKey(nelson));
      assertTrue(track.get(collingwood).getStepsToSkip() == 1);
      assertTrue(track.get(collingwood).getSegments().size() == 2);
      assertTrue("0064bd".equals(track.get(collingwood).getColorAsString()));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint firstSegmentCollingwood =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      firstSegmentCollingwood.setCourse(5.7386427f);
      firstSegmentCollingwood.setLatitude(-341.0f);
      firstSegmentCollingwood.setLongitude(969.0f);
      firstSegmentCollingwood.setSpeed(1.9691114f);
      firstSegmentCollingwood.setTime(new Date(818748600000L));
      firstSegmentCollingwood.setFormattedTime("120610");
      assertEquals(firstSegmentCollingwood, track.get(collingwood).getSegments()
          .get(0));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint secondSegmentCollingwood =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      secondSegmentCollingwood.setCourse(5.761332f);
      secondSegmentCollingwood.setLatitude(-338.0f);
      secondSegmentCollingwood.setLongitude(966.0f);
      secondSegmentCollingwood.setSpeed(1.9691114f);
      secondSegmentCollingwood.setTime(new Date(818748660000L));
      secondSegmentCollingwood.setFormattedTime("120611");
      assertEquals(secondSegmentCollingwood, track.get(collingwood)
          .getSegments().get(1));

      assertTrue(track.get(nelson).getStepsToSkip() == 0);
      assertTrue(track.get(nelson).getSegments().size() == 3);
      assertTrue("e01c3e".equals(track.get(nelson).getColorAsString()));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint firstSegmentNelson =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      firstSegmentNelson.setCourse(2.6267204f);
      firstSegmentNelson.setLatitude(20.0f);
      firstSegmentNelson.setLongitude(245.0f);
      firstSegmentNelson.setSpeed(1.1252066f);
      firstSegmentNelson.setTime(new Date(818748540000L));
      firstSegmentNelson.setFormattedTime("120609");
      assertEquals(firstSegmentNelson, track.get(nelson).getSegments().get(0));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint secondSegmentNelson =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      secondSegmentNelson.setCourse(2.6162486f);
      secondSegmentNelson.setLatitude(14.0f);
      secondSegmentNelson.setLongitude(250.0f);
      secondSegmentNelson.setSpeed(1.1252066f);
      secondSegmentNelson.setTime(new Date(818748600000L));
      secondSegmentNelson.setFormattedTime("120610");
      assertEquals(secondSegmentNelson, track.get(nelson).getSegments().get(1));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint thirdSegmentNelson =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      thirdSegmentNelson.setCourse(2.6267204f);
      thirdSegmentNelson.setLatitude(7.0f);
      thirdSegmentNelson.setLongitude(255.0f);
      thirdSegmentNelson.setSpeed(1.1252066f);
      thirdSegmentNelson.setTime(new Date(818748660000L));
      thirdSegmentNelson.setFormattedTime("120611");
      assertEquals(thirdSegmentNelson, track.get(nelson).getSegments().get(2));
      Application.logError2(Application.INFO,
          "Recording Test Passed (Primary Starting First)", null);
    }

    /**
     * COLLINGWOOD starts with NELSON
     */
    public void testPrimaryStartsWithSecondary()
    {
      CoordinateRecorder recorder = getRecorder();

      MWC.GenericData.HiResDate currentTime = new MWC.GenericData.HiResDate(
          818748600000L);

      recorder.startStepping(currentTime);

      long timeDelta = 60000; // 1 min.
      final int AMOUNT_OF_STEPS = 2;
      for (int i = 0; i < AMOUNT_OF_STEPS; i++)
      {
        recorder.newTime(currentTime);
        currentTime = new MWC.GenericData.HiResDate(currentTime.getMicros()
            / 1000L + timeDelta);
      }

      currentTime = new MWC.GenericData.HiResDate(currentTime.getMicros()
          / 1000L + timeDelta);

      final String collingwood = "COLLINGWOOD";
      final String nelson = "NELSON";

      final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track =
          recorder._tracks;
      assertTrue(track.size() == 2);
      assertTrue(track.containsKey(collingwood));
      assertTrue(track.containsKey(nelson));
      assertTrue(track.get(collingwood).getStepsToSkip() == 0);
      assertTrue(track.get(collingwood).getSegments().size() == 2);
      assertTrue("0064bd".equals(track.get(collingwood).getColorAsString()));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint firstSegmentCollingwood =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      firstSegmentCollingwood.setCourse(5.7386427f);
      firstSegmentCollingwood.setLatitude(-341.0f);
      firstSegmentCollingwood.setLongitude(969.0f);
      firstSegmentCollingwood.setSpeed(1.9691114f);
      firstSegmentCollingwood.setTime(new Date(818748600000L));
      firstSegmentCollingwood.setFormattedTime("120610");
      assertEquals(firstSegmentCollingwood, track.get(collingwood).getSegments()
          .get(0));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint secondSegmentCollingwood =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      secondSegmentCollingwood.setCourse(5.761332f);
      secondSegmentCollingwood.setLatitude(-338.0f);
      secondSegmentCollingwood.setLongitude(966.0f);
      secondSegmentCollingwood.setSpeed(1.9691114f);
      secondSegmentCollingwood.setTime(new Date(818748660000L));
      secondSegmentCollingwood.setFormattedTime("120611");
      assertEquals(secondSegmentCollingwood, track.get(collingwood)
          .getSegments().get(1));

      assertTrue(track.get(nelson).getStepsToSkip() == 0);
      assertTrue(track.get(nelson).getSegments().size() == 2);
      assertTrue("e01c3e".equals(track.get(nelson).getColorAsString()));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint firstSegmentNelson =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      firstSegmentNelson.setCourse(2.6162486f);
      firstSegmentNelson.setLatitude(14.0f);
      firstSegmentNelson.setLongitude(250.0f);
      firstSegmentNelson.setSpeed(1.1252066f);
      firstSegmentNelson.setTime(new Date(818748600000L));
      firstSegmentNelson.setFormattedTime("120610");
      assertEquals(firstSegmentNelson, track.get(nelson).getSegments().get(0));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint secondSegmentNelson =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      secondSegmentNelson.setCourse(2.6267204f);
      secondSegmentNelson.setLatitude(7.0f);
      secondSegmentNelson.setLongitude(255.0f);
      secondSegmentNelson.setSpeed(1.1252066f);
      secondSegmentNelson.setTime(new Date(818748660000L));
      secondSegmentNelson.setFormattedTime("120611");
      assertEquals(secondSegmentNelson, track.get(nelson).getSegments().get(1));
      Application.logError2(Application.INFO,
          "Recording Test Passed (Starting at the same time)", null);
    }

    /**
     * COLLINGWOOD ends before NELSON
     */
    public void testPrimaryEndsBeforeSecondary()
    {
      CoordinateRecorder recorder = getRecorder();

      MWC.GenericData.HiResDate currentTime = new MWC.GenericData.HiResDate(
          818764200000L);

      recorder.startStepping(currentTime);

      long timeDelta = 60000; // 1 min.
      final int AMOUNT_OF_STEPS = 2;
      for (int i = 0; i < AMOUNT_OF_STEPS; i++)
      {
        recorder.newTime(currentTime);
        currentTime = new MWC.GenericData.HiResDate(currentTime.getMicros()
            / 1000L + timeDelta);
      }

      currentTime = new MWC.GenericData.HiResDate(currentTime.getMicros()
          / 1000L + timeDelta);

      final String collingwood = "COLLINGWOOD";
      final String nelson = "NELSON";

      final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track =
          recorder._tracks;
      assertTrue(track.size() == 2);
      assertTrue(track.containsKey(collingwood));
      assertTrue(track.containsKey(nelson));
      assertTrue(track.get(collingwood).getStepsToSkip() == 0);
      assertTrue(track.get(collingwood).getSegments().size() == 2);
      assertTrue("0064bd".equals(track.get(collingwood).getColorAsString()));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint firstSegmentCollingwood =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      firstSegmentCollingwood.setCourse(0.13264503f);
      firstSegmentCollingwood.setLatitude(76.0f);
      firstSegmentCollingwood.setLongitude(515.0f);
      firstSegmentCollingwood.setSpeed(1.9691114f);
      firstSegmentCollingwood.setTime(new Date(818764200000L));
      firstSegmentCollingwood.setFormattedTime("121030");
      assertEquals(firstSegmentCollingwood, track.get(collingwood).getSegments()
          .get(0));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint secondSegmentCollingwood =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      secondSegmentCollingwood.setCourse(6.112143f);
      secondSegmentCollingwood.setLatitude(80.0f);
      secondSegmentCollingwood.setLongitude(516.0f);
      secondSegmentCollingwood.setSpeed(1.9691114f);
      secondSegmentCollingwood.setTime(new Date(818764260000L));
      secondSegmentCollingwood.setFormattedTime("121031");
      assertEquals(secondSegmentCollingwood, track.get(collingwood)
          .getSegments().get(1));

      assertTrue(track.get(nelson).getStepsToSkip() == 0);
      assertTrue(track.get(nelson).getSegments().size() == 1);
      assertTrue("e01c3e".equals(track.get(nelson).getColorAsString()));
      final Debrief.ReaderWriter.powerPoint.model.TrackPoint firstSegmentNelson =
          new Debrief.ReaderWriter.powerPoint.model.TrackPoint();
      firstSegmentNelson.setCourse(6.049311f);
      firstSegmentNelson.setLatitude(113.0f);
      firstSegmentNelson.setLongitude(427.0f);
      firstSegmentNelson.setSpeed(1.1252066f);
      firstSegmentNelson.setTime(new Date(818764200000L));
      firstSegmentNelson.setFormattedTime("121030");
      assertEquals(firstSegmentNelson, track.get(nelson).getSegments().get(0));
      Application.logError2(Application.INFO,
          "Recording Test Passed (Starting at the same time)", null);
    }
  }
}
