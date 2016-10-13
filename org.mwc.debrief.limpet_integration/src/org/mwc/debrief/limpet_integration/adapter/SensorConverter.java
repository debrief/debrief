package org.mwc.debrief.limpet_integration.adapter;

import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;

public class SensorConverter
{

  public SensorConverter()
  {
  }

  public List<Dataset> convertToDataset(SensorWrapper sensor)
  {
    List<Dataset> res = new ArrayList<Dataset>();
    
    List<Dataset> datasets = getDatasets(sensor);

    if (datasets != null && datasets.size() > 0)
      res.addAll(datasets);

    return res;
  }
  

  public List<ScatterSet> convertToScatterset(SensorWrapper sensor)
  {
    List<ScatterSet> res = new ArrayList<ScatterSet>();

    List<ScatterSet> datasets = getScatterset(sensor);

    if (datasets != null && datasets.size() > 0)
      res.addAll(datasets);

    return res;
  }

  List<Dataset> getDatasets(SensorWrapper sensor)
  {
    List<Dataset> res = null;

    final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

    Dataset dataset = factory.createDataset();
    dataset.setName(sensor.getName());
    dataset.setUnits("degs");
    PlainStyling ps = factory.createPlainStyling();
    ps.setColor(sensor.getColor());
    ps.setLineThickness(2.0d);
    dataset.setStyling(ps);

    Enumeration<Editable> enumer = sensor.elements();
    while (enumer.hasMoreElements())
    {
      SensorContactWrapper cut = (SensorContactWrapper) enumer.nextElement();
      DataItem item = factory.createDataItem();
      item.setIndependentVal(cut.getTime().getDate().getTime());
      item.setDependentVal(cut.getBearing());

      // and store it
      dataset.getMeasurements().add(item);
    }

    // did we find any?
    if (!dataset.getMeasurements().isEmpty())
    {
      if (res == null)
      {
        res = new ArrayList<Dataset>();
      }
      res.add(dataset);
    }

    return res;
  }

  List<ScatterSet> getScatterset(SensorWrapper sensor)
  {
    List<ScatterSet> res = null;

    final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

    ScatterSet scatter = factory.createScatterSet();
    scatter.setName(sensor.getName());
    scatter.setColor(sensor.getColor());

    Enumeration<Editable> enumer = sensor.elements();
    while (enumer.hasMoreElements())
    {
      SensorContactWrapper cut = (SensorContactWrapper) enumer.nextElement();
      Datum item = factory.createDatum();
      item.setVal(cut.getTime().getDate().getTime());
      item.setColor(cut.getColor());

      // and store it
      scatter.getDatums().add(item);
    }

    // did we find any?
    if (scatter.getDatums().size() > 0)
    {
      if (res == null)
      {
        res = new ArrayList<ScatterSet>();
      }
      res.add(scatter);
    }

    return res;

  }
}
