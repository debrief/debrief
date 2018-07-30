package org.mwc.cmap.TimeController.recorders;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.TimeController.wizards.ExportPPTDialog;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Layers.OperateFunction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class CoordinateRecorder 

{
  private final Layers _myLayers;
  private final PlainProjection _projection;
  final private Map<String, TrackWrapper> _tracks =
      new HashMap<String, TrackWrapper>();
  final private List<String> _times = new ArrayList<String>();
  private boolean _running = false;
  private TimeControlPreferences _timePrefs;
  private String exportLocation;
  private String fileName;
  private String fileFormat;
  private int fileNameIncr;
  public static final String PREF_PPT_EXPORT_LOCATION="pptExportLocation";
  public static final String PREF_PPT_EXPORT_FILENAME="pptExportFilename";
  public static final String PREF_PPT_EXPORT_FILEFORMAT="pptExportFormat";
  

  private String startTime = null;
  public CoordinateRecorder(final Layers _myLayers,
      final PlainProjection plainProjection,TimeControlPreferences timePreferences)
  {
    this._myLayers = _myLayers;
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

          TrackWrapper match = _tracks.get(track.getName());
          if (match == null)
          {
            match = new TrackWrapper();
            match.setName(track.getName());
            match.setColor(track.getColor());
            _tracks.put(track.getName(), match);
          }
          final Point point = _projection.toScreen(fix.getLocation());
          final WorldLocation newLoc = new WorldLocation(point.getY(), point.getX(),
              fix.getLocation().getDepth());
          final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(fix.getCourseDegs());
          final double speedYps = new WorldSpeed(fix.getSpeed(),
          WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec)/3;
          final Fix fix2 = new Fix(timeNow, newLoc,
              courseRads,
              speedYps);
          final FixWrapper fw2 = new FixWrapper(fix2);
          match.addFix(fw2);
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
    
    exportLocation = PlatformUI.getPreferenceStore().getString(PREF_PPT_EXPORT_LOCATION);
    fileName = PlatformUI.getPreferenceStore().getString(PREF_PPT_EXPORT_FILENAME);
    fileFormat = PlatformUI.getPreferenceStore().getString(PREF_PPT_EXPORT_FILEFORMAT);
    List<TrackWrapper> list = new ArrayList<TrackWrapper>();
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
      System.out.println("export path:"+exportLocation+File.separator+fileName+"."+fileFormat);
      //export to file now and open the file
          
    }
    
  }
  private String getFileNameStem(String fileName2)
  {
    String newName;
    if(fileName2.indexOf("-")!=-1){
      newName = fileName2.substring(0,fileName2.lastIndexOf("-"));
    }
    else {
      newName = fileName2;
    }
    return newName;
  }

  private String getNewFileName(final String fileName,final String recordingStartTime) {
    String newName=fileName;
    String[] fileNameParts = fileName.split("-");
    if(fileNameParts.length>0) {
      newName = fileNameParts[0]+"-"+recordingStartTime;      
    }
    if(fileName.matches("^.*_\\d+$")) {
      int fileNameIncr = Integer.valueOf(fileName.substring(fileName.lastIndexOf("_")+1));
      newName+="_"+(++fileNameIncr);  
    }
    else {
      newName+="_1";
    }
    return newName;
    
  }
}
