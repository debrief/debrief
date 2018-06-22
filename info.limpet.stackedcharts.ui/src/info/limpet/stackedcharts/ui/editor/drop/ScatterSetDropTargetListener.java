package info.limpet.stackedcharts.ui.editor.drop;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ScatterSetContainerEditPart;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * base for classes supporting the drop process, including establishing if the target is valid
 * 
 * @author ian
 * 
 */
abstract public class ScatterSetDropTargetListener extends
    CoreDropTargetListener
{

  protected AbstractGraphicalEditPart feedback;

  protected ScatterSetDropTargetListener(GraphicalViewer viewer)
  {
    super(viewer);
  }

  /**
   * wrap up the data change for the drop event
   * 
   * @param chart
   * @param scatterSets
   * @return
   */
  abstract protected Command createScatterCommand(Chart chart,
      List<ScatterSet> scatterSets);

  protected static boolean datasetAlreadyExistsOnTheseAxes(
      final Iterator<DependentAxis> axes, final String name)
  {
    boolean exists = false;

    while (axes.hasNext())
    {
      final DependentAxis dAxis = (DependentAxis) axes.next();
      Iterator<Dataset> dIter = dAxis.getDatasets().iterator();
      while (dIter.hasNext())
      {
        Dataset thisD = (Dataset) dIter.next();
        if (name.equals(thisD.getName()))
        {
          // ok, we can't add it
          System.err.println("Not adding dataset - duplicate name");
          exists = true;
          break;
        }
      }
    }

    return exists;
  }

  @Override
  public void drop(DropTargetEvent event)
  {
    if (LocalSelectionTransfer.getTransfer().isSupportedType(
        event.currentDataType))
    {
      StructuredSelection sel =
          (StructuredSelection) LocalSelectionTransfer.getTransfer()
              .getSelection();
      if (sel.isEmpty())
      {
        event.detail = DND.DROP_NONE;
        return;
      }
      List<ScatterSet> scatterSets = convertSelectionToScatterSet(sel);
      EditPart part = findPart(event);

      AbstractGraphicalEditPart target = (AbstractGraphicalEditPart) part;

      // ok, now build up the commands necessary to 
      // make the changes

      Command scatterCommand;
      if (scatterSets.size() > 0)
      {
        Chart chart = null;

        // get the target - we need the chart
        if (target instanceof AxisEditPart)
        {
          AxisEditPart axis = (AxisEditPart) target;
          chart = (Chart) axis.getParent().getModel();
        }
        else if (target instanceof ChartEditPart)
        {
          chart = (Chart) target.getModel();
        }
        else if(target instanceof ScatterSetContainerEditPart)
        {
          ScatterSetContainerEditPart scatter = (ScatterSetContainerEditPart) target;
          chart = (Chart) scatter.getParent().getModel();
        }

        scatterCommand = createScatterCommand(chart, scatterSets);
      }
      else
      {
        scatterCommand = null;
      }

      if (scatterCommand != null)
      {
        getCommandStack().execute(scatterCommand);
      }
    }

    feedback = null;
  }

}
