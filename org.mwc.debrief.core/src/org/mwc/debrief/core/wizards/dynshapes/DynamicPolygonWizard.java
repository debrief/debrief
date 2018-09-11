/**
 * 
 */
package org.mwc.debrief.core.wizards.dynshapes;

import java.awt.Color;
import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicShapeWrapper;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

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
  public DynamicPolygonWizard(Layers theLayers,Date startDate,Date endDate)
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
    PolygonShape polygon = _boundsPage.getPolygonShape();
    final Color theColor = ImportReplay.replayColorFor(_stylingPage.getSymbology());
    _dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(),polygon,theColor,new HiResDate(startTime),"rectangle");
    _dynamicShape.setTimeEnd(new HiResDate(_shapeTimingsPage.getEndTime()));
    return true;
  }
  
  public DynamicShapeWrapper getDynamicShapeWrapper()
  {
    return _dynamicShape;
  }
}
