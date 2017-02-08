package org.mwc.debrief.limpet_integration.adapter;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.ui.view.adapter.IStackedDatasetAdapter;
import info.limpet.stackedcharts.ui.view.adapter.IStackedScatterSetAdapter;

import java.util.List;

import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.WatchableList;

public class DebriefStackedChartsAdapter implements IStackedDatasetAdapter, IStackedScatterSetAdapter
{

  @Override
  public boolean canConvertToDataset(Object data)
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
  public List<Dataset> convertToDataset(Object data)
  {
    List<Dataset> res = null;

    // we should have already checked, but just
    // double-check we can handle it
    if (canConvertToDataset(data))
    {
      EditableWrapper wrapper = (EditableWrapper) data;
      Editable editable = wrapper.getEditable();
      if (editable instanceof SensorWrapper)
      {
        SensorWrapper sensor = (SensorWrapper) editable;

        SensorConverter converter = new SensorConverter();
        
        res = converter.convertToDataset(sensor);

        // and store the data
      }
      else if (editable instanceof WatchableList)
      {
        WatchableList list = (WatchableList) editable;
        
        TrackConverter converter = new TrackConverter();
        
        res = converter.convertToDataset(list);

        // now store the data
        // hook up listener
      }

    }

    return res;
  }

  @Override
  public boolean canConvertToScatterSet(Object data)
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
  public List<ScatterSet> convertToScatterSet(Object data)
  {
    List<ScatterSet> res = null;

    // we should have already checked, but just
    // double-check we can handle it
    if (canConvertToDataset(data))
    {
      EditableWrapper wrapper = (EditableWrapper) data;
      Editable editable = wrapper.getEditable();
      if (editable instanceof SensorWrapper)
      {
        SensorWrapper sensor = (SensorWrapper) editable;

        SensorConverter converter = new SensorConverter();
        
        res = converter.convertToScatterset(sensor);

        // and store the data
      }
      else if (editable instanceof WatchableList)
      {
        WatchableList list = (WatchableList) editable;
        
        res = new TrackConverter().convertToScatterset(list);
        // now store the data
        // hook up listener
      }

    }

    return res;
  }

}
