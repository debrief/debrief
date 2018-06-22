package info.limpet.stackedcharts.ui.editor.drop;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.dnd.DropTargetEvent;

public class DatasetToAxisDropTargetListener extends DatasetDropTargetListener
{
  public DatasetToAxisDropTargetListener(GraphicalViewer viewer)
  {
    super(viewer);
  }

  @Override
  public boolean appliesTo(DropTargetEvent event)
  {
    EditPart findObjectAt = findPart(event);
    return findObjectAt instanceof AxisEditPart;
  }

  protected Command createCommand(AbstractGraphicalEditPart axis,
      List<Dataset> datasets)
  {
    AddDatasetsToAxisCommand addDatasetsToAxisCommand =
        new AddDatasetsToAxisCommand((DependentAxis) axis.getModel(),
            datasets.toArray(new Dataset[datasets.size()]));
    return addDatasetsToAxisCommand;
  }

}