package org.mwc.debrief.limpet_integration.adapter;

import info.limpet.stackedcharts.ui.view.adapter.IStackedAdapter;

import java.util.List;

import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.WatchableList;

public class DebriefStackedChartsAdapter implements IStackedAdapter
{

  @Override
  public boolean canConvert(Object data)
  {
    boolean res = false;

    // have a look at the type
    if (data instanceof EditableWrapper)
    {
      EditableWrapper ew = (EditableWrapper) data;
      Editable editable = ew.getEditable();
      if (editable instanceof WatchableList)
      {
        res = true;
      }
      else if (editable instanceof SensorWrapper)
      {
        res = true;
      }
    }

    return res;
  }

  @Override
  public List<Object> convert(Object data)
  {
    List<Object> res = null;

    // we should have already checked, but just
    // double-check we can handle it
    if (canConvert(data))
    {
      EditableWrapper wrapper = (EditableWrapper) data;
      Editable editable = wrapper.getEditable();
      if (editable instanceof SensorWrapper)
      {
        SensorWrapper sensor = (SensorWrapper) editable;

        SensorConverter converter = new SensorConverter(sensor);
        
        res = converter.convert(res);

        // and store the data
      }
      else if (editable instanceof WatchableList)
      {
        WatchableList list = (WatchableList) editable;
        
        TrackConverter converter = new TrackConverter(list);
        
        res = converter.convert(res);

        // now store the data
        // hook up listener
      }

    }

    return res;
  }

}
