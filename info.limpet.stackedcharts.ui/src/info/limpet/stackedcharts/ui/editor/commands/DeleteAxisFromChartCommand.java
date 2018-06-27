package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.DependentAxis;

import org.eclipse.gef.commands.Command;

public class DeleteAxisFromChartCommand extends Command
{
  private final DependentAxis[] datasets;
  private final Chart parent;
  private boolean onMin = false;
  private boolean onMax = false;

  public DeleteAxisFromChartCommand(Chart parent, DependentAxis... datasets)
  {
    this.datasets = datasets;
    this.parent = parent;
  }

  @Override
  public void execute()
  {
    for (DependentAxis ds : datasets)
    {
      if (parent.getMinAxes().contains(ds))
      {
        onMin = true;
        parent.getMinAxes().remove(ds);
      }
      if (parent.getMaxAxes().contains(ds))
      {
        onMax = true;
        parent.getMaxAxes().remove(ds);
      }

      parent.getMaxAxes().remove(ds);
    }
  }

  @Override
  public void undo()
  {
    for (DependentAxis ds : datasets)
    {
      if (onMin)
      {
        parent.getMinAxes().add(ds);
      }
      if (onMax)
      {
        parent.getMaxAxes().add(ds);
      }
    }
  }
}
