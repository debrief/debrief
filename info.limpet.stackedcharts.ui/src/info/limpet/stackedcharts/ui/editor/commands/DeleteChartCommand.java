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
