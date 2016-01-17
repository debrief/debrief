package org.debrief.limpet_integration.adapters;

import info.limpet.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.TemporalLocation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Iterator;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.PlainWrapper;

public class LimpetTrack extends CoreLimpetTrack
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private TrackWrapper _myTrack;

  public LimpetTrack(TrackWrapper track)
  {
    super(track.getName(), false);

    _myTrack = track;

    // setup listeners
    _myTrack.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED,
        new PropertyChangeListener()
        {
          @Override
          public void propertyChange(PropertyChangeEvent evt)
          {
            // ok, clear our data
            reset(false);

            Iterator<IStoreItem> children = children().iterator();
            while (children.hasNext())
            {
              IStoreItem iStoreItem = (IStoreItem) children.next();
              iStoreItem.fireDataChanged();
            }
          }
        });

    // store our data
    super.add(new StockTypes.Temporal.AngleDegrees(COURSE, null));
    super.add(new StockTypes.Temporal.SpeedMSec(SPEED, null));
    super.add(new StockTypes.Temporal.LengthM(DEPTH, null));
    super.add(new TemporalLocation(LOCATION));

    reset(false);

  }

  public TrackWrapper getTrack()
  {
    return _myTrack;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.debrief.limpet_integration.adapters.CoreLimpetTrack#getLocations()
   */
  @Override
  Enumeration<Editable> getLocations()
  {
    return _myTrack.getPositions();
  }

}
