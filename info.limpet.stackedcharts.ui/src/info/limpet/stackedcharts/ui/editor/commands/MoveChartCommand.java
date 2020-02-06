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

import java.util.List;

import org.eclipse.gef.commands.Command;

public class MoveChartCommand extends Command
{
  private final Chart chart;
  private final List<Chart> charts;
  private final int newIndex;
  private final int oldIndex;

  public MoveChartCommand(final List<Chart> charts, final Chart chart,
      final int index)
  {
    this.chart = chart;
    this.charts = charts;
    this.newIndex = index;
    this.oldIndex = charts.indexOf(chart);
  }

  @Override
  public void execute()
  {
    charts.remove(chart);
    if (newIndex != -1)
    {
      charts.add(newIndex, chart);
    }
    else
    {
      charts.add(chart);
    }
  }

  @Override
  public void undo()
  {
    charts.remove(chart);
    if (oldIndex != -1)
    {
      charts.add(oldIndex, chart);
    }
    else
    {
      charts.add(chart);
    }
  }
}
