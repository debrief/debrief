package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;

import org.eclipse.gef.commands.Command;

public class AddDatasetsToAxisCommand extends Command
{
  private final Dataset[] datasets;
  private final DependentAxis parent;

  public AddDatasetsToAxisCommand(DependentAxis parent, Dataset... datasets)
  {
    this.datasets = datasets;
    this.parent = parent;
  }

  @Override
  public void execute()
  {
    for (Dataset ds : datasets)
    {
      parent.getDatasets().add(ds);
    }
  }

  @Override
  public void undo()
  {
    for (Dataset ds : datasets)
    {
      parent.getDatasets().remove(ds);
    }
  }
}
