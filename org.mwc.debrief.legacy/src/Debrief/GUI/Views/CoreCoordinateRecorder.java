/**
 *
 */
package Debrief.GUI.Views;

import java.awt.Point;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Debrief.ReaderWriter.powerPoint.DebriefException;
import Debrief.ReaderWriter.powerPoint.PlotTracks;
import Debrief.ReaderWriter.powerPoint.model.ExportNarrativeEntry;
import Debrief.ReaderWriter.powerPoint.model.Track;
import Debrief.ReaderWriter.powerPoint.model.TrackData;
import Debrief.ReaderWriter.powerPoint.model.TrackPoint;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
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
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import net.lingala.zip4j.exception.ZipException;

/**
 * @author Ayesha
 *
 */
public abstract class CoreCoordinateRecorder
{

  public static class ExportDialogResult
  {
    private boolean status;
    private String selectedFile;
    private String fileName;
    private String masterTemplate;
    private boolean openOnComplete;

    public String getFileName()
    {
      return fileName;
    }

    public String getMasterTemplate()
    {
      return masterTemplate;
    }

    public String getSelectedFile()
    {
      return selectedFile;
    }

    public boolean getStatus()
    {
      return status;
    }

    public boolean isOpenOnComplete()
    {
      return openOnComplete;
    }

    public void setFileName(final String fileName)
    {
      this.fileName = fileName;
    }

    public void setMasterTemplate(final String masterTemplate)
    {
      this.masterTemplate = masterTemplate;
    }

    public void setOpenOnComplete(final boolean openOnComplete)
    {
      this.openOnComplete = openOnComplete;
    }

    public void setSelectedFile(final String selectedFile)
    {
      this.selectedFile = selectedFile;
    }

    public void setStatus(final boolean status)
    {
      this.status = status;
    }
  }

  public static class ExportResult
  {
    private String errorMessage;
    private String exportedFile;

    public String getErrorMessage()
    {
      return errorMessage;
    }

    public String getExportedFile()
    {
      return exportedFile;
    }

    public void setErrorMessage(final String errorMessage)
    {
      this.errorMessage = errorMessage;
    }

    public void setExportedFile(final String exportedFile)
    {
      this.exportedFile = exportedFile;
    }
  }

  private final Layers _myLayers;
  private final PlainProjection _projection;
  final private Map<String, Track> _tracks = new HashMap<>();
  final private List<String> _times = new ArrayList<String>();
  private boolean _running = false;
  protected String startTime = null;
  private long _startMillis;

  private final long _worldIntervalMillis;

  private final long _modelIntervalMillis;
  private final DateFormat _dateFormat;

  public CoreCoordinateRecorder(final Layers layers,
      final PlainProjection plainProjection, final long worldIntervalMillis,
      final long modelIntervalMillis, final String dateFormat)
  {
    _myLayers = layers;
    _projection = plainProjection;
    _worldIntervalMillis = worldIntervalMillis;
    _modelIntervalMillis = modelIntervalMillis;
    _dateFormat = new GMTDateFormat(dateFormat);
  }

  private ExportResult exportFile(final String fileName,
      final String exportFile, final String masterTemplateFile,
      final long interval)
  {
    final ExportResult retVal = new ExportResult();
    final TrackData td = new TrackData();
    td.setName(fileName);
    td.setIntervals((int) interval);
    td.setWidth(_projection.getScreenArea().width);
    td.setHeight(_projection.getScreenArea().height);
    td.getTracks().addAll(_tracks.values());
    storeNarrativesInto(td.getNarrativeEntries(), _myLayers, _tracks,
        _startMillis);

    // start export
    final PlotTracks plotTracks = new PlotTracks();
    String errorMessage = null;
    String exportedFile = null;
    try
    {
      exportedFile = plotTracks.export(td, masterTemplateFile, exportFile);
    }
    catch (final IOException ie)
    {
      errorMessage = "Error exporting to powerpoint (File access problem)";
      Trace.trace(ie, errorMessage);
    }
    catch (final ZipException ze)
    {
      errorMessage = "Error exporting to powerpoint (Unable to extract ZIP)";
      Trace.trace(ze, errorMessage);
    }
    catch (final DebriefException de)
    {
      errorMessage =
          "Error exporting to powerpoint (template may be corrupt).\n" + de
              .getMessage();
      Trace.trace(de, errorMessage);
    }
    retVal.setErrorMessage(errorMessage);
    retVal.setExportedFile(exportedFile);
    return retVal;
  }

  public boolean isRecording()
  {
    return _running;
  }

