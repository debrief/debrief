package org.mwc.debrief.core.wizards.dynshapes;

import java.awt.Color;
import java.util.Date;

import org.eclipse.jface.wizard.Wizard;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicPolygonShapeWrapper;
import Debrief.Wrappers.DynamicShapeWrapper;
import Debrief.Wrappers.IDynamicShapeWrapper;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GenericData.HiResDate;

abstract public class CoreDynamicShapeWizard<WizardPageType extends DynamicShapeBaseWizardPage>
    extends Wizard
{
  private final String _shapeName;
  private final Date _startDate;
  private final Date _endDate;

  private IDynamicShapeWrapper _dynamicShape;

  private DynamicShapeTimingsWizardPage _shapeTimingsPage;
  private WizardPageType _boundsPage;
  private DynamicShapeStylingPage _stylingPage;

  public CoreDynamicShapeWizard(final String shapeName, final Date startDate,
      final Date endDate)
  {
    _shapeName = shapeName;
    _startDate = startDate;
    _endDate = endDate;
  }

  @Override
  public void addPages()
  {
    _shapeTimingsPage = new DynamicShapeTimingsWizardPage(
        DynamicShapeBaseWizardPage.TIMINGS_PAGE, _shapeName, _startDate,
        _endDate);
    _boundsPage = getBoundsPage();
    _stylingPage = new DynamicShapeStylingPage(
        DynamicShapeBaseWizardPage.STYLING_PAGE, _shapeName);
    addPage(_shapeTimingsPage);
    addPage(_boundsPage);
    addPage(_stylingPage);
  }

  /**
   * get the page that provides the bounds values
   *
   * @return
   */
  abstract protected WizardPageType getBoundsPage();

  public IDynamicShapeWrapper getDynamicShapeWrapper()
  {
    return _dynamicShape;
  }

  /**
   * retrieve the shape from the bounds page
   *
   * @return
   */
  abstract protected PlainShape getShape(WizardPageType boundsPage);

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish()
  {
    final Date startTime = _shapeTimingsPage.getStartTime();
    final Date endTime = _shapeTimingsPage.getEndTime();
    final PlainShape shape = getShape(_boundsPage);
    final Color theColor = ImportReplay.replayColorFor(_stylingPage
        .getSymbology());

    final HiResDate shapeDate = startTime == null ? null : new HiResDate(
        startTime);
    final HiResDate endDate = endTime == null?null:new HiResDate(endTime);
    if(shape instanceof PolygonShape) {
      _dynamicShape = new DynamicPolygonShapeWrapper(_stylingPage.getShapeLabel(),
          (PolygonShape) shape,
          theColor, shapeDate, endDate);
    }
    else {
      _dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(), shape,
        theColor, shapeDate, "dynamic " + _shapeName);
      if (endTime != null)
      {
        _dynamicShape.setEndDTG(new HiResDate(endDate));
      }
    }
    
    return true;
  }

}
