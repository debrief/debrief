package info.limpet.stackedcharts.ui.editor.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.Styling;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart.ScatterSetContainer;

public class StackedChartsEditPartFactory implements EditPartFactory
{

  @Override
  public EditPart createEditPart(EditPart context, Object model)
  {
    EditPart editPart = null;

    if (model instanceof ChartSet)
    {
      editPart = new ChartSetEditPart();
    }
    else if (model instanceof Chart)
    {
      editPart = new ChartEditPart();
    }
    else if (model instanceof ChartEditPart.ChartPanePosition)
    {
      editPart = new ChartPaneEditPart();
    }
    else if (model instanceof DependentAxis)
    {
      editPart = new AxisEditPart();
    }
    else if (model instanceof Dataset)
    {
      editPart = new DatasetEditPart();
    }
    else if (model instanceof ChartSetEditPart.ChartsWrapper)
    {
      editPart = new ChartsPanelEditPart();
    }
    else if (model instanceof ChartSetEditPart.ChartSetWrapper)
    {
      editPart = new ChartSetHeaderEditPart();
    }
    else if (model instanceof ChartPaneEditPart.AxisLandingPad)
    {
      editPart = new AxisLandingPadEditPart();
    }
    else if (model instanceof IndependentAxis)
    {
      editPart = new SharedAxisEditPart();
    }
    else if (model instanceof Styling)
    {
      editPart = new StylingEditPart();
    }
    else if (model instanceof ScatterSetContainer)
    {
      editPart = new ScatterSetContainerEditPart();
    }
    else if (model instanceof ScatterSet) {
      editPart = new ScatterSetEditPart();
    }

    if (editPart != null)
    {
      editPart.setModel(model);
    }

    return editPart;
  }

}
