package org.debrief.limpet_integration.adapters;

import info.limpet.IStoreItem;

import org.eclipse.core.runtime.IAdapterFactory;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;

public class DebriefLimpetAdapterFactory implements IAdapterFactory
{

  private static final Class<?>[] adapterList =
  { IStoreItem.class };

  @Override
  public Object getAdapter(Object subject, Class adapterType)
  {
    Object res = null;

    if (adapterType == IStoreItem.class)
    {
      if (subject instanceof TrackWrapper)
      {
        // ok, take the steps to make it look like a Limpet Group Track
        res = new LimpetTrack((TrackWrapper) subject);

        // now store this in our dictionary, in case we get asked again
      }
      else if (subject instanceof TrackSegment)
      {
        // ok, take the steps to make it look like a Limpet Group Track

        // now store this in our dictionary, in case we get asked again
      }
    }

    return res;
  }

  @Override
  public Class[] getAdapterList()
  {
    return adapterList;
  }

}
