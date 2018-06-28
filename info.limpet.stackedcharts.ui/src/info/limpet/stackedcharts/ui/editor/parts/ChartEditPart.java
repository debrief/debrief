package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.ui.editor.commands.DeleteChartCommand;
import info.limpet.stackedcharts.ui.editor.figures.ChartFigure;
import info.limpet.stackedcharts.ui.editor.policies.ChartContainerEditPolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ChartEditPart extends AbstractGraphicalEditPart implements
    ActionListener
{
  public static final Color BACKGROUND_COLOR = Display.getDefault()
      .getSystemColor(SWT.COLOR_WHITE);

  /**
   * Helper class to handle the container of {@link ScatterSet}s
   */
  @SuppressWarnings("serial")
  public static class ScatterSetContainer extends ArrayList<ScatterSet>
  {

  }

  public enum ChartPanePosition
  {
    MIN, MAX
  }

  final private ChartAdapter adapter = new ChartAdapter();
  final private SharedAxisAdapter sharedAxisAdapter = new SharedAxisAdapter();
  public final ArrayList<EAttribute> _visualUpdates;
  public final ArrayList<EReference> _childrenUpdates;
  
  public ChartEditPart()
  {
    
    // get our model definition
    final StackedchartsPackage pckg = StackedchartsPackage.eINSTANCE;

    // collate a list of what features trigger a visual update
    _visualUpdates = new ArrayList<EAttribute>();
    _visualUpdates.add(pckg.getChart_Name());
    _visualUpdates.add(pckg.getStyling_LineStyle());
    _visualUpdates.add(pckg.getStyling_LineThickness());
    _visualUpdates.add(pckg.getStyling_MarkerSize());
    _visualUpdates.add(pckg.getStyling_MarkerStyle());
    _visualUpdates.add(pckg.getPlainStyling_Color());
    
    // and now collate a list of which attributes trigger the
    // chidren to update
    _childrenUpdates = new ArrayList<EReference>();
    _childrenUpdates.add(pckg.getChart_MaxAxes());
    _childrenUpdates.add(pckg.getChart_MinAxes());
  }
  

  @Override
  public void activate()
  {
    super.activate();
    getModel().eAdapters().add(adapter);
    sharedAxisAdapter.attachTo(getSharedAxis());
  }

  @Override
  public void deactivate()
  {
    getModel().eAdapters().remove(adapter);
    // effectively detach the adapter/listener
    sharedAxisAdapter.attachTo(null);
    super.deactivate();
  }

  private IndependentAxis getSharedAxis()
  {
    return getModel().getParent().getSharedAxis();
  }

  @Override
  protected IFigure createFigure()
  {
    return new ChartFigure(getModel(), this);
  }

  @Override
  public Chart getModel()
  {
    return (Chart) super.getModel();
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
        new NonResizableEditPolicy());
    installEditPolicy(EditPolicy.CONTAINER_ROLE, new ChartContainerEditPolicy());

    installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy()
    {
      protected Command createDeleteCommand(GroupRequest deleteRequest)
      {
        Chart chart = getModel();
        ChartSet parent = chart.getParent();
        DeleteChartCommand deleteChartCommand =
            new DeleteChartCommand(parent, chart);
        return deleteChartCommand;
      }
    });
  }

  @SuppressWarnings(
  {"rawtypes", "unchecked"})
  @Override
  protected List getModelChildren()
  {
    List modelChildren = new ArrayList();
    modelChildren.addAll(Arrays.asList(ChartPanePosition.values()));
    ScatterSetContainer scatterSets = new ScatterSetContainer();
    for (SelectiveAnnotation annotation : getSharedAxis().getAnnotations())
    {
      if (annotation.getAnnotation() instanceof ScatterSet
          && annotation.getAppearsIn().contains(getModel()))
      {
        scatterSets.add((ScatterSet) annotation.getAnnotation());
      }
    }
    modelChildren.add(scatterSets);
    return modelChildren;
  }

  @Override
  protected void refreshVisuals()
  {
    String name = getModel().getName();
    ChartFigure chartFigure = (ChartFigure) getFigure();
    chartFigure.setName(name);
    chartFigure
        .setVertical(getModel().getParent().getOrientation() == Orientation.VERTICAL);

    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = SWT.FILL;
    gridData.verticalAlignment = SWT.FILL;

    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
        gridData);

  }

  protected void refreshChildren()
  {
    // remove all Childs
    @SuppressWarnings("unchecked")
    List<EditPart> children = getChildren();
    for (EditPart object : new ArrayList<EditPart>(children))
    {
      removeChild(object);
    }
    // add back all model elements
    @SuppressWarnings("rawtypes")
    List modelObjects = getModelChildren();
    for (int i = 0; i < modelObjects.size(); i++)
    {
      addChild(createChild(modelObjects.get(i)), i);

    }

    ((ChartFigure) getFigure()).getLayoutManager().layout(getFigure());
  }

  @Override
  public void actionPerformed(ActionEvent event)
  {
    Command deleteCommand = getCommand(new GroupRequest(REQ_DELETE));
    if (deleteCommand != null)
    {
      CommandStack commandStack = getViewer().getEditDomain().getCommandStack();
      commandStack.execute(deleteCommand);
    }

  }

  public class ChartAdapter extends EContentAdapter
  {

    @Override
    public void notifyChanged(Notification notification)
    {
      Object feature = notification.getFeature();

      // ok, now check if anything changed that causes a visual update
      for (final EAttribute thisA : _visualUpdates)
      {
        if (feature == thisA)
        {
          refreshVisuals();
          break;
        }
      }

      // ok, now check for a children update
      for (final EReference thisA : _childrenUpdates)
      {
        if (feature == thisA)
        {
          refreshChildren();
          break;
        }
      }
    }
  }

  /**
   * Update scatter sets in the scatter set container when model changes. Use an
   * {@link EContentAdapter}, since we'd like to be notified when multiple properties of different
   * objects in the shared axis get changed.
   */
  public class SharedAxisAdapter extends EContentAdapter
  {
    private IndependentAxis independentAxis;

    public void notifyChanged(Notification notification)
    {
      super.notifyChanged(notification);
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.INDEPENDENT_AXIS__ANNOTATIONS:
      case StackedchartsPackage.SELECTIVE_ANNOTATION__APPEARS_IN:
        refreshChildren();
        break;
      }
    }

    void attachTo(IndependentAxis independentAxis)
    {
      if (this.independentAxis != null)
      {
        this.independentAxis.eAdapters().remove(this);
      }
      this.independentAxis = independentAxis;
      if (this.independentAxis != null)
      {
        this.independentAxis.eAdapters().add(this);
      }
    }
  }
}
