package info.limpet.stackedcharts.ui.editor.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsFactory;

public class AddScatterSetsToChartCommand extends Command
{
  private final ScatterSet[] scatterSets;
  private final Chart parent;

  /**
   * Contains all newly created annotations during {@link #execute()} that need to be removed during
   * {@link #undo()}
   */
  private List<SelectiveAnnotation> createdAnnotations;

  /**
   * Contains annotations which have been added to appear in the parent. Again the parent needs to
   * be removed from those during {@link #undo()}.
   */
  private List<SelectiveAnnotation> appearInParent;

  public AddScatterSetsToChartCommand(Chart parent, ScatterSet... scatterSets)
  {
    this.scatterSets = scatterSets;
    this.parent = parent;
  }

  @Override
  public void execute()
  {

    createdAnnotations = new ArrayList<SelectiveAnnotation>();
    appearInParent = new ArrayList<SelectiveAnnotation>();

    for (ScatterSet ds : scatterSets)
    {
      // ok, we may have to add it to the chartset first
      ChartSet charts = parent.getParent();
      EList<SelectiveAnnotation> annots = charts.getSharedAxis()
          .getAnnotations();
      SelectiveAnnotation host = findAnnotationByName(ds.getName(), charts);

      if (host == null)
      {
        host = StackedchartsFactory.eINSTANCE.createSelectiveAnnotation();
        host.setAnnotation(ds);
        annots.add(host);
        createdAnnotations.add(host);
      }

      // check we're not already in that chart
      EList<Chart> appearsIn = host.getAppearsIn();
      if (!appearsIn.contains(parent))
      {
        appearsIn.add(parent);
        appearInParent.add(host);
      }
    }
  }

  public static SelectiveAnnotation findAnnotationByName(String annotationName,
      ChartSet charts)
  {
    SelectiveAnnotation host = null;
    for (SelectiveAnnotation annot : charts.getSharedAxis().getAnnotations())
    {
      if (annot.getAnnotation().getName() != null && annot.getAnnotation()
          .getName().equals(annotationName))
      {
        host = annot;
        break;
      }
    }
    return host;
  }

  @Override
  public void undo()
  {
    for (SelectiveAnnotation annotation : appearInParent)
    {
      annotation.getAppearsIn().remove(parent);
    }

    ChartSet charts = parent.getParent();
    EList<SelectiveAnnotation> annotations = charts.getSharedAxis()
        .getAnnotations();
    for (SelectiveAnnotation annotation : createdAnnotations)
    {
      annotations.remove(annotation);
    }
  }
}