package org.debrief.limpet_integration.adapters;

import info.limpet.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.Editable;
import MWC.GUI.PlainWrapper;
import MWC.TacticalData.Fix;

public class LimpetSingletonTrack extends CoreLimpetTrack
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private LabelWrapper _myLabel;

  public LimpetSingletonTrack(LabelWrapper label)
  {
    super(label.getName(), true);

    _myLabel = label;

    // setup listeners
    _myLabel.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED,
        new PropertyChangeListener()
        {
          @Override
          public void propertyChange(PropertyChangeEvent evt)
          {
            // ok, clear our data
            reset(true);

            Iterator<IStoreItem> children = children().iterator();
            while (children.hasNext())
            {
              IStoreItem iStoreItem = (IStoreItem) children.next();
              iStoreItem.fireDataChanged();
            }
          }
        });

    // create out input data
    super.add(new StockTypes.NonTemporal.AngleDegrees(COURSE, null));
    super.add(new StockTypes.NonTemporal.SpeedMSec(SPEED, null));
    super.add(new StockTypes.NonTemporal.LengthM(DEPTH, null));
    super.add(new StockTypes.NonTemporal.Location(LOCATION));

    reset(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.debrief.limpet_integration.adapters.CoreLimpetTrack#getLocations()
   */
  @Override
  Enumeration<Editable> getLocations()
  {
    Fix newF = new Fix(null, _myLabel.getLocation(), 0, 0);
    FixWrapper fw = new FixWrapper(newF);
    Vector<Editable> res = new Vector<Editable>();
    res.add(fw);
    return res.elements();
  }

}
