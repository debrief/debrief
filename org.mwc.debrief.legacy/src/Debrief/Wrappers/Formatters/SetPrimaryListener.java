package Debrief.Wrappers.Formatters;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers.INewLayerListener;

public class SetPrimaryListener implements INewLayerListener
{

  @Override
  public void newLayer(Layer layer)
  {
    if (layer instanceof TrackWrapper)
    {
      TrackWrapper track = (TrackWrapper) layer;

      // TODO: get a TrackDataManager object from the current editor,
      // then set this track as primary
    }
  }

}
