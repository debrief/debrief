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
package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;

import org.eclipse.gef.commands.Command;

public class DeleteChartCommand extends Command
{
  private final Chart chart;
  private final ChartSet parent;
  private int index = -1;

  public DeleteChartCommand(final ChartSet parent, final Chart chart)
  {
    this.chart = chart;
    this.parent = parent;
  }

  @Override
  public void execute()
  {
    index = parent.getCharts().indexOf(chart);
    parent.getCharts().remove(chart);
  }

  @Override
  public void undo()
  {
    if (index != -1)
    {
      parent.getCharts().add(index, chart);
    }
    else
    {
      parent.getCharts().add(chart);
    }
  }
}
