package Debrief.ReaderWriter.Replay.extensions;

import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.Utilities.ReaderWriter.ExtensibleLineImporter;

abstract class Core_TA_Handler implements ExtensibleLineImporter
{

  @SuppressWarnings("unused")
  private Layers _myLayer;
  private final String _myType;
  
  Core_TA_Handler(final String type)
  {
    _myType = type;
  }

  @Override
  final public void setLayers(final Layers parent)
  {
    _myLayer = parent;
  }
  
  @Override
  final public String getYourType()
  {
    return _myType;
  }


  @Override
  final public String exportThis(Plottable theShape)
  {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  final public boolean canExportThis(Object val)
  {
    return false;
  }
}
