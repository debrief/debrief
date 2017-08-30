package org.mwc.debrief.track_shift.ambiguity;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.views.StackedDotHelper;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

public class AmbiguityResolver
{
  public void resolve(TrackWrapper track, Zone[] zones, TimePeriod period)
  {
    BaseLayer sensors = track.getSensors();
    
    // find the O/S legs
    
    // ok, loop through the sensors
    Enumeration<Editable> numer = sensors.elements();
    while(numer.hasMoreElements())
    {
      SensorWrapper sensor = (SensorWrapper) numer.nextElement();
      processSensor(sensor);
    }
  }
  
  public void dropCutsInTurn(TrackWrapper track, Zone[] zones, TimePeriod period)
  {
    if(zones != null && zones.length > 0)
    {
      // ok, go for it
      BaseLayer sensors = track.getSensors();
      Enumeration<Editable> numer = sensors.elements();
      while(numer.hasMoreElements())
      {
        SensorWrapper sensor = (SensorWrapper) numer.nextElement();
        final List<SensorContactWrapper> toDelete = new ArrayList<SensorContactWrapper>();
        Enumeration<Editable> cNumer = sensor.elements();
        while(cNumer.hasMoreElements())
        {
          SensorContactWrapper scw = (SensorContactWrapper) cNumer.nextElement();
          final HiResDate dtg = scw.getDTG();
          if(outOfZones(zones, dtg))
          {
            toDelete.add(scw);
          }
        }
        
        // ok, do the delete
        for(SensorContactWrapper sc: toDelete)
        {
          // ok, drop it.
          sensor.removeElement(sc);
        }
      }     
    }
  }
  
  
  private boolean outOfZones(Zone[] zones, HiResDate dtg)
  {
    final long thisLong = dtg.getDate().getTime();
    boolean found = false;
    for(Zone zone: zones)
    {
      if(zone.getStart() <= thisLong && zone.getEnd() >= thisLong)
      {
        // ok, valid.
        found = true;
        break;
      }
    }
    return !found;
  }

  private void processSensor(SensorWrapper sensor)
  {
    // work through this sensor
    @SuppressWarnings("unused")
    SensorWrapper tmpSensor = sensor;
  }


  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testImportTMA_POS extends junit.framework.TestCase
  {
    
    
    public void testSplittingAllTime() throws FileNotFoundException
    {
      TrackWrapper track = getData();
      assertNotNull("found track", track);
      
      // has sensors
      assertEquals("has sensor", 1, track.getSensors().size());
      
      SensorWrapper sensor = (SensorWrapper) track.getSensors().elements().nextElement();
      
      ColorProvider provider = new ColorProvider()
      {
        @Override
        public Color getZoneColor()
        {
          return Color.blue;
        }
      };
      TimeSeries osCourse = getOSCourse(track);
      // try to slice the O/S zones
      ArrayList<Zone> zonesList = StackedDotHelper.sliceOwnship(osCourse, provider);
      Zone[] zones = zonesList.toArray(new Zone[]{});
      
      // ok, get resolving
      AmbiguityResolver res = new AmbiguityResolver();
      
      // drop cuts in turn
      int numCuts = sensor.size();
      assertEquals("right cuts at start", 721, numCuts);
      res.dropCutsInTurn(track, zones, null);
      assertEquals("fewer cuts", 587, sensor.size());
      
      res.resolve(track, zones, null);
      
      // ok, check the data      
    }

    private TimeSeries getOSCourse(TrackWrapper track)
    {
      TimeSeries ts = new TimeSeries("OS Course");
      Enumeration<Editable> pts = track.getPositionIterator();
      while(pts.hasMoreElements())
      {
        FixWrapper fw = (FixWrapper) pts.nextElement();
        final double course = fw.getCourseDegs();
        
        final FixedMillisecond thisMilli =
            new FixedMillisecond(fw.getDateTimeGroup().getDate()
                .getTime());
        final ColouredDataItem crseBearing =
            new ColouredDataItem(thisMilli, course,
                fw.getColor(), true, null, true, true);

        
        ts.add(crseBearing);
      }
      return ts;
    }

    private TrackWrapper getData() throws FileNotFoundException
    {
      // get our sample data-file
      ImportReplay importer = new ImportReplay();
      final Layers theLayers = new Layers();
      final String fName = "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/Ambig_tracks.rep";
      File inFile = new File(fName);
      assertTrue("input file exists", inFile.exists());
      FileInputStream is = new FileInputStream(fName);
      importer.importThis(fName, is, theLayers);
      
      // sort out the sensors
      importer.storePendingSensors();
      assertEquals("has some layers", 3, theLayers.size());
      
      // get the sensor track
      TrackWrapper track = (TrackWrapper) theLayers.findLayer("SENSOR");
      return track;
    }
  }
  
}
