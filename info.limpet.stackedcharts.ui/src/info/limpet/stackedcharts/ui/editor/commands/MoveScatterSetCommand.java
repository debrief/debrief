package info.limpet.stackedcharts.ui.editor.commands;

import org.eclipse.gef.commands.Command;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;

public class MoveScatterSetCommand extends Command
{

  private final Chart from;
  private final Chart to;
  private final SelectiveAnnotation selectiveAnnotation;

  public MoveScatterSetCommand(ScatterSet scatterSet, Chart from, Chart to)
  {
    this.from = from;
    this.to = to;
    this.selectiveAnnotation = AddScatterSetsToChartCommand
        .findAnnotationByName(scatterSet.getName(), from.getParent());
  }

  @Override
  public void execute()
  {
    selectiveAnnotation.getAppearsIn().remove(from);
    selectiveAnnotation.getAppearsIn().add(to);
  }

  @Override
  public void undo()
  {
    selectiveAnnotation.getAppearsIn().add(from);
    selectiveAnnotation.getAppearsIn().remove(to);
  }
}
