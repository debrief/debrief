package org.mwc.debrief.core.wizards.dynshapes;

import java.awt.Color;
import java.util.Date;

import org.eclipse.jface.wizard.Wizard;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicShapeWrapper;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.HiResDate;

abstract public class CoreDynamicShapeWizard extends Wizard
{

  private final String SHAPE_NAME;
  private Date _startDate;
  private Date _endDate;

  private DynamicShapeWrapper _dynamicShape;

  private DynamicShapeTimingsWizardPage _shapeTimingsPage;
  protected DynamicShapeBaseWizardPage _boundsPage;
  private DynamicShapeStylingPage _stylingPage;
  
  public CoreDynamicShapeWizard(String shapeName, Date startDate,Date endDate)
  {
    SHAPE_NAME = shapeName;
    _startDate = startDate;
    _endDate = endDate;
  }
  
  public DynamicShapeWrapper getDynamicShapeWrapper()
  {
    return _dynamicShape;
  }
  
  @Override
  public void addPages()
  {
    _shapeTimingsPage = new DynamicShapeTimingsWizardPage(DynamicShapeBaseWizardPage.TIMINGS_PAGE,SHAPE_NAME,_startDate,_endDate);
    _boundsPage = getBoundsPage();
    _stylingPage = new DynamicShapeStylingPage(DynamicShapeBaseWizardPage.STYLING_PAGE, SHAPE_NAME);
    addPage(_shapeTimingsPage);
    addPage(_boundsPage);
    addPage(_stylingPage);
  }
  
  /** get the page that provides the bounds values
   * 
   * @return
   */
  abstract DynamicShapeBaseWizardPage getBoundsPage();
  
  /** retrieve the shape from the bounds page
   * 
   * @return
   */
  abstract protected PlainShape getShape();

  
  /* (non-Javadoc)
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish()
  {
    Date startTime = _shapeTimingsPage.getStartTime();
    Date endTime = _shapeTimingsPage.getEndTime();
    PlainShape shape = getShape();
    final Color theColor = ImportReplay.replayColorFor(_stylingPage
        .getSymbology());
    
    final HiResDate shapeDate = startTime == null ? null : new HiResDate(startTime);
    
    _dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(), shape,
        theColor, shapeDate, "dynamic " + SHAPE_NAME);
      
    if (endTime != null)
    {
      _dynamicShape.setEndDTG(new HiResDate(_shapeTimingsPage.getEndTime()));
    }
    return true;
  }

}
