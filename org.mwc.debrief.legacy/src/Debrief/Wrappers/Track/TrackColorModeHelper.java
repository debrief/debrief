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
        return "Fix Shades";
      }

      @Override
      public Color colorFor(Watchable thisFix)
      {
        return thisFix.getColor();
      }
    };
  }

  public static class MeasuredDatasetColorMode implements TrackColorMode
  {
    private final TimeSeriesDatasetDouble _dataset;
    private String _source;

    public MeasuredDatasetColorMode(String source,
        TimeSeriesDatasetDouble dataset)
    {
      _dataset = dataset;
      _source = source;
    }

    @Override
    public String asString()
    {
      return _source + ":" + _dataset.getPath();
    }

    public TimeSeriesDatasetDouble getDataset()
    {
      return _dataset;
    }

    @Override
    public Color colorFor(Watchable thisFix)
    {
      // TODO find the element nearest this time
      return Color.YELLOW;
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
      return null;
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
            // ok, is it a 2D dataset?
            if (dataset instanceof TimeSeriesDatasetDouble)
            {
              TimeSeriesDatasetDouble ts = (TimeSeriesDatasetDouble) dataset;

              String hisUnits = ts.getUnits();

              // is it suitable?
              if ("m".equals(hisUnits) || "\u00b0".equals(hisUnits))
              {
                res.add(new MeasuredDatasetColorMode(sensor.getName(), ts));
              }
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
              TimeSeriesDatasetDouble ts = (TimeSeriesDatasetDouble) dataset;

              if (ts.getPath().equals(dName))
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
