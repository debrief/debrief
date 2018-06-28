package info.limpet.stackedcharts.ui.editor.drop;

import info.limpet.stackedcharts.model.AxisType;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.NumberAxis;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.dnd.DropTargetEvent;

public class DatasetToChartDropTargetListener extends DatasetDropTargetListener
{
  public DatasetToChartDropTargetListener(GraphicalViewer viewer)
  {
    super(viewer);
  }

  @Override
  public boolean appliesTo(DropTargetEvent event)
  {
    EditPart findObjectAt = findPart(event);
    return findObjectAt instanceof ChartEditPart;
  }

  protected Command createCommand(AbstractGraphicalEditPart editPart,
      List<Dataset> datasets)
  {
    
    // just check what we've received
    if(!(editPart instanceof ChartEditPart))
    {
      System.err.println(this.toString() + " received wrong type of edit part");
    }
    
    CompoundCommand res = null;
    Chart chart = ((ChartEditPart) editPart).getModel();

    for (Dataset dataset : datasets)
    {
      // find a dataset to dump this dataset into
      String units = dataset.getUnits();

      DependentAxis targetAxis = null;

      if (targetAxis == null)
      {
        // ok, we have to go in at the chart level
        targetAxis = findAxisFor(chart.getMinAxes(), dataset.getUnits());

        if (targetAxis == null)
        {
          targetAxis = findAxisFor(chart.getMinAxes(), dataset.getUnits());
        }

        if (targetAxis == null)
        {
          // ok, just create a new one
          StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();
          targetAxis = factory.createDependentAxis();
          targetAxis.setName(units);
          NumberAxis aType = factory.createNumberAxis();
          aType.setUnits(units);
          targetAxis.setAxisType(aType);
          chart.getMinAxes().add(targetAxis);
        }
      }

      // ok, create the command
      AddDatasetsToAxisCommand command =
          new AddDatasetsToAxisCommand(targetAxis, dataset);
      if (res == null)
      {
        res = new CompoundCommand();
      }
      res.add(command);
    }

    return res;
  }


  private DependentAxis checkThisAxis(DependentAxis axis, String units)
  {
    DependentAxis res = null;
    AxisType aType = axis.getAxisType();
    if (aType != null && aType instanceof NumberAxis)
    {
      NumberAxis na = (NumberAxis) aType;
      String axisUnits = na.getUnits();
      if (axisUnits != null && units != null && axisUnits.equals(units))
      {
        res = axis;
      }
    }
    return res;
  }

  private DependentAxis findAxisFor(EList<DependentAxis> axes, String units)
  {
    DependentAxis res = null;
    for (DependentAxis axis : axes)
    {
      res = checkThisAxis(axis, units);
      if (res != null)
      {
        break;
      }
    }
    return res;
  }
}