  public void newTime(final HiResDate timeNow)
  {
    if (!_running)
      return;

    // get the new time.
    final String time = _dateFormat.format(timeNow.getDate());
    if (startTime == null)
    {
      startTime = time;
    }
    _times.add(time);

    final OperateFunction outputIt = new OperateFunction()
    {

      @Override
      public void operateOn(final Editable item)
      {
        final LightweightTrackWrapper track = (LightweightTrackWrapper) item;
        final Watchable[] items = track.getNearestTo(timeNow);
        if (items != null && items.length > 0 && items[0] != null)
        {
          final FixWrapper fix = (FixWrapper) items[0];
          Track tp = _tracks.get(track.getName());
          if (tp == null)
          {
            // the _times list will have received a value before
            // we get called, so we decrement by one.
            int waitingSteps = _times.size() - 1;
            tp = new Track(track.getName(), track.getColor(), waitingSteps);
            _tracks.put(track.getName(), tp);
          }
          final Point point = _projection.toScreen(fix.getLocation());
          final double screenHeight = _projection.getScreenArea().getHeight();
          final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(fix
              .getCourseDegs());
          final double speedYps = new WorldSpeed(fix.getSpeed(), WorldSpeed.Kts)
              .getValueIn(WorldSpeed.ft_sec) / 3;
          final TrackPoint trackPoint = new TrackPoint();
          trackPoint.setCourse((float) courseRads);
          trackPoint.setSpeed((float) speedYps);
          trackPoint.setLatitude((float) (screenHeight - point.getY()));
          trackPoint.setLongitude((float) point.getX());
          trackPoint.setElevation((float) fix.getLocation().getDepth());
          trackPoint.setTime(fix.getDTG().getDate());
          trackPoint.setFormattedTime(_times.get(_times.size() - 1));
          tp.getSegments().add(trackPoint);
        }
      }
    };
    _myLayers.walkVisibleItems(LightweightTrackWrapper.class, outputIt);
  }

  protected abstract void openFile(String filename);

  public abstract ExportDialogResult showExportDialog();

  protected abstract void showMessageDialog(String message);

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

    if (_tracks.values().size() > PlotTracks.MARKER_FOOTPRINT_DELTA)
    {
      // export failed.
      MWC.GUI.Dialogs.DialogFactory.showMessage("Export to PPTX Errors",
          "There are too many tracks. No more than "
              + PlotTracks.MARKER_FOOTPRINT_DELTA
              + " can be exported in the same presentation file.");
    }
    else
    {

      final List<Track> list = new ArrayList<Track>();
      list.addAll(_tracks.values());
      final long interval = _worldIntervalMillis;
      // output tracks object.
      // showDialog now
      final ExportDialogResult exportResult = showExportDialog();
      // collate the data object
      if (exportResult.getStatus())
      {
        final ExportResult expResult = exportFile(exportResult.fileName,
            exportResult.selectedFile, exportResult.masterTemplate, interval);

        if (expResult.errorMessage == null)
        {
          // do we open resulting file?
          if (exportResult.openOnComplete)
          {
            openFile(expResult.exportedFile);
          }
          else
          {
            showMessageDialog("File exported to:" + expResult.exportedFile);
          }
        }
        else
        {
          // export failed.
          MWC.GUI.Dialogs.DialogFactory.showMessage("Export to PPTX Errors",
              "Exporting to PPTX failed. See error log for more details");
        }
      }
    }
  }

  private void storeNarrativesInto(
      final ArrayList<ExportNarrativeEntry> narrativeEntries,
      final Layers layers, final Map<String, Track> tracks,
      final long startTime)
  {
    // look for a narratives layer
    final Layer narratives = layers.findLayer(NarrativeEntry.NARRATIVE_LAYER);
    if (narratives != null)
    {
      Date firstTime = null;
      Date lastTime = null;

      // ok, get the bounding time period
      for (final Track track : tracks.values())
      {
        final ArrayList<TrackPoint> segs = track.getSegments();
        for (final TrackPoint point : segs)
        {
          final Date thisTime = point.getTime();
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
      // final long worldIntervalMillis = timePrefs.getAutoInterval().getMillis();
      // and the model world time step?
      // final long modelIntervalMillis = timePrefs.getSmallStep().getMillis();

      final TimePeriod period = new TimePeriod.BaseTimePeriod(new HiResDate(
          firstTime), new HiResDate(lastTime));

      // sort out a scale factor
      final double scale = ((double) _worldIntervalMillis)
          / _modelIntervalMillis;

      final SimpleDateFormat df = new GMTDateFormat("ddHHmm.ss");

      final Enumeration<Editable> nIter = narratives.elements();
      while (nIter.hasMoreElements())
      {
        final NarrativeEntry entry = (NarrativeEntry) nIter.nextElement();
        if (period.contains(new HiResDate(entry.getDTG())))
        {
          final String dateStr = df.format(entry.getDTG().getDate());
          String elapsedStr = null;

          final double elapsed = entry.getDTG().getDate().getTime() - startTime;
          final double scaled = elapsed * scale;
          elapsedStr = "" + (long) scaled;

          // ok, create a narrative entry for it
          final ExportNarrativeEntry newE = new ExportNarrativeEntry(entry
              .getEntry(), dateStr, elapsedStr, entry.getDTG().getDate());

          // and store it
          narrativeEntries.add(newE);
        }
      }
    }
  }

}
