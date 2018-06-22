package info.limpet.stackedcharts.ui.editor.parts;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**
 * An edit part for Scatter Set object
 */
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;
import info.limpet.stackedcharts.ui.editor.commands.DeleteScatterSetCommand;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalIconLabel;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalShape;

public class ScatterSetEditPart extends AbstractGraphicalEditPart implements
    ActionListener
{

  private ScatterSetAdapter adapter = new ScatterSetAdapter();

  private DirectionalIconLabel scatterSetNameLabel;

  @Override
  protected IFigure createFigure()
  {
    DirectionalShape figure = new DirectionalShape();
    scatterSetNameLabel = new DirectionalIconLabel(StackedchartsImages.getImage(
        StackedchartsImages.DESC_DATASET));
    figure.add(scatterSetNameLabel);
    final Button button = new Button(StackedchartsImages.getImage(
        StackedchartsImages.DESC_DELETE));
    button.setToolTip(new Label("Remove scatter set"));
    button.addActionListener(this);
    figure.add(button);

    return figure;
  }

  @Override
  public ScatterSet getModel()
  {
    return (ScatterSet) super.getModel();
  }

  @Override
  public void activate()
  {
    super.activate();
    getModel().eAdapters().add(adapter);
  }

  @Override
  public void deactivate()
  {
    getModel().eAdapters().remove(adapter);
    super.deactivate();
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
        new NonResizableEditPolicy());

    installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy()
    {
      protected Command createDeleteCommand(GroupRequest deleteRequest)
      {
        // TODO: implement
        // 1. do not use this scatter set in the current chart
        // 2. if scatter set used only here, then delete scatter set from shared axis
        return new DeleteScatterSetCommand(getModel(), getChart());
      }
    });
  }

  @Override
  protected void refreshVisuals()
  {
    super.refreshVisuals();
    ScatterSet scatterSet = getModel();
    String name = scatterSet.getName();
    scatterSetNameLabel.getLabel().setText(name != null ? name : "<unnamed>");

    ChartSet chartSet = getChart().getParent();
    boolean vertical = chartSet.getOrientation() == Orientation.VERTICAL;
    ((DirectionalShape) getFigure()).setVertical(!vertical);
    scatterSetNameLabel.setVertical(!vertical);
  }

  public Chart getChart()
  {
    return (Chart) getParent().getParent().getModel();
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

  public class ScatterSetAdapter implements Adapter
  {

    @Override
    public void notifyChanged(Notification notification)
    {
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.SCATTER_SET__NAME:
        refreshVisuals();
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
      return type.equals(ScatterSet.class);
    }
  }

}
