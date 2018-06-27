package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.model.Styling;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;
import info.limpet.stackedcharts.ui.editor.commands.DeleteDatasetsFromAxisCommand;
import info.limpet.stackedcharts.ui.editor.figures.DatasetFigure;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalShape;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * An {@link GraphicalEditPart} to represent datasets
 */
public class DatasetEditPart extends AbstractGraphicalEditPart implements
    ActionListener, IPropertySourceProvider
{

  private DatasetFigure contentPane;

  private DatasetAdapter adapter = new DatasetAdapter();

  @Override
  protected IFigure createFigure()
  {
    DirectionalShape figure = new DirectionalShape();

    contentPane = new DatasetFigure();
    figure.add(contentPane);

    Button button =
        new Button(StackedchartsImages
            .getImage(StackedchartsImages.DESC_DELETE));
    button.setToolTip(new Label("Remove the dataset from this axis"));
    button.addActionListener(this);
    figure.add(button);
    return figure;
  }

  @Override
  protected void addChildVisual(EditPart childEditPart, int index)
  {
    super.addChildVisual(childEditPart, getContentPane().getChildren().size());
  }

  @Override
  public IFigure getContentPane()
  {
    return contentPane;
  }
  

  @Override
  public IPropertySource getPropertySource()
  {
    final Dataset axis = getDataset();
    final Styling axisType = getDataset().getStyling();

    // Proxy two objects in to one
    return new CombinedProperty(axis, axisType, "Styling");
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
        Dataset dataset = (Dataset) getHost().getModel();
        DependentAxis parent = (DependentAxis) getHost().getParent().getModel();
        DeleteDatasetsFromAxisCommand cmd =
            new DeleteDatasetsFromAxisCommand(parent, dataset);
        return cmd;
      }
    });
  }

  @Override
  public void activate()
  {
    super.activate();
    getDataset().eAdapters().add(adapter);
  }

  @Override
  public void deactivate()
  {
    getDataset().eAdapters().remove(adapter);
    super.deactivate();
  }

  @Override
  protected void refreshVisuals()
  {
    contentPane.setName(getDataset().getName());

    ChartSet parent =
        ((Chart) getParent().getParent().getParent().getModel()).getParent();

    boolean horizontal = parent.getOrientation() == Orientation.HORIZONTAL;
    ((DirectionalShape) getFigure()).setVertical(!horizontal);

    if (horizontal)
    {
      contentPane.setVertical(false);
      setLayoutConstraint(this, getFigure(), new GridData(GridData.FILL,
          GridData.FILL, true, false));

    }
    else
    {
      contentPane.setVertical(true);
      setLayoutConstraint(this, getFigure(), new GridData(GridData.CENTER,
          GridData.FILL, false, true));

    }
  }

  protected Dataset getDataset()
  {
    return (Dataset) getModel();
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

  public class DatasetAdapter implements Adapter
  {

    @Override
    public void notifyChanged(Notification notification)
    {
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.DATASET__STYLING:
        refreshChildren();
        break;
      case StackedchartsPackage.DATASET__NAME:
        refreshVisuals();
        break;
      }
    }

    @Override
    public Notifier getTarget()
    {
      return getDataset();
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return type.equals(Dataset.class);
    }
  }
}
