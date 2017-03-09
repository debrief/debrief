package org.mwc.debrief.limpet_integration.measured_data;

import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.adapter.IStackedDatasetAdapter;
import info.limpet.stackedcharts.ui.view.adapter.IStackedScatterSetAdapter;
import info.limpet.stackedcharts.ui.view.adapter.IStackedTimeListener;
import info.limpet.stackedcharts.ui.view.adapter.IStackedTimeProvider;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.core.providers.measured_data.DatasetWrapper;

import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDouble;
import MWC.GUI.Editable;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;

public class MeasuredDataInStackedChartsAdapter implements
    IStackedDatasetAdapter, IStackedScatterSetAdapter, IStackedTimeProvider
{

  /**
   * helper, to embody some processing
   * 
   * @author ian
   * 
   */
  private static interface ProcessHelper
  {
    /**
     * handle this instance
     * 
     * @param index
     * @param value
     */
    void processThis(long index, double value);

    /**
     * set the dataset name
     * 
     * @param name
     */
    void setName(String name);

    /**
     * set the dataset styling
     * 
     * @param ps
     */
    void setStyling(PlainStyling ps);
  }

  private HashMap<IStackedTimeListener, TimeDoublet> _timeListeners;

  @Override
  public boolean canConvertToDataset(Object data)
  {
    boolean res = false;
    if (data instanceof EditableWrapper)
    {
      EditableWrapper ew = (EditableWrapper) data;
      Editable ed = ew.getEditable();
      if (ed instanceof DatasetWrapper)
      {
        DatasetWrapper ds = (DatasetWrapper) ed;
        @SuppressWarnings("unused")
        TimeSeriesCore cd = ds.getDataset();
        res = true;
      }
    }
    return res;
  }

  @Override
  public boolean canConvertToScatterSet(Object data)
  {
    boolean res = false;
    if (data instanceof EditableWrapper)
    {
      EditableWrapper ew = (EditableWrapper) data;
      Editable ed = ew.getEditable();
      if (ed instanceof DatasetWrapper)
      {
        DatasetWrapper ds = (DatasetWrapper) ed;
        @SuppressWarnings("unused")
        TimeSeriesCore cd = ds.getDataset();
        res = true;
      }
    }
    return res;
  }

  private void DoDataset(TimeSeriesDouble cd, ProcessHelper helper,
      StackedchartsFactory factory)
  {
    // sort out a name
    final String name;
    DataFolder parent = cd.getParent();
    if (parent != null)
    {
      name = parent.getName() + " - " + cd.getName();
    }
    else
    {
      name = cd.getName();
    }
    helper.setName(name);

    PlainStyling ps = factory.createPlainStyling();
    // get a hash-code, for the color
    int hash = cd.hashCode();
    ps.setColor(DebriefColors.RandomColorProvider.getRandomColor(hash));
    ps.setLineThickness(2.0d);
    helper.setStyling(ps);

    Iterator<Long> iIter = cd.getIndices();
    Iterator<Double> vIter = cd.getValues();

    while (iIter.hasNext())
    {
      helper.processThis(iIter.next(), vIter.next());
    }
  }

  private void DoDatasetCore(TimeSeriesCore cd, ProcessHelper helper,
      StackedchartsFactory factory)
  {
    // sort out a name
    final String name;
    DataFolder parent = cd.getParent();
    if (parent != null)
    {
      name = parent.getName() + " - " + cd.getName();
    }
    else
    {
      name = cd.getName();
    }
    helper.setName(name);

    PlainStyling ps = factory.createPlainStyling();
    // get a hash-code, for the color
    int hash = cd.hashCode();
    ps.setColor(DebriefColors.RandomColorProvider.getRandomColor(hash));
    ps.setLineThickness(2.0d);
    helper.setStyling(ps);

    Iterator<Long> iIter = cd.getIndices();

    while (iIter.hasNext())
    {
      helper.processThis(iIter.next(), 0d);
    }
  }

  @Override
  public List<Dataset> convertToDataset(Object data)
  {
    List<Dataset> res = null;

    if (data instanceof EditableWrapper)
    {
      EditableWrapper ew = (EditableWrapper) data;
      Editable ed = ew.getEditable();
      if (ed instanceof DatasetWrapper)
      {
        DatasetWrapper ds = (DatasetWrapper) ed;
        TimeSeriesCore tsc = ds.getDataset();

        if (tsc instanceof TimeSeriesDouble)
        {
          TimeSeriesDouble cd = (TimeSeriesDouble) tsc;

          final StackedchartsFactory factory = new StackedchartsFactoryImpl();
          final Dataset dataset = factory.createDataset();

          dataset.setUnits(cd.getUnits());

          ProcessHelper ph = new ProcessHelper()
          {
            @Override
            public void processThis(long index, double value)
            {
              DataItem item = factory.createDataItem();
              item.setIndependentVal(index);
              item.setDependentVal(value);

              // and store it
              dataset.getMeasurements().add(item);
            }

            @Override
            public void setName(String name)
            {
              dataset.setName(name);
            }

            @Override
            public void setStyling(PlainStyling ps)
            {
              dataset.setStyling(ps);
            }

          };

          DoDataset(cd, ph, factory);

          // did we find any?
          if (!dataset.getMeasurements().isEmpty())
          {
            if (res == null)
            {
              res = new ArrayList<Dataset>();
            }
            res.add(dataset);
          }
        }
      }
    }
    return res;
  }

  @Override
  public List<ScatterSet> convertToScatterSet(Object data)
  {
    List<ScatterSet> res = null;

    if (data instanceof EditableWrapper)
    {
      EditableWrapper ew = (EditableWrapper) data;
      Editable ed = ew.getEditable();
      if (ed instanceof DatasetWrapper)
      {
        DatasetWrapper ds = (DatasetWrapper) ed;
        final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();
        final ScatterSet dataset = factory.createScatterSet();

        ProcessHelper ph = new ProcessHelper()
        {
          @Override
          public void processThis(long index, double value)
          {
            Datum item = factory.createDatum();
            item.setVal(index);

            // and store it
            dataset.getDatums().add(item);
          }

          @Override
          public void setName(String name)
          {
            dataset.setName(name);
          }

          @Override
          public void setStyling(PlainStyling ps)
          {
            // ignore
          }
        };

        TimeSeriesCore cd = ds.getDataset();
        DoDatasetCore(cd, ph, factory);

        // did we find any?
        if (!dataset.getDatums().isEmpty())
        {
          if (res == null)
          {
            res = new ArrayList<ScatterSet>();
          }
          res.add(dataset);
        }
      }
    }

    return res;
  }

  @Override
  public void controlThis(final IStackedTimeListener listener)
  {
    // ok, get ready to control this time view

    // ok, get the active editor
    final IWorkbenchWindow window =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window == null)
    {
      // handle case where application is closing
    }
    final IWorkbenchPage page = window.getActivePage();
    final IEditorPart editor = page.getActiveEditor();

    // see if we have a time provider
    final TimeProvider timeProv =
        (TimeProvider) editor.getAdapter(TimeProvider.class);
    if (timeProv != null)
    {
      final PropertyChangeListener evt = new PropertyChangeListener()
      {
        @Override
        public void propertyChange(final PropertyChangeEvent evt)
        {
          final HiResDate hd = (HiResDate) evt.getNewValue();
          if (hd != null)
          {
            final Date newDate = new Date(hd.getDate().getTime());
            listener.updateTime(newDate);
          }
        }
      };
      timeProv.addListener(evt, TimeProvider.TIME_CHANGED_PROPERTY_NAME);

      // get ready to store this permutation (so we can cancel it)
      TimeDoublet match = new TimeDoublet();
      match.provider = timeProv;
      match.event = evt;
      match.eType = TimeProvider.TIME_CHANGED_PROPERTY_NAME;

      // ok, remember it
      if (_timeListeners == null)
      {
        _timeListeners = new HashMap<IStackedTimeListener, TimeDoublet>();
      }
      _timeListeners.put(listener, match);
    }
  }

  /**
   * helper to store information necessary to cancel time listening
   * 
   * @author ian
   * 
   */
  private static class TimeDoublet
  {
    public String eType;
    public TimeProvider provider;
    public PropertyChangeListener event;
  }

  @Override
  public void releaseThis(IStackedTimeListener listener)
  {
    TimeDoublet match = _timeListeners.get(listener);
    if (match != null)
    {
      match.provider.removeListener(match.event, match.eType);
    }
  }

  @Override
  public boolean canProvideControl()
  {
    boolean canProvideControl = false;

    // ok, get the active editor
    final IWorkbenchWindow window =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window != null)
    {
      final IWorkbenchPage page = window.getActivePage();

      if (page != null)
      {
        final IEditorPart editor = page.getActiveEditor();

        // see if we have a time provider
        final TimeProvider timeProv =
            (TimeProvider) editor.getAdapter(TimeProvider.class);
        if (timeProv != null)
        {
          canProvideControl = true;
        }
      }
    }

    return canProvideControl;
  }

}
