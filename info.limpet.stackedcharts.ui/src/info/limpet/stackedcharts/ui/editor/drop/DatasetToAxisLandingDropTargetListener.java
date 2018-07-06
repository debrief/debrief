package info.limpet.stackedcharts.ui.editor.drop;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.editor.commands.AddAxisToChartCommand;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.AxisLandingPadEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart.ChartPanePosition;
import info.limpet.stackedcharts.ui.editor.parts.ChartPaneEditPart;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.dnd.DropTargetEvent;

public class DatasetToAxisLandingDropTargetListener extends
    DatasetDropTargetListener
{
  public DatasetToAxisLandingDropTargetListener(GraphicalViewer viewer)
  {
    super(viewer);
  }

  @Override
  public boolean appliesTo(DropTargetEvent event)
  {
    EditPart findObjectAt = findPart(event);
    return findObjectAt instanceof AxisLandingPadEditPart;
  }

  protected Command createCommand(AbstractGraphicalEditPart axis,
      List<Dataset> datasets)
  {
    CompoundCommand compoundCommand = new CompoundCommand();
    
    // get the dimensions of the first dataset
    final String units;
    if (datasets != null && datasets.size() > 0 && datasets.get(0).getUnits() != null)
    {
      final Dataset dataset = datasets.get(0);
      units = dataset.getUnits();
    }
    else
    {
      units = "[dimensionless]";
    }

    StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();
    DependentAxis newAxis = factory.createDependentAxis();
    newAxis.setName(units);
    newAxis.setAxisType(factory.createNumberAxis());

    final ChartPaneEditPart.AxisLandingPad pad =
        (ChartPaneEditPart.AxisLandingPad) axis.getModel();
    // find out which list (min/max) this axis is currently on
    final EList<DependentAxis> destination =
        pad.getPos() == ChartPanePosition.MIN ? pad.getChart().getMinAxes()
            : pad.getChart().getMaxAxes();

    compoundCommand.add(new AddAxisToChartCommand(destination, newAxis));

    AddDatasetsToAxisCommand addDatasetsToAxisCommand =
        new AddDatasetsToAxisCommand(newAxis, datasets
            .toArray(new Dataset[datasets.size()]));
    compoundCommand.add(addDatasetsToAxisCommand);
    return compoundCommand;
  }

}
