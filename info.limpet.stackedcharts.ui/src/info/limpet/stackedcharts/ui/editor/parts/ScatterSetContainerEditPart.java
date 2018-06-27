package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalIconLabel;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalShape;
import info.limpet.stackedcharts.ui.editor.figures.ScatterSetContainerFigure;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart.ScatterSetContainer;
import info.limpet.stackedcharts.ui.editor.policies.ScatterSetContainerEditPolicy;

import java.util.List;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class ScatterSetContainerEditPart extends AbstractGraphicalEditPart
{

  private ScatterSetContainerFigure scatterSetContainerFigure;
  private DirectionalIconLabel titleLabel;

  @Override
  protected IFigure createFigure()
  {
    DirectionalShape figure = new DirectionalShape();
    titleLabel = new DirectionalIconLabel(StackedchartsImages.getImage(
        StackedchartsImages.DESC_SCATTERSET));
    figure.add(titleLabel);
    titleLabel.getLabel().setText("Scatterset");

    scatterSetContainerFigure = new ScatterSetContainerFigure();
    figure.add(scatterSetContainerFigure);
    return figure;
  }

  @Override
  public IFigure getContentPane()
  {
    return scatterSetContainerFigure;
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.CONTAINER_ROLE,
        new ScatterSetContainerEditPolicy());
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected List getModelChildren()
  {
    return (ScatterSetContainer) getModel();
  }

  @Override
  protected void refreshVisuals()
  {
    final DirectionalShape figure = (DirectionalShape) getFigure();

    ChartSet chartSet = ((Chart) getParent().getModel()).getParent();
    final boolean vertical = chartSet.getOrientation() == Orientation.VERTICAL;

    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure, vertical
        ? BorderLayout.BOTTOM : BorderLayout.RIGHT);

    figure.setVertical(!vertical);
    scatterSetContainerFigure.setVertical(!vertical);
    titleLabel.setVertical(!vertical);
  }
}
