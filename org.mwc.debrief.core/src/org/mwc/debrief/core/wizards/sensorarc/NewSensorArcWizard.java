/**
 * 
 */
package org.mwc.debrief.core.wizards.sensorarc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.Wizard;
import org.mwc.debrief.core.wizards.dynshapes.DynamicShapeBaseWizardPage;
import org.mwc.debrief.core.wizards.dynshapes.DynamicShapeTimingsWizardPage;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackCoverageWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackCoverageWrapper.DynamicCoverageShape;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper.DynamicShape;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;

/**
 * 
 * This wizard collects parameters required for creating a new sensor arc.
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class NewSensorArcWizard extends Wizard
{
  private DynamicShapeTimingsWizardPage _timingsPage;
  private SensorArcBoundsWizardPage _boundsPage;
  private SensorArcStylingWizardPage _stylingPage;
  public static final String SHAPE_NAME="Sensor Arc";
  
  private DynamicTrackShapeWrapper dynamicShape;
  public NewSensorArcWizard(Map<String,Editable> tracksMap,String selectedArc,Date startTime,Date endTime)
  {
    _timingsPage = new DynamicShapeTimingsWizardPage(DynamicShapeBaseWizardPage.TIMINGS_PAGE,SHAPE_NAME,startTime,endTime);
    _boundsPage = new SensorArcBoundsWizardPage("Bounds");
    _stylingPage = new SensorArcStylingWizardPage("Styling",tracksMap.keySet().toArray(new String[] {}),selectedArc);
    
  }
  
  @Override
  public void addPages()
  {
    addPage(_timingsPage);
    addPage(_boundsPage);
    addPage(_stylingPage);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish()
  {
    Date startTime = _timingsPage.getStartTime();
    Date endTime = _timingsPage.getEndTime();
    int arcStart = _boundsPage.getArcStart();
    int arcEnd = _boundsPage.getArcEnd();
    int innerRadius = _boundsPage.getInnerRadius();
    int outerRadius = _boundsPage.getOuterRadius();
    String trackName = _stylingPage.getTrackName();
    String symbology = _stylingPage.getSymbology();
    String arcName = _stylingPage.getArcName();
    //create the object here and return it to command.
    HiResDate startDtg = null;
    HiResDate endDtg = null;
    if(startTime!=null) {
      startDtg = new HiResDate(startTime);
    }
    if(endTime!=null) {
      endDtg = new HiResDate(endTime);
    }
    Color theColor = null;
    theColor = ImportReplay.replayColorFor(symbology);
    List<DynamicShape> values = new ArrayList<DynamicShape>();
    values.add(new DynamicCoverageShape(arcStart,arcEnd,innerRadius,outerRadius));
    final int theStyle = ImportReplay.replayLineStyleFor(symbology);
    DynamicTrackShapeWrapper data =
        new DynamicTrackCoverageWrapper(trackName, startDtg, 
            endDtg, 
            values,
            theColor,
            theStyle, arcName);
    String fillStyle = ImportReplay.replayFillStyleFor(symbology);
    if(fillStyle != null)
    {
      if ("1".equals(fillStyle))
      {
        data.setSemiTransparent(false);
      }
      else if ("2".equals(fillStyle))
      {
        data.setSemiTransparent(true);
      }
      else
      {
      }
      
    }
    dynamicShape = data;
    return true;
  }
  
  
  public DynamicTrackShapeWrapper getDynamicShapeWrapper() {
    return dynamicShape;
  }
   
  
  
}
