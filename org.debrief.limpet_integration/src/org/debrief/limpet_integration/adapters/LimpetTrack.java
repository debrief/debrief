package org.debrief.limpet_integration.adapters;

import info.limpet.IQuantityCollection;
import info.limpet.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.samples.TemporalLocation;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.util.ArrayList;
import java.util.List;

import javax.measure.quantity.Quantity;

import Debrief.Wrappers.TrackWrapper;

public class LimpetTrack extends StoreGroup
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private TrackWrapper _myTrack;

  public LimpetTrack(TrackWrapper track)
  {
    super(track.getName());

    _myTrack = track;
  }

  @Override
  public List<IStoreItem> children()
  {
    List<IStoreItem> res = new ArrayList<IStoreItem>();
    // ok, produce a collection for each data type

    // location
    TemporalLocation location = getLocations(_myTrack);

    // course
    ITemporalQuantityCollection<Quantity> course = getDataset(_myTrack, new DoubleGetter(){
      @Override
      public double getValue(TrackWrapper track, long time)
      {
        // TODO Auto-generated method stub
        return 0;
      }});

    // speed

    // collate results
    res.add(location);
    res.add(course);
    
    return res;
  }

  public TemporalLocation getLocations(TrackWrapper track)
  {
    return null;
  }

  public ITemporalQuantityCollection<Quantity> getDataset(TrackWrapper track,
      DoubleGetter getter)
  {
    return null;
  }

  public interface DoubleGetter
  {
    public double getValue(TrackWrapper track, long time);
  }

}
