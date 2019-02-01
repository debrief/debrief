package org.mwc.debrief.lite;

import java.awt.Container;

import Debrief.GUI.Tote.AnalysisTote;
import Debrief.GUI.Tote.StepControl;
import MWC.GUI.Layers;

public class LiteTote extends AnalysisTote
{

  public LiteTote(Layers theData, StepControl stepper)
  {
    super(theData);
    setStepper(stepper);
  }

  @Override
  protected void updateToteMembers()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Container getPanel()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
