/**
 * 
 */
package org.mwc.debrief.core.wizards.dynshapes;

import java.awt.Color;
import java.util.Date;

import org.eclipse.jface.wizard.Wizard;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicShapeWrapper;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class DynamicCircleWizard extends Wizard
{
  
  private DynamicShapeTimingsPage _shapeTimingsPage;
  private DynamicCircleBoundsPage _boundsPage;
  private DynamicShapeStylingPage _stylingPage;
  private DynamicShapeWrapper dynamicShape;
  private Layers _layers;
  private Date _startDate;

  public DynamicCircleWizard(Layers theLayers, Date startDate)
  {
    _layers = theLayers;
    _startDate = startDate;
        
  }
  @Override
  public void addPages()
  {
    _shapeTimingsPage = new DynamicShapeTimingsPage("Timings","Circle",_startDate);
    _boundsPage = new DynamicCircleBoundsPage("Bounds");
    _stylingPage = new DynamicShapeStylingPage("Styling", "Circle");
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
    WorldLocation center = _boundsPage.getCenter();
    PlainShape circle = new CircleShape(center, _boundsPage.getRadius());
    final Color theColor = ImportReplay.replayColorFor(_stylingPage.getSymbology());
    dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(),circle,theColor,new HiResDate(startTime),"rectangle");
    return true;
  }
  public DynamicShapeWrapper getDynamicShapeWrapper()
  {
    return dynamicShape;
  }

}
