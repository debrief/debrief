/**
 * 
 */
package org.mwc.debrief.core.wizards.dynshapes;

import java.awt.Color;
import java.util.Date;

import org.eclipse.jface.wizard.Wizard;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicShapeWrapper;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GenericData.HiResDate;

/**
 * @author Ayesha
 *
 */
public class DynamicPolygonWizard extends Wizard
{

  private DynamicShapeTimingsWizardPage _shapeTimingsPage;
  private DynamicPolygonBoundsPage _boundsPage;
  private DynamicShapeStylingPage _stylingPage;
  public static final String SHAPE_NAME = "Polygon";
  private Date _startDate;
  private Date _endDate;

  private DynamicShapeWrapper _dynamicShape;
  public DynamicPolygonWizard(Date startDate,Date endDate)
  {
    _startDate = startDate;
    _endDate = endDate;
  }
  @Override
  public void addPages()
  {
    _shapeTimingsPage = new DynamicShapeTimingsWizardPage(DynamicShapeBaseWizardPage.TIMINGS_PAGE,SHAPE_NAME,_startDate,_endDate);
    _boundsPage = new DynamicPolygonBoundsPage(DynamicShapeBaseWizardPage.BOUNDS_PAGE);
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
    PolygonShape polygon = _boundsPage.getPolygonShape();
    final Color theColor = ImportReplay.replayColorFor(_stylingPage.getSymbology());
    if(startTime==null) {
      _dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(),polygon,theColor,null,"dynamic polygon");
    }
    else {
    _dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(),polygon,theColor,new HiResDate(startTime),"dynamic polygon");
    }
    if(endTime!=null) {
      _dynamicShape.setTimeEnd(new HiResDate(_shapeTimingsPage.getEndTime()));
    }
    return true;
  }
  
  public DynamicShapeWrapper getDynamicShapeWrapper()
  {
    return _dynamicShape;
  }
}
