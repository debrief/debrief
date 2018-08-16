/**
 * 
 */
package Debrief.GUI.Views;

import java.awt.Point;
import java.io.IOException;
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

/**
 * @author Ayesha
 *
 */
public abstract class CoreCoordinateRecorder
{

  private final Layers _myLayers;
  private final PlainProjection _projection;
  final private Map<String, Track> _tracks = new HashMap<>();
  final private List<String> _times = new ArrayList<String>();
  private boolean _running = false;
  protected String startTime = null;
  private long _startMillis;
  private long _worldIntervalMillis;
  private long _modelIntervalMillis;

  public CoreCoordinateRecorder(final Layers layers,
      final PlainProjection plainProjection,long worldIntervalMillis,long modelIntervalMillis)
  {
    _myLayers = layers;
    _projection = plainProjection;
    _worldIntervalMillis = worldIntervalMillis;
    _modelIntervalMillis = modelIntervalMillis;
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

  private void storeNarrativesInto(
      ArrayList<ExportNarrativeEntry> narrativeEntries, Layers layers,
      Map<String, Track> tracks, final long startTime
      )
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
      //final long worldIntervalMillis = timePrefs.getAutoInterval().getMillis();
      // and the model world time step?
      //final long modelIntervalMillis = timePrefs.getSmallStep().getMillis();

      TimePeriod period = new TimePeriod.BaseTimePeriod(new HiResDate(
          firstTime), new HiResDate(lastTime));

      // sort out a scale factor
      final double scale =  (double)(_worldIntervalMillis/_modelIntervalMillis);

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
  protected abstract void showMessageDialog(String message);
  protected abstract void openFile(String filename);
  public static class ExportDialogResult{
    private boolean status;
    private String selectedFile;
    private String fileName;
    private String masterTemplate;
    private boolean openOnComplete;

    public void setStatus(boolean status)
    {
      this.status = status;
    }
    public void setOpenOnComplete(boolean openOnComplete)
    {
      this.openOnComplete = openOnComplete;
    }
    public void setSelectedFile(String selectedFile)
    {
      this.selectedFile = selectedFile;
    }
    public void setFileName(String fileName)
    {
      this.fileName = fileName;
    }
    public void setMasterTemplate(String masterTemplate)
    {
      this.masterTemplate = masterTemplate;
    }

    public boolean getStatus() {
      return status;
    }
    public String getSelectedFile()
    {
      return selectedFile;
    }
    public boolean isOpenOnComplete()
    {
      return openOnComplete;
    }
    public String getFileName()
    {
      return fileName;
    }
    public String getMasterTemplate()
    {
      return masterTemplate;
    }
  }
  public abstract ExportDialogResult showExportDialog();

  public static class ExportResult{
    private String errorMessage;
    private String exportedFile;
    public String getExportedFile()
    {
      return exportedFile;
    }
    public void setExportedFile(String exportedFile)
    {
      this.exportedFile = exportedFile;
    }
    public String getErrorMessage()
    {
      return errorMessage;
    }
    public void setErrorMessage(String errorMessage)
    {
      this.errorMessage = errorMessage;
    }
  }
  private ExportResult exportFile(String fileName,String exportFile,String masterTemplateFile,long interval) {
    String errorMessage = null;
    ExportResult retVal = new ExportResult();
    TrackData td = new TrackData();
    td.setName(fileName);
    td.setIntervals((int) interval);
    td.setWidth(_projection.getScreenArea().width);
    td.setHeight(_projection.getScreenArea().height);
    td.getTracks().addAll(_tracks.values());
    storeNarrativesInto(td.getNarrativeEntries(), _myLayers, _tracks,
        _startMillis);

    // start export
    PlotTracks plotTracks = new PlotTracks();
    String exportedFile = null;
    try
    {
      exportedFile = plotTracks.export(td, masterTemplateFile, exportFile);
    } catch (IOException ie)
    {
      errorMessage = "Error exporting to powerpoint (File access problem)";
    }
    catch (ZipException ze)
    {
      errorMessage = "Error exporting to powerpoint (Unable to extract ZIP)";
    }
    catch (DebriefException de)
    {
      errorMessage = "Error exporting to powerpoint (template may be corrupt).\n" + de.getMessage();
    }
    retVal.setErrorMessage(errorMessage);
    retVal.setExportedFile(exportedFile);
    return retVal;
  }
  public void stopStepping(final HiResDate now)
  {
    _running = false;

    List<Track> list = new ArrayList<Track>();
    list.addAll(_tracks.values());
    final long interval = _worldIntervalMillis;
    // output tracks object.
    // showDialog now
    ExportDialogResult exportResult = showExportDialog();
    // collate the data object
    if(exportResult.getStatus()) {
      ExportResult expResult = exportFile(exportResult.fileName, exportResult.selectedFile, exportResult.masterTemplate, interval);

      if(expResult.errorMessage==null) {
        // do we open resulting file?
        if (exportResult.openOnComplete)
        {
          openFile(expResult.exportedFile);
        }
        else
        {
          showMessageDialog("File exported to:"
              + expResult.exportedFile);
        }
      }else {
        //export was cancelled, do nothing.
      }
    }
  }
  public boolean isRecording()
  {
    return _running;
  }

}
