package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.AxisType;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.ui.editor.commands.DeleteAxisFromChartCommand;
import info.limpet.stackedcharts.ui.editor.figures.ArrowFigure;
import info.limpet.stackedcharts.ui.editor.figures.AxisNameFigure;
import info.limpet.stackedcharts.ui.editor.policies.AxisContainerEditPolicy;

import java.util.List;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.SimpleLoweredBorder;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
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
import org.eclipse.ui.views.properties.IPropertySource;

public class AxisEditPart extends AbstractGraphicalEditPart implements
    ActionListener, IPropertySourceProvider
{

  public static final Color BACKGROUND_COLOR = Display.getDefault()
      .getSystemColor(SWT.COLOR_WHITE);

  private RectangleFigure datasetsPane;

  private AxisNameFigure axisNameLabel;

  private AxisAdapter adapter = new AxisAdapter();

  private ArrowFigure arrowFigure;

  @Override
  public void activate()
  {
    super.activate();
    getAxis().eAdapters().add(adapter);
  }

  @Override
  public void deactivate()
  {
    getAxis().eAdapters().remove(adapter);
    super.deactivate();
  }

  protected DependentAxis getAxis()
  {
    return (DependentAxis) getModel();
  }

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure figure = new RectangleFigure();
    figure.setBackgroundColor(BACKGROUND_COLOR);
    Color borderCol = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
    Border figureBorder = new LineBorder(borderCol, 2);
    figure.setBorder(figureBorder);

    figure.setOutline(false);
    GridLayout layoutManager = new GridLayout();
    // zero margin, in order to connect the dependent axes to the shared one
    layoutManager.marginHeight = 0;
    layoutManager.marginWidth = 0;
    figure.setLayoutManager(layoutManager);

    datasetsPane = new RectangleFigure();
    datasetsPane.setOutline(false);
    final SimpleLoweredBorder datasetBorder = new SimpleLoweredBorder(3);
    datasetsPane.setBorder(datasetBorder);
    GridLayout datasetsPaneLayout = new GridLayout();
    datasetsPane.setLayoutManager(datasetsPaneLayout);
    figure.add(datasetsPane);

    arrowFigure = new ArrowFigure(false);
    figure.add(arrowFigure);

    axisNameLabel = new AxisNameFigure(this);
    figure.add(axisNameLabel);

    return figure;
  }

  @Override
  protected void refreshVisuals()
  {
    axisNameLabel.setName(getAxis().getName());

    GraphicalEditPart parent = (GraphicalEditPart) getParent();

    boolean horizontal =
        ((ChartSet) parent.getParent().getParent().getParent().getModel())
            .getOrientation() == Orientation.HORIZONTAL;

    GridLayout layout = (GridLayout) getFigure().getLayoutManager();
    if (horizontal)
    {
      layout.numColumns = 1;
      parent.setLayoutConstraint(this, figure, new GridData(GridData.FILL,
          GridData.CENTER, true, false));

      layout.setConstraint(datasetsPane, new GridData(GridData.FILL,
          GridData.CENTER, true, false));

      layout.setConstraint(arrowFigure, new GridData(GridData.FILL,
          GridData.CENTER, true, false));
      layout.setConstraint(axisNameLabel, new GridData(GridData.FILL,
          GridData.CENTER, true, false));

      axisNameLabel.setVertical(false);
      arrowFigure.setHorizontal(true);
    }
    else
    {
      layout.numColumns = figure.getChildren().size();
      parent.setLayoutConstraint(this, figure, new GridData(GridData.CENTER,
          GridData.FILL, false, true));

      layout.setConstraint(datasetsPane, new GridData(GridData.CENTER,
          GridData.FILL, false, true));

      layout.setConstraint(arrowFigure, new GridData(GridData.CENTER,
          GridData.FILL, false, true));
      layout.setConstraint(axisNameLabel, new GridData(GridData.CENTER,
          GridData.FILL, false, true));

      axisNameLabel.setVertical(true);
      arrowFigure.setHorizontal(false);
    }
    layout.invalidate();
    parent.refresh();

    GridLayout layoutManager = (GridLayout) datasetsPane.getLayoutManager();
    layoutManager.numColumns = horizontal ? 1 : getModelChildren().size();
    layoutManager.invalidate();

  }

  @Override
  public IFigure getContentPane()
  {
    return datasetsPane;
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
        new NonResizableEditPolicy());

    installEditPolicy(EditPolicy.CONTAINER_ROLE, new AxisContainerEditPolicy());

    installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy()
    {
      protected Command createDeleteCommand(GroupRequest deleteRequest)
      {
        DependentAxis dataset = (DependentAxis) getHost().getModel();
        Chart parent = (Chart) dataset.eContainer();
        DeleteAxisFromChartCommand cmd =
            new DeleteAxisFromChartCommand(parent, dataset);
        return cmd;
      }
    });
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected List getModelChildren()
  {
    return getAxis().getDatasets();
  }

  public class AxisAdapter implements Adapter
  {

    @Override
    public void notifyChanged(Notification notification)
    {
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
        refreshChildren();
      case StackedchartsPackage.ABSTRACT_AXIS__NAME:
        refreshVisuals();
      }
    }

    @Override
    public Notifier getTarget()
    {
      return getAxis();
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return type.equals(DependentAxis.class);
    }
  }

  @Override
  public void actionPerformed(org.eclipse.draw2d.ActionEvent event)
  {
    Command deleteCommand = getCommand(new GroupRequest(REQ_DELETE));
    if (deleteCommand != null)
    {
      CommandStack commandStack = getViewer().getEditDomain().getCommandStack();
      commandStack.execute(deleteCommand);
    }
  }

  @Override
  public IPropertySource getPropertySource()
  {
    final DependentAxis axis = getAxis();
    final AxisType axisType = getAxis().getAxisType();

    // Proxy two objects in to one
    return new CombinedProperty(axis, axisType, "Axis type");
  }

}
