/**
 *
 */
package Debrief.GUI.Views;

import java.awt.Point;
import java.io.IOException;
import java.io.Serializable;
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
import MWC.GenericData.WorldLocation;
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
    private boolean scaleBarVisible;
    private String scaleBarUnit;

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

    public boolean isScaleBarVisible()
    {
      return scaleBarVisible;
    }

    public void setScaleBarVisible(boolean scaleBarVisible)
    {
      this.scaleBarVisible = scaleBarVisible;
    }

    public String getScaleBarUnit()
    {
      return scaleBarUnit;
    }

    public void setScaleBarUnit(String scaleBarUnit)
    {
      this.scaleBarUnit = scaleBarUnit;
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
  final protected Map<String, Track> _tracks = new HashMap<>();
  final private List<String> _times = new ArrayList<String>();
  private boolean _running = false;
  protected String startTime = null;
  private long _startMillis;

  /**
   * set of values used to doDecide on what steps to use for the scale
   */
  transient private Label_Limit _limits[];

  /**
   * the list of units types we know about (we don't remember this when serialising, we create it
   * afresh)
   */
  private static transient HashMap<String, UnitsConverter> _unitsList;

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

  /**
   * setup the list
   */
  private void initialiseLimits()
  {
    // create the array of limits values in a tmp parameter
    final Label_Limit[] tmp =
    {new Label_Limit(7, 1), new Label_Limit(20, 5), new Label_Limit(70, 10),
        new Label_Limit(200, 50), new Label_Limit(700, 100), new Label_Limit(
            2000, 500), new Label_Limit(7000, 1000), new Label_Limit(20000,
                5000), new Label_Limit(70000, 10000), new Label_Limit(200000,
                    50000), new Label_Limit(700000, 100000), new Label_Limit(
                        2000000, 500000), new Label_Limit(7000000, 1000000),
        new Label_Limit(20000000, 5000000), new Label_Limit(70000000,
            10000000)};

    // and now store the array in our local variable
    _limits = tmp;
  }

  /////////////////////////////////////////////////////////////
  // scale limits and labels from a data range
  ////////////////////////////////////////////////////////////
  class Label_Limit implements Serializable
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public long upper_limit;
    public long increment;

    Label_Limit(final long limit, final long inc)
    {
      upper_limit = limit;
      increment = inc;
    }
  }

  /**
   * setup the list of units converters
   */
  private synchronized void setupUnits()
  {

    // just check it hasn't already been generated
    if (_unitsList != null)
      return;

    // create the list itself
    _unitsList = new HashMap<String, UnitsConverter>();

    // and put in the converters
    _unitsList.put(MWC.GUI.Properties.UnitsPropertyEditor.KM_UNITS,
        new UnitsConverter()
        {
          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          public double convertThis(final double degs)
          {
            return MWC.Algorithms.Conversions.Degs2Km(degs);
          }
        });

    _unitsList.put(MWC.GUI.Properties.UnitsPropertyEditor.METRES_UNITS,
        new UnitsConverter()
        {
          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          public double convertThis(final double degs)
          {
            return MWC.Algorithms.Conversions.Degs2m(degs);
          }
        });

    _unitsList.put(MWC.GUI.Properties.UnitsPropertyEditor.NM_UNITS,
        new UnitsConverter()
        {
          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          public double convertThis(final double degs)
          {
            return MWC.Algorithms.Conversions.Degs2Nm(degs);
          }
        });

    _unitsList.put(MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS,
        new UnitsConverter()
        {
          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          public double convertThis(final double degs)
          {
            return MWC.Algorithms.Conversions.Degs2Yds(degs);
          }
        });

    _unitsList.put(MWC.GUI.Properties.UnitsPropertyEditor.KYD_UNITS,
        new UnitsConverter()
        {
          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          public double convertThis(final double degs)
          {
            return MWC.Algorithms.Conversions.Degs2Yds(degs) / 1000;
          }
        });

  }

  private ExportResult exportFile(final String fileName,
      final String exportFile, final String masterTemplateFile,
      final long interval, String scaleBarUnit, boolean scaleVisible)
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
    calculateScaleWidth(td, _projection, scaleVisible ? scaleBarUnit : "");

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

  private void calculateScaleWidth(TrackData td, PlainProjection proj,
      String scaleBarUnit)
  {
    if ("".equals(scaleBarUnit))
    {
      td.setScaleUnit("");
      td.setScaleWidth(-1);
      td.setScaleAmount(-1);
      return;
    }

    // create the list of units
    setupUnits();

    /**
     * the units to use for the scale
     */
    final UnitsConverter _DisplayUnits = _unitsList.get(scaleBarUnit);

    // find the screen width
    final java.awt.Dimension screen_size = proj.getScreenArea().getSize();
    final long screen_width = screen_size.width;

    // generate screen points in the middle on the left & right-hand sides
    final Point left = new Point(0, (int) screen_size.getHeight() / 2);
    final Point right = new Point((int) screen_width, (int) screen_size
        .getHeight() / 2);

    // and now world locations to represent them
    final WorldLocation leftLoc = new WorldLocation(proj.toWorld(left));
    final WorldLocation rightLoc = proj.toWorld(right);

    // and get the distance between them
    double data_width = rightLoc.rangeFrom(leftLoc);

    // convert this data width (in degs) to our units
    data_width = _DisplayUnits.convertThis(data_width);

    // make a guess at the scale
    final double scale = data_width / screen_width;

    // check we have our set of data
    if (_limits == null)
      initialiseLimits();

    // find the range we are working in
    int counter = 0;
    while ((counter < _limits.length)
        && (data_width > _limits[counter].upper_limit))
    {
      counter++;
    }

    // set our increment counter
    final long _scaleStep = _limits[counter].increment;

    final int tick_step = (int) (_scaleStep / scale);

    td.setScaleWidth(tick_step);
    td.setScaleAmount(_scaleStep);
    td.setScaleUnit(scaleBarUnit);
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
          final TrackPoint trackPoint = new TrackPoint((float) (screenHeight
              - point.getY()), (float) point.getX(), (float) fix.getLocation()
                  .getDepth(), fix.getDTG().getDate(), _times.get(_times.size()
                      - 1));
          tp.getPoints().add(trackPoint);
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
      final ExportDialogResult dialogResult = showExportDialog();

      // collate the data object
      if (dialogResult.getStatus())
      {
        final ExportResult expResult = exportFile(dialogResult.fileName,
            dialogResult.selectedFile, dialogResult.masterTemplate, interval,
            dialogResult.getScaleBarUnit(), dialogResult.isScaleBarVisible());

        if (expResult.errorMessage == null)
        {
          // do we open resulting file?
          if (dialogResult.openOnComplete)
          {
            openFile(expResult.exportedFile);
          }
          else
          {
            showMessageDialog("File Exported Successfully to:\n" + expResult.exportedFile);
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
        final ArrayList<TrackPoint> segs = track.getPoints();
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

  ////////////////////////////////////
  // static interior class to convert between units
  ////////////////////////////////////
  abstract private static class UnitsConverter implements Serializable
  {
    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    /**
     * convert this value to our units
     * 
     * @param degs
     * @return
     */
    abstract public double convertThis(double degs);
  }
}
