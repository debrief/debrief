package org.mwc.debrief.limpet_integration.adapter;

import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.WatchableList;

public class TrackConverter
{

  public TrackConverter()
  {
  }

  protected abstract static class DataChoiceProvider
  {
    protected final String _name;
    protected final String _units;

    DataChoiceProvider(String name, String units)
    {
      _name = name;
      _units = units;
    }

    public String toString()
    {
      return _name;
    }

    abstract List<Dataset> getDatasets(WatchableList track);

    abstract List<ScatterSet> getScatterSets(WatchableList track);
  }

  private abstract static class StateChoiceProvider extends DataChoiceProvider
  {

    StateChoiceProvider(String name, String units)
    {
      super(name, units);
    }

    List<Dataset> getDatasets(WatchableList track)
    {
      List<Dataset> res = null;

      final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

      Dataset dataset = factory.createDataset();
      dataset.setName(track.getName() + "-" + _name);
      dataset.setUnits(_units);
      PlainStyling ps = factory.createPlainStyling();
      ps.setColor(track.getColor());
      ps.setLineThickness(2.0d);
      dataset.setStyling(ps);

      Collection<Editable> elements =
          track.getItemsBetween(track.getStartDTG(), track.getEndDTG());
      Iterator<Editable> items = elements.iterator();
      while (items.hasNext())
      {
        FixWrapper fix = (FixWrapper) items.next();
        double dependent = valueFor(fix);

        DataItem item = factory.createDataItem();
        item.setIndependentVal(fix.getDateTimeGroup().getDate().getTime());
        item.setDependentVal(dependent);

        dataset.getMeasurements().add(item);
      }

      if (dataset.getMeasurements().size() > 0)
      {
        if (res == null)
        {
          res = new ArrayList<Dataset>();
        }
        res.add(dataset);
      }

      return res;
    }

    List<ScatterSet> getScatterSets(WatchableList track)
    {
      List<ScatterSet> res = null;

      final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

      ScatterSet scatter = factory.createScatterSet();
      scatter.setName(track.getName() + "-" + _name);
      PlainStyling ps = factory.createPlainStyling();
      ps.setColor(track.getColor());
      ps.setLineThickness(2.0d);

      Collection<Editable> elements =
          track.getItemsBetween(track.getStartDTG(), track.getEndDTG());
      Iterator<Editable> items = elements.iterator();
      while (items.hasNext())
      {
        FixWrapper fix = (FixWrapper) items.next();
        Datum item = factory.createDatum();
        item.setVal(fix.getDateTimeGroup().getDate().getTime());

        scatter.getDatums().add(item);
      }

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

    abstract protected double valueFor(FixWrapper fix);
  }

  private Object[] _previousChoices = null;

  public List<Dataset> convertToDataset(final WatchableList list)
  {
    List<Dataset> res = new ArrayList<Dataset>();

    // have a look at the type
    DataChoiceProvider[] choices = new DataChoiceProvider[]
    {new StateChoiceProvider("Course", "degs")
    {
      @Override
      protected double valueFor(FixWrapper fix)
      {
        return fix.getCourseDegs();
      }
    }, new StateChoiceProvider("Speed", "kts")
    {
      @Override
      protected double valueFor(FixWrapper fix)
      {
        return fix.getSpeed();
      }
    }, new StateChoiceProvider("Depth", "m")
    {
      @Override
      protected double valueFor(FixWrapper fix)
      {
        return fix.getLocation().getDepth();
      }
    }};

    Shell shell = Display.getCurrent().getActiveShell();
    ListSelectionDialog dialog =
        new ListSelectionDialog(shell, choices, ArrayContentProvider
            .getInstance(), new LabelProvider(),
            "Please choose which data to display");

    dialog.setTitle("Loading track:" + list.getName());

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
        DataChoiceProvider provider = (DataChoiceProvider) _previousChoices[i];
        List<Dataset> datasets = provider.getDatasets(list);

        if (datasets != null && datasets.size() > 0)
          if (res == null)
          {
            res = new ArrayList<Dataset>();
          }
        res.addAll(datasets);

      }
    }
    return res;
  }

  public List<ScatterSet> convertToScatterset(final WatchableList list)
  {
    List<ScatterSet> res = new ArrayList<ScatterSet>();

    // have a look at the type
    DataChoiceProvider[] choices = new DataChoiceProvider[]
    {new StateChoiceProvider("Course", "degs")
    {
      @Override
      protected double valueFor(FixWrapper fix)
      {
        return fix.getCourseDegs();
      }
    }, new StateChoiceProvider("Speed", "kts")
    {
      @Override
      protected double valueFor(FixWrapper fix)
      {
        return fix.getSpeed();
      }
    }, new StateChoiceProvider("Depth", "m")
    {
      @Override
      protected double valueFor(FixWrapper fix)
      {
        return fix.getLocation().getDepth();
      }
    }};

    Shell shell = Display.getCurrent().getActiveShell();
    ListSelectionDialog dialog =
        new ListSelectionDialog(shell, choices, ArrayContentProvider
            .getInstance(), new LabelProvider(),
            "Please choose which data to display");

    dialog.setTitle("Loading track:" + list.getName());

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
        DataChoiceProvider provider = (DataChoiceProvider) _previousChoices[i];
        List<ScatterSet> datasets = provider.getScatterSets(list);

        if (datasets != null && datasets.size() > 0)
          if (res == null)
          {
            res = new ArrayList<ScatterSet>();
          }
        res.addAll(datasets);

      }
    }
    return res;
  }

}
