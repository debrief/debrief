package org.debrief.limpet_integration.adapters;

import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.TemporalLocation;
import info.limpet.data.operations.spatial.GeoSupport;
import info.limpet.data.operations.spatial.IGeoCalculator;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.awt.geom.Point2D;
import java.util.Enumeration;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

public class LimpetTrack extends StoreGroup
{
  public static final String LOCATION = "Location";
  public static final String DEPTH = "Depth";
  public static final String SPEED = "Speed";
  public static final String COURSE = "Course";
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private TrackWrapper _myTrack;

  public LimpetTrack(TrackWrapper track)
  {
    super(track.getName());

    _myTrack = track;
    
    init();
  }

  protected void init()
  {
    // have we been built/collated?
    if (size() == 0)
    {
      // nope, get building
      
      // location
      TemporalLocation location = getLocations(_myTrack);

      // course
      ITemporalQuantityCollection<?> course = fillDataset(
          new StockTypes.Temporal.AngleDegrees(COURSE, null), _myTrack,
          new DoubleGetter()
          {
            @Override
            public double getValue(FixWrapper fix)
            {
              return fix.getCourse();
            }
          });
      ITemporalQuantityCollection<?> speed = fillDataset(
          new StockTypes.Temporal.SpeedMSec(SPEED, null), _myTrack,
          new DoubleGetter()
          {
            @Override
            public double getValue(FixWrapper fix)
            {
              WorldSpeed kts = new WorldSpeed(fix.getSpeed(), WorldSpeed.Kts);
              return kts.getValueIn(WorldSpeed.M_sec);
            }
          });
      ITemporalQuantityCollection<?> depth = fillDataset(
          new StockTypes.Temporal.LengthM(DEPTH, null), _myTrack,
          new DoubleGetter()
          {
            @Override
            public double getValue(FixWrapper fix)
            {
              return fix.getLocation().getDepth();
            }
          });

      // collate results
      add(location);
      add(course);
      add(speed);
      add(depth);
    }   
  }

  protected static TemporalLocation getLocations(TrackWrapper track)
  {
    TemporalLocation destination = new TemporalLocation(LOCATION);
    Enumeration<Editable> data = track.getPositions();

    while (data.hasMoreElements())
    {
      final FixWrapper fix = (FixWrapper) data.nextElement();
      IGeoCalculator calc = GeoSupport.getCalculator();
      Point2D point = calc.createPoint(fix.getLocation().getLong(), fix
          .getLocation().getLat());
      destination.add(fix.getDateTimeGroup().getDate().getTime(), point);
    }
    return destination;
  }

  protected static ITemporalQuantityCollection<?> fillDataset(
      ITemporalQuantityCollection<?> destination, TrackWrapper track,
      DoubleGetter getter)
  {
    Enumeration<Editable> data = track.getPositions();

    while (data.hasMoreElements())
    {
      final FixWrapper fix = (FixWrapper) data.nextElement();
      destination.add(fix.getDateTimeGroup().getDate().getTime(),
          getter.getValue(fix));
    }
    return destination;
  }

  protected static interface DoubleGetter
  {
    double getValue(FixWrapper fix);
  }

}
