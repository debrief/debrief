/**
 * 
 */
package org.debrief.limpet_integration.adapters;

import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.TemporalLocation;
import info.limpet.data.operations.spatial.GeoSupport;
import info.limpet.data.operations.spatial.IGeoCalculator;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.awt.geom.Point2D;
import java.util.Enumeration;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

/**
 * @author ian
 * 
 */
abstract public class CoreLimpetTrack extends StoreGroup
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public static final String LOCATION = "Location";
  public static final String DEPTH = "Depth";
  public static final String SPEED = "Speed";
  public static final String COURSE = "Course";

  /**
   * @param name
   */
  public CoreLimpetTrack(String name, boolean isSingleton)
  {
    super(name);
  }

  abstract Enumeration<Editable> getLocations();

  protected void init(boolean isSingleton)
  {
    // have we been built/collated?
    if (size() == 0)
    {
      // nope, get building

      // location
      Enumeration<Editable> fixes = getLocations();

      IObjectCollection<Point2D> location = fillLocations(fixes, isSingleton);

      // course
      IQuantityCollection<?> course = fillDataset(getLocations(),
          new DoubleGetter()
          {
            @Override
            public double getValue(FixWrapper fix)
            {
              return fix.getCourse();
            }

            @Override
            public IQuantityCollection<?> getOutput(boolean isSingleton)
            {
              if (isSingleton)
              {
                return new StockTypes.NonTemporal.AngleDegrees(COURSE, null);
              }
              else
              {
                return new StockTypes.Temporal.AngleDegrees(COURSE, null);
              }
            }

          }, isSingleton);
      IQuantityCollection<?> speed = fillDataset(getLocations(),
          new DoubleGetter()
          {
            @Override
            public double getValue(FixWrapper fix)
            {
              WorldSpeed kts = new WorldSpeed(fix.getSpeed(), WorldSpeed.Kts);
              return kts.getValueIn(WorldSpeed.M_sec);
            }

            @Override
            public IQuantityCollection<?> getOutput(boolean isSingleton)
            {
              if (isSingleton)
              {
                return new StockTypes.NonTemporal.SpeedMSec(SPEED, null);
              }
              else
              {
                return new StockTypes.Temporal.SpeedMSec(SPEED, null);
              }
            }

          }, isSingleton);
      IQuantityCollection<?> depth = fillDataset(getLocations(),
          new DoubleGetter()
          {
            @Override
            public double getValue(FixWrapper fix)
            {
              return fix.getLocation().getDepth();
            }

            @Override
            public IQuantityCollection<?> getOutput(boolean isSingleton)
            {
              if (isSingleton)
              {
                return new StockTypes.NonTemporal.LengthM(DEPTH, null);
              }
              else
              {
                return new StockTypes.Temporal.LengthM(DEPTH, null);
              }
            }
          }, isSingleton);

      // collate results
      add(location);
      add(course);
      add(speed);
      add(depth);
    }
  }

  protected IObjectCollection<Point2D> fillLocations(
      Enumeration<Editable> fixes, boolean isSingleton)
  {
    final IObjectCollection<Point2D> destination;

    if (isSingleton)
    {
      destination = new StockTypes.NonTemporal.Location(LOCATION);
    }
    else
    {
      destination = new TemporalLocation(LOCATION);

    }

    while (fixes.hasMoreElements())
    {
      final FixWrapper fix = (FixWrapper) fixes.nextElement();
      IGeoCalculator calc = GeoSupport.getCalculator();
      Point2D point = calc.createPoint(fix.getLocation().getLong(), fix
          .getLocation().getLat());
      if (isSingleton)
      {
        destination.add(point);
      }
      else
      {
        TemporalLocation tDest = (TemporalLocation) destination;
        tDest.add(fix.getDateTimeGroup().getDate().getTime(), point);
      }
    }
    return destination;
  }

  protected static IQuantityCollection<?> fillDataset(
      Enumeration<Editable> fixes, DoubleGetter getter, boolean isSingleton)
  {
    IQuantityCollection<?> destination = getter.getOutput(isSingleton);

    while (fixes.hasMoreElements())
    {
      final FixWrapper fix = (FixWrapper) fixes.nextElement();

      if (isSingleton)
      {
        destination.add(getter.getValue(fix));
      }
      else
      {
        ITemporalQuantityCollection<?> tDest = (ITemporalQuantityCollection<?>) destination;
        tDest.add(fix.getDateTimeGroup().getDate().getTime(),
            getter.getValue(fix));
      }
    }
    return destination;
  }

  protected static interface DoubleGetter
  {
    double getValue(FixWrapper fix);

    IQuantityCollection<?> getOutput(boolean isSingleton);
  }

}
