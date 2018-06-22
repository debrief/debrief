package info.limpet.stackedcharts.ui.view.adapter;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.ui.editor.Activator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class AdapterRegistry implements IStackedDatasetAdapter,
    IStackedScatterSetAdapter
{

  private static final String DATASET_ADAPTER_ID =
      "info.limpet.stackedcharts.ui.dataset_adapter";

  private static final String SCATTERSET_ADAPTER_ID =
      "info.limpet.stackedcharts.ui.scatterset_adapter";

  @SuppressWarnings("unchecked")
  @Override
  public List<Dataset> convertToDataset(Object data)
  {
    List<Dataset> res = null;
    try
    {
      // ok, do we need to loop through the items?
      List<Object> list;
      if (data instanceof List<?>)
      {
        list = (List<Object>) data;
      }
      else
      {
        list = new ArrayList<Object>();
        list.add(data);
      }

      // now loop through them
      for (Object item : list)
      {

        IConfigurationElement[] config =
            Platform.getExtensionRegistry().getConfigurationElementsFor(
                DATASET_ADAPTER_ID);
        for (IConfigurationElement e : config)
        {
          Object o = e.createExecutableExtension("class");
          if (o instanceof IStackedDatasetAdapter)
          {
            IStackedDatasetAdapter sa = (IStackedDatasetAdapter) o;

            List<Dataset> matches = sa.convertToDataset(item);

            if (matches != null)
            {
              if (res == null)
              {
                res = new ArrayList<Dataset>();
              }

              res.addAll(matches);
              // success, drop out
              break;
            }
          }
        }
      }
    }
    catch (Exception ex)
    {
      Activator.getDefault().getLog().log(
          new Status(Status.ERROR, Activator.PLUGIN_ID,
              "Failed to load stacked charts adapter", ex));
    }

    return res;
  }

  @Override
  public boolean canConvertToDataset(Object data)
  {
    boolean res = false;
    try
    {
      IConfigurationElement[] config =
          Platform.getExtensionRegistry().getConfigurationElementsFor(
              DATASET_ADAPTER_ID);
      for (IConfigurationElement e : config)
      {
        Object o = e.createExecutableExtension("class");
        
        if (o instanceof IStackedDatasetAdapter)
        {
          IStackedDatasetAdapter sa = (IStackedDatasetAdapter) o;
          if (sa.canConvertToDataset(data))
          {
            // success, drop out
            res = true;
            break;
          }
        }
      }
    }
    catch (Exception ex)
    {
      Activator.getDefault().getLog().log(
          new Status(Status.ERROR, Activator.PLUGIN_ID,
              "Failed to load stacked charts adapter", ex));
    }

    return res;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ScatterSet> convertToScatterSet(Object data)
  {
    List<ScatterSet> res = null;
    try
    {
      // ok, do we need to loop through the items?
      List<Object> list;
      if (data instanceof List<?>)
      {
        list = (List<Object>) data;
      }
      else
      {
        list = new ArrayList<Object>();
        list.add(data);
      }

      // now loop through them
      for (Object item : list)
      {

        IConfigurationElement[] config =
            Platform.getExtensionRegistry().getConfigurationElementsFor(
                SCATTERSET_ADAPTER_ID);
        for (IConfigurationElement e : config)
        {
          Object o = e.createExecutableExtension("class");
          if (o instanceof IStackedScatterSetAdapter)
          {
            IStackedScatterSetAdapter sa = (IStackedScatterSetAdapter) o;

            List<ScatterSet> matches = sa.convertToScatterSet(item);

            if (matches != null)
            {
              if (res == null)
              {
                res = new ArrayList<ScatterSet>();
              }

              res.addAll(matches);
              // success, drop out
              break;
            }
          }
        }
      }
    }
    catch (Exception ex)
    {
      Activator.getDefault().getLog().log(
          new Status(Status.ERROR, Activator.PLUGIN_ID,
              "Failed to load stacked charts adapter", ex));
    }

    return res;
  }

  @Override
  public boolean canConvertToScatterSet(Object data)
  {
    boolean res = false;
    try
    {
      IConfigurationElement[] config =
          Platform.getExtensionRegistry().getConfigurationElementsFor(
              SCATTERSET_ADAPTER_ID);
      for (IConfigurationElement e : config)
      {
        Object o = e.createExecutableExtension("class");
        if (o instanceof IStackedScatterSetAdapter)
        {
          IStackedScatterSetAdapter sa = (IStackedScatterSetAdapter) o;
          if (sa.canConvertToScatterSet(data))
          {
            // success, drop out
            res = true;
            break;
          }
        }
      }
    }
    catch (Exception ex)
    {
      Activator.getDefault().getLog().log(
          new Status(Status.ERROR, Activator.PLUGIN_ID,
              "Failed to load stacked charts adapter", ex));
    }

    return res;
  }

}
