/**
 * 
 */
package org.mwc.debrief.core.wizards.dynshapes;

import java.awt.Color;
import java.util.Date;

import org.eclipse.jface.wizard.Wizard;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicShapeWrapper;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class DynamicRectangleWizard extends Wizard
{

  private DynamicShapeTimingsWizardPage _shapeTimingsPage;
  private DynamicRectangleBoundsPage _boundsPage;
  private DynamicShapeStylingPage _stylingPage;
  private DynamicShapeWrapper dynamicShape;
  private Date _startDate;
  private Date _endDate;
  public DynamicRectangleWizard(Date startDate,Date endDate)
  {
    _startDate = startDate;
    _endDate = endDate;
  }
  
  @Override
  public void addPages()
  {
    _shapeTimingsPage = new DynamicShapeTimingsWizardPage("Timings","Rectangle",_startDate,_endDate);
    _boundsPage = new DynamicRectangleBoundsPage("Bounds");
    _stylingPage = new DynamicShapeStylingPage("Styling", "Rectangle");
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
    WorldLocation topLeft = _boundsPage.getTopLeftLocation();
    WorldLocation bottomRight = _boundsPage.getBottomRightLocation();
    PlainShape rectangle = new RectangleShape(topLeft, bottomRight);
    final Color theColor = ImportReplay.replayColorFor(_stylingPage.getSymbology());
    dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(),rectangle,theColor,new HiResDate(startTime),"rectangle");
    return true;
  }
  public DynamicShapeWrapper getDynamicShapeWrapper()
  {
    return dynamicShape;
  }

}
