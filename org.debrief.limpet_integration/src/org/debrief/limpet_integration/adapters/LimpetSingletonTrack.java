package org.debrief.limpet_integration.adapters;

import info.limpet.data.impl.samples.StockTypes;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldLocation;

public class LimpetSingletonTrack extends StockTypes.NonTemporal.Location
{

  private final LabelWrapper _myLabel;

  public LimpetSingletonTrack(LabelWrapper label)
  {
    super(label.getName());

    _myLabel = label;

    // setup listeners
    _myLabel.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED,
        new PropertyChangeListener()
        {
          @Override
          public void propertyChange(PropertyChangeEvent evt)
          {
            // ok, clear our data
            reset();

            fireDataChanged();
          }
        });

    reset();
  }

  /**
   * 
   */
  private void reset()
  {
    // get rid of existing data
    this.clearQuiet();

    // stick our location into the track
    WorldLocation loc = _myLabel.getLocation();
    Point2D pt = new Point2D.Double(loc.getLong(), loc.getLat());
    add(pt);
  }

}
