package org.mwc.cmap.TimeController.recorders;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.TimeController.wizards.ExportPPTDialog;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.debrief.core.preferences.PrefsPage;

import Debrief.ReaderWriter.powerPoint.DebriefException;
import Debrief.ReaderWriter.powerPoint.PlotTracks;
import Debrief.ReaderWriter.powerPoint.model.ExportNarrativeEntry;
import Debrief.ReaderWriter.powerPoint.model.Track;
import Debrief.ReaderWriter.powerPoint.model.TrackData;
import Debrief.ReaderWriter.powerPoint.model.TrackPoint;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.OperateFunction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.NarrativeEntry;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import net.lingala.zip4j.exception.ZipException;

public class CoordinateRecorder

{
  private final Layers _myLayers;
  private final PlainProjection _projection;
  final private Map<String, Track> _tracks = new HashMap<>();
  final private List<String> _times = new ArrayList<String>();
  private boolean _running = false;
  private final TimeControlPreferences _timePrefs;

  private String startTime = null;
  private long _startMillis;

  public CoordinateRecorder(final Layers layers,
      final PlainProjection plainProjection,
      TimeControlPreferences timePreferences)
  {
    _myLayers = layers;
    _projection = plainProjection;
    _timePrefs = timePreferences;
  }

  public void newTime(final HiResDate timeNow)
  {
    if (!_running)
      return;

    // get the new time.
    String time = FormatRNDateTime.toMediumString(timeNow.getDate().getTime());
    if (startTime == null)
    {
      startTime = time;
    }
    _times.add(time);

    OperateFunction outputIt = new OperateFunction()
    {

      @Override
      public void operateOn(final Editable item)
      {
        final TrackWrapper track = (TrackWrapper) item;
        final Watchable[] items = track.getNearestTo(timeNow);
        if (items != null && items.length > 0)
        {
          final FixWrapper fix = (FixWrapper) items[0];
          Track tp = _tracks.get(track.getName());
          if (tp == null)
          {
            tp = new Track(track.getName(), track.getColor());
            _tracks.put(track.getName(), tp);
          }
          final Point point = _projection.toScreen(fix.getLocation());
          final double screenHeight = _projection.getScreenArea().getHeight();
          final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(fix
              .getCourseDegs());
          final double speedYps = new WorldSpeed(fix.getSpeed(), WorldSpeed.Kts)
              .getValueIn(WorldSpeed.ft_sec) / 3;
          TrackPoint trackPoint = new TrackPoint();
          trackPoint.setCourse((float) courseRads);
          trackPoint.setSpeed((float) speedYps);
          trackPoint.setLatitude((float) (screenHeight - point.getY()));
          trackPoint.setLongitude((float) point.getX());
          trackPoint.setElevation((float) fix.getLocation().getDepth());
          trackPoint.setTime(fix.getDTG().getDate());
          tp.getSegments().add(trackPoint);
        }
      }
    };
    _myLayers.walkVisibleItems(TrackWrapper.class, outputIt);
  }

  public void startStepping(final HiResDate now)
  {
    _tracks.clear();
    _times.clear();
    _running = true;
    _startMillis = now.getDate().getTime();
  }

