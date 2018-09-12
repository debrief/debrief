/**
 * 
 */
package org.mwc.debrief.core.creators.shapes.dynamic;

import java.util.Date;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.mwc.debrief.core.creators.shapes.CoreInsertShape;
import org.mwc.debrief.core.wizards.dynshapes.DynamicCircleWizard;

import Debrief.Wrappers.DynamicShapeWrapper;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class InsertDynamicCircle extends CoreInsertShape
{
  
  @Override
  protected Plottable getPlottable(PlainChart theChart)
  {
    final Layers theLayers = theChart.getLayers();
    final Date startDate = getTimeControllerDate(theLayers,true);
    final Date endDate = getTimeControllerDate(theLayers,false);
    DynamicCircleWizard wizard = new DynamicCircleWizard(startDate,endDate);
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
  
  @Override
  protected PlainShape getShape(WorldLocation centre)
  {
    return null;
  }
  @Override
  protected String getShapeName()
  {
    return null;
  }
  

}
