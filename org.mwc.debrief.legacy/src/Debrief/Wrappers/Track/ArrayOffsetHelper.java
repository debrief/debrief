package Debrief.Wrappers.Track;

import java.util.ArrayList;
import java.util.List;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataFolder.DatasetOperator;
import Debrief.Wrappers.Extensions.Measurements.TimeSeries2Double;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;

public class ArrayOffsetHelper
{

  public static interface ArrayCentreMode
  {
    /**
     * yes, all objects have a toString() method. But, we do want to be certain that this method is
     * explicitly implemented. We don't want a default method.
     * 
     * @return
     */
    public String asString();
  }

  public static enum LegacyArrayOffsetModes implements ArrayCentreMode
  {
    PLAIN
    {

      @Override
      public String asString()
      {
        return "Plain";
      }
    },
    WORM
    {

      @Override
      public String asString()
      {
        return "Worm in hole";
      }
    };
  }

  public static class MeasuredDatasetArrayMode implements ArrayCentreMode
  {
    private final TimeSeries2Double _source;

    public MeasuredDatasetArrayMode(TimeSeries2Double source)
    {
      _source = source;
    }

    @Override
    public String asString()
    {
      return _source.getPath();
    }

    public TimeSeries2Double getDataset()
    {
      return _source;
    }
  }

  public static class DeferredDatasetArrayMode implements ArrayCentreMode
  {
    private final String _source;

    public DeferredDatasetArrayMode(String source)
    {
      _source = source;
    }

    public String getSourceName()
    {
      return _source;
    }

    @Override
    public String asString()
    {
      return "DEFERRED LOADING FAILED";
    }
  }

  public static List<ArrayCentreMode> getAdditionalArrayCentreModes(SensorWrapper sensor)
  {
    final List<ArrayCentreMode> res = new ArrayList<ArrayCentreMode>();

    // start off with our legacy modes
    res.add(LegacyArrayOffsetModes.PLAIN);
    res.add(LegacyArrayOffsetModes.WORM);

    Object measuredData =
        sensor.getAdditionalData().getThisType(DataFolder.class);
    if (measuredData != null)
    {
      // ok. walk the tree, and see if there are any datasets with location
      DataFolder df = (DataFolder) measuredData;

      DatasetOperator processor = new DataFolder.DatasetOperator()
      {
        @Override
        public void process(TimeSeriesCore dataset)
        {
          // ok, is it a 2D dataset?
          if (dataset instanceof TimeSeries2Double)
          {
            TimeSeries2Double ts = (TimeSeries2Double) dataset;

            String hisUnits = ts.getUnits();

            // is it suitable?
            if ("m".equals(hisUnits) || "\u00b0".equals(hisUnits))
            {
              res.add(new MeasuredDatasetArrayMode(ts));
            }
          }
        }
      };

      df.walkThisDataset(processor);
    }

    return res;
  }

  public static WorldLocation getArrayCentre(SensorWrapper sensor,
      HiResDate time, WorldLocation hostLocation, TrackWrapper track)
  {
    final WorldLocation centre;
    final ArrayCentreMode arrayCentre = sensor.getArrayCentreMode();
    if (arrayCentre instanceof LegacyArrayOffsetModes)
    {
      // ok, we need a sensor offset to do this.
      // check we have one
      final ArrayLength len = sensor.getSensorOffset();
      if (len != null && len.getValue() != 0)
      {
        // it's ok, we can use our old legacy worm in hole processing
        boolean inWormMode = arrayCentre.equals(LegacyArrayOffsetModes.WORM);
        centre =
            track.getBacktraceTo(time, len, inWormMode)
                .getLocation();
      }
      else
      {
        centre = null;
      }
    }
    else
    {
      // not in legacy mode, use measurements
      MeasuredDatasetArrayMode meas =
          (MeasuredDatasetArrayMode) arrayCentre;

      // now try get the location from the measured dataset

      // do we know the host location?
      if(hostLocation != null)
      {
        // ok, get calculating
        centre =
            sensor.getMeasuredLocationAt(meas, time, hostLocation);
      }
      else
      {        
        // ok, we'll have to find it
        Watchable[] matches = track.getNearestTo(time);
        if(matches.length == 1)
        {
          hostLocation = matches[0].getLocation();
          centre =
              sensor.getMeasuredLocationAt(meas, time, hostLocation);
        }
        else
        {
          centre = null;
        }
      }
    }
    
    return centre;
  }

  public static ArrayCentreMode sortOutDeferredMode(
      final DeferredDatasetArrayMode dMode, final SensorWrapper sensor)
  {    
    final String dName = dMode.getSourceName();
    
    final List<MeasuredDatasetArrayMode> matches = new ArrayList<MeasuredDatasetArrayMode>();
    
    Object measuredData =
        sensor.getAdditionalData().getThisType(DataFolder.class);
    if (measuredData != null)
    {
      // ok. walk the tree, and see if there are any datasets with location
      DataFolder df = (DataFolder) measuredData;

      DatasetOperator processor = new DataFolder.DatasetOperator()
      {
        @Override
        public void process(TimeSeriesCore dataset)
        {
          // ok, is it a 2D dataset?
          if (dataset instanceof TimeSeries2Double)
          {
            TimeSeries2Double ts = (TimeSeries2Double) dataset;

            if(ts.getPath().equals(dName))
            {
              matches.add(new MeasuredDatasetArrayMode(ts));
            }
          }
        }
      };

      df.walkThisDataset(processor);
    }
    
    final ArrayCentreMode res;
    
    if(matches.size() == 1)
    {
      res = matches.get(0);
    }
    else
    {
      Application.logStack2(Application.ERROR, "Failed to find measured data source to match:" + dName);
      res = null;
    }
    
    return res;
  }
  
}