  public void stopStepping(final HiResDate now)
  {
    _running = false;

    List<Track> list = new ArrayList<Track>();
    list.addAll(_tracks.values());
    long interval = _timePrefs.getAutoInterval().getMillis();
    // output tracks object.
    // showDialog now
    ExportPPTDialog exportDialog = new ExportPPTDialog(Display.getDefault()
        .getActiveShell());
    
    // fix the filename
    String exportLocation = exportDialog.getExportLocation();
    String fileName = exportDialog.getFileName() + "-" + startTime;
    
    if (exportLocation != null && !"".equals(exportLocation))
    {
      String filePath = exportDialog.getFileToExport(fileName);
      File f = new File(filePath);
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
      
      // collate the data object
      TrackData td = new TrackData();
      td.setName(fileName);
      td.setIntervals((int) interval);
      td.setWidth(_projection.getScreenArea().width);
      td.setHeight(_projection.getScreenArea().height);
      td.getTracks().addAll(_tracks.values());
      storeNarrativesInto(td.getNarrativeEntries(), _myLayers, _tracks,
          _startMillis, _timePrefs);
      
      // start export
      PlotTracks plotTracks = new PlotTracks();
      String exportFile = exportDialog.getFileToExport(null);
      String masterTemplate = getMasterTemplateFile();
      try
      {
        String exportedFile = plotTracks.export(td, masterTemplate, exportFile);

        // do we open resulting file?
        if (exportDialog.getOpenOncomplete())
        {
          CorePlugin.logError(Status.INFO, "Opening file:" + exportedFile,
              null);
          boolean worked = Program.launch(exportedFile);
          CorePlugin.logError(Status.INFO, "Open file result:" + worked, null);
        }
        else
        {
          MessageDialog.open(MessageDialog.INFORMATION, Display.getDefault()
              .getActiveShell(), "PowerPoint Export", "File exported to:"
                  + exportedFile, MessageDialog.INFORMATION);
        }
      }
      catch (IOException ie)
      {
        MessageDialog.open(MessageDialog.ERROR, Display.getDefault()
            .getActiveShell(), "Error",
            "Error exporting to powerpoint (File access problem)",
            MessageDialog.ERROR);
        CorePlugin.logError(IStatus.ERROR, "During export to PPTX", ie);
      }
      catch (ZipException ze)
      {
        MessageDialog.open(MessageDialog.ERROR, Display.getDefault()
            .getActiveShell(), "Error",
            "Error exporting to powerpoint (Unable to extract ZIP)",
            MessageDialog.ERROR);
        CorePlugin.logError(IStatus.ERROR, "During export to PPTX", ze);
      }
      catch (DebriefException de)
      {
        MessageDialog.open(MessageDialog.ERROR, Display.getDefault()
            .getActiveShell(), "Error",
            "Error exporting to powerpoint (template may be corrupt).\n" + de.getMessage(),
            MessageDialog.ERROR);
        CorePlugin.logError(IStatus.ERROR, "During export to PPTX", de);
      }
    }
  }

  private static void storeNarrativesInto(
      ArrayList<ExportNarrativeEntry> narrativeEntries, Layers layers,
      Map<String, Track> tracks, final long startTime,
      TimeControlPreferences timePrefs)
  {
    // look for a narratives layer
    Layer narratives = layers.findLayer(NarrativeEntry.NARRATIVE_LAYER);
    if (narratives != null)
    {
      Date firstTime = null;
      Date lastTime = null;

      // ok, get the bounding time period
      for (Track track : tracks.values())
      {
        ArrayList<TrackPoint> segs = track.getSegments();
        for (TrackPoint point : segs)
        {
          Date thisTime = point.getTime();
          if (firstTime == null)
          {
            firstTime = thisTime;
            lastTime = thisTime;
          }
          else
          {
            firstTime = thisTime.getTime() < firstTime.getTime() ? thisTime
                : firstTime;
            lastTime = thisTime.getTime() > lastTime.getTime() ? thisTime
                : lastTime;
          }
        }
      }

      // what's the real world time step?
      final long worldIntervalMillis = timePrefs.getAutoInterval().getMillis();
      // and the model world time step?
      final long modelIntervalMillis = timePrefs.getSmallStep().getMillis();

      TimePeriod period = new TimePeriod.BaseTimePeriod(new HiResDate(
          firstTime), new HiResDate(lastTime));

      // sort out a scale factor
      final double scale = ((double) worldIntervalMillis)
          / ((double) modelIntervalMillis);

      final SimpleDateFormat df = new GMTDateFormat("ddHHmm.ss");

      Enumeration<Editable> nIter = narratives.elements();
      while (nIter.hasMoreElements())
      {
        NarrativeEntry entry = (NarrativeEntry) nIter.nextElement();
        if (period.contains(new HiResDate(entry.getDTG())))
        {
          String dateStr = df.format(entry.getDTG().getDate());
          String elapsedStr = null;

          final double elapsed = entry.getDTG().getDate().getTime() - startTime;
          final double scaled = elapsed * scale;
          elapsedStr = "" + (long) scaled;

          // ok, create a narrative entry for it
          ExportNarrativeEntry newE = new ExportNarrativeEntry(entry.getEntry(),
              dateStr, elapsedStr, entry.getDTG().getDate());

          // and store it
          narrativeEntries.add(newE);
        }
      }
    }
  }

  private String getMasterTemplateFile()
  {
    String templateFile = CorePlugin.getDefault().getPreferenceStore()
        .getString(PrefsPage.PreferenceConstants.PPT_TEMPLATE);
    if (templateFile == null || templateFile.isEmpty())
    {
      templateFile = CorePlugin.getDefault().getPreferenceStore().getString(
          PrefsPage.PreferenceConstants.PPT_TEMPLATE);
    }
    return templateFile;
  }

  private String getNewFileName(final String fileName,
      final String recordingStartTime)
  {
    String newName = fileName;
    String[] fileNameParts = fileName.split("-");
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

  public boolean isRecording()
  {
    return _running;
  }
}
