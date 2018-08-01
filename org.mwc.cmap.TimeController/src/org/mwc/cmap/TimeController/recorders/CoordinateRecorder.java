package org.mwc.cmap.TimeController.recorders;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.TimeController.wizards.ExportPPTDialog;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.debrief.core.preferences.PrefsPage;

import Debrief.ReaderWriter.Replay.ImportReplay;
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
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.NarrativeEntry;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import net.lingala.zip4j.exception.ZipException;

public class CoordinateRecorder 

{
  private final Layers _myLayers;
  private final PlainProjection _projection;
  final private Map<String,Track> _tracks = new HashMap<>();
  final private List<String> _times = new ArrayList<String>();
  private boolean _running = false;
  private final TimeControlPreferences _timePrefs;
  final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd'T'HH:mm:ss'Z'");
  public static final String PREF_PPT_EXPORT_LOCATION="pptExportLocation";
  public static final String PREF_PPT_EXPORT_FILENAME="pptExportFilename";
  public static final String PREF_PPT_EXPORT_FILEFORMAT="pptExportFormat"; 
  private String startTime = null;
  
  public CoordinateRecorder(final Layers layers,
      final PlainProjection plainProjection,TimeControlPreferences timePreferences)
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
    String time =FormatRNDateTime.toMediumString(timeNow.getDate().getTime()); 
    if(startTime==null) {
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
          if(tp == null) {
            tp = new Track(track.getName(),track.getColor());
            _tracks.put(track.getName(), tp);
          }
          final Point point = _projection.toScreen(fix.getLocation());
          final double screenHeight = _projection.getScreenArea().getHeight();
          final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(fix.getCourseDegs());
          final double speedYps = new WorldSpeed(fix.getSpeed(),
          WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec)/3;
          TrackPoint trackPoint = new TrackPoint();
          trackPoint.setCourse((float)courseRads);
          trackPoint.setSpeed((float)speedYps);
          trackPoint.setLatitude((float)(screenHeight - point.getY()));
          trackPoint.setLongitude((float)point.getX());
          trackPoint.setElevation((float)fix.getLocation().getDepth());
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
  }

  public void stopStepping(final HiResDate now)
  {
    _running = false;
    
    // output timestamps
    for(String time:_times)
    {
      System.out.println(time);
    }
    
    String exportLocation = PlatformUI.getPreferenceStore().getString(PREF_PPT_EXPORT_LOCATION);
    String fileName = PlatformUI.getPreferenceStore().getString(PREF_PPT_EXPORT_FILENAME);
    String fileFormat = PlatformUI.getPreferenceStore().getString(PREF_PPT_EXPORT_FILEFORMAT);
    List<Track> list = new ArrayList<Track>();
    list.addAll(_tracks.values());
    Dimension dims = _projection.getScreenArea();
    long interval =  _timePrefs.getAutoInterval().getMillis();
    System.out.println(dims);
    System.out.println(interval);
    // output tracks object.
    //showDialog now
    ExportPPTDialog exportDialog = new ExportPPTDialog(Display.getDefault().getActiveShell());
    if(fileName == null || "".equals(fileName)) {
      exportDialog.setFileName("DebriefExport-"+startTime);
    }
    else {
      if(exportLocation!=null && !"".equals(exportLocation)) {
        File f = new File(exportLocation+File.separator+fileName+"."+fileFormat);
        if(f.exists()) {
          fileName = getNewFileName(fileName,startTime);
        }
      }
      exportDialog.setFileName(fileName+"-"+startTime);
    }
    exportDialog.setExportLocation(exportLocation);
    if(fileFormat==null || "".equals(fileFormat)){
      fileFormat="PPTX";
    }
    exportDialog.setFileFormat(fileFormat);
    if(exportDialog.open() == Window.OK) {
      exportLocation = exportDialog.getExportLocation();
      fileName = exportDialog.getFileName();
      String fileNameToSave=getFileNameStem(fileName);
      fileFormat = exportDialog.getFileFormat();
      PlatformUI.getPreferenceStore().setValue(PREF_PPT_EXPORT_LOCATION,exportLocation);
      PlatformUI.getPreferenceStore().setValue(PREF_PPT_EXPORT_FILENAME,fileNameToSave);
      PlatformUI.getPreferenceStore().setValue(PREF_PPT_EXPORT_FILEFORMAT,fileFormat);
      startTime=null;
      TrackData td = new TrackData();
      td.setName(fileName);
      td.setIntervals((int)interval);
      td.setWidth(_projection.getScreenArea().width);
      td.setHeight(_projection.getScreenArea().height);
      td.getTracks().addAll(_tracks.values());
      storeNarrativesInto(td.getNarrativeEntries(), _myLayers, _tracks);
      PlotTracks plotTracks = new PlotTracks();
      String exportFile = getFileToExport(exportLocation, fileName, fileFormat);
      String masterTemplate = getMasterTemplateFile();
      try {
        String exportedFile = plotTracks.export(td, masterTemplate , exportFile);
        MessageDialog.open(MessageDialog.INFORMATION, Display.getDefault().getActiveShell(), "PowerPoint Export", "The file is exported to:"+exportedFile, MessageDialog.INFORMATION);
        
      }catch(IOException ie) {
        MessageDialog.open(MessageDialog.ERROR, Display.getDefault().getActiveShell(), "Error", "Error exporting to powerpoint", MessageDialog.ERROR);
        CorePlugin.logError(IStatus.ERROR,
            "Host track for TMA leg can't be determined", ie);
      }catch(ZipException ze) {
        MessageDialog.open(MessageDialog.ERROR, Display.getDefault().getActiveShell(), "Error", "Error exporting to powerpoint", MessageDialog.ERROR);
        CorePlugin.logError(IStatus.ERROR,
            "Host track for TMA leg can't be determined", ze);
      }catch(DebriefException de) {
        MessageDialog.open(MessageDialog.ERROR, Display.getDefault().getActiveShell(), "Error", "Error exporting to powerpoint", MessageDialog.ERROR);
        CorePlugin.logError(IStatus.ERROR,
            "Host track for TMA leg can't be determined", de);
      }
    }    
  }

