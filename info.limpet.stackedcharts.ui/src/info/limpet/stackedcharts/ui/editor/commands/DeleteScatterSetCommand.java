package info.limpet.stackedcharts.ui.editor.commands;

import org.eclipse.gef.commands.Command;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;

public class DeleteScatterSetCommand extends Command
{
  private final SelectiveAnnotation annotation;
  private final Chart chart;
  private boolean deleted;

  public DeleteScatterSetCommand(ScatterSet scatterSet, Chart chart)
  {
    this.annotation = AddScatterSetsToChartCommand.findAnnotationByName(
        scatterSet.getName(), chart.getParent());
    this.chart = chart;
  }

  @Override
  public void execute()
  {
    annotation.getAppearsIn().remove(chart);
    // delete the annotation as well
    if (annotation.getAppearsIn().isEmpty())
    {
      chart.getParent().getSharedAxis().getAnnotations().remove(annotation);
      deleted = true;
    }
  }

  @Override
  public void undo()
  {
    annotation.getAppearsIn().add(chart);
    if (deleted)
    {
      chart.getParent().getSharedAxis().getAnnotations().add(annotation);
      deleted = false;
    }
  }
}
