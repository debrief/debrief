/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package info.limpet.stackedcharts.ui.editor.policies;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.ui.editor.commands.MoveChartCommand;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartsPanelEditPart;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

public class ChartContainerEditPolicy extends ContainerEditPolicy implements
    EditPolicy
{

  @Override
  public void eraseTargetFeedback(final Request request)
  {
    // remove the highlight
    if (REQ_ADD.equals(request.getType()))
    {
      final ChartEditPart axisEditPart = getHost();
      final IFigure figure = axisEditPart.getFigure();
      figure.setBackgroundColor(ChartEditPart.BACKGROUND_COLOR);
    }
  }

  @Override
  protected Command getAddCommand(final GroupRequest request)
  {
    @SuppressWarnings("rawtypes")
    final List toAdd = request.getEditParts();

    CompoundCommand res = null;

    if (toAdd.size() > 0)
    {
      final Object first = toAdd.get(0);
      if (first instanceof ChartEditPart)
      {
        res = new CompoundCommand();
        final List<Chart> charts =
            ((ChartsPanelEditPart) getHost().getParent()).getModel()
                .getCharts();
        for (final Object o : toAdd)
        {
          if (o instanceof ChartEditPart)
          {
            final ChartEditPart chartEditPart = (ChartEditPart) o;
            final ChartEditPart hostPart = getHost();
            int indexOfHost = charts.indexOf(hostPart.getModel());
            int newindex = indexOfHost--;
            if(newindex<0)
              newindex = 0;
            res.add(new MoveChartCommand(charts, chartEditPart.getModel(),
                newindex));
          }
        }
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
  public ChartEditPart getHost()
  {
    return (ChartEditPart) super.getHost();
  }

  @Override
  public EditPart getTargetEditPart(final Request request)
  {
    if (REQ_ADD.equals(request.getType()))
    {
      return getHost();
    }
    if (REQ_CREATE.equals(request.getType()))
    {
      return getHost();
    }
    return super.getTargetEditPart(request);
  }

  @Override
  public void showTargetFeedback(final Request request)
  {
    if (REQ_ADD.equals(request.getType()))
    {
      final ChartEditPart axisEditPart = getHost();
      final IFigure figure = axisEditPart.getFigure();
      figure.setBackgroundColor(ColorConstants.lightGray);
    }
  }
}
