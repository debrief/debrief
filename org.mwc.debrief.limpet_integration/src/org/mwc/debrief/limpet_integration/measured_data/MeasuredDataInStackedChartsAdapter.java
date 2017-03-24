package org.mwc.debrief.limpet_integration.measured_data;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.StackedChartsView;
import info.limpet.stackedcharts.ui.view.StackedChartsView.ControllableDate;
import info.limpet.stackedcharts.ui.view.adapter.IStackedDatasetAdapter;
import info.limpet.stackedcharts.ui.view.adapter.IStackedScatterSetAdapter;
import info.limpet.stackedcharts.ui.view.adapter.IStackedTimeListener;
import info.limpet.stackedcharts.ui.view.adapter.IStackedTimeProvider;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble;
import Debrief.Wrappers.Extensions.Measurements.Wrappers.DatasetWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;

public class MeasuredDataInStackedChartsAdapter implements
    IStackedDatasetAdapter, IStackedScatterSetAdapter, IStackedTimeProvider,
    RightClickContextItemGenerator
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

  private static void DoDataset(TimeSeriesDatasetDouble cd, ProcessHelper helper,
      StackedchartsFactory factory)
  {
    // sort out a name
    final String name;
    DataFolder parent = cd.getParent();
    if (parent != null)
    {
      name = parent.getName() + " : " + cd.getName();
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

  private static void DoDatasetCore(TimeSeriesCore cd, ProcessHelper helper,
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

  protected static Dataset _convertToDataset(Object data)
  {
    Dataset res = null;

    DatasetWrapper ds = null;

    if (data instanceof EditableWrapper)
    {
      // ok, get the editable out, we'll process it in a minute
      EditableWrapper ew = (EditableWrapper) data;
      data = ew.getEditable();
    }

    if (data instanceof DatasetWrapper)
    {
      ds = (DatasetWrapper) data;
    }

    if (ds != null)
    {
      TimeSeriesCore tsc = ds.getDataset();

      if (tsc instanceof TimeSeriesDatasetDouble)
      {
        TimeSeriesDatasetDouble cd = (TimeSeriesDatasetDouble) tsc;

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
          res = dataset;
        }
      }

    }
    return res;
  }

  @Override
  public List<Dataset> convertToDataset(Object data)
  {
    Dataset ds = _convertToDataset(data);
    final List<Dataset> res;
    if (ds != null)
    {
      res = new ArrayList<Dataset>();
      res.add(ds);
    }
    else
    {
      res = null;
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

  private static class ViewInChartsOperation extends CMAPOperation
  {

    private final List<Editable> _subjects;

    public ViewInChartsOperation(final String title,
        final List<Editable> datasets)
    {
      super(title);
      _subjects = datasets;
    }

    @Override
    public boolean canRedo()
    {
      return false;
    }

    @Override
    public boolean canUndo()
    {
      return false;
    }

    private static ChartSet
        produceChartset(Map<String, List<Dataset>> datasets)
    {

      // keep track of the current chart
      StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

      // create the chartset
      ChartSet charts = factory.createChartSet();

      // and the curernt chart
      Chart currentC = factory.createChart();
      charts.getCharts().add(currentC);

      IndependentAxis timeAxis = factory.createIndependentAxis();
      timeAxis.setAxisType(factory.createDateAxis());
      charts.setSharedAxis(timeAxis);

      // put each group on a new axis
      for (String units : datasets.keySet())
      {
        List<Dataset> theseSets = datasets.get(units);

        final DependentAxis targetAxis;

        // is the first axis populated?
        if (currentC.getMinAxes().size() == 0)
        {
          // no, add these datasets to this axis
          targetAxis = factory.createDependentAxis();
          targetAxis.setName(units);
          currentC.getMinAxes().add(targetAxis);
        }
        else if (currentC.getMaxAxes().size() == 0)
        {
          // no, add these datasets to this axis
          targetAxis = factory.createDependentAxis();
          targetAxis.setName(units);
          currentC.getMaxAxes().add(targetAxis);
        }
        else
        {
          // ok, min and max axes populated, time to insert new chart
          currentC = factory.createChart();
          charts.getCharts().add(currentC);
          // and put the data on the min axis
          targetAxis = factory.createDependentAxis();
          targetAxis.setName(units);
          currentC.getMinAxes().add(targetAxis);
        }

        targetAxis.getDatasets().addAll(theseSets);
      }

      return charts;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {

      Map<String, List<Dataset>> datasets =
          new HashMap<String, List<Dataset>>();

      // ok, loop through them, finding groups of the same units
      for (Editable thisD : _subjects)
      {
        // get this as a dataset
        Dataset dataset = _convertToDataset(thisD);

        // did it work?
        if (dataset != null)
        {
          final String units = dataset.getUnits();

          List<Dataset> matches = datasets.get(units);

          if (matches == null)
          {
            matches = new ArrayList<Dataset>();
            datasets.put(units, matches);
          }

          matches.add(dataset);
        }
      }

      // get a charts model
      ChartSet charts = produceChartset(datasets);

      // ok, we have our model. Now create the view, and show the model
      if (charts != null)
      {

        // ok, get the active editor
        final IWorkbenchWindow window =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null)
        {
          // handle case where application is closing
          return null;
        }
        final IWorkbenchPage page = window.getActivePage();
        
        // produce a name for the view
        DateFormat df = new SimpleDateFormat("hh_mm_ss");
        final String viewId = "Measured Data " + df.format(new Date());

        // create a new instance of the Tactical Overview
        final String ID = StackedChartsView.ID;
        try
        {
          page.showView(ID, viewId, IWorkbenchPage.VIEW_ACTIVATE);
        }
        catch (final PartInitException e)
        {
          CorePlugin.logError(Status.ERROR, "Failed to open Stacked Charts view", e);
          return Status.CANCEL_STATUS;
        }

        // send over the data
        final IViewReference viewRef = page.findViewReference(ID, viewId);
        if (viewRef != null)
        {
          final IViewPart theView = viewRef.getView(true);

          // double check it's what we're after
          if (theView instanceof StackedChartsView)
          {
            final StackedChartsView cv = (StackedChartsView) theView;

            // give it the model data
            cv.setModel(charts);

            // see if we have a time provider
            final IEditorPart editor = page.getActiveEditor();
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
                    cv.updateTime(newDate);
                  }
                }
              };
              timeProv
                  .addListener(evt, TimeProvider.TIME_CHANGED_PROPERTY_NAME);

              // we also need to listen for it closing, to remove the listner
              cv.addRunOnCloseCallback(new Runnable()
              {

                @Override
                public void run()
                {
                  // stop listening for time changes
                  timeProv.removeListener(evt,
                      TimeProvider.TIME_CHANGED_PROPERTY_NAME);
                }
              });
            }

            // see if we have a time provider
            final ControllableTime timeCont =
                (ControllableTime) editor.getAdapter(ControllableTime.class);
            if (timeCont != null && timeProv != null)
            {

              ControllableDate dateC = new ControllableDate()
              {

                @Override
                public void setDate(Date time)
                {
                  timeCont.setTime(this, new HiResDate(time), true);
                }

                @Override
                public Date getDate()
                {
                  return timeProv.getTime().getDate();
                }
              };

              cv.setDateSupport(dateC);

              // add a utility to cancel the support on close
              cv.addRunOnCloseCallback(new Runnable()
              {

                @Override
                public void run()
                {
                  // clear date support helper
                  cv.setDateSupport(null);
                }
              });
            }
          }
        }
      }

      return Status.CANCEL_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      return Status.CANCEL_STATUS;
    }
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

  @Override
  public void generate(IMenuManager parent, Layers theLayers,
      Layer[] parentLayers, Editable[] subjects)
  {
    // see if the selection are all datasets
    final List<Editable> res = new ArrayList<Editable>(5);

    boolean foundDodgy = false;

    // ok.
    for (int i = 0; i < subjects.length; i++)
    {
      Editable editable = subjects[i];
      if (editable instanceof DatasetWrapper)
      {
        DatasetWrapper dw = (DatasetWrapper) editable;
        TimeSeriesCore ds = dw.getDataset();
        if (ds instanceof TimeSeriesDatasetDouble)
        {
          // ok, add it
          res.add(editable);
        }
        else
        {
          foundDodgy = true;
        }
      }
      else
      {
        foundDodgy = true;
      }
    }

    // did it work?
    if (!foundDodgy && res.size() > 0)
    {
      final String title;
      if (res.size() == 1)
      {
        title = "View dataset in Stacked Charts";
      }
      else
      {
        title = "View datasets in Stacked Charts";
      }

      // ok, generate the action
      final Action doMerge = new Action(title)
      {
        public void run()
        {
          final IUndoableOperation theAction =
              new ViewInChartsOperation(title, res);

          CorePlugin.run(theAction);
        }
      };
      parent.add(doMerge);
    }
  }

}
