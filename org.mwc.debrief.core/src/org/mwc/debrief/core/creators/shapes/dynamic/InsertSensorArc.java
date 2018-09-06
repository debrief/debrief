/**
 * 
 */
package org.mwc.debrief.core.creators.shapes.dynamic;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.mwc.debrief.core.creators.shapes.CoreInsertSensorArc;
import org.mwc.debrief.core.wizards.sensorarc.NewSensorArcWizard;

import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;

/**
 * @author Ayesha<ayesha.ma@gmail.com>
 *
 */
public class InsertSensorArc  extends CoreInsertSensorArc
{

  public static final String COMMAND_ID="org.mwc.debrief.core.creators.shapes.InsertSensorArc";
  
  @Override
  protected DynamicTrackShapeWrapper getPlottable(PlainChart thePlainChart)
  {
    Layers layers = getChart().getLayers();
    Date startDate = null;
    Date endDate = null;
    
    Map<String,Editable> tracks = new HashMap<>();
    Enumeration<Editable> elements = layers.elements();
    while(elements.hasMoreElements()) {
      TrackWrapper theTrack = null;
      Editable elem = elements.nextElement();
      if(elem instanceof TrackWrapper &&!(elem instanceof CompositeTrackWrapper)) {
        theTrack = (TrackWrapper)elem;
        if(startDate == null || theTrack.getStartDTG().getDate().before(startDate)) {
          startDate = theTrack.getStartDTG().getDate();
        }
        if(endDate == null || theTrack.getEndDTG().getDate().after(endDate)) {
          endDate = theTrack.getEndDTG().getDate();
        }
        tracks.put(elem.getName(), theTrack);
      }
    }
    NewSensorArcWizard wizard = new NewSensorArcWizard(tracks,null,startDate,endDate);
    WizardDialog wd = new WizardDialog(getShell(), wizard);
    final DynamicTrackShapeWrapper thisShape;
    if(wd.open()==Window.OK) {
      
      //get all param details from the wizard now.
      thisShape = wizard.getDynamicShapeWrapper();
    }
    else {
      thisShape = null;
    }
    //need to return the shape here to display
    return thisShape;
  }
  
  @Override
  protected void execute()
  {
    boolean proceed = false;
    Layers layers = getChart().getLayers();
    List<String> trackNames = new ArrayList<>();
    Enumeration<Editable> elements = layers.elements();
    while(elements.hasMoreElements()) {
      Editable elem = elements.nextElement();
      if(elem instanceof TrackWrapper && !(elem instanceof CompositeTrackWrapper)) {
        trackNames.add(elem.getName());
        proceed=true;
      }
    }
    if(!proceed){
      //popup dialog
      MessageDialog.openWarning(getShell(), "Warning!", "Sorry, one or more tracks must be present in order to add sensor arcs" );
    }
    else {
      super.execute();
    }
  }
}
