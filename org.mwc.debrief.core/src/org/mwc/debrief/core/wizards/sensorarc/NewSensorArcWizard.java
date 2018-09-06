/**
 * 
 */
package org.mwc.debrief.core.wizards.sensorarc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackCoverageWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackCoverageWrapper.DynamicCoverageShape;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper;
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
  static final String TIMINGS_PAGE = "Timings";
  static final String BOUNDS_PAGE = "Bounds";
  static final String STYLING_PAGE = "Styling";
  private SensorArcTimingsWizardPage _timingsPage;
  private SensorArcBoundsWizardPage _boundsPage;
  private SensorArcStylingWizardPage _stylingPage;
  
  private DynamicTrackShapeSetWrapper dynamicShape;
  private Map<String, Editable> _tracksMap;
  public NewSensorArcWizard(Map<String,Editable> tracksMap,String selectedArc,Date startTime,Date endTime)
  {
    this._tracksMap = tracksMap;
    _timingsPage = new SensorArcTimingsWizardPage("Timings",startTime,endTime);
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
    dynamicShape = addShapeToTrack((TrackWrapper)_tracksMap.get(data.getTrackName()),data);
    return true;
  }
  
  private DynamicTrackShapeSetWrapper addShapeToTrack(TrackWrapper theTrack,
      DynamicTrackShapeWrapper dynamicShapeWrapper) {
    DynamicTrackShapeSetWrapper thisShape = null;
    final Enumeration<Editable> iter = theTrack.getDynamicShapes().elements();
    if (iter != null)
    {
      while (iter.hasMoreElements())
      {
        final DynamicTrackShapeSetWrapper shape =
            (DynamicTrackShapeSetWrapper) iter.nextElement();

        // is this our sensor?
        if (shape.getName().equals(dynamicShapeWrapper.getSensorName()))
        {
          // cool, drop out
          thisShape = shape;
          break;
        }
      } // looping through the sensors
    } // whether there are any sensors
    if (thisShape == null)
    {
      // then create it
      thisShape = new DynamicTrackShapeSetWrapper(dynamicShapeWrapper.getSensorName());

      theTrack.add(thisShape);
    }
    thisShape.add(dynamicShapeWrapper);
    return thisShape;
  }
  
  public DynamicTrackShapeSetWrapper getDynamicShapeWrapper() {
    return dynamicShape;
  }
   
  
  
}
