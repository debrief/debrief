package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class ChartSetEditPart extends AbstractGraphicalEditPart
{
  /**
   * Wraps the charts, so that they are displayed in a separate container and not together with the
   * shared axis.
   */
  public static class ChartsWrapper
  {
    private final List<Chart> charts;

    public ChartsWrapper(List<Chart> charts)
    {
      this.charts = charts;
    }

    public List<Chart> getCharts()
    {
      return charts;
    }
  }

  public static class ChartSetWrapper
  {
    private final ChartSet charts;

    public ChartSetWrapper(ChartSet charts)
    {
      this.charts = charts;
    }

    public ChartSet getcChartSet()
    {
      return charts;
    }
  }

  @Override
  protected void refreshVisuals()
  {
    GridLayout layoutManager = (GridLayout) getFigure().getLayoutManager();

    layoutManager.numColumns =
        getChartSet().getOrientation() == Orientation.HORIZONTAL
            ? getModelChildren().size() : 1;
    layoutManager.invalidate();
  }

  private ChartSetAdapter adapter = new ChartSetAdapter();

  @Override
  public void activate()
  {
    super.activate();
    getChartSet().eAdapters().add(adapter);
  }

  @Override
  public void deactivate()
  {
    getChartSet().eAdapters().remove(adapter);
    super.deactivate();
  }

  ChartSet getChartSet()
  {
    return (ChartSet) getModel();
  }

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure rectangle = new RectangleFigure();
    rectangle.setOutline(false);
    GridLayout gridLayout = new GridLayout();
    gridLayout.marginHeight = 10;
    gridLayout.marginWidth = 10;
    rectangle.setLayoutManager(gridLayout);
    rectangle.setBackgroundColor(Display.getDefault().getSystemColor(
        SWT.COLOR_WIDGET_BACKGROUND));

    return rectangle;
  }

  @Override
  protected void createEditPolicies()
  {
  }

  @Override
  public ChartSet getModel()
  {
    return (ChartSet) super.getModel();
  }

  public class ChartSetAdapter implements Adapter
  {

    @Override
    public void notifyChanged(Notification notification)
    {
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.CHART_SET__CHARTS:
        refreshChildren();
        break;
      case StackedchartsPackage.CHART_SET__ORIENTATION:
        refresh();
        break;
      }
    }

    @Override
    public Notifier getTarget()
    {
      return getModel();
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
      // Do nothing.
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return type.equals(ChartSet.class);
    }
  }

  @SuppressWarnings(
  {"rawtypes", "unchecked"})
  @Override
  protected List getModelChildren()
  {
    // 2 model children - the charts, displayed in a separate container and the shared (independent
    // axis) shown on the bottom
    List modelChildren = new ArrayList<>();
    ChartSet chartSet = getModel();
    modelChildren.add(new ChartSetWrapper(chartSet));
    modelChildren.add(new ChartsWrapper(chartSet.getCharts()));

    boolean horizontal = chartSet.getOrientation() == Orientation.HORIZONTAL;
    modelChildren.add(horizontal ? 1 : modelChildren.size(), chartSet
        .getSharedAxis());
    return modelChildren;
  }
}
