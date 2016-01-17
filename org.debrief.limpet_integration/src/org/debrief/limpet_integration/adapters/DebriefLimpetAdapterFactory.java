package org.debrief.limpet_integration.adapters;

import info.limpet.IStoreItem;
import info.limpet.ui.data_provider.data.LimpetWrapper;

import org.eclipse.core.runtime.IAdapterFactory;
import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;

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
      else if(subject instanceof LabelWrapper)
      {
        LabelWrapper lw= (LabelWrapper) subject;
        res = new LimpetSingletonTrack(lw);
      }
      else if (subject instanceof EditableWrapper)
      {
        EditableWrapper ew = (EditableWrapper) subject;
        Editable object = ew.getEditable();
        if (object instanceof LimpetWrapper)
        {
          LimpetWrapper lw = (LimpetWrapper) object;
          Object limp = lw.getSubject();
          if (limp instanceof IStoreItem)
          {
            res = (IStoreItem) limp;
          }
        }
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
