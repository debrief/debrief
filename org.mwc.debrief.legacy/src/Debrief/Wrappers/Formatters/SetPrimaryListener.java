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

}
