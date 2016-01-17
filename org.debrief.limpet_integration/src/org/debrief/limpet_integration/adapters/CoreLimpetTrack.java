/**
 * 
 */
package org.debrief.limpet_integration.adapters;

import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.samples.TemporalLocation;
import info.limpet.data.operations.spatial.GeoSupport;
import info.limpet.data.operations.spatial.IGeoCalculator;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.awt.geom.Point2D;
import java.util.Enumeration;
import java.util.Iterator;

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

  protected boolean _pending = true;

  /**
   * @param name
   */
  public CoreLimpetTrack(String name, boolean isSingleton)
  {
    super(name);
  }

  abstract Enumeration<Editable> getLocations();

  /**
   * clear our stored data, it has clearly changed
   * 
   */
  protected void reset(boolean isSingleton)
  {
    Iterator<IStoreItem> kids = children().iterator();
    while (kids.hasNext())
    {
      IObjectCollection<?> item = (IObjectCollection<?>) kids.next();
      item.clearQuiet();
    }

    _pending = true;

    // ok, populate ourselves
    init(isSingleton);
  }

  @SuppressWarnings("unchecked")
  private void init(boolean isSingleton)
  {
    // have we been built/collated?
    if (_pending)
    {
      // nope, get building

      // location
      Enumeration<Editable> fixes = getLocations();

      fillLocations((IObjectCollection<Point2D>) get(LOCATION), fixes,
          isSingleton);

      // course
      fillDataset((IQuantityCollection<?>) get(COURSE), getLocations(),
          new DoubleGetter()
          {
            @Override
            public double getValue(FixWrapper fix)
            {
              return fix.getCourse();
            }
          }, isSingleton);
      fillDataset((IQuantityCollection<?>) get(SPEED), getLocations(),
          new DoubleGetter()
          {
            @Override
            public double getValue(FixWrapper fix)
            {
              WorldSpeed kts = new WorldSpeed(fix.getSpeed(), WorldSpeed.Kts);
              return kts.getValueIn(WorldSpeed.M_sec);
            }
          }, isSingleton);
      fillDataset((IQuantityCollection<?>) get(DEPTH), getLocations(),
          new DoubleGetter()
          {
            @Override
            public double getValue(FixWrapper fix)
            {
              return fix.getLocation().getDepth();
            }
          }, isSingleton);

      _pending = false;

    }
  }

  protected IObjectCollection<Point2D> fillLocations(
      IObjectCollection<Point2D> destination, Enumeration<Editable> fixes,
      boolean isSingleton)
  {

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
      IQuantityCollection<?> destination, Enumeration<Editable> fixes,
      DoubleGetter getter, boolean isSingleton)
  {
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
  }

}
