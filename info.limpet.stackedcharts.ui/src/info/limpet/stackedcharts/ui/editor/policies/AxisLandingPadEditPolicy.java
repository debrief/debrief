package info.limpet.stackedcharts.ui.editor.policies;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.ui.editor.commands.AddAxisToChartCommand;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.commands.MoveAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;
import info.limpet.stackedcharts.ui.editor.parts.AxisLandingPadEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart.ChartPanePosition;
import info.limpet.stackedcharts.ui.editor.parts.ChartPaneEditPart;
import info.limpet.stackedcharts.ui.editor.parts.DatasetEditPart;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

public class AxisLandingPadEditPolicy extends ContainerEditPolicy implements
    EditPolicy
{

  @Override
  public void eraseTargetFeedback(final Request request)
  {
    // remove the highlight
    if (REQ_ADD.equals(request.getType()))
    {
      final AxisLandingPadEditPart axisEditPart =
          (AxisLandingPadEditPart) getHost();
      final IFigure figure = axisEditPart.getFigure();
      figure.setBackgroundColor(AxisEditPart.BACKGROUND_COLOR);
    }
  }

  @Override
  protected Command getAddCommand(final GroupRequest request)
  {
    @SuppressWarnings("rawtypes")
    final List toAdd = request.getEditParts();
    final Command res;

    if (toAdd.size() == 0)
    {
      res = null;
    }
    else
    {
        
      final Object first = toAdd.get(0);
      if (first instanceof AxisEditPart)
      {
        CompoundCommand compoundCommand = new CompoundCommand();
        res = compoundCommand;
        // find the landing side
        final AxisLandingPadEditPart landingPadEditPart =
            (AxisLandingPadEditPart) getHost();
        final ChartPaneEditPart.AxisLandingPad pad =
            (ChartPaneEditPart.AxisLandingPad) landingPadEditPart.getModel();

        // find out which list (min/max) this axis is currently on
        final EList<DependentAxis> destination =
            pad.getPos() == ChartPanePosition.MIN ? pad.getChart()
                .getMinAxes() : pad.getChart().getMaxAxes();

        // ok, did we find it?
        if (destination != null)
        {
          for (final Object o : toAdd)
          {
            if (o instanceof AxisEditPart)
            {
              compoundCommand.add(new MoveAxisCommand(destination,
                  (DependentAxis) ((AxisEditPart) o).getModel()));
            }
          }
        }
      }
      else if (first instanceof DatasetEditPart)
      {

        // find the landing side
        final AxisLandingPadEditPart landingPadEditPart =
            (AxisLandingPadEditPart) getHost();
        final ChartPaneEditPart.AxisLandingPad pad =
            (ChartPaneEditPart.AxisLandingPad) landingPadEditPart.getModel();

        // find out which list (min/max) this axis is currently on
        final EList<DependentAxis> destination =
            pad.getPos() == ChartPanePosition.MIN ? pad.getChart()
                .getMinAxes() : pad.getChart().getMaxAxes();

        if (destination != null)
        {
          CompoundCommand compoundCommand = new CompoundCommand();
          res = compoundCommand;

          if (first instanceof DatasetEditPart)
          {
            // ok, it's a dataset. we need to wrap it into an axis before
            // we can add it to the chart
            DatasetEditPart datasetEditPart = (DatasetEditPart) first;
            AxisEditPart parent = (AxisEditPart) datasetEditPart.getParent();
            Dataset dataset = (Dataset) datasetEditPart.getModel();
            DependentAxis parentAxis = (DependentAxis) parent.getModel();
            
            // take a copy of the parent axis, since we know it's suitable
            DependentAxis newAxis = EcoreUtil.copy(parentAxis);
            newAxis.getDatasets().clear();
            compoundCommand.add(new AddAxisToChartCommand(destination, newAxis));
            compoundCommand.add(new AddDatasetsToAxisCommand(newAxis, dataset));
          }
        }
        else
        {
          // we don't have a target location
          res = null;
        }
      }
      else
      {
        // it's not a type that we're interested in
        res = null;
      }
    }
    return res;
  }

  @Override
  protected Command getCreateCommand(final CreateRequest request)
  {
    return null;
  }

  @Override
  public EditPart getTargetEditPart(final Request request)
  {
    if (REQ_ADD.equals(request.getType()))
    {
      return getHost();
    }
    return super.getTargetEditPart(request);
  }

  @Override
  public void showTargetFeedback(final Request request)
  {
    // highlight the Axis when user is about to drop a dataset on it
    if (REQ_ADD.equals(request.getType()))
    {
      final AxisLandingPadEditPart axisEditPart =
          (AxisLandingPadEditPart) getHost();
      final IFigure figure = axisEditPart.getFigure();
      figure.setBackgroundColor(ColorConstants.lightGray);
    }
  }

}
