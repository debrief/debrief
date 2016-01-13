package org.debrief.limpet_integration;

import info.limpet.IStoreItem;

import org.eclipse.core.runtime.IAdapterFactory;

import Debrief.Wrappers.TrackWrapper;

public class DebriefLimpetAdapterFactory implements IAdapterFactory
{
  
  private static final Class<?>[] adapterList = { IStoreItem.class };

  @Override
  public Object getAdapter(Object subject, Class adapterType)
  {
    Object res = null;
    
    if(adapterType == IStoreItem.class)
    {
      if(subject instanceof TrackWrapper)
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
