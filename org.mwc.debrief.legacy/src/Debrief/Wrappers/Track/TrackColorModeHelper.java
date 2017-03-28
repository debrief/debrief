package Debrief.Wrappers.Track;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataFolder.DatasetOperator;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble;
import MWC.GUI.Editable;
import MWC.GenericData.Watchable;

public class TrackColorModeHelper
{

  public static interface TrackColorMode
  {
    /**
     * yes, all objects have a toString() method. But, we do want to be certain that this method is
     * explicitly implemented. We don't want a default method.
     * 
     * @return
     */
    public String asString();

    /**
     * get the color at this time
     * 
     * @param time
     * @return
     */
    public Color colorFor(Watchable thisFix);
  }

  public static enum LegacyTrackColorModes implements TrackColorMode
  {
    OVERRIDE
    {

      @Override
      public String asString()
      {
        return "Track Color Override";
      }

      @Override
      public Color colorFor(Watchable thisFix)
      {
        FixWrapper fix = (FixWrapper) thisFix;
        return fix.getTrackWrapper().getColor();
      }
    },
    PER_FIX
    {
      @Override
      public String asString()
      {
        return "Per-fix Shades";
      }

      @Override
      public Color colorFor(Watchable thisFix)
      {
        return thisFix.getColor();
      }
    };
  }
  
  private static String nameFor(String source, String dataset)
  {
    return source + ":" + dataset;
  }

  public static class MeasuredDatasetColorMode implements TrackColorMode
  {
    private final TimeSeriesDatasetDouble _dataset;
    private String _source;
    private Double _min;
    private Double _scale;

    public MeasuredDatasetColorMode(String source,
        TimeSeriesDatasetDouble dataset)
    {
      _dataset = dataset;
      _source = source;
    }

    @Override
    public String asString()
    {
      return nameFor(_source, _dataset.getPath());
    }

    public TimeSeriesDatasetDouble getDataset()
    {
      return _dataset;
    }
    
    private void init()
    {
      // initialised?
      if(_min != null)
        return;
      
      // ok, sort out the scaling factors
      _min = (Double) _dataset.getDataset().min();
      double max = (Double) _dataset.getDataset().max();
      double range = max - _min;
      
      // we take the cube root of the range. It suits our initial trial data
      _scale = range / 255d;
    }

    @Override
    public Color colorFor(Watchable thisFix)
    {
      // check we have our limits
      init();
      
      // what's the index of the nearest time to this value?
      int tIndex = _dataset.getIndexNearestTo(thisFix.getTime().getDate().getTime());      
      
      if(tIndex != TimeSeriesCore.INVALID_INDEX)
      {
        double val = _dataset.getDataset().getDouble(tIndex);
        int red = (int)((val - _min) / _scale);
        Color color = new Color(red, 0, 255 - red);
        return color;
      }
      else
      {
        return Color.YELLOW;
      }
    }
  }

  public static class DeferredDatasetColorMode implements TrackColorMode
  {
    private final String _source;

    public DeferredDatasetColorMode(String source)
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

    @Override
    public Color colorFor(Watchable thisFix)
    {
      return Color.GREEN;
    }
  }

  public static List<TrackColorMode> getAdditionalTrackColorModes(
      TrackWrapper track)
  {
    final List<TrackColorMode> res = new ArrayList<TrackColorMode>();
    
    // start off with our legacy modes
    res.add(LegacyTrackColorModes.OVERRIDE);
    res.add(LegacyTrackColorModes.PER_FIX);

    // loop through the sensors
    Enumeration<Editable> sIter = track.getSensors().elements();
    while (sIter.hasMoreElements())
    {
      final SensorWrapper sensor = (SensorWrapper) sIter.nextElement();
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
            // ok, is it a 1D dataset?
            if (dataset instanceof TimeSeriesDatasetDouble)
            {
              TimeSeriesDatasetDouble ts = (TimeSeriesDatasetDouble) dataset;
              res.add(new MeasuredDatasetColorMode(sensor.getName(), ts));
            }
          }
        };

        df.walkThisDataset(processor);
      }

    }

    return res;
  }

  public static TrackColorMode sortOutDeferredMode(
      final DeferredDatasetColorMode dMode, final TrackWrapper track)
  {
    final String dName = dMode.getSourceName();

    final List<TrackColorMode> matches = new ArrayList<TrackColorMode>();

    // loop through the sensors
    Enumeration<Editable> sIter = track.getSensors().elements();
    while (sIter.hasMoreElements())
    {
      final SensorWrapper sensor = (SensorWrapper) sIter.nextElement();
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
            if (dataset instanceof TimeSeriesDatasetDouble)
            {
              final TimeSeriesDatasetDouble ts = (TimeSeriesDatasetDouble) dataset;
              final String tsPath = nameFor(sensor.getName(), ts.getPath());

              if (tsPath.equals(dName))
              {
                matches.add(new MeasuredDatasetColorMode(sensor.getName(), ts));
              }
            }
          }
        };

        df.walkThisDataset(processor);
      }
    }
    final TrackColorMode res;

    if (matches.size() == 1)
    {
      res = matches.get(0);
    }
    else
    {
      Application.logStack2(Application.ERROR,
          "Failed to find measured data source to match:" + dName);
      res = null;
    }

    return res;
  }

}