  private void storeNarrativesInto(ArrayList<ExportNarrativeEntry> narrativeEntries,
      Layers layers, Map<String, Track> tracks)
  {
    // look for a narratives layer
    Layer narratives = layers.findLayer(ImportReplay.NARRATIVE_LAYER);
    if(narratives != null)
    {
      Date firstTime = null;
      Date lastTime = null;
      
      // ok, get the bounding time period
      for(Track track: tracks.values())
      {
        ArrayList<TrackPoint> segs = track.getSegments();
        for(TrackPoint point: segs)
        {
          Date thisTime = point.getTime();
          if(firstTime == null)
          {
            firstTime = thisTime;
            lastTime = thisTime;
          }
          else
          {
            firstTime = thisTime.getTime() < firstTime.getTime() ? thisTime : firstTime;
            lastTime = thisTime.getTime() > lastTime.getTime() ? thisTime : lastTime;
          }
        }
      }
      
      TimePeriod period = new TimePeriod.BaseTimePeriod(new HiResDate(firstTime), new HiResDate(lastTime));
      
      Enumeration<Editable> nIter = narratives.elements();
      while(nIter.hasMoreElements())
      {
        NarrativeEntry entry = (NarrativeEntry) nIter.nextElement();
        if(period.contains(new HiResDate(entry.getDTG())))
        {
          String dateStr = null;
          String elapsed = null;
          // ok, create a narrative entry for it
          ExportNarrativeEntry newE = new ExportNarrativeEntry(entry.getEntry(),dateStr, elapsed, entry.getDTG().getDate());
          
          // and store it
          narrativeEntries.add(newE);
        }
      }
    }
  }

  private String getMasterTemplateFile() {
    String templateFile = CorePlugin.getDefault().getPreferenceStore().getString(PrefsPage.PreferenceConstants.PPT_TEMPLATE);
    if(templateFile==null || templateFile.isEmpty()) {
      templateFile = CorePlugin.getDefault().getPreferenceStore().getString(PrefsPage.PreferenceConstants.PPT_TEMPLATE);
    }
    return templateFile;
  }
  private String getFileToExport(String exportLocation,String fileName,String fileFormat) {
    return exportLocation+File.separator+fileName+"."+fileFormat;
  }
  private String getFileNameStem(String fileName2)
  {
    String newName;
    if (fileName2.indexOf("-") != -1)
    {
      newName = fileName2.substring(0, fileName2.lastIndexOf("-"));
    }
    else
    {
      newName = fileName2;
    }
    return newName;
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
