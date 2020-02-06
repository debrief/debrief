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
package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.ui.editor.commands.AddChartCommand;
import info.limpet.stackedcharts.ui.editor.figures.ChartsetFigure;

import java.util.List;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;

/**
 * Represents header of a {@link ChartSet} object
 */
public class ChartSetHeaderEditPart extends AbstractGraphicalEditPart implements
    ActionListener
{

  @Override
  public void actionPerformed(final ActionEvent event)
  {
    final List<Chart> charts = getModel().getCharts();
    final StackedchartsFactory factory = StackedchartsFactory.eINSTANCE;
    final Chart chart = factory.createChart();
    chart.setName("New Chart");
    final AddChartCommand addChartCommand = new AddChartCommand(charts, chart);
    final CommandStack commandStack = getViewer().getEditDomain()
        .getCommandStack();
    commandStack.execute(addChartCommand);
  }

  @Override
  protected void createEditPolicies()
  {
  }

  @Override
  protected IFigure createFigure()
  {
    return new ChartsetFigure(this);
  }

  @Override
  public ChartSet getModel()
  {
    return ((ChartSetEditPart.ChartSetWrapper) super.getModel()).getcChartSet();
  }

  @Override
  protected void refreshVisuals()
  {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.CENTER;
    gridData.verticalAlignment = SWT.CENTER;

    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
        gridData);
    ((ChartsetFigure) getFigure()).setVertical(getModel()
        .getOrientation() == Orientation.HORIZONTAL);
  }
}
