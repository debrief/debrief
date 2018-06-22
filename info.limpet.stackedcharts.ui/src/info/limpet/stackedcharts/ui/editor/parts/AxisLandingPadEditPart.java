package info.limpet.stackedcharts.ui.editor.parts;

import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.ui.editor.Activator;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalLabel;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart.ChartPanePosition;
import info.limpet.stackedcharts.ui.editor.parts.ChartPaneEditPart.AxisLandingPad;
import info.limpet.stackedcharts.ui.editor.policies.AxisLandingPadEditPolicy;

public class AxisLandingPadEditPart extends AbstractGraphicalEditPart
{

  private DirectionalLabel nameLabel;

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.CONTAINER_ROLE,
        new AxisLandingPadEditPolicy());
  }

  @Override
  protected IFigure createFigure()
  {
    final RectangleFigure figure = new RectangleFigure();
    figure.setOutline(false);
    final Color borderCol = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
    final Border figureBorder = new LineBorder(borderCol, 2);
    figure.setBorder(figureBorder);

    figure.setLayoutManager(new GridLayout());
    nameLabel = new DirectionalLabel(Activator.FONT_8);

    final ChartPaneEditPart.AxisLandingPad pad =
        (ChartPaneEditPart.AxisLandingPad) getModel();

    nameLabel.setText(pad.pos == ChartPanePosition.MIN ? "Min Axis"
        : "Max Axis");

    figure.add(nameLabel);
    figure.getLayoutManager().setConstraint(nameLabel, new GridData(
        GridData.FILL, GridData.FILL, true, true));

    return figure;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected List getModelChildren()
  {
    return Arrays.asList();
  }

  @Override
  protected void refreshVisuals()
  {
    boolean horizontal = ((AxisLandingPad) getModel()).chart.getParent()
        .getOrientation() == Orientation.HORIZONTAL;
    nameLabel.setVertical(!horizontal);

    if (horizontal)
    {
      ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
          new GridData(GridData.FILL, GridData.CENTER, true, false));
    }
    else
    {
      ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
          new GridData(GridData.CENTER, GridData.FILL, false, true));
    }
    figure.invalidate();
  }
}
