package org.mwc.debrief.limpet_integration.adapter;

import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.adapter.IStackedAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.WatchableList;

public class DebriefStackedChartsAdapter implements IStackedAdapter
{

  protected abstract static class DataChoiceProvider
  {
    protected final String _name;

    DataChoiceProvider(String name)
    {
      _name = name;
    }

    public String toString()
    {
      return _name;
    }

    abstract List<Dataset> getDatasets(WatchableList track);
  }

  protected abstract static class StateChoiceProvider extends
      DataChoiceProvider
  {

    StateChoiceProvider(String name)
    {
      super(name);
    }

    List<Dataset> getDatasets(WatchableList track)
    {
      List<Dataset> res = null;

      final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

      Dataset dataset = factory.createDataset();
      dataset.setName(track.getName() + "-" + _name);
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

    abstract protected double valueFor(FixWrapper fix);
  }

  private Object[] _previousChoices = null;

  @SuppressWarnings(
  {"unused"})
  @Override
  public List<Dataset> convert(Object data)
  {
    List<Dataset> res = null;

    // we should have already checked, but just
    // double-check we can handle it
    if (canConvert(data))
    {
      final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

      EditableWrapper wrapper = (EditableWrapper) data;
      Editable editable = wrapper.getEditable();
      if (editable instanceof WatchableList)
      {
        WatchableList list = (WatchableList) editable;

        // have a look at the type
        if (data instanceof EditableWrapper)
        {

          DataChoiceProvider[] choices = new DataChoiceProvider[]
          {new StateChoiceProvider("Course (degs)")
          {
            @Override
            protected double valueFor(FixWrapper fix)
            {
              return fix.getCourseDegs();
            }
          }, new StateChoiceProvider("Speed (kts)")
          {
            @Override
            protected double valueFor(FixWrapper fix)
            {
              return fix.getSpeed();
            }
          }, new StateChoiceProvider("Depth (m)")
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

          dialog.setTitle("dialog title");

          if(_previousChoices != null)
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

          // now store the data
          // hook up listener
        }
        else if (editable instanceof SensorWrapper)
        {
          SensorWrapper sensor = (SensorWrapper) editable;

          // and store the data
        }
      }
    }

    return res;
  }

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

}
