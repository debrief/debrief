/**
 * 
 */
package org.mwc.debrief.core.wizards.dynshapes;

import java.awt.Color;
import java.util.Date;

import org.eclipse.jface.wizard.Wizard;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicShapeWrapper;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;

/**
 * Wizard for inputting parameters required for
 * creating a dynamic circle
 * @author Ayesha
 *
 */
public class DynamicCircleWizard extends Wizard
{
  
  private DynamicShapeTimingsWizardPage _shapeTimingsPage;
  private DynamicCircleBoundsPage _boundsPage;
  private DynamicShapeStylingPage _stylingPage;
  private DynamicShapeWrapper dynamicShape;
  public static final String SHAPE_NAME = "Circle";
  private Date _startDate;
  private Date _endDate;

  public DynamicCircleWizard(Date startDate,Date endDate)
  {
    _startDate = startDate;
    _endDate = endDate;
        
  }
  @Override
  public void addPages()
  {
    _shapeTimingsPage = new DynamicShapeTimingsWizardPage(DynamicShapeBaseWizardPage.TIMINGS_PAGE,SHAPE_NAME,_startDate,_endDate);
    _boundsPage = new DynamicCircleBoundsPage(DynamicShapeBaseWizardPage.BOUNDS_PAGE);
    _stylingPage = new DynamicShapeStylingPage(DynamicShapeBaseWizardPage.STYLING_PAGE, SHAPE_NAME);
    addPage(_shapeTimingsPage);
    addPage(_boundsPage);
    addPage(_stylingPage);
  }
  /* (non-Javadoc)
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish()
  {
    Date startTime = _shapeTimingsPage.getStartTime();
    Date endTime = _shapeTimingsPage.getEndTime();
    WorldLocation center = _boundsPage.getCenter();
    PlainShape circle = new CircleShape(center, _boundsPage.getRadius());
    final Color theColor = ImportReplay.replayColorFor(_stylingPage.getSymbology());
    if(startTime!=null) {
      dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(),circle,theColor,new HiResDate(startTime),"rectangle");
    }
    else {
      dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(),circle,theColor,null,"rectangle");
    }
    if(endTime!=null) {
      dynamicShape.setTimeEnd(new HiResDate(_shapeTimingsPage.getEndTime()));
    }
    return true;
  }
  public DynamicShapeWrapper getDynamicShapeWrapper()
  {
    return dynamicShape;
  }

}
