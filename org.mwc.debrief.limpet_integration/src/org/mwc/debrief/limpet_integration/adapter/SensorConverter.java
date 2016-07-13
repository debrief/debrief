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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;

public class SensorConverter
{

  private final SensorWrapper sensor;

  public SensorConverter(final SensorWrapper sensor)
  {
    this.sensor = sensor;
  }

  protected abstract static class ChoiceProvider
  {
    protected final String _name;

    ChoiceProvider(String name)
    {
      _name = name;
    }

    public String toString()
    {
      return _name;
    }

    abstract List<Object> getDatasets(SensorWrapper sensor);
  }

  private static class BearingsProvider extends ChoiceProvider
  {

    BearingsProvider()
    {
      super("Measured bearings");
    }

    List<Object> getDatasets(SensorWrapper sensor)
    {
      List<Object> res = null;

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
      if (dataset.getMeasurements().size() > 0)
      {
        if (res == null)
        {
          res = new ArrayList<Object>();
        }
        res.add(dataset);
      }

      return res;
    }
  }

  private static class ScatterProvider extends ChoiceProvider
  {

    ScatterProvider()
    {
      super("Time of observations (as scatter)");
    }

    List<Object> getDatasets(SensorWrapper sensor)
    {
      List<Object> res = null;

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
          res = new ArrayList<Object>();
        }
        res.add(scatter);
      }

      return res;
    
    }
  }

  private Object[] _previousChoices = null;

  public List<Object> convert(List<Object> res)
  {

    // have a look at the type
    ChoiceProvider[] choices = new ChoiceProvider[]
    {new BearingsProvider(), new ScatterProvider()};

    Shell shell = Display.getCurrent().getActiveShell();
    ListSelectionDialog dialog =
        new ListSelectionDialog(shell, choices, ArrayContentProvider
            .getInstance(), new LabelProvider(),
            "Please choose which data to display");

    dialog.setTitle("Loading sensor data for:" + sensor.getName());

    if (_previousChoices != null)
    {
      dialog.setInitialSelections(_previousChoices);
    }

    dialog.open();

    _previousChoices = dialog.getResult();

    if (_previousChoices != null)
    {

      for (int i = 0; i < _previousChoices.length; i++)
      {
        ChoiceProvider provider = (ChoiceProvider) _previousChoices[i];
        List<Object> datasets = provider.getDatasets(sensor);

        if (datasets != null && datasets.size() > 0)
          if (res == null)
          {
            res = new ArrayList<Object>();
          }
        res.addAll(datasets);

      }
    }
    return res;
  }

}
