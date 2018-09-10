/**
 * 
 */
package org.mwc.debrief.core.creators.shapes.dynamic;

import java.util.Date;
import java.util.Enumeration;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.mwc.debrief.core.creators.shapes.CoreInsertShape;
import org.mwc.debrief.core.wizards.dynshapes.DynamicRectangleWizard;

import Debrief.Wrappers.DynamicShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class InsertDynamicRectangle extends CoreInsertShape{

  @Override
  protected Plottable getPlottable(PlainChart theChart)
  {
    final Layers theLayers = theChart.getLayers();
    final Date startDate = getStartDate(theLayers);
    DynamicRectangleWizard wizard = new DynamicRectangleWizard(startDate);
    WizardDialog wd = new WizardDialog(getShell(), wizard);
    final DynamicShapeWrapper thisShape;
    if(wd.open()==Window.OK) {
      
      //get all param details from the wizard now.
      thisShape = wizard.getDynamicShapeWrapper();
    }
    else {
      thisShape = null;
    }
    return thisShape;
  }

  private Date getStartDate(Layers layers) {
    Date startDate = null;
    Enumeration<Editable> elements = layers.elements();
    while(elements.hasMoreElements()) {
      Editable elem = elements.nextElement();
      if(elem instanceof TrackWrapper) {
        TrackWrapper theTrack = (TrackWrapper)elem;
        if(startDate == null || theTrack.getStartDTG().getDate().before(startDate)) {
          startDate = theTrack.getStartDTG().getDate();
        }
      }
    }
    return startDate;
  }

  @Override
  protected PlainShape getShape(WorldLocation centre)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected String getShapeName()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
