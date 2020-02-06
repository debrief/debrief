/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package Debrief.Wrappers.Formatters;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.INewItemListener;

public class SetPrimaryListener implements INewItemListener
{

  @Override
  public void newItem(Layer layer, Editable item, String theSymbology)
  {
    if (item == null)
    {
      if (layer instanceof TrackWrapper)
      {
        @SuppressWarnings("unused")
        TrackWrapper track = (TrackWrapper) layer;

        // TODO: get a TrackDataManager object from the current editor,
        // then set this track as primary
      }
    }
  }

  @Override
  public void reset()
  {
  }

  @Override
  public void fileComplete()
  {
  }

}
