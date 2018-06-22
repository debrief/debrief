package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.DependentAxis;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;

public class AddAxisToChartCommand extends Command
{
  private final DependentAxis[] axes;
  private final EList<DependentAxis> destination;

  public AddAxisToChartCommand(EList<DependentAxis> destination,
      DependentAxis... axes)
  {
    this.axes = axes;
    this.destination = destination;
  }

  @Override
  public void execute()
  {

    for (DependentAxis ds : axes)
    {

      destination.add(ds);
    }

  }

  @Override
  public void undo()
  {
    for (DependentAxis ds : axes)
    {

      destination.remove(ds);
    }
  }
}
